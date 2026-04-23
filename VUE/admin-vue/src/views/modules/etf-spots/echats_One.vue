<template>
  <div :style="{ height: height, width: width, }" id="main">
    <div></div>
  </div>
</template>

<script>
// 引入基本模板
// let echarts = require("echarts/lib/echarts");
// // 引入柱状图组件
// require("echarts/lib/chart/bar");
// require("echarts/lib/chart/line"); //echarts/components
// // 引入提示框和title组件
// require("echarts/lib/component/tooltip");
// require("echarts/lib/component/title");
// require("echarts/lib/echarts");
// // 不引入这个会报错 xAxis "0" not found
// require("echarts/lib/component/grid");
// // import 'echarts/lib/component/grid';
import * as echarts from "echarts";
import "echarts-gl";
import { hide } from "yargs";
export default {
  props: {
    height: {
      type: String,
      default: "1000px",
      overfloat:hide,
    },
    width: {
      type: String,
      default: "90%",
    },
  },
  data() {
    return {
        timeArr:[],
        linArr:[],
    };
  },
  components: {},
  mounted() {
    this.drawLine();
  },
  methods: {
    init(timeArr,linArr) {
     this.timeArr = timeArr
     this.linArr = linArr
     this.drawLine();
    },
    drawLine() {
      var chartDom = document.getElementById("main");
      var myChart = echarts.init(chartDom);
      var option;

      option = {
  xAxis: {
    data: this.timeArr,
//     axisLabel: {
//     interval: 20
//   }
    //data: ['2017-10-24', '2017-10-25', '2017-10-26', '2017-10-27']
    axisTick:{
        show:false,
    },
    axisLabel:{
        show:false,
        interval:2000,
    },
    },
   
     yAxis: {},
  series: [
    {
      type: 'candlestick',
    //   data: [
    //     [20, 34, 10, 38],
    //     [40, 35, 30, 50],
    //     [31, 38, 33, 44],
    //     [38, 15, 5, 42]
    //   ]
    data:this.linArr
    }
  ]
};
      option && myChart.setOption(option);
    },
  },
};
</script>
<style lang="scss" scoped></style>
