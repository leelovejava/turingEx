<template>
    <div id="cryptos">
        <div class="rechargePage box-border px-8 text-28">
            <assets-head :title="$t('还款')">
                <van-icon name="orders-o" @click="openRecord" class="list-icon"></van-icon>
            </assets-head>
            <div class="mt-8 w-full text-center text-36 textColor">{{ coin.toUpperCase() + ' ' + $t('还款') }}</div>
            <div class="pl-8 pt-8 pr-8 text-center flex flex-col items-center justify-center mt40">
                <div>
                    <canvas id="QRcodeCanvas" v-show="!imgshow"></canvas>
                    <img :src="img" alt="" v-show="imgshow" class="QRcodeImg" />
                </div>
                <div class="code-btn btnMain text-center text-26 text-white" @click="download('#QRcodeCanvas')">{{
                    $t('保存二维码') }}</div>
            </div>
            <div class="border-light-grey"></div>
            <div class="pl-2 pr-2 text-28">
                <div class="flex flex-col items-center justify-center text-28">
                    <div class="textColor text-30 mt50" ref="address">{{ address }}</div>
                    <div @click="copy(address)" class="text-26 border-solid-grey text-center code-btn rounded-6 textColor">
                        {{ $t('复制地址') }}</div>
                </div>
                <div class="mb-5">
                    <div class="text-28 textColor">{{ $t('还款数量') }}</div>
                    <div>
                        <input v-model="amount" class="input-view w-full text-28 textColor inputBackground"
                            :placeholder="$t('请输入还款数量')" />
                    </div>
                </div>
                <div>
                    <div class="text-28 textColor">{{ $t('链名称') }}</div>
                    <div class="flex">
                        <div :class="blockchainIndex == index ? 'borderMain colorMain' : 'border-solid-dark-grey border-r-grey'"
                            class="mr-5 flex-wrap text-32 text-center name-btn rounded textColor"
                            v-for="(item, index) in chainList" :key="index" @click="changeBlockchain(index)">{{
                                item.blockchain_name }}</div>
                    </div>
                </div>
                <div>
                    <div class="text-28 textColor">{{ $t('付款凭证（上传支付详情截图）') }}</div>
                    <div class="img-box">
                        <van-uploader accept="image/*" v-model="fileList" :max-count="1" :before-read="beforeRead"
                            :after-read="afterRead" />
                    </div>
                </div>
            </div>
            <div class="hint-box">
                <div class="pl-8 pr-8">
                    <button class="btnMain text-white next-btn text-30 rounded-lg" @click="nextBtn">{{ $t('提交') }}</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import QRCode from "qrcode";
import { Icon, Uploader, showToast } from 'vant';
import Axios from "@/service/recharge.js";
import { _rechargeApply, _getRechargeToken } from "@/service/recharge.api.js";
import assetsHead from "@/components/Transform/assets-head/index.vue";
import { _uploadImage } from "@/service/upload.api.js";
export default {
    name: "repayCrypto",
    components: {
        [Uploader.name]: Uploader,
        [Icon.name]: Icon,
        assetsHead,
    },
    data() {
        return {
            fileList: [],
            address: "",
            session_token: "",
            amount: "",
            coin: "",
            blockchainIndex: 0,
            blockchain_name: "",
            chainList: [],
            imgshow: false,
            img: "",
        }
    },
    mounted() {
        this.coin = this.$route.query.symbol || 'usdt';
        this.getData();
        _getRechargeToken().then(res => { this.session_token = res.session_token; });
    },
    methods: {
        openRecord() {
            this.$router.push({ path: '/cryptos/assetsCenter/rechargeWithdrawRecord', query: { back: "1" } });
        },
        getData() {
            Axios.getBlock({ coin: this.coin, addressType: 'repayment' }).then(res => {
                this.chainList = res;
                this.address = res[0].address;
                this.blockchain_name = res[0].blockchain_name;
                this.getQRCode();
            });
        },
        changeBlockchain(index) {
            this.blockchainIndex = index;
            this.address = this.chainList[index].address;
            this.blockchain_name = this.chainList[index].blockchain_name;
            this.getQRCode();
        },
        copy(val) {
            if (!val) return;
            navigator.clipboard.writeText(val).then(() => { showToast(this.$t('复制成功')); });
        },
        beforeRead(file) {
            const types = ['image/jpeg', 'image/jpg', 'image/gif', 'image/bmp', 'image/png'];
            if (!types.includes(file.type)) {
                showToast(this.$t('上传图片只能是JPG、JPEG、gif、bmp、PNG格式!'));
                return false;
            }
            return true;
        },
        async afterRead(file) {
            file.status = 'uploading';
            file.message = this.$t('uploading');
            _uploadImage(file).then(data => {
                file.status = 'success';
                file.message = this.$t('uploadSuccess');
                showToast(this.$t('uploadSuccess'));
                file.resURL = data;
            }).catch(() => {
                file.status = 'failed';
                file.message = this.$t('uploadFailed');
            });
        },
        getQRCode() {
            let msg = document.getElementById("QRcodeCanvas");
            QRCode.toCanvas(msg, this.address, {
                errorCorrectionLevel: "H", type: "image/png", quality: 0.3,
                margin: 4, width: 200, height: 200,
                color: { dark: "#333333", light: "#fff" }
            }, () => {});
            this.img = msg.toDataURL('image/png');
            this.imgshow = true;
        },
        download(selector) {
            const el = document.createElement('a');
            el.href = document.querySelector(selector).toDataURL();
            el.download = 'qrcode';
            el.dispatchEvent(new MouseEvent('click'));
        },
        nextBtn() {
            if (!this.amount) { showToast(this.$t('请输入数量')); return; }
            if (!/^[0-9]+([.]{1}[0-9]+){0,1}$/.test(this.amount)) { showToast(this.$t('请输入数字')); return; }
            _rechargeApply({
                session_token: this.session_token,
                amount: this.amount,
                from: '',
                blockchain_name: this.blockchain_name,
                img: this.fileList.length > 0 ? this.fileList[0].resURL : '',
                coin: this.coin,
                channel_address: this.address,
                tx: "",
                order_type: 'repayment',
            }).then(() => {
                this.$router.push({ path: '/order/apply-success', query: { currentType: 'recharge', type: 'hb' } });
            }).catch(error => {
                if (error.msg) showToast(this.$t(error.msg));
                _getRechargeToken().then(res => { this.session_token = res.session_token; });
            });
        },
    }
}
</script>
<style lang="scss" scoped>
#cryptos {
    .rechargePage {
        width: 100%;
        box-sizing: border-box;
        :deep(.van-uploader__upload) { background-color: $upload_bg !important; }
    }
    .code-btn {
        width: 280px; height: 70px; margin-top: 36px; margin-bottom: 56px; line-height: 70px;
    }
    .mt50 { margin-top: 50px; }
    .input-view {
        height: 100px; width: 100%; border: none; outline: none; padding-left: 20px; box-sizing: border-box;
    }
    .name-btn {
        width: 210px; height: 80px; line-height: 80px; margin-top: 30px; margin-bottom: 50px;
    }
    .img-box { margin-top: 32px; margin-bottom: 90px; }
    .hint-box {
        background-color: $tab_background; padding-top: 35px; padding-bottom: 52px;
    }
    .next-btn {
        width: 100%; height: 100px; margin-top: 35px; border: none; outline: none;
    }
    .list-icon { color: $text_color; font-size: 36px !important; }
}
</style>
