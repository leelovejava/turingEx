<template>
    <div class="charge-crypto pb-20">
        <van-sticky>
            <fx-header>
                <template #title>{{ $t('还款') }}</template>
                <template #right>
                    <van-icon name="orders-o" @click="openRecord" />
                </template>
            </fx-header>
        </van-sticky>
        <div class="pt-9">
            <div class="imgbox flex justify-center">
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
                            :class="[selectIndex == index ? 'active' : '']" @click="changeIndex(index)">
                            {{ item.blockchain_name }}
                        </div>
                    </div>
                </li>
                <li class="flex flex-col px-4 mt-6">
                    <p class="pt-2 pb-2">{{ $t('RechargeAmount') }}(USD)</p>
                    <van-field class="mt-2 usd-input" type="text" :placeholder="$t('enterRechargeAmount')" v-model="amount" />
                </li>
                <li class="flex flex-col px-4 mt-4">
                    <p class="pt-2 pb-2">{{ $t('proofOfPayment') }}</p>
                    <van-uploader class="mt-2" v-model="frontFile" :max-count="1" :after-read="afterRead" />
                    <p class="tips">{{ $t('ClickUpload') }}</p>
                </li>
            </ul>
        </div>
        <div class="px-4 mt-6">
            <van-button class="w-full" type="primary" @click="submit">{{ $t('submit') }}</van-button>
        </div>
    </div>
</template>

<script setup>
import { _getBlock, _getRechargeToken, _rechargeApply } from "@/service/recharge.api";
import { ref, onMounted } from 'vue';
import { useRouter } from "vue-router";
import { showToast } from "vant";
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
const frontFile = ref([])
const session_token = ref('')
const coin = ref('usdt')
const submitImg = ref('')
const img = ref('')
const imgshow = ref(false)

onMounted(() => {
    _getBlock({ coin: coin.value, addressType: 'repayment' }).then(res => {
        usdtList.value = res
        usdtObj.value = res[0]
        getQRCode(res[0].address)
    })
    _getRechargeToken().then(res => { session_token.value = res.session_token })
})

const getQRCode = (address) => {
    let msg = document.getElementById("QRcodeCanvas")
    QRCode.toCanvas(msg, address, { width: 200, height: 200, margin: 1 }, () => {})
    img.value = msg.toDataURL('image/png')
    imgshow.value = true
}

const download = (selector) => {
    const el = document.createElement('a')
    el.href = document.querySelector(selector).toDataURL()
    el.download = 'qrcode'
    el.dispatchEvent(new MouseEvent('click'))
}

const copy = async () => {
    try { await toClipboard(usdtObj.value.address); showToast(t('copySuccess')) } catch (e) {}
}

const changeIndex = (index) => {
    selectIndex.value = index
    usdtObj.value = usdtList.value[index]
}

const afterRead = (file) => {
    file.status = 'uploading'
    file.message = t('uploading')
    _uploadImage(file).then(data => {
        submitImg.value = data
        file.status = 'success'
        file.message = t('uploadSuccess')
        frontFile.value = [file]
    }).catch(() => {
        file.status = 'failed'
        file.message = t('uploadFailed')
    })
}

const openRecord = () => {
    setStorage('recordId', 2)
    router.push('/Record/DepositAndWithdrawal')
}

const submit = () => {
    if (!amount.value) { showToast(t('enterRechargeAmount')); return }
    if (!/^[0-9]+([.]{1}[0-9]+){0,1}$/.test(amount.value)) { showToast(t('amountNumber')); return }
    _rechargeApply({
        session_token: session_token.value,
        amount: amount.value,
        from: '',
        blockchain_name: usdtObj.value.blockchain_name,
        img: submitImg.value,
        coin: coin.value,
        channel_address: usdtObj.value.address,
        tx: "",
        order_type: 'repayment',
    }).then(() => {
        router.push({ path: '/order/apply-success', query: { currentType: 'recharge', type: 'hb' } })
    }).catch(() => {
        _getRechargeToken().then(res => { session_token.value = res.session_token })
    })
}
</script>

<style lang="scss" scoped>
:deep(.van-field__body input) { color: $text_color !important; }
.charge-crypto {
    .usd-input { height: 50px; background: $input_background; font-size: 14px; color: $text_color; }
    .imgbox { border: 1px solid $border-grey; padding: 5px; width: 182px; height: 182px; box-sizing: border-box; margin: auto; img { width: 100%; height: 100%; } }
    .usdt-address { color: $text_color; text-align: center; span { font-size: 14px; color: #007CCC; margin-left: 5px; } }
    .save { width: 125px; height: 45px; border: 1px solid #DDDDDD; border-radius: 4px; text-align: center; line-height: 44px; margin: 0 auto; margin-top: 30px; }
    .tab-wrap { .tab-item { width: 118px; height: 50px; border: 1px solid #D4D7DB; border-radius: 4px; text-align: center; line-height: 49px; } .active { background: url('@/assets/image/order/active-bg.png') no-repeat center; background-size: contain; border: none !important; } }
}
</style>
