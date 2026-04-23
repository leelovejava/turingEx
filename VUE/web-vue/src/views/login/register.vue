<!-- 注册 -->
<template>
  <div id="wrap_app" class="css-155nz97" style="margin-top: 60px">
    <Steps :stepIndex="1" />
    <main class="main css-6q12pm">
      <div class="binance-row css-16kn2us">
        <div class="binance-col css-1wz0uwi" style="width: 420px">
          <div class="css-ojbpe7">
            {{ t("zhanghaozhuce") }}
          </div>
          <!-- 选择 手机 邮箱 账户 -->
          <div class="css-62gygx">
            <div
              class="css-ov54vn"
              style="margin-bottom: 20px"
              v-for="(val, index) in funcArr"
              :key="index"
              @click="clickChange(index)"
            >
              <div
                :class="[recordActive == index ? 'active' : '', 'css-1cahv52']"
              >
                {{ val }}
              </div>
            </div>
          </div>

          <div class="css-vurnku">
            <!-- 1. 输入手机/邮箱/账户 -->
            <div value="" class="css-15651n7">
              <div class="css-xjlny9">
                {{
                  recordActive == 0
                    ? t("shoujihao")
                    : recordActive == 1
                    ? t("youxiang")
                    : t("zhanghao")
                }}
              </div>
              <!-- 选择区号 -->
              <div class="css-hiy16i">
                <div
                  v-if="recordActive == 0"
                  class="border-countrycode"
                  @click="tansferSelecCoun"
                >
                  + {{ countryCodeStore.code }}
                </div>

                <el-input
                  :placeholder="
                    recordActive == 0
                      ? t('qsr_shoujihao')
                      : recordActive == 1
                      ? t('qsr_youxiang')
                      : t('qsr_zhanghao6wei')
                  "
                  clearable
                  v-model.trim="register.username"
                >
                </el-input>
              </div>
            </div>
            <!-- 2.Invitation code (optional) -->
            <div value="" class="css-15651n7">
              <div class="css-xjlny9">
                {{ t("yaoqingmaxuantian") }}
              </div>
              <div class="css-hiy16i">
                <el-input
                  :placeholder="t('qsr_yaoqingma')"
                  clearable
                  class="rest3"
                  v-model.trim="register.usercode"
                >
                </el-input>
              </div>
            </div>
            <!-- 3.set password -->
            <div value="" class="css-15651n7">
              <div class="css-xjlny9">
                {{ t("shezhimima") }}
              </div>
              <div class="css-hiy16i">
                <el-input
                  :placeholder="t('qsr_mima2')"
                  clearable
                  show-password
                  v-model.trim="register.password"
                >
                </el-input>
              </div>
            </div>
            <!-- 4.Confirm password -->
            <div class="css-15651n7">
              <div class="css-xjlny9">
                {{ t("querenmima") }}
              </div>
              <div class="css-hiy16i">
                <el-input
                  :placeholder="t('qsr_querenmima')"
                  clearable
                  show-password
                  v-model.trim="subPwd"
                />
              </div>
            </div>

            <!-- 5.Funding password -->
            <div class="css-15651n7">
              <div class="css-xjlny9">
                {{ t("zijinmima") }}
              </div>
              <div class="css-hiy16i">
                <el-input
                  :placeholder="t('qsr_zijinmima')"
                  clearable
                  show-password
                  v-model.trim="register.safeword"
                />
              </div>
            </div>

            <!-- 6.验证码 Verification code -->
            <div
              class="css-15651n7"
              v-if="recordActive == 0 || recordActive == 1"
            >
              <div class="css-xjlny9">
                {{ t("yanzhengma") }}
              </div>
              <div class="css-hiy16i">
                <el-input
                  :placeholder="t('qsr_yanzhengma')"
                  clearable
                  v-model.trim="register.verifcode"
                >
                  <template #append>
                    <el-button @click="getVerifcode" :disabled="isDisabaled">{{
                      VeriCode
                    }}</el-button>
                  </template>
                </el-input>
              </div>
            </div>
            <!-- 注册  -->
            <button
              data-bn-type="button"
              class="css-1bsmpdm"
              @click="handleRegisters"
            >
              {{ t("zhuce") }}
            </button>
          </div>
          <div class="css-jhkvqo">
            {{ t("zhuceguo") }}？&nbsp;
            <div class="css-r3o9q9" @click="goLogin">
              {{ t("denglu") }}
            </div>
          </div>
        </div>
      </div>
      <selector-country ref="selectCountryRef" />
      <verificat ref="ImageVerRef" />
    </main>
  </div>
</template>

<script setup>
import Axios from "@/api/login.js";
import { useI18n } from "vue-i18n";
import { ElMessage } from "element-plus";
import verificat from "./components/ImageVerificat.vue";
import Steps from "./components/steps.vue";
import { useUserStore } from "@/store/user";
import selectorCountry from "./components/selecterPup.vue";

import { useRouter } from "vue-router";
import { useCountryCodeStore } from "@/store/countryCode";
import { setStorage,getStorage } from "@/utils/index";
import {onMounted,onUnmounted} from "vue"

const router = useRouter();
const countryCodeStore = useCountryCodeStore();
const userStore = useUserStore();
const { t } = useI18n();
const funcArr = [t("shoujihao"), t("youxiang"), t("zhanghao")];
const register = ref({
  username: "",
  password: "",
  verifcode: "",
  usercode: "",
  safeword: "",
  type: "1", //手机1，邮箱2,传接口用的
});
const selectCountryRef = ref(null);
const ImageVerRef = ref(null);
const isDisabaled = ref(false);
const type = ref("");
const recordActive = ref(2); //注册方式
const VeriCode = ref(t("message.user.huoquyanzhengma"));
const subPwd = ref("");

