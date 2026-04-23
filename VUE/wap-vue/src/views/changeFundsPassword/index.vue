<template>
  <div class="changePassword">
    <fx-header>
      <template #title>
        {{ $t('changeFunsPassword') }}
      </template>
    </fx-header>
    <div class="content">
      <ExInput :label="$t('newPassword')" :placeholderText="$t('entryPassword')" :tips="$t('funsPasswordTips')"
        v-model="password" typeText="password" />
      <ExInput :label="$t('sureNewPassword')" :placeholderText="$t('entryPassword')" :tips="$t('funsPasswordTips')"
        v-model="rePassword" typeText="password" />
      <ExChecked class="mb-42" :list="list" @checkedSelect="onChecked"></ExChecked>
      <p class="label mt-14 textColor">{{ $t('verificationCode') }}</p>
      <div class="iptbox inputBackground">
        <input class="inputBackground textColor" type="text" :placeholder="$t('entryVerifyCode')" v-model="verifyCode">
        <span v-if="currentType != 3" @click="senCode">{{ $t('sendVerifyCode') }}<template v-if="time">
            ({{ time }})s</template></span>
      </div>
      <van-button class="w-full" style="margin-top:10px;" @click="submit" type="primary">
        {{ $t('sure') }}
      </van-button>
    </div>
  </div>
</template>

<script setup>
import ExInput from "@/components/ex-input/index.vue";
import ExChecked from "@/components/ex-checked/index.vue";
import { _setSafeword, _getVerifTarget } from '@/service/user.api.js'
import { _sendVerifyCode } from "@/service/login.api";
import { ref, onMounted, onUnmounted } from "vue";
import { showToast } from "vant";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
const { t } = useI18n()
const router = useRouter()

const password = ref('')//密码
const rePassword = ref('')//确认密码
const verifyCode = ref('') //验证码
let currentType = ref(1) //默认选中手机验证1/手机；2/邮箱；3/谷歌验证器；
const list = ref([
  {
    name: t('phoneVerify'),
    type: 1
  },
  {
    name: t('emailVerify'),
    type: 2
  },
  {
    name: t('googleVerify'),
    type: 3
  },
])
const email_authority = ref(false) //是否绑定邮箱
const google_auth_bind = ref(false)//是否绑定谷歌
const phone_authority = ref(false)//是否绑定手机
const email = ref('')
const phone = ref('')
const google_auth_secret = ref('')
const account = ref('')
const timer = ref(null)
const time = ref(0)

onMounted(() => {
  getVerifTarget();
  clearTimer()
})

const onChecked = (type) => {
  currentType.value = type;
  if (currentType.value == 3 && !google_auth_bind.value) {
    console.log('sd')
    showToast(t('bindGoogleTips'))
  }
  if (currentType.value == 1) {
    account.value = phone.value;
  } else if (currentType.value == 2) {
    account.value = email.value;
  } else if (currentType.value == 3) {
    account.value = google_auth_secret.value;
  }
}
const submit = () => {
  if (password.value.length < 6) {
    showToast(t('funpasswordTips'))
    return false
  }
  _setSafeword({
    safeword: password.value,
    verifcode_type: currentType.value,
    verifcode: verifyCode.value
  }).then((res) => {
    showToast(t('changeSuccess'))
    setTimeout(() => {
      router.push('/my/index')
    }, 1000)
  })
}
const getVerifTarget = async () => {
  _getVerifTarget({

  }).then((res) => {
    email_authority.value = res.email_authority
    google_auth_bind.value = res.google_auth_bind
    phone_authority.value = res.phone_authority
    email.value = res.email
    phone.value = res.phone
    google_auth_secret.value = res.google_auth_secret
  })

}
const senCode = () => {
  if (currentType.value == 1 && !phone_authority.value) {
    showToast(t('bindPhoneTips'));
    return false
  }
  if (currentType.value == 2 && !email_authority.value) {
    showToast(t('bindEmailTips'));
    return false
  }
  onChecked(currentType.value)
  if (time.value > 0) {
    return false
  }
  _sendVerifyCode({
    target: account.value,
  }).then((res) => {
    showToast(t('sendSuccess'));
    time.value = 30;
    timer.value = setInterval(() => {
      if (time.value > 0) {
        time.value = time.value - 1
      } else {
        time.value = 0;
        clearTimer()
      }
    }, 1000);
  })
}

const clearTimer = () => {
  clearInterval(timer.value)
  timer.value = null
}

onUnmounted(() => {
  clearTimer()
})

</script>

<style lang="scss" scoped>
.changePassword {
  width: 100%;
  box-sizing: border-box;
}

.line {
  width: 100%;
  height: 1px;
  background: $light-grey;
}

.content {
  padding: 16px;
  font-size: 13px;
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
</style>
