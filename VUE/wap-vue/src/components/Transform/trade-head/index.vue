<template>
  <!-- 永续合约，交割合约公共头部 -->
  <div>
    <div class="contract-header text-28">
      <div class="pl-8 pr-8">
        <div class="flex justify-between pt-11">
          <div class="flex items-center">
            <!-- 没有切换模式的 -->
            <van-icon v-if="isReturn" name="arrow-left" size="20" @click="backPath()" />
            <template v-if="!isChange">
              <img :src="handleImage(leftIcon)" alt="convert-img" class="ml-10 w-9 h-9" @click="onSidebar" />
            </template>
            <!-- 在切换模式下 -->
            <template v-if="isChange">
              <template v-if="isNight">
                <van-icon name="arrow-left" size="20" @click="backPath()" />
                <img src="./white-convert.png" alt="convert-img" class="ml-10 w-9 h-9" @click="onSidebar" />
              </template>
              <template v-else>
                <van-icon name="arrow-left" size="20" @click="backPath()" />
                <!-- <img src="../../../assets/image/icon_back.png" class="w-10 h-10 back mr-50" alt="" @click="backPath()"> -->
                <img :src="handleImage(leftIcon)" alt="convert-img" class="ml-10 w-9 h-9" @click="onSidebar" />
              </template>
            </template>
            <div class="flex flex-col pl-21" @click="onSidebar">
              <template v-if="!isChange">
                <div class="text-36 textColor ml-5">{{ symbol.toUpperCase() || '--' }}/USDT</div>
              </template>
              <template v-if="isChange">
                <div class="text-36" :class="isNight ? 'text-white' : 'text-black'">{{ symbol.toUpperCase() || '--'
                }}/USDT</div>
              </template>
            </div>
            <div v-if="range" class="pl-8" :class="{ 'text-green': range > 0, 'text-red': range <= 0 }">{{
              range > 0 ? '+' : '' }}{{ range || '--' }}%</div>
          </div>
          <div class="flex items-center" v-if="islevel">
            <img src="../../../assets/image/icon-star_active.png" class="w-8 h-8 mr-2 ml-4" @click="openCurrency"
              v-if="isCollect" />
            <img v-else src="../../../assets/image/icon-star.png" class="w-8 h-8 mr-2 ml-4" @click="openCurrency" />
          </div>
          <img v-if="isTrade" src="../../../assets/image/kline.png" class="w-11 h-11 right" alt=""
            @click="$router.push(`/cryptos/trendDetails/${symbol}?kineType=trade`)">
        </div>
      </div>
      <!-- 左侧边弹出菜单 -->
      <van-popup class="popup" round v-model:show="show" close-icon-position="top-left" position="left" @closed="onClose">
        <div class="pl-10 pr-10 text-28">
          <div class="flex justify-between mb-10 mt-13">
            <div class="flex items-center text-grey">
              <div class="mr-3 text-28">{{ $t('名称') }}</div>
            </div>
            <div class="flex text-grey">
              <div class="flex items-center">
                <div class="text-28">{{ $t('最新价格') }}</div>
              </div>
              <div class="flex items-center">
                <div class="mr-3 text-28">/24H{{ $t('涨跌') }}</div>
              </div>
            </div>
          </div>
          <div class="flex justify-between mb-12" v-for="item in list" :key="item.name" @click="onRoute(item)">
            <div>
              <div class="font-bold textColor text-28">{{ item.name || '--' }}</div>
              <div v-if="!kineType" class="text-grey mt-2 text-28">{{ title }}</div>
            </div>
            <div class="text-right">
              <div class="textColor text-28">{{ item.close }}</div>
              <div class="mt-2 text-28" :class="item.change_ratio > 0 ? 'text-green' : 'text-red'">
                {{ item.change_ratio }}%</div>
            </div>
          </div>
        </div>
      </van-popup>
    </div>

    <add-currency ref="addCurrencyRef" @updateItem="getIsItemHasAddGlobal" :isCollect="isCollect"></add-currency>
  </div>
</template>
    
