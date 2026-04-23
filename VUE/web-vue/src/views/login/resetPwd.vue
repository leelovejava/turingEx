<template>
  <div class="restpwd">
    <div
      class="binance-row rests1"
      style="margin-left: -12px; margin-right: -12px"
    >
      <div
        class="binance-col css-yyvsvt"
        style="padding-left: 12px; padding-right: 12px"
      >
        <div class="mt-20 mb-12">
          <div class="css-1mzk4e3">
            {{ $t("chongzhimima") }}
          </div>
        </div>
        <div class="css-tmpver">
          <!-- 邮箱、手机号、账号 -->
          <div class="css-62gygx">
            <div
              class="css-ov54vn"
              style="margin-bottom: 20px"
              v-for="(val, index) in tabArr"
              :key="index"
            >
              <div
                :class="[typeVal == index ? 'active' : '', 'css-1cahv52']"
                @click="changeTypeEmaPwd(index)"
              >
                {{ val }}
              </div>
            </div>
          </div>
          <!-- 邮箱 -->
          <div v-if="typeVal == 0">
            <!-- 账号 -->
            <div class="label">
              {{ $t("youxiang") }}
            </div>

            <el-input
              autocomplete="off"
              type="email"
              :placeholder="$t('qsr_youxiang')"
              name="email"
              v-model="restObj.username"
            />

            <!-- 验证码 -->
            <div class="label" style="margin-top: 10px">
              {{ $t("yanzhengma") }}
            </div>
            <el-input
              :placeholder="$t('qsr_yanzhengma')"
              clearable
              v-model.trim.number="restObj.verifcode"
            >
              <template #append>
                <el-button @click="getVerifcode" :disabled="isDisabaled">{{
                  VeriCode
                }}</el-button>
              </template>
            </el-input>

            <!-- 新密码 -->
            <div class="label" style="margin-top: 10px">
              {{ $t("xinmima") }}
            </div>

            <el-input
              :placeholder="$t('qingshuruxinmima')"
              clearable
              type="email"
              show-password
              v-model.trim="restObj.password"
            />
          </div>
          <!-- 手机号 -->
          <div class="css-15651n7" v-else-if="typeVal == 1">
            <div class="label">
              {{ $t("shoujihao") }}
            </div>

            <div class="flex">
              <div class="border-countrycode" @click="tansferSelecCoun">
                + {{ countryCodeStore.code }}
              </div>
              <el-input
                autocomplete="off"
                class="css-uesmnb"
                :placeholder="$t('qsr_shoujihao')"
                v-model.number="restObj.username"
                clearable
              />
            </div>
            <div class="label" style="margin-top: 10px">
              {{ $t("yanzhengma") }}
            </div>

            <el-input
              :placeholder="$t('qsr_yanzhengma')"
              clearable
              v-model.trim.number="restObj.verifcode"
            >
              <template #append>
                <el-button @click="getVerifcode" :disabled="isDisabaled">{{
                  VeriCode
                }}</el-button>
              </template>
            </el-input>

            <div class="label" style="margin-top: 10px">
              {{ $t("xinmima") }}
            </div>

            <el-input
              :placeholder="$t('qingshuruxinmima')"
              clearable
              type="email"
              show-password
              v-model.trim="restObj.password"
            />
          </div>
          <!-- 账号 -->
          <div class="css-15651n7" v-else-if="typeVal == 2">
            <div class="label">
              {{ $t("zhanghao") }}
            </div>

            <el-input
              autocomplete="off"
              type="email"
              :placeholder="$t('qsr_zhanghao')"
              name="email"
              class="css-16fg16t"
              v-model="restObj.username"
            />

            <div class="label" style="margin-top: 10px">
              {{ $t("6weigugeyanzhnegma") }}
            </div>

            <el-input
              :placeholder="$t('qsr_yanzhengma')"
              clearable
              v-model.trim.number="restObj.verifcode"
            >
            </el-input>

            <div class="label" style="margin-top: 10px">
              {{ $t("xinmima") }}
            </div>

            <el-input
              :placeholder="$t('qingshuruxinmima')"
              clearable
              type="email"
              show-password
              v-model.trim="restObj.password"
            />
          </div>
          <button class="btn" @click="nextStep">
            {{ $t("chongzhimima") }}
          </button>
          <selector-country ref="selectCountryRef" />
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import Axios from "@/api/login.js";
import selectorCountry from "./components/selecterPup.vue";
import { ElMessage } from "element-plus";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import { useCountryCodeStore } from "@/store/countryCode";
import { onUnmounted } from "vue";

const { t } = useI18n();

const countryCodeStore = useCountryCodeStore();
const router = useRouter();
const isDisabaled = ref(false);
const tabArr = [t("youxiang"), t("shoujihao"), t("zhanghao")];
const typeVal = ref(0);
const selectCountryRef = ref(null);
const restObj = ref({
  username: "",
  password: "",
  verifcode: "",
  verifcode_type: "1",
});
const currentType = ref(2); //1表示手机号 2表示邮箱 3表示账号 与传给后端的字段枚举一致
const target = ref("");
const tansferSelecCoun = () => {
  selectCountryRef.value.isShow();
};

