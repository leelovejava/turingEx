<template>
  <section class="pb-40">
    <div class="container-box">
      <header class="header">
        <div class="flex-l">
          <div class="icon back" @click="handleGoBack">
            <van-icon name="arrow-left" size="20" />
          </div>
          <div class="name-box">
            <img :src="handleImage(leftIcon)" alt="convert-img" class="convert-img" @click="onSidebar" />
            <p class="title">{{ enName }}&nbsp;&nbsp;</p>
            <p class="type">{{ symbol }}</p>
          </div>
        </div>
        <div class="icon-group">
          <div class="icon search" @click="onRoute(`/optional/search?symbolType=${symbolType}`)">
            <img :src="handleImage(searchIcon)" alt="" />
          </div>
          <div class="icon setting">
            <img src="../../assets/image/icon-star_active.png" class="collected-img" @click="openCurrency"
              v-if="isCollect" />
            <img v-else src="../../assets/image/icon-star.png" class="collected-img" @click="openCurrency" />
          </div>
        </div>
      </header>
      <section class="value-container">
        <div class="flex-l">
          <p class="first-line red">{{ formatMoney(chartData?.close) }}</p>
          <p class="second-line">
            <span class="red m-4">{{ chartData?.netChange ?? '-' }}</span>
            <span class="red">{{ chartData?.change_ratio ? `${chartData?.change_ratio}%` : '-' }}</span>
          </p>
          <!-- <p class="mt-4">{{ t("盘前最新") }}</p>
          <p class="text">
            <span>{{ chartData?.chg ?? '-' }}&nbsp;&nbsp;{{ chartData?.percent ?? '-' }}</span>
          </p> -->
        </div>
        <div class="flex-r">
          <div class="flex-r-item">
            <p>
              <span class="label">{{ $t("high") }}</span>
              <span class="value">{{ formatMoney(chartData?.high) }}</span>
            </p>
            <p>
              <span class="label">{{ $t("Low") }}</span>
              <span class="value">{{ formatMoney(chartData?.low) }}</span>
            </p>
            <p>
              <span class="label">{{ $t("open") }}</span>
              <span class="value">{{ formatMoney(chartData?.open) }}</span>
            </p>
            <p class="flex">
              <span class="label">{{ $t("quantity") }}</span>
              <span class="value">{{ formatMoney(chartData?.volume) }}</span>
            </p>
            <!-- <p class="flex">
              <span class="label">{{ $t("Change") }}</span>
              <span class="value">{{
                chartData?.turnoverRate ? formatMoney(chartData?.turnoverRate) + "%" : "-"
              }}</span>
            </p> -->
          </div>
          <div class="flex-r-item">
            <p class="flex">
              <span class="label">{{ $t("Forehead") }}</span>
              <span class="value">{{ formatMoney(chartData?.volume) }}</span>
            </p>
            <p class="flex" v-if="symbolType !== 'JP-stocks' && symbolType !== 'INDIA-stocks'">
              <span class="label">{{ $t("marketValue") }}</span>
              <span class="value">{{ formatMoney(chartData?.marketCapital) }}</span>
            </p>
            <p class="flex">
              <span class="label">{{ $t("share") }}</span>
              <span class="value">{{ formatMoney(chartData?.open) }}</span>
            </p>
            <p class="flex">
              <span class="label">{{ $t("amplitude") }}</span>
              <span class="value">{{ formatMoney(chartData?.changeRatio) }}</span>
            </p>
          </div>
        </div>
      </section>
      <p class="status-info" v-if="chartData?.market?.status">
        <span>{{ chartData?.market?.status && $t(chartData?.market?.status) }},</span>
        <span class="time">{{ chartData?.market?.time_str }}</span>&nbsp;
        <span>{{ chartData?.market?.time_zone && $t(chartData?.market?.time_zone) }}</span>
      </p>
      <section class="indicator-index-container">
        <div class="indicator-index-box">
          <div class="flex-l">
            <ul>
              <li v-for="(item, index) in filterOne" :key="item" @click="handleClickSelectTime(item, index)"
                :class="[item.index === timeLabelActive ? 'active' : '']">{{
                  item.name
                }}
              </li>
              <li @click="handleClickMoreBtn">{{ t('更多') }}</li>
            </ul>
          </div>
        </div>
        <div class="indicator-index-box-second" v-if="showMore">
          <ul>
            <li v-for="(item, index) in filterTwo" :key="item" @click="handleClickSelectTime(item, index)"
              :class="[item.index === timeLabelActive ? 'active' : '']">{{
                item.name
              }}
            </li>
          </ul>
        </div>
      </section>
      <section class="kline-container flex">
        <div class="chart-index">
          <fx-kline :height="400" :symbol="symbol" :isShowsolid="true" :chartType="chartType" v-if="symbol" @data="onData"
            :key="`${symbol}-${timeValue}`" />
        </div>
        <div class="order-book-container"
          v-if="timeLabelActive === 0 && !['HK-stocks', 'JP-stocks'].includes(symbolType)">
          <!-- <keep-alive>
            <trade-deep-data :symbol="symbol" v-if="symbol" :price="price" class="trade-deep-container" />
          </keep-alive> -->
          <ul>
            <li class="flex text-22 orderbook sell" v-for="(item, index) in tradeList" :key="index">
              <span class="text">{{
                item.timestamp ? dayjs(item.timestamp * 1000).format('HH:mm') : '--'
              }}</span>
              <span>&nbsp;&nbsp;</span>
              <div class="justify-between flex flex-1">
                <span :class="item.trade_volume >= 100 ? 'text-green' : 'text-red'">{{
                  item.current ?
                  Number(item.current).toFixed(3) : '--'
                }}</span>
                <span class="text-right textColor" :class="item.trade_volume >= 100 ? 'text-green' : ''">{{
                  formatMoney(item?.trade_volume)
                }}</span>
              </div>
            </li>
          </ul>
        </div>
      </section>
      <div class="divider"></div>
      <div class="all-etf-ranking">
        <div class="tabs flex">
          <div class="tab-item" v-for="(item, index) in tabList" @click="selectTabIndex(index, item.value)"
            :class="[tabIndex === index ? 'active' : '']" :key="item">
            {{ item.title }}
          </div>
        </div>
        <F10Details :details="details" v-if="tabIndex === 0" />
        <div class="new-trade" v-if="tabIndex === 1">
          <ul class="px-12 text-grey">
            <li class="flex justify-between mt-30">
              <span class="flex-1">{{ $t('时间') }}</span>
              <span class="flex-1">{{ $t('方向') }}</span>
              <span class="flex-1">{{ $t('价格') }}(USD)</span>
              <span class="flex-1 flex justify-center">{{ $t('数量') }}</span>
            </li>
            <li v-for="(item, index) in deals" :key="item.ts + item.price + item.amount || index"
              class="flex  justify-between mt-30">
              <span class="flex-1">{{ item.current_time ? item.current_time : '--' }}</span>
              <span :class="item.direction === 'buy' ? 'text-green' : 'text-red'" class="flex-1">{{
                item.direction === 'buy' ? $t('买入') : $t('卖出')
              }}</span>
              <span :class="item.direction === 'buy' ? 'text-green' : 'text-red'" class="flex-1 flex-justify-center">{{
                item.price || '--'
              }}</span>
              <span class="flex-1 flex justify-center">{{ item.amount || '--' }}</span>
            </li>
          </ul>
        </div>
        <div class="deep-map" v-if="tabIndex === 2">
          <div class="buy-sell-box">
            <div class="buy-item">
              <div class="bg-line"></div>
              {{ $t('买盘') }}
            </div>
            <div class="sell-item">
              <div class="bg-line"></div>
              {{ $t('卖盘') }}
            </div>
          </div>
          <div class="deep-chart-box">
            <deep-chart :deepBuy="deepBuy" :deepSell="deepSell"></deep-chart>
          </div>
        </div>
      </div>
      <div class="footer-btn-group">
        <div class="flex btn-group">
          <div class="flex-l" @click="onRoute('/cryptos/exchangeRate')">
            <img src="@/assets/image/quotes/exchange.png" alt="">
            <p class="rate">{{ t('汇率') }}</p>
          </div>
          <div class="flex-r flex-1 flex">
            <div class="buy-btn" @click="onRoute('/quotes/openTrade', 0)">{{ t('买入') }}</div>
            <div class="sell-btn" @click="onRoute('/quotes/openTrade', 1)">{{ t('卖出') }}</div>
          </div>
        </div>
      </div>
    </div>
    <add-currency @updateItem="getIsItemHasAddGlobal" :isCollect="isCollect" ref="addCurrencyRef"></add-currency>
    <!-- 左侧边弹出菜单 -->
    <van-popup class="popup" round v-model:show="show" close-icon-position="top-left" position="left" @closed="onClose">

      <div class="flex pl-10 pr-10 justify-between mb-10 mt-10 popup-wrap ">
        <div class="flex items-center text-grey">
          <div class="mr-12 popup-title">{{ $t('名称') }}</div>
        </div>
        <div class="flex text-grey">
          <div class="flex items-center">
            <div class="popup-title">{{ $t('最新价格') }}</div>
          </div>
          <div class="flex items-center">
            <div class="mr-12 popup-title">/24H{{ $t('涨跌') }}</div>
          </div>
        </div>
      </div>
      <div class="pl-10 pr-10">
        <div class="flex justify-between mb-10" v-for="item in iconList" :key="item.name" @click="onRouteTwo(item)">
          <div>
            <div class="font-bold textColor popup-title">{{ item.name || '--' }}</div>
            <div v-if="!kineType" class="text-grey mt-4 popup-title">{{ title }}</div>
          </div>
          <div class="text-right">
            <div class="textColor popup-title">{{ item.close || '--' }}</div>
            <div class="mt-1 popup-title" :class="item.change_ratio > 0 ? 'text-green' : 'text-red'">{{
              item.change_ratio
            }}%
            </div>
          </div>
        </div>
      </div>
    </van-popup>
  </section>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Popup } from 'vant';