<script>
import { setStorage, handleImage } from '@/utils/utis.js'
import { Popup, showSuccessToast } from "vant";
import { mapGetters } from "vuex";
import { _getHomeList, _collect, _deleteCollect, _checkIsInCollect } from "@/service/home.api";
import { useRouter } from "vue-router";
import addCurrency from '@/components/add-currency/index.vue'
import { _isItemHasAdd, _isItemHasAddGlobal } from '@/service/quotes.api'
import { themeStore } from '@/store/theme';
const thStore = themeStore()
const THEME = thStore.theme
export default {
  name: "contractHeader",
  props: {
    tabIndex: {
      type: String,
      default: null
    },
    backFunc: {
      type: Function,
      default: null
    },
    symbol: {
      type: String,
      default: ''
    },
    range: {
      type: String,
      defalult: ''
    },
    islevel: {
      type: Boolean,
      defalult: false
    },
    title: {
      type: String,
      defalult: '--'
    },
    isNight: {
      type: Boolean,
      defalult: false
    },
    isChange: {
      type: Boolean,
      defalult: false
    },
    isTrade: {
      type: Boolean,
      defalult: false
    },
    kineType: {
      type: String,
      defalult: ''
    },
    isReturn: {
      type: Boolean,
      defalult: false
    },
    symbolName: {
      type: String,
      defalult: ''
    },
  },
  components: {
    [Popup.name]: Popup,
    addCurrency
  },
  computed: {
    ...mapGetters({ coinList: 'home/coinList' }),
  },
  data() {
    const arr = []
    for (let i = 0; i < 10; i++) {
      arr.push({ id: i })
    }
    return {
      THEME,
      //   selectIndex2:this.selectIndex,
      show: false,
      timeout: null,
      collected: '0',
      // title: '',
      list: arr, //[
      // { name:"BTC/USDT",close:"22042.28",change_ratio:"2.21"},
      // { name:"XTZ/USDT",close:"1.568",change_ratio:"-7.1"},
      // { name:"ADA/USDT",close:"0.493085",change_ratio:"-4.08"},
      //]
      isCollect: false,
      queryType: 'cryptos',
      leftIcon: new URL(`../../../assets/theme/${thStore.theme}/image/black-convert.png`, import.meta.url),
    }
  },
  watch: {
    symbol(val) {
      this.getIsItemHasAddGlobal()
      if (this.islevel) {
        this.$emit('changeLine', true)
      }
      if (this.islevel && this.$store.state.user.userInfo.token) {
        let symbol = this.$route.params.symbol;
        _checkIsInCollect(symbol).then(data => {
          const { status } = data
          this.collected = status
        })
      }
    }
  },
  created() {
    // this.coins = this.coinList.map(item => item.symbol)
    // console.log('this.coins', this.coins)
    if (this.islevel && this.$store.state.user.userInfo.token) {
      let symbol = this.$route.params.symbol;
      // _checkIsInCollect(symbol).then(data => {
      //   const { status } = data
      //   this.collected = status
      // })
    }
    if (this.$route.query.type) {
      this.queryType = this.$route.query.type
    }
  },
  methods: {
    handleImage,
    handleImage(url) {
      return new URL(url, import.meta.url).href
    },
    onRoute(item) {
      if (this.islevel) {
        if (this.$route.params.symbol !== item.symbol) {
          this.$router.push(`/cryptos/trendDetails/${item.symbol}?type=${this.$route.query.type}`)
          // alert(this.kineType)
          if (this.kineType) {
            setStorage('tradeSymbol', item.symbol)
          } else {
            setStorage('symbol', item.symbol)
          }
          this.$emit('update-coin', item.symbol)
          this.$forceUpdate()
        }

      } else {
        if (this.$route.params.symbol !== item.symbol) {
          this.$router.push(`/cryptos/trade/${item.symbol}`)
          setStorage('tradeSymbol', item.symbol)
          this.$emit('update-coin', item.symbol)
          this.$forceUpdate()
        }
      }
      this.show = false
    },
    onSidebar() { // 侧边栏打开
      // console.log(this.userInfo)
      this.coins = this.coinList.map(item => item.symbol)
      this.show = true
      this.fetchList()
    },
    fetchList() { // 获取行情
      _getHomeList(this.coins.join(',')).then(list => {
        // console.log(list)
        this.list = list
        if (this.timeout) {
          clearTimeout(this.timeout)
          this.timeout = null
        }
        this.timeout = setTimeout(() => {
          this.fetchList()
        }, 1000)
      })
    },
    onClose() {
      if (this.timeout) {
        clearTimeout(this.timeout)
        this.timeout = null
      }
    },
    jump() {
      this.$router.push(`/cryptos/trendDetails/${this.symbol}?type=${this.$route.query.type}`)
    },
    changeTab(index) {
      this.$emit('tab', index)
      // this.selectIndex2 = index;
    },
    goBack() {
      if (this.backFunc) {
        this.backFunc()
      } else {
        this.$router.go(-1);
      }
    },
    goRouter(params) {
      this.$router.push(params);
    },
    onCollect() { // 收藏，取消收藏
      let _api = _collect
      if (this.collected === '1') {
        _api = _deleteCollect
      }
      _api(this.symbol).then((res) => {
        if (this.$store.state.user.userInfo.token) {
          this.collected = this.collected === '1' ? '0' : '1'
          if (this.collected === '1') {
            showSuccessToast(this.$t('收藏成功'))
          } else {
            showSuccessToast(this.$t('取消收藏'))
          }
        }
      })
    },
    // goHistory() {
    //   const url = ((this.tabIndex * 1) == 1) ? 'perpetualHistory' : 'deliveryContractHistory'
    //   this.$router.push({
    //     path: `/${url}?symbol=${this.symbol}`
    //   });
    // },
    changeBg() {
      this.$emit('changeNight', !this.isNight)
    },
    backPath() {
      if (this.$route.query?.from === 'trade') {
        this.$router.push('/trade/index?tabActive=1')
      } else if (this.$route.query.isOptional == 1) {
        this.$router.push('/optional/index')
      }
      // else if (this.$route.query.isOptional == 2){
      //   this.$router.push('/optional/search')
      // }
      else {
        this.$router.push('/quotes/index?tabActive=1')
      }
    },
    //打开自选弹窗
    openCurrency() {
      this.$refs.addCurrencyRef.openCurrency(this.symbol)
    },
    //判断是否加入收藏
    getIsItemHasAddGlobal() {
      let obj = {
        symbol: this.symbol
      }
      _isItemHasAddGlobal(obj).then((data) => {
        this.isCollect = data
      })
    }
  },
}

</script>
    
<style lang="scss" scoped>
#cryptos {
  // .contract-header{
  //     background-color:#F5F5F5;
  // }

  .wallet-background {
    background-color: #E8E8E8;
  }

  .select-active {
    background-color: white;
    color: black;
  }

  .no-select {
    background-color: #e8e8e8;
    color: $text_color1;
    ;
  }

  // 弹出层样式

  .night {
    color: $text_color;
  }

  .h-40 {
    height: 40px !important;
  }
}

:deep(.contract-header .van-popup) {
  width: 670px;
  height: 100%;
}
</style>
    