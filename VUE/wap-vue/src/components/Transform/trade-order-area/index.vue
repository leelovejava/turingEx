<template>
  <div class="flex flex-col flex-1 text-28">
    <div class="flex items-center h-16 tabBackground text-grey">
      <p class="text-28 flex-1 flex items-center justify-center h-16 buy-item"
        :class="currentType == 'open' ? 'open' : ''" @click="changeTab('open')">{{ $t('买入') }}</p>
      <p class="text-28 flex-1 flex items-center justify-center h-16 buy-item"
        :class="currentType == 'close' ? 'close' : ''" @click="changeTab('close')">{{ $t('卖出') }}</p>
    </div>

    <div class="mt-5 mb-5 inputBackground" style="position:relative;">
      <div class="flex justify-between  items-center w-full h-18" @click="selectBtn">
        <!-- <img src="@/assets/image/public/warn.png" alt="warn-icon" class="w-25 h-25 pl-20"/> -->
        <div class="pl-4 textColor" style="width:80%;">{{ title }}</div>
        <img src="@/assets/image/public/grey-select.png" alt="select-icon" class="w-5 h-2 pr-5" />
      </div>
      <div class="option-box" v-if="isShow">
        <div class="text-30" v-for="item in selectData" :key="item.type" @click="selectItem(item)">{{ item.title }}
        </div>
      </div>
    </div>
    <div class="h-18 inputBackground mb-6 flex justify-center px-4 textColor2 items-center">
      <input placeholder="" class="targetPrice w-full h-18 border-none" v-model="form.price" :disabled="type / 1 === 1" />
      <span>USDT</span>
    </div>
    <div class="h-18 inputBackground mb-6 flex justify-center px-4 textColor2 items-center">
      <span>{{ title }}</span>
    </div>
    <div class="flex total-list">
      <div class="total-div" :class="[!isTotal ? 'active-bg' : '']" @click="checkIsTotal(false)">
        {{ $t('数量') }}
      </div>
      <div class="total-div" :class="[isTotal ? 'active-bg' : '']" @click="checkIsTotal(true)">
        {{ $t('总额') }}
      </div>
    </div>
    <div v-if="!isTotal" class="h-18 inputBackground mb-9 flex justify-center px-4 items-center">
      <input :placeholder="$t('数量')" class=" w-full h-18 border-none textColor text-28" v-model="form.volume"
        @input="onInput" />
      <span class="textColor text-28">{{ symbolName.toLocaleUpperCase() }}</span>
    </div>
    <div v-if="isTotal" class="h-18 inputBackground  mb-9 flex justify-center px-4 items-center">
      <input :placeholder="$t('总额')" class=" w-full h-18 border-none textColor text-28" v-model="form.total"
        @input="onInputTotal" />
      <span class="textColor text-28">{{ 'USDT' }}</span>
    </div>
    <div class="text-24 w-full flex justify-between items-center textColor1">
      <!-- <span class="flex-1 text-left">0%</span> -->
      <span class="flex-1 tab-item" :class="[percentageVal == 25 ? 'select-active' : '']"
        @click="exchangeVal(25)">25%</span>
      <span class="flex-1 tab-item" :class="[percentageVal == 50 ? 'select-active' : '']"
        @click="exchangeVal(50)">50%</span>
      <span class="flex-1 tab-item" :class="[percentageVal == 75 ? 'select-active' : '']"
        @click="exchangeVal(75)">75%</span>
      <span class="flex-1 tab-item" :class="[percentageVal == 100 ? 'select-active' : '']"
        @click="exchangeVal(100)">100%</span>
    </div>
    <div class="flex justify-between items-center mt-10 mb-10">
      <div class="flex flex-col text-24">
        <p class="text-grey" v-if="this.currentType === 'open'">{{ $t('可用') }}<span class="textColor ml-2">
            {{ initOpen.volume }}&nbsp;USDT</span>
        </p>
        <p class="text-grey" v-else>{{ $t('可卖') }}<span class="textColor ml-2">{{ initClose.volume }}&nbsp;{{
          symbolName.toLocaleUpperCase() }}</span></p>
      </div>
      <van-icon name="add-o" @click="$router.push('/cryptos/exchangePage')" class="text-30 add-icon" />
      <!-- <img @click="$router.push('/exchange/exchangePage')" src="@/assets/image/public/switch.png" class="w-24 h-24" /> -->
    </div>
    <div style="" class="w-full h-20 flex justify-center text-white text-center rounded buyandSell mt-18 items-center"
      :class="currentType === 'open' ? 'bg-green' : 'bg-red'" @click="order()">
      {{ currentType == 'open' ? $t('买入') : $t('卖出') }}
    </div>

  </div>
