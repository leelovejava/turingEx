<template>
  <div id="cryptos">
    <div class="SharePoster">
      <assets-head :title="$t('分享二维码')" />
      <!-- <div id="example px-16" v-if="list.length" :style="{visibility: readyCount === 3 ? 'visiable' : 'hidden'}">
      <carousel-3d :width="285" :height="505" :space="340" :onSlideChange="onSlideChange">
        <slide v-for="(slide, index) in list" :index="index" :key="index">
          <vue-canvas-poster :widthPixels="1000" :painting="slide.painting" @success="success(index, $event)"
            @fail="fail"></vue-canvas-poster>
          <img :src="slide.posterImg" style="width: auto; height: 507px;" v-if="slide.posterImg">
        </slide>
      </carousel-3d>
    </div> -->


      <!-- <div class="qr-img">
        <canvas id="QRcodeCanvas" v-show="!imgshow" style="width: 100%;height: 100%;"></canvas>
        <img :src="img" alt="" v-show="imgshow" class="QRcodeImg" />
    </div> -->
      <div id="example px-16 QRcodeCanvas" v-if="list.length"
        :style="{ visibility: readyCount === 3 ? 'visiable' : 'hidden' }">
        <carousel-3d ref="carousel" :onSlideChange="onSlideChange" :width="285" :height="505">
          <slide v-for="(slide, index) in list" :index="index" :key="index">
            <!-- <vue-canvas-poster :widthPixels="1000" :painting="slide.painting" @success="success(index, $event)"
            @fail="fail"></vue-canvas-poster> -->
            <div :id="`capture_${index}`">
              <img :src="slide.posterImg" style="width: auto; height: 507px;" v-if="slide.posterImg">
              <img :class="index == 2 ? 'bottom-90' : 'bottom-20'" :src="img" alt="" class="QRcodeImg" style="
                width: 120px;height: 120px;
                position:absolute;
                right: 80px;
                " />
            </div>

          </slide>
        </carousel-3d>
      </div>



      <div class="button" @click="download" v-if="readyCount === 3" style="color:#273549">{{ $t('保存海报') }}</div>


    </div>
  </div>
</template>
<script>
import assetsHead from "@/components/Transform/assets-head/index.vue";
// import { Carousel3d, Slide } from 'vue-carousel-3d';
// import { VueCanvasPoster } from 'vue-canvas-poster';
import { mapGetters } from "vuex";
import { showToast, closeToast } from "vant";

// import Vue from 'vue';
import { Carousel3d, Slide } from 'vue3-carousel-3d';
import "vue3-carousel-3d/dist/index.css"

import QRCode from "qrcode";
import { _localuser } from "@/service/promote";
import html2canvas from "html2canvas";


