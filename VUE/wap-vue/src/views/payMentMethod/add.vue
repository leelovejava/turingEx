<template>
    <div class="addPay pb-10">
        <fx-header>
            <template #title>{{ type == 'add' ? $t('add') : $t('edit') }} {{ methodName }}</template>
        </fx-header>
        <van-form @failed="onFailed" v-if="configDetail">
            <van-cell-group inset>
                <p class="pt-6 pb-2 ash">{{ $t('payConfig') }}</p>
                <van-field class="select-item disabledInput" v-model="methodName" readonly name="pattern">
                </van-field>
            </van-cell-group>
            <van-cell-group inset>
                <p class="pt-6 pb-2 ash">{{ $t('realName') }}</p>
                <van-field class="select-item" v-model="params.name" :maxlength="10" name="pattern"
                    :placeholder="$t('entryRealName')">
                </van-field>
            </van-cell-group>
            <van-cell-group inset v-for="(item, index) in list" :key="index">
                <p class="pt-6 pb-2 ash">{{ $t(item[`paramName${parseInt(index) + 1}`]) }}</p>
                <van-field class="select-item" v-model="item[`param_value${parseInt(index) + 1}`]"
                    :name="item[`paramName${parseInt(index) + 1}`]" :placeholder="item[`paramName${parseInt(index) + 1}`]">
                </van-field>
            </van-cell-group>
            <van-cell-group inset v-if="configDetail.methodType !== 1">
                <p class="pt-6 pb-2 ash">{{ $t('payQrcode') }}</p>
                <van-uploader v-model="frontFile" :after-read="afterRead" :max-count="1" />
            </van-cell-group>
            <van-cell-group inset>
                <p class="pt-6 pb-2 ash">{{ $t('Remarksoptional') }}</p>
                <van-field class="select-item-textarea" v-model="params.remark" type="textarea" name="picker" clearable
                    :placeholder="$t('Remarks')">
                </van-field>
            </van-cell-group>
        </van-form>
        <div class="text-24 mt-20 ash px-4">{{ $t('tips12') }}</div>
        <div class="px-4 pt-6 mt-3">
            <van-button class="w-full" type="primary" @click="submit">{{ $t('submit') }}</van-button>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { _getAddPaymentMethod, _getUpdatePaymentMethod } from "@/service/trade.api.js";
import { _paymentMethodConfigDetail, _ctcPaymentMethodDetail } from "@/service/user.api.js";
import { _getIdentify } from "@/service/user.api.js";
import { _uploadImage } from '@/service/upload.api.js'
import { useUserStore } from "@/store/user.js";
import { showToast } from "vant";
import { useI18n } from "vue-i18n";
import { BASE_URL } from '@/config'
const { t, locale } = useI18n()
const useStore = useUserStore();
const route = useRoute()
const router = useRouter()

const configDetail = ref(null)
const disabled1 = ref(false)
const disabled2 = ref(false)
const list = ref([])
const params = ref({
    name: '',
    remark: '',
})
const frontFile = ref([])
const submitImg = ref('')

let methodName = ref('')
let id = ref('')
let type = ref('')

const onFailed = (errorInfo) => {
    console.log('failed', errorInfo);
};
const data = ref({})

onMounted(async () => {
    let data = JSON.parse(sessionStorage.getItem("editAdd")) || {}
    type.value = data.type || ''
    id.value = data.id || ''
    methodName.value = t(data.name) || ''
    if (type.value === 'add') {
        getPaymentMethodConfigDetail();
        getUserName();
    } else {
        getPaymentMethodDetail();
    }
})

//获取支付模板参数配置
const getPaymentMethodConfigDetail = () => {
    _paymentMethodConfigDetail({
        id: id.value,
        language: locale.value,
    }).then(data => {
        configDetail.value = data
        formatData(configDetail.value)
    })
}

// 获取实名
const getUserName = () => {
    _getIdentify().then(data => {
        params.value.name = data.name || '';
    })
}

