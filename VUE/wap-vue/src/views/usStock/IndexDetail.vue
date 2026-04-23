<template>
  <section class="pb-40">
    <div class="container-box">
      <header class="header">
        <div class="flex-l" @click="handleGoBack">
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
          <p class="first-line red">{{ chartData?.close }}</p>
          <p class="second-line">
            <span class="red mr-5">{{ chartData?.change_ratio }}</span>
            <span class="red">{{ chartData?.netChange }}</span>
          </p>
        </div>
        <div class="flex-r">
          <div class="flex-r-item">
            <div>
              <p class="label">{{ t('24h最高价') }}</p>
              <p class="value">{{ formatMoney(chartData?.high) }}</p>
            </div>
          </div>
          <div class="flex-r-item">
            <div>
              <p class="label">{{ t('24h最低价') }}</p>
              <p class="value">{{ formatMoney(chartData?.low) }}</p>
            </div>
          </div>
          <div class="flex-r-item">
            <div>
              <p class="label">{{ t('成交量') }} </p>
              <p class="value">{{ formatMoney(chartData?.volume) }}</p>
            </div>
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
        <!-- <div class="order-book-container" v-if="timeLabelActive === 0">
          <keep-alive>
            <trade-deep-data :symbol="symbol" v-if="symbol" :price="price" class="trade-deep-container" />
          </keep-alive>
        </div> -->
      </section>
      <div class="divider"></div>
      <div class="all-etf-ranking">
        <div class="tabs flex">
          <div class="tab-item" v-for="(item, index) in tabList" @click="selectTabIndex(item.value)"
            :class="[tabIndex === item.value ? 'active' : '']" :key="item">
            {{ item.title }}
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
                  <p>{{ t('盘前涨跌幅') }}</p>
                </div>
                <div class="flex-r-item">
                  <p>{{ t('盘前价') }}</p>
                </div>
              </div>
            </li>
            <li v-for="(item) in relateStocksList" :key="item.relatedStockSymbolName" @click="itemClick(item)">
              <div class="flex-l">
                <p>{{ item.name }}</p>
                <p class="gray-text">{{ item.symbol }}</p>
              </div>
              <div class="flex-r">
                <div class="flex-r-item">
                  <p :class="item.change_ratio < 0 ? 'text-up' : 'text-down'">{{
                    item?.change_ratio
                  }}</p>
                </div>
                <div class="flex-r-item">
                  <p>{{
                    item?.close
                  }}</p>
                </div>
              </div>
            </li>
          </ul>
        </div>
        <F10Details :details="details" v-if="tabIndex === 1" />
      </div>
    </div>
    <add-currency @updateItem="getIsItemHasAddGlobal" :isCollect="isCollect" ref="addCurrencyRef"></add-currency>
  </section>
</template>
      
<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n'
import fxKline from '@/components/fx-kline/index.vue'
import { useUserStore } from '@/store/user';
import { useQuotesStore } from '@/store/quotes.store';
import { SET_STAGE } from '@/store/types.store';
import { _getQuotes, _isItemHasAddGlobal } from '@/service/quotes.api'
import { _getItemSummary, _getRelateStocks } from '@/service/etf.api'
import TradeDeepData from '@/components/trade-deep-data/index.vue'
import F10Details from './components/F10Details.vue'
import addCurrency from '@/components/add-currency/index.vue'
import { formatMoney } from '@/utils/index'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const show = ref(false)
const quotesStore = useQuotesStore()
const symbol = ref('')
const timeValue = ref('')
const chartData = ref({})
const listData = ref([])
const active = ref(0)
const timeLabelActive = ref(2)
const chartType = ref('')
const tabIndex = ref(0)
const relateStocksList = ref([])
const price = ref('')
const showMore = ref(false)
const symbolType = route.query.symbolType || "US-stocks";
const details = ref({})
const isCollect = ref(false)
const addCurrencyRef = ref(null)
const tabList = ref([
  { title: t('relatedStocks'), value: 0 },
  { title: t('ProfileF10'), value: 1 },
])

//A股
if (symbolType === 'A-stocks') {
  tabList.value = [{ title: t("ProfileF10"), value: 1 }]
  tabIndex.value = 1
}

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
  getRelateStocks(symbol.value)
  getIsItemHasAddGlobal()
  // 默认日k
  handleClickSelectTime({
    paramsValue: '1day',
    seconds: 1 * 24 * 60 * 60 * 1000,
    index: 1
  })
})

onBeforeUnmount(() => {

})

const handleGoBack = () => {
  if(symbolType === 'US-stocks') {
    onRoute('/quotes/index?tabActive=3')
  } else if(symbolType === 'A-stocks'){
    onRoute('/quotes/index?tabActive=7')
  }else{
    router.go(-1)
  }
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
  router.push(`/quotes/usStockDetail?symbol=${item.symbol}&fromIndexDetail=true`)
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

const getRelateStocks = (symbol) => {
  _getRelateStocks(symbol).then(res => {
    relateStocksList.value = res?.stocks || []
  })
}

const selectTabIndex = (value) => {
  tabIndex.value = value
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
      width: 160px;

      .first-line {
        font-weight: 700;
        font-size: 30px;
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
        text-align: left;

        .label {
          color: $lable_color;
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
      padding: 0 2px 0 6px;
      width: 130px;
      border-left: 1px solid $border_color;

      .ul-container {
        li {
          text-align: right;

          .time {
            width: 50px;
            text-align: left;
          }
        }
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
        padding: 4px 8px;
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

}
</style>