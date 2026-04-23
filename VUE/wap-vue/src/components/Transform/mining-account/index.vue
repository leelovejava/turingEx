<template>
  <div id="cryptos">
    <div id="mining-account">
      <div style="position: relative" class="px-8 pb-24">
        <mining-exchange-input :title="$t('从')" :value1="form.volume" :actions="actions"
          @select="onSelect('symbol', $event)" @input="onInput" :type="1" @max="onMax" :coin="form.symbol"
          :coin1="form.symbol_to" :tips="[]" :iconImg1="form.iconImg1" :iconImg2="form.iconImg2" :showMax="true" />
        <div v-if="show" class="text-28 text-grey">{{ $t('可用数量') }}：<span class="textColor">{{ amountAvailable
        }}&nbsp;{{ form.symbol.toUpperCase() }}</span>
        </div>
        <div class="flex justify-center convert-box my-42 w-16 h-16">
          <img src="../../../assets/image/assets-center/convert-two.png" alt="" class="w-6 h-6" @click="onSwitch" />
        </div>
        <mining-exchange-input :title="$t('至')" :value="form.volume ? (form.volume * form.rate).toFixed(5) : ''"
          :actions="actions" @select="onSelect('symbol_to', $event)" :disabled="true" :type="2" :coin="form.symbol"
          :coin1="form.symbol_to" :tips="[]" :showMax="false" :iconImg1="form.iconImg1" :iconImg2="form.iconImg2" />
        <div class="exchange-btn w-full flex justify-center mt-28">
          <p class="h-24 btnMain mx-auto flex justify-center items-center text-white w-full" @click="onConfirm">
            {{ $t('询价') }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MiningExchangeInput from "../mining-exchange-input/index.vue";
import { Grid, GridItem, Popup, Icon, showToast } from "vant";
// import { mapState } from "vuex";
import { _exchangeRage, _getAllWallet, _initWidthdrawl, _widthdrawl, _records } from "@/service/fund.api";
import { mapGetters } from "vuex";
import { List, PullRefresh } from 'vant';
import { debounce } from '@/utils'
export default {
  name: "MiningAcccount",
  components: {
    [Grid.name]: Grid,
    [GridItem.name]: GridItem,
    [Popup.name]: Popup,
    [Icon.name]: Icon,
    MiningExchangeInput,
    [List.name]: List,
    [PullRefresh.name]: PullRefresh
  },
  props: {
    show: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    ...mapGetters('user', ['userInfo']),
    tabList() {
      return [
        { id: 1, text: this.$t('提幣') },
        { id: 2, text: this.$t('交易記錄') }
      ]
    },
    addr() {
      const account = this.userInfo.account
      return account && account.substr(0, 5) + '********' + account.substr(account.length - 5)
    },
    amountAvailable() {
      console.log(this.form.symbol)
      console.log(this.wallets)
      let res
      let dataInfo = this.wallets.find(item => item.symbol.toUpperCase() === this.form.symbol.toUpperCase())

      if (dataInfo) {
        res = dataInfo.usable
      } else {
        res = 0
      }
      return res
    },
  },
  data() {
    return {
      showPopup: false,
      activeRecord: 'exchange',
      valueTwo: 0,
      rate: 0,
      form: { // 闪兑
        symbol: 'ETH',
        symbol_to: 'USDT',
        volume: '',
        rate: 0,
        iconImg2: '',
        iconImg1: '',
        symbol_ex: '',
        symbol_to_ex: '',

      },
      interval: null,
      actions: [],
      balance: 0, // 质押余额
      page: 1,
      loading: false, // 当loading为true时，转圈圈
      finished: false,  // 数据是否请求结束，结束会先显示'已经全部加载完毕'
      noData: false,// 如果没有数据，显示暂无数据
      wallets: [], // 钱包列表
      active: 0,
    }
  },
  created() {
    let obj = {
      symbolType: 'cryptos'
    }
    _getAllWallet(obj).then(data => {
      this.actions = []
      data.extends.map(item => {
        this.actions.push({ symbol: item.symbol, name: item.symbol, symbol_data: item.symbol, usable: item.usable, usdt: item.usdt, symbol_data: item.symbol_data })
      })
      this.wallets = data.extends
      this.form.symbol_to = this.actions[2].symbol_data
      this.form.symbol = this.actions[0].symbol_data

      this.form.iconImg1 = this.actions[0].symbol_data
      this.form.iconImg2 = this.actions[2].symbol_data

      this.form.symbol_ex = this.actions[0].symbol
      this.form.symbol_to_ex = this.actions[2].symbol

    })
  },
  methods: {
    onMax() { // 最大
      if (this.amountAvailable / 1) {
        this.form.volume = this.amountAvailable
        this.debounceFn()
      } else {
        showToast(this.$t('你没有可提数量'))
      }
    },
    onInput(e) { // 获取最新
      this.form.volume = e.target.value
      this.clearInterval()
      if (this.form.volume == '') {
        this.form.get_volume = ''
      } else {
        this.debounceFn()
      }
    },
    debounceFn: debounce(function () {

      // this.fetchRate(true)
      if (this.form.volume <= 0) {
        showToast(this.$t('交易价格不能小于等于0'))
        return
      } else {
        this.fetchRate(true)
      }

    }, 300),
    fetchRate(callback) { // interval
      let obj = {
        symbol: this.form.symbol,
        symbol_to: this.form.symbol_to,
        volume: this.form.volume
      }
      _exchangeRage(obj).then(data => {
        const { get_rate } = data
        this.form.rate = get_rate
        this.clearInterval()
        if (callback && typeof callback === 'function') {
          callback()
        }
      })
    },
    onSwitch() { // 交换
      const temp = this.form.symbol
      this.form.symbol = this.form.symbol_to
      this.form.symbol_to = temp
      const tempImg = this.form.iconImg1
      this.form.iconImg1 = this.form.iconImg2
      this.form.iconImg2 = tempImg

      this.form.volume = ''
      this.clearInterval()
    },
    onSelect(type, evt) { // 选择
      if (evt.type == 0) {
        this.form['symbol'] = evt.item.symbol_data
        this.form['iconImg1'] = evt.item.symbol_data
        this.form['symbol_ex'] = evt.item.symbol
      } else {
        this.form['symbol_to'] = evt.item.symbol_data
        this.form['iconImg2'] = evt.item.symbol_data
        this.form['symbol_to_ex'] = evt.item.symbol
      }
      if (this.form.symbol !== this.form.symbol_to) {
        this.form.volume = ''
      }
      this.clearInterval()
    },
    onConfirm() { // 闪兑
      this.clearInterval()
      if (this.form.volume == '') {
        showToast(this.$t('请输入数量'));
      } else {
        this.fetchRate(() => {
          this.$emit("exchange", this.form);
        })
      }
    },
    clearInterval() { // 清除定时器
      clearInterval(this.interval)
      this.interval = null
    }
  },
  beforeUnmount() {
    // console.log('beforeDestroy')
    this.clearInterval()
  }
};
</script>

<style lang="scss" scoped>
#cryptos {
  .exchange-btn {
    bottom: 48px;
    left: 0;

    font-size: 36px;

    p {
      border-radius: 10px
    }
  }

  .active {
    color: $black;

    &:after {
      content: '';
      display: block;
      position: absolute;
      bottom: 0;
      width: 140px;
      height: 8px;
      background: $active_line;
      margin: 0 auto;
    }
  }

  .convert-box {
    background: $selectSymbol_background;
    margin: 0 auto;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 42px;
  }

  .btnMain {
    background: linear-gradient(90deg, #2C64D4 0%, #38AEEA 100%);
    border-radius: 5px;
    font-weight: bold;
  }
}
</style>
