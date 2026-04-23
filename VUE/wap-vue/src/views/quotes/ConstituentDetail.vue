<template>
  <section class="pb-40">
    <div class="container-box">
      <header class="header">
        <div class="flex-l" @click="router.go(-1)">
          <div class="icon back">
            <van-icon name="arrow-left" size="20" />
          </div>
          <div class="name-box">
            <p class="title">{{ chartData?.name }}</p>
            <p class="type">{{ symbol }}</p>
          </div>
        </div>
        <div class="icon-group">
          <div class="icon search" @click="onRoute('/optional/search')">
            <img src="@/assets/image/optional/search.png" alt="">
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
          <p class="first-line red">0.1507</p>
          <p class="second-line">
            <span class="red">-0.74</span>
            <span class="red">-123.15%</span>
          </p>
        </div>
        <div class="flex-r">
          <div class="flex-r-item">
            <p>
              <span class="label">{{ $t('high') }}</span>
              <span class="value">{{ chartData?.high }}</span>
            </p>
            <p>
              <span class="label">{{ $t('Low') }}</span>
              <span class="value">{{ chartData?.low }}</span>
            </p>
            <p>
              <span class="label">{{ $t('open') }}</span>
              <span class="value">{{ chartData?.open }}</span>
            </p>
          </div>
          <div class="flex-r-item">
            <p>
              <span class="label">{{ t('marketValue') }}</span>
              <span class="value">{{ chartData?.open }}</span>
            </p>
            <p>
              <span class="label">{{ $t('share') }}</span>
              <span class="value">{{ chartData?.open }}</span>
            </p>
            <p>
              <span class="label">{{ $t('amplitude') }}</span>
              <span class="value">{{ chartData?.open }}</span>
            </p>
          </div>
          <div class="flex-r-item">
            <p>
              <span class="label">{{ $t('quantity') }}</span>
              <span class="value">{{ chartData?.volume }}</span>
            </p>
            <p>
              <span class="label">{{ $t('Change') }}</span>
              <span class="value">{{ chartData?.change_ratio }}</span>
            </p>
            <p>
              <span class="label">{{ $t('Forehead') }}</span>
              <span class="value">{{ chartData?.open }}</span>
            </p>
          </div>
        </div>
      </section>
      <section class="indicator-index-container">
        <div class="indicator-index-box">
          <div class="flex-l">
            <ul>
              <li v-for="(item, index) in filterOne" :key="item" @click="handleClickSelectTime(item, index)"
                :class="[item.index === timeLabelActive ? 'active' : '']">{{
                  item.name
                }}</li>
              <li @click="handleClickMoreBtn">{{ t('更多') }}</li>
            </ul>
          </div>
          <div class="flex-r">
            <!-- <img src="@/assets/image/quotes/index-setting.png" alt=""> -->
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
      </section>
      <section class="kline-container flex">
        <div class="chart-index">
          <fx-kline :height="550" :symbol="symbol" :isShowsolid="true" :chartType="chartType" v-if="symbol" @data="onData"
            :key="`${symbol}-${timeValue}`" />
        </div>
        <div class="order-book-container" v-if="timeLabelActive === 0">
          <keep-alive>
            <trade-deep-data :symbol="symbol" v-if="symbol" :price="price" class="trade-deep-container" />
          </keep-alive>
        </div>
      </section>
      <div class="divider"></div>
      <div class="all-etf-ranking">
        <div class="tabs flex">
          <div class="tab-item flex-1 active">
            {{ t('ProfileF10') }}
          </div>
        </div>
        <div class="etf-table" v-if="tabIndex === 0">
          <ul>
            <li class="title-line">
              <div class="flex-l">
                <p>{{ t('nameCode') }}</p>
              </div>
              <div class="flex-r">
                <div class="flex-r-item">
                  <p>{{ t('PositionRatio') }}</p>
                </div>
                <div class="flex-r-item">
                  <p>{{ t('uptodate') }}</p>
                </div>
                <div class="flex-r-item">
                  <p>{{ t('涨跌幅') }}</p>
                </div>
              </div>
            </li>
            <li v-for="(item) in constituentList" :key="item.relatedStockSymbolName" @click="itemClick(item)">
              <div class="flex-l">
                <p>{{ item.relatedStockSymbolName }}</p>
                <p class="gray-text">{{ item.transactionPairsSymbol }}</p>
              </div>
              <div class="flex-r">
                <div class="flex-r-item">
                  <p class="text-down">
                    {{ item.positionProportion ? `${item.positionProportion}%` : '-' }}
                  </p>
                </div>
                <div class="flex-r-item">
                  <p :class="item.close < 1 ? 'text-up' : 'text-down'">{{
                    item.realtime?.close
                  }}</p>
                </div>
                <div class="flex-r-item">
                  <p :class="item.close < 1 ? 'text-up' : 'text-down'">{{
                    item.realtime?.change_ratio
                  }}</p>
                </div>
              </div>
            </li>
          </ul>
        </div>
        <F10Details :details="details" v-if="tabIndex === 1" />
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
  </section>
