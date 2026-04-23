<template>
  <div id="cryptos">
    <div class="kline-charts">
      <ul class="flex px-8 pb-5 box-border justify-between text-grey fontStyle text-26"
        style="border-bottom:1px solid rgba(68,75,88,0.2);">
        <template v-if="!isChange">
          <li v-for="item in timeList" :key="item.id" class="mr-2" :class="{ 'textColor': item.id === timeValue.id }"
            @click="choiceTime(item)">{{ item.text }}</li>
        </template>
        <template v-else>
          <template v-if="isNight">
            <li v-for="item in timeList" :key="item.id" class="mr-2" :class="{ 'text-white': item.id === timeValue.id }"
              @click="choiceTime(item)">{{ item.text }}</li>
          </template>
          <template v-if="!isNight">
            <li v-for="item in timeList" :key="item.id" class="mr-2" :class="{ 'text-black': item.id === timeValue.id }"
              @click="choiceTime(item)">{{ item.text }}</li>
          </template>
        </template>
      </ul>
      <div class="relative">
        <div id="kline" class="h-192"></div>
        <div
          class="flex justify-center items-center text-center text-grey absolute left-0 top-0  w-full h-full z-10 mainBackground"
          v-if="chartLoading">
          <van-loading type="spinner"></van-loading>
        </div>
      </div>
      <ul class="flex px-8 py-5 box-border justify-between text-grey text-26" v-if="showBottom"
        style="border-top:1px solid rgba(68,75,88,0.2);">
        <template v-if="!isChange">
          <li v-for="item in subTechnicalIndicatorTypes" :key="item" class="mr-5"
            :class="{ 'textColor': typeValue === item }" @click="choiceType(item)">{{ item }}</li>
        </template>
        <template v-else>
          <template v-if="isNight">
            <li v-for="item in subTechnicalIndicatorTypes" :key="item" class="mr-5"
              :class="{ 'text-white': typeValue === item }" @click="choiceType(item)">{{ item }}</li>
          </template>
          <template v-if="!isNight">
            <li v-for="item in subTechnicalIndicatorTypes" :key="item" class="mr-5"
              :class="{ 'text-black': typeValue === item }" @click="choiceType(item)">{{ item }}</li>
          </template>
        </template>
      </ul>
    </div>
  </div>
