<template>
  <div>
    <el-dialog
      :title="$t('message.user.xiugaizijinmima')"
      v-model="visible"
      :close-on-click-modal="false"
      @close="handClose"
      width="480px"
      class="my_security"
      center
    >
      <div>
        <div class="label">{{ $t("message.user.xinmima") }}</div>
        <el-input
          :placeholder="$t('message.user.qsr_xinmima')"
          show-password
          clearable
          type="text"
          class="rest3"
          v-model="newPassword"
        >
        </el-input>

        <div class="label label_top">
          {{ $t("message.user.querenxinmima") }}
        </div>
        <el-input
          :placeholder="$t('message.user.qingquerenxinmima')"
          show-password
          clearable
          type="text"
          class="rest3"
          v-model="confirmPassword"
        >
        </el-input>
        <el-radio-group
          class="label_top"
          v-model="selectRadio"
          @change="changeRadio"
        >
          <el-radio :label="3">{{
            $t("message.user.gugeyanzhengqi")
          }}</el-radio>
          <el-radio :label="2">{{
            $t("message.user.youxiangyanzheng")
          }}</el-radio>
        </el-radio-group>

        <!--谷歌验证器 -->
        <div class="margin-top10" v-if="selectRadio == 3">
          <el-input
            :placeholder="$t('message.user.qsr_6weiyanzhengma')"
            clearable
            type="text"
            v-model.trim="googleCodeNum"
          >
          </el-input>
          <div v-if="showGoogle" class="margin-top10">
            {{ $t("message.user.qsr_gugeyanzhengqi") }}&nbsp;{{
              showGoogle
            }}&nbsp;{{ $t("message.user.6weiyanzhengma") }}
          </div>
        </div>

        <div v-if="selectRadio == 2" class="margin-top10">
          <el-input
            :placeholder="$t('message.user.qsr_6weiyanzhengma')"
            clearable
            type="text"
            v-model.trim="verifcodeNum"
          >
            <template #append>
              <el-button
                :style="'color:#1D91FF'"
                @click="getVerifcode"
                :disabled="isDisabaled"
                >{{ VeriCode }}</el-button
              >
            </template>
          </el-input>
          <div v-if="showEmail" class="margin-top10">
            {{ $t("message.user.shuruyouxianghao") }}&nbsp;{{
              showEmail
            }}&nbsp;{{ $t("message.user.6weiyanzhengma") }}
          </div>
        </div>
      </div>
      <div class="bind_btn">
        <el-button type="primary" @click="confirmBtn">{{
          $t("message.user.bangding")
        }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Axios from "@/api/login.js";
import Axios2 from "@/api/my.js";
import { ElMessage } from "element-plus";
export default {
  emits: ["changeModalShow"],
  props: {
    isShowFund: {
      // 弹窗是否展示
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      timer: null,
      newPassword: "",
      confirmPassword: "",
      email: "",
      googleCodeNum: "", //谷歌验证码
      verifcodeNum: "", //邮箱或手机验证码
      VeriCode: this.$t("message.user.huoquyanzhengma"),
      isDisabaled: false,
      selectRadio: 3,

      emailInfo: "",
      googleInfo: "",
      showEmail: "",

      showGoogle: "",
    };
  },
  computed: {
    visible: {
      get() {
        return this.isShowFund;
      },
      set(val) {
        this.$emit("changeModalShow", false, "isShowFund");
      },
    },
  },
  mounted() {
    this.getVerify();
  },
  methods: {
    //获取验证码
    getVerifcode() {
      if (this.emailInfo) {
        this.isDisabaled = true;
        this.VeriCode = 60;
        clearInterval(this.timer);
        this.timer = setInterval(() => {
          this.VeriCode--;
          if (this.VeriCode == 0) {
            this.isDisabaled = false;
            clearInterval(this.timer);
            this.timer = null;
            this.VeriCode = this.$t("message.user.fasongyanzhengma");
          }
        }, 1000);

        Axios.getVeriCode({
          target: this.emailInfo,
          areacode: "",
        }).then((res) => {
          if (res.code == "0") {
            ElMessage.success(
              this.$t("message.user.yanzhengmafasongchenggong")
            );
          }
        });
      } else {
        ElMessage.error(this.$t("nobindemail"));
      }
    },
    confirmBtn() {
      if (this.newPassword != this.confirmPassword) {
        ElMessage.error(this.$t("message.user.liangcimimabuyizhi"));
        return;
      }

      if (this.confirmPassword && (this.googleCodeNum || this.verifcodeNum)) {
        Axios2.setSafeword({
          safeword: this.confirmPassword,
          verifcode_type: this.selectRadio,
          verifcode:
            this.selectRadio == 3 ? this.googleCodeNum : this.verifcodeNum,
        }).then((res) => {
          if (res.code == "0") {
            ElMessage.success(this.$t("message.user.xiugaichenggong"));
            this.$parent.getUserInfo();
            this.handClose();
          }
        });
      } else {
        ElMessage.warning(this.$t("qsr_wanzhengdexinxi"));
      }
    },
    handClose() {
      this.visible = false;
      this.newPassword = "";
      this.confirmPassword = "";
      this.googleCodeNum = "";
      this.verifcodeNum = "";
      this.selectRadio = 3;
    },
    getVerify() {
      Axios2.getVerifTarget({
        verifcode_type: this.selectRadio,
      }).then((res) => {
        const { email, google_auth_secret } = res.data;

        this.emailInfo = email;
        this.googleInfo = google_auth_secret;
        //打*显示的
        if (this.selectRadio == 2 && this.emailInfo) {
          let arremail = this.emailInfo.split("@");
          this.showEmail =
            this.emailInfo.substring(0, 3) + "*****" + "@" + arremail[1];
        }
        if (this.selectRadio == 3 && this.googleInfo) {
          this.showGoogle = this.googleInfo.replace(
            this.googleInfo.substr(3, 4),
            "****"
          );
        }
      });
    },
    changeRadio() {
      this.getVerify();
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
@import url("@/assets/css/commonTrade/global.css");
@import url("@/assets/css/my/security.css");
.margin-top10 {
  margin-top: 10px;
}
</style>
