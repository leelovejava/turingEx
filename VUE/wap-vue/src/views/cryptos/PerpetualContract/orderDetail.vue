<template>
  <!-- 合约订单详情 -->
  <div id="cryptos">
    <div class="orderDetail text-28">
      <assets-head :title="$t('订单详情')" />
      <div class="pt-16 textColor">
        <div class="text-center text-30">{{ $t('盈亏金额') }}</div>
        <div class="text-center mt-5 text-50" :class="{
          'text-green': detail.profit / 1 > 0,
          'text-red': detail.profit / 1 < 0,
        }">{{ detail.profit / 1 > 0 ? '+' + detail.profit : detail.profit }} ({{ detail.change_ratio }} %)</div>
        <div class="flex justify-between cell-item mt-8">
          <div class="text-grey">{{ $t('操作') }}</div>
          <div> {{ detail.direction == 'buy' ? $t('开多') : $t('开空') }}&nbsp;{{
            detail.name ? detail.name : '--'
          }}</div>
        </div>
        <div class="flex justify-between cell-item">
          <div class="text-grey">{{ $t('状态') }}</div>
          <div class="textColor">{{ detail.state ? handleText(detail.state) : '--' }}</div>
        </div>
        <div class="flex justify-between cell-item">
          <div class="text-grey">{{ $t('开仓金额') }}</div>
          <div class="textColor">{{ detail.amount_open }}</div>
        </div>
        <div class="flex justify-between cell-item">
          <div class="text-grey">{{ $t('可平金额') }}</div>
          <div class="textColor">{{ detail.amount }}</div>
        </div>
        <div class="flex justify-between cell-item">
          <div class="text-grey">{{ $t('保证金') }}</div>
          <div class="textColor">{{ detail.deposit }}</div>
        </div>
        <div class="flex justify-between cell-item">
          <div class="text-grey">{{ $t('杠杆') }}</div>
          <div class="textColor">{{ detail.lever_rate }}x</div>
        </div>
        <div class="flex justify-between cell-item">
          <div class="text-grey">{{ $t('手续费') }}</div>
          <div class="textColor">{{ detail.fee }}</div>
        </div>
        <div class="flex justify-between cell-item">
          <div class="text-grey">{{ $t('建仓成本') }}</div>
          <div class="textColor">{{ detail.trade_avg_price }}</div>
        </div>
        <div class="flex justify-between cell-item">
          <div class="text-grey">{{ $t('平仓价格') }}</div>
          <div class="textColor">{{ detail.close_avg_price }}</div>
        </div>
        <div class="flex justify-between cell-item">
          <div class="text-grey">{{ $t('订单号') }}</div>
          <div class="textColor">{{ detail.order_no }}</div>
        </div>
        <div class="flex justify-between cell-item">
          <div class="text-grey">{{ $t('开仓时间') }}</div>
          <div class="textColor">{{ detail.create_time }}</div>
        </div>
        <div class="flex justify-between cell-item ">
          <div class="text-grey">{{ $t('平仓时间') }}</div>
          <div class="textColor">{{ detail.close_time ? dayjs(detail.close_time * 1000).format('YYYY-MM-DD HH:mm:ss') :
            '--' }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { _orderHoldDetail } from "@/service/trade.api";
import assetsHead from "@/components/Transform/assets-head/index.vue";
import { Popup } from "vant";
import dayjs from 'dayjs'
export default {
  name: "orderDetail",
  data() {
    return {
      detail: {

      },
      timer: null
    }
  },
  components: {
    assetsHead,
  },
  created() {
    let order_no = this.$route.query.order_no;
    this.fetchDetail(order_no);
    this.timer = setInterval(() => {
      this.fetchDetail(order_no);
    }, 1000);
  },

  methods: {
    dayjs,
    handleText(state) {
      let str = '';
      if (state == 'created') {
        str = this.$t('已平仓')
      } else {
        str = this.$t('持仓')
      }
      return str;
    },
    handleWord(direction, offset, price_type) {
      let a = ''
      let b = ''
      if (price_type === 'limit') {
        a = this.$t('限价')
      } else {
        a = this.$t('市价')
      }
      console.log(direction)
      if (direction === 'buy' && offset === 'open') {
        b = this.$t('开多')
      } else if (direction === 'sell' && offset === 'open') {
        b = this.$t('开空')
      } else if (direction === 'buy' && offset === 'close') {
        b = this.$t('平多')
      } else {
        b = this.$t('平空')
      }
      return b
    },
    onClickLeft() {
      this.$router.go(-1);
    },
    fetchDetail(order_no) {
      _orderHoldDetail(order_no).then(data => {
        this.detail = data
        if (data.state === 'created') {
          this.clearTimer()
        }
      })
    },
    clearTimer() {
      clearInterval(this.timer)
      this.timer = null
    },
  },
  beforeUnmount() {
    this.clearTimer()
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  .orderDetail {
    width: 100%;
    box-sizing: border-box;
  }

  .grey-line {
    background-color: $light-grey;
    height: 15px;
  }

  .cell-item {
    padding: 0 30px 30px 30px;
  }

  .mt-30 {
    margin-top: 30px;
  }
}
</style>
