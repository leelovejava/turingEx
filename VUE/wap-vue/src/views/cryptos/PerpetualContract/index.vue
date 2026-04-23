<template>
  <div id="cryptos">
    <div :key="symbol" class="pb-28 no_touch">
      <!-- 头部区 -->
      <ContractHeader :symbol="symbol" :range="range" :selectIndex="selectIndex"
        :symbolName="type == 'cryptos' ? symbol : symbolName" :balance="userInfo.balance" @tab="onTopTab"
        @update-coin="onUpdate">
      </ContractHeader>
      <p class="status-info" v-if="type !== 'cryptos'&& chartData.market && chartData.market.status">
        <span>{{ chartData.market.status && $t(chartData.market.status) }},</span>
        <span class="time">{{ chartData.market.time_str }}</span>&nbsp;
        <span>{{ chartData.market.time_zone && $t(chartData.market.time_zone) }}</span>
      </p>
      <div :class="{ slide2: animated1 }" v-if="selectIndex === 1">
        <!-- <div class="line-container">
          <div class="index-info line-l" :class="showMore ? 'hidden' : ''">
            <p class="first-line red">{{ formatMoney(chartData?.close) }}</p>
            <p class="second-line">
              <span class="red">{{ chartData?.netChange }}</span>&nbsp;
              <span class="red">{{ `${chartData?.change_ratio}%` }}</span>
            </p>
          </div>
          <div @click="handleClickShowMore()" class="line-r">
            <div class="title-box" v-if="!showMore">
              <span class="text">{{ $t('ExpandQuotes') }}</span>
              <img src="@/assets/image/down-arrow2.png" alt="arrow" />
            </div>
            <div class="title-box" v-else>
              <span class="text">{{ $t('PutAwayQuotes') }}</span>
              <img src="@/assets/image/up-arrow2.png" alt="arrow" />
            </div>
          </div>
        </div> -->
        <section class="value-container" v-if="showMore">
          <div class="flex-l">
            <p class="first-line red">{{ formatMoney(chartData?.close) }}</p>
            <p class="second-line">
              <span class="red">{{ chartData?.netChange }}</span>&nbsp;
              <span class="red">{{ `${chartData?.change_ratio}%` }}</span>
            </p>
          </div>
          <div class="flex-r">
            <div class="flex-r-item">
              <p>
                <span class="label">{{ $t('high') }}</span>
                <span class="value">{{ formatMoney(chartData?.high) }}</span>
              </p>
              <p>
                <span class="label">{{ $t('Low') }}</span>
                <span class="value">{{ formatMoney(chartData?.low) }}</span>
              </p>
              <p>
                <span class="label">{{ $t('open') }}</span>
                <span class="value">{{ formatMoney(chartData?.open) }}</span>
              </p>
            </div>
            <div class="flex-r-item">
              <p>
                <span class="label">{{ $t('marketValue') }}</span>
                <span class="value">{{ formatMoney(chartData?.marketCapital) }}</span>
              </p>
              <p>
                <span class="label">{{ $t('share') }}</span>
                <span class="value">{{ formatMoney(chartData?.open) }}</span>
              </p>
              <p>
                <span class="label">{{ $t('amplitude') }}</span>
                <span class="value">{{ formatMoney(chartData?.changeRatio) }}</span>
              </p>
            </div>
            <div class="flex-r-item">
              <p>
                <span class="label">{{ $t('quantity') }}</span>
                <span class="value">{{ formatMoney(chartData?.volume) }}</span>
              </p>
              <p>
                <span class="label">{{ $t('Change') }}</span>
                <span class="value">{{ formatMoney(chartData?.turnoverRate) }}</span>
              </p>
              <p>
                <span class="label">{{ $t('Forehead') }}</span>
                <span class="value">{{ formatMoney(chartData?.volume) }}</span>
              </p>
            </div>
          </div>
        </section>
        <div class="mainBackground rounded-view" key="x">
          <PerpetualOpen class="pl-8 pr-8" :key="keyIndex + 'a'" :selectIndex="selectIndex" :symbol="symbol"
            :green-data="bids" :red-data="asks" :price="price" :init-open="initOpen" :init-close="initClose"
            :init-futrue="initFutrue" :currentType="currentType" @changeValueBack="changeValueBack"
            @changeCurrentType="changeCurrentType" @ordered="onOrdered">
          </PerpetualOpen>
          <div class="line"></div>
          <!-- 委托/持仓-->
          <PerpetualOrder class="pl-8 pr-8" :key="keyIndex + 'b'" :symbol="symbol" :order-cur="orderCur"
            :order-hold="orderHold" :topIndex="selectIndex" :futrue-hold="futrueHold" :futrue-histroy="futrueHistroy"
            @tab="onTab" @recall="onRecall">
          </PerpetualOrder>
        </div>
      </div>
      <div :class="{ slide1: animated2 }" v-else>
        <!-- <div class="line-container">
          <div class="index-info line-l" :class="showMore ? 'hidden' : ''">
            <p class="first-line red">{{ formatMoney(chartData?.close) }}</p>
            <p class="second-line">
              <span class="red">{{ chartData?.netChange }}</span>&nbsp;
              <span class="red">{{ `${chartData?.change_ratio}%` }}</span>
            </p>
          </div>
          <div @click="handleClickShowMore()" class="line-r">
            <div class="title-box" v-if="!showMore">
              <span class="text">{{ $t('ExpandQuotes') }}</span>
              <img src="@/assets/image/down-arrow2.png" alt="arrow" />
            </div>
            <div class="title-box" v-else>
              <span class="text">{{ $t('PutAwayQuotes') }}</span>
              <img src="@/assets/image/up-arrow2.png" alt="arrow" />
            </div>
          </div>
        </div> -->
        <section class="value-container" v-if="showMore">
          <div class="flex-l">
            <p class="first-line red">{{ formatMoney(chartData?.close) }}</p>
            <p class="second-line">
              <span class="red">{{ chartData?.netChange }}</span>&nbsp;
              <span class="red">{{ `${chartData?.change_ratio}%` }}</span>
            </p>
          </div>
          <div class="flex-r">
            <div class="flex-r-item">
              <p>
                <span class="label">{{ $t('high') }}</span>
                <span class="value">{{ formatMoney(chartData?.high) }}</span>
              </p>
              <p>
                <span class="label">{{ $t('Low') }}</span>
                <span class="value">{{ formatMoney(chartData?.low) }}</span>
              </p>
              <p>
                <span class="label">{{ $t('open') }}</span>
                <span class="value">{{ formatMoney(chartData?.open) }}</span>
              </p>
            </div>
            <div class="flex-r-item">
              <p>
                <span class="label">{{ $t('marketValue') }}</span>
                <span class="value">{{ formatMoney(chartData?.marketCapital) }}</span>
              </p>
              <p>
                <span class="label">{{ $t('share') }}</span>
                <span class="value">{{ formatMoney(chartData?.open) }}</span>
              </p>
              <p>
                <span class="label">{{ $t('amplitude') }}</span>
                <span class="value">{{ formatMoney(chartData?.changeRatio) }}</span>
              </p>
            </div>
            <div class="flex-r-item">
              <p>
                <span class="label">{{ $t('quantity') }}</span>
                <span class="value">{{ formatMoney(chartData?.volume) }}</span>
              </p>
              <p>
                <span class="label">{{ $t('Change') }}</span>
                <span class="value">{{ formatMoney(chartData?.turnoverRate) }}</span>
              </p>
              <p>
                <span class="label">{{ $t('Forehead') }}</span>
                <span class="value">{{ formatMoney(chartData?.volume) }}</span>
              </p>
            </div>
          </div>
        </section>
        <div class="mainBackground rounded-view" key="y">
          <PerpetualOpen class="pl-8 pr-8" :key="keyIndex + 'c'" :selectIndex="selectIndex" :symbol="symbol"
            :green-data="bids" :red-data="asks" :price="price" :init-open="initOpen" :init-close="initClose"
            :init-futrue="initFutrue" @ordered="onOrdered" @changeValueBack="changeValueBack">
          </PerpetualOpen>
          <div class="line"></div>
          <!-- 委托/持仓-->
          <PerpetualOrder class="pl-8 pr-8" :key="keyIndex + 'd'" :symbol="symbol" :order-cur="orderCur"
            :order-hold="orderHold" :price="price" :topIndex="selectIndex" :futrue-hold="futrueHold"
            :futrue-histroy="futrueHistroy" @tab="onTab" @recall="onRecall">
          </PerpetualOrder>
        </div>
      </div>

    </div>
    <div class="fixed-box">
      <div class="flex justify-between items-center px-8 py-5" @click.stop="handleClickShowKlineChart()">
        <span class="text-30  textColor">{{ type == 'cryptos' ? symbol_data.toUpperCase() + '/USDT' :
          symbol.toUpperCase() }}&nbsp;&nbsp;{{ $t('k线图表') }}</span>
        <van-icon class="textColor text-28" :name="showCharts ? 'arrow-down' : 'arrow-up'"></van-icon>
      </div>
      <!-- <section class="indicator-index-container" v-if="showKlineChart">
        <div class="indicator-index-box">
          <div class="flex-l">
            <ul>
              <li v-for="(item, index) in filterOne" :key="item" @click="handleClickSelectTime(item, index)"
                :class="[item.index === timeLabelActive ? 'active' : '']">{{
                  item.name
                }}</li>
              <li @click="handleClickMoreBtn">{{ $t('更多') }}</li>
            </ul>
          </div>
        </div>
        <div class="indicator-index-box-second" v-if="showMore">
          <ul>
            <li v-for="(item, index) in filterTwo" :key="item" @click="handleClickSelectTime(item, index)"
              :class="[item.index === timeLabelActive ? 'active' : '']">{{
                item.name
              }}</li>
          </ul>
        </div>
      </section> -->
      <div class="kline-container flex" v-if="showKlineChart">
        <div class="chart-index">
          <!-- <fx-kline :height="500" :symbol="symbol" :isShowsolid="true" :chartType="chartType" v-if="symbol" @data="onData"
            :key="`${symbol}-${timeValue}`" /> -->
          <k-line-charts :update-key="updateKey" :update-data="quote" :symbol="symbol" :showBottom="true" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ContractHeader from '@/components/Transform/contract-header/index.vue'
