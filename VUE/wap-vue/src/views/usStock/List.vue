<template>
  <section class="pb-fix">
    <div class="us-stocks-container-box">
      <van-loading color="#1194F7" class="loading-box-us" v-if="isLoading" />
      <section class="tab-container">
        <section class="etf-container">
          <!-- <div class="kline-chart-container">
            <div class="kline-chart" id="usStockChart"></div>
            <p class="max">+0.39%</p>
            <p class="min">-1.62%</p>
            <div class="time-x">
              <span>09:30</span>
              <span>16:00</span>
            </div>
          </div> -->
          <section class="news-banner-container">
            <van-swipe class="swipe-box" :autoplay="5000" indicator-color="white" v-if="!isZh">
              <van-swipe-item>
                <img src="@/assets/image/usStock/banner1.png" alt="">
              </van-swipe-item>
              <van-swipe-item>
                <img src="@/assets/image/usStock/banner2.png" alt="">
              </van-swipe-item>
              <van-swipe-item>
                <img src="@/assets/image/usStock/banner3.png" alt="">
              </van-swipe-item>
            </van-swipe>
            <van-swipe class="swipe-box" :autoplay="5000" indicator-color="white" v-else>
              <van-swipe-item>
                <img src="@/assets/image/usStock/banner4.png" alt="">
              </van-swipe-item>
              <van-swipe-item>
                <img src="@/assets/image/usStock/banner5.png" alt="">
              </van-swipe-item>
              <van-swipe-item>
                <img src="@/assets/image/usStock/banner6.png" alt="">
              </van-swipe-item>
            </van-swipe>
          </section>
          <div class="indicator-container flex">
            <div :class="{ 'indicator-item': true, 'active-item': index === typeIndex }"
              v-for="(item, index) in indexCardInfo" :key="item.symbol" @click="handleIndexClick(item, index)">
              <div class="title-box">
                <div class="point"></div>
                <p class="title">{{ t(item.name) }}</p>
              </div>
              <p class="num">{{ item.close ?? '--' }}</p>
              <p class="value"  :class="{
                'text-up': item.changeRatio > 0,
                'text-down': item.changeRatio <= 0,
              }">{{ item.changeRatio ? `${item.changeRatio}%` : item.changeRatio === 0 ? 0
                :
                '--' }}</p>
            </div>
          </div>
          <div class="divider mt-10"></div>
          <div class="nav">
            <us-stock-ex-nav :initObjData="initObjData" :stockActive="props.tabActive" />
          </div>
          <div class="divider"></div>
          <section class="notice-box">
            <div class="px-12">
              <van-notice-bar class="text-26 textColor" left-icon="" :scrollable="false" background="transparent">
                <div slot="left-icon" class="flex items-center more-img">
                  <img class="w-10 h-10 more-img" src="../../assets/Horn.png" alt="" />
                </div>
                <van-swipe vertical class="notice-swipe" :autoplay="2000" :show-indicators="false">
                  <van-swipe-item v-for="item in newsList" :key="item.id" @click="toAnnounceDetail(item.uuid)">{{
                    item.title }}
                  </van-swipe-item>
                </van-swipe>
                <div class="ml-20 flex items-center" slot="right-icon" @click.stop="$router.push('/cryptos/announce')">
                  <img class="w-10 h-10 more-img" src="../../assets/more.png" alt="" />
                </div>
              </van-notice-bar>
            </div>
          </section>

          <div class="divider"></div>
          <div class="market-chart">
            <div class="chart-title flex justify-between">
              <div>
                {{ t('市场涨跌分布') }}
              </div>
              <div>
                <img class="top-img" src="@/assets/image/chart/top.png" alt="">
              </div>
            </div>
            <div class="text_color6 px-5"> {{ t('总计') }}:7888</div>
            <div class="bar-chart-wrap">
              <fx-bar-chart></fx-bar-chart>
            </div>
            <div class="chart-bottom">
              <div class="rect">
                <div class="left"></div>
                <div class="right"></div>
                <div class="shape"></div>
              </div>
              <div class="params">
                <span class="down">{{ t('下跌') }}: 3339</span>
                <span class="up">{{ t('上涨') }}: 2965</span>
              </div>
            </div>
          </div>
          <div class="divider"></div>
          <p class="chart-title">{{ t('Netfunds') }}</p>
          <div class="kline-chart-container">
            <div class="kline-chart" id="fundChart"></div>
            <p class="max">15.77{{ t('亿') }}({{ t('Dollar') }})</p>
            <p class="min">-18.62({{ t('Dollar') }})</p>
            <div class="time-x">
              <span>09:30</span>
              <span>16:00</span>
            </div>
          </div>
          <div class="divider"></div>
          <div class="all-etf-ranking">
            <p class="title">{{ t('WellUSstocks') }}</p>
            <div class="etf-table">
              <ul>
                <li class="title-line">
                  <div class="flex-l">
                    <p>{{ t('nameCode') }}</p>
                  </div>
                  <div class="flex-r">
                    <div class="flex-r-item">
                      <p>{{ t('Premarketfall') }}</p>
                    </div>
                    <div class="flex-r-item">
                      <p>{{ t('premarketPrice') }}</p>
                    </div>
                  </div>
                </li>
                <li v-for="(item, index) in prominentListData.slice(0, 5)" :key="item.symbol" @click="itemClick(item)">
                  <div class="flex-l">
                    <p>{{ item.name }}</p>
                    <p class="gray-text">
                      {{ item.symbol }}
                    </p>
                  </div>
                  <div class="flex-r">
                    <div class="flex-r-item">
                      <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                        <span>{{ item.changeRatio }}%</span>
                      </p>
                    </div>
                    <div class="flex-r-item">
                      <p>{{ item.close }}</p>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
            <div class="more-box" @click="moreOpen(0, t('WellUSstocks'), 'prominent')">
              <div class="icon-group">
                <p class="">{{ t('更多') }}</p>
                <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow">
              </div>
            </div>
          </div>

          <div class="divider"></div>
          <div class="other-etf-ranking">
            <p class="title">{{ t('热力图') }}</p>
            <hot-map></hot-map>
          </div>

          <div class="technology" v-if="technologyListData && technologyListData.length > 0">
            <div class="divider"></div>
            <div class="other-etf-ranking">
              <p class="title">{{ t('Technology') }}</p>
              <div class="etf-table">
                <ul>
                  <li class="title-line">
                    <div class="flex-l">
                      <p>{{ t('nameCode') }}</p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p>{{ t('Premarketfall') }}</p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ t('premarketPrice') }}</p>
                      </div>
                    </div>
                  </li>

                  <li v-for="(item, index) in technologyListData.slice(0, 5)" :key="item.symbol" @click="itemClick(item)">
                    <div class="flex-l">
                      <p>{{ item.name }}</p>
                      <p class="gray-text">
                        {{ item.symbol }}
                      </p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                          <span>{{ item.changeRatio }}%</span>
                        </p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ item.close }}</p>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
              <div class="more-box" @click="moreOpen(1, t('Technology'), 'technology')">
                <div class="icon-group">
                  <p class="">{{ t('更多') }}</p>
                  <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow">
                </div>
              </div>
            </div>
          </div>

          <div class="financial" v-if="dinancialListData && dinancialListData.length > 0">
            <div class="divider"></div>
            <div class="other-etf-ranking">
              <p class="title">{{ t('Financial') }}</p>
              <div class="etf-table">
                <ul>
                  <li class="title-line">
                    <div class="flex-l">
                      <p>{{ t('nameCode') }}</p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p>{{ t('Premarketfall') }}</p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ t('premarketPrice') }}</p>
                      </div>
                    </div>
                  </li>
                  <li v-for="(item, index) in dinancialListData.slice(0, 5)" :key="item.symbol" @click="itemClick(item)">
                    <div class="flex-l">
                      <p>{{ item.name }}</p>
                      <p class="gray-text">
                        {{ item.symbol }}
                      </p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                          <span>{{ item.changeRatio }}%</span>
                        </p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ item.close }}</p>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
              <div class="more-box" @click="moreOpen(2, t('Financial'), 'dinancial')">
                <div class="icon-group">
                  <p class="">{{ t('更多') }}</p>
                  <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow">
                </div>
              </div>
            </div>
          </div>

          <div class="medicineAndfood" v-if="healthcareListData && healthcareListData.length > 0">
            <div class="divider"></div>
            <div class="other-etf-ranking">
              <p class="title">{{ t('MedicineAndfood') }}</p>
              <div class="etf-table">
                <ul>
                  <li class="title-line">
                    <div class="flex-l">
                      <p>{{ t('nameCode') }}</p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p>{{ t('Premarketfall') }}</p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ t('premarketPrice') }}</p>
                      </div>
                    </div>
                  </li>
                  <li v-for="(item, index) in healthcareListData.slice(0, 5)" :key="item.symbol" @click="itemClick(item)">
                    <div class="flex-l">
                      <p>{{ item.name }}</p>
                      <p class="gray-text">
                        {{ item.symbol }}
                      </p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                          <span>{{ item.changeRatio }}%</span>
                        </p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ item.close }}</p>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
              <div class="more-box" @click="moreOpen(3, t('MedicineAndfood'), 'healthcare')">
                <div class="icon-group">
                  <p class="">{{ t('更多') }}</p>
                  <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow">
                </div>
              </div>
            </div>
          </div>

          <div class="autoEnergy" v-if="energyListData && energyListData.length > 0">
            <div class="divider"></div>
            <div class="other-etf-ranking">
              <p class="title">{{ t('AutoEnergy') }}</p>
              <div class="etf-table">
                <ul>
                  <li class="title-line">
                    <div class="flex-l">
                      <p>{{ t('nameCode') }}</p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p>{{ t('Premarketfall') }}</p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ t('premarketPrice') }}</p>
                      </div>
                    </div>
                  </li>
                  <li v-for="(item, index) in energyListData.slice(0, 5)" :key="item.symbol" @click="itemClick(item)">
                    <div class="flex-l">
                      <p>{{ item.name }}</p>
                      <p class="gray-text">
                        {{ item.symbol }}
                      </p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                          <span>{{ item.changeRatio }}%</span>
                        </p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ item.close }}</p>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
              <div class="more-box" @click="moreOpen(4, t('AutoEnergy'), 'energy')">
                <div class="icon-group">
                  <p class="">{{ t('更多') }}</p>
                  <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow">
                </div>
              </div>
            </div>
          </div>

          <div class="manufacturingRetaiSales" v-if="manufacturingListData && manufacturingListData.length > 0">
            <div class="divider"></div>
            <div class="other-etf-ranking">
              <p class="title">{{ t('ManufacturingRetaiSales') }}</p>
              <div class="etf-table">
                <ul>
                  <li class="title-line">
                    <div class="flex-l">
                      <p>{{ t('nameCode') }}</p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p>{{ t('Premarketfall') }}</p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ t('premarketPrice') }}</p>
                      </div>
                    </div>
                  </li>
                  <li v-for="(item, index) in manufacturingListData.slice(0, 5)" :key="item.symbol"
                    @click="itemClick(item)">
                    <div class="flex-l">
                      <p>{{ item.name }}</p>
                      <p class="gray-text">
                        {{ item.symbol }}
                      </p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                          <span>{{ item.changeRatio }}%</span>
                        </p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ item.close }}</p>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
              <div class="more-box" @click="moreOpen(5, t('ManufacturingRetaiSales'), 'manufacturing')">
                <div class="icon-group">
                  <p class="">{{ t('更多') }}</p>
                  <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow">
                </div>
              </div>
            </div>
          </div>

          <div class="chineseStocks" v-if="chineseListData && chineseListData.length > 0">
            <div class="divider"></div>
            <div class="other-etf-ranking">
              <p class="title">{{ t('ChineseStocks') }}</p>
              <div class="etf-table">
                <ul>
                  <li class="title-line">
                    <div class="flex-l">
                      <p>{{ t('nameCode') }}</p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p>{{ t('Premarketfall') }}</p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ t('premarketPrice') }}</p>
                      </div>
                    </div>
                  </li>
                  <li v-for="(item, index) in chineseListData.slice(0, 5)" :key="item.symbol" @click="itemClick(item)">
                    <div class="flex-l">
                      <p>{{ item.name }}</p>
                      <p class="gray-text">
                        {{ item.symbol }}
                      </p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                          <span>{{ item.changeRatio }}%</span>
                        </p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ item.close }}</p>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
              <div class="more-box" @click="moreOpen(6, t('ChineseStocks'), 'chinese')">
                <div class="icon-group">
                  <p class="">{{ t('更多') }}</p>
                  <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow">
                </div>
              </div>
            </div>
          </div>
          <div class="divider"></div>
        </section>
      </section>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, nextTick, watch, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import UsStockExNav from "@/components/trade/us-stock-ex-nav/index.vue"
