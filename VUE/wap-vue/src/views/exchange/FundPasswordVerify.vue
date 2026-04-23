<template>
    <div class="setFond">
        <fx-header>

        </fx-header>
        <div class="content">
            <div class="title textColor">{{ $t('safeVertify') }}</div>
            <ExInput :label="$t('enterPassword')" :placeholderText="$t('funpasswordTips')" v-model="password"
                typeText="password" />
            <van-button class="w-full" style="margin-top:10px;" @click="submitBind" type="primary">{{ $t('confirm') }}
            </van-button>
            <p @click="router.push('/resetVerify?type=0')">{{ $t('enterNotPassword') }}</p>
        </div>
    </div>
</template>

<script setup>
import ExInput from "@/components/ex-input/index.vue";
import { _withdrawApply } from "@/service/withdraw.api";
import { c2cOrder, _getSessionToken } from "@/service/recharge.api";
import { ref, onMounted } from "vue";
import { showToast } from 'vant'
import { useI18n } from 'vue-i18n'
import { useRouter, useRoute } from "vue-router";
import { useUserStore } from '@/store/user';
const userStore = useUserStore()
const { t } = useI18n()
const router = useRouter()
const route = useRoute()

const password = ref('')
const payInfo = ref({})
const banType = ref({})
onMounted(async () => {
    if (route.query.payInfo) {
        payInfo.value = JSON.parse(route.query.payInfo)
    }
    if (route.query.bankType) {
        banType.value = route.query.bankType
    }
})
const submitBind = () => {
    if (password.value.length < 6) {
        showToast(t('funpasswordTips'))
        return false
    }
    if (route.query.type === 'bank') {
        // router.push('/order/submit')
        payInfo.value.safeword = password.value
        c2cOrder(payInfo.value).then((res) => {
            if (banType.value == 'recharge') {
                router.push('/order/submit?orderNo=' + res.order_no)
            } else {
                router.push('/order/apply-success?currentType=withdraw&type=bank')
            }
        }).catch(err => {
            _getSessionToken({}).then(res => {
                payInfo.value.session_token = res.session_token
            })
        })

    } else {
        _withdrawApply({
            session_token: route.query.session_token,
            amount: route.query.amount,
            from: route.query.from,
            safeword: password.value,
            channel: route.query.channel,
            token: userStore.userInfo && userStore.userInfo.token
        }).then((res) => {
            router.push('/order/apply-success?currentType=withdraw&type=hb')
        })

    }
}

</script>

<style lang="scss" scoped>
.setFond {
    width: 100%;
    box-sizing: border-box;
    font-size: 13px;
}

.content {
    padding: 0 16px;

    p {
        color:  $active_line;
        margin-top: 40px;
        text-align: center;
    }
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