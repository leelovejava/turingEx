<template>
  <div id="TrendDetailsPage">
    <div class="trend-detail pb-20" :class="isNight ? 'bg-night' : 'bg-white'">
      <trade-head :backFunc="() => $router.push('/')" :symbol="symbol" :islevel="true" @update-coin="onUpdate"
        @data="quote = $event" :title="$t('永续')" @changeLine="onChangeLine" :isNight="isNight" :isChange="isChange"
        :kineType="kineType" :tabIndex="tab" @changeNight="OnchangeNight" :symbolName="symbolName" />
      <div class="flex justify-between px-8 pt-10" v-if="!kineType">
        <div class="w-96 h-20 mr-8 text-28 flex justify-center items-center rounded box-border tabBtn"
          :class="tab === '1' ? 'activeBtn' : ''" @click="onTab('1')">{{ $t('永续合约') }}</div>
        <div class="w-96 h-20 text-28 flex justify-center items-center rounded box-border tabBtn"
          :class="tab === '2' ? 'activeBtn' : ''" @click="onTab('2')">{{ queryType == 'cryptos' ? $t('交割合约') : $t('期货交易')
          }}</div>
      </div>
      <!-- 数据区 -->
      <div :class="{ slide2: animated1, slide1: animated2 }">
        <div class="data-index">
          <div class="flex flex-1 px-8 mt-10">
            <div class="flex flex-col text-22">
              <p class="text-66 font-semibold" :class="quote.change_ratio > 0 ? 'text-green' : 'text-red'">
                {{ quote.close || '--' }}</p>
              <p class="text-22 mt-2">≈ {{ currency.currency_symbol }}{{ quote.close ? (quote.close *
                currency.rate).toFixed(2) : '--' }}
                <span :class="quote.change_ratio > 0 ? 'text-green' : 'text-red'">{{ quote.change_ratio }}%</span>
              </p>
              <p class="text-grey mt-2">{{ $t('标记价格') }} {{ quote.open || 0 }}</p>
            </div>
            <div class="flex-wrap flex-1 justify-end text-22">
              <div class="flex justify-end">
                <div class="flex flex-col justify-between w-44">
                  <p class="text-grey">{{ $t('24h最高价') }}</p>
                  <p>{{ quote.high || 0 }}</p>
                </div>
                <div class="flex flex-col justify-between w-44 buy-title">
                  <p class="text-grey">{{ $t('24h成交量') }}({{ symbolData && symbolData.toUpperCase() || '--' }})</p>
                  <p>{{ quote.amount && quote.amount.toFixed(3) || 0 }}</p>
                </div>
              </div>
              <div class="flex justify-end">
                <div class="flex flex-col justify-between w-44 mt-55">
                  <p class="text-grey">{{ $t('24h最低价') }}</p>
                  <p>{{ quote.low || 0 }}</p>
                </div>
                <div class="flex flex-col justify-between w-44 mt-5">
                  <p class="text-grey">{{ queryType == 'cryptos' ? $t('24h成交额') + '(USDT)' : $t('24h成交额') + '(USD)' }}</p>
                  <p>{{ (quote.volume * 1).toFixed(2) || 0 }}</p>
                </div>
              </div>
            </div>

          </div>
        </div>
        <p class="status-info" v-if="quote.market && quote.market.status && queryType != 'cryptos'">
          <span>{{ quote.market.status && $t(quote.market.status) }},</span>
          <span class="time">{{ quote.market.time_str }}</span>&nbsp;
          <span>{{ quote.market.time_zone && $t(quote.market.time_zone) }}</span>
        </p>
        <!-- k line-->
        <div class="mt-9">
          <kline-charts :update-key="updateKey" :update-data="quote" :isChangeLine="isChangeLine" :symbol="symbol"
            v-if="symbol" @updataLine="isChangeLine = false" :isNight="isNight" :isChange="isChange" />
        </div>
        <div class="w-full h-5" :class="isNight ? 'bg-black' : 'white'"></div>
        <!-- tab 区域-->
        <div class="pb-44 tabContent">
          <van-tabs v-model:active="active">
            <van-tab :title="$t('entrustOrder')" name="1">
              <div class="flex justify-between px-8">
                <ul class="flex flex-col flex-1 mr-20">
                  <li class="text-28 text-grey mt-8 mb-4 w-full">
                    <p>{{ $t('买入') }}</p>
                    <p class="flex  justify-between text-22">
                      <span style="margin-right: -10px;">{{ $t('价格') }}{{ queryType == 'cryptos' ? '(USDT)' : '(USD)'
                      }}</span>
                      <span class="buy-title">{{ $t('数量') }}({{ symbolData.toUpperCase() }})</span>
                    </p>
                  </li>
                  <li v-for="(item, index) in bids" :key="index" class="flex justify-between text-26 py-4" :style="{
                    'background': `linear-gradient(to left,${THEME == 'dark' ? '#131A2E' : '#fff'} 0% ` +
                      (item.amount / bids[bids.length - 1].amount) * 100 + '%,rgba(94,186,137,.1) ' +
                      (item.amount / bids[bids.length - 1].amount) * 100 + '%,rgba(94,186,137,.1) 100%)'
                  }">
                    <span class="textColor">{{ item.price || '--' }}</span>
                    <span v-if="symbol == 'shib'" class="text-green">{{ fixDate(item.amount, $i18n) ||
                      '--' }}</span>
                    <span v-else class="text-green">{{ item.amount || '--' }}</span>
                  </li>
                </ul>
                <ul class="flex-1 flex flex-col ml-5">
                  <li class="text-28 text-grey mt-8 mb-4 w-full">
                    <p>{{ $t('卖出') }}</p>
                    <p class="flex  justify-between  text-22">
                      <span class="ml-2">{{ $t('价格') }}{{ queryType == 'cryptos' ? '(USDT)' : '(USD)' }}</span>
                      <span class="buy-title">{{ $t('数量') }}({{ symbolData.toUpperCase() }})</span>
                    </p>
                  </li>
                  <li v-for="(item, index) in asks" :key="index" class="flex justify-between text-26 py-4" :style="{
                    'background': `linear-gradient(to left,${THEME == 'dark' ? '#131A2E' : '#fff'} 0% ` +
                      (item.amount / asks[asks.length - 1].amount) * 100 + '%,rgba(246,70,93,.1) ' +
                      (item.amount / asks[asks.length - 1].amount) * 100 + '%,rgba(246,70,93,.1) 100%)'
                  }">
                    <span class="textColor">{{ item.price || '--' }}</span>
                    <span v-if="symbol == 'shib'" class="text-red">{{ fixDate(item.amount, $i18n) ||
                      '--' }}</span>
                    <span v-else class="text-red">{{ item.amount || '--' }}</span>
                  </li>
                </ul>
              </div>
            </van-tab>
            <van-tab :title="$t('最新交易')" name="2">
              <ul class="px-8 text-28 text-grey">
                <li class="flex justify-between mt-8">
                  <span class="flex-1">{{ $t('时间') }}</span>
                  <span class="flex-1">{{ $t('方向') }}</span>
                  <span class="flex-1">{{ $t('价格') }}{{ queryType == 'cryptos' ? '(USDT)' : '(USD)' }}</span>
                  <span class="flex-1 flex justify-center buy-title">{{ $t('数量') }}({{ symbolData.toUpperCase() }})</span>
                </li>
                <li v-for="(item, index) in deals" :key="item.ts + item.price + item.amount || index"
                  class="flex  justify-between mt-8">
                  <span class="flex-1">{{ item.current_time ? item.current_time : '--' }}</span>
                  <span :class="item.direction === 'buy' ? 'text-green' : 'text-red'" class="flex-1">{{
                    item.direction === 'buy' ? $t('买入') : $t('卖出') }}</span>
                  <span :class="item.direction === 'buy' ? 'text-green' : 'text-red'"
                    class="flex-1 flex-justify-center">{{ item.price || '--' }}</span>
                  <span class="flex-1 flex justify-center">{{ item.amount || '--' }}</span>
                </li>
              </ul>
            </van-tab>
            <van-tab :title="$t('深度图')" name="3">
              <div class="buy-sell-box">
                <div class="buy-item text-28">
                  <div class="bg-line"></div>
                  {{ $t('买盘') }}
                </div>
                <div class="sell-item text-28">
                  <div class="bg-line"></div>
                  {{ $t('卖盘') }}
                </div>
              </div>
              <div class="deep-chart-box">
                <deep-chart :deepBuy="deepBuy" :deepSell="deepSell"></deep-chart>
              </div>
            </van-tab>
            <!-- <van-tab :title="$t('数据分析')" name="4">
                        <div class="capital-box">
                            <div class="capital-flows">
                                <span class="title">资金流向分析</span><van-icon name="warning-o" />
                            </div>
                            <div class="tab-box">
                                <div class="tab-item" v-for="(item, index) in 10">
                                    15M
                                </div>
                            </div>
                            <div class="pie-box">
                                <pie-chart></pie-chart>
                            </div>
                            <div class="order-box">
                                <div class="order-header">
                                    <div class="flex-item">订单类型</div>
                                    <div class="flex-item">买入</div>
                                    <div class="flex-item">卖出</div>
                                    <div class="flex-item">净流入</div>
                                </div>
                                <div class="order-main flex" v-for="(item, index) in 4" :key="index">
                                    <div class="flex-item">大单</div>
                                    <div class="flex-item text-red">9.3720</div>
                                    <div class="flex-item text-red">3.0200</div>
                                    <div class="flex-item text-green">6.3400</div>
                                </div>
                            </div>

                            <div class="capital-title">
                                <span class="title">5*24小时大单流入（{{ symbol.toUpperCase() }}）</span>
                                <div class="text">5日主力净流入 -348.9411</div>
                            </div>
                            <div class="cylinder-kline">
                                <cylinder-chart></cylinder-chart>
                            </div>
                            <div class="capital-title">
                                <span class="title">24小时资金流入（{{ symbol.toUpperCase() }}）</span>
                            </div>
                            <div class="inflow-kline">
                                <inflow-kline></inflow-kline>
                            </div>
                        </div>
                    </van-tab> -->
            <!--                     <van-tab  :title="$t('币种简介')" name="3" class="border-t-color" v-if="this.$te(`${symbol}简介`)">-->
            <!--                        <div class="px-20 py-20 text-40 text-grey">-->
            <!--                           {{  $t('关于名称',{'symbol':symbol.toUpperCase() }) }}-->
            <!--                        </div>-->
            <!--                        <div class="px-20 py-20 text-32 text-grey">-->
            <!--                            {{  $t(`${symbol}简介`,{'symbol':symbol.toUpperCase() }) }}-->
            <!--                        </div>-->
            <!--                    </van-tab>-->
          </van-tabs>
        </div>
        <!-- 按钮区 -->
        <div class="flex pl-10 pt-5 pb-2 pr-8 text-white justify-between fixed w-full box-border z-10 buttonSafe"
          v-if="!kineType" :class="isNight ? 'bg-night1' : 'bg-grey'">
          <div class="flex flex-col mx-8 items-center" @click="onRoute('/cryptos/exchangeRate')">
            <img src="@/assets/image/icon-rate.png" class="w-10 h-10" />
            <p class="text-26 text-grey mt-2 text-center">{{ $t('汇率') }}</p>
          </div>
          <div ref="toBuy" class="w-72 h-20 bg-green flex justify-center items-center rounded-lg text-28"
            @click=" onSubmit('buy')">
            {{ tab == 1 ? $t('开多') : $t('买多') }}
          </div>
          <div ref="toSell" class="w-72 h-20 bg-red flex justify-center items-center rounded-lg text-28"
            @click="onSubmit('sell')">
            {{ tab == 1 ? $t('开空') : $t('买空') }}</div>
        </div>
      </div>
      <van-popup v-model:show="show" class="rounded-xl" :key="initFutrue.session_token">
        <popup-confirm-order :symbolName="symbolName" :paras="initFutrue.para" :symbol="symbol" :direction="direction"
          :balance="initFutrue.amount" :price="quote.close" @close="onClose" @confirm="onOrderConfirm"
          v-if="popType === 'confirm'" />
        <popup-delivery showBtns :detailData="detailData" :key="detailData.order_no" @close="onClose"
          @continueToBuy="order()" @timeEnd="handleTimeEnd" :price="quote.close" v-else />
      </van-popup>
    </div>
  </div>
