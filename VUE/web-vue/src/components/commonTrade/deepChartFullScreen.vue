<template>
  <div id="myChart"></div>
</template>

<script>
import * as echarts from "echarts";

export default {
  name: "deepChartFullScreen",
  data() {
    return {
      asks: [],

      bids: [],
    };
  },
  mounted() {},
  props: {
    deepBuy: {
      type: Array,
      default() {
        return [];
      },
    },
    deepSell: {
      type: Array,
      default() {
        return [];
      },
    },
    isFullscreen: {
      type: Boolean,
    },
    isSpotGoods: {
      //是否现货
      type: Boolean,
      default: false,
    },
  },
  computed: {},
  watch: {
    isFullscreen(val) {},
    deepBuy(val) {
      if (val.length > 0 && this.deepSell.length > 0) {
        let arry1 = [];
        this.deepBuy.map((item) => {
          let newarry = [];
          newarry[0] = item.price;
          newarry[1] = item.amount;
          arry1.push(newarry);
        });
        let arry2 = [];
        this.deepSell.map((item) => {
          let newarry = [];
          newarry[0] = item.price;
          newarry[1] = item.amount;
          arry2.push(newarry);
        });

        this.depthMe(this.unique(arry1), this.unique(arry2));
      }
    },
  },
  methods: {
    //数字排序
    orderListAsc(arr) {
      var len = arr.length;
      for (var i = 0; i < len - 1; i++) {
        for (var j = i + 1; j < len; j++) {
          // 确保 j 比 i 大 1
          if (arr[i][0] < arr[j][0]) {
            var temp = arr[i][0];
            arr[i][0] = arr[j][0];
            arr[j][0] = temp;
          }
        }
      }
      return arr; // 返回新的数组
    },
    //去重
    unique(arr) {
      let newArr = [arr[0]];
      for (let i = 1; i < arr.length; i++) {
        let repeat = false;
        for (let j = 0; j < newArr.length; j++) {
          if (arr[i] === newArr[j]) {
            repeat = true;
            break;
          } else {
          }
        }
        if (!repeat) {
          newArr.push(arr[i]);
        }
      }
      return newArr;
    },
    depthMe(arr1, arr2) {
      var bids = [];
      var asks = [];

      for (var i = 0; i < arr1.length; i++) {
        bids.push(arr1[i].splice(0, 2).map(Number).reverse()); //取数组内容前两位，再倒序，变为数量排在前面用来作为排序字段。
      }
      for (var i = 0; i < arr2.length; i++) {
        asks.push(arr2[i].splice(0, 2).map(Number).reverse());
      }
      this.K_chart(bids, asks);
    },
    K_chart(bids, asks) {
      var bids = this.orderListAsc(bids); //以数量从小到大排序
      var asks = this.orderListAsc(asks).reverse(); //排序之后再倒过来，变成从大到小
      let arr1 = [];
      let arr2 = [];
      for (var i = 0; i < bids.length; i++) {
        arr1.push(bids[i].splice(0, 2).reverse()); //将价格重新变为内容数组第一位
      }
      for (var i = 0; i < asks.length; i++) {
        arr2.push(asks[i].splice(0, 2).reverse());
      }
      this.drawLine(this.orderListAsc(arr1), this.orderListAsc(arr2));
      //最终得出的arr1和arr2就可以去用来渲染深度图了
    },
    drawLine(arr1, arr2) {
      let _this = this;
      // 基于准备好的dom，初始化echarts实例
      let myChart = echarts.init(document.getElementById("myChart"));
      // 绘制图表
      myChart.setOption({
        animation: false,

        tooltip: {
          trigger: "axis",
          confine: true,
          showContent: false,
          axisPointer: {
            lineStyle: {
              color: "#fff",
            },
          },

          axisPointer: {
            type: "cross",
            lineStyle: {
              color: "#60698D",
              type: "dashed",
            },
            label: {
              show: false, //坐标指示器
              fontSize: 11,
              backgroundColor: "#222E5D",
            },
          },
        },
        textStyle: {
          fontSize: 12,
          color: "#929AA5",
        },
        grid: {
          top: "10px",
          left: "0px",
          right: "50px", //数量会很多的情况
          bottom: "20px",
        },
        xAxis: {
          type: "category",
          boundaryGap: false,
          axisLabel: {
            showMinLabel: false,
            // formatter: function (val) {
            //   return '￥' + val
            // }
          },
          axisLine: {
            // 坐标轴线
            show: true,
          },
          axisTick: {
            length: 6,
            lineStyle: {
              type: "dashed",
              // ...
            },
          },
        },
        yAxis: [
          {
            type: "value",
            position: "right",
            splitNumber: 4,
            // axisTick: {
            //   inside: true,
            // },
            axisTick: {
              length: 6,
              lineStyle: {
                type: "dashed",
                // ...
              },
            },
            axisLabel: {
              inside: true,
              showMinLabel: true,
            },
            splitLine: {
              show: false,
            },
            axisLine: {
              lineStyle: {
                type: "dashed",
                // ...
              },
            },
          },
        ],

        series: [
          {
            name: "buy",
            showSymbol: false,
            symbol: "circle",
            zlevel: -22,
            label: {
              show: true,
              position: "right",
              distance: 10,
              padding: 10,
              fontSize: 12,
              color: "#fff",
              backgroundColor: "rgba(255, 255, 255, .3)",
              formatter: function (params) {
                return [
                  `${_this.$t("message.hangqing.jiage")} ：{a|${
                    params.data[0]
                  }}`,
                  `${_this.$t("message.jiaoyi.shuliang")} ：{a|${
                    params.data[1]
                  }}`,
                  `${_this.$t("message.jiaoyi.chengjiaoe")} ：{a|${(
                    params.data[1] * params.data[0]
                  ).toFixed(5)}}`,
                ].join("\n");
              },
              rich: {
                a: {
                  color: "#fff",
                  fontSize: "12",
                  fontWeight: "bold",
                  lineHeight: "20",
                },
              },
            },
            symbolSize: 11,
            connectNulls: true,
            //step:true,
            emphasis: {},
            lineStyle: {},
            type: "line",
            itemStyle: {
              normal: {
                color: "#01C57B",
                borderColor: "#01C57B",
              },
            },

            areaStyle: {
              color: "green",
              opacity: 0.2,
            },

            data: arr1,
          },
          {
            name: "sell",
            showSymbol: false,
            symbol: "circle",
            label: {
              show: true,
              position: "left",
              distance: 10,
              padding: 10,
              fontSize: 12,
              color: "#fff",
              backgroundColor: "rgba(255, 255, 255, .3)",
              formatter: function (params) {
                return [
                  `${_this.$t("message.hangqing.jiage")} ：{a|${
                    params.data[0]
                  }}`,
                  `${_this.$t("message.jiaoyi.shuliang")} ：{a|${
                    params.data[1]
                  }}`,
                  `${_this.$t("message.jiaoyi.chengjiaoe")} ：{a|${(
                    params.data[1] * params.data[0]
                  ).toFixed(5)}}`,
                ].join("\n");
              },
              rich: {
                a: {
                  color: "#fff",
                  fontSize: "12",
                  fontWeight: "bold",
                  lineHeight: "20",
                },
              },
            },
            symbolSize: 11,
            connectNulls: true,
            //step:true,
            type: "line",
            stack: "1111222",
            itemStyle: {
              normal: {
                color: "#D8195A",
              },
            },
            areaStyle: {
              color: "red",
              opacity: 0.2,
            },

            data: arr2,
          },
        ],
      });
    },
  },
};
</script>
<style scoped>
#myChart {
  position: fixed !important;
  top: 120px;
  left: 0;
  width: 100% !important;
  height: calc(100vh - 120px) !important;
  background: #121212;
  z-index: 30;
}
</style>
