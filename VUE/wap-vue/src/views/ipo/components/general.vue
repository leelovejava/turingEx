<template>
  <div class="general">
    <div class="market-chart">
      <div class="chart-title">
        <div>{{ t('已上市IPO表现') }}</div>
      </div>
      <div class="text_color6 px-5">{{ t('涨跌分布') }}</div>
      <div class="bar-chart-wrap">
        <fx-bar-chart-set :option="barOption" />
      </div>
      <div class="chart-bottom">
        <div class="rect">
          <div class="left"></div>
          <div class="right"></div>
          <div class="shape"></div>
        </div>
        <div class="params">
          <span class="down">{{ t('下跌') }}: 24</span>
          <span class="up">{{ t('上涨') }}: 22</span>
        </div>
      </div>
    </div>
    <p class="chart-title">{{ t('次新股指数走势') }}</p>
    <div class="kline-chart-container">
      <fx-line-chart :hiddenBtn="true" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from "vue-i18n";
import fxBarChartSet from "@/components/fx-barChartSet/index.vue"
import fxLineChart from "@/components/fx-LineChart/index.vue"
const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const list = ref([]);
const loading = ref(false);
const finished = ref(false);

const barOption = reactive(
  {
    xData: ['<-50%', '-50~-10%', '-10~0%', '0', '0~10%', '10~50%', '>50%'],
    yData : [
      {
        value: 0,
        itemStyle: {
          color: '#06CDA5'
        }
      },
      {
        value: 12,
        itemStyle: {
          color: '#06CDA5'
        }
      },
      {
        value: 25,
        itemStyle: {
          color: '#06CDA5'
        }
      },
      {
        value: 15,
        itemStyle: {
          color: '#747A8F'
        }
      },
      {
        value: 22,
        itemStyle: {
          color: '#F43368'
        }
      },
      {
        value: 10,
        itemStyle: {
          color: '#F43368'
        }
      },
      {
        value: 5,
        itemStyle: {
          color: '#F43368'
        }
      },
    ],
    dataIndex: 3,
  }
)

onMounted(() => {


})
</script>
<style lang="scss" scoped>

.general{
  padding-bottom: 30px;
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

  .bar-chart-wrap,.kline-chart-container {
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
        width: 50%;
        background-color: #06CDA5;
        height: 100%;
        border-radius: 4px 0 0 4px;
      }

      .right {
        display: inline-block;
        width: 50%;
        background-color: #F43368;
        height: 100%;
        border-radius: 0 4px 4px 0;
      }
    }

    .shape {
      position: absolute;
      left: 150px;
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
</style>