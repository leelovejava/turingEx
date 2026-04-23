<template>
  <div id="SellGeneratePage">
    <div id="full" class="w-full h-full">
      <loading v-if="loading" />
      <!--完成加载-->
      <div v-else>
        <order-nav />
        <div class="px-8 pb-10 mainBackground">
          <div class="flex justify-between c2cColor">
            <div class="mt-2">
              <div class="text-48">{{ $t('等待买家付款') }}</div>
              <div class="mt-4 text-24">{{ $t('预计收到付款') }} <span style="color: #1D91FF">{{
                orderDetail.expireTime
              }}{{ $t('分钟') }}</span>
              </div>
            </div>
            <div class="w-36 h-36 mt-10">
              <otc-circle :time="orderDetail.expireTime * 60" @finish="finish" class="w-full h-full" />
            </div>
          </div>
          <div class="w-full mt-12 pt-12 pl-8 pb-10 box-border border-ra tabBackground c2cColor">
            <div class="flex justify-between">
              <div class="flex items-center">
                <img class="w-10 h-10 mr-2" :src="orderDetail.c2cUserHeadImg" alt="">
                <h3 class="text-32 font-normal">{{ $t(orderDetail.c2cUserNickName || '') }}</h3>
                <img class="w-8 h-8 ml-2 mr-5" src="~@/assets/image/otc/buy/star.png" alt="">
                <van-icon color="#9399A4" name="arrow" class="relative top-1 font-bold text-24" />
              </div>
              <div
                style="background:linear-gradient(to right,#b6dbff,#1d91ff);border-top-left-radius: 40px;border-bottom-left-radius: 40px;"
                class="flex items-center text-24 text-white py-3 px-3" @click="$router.push({ path: '/cryptos/chat' })">
                <img class="w-9 h-8 mr-2" src="~@/assets/image/c2c/Vector-white.png" alt="" />
                {{ $t('联系买家') }}
              </div>
            </div>
            <div class="mt-14">
              <div class="flex items-center mb-8 pr-48">
                <img class="w-7 h-7 mr-3" src="~@/assets/image/c2c/Vector2.png" alt="">
                <p class="text-24">
                  <span class="text-grey">{{ $t('买家实名 :') }} </span>
                  <span>{{ $t(orderDetail.realName || '') }}</span>
                </p>
              </div>
              <div class="flex justify-between">
                <div class="flex items-center">
                  <img class="w-7 h-7 mr-3" src="~@/assets/image/c2c/gou.png" alt="">
                  <p class="text-24">{{ $t('7x24小时客服支持') }}</p>
                </div>
                <div class="pr-10" @click="tokefu">
                  <van-icon color="#9399A4" name="arrow" class="relative top-1 font-bold text-22" />
                </div>
              </div>
            </div>
          </div>
        </div>
        <div>
          <trade-data :title="$t('出售')" :detail="orderDetail" :count="orderDetail.coinAmount"
            :total-price="orderDetail.amount" :order-number="orderDetail.orderNo" :trade-method="tradeMethod"
            :method-name="fullMethodType(orderDetail.methodType)" :unit-price="orderDetail.symbolValue">
            <template #trade-title>
              <span>{{ $t('我的收款方式') }}</span>
            </template>
            <template #terms>
              <van-collapse v-model="activeNames">
                <van-collapse-item name="2">
                  <template #title>
                    <span>{{ $t('交易条款') }}</span>
                  </template>
                  <van-divider />
                  <p class="text-28">{{ $t('资金绝对安全') }}</p>
                  <p class="mt-5 text-28">{{ $t('平时订单较多，看见了会立马打款。急单勿拍！') }}</p>
                </van-collapse-item>
              </van-collapse>
            </template>
          </trade-data>
        </div>
        <div class="flex justify-between px-10 pt-12 pb-10 mt-44 mainBackground">
          <van-button class="w-60 h-20 c2cColor tabBackground" type="primary" style="margin-right: 16px;"
            @click="$router.push({ path: '/cryptos/c2cHelp', query: { 'expireTimeRemain': orderDetail.expireTime * 60, 'state': orderDetail.state } })">{{
              $t('帮助') }}
          </van-button>
          <van-button class="disable flex-1 h-20 enter" color="#CCCFD6" type="primary">{{ $t('我已确认收款') }}
          </van-button>
        </div>
        <van-popup class="w-full h-full" position="right" v-model:show="isShowCancelOrder">
          <cancel-success v-if="!timeout" :title="$t('出售')" :count="orderDetail.coinAmount"
            :total-price="orderDetail.amount" :order-number="orderDetail.orderNo"
            :create-order-time="fullTime(orderDetail.createTime)" :seller-name="orderDetail.c2cUserNickName"
            :unit-price="orderDetail.symbolValue" />
          <!--   已超时   -->
          <cancel-success v-if="timeout" :title="$t('出售')" :count="orderDetail.coinAmount"
            :total-price="orderDetail.amount" :order-number="orderDetail.orderNo"
            :create-order-time="fullTime(orderDetail.createTime)" :seller-name="orderDetail.c2cUserNickName"
            :unit-price="orderDetail.symbolValue">
            <template #title>{{ $t('已超时') }}</template>
            <template #desc>{{ $t('买家付款超时，您的订单已取消') }}</template>
          </cancel-success>
        </van-popup>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import { Button, Collapse, CollapseItem, Divider, Icon, Popup, Toast } from "vant";
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import TradeData from "@/views/cryptos/c2cOrder/components/trade-data/TradeData.vue";
import OtcCircle from "@/components/Transform/otcCircle/index.vue";
import CancelSuccess from "@/views/cryptos/c2cOrder/components/order-generation/CancelSuccess.vue";
import loading from "@/components/Transform/loading/index.vue";