</template>
<script>
import { Tab, Tabs, Popup, showToast } from 'vant';
import TradeHead from '@/components/Transform/trade-head/index.vue'
import KlineCharts from '@/components/Transform/kline-charts/index.vue'
import { _getHomeList, _collect, _deleteCollect } from '@/service/home.api'
import { _getBalance, _futrueOrder, _futrueOrderInit, _futrueOrderDetail } from '@/service/trade.api'
import { mapGetters } from 'vuex';
import { fixDate } from "@/utils";
import { WS_URL } from '@/config';
import PopupDelivery from "@/components/Transform/popup-delivery/index.vue";
import PopupConfirmOrder from '@/components/Transform/popup-confirm-order/index.vue'
import deepChart from '@/components/Transform/deepChart/index.vue'
import { themeStore } from '@/store/theme';
const thStore = themeStore()
const THEME = thStore.theme
export default {
  name: 'TrendDetails',
  components: {
    [Tabs.name]: Tabs,
    [Tab.name]: Tab,
    [Popup.name]: Popup,
    TradeHead,
    KlineCharts,
    PopupDelivery,
    PopupConfirmOrder,
    deepChart
  },
  data() {
    const arr = []
    for (let i = 0; i < 17; i++) {
      arr.push({
        id: i
      })
    }
    return {
      THEME,
      show: false,
      direction: 'buy',
      initFutrue: {},
      detailData: {},
      price: '',
      popType: 'confirm', // confirm / counting
      fixDate,
      tab: '1', // 顶部标签
      symbol: '',
      quoteData: [],
      quote: {},
      active: 1,
      asks: arr, // 卖
      bids: arr, // 买
      deals: arr, // 交易
      sockets: {
        quote: null,
        deals: null,
        askBid: null
      },
      animated1: false,
      animated2: false,
      updateKey: 1,
      collected: '0',  // 是否自选
      maxSell: 0, // 卖单最大
      maxBuy: 0, // 买单最大
      order_no: '',
      timer: null,
      isChangeLine: false,
      isNight: true,
      isChange: true, //是否有切换黑夜白天模式
      kineType: '',
      deepBuy: [],
      deepSell: [],
      fromPath: '',
      symbolData: '',
      queryType: 'cryptos',
      symbolName: ''
      // updateData: {}
    }
  },
  computed: {
    ...mapGetters({ currency: 'home/currency', userInfo: 'user/userInfo' })
  },
  mounted() {
    console.log('sss')
    //this.continueOrder()
    this.kineType = this.$route.query.kineType
    if (this.$route.query.type) {
      this.queryType = this.$route.query.type
    }
    THEME == 'dark' ? this.isNight = true : this.isNight = false
  },
  created() { },
  watch: {
    active(val) { // tab切换
      if (val / 1 === 1) {
        this.sockets.deals && this.sockets.deals.close()
        this.sockets.deals = null
        if (this.symbol) { // 刚进来可能是null
          this.startAskBidSocket()
        }
      } else {
        this.sockets.deals && this.sockets.deals.close()
        this.sockets.deals = null
        this.startDealsSocket()
      }
    }
  },
  methods: {
    onChangeLine() {
      this.isChangeLine = true
    },
    continueOrder() {
      let direction = this.$route.query.direction
      if (direction) {
        this.symbol = this.$route.params.symbol
        this.tab = '2'
        this.onSubmit(direction);
      }
    },
    order() {
      this.onSubmit(this.direction);
    },
    onTab(e) {
      this.tab = e
      // this.$router.go()
      if (e == 1) {
        this.animated1 = true
        this.timer = setTimeout(() => {
          this.animated1 = false
          clearTimeout(this.timer)
        }, 200)
      } else {
        this.animated2 = true
        this.timer = setTimeout(() => {
          this.animated2 = false
          clearTimeout(this.timer)
        }, 200)
      }
    },
    onCollect() { // 收藏，取消收藏
      let _api = _collect
      if (this.collected === '1') {
        _api = _deleteCollect
      }
      _api(this.symbol).then(() => {
        this.collected = this.collected === '1' ? '0' : '1'
        if (this.collected === '1') {
          showToast(this.$t('收藏成功'))
        } else {
          showToast(this.$t('取消收藏'))
        }
      })
    },
    goHistory() {
      const url = this.topIndex / 1 === 1 ? 'perpetualHistory' : 'deliveryContractHistory'
      this.$router.push({
        path: `/${url}?symbol=${this.symbol}`
      });
    },
    fetchData() {
      this.closeSocket()
      _getHomeList(this.symbol).then(data => {
        console.log(data, 22222)
        // console.log(data[0])
        this.quote = data[0]
        this.symbolData = data[0].symbol_data
        this.symbolName = data[0].name
        this.$nextTick(() => {
          if (!this.sockets.quote && this.symbol) {
            this.startQuoteScoket()
          }

          if ((this.tab === '1' || this.tab === '3') && !this.sockets.askBid && this.symbol) {
            this.startAskBidSocket()
          }
        })
        this.startDealsSocket()
      })
    },
    startQuoteScoket() {
      this.sockets.quote = new WebSocket(`${WS_URL}/1/${this.symbol}`)
      this.sockets.quote.onmessage = (evt) => {
        const { data } = evt
        const { code, data: _data } = JSON.parse(data)
        if (code / 1 === 0) {
          this.quote = _data[0]
          this.updateKey++
        }
      }
    },
    startAskBidSocket() { // 委托

      this.sockets.askBid = new WebSocket(`${WS_URL}/3/${this.symbol}`)
      this.sockets.askBid.onmessage = (evt) => {
        const { data } = evt
        const { code, data: _data } = JSON.parse(data)
        if (code / 1 === 0) {
          let { asks, bids } = _data
          this.deepBuy = bids
          this.deepSell = asks
          asks = asks.sort((prev, next) => prev.price - next.price)
          bids = bids.sort((prev, next) => prev.price - next.price)
          this.asks = asks.slice(0, 17)
          this.bids = bids.reverse().slice(0, 17)
        }
      }
    },
    startDealsSocket() { // 交易
      this.sockets.deals = new WebSocket(`${WS_URL}/2/${this.symbol}`)
      this.sockets.deals.onmessage = (evt) => {
        const { data } = evt
        const { code, data: _data } = JSON.parse(data)
        if (code / 1 === 0) {
          this.deals = _data.data.slice(0, 17)
        }
      }
    },
    onRoute(path) { // 跳转汇率页面
      this.$router.push(path)
    },
    onSubmit(direction) { // TODO: 返回
      if (this.tab === '2') {
        this.popType = 'confirm'
        this.direction = direction
        _futrueOrderInit(this.symbol).then(data => {
          this.initFutrue = data
          this.show = true
        })
        return
      } else {
        this.$router.push(`/cryptos/perpetualContract/${this.symbol}?type=${this.queryType}`)
      }
    },
    onClose() {
      this.show = false
    },
    handleTimeEnd() {
      _futrueOrderDetail(this.order_no).then(data => {
        clearTimeout(this.timeout)
        this.detailData = data
        if (data.state !== 'created') {
          this.timeout = setTimeout(() => {
            this.handleTimeEnd()
          }, 1000)
        }
      })
    },
    onOrderConfirm(e) { // 确认订单
      _futrueOrder({ ...e, symbol: this.symbol, direction: this.direction, session_token: this.initFutrue.session_token }).then(data => {
        // console.log(data)
        this.order_no = data.order_no
        _getBalance().then(data => { // 刷新余额
          this.$store.commit('user/SET_USERINFO', { balance: data.money })
        })
        _futrueOrderDetail(data.order_no).then(data => {
          this.detailData = data
          this.popType = ''
        })
      }).catch(() => {
        setTimeout(() => {
          _futrueOrderInit(this.symbol).then(data => {
            this.order_no = ''
            this.initFutrue = data
          })
        }, 1000);
      })
    },
    closeSocket() {
      this.sockets.quote && this.sockets.quote.close()
      this.sockets.deals && this.sockets.deals.close()
      this.sockets.askBid && this.sockets.askBid.close()
      this.sockets.quote = null
      this.sockets.deals = null
      this.sockets.askBid = null
    },
    onUpdate(symbol) { // 更新
      console.log(symbol)
      this.symbol = symbol;
      this.fetchData()
    },
    OnchangeNight(val) {
      this.isNight = val
    },
  },
  activated() {
    // if (!this.sockets.quote && this.symbol) {
    //     this.startQuoteScoket()
    // }
    // if (this.tab === '1' && !this.sockets.askBid && this.symbol) {
    //     this.startAskBidSocket()
    // }
    if (!this.symbol) {
      this.symbol = this.$route.params.symbol
      this.fetchData()
      this.closeSocket()
    }
    if (this.tab === '2' && !this.sockets.deals && this.symbol) {
      this.startDealsSocket()
    }
    this.continueOrder()
    this.kineType = this.$route.query.kineType
  },
  deactivated() {
    this.closeSocket()
  },
  beforeUnmount() {
    this.sockets.quote && this.sockets.quote.close()
    this.sockets.deals && this.sockets.deals.close()
    this.sockets.askBid && this.sockets.askBid.close()
    this.sockets.quote = null
    this.sockets.deals = null
    this.sockets.askBid = null
  },
  beforeRouteEnter(to, from, next) {
    const { params: { symbol } } = to
    console.log(symbol)
    next(vm => {
      vm.symbol = symbol
      vm.fetchData()
    })
  }
}
</script>
<style lang="scss" scoped>
#TrendDetailsPage {
  :deep(.van-tabs__wrap) {
    height: 60px !important;
  }
}

