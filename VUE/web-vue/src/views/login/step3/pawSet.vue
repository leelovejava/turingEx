<!-- 设置邮箱 -->
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
                      {{ $t("message.login.shezhizijinmima") }}
                    </div>

                    <div class="css-vurnku">
                      <div class="css-15651n7">
                        <div class="css-xjlny9">
                          {{ $t("message.login.mima") }}
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
                                    $t('message.login.qsr_zijinmima')
                                  "
                                  clearable
                                  class="rest3"
                                  show-password
                                  value=""
                                  v-model.trim="safeword"
                                />
                                <div class="bn-input-suffix css-vurnku"></div>
                              </div>
                            </div>
                          </div>
                        </div>
                        <div class="help_default css-1acqidq"></div>
                      </div>
                      <div class="css-15651n7">
                        <div class="css-xjlny9">
                          {{ $t("message.login.querenmima") }}
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
                                    $t('message.login.qsr_querenmima')
                                  "
                                  clearable
                                  class="rest3"
                                  v-model.trim="safewordTwo"
                                  show-password
                                  value=""
                                />
                                <div class="bn-input-suffix css-vurnku"></div>
                              </div>
                            </div>
                          </div>
                        </div>
                        <div class="help_default css-1acqidq"></div>
                      </div>

                      <button
                        @click="submitPaw"
                        data-bn-type="button"
                        class="css-1bsmpdm"
                      >
                        {{ $t("message.login.bangding") }}
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
  name: "pawSet",
  data() {
    return {
      safeword: "",
      safewordTwo: "",
    };
  },
  created() {},
  computed: {},
  methods: {
    jumpOver() {
      this.$router.push("/idSet");
    },
    submitPaw() {
      if (!this.safeword) {
        this.$message.error(this.$t("message.login.input3"));
        return;
      }
      if (this.safeword.length > 6 || this.safeword < 6) {
        this.$message.error(this.$t("message.login.input4"));
        return;
      }
      if (!this.safewordTwo) {
        this.$message.error(this.$t("message.login.input5"));
        return;
      }
      if (this.safewordTwo.length > 6 || this.safewordTwo < 6) {
        this.$message.error(this.$t("message.login.input6"));
        return;
      }
      if (this.safewordTwo != this.safeword) {
        this.$message.error(this.$t("message.login.input7"));
        return;
      }
      let obj = {
        safeword: this.safeword,
      };
      Axios.setSafewordReg(obj).then((res) => {
        if (res.code == "0") {
          this.$message.success(this.$t("message.login.input8"));
          this.$router.push("/idSet");
        }
      });
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
</style>
