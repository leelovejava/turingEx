<template>
    <div class="charge-crypto pb-20">
        <van-sticky>
            <fx-header>
                <template #title>
                    USDT {{ $t('recharge') }}
                </template>
                <template #right>
                    <van-icon name="orders-o" @click="openRecord" />
                </template>
            </fx-header>
        </van-sticky>
        <div class="pt-9">
            <div class="imgbox flex justify-center">
                <!-- <van-image :src="usdtObj.img">
                    <template v-slot:loading>
                        <van-loading type="spinner" size="20" />
                    </template>
                </van-image> -->
                <div>
                    <canvas id="QRcodeCanvas" v-show="!imgshow"></canvas>
                    <img :src="img" alt="" v-show="imgshow" class="QRcodeImg" />
                </div>
            </div>
            <div class="usdt-address mt-8">
                {{ usdtObj.address }}<span @click="copy">{{ $t('copy') }}</span>
            </div>
            <div class="save mt-2" @click="download('#QRcodeCanvas')">
                {{ $t('saveQr') }}
            </div>
        </div>
        <div class="pt-6">
            <ul class="flex flex-col">
                <li class="flex flex-col px-4 mt-6">
                    <p class="pt-2 pb-2">{{ $t('network') }}</p>
                    <div class="tab-wrap flex">
                        <div class="tab-item mr-4" v-for="(item, index) in usdtList" :key="index"
                            :class="[selectIndex == index ? 'active' : '']" @click="changeIndex(index)">{{
                                item.blockchain_name
                            }}
                        </div>
                    </div>
                </li>
                <!-- <li class="flex flex-col px-4 mt-6">
                    <p class="pt-2 pb-2">{{ $t('ForwardingAddress') }}<span class="ash">{{ $t('optional') }}</span></p>
                    <van-field class="mt-2 usd-input" :placeholder="$t('enterRechargeAddress')" type="text"
                        v-model="enterAddress">
                        <template #extra>
                            <div class="paste" @click="pastCont">{{ $t('paste') }}</div>
                        </template>
                    </van-field>
                </li> -->
                <li class="flex flex-col px-4 mt-6">
                    <p class="pt-2 pb-2">{{ $t('RechargeAmount') }}(USD)</p>
                    <van-field class="mt-2 usd-input" type="text" :placeholder="$t('enterRechargeAmount')" v-model="amount">
                    </van-field>
                </li>
                <li class="flex flex-col px-4 mt-4">
                    <p class="pt-2 pb-2">{{ $t('proofOfPayment') }})</p>
                    <van-uploader class="mt-2" v-model="frontFile" :max-count="1" :after-read="afterRead"></van-uploader>
                    <p class="tips">{{ $t('ClickUpload') }}</p>
                </li>
            </ul>
        </div>
        <div class="px-4 mt-6 centent">
            <h2>{{ $t('RechargeInstructions') }}</h2>
            <p class="mt-2 text-xs">{{ $t('RechargeTitle7') }} USDT-{{ usdtObj.blockchain_name }} {{
                $t('RechargeTitle1')
            }}
            </p>
            <p class="mt-2 text-xs">{{ $t('RechargeTitle2') }}</p>
            <p class="mt-2 text-xs">{{ $t('RechargeTitle3') }}</p>
            <p class="mt-2 text-xs">{{ $t('RechargeTitle4') }}</p>
            <p class="mt-2 text-xs">{{ $t('RechargeTitle5') }}</p>
        </div>
        <div class="px-4 mt-6">
            <van-button class="w-full" type="primary" @click="submit">{{ $t('submit') }} </van-button>
        </div>
    </div>
</template>

<script setup>
import { _getBlock, _getRechargeToken, _rechargeApply } from "@/service/recharge.api";
import { ref, onMounted } from 'vue';
import { useRouter } from "vue-router";
import { showToast, Image as VanImage } from "vant";
import { _uploadImage } from '@/service/upload.api.js'
import useClipboard from "vue-clipboard3";
import { useI18n } from "vue-i18n";
import { setStorage } from '@/utils/index'
import QRCode from "qrcode";


const { t } = useI18n()
const { toClipboard } = useClipboard();
const router = useRouter()

const usdtList = ref([])
const usdtObj = ref({})
const selectIndex = ref(0)

const amount = ref('')
const enterAddress = ref('')
const frontFile = ref([])
const session_token = ref('')
const coin = ref('usdt')
const submitImg = ref('')
const QRCodeMsg = ref('')
const img = ref('')
const imgshow = ref(false)

