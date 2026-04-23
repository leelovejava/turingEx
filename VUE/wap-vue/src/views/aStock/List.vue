<template>
  <section class="pb-fix">
    <div class="a-stocks-container-box">
      <van-loading color="#1194F7" class="loading-box-us" v-if="isLoading" />
      <section class="tab-container">
        <section class="etf-container">
          <section class="news-banner-container">
            <van-swipe class="swipe-box" :autoplay="5000" indicator-color="white" v-if="!isZh">
              <van-swipe-item>
                <img src="@/assets/image/aStock/banner1.png" alt="">
              </van-swipe-item>
              <van-swipe-item>
                <img src="@/assets/image/aStock/banner2.png" alt="">
              </van-swipe-item>
            </van-swipe>
            <van-swipe class="swipe-box" :autoplay="5000" indicator-color="white" v-else>
              <van-swipe-item>
                <img src="@/assets/image/aStock/banner3.png" alt="">
              </van-swipe-item>
              <van-swipe-item>
                <img src="@/assets/image/aStock/banner4.png" alt="">
              </van-swipe-item>
            </van-swipe>
          </section>
          <section class="notice-box">
            <div class="px-12">
              <van-notice-bar class="font-26 textColor" left-icon="" :scrollable="false" background="transparent">
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
          <div class="indicator-container flex">
            <div :class="{
              'indicator-item': true,
              'up-bg': item.changeRatio > 0,
              'down-bg': item.changeRatio <= 0,
            }" v-for="(item, index) in indexCardInfo" :key="item.symbol" @click="handleIndexClick(item, index)">
              <div class="title-box">
                <div class="point"></div>
                <p class="title">{{ t(item.name) }}</p>
              </div>
              <p class="num" :class="{
                'text-up': item.changeRatio > 0,
                'text-down': item.changeRatio <= 0,
              }">
                {{ item.close ?? "--" }}
              </p>
              <p class="value" :class="{
                'text-up': item.changeRatio > 0,
                'text-down': item.changeRatio <= 0,
              }">
                {{
                  item.changeRatio
                  ? `${item.changeRatio}%`
                  : item.changeRatio === 0
                    ? 0
                    : "--"
                }}
              </p>
              <div class="trend">
                <m-echarts :dataObj="item" :ratio="Number(item.changeRatio)" :index="item.symbol" />
              </div>
            </div>
          </div>
          <div class="divider"></div>
          <div class="market-chart">
            <div class="chart-title flex justify-between">
              <div>
                {{ t('市场涨跌分布') }}
              </div>
            </div>
            <div class="text_color6 px-5 font-12"> {{ t('总计') }}:7888</div>
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
          <div class="nav">
            <a-stock-ex-nav :prominentListData="prominentListData" />
          </div>
          <div class="divider"></div>
          <div class="hot-container">
            <div class="header-box">
              <p class="title">{{ t('hotPlate') }}</p>
              <div class="icon-group" @click="onRoute('/quotes/hotModules?symbolType=A-stocks')">
                <p class="text">{{ t('更多') }}</p>
                <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow" />
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
            <p class="title">{{ t("市场表现") }}</p>
            <div class="tabs flex">
              <div class="tab-item flex-1" v-for="(item, index) in tabList" @click="selectIndex(index, item.value)"
                :class="[tabIndex === index ? 'active' : '']" :key="item">
                {{ item.title }}
              </div>
            </div>
            <div class="etf-table">
              <ul>
                <li class="title-line">
                  <div class="flex-l">
                    <p>{{ t("nameCode") }}</p>
                  </div>
                  <div class="flex-r">
                    <div class="flex-r-item">
                      <p>{{ t("uptodate") }}</p>
                    </div>
                    <div class="flex-r-item">
                      <p>{{ t("increase") }}</p>
                    </div>
                    <div class="flex-r-item">
                      <p>{{ t('SparkDiagram') }}</p>
                    </div>
                    <div class="flex-r-item right">
                      <p>{{ t("Amount") }}</p>
                    </div>
                  </div>
                </li>
                <li v-for="(item, index) in hkStocksListData.slice(0, 5)" :key="item.symbol" @click="itemClick(item)">
                  <div class="flex-l">
                    <p>{{ item.name }}</p>
                    <p class="gray-text">
                      {{ item.symbol }}
                    </p>
                  </div>
                  <div class="flex-r">
                    <div class="flex-r-item">
                      <p :class="item.close < 1 ? 'text-up' : 'text-down'">
                        {{ item.close }}
                      </p>
                    </div>
                    <div class="flex-r-item">
                      <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                        <span>{{ item.changeRatio }}%</span>
                      </p>
                    </div>
                    <div class="flex-r-item m-charts">
                      <div class="mecharts-box">
                        <m-echarts :dataObj="item" :ratio="item.changeRatio" :index="item.symbol + 1" />
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
            <div class="more-box" @click="moreOpen(0, t('A股'))">
              <div class="icon-group">
                <p class="">{{ t('更多') }}</p>
                <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow">
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
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import AStockExNav from "@/components/trade/a-stock-ex-nav/index.vue"
import fxBarChart from "@/components/fx-barChart/index.vue"
import { useI18n } from 'vue-i18n'
import { _getNewsList1 } from '@/service/user.api'
import MEcharts from "@/components/ex-echarts/index.vue";
import { fixDate } from "@/utils";
import { _getRealtimeByType, _publicRealtimeTop } from '@/service/quotes.api'


