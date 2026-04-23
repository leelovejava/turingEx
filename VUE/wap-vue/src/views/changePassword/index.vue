<template>
    <div class="changePassword">
        <fx-header>
            <template #title>
                {{ $t('changeLoginPassword') }}
            </template>
        </fx-header>
        <div class="content">
            <ExInput :label="$t('oldPassword')" :placeholderText="$t('entryPassword')" v-model="oldPassword"
                typeText="password" />
            <ExInput :label="$t('newPassword')" :placeholderText="$t('entryPassword')" :tips="$t('setPasswordTips')"
                v-model="newPassword" typeText="password" />
            <ExInput :label="$t('sureNewPassword')" :placeholderText="$t('entryPassword')" :tips="$t('setPasswordTips')"
                v-model="rePassword" typeText="password" />
            <van-button class="w-full" style="margin-top:10px;" type="primary" @click="submit">{{ $t('sure') }}
            </van-button>
        </div>
    </div>
</template>

<script setup>
import ExInput from "@/components/ex-input/index.vue";
import { _changePassword } from '@/service/user.api.js'
import { ref } from "vue";
import { showToast } from "vant";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
const { t } = useI18n()
const router = useRouter()

const oldPassword = ref('')
const newPassword = ref('')
const rePassword = ref('')

const submit = () => {
    _changePassword({
        old_password: oldPassword.value,
        password: newPassword.value,
        re_password: rePassword.value,
    }).then((res) => {
        showToast(t('changeSuccess'))
        setTimeout(() => {
            router.push('/my/index')
        }, 1000);
    })
}
</script>

<style lang="scss" scoped>
.changePassword {
    width: 100%;
    box-sizing: border-box;
}

.line {
    width: 100%;
    height: 2px;
    background: $light-grey;
}

.content {
    padding: 16px;
    font-size: 13px;
}

.hightLight {
    background: $btn_main;
    color: $text_color;
}
</style>