onMounted(() => {
    initData()
})
const initData = () => {
    _getBlock({
        coin: coin.value
    }).then(res => {
        usdtList.value = res
        usdtObj.value = res[0]
        const { address } = res[0]
        getQRCode(address)
    })
    getRechargeToken()
}


//生成二维码
const getQRCode = (address) => {
    let opts = {
        errorCorrectionLevel: "H",//容错级别
        type: "image/png",//生成的二维码类型
        quality: 0.3,//二维码质量
        margin: 1,//二维码留白边距
        width: 200,//宽
        height: 200,//高
        text: "http://www.xxx.com",//二维码内容
        color: {
            dark: "#333333",//前景色
            light: "#fff"//背景色
        }
    };
    let msg = document.getElementById("QRcodeCanvas");    // 将获取到的数据（val）画到msg（canvas）上
    QRCode.toCanvas(msg, address, opts, function (error) {
        console.log(error)
    });
    // 将canvas转成图片格式，可以长按保存
    img.value = msg.toDataURL('image/png');
    imgshow.value = true;
}

const download = (selector) => {
    // 通过 API 获取目标 canvas 元素
    const canvas = document.querySelector(selector);
    // 创建一个 a 标签，并设置 href 和 download 属性
    const el = document.createElement('a');
    // 设置 href 为图片经过 base64 编码后的字符串，默认为 png 格式
    el.href = canvas.toDataURL();
    el.download = 'qrcode';

    // 创建一个点击事件并对 a 标签进行触发
    const event = new MouseEvent('click');
    el.dispatchEvent(event);
}

const getRechargeToken = () => {
    _getRechargeToken().then((res) => {
        session_token.value = res.session_token;
    });
}
const copy = async () => {
    try {
        await toClipboard(usdtObj.value.address);
        showToast(t('copySuccess'));
    } catch (e) {
        console.error(e);
    }
}
const pastCont = async () => {
    enterAddress.value = await navigator.clipboard.readText();
}
const changeIndex = (index) => {
    selectIndex.value = index
    usdtObj.value = usdtList.value[index]
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

const openRecord = () => {
    setStorage('recordId', 2)
    router.push('/Record/DepositAndWithdrawal')
}

const submit = () => {
    if (!amount.value) {
        showToast(t('enterRechargeAmount'));
        return;
    }

    let numReg = /^[0-9]+([.]{1}[0-9]+){0,1}$/;
    if (!numReg.test(amount.value)) {
        showToast(t('amountNumber'));
        return;
    }
    _rechargeApply({
        session_token: session_token.value,
        amount: amount.value,
        from: enterAddress.value,
        blockchain_name: usdtObj.value.blockchain_name,
        img: submitImg.value,
        coin: coin.value,
        channel_address: usdtObj.value.address,
        tx: "",
    }).then((res) => {
        router.push({ path: '/order/apply-success', query: { 'currentType': 'recharge', 'type': 'hb' } })

    }).catch((error) => {
        getRechargeToken()
    });

}

</script>
<style lang="scss" scoped>
:deep(.van-field__body input) {
    color: $text_color !important;
}

.centent {
    h2 {
        color: $text_color;
        font-size: 16px;
    }

    p {
        color: $text_color1;
        ;
        line-height: 22px;
    }
}

.charge-crypto {
    .usd-input {
        height: 50px;
        background: $input_background;
        font-size: 14px;
        color: $text_color;
    }

    .imgbox {
        border: 1px solid $border-grey;
        padding: 5px;
        width: 182px;
        height: 182px;
        box-sizing: border-box;
        margin: auto;

        img {
            width: 100%;
            height: 100%;
        }
    }

    .usdt-address {
        color: $text_color;
        text-align: center;

        span {
            font-size: 14px;
            color: #007CCC;
            ;
            margin-left: 5px;
        }
    }

    .save {
        width: 125px;
        height: 45px;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
        text-align: center;
        line-height: 44px;
        margin: 0 auto;
        margin-top: 30px;
    }

    .tab-wrap {
        .tab-item {
            width: 118px;
            height: 50px;
            border: 1px solid #D4D7DB;
            border-radius: 4px;
            text-align: center;
            line-height: 49px;

        }

        .active {
            background: url('@/assets/image/order/active-bg.png') no-repeat center;
            background-size: contain;
            border: none !important;
        }
    }

    .ash {
        color: $text_color1;
        ;
    }

    .paste {
        color: $active_line;
    }
}
</style>