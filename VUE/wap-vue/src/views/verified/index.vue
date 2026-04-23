<template>
    <!-- 已认证 -->
    <div class="verified">
        <fx-header @back="loginOut">
            <template #title>
                {{ $t('authVerify') }}
            </template>
        </fx-header>
        <div class="flex flex-col px-8 justify-center items-center pt-32">
            <img src="../../assets/image/idImg/small-success.png" alt="success img" class="w-20 h-20 mt-10" />
            <div class="font16 textColor mt-14">{{ $t('submitSuccess') }}</div>
            <div class="font14 text-grey mt-6">{{ $t('tips3') }}</div>
            <div class="font14 text-grey ">{{ $t('tips4') }}</div>
            <button class="apply-btn w-full border-none rounded btnMain text-white text-36 h-24 rounded mt-48"
                @click="$router.go(-1)">{{ $t('back') }}</button>
        </div>
    </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
const { t } = useI18n()
const router = useRouter()
const loginOut = () => {
    router.push('/certificationCenter')
}
</script>
<style lang="scss" scoped>
.verified {
    width: 100%;
    box-sizing: border-box;

}

.font16 {
    font-size: 16px;
}

.font14 {
    font-size: 14px;
}

.textColor {
    margin-top: 3rem;
    font-size: 16px;
}

.text-grey {
    margin: 1rem 0 0 0;
}

.btnMain {
    background: $btn_background;
    margin-top: 40px;
    width: 90%;
    height: 45px;
    line-height: 45px;
    text-align: center;
}
</style>
