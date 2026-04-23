<template>
  <section class="pb-fix">
    <div class="container-box">
      <van-loading color="#1194F7" class="loading-box-us" v-if="isLoading" />
      <section class="tab-container">
        <section class="etf-container">
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
          <section class="line-box">
            <div class="line-content">
              <div class="green-line"></div>
              <div class="mind-line"></div>
              <div class="red-line"></div>
            </div>
            <div class="text-info">
              <div class="left-text">
                <p>{{ t('上涨') }}：</p>
                <span>3,339</span>
              </div>
              <div class="right-text">
                <p>{{ t('下跌') }}：</p>
                <span>2,965</span>
              </div>
            </div>
          </section>
          <section class="news-banner-container">
            <van-swipe class="swipe-box" :autoplay="5000" indicator-color="white" v-if="isZh">
              <van-swipe-item>
                <img src="@/assets/image/jpStock/jp1-zh.png" alt="" />
              </van-swipe-item>
              <van-swipe-item>
                <img src="@/assets/image/jpStock/jp2-zh.png" alt="" />
              </van-swipe-item>
              <van-swipe-item>
                <img src="@/assets/image/jpStock/jp3-zh.png" alt="" />
              </van-swipe-item>
            </van-swipe>
            <van-swipe class="swipe-box" :autoplay="5000" indicator-color="white" v-else>
              <van-swipe-item>
                <img src="@/assets/image/jpStock/jp1-en.png" alt="" />
              </van-swipe-item>
              <van-swipe-item>
                <img src="@/assets/image/jpStock/jp2-en.png" alt="" />
              </van-swipe-item>
              <van-swipe-item>
                <img src="@/assets/image/jpStock/jp3-en.png" alt="" />
              </van-swipe-item>
            </van-swipe>
          </section>
          <section class="notice-box">
            <div class="px-12">
              <van-notice-bar class="font-26 textColor" left-icon="" :scrollable="false" background="transparent">
                <div slot="left-icon" class="flex items-center more-img">
                  <img class="w-10 h-10 more-img" src="../../assets/JPHorn.png" alt="" />
                </div>
                <van-swipe vertical class="notice-swipe" :autoplay="2000" :show-indicators="false">
                  <van-swipe-item v-for="item in newsList" :key="item.id" @click="toAnnounceDetail(item.uuid)">{{
                    item.title
                  }}
                  </van-swipe-item>
                </van-swipe>
                <div class="ml-20 flex items-center" slot="right-icon" @click.stop="$router.push('/cryptos/announce')">
                  <img class="w-10 h-10 more-img" src="../../assets/more.png" alt="" />
                </div>
              </van-notice-bar>
            </div>
          </section>
          <div class="divider mt-4"></div>
          <div class="nav">
            <jp-stock-ex-nav :prominentListData="prominentListData" />
          </div>
          <div class="divider"></div>
          <div class="all-etf-ranking">
            <div class="etf-table">
              <ul>
                <li v-for="(item ) in jpStocksListData.slice(0, 5)" :key="item.symbol" @click="itemClick(item)">
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
                      <p :class="item.changeRatio > 0 ? 'text-up' : 'text-up'">
                        <span>{{ item.changeRatio }}%</span>
                      </p>
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
            <div class="more-box" @click="moreOpen(0, t('日股'))">
              <div class="icon-group">
                <p class="">{{ t('更多') }}</p>
                <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow">
              </div>
            </div>
          </div>
        </section>
      </section>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import jpStockExNav from "@/components/trade/jp-stock-ex-nav/index.vue";
import { _getHkStocksItemList } from "@/service/etf.api.js";
import { useI18n } from "vue-i18n";
import { _getNewsList1 } from "@/service/user.api";
import MEcharts from "@/components/ex-echarts/index.vue";
import { fixDate } from "@/utils";
import { _getRealtimeByType, _publicRealtimeTop } from '@/service/quotes.api'


const { t } = useI18n();
const newsList = ref([]);
const jpStocksListData = ref([]);
const indexCardInfo = ref([]);
const prominentListData = ref([]); //知名美股
const typeIndex = ref(0);
const router = useRouter();
const language = JSON.parse(localStorage.getItem("lang"));
const isZh = language == "zh-CN" || language == "CN";
const isLoading = ref(false);

onMounted(async () => {
  getNewsList();
  getRealtimeByType();
  publicRealtimeTop();
  letMeGo()
});


