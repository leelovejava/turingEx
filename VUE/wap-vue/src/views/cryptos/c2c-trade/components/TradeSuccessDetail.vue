<template>
  <div id="TradeSuccessDetailPage">
    <div id="full" style="z-index: 100;overflow: auto;"
      class="fixed top-0 left-0 w-full h-full c2cBackground1 tradeSuccessDetail">
      <div :class="orderDetail.state == 3 ? 'green' : 'greyBg'">
        <order-nav :back="back" :class="orderDetail.state == 3 ? 'green' : 'greyBg'"
          @back="$router.replace('/cryptos/wantBuy')">
          <template #left>
            <van-icon name="arrow-left" class="arrow-left-icon" />
          </template>
          <template #right>
            <div class="flex items-center chat flex justify-center" @click="$router.push({
              path: '/cryptos/chat'
            })">
              <van-badge class="w-9 h-8 mr-5">
                <img class="w-full h-full" v-if="orderDetail.state == 3" src="~@/assets/image/c2c/Vector-white.png"
                  alt="" />
                <img class="w-full h-full" v-else src="~@/assets/image/c2c/Vector-black.png" alt="" />
              </van-badge>
              <span class="text-28" :class="orderDetail.state == 3 ? 'text-white' : 'c2cColor'">
                <span>{{ orderDetail.direction == 'buy' ? $t('联系卖家') : $t('联系买家') }}</span>
              </span>
            </div>
          </template>
        </order-nav>
        <div class="flex justify-between items-center mt-5 px-8 pb-12 text-white">
          <div>
            <div class="text-50" :class="orderDetail.state != 3 ? 'c2cColor' : ''">{{ fixStr() }}</div>
            <div class="mt-7 text-20" v-if="orderDetail.state == 3">
              {{ $t('您已成功') }}&nbsp;{{ orderDetail.direction == 'buy' ? $t('购买') : $t('出售') }}&nbsp;{{
                orderDetail.symbol.toLowerCase() == 'usdt' ? Math.floor(orderDetail.amountUsdt * 100) / 100 :
                Math.floor(orderDetail.coinAmount * 10000) / 10000 }}&nbsp;{{ orderDetail.symbol.toUpperCase() }}
            </div>
            <div class="mt-6 text-26 c2cColor" v-else>{{ fixText() }}</div>
          </div>
          <div>
            <img v-if="orderDetail.state == 3" class="w-24 h-24" src="~@/assets/image/c2c/Group181.png" alt="">
            <img v-else class="w-24 h-24" src="~@/assets/image/c2c/Group1212.png" alt="">
          </div>
        </div>
      </div>
      <div class="pt-11 pb-8 c2cTabBg">
        <order-data :detail="orderDetail">
          <div class="w-full h-4 mb-10 diviLine"></div>
        </order-data>
      </div>
      <div class="px-8 mt-4 mainBackground py-10">
        <h2 class="font-normal text-30 c2cColor">{{ $t('交易方式') }}</h2>
        <div class="flex items-center mt-7">
          <div class="w-1 h-7 border-ra" style="background: #E7BB41;"></div>
          <span class="ml-2 text-24 text-grey">{{ orderDetail.methodName }}</span>
        </div>
      </div>
      <!--    <van-divider/>-->
      <!--    <evaluation class="pb-60 bg-white">-->
      <!--      <template #desc>-->
      <!--        <slot name="desc"></slot>-->
      <!--      </template>-->
      <!--    </evaluation>-->
      <div class="mt-4">
        <div class="flex justify-between items-center h-24 px-8 mt-4 c2cTabBg c2cColor" @click="tokefu">
          <div>
            <span class="text-30 c2cColor">{{ $t('联系客服') }}</span>
          </div>
          <div>
            <van-icon name="arrow" class="font-bold" />
          </div>
        </div>
        <div class="flex justify-between items-center h-24 px-8 mt-4 c2cTabBg c2cColor"
          @click="$router.push('/cryptos/Appeal')">
          <div>
            <span class="text-30 c2cColor">{{ $t('对订单存在疑问') }}</span>
          </div>
          <div>
            <van-icon name="arrow" class="font-bold" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  mapState
} from "vuex";
import {
  Badge,
  Divider,
  Cell,
  CellGroup,
  Icon,
} from "vant";
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import OrderData from "@/views/cryptos/c2cOrder/components/order-data/OrderData.vue";
import Evaluation from "@/views/cryptos/c2cOrder/payment/components/Evaluation.vue";
import otcApi from "@/service/otc.api";
export default {
  name: "TradeSuccessDetail",
  props: ['title', 'back'],
  data() {
    return {
      checked: false,
      orderDetail: {},
      timer: null
    }
  },
  components: {
    [Badge.name]: Badge,
    [Divider.name]: Divider,
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    [Icon.name]: Icon,
    OrderNav,
    OrderData,
    Evaluation,
  },
  created() {
    this.getOrderStatus()
    this.timer = setInterval(() => {
      this.getOrderStatus()
    }, 2000);
  },
  beforeUnmount() {
    clearInterval(this.timer)
    this.timer = null
  },
  methods: {
    async getOrderStatus() {
      const order_no = this.$store.state.c2c.order_no
      const res = await otcApi.ctcOrderGetDetail({ order_no, language: this.$i18n.locale });
      this.orderDetail = res;
      if (this.orderDetail.state / 1 === 1) {
        this.$router.push('/cryptos/confirmedPaid')
      }
    },
    fixStr() {
      let str = ''
      if (this.orderDetail.state == 1) {
        str = ''
      } else if (this.orderDetail.state == 2) {
        str = this.$t('申诉中')
      } else if (this.orderDetail.state == 3) {
        str = this.$t('已完成')
      } else if (this.orderDetail.state == 4) {
        str = this.$t('已取消')
      } else if (this.orderDetail.state == 5) {
        str = this.$t('已超时')
      }
      return str
    },
    fixText() {
      let str = ''
      if (this.orderDetail.state == 2) {
        str = this.$t('您的订单正在申诉处理中')
      } else if (this.orderDetail.state == 4) {
        str = this.$t('您已取消订单')
      } else if (this.orderDetail.state == 5) {
        str = this.$t('您的订单已超时')
      }
      return str
    },
    tokefu() {
      this.$router.push('/workerOrder')
    }
  }
}
</script>

<style lang="scss" scoped>
#TradeSuccessDetailPage {
  font-size: 30px;

  .tradeSuccessDetail {
    .arrow-left-icon {
      color: $text_color;
    }

    :deep(.van-nav-bar__right) {
      padding: 0;
    }

    :deep(.van-nav-bar__arrow) {
      color: #fff;
    }

    :deep(.greyBg .van-nav-bar) {
      background: $grey_bg;
    }

    :deep(.green .van-nav-bar) {
      background: #2EBD85;
    }

    :deep(.van-cell) {
      padding: 0 32px !important;
      background: $c2c-tab-bg;

      &:last-child {
        margin-bottom: 0;
      }
    }

    :deep(.van-cell-group) {
      background: $c2c-tab-bg;
    }

    :deep(.van-cell__title) {
      color: $text_color1;
    }

    :deep(.van-cell__value) {
      color: $text_color;
    }
  }

  .switch {
    transform: scale(.7) translateX(20px);
  }

  .chat {
    width: 232px;
    height: 64px;
    border-radius: 36px 0px 0px 36px;
  }

  .green {
    background: #2EBD85;
  }

}
</style>

