<template>
  <div class="etf-container">
    <div class="kline-chart-container">
      <div class="kline-chart" id="lineChart"></div>
      <p class="max green">+0.39%</p>
      <p class="min red">-1.62%</p>
      <div class="time-x">
        <span>09:30</span>
        <span>16:00</span>
      </div>
    </div>
    <div class="nav">
      <etf-ex-nav :defaultEtfListData="defaultListData" />
    </div>
    <div class="banner-container">
      <van-swipe class="swipe-box" :autoplay="5000" indicator-color="white">
        <van-swipe-item v-for="(item) in newsList" :key="item"
          @click="onRoute(`/cryptos/announceDetail?id=${item.uuid}`)">
          <header class="flex header">
            <span class="title"> {{ item?.title }}</span>
            <span class="date"> {{ item?.createTime }}</span>
          </header>
          <div class="content van-multi-ellipsis--l2" v-html="formatNews(item.content)"></div>
        </van-swipe-item>
      </van-swipe>
    </div>
    <div class="divider"></div>
    <div class="hot-container">
      <div class="header-box">
        <p class="title">{{ t('hotPlate') }}</p>
        <div class="icon-group" @click="moreOpen(0, t('hotPlate'))">
          <p class="text">{{ t('更多') }}</p>
          <img src="@/assets/image/quotes/right-arrow.png" alt="" class="icon arrow">
        </div>
      </div>
      <ul class="hot-box">
        <li class="hot-item" v-for="value in hotSymbol" :key="value" @click="itemClick(value)">
          <p class="name">
            {{ value.name }}
          </p>
          <p class="value"> {{ value.close ?? '--' }}</p>
          <p class="num">
            <span class="num-left" :class="value.chg > 0 ? 'text-up' : 'text-down'">{{ formatNumber(value.chg)
            }}</span>
            <span class="num-right" :class="value.changeRatio > 0 ? 'text-up' : 'text-down'">{{
              value.changeRatio ? `${value.changeRatio}%` : value.changeRatio === 0 ? 0
              :
              '--' }}</span>
          </p>
        </li>
      </ul>
    </div>
    <div class="divider"></div>
    <div class="all-etf-ranking">
      <p class="title">{{ t('AllETFRankings') }}</p>
      <div class="tabs flex">
        <div class="tab-item flex-1" v-for="(item, index) in allEtfTabList" @click="selectAllEtfIndex(index, item.value)"
          :class="[allEtfTabIndex === index ? 'active' : '']" :key="item">
          {{ item.title }}
        </div>
      </div>
      <div class="etf-table">
        <ul>
          <li class="title-line">
            <div class="flex-l">
              <p>{{ t('nameCode') }}</p>
            </div>
            <div class="flex-r">
              <div class="flex-r-item">
                <p>{{ t('uptodate') }}</p>
              </div>
              <div class="flex-r-item">
                <p>{{ t('increase') }}</p>
              </div>
              <div class="flex-r-item">
                <p>{{ t('SparkDiagram') }}</p>
              </div>
              <div class="flex-r-item right">
                <p>{{ t('Amount') }}</p>
              </div>
            </div>
          </li>
          <li v-for="(item, index) in allEtfListData.slice(0, 5)" :key="item.symbol" @click="itemClick(item)">
            <div class="flex-l">
              <p>{{ item.name }}</p>
              <p class="gray-text">
                {{ item.symbol }}
              </p>
            </div>
            <div class="flex-r">
              <div class="flex-r-item">
                <p :class="item.close < 1 ? 'text-up' : 'text-down'">{{
                  item.close
                }}</p>
              </div>
              <div class="flex-r-item">
                <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                  <span>{{ item.changeRatio }}%</span>
                </p>
              </div>
              <div class="flex-r-item">
                <div class="mecharts-box">
                  <m-echarts :dataObj="item" :ratio="item.changeRatio" :index="item.symbol" />
                </div>
              </div>
              <div class="flex-r-item">
                <div class="last-item right">
                  <p>
                    <span>{{ fixDate(item.amount) }}</span>
                  </p>
                </div>
              </div>
            </div>
          </li>
        </ul>
      </div>
      <div class="more-box">
        <div class="icon-group" @click="moreOpen(1, t('AllETFRankings'))">
          <p class="text">{{ t('更多') }}</p>
          <img src="@/assets/image/quotes/right-arrow.png" alt="" class="icon arrow">
        </div>
      </div>
    </div>
    <div class="divider"></div>
    <div class="other-etf-ranking">
      <p class="title">{{ t('行业热力图') }}</p>
      <hot-map></hot-map>
    </div>
    <div class="divider"></div>
    <div class="other-etf-ranking">
      <p class="title">{{ t('OtherETFRankings') }}</p>
      <div class="tabs flex">
        <div class="tab-item flex-1" v-for="(item, index) in otherEtfTabList"
          @click="selectOtherEtfIndex(index, item.value, item.title)"
          :class="[otherEtfTabIndex === index ? 'active' : '']" :key="item">
          {{ item.title }}
        </div>
      </div>
      <div class="etf-table">
        <ul>
          <li class="title-line">
            <div class="flex-l">
              <p>{{ t('nameCode') }}</p>
            </div>
            <div class="flex-r">
              <div class="flex-r-item">
                <p>{{ t('uptodate') }}</p>
              </div>
              <div class="flex-r-item">
                <p>{{ t('increase') }}</p>
              </div>
              <div class="flex-r-item">
                <p>{{ t('SparkDiagram') }}</p>
              </div>
              <div class="flex-r-item right">
                <p>{{ t('Amount') }}</p>
              </div>
            </div>
          </li>
          <li v-for="(item, index) in otherEtfListData" :key="item.symbol" @click="itemClick(item)">
            <div class="flex-l">
              <p>{{ item.name }}</p>
              <p class="gray-text">
                {{ item.symbol }}
              </p>
            </div>
            <div class="flex-r">
              <div class="flex-r-item">
                <p :class="item.close < 1 ? 'text-up' : 'text-down'">{{
                  item.close
                }}</p>
              </div>
              <div class="flex-r-item">
                <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                  <span>{{ item.changeRatio }}%</span>
                </p>
              </div>
              <div class="flex-r-item">
                <m-echarts :dataObj="item" :ratio="item.changeRatio" :index="item.symbol + 1" />
              </div>
              <div class="flex-r-item">
                <div class="last-item right">
                  <p>
                    <span>{{ item.amount }}</span>
                  </p>
                </div>
              </div>
            </div>
          </li>
        </ul>
      </div>
      <div class="more-box">
        <div class="icon-group" @click="moreOpen(2, category, true)">
          <p class="text">{{ t('更多') }}</p>
          <img src="@/assets/image/quotes/right-arrow.png" alt="" class="icon arrow">
        </div>
      </div>
    </div>
    <div class="divider"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue';