import { useI18n } from 'vue-i18n'
import fxKline from '@/components/fx-kline/index.vue'
import { useUserStore } from '@/store/user';
import { useQuotesStore } from '@/store/quotes.store';
import { SET_STAGE } from '@/store/types.store';
import { _getHomeList } from "@/service/home.api";
import { _getCoins } from '@/service/cryptos.api'
import { _getQuotes, _isItemHasAddGlobal } from '@/service/quotes.api'
import { _getItemSummary, _getConstituentStockList, _getStockTradeList } from '@/service/etf.api'
import TradeDeepData from '@/components/trade-deep-data/index.vue'
import { formatMoney } from '@/utils'
import F10Details from './components/F10Details.vue'
import addCurrency from '@/components/add-currency/index.vue'
import dayjs from 'dayjs'
import { WS_URL } from '@/config';
import deepChart from '@/components/Transform/deepChart/index.vue'
import { themeStore } from '@/store/theme';

const thStore = themeStore()

const arr = []
for (let i = 0; i < 17; i++) {
  arr.push({
    id: i
  })
}
const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const show = ref(false)
const quotesStore = useQuotesStore()
const symbol = ref('')
const symbolName = ref('')
const enName = ref('')
const fromIndexDetail = route.query.fromIndexDetail
const timeValue = ref('')
const chartData = ref({})
const listData = ref([])
const tradeList = ref([])
const active = ref(0)
const timeLabelActive = ref(0)
const chartType = ref('')
const tabIndex = ref(0)
const constituentList = ref([])
const isConstituent = ref(false)
const price = ref('')
const showMore = ref(false)
const details = ref({})
const addCurrencyRef = ref(null)
const isCollect = ref(false)
const symbolType = ref(route.query.symbolType || 'US-stocks')
const iconList = ref([])
const coins = ref([])
const timeout = ref(null)
const deals = ref(arr)
const quote = ref({})
const sockets = ref({
  quote: null,
  deals: null,
  askBid: null
})
const deepBuy = ref([])
const deepSell = ref([])
const asks = ref(arr)
const bids = ref(arr)

