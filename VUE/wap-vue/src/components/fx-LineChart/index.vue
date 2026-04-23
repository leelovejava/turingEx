<template>
  <div class="flex-1 my-2 relative">
    <div id="myLineChart" class="kline-BoxMain"></div>
    <div class="persent">
      <div class="left">
        <span>{{ $t('次新股指数') }}</span>
        <span class="green">-{{ persent }}%</span>
      </div>
      <div class="right">
        <span>{{ $t('S&P 500') }}</span>
        <span class="red">+{{ persent1 }}%</span>
      </div>
    </div>
    <div class="time">
      <span>{{ leftTime }}</span>
      <span>{{ rightTime }}</span>
    </div>
    <div class="btnGroup my-10">
      <button v-for="(item, index) in btnGroup" :key="index" @click="changeData(item.value, index)"
        :class="index === activeIndex ? 'active btn' : 'btn'">{{ $t(item.name) }}</button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { themeStore } from '@/store/theme';
import * as echarts from 'echarts/core';
import { GridComponent } from 'echarts/components';
import { LineChart } from 'echarts/charts';
import { CanvasRenderer } from 'echarts/renderers';
echarts.use([
  GridComponent,
  LineChart,
  CanvasRenderer,
]);

const thStore = themeStore()

let myChart = null

const chartData = ref([])
const chartData1 = ref([])
const xAxisData = ref([])
const leftTime = ref('')
const rightTime = ref('')
const activeIndex = ref(0)
const btnGroup = ref([
  {
    value: 'month',
    name: '最近1月',
  },
  {
    value: 'threeMonths',
    name: '最近3月',
  },
  {
    value: 'sixMonths',
    name: '最近6月',
  }
])
const persent = ref('')
const persent1 = ref('')



onMounted(() => {
  drawLine()
  getData('month');
})

const drawLine = () => {
  // 基于准备好的dom，初始化echarts实例
  const ele = document.getElementById("myLineChart")
  ele.style.width = window.innerWidth + 'px';
  ele.style.height = '300px';
  myChart = echarts.init(ele);

  // 绘制图表
  myChart.setOption(
    {
      tooltip: {
        trigger: 'axis'
      },
      // color:['#EEA642','#787EA0'],
      // legend: {
      //   left: 10,
      //   data: [{icon: 'circle', name: '次新股指数'},{icon: 'circle', name: 'S&P 500'}],
      //   textStyle:{//图例文字的样式
      //       color: THEME === 'dark' ? '#fff' : '#000',
      //   }
      // },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,

        axisLine: {
          lineStyle: {
            color: '#ccc',
          }
        }
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          show: false
        },
        min: 20,
        max: 100,
        splitLine: { show: false },
        axisLine: { show: false },
        axisTick: { show: false }
      },
      series: [
        {
          name: '次新股指数',
          symbol: "none",
          type: 'line',
          stack: 'Total',
          smooth: true,
          data: chartData.value,
          itemStyle: {
            normal: {
              lineStyle: {
                color: '#EEA642' //改变折线颜色
              }
            }
          },
        },
        {
          name: 'S&P 500',
          symbol: "none",
          type: 'line',
          stack: 'Total',
          smooth: true,
          data: chartData1.value,
          itemStyle: {
            normal: {
              lineStyle: {
                color: '#787EA0' //改变折线颜色
              }
            }
          },
        },
      ]
    }
  );
}

const getRoundDate = (value) => {
  const currentDate = new Date();
  const monthAgo = new Date(currentDate.getFullYear(), currentDate.getMonth() - value, 1);
  monthAgo.setHours(0, 0, 0, 0);
  const lastDayOfMonth = new Date(monthAgo.getFullYear(), monthAgo.getMonth() + 1, 0).getDate();
  monthAgo.setDate(Math.min(currentDate.getDate(), lastDayOfMonth));

  // 输出格式化后的日期字符串
  leftTime.value = formatDate(monthAgo)
  rightTime.value = formatDate(currentDate)
}

// 格式化时间
const formatDate = (date) => {
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  return year + '-' + month + '-' + day;
}


const getXData = (value) => {
  let data = []
  for (let i = 0; i < value; i++) {
    data.push(value);
  }
  return data
}


const updateChart = () => {
  myChart.setOption({
    xAxis: {
      data: xAxisData.value,
    },
    series: [
      {
        data: chartData.value,
      },
      {
        data: chartData1.value,
      },
    ],
  });
}

const changeData = (timePeriod, index) => {
  getData(timePeriod);
  activeIndex.value = index;
}

const getData = (timePeriod) => {
  if (timePeriod === 'month') {
    chartData.value = [45, 54, 79, 70, 72, 65, 58, 79, 44, 58]
    chartData1.value = [46, 44, 49, 60, 65, 65, 73, 49, 42, 69]
    persent.value = 0.06
    persent1.value = 4.33
    getRoundDate(1)
  } else if (timePeriod === 'threeMonths') {
    chartData.value = [43, 66, 41, 57, 78, 51, 77, 51, 49, 40]
    chartData1.value = [71, 63, 67, 75, 52, 68, 60, 58, 41, 69]
    persent.value = 0.02
    persent1.value = 5.33
    getRoundDate(3)
  } else if (timePeriod === 'sixMonths') {
    chartData.value = [46, 68, 75, 67, 61, 61, 53, 72, 68, 79]
    chartData1.value = [61, 58, 71, 77, 53, 73, 46, 60, 74, 43]
    persent.value = 0.03
    persent1.value = 8.33
    getRoundDate(6)
  }
  updateChart();
}

</script>
<style lang="scss" scoped>
.persent {
  position: absolute;
  left: 36px;
  top: 0;
  display: flex;
  flex-wrap: wrap;
  gap: 0 30px;

  .left,
  .right {
    display: flex;
    flex-direction: column;
    font-size: 12px;
    line-height: 16px;
    gap: 5px;

    span {
      position: relative;

      &:first-child::before {
        content: '';
        position: absolute;
        left: -10px;
        top: 5px;
        width: 6px;
        height: 6px;
        border-radius: 6px;
      }
    }
  }

  .left span:first-child::before {
    background-color: #EEA642;
  }

  .right span:first-child::before {
    background-color: #787EA0;
  }
}

.time {
  display: flex;
  justify-content: space-between;
  padding: 0 5%;
  color: $text_color;
}

.btnGroup {
  padding: 0 5%;
  display: flex;
  flex-wrap: wrap;

  .btn {
    padding: 5px 12px;
    color: #B8C0D7;
    border-radius: 4px;

    &.active {
      background-color: #1194F7;
      color: #fff;
    }
  }
}
</style>