const handleIndexClick = (item, index) => {
  typeIndex.value = index;
  router.push(`/quotes/usStockIndexDetail?symbol=${item.symbol}&symbolType=JP-stocks`);
};

const getNewsList = () => {
  _getNewsList1({
    language: JSON.parse(localStorage.getItem("lang")) || "en",
  }).then((res) => {
    const result = res.filter((item) => item.show);
    newsList.value = result.slice(0, 3);
  });
};


const publicRealtimeTop = () => {
  _publicRealtimeTop({
    type: 'JP-stocks',
  }).then(data => {
    indexCardInfo.value = data || []
  }).catch((e) => {

  })
}



const getRealtimeByType = () => {
  _getRealtimeByType({
    type: "JP-stocks",
    pageNo: 1
  }).then((data) => {
    if (data === null) {
      data = [];
    }
    jpStocksListData.value = data;
    if (data.length === 0) return;
    prominentListData.value = data;
  });
}



const itemClick = (item) => {
  router.push(
    `/quotes/usStockDetail?symbol=${item.symbol}&symbolType=JP-stocks&enName=${item.enName}&tabIndex=${props.tabActive}`
  );
};
//打开更多
const moreOpen = (index, name) => {
  router.push(`/quotes/UsStockMore?index=${index}&typName=${name}&symbolType=JP-stocks&tabIndex=${props.tabActive}`);
};

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

.container-box {
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
        color: #b8bdd1;
      }

      .min {
        position: absolute;
        z-index: 2;
        bottom: 35px;
        right: 10px;
        font-size: 12px;
        color: #b8bdd1;
      }
    }

    .time-x {
      padding: 0 12px 8px;
      font-size: 10px;
      color: #747a8f;
      display: flex;
      justify-content: space-between;
    }

    .indicator-container {
      padding: 0 12px;

      .indicator-item {
        flex: 1;
        max-width: 33.33%;
        padding-left: 6px;
        text-align: left;
        margin: 0 4px;
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
        background: linear-gradient(180deg, $jp-hot-bg-1 0%, $jp-hot-bg-2 100%);
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

    .line-box {
      background: $mainBgColor;
      margin: 20px 0;
      padding: 0 15px;
      height: 56px;
      width: 100%;
      display: flex;
      flex-direction: column;

      .line-content {
        display: flex;
        height: 5px;
        position: relative;

        div {
          width: 188px;
          height: 5px;
        }

        .green-line {
          background: #05CDA5;
          border-radius: 30px 0 0 30px;
          width: calc(50% + 57px);
        }

        .mind-line {
          background: #747A8F;
          transform: skew(-40deg);
          border-left: 5px solid $mainBgColor;
          border-right: 5px solid $mainBgColor;
          width: 57px;
          position: absolute;
          left: 50%;
          top: 0;
        }

        .red-line {
          background: #F33368;
          border-radius: 0 30px 30px 0;
          width: 50%;
        }
      }
    }

    .text-info {
      margin-top: 10px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 14px;

      div {
        display: flex;
        justify-content: flex-start;
        align-items: center;
      }

      .left-text {
        color: #06CDA5
      }

      .right-text {
        color: #F43368
      }
    }

    .hk-title {
      margin: 10px 0;
      padding: 0 12px;
      font-weight: 700;
      font-size: 16px;
      color: $mainTextColor;
    }

    .hot-industry {
      margin-bottom: 10px;

      .indicator-item {
        flex: 1;
        text-align: center;

        .title-box {
          display: inline-flex;
          align-items: center;
        }

        .title {
          position: relative;
          font-size: 12px;
          line-height: 16px;
          color: $lower-text-color;
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
        background: linear-gradient(180deg, #332d2e 0%, #24242e 100%);

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
            color: #e69a38;
          }

          .date {
            font-size: 12px;
            color: #b8bdd1;
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
        color: #b8bdd1;
        background: #1b2134;
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
        color: #bbbcbd;

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
          color: #747a8f;
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
            color: #bcbdc2;
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
        background-color: #06cda5;
        height: 100%;
        border-radius: 4px 0 0 4px;
      }

      .right {
        display: inline-block;
        width: 40%;
        background-color: #f43368;
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
      background-color: #747a8f;
      transform: skew(-20deg);
    }

    .params {
      margin-top: 4px;
      margin-bottom: 10px;
      display: flex;
      justify-content: space-between;
      font-size: 14px;

      .down {
        color: #06cda5;
      }

      .up {
        color: #f43368;
      }
    }
  }
}
</style>
