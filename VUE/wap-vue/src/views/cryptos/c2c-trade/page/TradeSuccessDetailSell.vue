<template>
  <div class="w-full h-full">
    <trade-success-detail
        :title="$t('出售')"
    >
      <template #price>
        <div>{{$t('您已成功出售')}}<span class="mx-8">{{ orderInfo.coinAmount }}</span>{{ orderInfo.symbol.toLocaleUpperCase() }}</div>
      </template>
    </trade-success-detail>
  </div>
</template>

<script>
import {
  mapState
} from "vuex";
import TradeSuccessDetail from "@/views/cryptos/c2c-trade/components/TradeSuccessDetail.vue";
export default {
  name: "TradeSuccessDetailSell",
  computed: {
    ...mapState('c2cSell', ['orderInfo'])
  },
  components: {
    TradeSuccessDetail,
  }
}
</script>

<style lang="scss" scoped>
::deep(.order-data) {
  .title {
    padding-left: 32px;
  }
}
</style>
