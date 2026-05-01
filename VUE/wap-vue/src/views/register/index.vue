<template>
  <div class="register">
    <div class="top" @click="router.go(-1)">
      <img src="../../assets/image/icon-close.png" alt="" />
    </div>
    <Step :step="1"></Step>
    <div class="title textColor">{{ $t("register") }}</div>

    <div class="flex re-tab">
      <div class="textColor1 active">{{ $t("email") }}</div>
    </div>

    <ExInput :label="$t('email')" :placeholderText="$t('entryEmail')" v-model="username" />

    <ExInput
      :label="$t('phoneNum')"
      :placeholderText="$t('entryPhone')"
      v-model="phone"
      :area="true"
      :dialCode="dialCode"
      @selectArea="onSelectArea"
      :icon="icon"
    />

    <ExInput :label="$t('setPassword')" :placeholderText="$t('passwordTips')" v-model="password" typeText="password" />
    <ExInput :label="$t('repassword')" :placeholderText="$t('surePassword')" v-model="repassword" typeText="password" />
    <ExInput :label="$t('setSafeword')" :placeholderText="$t('safewordTips')" v-model="safeword" typeText="password" />

    <div class="inputCom">
      <p class="label textColor">{{ $t("楠岃瘉鐮?) }}</p>
      <div class="iptbox inputBackground">
        <input class="inputBackground textColor" type="text" :placeholder="$t('entryVerifyCode')" v-model="verifyCode" />
        <span @click="senCode">{{ $t("sendVerifyCode") }}
          <template v-if="time">({{ time }})s</template>
        </span>
      </div>
    </div>

    <ExInput :label="$t('invitCode')" :placeholderText="$t('entryInvitCode')" v-model="invitCode" :clearBtn="false" />
    <div class="protocol textColor">
      <i @click="agreeProt">
        <img v-show="agree" src="../../assets/image/login/prot2.png" alt="" />
        <img v-show="!agree" src="../../assets/image/login/prot1.png" alt="" />
      </i>
      {{ $t("readAgree")
      }}<span class="colorMain" @click="router.push('/aboutUs?serviceTerm=1')">{{
  $t("serviceConf")
}}</span>
    </div>
    <van-button class="w-full" style="margin-top: 10px" type="primary" @click="register">{{ $t("register") }}
    </van-button>
    <div class="noTips textColor">
      {{ $t("hasAccount")
      }}<span class="colorMain" @click="router.push('/login')"> {{ $t("goLogin") }}</span>
    </div>
    <nationality-list ref="controlChildRef" :title="$t('selectArea')" @getName="getName"></nationality-list>

    <Vcode :imgs="[img1, img2]" :show="show" @success="onSuccess" :canvasHeight="200" @fail="onFail" @close="show = false"
      sliderText="" :successText="$t('vertifyPass')" :failText="$t('vertifuFail')" />

    <div>{{ msg }}</div>
  </div>
</template>

<script setup>
import ExInput from "@/components/ex-input/index.vue";
import Step from "./step.vue";
import { _sendVerifyCode } from "@/service/login.api";
import { _bindEmailRegister } from "@/service/user.api.js";
import { useUserStore } from "@/store/user";
import { GET_USERINFO } from "@/store/types.store";
import nationalityList from "../authentication/components/nationalityList.vue";
import Vcode from "vue3-puzzle-vcode";
import img1 from "../../assets/image/slider/1.png";
import img2 from "../../assets/image/slider/2.png";
import { getStorage } from "@/utils/index";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import { ref, onMounted, reactive, onUnmounted } from "vue";
import { showToast } from "vant";
import store from "@/store/store";

const { t } = useI18n();
const router = useRouter();
const userStore = useUserStore();

const show = ref(false);
const msg = ref("");
const time = ref(0);
const agree = ref(false);
const username = ref("");
const phone = ref("");
const password = ref("");
const repassword = ref("");
const verifyCode = ref("");
const safeword = ref("");
const dialCode = ref(0);
const icon = ref("");
const state = reactive({ timer: null });
const invitCode = ref("");

onMounted(() => {
  const usercode = getStorage("usercode");
  if (usercode) invitCode.value = usercode;
  clearInterval(state.timer);
  state.timer = null;
});

onUnmounted(() => {
  clearInterval(state.timer);
  state.timer = null;
});

const senCode = () => {
  if (time.value > 0) return;
  const match = username.value.search(/@/);
  if (username.value == "" || match == -1) {
    showToast(t("entryCorrectEmail"));
    return;
  }

  _sendVerifyCode({ target: username.value })
    .then(() => {
      time.value = 60;
      state.timer = setInterval(() => {
        if (time.value > 0) {
          time.value = time.value - 1;
        } else {
          time.value = 0;
          clearInterval(state.timer);
          state.timer = null;
        }
      }, 1000);
    })
    .catch((error) => {
      showToast(t(error.msg));
    });
};

const onSuccess = () => {
  registerApi();
  show.value = false;
};

const onFail = () => {
  msg.value = "";
};

const controlChildRef = ref(null);
const onSelectArea = () => {
  controlChildRef.value.open();
};

const getName = (params) => {
  icon.value = params.code;
  dialCode.value = params.dialCode;
};

const agreeProt = () => {
  agree.value = !agree.value;
};

const register = () => {
  const match = username.value.search(/@/);
  if (username.value == "" || match == -1) {
    showToast(t("entryCorrectEmail"));
    return;
  }
  if (!/(^[1-9]\d*$)/.test(phone.value)) {
    showToast(t("entryPhone"));
    return;
  }
  if (password.value == "") {
    showToast(t("entryPassword"));
    return;
  }
  if (password.value.length < 6) {
    showToast(t("passwordTips"));
    return;
  }
  if (repassword.value !== password.value) {
    showToast(t("noSamePassword"));
    return;
  }
  if (safeword.value == "") {
    showToast(t("safewordTips"));
    return;
  }
  if (verifyCode.value.length < 6) {
    showToast(t("entryVerifyTips"));
    return;
  }
  if (!agree.value) {
    showToast(t("agreeServiceCond"));
    return;
  }
  show.value = true;
};

const registerApi = () => {
  _bindEmailRegister({
    username: username.value,
    password: password.value,
    type: "2",
    verifcode: verifyCode.value,
    usercode: invitCode.value,
    safeword: safeword.value,
    phone: `${dialCode.value}${phone.value}`,
  }).then((res) => {
    userStore[GET_USERINFO](res);
    store.state.user.userInfo = res;
    router.push("/identity");
  });
};
</script>

<style lang="scss" scoped>
.register {
  width: 100%;
  box-sizing: border-box;
  padding: 16px;
  font-size: 13px;
}

.top {
  padding-left: 9px;
  padding-top: 9px;

  img {
    width: 18px;
    height: 18px;
  }
}

.title {
  font-weight: 700;
  font-size: 26px;
  margin-top: 27px;
  margin-bottom: 33px;
}

.re-tab {
  margin-bottom: 22px;

  div {
    padding: 0 18px;
    height: 34px;
    line-height: 34px;
    text-align: center;
    border-radius: 4px;
    margin-right: 10px;
  }

  .active {
    background: $US_tabActice_background;
    color: $color_main;
  }
}

.noTips {
  margin-top: 24px;
}

.protocol {
  display: flex;
  align-items: center;
  height: 15px;

  i {
    width: 15px;
    height: 15px;
    margin-right: 9px;

    img {
      width: 100%;
      height: 100%;
    }
  }
}

.iptbox {
  height: 44px;
  margin-top: 8px;
  margin-bottom: 18px;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 6px;

  input {
    flex: 1;
    height: 100%;
    border: none;
  }

  span {
    color: $color_main;
  }
}
</style>
