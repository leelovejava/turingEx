<template>
  <div id="orderGenerationPage">
    <div id="order-generation" class="w-full">
      <order-nav />
      <div class="mt-10 px-8 c2cColor">
        <h1 class="text-48 font-normal">{{ $t('订单已生成') }}</h1>
        <div class="flex items-center mt-4 text-28">
          <span>{{ $t('请在倒计时时间内付款给卖家。') }}</span>
          <van-count-down class="flex font-bold mx-2" :time="time">
            <template #default="timeData">
              <span class="block">{{ timeData.hours }}</span>
              <span class="colon">:</span>
              <span class="block">{{ timeData.minutes }}</span>
              <span class="colon">:</span>
              <span class="block">{{ timeData.seconds }}</span>
            </template>
          </van-count-down>
        </div>
        <div class="w-full mt-12 pt-12 pl-8 pb-10 box-border border-ra tabBackground">
          <div class="flex justify-between">
            <div @click="$router.push(`/cryptos/advertiserDetail?uid=${orderDetail.c2cUserId}`)"
              class="flex items-center">
              <img class="w-10 h-10 mr-2" :src="orderDetail.c2cUserHeadImg" alt="">
              <h3 class="text-32 font-normal">{{ orderDetail.realName }}</h3>
              <img class="w-8 h-8 ml-2 mr-5" src="~@/assets/image/otc/buy/star.png" alt="" v-if="orderDetail.c2cUserType">
              <van-icon color="#9399A4" name="arrow" class="relative top-1 font-bold text-26" />
            </div>
            <div
              style="background:linear-gradient(to right,#b6dbff,#1d91ff);border-top-left-radius: 40px;border-bottom-left-radius: 40px;"
              class="flex items-center text-24 text-white py-2 px-4" @click="gotoChat">
              <img class="w-9 h-8 mr-2" src="~@/assets/image/c2c/Vector-white.png" alt="" />
              {{ $t('联系卖家') }}
            </div>
          </div>
          <div class="mt-16">
            <div class="flex items-center mb-8 pr-44">
              <img class="w-7 h-7 mr-3" src="~@/assets/image/c2c/gou.png" alt="">
              <p class="text-24">{{ $t('卖家的数字货币现已存入托管账户，请放心付款') }}</p>
            </div>
            <div class="flex justify-between">
              <div class="flex items-center">
                <img class="w-7 h-7 mr-4" src="~@/assets/image/c2c/gou.png" alt="">
                <p class="text-24">{{ $t('7x24小时客服支持') }}</p>
              </div>
              <div class="pr-10" @click="tokefu">
                <van-icon color="#9399A4" name="arrow" class="relative top-1 font-bold text-26" />
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="w-full h-4 my-10 diviLine"></div>
      <div class="px-8">
        <order-data :title="$t('购买')" :detail="orderDetail" />
      </div>
      <div class="w-full h-4 my-10 diviLine"></div>
      <div class="px-8">
        <h2 class="font-normal text-30 c2cColor">{{ $t('交易方式') }}</h2>
        <div class="flex items-center mt-7">
          <div class="w-2 h-7 border-ra" style="background: #E7BB41;"></div>
          <span class="ml-2 text-grey">{{ orderDetail.methodName }}</span>
        </div>
      </div>
      <div class="w-full h-14 mt-8 mb-12 diviLine"></div>
      <div class="flex px-10 pb-20 footer-box">
        <van-button class="w-60 h-20 text-30 text-black border-none" type="primary" style="background: #EAEBEE"
          @click="cancelOrder">{{ $t('取消订单') }}</van-button>
        <van-button class="w-60 flex-1 h-20 text-30 btnMain border-none text-white" type="info" @click="goToPayment">{{
          $t('去付款')
        }}</van-button>
      </div>
    </div>
  </div>
</template>

<script>
import {
  mapState
} from "vuex";

import { CountDown, Icon, Cell, CellGroup, Button, showToast } from 'vant';
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import OrderData from "@/views/cryptos/c2cOrder/components/order-data/OrderData.vue";
import otcApi from "@/service/otc.api";


export default {
  name: "orderGeneration",
  data() {
    return {
      time: 0, //15 * 60 * 60 * 1000,
      orderDetail: {},
      interval: null
      // orderNumber: ''
    }
  },
  async created() {
    const order_no = this.$store.state.c2c.order_no
    const res = await otcApi.ctcOrderGetDetail({ order_no, language: this.$i18n.locale });
    this.orderDetail = res;
    this.time = this.orderDetail.autoCancelTimeRemain * 1000
    this.interval = setInterval(() => {
      this.time = this.time - 1000
      if (this.time <= 0) {
        clearInterval(this.interval)
      }
    }, 1000)
  },
  methods: {
    // 取消订单
    cancelOrder() {
      if (this.time >= 0) {
        this.$router.push({ path: '/cryptos/cancelOrder' })
      } else {
        showToast(this.$t('订单不能取消'))
        // Notify({ type: 'warning', message: this.$t('订单不能取消') })
      }
    },
    // 付款
    goToPayment() {
      this.$router.push({ path: '/cryptos/paymentBuy' })
    },
    gotoChat() {
      this.$router.push({ path: '/cryptos/chat' })
    },
    tokefu() {
      this.$router.push('/workerOrder')
    }
  },
  computed: {
    ...mapState('c2cBuy', ['count', 'totalPrice', 'orderNumber']),
  },
  components: {
    [CountDown.name]: CountDown,
    [Icon.name]: Icon,
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    [Button.name]: Button,
    OrderNav,
    OrderData,
  }
}
</script>

<style lang="scss" scoped>
#orderGenerationPage {
  font-size: 30px;

  .border-ra {
    border-radius: 20px;
  }

  #order-generation {
    :deep(.van-count-down) {
      font-size: 28px;
      color: $blue;
    }

    :deep(.van-cell__value) {
      color: $text_color;
    }

    :deep(.van-cell) {
      color: $text_color1;
      background: $main_background;
    }

    :deep(.van-cell-group) {
      color: $text_color1;
      background: $main_background;
    }

    .footer-box {
      background: $main_background;
    }

  }

  .btnMain {
    margin-left: 10px;
  }
}
</style>

