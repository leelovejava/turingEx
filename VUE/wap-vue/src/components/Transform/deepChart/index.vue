<template>
  <div class="flex-1">
    <div id="myChart3" class="kline-BoxMain"></div>
  </div>
</template>

<script setup>
import { ref, onBeforeUnmount, nextTick, watch } from 'vue';
import * as echarts from 'echarts/core';
import { themeStore } from '@/store/theme';
import { useI18n } from 'vue-i18n'
import {
  TooltipComponent,
  GridComponent,
} from 'echarts/components';
import { LineChart } from 'echarts/charts';
import { CanvasRenderer } from 'echarts/renderers';

echarts.use([
  TooltipComponent,
  GridComponent,
  LineChart,
  CanvasRenderer,
]);


const { t } = useI18n()
const thStore = themeStore()
const THEME = thStore.theme
let myChart = null

const props = defineProps({
  deepBuy: {
    type: Array,
    default: []
  },
  deepSell: {
    type: Array,
    default: []
  }
})

const asks = ref([])
const bids = ref([])
const activatedFlag = ref(true)


const depthMe = (arr1, arr2) => {
  var bids = [];
  var asks = [];

  for (var i = 0; i < arr1.length; i++) {
    bids.push(arr1[i].splice(0, 2).map(Number).reverse()); //取数组内容前两位，再倒序，变为数量排在前面用来作为排序字段。
  }
  for (var i = 0; i < arr2.length; i++) {
    asks.push(arr2[i].splice(0, 2).map(Number).reverse());
  }
  K_chart(bids, asks);
}

const K_chart = (bids, asks) => {
  var bids = orderListAsc(bids); //以数量从小到大排序
  var asks = orderListAsc(asks).reverse(); //排序之后再倒过来，变成从大到小
  let arr1 = [];
  let arr2 = [];
  for (var i = 0; i < bids.length; i++) {
    arr1.push(bids[i].splice(0, 2).reverse()); //将价格重新变为内容数组第一位
  }
  for (var i = 0; i < asks.length; i++) {
    arr2.push(asks[i].splice(0, 2).reverse());
  }
  nextTick(() => {
    drawLine(orderListAsc(arr1), orderListAsc(arr2));
  })

  //最终得出的arr1和arr2就可以去用来渲染深度图了
}

const drawLine = (arr1, arr2) => {
  // 基于准备好的dom，初始化echarts实例
  const ele = document.getElementById("myChart3")
  myChart = echarts.init(ele);
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
          // symbol: "arrow",
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
          color: THEME == 'white' ? "#666" : "#fff",
          backgroundColor: THEME == 'white' ? "rgba(0, 0, 0, .3)" : "rgba(255, 255, 255, .3)",
          formatter: function (params) {
            return [
              `${t('价格')} ：{a|${params.data[0]}}`,
              `${t('数量')} ：{a|${params.data[1]}}`,
              `${t('成交额')} ：{a|${(params.data[1] * params.data[0]).toFixed(5)}}`,
            ].join("\n");
          },
          rich: {
            a: {
              color: THEME == 'white' ? "#666" : "#fff",
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
          color: THEME == 'white' ? "#666" : "#fff",
          backgroundColor: THEME == 'white' ? "rgba(0, 0, 0, .3)" : "rgba(255, 255, 255, .3)",
          formatter: function (params) {
            return [
              `${t('价格')} ：{a|${params.data[0]}}`,
              `${t('数量')} ：{a|${params.data[1]}}`,
              `${t('成交额')} ：{a|${(params.data[1] * params.data[0]).toFixed(5)}}`,
            ].join("\n");
          },
          rich: {
            a: {
              color: THEME == 'white' ? "#666" : "#fff",
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
        stack: "",
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
}



onBeforeUnmount(() => {
  if (myChart) {
    myChart.dispose();
  }
})

const orderListAsc = (arr) => {
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
}
//去重
const unique = (arr) => {
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
}

watch(() => props.deepBuy,
  (val) => {
    console.log('11')
    if (val.length > 0 && props.deepSell.length > 0 && activatedFlag.value) {
      let arry1 = [];
      props.deepBuy.map((item) => {
        let newarry = [];
        newarry[0] = item.price;
        newarry[1] = item.amount;
        arry1.push(newarry);
      });
      let arry2 = [];
      props.deepSell.map((item) => {
        let newarry = [];
        newarry[0] = item.price;
        newarry[1] = item.amount;
        arry2.push(newarry);
      });

      depthMe(unique(arry1), unique(arry2));
    }
  },
  { immediate: true }
)

</script>
<style scoped>
.kline-BoxMain {
  width: 110%;
  height: 500px;
}
</style>