const { t } = useI18n()
const newsList = ref([])
const hkStocksListData = ref([]);
const indexCardInfo = ref([]);
const prominentListData = ref([]) //
const typeIndex = ref(0);
const tabIndex = ref(0);
const router = useRouter()
const language = JSON.parse(localStorage.getItem('lang'))
const isZh = language == "zh-CN" || language == "CN"
const isLoading = ref(false)
const tabList = ref([
  { title: t("涨幅榜"), value: "changeRatio" },
  { title: t("跌幅榜"), value: "decrease" },
  { title: t("成交量"), value: "volume" },
  { title: t("成交额"), value: "amount" },
]);
const hotSymbol = ref([1, 2, 3, 4, 5, 6])
const sortType = ref("changeRatio");
const defaultListData = ref([])

onMounted(async () => {
  getNewsList()
  publicRealtimeTop();
  getRealtimeByType()
  letMeGo()
})

const handleIndexClick = (item, index) => {
  typeIndex.value = index;
  router.push(`/quotes/usStockIndexDetail?symbol=${item.symbol}&symbolType=A-stocks`);
};

const publicRealtimeTop = () => {
  _publicRealtimeTop({
    type: 'A-stocks',
  }).then(data => {
    indexCardInfo.value = data.slice(0, 6)
    hotSymbol.value = data.slice(6)
  }).catch((e) => {

  })
}

const getNewsList = () => {
  _getNewsList1({
    language: JSON.parse(localStorage.getItem('lang')) || 'en',
  }).then(res => {
    const result = res.filter(item => item.show);
    newsList.value = result.slice(0, 3);
  })
}

const toAnnounceDetail = (announceId) => {
  if (announceId) {
    router.push({ path: '/cryptos/AnnounceDetail', query: { id: announceId } })
  }
}


const getRealtimeByType = () => {
  _getRealtimeByType({
    type: "A-stocks",
    pageNo: 1
  }).then((data) => {
    if (data === null) {
      data = [];
    }
    defaultListData.value = data
    let result = []
    if (sortType.value === 'decrease') {
      result = defaultListData.value.sort((a, b) => a.changeRatio - b.changeRatio)
    } else if (sortType.value === 'amount') {
      result = defaultListData.value.sort((a, b) => b.amount - a.amount)
    } else if (sortType.value === 'changeRatio') {
      result = defaultListData.value.sort((a, b) => b[sortType.value] - a[sortType.value])
    } else if (sortType.value === 'volume') {
      result = defaultListData.value.sort((a, b) => b.volume - a.volume)
    } else {
      result = data
    }
    hkStocksListData.value = result
    if (data.length === 0) return;
    prominentListData.value = data;
  });
};

const selectIndex = (index, value) => {
  tabIndex.value = index;
  sortType.value = value
  let result = []
  if (sortType.value === 'decrease') {
    result = defaultListData.value.sort((a, b) => a.changeRatio - b.changeRatio)
  } else if (sortType.value === 'amount') {
    result = defaultListData.value.sort((a, b) => b.amount - a.amount)
  } else if (sortType.value === 'changeRatio') {
    result = defaultListData.value.sort((a, b) => b[sortType.value] - a[sortType.value])
  } else if (sortType.value === 'changeRatio') {
    result = defaultListData.value.sort((a, b) => b[sortType.value] - a[sortType.value])
  } else {
    result = defaultListData.value
  }
  hkStocksListData.value = result
};

const onRoute = (path) => {
  router.push(path)
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
  router.push(`/quotes/detail?symbol=${item.symbol}&type=A-stocks&symbolType=A-stocks&enName=${item.enName}`)
}
//打开更多
const moreOpen = (index, name) => {
  router.push(`/quotes/UsStockMore?index=${index}&typName=${name}&symbolType=A-stocks&tabIndex=${props.tabActive}`)
}

const emit = defineEmits(['changeLetMego'])
const letMeGo = () => {
  emit('changeLetMego', () => {
    getRealtimeByType()
    publicRealtimeTop()
  })
}

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
watch(() => props.tabActive, (val) => {
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

.a-stocks-container-box {
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
      padding: 20px 12px;
      flex-wrap: wrap;

      .indicator-item {
        flex: 0 0 calc((100% - 24px)/3);
        padding-left: 6px;
        text-align: left;
        margin: 4px;
        border-radius: 10px;
        background: #1a2136;

        .trend {
          height: 65px;
        }

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

      .up-bg {
        background: $up-bg;
      }

      .down-bg {
        background: $down-bg;
      }

      .text-down {
        color: $red !important;
      }

      .text-up {
        color: $green !important;
      }
    }

    .news-banner-container {
      margin: 10px 0;
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
        background: $hot-item-bg;
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

            .m-charts {
              width: 15%;
            }

            .flex-r-item {
              flex: 1;
              align-self: center;

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
</style>
