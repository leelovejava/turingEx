<template>
    <div id="cryptos">
        <div class="rechargePage box-border px-8 text-28">
            <assets-head :title="$t('快速充币')">
                <van-icon name="orders-o" @click="openRecord" class="list-icon"></van-icon>
            </assets-head>
            <div class="mt-8 w-full text-center text-36 textColor">{{ coin.toUpperCase() + ' ' + $t('充值') }}</div>
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
                <div class=" flex flex-col items-center justify-center text-28">
                    <div class="textColor text-30 mt50" ref="address">{{ address }}</div>
                    <div @click="copy(address)" class="text-26 border-solid-grey text-center code-btn rounded-6 textColor">
                        {{ $t('复制地址') }}</div>
                </div>
                <div>
                    <div class="textColor text-28">{{ $t('转出地址(选填)') }}</div>
                    <div style="position: relative;" class="mt-6 mb-5 text-28">
                        <input style="padding-right: 80px;" v-model="enterAddress"
                            class="input-view text-26 textColor inputBackground" :placeholder="$t('请输入转出地址')" />
                        <div style="position: absolute;right: 10px;top: 50%;color: #1194F7;transform: translateY(-50%)"
                            @click="enterAddress = address">
                            {{ $t('粘贴') }}</div>
                    </div>
                </div>
                <div class="mb-5">
                    <div class="text-28 textColor">{{ $t('充币数量') }}</div>
                    <div>
                        <input v-model="amount" class="input-view w-full text-28 textColor inputBackground"
                            :placeholder="$t('请输入充币数量')" />
                    </div>
                </div>
                <div>
                    <div class="text-28 textColor">{{ $t('链名称') }}</div>
                    <div class="flex ">
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
                    <div class="text-30 mb-7 textColor">{{ $t('重要提示') }}</div>
                    <div class="text-28 text-grey" v-html="tip"></div>
                    <button class="btnMain text-white next-btn text-30 rounded-lg" @click="nextBtn">{{ $t('下一步') }}</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import QRCode from "qrcode";
