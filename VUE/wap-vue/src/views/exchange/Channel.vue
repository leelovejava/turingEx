<template>
    <div class="channel">
        <van-nav-bar :left-arrow="true" @click-left="onClickLeft">
            <template #title>{{ title }}</template>
            <template #right>
                <van-icon name="cross" @click="onRoute('/trade/index')"></van-icon>
            </template>
        </van-nav-bar>
        <h2 class="px-4 mt-4 select-title">{{ label }} </h2>
        <ul class="mt-4 px-4 flex flex-wrap">
            <li class="item-type rounded flex flex-col p-4 justify-center items-center" v-for="(item, index) in list"
                :key="index" @click="onRoute(item.path)">
                <img :src="item.icon" />
                <p class="mt-2">{{ item.title }} </p>
            </li>
        </ul>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from "vue-i18n";

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const isIn = route.name === 'channelIn'

const title = isIn ? t('recharge') : t('withdraw')

const label = isIn ? t('selectRechargeEeor') : t('selectWithdrawEeor')

const list = isIn ? [
    { title: 'USDT', icon: new URL('@/assets/image/symbol/usdt.png', import.meta.url), path: '/cryptos/recharge/rechargePage?symbol=usdt' },
    { title: 'USDC', icon: new URL('@/assets/image/symbol/usdc.png', import.meta.url), path: '/cryptos/recharge/rechargePage?symbol=usdc' },
    { title: 'BTC', icon: new URL('@/assets/image/symbol/btc.png', import.meta.url), path: '/cryptos/recharge/rechargePage?symbol=btc' },
    { title: 'ETH', icon: new URL('@/assets/image/symbol/eth.png', import.meta.url), path: '/cryptos/recharge/rechargePage?symbol=eth' },
] : [
    { title: 'USDT', icon: new URL('@/assets/image/symbol/usdt.png', import.meta.url), path: '/cryptos/withdraw/withdrawPage?type=exchange' },
    { title: 'USDC', icon: new URL('@/assets/image/symbol/usdc.png', import.meta.url), path: '/cryptos/withdraw/withdrawPage?type=exchange' },
    { title: 'BTC', icon: new URL('@/assets/image/symbol/btc.png', import.meta.url), path: '/cryptos/withdraw/withdrawPage?type=exchange' },
    { title: 'ETH', icon: new URL('@/assets/image/symbol/eth.png', import.meta.url), path: '/cryptos/withdraw/withdrawPage?type=exchange' },
]

const onRoute = (path) => {
    router.push(path)
}

const onClickLeft = () => {
    router.push('/trade/index')
}
</script>
<style lang="scss" scoped>
.item-type {
    width: calc(50% - 6px);
    margin: 0 3px 6px 3px;
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