const tabList = route.query.symbolType === 'US-stocks' ? ref([
  { title: t('ProfileF10'), value: 0 },
  { title: t('最新交易'), value: 1 },
  { title: t('深度图'), value: 2 },
]) : ref([
  { title: t('ProfileF10'), value: 0 },
])

const filterOne = ref([
  { name: t('分时'), paramsValue: 'timeSharing', seconds: 1 * 60 * 1000, index: 0, },
  { name: '1' + t('天'), paramsValue: '1day', seconds: 1 * 24 * 60 * 60 * 1000, index: 1, },
  { name: '1' + t('周'), paramsValue: '1week', seconds: 7 * 24 * 60 * 60 * 1000, index: 2, },
  { name: '1' + t('月'), paramsValue: '1mon', seconds: 30 * 24 * 60 * 60 * 1000, index: 3, },
  { name: '5' + t('天'), paramsValue: '5day', seconds: 5 * 24 * 60 * 60 * 1000, index: 4, },
  { name: t('season'), paramsValue: '1quarter', seconds: 3 * 30 * 24 * 60 * 60 * 1000, index: 5, },
  { name: t('Year'), paramsValue: '1year', seconds: 12 * 30 * 24 * 60 * 60 * 1000, index: 6, },
])

const filterTwo = ref([
  { name: '120' + t('分'), paramsValue: '120min', seconds: 2 * 60 * 60 * 1000, index: 7, },
  { name: '60' + t('分'), paramsValue: '60min', seconds: 1 * 60 * 60 * 1000, index: 8, },
  { name: '30' + t('分'), paramsValue: '30min', seconds: 30 * 60 * 1000, index: 9, },
  { name: '15' + t('分'), paramsValue: '15min', seconds: 15 * 60 * 1000, index: 10, },
  { name: '5' + t('分'), paramsValue: '5min', seconds: 5 * 60 * 1000, index: 11, },
  { name: '1' + t('分'), paramsValue: '1min', seconds: 1 * 60 * 1000, index: 12, },
])

