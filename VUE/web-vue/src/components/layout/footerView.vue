<template>
  <div class="footer-box footer-background" style="height: 382px">
    <div>
      <div class="content-view-box footer-view" style="height: 382px">
        <div class="left-footer" style="width: 1200px">
          <!--  -->
          <div>
            <div
              class="footer-icon-box"
              style="margin-left: 0px; margin-top: 10px"
            >
              <!-- <img
                src="@/assets/images/compositeHome/logo.png"
                alt=""
                width="45"
                height="45"
              /> -->
              <el-image class="w-45" style="width: 45px;flex: 0 0 auto;" :src="getImages('compositeHome/logo.png')" fit="contain" lazy loading="lazy" />
              <div
                class="footer-view-title"
                style="margin-left: 20px; margin-top: 10px"
              >
                {{ $title }}
              </div>
            </div>
            <!-- 多语言 -->
            <div @click="handleChangeLang('lang')" class="footer-view-lang">
              {{ $t("message.home.language") }}
            </div>

            <div class="footer-view-copyright">
              ©️ Copyright 2023 {{ $title }}, Inc. All Rights Reserved
            </div>
          </div>
          <!-- 关于我们 -->
          <div style="margin-left: 100px; margin-top: 0px; height: 382px">
            <h5
              class="font-size20 margin-top-bottom20 linght-grey-color footer-view-title-h1"
            >
              {{ $t("message.home.dibu1") }}
            </h5>
            <div class="link-box">
              <div class="footer-view-title-p1" @click="goRouter('/aboutUs')">
                {{ $t("message.home.gongsijianjie") }}
              </div>
            </div>
          </div>
          <!-- 法律隐私政策 -->
          <div style="margin-left: 40px; margin-top: 0px; height: 382px">
            <h5
              class="font-size20 margin-top-bottom20 linght-grey-color footer-view-title-h1"
            >
              {{ $t("message.home.falvyinsizhengce") }}
            </h5>
            <div class="link-box">
              <div class="footer-view-title-p1" @click="goRouter('/clause')">
                {{ $t("message.home.shiyongtikuan") }}
              </div>
              <div
                class="footer-view-title-p1"
                @click="goRouter('/riskStatement')"
              >
                {{ $t("message.home.fengxianshengming") }}
              </div>
              <div class="footer-view-title-p1" @click="goRouter('/privacy')">
                {{ $t("message.home.yinsibaohu") }}
              </div>
            </div>
          </div>
          <!-- 服务 -->
          <div style="margin-left: 60px; margin-top: 0px; height: 382px">
            <h5 class="font-size20 margin-top-bottom20 footer-view-title-h1">
              {{ $t("message.home.fuwu") }}
            </h5>
            <div class="link-box">
              <div class="footer-view-title-p1" @click="goRouter('/help')">
                {{ $t("message.home.xinshouzhidao") }}
              </div>
              <div class="footer-view-title-p1" @click="goRouter('/reminder')">
                {{ $t("message.home.wenxintishi") }}
              </div>

              <div class="footer-view-title-p1" @click="goRouter('/purchase')">
                {{ $t("message.home.gouzhiyuzhuanhuan") }}
              </div>
            </div>
          </div>

          <!-- Community+下载图标 -->
          <div
            class="right-footer"
            style="margin-left: 80px; margin-top: 0px; height: 382px"
          >
            <div style="">
              <h5 class="font-size20 margin-top-bottom20 white-color">
                {{ $t("message.home.dibu27") }}
              </h5>
              <div class="footer-icon-box">
                <div>
                  <!-- <img
                    src="@/assets/images/exchangeHome/footer/chat1.png"
                    alt=""
                  /> -->
                  <el-image :src="getImages('exchangeHome/footer/chat1.png')" fit="contain" lazy loading="lazy" />
                </div>
                <div>
                  <el-image :src="getImages('exchangeHome/footer/chat2.png')" fit="contain" lazy loading="lazy" />
                </div>
                <div>
                  <el-image :src="getImages('exchangeHome/footer/chat3.png')" fit="contain" lazy loading="lazy" />
                </div>
              </div>
            </div>
            <div class="footer-view-download" style="margin-top: 0px">
              {{ $t("message.home.xiazai") }}
            </div>
            <div style="margin-top: 10px">
              <img
                src="@/assets/images/exchangeHome/footer/download2.png"
                alt=""
                style="width: 169px; height: 60px"
                class="mouse-cursor"
              />
            </div>
            <div style="margin-top: 10px">
              <img
                src="@/assets/images/exchangeHome/footer/download1.png"
                alt=""
                style="width: 168px; height: 60px"
                class="mouse-cursor"
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <lang-select ref="langSelect" />
  </div>