:deep(.van-tab) {
  padding: 0 12px;
}

:deep(.van-tabs__nav) {
  background-color: $mainBgColor !important;
}

@keyframes animate1 {
  0% {
    opacity: 1;
    transform: translate3d(100%, 0, 0);
  }

  //   40% {
  //      opacity: 1;
  //     transform: translate3d(50%, 0, 0);
  //   }
  100% {
    opacity: 1;
    transform: translate3d(0%, 0, 0);
  }
}

@keyframes animate2 {
  0% {
    opacity: 1;
    transform: translate3d(-100%, 0, 0);
  }

  //   40% {
  //      opacity: 1;
  //     transform: translate3d(50%, 0, 0);
  //   }
  100% {
    opacity: 1;
    transform: translate3d(0%, 0, 0);
  }
}

.status-info {
  margin-top: 20px;
  padding: 0 6px;
  font-size: 24px;
  line-height: 64px;
  height: 64px;
  border-top: 1px solid $border_color;
  border-bottom: 1px solid $border_color;

  .time {
    width: 220px;
    display: inline-block;
  }
}


.slide1 {
  animation: animate1 200ms linear;
}

.slide2 {
  animation: animate2 200ms linear;
}

.tabBtn {
  border: 2px solid #EAEDF2;
  border-radius: 9px;
  color: $text_color1;
  ;
  ;
}