const leftIcon = new URL(`../../assets/theme/${thStore.theme}/image/black-convert.png`, import.meta.url)
const searchIcon = new URL(
  `../../assets/theme/${thStore.theme}/image/search.png`,
  import.meta.url
);

onMounted(async () => {
  if (route.query.symbol) {
    symbol.value = route.query.symbol
  } else {
    symbol.value = quotesStore.coins.length ? quotesStore.coins[0].symbol : 'TSLA'
  }
  enName.value = route.query.enName ? route.query.enName : ''
  if (quotesStore.stage === 'timeSharing') {
    chartType.value = 'area'
  } else {
    chartType.value = 'candle_solid'
  }
  fetchQuotes()
  getStockTradeList(symbol.value)
  getItemSummary(symbol.value)
  getConstituentStockList(symbol.value)
  getIsItemHasAddGlobal()
  // 默认日k
  handleClickSelectTime({
    paramsValue: 'timeSharing',
    seconds: 1 * 24 * 60 * 60 * 1000,
    index: 0
  })
})

onBeforeUnmount(() => {
  if (timeout.value) {
    clearTimeout(timeout.value)
  }
  closeSocket()
  sockets.value.quote && sockets.value.quote.close()
  sockets.value.deals && sockets.value.deals.close()
  sockets.value.askBid && sockets.value.askBid.close()
  sockets.value.quote = null
  sockets.value.deals = null
  sockets.value.askBid = null
})

watch(show, (newVal, oldVal) => {
  if (!newVal) {
    if (timeout.value) {
      clearTimeout(timeout.value)
    }
  }
})

watch([tabIndex, symbol], ([val, val2]) => {
  if (val / 1 === 0) {
    sockets.value.deals && sockets.value.deals.close()
    sockets.value.deals = null
    if (val2) { // 刚进来可能是null
      symbol.value = val2
      startAskBidSocket()
    }
  } else {
    sockets.value.askBid && sockets.value.askBid.close()
    sockets.value.askBid = null
    startDealsSocket()
  }
}, {
  immediate: true
})

const startAskBidSocket = () => { // 委托
  sockets.value.askBid = new WebSocket(`${WS_URL}/3/${symbol.value}`)
  sockets.value.askBid.onmessage = (evt) => {
    const { data } = evt
    const { code, data: _data } = JSON.parse(data)
    if (code / 1 === 0) {
      deepBuy.value = _data.bids
      deepSell.value = _data.asks
      _data.asks = _data.asks.sort((prev, next) => prev.price - next.price)
      _data.bids = _data.bids.sort((prev, next) => prev.price - next.price)
      asks.value = _data.asks.slice(0, 17)
      bids.value = _data.bids.reverse().slice(0, 17)
    }
  }
}
const startDealsSocket = () => { // 交易
  sockets.value.deals = new WebSocket(`${WS_URL}/2/${symbol.value}`)
  sockets.value.deals.onmessage = (evt) => {
    const { data } = evt
    const { code, data: _data } = JSON.parse(data)
    // todo: 数据有些问题
    if (code / 1 === 0) {
      deals.value = _data.data.slice(0, 17)
    }
  }
}

