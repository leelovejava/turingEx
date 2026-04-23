<template>
  <div :style="{ height: height, width: width }" id="mainThree">
    <div></div>
  </div>
</template>

<script>
import * as echarts from "echarts/core";
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
} from "echarts/components";
import { PieChart } from "echarts/charts";
import { LabelLayout } from "echarts/features";
import { CanvasRenderer } from "echarts/renderers";
echarts.use([
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  PieChart,
  CanvasRenderer,
  LabelLayout,
]);
// Vue.prototype.$echarts = echarts;
export default {
  props: {
    height: {
      type: String,
      default: "100%",
    },
    width: {
      type: String,
      default: "100%",
    },
  },
  name: "sevSystem",
  data() {
    return {
    //   charts: "",
    //   opinion: ["未交易用户", "交易一次用户", "留存客户", "回流客户"],
    //   opinionData: [
    //     { value: 335, name: "未交易用户" },
    //     { value: 310, name: "交易一次用户" },
    //     { value: 234, name: "留存客户" },
    //     { value: 135, name: "回流客户" },
    //   ],
    };
  },
  components: {},
  created(){
    Vue.prototype.$echarts = echarts;
  },
  mounted() {
    this.$nextTick(function () {
      this.drawPie("mainThree");
    });
  },
  methods: {
    drawPie(id) {
      var chartDom = document.getElementById(id);
      var myChart = echarts.init(chartDom);
      var option;
      option = {
        title: {
          text: "",
          subtext: "",
          left: "center",
        },
        tooltip: {
          trigger: "item",
          formatter: "{a}<br/>{b}:{c} ({d}%)",
        },
        legend: {
          orient: "vertical",
          left: "left",
        },
        series: [
          {
            name: "数据统计",
            type: "pie",
            radius: "50%",
            data: [
              { value: 1048, name: "未交易用户" },
              { value: 735, name: "交易一次用户" },
              { value: 580, name: "留存客户" },
              { value: 300, name: "回流客户" },
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)",
              },
            },
          },
        ],
      };
      option && myChart.setOption(option);
    },
  },
};
</script>
<style lang="scss" scoped></style>
