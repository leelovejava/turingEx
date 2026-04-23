<template>
  <div class="deep-data">

    <div class="flex justify-between text-22 orderbook buy" v-for="(item, index) in redData" :key="item + index"
      @click="onPrice(item.price)" :style="{
        'background': `linear-gradient(to right,${THEME == 'dark' ? '#131A2E' : '#ffffff'} 0%` +
          (item.amount / greenData[greenData.length - 1].amount) * 100 + '%,rgba(246,70,93,.1) ' +
          (item.amount / greenData[greenData.length - 1].amount) * 100 + '%,rgba(246,70,93,.1) 100%)'
      }">
      <div class="text">{{ $t('卖出') }}{{ 5 - index }}</div>
      <div>&nbsp;&nbsp;</div>
      <div class="justify-between flex flex-1">
        <div class="text-red">{{ formatMoney(item.price) }}</div>
        <div class="text-right textColor">{{ formatMoney(item.amount) }}</div>
      </div>
    </div>
    <div class="text-red pt-2 pb-2 text-24 font-bold text-center middle-box">
      <div class="rect-box">
        <div class="red"></div>
        <div class="green"></div>
      </div>
    </div>
    <div class="flex text-22 orderbook sell" v-for="(item, index) in greenData" :key="index" @click="onPrice(item.price)"
      :style="{
        'background': `linear-gradient(to right,${THEME == 'dark' ? '#131A2E' : '#ffffff'} 0%` +
          (item.amount / greenData[greenData.length - 1].amount) * 100 + '%,rgba(94,186,137,.1) ' +
          (item.amount / greenData[greenData.length - 1].amount) * 100 + '%,rgba(94,186,137,.1) 100%)'
      }">
      <div class="text">{{ $t('买入') }}{{ index + 1 }}</div>
      <div>&nbsp;&nbsp;</div>
      <div class="justify-between flex flex-1">
        <div class="text-green">{{ formatMoney(item.price) }}</div>
        <div class="text-right textColor">{{ formatMoney(item.amount) }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { WS_URL } from '@/config'
import { fixDate, formatMoney } from "@/utils";
import { themeStore } from '@/store/theme'
const thStore = themeStore()
const DEEP_LENGTH = 5
const THEME = thStore.theme

export default {
  name: 'TradeDeepData',
  props: {
    symbol: {
      type: String,
    },
    price: {
      type: [Number, String],
      default: '0.00'
    },
    isAddBlank: {
      type: Boolean,
      default: false
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
      formatMoney,
      greenData: init,
      redData: init,
      socket: null,
    }
  },
  mounted() {
    // this.startDeepSocket()
  },
  watch: {
    symbol(val) {
      this.startDeepSocket()
    }
  },
  methods: {
    startDeepSocket() { // 开启socket链接
      this.closeSocket()
      this.socket = new WebSocket(`${WS_URL}/3/${this.symbol}`)
      this.socket.onmessage = (evt) => {
        const { data } = evt
        const { code, data: _data } = JSON.parse(data)
        if (code / 1 === 0) {
          this.handleDeep(_data)
          const newPrice = (_data?.bids && _data?.bids[0].price) || '0'
          this.$emit('newPrice', newPrice)
        }
      }
    },
    handleDeep(data = {}) { // 格式化数据
      this.deepData = data
      const { asks = [], bids = [] } = data || {}
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
<style lang="scss">
.orderbook {
  line-height: 20px;
  margin-bottom: 6px;
}

.middle-box {
  .rect-box {
    display: flex;
    width: 100%;
    height: 7px;
    background: $red;
  }

  .red {
    width: 50%;
  }

  .green {
    width: 50%;
    background: url('../../assets/image/quotes/g-rectangle.png') no-repeat;
  }
}

.sell {
  color: $red;
}

.buy {
  color: $green;
}

.text {
  color: $text_color;
}
</style>