</template>

<script>
import config from "@/config";
import { Popup, Tabs, Tab, showFailToast, showSuccessToast } from 'vant';
import 'vue-slider-component/theme/default.css'
import { _getBalance } from "@/service/user.api.js";
import TradeApi from "@/service/trading.js";
import { mapGetters } from "vuex";
export default {
  name: "perpetualPosition",
  components: {
    [Popup.name]: Popup,
    [Tabs.name]: Tabs,
    [Tab.name]: Tab
  },
  props: {
    symbol: {
      type: String,
      default: ''
    },
    symbolName: {
      type: String,
      default: ''
    },
    price: {
      type: [Number, String],
      default: '0.00'
    },
    initOpen: {
      type: Object,
      default() {
        return {}
      }
    },
    initClose: {
      type: Object,
      default() {
        return {}
      }
    },
  },
  computed: {
    ...mapGetters('user', ['userInfo'])
  },
  activated() {
    this.selectData = [
      { title: this.$t('市价委托'), type: '1' },
      { title: this.$t('限价委托'), type: '2' },
    ]
    this.title = this.selectData[0].title
    this.type = this.selectData[0].type
    this.form.order_price_type = 'opponent'
  },
  watch: {
    initOpen: { // 处理滚动条初始值
      deep: true,
      handler(val) {
        if (val.volume / 1 > 0) {
          this.handleInitSliderOption()
        }
      }
    },
    initClose: { // 处理滚动条初始值
      deep: true,
      handler(newVal, oldVal) {
        if (newVal.volume / 1 > 0) {
          this.handleInitSliderOption(true)
        }
      }
    },
    price(val) {
      if (this.type === '1') { // !this.focus &&
        this.form.price = val
      }
    },
  },
  data() {
    return {
      options: config.sliderOptions,
      value: 0,
      isShow: false,
      title: this.$t('市价委托'),
      selectData: [],
      form: {
        volume: '',
        session_token: '',
        symbol: '', // 币种
        price: '',
        total: '',
        order_price_type: 'opponent', // 市价or限价
      },
      type: "1",//选中市价或限价类型
      currentType: "open", //开仓类型
      interval: 0.001,
      marks: (val) => val % 25 === 0,
      isTotal: false,
      percentageVal: 0
    }
  },
  mounted() {
    this.selectData = [
      { title: this.$t('市价委托'), type: '1' },
      { title: this.$t('限价委托'), type: '2' },
    ]
    this.title = this.selectData[0].title
    this.type = this.selectData[0].type
    this.form.order_price_type = 'opponent'
  },
  methods: {
    checkIsTotal(val) {
      this.isTotal = val
      this.percentageVal = 0
      this.form.total = ''
      this.form.volume = ''
    },
    exchangeVal(val) {
      if (!this.userInfo.token) {
        this.$router.push('/login')
        return
      }
      if (this.initOpen.volume > 0 && this.form.price) {
        if (!this.isTotal) {
          if (this.currentType == 'open') {
            let sum = (parseFloat(this.initOpen.volume) * (val / 100) / parseFloat(this.form.price))
            this.form.volume = Math.floor(sum * 100000) / 100000
          } else {
            let sum = parseFloat(this.initClose.volume)
            this.form.volume = (Math.floor(sum * (val / 100) * 100000)) / 100000
          }
        } else {
          if (this.currentType == 'open') {
            this.form.total = this.initOpen.volume * (val / 100)
          } else {
            this.form.total = Math.floor((this.initClose.volume * (val / 100) * parseFloat(this.form.price)) * 1000) / 1000
          }
        }
        this.percentageVal = val
      }
    },
    handleInitSliderOption(volume) {
      if (!volume) { // 金额是否需要变动
        this.form.volume = ''
      }
      console.log(this.initOpen.volume)
      let vol
      if (this.currentType === 'open') {
        vol = this.initOpen.volume / 1
      } else {
        vol = this.initClose.volume / 1
      }
      this.options.max = Number(vol.toFixed(3))
      if (this.options.max > 0) {
        this.options.disabled = false
      } else {
        this.options.disabled = true
      }
    },
    onInput() { // 数量变化
      if (this.currentType === 'open') {
        let maxSum = (parseFloat(this.initOpen.volume) / parseFloat(this.form.price))
        console.log(this.initOpen.volume)
        console.log(this.form.price)
        console.log(maxSum)
        if (this.form.volume * 1 > maxSum / 1) {
          this.form.volume = maxSum
        }
      } else {
        if (this.form.volume * 1 / 1 > this.options.max / 1) {
          this.form.volume = this.options.max / 1
        }
      }
    },
    onInputTotal() { //总额变化
      //最大额度
      if (this.form.total > 0) {
        if (this.currentType === 'open') {
          console.log(this.initOpen.volume)
          if (this.form.total * 1 > this.initOpen.volume * 1) {
            this.form.total = this.initOpen.volume
          }
        } else {
          let sum = (parseFloat(this.form.total) / parseFloat(this.form.price)).toFixed(5)
          if (parseFloat(sum) > parseFloat(this.initClose.volume)) {
            this.form.total = (parseFloat(this.initClose.volume) * parseFloat(this.form.price)).toFixed(5)
          }
        }
      }
    },
    //价格类型下拉框切换
    selectBtn() {
      this.isShow = !this.isShow;
    },
    //选择价格类型
    selectItem(item) {
      if (item.type == 1) {
        this.form.price = this.price
      }
      this.form.order_price_type = item.type === '1' ? 'opponent' : 'limit'
      this.title = item.title;
      this.type = item.type;
      this.isShow = false;
    },
    //选择开仓类型
    changeTab(type) { // 开仓和
      this.percentageVal = 0
      this.isTotal = false
      this.form.total = ''
      console.log('changeTab', type)
      // TODO: 这里应该是换函数
      // this.form.direction = type === 'open' ? 'buy' : 'sell'
      if (this.currentType === type) {
        return
      }
      this.currentType = type;
      if (this.currentType === 'close') {
        this.$emit('ordered', 'close') // 更新平仓初始化参数
      } else {
        this.$emit('ordered', 'open') // 更新平仓初始化参数
      }
      this.handleInitSliderOption()
    },
    order() {
      if (!this.userInfo.token) {
        this.$router.push('/login')
        return
      }
      let volume = ''
      if (this.isTotal) {
        if (!this.form.total) {
          showFailToast(this.$t('请输入总额'))
          return
        }
        if (this.currentType === 'open') {
          this.form.volume = parseFloat(this.form.total).toFixed(5)
          this.form.total =  ((parseFloat(this.form.total) / parseFloat(this.form.price))).toFixed(5)
        } else {
          this.form.volume = (parseFloat(this.form.total) / parseFloat(this.form.price))
          this.form.volume = this.form.volume.toFixed(5)
        }
        volume = this.form.volume
      } else {
        if (!this.form.volume) {
          showFailToast(this.$t('请输入数量'))
          return
        }
        if (this.currentType === 'open') {
          this.form.total = this.form.volume
          volume = (parseFloat(this.form.volume) * parseFloat(this.form.price))
        } else {
          volume = this.form.volume
        }
      }
      this.form.symbol = this.$route.params.symbol
      let _order = null // api
      let emitFunc = null // 触发函数

      if (this.currentType === 'open') {
        this.form.session_token = this.initOpen.session_token
      } else {
        this.form.session_token = this.initClose.session_token
      }
      _order = this.currentType === 'open' ? TradeApi.tradeBuy : TradeApi.tradeSell
      emitFunc = this.currentType
      _order({
        volume: volume,
        session_token: this.form.session_token,
        symbol: this.form.symbol, // 币种
        price: this.form.price,
        total: this.form.total,
        order_price_type: this.form.order_price_type, // 市价or限价
      }).then((res) => {
        this.percentageVal = 0
        showSuccessToast(this.$t('操作成功'))
        this.form.volume = ''
        this.form.total = ''
        _getBalance().then(data => { // 刷新余额
          this.$store.commit('user/SET_USERINFO', { balance: data.money })
        })
        this.$emit('ordered', emitFunc)
      }).catch(() => { // 也需要重新初始化
        this.$emit('ordered', emitFunc)
      })
    },
  }

}
</script>