const closeSocket = () => {
  sockets.value.quote && sockets.value.quote.close()
  sockets.value.deals && sockets.value.deals.close()
  sockets.value.askBid && sockets.value.askBid.close()
  sockets.value.quote = null
  sockets.value.deals = null
  sockets.value.askBid = null
}

const onRouteTwo = (item) => {
  symbol.value = item.symbol
  enName.value = item.name
  show.value = false
  router.push(`/quotes/usStockDetail?symbol=${item.symbol}&symbolType=${symbolType.value}&enName=${item.name}`)
  fetchQuotes()
  getItemSummary(symbol.value)
  getConstituentStockList(symbol.value)
  getIsItemHasAddGlobal()
  getStockTradeList(symbol.value)
  fetchData()
}

const fetchData = () => {
  closeSocket()
  _getHomeList(symbol.value).then(data => {
    quote.value = data[0]
    nextTick(() => {
      if (!sockets.value.quote && symbol.value) {
        startQuoteScoket()
      }
      if ((tabIndex.value === 2 || tabIndex.value === 3) && !sockets.value.askBid && symbol.value) {
        startAskBidSocket()
      }
    })
    startDealsSocket()
  })
}
const startQuoteScoket = () => {
  sockets.value.quote = new WebSocket(`${WS_URL}/1/${symbol.value}`)
  sockets.value.quote.onmessage = (evt) => {
    const { data } = evt
    const { code, data: _data } = JSON.parse(data)
    if (code / 1 === 0) {
      quote.value = _data[0]
      // updateKey.value++
    }
  }
}
const fetchList = () => { // 获取行情
  _getHomeList(coins.value.join(',')).then(list => {
    iconList.value = list
    if (timeout.value) {
      clearTimeout(timeout.value)
      timeout.value = null
    }
    timeout.value = setTimeout(() => {
      fetchList()
    }, 2000)
  })
}

//获取币种
const getCoins = () => {
  _getCoins({ type: symbolType.value }).then((res) => {
    coins.value = res.map(item => item.symbol)
    fetchList()
  })
}

const onSidebar = () => { // 侧边栏打开
  if (!symbolType.value) {
    return
  }
  show.value = true
  getCoins()
}

const handleGoBack = () => {
  console.log(route.query, 'route.query')
  if (route.query.isOptional == 1) {
    router.push(`/optional/index`)
    return
  }
  // if (route.query.isOptional == 2){
  //   router.push(`/optional/search`)
  //   return
  // }
  if (route.query.isMore == 1) {
    if(route.query.category){
      router.push(`/quotes/UsStockMore?symbolType=${route.query.symbolType}&typName=${route.query.typName}&category=${route.query.category}&tabIndex=${route.query.tabIndex}`)
    }else{
      router.push(`/quotes/UsStockMore?symbolType=${route.query.symbolType}&typName=${route.query.typName}&tabIndex=${route.query.tabIndex}`)
    }
    return
  }
  if (route.query.from === 'trade') {
    router.push(`/trade/index?tabActive=3&navActive=3`)
  }
  if (fromIndexDetail) {
    router.go(-1)
  } else if(route.query.tabIndex) {
    onRoute(`/quotes/index?tabActive=${route.query.tabIndex}`)
  }else{
    router.go(-1)
  }
}

const onRoute = (path, tabActive) => {
  if (path === '/quotes/openTrade') {
    router.push({
      path,
      query: {
        tabActive,
        symbol: symbol.value,
        type: symbolType.value,
        tabIndex:route.query.tabIndex
      }
    })
  } else {
    router.push(path)
  }

}

// 点击成分股查一次详情
const itemClick = (item) => {
  router.push(`/quotes/constituentDetail?symbol=${item.relatedStockSymbol}`)
}

const handleClickSelectTime = (params) => {
  const { paramsValue, seconds, index } = params;
  timeLabelActive.value = index;
  quotesStore[SET_STAGE]({ stage: paramsValue, seconds })
  onSelectTime(paramsValue)
}

const onSelectTime = (evt) => {
  timeValue.value = evt
  if (evt == 'timeSharing') {
    chartType.value = 'area'
  } else {
    chartType.value = 'candle_solid'
  }
}