import { useRouter } from 'vue-router';
import EtfExNav from "@/components/trade/etf-ex-nav/index.vue"
import HotMap from '../quotes/components/HotMap.vue'
import MEcharts from "@/components/ex-echarts/index.vue";
import linesData from './data.json'
import * as echarts from 'echarts/core';
const props = defineProps({
  index: {
    type: Number,
    default: 0
  },
  tabActive: {
    type: Number,
    default: 0
  }
})


import {
  DatasetComponent,
  GridComponent,
  TransformComponent
} from 'echarts/components';
import { LineChart } from 'echarts/charts';
import { useI18n } from 'vue-i18n'
import { CanvasRenderer } from 'echarts/renderers';
import { formatNews } from '@/utils'
import { _getNewsList1 } from '@/service/user.api'
import { _getRealtimeByType, _publicRealtimeTop } from '@/service/quotes.api'
import { REQUEST_TIMER, TIME_OUT } from "@/config/index.js";

import { fixDate } from "@/utils";

echarts.use([
  DatasetComponent,
  GridComponent,
  TransformComponent,
  LineChart,
  CanvasRenderer,
]);

const { t } = useI18n()
const router = useRouter()


let myChart = null
const typeIndex = ref(0)
const newsList = ref([])
const chartDom = ref(null)
const otherEtfTabIndex = ref(0)
const category = ref('global') // 其他ETF排名 全球ETF
const otherEtfListData = ref([])
const indexCardInfo = ref([])
const allEtfTabIndex = ref(0)
const sortType = ref('changeRatio') // 全部ETF排名 默认涨幅榜
const defaultListData = ref([])
const allEtfListData = ref([])
const interval = ref(null)
const allEtfTabList = ref([
  { title: t('涨幅榜'), value: "changeRatio" },
  { title: t('跌幅榜'), value: "decrease" },
  { title: t('成交额'), value: "amount" },
])

const otherEtfTabList = ref([
  { title: t('GlobalETFs'), value: "global" },
  { title: t('EnergyETFs'), value: "energy" },
  { title: t('GoldEFs'), value: "gold" },
  { title: t('ArtificialIntelligenceETFs'), value: "ai" },
])

const hotSymbol = ref([1, 2, 3, 4, 5, 6])
const language = JSON.parse(localStorage.getItem("lang"));
const categoryTitle = ref(t('GlobalETFs'))

onMounted(async () => {
  getNewsList()
  getRealtimeByType()
  publicRealtimeTop()
  initInterValData()
})