import fxBarChart from "@/components/fx-barChart/index.vue"
import { _getQuotes } from '@/service/quotes.api'
import { useI18n } from 'vue-i18n'
import * as echarts from 'echarts/core';
import {
  DatasetComponent,
  GridComponent,
  TransformComponent
} from 'echarts/components';
import { LineChart } from 'echarts/charts';
import { CanvasRenderer } from 'echarts/renderers';
import linesData from './data.json'
import { _getNewsList1 } from '@/service/user.api'
import HotMap from './components/HotMap.vue'
import { _getRealtimeByType, _publicRealtimeTop } from '@/service/quotes.api'

echarts.use([
  DatasetComponent,
  GridComponent,
  TransformComponent,
  LineChart,
  CanvasRenderer,
]);

const { t } = useI18n()
const newsList = ref([])
const indexCardInfo = ref([])
const prominentListData = ref([]) //知名美股
const technologyListData = ref([]) // 科技类
const dinancialListData = ref([]) // 金融类
const healthcareListData = ref([]) // 医疗类
const energyListData = ref([]) // 能源类
const manufacturingListData = ref([]) // 制造业
const chineseListData = ref([]) // 中概股
const typeIndex = ref(0)
const router = useRouter()
const chartDom = ref(null)
let fundChart = null
const language = JSON.parse(localStorage.getItem('lang'))
const isZh = language == "zh-CN" || language == "CN"
const isLoading = ref(false)
const initObjData = ref({})
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

