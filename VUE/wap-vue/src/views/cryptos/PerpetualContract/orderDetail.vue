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
        <div class="flex justify-between cell-item">
          <div class="text-grey">{{ $t('止盈价格') }}</div>
          <div class="textColor">{{ detail.stop_price_profit || '--' }}</div>
        </div>
        <div class="flex justify-between cell-item">
          <div class="text-grey">{{ $t('止损价格') }}</div>
          <div class="textColor">{{ detail.stop_price_loss || '--' }}</div>
        </div>
        <div class="flex justify-center cell-item mt-30" v-if="detail.state !== 'created'">
          <van-button type="primary" size="small" @click="openStopLossProfit">{{ $t('设置止盈止损') }}</van-button>
        </div>
      </div>
    </div>
    <van-popup v-model:show="stopLossProfitVisible" round position="bottom" :style="{ height: '50%' }">
      <div class="p-4">
        <div class="text-center text-32 font-semibold mb-4">{{ $t('设置止盈止损') }}</div>
        <van-field v-model="stopPriceProfit" :label="$t('止盈价格')" :placeholder="$t('请输入止盈价格')" type="number" />
        <van-field v-model="stopPriceLoss" :label="$t('止损价格')" :placeholder="$t('请输入止损价格')" type="number" />
        <div class="flex gap-4 mt-4">
          <van-button block @click="stopLossProfitVisible = false">{{ $t('取消') }}</van-button>
          <van-button type="primary" block @click="submitStopLossProfit">{{ $t('确定') }}</van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script>
import { _orderHoldDetail, _stopLossAndProfit } from "@/service/trade.api";
import assetsHead from "@/components/Transform/assets-head/index.vue";
import { Popup, Field, Button, showToast } from "vant";
import dayjs from 'dayjs'
export default {
  name: "orderDetail",
  data() {
    return {
      detail: {

      },
      timer: null,
      stopLossProfitVisible: false,
      stopPriceProfit: '',
      stopPriceLoss: '',
    }
  },
  components: {
    assetsHead,
    [Popup.name]: Popup,
    [Field.name]: Field,
    [Button.name]: Button,
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
    openStopLossProfit() {
      this.stopPriceProfit = this.detail.stop_price_profit || '';
      this.stopPriceLoss = this.detail.stop_price_loss || '';
      this.stopLossProfitVisible = true;
    },
    submitStopLossProfit() {
      _stopLossAndProfit({
        order_no: this.detail.order_no,
        stop_price_profit: this.stopPriceProfit,
        stop_price_loss: this.stopPriceLoss,
      }).then(() => {
        showToast(this.$t('修改成功'));
        this.stopLossProfitVisible = false;
        this.fetchDetail(this.detail.order_no);
      })
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