const emit = defineEmits(['changeLetMego'])
const initInterValData = () => {
  console.log('initInterValData etf')

  emit('changeLetMego', () => {
    getRealtimeByType()
    publicRealtimeTop()
  })
  nextTick(() => {
    renderChart(linesData)
  })
}
const getNewsList = () => {
  _getNewsList1({
    language: JSON.parse(localStorage.getItem('lang')) || 'en',
  }).then(res => {
    const result = res.filter(item => item.show)
    newsList.value = result.slice(0, 3)
  }).catch((e) => {
  })
}

const publicRealtimeTop = () => {
  _publicRealtimeTop({
    type: 'indices',
  }).then(data => {
    hotSymbol.value = data || []
  }).catch((e) => {

  })
}

const getRealtimeByType = () => {
  _getRealtimeByType({
    type: 'indices',
    pageNo: 1
  }).then(data => {
    if (data === null) {
      data = []
    }
    defaultListData.value = data
    const copyData = Array.from(data)
    const type = sortType.value;
    let result = []
    if (type === 'decrease') {
      result = copyData.sort((a, b) => a.changeRatio - b.changeRatio)
    } else {
      result = copyData.sort((a, b) => b[type] - a[type])
    }
    allEtfListData.value = result
    otherEtfListData.value = data.filter(item => item.category === category.value) || []
  })
}

const selectAllEtfIndex = (index, value) => {
  allEtfTabIndex.value = index
  sortType.value = value
  let result = []
  if (sortType.value === 'decrease') {
    result = defaultListData.value.sort((a, b) => a.changeRatio - b.changeRatio)
  } else {
    result = defaultListData.value.sort((a, b) => b[sortType.value] - a[sortType.value])
  }
  allEtfListData.value = result
}

const onRoute = (path) => {
  router.push(path)
}

const selectOtherEtfIndex = (index, value, name) => {
  otherEtfTabIndex.value = index
  category.value = value
  categoryTitle.value = name
  otherEtfListData.value = defaultListData.value.filter(item => item.category === category.value)
}

const formatNumber = (num) => {
  if (num === null || num === undefined) return '--'
  if (num === 0) return 0
  if (num > 0) {
    return `+${num}`
  }
  if (num < 0) {
    return num
  }
}

const itemClick = (item) => {
  router.push(`/quotes/detail?symbol=${item.symbol}`)
}

//打开更多
const moreOpen = (index, name, iscategory) => {
  if (iscategory) {
    router.push(`/quotes/hotModules?index=${index}&typName=${categoryTitle.value}&symbolType=indices&category=${category.value}&tabIndex=${props.tabActive}`)
  } else {
    router.push(`/quotes/hotModules?index=${index}&typName=${name}&symbolType=indices&tabIndex=${props.tabActive}`)
  }

}

const renderChart = (_rawData) => {
  chartDom.value = document.getElementById('lineChart')
  if (!chartDom.value) return
  myChart = echarts.init(chartDom.value);
  let option;

  const countries = [
    'France',
    'Russia',
    'United Kingdom'
  ];
  const datasetWithFilters = [];
  const seriesList = [];
  echarts.util.each(countries, function (country) {
    const datasetId = 'dataset_' + country;
    datasetWithFilters.push({
      id: datasetId,
      fromDatasetId: 'dataset_raw',
      transform: {
        type: 'filter',
        config: {
          and: [
            { dimension: 'Year', gte: 1950 },
            { dimension: 'Country', '=': country }
          ]
        }
      }
    });
    seriesList.push({
      type: 'line',
      datasetId: datasetId,
      showSymbol: false,
      name: country,
      endLabel: {
        show: false,
      },
      labelLayout: {
        moveOverlap: 'shiftY'
      },
      emphasis: {
        focus: 'series'
      },
      encode: {
        x: 'Year',
        y: 'Income',
        label: ['Country', 'Income'],
        itemName: 'Year',
      }
    });
  });
  option = {
    color: ['#3A6DC4', '#5F42E7', '#E3702C'],
    animationDuration: 0,
    dataset: [
      {
        id: 'dataset_raw',
        source: _rawData
      },
      ...datasetWithFilters
    ],
    xAxis: {
      type: 'category',
      nameLocation: 'middle',
      show: false,
    },
    yAxis: {
      name: 'Income',
      show: false,
    },
    grid: {
      top: 20,
      bottom: 0,
      left: 0,
      right: 0,
    },
    series: seriesList
  };
  myChart.setOption(option);

  option && myChart.setOption(option);
}

// onBeforeUnmount(() => {
//   if (interval.value) {
//     clearInterval(interval.value)
//   }
// })
onUnmounted(() => {
  myChart && myChart.dispose()
})

// const destroyHandler = () => {
//   if (interval.value) {
//     clearInterval(interval.value)
//   }
//   myChart && myChart.dispose()
// }
defineExpose({
  getTabName: () => '0',
  initInterValData
})
watch(() => props.tabActive, (val) => {
  console.log('watch etf', val)
  if (props.index === val) {
    initInterValData()
  }
})
</script>

