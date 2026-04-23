<template>
    <div class="channel">
        <!-- <fx-header @back="openUrl(1)"> -->
        <!-- <template #left>{{}}</template>
            <template #title>{{ title }}</template>
            <template #right>
                <van-icon name="cross" @click="onRoute('list')"></van-icon>
            </template> -->
        <!-- </fx-header> -->
        <van-nav-bar :left-arrow="true" @click-left="onClickLeft">
            <template #title>{{ title }}</template>
            <template #right>
                <van-icon name="cross" @click="onRoute('/trade/index')"></van-icon>
            </template>
        </van-nav-bar>
        <h2 class="px-4 mt-4 select-title">{{ label }} </h2>
        <ul class="mt-4 px-4 flex">
            <li class="item-type rounded flex flex-col p-4 justify-center items-center" v-for="(item, index) in list"
                :key="index" @click="onRoute(item.path)">
                <img :src="item.icon" />
                <p class="mt-2">{{ item.title }} </p>
            </li>
        </ul>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from "vue-i18n";
import { _foreignRechargeWith } from "@/service/recharge.api";

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const isFinishRecharge = ref(true)
const orderNo = ref('')
const isIn = route.name === 'channelIn'

const title = isIn ? t('recharge') : t('withdraw')

const label = isIn ? t('selectRechargeEeor') : t('selectWithdrawEeor')

const list = isIn ? [
    { title: t('bankDeposit'), icon: new URL('@/assets/image/order/user-icon1.png', import.meta.url), path: 'charge-bank' },
    // { title: 'USDT ' + t('recharge'), icon: new URL('@/assets/image/order/user-icon2.png', import.meta.url), path: '/cryptos/recharge/rechargeList?isForeign=true' },
] : [
    { title: t('bankwithdrawal'), icon: new URL('@/assets/image/order/user-icon1.png', import.meta.url), path: 'withdraw-bank' },
    // { title: 'USDT ' + t('withdraw'), icon: new URL('@/assets/image/order/user-icon2.png', import.meta.url), path: '/cryptos/withdraw/withdrawPage?type=exchange' },
]

onMounted(() => {
    foreignRechargeWith()
})

const onRoute = (path) => {
    if (path === 'charge-bank') {
        isFinishRecharge.value ? router.push('charge-bank') : router.push(`/order/submit?orderNo=${orderNo.value}`)
    } else {
        router.push(path)
    }
}
const onClickLeft = () => {
    router.push('/trade/index')
}

// 银行卡充值
const foreignRechargeWith = () => {
    _foreignRechargeWith({
        page_no: 1,
        direction: 'recharge'
    }).then((res) => {
        // 判断上一笔是否结束充值
        if (res && res.length > 0 && res[0].state === '0') {
            isFinishRecharge.value = false
            orderNo.value = res[0].order_no
        } else {
            isFinishRecharge.value = true
        }
    })
}



</script>
<style lang="scss" scoped>
.item-type {
    flex: 1;
    margin: 0 6px;
    font-size: 14px;
    border: 1px solid $border_color;
    height: 130px;
    color: $dark-grey;

    img {
        width: 55px;
        height: 55px;
    }
}

:deep(.van-icon) {
    font-size: 18px;
    color: $text_color;
}

.channel {
    font-size: 14px;
}
</style>