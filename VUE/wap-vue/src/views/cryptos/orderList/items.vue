<template>
  <div class="item-main" @click="gotoPage">
    <div class="flex justify-between items-center buy">
      <span class="us-buy">
        <span v-if="items.direction === 'buy'" style="color: #5eba89">{{ $t('购买') }}</span>
        <span v-else style="color: #e35461">{{ $t('出售') }}</span>
        {{ items.symbol }}
      </span>
      <span v-if="items.direction === 'buy'">
        <span
          :class="{ 'blue': items.state === '0', 'yellow': items.state === '1', 'red': items.state === '2' || items.state === '5', 'green': items.state === '3' }">{{
            arr[items.state / 1] }}</span>
        <van-icon name="arrow" />
      </span>
      <span v-else>
        <span
          :class="{ 'blue': items.state === '0', 'yellow': items.state === '1', 'red': items.state === '2' || items.state === '5', 'green': items.state === '3' }">{{
            arr1[items.state / 1] }}</span>
        <van-icon name="arrow" />
      </span>
    </div>
    <div class="flex justify-between items-center price-i">
      <span>{{ $t('单价') }} {{ items.currency || currency.currency_symbol }} {{ items.symbol_value }}</span>
      <span style="color: #b8bcc5">{{ items.time }}</span>
    </div>
    <div class="flex justify-between items-center price-i pd-32">
      <span>{{ $t('数量') }} {{ (items.coin_amount / 1).toFixed(items.symbol === 'USDT' ? 2 : 4) }} {{ items.symbol
      }}</span>
      <span class="textColor" style="font-weight: bold">{{ items.currency }}
        {{ (items.symbol_value * items.coin_amount / 1).toFixed(2) }}</span>
    </div>
    <div class="niu items-center">
      <van-badge v-if="items.msg" :content="items.msg">
        <img src="@/assets/image/Subtract.png" class="mr-20" alt="" />
      </van-badge>
      <img v-else src="@/assets/image/Subtract.png" class="mr-20" alt="" />
      {{ items.c2c_user_nick_name }}
      <!-- <img src="@/assets/image/up.png" alt="" />
      资金安全 -->
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import { Icon, Badge } from 'vant';

export default {
  props: ['items'],
  data() {
    return {
      arr: [this.$t('未付款'), this.$t('已付款'), this.$t('申诉中'), this.$t('已完成'), this.$t('已取消'), this.$t('已超时')],
      arr1: [this.$t('等待付款'), this.$t('待确认'), this.$t('申诉中'), this.$t('已完成'), this.$t('已取消'), this.$t('已超时')],
    }
  },
  computed: {
    ...mapState('home', ['currency'])
  },
  methods: {
    gotoPage() { // 去到相应的页面
      // console.log(this.items)
      // this.$store.commit('c2c/SET_ADV_ID',this.items.id)
      this.$store.commit('c2c/SET_ORDER_NO', this.items.order_no)
      // return
      if (this.items.direction === 'buy') {
        if (this.items.state / 1 === 0 || this.items.state / 1 === 5) { // 未付款
          this.$router.push(`/cryptos/orderGeneration`)
        } else if (this.items.state / 1 === 1) { // 已付款
          this.$router.push('/cryptos/paymentDetail')
        } else { /// 111  其他
          this.$router.push('/cryptos/tradeOrderDetail')
        }
      } else {
        if (this.items.state / 1 === 0 || this.items.state / 1 === 5) { // 未付款
          this.$router.push(`/cryptos/sellGenerate`)
        } else if (this.items.state / 1 === 1) { // 已付款
          this.$router.push(`/cryptos/confirmedPaid`)
          // this.$router.push('/paymentDetail?order_no=' + this.items.order_no)
        } else { /// 111  其他
          this.$router.push('/cryptos/tradeOrderDetail')
        }
      }
    }
  },
  components: {
    [Icon.name]: Icon,
    [Badge.name]: Badge,
  },
}
</script>

<style lang="scss" scoped>
.item-main {
  padding: 42px 0 66px;
  margin: 0 30px;
  font-size: 28px;
  color: #868d9a;
  border-bottom: 1px solid $line_color;

  .buy {
    margin-bottom: 40px;
    font-size: 32px;

    .us-buy {
      color: #fff;
    }
  }

  .price-i {
    margin-bottom: 28px;
  }

  .pd-32 {
    margin-bottom: 32px;
  }

  .niu {
    min-width: 150px;
    height: 57px;
    overflow: hidden;
    background: $tab_background;
    border-radius: 50px;
    font-size: 22px;
    padding: 0 20px;
    display: inline-flex;
    
    img {
      width: 30px;
      height: 30px;
      margin-top: 6px;
    }
  }
}


:deep(.van-badge--fixed) {
  top: 80%;
}

.blue {
  color: #1D91FF;
}

.green {
  color: #2EBD85;
}

.red {
  color: #E35461
}

.yellow {
  color: #E2AE27;
}
</style>