const getPaymentMethodDetail = () => {
    _ctcPaymentMethodDetail({
        id: id.value,
        language: locale.value,
    }).then(data => {
        configDetail.value = data
        formatData(configDetail.value)
        initData();
    })

}

//整理参数获取列表渲染
const formatData = (data) => {
    let index = 1;
    Object.keys(data).forEach(key => {
        if (key.indexOf('paramName') !== -1) {
            if (configDetail.value[key] && configDetail.value[key].length > 0) {
                if (type.value === 'add') {
                    list.value.push({
                        ['paramName' + index]: configDetail.value[key],
                        ['param_value' + index]: '',
                    })
                } else {
                    list.value.push({
                        ['paramName' + index]: configDetail.value['paramName' + index],
                        ['param_value' + index]: configDetail.value['paramValue' + index],
                    })
                    console.log(list)
                }
                index++;
            }
        }
    })
}

const initData = () => {
    params.value.name = configDetail.value.realName;
    params.value.remark = configDetail.value.remark;
    frontFile.value.push({
        url: imgSrc(configDetail.value.qrcode),
        resURL: configDetail.value.qrcode
    })
}
const imgSrc = (path) => {
    if (path) {
        if (path.indexOf('http') !== -1) {
            return path
        } else {
            const url = `${BASE_URL}/wap/public/showimg!showImg.action?imagePath=`
            return url + path;
        }
    }
}


const afterRead = (file) => {
    file.status = 'uploading';
    file.message = t('uploading')

    _uploadImage(file).then(data => {
        submitImg.value = data
        file.status = 'success';
        file.message = t('uploadSuccess');

        frontFile.value = [file]
    }).catch(err => {
        file.status = 'failed';
        file.message = t('uploadFailed');
    })
};


const submit = () => {
    if (type.value === 'add') {
        if (params.value.name == '') {
            showToast(t('请输入真实姓名'))
            return
        }
        const paramsForm = formatParams('method_config_id');
        //添加支付方式
        _getAddPaymentMethod(paramsForm).then(res => {
            router.push('/payMentMethod/list')
        })
    } else {
        const paramsForm = formatParams('id');
        _getUpdatePaymentMethod(paramsForm).then(res => {
            router.push('/payMentMethod/list')
        })
    }

}


//支付方式参数整理 提交参数
const formatParams = (id) => {
    let newParams = {}
    if (id == 'method_config_id') {
        newParams = {
            method_config_id: configDetail.value.uuid,
            real_name: params.value.name,
            remark: params.value.remark,
            qrcode: submitImg.value || ''
        }
    } else {
        newParams = {
            id: configDetail.value.uuid,
            method_config_id: configDetail.value.methodConfigId,
            real_name: params.value.name,
            remark: params.value.remark,
            qrcode: submitImg.value || ''
        }
    }


    list.value.forEach(item => {
        Object.keys(item).forEach(key => {
            if (key.indexOf('param_value') !== -1) {
                newParams[key] = item[key]
            }
        })
    })
    return newParams;
}


</script>
<style lang="scss" scoped>
.addPay {
    font-size: 14px;

    .select-item {
        padding: 0 15px;
        align-items: center;
        height: 50px;
        border-radius: 3px;
    }

    .select-item-textarea {
        background: $tab_background;
        padding: 0 15px;
        align-items: center;
        height: 120px;
        border-radius: 3px;
        color: $text_color;
    }

    .ash {
        color: $text_color1;
    }

    .tips {
        background: $tab_background;
        border-radius: 3px;

        .tip-title {
            font-weight: bold;
            align-items: center;

            img {
                width: 20px;
                height: 20px;

            }
        }
    }
}

:deep(.van-cell-group) {
    background: transparent;
}

:deep(.van-cell) {
    background: $tab_background;
    color: $text_color;

    input {
        color: $text_color;
    }
}

:deep(.van-field__control) {

    color: $text_color;

}

.disabledInput {
    background: $financialBut;
}
</style>
