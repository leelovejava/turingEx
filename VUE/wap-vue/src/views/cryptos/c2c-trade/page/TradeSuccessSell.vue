<template>
  <div>
    <div id="TradeSuccessSell">
      <trade-success>
        <template #price>

          <div class="w-full text-center">
            <div>
              <span class="relative bottom-2 mr-4 text-36">{{ currencySymbol }}</span>
              <span class="text-60">{{ detail.amount }}</span>
            </div>
            <div class="mt-3 font-normal text-28 text-grey">
              {{ $t('您已成功出售') }}
              <span class="mx-2">{{ detail.symbol == 'usdt' ? Math.floor(detail.coinAmount * 100) / 100 :
                Math.floor(detail.coinAmount * 100000) / 100000 }}</span>
              <span>{{ detail.symbol && detail.symbol.toLocaleUpperCase() }}</span>
            </div>
          </div>
        </template>
        <template #desc>
          <span>{{ $t('请您对买家进行评价') }}</span>
        </template>
      </trade-success>
    </div>
  </div>
</template>

<script>
import TradeSuccess from "@/views/cryptos/c2c-trade/components/TradeSuccess.vue";
import otcApi from "@/service/otc.api";
import { mapGetters } from "vuex";
export default {
  name: "TradeSuccessSell",
  components: {
    TradeSuccess,
  },
  data() {
    return {
      detail: {}
    }
  },
  computed: {
    ...mapGetters("c2c", ['currencySymbol']),
  },
  created() {
    const order_no = this.$store.state.c2c.order_no
    otcApi.ctcOrderGetDetail({ order_no, language: this.$i18n.locale }).then(res => {
      this.detail = res
    })
  }
}
</script>

<style lang="scss" scoped>
#TradeSuccessSell {
  font-size: 30px;
}
</style>