</template>
      
<script setup>
import { ref, onMounted, onBeforeMount } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n'
import fxKline from '@/components/fx-kline/index.vue'
import { useUserStore } from '@/store/user';
import { useQuotesStore } from '@/store/quotes.store';
import { SET_STAGE } from '@/store/types.store';
import { _getQuotes, _isItemHasAddGlobal } from '@/service/quotes.api'
import { _getItemSummary, _getConstituentStockList } from '@/service/etf.api'
import TradeDeepData from '@/components/trade-deep-data/index.vue'
import F10Details from './components/F10Details.vue'
import addCurrency from '@/components/add-currency/index.vue'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const show = ref(false)
const quotesStore = useQuotesStore()
const symbol = ref('')
const timeValue = ref('')
const chartData = ref({})
const listData = ref([])
const timeLabelActive = ref(1)
const chartType = ref('')
const tabIndex = ref(0)
const constituentList = ref([])
const isConstituent = ref(false)
const price = ref('')
const showMore = ref(false)
const stocksType = ref('US-stocks')
const details = ref({})
const addCurrencyRef = ref(null)
const isCollect = ref(false)
const tabList = ref([
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

onMounted(async () => {
  if (route.query.symbol) {
    symbol.value = route.query.symbol
  } else {
    symbol.value = quotesStore.coins.length ? quotesStore.coins[0].symbol : 'TSLA'
  }
  if (quotesStore.stage === 'timeSharing') {
    chartType.value = 'area'
  } else {
    chartType.value = 'candle_solid'
  }
  fetchQuotes()
  getItemSummary(symbol.value)
  getConstituentStockList(symbol.value)
  getIsItemHasAddGlobal()
  handleClickSelectTime({
    paramsValue: '1day',
    seconds: 1 * 24 * 60 * 60 * 1000,
    index: 1
  })
})

onBeforeMount(() => {

})

const handleShowPopup = () => {
  show.value = true;
}

const onRoute = (path, tabActive) => {
  if (path === '/quotes/openTrade') {
    router.push({
      path,
      query: {
        tabActive
      }
    })
  } else {
    router.push(path)
  }

}

// 点击成分股查一次详情
const itemClick = (item) => {
  router.push(`/quotes/detail?symbol=${item.relatedStockSymbol}`)
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
  })
}

const getConstituentStockList = (symbol) => {
  _getConstituentStockList(symbol).then(res => {
    constituentList.value = res
    isConstituent.value = res.length === 0
  })
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
  padding: 0 10px 50px 10px;

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

  .value-container {
    margin-top: 20px;
    padding-bottom: 20px;
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #747A8F;
    border-bottom: 1px solid $border_color;

    .flex-l {
      display: flex;
      flex-direction: column;
      justify-content: center;
      width: 120px;

      .first-line {
        font-weight: 700;
        font-size: 32px;
      }

      .second-line {
        margin-top: 8px;
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
      width: 140px;

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
    bottom: calc(50px + constant(safe-area-inset-bottom)) !important;
    bottom: calc(50px + env(safe-area-inset-bottom)) !important;
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
      justify-content: flex-end;

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
</style>