<template>
  <!-- 币币订单详情 -->
  <div id="cryptos">
    <div class="tradeDetail">
      <assets-head :title="$t('订单详情')" />
      <div class="header pl-8 pr-8 text-center ">
        <div class="textColor">{{ $t('币种') }}</div>
        <div class="mt-7 textColor">{{ detail.name ? detail.name.toUpperCase() : '--' }}</div>
        <div class="mt-7 text-green">{{ detail.state ? handleText(detail.state) : '--' }}</div>
      </div>
      <div class="pl-8 pr-8 pt-10 pb-14">
        <div class="flex justify-between pb-16">
          <div class="text-grey">{{ $t('操作') }}</div>
          <div class="textColor" :class="detail.offset == 'open' ? 'text-green' : 'text-red'">
            {{ detail.order_price_type ? handleWord(detail.order_price_type, detail.offset) : '--' }}</div>
        </div>
        <div class="flex justify-between pb-16">
          <div class="text-grey">{{ $t('成交价格') }}</div>
          <div class="textColor">{{ detail.close_price ?? '--' }}</div>
        </div>
        <div class="flex justify-between pb-16">
          <div class="text-grey">{{ $t('成交时间') }}</div>
          <div class="textColor">{{ detail.close_time ? detail.close_time : '--' }}</div>
        </div>
        <div class="flex justify-between pb-16">
          <div class="text-grey">{{ $t('手续费') }}</div>
          <div class="textColor">{{ detail.fee ?? '--' }}</div>
        </div>
        <div class="flex justify-between pb-16">
          <div class="text-grey">{{ $t('订单类型') }}</div>
          <div class="textColor">{{ detail.order_price_type ? handleType(detail.order_price_type) : '--' }}</div>
        </div>
        <div class="flex justify-between pb-16">
          <div class="text-grey">{{ $t('限价') }}</div>
          <div class="textColor">{{ detail.price ?? '--' }}</div>
        </div>
        <div class="flex justify-between pb-16">
          <div class="text-grey">{{ $t('订单号') }}</div>
          <div class="textColor">{{ detail.order_no ? detail.order_no : '--' }}</div>
        </div>
        <div class="flex justify-between pb-16">
          <div class="text-grey">{{ $t('委托时间') }}</div>
          <div class="textColor">{{ detail.create_time ? detail.create_time : '--' }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import assetsHead from "@/components/Transform/assets-head/index.vue";
import TradeApi from "@/service/trading.js";
export default {
  name: "symbolOrderDetail",
  data() {
    return {
      detail: {}
    }
  },
  components: {
    assetsHead,
  },
  methods: {
    handleText(state) {
      let str = '';
      if (state == 'submitted') {
        str = this.$t('submitted')
      } else if (state == 'canceled') {
        str = this.$t('canceled')
      } else {
        str = this.$t('已完成')
      }
      return str;
    },
    handleType(type) {
      let str = '';
      if (type == 'limit') {
        str = this.$t('限价委托')
      } else {
        str = this.$t('市价委托')
      }
      return str;
    },
    handleWord(price_type, offset) {
      let a = ''
      let b = ''
      if (price_type === 'limit') {
        a = this.$t('限价')
      } else {
        a = this.$t('市价')
      }
      if (offset === 'open') {
        b = this.$t('买入')
      } else {
        b = this.$t('卖出')
      }
      return a + '/' + b
    },
    onClickLeft() {
      this.$router.go(-1);
    },
    fetchDetail(order_no) {
      TradeApi.tradeDetail({ order_no }).then(res => {
        this.detail = res
      })
    }
  },
  beforeRouteEnter(to, from, next) {
    const { query: { order_no } } = to
    next(vm => {
      vm.fetchDetail(order_no)
    })
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  font-size: 32px;

  .grey-line {
    background-color: $light-grey;
    height: 15px;
  }

  .tradeDetail {
    width: 100%;
    box-sizing: border-box;
    min-height: 100vh;


    .header {
      padding-top: 42px;
    }
  }


  .text-green {
    color: $green !important;
  }

  .text-red {
    color: $red !important;
  }
}
</style>
