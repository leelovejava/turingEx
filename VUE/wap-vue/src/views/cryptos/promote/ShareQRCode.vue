<template xmlns="http://www.w3.org/1999/html">
  <div class="ShareQRCode">
    <assets-head :title="$t('分享二维码')" />
    <div class="qr-padding">
      <div class="qr-flex"></div>
      <div class="qr-img">
        <canvas id="QRcodeCanvas" v-show="!imgshow" style="width: 100%;height: 100%;"></canvas>
        <img :src="img" alt="" v-show="imgshow" class="QRcodeImg" />
        <!--        <img :src="localuser.usercode_qr" alt="" class="QRcodeImg"/>-->
        <!--       -->

      </div>
      <div class="qr-flex"></div>
    </div>
    <div class="qr-bottom">
      <div class="qr-bottom-flex"></div>
      <router-link to="/SharePoster">
        <div class="qr-bottom1">{{ $t('查看海报') }}</div>
      </router-link>
      <div class="qr-bottom2" @click="download('#QRcodeCanvas')">{{ $t('保存二维码') }}</div>
      <!--      <span class="qr-bottom2" @click="download('#QRcodeCanvas">{{$t('保存二维码')}}</span>-->

      <div class="qr-bottom-flex"></div>
    </div>
    <div class="share-href-title textColor">
      {{ $t('链接分享') }}
    </div>
    <div class="share-href">
      <div class="share-href-text">{{ localuser.invitUrl }}</div>
      <div class="share-href-text2" @click="share(localuser.invitUrl);">{{ $t('复制') }}</div>
    </div>
    <div class="share-href-title textColor">
      {{ $t('您的邀请码') }}
    </div>
    <div class="share-href">
      <div class="share-href-text">{{ localuser.usercode }}</div>
      <div class="share-href-text2" @click="share(localuser.usercode);">{{ $t('复制') }}</div>
    </div>
    <div class="qr-title"></div>
  </div>
</template>
<script>
import QRCode from "qrcode";
import { _localuser } from "@/service/promote";
import { showToast } from 'vant';
import assetsHead from "@/components/Transform/assets-head/index.vue";
export default {
  data() {
    return {
      img: '',
      imgshow: false,
      localuser: {
        invitUrl:''
      }
    }
  },
  components: {
    assetsHead
  },
  mounted() {
    this.init()

  },
  activated() {
    this.init()
  },
  methods: {
    // download(imgSrc, fileName) {
    //   let alink = document.createElement("a");
    //   alink.style.display = "none";
    //   alink.href = imgSrc;
    //   alink.setAttribute("download", fileName);
    //   document.body.appendChild(alink);
    //   alink.click();
    //   document.body.removeChild(alink);
    //   window.URL.revokeObjectURL(alink.href);
    // },
    init() {
      this.localuser_get()
    },

    //数据处理
    handleData(val) {
      this.message = this.message + ' ' + val
    },
    // 点击事件
    share(val) {
      if (!val) {
        return
      }
      navigator.clipboard.writeText(val).then(() => {
        showToast(this.$t('复制成功'));
      })

      // this.$copyText(val).then(message => {
      //   this.$toast(this.$t('复制成功'));
      // }).catch(err => {

      // })
      // navigator.clipboard.writeText(val)
      //     .then(() => {
      //       alert(this.$t('已复制'))
      //     })
    },
    localuser_get() {
      const t = this
      _localuser({}).then((res) => {
        t.localuser = res
        const shareUrl = 'https://' + window.location.hostname + '/syn/#/?usercode='+res.usercode;
        t.localuser.invitUrl = shareUrl
        console.log(res)
        t.getQRCode()
      })
    },
    onClickLeft() {
      this.$router.push('/promote')
    },
    download(selector) {
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
    },
    getQRCode() {
      let opts = {
        errorCorrectionLevel: "H",//容错级别
        type: "image/png",//生成的二维码类型
        quality: 0.3,//二维码质量
        margin: 4,//二维码留白边距
        width: 200,//宽
        height: 200,//高
        text: this.localuser.invitUrl,//二维码内容
        color: {
          dark: "#333333",//前景色
          light: "#fff"//背景色
        }
      };
      this.QRCodeMsg = "qr"; //生成的二维码为URL地址js
      let msg = document.getElementById("QRcodeCanvas");    // 将获取到的数据（val）画到msg（canvas）上
      QRCode.toCanvas(msg, this.QRCodeMsg, opts, function (error) {
        console.log(error)
      });    // 将canvas转成图片格式，可以长按保存
      this.img = msg.toDataURL('image/png');
      this.imgshow = true;
    },
  }
}
</script>
<style lang="scss" scoped>
.ShareQRCode {
  width: 100%;
  box-sizing: border-box;
}

.qr-padding {
  margin-top: 66px;
  display: flex;

  .qr-flex {
    flex: 1;
  }

  .qr-img {
    /*   width: 320px;
    height: 320px;*/
    //background: $btn_main;
  }
}

.qr-bottom {
  margin-top: 67px;
  display: flex;

  .qr-bottom-flex {
    flex: 1;
  }

  .qr-bottom1 {
    width: 288px;
    height: 88px;
    background: #EAECEF;
    line-height: 88px;
    border-radius: 8px;
    margin-right: 17px;
    font-style: normal;
    font-weight: 400;
    font-size: 31px;
    text-align: center;
    color: $text_color4;
  }

  .qr-bottom2 {
    width: 288px;
    height: 88px;
    line-height: 88px;
    background: $btn_main;
    border-radius: 4px;
    margin-left: 17px;
    font-style: normal;
    font-weight: 400;
    font-size: 30px;
    text-align: center;
    color: $main-btn-color;
  }
}

.share-href-title {
  margin: 85px 35px 40px;
  font-style: normal;
  font-weight: 400;
  font-size: 28px;
}

.share-href {
  display: flex;
  margin-left: 35px;
  margin-right: 35px;
  background: $light-grey;
  height: 96px;
  border-radius: 8px;
}

.share-href-text {
  flex: 1;
  font-style: normal;
  font-weight: 400;
  font-size: 32px;
  line-height: 90px;
  margin-left: 20px;
  color: $text_color4;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

.share-href-text2 {
  font-style: normal;
  font-weight: 400;
  font-size: 26px;
  line-height: 90px;
  color: $color_main;
  margin-right: 20px;
}

.qr-title {
  font-style: normal;
  font-weight: 400;
  font-size: 14px;
  line-height: 16px;

  /* 灰 */

  color: $text_color1;
  ;
  text-align: center;
  margin-top: 44px;
}
</style>