onMounted(async () => {
  getNewsList()
  getRealtimeByType()
  publicRealtimeTop()
  letMeGo()
  nextTick(() => {
    renderFundChart(linesData)
  })
})

const emit = defineEmits(['changeLetMego'])
const letMeGo = () => {
  emit('changeLetMego', () => {
    getRealtimeByType()
    publicRealtimeTop()
  })
}
onBeforeUnmount(() => {
  fundChart && fundChart.dispose()
})

const renderFundChart = (_rawData) => {
  chartDom.value = document.getElementById('fundChart')
  if (!chartDom.value) return
  fundChart = echarts.init(chartDom.value);
  let option;

  const countries = [
    'Poland',
    'Germany'
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
    color: ['#EEA642', '#46A1FB'],
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
  fundChart.setOption(option);
  option && fundChart.setOption(option);
}

const handleIndexClick = (item, index) => {
  typeIndex.value = index
  router.push(`/quotes/usStockIndexDetail?symbol=${item.symbol}`)
}

const getNewsList = () => {
  _getNewsList1({
    language: JSON.parse(localStorage.getItem('lang')) || 'en',
  }).then(res => {
    const result = res.filter(item => item.show);
    newsList.value = result.slice(0, 3);
  })
}

const publicRealtimeTop = () => {
  _publicRealtimeTop({
    type: 'US-stocks',
  }).then(data => {
    indexCardInfo.value = data.slice(0, 3) || []
  }).catch((e) => {

  })
}

const getRealtimeByType = () => {
  _getRealtimeByType({
    type: 'US-stocks',
    pageNo: 1
  }).then(data => {
    if (data === null) {
      data = []
    }
    prominentListData.value = data.filter(item => item.category.indexOf('prominent') > -1) || []
    technologyListData.value = data.filter(item => item.category.indexOf('technology') > -1) || []
    dinancialListData.value = data.filter(item => item.category.indexOf('dinancial') > -1) || []
    healthcareListData.value = data.filter(item => item.category.indexOf('healthcare') > -1) || []
    energyListData.value = data.filter(item => item.category.indexOf('energy') > -1) || []
    manufacturingListData.value = data.filter(item => item.category.indexOf('manufacturing') > -1) || []
    chineseListData.value = data.filter(item => item.category.indexOf('chinese') > -1) || []
    initObjData.value = data[0]
  })
}

const onRoute = (path) => {
  router.push(path)
}

const itemClick = (item) => {
  router.push(`/quotes/usStockDetail?symbol=${item.symbol}&symbolType=US-stocks&enName=${item.enName}&tabIndex=${props.tabActive}`)
}
//打开更多
const moreOpen = (index, name, category) => {
  router.push(`/quotes/UsStockMore?typName=${name}&symbolType=US-stocks&category=${category}&tabIndex=${props.tabActive}`)
}

watch(() => props.tabActive, (val) => {
  console.log('watch etf', val)
  if (props.index === val) {
    letMeGo()
  }
})
</script>
<style lang="scss" scoped>
.loading-box-us {
  position: absolute;
  top: 8% !important;
  left: 50%;
  transform: translate(-50%, -50%);
}

:deep(.van-tabs__nav) {
  background: $selectSymbol_background;
}

:deep(.van-tab) {
  font-size: 14px;
  color: $text_color;
  font-weight: 400;
}

:deep(.van-tab.van-tab--active) {
  font-weight: 700;
}

.us-stocks-container-box {
  position: relative;

  .green {
    color: $green;
  }

  .red {
    color: $red;
  }

  .header {
    display: flex;
    height: 28px;
    padding: 0 12px;

    .flex-l {
      flex: 1;
      display: inline-flex;

      .icon {
        display: inline-block;
        width: 24px;
        height: 28px;
        padding: 6px 4px;

        img {
          height: 16px;
          width: 12px;
        }
      }
    }

    .title {
      font-weight: 700;
      font-size: 20px;
      line-height: 28px;
      color: $mainTextColor;
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

  .tab-container {
    margin-top: 18px;
  }

  .chart-title {
    padding: 12px;
    font-weight: 700;
    font-size: 16px;
  }

  .etf-container {
    padding: 0;

    .kline-chart-container {
      position: relative;

      .kline-chart {
        width: 100%;
        height: 200px;
        border-bottom: 1px dashed $border_color;
      }

      .max {
        position: absolute;
        top: 5px;
        right: 10px;
        z-index: 2;
        font-size: 12px;
        color: #B8BDD1;
      }

      .min {
        position: absolute;
        z-index: 2;
        bottom: 35px;
        right: 10px;
        font-size: 12px;
        color: #B8BDD1;
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
        padding-left: 6px;
        text-align: left;
        margin: 0 4px;
        border-radius: 10px;
        border: 1px solid $active_line !important;
        background: $recommend-bg;

        .title-box {
          display: inline-flex;
          align-items: center;
        }

        .title {
          position: relative;
          font-size: 12px;
          line-height: 16px;
          color: $text_color;
        }

        .point {
          margin-right: 4px;
          width: 6px;
          height: 6px;
          border-radius: 50%;
        }

        .num {
          margin-left: 10px;
          font-weight: 700;
          font-size: 16px;
          line-height: 24px;
          color: $text_color;
        }

        .value {
          margin-left: 10px;
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

    .news-banner-container {
      margin: 20px 0;
      height: 160px;
      padding: 0 12px;

      .swipe-box {
        border-radius: 8px;
      }

      .van-swipe-item {
        color: $text_color;
        font-size: 20px;
        line-height: 160px;
        text-align: center;
        background-color: $selectSymbol_background;

        img {
          display: block;
          height: 160px;
          width: 100%;
          object-fit: cover;
        }
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
        background: linear-gradient(180deg, $news-bg-1 0%, $news-bg-2 100%);

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
            width: 15px;
            height: 15px;
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
        color: #B8BDD1;
        background: #1B2134;
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
        font-size: 16px;
        font-weight: 700;
        padding: 0 12px;
      }

      .tabs {
        padding: 0 12px;
        margin-top: 10px;
        height: 40px;
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
          justify-content: space-between;
          align-items: center;
          font-size: 12px;
          line-height: 18px;
          border-bottom: 1px solid $border_color;

          .gray-text {
            color: #BCBDC2;
            font-size: 12px;
          }

          .flex-l {
            width: 40%;
          }

          .flex-r {
            display: inline-flex;
            flex: 1;

            .flex-r-item {
              flex: 1;
              align-self: center;
              text-align: center;

              &:last-child {
                text-align: right;
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
          color: $text_color6;

          .icon.arrow {
            margin-left: 10px;
            width: 15px;
            height: 15px;
            color: $text_color6;
          }
        }
      }

    }

  }

  .bar-chart-wrap {
    overflow: hidden;
    display: flex;
    justify-content: center;
  }

  .top-img {
    width: 20px;
    height: 20px;
  }

  .chart-bottom {
    padding: 0 10px;

    .rect {
      position: relative;
      display: flex;
      height: 8px;
      width: 100%;

      .left {
        display: inline-block;
        width: 60%;
        background-color: #06CDA5;
        height: 100%;
        border-radius: 4px 0 0 4px;
      }

      .right {
        display: inline-block;
        width: 40%;
        background-color: #F43368;
        height: 100%;
        border-radius: 0 4px 4px 0;
      }
    }

    .shape {
      position: absolute;
      left: 180px;
      top: 0;
      width: 60px;
      height: 100%;
      background-color: #747A8F;
      transform: skew(-20deg);
    }

    .params {
      margin-top: 4px;
      margin-bottom: 10px;
      display: flex;
      justify-content: space-between;
      font-size: 14px;

      .down {
        color: #06CDA5;
      }

      .up {
        color: #F43368;
      }
    }
  }
}

.notice-box {
  :deep(.van-notice-bar__content) {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .van-notice-bar {
    padding: 0;
    height: 30px;
  }

  .notice-swipe {
    flex: 1;
    margin-left: 10px;
    height: 30px;
    line-height: 30px;
    font-size: 13px;
  }

  .ml-20 {
    margin-left: 20px;
  }
}
</style>
