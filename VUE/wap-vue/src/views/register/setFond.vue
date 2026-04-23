<template>
    <div class="setFond">
        <div class="header">
            <div class="flex items-center" @click="$router.go(-1)"><img
                    src="../../assets/image/assets-center/left-arrow.png" alt="" class="leftReturn" /></div>
        </div>
        <div class="content">
            <div class="title textColor">{{ $t('setFundPassword') }}</div>
            <ExInput :label="$t('password')" :placeholderText="$t('funpasswordTips')" v-model="password"
                typeText="password" />
            <ExInput :label="$t('repassword')" :placeholderText="$t('surePassword')" v-model="repassword"
                typeText="password" />
            <van-button class="w-full" style="margin-top:10px;" type="primary" @click="submitBind">{{ $t('sure') }}
            </van-button>
        </div>
    </div>
</template>

<script setup>
import { _setSafewordReg } from "@/service/user.api.js";
import ExInput from "@/components/ex-input/index.vue";
import { ref } from "vue";
import { showToast } from 'vant'
import { useI18n } from 'vue-i18n'
import { useRouter } from "vue-router";

const { t } = useI18n()
const router = useRouter()


const password = ref('')
const repassword = ref('')


const setSafewordReg = () => {
    _setSafewordReg({
        safeword: repassword.value
    }).then((res) => {
        // showToast(t('bindSuccess'));
        console.log('sss')
        router.push('/identity')
    }).catch((error) => {
        showToast(t(error.msg));
    });
}
const submitBind = () => {
    if (password.value.length < 6 || repassword.value.length < 6) {
        showToast(t('funpasswordTips'))
        return false
    }
    if (password.value !== repassword.value) {
        showToast(t('noSamePassword'))
        return false
    }
    setSafewordReg()
}

</script>

<style lang="scss" scoped>
.setFond {
    width: 100%;
    box-sizing: border-box;
    font-size: 13px;
    padding: 0 16px;
}

.header {
    display: flex;
    justify-content: space-between;
    padding: 0 13px;
    font-size: 14px;
    height: 50px;
    line-height: 50px;
}

.title {
    font-weight: 700;
    font-size: 26px;
    margin-top: 25px;
    margin-bottom: 30px;
}
</style>