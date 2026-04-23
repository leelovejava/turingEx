<template>
  <div class="bindVerify">
    <fx-header>
      <template #title>
        {{ title }}
      </template>
    </fx-header>
    <div class="content">
      <!-- 手机邮箱验证 -->
      <div v-if="type == 1 || type == 2">
        <ExInput
          :label="type == 2 ? $t('email') : $t('phoneNum')"
          :placeholderText="type == 2 ? $t('entryEmail') : $t('entryPhone')"
          v-model="account"
          :area="isArea"
          @selectArea="onSelectArea"
          :dialCode="dialCode"
          :icon="icon"
        />
        <p class="label mt-2 textColor">{{ $t("verificationCode") }}</p>
        <div class="iptbox inputBackground">
          <input
            class="inputBackground textColor"
            type="text"
            :placeholder="$t('entryVerifyCode')"
            v-model="verifyCode"
          />
          <span v-if="type != 3" @click="senCode"
            >{{ $t("sendVerifyCode")
            }}<template v-if="time"> ({{ time }})s</template></span
          >
        </div>
      </div>
      <!-- 谷歌验证 -->
      <div v-if="type == 3">
        <div
          class="pl-30 pr-30 text-center flex flex-col items-center justify-center mt40"
        >
          <div class="imgbox">
            <van-image :src="google_auth_url">
              <template v-slot:loading>
                <van-loading type="spinner" size="20" />
              </template>
            </van-image>
          </div>
          <div class="code flex items-center justify-center textColor">
            {{ google_auth_secret }}
            <img src="@/assets/image/reload.png" alt="" @click="getGoogleauth" />
          </div>
          <p class="tips">{{ $t("saveKeyTips") }}</p>
          <div class="copy textColor" @click="copy">{{ $t("copy") }}</div>
        </div>
        <div class="flex justify-between mt-6 mb-3">
          <div>{{ $t("googleVerificationCode") }}</div>
          <div class="flex items-center">
            <div class="colorMain" @click="googleverifyCode = ''">{{ $t("clear") }}</div>
            <div class="colorMain ml-30" @click="pastCont">{{ $t("paste") }}</div>
          </div>
        </div>
        <van-password-input
          :value="googleverifyCode"
          :gutter="16"
          :focused="showKeyboard"
          @focus="showKeyboard = true"
          :mask="false"
        />
        <van-number-keyboard
          v-model="googleverifyCode"
          :show="showKeyboard"
          @blur="showKeyboard = false"
        />
        <div class="mt-2"></div>
        <div class="mt-5 bottom tabBackground textColor">
          <p>{{ $t("precautions") }}</p>
          <p>{{ $t("precautionsTips1") }}</p>
          <p>{{ $t("precautionsTips2") }}</p>
        </div>
      </div>
      <van-button class="w-full" style="margin-top: 10px" type="primary" @click="submit"
        >{{ $t("confirm") }}
      </van-button>
    </div>
    <nationality-list ref="controlChildRef" :title="$t('selectArea')" @getName="getName">
    </nationality-list>
  </div>
</template>

