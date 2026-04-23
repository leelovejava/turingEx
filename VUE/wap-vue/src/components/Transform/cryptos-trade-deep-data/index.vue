<template>
  <div class="deep-data">
    <div class="flex justify-between text-grey text-24">
      <div>
        <div>{{ $t('价格') }}</div>
        <div class="mt-1">(USDT)</div>
      </div>
      <div class="text-right items-end justify-end">
        <div class="">{{ $t('数量') }}</div>
        <div class="mt-1">({{ symbol.toUpperCase() || '--' }})</div>
      </div>
    </div>

    <div v-if="showType == 0 || showType == 2" class="flex justify-between pt-1 text-26" v-for="(item, index) in redData"
      :key="item + index" @click="onPrice(item.price)" :style="{
        'background': `linear-gradient(to right,${THEME == 'dark' ? '#131A2E' : '#fff'} 0%` +
          (item.amount / greenData[greenData.length - 1].amount) * 100 + '%,rgba(246,70,93,.1) ' +
          (item.amount / greenData[greenData.length - 1].amount) * 100 + '%,rgba(246,70,93,.1) 100%)'
      }">
      <div class="text-red ">{{ item.price || '--' }}</div>
      <div class="text-right textColor" v-if="symbol == 'shib'">{{ fixDate(item.amount, $i18n) || '--' }}</div>
      <div class="text-right textColor" v-else>{{ item.amount || '--' }}</div>
    </div>
    <div class="text-red pt-3 text-34 font-bold text-center">
      {{ price || '--' }}
    </div>
    <div class="pb-3 text-20 text-center">
      ≈ {{ ((price *
        currency.rate).toFixed(price.toString().split('.')[1] ?
          price.toString().split('.')[1].length : 2)) || '--' }}
    </div>
    <div v-if="showType == 0 || showType == 1" class="flex justify-between pt-1 text-26"
      v-for="(item, index) in greenData" :key="index" @click="onPrice(item.price)" :style="{
        'background': `linear-gradient(to right,${THEME == 'dark' ? '#131A2E' : '#fff'} 0%` +
          (item.amount / greenData[greenData.length - 1].amount) * 100 + '%,rgba(94,186,137,.1) ' +
          (item.amount / greenData[greenData.length - 1].amount) * 100 + '%,rgba(94,186,137,.1) 100%)'
      }">
      <div class="text-green">{{ item.price || '--' }}</div>
      <div class="text-right textColor" v-if="symbol == 'shib'">{{ fixDate(item.amount, $i18n) || '--' }}</div>
      <div class="text-right textColor" v-else>{{ item.amount || '--' }}</div>
    </div>
  </div>
</template>

<script>
import { WS_URL } from '@/config'
import { fixDate } from "@/utils/index";
import { _getHomeList } from '@/service/home.api'
import { mapGetters } from 'vuex'
import { themeStore } from '@/store/theme';
const thStore = themeStore()
const THEME = thStore.theme
let DEEP_LENGTH = 6

export default {
  name: 'CryptosTradeDeepData',
  props: {
    symbol: {
      type: String,
    },
    price: {
      type: [Number, String],
      default: '0.00'
    },
    showType: {
      type: Number,
      default: 0
    },
    selectValue: {
      type: Number,
    }
  },
  data() {
    const init = []
    for (let i = 0; i < DEEP_LENGTH; i++) {
      init.push({ amount: '', price: '' })
    }
    return {
      THEME,
      fixDate,
      greenData: init,
      redData: init,
      socket: null,
    }
  },
  mounted() {
    // this.startDeepSocket()
    if (this.showType == 1 || this.showType == 2) {
      DEEP_LENGTH = 12
    } else {
      DEEP_LENGTH = 6
    }
    this.getHomeList()
  },
  watch: {
    symbol(val) {
      this.startDeepSocket()
      this.getHomeList()
    },
    showType(val) {
      if (val == 1 || val == 2) {
        DEEP_LENGTH = 12
      } else {
        DEEP_LENGTH = 6
      }
    },
  },
  computed: {
    ...mapGetters('home', ['currency']),
  },
  methods: {
    getHomeList() {
      _getHomeList(this.symbol).then((res) => {
        let numberText = res[0].close.toString()
        let numberLength = (numberText.substring(numberText.indexOf('.') + 1, numberText.length)).length

        let arry = []
        let str = '0.'
        for (let j = 0; j < numberLength - 1; j++) {
          str = str + '0'
          arry.push(str)
        }
        let newArry = []
        arry.map((item) => {
          let obj = {
            name: item + '1',
            value: (item.substring(item.indexOf('.') + 1, item.length)).length + 1
          }
          newArry.push(obj)
        })
        if (numberLength <= 2) {
          let obj = [
            {
              name: '0.1',
              value: 1,
            },
            {
              name: '1',
              value: -1,
            },
            {
              name: '10',
              value: -2,
            },
            {
              name: '100',
              value: -3,
            }
          ]
          newArry = newArry.concat(obj)
        }
        if (this.symbol == 'shib') {
          newArry = newArry.slice(-4)
        }
        let dataObj = {
          arry: newArry,
          numberLength: numberLength
        }
        this.$emit('getList', dataObj)
      })
    },
    startDeepSocket() { // 开启socket链接 
      this.closeSocket()
      this.socket = new WebSocket(`${WS_URL}/3/${this.symbol}`)
      this.socket.onmessage = (evt) => {
        const { data } = evt
        const { code, data: _data } = JSON.parse(data)
        if (code / 1 === 0) {
          this.handleDeep(_data)
        }
      }
    },
    handleDeep(data) { // 格式化数据
      this.deepData = data
      if (this.selectValue >= 1) {
        this.deepData.asks.map((item) => {
          item.price = parseFloat(item.price).toFixed(this.selectValue)
        })
        this.deepData.bids.map((item) => {
          item.price = parseFloat(item.price).toFixed(this.selectValue)
        })
      } else {
        if (this.selectValue == -1) {
          this.deepData.asks.map((item) => {
            item.price = item.price.substring(0, item.price.indexOf('.'))
          })
          this.deepData.bids.map((item) => {
            item.price = item.price.substring(0, item.price.indexOf('.'))
          })
        }
        if (this.selectValue == -2) {
          this.deepData.asks.map((item) => {
            item.price = item.price.substring(0, item.price.indexOf('.') - 1) + '0'
          })
          this.deepData.bids.map((item) => {
            item.price = item.price.substring(0, item.price.indexOf('.') - 1) + '0'
          })
        }
        if (this.selectValue == -3) {
          this.deepData.asks.map((item) => {
            item.price = item.price.substring(0, item.price.indexOf('.') - 2) + '00'
          })
          this.deepData.bids.map((item) => {
            item.price = item.price.substring(0, item.price.indexOf('.') - 2) + '00'
          })
        }
        // this.selectValue
      }
      const { asks, bids } = data
      //获取小数位数
      this.redData = asks.sort((a, b) => b.price - a.price).slice(0, DEEP_LENGTH)
      this.greenData = bids.sort((a, b) => b.price - a.price).slice(0, DEEP_LENGTH)

    },
    onPrice(price) {
      this.$emit('price', price)
    },
    closeSocket() {
      this.socket && this.socket.close()
      this.socket = null
    },
  },
  activated() {
    this.startDeepSocket()
  },
  deactivated() {
    this.closeSocket()
  },
  beforeUnmount() { //
    this.socket && this.socket.close()
    this.socket = null
  }

}    
</script>
<style lang="scss" scoped></style>