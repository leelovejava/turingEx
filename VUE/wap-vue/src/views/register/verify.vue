<template>
    <div class="verify">
        <div class="header">
            <div class="flex items-center" @click="$router.go(-1)"><img
                    src="../../assets/image/assets-center/left-arrow.png" alt="" class="leftReturn" /></div>
            <div class="textColor" @click="$router.push('/setFond')">{{ $t('skip') }}</div>
        </div>
        <div class="content">
            <div class="title textColor" v-if="type == 1">{{ $t('emailVerify') }}</div>
            <div class="title textColor" v-if="type == 2">{{ $t('phoneVerify') }}</div>
            <p v-if="type == 1">{{ $t('verifyEmailTips', { 'account': account }) }}</p>
            <p v-if="type == 2">{{ $t('verifyPhoneTips', { 'account': account }) }}</p>
            <div class="iptbox inputBackground">
                <input class="inputBackground textColor" type="text" :placeholder="$t('entryVerifyCode')"
                    v-model="verifyCode">
                <span @click="senCode">{{ $t('reSendVerifyCode') }} <template v-if="time"> ({{
                    time
                }})s</template></span>
            </div>
            <van-button class="w-full" style="margin-top:10px;" type="primary" @click="bound">{{ $t('bind') }}
            </van-button>
        </div>
    </div>
</template>

<script setup>
import { _sendVerifyCode } from "@/service/login.api";
import { _bindEmail, _bindPhone } from "@/service/user.api.js";
import { ref, reactive, onMounted, onBeforeMount, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n'
import { showToast } from "vant";

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const verifyCode = ref('')
const type = ref('')
const account = ref('')
const state = reactive({
    timer: null
})
const time = ref(0)

onMounted(() => {
    type.value = route.query.type;
    account.value = route.query.account;
    clearInterval(state.timer)
    state.timer = null
    senCode();
})

const bound = () => {
    if (verifyCode.value.length < 6) {
        showToast(t('entryVerifyTips'));
        return
    }

    switch (type.value * 1) {
        case 1:
            {
                bindEmail()
                break;
            }
        case 2:
            {
                bindPhone()
                break;
            }
    }

}
const bindEmail = () => {
    _bindEmail({
        email: account.value,
        verifcode: verifyCode.value
    }).then((res) => {
        showToast(t('bindSuccess'));
        console.log(123)
        router.push('/setFond')
    })
}
const bindPhone = () => {
    _bindPhone({
        phone: account.value,
        verifcode: verifyCode.value
    }).then((res) => {
        showToast(t('bindSuccess'));
        router.push('/setFond')
    }).catch((error) => {
        showToast(t(error.msg));
    });
}
const senCode = () => {
    if (time.value > 0) {
        return false
    }
    _sendVerifyCode({
        target: account.value,
    }).then((res) => {
        time.value = 30;
        state.timer = setInterval(() => {
            if (time.value > 0) {
                time.value = time.value - 1
            } else {
                time.value = 0;
                clearInterval(state.timer)
                state.timer = null
            }
        }, 1000);
    }).catch((error) => {
        showToast(t(error.msg));
    });
}
onUnmounted(() => {
    clearInterval(state.timer)
    state.timer = null
})

</script>

<style lang="scss" scoped>
.verify {
    font-size: 13px;
    padding: 0 16px;
    width: 100%;
    box-sizing: border-box;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 13px;
    font-size: 14px;
    height: 50px;
    line-height: 50px;
}

.title {
    font-weight: 700;
    font-size: 26px;
    margin-top: 27px;
    margin-bottom: 17px;
}

.content {
    p {
        color: $text_color1;
        font-size: 15px;
        margin-bottom: 25px;
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
}
</style>