.activeBtn {
  background: $btn_main;
  color: $white;
  border: none;
}

.pb-180 {
  :deep(.van-tab) {
    flex: initial;
    padding: 0 2rem !important;
  }

  :deep(.van-tabs__line) {
    width: 160px;
    bottom: 0;
  }
}

.buttonSafe {
  bottom: calc(constant(safe-area-inset-bottom));
  bottom: calc(env(safe-area-inset-bottom));
}

.bg-night {
  .tabBtn {
    border: 2px solid #444B58;
  }

  :deep(.van-tab--active) {
    color: $white;
  }

  ul li {
    background: $night ;
  }

  .activeBtn {
    border: none !important;
    background: $green;
  }
}

.buy-sell-box {
  display: flex;
  justify-content: center;
  margin: 30px 0px !important;

  .buy-item {
    display: flex;

    color: $text_color;

    .bg-line {
      width: 40px;
      height: 40px;
      background: $red;
      border-radius: 5px;
      margin-right: 10px;
    }
  }

  .sell-item {
    margin-left: 30px !important;
    display: flex;
    color: $text_color;

    .bg-line {
      width: 40px;
      height: 40px;
      background: $green;
      border-radius: 5px;
      margin-right: 10px;
    }
  }
}

.deep-chart-box {
  width: 100%;
  overflow: hidden;
}

.buy-title {
  word-wrap: break-word;
  word-break: break-all;
  max-width: 200px;
}
</style>
