<!-- 谷歌验证 -->
<template>
  <div>
    <div style="margin-top: 50px">
      <main class="main css-6q12pm">
        <div class="css-108ucgm">
          <div class="binance-row css-16kn2us">
            <div class="binance-col css-1wz0uwi" style="margin-left: -12px">
              <div class="css-cz4mv2">
                <div class="css-6x3lo1">
                  <div class="css-vurnku">
                    <div class="css-ojbpe7" style="text-align: center">
                      {{ $t("gugeyanzhengqibangding") }}
                    </div>

                    <div class="css-vurnku">
                      <!-- QRCode -->
                      <div class="code-box">
                        <!-- 二维码 + 刷新 -->
                        <div class="qr_code">
                          <div class="code_img_wrapper">
                            <img
                              class="code-img"
                              :src="google_auth_url"
                              alt="QR code"
                            />
                          </div>
                          <div>
                            <i
                              class="el-icon-refresh-right font-size24"
                              style="color: rgb(29, 145, 255); margin-left: 8px"
                              @click="refreshBtn"
                            ></i>
                          </div>
                        </div>

                        <!-- 秘钥 + 复制 -->
                        <div class="label_top">
                          <span>{{ google_auth_secret }}</span>
                          <span
                            :data-clipboard-text="google_auth_secret"
                            class="copy-btn"
                            @click="copyBtn"
                            >{{ $t("message.user.fuzhi") }}</span
                          >
                        </div>
                        <!-- 妥善保管 -->
                        <div class="key_msg">
                          ({{ $t("message.user.chang15") }})
                        </div>
                      </div>
                      <!-- 输入谷歌验证码 -->
                      <div>
                        <div class="label_light">
                          {{ $t("message.user.gugeyanzhengma") }}
                        </div>
                        <security-code
                          @getGoogleCode="getGoogleCode"
                        ></security-code>
                      </div>

                      <!-- 提示语句 -->
                      <div class="illustrate">
                        <div>
                          <div>{{ $t("message.user.gugeyanzhengma") }}</div>
                          <p>{{ $t("message.user.chang16") }}</p>
                          <p>{{ $t("message.user.chang17") }}</p>
                        </div>
                        <div>
                          <div>{{ $t("message.user.chang18") }}</div>
                          <p>{{ $t("message.user.chang19") }}</p>
                          <p>{{ $t("message.user.chang20") }}</p>
                        </div>
                      </div>

                      <button @click="confirmBtn" class="css-1bsmpdm">
                        {{ $t("message.user.bangding") }}
                      </button>

                      <div
                        @click="jumpOver"
                        style="
                          text-align: center;
                          padding: 20px 0;
                          color: #868d9a;
                          cursor: pointer;
                        "
                      >
                        {{ $t("tiaoguo") }}
                      </div>
                    </div>
                    <div class="css-vurnku"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>
<script>
import securityCode from "@/components/my/securityCode.vue";
import Axios2 from "@/api/my.js";
import Clipboard from "clipboard";

export default {
  name: "googleSet",
  components: { securityCode },
  data() {
    return {
      google_auth_secret: "", //谷歌验证器密钥
      google_auth_url: "", //图片URL地址
      google_auth_bind: "", //是否已绑定：1/绑定；0/未绑定
      //验证码
      code: [],
    };
  },
  created() {
    let interimToken = localStorage.getItem("spToken");
    if (interimToken) {
      this.getGoogleInfo();
    }
  },
  computed: {},
  methods: {
    jumpOver() {
      this.$router.push("/setDone");
    },
    getGoogleCode(data) {
      this.code = data; //数组
    },
    //获取谷歌绑定的信息
    getGoogleInfo() {
      Axios2.getGoogleAuth().then((res) => {
        const { googleAuthSecret, googleAuthImg } = res.data;
        this.google_auth_secret = googleAuthSecret;
        this.google_auth_url = googleAuthImg;
      });
    },
    refreshBtn() {
      this.getGoogleInfo();
    },
    //绑定
    confirmBtn() {
      if (this.code.length === 6) {
        const paramsCode = this.code.join("");
        Axios2.bindGoogleAuth({
          secret: this.google_auth_secret,
          code: paramsCode,
        }).then((res) => {
          if (res.data?.google_auth_bind == true) {
            ElMessage.success(this.$t("message.user.bangdingchenggong"));
            this.$router.push("/setDone");
            this.handClose();
          }
        });
      } else {
        ElMessage.warning(this.$t("message.user.qsr_yanzhengma"));
      }
    },
    //复制密钥
    copyBtn() {
      const clipboard = new Clipboard(".copy-btn");
      clipboard.on("success", (e) => {
        ElMessage.success(this.$t("message.user.fuzhichenggong"));
        clipboard.destroy();
      });
      clipboard.on("error", (e) => {
        console.error("复制出错", e);
        clipboard.destroy();
      });
    },
    handClose() {
      this.visible = false;
      this.code = [];
    },
  },
};
</script>
<style scoped>
@import url("@/assets/css/login/register.css");

.el-input--suffix >>> .el-input__inner {
  padding-right: 30px;
  outline: none;
  border: none;
}

.code-box {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-bottom: 1px solid #eeeeee;
}

.qr_code {
  display: flex;
}

.code_img_wrapper {
  border: 1px solid #e5e5e5;
}

.code-img {
  width: 200px;
  height: 200px;
}

.label_light {
  margin-top: 30px;
  font-size: 14px;
}

.key_msg {
  color: #999999;
  margin-bottom: 30px;
}

.num-content {
  display: flex;
  margin-top: 15px;
}

.num-content > p {
  font-size: 15px;
  margin-right: 10px;
}

.input-num-style {
  width: 40px;
  height: 40px;
  border: 1px solid #e5e5e5;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
  border-radius: 3px;
  margin-right: 15px;
}

/* 提示 */
.illustrate {
  background: #fafafa;
  padding: 15px;
  margin-top: 25px;
  border-radius: 3px;
}

.illustrate > div:nth-child(1) {
  margin-bottom: 30px;
}

.illustrate > div > p {
  color: #484d56;
  font-size: 12px;
  line-height: 26px;
}

.illustrate > div > div {
  color: #202123;
  font-size: 12px;
  line-height: 26px;
}

.copy-btn {
  width: 120px;
  height: 36px;
  color: #333;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 20px 0;

  cursor: pointer;
}
</style>
