<template>
  <div class="flex-1">
    <div id="myChart3" class="kline-BoxMain"></div>
  </div>
</template>

<script setup>
import { onMounted, } from 'vue';
import * as echarts from 'echarts/core';
import { GridComponent } from 'echarts/components';
import { BarChart } from 'echarts/charts';
import { CanvasRenderer } from 'echarts/renderers';

echarts.use([
  GridComponent,
  BarChart,
  CanvasRenderer,
]);

let myChart = null

onMounted(() => {
  drawLine()
})

const drawLine = () => {
  // 基于准备好的dom，初始化echarts实例
  const ele = document.getElementById("myChart3")
  ele.style.width = window.innerWidth + 70 + 'px';
  ele.style.height = '200px';
  myChart = echarts.init(ele);
  // 绘制图表
  myChart.setOption(
    {
      grid: {
        top: '10%',
        bottom: '25%',
      },
      xAxis: {
        // axisLabel: {
        //   interval: 0,
        //   rotate: 40
        // },
        type: 'category',
        data: ['<-15%', '-15~10%', '-10~-5%', '-5~-2%', '-2~0%', '-2~0%', '0~2%', '2~-5%', '5~10%', '10~15%', '>15%']
      },
      yAxis: {
        type: 'value',
        show: false,
        axisLine: {
          show: false
        },
        axisTick: {
          show: false
        },
        splitLine: {
          show: false
        },
      },
      splitLine: { show: false },
      series: [
        {
          data: [
            {
              value: 96,
              itemStyle: {
                color: '#06CDA5'
              }
            },
            {
              value: 74,
              itemStyle: {
                color: '#06CDA5'
              }
            },
            {
              value: 252,
              itemStyle: {
                color: '#06CDA5'
              }
            },
            {
              value: 646,
              itemStyle: {
                color: '#06CDA5'
              }
            },
            {
              value: 2271,
              itemStyle: {
                color: '#06CDA5'
              }
            },
            {
              value: 1121,
              itemStyle: {
                color: '#747A8F'
              }
            },
            {
              value: 1821,
              itemStyle: {
                color: '#F43368'
              }
            },
            {
              value: 661,
              itemStyle: {
                color: '#F43368'
              }
            },
            {
              value: 277,
              itemStyle: {
                color: '#F43368'
              }
            },
            {
              value: 81,
              itemStyle: {
                color: '#F43368'
              }
            },
            {
              value: 125,
              itemStyle: {
                color: '#F43368'
              }
            },
          ],
          type: 'bar',
          datasetIndex: 1,
          label: {
            show: true, //是否开启柱子顶部文字显示
            position: "top", //在上方显示 文字显示方向
            color: '#fff',
            formatter: function (item, index) {//index为undefined 下标正确为item.dataInde
              let returnStr = "";
              if (item.dataIndex < 5) {
                returnStr = item.value + " \n ";
                return "{a|" + returnStr + "}";
              } else if (item.dataIndex == 5) {
                returnStr = item.value + " \n ";
                return "{b|" + returnStr + "}";
              } else {
                returnStr = item.value + " \n ";
                return "{c|" + returnStr + "}";
              }
            },
            rich: {
              a: {
                color: "#06CDA5",
              },
              b: {
                color: "#fff",
              },
              c: {
                //默认
                color: "#F43368",
              },
            },

          }
        }
      ]
    }
  );
}

</script>
<style scoped></style>