</template>
<script>
import { init, dispose } from 'klinecharts'
let chart = null
import { _getKline } from "@/service/trade.api";
import config from './config'
import { Loading } from 'vant';
export default {
  name: 'KlineCharts',
  data() {
    return {
      // symbol: 'btc',
      dealInfo: {},//交易对信息

      timeValue: {}, // 当前k线周期
      subTechnicalIndicatorTypes: ['MA', 'EMA', 'BOLL', 'VOL', 'MACD', 'KDJ', 'RSI'],
      typeValue: 'VOL',//图形类型
      klinecharts: null,//K线图实例
      chart: null,
      // resolution: '1',//分辨率
      // lang: 'en', //语言
      chartLoading: true, //加载动画
      paneId: '',
      chartData: [], // 图表数据
      timer: null
    }
  },
  computed: {
    timeList() {
      return [
        { id: '1min', time: '1min', text: this.$t('分时'), ts: 1 * 60 * 1000 },
        { id: '1mins', time: '1min', text: '1' + this.$t('分'), ts: 1 * 60 * 1000 },
        { id: '5min', time: '5min', text: '5' + this.$t('分'), ts: 5 * 60 * 1000 },
        { id: '15min', time: '15min', text: '15' + this.$t('分'), ts: 15 * 60 * 1000 },
        { id: '30min', time: '30min', text: '30' + this.$t('分'), ts: 30 * 60 * 1000 },
        { id: '60min', time: '60min', text: '1' + this.$t('小时'), ts: 60 * 60 * 1000 },
        { id: '4hour', time: '4hour', text: '4' + this.$t('小时'), ts: 4 * 60 * 60 * 1000 },
        { id: '1day', time: '1day', text: '1' + this.$t('天'), ts: 24 * 60 * 60 * 1000 },
        { id: '1week', time: '1week', text: '1' + this.$t('周'), ts: 7 * 24 * 60 * 60 * 1000 },
        { id: '1mon', time: '1mon', text: '1' + this.$t('月'), ts: 30 * 24 * 60 * 60 * 1000 }
      ]//时间列表
    }
  },
  components: {
    [Loading.name]: Loading
  },
  props: {
    symbol: {
      type: String,
      default: ''
    },
    updateKey: {
      type: Number,
      default: 0
    },
    updateData: {
      type: Object,
      default() {
        return {}
      }
    },
    showBottom: {
      type: Boolean,
      default: true
    },
    isChangeLine: {
      type: Boolean,
      default: false
    },
    isNight: {
      type: Boolean,
      defalult: true
    },
    isChange: {
      type: Boolean,
      defalult: false
    }
  },
  mounted() {
    this.initData()
  },
  beforeUnmount() {
    dispose('kline')
  },
  watch: {
    isChangeLine(val) {
      this.clearTimer()
      this.fetchData()
    },
    updateKey() { // 更新charts

      const dataList = chart.getDataList()
      if (dataList.length > 0) {
        const lastData = dataList[dataList.length - 1]
        const nowData = this.updateData
        const timeValue = this.timeValue
        const newData = {
          close: nowData.close / 1,
          current_time: lastData.current_time,
          high: lastData.high > nowData.close / 1 ? lastData.high : (nowData.close / 1),
          // high: lastData.high,
          line: timeValue.id,
          low: lastData.low < nowData.close / 1 ? lastData.low : (nowData.close / 1),
          // low: lastData.low,
          open: lastData.open,
          symbol: lastData.symbol,
          ts: lastData.ts, //
          timestamp: (nowData.ts - lastData.ts) < timeValue.ts ? lastData.ts : (lastData.ts + timeValue.ts),
          volume: lastData.volume / 1
        }
        this.$nextTick(() => {
          this.setChartType()
          chart.updateData(newData)
        })
      }
    }
  },
  methods: {
    initData() {
      this.timeValue = this.timeList[0]
      chart = init('kline', config);
      chart.setOffsetRightSpace(25)
      chart.setDataSpace(10)
      chart.setPriceVolumePrecision(4, 2)
      chart.createTechnicalIndicator('MA', false, { id: 'candle_pane' });
      this.paneId = chart.createTechnicalIndicator('VOL');
      this.fetchData()
    },
    fetchData(time) { // 获取数据
      this.chartLoading = true
      _getKline(this.symbol, this.timeValue.time || '1min').then(data => {
        this.chartLoading = false
        data.map(item => {
          item.timestamp = item.ts
        })

        let length = 2
        if (data[0].decimals) {
          length = data[0].decimals
        }
        //let length = data[0] ? data[0].close.toString().split('.')[1].length : 4
        chart.setPriceVolumePrecision(length, 2)
        this.setChartType()
        chart.applyNewData(data);
        this.$emit('updataLine', false)
      })
      // TODO:轮询，删掉上面那段，添加到定时器中
      // this.timer = setInterval(() => {
      //   _getKline(this.symbol, this.timeValue.time || time).then(data => {
      //     this.chartLoading = false
      //     data.map(item => {
      //       item.timestamp = item.ts
      //     })
      //     let str = data[0] ? data[0].close.toString() : ''
      //     let length = 2
      //     if (str.indexOf('.') != -1) {
      //       length = str.split('.')[1].length
      //     }
      //     //let length = data[0] ? data[0].close.toString().split('.')[1].length : 4
      //     chart.setPriceVolumePrecision(length, 2)
      //     this.setChartType()
      //     chart.applyNewData(data);
      //     this.$emit('updataLine', false)
      //   })
      // }, 1000);
    },
    setChartType() {
      let type = 'area'
      if (this.timeValue.id === '1min') {
        type = 'area'
      } else {
        type = 'candle_solid'
      }
      chart.setStyleOptions({
        candle: {
          type
        }
      })
    },
    choiceTime(value) { // 选择周期
      this.timeValue = value
      this.clearTimer()
      this.fetchData()
    },
    choiceType(type) { // 选择副线
      this.typeValue = type
      chart.createTechnicalIndicator(type, false, { id: this.paneId })
    },
    clearTimer() {
      clearInterval(this.timer)
      this.timer = null
    },
  },
  deactivated() {
    this.clearTimer()
  }
}
</script>
<style lang="scss" scoped>
// #kline {
//   // min-height: 828px;
//   height: 1200px;
// }
</style>