import { Icon } from 'vant';
import { Uploader, showToast } from 'vant';
import Axios from "@/service/recharge.js";
import assetsHead from "@/components/Transform/assets-head/index.vue";
import { _uploadImage } from "@/service/upload.api.js";
import { BASE_URL } from "@/config";
export default {
    name: "rechargePage",
    components: {
        [Uploader.name]: Uploader,
        [Icon.name]: Icon,
        assetsHead,
    },
    data() {
        return {
            BASE_URL,
            tip: '',
            fileList: [],
            address: "",
            nowUrl: "",
            session_token: "",
            amount: "", //充币数量
            from: "", //客户转出地址
            coin: "", //币种
            blockchainIndex: 0,
            blockchain_name: "",
            chainList: [],
            QRCodeMsg: "",
            imgshow: false,
            img: "",
            enterAddress: ""
        }
    },
    mounted() {
        //获取域名
        this.nowUrl = 'https://' + window.location.hostname;
        this.coin = this.$route.query.symbol;
        this.getData();
        this.getToken();
        // https://www.wbfjsfhjdbsh.site/wap/api/cms!get.action?language=en&content_code=002&token=fe3c35583f8a4f12a86b0c13faa75ee6
        Axios.getRechargeTips({
            language: this.$i18n.locale,
            token: this.$store.state.user.userInfo.token,
            content_code: '002',
        }).then(res => {
            console.log('充值提示', res)

            this.tip = res?.content || '';
        })
    },
    methods: {
        openRecord() {
            this.$router.push({
                path: '/cryptos/assetsCenter/rechargeWithdrawRecord',
                query: {
                    back: "1"
                }
            });
        },
        getData() {
            Axios.getBlock({
                coin: this.coin
            }).then((res) => {

                this.chainList = res;
                this.address = res[0].address;
                this.blockchain_name = res[0].blockchain_name;
                this.getQRCode();

            });
        },
        changeBlockchain(index) {
            this.blockchainIndex = index;
            this.address = this.chainList[this.blockchainIndex].address;
            this.blockchain_name = this.chainList[this.blockchainIndex].blockchain_name;
            this.getQRCode();
        },
        getToken() {
            Axios.getRechargeToken().then((res) => {
                this.session_token = res.session_token;
            });
        },
        onClickLeft() {
            this.$router.go(-1);
        },
        copy(val) {
            if (!val) {
                return
            }
            navigator.clipboard.writeText(val).then(() => {
                showToast(this.$t('复制成功'));
            })
        },
        //上传前
        beforeRead(file) {
            let types = ['image/jpeg', 'image/jpg', 'image/gif', 'image/bmp', 'image/png']
            const isImage = types.includes(file.type);
            if (!isImage) {
                showToast(this.$t('上传图片只能是JPG、JPEG、gif、bmp、PNG格式!'));
                return false;
            }
            return true;

        },
        //上传完成
        async afterRead(file) {
            file.status = 'uploading'
            file.message = this.$t('uploading')

            _uploadImage(file).then(data => {
                file.status = 'success'
                file.message = this.$t('uploadSuccess')
                showToast(this.$t('uploadSuccess'));
                file.resURL = data
            }).catch(err => {
                file.status = 'failed'
                file.message = this.$t('uploadFailed')
            })
        },
        //生成二维码
        getQRCode() {
            let opts = {
                errorCorrectionLevel: "H",//容错级别
                type: "image/png",//生成的二维码类型
                quality: 0.3,//二维码质量
                margin: 4,//二维码留白边距
                width: 200,//宽
                height: 200,//高
                text: "http://www.xxx.com",//二维码内容
                color: {
                    dark: "#333333",//前景色
                    light: "#fff"//背景色
                }
            };
            this.QRCodeMsg = this.address; //生成的二维码为URL地址js
            let msg = document.getElementById("QRcodeCanvas");    // 将获取到的数据（val）画到msg（canvas）上
            QRCode.toCanvas(msg, this.QRCodeMsg, opts, function (error) {
                console.log(error)
            });
            // 将canvas转成图片格式，可以长按保存
            this.img = msg.toDataURL('image/png');
            this.imgshow = true;
        },
        download(selector) {
            // 通过 API 获取目标 canvas 元素
            const canvas = document.querySelector(selector);
            // 创建一个 a 标签，并设置 href 和 download 属性
            const el = document.createElement('a');
            // 设置 href 为图片经过 base64 编码后的字符串，默认为 png 格式
            el.href = canvas.toDataURL();
            el.download = '123';

            // 创建一个点击事件并对 a 标签进行触发
            const event = new MouseEvent('click');
            el.dispatchEvent(event);
        },
        nextBtn() {
            if (!this.amount) {
                showToast(this.$t('请输入数量'));
                return;
            }

            let numReg = /^[0-9]+([.]{1}[0-9]+){0,1}$/;
            if (!numReg.test(this.amount)) {
                showToast(this.$t('请输入数字'));
                return;
            }
            if (this.amount) {
                Axios.rechargeApply({
                    session_token: this.session_token,
                    amount: this.amount,
                    from: this.enterAddress,
                    blockchain_name: this.blockchain_name,
                    img: this.fileList.length > 0 ? this.fileList[0].resURL : '',
                    coin: this.coin,
                    channel_address: this.address,
                    tx: "",
                }).then((res) => {
                    this.$router.push({
                        path: "/cryptos/recharge/rechargeSubmit",
                        query: {
                            orderTime: res.orderTime
                        }
                    });

                }).catch((error) => {
                    if (error.code === 'ECONNABORTED') { showToast(this.$t('网络超时！')); }
                    else if (error.msg !== undefined) { showToast(this.$t(error.msg)); }
                    this.getToken();
                });
            } else {
                showToast(this.$t('请输入充币数量'));
            }
        },
    }
}
</script>
<style lang="scss" scoped>
#cryptos {


    .rechargePage {
        width: 100%;
        box-sizing: border-box;

        :deep(.van-uploader__upload) {
            background-color: $upload_bg !important;
        }
    }

    .code-btn {
        width: 280px;
        height: 70px;
        margin-top: 36px;
        margin-bottom: 56px;
        line-height: 70px;
    }

    .mt50 {
        margin-top: 50px;
    }

    .mt70 {
        margin-top: 70px;
    }

    .input-view {
        height: 100px;
        width: 100%;
        border: none;
        outline: none;
        padding-left: 20px;
        box-sizing: border-box;
    }

    .name-btn {
        width: 210px;
        height: 80px;
        line-height: 80px;
        margin-top: 30px;
        margin-bottom: 50px;
    }

    .img-box {
        margin-top: 32px;
        margin-bottom: 90px;


    }

    .hint-box {
        background-color: $tab_background;
        padding-top: 35px;
        padding-bottom: 52px;
    }

    .mtb27 {
        margin: 27px 0;
    }

    .next-btn {
        width: 100%;
        height: 100px;
        margin-top: 35px;
        border: none;
        outline: none;
    }

    .list-icon {
        color: $text_color;
        font-size: 36px !important;
    }
}
</style>
