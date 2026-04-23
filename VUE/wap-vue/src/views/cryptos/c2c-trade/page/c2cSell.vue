<template>
  <div id="c2cSellPage">
    <c2c-trade :title="$t('出售') + ' ' + $store.state.c2c.symbol" :detail="data" v-if="data.id">
      <template #trade>
        <div class="buy-item mt-10 w-full mainBackground c2cColor">
          <div class="buy-item-title flex justify-between text-28 py-8 border-b-1 border-light-grey">
            <div class="flex-1 text-center" :class="{ 'text-grey': genre === 'num' }" @click="typeSwitch('amount')">
              {{ $t("按金额出售") }}
            </div>
            <div class="flex-1 text-center" :class="{ 'text-grey': genre === 'amount' }" @click="typeSwitch('num')">
              {{ $t("按数量出售") }}
            </div>
          </div>
          <div class="px-8 mt-10">
            <div class="w-full buy-item-input relative box-border" v-show="genre === 'amount'">
              <span class="text-28 absolute left-7 text font-bold">{{
                currencySymbol
              }}</span>
              <input class="w-full text-36 h-28 border-none box-border pl-32" type="text"
                :placeholder="`${data.investment_min} - ${data.investment_max}`" v-model="money" @input="changeVal" />
              <span class="text-28 absolute right-5 text" style="color: #1a6ebd" @click="all">{{ $t("全部") }}</span>
            </div>
            <div class="w-full mb-10 buy-item-input relative box-border" v-show="genre === 'num'">
              <input class="w-full text-36 h-28 border-none box-border pl-8 font-normal" type="number"
                :placeholder="$t('请输入数量')" v-model="quantity" @input="changeValNum" />
              <span class="text-28 absolute right-5 text">
                <span class="mr-4" style="color: #b8bcc5">{{ $store.state.c2c.symbol }}</span>

                <span style="color: #1a6ebd" @click="all">{{
                  $t("全部")
                }}</span>
              </span>
            </div>
            <div v-if="tips" class="flex mt-3 text-red text-28">
              {{ tips }}
            </div>
            <div v-show="genre === 'amount'" class="mt-4 text-24 text-grey">
              {{ $t('可用余额') }} {{ Math.floor(usableVolume * 1000000) / 1000000 }} {{ $store.state.c2c.symbol }} ≈ {{
                (Math.floor(usableVolume * 100000) / 100000 * data.symbol_value / 1).toFixed(2) }}
              {{ currencySymbol }}
            </div>
            <div class="flex justify-between items-center w-full py-5 my-9 pl-8 pr-9 box-border rounded-md tabBackground"
              @click="toc2cCollection">
              <div>
                <span v-if="!reciveInfo.uuid" class="text-32" style="color: #b8bcc5">{{ $t("选择收款方式") }}</span>
                <div v-else class="flex items-center text-30 c2cColor">
                  <div class="w-2 h-8 rounded-full" style="background: #e7bb41"></div>
                  <span class="mx-5">{{ reciveInfo.methodName }}</span>
                  <span>{{ fullBankCarNumber }}</span>
                </div>
              </div>
              <van-icon class="font-bold" color="#B8BCC5" name="arrow" />
            </div>
            <div class="flex justify-between mb-3">
              <span class="text-grey">{{ $t("数量") }}</span>
              <span>{{ totalQuantity }}
                {{ data.symbol && data.symbol.toUpperCase() }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-grey">{{ $t("总额") }}</span>
              <span>{{ totalMoney }} {{ currencySymbol }}</span>
            </div>
            <div class="w-full mt-6 pb-8">
              <van-button color="#E35461" @click.native="SellClick" class="w-full text-30 buy-button" :disabled="loading"
                type="primary">
                <span v-if="!loading">{{ $t("出售") }}&nbsp;{{ data.symbol && data.symbol.toUpperCase() }}</span>
                <van-loading v-else type="spinner" />
              </van-button>
            </div>
          </div>
        </div>
      </template>
      <template #desc>
        <div class="mt-9">
          <h2 class="text-28 font-normal c2cColor">{{ $t("交易条款") }}</h2>
          <div class="mt-8 text-26 text-grey">
            <p>{{ data.transaction_terms }}</p>
          </div>
        </div>
      </template>
    </c2c-trade>
  </div>
</template>

<script>
import { mapState, mapGetters } from "vuex";
import otcApi from "@/service/otc.api";

import { Icon, showToast } from "vant";
import C2cTrade from "../components/C2cTrade.vue";
import { _getBalance } from "@/service/trade.api";
import { _getAllWallet } from "@/service/fund.api";

// keep-alive
export default {
  name: "c2cSell",
  props: ["type"],
  data() {
    return {
      id: "", // 广告id
      genre: "amount", // 按金额/数量购买
      amount: "", // 金额
      num: "", // 数量
      session_token: "",
      usableVolume: "",
      data: {},
      passwd: "", // 资金密码
      tips: '',
      money: '',
      quantity: '',
      totalMoney: '-',
      totalQuantity: '-',
      loading: false,
      // orderInfo: {
      //   unitPrice: "", // 单价
      //   count: "", // 数量
      //   totalPrice: "", // 总价
      //   orderNumber: "", // 订单号
      //   orderCreateTime: "", // 创建时间
      //   paymentMethodId: "", // 支付方式id
      //   methodName: "", // 支付名称
      //   bankNumber: "", // 银行卡号
      //   realName: "", // 卖家姓名
      //   bankName: "", // 银行名
      //   expire_time: "", // 支付时效

      // },
      reciveInfo: {},
    };
  },
  created() {
    // this.id = this.$route.query.id
    this.id = this.$store.state.c2c.adv_id;
    let { reciveType } = this.$route.query; // 支付方式
    if (reciveType) {
      reciveType = JSON.parse(reciveType);
      this.reciveInfo = reciveType;
      this.data = JSON.parse(this.$route.query.data)
      if (this.$route.query.type == 'amount') {
        this.money = this.$route.query.total
        this.totalMoney = this.money * 1
        if (this.symbol !== "USDT") {
          console.log(this.data.symbol_value)
          this.totalQuantity = Math.floor((this.totalMoney / this.data.symbol_value) * 1000000) / 1000000
        } else {
          this.totalQuantity = Math.floor((this.totalMoney / this.data.symbol_value) * 100) / 100;
        }
      } else {
        this.quantity = this.$route.query.total * 1
        this.totalQuantity = this.quantity
        if (this.symbol !== "USDT") {
          console.log(this.quantity)
          console.log(this.data.symbol_value)
          this.totalMoney = (this.quantity * this.data.symbol_value).toFixed(6)
        } else {
          this.totalMoney = (this.quantity * this.data.symbol_value).toFixed(2)
        }
      }
      this.genre = this.$route.query.type
    }
    this.initData();
    // 获取余额
    _getAllWallet().then((res) => {
      let walletList = res.extends;
      let initObj = walletList.find(item => {
        return item.symbol.toLowerCase() == this.$store.state.c2c.symbol.toLowerCase()
      })
      this.usableVolume = initObj.usable
    });
  },
  mounted() {
    // this.$bus.$on("returnPwd", (pwd) => {
    //   this.passwd = pwd;
    // });
    // 设置关于订单的信息
  },
  beforeUnmount() {
    // this.$bus.$off("returnPwd");
  },
  methods: {
    toc2cCollection() {
      let total = ''
      if (this.genre == 'amount') {
        total = this.money
      } else {
        total = this.quantity
      }
      this.$router.replace({ path: '/cryptos/c2cCollection', query: { id: this.data.id, type: this.genre, total, data: JSON.stringify(this.data) } })
    },
    initData() {
      otcApi.ctcAdvertGetDetail({ id: this.id, language: this.$i18n.locale }).then((res) => {
        this.data = res;
      });
    },
    // 切换类型
    typeSwitch(type) {
      this.genre = type;
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
        if (this.money * 1 < this.data.investment_min * 1) {
          this.tips = this.$t('最小金额') + this.data.investment_min + ' ' + this.exchangeCurrency
          this.totalMoney = '-'
          this.totalQuantity = '-'
        } else if (this.money * 1 > this.data.investment_max * 1) {
          this.tips = this.$t('最大金额') + this.data.investment_max + ' ' + this.exchangeCurrency
          this.totalMoney = '-'
          this.totalQuantity = '-'
        } else {
          this.tips = ''
          this.totalMoney = this.money
          if (this.symbol !== "USDT") {
            this.totalQuantity = Math.floor((this.totalMoney / this.data.symbol_value) * 1000000) / 1000000
          } else {
            this.totalQuantity = Math.floor((this.totalMoney / this.data.symbol_value) * 100) / 100;
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
        let minNum = Math.floor((this.data.investment_min / this.data.symbol_value) * 1000000) / 1000000;
        let maxNum = Math.floor((this.data.investment_max / this.data.symbol_value) * 1000000) / 1000000;
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
            this.totalMoney = (this.quantity * this.data.symbol_value).toFixed(6)
          } else {
            this.totalMoney = (this.quantity * this.data.symbol_value).toFixed(2)
          }
        }
      }
    },
    // 购买
    async SellClick() {
      if (!this.reciveInfo.uuid) {
        showToast(this.$t('请选择收款方式'));
        return;
      }
      // const index = this.data.pay_type_name.split(',').findIndex(item => item === this.reciveInfo.methodName)
      console.log(this.exchangeCurrency)
      this.loading = true
      let res = await otcApi.getSessionToken({
        currency: this.exchangeCurrency
      });
      this.session_token = res.session_token;
      const params = {
        session_token: this.session_token, // session_token
        c2c_advert_id: this.data.id,
        payment_method_id: this.reciveInfo.uuid, //this.data.pay_type.split(',')[index]
        direction: "sell",
        order_type: this.genre === "amount" ? "by_amount" : "by_num", // 'by_num'
        amount: this.genre === "amount" ? this.totalMoney / 1 : "", // 金额
        coin_amount: this.genre === "amount" ? "" : this.totalQuantity / 1, // 数量
        remark: this.data.remark,
      };

      // 获取订单号
      otcApi.ctcOrderOpen(params).then((res) => {
        this.loading = false
        this.$store.commit("c2c/SET_ORDER_NO", res.order_no);
        this.$router.push({ path: "/cryptos/sellGenerate" });
      }).catch(err => {
        this.loading = false
      })
    },
    // 全部点击
    all() {
      this.tips = ''
      let usableMoney = (Math.floor(this.usableVolume * 100000) / 100000 * this.data.symbol_value / 1).toFixed(2)
      if (usableMoney * 1 <= this.data.investment_max) {
        this.money = usableMoney
        this.totalMoney = usableMoney
        if (this.symbol !== "USDT") {
          this.quantity = Math.floor((usableMoney / this.data.symbol_value) * 1000000) / 1000000
          this.totalQuantity = Math.floor((usableMoney / this.data.symbol_value) * 1000000) / 1000000
        } else {
          this.quantity = Math.floor((usableMoney / this.data.symbol_value) * 100) / 100
          this.totalQuantity = Math.floor((usableMoney / this.data.symbol_value) * 100) / 100
        }
      } else {
        this.money = this.data.investment_max
        this.totalMoney = this.data.investment_max
        if (this.symbol !== "USDT") {
          this.quantity = Math.floor((this.data.investment_max / this.data.symbol_value) * 1000000) / 1000000
          this.totalQuantity = Math.floor((this.data.investment_max / this.data.symbol_value) * 1000000) / 1000000
        } else {
          this.quantity = Math.floor((this.data.investment_max / this.data.symbol_value) * 100) / 100
          this.totalQuantity = Math.floor((this.data.investment_max / this.data.symbol_value) * 100) / 100
        }
      }
    },
  },
  watch: {
    // paymentMethod() {
    //   if (Object.prototype.toString.call(this.paymentMethod) === '[object Object]') {
    //     console.log(this.paymentMethod)
    //     this.orderInfo.paymentMethodId = this.paymentMethod.id;
    //     this.orderInfo.methodName = this.paymentMethod.methodName;
    //     this.orderInfo.bankNumber = this.paymentMethod.paramValue1;
    //     this.orderInfo.realName = this.paymentMethod.realName;
    //     this.orderInfo.bankName = this.paymentMethod.paramName1;
    //   }
    // }
  },
  computed: {
    ...mapState("home", ["currency"]),
    ...mapGetters("c2c", ["symbol", "currencySymbol", "exchangeCurrency"]),
    fullMethodName() {
      if (this.paymentMethod) {
        return this.paymentMethod.methodName;
      } else {
        return "";
      }
    },
    fullBankCarNumber() {
      if (!this.reciveInfo.id) return;
      return this.reciveInfo.paramValue1.replace(
        /^([0-9]{4})[0-9]*([0-9]{4})$/,
        "$1******$2"
      );
    },
  },
  components: {
    [Icon.name]: Icon,
    C2cTrade,
  },
};
</script>

<style lang="scss" scoped>
#c2cSellPage {
  font-size: 30px;

  :deep(.van-button) {
    border-radius: 10px;
    background: #2EBD85;
    border: none;
  }

  .buy-item {
    border-radius: 25px;
    box-shadow: 0 0 8px rgba(0, 0, 0, 0.2);
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

  .border-bottom-1px {
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

  .tips {
    border-radius: 8px;
  }
}
</style>