// 事件
const onData = (data) => {
  chartData.value = data
  // symbolType.value = data?.type
}
const fetchQuotes = () => {
  _getQuotes(quotesStore.coins).then(data => {
    data.map(item => {
      item.name = item.symbol
    })
    listData.value = data
  })
}

// 获取简况数据
const getItemSummary = (symbol) => {
  _getItemSummary(symbol).then(res => {
    details.value = res
    symbolName.value = res.symbolName
  })
}

const getConstituentStockList = (symbol) => {
  _getConstituentStockList(symbol).then(res => {
    constituentList.value = res
    isConstituent.value = res.length === 0
  })
}

// 交易记录
const getStockTradeList = (symbol) => {
  _getStockTradeList(symbol).then(res => {
    if (res && res.length > 0) {
      const result = res.sort((a, b) => {
        return b.timestamp - a.timestamp
      })
      tradeList.value = result.slice(0, 16) || []
    } else {
      tradeList.value = []
    }
  })
}

const selectTabIndex = (index, value) => {
  tabIndex.value = index
}

const handleClickMoreBtn = () => {
  showMore.value = !showMore.value
}
//打开自选弹窗
const openCurrency = () => {
  addCurrencyRef.value.openCurrency(symbol.value)
}
//判断是否加入收藏
const getIsItemHasAddGlobal = () => {
  let obj = {
    symbol: symbol.value
  }
  _isItemHasAddGlobal(obj).then((data) => {
    isCollect.value = data
  })
}

const onClose = () => {
  show.value = false
}
const handleImage = (url) => {
  return new URL(url, import.meta.url).href
}
</script>
<style lang="scss" scoped>
:deep(.van-sidebar) {
  width: 100%;
}

:deep(.van-sidebar-item) {
  background-color: $main2_background;
  color: $text_color;
  padding: 10px;
}

:deep(.van-sidebar-item--select) {
  background-color: $select-bg;
  color: $color_main;
}

