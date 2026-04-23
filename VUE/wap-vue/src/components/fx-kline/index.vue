<template>
  <van-loading color="#1194F7" class="loading-box" v-if="isLoading" />
  <div id="kline" class="boxDisplay" :style="{ height: `${props.height || defaultH}px`, position: 'relative' }"
    v-if="defaultH">
  </div>
  <ul class="flex px-4 py-4 box-border justify-between indicator-box" v-if="showBottom"
    style="border-top:1px solid rgba(68,75,88,0.2);">
    <li v-for="item in subTechnicalIndicatorTypes" :key="item" class="mr-2" :class="{ 'textColor': typeValue === item }"
      @click="choiceType(item)">{{ item }}
    </li>
  </ul>
</template>

<script setup>
import { init, dispose } from 'klinecharts'
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import config from './config'
import { SET_STAGE } from '@/store/types.store';
import fakeData from './fake-data'
import { _getKline } from "@/service/trade.api";
import { WS_URL } from '@/config'
import { useQuotesStore } from '@/store/quotes.store'
import { useI18n } from "vue-i18n";
const { t } = useI18n()
const quotesStore = useQuotesStore()

let chart = null
const paneId = ref('')
const typeValue = ref('MA')//图形类型
const subTechnicalIndicatorTypes = ref(['MA', 'EMA', 'BOLL', 'VOL', 'MACD', 'KDJ', 'RSI'])

const data = ref(fakeData)

const defaultH = ref(0)
const loading = ref(false)
const isLoading = ref(false)
const socket = ref(null)
const timer = ref(null)
const emits = defineEmits(['data', 'loading'])

onMounted(() => {
  defaultH.value = window.innerHeight - 94
  nextTick(async () => {
    await initData1()
    await initData()
    startQuoteScoket()
  })
})

const props = defineProps({
  symbol: {
    type: String
  },
  height: {
    type: Number
  },
  chartType: {
    type: String,
    default: 'candle_solid'
  },
  showBottom: {
    type: Boolean,
    default: true
  },
  isShowsolid: {
    type: Boolean,
    default: false
  }
})

const startQuoteScoket = () => {
  closeSocket()
  socket.value = new WebSocket(`${WS_URL}/1/${props.symbol}`)
  socket.value.onmessage = (evt) => {
    const { data } = evt
    const { code, data: _data } = JSON.parse(data)
    if (code / 1 === 0) {
      emits('data', _data[0])
      updateCharts(_data[0])
    }
  }
}

onBeforeUnmount(() => {
  closeSocket()
})

const closeSocket = () => {
  socket.value && socket.value.close()
  socket.value = null
}


const updateCharts = async (nowData) => {
  const dataList = chart.getDataList()
  if (!dataList || dataList.length === 0) {
    return
  }
  const lastData = dataList[dataList.length - 1]
  // const nowData = this.updateData
  // const timeValue = this.timeValue
  // nowData.timestamp = nowData.ts
  // 防止停盘后柱状图自动刷新修改
  if ((nowData.timestamp - lastData.timestamp) >= quotesStore.seconds) {
    data.value = await _getKline(props.symbol, quotesStore.stage === 'timeSharing' ? '1min' : quotesStore.stage)

    // 修改 k 线图价格和 ws 推送价格不一致

    data.value.unshift()

    data.value.push({
      high: nowData.high,
      low: nowData.low,
      close: nowData.close,
      open: nowData.open,
      timestamp: nowData.timestamp
    })

    chart.applyNewData(data.value);
    return false
  }

  const newData = {
    close: nowData.close / 1,
    current_time: lastData.current_time,
    high: lastData.high > nowData.close / 1 ? lastData.high : (nowData.close / 1),
    // high: lastData.high,
    line: quotesStore.stage === 'timeSharing' ? '1min' : quotesStore.stage, // timeValue.id,
    low: lastData.low < nowData.close / 1 ? lastData.low : (nowData.close / 1),
    // low: lastData.low,
    open: lastData.open,
    symbol: lastData.symbol,
    // ts: lastData.ts, //
    // timestamp: lastData.timestamp, //
    // timestamp: nowData.ts || nowData.timestamp, //
    timestamp: (nowData.timestamp - lastData.timestamp) < quotesStore.seconds ? lastData.timestamp : (lastData.timestamp + quotesStore.seconds),
    // volume: lastData.volume / 1,
    volume: nowData.volume / 1
  }
  nextTick(() => {
    chart.setStyleOptions({
      candle: {
        type: props.chartType
      },
      yAxis: {
        width: quotesStore.stage === 'timeSharing' ? 0 : null,
      }
    })
    chart.updateData(newData)
  })
}

