<template>
  <div class="flex-1">
    <div id="myBarChart" class="kline-BoxMain"></div>
  </div>
</template>

<script setup>
import { onMounted, } from 'vue';
import { themeStore } from "@/store/theme";
import * as echarts from 'echarts/core';
import { GridComponent } from 'echarts/components';
import { BarChart } from 'echarts/charts';
import { CanvasRenderer } from 'echarts/renderers';
const thStore = themeStore();
echarts.use([
  GridComponent,
  BarChart,
  CanvasRenderer,
]);


const props = defineProps({
  option: {
    type: Object,
    default: () => (
      {
        xData: ['<-50%', '-50~-10%', '-10~0%', '0', '0~10%', '10~50%', '>50%'],
        yData: [
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
        dataIndex: 3,
      }
    )
  }
})


let myChart = null

onMounted(() => {
  drawLine()
})

const drawLine = () => {
  // 基于准备好的dom，初始化echarts实例
  const ele = document.getElementById("myBarChart")
  ele.style.width = window.innerWidth + 70 + 'px';
  ele.style.height = '200px';
  myChart = echarts.init(ele);
  // 绘制图表
  myChart.setOption(
    {
      grid: {
        top: '15%',
        bottom: '15%',
      },
      xAxis: {
        type: 'category',
        data: props.option.xData,
        axisTick: {
          show: false //不显示坐标轴刻度线
        },
        axisLine: {
          show: false, //不显示坐标轴线
        },
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
          barWidth: 20,
          data: props.option.yData,
          type: 'bar',
          datasetIndex: 1,
          label: {
            show: true, //是否开启柱子顶部文字显示
            position: "top", //在上方显示 文字显示方向
            color: '#fff',
            formatter: function (item, index) {//index为undefined 下标正确为item.dataInde
              let returnStr = "";
              if (item.dataIndex < props.option.dataIndex) {
                returnStr = item.value + " \n ";
                return "{a|" + returnStr + "}";
              } else if (item.dataIndex == props.option.dataIndex) {
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
                color: thStore.theme === 'dark' ? "#fff" : '#000',
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