import otcApi from "@/service/otc.api";

import { formatTime } from "@/utils/utis";
import { SET_ORDER_INFO } from "@/store/const.store";

export default {
  name: "SellGenerate",
  // props: ['orderNumber'],
  data() {
    return {
      loading: false, // 请求中
      isShowCancelOrder: false,// 显示取消订单
      timeout: false, //是否已超时
      activeNames: [],
      // 交易方式
      tradeMethod: [],
      orderDetail: {},
      orderNumber: '',
      timeStatus: null
    }
  },
  created() {
    // console.log(this.orderNumber)
    // this.orderNumber = this.$route.query.orderNumber
    this.orderNumber = this.$store.state.c2c.order_no
    this.getOrderDetail();
  },
  beforeUnmount() {
    console.log('bbb')
    if (this.timeStatus) {
      clearTimeout(this.timeStatus)
    }
  },
  methods: {
    async getOrderDetail() {
      console.log(this.orderNumber)
      const res = await otcApi.ctcOrderGetDetail({ order_no: this.orderNumber, language: this.$i18n.locale });
      this.orderDetail = res;
      console.log(res)
      if (res.state / 1 === 0) {
        this.timeStatus = setTimeout(() => {
          this.getOrderDetail()
        }, 3000)
      } else if (res.state / 1 === 4) {
        this.$router.replace({
          path: '/cryptos/tradeOrderDetail'
        })
      } else {
        if (this.timeStatus) {
          clearTimeout(this.timeStatus)
          this.$router.replace({
            path: '/cryptos/confirmedPaid'
          })
        }
      }

      // 保存订单数据
      this.$store.commit(`c2cSell/${SET_ORDER_INFO}`, {
        info: this.orderDetail
      })
      this.loading = false;
    },
    // 已确认收款
    enter() {

      // 跳转页面
      this.$router.replace({
        path: '/cryptos/confirmedPaid'
      })
    },
    // 已超时
    finish() {
      if (this.timeout) return

      this.loading = true;
      // 重新获取订单状态
      //this.getOrderDetail();
      this.isShowCancelOrder = false;
      this.timeout = true;
    },
    // 取消订单
    t() {
      // this.isShowCancelOrder = true;

      // 取消订单
      // otcApi.ctcOrderCancel({order_no: this.orderInfo.orderNumber}).then(res => {
      //   console.log(res)
      // }).catch(err => {
      //   console.log(err)
      // })
      // setTimeout(() => {
      //   Toast("取消成功")
      // }, 400)
    },
    // 支付方法
    fullMethodType(num) {
      const arr = [this.$t('其它'), this.$t('银行卡'), this.$t('虚拟货币'), this.$t('微信'), this.$t('支付宝'), 'paypal', this.$t('西联汇款'), this.$t('swift国际汇款')]
      return arr[num];
    },
    fullTime(time) {
      console.log(formatTime(new Date(time), 'yyyy-MM-dd hh:mm:ss'));
      return formatTime(new Date(time), 'yyyy-MM-dd hh:mm:ss')
    },
    tokefu() {
      this.$router.push('/customerService')
    }
  },
  watch: {
    orderDetail() {
      this.tradeMethod.push({
        label: this.$t('姓名'), value: this.orderDetail.realName
      })
      this.tradeMethod.push({
        label: this.$t('银行卡号/账号'), value: this.orderDetail.paramValue1
      })
      this.tradeMethod.push({
        label: this.$t('银行名称'),
        value: this.orderDetail.methodName
      })
    }
  },
  computed: {
    ...mapState('c2cSell', ['orderInfo']),
  },
  components: {
    [Icon.name]: Icon,
    [Collapse.name]: Collapse,
    [CollapseItem.name]: CollapseItem,
    [Divider.name]: Divider,
    [Button.name]: Button,
    [Popup.name]: Popup,
    OrderNav,
    TradeData,
    OtcCircle,
    CancelSuccess,
    loading,
  },
}
</script>

<style lang="scss" scoped>
#SellGeneratePage {
  font-size: 30px;

  .border-ra {
    border-radius: 20px;
  }

  :deep(.order-data) {
    .title {
      color: #E35461;
    }

  }

  :deep(.disable.van-button) {
    color: #7C838F !important
  }

  :deep(.van-button--primary) {
    border: none;
  }

  .loading {
    background: #fff;

    :deep(.van-loading) {
      color: #1D91FF;
    }

  }

  .enter {
    //color: #7C838F !important;
  }
}
</style>
