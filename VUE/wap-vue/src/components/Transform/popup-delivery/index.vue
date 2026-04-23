<template>
  <div id="cryptos">
    <div class="popup-delivery text-28 rounded-2xl overflow-hidden">
      <div class="border-b-color pt-14 pb-9 relative textColor">
        <h1 class="text-38 px-8">{{ detailData.name }} {{ $t('交割') }}</h1>
        <img src="@/assets/image/icon-close.png" class="w-9 h-9 absolute right-10 top-10" @click="onClose" />
      </div>
      <div class="flex justify-center mt-11" v-if="!(detailData.state === 'created')">
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
      <div class="mt-11 text-50 text-center"
        :class="{ 'text-green': detailData.profit_state === '1', 'text-red': detailData.profit_state === '0' }" v-else>{{
          $t('盈亏') }}<span>{{ detailData.profit_state === '1' ? '+' : '' }}{{ detailData.profit }}</span> {{ getCurrentUnit() }}</div>
      <ul class="flex flex-col pb-16 textColor">
        <li v-for="item in listItem" :key="item.id" class="flex justify-between px-8 mt-7">
          <p class="text-grey text-30">{{ item.text }}</p>
          <p class="text-30 " v-if="item.key == 'close_price'" :class="{
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
          <p class="text-30 " :class="colorChoose(item)" v-else>{{ handleBuyWord(item) }}</p>
        </li>
      </ul>
      <div v-if="showBtns" class="flex justify-between px-10 pb-16">
        <p :class="{ 'btnMain': detailData.state !== 'created', 'bgDark': detailData.state === 'created' }"
          class="w-64 h-20 rounded-lg flex justify-center items-center mr-5 text_color" @click="onClose">{{ $t('关闭') }}
        </p>
        <p @click="continueToBuy"
          :class="{ 'btnMain': detailData.state === 'created', 'bgDark': detailData.state !== 'created', 'disableBtn': disabled }"
          class="w-96 h-20 rounded-lg flex justify-center items-center text_color">{{ $t('继续下单') }}</p>
      </div>
    </div>
  </div>
</template>
<script>
import { Circle, CountDown } from 'vant';
import { currentUnit } from '@/utils/coinUnit.js'
export default {
  name: 'PopupDelivery',
  components: {
    [Circle.name]: Circle,
    [CountDown.name]: CountDown
  },
  props: {
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
      default: '0.00'
    },
  },
  // watch: {
  //   detailData: {
  //     deep: true,
  //     handler(newVal) {
  //       this.ended = newVal.state === 'created';
  //     },
  //   }
  // },
  computed: {
    timeFormat() {
      let res
      if (!this.arr.length) {
        return '--'
      }
      if (this.arr[0] / 1 > 0) {
        res = 'DDd HHh mmm sss'
      } else {
        if (this.arr[1] / 1 > 0) {
          res = 'mmm sss'
        } else {
          res = 'sss'
        }
      }
      return res
    },
    text() {
      return this.currentRate + 's';
    },
    listItem() {
      return [
        { id: 1, text: this.$t('购买价'), key: 'open_price' },
        { id: 2, text: this.detailData.state !== 'created' ? this.$t('现价') : this.$t('结算价格'), key: 'close_price' },
        { id: 3, text: this.$t('方向'), key: 'direction' },
        { id: 4, text: this.$t('数量'), key: 'amount', extro: this.getCurrentUnit() },
        { id: 5, text: this.$t('交割时间'), key: 'settlement_time' }
      ]
    }
  },
  created() {
    this.routeType = this.$route.query.type
    if (!(this.detailData.state === 'created')) {// 交割已完成
      this.handleInit()
    }
  },
  beforeUnmount() {
    clearInterval(this.interval)
    console.log('before destroy..')
  },
  data() {
    return {
      totalTime: '', // 合约总时长
      remain_time: 0,
      arr: [],
      currentRate: 0, // 当前进度
      interval: null,
      rate: 0, // 目标进度
      // listItem: [
      //     {id: 1, text: '购买价', key: 'open_price'},
      //     {id: 2, text: '现价', key: 'close_price'},
      //     {id: 3, text: '方向', key: 'direction'},
      //     {id: 4, text: '数量', key: 'amount', extro: 'USDT'},
      //     {id: 5, text: '交割时间', key: 'settlement_time'}
      // ]
      routeType: 'cryptos'
    }
  },
  methods: {
    getCurrentUnit() {
      return currentUnit(this.routeType)
    },
    continueToBuy() {
      if (this.detailData.state !== 'created') {
        return
      }
      this.$emit('continueToBuy', this.detailData)

      //this.$router.push(`/trendDetails/${symbol}`)
    },
    handleBuyWord(item) {
      if (item.id === 3) {
        if (this.detailData.direction === 'buy') {
          return this.$t('开多')
        } else {
          return this.$t('开空')
        }
      } else if (item.key === 'settlement_time') {
        return this.detailData.time_num + this.detailData.time_unit
      } else {
        if ('extro' in item) {
          return this.detailData[item.key] && this.detailData[item.key].toString() + item.extro
        }
        return this.detailData[item.key] && this.detailData[item.key].toString()
      }
    },
    colorChoose(item) {
      if (item.id === 2) {
        return 'text-green'
      } else if (item.id === 3) {
        return this.detailData.direction === 'buy' ? 'text-green' : 'text-red'
      }
    },
    handleFinish() {
      this.$emit('timeEnd', this.detailData.order_no)
    },
    handleInit() {
      // let a =  new Date(this.detailData.open_time).getTime()
      // let b =  new Date(this.detailData.settlement_time).getTime()
      // this.totalTime = (b - a) / 1000   苹果不兼容
      this.totalTime = this.detailData.time_num
      this.arr = []
      if (this.detailData.remain_time != undefined) {
        this.arr = this.detailData.remain_time.split(':');
      }
      //this.arr = this.detailData?.remain_time?.split(':') || []
      // 168:50:50 = 后台返回的时间格式
      // 转化为秒
      this.remain_time = this.arr.length && this.arr[0] / 1 * 3600 + this.arr[1] / 1 * 60 + this.arr[2] / 1

      this.rate = ((this.remain_time) / this.totalTime) * 100

    },
    timeChange(time) {
      let temp = time.days * 86400 + time.hours * 3600 + time.minutes * 60 + time.seconds
      this.rate = ((temp) / this.totalTime) * 100
    },
    onClose() { /// 关闭
      this.$emit('close')
    }
  }
}
</script>
<style lang="scss" scoped>
#cryptos {
  .disableBtn {
    background: $bg_dark !important;
  }

  .popup-delivery {

    :deep(.van-circle__hover) {
      stroke: $color_main
    }
  }

}
</style>
