<template>
  <div :id="mainIndex" class="main-box"></div>
</template>

<script setup>
import { ref, onMounted, nextTick } from "vue";
// 引入 echarts 核心模块，核心模块提供了 echarts 使用必须要的接口。
import * as echarts from "echarts/core";
// 引入柱状图图表，图表后缀都为 Chart
import { LineChart } from "echarts/charts";
// 引入提示框，标题，直角坐标系，数据集，内置数据转换器组件，组件后缀都为 Component
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
} from "echarts/components";
// 标签自动布局，全局过渡动画等特性
import { LabelLayout, UniversalTransition } from "echarts/features";
// 引入 Canvas 渲染器，注意引入 CanvasRenderer 或者 SVGRenderer 是必须的一步
import { CanvasRenderer } from "echarts/renderers";

// 注册必须的组件
echarts.use([
  TitleComponent,
  TooltipComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
  LineChart,
  LabelLayout,
  UniversalTransition,
  CanvasRenderer,
]);

const props = defineProps({
  ratio: {
    type: Number,
    default: 0,
  },
  dataObj: {
    type: Object,
    default() {
      return {};
    },
  },
  index: {
    type: [String, Number],
    default: 0,
  },
});
const { ratio, dataObj, index } = props;
const mainIndex = `main${index}`;
onMounted(() => {
  nextTick(() => {
    const width = document.querySelector(".main-box").clientWidth;
    const height = document.querySelector(".main-box").clientHeight;
    const chartDom = document.getElementById(mainIndex);

    const lineChart = echarts.init(chartDom);
    chartDom.style.width = width + "px";
    chartDom.style.height = height + "px";

    function randomData() {
      now = new Date(+now + oneDay);
      value = value + Math.random() * 21 - 10;
      return {
        name: now.toString(),
        value: [
          [now.getFullYear(), now.getMonth() + 1, now.getDate()].join("/"),
          Math.round(value),
        ],
      };
    }

    let data = [];
    let now = new Date(1997, 9, 3);
    let oneDay = 24 * 3600 * 1000;
    let value = Math.random() * 1000;
    for (var i = 0; i < 1000; i++) {
      data.push(randomData());
    }
    let greenColor = [
      {
        offset: 0,
        color: "rgb(17,202,161,0.8)",
      },
      {
        offset: 1,
        color: "rgb(17,202,161,0.1)",
      },
    ];
    let redColor = [
      {
        offset: 0,
        color: "rgb(227,85,97,0.8)",
      },
      {
        offset: 1,
        color: "rgb(227,85,97,0.1)",
      },
    ];
    let option = {
      // tooltip: {
      //   trigger: 'axis',
      //   formatter: function (params) {
      //     params = params[0];
      //     let date = new Date(params.name);
      //     return (
      //       date.getDate() +
      //       '/' +
      //       (date.getMonth() + 1) +
      //       '/' +
      //       date.getFullYear() +
      //       ' : ' +
      //       params.value[1]
      //     );
      //   },
      //   axisPointer: {
      //     animation: false
      //   }
      // },
      xAxis: {
        show: false,
        type: "time",
        splitLine: {
          show: false,
        },
      },
      yAxis: {
        show: false,
        type: "value",
        boundaryGap: [0, "100%"],
        splitLine: {
          show: false,
        },
      },
      series: [
        {
          name: "Fake Data",
          type: "line",
          showSymbol: false,
          data: data,
          itemStyle: {
            color: ratio <= 0 ? "#E35561" : "#11caa1",
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(
              0,
              0,
              0,
              1,
              ratio <= 0 ? redColor : greenColor,
            ),
          },
        },
      ],
    };

    option && lineChart.setOption(option);
  });
});
</script>

<style lang="scss" scoped>
.main-box {
  height: 55px !important;
}
</style>
