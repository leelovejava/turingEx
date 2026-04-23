<template>
  <div id="PaymentDetailPage">
    <div id="full" class="w-full h-full flex flex-col ">
      <order-nav :back="false" @back="$router.push('/cryptos/wantBuy')">
        <template #right>
          <div class="flex items-center" @click="$router.push({ path: '/cryptos/chat' })">
            <van-badge class="w-9 h-8 mr-5" :content="unreadMsg">
              <img class="w-full h-full " src="~@/assets/image/c2c/Vector.png" alt="">
            </van-badge>
            <div class="text-28">{{ $t('联系卖家') }}</div>
          </div>
        </template>
      </order-nav>
      <div style="overflow-y: auto" class="flex-1 mainBackground">
        <div class="flex justify-between items-center pt-8 px-8 pb-24 mainBackground">
          <div class="mainBackground">
            <p class="text-48 c2cColor">{{ $t('等待卖家确认收款') }}</p>
            <!-- <p class="mt-4 text-24"> {{$t('此卖家95%的订单会在')}}<span style="color: #1D91FF">02</span> {{$t('分钟内完成')}}</p> -->
          </div>
          <div class="w-36 h-36">
            <otc-circle :time="time" :expireTime="expireTime" class="w-full h-full" />
          </div>
        </div>
        <div class="">
          <trade-data title="购买" :count="count" :total-price="totalPrice" :create-order-time="createOrderTime"
            :order-number="orderNumber" sell-name="钱多多爱妞商行" client-type="买家昵称" :detail="detail"
            :trade-method="tradeMethod">
            <template #divider>
              <van-divider class="order-msg-divider" />
            </template>
            <template #trade-title>
              <span>{{ $t('交易方式') }}</span>
            </template>
            <template #terms>
              <van-collapse v-model="activeNames">
                <van-collapse-item name="2">
                  <template #title>
                    <span>{{ $t('交易条款') }}</span>
                  </template>
                  <van-divider />
                  <p>{{ $t('敢打黑钱者，虽远逼诛！') }}</p>
                  <p class="mt-5">{{ $t('急单勿拍！') }}</p>
                </van-collapse-item>
              </van-collapse>
            </template>
          </trade-data>
        </div>
      </div>
      <div class="px-10 pt-12 h-44 box-border flex text-30 mainBackground">
        <van-button class="flex-1 h-20 mr-4 rounded-xl c2cColor tabBackground border-none" type="primary"
          style="margin-right: 16px;"
          @click="$router.push({ path: '/cryptos/cancelOrder?order_no=' + $route.query.order_no })">{{ $t('取消订单') }}
        </van-button>
        <van-button class="flex-1 h-20 rounded-xl tabBackground c2cColor border-none" type="primary"
          @click="$router.push({ path: '/cryptos/appeal' })">{{ $t('申诉') }}
        </van-button>
      </div>
      <!--  交易成功  -->
      <!--        <trade-success/>-->
      <!--  交易成功详情  -->
      <!--        <trade-success-detail/>-->
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex"
import { Badge, Collapse, CollapseItem, Divider, } from "vant"
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import OtcCircle from "@/components/Transform/otcCircle/index.vue";
// import TradeSuccess from "@/page/c2c-trade/components/TradeSuccess";
// import TradeSuccessDetail from "@/page/c2c-trade/components/TradeSuccessDetail";
import TradeData from "@/views/cryptos/c2cOrder/components/trade-data/TradeData.vue";
import otcApi from "@/service/otc.api";

export default {
  name: "PaymentDetail",
  data() {
    return {
      activeNames: [],
      // 交易方式
      tradeMethod: [
        { label: this.$t('姓名'), value: this.$t('刘德华') },
        { label: this.$t('银行卡号/账号'), value: this.$t('1122 3344 5566 7788') },
        { label: this.$t('银行名称'), value: this.$t('中国工商银行') },
        { label: this.$t('开户支行'), value: this.$t('北京海淀支行') },
      ],
      detail: { methodName: '' },
      time: 0,
      expireTime: 0,
      timeout: null,
      unreadMsg: 0
    }
  },
  computed: {
    ...mapState('c2cBuy', ['count', 'totalPrice', 'createOrderTime', 'orderNumber'])
  },
  created() {
    this.fetchState()
    // otcApi.ctcOrderGetDetail({order_no: this.$route.query.order_no}).then(res => {
    //   this.detail = res.data
    //   this.time = this.detail.expireTimeRemain
    //   console.log(this.detail)
    // })
  },
  methods: {
    fetchState() {
      const order_no = this.$store.state.c2c.order_no
      otcApi.ctcOrderGetDetail({ order_no, language: this.$i18n.locale }).then(res => {
        const state = res.state / 1
        if (state === 1) { // 已付款

          // TOOD: 完成
          this.detail = res
          this.time = this.detail.expireTimeRemain
          this.expireTime = this.detail.expireTime * 60
          this.unreadMsg = this.detail.unreadMsg
          if (this.timeout) {
            clearTimeout(this.timeout)
          }
          this.timeout = setTimeout(() => {
            this.fetchState()
          }, 3000)
        } else if (state == 3) { // 已成功
          // TODO 跳转到
          clearTimeout(this.timeout)
          this.$router.push('/cryptos/tradeSuccessBuyer')
        } else if (state == 5) {
          clearTimeout(this.timeout)
          this.$router.push({ path: '/cryptos/tradeOrderDetail', query: { back: false } })
        } else {
          console.log(this.detail)
        }
      })
    }
  },
  beforeUnmount() {
    clearTimeout(this.timeout)
  },
  components: {
    [Badge.name]: Badge,
    [Divider.name]: Divider,
    [Collapse.name]: Collapse,
    [CollapseItem.name]: CollapseItem,
    OrderNav,
    OtcCircle,
    TradeData,
  }
}
</script>

<style lang="scss" scoped>
#PaymentDetailPage {
  font-size: 30px;

  :deep(.order-msg-divider) {
    margin: 48px 0;
    border-color: $divi_line;
  }
}
</style>