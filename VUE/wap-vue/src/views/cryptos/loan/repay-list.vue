<template>
  <div id="cryptos">
    <div class="recharge box-border">
      <assets-head :title="$t('还款通道')"></assets-head>
      <div class="pl-9 pr-9">
        <div class="recharge-title text-36 textColor">{{ $t('请选择还款币种') }}</div>
        <div class="recharge-list flex flex-wrap justify-start">
          <div class="item-view flex flex-col items-center justify-center text-center w-48 h-56 box-border"
            @click="selectSymbol('usdt')">
            <img src="../../../assets/image/symbol/usdt.png" class="w-24 h-92" />
            <div class="text-grey text-26 mt-8">USDT</div>
          </div>
          <div class="item-view flex flex-col items-center justify-center text-center w-48 h-56 box-border"
            @click="selectSymbol('btc')">
            <img src="../../../assets/image/symbol/btc.png" class="w-24 h-92" />
            <div class="text-grey text-26 mt-8">BTC</div>
          </div>
          <div class="item-view flex flex-col items-center justify-center text-center w-48 h-56 box-border"
            @click="selectSymbol('eth')">
            <img src="../../../assets/image/symbol/eth.png" class="w-24 h-92" />
            <div class="text-grey text-26 mt-8">ETH</div>
          </div>
          <div class="item-view flex flex-col items-center justify-center text-center w-48 h-56 box-border"
            @click="selectSymbol('usdc')">
            <img src="../../../assets/image/symbol/usdc.png" class="w-24 h-92" />
            <div class="text-grey text-26 mt-8">USDC</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import assetsHead from "@/components/Transform/assets-head/index.vue";
export default {
  name: 'repayList',
  components: { assetsHead },
  methods: {
    selectSymbol(symbol) {
      this.$router.push({ path: '/cryptos/repayCrypto', query: { symbol } });
    }
  }
}
</script>
<style lang="scss" scoped>
#cryptos {
  .recharge { width: 100%; box-sizing: border-box; }
  .recharge-title { margin: 86px 0 104px 0; }
  .recharge-list > div {
    border: 2px solid $border_color;
    border-radius: 15px;
    box-sizing: border-box;
    margin-right: 10px;
    margin-bottom: 10px;
  }
}
</style>