</template>

<script>
import LangSelect from "./langSelect.vue";
import { getImages } from "@/utils/index";
export default {
  name: "footerView",
  components: {
    LangSelect,
  },
  data() {
    return {};
  },
  mounted() {},
  methods: {
    goRouter(parmas) {
      this.$router.push(parmas);
    },

    goapp() {
      if (localStorage.getItem("localLan") == "en") {
        window.location.href = "https://" + this.host_url + "/app.html";
      } else if (localStorage.getItem("localLan") == "cht") {
        window.location.href = "https://" + this.host_url + "/app-ft.html";
      } else if (localStorage.getItem("localLan") == "zh-CN") {
        window.location.href = "https://" + this.host_url + "/app-cn.html";
      }
    },
    handleChangeLang(type) {
      this.$refs.langSelect.isShowClick(type);
    },
    getImages
  },
};
</script>

<style scoped>
.footer-view {
  display: flex;
}

.footer-background {
  background-color: #171a1e;
}

.left-footer {
  flex: 4;
  display: flex;
  /* padding:50px 0 40px 0; */
}

.left-footer > div {
  flex: 1;
  justify-content: space-around;
}

/* .right-footer{
    flex:2;
} */

.link-box > div {
  padding-bottom: 16px;
  /* font-size:16px;
    color:#FFFFFF; */
  cursor: pointer;
  font-size: 14px;
  color: #b8bfc9;
}

.right-footer {
  padding-top: 0px;
}

.right-footer > div > h5 {
  margin-bottom: 20px;
}

.right-footer > div:nth-child(1) {
  width: 138px;
}
.footer-icon-box {
  display: flex;
}

.footer-icon-box > div {
  flex: 1;
  justify-content: space-around;
}
.footer-icon-box > div > img {
  display: inline-block;
  width: 19px;
  height: 19px;
}

.lange-view {
  display: flex;
  background-color: #ececec;
  width: 150px;
  height: 30px;
  line-height: 30px;
  align-items: center;
  justify-content: space-around;
  font-size: 12px;
  cursor: pointer;
  border: 1px solid #e5e7eb;
  margin-top: 58px;
}

.lange-view > img {
  margin-left: 10px;
  margin-top: -5px;
}

/* 版权 */
.copyright-box > div {
  border-top: 1px solid #2c2e3a;
  padding: 30px 0;
  font-size: 16px;
  color: white;
}

.footer-view-title {
  color: #1d91ff;
  width: 45px;
  height: 39px;
}

.footer-view-lang {
  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 400;
  font-size: 16px;
  line-height: 22px;

  color: #ffffff;
  cursor: pointer;
}

.footer-view-copyright {
  width: 197px;
  height: 44px;
  margin-left: 0px;
  margin-top: 30px;

  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 400;
  font-size: 16px;
  line-height: 22px;

  color: #b8bfc9;

  /* Inside auto layout */

  flex: none;
  order: 2;
  flex-grow: 0;
}

.footer-view-title-h1 {
  width: 164px;
  height: 22px;

  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 400px;
  font-size: 16px;
  line-height: 22px;

  color: #ffffff;
}

.footer-view-title-p1 {
  /* width: 400px; */
  height: 20px;

  font-family: "PingFang SC";
  font-style: normal;
  /* font-weight: 400px; */

  line-height: 20px;

  padding-top: 20px;
}

.footer-view-download {
  width: auto;
  height: 31px;

  font-family: "PingFang SC";
  font-style: normal;
  /* font-weight: 600px; */
  font-size: 22px;
  line-height: 31px;
  /* identical to box height */

  color: #ffffff;

  /* Inside auto layout */

  flex: none;
  order: 2;
  flex-grow: 0;
}
</style>
