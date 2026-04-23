import Axios from '@/utils/http'
import axios from "axios";
import {compress} from 'image-conversion';
import httpRequest from '@/utils/httpRequest'

// 获取消息列表
export const _getMsg = (message_id= '',order_no, party_id,show_img = true) => {
    console.log("order_no = " + order_no);
    console.log("party_id = " + party_id);
    return new Promise((resolve, reject) => {
        // Axios.fetch('api/newOnlinechat!list.action', {message_id,show_img }).then(res => {
        //     resolve(res.data)
        // })
        httpRequest({
            url: httpRequest.adornUrl('/normal/adminOtcOnlineChatAction!list.action'),
            method: 'get',
            params: httpRequest.adornParams(Object.assign(
                {
                    message_id:message_id,
                    show_img:show_img,
                    order_no:order_no,
                    partyid:party_id,
                }
            )),
            data: httpRequest.adornData(
              Object.assign(
                {
                }
              )
            )
          }).then(({ data }) => {
            resolve(data)
          })
    }) 
    
    // return request({
    //     url: "api/newOnlinechat!list.action",
    //     // loading: true,
    //     method: "GET",
    //     params: {
    //         message_id: params.message_id || '',  // 翻页用到
    //         show_img: params.show_img || true
    //     }
    // })
};

// 未读消息
export const _getUnreadMsg = (order_no, party_id) => {
    // return Axios.fetch('api/newOnlinechat!unread.action', params)
    // return request({
    //     url: "api/newOnlinechat!unread.action",
    //     loading: true,
    //     method: "GET",
    // })
    return new Promise((resolve, reject) => {

        httpRequest({
            url: httpRequest.adornUrl('/normal/adminOtcOnlineChatAction!unread.action'),
            method: 'get',
            params: httpRequest.adornParams(Object.assign(
                {
                    order_no:order_no,
                    partyid:party_id,
                }
            )),
            data: httpRequest.adornData(
              Object.assign(
                {
                    order_no:order_no,
                    partyid:party_id,
                }
              )
            )
          }).then(({ data }) => {
            resolve(data)
          })
    }) 
};

// 发送消息
export const _sendMsg= (type = 'text', content = '', order_no, party_id) => {
    // return Axios.fetch('api/newOnlinechat!send.action', {type, content})
    // return request({
    //     url: "api/newOnlinechat!send.action",
    //     // loading: true,
    //     method: "GET",
    //     params: {
    //         type, // 消息类型， img / text
    //         content
    //     }
    // })

    return new Promise((resolve, reject) => {

        httpRequest({
            url: httpRequest.adornUrl('/normal/adminOtcOnlineChatAction!send.action'),
            method: 'get',
            params: httpRequest.adornParams(Object.assign(
                {
                    type, content,
                    order_no:order_no,
                    partyid:party_id,
                }
            )),
            data: httpRequest.adornData(
              Object.assign(
                {
                }
              )
            )
          }).then(({ data }) => {
            resolve(data)
          })
    }) 
};

// 图片上传
export const _uploadImage = (file, callback) => {
    console.log(file)
    const isLt2M = file.size / 1024 / 1024 < 2;
    if (!isLt2M) {
        Toast.fail('上传头像图片大小不能超过 2MB!');
        return false;
    }
    // Toast.loading()
    // const BASE_URL = 'https://' + window.location.hostname
    const BASE_URL = ''
    return new Promise((resolve, reject) => {
        compress(file, 0.6).then(res => {
            const formData = new FormData()
            formData.append('file', res)
            axios.post(`${BASE_URL}public/uploadimg!execute.action`,
                formData,
                {
                    onDownloadProgress: (progressEvent) => {
                        console.log(progressEvent)
                        if (progressEvent.lengthComputeable) {
                            callback(((progressEvent.loaded / progressEvent.total) * 100).toFixed(2))
                        }
                    }
                },
                { headers:
                    { "Content-Type": "multipart/form-data" }
                }).then(res => {
                    // Toast.clear()
                    const { code, data } =res.data
                    if (code / 1=== 0) {
                        resolve(data)
                    }
                }).catch(err => {
                    // Toast.clear()
                    reject(err)
                })
            })
        })
    };

    // 选择图片文件后回调方法
    export const uploadImageChange = (file,callback) => {
        const isLt2M = file.size / 1024 / 1024 < 2;
        if (!isLt2M) {
            Toast.fail('上传图片大小不能超过 2MB!');
            return false;
        }
  
        let fileData = new FormData()
        fileData.append('file', file)

        
        //
        httpRequest({
          url: httpRequest.adornUrl('/api/uploadFile'),
          method: 'post',
          data: fileData,
          headers: {
            'Content-Type': 'multipart/form-data' // 设置请求头为 multipart/form-data
          }
        }).then(({ data }) => {
          //
          console.log("uploaded image = " + JSON.stringify(data));
          if(data.code == 0){
            if(callback){callback(data.data.path)}
            // this.$bus.$emit('SendMessage',{type:"img",content:""+data.data.httpUrl})
          }else{
            // this.$message(''+data.msg)
            if(callback){callback(null)}
          }
          //
        })
        .finally(() => {
            if(callback){callback(null)}
        })

      };