<style lang="scss" scoped>
.bg-f3 {
  background: $border-grey;
}

.options {
  box-shadow: 0px 0px 7px rgba(0, 0, 0, 0.3);
  border-radius: 6px;

  div {
    font-size: 22px;
    text-align: center;
    width: 140px;
    height: 60px;
    line-height: 60px;

    &.option-active {
      background-color: $light-grey;
    }
  }
}

.right-word {
  top: 50%;
  transform: translateY(-50%);
  right: 19px;
  position: absolute;
  border-radius: 6px;
}

.vue-slider {
  height: 3px !important;
  padding: 0 10px !important;
}

.vue-slider-dot {
  width: 28px !important;
  height: 28px !important;

}

.vue-slider-mark-label {
  font-size: 16px;
  margin-top: 14px !important;
}

.vue-slider-mark-label-active {
  color: $text_color1 !important;
}

.vue-slider-dot-handle {
  border: 3px solid $btn_main;
}

.vue-slider-mark {
  width: 20px !important;
  height: 20px !important;

  &:nth-child(1) {
    .vue-slider-mark-label {
      transform: translateX(-14px) !important;
    }
  }

  &:nth-child(5) {
    .vue-slider-mark-label {
      transform: translateX(-60px) !important;
    }
  }
}

// 开仓样式
.tabcon {
  // padding-bottom: 16%;
  position: relative;
  height: 80px;
}