onMounted(() => {
  if (getStorage("usercode")) {
    register.value.usercode = getStorage("usercode");
  }
});
// 切换注册方式
const clickChange = (index) => {
  recordActive.value = index;
  if (index == 0) {
    type.value = 1;
  } else if (index == 1) {
    type.value = 2;
  }
  register.value = {
    username: "",
    password: "",
    verifcode: "",
    usercode: "",
    safeword: "",
  };
  subPwd.value = "";
};
// 获取手机号前缀
const tansferSelecCoun = () => {
  selectCountryRef.value.isShow();
};
const verifyEmailNoPass = () => {
  let emailReg = /[a-zA-Z0-9]+([-_.][A-Za-zd]+)*@([a-zA-Z0-9]+[-.])+[A-Za-zd]{2,5}$/;
  // 验证邮箱
  if (recordActive.value == 1 && !emailReg.test(register.value.username)) {
    ElMessage.error(t("qsr_zhengquedeyouxiang"));
    return true;
  }
  return false;
};
const verifyPhoneNoPass = () => {
  let numReg = /^([0-9]\d*)$/;
  // 验证手机号
  if (recordActive.value == 0 && !numReg.test(register.value.username)) {
    ElMessage.error(t("shoujihaozhinengshurushuzi"));
    return true;
  }
  return false;
};

// 点击注册
const handleRegisters = () => {
  const { username, password, verifcode, safeword, usercode } = register.value;

  if (recordActive.value === 0 && !username) {
    ElMessage.error(t("qsr_shoujihao"));
    return;
  }

  if (recordActive.value === 1 && !username) {
    ElMessage.error(t("qsr_youxiang"));
    return;
  }

  if (recordActive.value === 2 && !username) {
    ElMessage.error(t("qsr_zhanghao"));
    return;
  }

  if (!password || !subPwd.value) {
    ElMessage.error(t("qsr_mima"));
    return;
  }

  if (!safeword) {
    ElMessage.error(t("qsr_zijinmima"));
    return;
  }

  if ([0, 1].includes(recordActive.value) && !verifcode) {
    ElMessage.error(t("qsr_yanzhengma"));
    return;
  }

  // 以上为验证不为空
  // 以下验证数据对不对
  // 此处还要验证一下邮箱和手机号，因为有可能用户随便输入的验证码
  if (verifyPhoneNoPass() || verifyEmailNoPass()) {
    return;
  }
  // 验证账户长度
  if (recordActive.value == 2 && username.length < 6) {
    ElMessage.error(t("qsr_zhanghao6wei"));
    return;
  }

  // 验证密码的长度
  if (password.length < 6) {
    ElMessage.error(t("message.user.qsr_mima6wei"));
    return;
  }
  // 验证资金密码
  if (safeword.length < 6) {
    ElMessage.error(t("message.user.qsr_zijinmima6wei"));
    return;
  }

  if (subPwd.value == password) {
    const account =
      recordActive.value == 0 ? countryCodeStore.code + username : username;
    if ([0, 1].includes(recordActive.value)) {
      Axios.resgister({
        username: account,
        password,
        verifcode,
        usercode,
        safeword,
        type: type.value,
      }).then((res) => {
        // 注册成功,存储账户信息
        if (res.code == 0) {
          ElMessage.success(t("message.home.zhucechenggong"));
          setStorage("spToken", res.data.token);
          setStorage("username", res.data.username);
          userStore.updateUserInfo(res.data);
          router.push("/idSet"); //去实名认证
        }
      });
    } else if (recordActive.value == 2) {
      Axios.imageVerifica().then((res) => {
        if (res.code == "0") {
          ImageVerRef.value.isShowFunc(res.data, {
            username: account,
            password,
            usercode,
            safeword,
          });
        }
      });
    }
  } else {
    ElMessage.error(t("liangcimimabuyizhi"));
  }
};
const goLogin = () => {
  router.push("/login");
};

const timer = ref(null);
// 获取验证码，做手机号和邮箱验证
const getVerifcode = () => {
  const { username } = register.value;
  if (username != "") {
    if (verifyPhoneNoPass() || verifyEmailNoPass()) {
      return;
    }

    isDisabaled.value = true;
    VeriCode.value = 60;
    timer.value = setInterval(() => {
      VeriCode.value--;
      if (VeriCode.value == 0) {
        isDisabaled.value = false;
        clearInterval(timer.value);
        timer.value = null
        VeriCode.value = t("fasongyanzhengma");
      }
    }, 1000);

    const target =
      recordActive.value == 0 ? countryCodeStore.code + username : username;
    Axios.getVeriCode({
      target,
      areacode: "",
    }).then((res) => {
      if (res.code == "0") {
        ElMessage.success(t("yanzhengmafasongchenggong"));
      }
    });
  } else {
    const tip =
      recordActive.value == 0
        ? t("shoujihaobunengweikong")
        : t("youxiangbunengweikong");

    ElMessage.error(tip);
  }
};

 // 清除定时器
onUnmounted(()=>{
  if(timer.value){
    clearInterval(timer.value);
    timer.value = null
  }
})

</script>

<style scoped lang="css">
@import url("@/assets/css/login/register.css");
.el-input {
  height: 52px;
}
/* @import url("@/assets/css/commonTrade/global.css"); */
.el-input--suffix >>> .el-input__inner {
  /* padding-right: 30px; */
  outline: none;
  border: none;
}
.border-countrycode {
  border: 1px solid #dcdfe6;
  display: flex;
  justify-content: center;
  align-items: center;
  min-width: 80px;
  margin-right: 12px;
  border-radius: 4px;
  height: 52px;
  line-height: 52px;
  position: absolute !important;
  left: -87px;
}
</style>