export default {
  data() {
    return {
      posterImg: '',//生成的海报
      painting: {},
      localuser: {
        invitUrl:''
      },
      background_url1: new URL('../../../assets/image/promote/sw_en_1.png', import.meta.url),
      background_url2: new URL('../../../assets/image/promote/sw_en_2.png', import.meta.url),
      background_url3: new URL('../../../assets/image/promote/sw_en_3.png', import.meta.url),
      list: [],
      active: 1,
      readyCount: 0, // 是否已经加载完全
      img: '',
      imgshow: false,
    }
  },
  components: {
    assetsHead,
    Carousel3d,
    // Carousel3d,
    Slide
    // VueCanvasPoster
  },
  computed: {
    ...mapGetters('user', ['userInfo'])
  },
  async mounted() {
    // console.log(this.$i18n.locale, this.userInfo)
    if (this.$i18n.locale == 'CN') {
      this.background_url1 = new URL('../../../assets/image/promote/sw_cn_1.png', import.meta.url)
      this.background_url2 = new URL('../../../assets/image/promote/sw_cn_2.png', import.meta.url)
      this.background_url3 = new URL('../../../assets/image/promote/sw_cn_3.png', import.meta.url)
    }
    setTimeout(() => {
      this.localuser_get2()
    }, 500)

  },
  methods: {
    handImg(url) {
      return new URL(url, import.meta.url).href
    },
    localuser_get2() {
      const t = this
      _localuser({}).then((res) => {
        t.localuser = res
        const shareUrl = 'https://' + window.location.hostname + '/syn/#/?usercode='+res.usercode;
        t.localuser.invitUrl = shareUrl
        t.getQRCode()

      })
    },
    getQRCode() {

      this.imgshow = true;

      // 生成二维码
      QRCode.toDataURL(this.localuser.invitUrl, (err, qrCodeData) => {
        if (err) showToast(err)
        this.img = qrCodeData
        this.readyCount = 3;
      })
      this.localuser_get();
    },
    localuser_get() {
      // this.$toast.loading({ duration: 0})
      const url = this.userInfo.url
      console.log("this.userInfo.url = " + url)
      for (let i = 0; i < 3; i++) {
        const background = this['background_url' + (i + 1)]
        var painting = {
          width: '570px',
          height: '1014px',
          background,
          views: [{
            type: 'qrcode',
            content: url,
            css: {
              bottom: i === 2 ? '380px' : '90px',
              right: '215px',
              color: '$black',
              background: '#fff',
              width: '150px',
              height: '150px',
              borderWidth: '10px',
              borderColor: '#fff'
            },
          }],
        }
        this.list.push({
          painting,
          posterImg: background,
          ready: false
        })
      }
      // })
    },
    onClickLeft() {
      this.$router.go(-1)
    },
    success(index, src) {
      this.list[index].posterImg = src
      this.readyCount++
      if (this.readyCount === 3) {
        closeToast();
        // console.log('ready')
      }
    },
    fail(err) {
      // alert(err)
    },
    getUrlBase64(url) {
      return new Promise(resolve => {
        let canvas = document.createElement('canvas')
        let ctx = canvas.getContext('2d')
        let img = new Image()
        img.crossOrigin = 'Anonymous' //允许跨域
        img.src = url
        img.onload = function () {
          canvas.height = 420
          canvas.width = 258
          ctx.drawImage(img, 0, 0, 258, 420)
          let dataURL = canvas.toDataURL('image/png')
          canvas = null
          resolve(dataURL)
        }
      })
    },
    onSlideChange(evt) {
      this.active = evt
    },
    download() {
      // this.getUrlBase64(this.list[this.active].posterImg).then(base64 => {
      //   let link = document.createElement('a')
      //   link.href = base64
      //   link.download = 'qrCode.png'
      //   link.click()
      //   // Toast.loading({
      //   //   duration: 1000,
      //   //   forbidClick: true
      //   // })
      // })
      this.screenshotHandler()
    },
    screenshotHandler() {
      html2canvas(document.querySelector('.current')).then(function (canvas) {
        let a = document.createElement("a");
        // 将 canvas  方法返回一个包含图片展示的 data URI 。可以使用 type
        // 参数其类型，默认为 PNG 格式。图片的分辨率为96dpi。
        a.href = canvas.toDataURL("image/png", 1.0);
        //将 a 标签插入到页面中
        document.body.appendChild(a);
        //download 下载图片
        //a.download = canvas.toDataURL("image/png", 1.0);
        a.download = 'qrCode.png';
        // 模拟a标签的点击事件
        var e = document.createEvent("MouseEvents");
        e.initEvent("click", true, true);
        a.dispatchEvent(e);
        // 这是向 body 追加自己截的图
        // document.body.appendChild(canvas);
      });
    }
  }
}
</script>
<style lang="scss" scoped>
* {
  touch-action: none;
}

#cryptos {

  .SharePoster {
    width: 100%;
    box-sizing: border-box;
  }

  .carousel-3d-container {
    .carousel-3d-slide {
      // padding: 20px;
      // background: #fff;
      // border: 1px solid #fff;

      .title {
        font-size: 22px;
      }
    }
  }

  // .carousel-3d-width{
  //   margin-top: 75px;
  //   width: 100%;
  //   display: flex;
  // }
  // .carousel-3d-width-flex{
  //   flex: 1;
  // }
  // img {
  //   width: 600px;
  //   height: 100%;
  // }
  .button {
    margin-top: 60px;
    width: 300px;
    height: 85px;
    background: #FCD436;
    border-radius: 4px;
    line-height: 85px;
    text-align: center;
    margin-right: auto;
    margin-left: auto;
    font-size: 30px;
  }


  .bottom-20 {
    bottom: 40px;
  }

  .bottom-90 {
    bottom: 370px;
  }
}
</style>
