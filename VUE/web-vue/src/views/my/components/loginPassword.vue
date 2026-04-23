<template>
  <!-- 修改密码 -->
  <div>
    <el-dialog
      class="my_security"
      :title="$t('message.user.xiugaidenglumima')"
      v-model="visible"
      :close-on-click-modal="false"
      @close="handClose"
      width="480px"
      center
    >
      <div>
        <div class="label">{{ $t("message.user.xinmima") }}</div>
        <el-input
          :placeholder="$t('message.user.qsr_xinmima')"
          show-password
          clearable
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
        <!-- 选择谷歌验证码 -->
        <div v-if="selectRadio == 3" style="margin-top: 15px">
          <el-input
            :placeholder="$t('message.user.qsr_gugeyanzhengqi6wei')"
            clearable
            type="text"
            class="rest3"
            v-model.trim="googleCodeNum"
          >
          </el-input>
          <div v-if="showGoogle != ''" class="margin-top10">
            {{ $t("message.user.qsr_gugeyanzhengqi") }}&nbsp;{{
              showGoogle
            }}&nbsp;{{ $t("message.user.6weiyanzhengma") }}
          </div>
        </div>

        <!-- 选择邮箱，手机号去掉了 -->
        <div v-if="selectRadio == 2" style="margin-top: 15px">
          <el-input
            :placeholder="$t('message.user.qsr_6weiyanzhengma')"
            clearable
            type="text"
            class="rest3"
            value=""
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

          <div v-if="showEmail != ''" class="margin-top10">
            {{ $t("message.user.shuruyouxianghao") }} &nbsp;{{
              showEmail
            }}&nbsp;{{ $t("message.user.6weiyanzhengma") }}
          </div>
        </div>
      </div>
      <!-- 邮箱或手机号 -->
      <div class="bind_btn">
        <el-button type="primary" @click="confirmBtn">{{
          $t("message.user.queding")
        }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Axios from "@/api/login.js";
import Axios2 from "@/api/my.js";
import { ElMessage } from "element-plus";
import { removeStorage } from "@/utils";
import { useUserStore } from "@/store/user";
export default {
  emits: ["changeModalShow"],
  props: {
    isShowLoginPassword: {
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
        return this.isShowLoginPassword;
      },
      set(val) {
        this.$emit("changeModalShow", false, "isShowLoginPassword");
      },
    },
  },
  mounted() {
    const sptoken = localStorage.getItem("spToken");
    if (sptoken) {
      this.getVerify();
    }
  },
  methods: {
    removeStorage,
    getVerify() {
      Axios2.getVerifTarget({
        verifcode_type: this.selectRadio,
      }).then((res) => {
        this.emailInfo = res.data.email;
        this.googleInfo = res.data.google_auth_secret;
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
    //获取验证码
    getVerifcode() {
      // 没有绑定邮箱时，不能发送验证码
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
        Axios2.updatepsw({
          password: this.confirmPassword,
          verifcode_type: this.selectRadio,
          verifcode:
            this.selectRadio == 3 ? this.googleCodeNum : this.verifcodeNum,
        }).then((res) => {
          if (res.code == "0") {
            ElMessage.success(this.$t("message.user.xiugaichenggong_qcxdl"));
            Axios.loginOut().then((res) => {
              if (res.code == "0") {
                const userStore = useUserStore();
                userStore.resetUserInfo();
                removeStorage("user");
                removeStorage("username");
                removeStorage("spToken");
                removeStorage("vuex");

                this.$router.push("/login");
              }
            });
          }
        });
      } else {
        ElMessage.warning(this.$t("qsr_wanzhengdexinxi"));
      }
    },
    handClose() {
      this.newPassword = "";
      this.confirmPassword = "";
      this.verifcodeNum = "";
      this.selectRadio = 3;
    },
    // 切换之后，验证码倒计时需要去掉
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