const initData = async () => {
  chart = init('kline', config);
  chart.setOffsetRightSpace(15)
  chart.setDataSpace(10)
  if (props.isShowsolid) {
    chart.createTechnicalIndicator('MA', false, { id: 'candle_pane' });
    paneId.value = chart.createTechnicalIndicator('MA');
  }

  // this.fetchData()
  chart.setStyleOptions({
    candle: {
      type: props.chartType
    },
    yAxis: {
      width: quotesStore.stage === 'timeSharing' ? 0 : null,
    }
  })
  if (!quotesStore.stage) {
    quotesStore[SET_STAGE]({ stage: '1min', seconds: 1 * 60 * 1000 })
  }

  emits('loading', true)
  isLoading.value = true
  await getKlineData()
  isLoading.value = false
  emits('loading', false)
  let length = 2
  if (data.value.length > 0) {
    length = data.value[data.value.length - 1].decimals
  }
  chart.setPriceVolumePrecision(length, 2)
  localStorage.setItem('kline', JSON.stringify(data.value))

  nextTick(() => {
    chart.applyNewData(data.value);
  })
}

const getKlineData = async () => {

  data.value = await _getKline(props.symbol, quotesStore.stage === 'timeSharing' ? '1min' : quotesStore.stage)

  if (data.value.length == 0) {
    timer.value = setTimeout(async () => {
      await getKlineData()
    }, 2000);
  } else {
    if (timer.value) {
      clearTimeout(timer.value)
      timer.value = null
    }
    isLoading.value = false
    emits('loading', false)
    let length = 2
    if (data.value.length > 0) {
      length = data.value[data.value.length - 1].decimals
    }
    chart.setPriceVolumePrecision(length, 2)
    localStorage.setItem('kline', JSON.stringify(data.value))

    nextTick(() => {
      console.log('ppp')
      chart.applyNewData(data.value);
    })
  }

}

const initData1 = async () => {
  chart = init('kline', config);
  chart.setOffsetRightSpace(15)
  chart.setDataSpace(10)

  data.value = JSON.parse(localStorage.getItem('kline'))
  if (!data.value) {
    data.value = await _getKline(props.symbol, quotesStore.stage === 'timeSharing' ? '1min' : quotesStore.stage)
  }

  let length = 2
  if (data.value.length > 0) {
    data.value[data.value.length - 1].decimals
  }
  chart.setPriceVolumePrecision(length, 2)
  // if (props.type === 'candle_solid') {
  //     chart.createTechnicalIndicator('MA', false, { id: 'candle_pane' });
  //     paneId.value = chart.createTechnicalIndicator('VOL');
  // }

  // this.fetchData()
  chart.setStyleOptions({
    candle: {
      type: props.chartType
    }
  })
  if (!quotesStore.stage) {
    quotesStore[SET_STAGE]({ stage: '1min', seconds: 1 * 60 * 1000 })
  }
  // console.log(data.value)

  nextTick(() => {
    chart.applyNewData(data.value);
  })
}
const choiceType = (type) => { // 选择副线
  typeValue.value = type
  chart.createTechnicalIndicator(type, false, { id: paneId.value })
}

</script>
<style lang="scss" scoped>
.textColor {
  color: $color_main;
}

.indicator-box {
  font-size: 12px;
  color: $text_color;
}

.loading-box {
  position: absolute;
  top: 30%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>
