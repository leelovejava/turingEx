<template>
  <div id="cryptos">
    <div class="c2cBuy w-full h-full">
      <c2c-trade :title="$t('购买') + ' ' + symbol" :detail="detail" @refresh="fetchDetail" @payType="payType = $event"
        v-if="detail.id">
        <template #trade>
          <div class="buy-item mt-10 w-full mainBackground c2cColor">
            <div class="buy-item-title flex justify-between text-28 py-8 border-b-1 border-light-grey c2cColor">
              <div class="flex-1 text-center" :class="{ 'text-grey': type === 'num' }" @click="typeSwitch('amount')">
                {{ $t('按金额购买') }}
              </div>
              <div class="flex-1 text-center" :class="{ 'text-grey': type === 'amount' }" @click="typeSwitch('num')">
                {{ $t('按数量购买') }}
              </div>
            </div>
            <div class="px-8 mt-10">
              <div class="w-full buy-item-input relative box-border" v-show="type === 'amount'">
                <span class="text-28 absolute left-28 text font-bold c2cColor">{{ currencySymbol }}</span>
                <input class="w-full text-36 h-28 border-none box-border pl-32 c2cColor" type="number"
                  :placeholder="$t('请输入金额')" v-model="money" @input="changeVal">
                <span class="text-28 absolute right-19 text c2cColor" @click="all">{{ $t('全部') }}</span>
              </div>
              <div class="w-full buy-item-input relative box-border" v-show="type === 'num'">
                <input class="w-full text-36 h-28 border-none box-border pl-8 c2cColor" type="number"
                  :placeholder="$t('请输入数量')" v-model="quantity" @input="changeValNum">
                <span class="text-28 absolute right-19 text">
                  <span class="mr-4" style="color:#B8BCC5;">{{ symbol }}</span>
                  <span class="c2cColor" @click="all">{{ $t('全部') }}</span>
                </span>
              </div>
              <div v-if="tips" class="flex mt-3 text-red text-28">
                {{ tips }}
              </div>
              <div class="flex justify-between mt-10 mb-4 c2cColor">
                <span class="text-grey">{{ $t('数量') }}</span>
                <span>{{ totalQuantity }} {{ symbol }}</span>
              </div>
              <div class="flex justify-between c2cColor">
                <span class="text-grey">{{ $t('总额') }}</span>
                <span>{{ totalMoney }} {{ currencySymbol }}</span>
              </div>
              <div class="w-full mt-6 pb-8">
                <van-button @click.native="buyClick" class="w-full text-30 buy-button" :disabled="loading" type="primary">
                  <span v-if="!loading">{{ $t('购买') }}&nbsp;{{ symbol }}</span>
                  <van-loading v-else type="spinner" />
                </van-button>
              </div>
            </div>
          </div>
        </template>
        <template #desc>
          <div class="mt-9">
            <h2 class="text-28 font-normal c2cColor">{{ $t('交易信息') }}</h2>
            <div class="mt-8 text-26 text-grey">
              <p>{{ $t('请先阅读以下内容:') }}</p>
              <p>{{ $t('银行卡转账切勿备注，不然不给予放币和直接封其账号。付款后 需要提供打款后新的交易明细图（如果P图或者隐藏交易明细上报平台冻结账户）') }}</p>
            </div>
          </div>
        </template>
      </c2c-trade>
    </div>
  </div>
</template>