.container-box {
  padding: 0 4px 50px 10px;

  .new-trade {
    margin-top: 10px;
    font-size: 14px;
  }

  .green {
    color: $green;
  }

  .red {
    color: $red;
  }

  .header {
    display: flex;
    align-items: center;

    .flex-l {
      flex: 1;
      display: inline-flex;
      align-items: center;

      .icon {
        margin-right: 10px;
        display: inline-block;
        width: 20px;
        height: 20px;

        img {
          height: 20px;
          width: 20px;
        }
      }

      .name-box {
        flex: 1;
        text-align: center;
        font-weight: 700;
        font-size: 14px;
        line-height: 28px;
        color: $mainTextColor;
        display: flex;
        align-items: center;
        justify-content: center;

        .convert-img {
          width: 20px;
          height: 20px;
          margin-right: 10px;
        }

        .title {
          font-size: 16px;
          font-weight: 700;
          line-height: 16px;
        }

        .type {
          font-size: 12px;
          color: $text_color6;
        }
      }
    }

    .icon-group {
      width: 100px;
      text-align: right;

      .icon {
        display: inline-block;
        width: 28px;
        height: 28px;
        padding: 4px;
        margin-left: 16px;
      }
    }
  }

  .status-info {
    padding: 0 6px;
    font-size: 12px;
    line-height: 32px;
    height: 32px;
    border-bottom: 1px solid $border_color;

    .time {
      display: inline-block;
      margin-right: 10px;
    }
  }

  .hk-new-price {
    margin-top: 10px;
    overflow: hidden;

    .text {
      color: $lable_color;
      font-size: 12px;
      font-weight: 700;
      margin-left: 124px;

      .value {}
    }
  }

  .value-container {
    margin-top: 10px;
    padding-bottom: 10px;
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    font-weight: 700;
    color: #747A8F;
    border-bottom: 1px solid $border_color;

    .flex-l {
      display: flex;
      flex-direction: column;
      justify-content: center;
      width: 128px;

      .first-line {
        font-weight: 700;
        font-size: 24px;
      }

      .second-line {
        margin-top: 8px;
      }
    }

    .flex-r {
      flex: 1;
      display: flex;
      // align-items: center;
      color: $text_color;

      .flex-r-item {
        // flex: 1;
        display: inline-block;
        width: 50%;
        text-align: left;

        .label {
          color: $lable_color;
          margin-right: 6px;
          text-align: left;
          padding-left: 5px;
        }
      }
    }
  }

  .base-info {
    .flex-r-item {
      margin: 0 !important;

      .label {
        display: inline-block;
        width: 130px;
        text-align: left;
      }

      .value {
        text-align: left;
        flex: 1;
      }
    }
  }

  .indicator-index-container {
    .indicator-index-box {
      padding: 12px 0;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .flex-l {
        flex: 1;

        ul {
          display: flex;

          li {
            flex: 1;
            text-align: center;
            margin: 0 4px;
            font-size: 12px;
            border-radius: 4px;
          }
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

    .indicator-index-box-second {
      ul {
        display: flex;
        border: 1px solid $border_color;
        align-items: center;
        border-right: none;
      }

      li {
        flex: 1;
        height: 32px;
        line-height: 32px;
        text-align: center;
        font-size: 12px;
        border-right: 1px solid $border_color;
      }
    }
  }

  .kline-container {
    margin-top: 10px;

    .order-book-container {
      padding: 10px 2px 0 4px;
      width: 130px;
      border-left: 1px solid $border_color;

      .orderbook {
        line-height: 20px;
        margin-bottom: 6px;
      }
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
          background: $mainbgWhiteColor;
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

  .all-etf-ranking {
    margin-top: 10px;

    .title {
      font-weight: 700;
      padding: 0 12px;
    }

    .tabs {
      padding: 0 12px;
      margin-top: 10px;
      // height: 40px;
      min-height: 40px;
      line-height: 24px;
      color: #BBBCBD;

      .tab-item {
        margin: 4px;
        text-align: center;
        padding: 4px 6px;
        font-size: 12px;
        color: $text_color5;
        background: $US_tab_background;
        border-radius: 10px;
        background-size: cover;
      }

      .active {
        font-weight: 700;
        color: $color_main !important;
        background: $US_tabActice_background;
        border-radius: 10px;
        background-size: cover;
      }
    }

    .etf-table {
      .right {
        text-align: right;
      }

      ul {
        margin-top: 10px;
      }

      .title-line {
        color: #747A8F;
        font-size: 12px;
        font-weight: 400;
        padding: 0 12px;
        border: none;
      }

      li {
        padding: 14px 12px;
        display: flex;
        font-size: 12px;
        line-height: 18px;
        border-bottom: 1px solid $border_color;

        .gray-text {
          color: #BCBDC2;
          font-size: 12px;
        }

        .flex-l {
          width: 100px;
        }

        .flex-r {
          display: inline-flex;
          flex: 1;

          .flex-r-item {
            flex: 1;
            align-self: center;
            text-align: center;
          }
        }
      }
    }
  }

  .footer-btn-group {
    position: fixed;
    z-index: 10;
    left: 0;
    right: 0;
    bottom: calc(constant(safe-area-inset-bottom)) !important;
    bottom: calc(env(safe-area-inset-bottom)) !important;
    background: $btn-group;
    height: 62px;
    width: 100%;

    .btn-group {
      padding: 8px;
      align-items: center;
    }

    .flex-l {
      margin-left: 20px;
      text-align: center;

      .rate {
        font-size: 12px;
      }

      img {
        width: 22px;
        height: 22px;
        margin: 0 auto;
      }
    }

    .flex-r {
      display: flex;
      justify-content: center;

      .sell-btn,
      .buy-btn {
        text-align: center;
        font-size: 14px;
        font-weight: 700;
        width: 140px;
        height: 36px;
        line-height: 36px;
      }

      .sell-btn {
        background: $red;
        color: $main-btn-color;
      }

      .buy-btn {
        background: #5BB989;
        margin-right: 20px;
        color: $main-btn-color;
      }
    }
  }
}

.collected-img {
  width: 30px !important;
  height: 20px !important;
}

:deep(.van-popup) {
  height: 100%;
  width: 300px;
  background: $main_background;
}

.text-green {
  color: $green;
}

.text-red {
  color: $red;
}

.popup-wrap {
  font-size: 16px;


}

.popup-title {
  font-size: 14px;
}

.buy-sell-box {
  display: flex;
  justify-content: center;
  margin: 30px 0px !important;

  .buy-item {
    display: flex;

    color: $text_color;

    .bg-line {
      width: 20px;
      height: 20px;
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
      width: 20px;
      height: 20px;
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
</style>