<style lang="scss" scoped>
.etf-container {
  padding: 0;

  .kline-chart-container {
    position: relative;

    .kline-chart {
      width: 100%;
      height: 200px;
      border: 1px solid $border_color;
    }

    .max {
      position: absolute;
      top: 5px;
      right: 10px;
      z-index: 2;
      font-size: 12px;
    }

    .min {
      position: absolute;
      z-index: 2;
      bottom: 35px;
      right: 10px;
      font-size: 12px;
    }
  }

  .time-x {
    padding: 0 12px 8px;
    font-size: 10px;
    color: #747A8F;
    display: flex;
    justify-content: space-between;
  }

  .indicator-container {
    padding: 0 12px;

    .active-item {
      border: 1px solid $active_line !important;
      border-radius: 10px;
    }

    .indicator-item {
      border: 1px solid transparent;
      flex: 1;
      max-width: 33.33%;
      // padding-left: 6px;
      text-align: left;
      margin: 0 4px;
      border-radius: 10px;
      // border: 1px solid $active_line !important;
      background: $recommend-bg;
      text-align: center;

      .title-box {
        display: inline-flex;
        align-items: center;
      }

      .title {
        position: relative;
        font-size: 12px;
        line-height: 16px;
        color: $text_color;
        font-weight: bold;
      }

      .point {
        margin-right: 4px;
        width: 6px;
        height: 6px;
        border-radius: 50%;
      }

      .num {
        // margin-left: 10px;
        font-weight: 700;
        font-size: 16px;
        line-height: 24px;
      }

      .value {
        // margin-left: 10px;
        font-size: 12px;
        font-weight: 400;
      }
    }

    .indicator-item:first-child .point {
      background: #3A6DC4;
    }

    .indicator-item:nth-child(2) .point {
      background: #5F42E7;
    }

    .indicator-item:last-child .point {
      background: #E3702C;
    }
  }

  .banner-container {
    margin: 20px 0;
    padding: 0 12px;

    .swipe-box {
      border-radius: 10px;
    }

    .van-swipe-item {
      height: 110px;
      padding: 10px 14px;
      color: $text_color;
      font-size: 14px;
      // background: linear-gradient(180deg, #332D2E 0%, #24242E 100%);
      // background: linear-gradient(180deg, #332D2E 0%, #24242E 100%);
      background: $recommend-bg;


      .header {
        padding: 0;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .title {
          font-size: 14px;
          text-overflow: ellipsis;
          overflow: hidden;
          word-break: break-all;
          white-space: nowrap;
          max-width: 160px;
        }

        .title:first-letter {
          color: #E69A38;
        }

        .date {
          font-size: 12px;
          color: #B8BDD1;
          width: 130px;
          text-align: right;
        }
      }
    }
  }

  .hot-container {
    margin: 20px 0;
    padding: 0 8px;

    .header-box {
      display: flex;

      .title {
        flex: 1;
        font-size: 16px;
        padding: 0 8px;
        font-weight: 700;
      }

      .icon-group {
        display: flex;
        align-items: center;
        font-size: 14px;

        .icon.arrow {
          margin-left: 10px;
          width: 7px;
          height: 9px;
        }
      }

    }

    .hot-box {
      display: grid;
      grid-template-columns: 33.33% 33.33% 33.33%;
      grid-template-rows: repeat(2, 100px);
    }

    .hot-item {
      padding: 6px 2px;
      margin: 4px;
      text-align: center;
      font-size: 12px;
      line-height: 18px;
      color: $text_color;
      background: $recommend-bg;
      border-radius: 4px;

      .value {
        font-weight: 700;
        color: $text_color;
        line-height: 24px;
      }

      .num {
        .num-left {
          margin-right: 6px;
        }
      }
    }
  }

  .all-etf-ranking,
  .other-etf-ranking {
    margin-top: 10px;

    .title {
      font-weight: 700;
      padding: 0 12px;
      font-size: 14px;
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
        padding: 4px 4px;
        font-size: 12px;
        color: $text_color5;
        background: $US_tab_background;
        border-radius: 10px;
        background-size: cover;
      }

      .active {
        font-weight: 400;
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
        align-items: center;
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

            .mecharts-box {
              margin-right: 10px;
            }

            .last-item {
              text-align: right;
              color: $text_color;
              font-weight: 700;
              font-size: 12px;
            }
          }
        }
      }
    }

    .more-box {
      height: 40px;

      .icon-group {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 40px;
        line-height: 40px;
        font-size: 14px;

        .icon.arrow {
          margin-left: 10px;
          width: 7px;
          height: 9px;
        }
      }
    }

  }

}
</style>