const verifyEmailNoPass = () => {
  let emailReg = /[a-zA-Z0-9]+([-_.][A-Za-zd]+)*@([a-zA-Z0-9]+[-.])+[A-Za-zd]{2,5}$/;
  // 验证邮箱
  if (currentType.value == 2 && !emailReg.test(restObj.value.username)) {
    ElMessage.error(t("qsr_zhengquedeyouxiang"));
    return true;
  }
  return false;
};

const verifyPhoneNoPass = () => {
  let numReg = /^([0-9]\d*)$/;
  // 验证手机号
  if (currentType.value == 1 && !numReg.test(restObj.value.username)) {
    ElMessage.error(t("shoujihaozhinengshurushuzi"));
    return true;
  }
  return false;
};

const VeriCode = ref(t("message.user.huoquyanzhengma"));

// 重置密码
const nextStep = () => {
  const { username, password, verifcode } = restObj.value;
  ///验证账号
  if (!username) {
    ElMessage.error(t("qsr_zhanghao"));
    return;
  }

  ///验证码判断
  if (!verifcode) {
    ElMessage.error(t("qsr_yanzhengma"));
    return;
  }

  //验证手机和邮箱格式
  if (verifyPhoneNoPass() || verifyEmailNoPass()) {
    return;
  }

  // 验证密码的长度
  if (password.length < 6) {
    ElMessage.error(t("message.user.qsr_mima6wei"));
    return;
  }

  const newUsername =
    currentType.value == 1 ? countryCodeStore.code + username : username;
  Axios.fixResgister({
    username: newUsername,
    password,
    verifcode,
    verifcode_type: currentType.value,
  }).then((res) => {
    if (res.code == 0) {
      ElMessage.success(t("message.user.mimachongzhichenggong"));
      router.push("/login");
    }
  });
};

const timer = ref(null);
// 获取验证码的方式
const getVerifcode = () => {
  const { username } = restObj.value;
  if (username != "") {
    const newUsername =
      currentType.value == 1 ? countryCodeStore.code + username : username;
    Axios.forgotVericode({
      verifcode_type: currentType.value,
      username: newUsername,
    }).then((res) => {
      if (currentType.value == 2 && !res.data?.email_authority) {
        ElMessage.warning(t("nobindemail"));
        return;
      }

      if (currentType.value == 1 && !res.data?.phone_authority) {
        ElMessage.warning(t("nobindphone"));
        return;
      }

      if (currentType.value == 3 && !res.data?.google_auth_bind) {
        ElMessage.warning(t("nobindgoogle"));
        return;
      }

      target.value =
        res.data.email || res.data.phone || res.data.google_auth_secret;
      ElMessage.success(t("alreadysend") + target.value);
      sendCode();
      isDisabaled.value = true;
      // 倒计时
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
    });
  } else {
    const errorTip = {
      1: t("qsr_youxiang"),
      2: t("qsr_shoujihao"),
      3: t("qsr_zhanghao"),
    };
    const curType = typeVal.value + 1;
    ElMessage.error(errorTip[curType]);
  }
};
const timeOutTimer1 = ref(null);
// 发送验证码
const sendCode = () => {
  timeOutTimer1.value = setTimeout(() => {
    if (target.value) {
      Axios.getVeriCode({
        target: target.value,
      }).then((res) => {
        if (res.code == "0") {
          ElMessage.success(t("yanzhengmafasongchenggong"));
        }
      });
    }
  }, 2000);
};
// 每次切换之后都需要清除手机区号
const changeTypeEmaPwd = (val) => {
  typeVal.value = val;
  if (val == 0) {
    currentType.value = 2;
  } else if (val == 1) {
    currentType.value = 1;
  } else {
    currentType.value = 3;
  }
  countryCodeStore.resetCountry();
  restObj.value = {
    username: "",
    password: "",
    verifcode: "",
    verifcode_type: val == 0 ? 2 : val == 1 ? 1 : val == 2 ? 3 : "",
  };
};

// 清除定时器
onUnmounted(()=>{
  if(timer.value){
    clearInterval(timer.value);
    timer.value = null
  }

  if(timeOutTimer1.value){
    clearTimeout(timeOutTimer1.value)
    timeOutTimer1.value = null
  }

})
</script>
<style lang="css" scoped>
@import url("@/assets/css/login/resetpwd.css");

.el-input {
  height: 52px;
}

.border-countrycode {
  border: 1px solid #dcdfe6;
  display: flex;
  justify-content: center;
  align-items: center;
  min-width: 90px;
  margin-right: 12px;
  border-radius: 4px;
}

.flex {
  display: flex;
}

.mt-20 {
  margin-top: 24px;
}

.mb-12 {
  margin-bottom: 12px;
}

.mb-24 {
  margin-bottom: 24px;
}

.label {
  margin: 0px 0px 4px;
  font-size: 14px;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background-color: #1d91ff;
  color: #fff;
  width: 100%;
  font-weight: 500;
  font-size: 14px;
  padding: 18px 24px;
  border-radius: 4px;
  min-height: 24px;
  margin-top: 40px;
}

.btn:disabled {
  cursor: not-allowed;
  background-image: none;
  background-color: rgb(234, 236, 239);
  color: rgb(183, 189, 198);
}

.btn:disabled:not(.inactive) {
  background-color: rgb(234, 236, 239);
  color: rgb(183, 189, 198);
  cursor: not-allowed;
}
</style>
