<template>
    <div :style="{ height: height, width: width }" id="main">
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
import * as echarts from 'echarts/lib/echarts';
import "echarts-gl";
  export default {
    props: {
    height: {
      type: String,
      default: "500px",
    },
    width: {
      type: String,
      default: "90%",
    },
  },
    data () {
      return {
      }
    },
    components: {
      
    },
    mounted(){
        this.drawLine();
    },
    methods: {
        drawLine() {
            var chartDom = document.getElementById('main');
            var myChart = echarts.init(chartDom);
            var option;

            option = {
                color:['#F3DEE1','#7890F7'],
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                        crossStyle: {
                        color: '#999'
                    }
                }
            },
            toolbox: {
                feature: {
                dataView: { show: true, readOnly: false },
                magicType: { show: true, type: ['line', 'bar'] },
                restore: { show: true },
                saveAsImage: { show: true }
                }
            },
            legend: {
                data: ['订单金额', '订单数']
            },
            xAxis: [
                {
                type: 'category',
                data: ['01-15', '01-15', '01-15', '01-15', '01-15', '01-15', '01-15', '01-15', '01-15', '01-15', '01-15', '01-15'],
                axisPointer: {
                    type: 'shadow'
                }
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    name: '订单金额',
                    min: 0,
                    max: 50000000,
                    interval: 10000000,
                    axisLabel: {
                        formatter: '{value}'
                    }
                },
                {
                    type: 'value',
                    name: '订单数',
                    min: 0,
                    max: 25,
                    interval: 5,
                    axisLabel: {
                        formatter: '{value} °C'
                    }
                }
            ],
            series: [
                {
                    name: '订单金额',
                    type: 'bar',
                    tooltip: {
                        valueFormatter: function (value) {
                        return value + ' $';
                        }
                    },
                    data: [
                        20000000.6, 11111115.9, 33111115.9, 44111115.9, 24111115.9, 11111115.9, 17111115.9, 29111115.9, 41111115.9, 1111115.9, 21111115.9, 11111115.9
                    ]
                },
                {
                    name: '订单数',
                    type: 'line',
                    yAxisIndex: 1,
                    tooltip: {
                        valueFormatter: function (value) {
                        return value + ' °#';
                        }
                    },
                    data: [2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0, 6.2],
                    areaStyle:{
                        color:{
                            type:'linear',
                            x:0,
                            y:1,
                            x2:0,
                            y2:0,
                            colorStops: [{
                                offset: 0, color: 'rgba(120, 144, 247, 0.3)' // 0% 处的颜色
                            }, {
                                offset: 1, color: 'rgba(120, 144, 247, 0)' // 100% 处的颜色
                            }],
                            globalCoord: false // 缺省为 false
                        }
                    }
                },
                //填充颜色
                {
                    
                }
            ]
            };

            option && myChart.setOption(option);
    },
  },
}
  </script>
  <style lang="scss" scoped>
  </style>
  