import PerpetualOpen from '@/components/Transform/perpetual-open/index.vue'
import PerpetualOrder from '@/components/Transform/perpetual-order/index.vue'

import { _getDeepData, _initOpen, _initClose, _futrueOrderInit, _contractApplyOrderList, contractOrder, _futrueOrderList } from "@/service/trade.api";
import { _getBalance } from '@/service/user.api'
import { _getHomeList } from '@/service/home.api'
import { Popup, Swipe, SwipeItem } from 'vant';
import { mapActions, mapGetters } from 'vuex'
import { SET_COIN_LIST } from '@/store/const.store'
import { WS_URL } from '@/config'
import { getStorage, formatMoney } from '@/utils/index'
import { useQuotesStore } from '@/store/quotes.store'
import { SET_STAGE } from '@/store/types.store';
import KLineCharts from '@/components/Transform/kline-charts/index.vue'
const quotesStore = useQuotesStore()

var showLength = 7
export default {
  name: "perpetualContract",
  components: {
    ContractHeader,
    PerpetualOpen,
    PerpetualOrder,
    [Popup.name]: Popup,
    [Swipe.name]: Swipe,
    [SwipeItem.name]: SwipeItem,
    KLineCharts
  },
  computed: {
    ...mapGetters('user', ['userInfo']),
    ...mapGetters({
      coinList: 'home/coinList',
    }),
  },
  watch: {
    selectIndex(val) {
      showLength = 7
    },
    initFutrue(val) {
      if (val) {
        if (this.$route.query.selectIndex == 2) {
          if (this.isUpdate) {
            setTimeout(() => {
              this.onTopTab(2)
            })
            this.isUpdate = false
          }
        }
      }
    }
  },
  data() {
    const initArr = []
    for (let i = 0; i < showLength; i++) {
      initArr.push({
        amount: '--',
        price: '--'
      })
    }
    return {
      quote: {},
      initTimer: null,
      keyIndex: 0,
      timeout: null,
      timer: null, // 底部持仓等的公用定时器
      timer1: null,
      balance: 0,
      symbol: '',
      price: '',
      range: '',
      initOpen: {},
      initClose: {},
      asks: initArr, // 卖单
      bids: initArr, // 买单
      orderCur: [], // 当前委托
      orderHold: [], // 持有仓位
      futrueHold: [], // 交割持仓
      futrueHistroy: [], // 交割历史
      sockets: {
        quotes: null, // 行情
        deep: null /// 深度
      },
      curTab: '', // 当前委托还是持有仓位
      selectIndex: 1, // 当前tab
      initFutrue: {}, /// 交割合约
      show: false, // popup
      animated1: false,
      animated2: false,
      currentType: 'open',
      showCharts: false,
      type: 'cryptos',
      chartData: {},
      showMore: false,
      filterOne: [
        { name: this.$t('分时'), paramsValue: 'timeSharing', seconds: 1 * 60 * 1000, index: 0, },
        { name: '1' + this.$t('天'), paramsValue: '1day', seconds: 1 * 24 * 60 * 60 * 1000, index: 1, },
        { name: '1' + this.$t('周'), paramsValue: '1week', seconds: 7 * 24 * 60 * 60 * 1000, index: 2, },
        { name: '1' + this.$t('月'), paramsValue: '1mon', seconds: 30 * 24 * 60 * 60 * 1000, index: 3, },
        { name: '5' + this.$t('天'), paramsValue: '5day', seconds: 5 * 24 * 60 * 60 * 1000, index: 4, },
        { name: this.$t('season'), paramsValue: '1quarter', seconds: 3 * 30 * 24 * 60 * 60 * 1000, index: 5, },
        { name: this.$t('Year'), paramsValue: '1year', seconds: 12 * 30 * 24 * 60 * 60 * 1000, index: 6, },
      ],
      filterTwo: [
        { name: '120' + this.$t('分'), paramsValue: '120min', seconds: 2 * 60 * 60 * 1000, index: 7, },
        { name: '60' + this.$t('分'), paramsValue: '60min', seconds: 1 * 60 * 60 * 1000, index: 8, },
        { name: '30' + this.$t('分'), paramsValue: '30min', seconds: 30 * 60 * 1000, index: 9, },
        { name: '15' + this.$t('分'), paramsValue: '15min', seconds: 15 * 60 * 1000, index: 10, },
        { name: '5' + this.$t('分'), paramsValue: '5min', seconds: 5 * 60 * 1000, index: 11, },
        { name: '1' + this.$t('分'), paramsValue: '1min', seconds: 1 * 60 * 1000, index: 12, },
      ],
      showKlineChart: false,
      chartType: '',
      timeValue: '',
      chartData: {},
      timeLabelActive: 1,
      showMore: false,
      symbolName: '',
      updateKey: 1,
      currentType: 'long',
      isUpdate: true,
      symbol_data: '',
      timerMoeny: null,
      initFunTimer1:null,
      initFunTimer2:null,
    }
  },
  async created() {

    if (this.$route.query.type) {
      this.type = this.$route.query.type
    }
    await this.SET_COIN_LIST(this.type)
    // _getBalance().then(data => { // 获取用户余额
    //   this.$store.commit('user/SET_USERINFO', { balance: data.money })
    //   // const { money } = data
    //   // this.balance = money
    // })
    if (this.userInfo.token) {
      this.getBalance()
      this.timerMoeny = setInterval(() => {
        this.getBalance()
      }, 3000)
    }
  },
  methods: {
    ...mapActions('home', [SET_COIN_LIST]),
    getBalance() {
      _getBalance().then(data => { // 获取用户余额
        this.$store.commit('user/SET_USERINFO', { balance: data.money })
        this.initFutrue.amount = data.money
        // const { money } = data
        // this.balance = money
      })
    },
    onUpdate(symbol) { // 更新
      this.currentType = 'long'
      this.symbol = symbol
      this.closeSocket()
      this.init(symbol)
    },
    changeCurrentType(type) {
      this.currentType = type
    },
    formatMoney,
    handleClickShowKlineChart() {
      this.showKlineChart = !this.showKlineChart
    },
    handleClickSelectTime(params) {
      const { paramsValue, seconds, index } = params;
      this.timeLabelActive = index;
      quotesStore[SET_STAGE]({ stage: paramsValue, seconds })
      this.onSelectTime(paramsValue)
    },
    onSelectTime(evt) {
      this.timeValue = evt
      if (evt == 'timeSharing') {
        this.chartType = 'area'
      } else {
        this.chartType = 'candle_solid'
      }
    },
    // 事件
    onData(data) {
      this.chartData = data
    },
    handleClickMoreBtn() {
      this.showMore = !this.showMore
    },
    onRecall() { // 撤单or 平仓 evt
      this.clearTimer()
      let obj = {
        symbol: this.symbol,
        type: 'orders',
        page_no: 1,
        symbolType: 'cryptos'
      }
      contractOrder(obj).then(data => {

        this.orderHold = data
      })
      this[this.curTab](this.symbol)
      _initOpen({ symbol: this.symbol }).then(data => {
        this.initOpen = data
      })
    },
    handleClickShowMore() {
      this.showMore = !this.showMore
    },
    onTopTab(evt) { // 当前tab 永续/交割
      this.keyIndex += 1
      this.selectIndex = evt
      this.clearTimer()
      if (this.selectIndex / 1 === 1) {
        this.curTab = 'fetchOrderListHold'
        this.animated2 = true
        this.timer1 = setTimeout(() => {
          this.animated2 = false
          clearTimeout(this.timer1)
        }, 200)
      } else {
        this.animated1 = true
        this.timer1 = setTimeout(() => {
          this.animated1 = false
          clearTimeout(this.timer1)
        }, 200)
      }
      this[this.curTab](this.symbol)
    },
    onOrdered(evt) { // 下单过后的回调
      this.clearTimer()
      // this.clearTimeout()
      this.initParam(this.symbol, evt) // 重新初始化
      // TODO: 这里要做判断
      if (this.selectIndex / 1 === 1) {
        this[this.curTab](this.symbol) // 重新调取记录
        console.log('this.curTab', this.curTab)
      } else { // 交割合约
        this[this.curTab](this.symbol)
        console.log('curTab', evt, this.curTab)

      }
      //console.log('下单后更新数据')
    },
    onTab(evt) { // 点击tab后的回调
      console.log('evt', evt)
      this.clearTimer()
      // this.clearTimeout()
      this.curTab = evt
      this[evt](this.symbol)
    },
    fetchQoutes(symbol) { // 获取当前行情
      _getHomeList(symbol).then(data => { // 获取行情
        this.symbolName = data[0].name
        this.symbol_data = data[0].symbol_data
        console.log(data[0].name)
        this.handleQoutes(data)
        this.startQuoteSocket() // socket
      })
    },
    handleQoutes(data) {
      if (data && data.length) {
        const cur = data[0]
        this.price = cur.close
        this.range = cur.change_ratio + ''
        this.quote = cur
        this.updateKey++
      }
    },
    fetchDeepData(symbol) {
      _getDeepData(symbol).then(data => { // 获取深度
        this.handleDeep(data)
        this.startDeepSocket() // socket
      })
    },
    handleDeep(data) {
      this.deepData = data
      const { asks, bids } = data
      // this.asks = []
      // this.bids = []
      // if (asks.length < showLength) {
      //   for (let i = 0; i < showLength - asks.length; i++) {
      //     let obj = {
      //       amount: '--',
      //       price: '--'
      //     }
      //     asks.push(obj)
      //   }
      // }
      // if (bids.length < showLength) {
      //   for (let i = 0; i < showLength - bids.length; i++) {
      //     let obj = {
      //       amount: '--',
      //       price: '--'
      //     }
      //     this.bids.push(obj)
      //   }
      // }
      this.asks = asks.sort((a, b) => b.price - a.price).slice(0, showLength)
      this.bids = bids.sort((a, b) => b.price - a.price).slice(0, showLength)
    },
    startQuoteSocket() { // 行情socket
      this.sockets.quotes = new WebSocket(`${WS_URL}/1/${this.symbol}`)
      // socket.onopen =  () => {
      //     console.log('open')
      //     socket.send('hello')
      // }
      this.sockets.quotes.onmessage = (evt) => {
        const { data } = evt
        const { code, data: _data } = JSON.parse(data)
        if (code / 1 === 0) {
          this.handleQoutes(_data)
          this.chartData = _data[0] ?? {}
        }
      }
    },
    startDeepSocket() {
      this.sockets.deep = new WebSocket(`${WS_URL}/3/${this.symbol}`)
      this.sockets.deep.onmessage = (evt) => {
        const { data } = evt
        const { code, data: _data } = JSON.parse(data)
        if (code / 1 === 0) {
          if (_data) {
            this.handleDeep(_data)
          }
        }
      }
    },
    initParam(symbol, type) { // 初始化参数
      if (type === 'open' || type === 'long' || type === 'short' || !type) {
        let initFun = () => {
          _initOpen({ symbol: symbol }).then(data => {
            console.log(data, '22222222')
            this.initOpen = data
            if(this.initFunTimer1){
              clearTimeout(this.initFunTimer1)
              this.initFunTimer1 = null
            }
          }).catch(err => {
            this.initFunTimer1 = setTimeout(() => {
              initFun()
            }, 3000);
          })
        }
        initFun()
      }
      if (type === 'close' || !type) {
        let initFun = () => {
          _initClose({ symbol: symbol }).then(data => {
            this.initClose = data
            if(this.initFunTimer2){
              clearTimeout(this.initFunTimer2)
              this.initFunTimer2 = null
            }
          }).catch(err => {
            this.initFunTimer2 = setTimeout(() => {
              initFun()
            }, 3000);
          })
        }
        this.initFunTimer2 = setTimeout(() => {
          initFun()
        }, 600);
      }
      if (type === 'futrue' || !type) {
        _futrueOrderInit(symbol).then(data => {
          console.log(data, '数据9999')
          this.initFutrue = data
        })
      }
    },
    fetchOrderListCur(symbol) { // 当前委托
      //console.log('当前委托')
      if (this.userInfo.token) {
        _contractApplyOrderList({
          symbol,
          type: 'orders',
          page_no: 1
        }).then(data => {
          this.orderCur = data
        })
        this.clearTimer()
        this.timer = setInterval(() => {
          _contractApplyOrderList({
            symbol,
            type: 'orders',
            page_no: 1
          }).then(data => {
            // if (typeof this.timer === 'string') {
            //   this.timer = null
            //   return
            // }
            this.orderCur = data
          })
        }, 1000)
      }
    },
    fetchOrderListHold(symbol) { // 当前持仓
      let obj = {
        symbol: symbol,
        type: 'orders',
        page_no: 1,
        symbolType: 'cryptos'
      }
      if (this.userInfo.token) {
        contractOrder(obj).then(data => {
          // this.orderHold = data
          this.orderHold = data.sort(this.sortData);
        })
        this.timer = setInterval(() => {
          contractOrder(obj).then(data => {
            // if (typeof this.timer === 'string') {
            //   this.timer = null
            //   return
            // }
            // this.orderHold = data
            this.orderHold = data.sort(this.sortData);
          })
        }, 1000)
      }
    },
    fetchFutrueHoldList(symbol) { // 交割持仓
      if (this.userInfo.token) {
        _futrueOrderList(symbol, 'orders', 1, 'cryptos').then(data => {
          // this.futrueHold = data
          this.futrueHold = data.sort(this.sortData);
        })
        this.timer = setInterval(() => {
          _futrueOrderList(symbol, 'orders', 1, 'cryptos').then(data => {
            // if (typeof this.timer === 'string') {
            //   this.timer = null
            //   return
            // }
            // this.futrueHold = data
            this.futrueHold = data.sort(this.sortData);
          })
        }, 1000)
      }
    },
    fetchFutrueHistory(symbol) { // 交割历史持仓
      _futrueOrderList(symbol, 'hisorders', 1, 'cryptos').then(data => {
        this.futrueHistroy = data
        // this.clearTimeout()
      })
    },
    init(symbol) { // 初始化页面
      this.symbol = symbol
      this.fetchQoutes(symbol)
      this.fetchDeepData(symbol)
      this.initParam(symbol) // 'open'
      this.clearTimer()
      //if (this.curTab === 'fetchOrderListCur') {
      if (this.curTab === 'fetchOrderListHold') {
        //this.fetchOrderListCur(symbol)
        this.fetchOrderListHold(symbol)
      } else {
        this.fetchFutrueHoldList(symbol)
      }

    },
    closeSocket() {
      this.sockets.quotes && this.sockets.quotes.close()
      this.sockets.deep && this.sockets.deep.close()
      this.sockets.quotes = null
      this.sockets.deep = null
    },
    // clearTimeout(noNeed) {
    //     clearTimeout(this.timeout)
    //     if (noNeed) { // 防止timout定时器因接口返回继续触发
    //       this.timeout = ''
    //       return
    //     }
    //     this.timeout = null
    // },
    clearTimer() {
      if(this.initFunTimer1){
        clearTimeout(this.initFunTimer1)
        this.initFunTimer1 = null
      }
      if(this.initFunTimer2){
        clearTimeout(this.initFunTimer2)
        this.initFunTimer2 = null
      }
      clearInterval(this.timer)
      // if (isNeed) {
      //   this.timer = ''
      //   return
      // }
      this.timer = null
    },
    changeCurrentType(type) {
      this.currentType = type
    },
    changeValueBack(val) {
      if (val == 0) {
        showLength = 7
      } else {
        showLength = 14
      }
    },
    sortData(a, b) {
      return new Date(b.open_time).getTime() - new Date(a.open_time).getTime()
    }
  },
  beforeRouteEnter(to, from, next) {
    let { params: { symbol }, query: { selectIndex } } = to
    let catchSymbol = getStorage('symbol')
    if (!symbol && catchSymbol) {
      symbol = catchSymbol
    }
    if (symbol) {
      next(vm => {
        if (selectIndex) {
          // vm.selectIndex = selectIndex
          if (vm.selectIndex / 1 === 2) {
            vm.curTab = 'fetchFutrueHoldList'
          } else {
            //vm.curTab = 'fetchOrderListCur'
            vm.curTab = 'fetchOrderListHold'
          }
        } else {
          //vm.curTab = 'fetchOrderListCur'
          vm.curTab = 'fetchOrderListHold'
        }
        vm.symbol = symbol
        vm.init(symbol)
      })
    } else {
      next()
    }
  },
  deactivated() {
    this.closeSocket()
    this.clearTimer()
  },
  beforeUnmount() {
    console.log(123)
    this.closeSocket()
    // this.clearTimeout(true)
    this.clearTimer()
    clearInterval(this.timerMoeny)
    this.timerMoeny = null
  }
}

