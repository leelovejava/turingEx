<template>
  <div class="kline-page">
    <div
      id="kline-BoxMain"
      :class="[
        isFullscreen ? 'kline-BoxMain-full' : '',
        isSpotGoods ? 'kline-BoxMain' : 'kline-BoxMain1',
      ]"
    ></div>
    <secondary-line
      v-if="!isFullscreen"
      @checkLineType="checkLineType"
    ></secondary-line>
  </div>
</template>
<script>
import { init, dispose } from "klinecharts";
import bus from "vue3-eventbus";
import secondaryLine from "./secondaryLine.vue";
import { useLanguageStore } from "@/store/lang";

const langStore = useLanguageStore();
// candle 最高最低价

export default {
  name: "klineBox",
  props: {
    data: {
      type: Array,
      default() {
        return [
          {
            close: 4976.16,
            high: 4977.99,
            low: 4970.12,
            open: 4972.89,
            timestamp: 1587660000000,
            volume: 204,
          },
          {
            close: 4977.33,
            high: 4979.94,
            low: 4971.34,
            open: 4973.2,
            timestamp: 1587660060000,
            volume: 194,
          },
          {
            close: 4977.93,
            high: 4977.93,
            low: 4974.2,
            open: 4976.53,
            timestamp: 1587660120000,
            volume: 197,
          },
          {
            close: 4966.77,
            high: 4968.53,
            low: 4962.2,
            open: 4963.88,
            timestamp: 1587660180000,
            volume: 28,
          },
          {
            close: 4961.56,
            high: 4972.61,
            low: 4961.28,
            open: 4961.28,
            timestamp: 1587660240000,
            volume: 184,
          },
          {
            close: 4964.19,
            high: 4964.74,
            low: 4961.42,
            open: 4961.64,
            timestamp: 1587660300000,
            volume: 191,
          },
          {
            close: 4968.93,
            high: 4972.7,
            low: 4964.55,
            open: 4966.96,
            timestamp: 1587660360000,
            volume: 105,
          },
          {
            close: 4979.31,
            high: 4979.61,
            low: 4973.99,
            open: 4977.06,
            timestamp: 1587660420000,
            volume: 35,
          },
          {
            close: 4977.02,
            high: 4981.66,
            low: 4975.14,
            open: 4981.66,
            timestamp: 1587660480000,
            volume: 135,
          },
          {
            close: 4985.09,
            high: 4988.62,
            low: 4980.3,
            open: 4986.72,
            timestamp: 1587660540000,
            volume: 76,
          },
        ];
      },
    },
    klineIndex: {
      type: String,
    },
    isFullscreen: {
      //是否全屏
      type: Boolean,
    },
    isSpotGoods: {
      //是否现货
      type: Boolean,
      default: false,
    },
    width: {
      type: Number,
      default: 0,
    },
    kTimeIndex: {
      type: String,
      default: "15min",
    },
  },
  data() {
    return {
      klineData: null,
      paneId: undefined,
      mssg: "",
      id: "",
      lineValue: "VOL",
      loadingInstance: null,
      isZoomed: false, //是否缩放过
      isInit: true, //初始化
      isScrollByPre: false, //是否向前滚动
      lastIndex: 0,
    };
  },
  components: { secondaryLine },
  mounted() {
    bus.on("aMsg", (msg) => {
      this.mssg = msg; //{0: 'MA'} 默认是5 10 30 60
      for (var v of this.mssg) {
        //https://klinecharts.com/guide/instance-api.html#createindicator-value-isstack-paneoptions-callback
        this.id = this.klineData?.createIndicator(v, false, {
          id: "candle_panes",
          height: 140,
          dragEnabled: true,
        });
      }
    });
    bus.on("removeKline", (msg) => {
      if (msg == "remove") {
        this.klineData?.removeIndicator("candle_panes");
      }
    });
  },
  unmounted: function () {
    dispose("kline-BoxMain");
  },
  watch: {
    kTimeIndex(val) {
      this.isZoomed = false;
      this.isScrollByPre = false;
    },
    data(val) {
      this.klineInIt();
    },
    klineIndex(val) {
      if (val) {
        this.klineData.setStyles({
          candle: {
            type: "area",
            lineColor: "#FFFFFF",
          },
        });
      } else {
        this.klineData.setStyles({
          candle: {
            type: "candle_solid",
          },
        });
      }
    },
  },
  methods: {
    klineInIt() {
      this.klineData = init("kline-BoxMain", {
        locale: langStore.language, //https://klinecharts.com/guide/i18n.html
        styles: {
          grid: {
            show: false,
            // 网格水平线
            horizontal: {
              show: false,
              size: 1,
              color: "#393939",
              style: "solid",
              dashValue: [2, 2],
            },
            // 网格垂直线
            vertical: {
              show: false,
              size: 1,
              color: "#000",
              style: "solid",
              dashValue: [2, 2],
            },
          },
          candle: {
            priceMark: {
              high: {
                color: "#FFFFFF",
              },
              low: {
                color: "#FFFFFF",
              },
            },
            // 蜡烛图上下间距，大于1为绝对值，大于0小余1则为比例
            margin: {
              top: 0.2,
              bottom: 0.1,
            },
            // 蜡烛图类型 'candle_solid'|'candle_stroke'|'candle_up_stroke'|'candle_down_stroke'|'ohlc'|'area'
            type: "candle_solid",
            // 蜡烛柱
            bar: {
              upColor: "#05C48E",
              downColor: "#DF473D",
              noChangeColor: "#888888",
            },
            area: {
              lineColor: "#FFFFFF",
              style: "fill",
            },
          },
          yAxis: {
            show: true,
            axisLine: {
              show: true,
              color: "#24272c",
              size: 1,
            },
          },
          xAxis: {
            show: true,
            axisLine: {
              show: true,
              color: "#24272c",
              size: 1,
            },
          },
          separator: {
            size: 1,
            color: "#24272c",
            fill: true,
          },
          technicalIndicator: {
            line: {
              size: 1,
              colors: ["#B3994A", "#C9C3C8", "#A98946", "#7D4F80"],
            },
          },
        },
      });

      // 问题：当滚动到之前的数据，但由于数据更新后，K线都会回到最新值。
      // 解决：初始时获取lastindex，当滚动的时候，判断当前渲染的数据index<lastindex,表示向前滚动了
      this.klineData.subscribeAction("onScroll", () => {
        const { from, to } = this.klineData.getVisibleRange();
        this.isScrollByPre = to < this.lastIndex;
      }); //监听滚动
      if (this.isScrollByPre) {
        return;
      }
      // 初始时获取lastindex
      const { from, to } = this.klineData.getVisibleRange();
      this.lastIndex = to;
      this.klineData.createIndicator("MA", false, {
        id: "candle_pane",
      });

      this.paneId = this.klineData.createIndicator(this.lineValue, false, {
        id: "pane_1",
        height: 100,
        dragEnabled: true,
      });
      //  Y轴价格展示小数位
      const val = this.data[0]?.close;
      const precision = String(val).split(".")[1]?.length;
      this.klineData.setPriceVolumePrecision(precision, 2); //pricePrecision, volumePrecision
      this.klineData.setOffsetRightDistance(2);
      this.klineData.subscribeAction("onZoom", () => {
        this.isZoomed = true;
      });

      // 如果没有缩放过，则bar的宽度进行自适应
      if (!this.isZoomed) {
        const { width } = this.klineData.getSize("candle_pane", "main");
        const len = this.data.length;
        const barWidth = Math.max(Math.round(width / len), 5); // 每个蜡烛的宽度 = 整个宽度 / 数据的个数 ，当数据过多时候，设置一个最小的宽度即可
        this.klineData.setBarSpace(barWidth); //setBarSpace会影响缩放
      }
      this.klineData.applyNewData(this.data); //设置数据
    },
    //切换副线
    checkLineType(val) {
      this.lineValue = val;
      this.klineData?.createIndicator(val, false, { id: "pane_1" });
    },
  },
};
</script>
<style scoped>
.kline-BoxMain {
  /* width: 875px;  */
  width: 100%;
  height: 500px;
  background: #171a1e;
}

.kline-BoxMain1 {
  width: 100%;
  height: 620px;
  background: #171a1e;
}

.kline-BoxMain-full {
  position: fixed !important;
  /* top: 110px; 永续 */
  top: 120px;
  left: 0;
  width: 100%;
  height: calc(100vh - 120px) !important;
  background: #171a1e;
  z-index: 30;
}

.kline-page {
  background: #171a1e;
  padding-bottom: 7px;
}
</style>