<script setup>
import ExInput from "@/components/ex-input/index.vue";
import { _sendVerifyCode } from "@/service/login.api";
import {
  _bindEmail,
  _bindPhone,
  _getGoogleauth,
  _bindGoogleauth,
} from "@/service/user.api.js";
import nationalityList from "../authentication/components/nationalityList.vue";
import { PasswordInput, NumberKeyboard, showToast, Image as VanImage } from "vant";
import { ref, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import useClipboard from "vue-clipboard3";
const route = useRoute();
const router = useRouter();
const { t } = useI18n();
const { toClipboard } = useClipboard();

const title = ref("");
const account = ref("");
const isArea = ref(false);
const type = ref(0);
const verifyCode = ref("");
const google_auth_secret = ref("");
const showKeyboard = ref(false);
const googleverifyCode = ref(""); //谷歌验证码code
const imgshow = ref(true);
const google_auth_url = ref("");
const dialCode = ref(0); //电话号前缀
const timer = ref(null);
const time = ref(0);
const icon = ref("");

onMounted(() => {
  init();
  clearInterval(timer.value);
  timer.value = null;
});

const init = () => {
  type.value = route.query.type;
  if (type.value == 1) {
    title.value = t("bindPhone");
    isArea.value = true;
  } else if (type.value == 2) {
    title.value = t("bindEmail");
  } else if (type.value == 3) {
    title.value = t("googleAuthenticatorEn");
    getGoogleauth();
  }
};
const senCode = () => {
  if (type.value == 2) {
    if (account.value == "") {
      showToast(t("entryEmail"));
      return;
    }
    if (account.value.indexOf("@") === -1) {
      showToast(t("请输入正确的邮箱地址"));
      return;
    }
  }
  if (type.value == 1 && account.value == "") {
    showToast(t("entryPhone"));
    return;
  }
  if (type.value == 1 && isNaN(account.value)) {
    showToast(t("请输入正确的手机号码"));
    return;
  }
  if (time.value > 0) {
    return false;
  }
  _sendVerifyCode({
    target: type.value == 1 ? `${dialCode.value}${account.value}` : account.value,
  }).then((res) => {
    showToast(t("sendSuccess"));
    time.value = 30;
    timer.value = setInterval(() => {
      if (time.value > 0) {
        time.value = time.value - 1;
      } else {
        time.value = 0;
        clearInterval(timer.value);
        timer.value = null;
      }
    }, 1000);
  });
};
const submit = () => {
  if (type.value == 2) {
    if (account.value == "") {
      showToast(t("entryEmail"));
      return;
    }
    if (account.value.indexOf("@") === -1) {
      showToast(t("请输入正确的邮箱地址"));
      return;
    }
  }
  if (type.value == 1 && account.value == "") {
    showToast(t("entryPhone"));
    return;
  }
  if (type.value == 1 && isNaN(account.value)) {
    showToast(t("请输入正确的手机号码"));
    return;
  }
  if (type.value == 1) {
    bindPhone();
  } else if (type.value == 2) {
    bindEmail();
  } else if (type.value == 3) {
    bindGoogleauth();
  }
};
const bindEmail = () => {
  _bindEmail({
    email: account.value,
    verifcode: verifyCode.value,
  }).then((res) => {
    showToast(t("bindSuccess"));
    router.push("/safety");
  });
};
const bindPhone = () => {
  _bindPhone({
    phone: `${dialCode.value || ""}${account.value}`,
    verifcode: verifyCode.value,
  }).then((res) => {
    showToast(t("bindSuccess"));
    router.push("/safety");
  });
};
const getGoogleauth = () => {
  _getGoogleauth({}).then((res) => {
    google_auth_secret.value = res.googleAuthSecret;
    google_auth_url.value = res.googleAuthImg;
  });
};
const bindGoogleauth = () => {
  if (!googleverifyCode.value) {
    showToast(t("请输入谷歌验证码"));
    return;
  }
  _bindGoogleauth({
    secret: google_auth_secret.value,
    code: googleverifyCode.value,
  }).then((res) => {
    let google_auth_bind = res.google_auth_bind;
    if (google_auth_bind) {
      showToast(t("bindSuccess"));
      router.push("/safety");
    } else {
      showToast(t("bindFailed"));
    }
  });
};

const copy = async () => {
  try {
    await toClipboard(google_auth_secret.value);
    showToast(t("copySuccess"));
  } catch (e) {
    console.error(e);
  }
};
const pastCont = async () => {
  googleverifyCode.value = await navigator.clipboard.readText();
};

// 选择了国家
const getName = (params) => {
  icon.value = params.code;
  dialCode.value = params.dialCode;
};

const controlChildRef = ref(null);
const onSelectArea = () => {
  controlChildRef.value.open();
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

.bindVerify {
  width: 100%;
  box-sizing: border-box;
}

.content {
  font-size: 12px;
  padding: 0 16px;
}

.iptbox {
  height: 44px;
  margin-top: 8px;
  padding: 0 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 3px;

  input {
    flex: 1;
    height: 100%;
    border: none;
  }

  span {
    color: $color_main;
  }
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
    width: 14px;
    height: 14px;
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
  padding: 20px 16px 7px 16px;

  p {
    padding-bottom: 13px;
  }
}

.van-password-input {
  margin: 0;
}

.van-password-input__security li {
  background: $input_background;
  width: 50px;
  height: 50px;
  color: $text_color;
}

:deep(.van-password-input__security li) {
  background: $input_background;
  width: 50px;
  height: 50px;
  color: $text_color;
}
</style>
