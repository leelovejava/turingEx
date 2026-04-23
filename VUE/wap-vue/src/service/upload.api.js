// 图片上传
import axios from "axios";
import { showFailToast } from "vant";
import { IMG_PATH } from '@/config'
import { compress } from 'image-conversion';

export const _uploadImage = (file, callback) => {
    const isLt2M = file.file.size / 1024 / 1024 < 30;
    console.log(isLt2M);
    if (!isLt2M) {
        showFailToast('上传图片大小不能超过 30MB!');
        return false;
    }
    let comp = 0.6
    if (file.file.size > 512 * 1024) {
        comp = 0.1
    }
    return new Promise((resolve, reject) => {
        compress(file.file, comp).then(res => {
            const formData = new FormData()
            formData.append('file', res)
            console.log(IMG_PATH)
            axios.post(`${IMG_PATH}/api/api/uploadFile`,
                formData,
                {
                    timeout: 10000,
                    headers: {
                        "Content-Type": "multipart/form-data",
                    },
                    onDownloadProgress: (progressEvent) => {
                        console.log(progressEvent)
                        if (progressEvent.lengthComputeable) {
                            callback(((progressEvent.loaded / progressEvent.total) * 100).toFixed(2))
                        }
                    }
                }).then(res => {
                    const { code, data } = res.data
                    if (code / 1 === 0) {
                        resolve(data.path)
                    } else {
                        reject(res.data)
                    }
                }).catch(err => {
                    reject(err)
                })
        })
    })
}