.tab {
  position: absolute;
  left: 0px;
  right: 0px;
  top: 0px;
  bottom: 0px;
  display: flex;
  align-items: center;
  background-color: $border-grey;
  border-radius: 5px;
  overflow: hidden;
}

.tab>* {
  height: 100%;
}

.tab>img {
  margin-left: -2px;
  margin-right: -2px;
}

.tab>a {
  flex-grow: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

.open {
  background-color: $green;
  background: url(@/assets/image/public/open-bg.png) no-repeat right center;
  background-size: 100% 100%;
  color: white;
}

.close {
  background-color: $green;
  background: url(@/assets/image/public/close-bg.png) no-repeat left center;
  background-size: 100% 100%;
  color: white;
}

// 下拉
.option-box {
  position: absolute;
  left: 0;
  right: 0;
  top: 90px;
  width: 100%;
  background-color: $grey_bg;
  text-align: center;
  box-shadow: 0px 0px 3px 3px $grey_bg;
  border-radius: 4px;
  color: $text-color;
  z-index: 10;
}

.option-box>div {
  padding: 30px 0;
}

.option-box>div:hover {
  background-color: $grey_bg;
}

.num-text-color {
  color: #4C4A54;
}

.with100 {
  width: 100%;
}

.height100 {
  height: 100%;
}

.targetPrice {

  opacity: 1;
}

.inputBackground {
  input {
    background: transparent !important;
  }
}

.tab-item {
  display: flex;
  align-items: center;
  justify-content: center;
  background: $input_background;
  border-radius: 4px;
  margin-right: 10px !important;
  padding: 10px 0;
}

.total-list {
  background: transparent;

  display: flex;

  color: $text_color;

  font-size: 26px;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px !important;
  position: relative;

  .total-div {
    flex: 1;
    text-align: center;
    padding: 20px 0 !important;
  }
}

.active-bg {
  background: $input_background;
  color: $text_color;
}

.buyandSell {
  position: relative;
  z-index: 100;
}

.select-active {
  color: $color_main !important;
}
</style>