</script>

<style lang="scss" scoped>
//.list-enter-active, .list-leave-active {
//  transition: all .5s;
//  transform: translateY(30px)
//}
//.list-enter, .list-leave-to
//  /* .list-leave-active for below version 2.1.8 */ {
//  transform: translateY(0)
//}


#cryptos {
  .status-info {
    margin-top: 20px;
    padding: 0 30px;
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

  .no_touch {
    -webkit-user-select: none;
    user-select: none;
    -ms-user-select: none;
    -moz-user-select: none;
  }

  .list-enter-active,
  .list-leave-active {
    will-change: transform;
    transition: all 250ms;
  }

  .list-enter {
    opacity: 0;
    transform: translate3d(-100%, 0, 0);
  }

  .list-leave {
    opacity: 0;
    transform: translate3d(100%, 0, 0);
  }

  .rounded-view {
    border-top-left-radius: 20px;
    border-top-right-radius: 20px;
    box-sizing: border-box;
  }


  .line-container {
    margin-top: 10px;
    display: flex;
    justify-content: space-between;

    .line-l {
      margin-left: 24px;
      display: flex;
      flex: 1;
      align-items: center;

      .first-line {
        font-weight: 700;
        font-size: 32px;
        color: $red;
      }

      .second-line {
        margin-left: 10px;
        text-align: left;
      }
    }


    .hidden {
      visibility: hidden;
    }

    .line-r {
      padding: 0 30px;
      display: flex;
      width: 210px;
      margin-right: 12px;
      font-size: 12px;
    }

    .title-box {
      flex: 1;
      display: flex;
      justify-content: flex-end;
      align-items: center;
      font-size: 12px;
      text-align: right;

      span {
        color: #747A8F;
      }

      img {
        margin-left: 10px;
        width: 20px;
        height: 20px;
      }
    }
  }


  .my-swipe {
    width: 100%;
  }

  .my-swipe .van-swipe-item {}

  .line {
    height: 13px;
    background: $tab_background;
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

  .slide1 {
    animation: animate1 200ms linear;
  }

  .slide2 {
    animation: animate2 200ms linear;
  }

  .value-container {
    padding: 10px 12px;
    margin-top: 4px;
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #747A8F;
    border-bottom: 1px solid $border_color;

    .flex-l {
      display: flex;
      flex-direction: column;
      justify-content: center;
      width: 240px;

      .first-line {
        font-weight: 700;
        font-size: 32px;
        color: $red;
      }

      .second-line {
        margin-top: 8px;
        text-align: left;
      }
    }

    .flex-r {
      flex: 1;
      display: flex;
      align-items: center;
      color: $text_color;

      .flex-r-item {
        flex: 1;

        .label {
          color: $lable_color;
          margin-right: 10px;
        }
      }
    }
  }
}

.fixed-box {
  position: fixed;
  width: 100%;
  bottom: 0;
  left: 0;
  z-index: 1000;
  background: $recommend_bg;
}

.indicator-index-container {
  .indicator-index-box {
    padding: 24px;
    display: flex;
    // justify-content: space-between;
    align-items: center;



    ul {
      display: flex;
      flex-wrap: wrap;

      li {
        text-align: center;
        margin: 0 8px;
        padding: 0px 8px;
        font-size: 24px;
        border-radius: 8px;
      }
    }


    .flex-r {
      display: flex;
      justify-content: flex-end;
      width: 80px;

      img {
        width: 12px;
        height: 16px;
      }

    }
  }

  .active {
    background: $btn_main;
    color: $white;
  }
}

.indicator-index-box-second {
  ul {
    display: flex;
    border: 1px solid $border_color;
    align-items: center;
    border-right: none;
  }

  li {
    flex: 1;
    height: 64px;
    line-height: 64px;
    text-align: center;
    font-size: 12px;
    border-right: 1px solid $border_color;
  }
}

.kline-container {
  margin-top: 10px;

  .order-book-container {
    padding: 100px 2px 0 6px;
    width: 130px;
    border-left: 1px solid $border_color;
  }

  .chart-index {
    flex: 1;
    min-width: 200px;
  }



  .text-sm {
    position: relative;
  }

  .select-div {
    width: 100px;
    position: absolute;
    top: 30px;
    left: 0;
    z-index: 100;

    ul {
      box-shadow: 0px 3px 11px 0px rgb(0 0 0 / 10%);

      li {
        background: $text_color;
        text-align: center;
        padding: 10px 0;
        font-size: 16px;
      }

      li:not(:last-child) {
        border-bottom: 1px solid $border-grey;
      }
    }
  }

  .active {
    background: $btn_main !important;
    color: $text_color;
  }
}
</style>
