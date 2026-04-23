<template>
  <div class="popup-delivery  overflow-hidden">
    <div class="border-b-color pt-14 pb-8 relative textColor">
      <h1 class="text-40 px-8">{{ detailData.name }} {{ $t('delivery') }}</h1>
      <img src="@/assets/image/icon-close.png" class="w-10 h-10 absolute right-10 top-10" @click="onClose" />
    </div>
    <div class="flex justify-center mt-22" v-if="!(detailData.state === 'created')">
      <!-- {{detailData.time_num}} -->
      <!--            <van-circle v-model=" currentRate" :rate="detailData.time_num" :speed="100" :text="text"
      layer-color="#F5F5F5">-->
      <!--            </van-circle>-->
      <van-circle v-show="rate != 0" layer-color="#ccc" size="120px" v-model:current-rate="currentRate" :rate="rate"
        :speed="0" :text="text">
        <van-count-down @finish="handleFinish" class="textColor" ref="coutDown" @change="timeChange"
          :time="remain_time * 1000" :format="timeFormat" />
      </van-circle>
    </div>
    <div class="mt-10 text-44 text-center"
      :class="{ 'text-green': detailData.profit_state === '1', 'text-red': detailData.profit_state === '0' }" v-else>{{
        $t('盈亏') }}<span>{{ detailData.profit_state === '1' ? '+' : '' }}{{ detailData.profit }}</span> USD</div>
    <ul class="flex flex-col pb-16 textColor">
      <li v-for="item in listItem" :key="item.id" class="flex justify-between px-10 mt-5">
        <p class="text-grey text-32">{{ item.text }}</p>
        <p class="text-32 " v-if="item.key == 'close_price'" :class="{
          'text-green':
            (detailData.direction === 'buy' && detailData.state !== 'created' && price * 1 >= detailData['open_price'] * 1) ||
            (detailData.direction === 'buy' && detailData.state == 'created' && detailData['close_price'] * 1 >= detailData['open_price'] * 1) ||
            (detailData.direction !== 'buy' && detailData.state !== 'created' && price * 1 <= detailData['open_price'] * 1) ||
            (detailData.direction !== 'buy' && detailData.state == 'created' && detailData['close_price'] * 1 <= detailData['open_price'] * 1),
          'text-red':
            (detailData.direction === 'buy' && detailData.state !== 'created' && price * 1 < detailData['open_price'] * 1) ||
            (detailData.direction === 'buy' && detailData.state == 'created' && detailData['close_price'] * 1 < detailData['open_price'] * 1) ||
            (detailData.direction !== 'buy' && detailData.state !== 'created' && price * 1 > detailData['open_price'] * 1) ||
            (detailData.direction !== 'buy' && detailData.state == 'created' && detailData['close_price'] * 1 > detailData['open_price'] * 1),
        }">{{ detailData.state !== 'created' ? price : detailData[item.key] }}</p>
        <p class="text-28 " :class="colorChoose(item)" v-else>{{ handleBuyWord(item) }}</p>
      </li>
    </ul>
    <div v-if="showBtns" class="flex justify-between textColor px-8 pb-16">
      <p :class="{ 'btnMain': detailData.state !== 'created', 'bgDark': detailData.state === 'created' }"
        class="w-72 h-24 rounded-lg flex justify-center items-center mr-5" @click="onClose">{{ $t('close') }}</p>
      <p @click="continueToBuy"
        :class="{ 'btnMain': detailData.state === 'created', 'bgDark': detailData.state !== 'created', 'disableBtn': disabled }"
        class="w-80 h-24 rounded-lg flex justify-center items-center">{{ $t('继续下单') }}</p>
    </div>
  </div>
</template>
<script setup>
import { Circle, CountDown } from 'vant';
import { ref, onMounted, computed, onUnmounted } from 'vue';
import { useI18n } from "vue-i18n";
const { t } = useI18n()
const emits = defineEmits(['timeEnd', 'close', 'continueToBuy'])
const props = defineProps({
  showBtns: {
    type: Boolean,
    default: false,
  },
  detailData: {
    type: Object,
    default() {
      return {}
    }
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  price: {
    type: [Number, String],
    default: '',
  },
})

let totalTime = ref('') // 合约总时长
let remain_time = ref(0)
let arr = ref([])
let currentRate = ref(0) // 当前进度
let interval = ref(null)
let rate = ref(0) // 目标进度

const timeFormat = computed(() => {
  let res
  if (!arr.value.length) {
    return '--'
  }
  if (arr.value[0] / 1 > 0) {
    res = 'DDd HHh mmm sss'
  } else {
    if (arr.value[1] / 1 > 0) {
      res = 'mmm sss'
    } else {
      res = 'sss'
    }
  }
  return res
})

const text = computed(() => {
  return currentRate.value + 's';
})


const listItem = computed(() => {
  return [
    { id: 1, text: t('buyPrice'), key: 'open_price' },
    { id: 2, text: props.detailData.state !== 'created' ? t('现价') : t('结算价格'), key: 'close_price' },
    { id: 3, text: t('方向'), key: 'direction' },
    { id: 4, text: t('number'), key: 'amount', extro: 'USD' },
    { id: 5, text: t('交割时间'), key: 'settlement_time' }
  ]
})

onMounted(() => {
  if (!(props.detailData.state === 'created')) {// 交割已完成
    handleInit()
  }
})

onUnmounted(() => {
  clearInterval(interval.value)
  interval.value = null
})

const continueToBuy = () => {
  if (props.detailData.state !== 'created') {
    return
  }
  emits('continueToBuy', props.detailData)
}

const handleBuyWord = (item) => {
  if (item.id === 3) {
    if (props.detailData.direction === 'buy') {
      return t('开多')
    } else {
      return t('开空')
    }
  } else if (item.key === 'settlement_time') {
    return props.detailData.time_num + props.detailData.time_unit
  } else {
    if ('extro' in item) {
      return props.detailData[item.key] && props.detailData[item.key].toString() + item.extro
    }
    return props.detailData[item.key] && props.detailData[item.key].toString()
  }
}

const colorChoose = (item) => {
  if (item.id === 2) {
    return 'text-green'
  } else if (item.id === 3) {
    return props.detailData.direction === 'buy' ? 'text-green' : 'text-red'
  }
}

const handleFinish = () => {
  emits('timeEnd', props.detailData.order_no)
}

const handleInit = () => {
  totalTime.value = props.detailData.time_num
  arr.value = []
  if (props.detailData.remain_time != undefined) {
    arr.value = props.detailData.remain_time.split(':');
  }
  remain_time.value = arr.value.length && arr.value[0] / 1 * 3600 + arr.value[1] / 1 * 60 + arr.value[2] / 1

  rate.value = ((remain_time.value) / totalTime.value) * 100

}

const timeChange = (time) => {
  let temp = time.days * 86400 + time.hours * 3600 + time.minutes * 60 + time.seconds
  rate.value = ((temp) / totalTime.value) * 100
}

const onClose = () => { /// 关闭
  emits('close')
}

</script>
<style lang="scss" scoped>
.popup-delivery {
  font-size: 16px;
  background: $main_background;
}

.disableBtn {
  background: $light-grey !important;
}


:deep(.van-circle__hover) {
  stroke: $btn_main
}


.border-b-color {
  border-bottom: 1px solid $border_color;
}

.van-count-down {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  font-size: 20px;
}
</style>
