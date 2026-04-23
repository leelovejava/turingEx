<template>
    <div :style="{ height: height, width: width }" id="mainTwo">
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
import * as echarts from 'echarts';
import "echarts-gl";
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
            var chartDom = document.getElementById('mainTwo');
            var myChart = echarts.init(chartDom);
            var option;

            option = {
                color:['#7890F7'],
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
                magicType: { show: true, type: ['line'] },
                restore: { show: true },
                saveAsImage: { show: true }
                }
            },
            legend: {
                data: ['用户数']
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
                    name: '精度值',
                    min: 0,
                    max: 30,
                    interval: 5,
                    axisLabel: {
                        formatter: '{value}'
                    }
                },
                {
                    type: 'value',
                    name: '数量',
                    min: 0,
                    max: 250,
                    interval: 50,
                    axisLabel: {
                        formatter: '{value}'
                    }
                }
            ],
            series: [
                // {
                // name: 'Evaporation',
                // type: 'bar',
                // tooltip: {
                //     valueFormatter: function (value) {
                //     return value + ' ml';
                //     }
                // },
                // data: [
                //     2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3
                // ]
                // },
                // {
                //     name: '订单金额',
                //     type: 'bar',
                //     tooltip: {
                //         valueFormatter: function (value) {
                //         return value + ' $';
                //         }
                //     },
                //     data: [
                //         20000000.6, 11111115.9, 33111115.9, 44111115.9, 24111115.9, 11111115.9, 17111115.9, 29111115.9, 41111115.9, 1111115.9, 21111115.9, 11111115.9
                //     ]
                // },
                {
                    name: '用户数',
                    type: 'line',
                    yAxisIndex: 1,
                    tooltip: {
                        valueFormatter: function (value) {
                        return '（人）' + value ;
                        }
                    },
                    data: [20, 22, 33, 45, 63, 102, 103, 134, 123.0, 165, 120, 222],
                    areaStyle:{
                        color:{
                            type:'linear',
                            x:0,
                            y:1,
                            x2:0,
                            y2:0,
                            colorStops: [{
                                offset: 0, color: 'rgba(120, 144, 247, 0)' // 0% 处的颜色
                            }, {
                                offset: 1, color: 'rgba(120, 144, 247, 0.3)' // 100% 处的颜色
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
  