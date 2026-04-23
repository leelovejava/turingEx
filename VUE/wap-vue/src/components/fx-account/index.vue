<template>
  <div class="fx-account px-4 pt-4">
    <h1 class="font-bold text-3xl">{{ $t('trade') }}</h1>
    <div class="text-lg mt-2"><span>{{
      userStore.userInfo && userStore.userInfo.username
    }}(USD)</span></div>
    <p class="mt-2 text-gray">{{ $t('balance') }}</p>
    <div class="text-3xl font-bold">{{ total }}</div>
    <div class="flex mt-2 gap-x-4">
      <div class="flex-1 flex flex-col gap-y-2">
        <p class="text-gray">{{ $t('netWorth') }}</p>
        <p class="text-xl font-bold">{{ money_wallet }}</p>
        <van-button type="primary" @click="onRoute('/exchange/channel-in')">{{ $t('recharge') }}</van-button>
      </div>
      <div class="flex-1 flex flex-col gap-y-2">
        <p class="text-gray">{{ $t('UnrealizedProfit') }}</p>
        <p class="text-xl font-bold">{{ money_contract_profit }}</p>
        <van-button @click="onRoute('/exchange/channel-out')">{{ $t('withdraw') }}</van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from 'vue-router';
import { _getAllAssets } from "@/service/user.api.js";
import { useI18n } from "vue-i18n";
import { useUserStore } from '@/store/user';
const userStore = useUserStore()
const { t } = useI18n()
const router = useRouter()
const total = ref('--') //总值
const money_wallet = ref('--') //净值
const money_contract_profit = ref('--') //未结盈利
const interval = ref(null)


const onRoute = (path) => {
  router.push(path)
}

onMounted(() => {
  getAllAssets()
  interval.value = setInterval(() => {
    getAllAssets()
  }, 1000)
})

const getAllAssets = () => {
  _getAllAssets()
    .then(res => {
      total.value = res.total
      money_wallet.value = res.money_wallet
      money_contract_profit.value = res.money_contract_profit || '--'
    })
}

onBeforeUnmount(() => {
  if (interval.value) {
    clearInterval(interval.value)
  }
})
</script>
<style lang="scss" scoped>
:deep(.van-button--primary) {
  background: $btn_main !important;
}

:deep(.van-button--default) {
  background: #F1F1F1 !important;
}
</style>