<script>
import { formatTime } from "@/utils/utis";
import { Button, Cell, DropdownItem, DropdownMenu, Field, Icon, Popup, Switch } from "vant";
// import { SET_COUNT, SET_CREATE_ORDER_TIME, SET_ORDER_NUMBER, SET_TOTAL_PRICE, } from "@/store/const.store";
import C2cTrade from "../components/C2cTrade.vue";
import otcApi from "@/service/otc.api";
import { mapGetters } from "vuex";
import { showToast } from 'vant';
export default {
  name: "c2cBuy",
  data() {
    return {
      type: 'amount', // 按金额/数量购买
      detail: {},
      payType: '',
      tips: '',
      money: '',
      quantity: '',
      totalMoney: '-',
      totalQuantity: '-',
      loading: false
    }
  },
  created() {
    this.fetchDetail()
  },
  methods: {
    fetchDetail() { // 获取详情
      const id = this.$store.state.c2c.adv_id
      otcApi.ctcAdvertGetDetail({ id, language: this.$i18n.locale }).then(res => {
        if (res.symbol_value == this.detail.symbol_value) {
          showToast(this.$t('无价格更新'))
        }
        this.detail = res
      })
    },
    async submitOrder() { // 发起订单
      this.loading = true
      otcApi.getSessionToken({ currency: this.exchangeCurrency }).then((data) => {
        const params = {
          session_token: data.session_token, // session_token
          c2c_advert_id: this.detail.id,
          payment_method_id: this.payType,
          direction: 'buy',
          order_type: this.type === 'amount' ? 'by_amount' : 'by_num', // 'by_num'
          amount: this.type === 'amount' ? this.totalMoney / 1 : '', // 金额
          coin_amount: this.type === 'amount' ? '' : this.totalQuantity / 1 // 数量
        }
        otcApi.ctcOrderOpen(params).then(res => {
          this.loading = false
          this.$store.commit('c2c/SET_ORDER_NO', res.order_no)
          this.$router.push('/cryptos/orderGeneration')
        }).catch(err => {
          this.loading = false
        })
      }).catch(err => {
        this.loading = false
      })

    },
    // 切换类型
    typeSwitch(type) {
      this.type = type;
      this.tips = ''
      this.money = ''
      this.quantity = ''
      this.totalMoney = '-'
      this.totalQuantity = '-'
    },
    changeVal(e) {
      // e.target.value = e.target.value.replace('-', ''); 不能输入小数了
      //this[this.type] = e.target.value;
      if (this.money == '') {
        this.tips = ''
        this.totalMoney = '-'
        this.totalQuantity = '-'
      } else {
        if (this.money * 1 < this.detail.investment_min * 1) {
          this.tips = this.$t('最小金额') + this.detail.investment_min + ' ' + this.exchangeCurrency
          this.totalMoney = '-'
          this.totalQuantity = '-'
        } else if (this.money * 1 > this.detail.investment_max * 1) {
          this.tips = this.$t('最大金额') + this.detail.investment_max + ' ' + this.exchangeCurrency
          this.totalMoney = '-'
          this.totalQuantity = '-'
        } else {
          this.tips = ''
          this.totalMoney = this.money
          if (this.symbol !== "USDT") {
            this.totalQuantity = Math.floor((this.totalMoney / this.detail.symbol_value) * 1000000) / 1000000
          } else {
            this.totalQuantity = Math.floor((this.totalMoney / this.detail.symbol_value) * 100) / 100;
          }
        }
      }
    },
    changeValNum() {
      if (this.quantity == '') {
        this.tips = ''
        this.totalMoney = '-'
        this.totalQuantity = '-'
      } else {
        let minNum = Math.floor((this.detail.investment_min / this.detail.symbol_value) * 1000000) / 1000000;
        let maxNum = Math.floor((this.detail.investment_max / this.detail.symbol_value) * 1000000) / 1000000;
        if (this.quantity * 1 < minNum * 1) {
          this.tips = this.$t('最小数量') + minNum + ' '
          this.totalMoney = '-'
          this.totalQuantity = '-'
        } else if (this.quantity * 1 > maxNum * 1) {
          this.tips = this.$t('最大数量') + maxNum + ' '
          this.totalMoney = '-'
          this.totalQuantity = '-'
        } else {
          this.tips = ''
          this.totalQuantity = this.quantity
          if (this.symbol !== "USDT") {
            this.totalMoney = (this.quantity * this.detail.symbol_value).toFixed(6)
          } else {
            this.totalMoney = (this.quantity * this.detail.symbol_value).toFixed(2)
          }
        }
      }
    },
    // 购买
    async buyClick() {
      if (this.type == 'amount') {
        if (this.money == '') {
          showToast(this.$t('请输入金额'))
        } else {
          this.submitOrder()
        }
      } else {
        console.log(this.quantity)
        if (this.quantity == '') {
          showToast(this.$t('请输入数量'))
        } else {
          this.submitOrder()
        }
      }
    },
    all() {
      this.tips = ''
      this.money = this.detail.investment_max
      this.totalMoney = this.detail.investment_max
      if (this.symbol !== "USDT") {
        this.quantity = Math.floor((this.detail.investment_max / this.detail.symbol_value) * 1000000) / 1000000
        this.totalQuantity = Math.floor((this.detail.investment_max / this.detail.symbol_value) * 1000000) / 1000000
      } else {
        this.quantity = Math.floor((this.detail.investment_max / this.detail.symbol_value) * 100) / 100
        this.totalQuantity = Math.floor((this.detail.investment_max / this.detail.symbol_value) * 100) / 100
      }
    }
  },
  computed: {
    ...mapGetters('c2c', ['symbol', "currencySymbol", "exchangeCurrency"]),
  },
  components: {
    [Icon.name]: Icon,
    [Field.name]: Field,
    [Popup.name]: Popup,
    [Cell.name]: Cell,
    [Switch.name]: Switch,
    [DropdownMenu.name]: DropdownMenu,
    [DropdownItem.name]: DropdownItem,
    [Button.name]: Button,
    C2cTrade,
  },
}
</script>

<style lang="scss" scoped>
#cryptos {
  font-size: 30px;

  .c2cBuy {
    :deep(.van-button) {
      border-radius: 10px;
      background: #2EBD85;
      border: none;
    }
  }


  .buy-item {

    border-radius: 25px;
    box-shadow: 0 0 8px rgba(0, 0, 0, .2);
  }

  .border-ra {
    border-radius: 10px;
  }

  .buy-item-title {
    position: relative;

    &:after {
      position: absolute;
      box-sizing: border-box;
      content: ' ';
      pointer-events: none;
      top: -50%;
      right: -50%;
      bottom: -50%;
      left: -50%;
      border-bottom: 1px solid #252738;
      -webkit-transform: scale(.5);
      transform: scale(.5);
    }
  }

  .buy-item-input {
    input {
      border-radius: 10px;
      background: $input_background;
    }

    .text {
      top: 50%;
      transform: translateY(-50%);
    }
  }




  .tips {
    border-radius: 8px;
  }
}
</style>
