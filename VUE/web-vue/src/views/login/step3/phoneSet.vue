<!-- 设置手机 -->
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
                    <div class="css-ojbpe7">
                      {{ $t("message.login.shoujihaoyanzheng") }}
                    </div>

                    <div class="css-vurnku">
                      <!-- 验证码 -->
                      <div class="css-15651n7">
                        <div
                          class="css-xjlny9"
                          style="color: #868d9a; padding: 20px 0"
                        >
                          {{ $t("message.login.input1") }}{{ username
                          }}{{ $t("message.login.input2") }}
                        </div>
                        <div class="css-hiy16i">
                          <div class="css-vpcnym">
                            <div class="css-1fymml5">
                              <div
                                class="bn-tooltip-box css-1yof1af"
                                data-popper-reference-hidden="false"
                                data-popper-escaped="false"
                                data-popper-placement="right-start"
                                style="
                                  position: absolute;
                                  left: 392px;
                                  top: -22px;
                                  transition: opacity 120ms ease-in-out 0s,
                                    transform 120ms ease-in-out 0s;
                                  opacity: 0;
                                  transform: translate3d(6px, 0px, 0px);
                                  visibility: hidden;
                                "
                              >
                                <i class="gap-fill"></i>
                              </div>
                              <div class="css-13xytqh">
                                <el-input
                                  :placeholder="
                                    $t('message.login.qsr_yanzhengma')
                                  "
                                  clearable
                                  class="rest3"
                                  v-model="verifcodeNum"
                                >
                                  <el-button slot="append">{{
                                    VeriCode
                                  }}</el-button>
                                </el-input>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>

                      <button
                        data-bn-type="button"
                        @click="confirmBtn"
                        class="css-1bsmpdm"
                      >
                        {{ $t("message.user.queding") }}
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
                        {{ $t("message.login.tiaoguo") }}
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
import Axios from "@/api/login/login";
export default {
  name: "phoneSet",
  data() {
    return {
      VeriCode: this.$t("message.user.huoquyanzhengma"),
      isDisabaled: false,
      verifcodeNum: "",
      timer:null
    };
  },
  created() {
    this.getVerifcode();
  },
  computed: {
    username() {
      return this.$store.state.username || "";
    },
  },
  methods: {
    jumpOver() {
      this.$router.push("/idSet");
    },
    // 获取验证码
    getVerifcode() {
      if (this.username != "") {
        this.isDisabaled = true;
        this.VeriCode = 60;
        this.timer = setInterval(() => {
          this.VeriCode--;
          if (this.VeriCode == 0) {
            this.isDisabaled = false;
            clearInterval(this.timer);
            this.timer = null
            this.VeriCode = this.$t("message.login.fasongyanzhengma");
          }
        }, 1000);

        Axios.getVeriCode({
          target: this.username,
          areacode: "",
        }).then((res) => {
          if (res.code == "0") {
            this.$message.success(
              this.$t("message.login.yanzhengmafasongchenggong")
            );
          }
        });
      } else {
        this.$message.error(this.$t("message.login.youxiangbunengweikong"));
      }
    },
    confirmBtn() {
      let numReg = /^([0-9]\d*)$/;
      if (!numReg.test(this.verifcodeNum)) {
        this.$message.error(
          this.$t("message.user.yanzhengmazhinengshurushuzi")
        );
        return;
      }

      if (this.verifcodeNum) {
        this.$fetch("api/localuser!save_phone.action", {
          phone: this.username,
          verifcode: this.verifcodeNum,
        }).then((res) => {
          if (res.code == "0") {
            this.$router.push("/idSet");
          }
        });
      } else {
        this.$message.warning(this.$t("message.login.qsr_wanzhengdexinxi"));
      }
    },
  },
  unmounted() {
    if(this.timer){
      clearInterval(this.timer);
      this.timer = null
    }
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
</style>
