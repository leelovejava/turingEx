<template>
  <div class="gooleVerify">
    <div class="header">
      <div class="flex items-center" @click="$router.go(-1)">
        <img src="../../assets/image/assets-center/left-arrow.png" alt="" class="leftReturn" />
      </div>
      <div class="textColor" @click="$router.push('/finish')">{{ $t("skip") }}</div>
    </div>
    <Step :step="3"></Step>
    <div class="title textColor">{{ $t("safeBind") }}</div>
    <div class="pl-30 pr-30 text-center flex flex-col items-center justify-center mt40">
      <div class="imgbox">
        <canvas id="QRcodeCanvas" v-show="!imgshow"></canvas>
        <img :src="google_auth_url" alt="" v-show="imgshow" class="QRcodeImg" />
      </div>
      <div class="code flex items-center justify-center textColor">
        {{ google_auth_secret }}
        <img src="@/assets/image/reload.png" @click="getGoogleauth" alt="" />
      </div>
      <p class="tips">{{ $t("saveKeyTips") }}</p>
      <div class="copy textColor" @click="copy">{{ $t("copy") }}</div>
    </div>
    <div class="flex justify-between mt-6 mb-3">
      <div class="textColor">{{ $t("googleVerificationCode") }}</div>
      <div class="flex items-center">
        <div class="colorMain" @click="googleverifyCode = ''">{{ $t("clear") }}</div>
        <div class="colorMain ml-30" @click="pastCont">{{ $t("paste") }}</div>
      </div>
    </div>
    <van-password-input :value="googleverifyCode" :gutter="16" :focused="showKeyboard" @focus="showKeyboard = true"
      :mask="false" />
    <van-number-keyboard v-model="googleverifyCode" :show="showKeyboard" @blur="showKeyboard = false" />
    <div class="mt-2"></div>
    <div class="mt-5 bottom tabBackground textColor">
      <p>{{ $t("precautions") }}</p>
      <p>{{ $t("precautionsTips1") }}</p>
      <p>{{ $t("precautionsTips2") }}</p>
    </div>
    <van-button class="w-full" style="margin-top: 10px" type="primary" @click="submit">{{ $t("sure") }}
    </van-button>
  </div>
</template>

<script setup>
import Step from "./step.vue";
import { PasswordInput, NumberKeyboard } from "vant";
import { _getGoogleauth, _bindGoogleauth } from "@/service/user.api.js";
import { ref, onMounted, watch } from "vue";
import { showToast } from "vant";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import useClipboard from "vue-clipboard3";
const { t } = useI18n();
const { toClipboard } = useClipboard();
const router = useRouter();

const google_auth_secret = ref("");
const imgshow = ref(true);
const google_auth_url = ref("");
const googleverifyCode = ref("");
const showKeyboard = ref(false);

onMounted(() => {
  getGoogleauth();
});

const copy = async () => {
  try {
    await toClipboard(google_auth_secret.value);
    showToast(t("copySuccess"));
  } catch (e) {
    console.error(e);
  }
};
const getGoogleauth = () => {
  _getGoogleauth({})
    .then((res) => {
      google_auth_secret.value = res.googleAuthSecret;
      google_auth_url.value = res.googleAuthImg;
    })
    .catch((error) => {
      showToast(t(error.msg));
    });
};
const bindGoogleauth = () => {
  _bindGoogleauth({
    secret: google_auth_secret.value,
    code: googleverifyCode.value,
  })
    .then((res) => {
      console.log(res);
      let google_auth_bind = res.google_auth_bind;
      if (google_auth_bind) {
        showToast(t("bindSuccess"));
        router.push("/finish");
      } else {
        showToast(t("bindFailed"));
      }
    })
    .catch((error) => {
      showToast(t(error.msg));
    });
};
const submit = () => {
  if (googleverifyCode.value.length < 6) {
    showToast(t("entryVerifyCode"));
    return false;
  }
  bindGoogleauth();
};
const pastCont = async () => {
  googleverifyCode.value = await navigator.clipboard.readText();
};
watch(googleverifyCode, (val, oldVal) => {
  if (googleverifyCode.value.length === 6) {
    showKeyboard.value = false;
  }
});
</script>

<style lang="scss" scoped>
:deep(.van-key) {
  background: $btn-group;
}

:deep(.van-number-keyboard__body) {
  background: $selectSymbol_background;
}

.gooleVerify {
  padding: 0 16px 54px 16px;
  font-size: 13px;
  width: 100%;
  box-sizing: border-box;
}

.header {
  display: flex;
  justify-content: space-between;
  padding: 0 13px;
  font-size: 14px;
  height: 50px;
  line-height: 50px;
}

.stepBox {
  padding: 0 30px;
}

.title {
  font-weight: 700;
  font-size: 26px;
  margin-top: 27px;
  margin-bottom: 26px;
}

.imgbox {
  border: 1px solid $border-grey;
  padding: 5px;
  width: 182px;
  height: 182px;
  box-sizing: border-box;

  img {
    width: 100%;
    height: 100%;
  }
}

.code {
  font-size: 15px;
  font-weight: 300;
  line-height: 18px;
  margin-top: 22px;
  height: 18px;

  img {
    width: 13px;
    height: 13px;
    margin-left: 5px;
  }
}

.tips {
  margin-top: 10px;
  color: #999999;
}

.copy {
  border-radius: 4px;
  width: 132px;
  height: 40px;
  margin-top: 16px;
  border: 1px solid $border-grey;
  line-height: 40px;
}

.bottom {
  padding: 20px 16px 12px 16px;

  p {
    padding-bottom: 13px;
  }
}

.van-password-input {
  margin: 0;
}

:deep(.van-password-input__security li) {
  background: $input_background;
  width: 50px;
  height: 50px;
  color: $text_color;
}
</style>
