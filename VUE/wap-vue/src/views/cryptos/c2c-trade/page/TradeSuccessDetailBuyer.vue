<template>
<div class="w-full h-full">
  <trade-success-detail
    :back="false"
    :title="$t('购买')"
  >
  </trade-success-detail>
</div>
</template>

<script>
import TradeSuccessDetail from "@/views/cryptos/c2c-trade/components/TradeSuccessDetail.vue";
export default {
  name: "TradeSuccessDetailBuyer",
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
