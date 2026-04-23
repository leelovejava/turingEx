<template>
  <el-dialog
    class="my_security"
    :title="$t('message.user.youxiangbangding')"
    v-model="visible"
    :close-on-click-modal="false"
    @close="handClose"
    width="480px"
    center
  >
    <div>
      <div class="label">{{ $t("youxiang") }}</div>
      <el-input
        :placeholder="$t('message.user.qsr_youxiangdizhi')"
        clearable
        v-model="email"
      >
      </el-input>
      <div class="label label_top">{{ $t("yanzhengma") }}</div>
      <el-input
        :placeholder="$t('message.user.qsr_yanzhengma')"
        clearable
        type="email"
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
      <div class="bind_btn">
        <el-button type="primary" @click="confirmBtn">{{
          $t("message.user.bangding")
        }}</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import Axios from "@/api/login.js";
import Axios2 from "@/api/my.js";
import { ElMessage } from "element-plus";
export default {
  emits: ["changeModalShow"],
  props: {
    isShowEmail: {
      // 弹窗是否展示
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      email: "",
      verifcodeNum: "",
      VeriCode: this.$t("message.user.huoquyanzhengma"),
      isDisabaled: false,
      timer: null,
    };
  },
  computed: {
    visible: {
      get() {
        return this.isShowEmail;
      },
      set(val) {
        this.$emit("changeModalShow", false, "isShowEmail");
      },
    },
  },
  methods: {
    //获取验证码
    getVerifcode() {
      // 邮箱需要校验
      if (this.email) {
        let emailReg =
          /^\.*[a-zA-Z0-9_-]+(\.*[a-zA-Z0-9_-]*)+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/; //允许前半段出现点（.）
        if (!emailReg.test(this.email)) {
          ElMessage.error(this.$t("message.user.qsr_zhengquedeyouxiang"));
          return;
        }

        this.isDisabaled = true;
        this.VeriCode = 60;
        clearInterval(this.timer);
        this.timer = setInterval(() => {
          this.VeriCode--;
          if (this.VeriCode == 0) {
            this.isDisabaled = false;
            clearInterval(this.timer);
            this.timer = null
            this.VeriCode = this.$t("message.user.fasongyanzhengma");
          }
        }, 1000);

        Axios.getVeriCode({
          target: this.email,
          areacode: "",
        }).then((res) => {
          if (res.code == "0") {
            ElMessage.success(
              this.$t("message.user.yanzhengmafasongchenggong")
            );
          }
        });
      } else {
        ElMessage.error(this.$t("message.user.youxiangbunengweikong"));
      }
    },
    confirmBtn() {
      //let emailReg = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
      let emailReg = /[a-zA-Z0-9]+([-_.][A-Za-zd]+)*@([a-zA-Z0-9]+[-.])+[A-Za-zd]{2,5}$/; 
      if (!emailReg.test(this.email)) {
        ElMessage.error(this.$t("message.user.qsr_zhengquedeyouxiang"));
        return;
      }

      let numReg = /^([0-9]\d*)$/;
      if (!numReg.test(this.verifcodeNum)) {
        ElMessage.error(this.$t("message.user.yanzhengmazhinengshurushuzi"));
        return;
      }

      if (this.email && this.verifcodeNum) {
        Axios2.saveEmail({
          email: this.email,
          verifcode: this.verifcodeNum,
        }).then((res) => {
          if (res.code == "0") {
            ElMessage.success(this.$t("message.user.bangdingchenggong"));
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
      this.email = "";
      this.verifcodeNum = "";
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
</style>
