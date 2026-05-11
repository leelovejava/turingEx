<template>
    <div class="changeVerify">
        <fx-header>
            <template #title>
                {{ title }}
            </template>
        </fx-header>
        <div class="content">
            <div class="imgBox">
                <img v-if="currentType == 2" src="@/assets/image/userCenter/emailVerity.png" alt="" />
                <img v-if="currentType == 1" src="@/assets/image/userCenter/phoneVerity.png" alt="" />
                <img v-if="currentType == 3" src="@/assets/image/userCenter/googleVerity.png" alt="" />
            </div>
            <p>{{ currentType == 1 ? $t('phoneVerify') : currentType == 2 ? $t('emailVerify') :
                    $t('googleAuthApp')
            }}{{ $t('protectAccount') }}</p>
            <p v-if="currentType == 2 && maskedEmail" class="email-text">{{ maskedEmail }}</p>
            <van-button class="w-full" style="margin-top:10px;" type="primary" @click="goChange">
                {{ currentType == 1 ? $t('changePhoneVertify') : currentType == 2 ? $t('changeEmailVertify')
        : $t('changeGoogleVertify')
                }}
            </van-button>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useI18n } from "vue-i18n";
import { _getVerifTarget } from "@/service/user.api.js";
const router = useRouter()
const route = useRoute()
const { t } = useI18n()

const title = ref('')
const currentType = ref('')
const maskedEmail = ref('')

const maskEmail = (email) => {
    if (!email) return ''
    const [name, domain] = email.split('@')
    const [domainName, ...tlds] = domain.split('.')
    return `${name.slice(0, 2)}***@***.${tlds.join('.')}`
}

onMounted(() => {
    init()
    _getVerifTarget({}).then(res => {
        if (res.email) maskedEmail.value = maskEmail(res.email)
    })
})

const init = () => {
    let type = route.query.type;
    currentType.value = type;
    if (type == 1) {
        title.value = t("phoneVerify");
    } else if (type == 2) {
        title.value = t("emailVerify");
    } else if (type == 3) {
        title.value = t("googleVertifyBind");
    }
}
const goChange = () => {
    router.push({ name: 'resetVerify', query: { type: currentType.value } })
}

</script>

<style lang="scss" scoped>
.changeVerify {
    width: 100%;
    box-sizing: border-box;
}


.content {
    font-size: 13px;
    padding: 0 16px;
    margin-top: 30px;
    text-align: center;
}

.imgBox {
    width: 88px;
    height: 88px;
    margin: auto;

    img {
        width: 100%;
        height: 100%;
    }
}

p {
    color: $dark-grey;
    font-size: 14px;
    margin-top: 22px;
}
</style>