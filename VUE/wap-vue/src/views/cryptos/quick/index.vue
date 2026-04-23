<template>
  <div id="cryptos">
    <div id="wantBuy" class="flex flex-col w-full h-full">
      <div>
        <div class="w-full h-32 pl-10 pr-8 box-border flex text-white items-center">
          <van-icon class="c2cColor" name="arrow-left" @click="$router.back()" />
          <div class="flex-1 text-right mr-24">
            <span @click="showPopup">
              <span class="text-36 font-bold mr-2 textColor">{{ $t("快捷区") }}</span>
              <van-icon name="arrow-down" class="c2cColor" />
            </span>
          </div>
          <van-popup round position="top" v-model:show="show" :closeable="true">
            <span class="w-60 text-28 c2cColor font-medium ml-8 block mt-5">{{
              $t("选择交易区")
            }}</span>
            <div class="flex justify-around c2cColor mt-10 pb-12">
              <div class="flex flex-col items-center" @click="show = false">
                <img class="w-14 h-14" src="@/assets/image/otc/wantBuyHead/fast.png" alt="" />
                <span class="text-24 mt-2 h-8 font-medium">{{ $t("快捷区") }}</span>
              </div>
              <div class="flex flex-col items-center" @click="
                $router.push({
                  path: '/cryptos/wantBuy',
                })
                ">
                <img class="w-14 h-14" src="@/assets/image/otc/wantBuyHead/optional.png" alt="" />
                <span class="text-24 mt-2 font-medium">{{ $t("自选区") }}</span>
              </div>
            </div>
          </van-popup>
          <!-- <div
            class="w-232 h-64 px-23 box-border box-radius textColor flex justify-around items-center tabBackground"
          >
            <div
              class="flex items-center"
              @click="
                $router.push({
                  path: '/cryptos/selectLegalCurrency',
                })
              "
            >
              <span class="mr-15 text-32 font-normal">{{ exchangeCurrency }}</span>
              <img
                class="w-24 h-20"
                src="@/assets/image/otc/wantBuyHead/Group3.png"
                alt=""
              />
            </div>
            <span class="w-2 h-36 parting-line"></span>
            <van-popover
              get-container="#quick"
              v-model="showPopover"
              :actions="actions"
              @select="onSelect"
              placement="bottom-end"
              theme="dark"
              :offset="[20, 20]"
            >
              <template #reference>
                <img
                  @click="showPopover = !showPopover"
                  class="relative w-24 h-6 list-img"
                  src="@/assets/image/otc/wantBuyHead/Group4.png"
                  alt=""
                />
              </template>
            </van-popover>
          </div>
        </div> -->
          <div class="w-60 h-16 px-6 box-border box-radius text-black flex justify-around items-center"
            style="background: #c2e1ff">
            <div class="flex items-center" @click="$router.push({ path: '/cryptos/selectLegalCurrency' })">
              <span class="mr-4 text-32 font-normal">{{ exchangeCurrency }}</span>
              <img class="w-6 h-5" src="@/assets/image/otc/wantBuyHead/Group.png" alt="" />
            </div>
            <span class="mx-7 w-2 h-12">|</span>
            <van-popover get-container="#quick" v-model="showPopover" :actions="actions" @select="onSelect"
              placement="bottom-end" theme="dark" :offset="[20, 20]">
              <template #reference>
                <img @click="showPopover = !showPopover" class="relative w-6 h-2 list-img"
                  src="@/assets/image/otc/wantBuyHead/Group2.png" alt="" />
              </template>
            </van-popover>
          </div>
        </div>
        <div class="px-8 pb-10 type">
          <div class="flex items-center text-36">
            <div :class="{ active: isBuy }" @click="isBuy = true">{{ $t("我要买") }}</div>
            <div class="w-8 h-1 mx-5" style="transform: rotate(90deg); background: #eaebee"></div>
            <div :class="{ active: !isBuy }" @click="isBuy = false">
              {{ $t("我要卖") }}
            </div>
          </div>
          <div class="flex mt-10 currency text-center">
            <div v-for="(item, index) in currency" :key="index" :class="{ active: activeCurrency === item.title }"
              @click="changeTab(item)">
              <p>{{ item.title }}</p>
              <p>{{ item.label }}</p>
            </div>
          </div>
        </div>
      </div>
      <div class="w-full flex-1 px-8 pt-9 box-border mainBackground">
        <div class="px-8 py-10 c2cTabBackground item" v-show="isBuy">
          <div v-show="order_type === 'Amount'">
            <div class="mb-4 textColor1">{{ $t("购买金额") }}</div>
            <div class="w-full relative box-border rounded-xl inputBackground1 input">
              <span class="text-32 absolute left-5 text font-bold c2cColor">{{
                currencySymbol
              }}</span>
              <input class="w-full text-36 h-28 rounded-xl border-none box-border pl-32 inputBackground1 c2cColor"
                type="number" :placeholder="$t('最小金额') + 100" v-model="buyAmount" />
            </div>
            <div class="flex justify-between mt-7 text-24">
              <div class="text-grey">
                {{ $t("参考单价") }}&nbsp;{{ currencySymbol }}{{ referPrice }}
              </div>
              <div class="flex items-center text-blue" @click="changeOrderType('Num')">
                <span class="mr-2">{{ $t("按数量购买") }}</span>
                <img class="w-6 h-6" src="../../../assets/image/c2c/Group241.png" alt="" />
              </div>
            </div>
          </div>
          <div v-show="order_type === 'Num'">
            <div class="flex justify-between mb-4">
              <span class="textColor1">{{ $t("购买数量") }}</span>
            </div>
            <div class="w-full relative box-border rounded-xl inputBackground1 input">
              <input class="w-full text-36 h-28 rounded-xl border-none box-border pl-10 inputBackground1 c2cColor"
                type="number" :placeholder="$t('请输入数量')" v-model="buyNum" />
              <span class="text-28 absolute right-19 text" style="color: #1a6ebd">
                <span class="mr-5 c2cColor">{{ activeCurrency }}</span>
              </span>
            </div>
            <div class="flex justify-between mt-7 text-24">
              <div class="text-grey">
                {{ $t("参考单价") }}&nbsp;{{ currencySymbol }}{{ referPrice }}
              </div>
              <div class="flex items-center text-blue" @click="changeOrderType('Amount')">
                <span class="mr-2">{{ $t("按金额购买") }}</span>
                <img class="w-6 h-6" src="../../../assets/image/c2c/Group241.png" alt="" />
              </div>
            </div>
          </div>
          <div class="mt-20">
            <van-button class="w-full h-20 text-32 rounded-xl" type="primary" color="#2EBD85" @click="handleClick">{{
              $t("0手续费购买") }}
            </van-button>
          </div>
        </div>
        <div class="px-8 py-10 c2cTabBackground item" v-show="!isBuy">
          <div v-show="order_type === 'Num'">
            <div class="flex justify-between mb-4">
              <span class="textColor1">{{ $t("出售数量") }}</span>
              <span class="text-24 text-blue" style="text-decoration: underline" @click="isBuy = !isBuy">{{ $t("划转")
              }}</span>
            </div>
            <div class="w-full relative box-border rounded-xl inputBackground1 input">
              <input class="w-full text-36 h-28 rounded-xl border-none box-border pl-10 inputBackground1 c2cColor"
                type="number" :placeholder="$t('请输入数量')" v-model="sellNum" />
              <span class="text-28 absolute right-19 text" style="color: #1a6ebd">
                <span class="mr-5 c2cColor">{{ activeCurrency }}</span>
              </span>
            </div>
            <div class="flex justify-between mt- text-24">
              <div class="text-grey">
                {{ $t("参考单价") }}&nbsp;{{ currencySymbol }}{{ referPrice }}
              </div>
              <div class="flex items-center text-blue" @click="order_type = 'Amount'">
                <span class="mr-11">{{ $t("按金额出售") }}</span>
                <img class="w-24 h-24" src="../../../assets/image/c2c/Group241.png" alt="" />
              </div>
            </div>
          </div>
          <div v-show="order_type === 'Amount'">
            <div class="flex justify-between mb-18" style="color: #595c61">
              <span class="textColor1">{{ $t("出售金额") }}</span>
              <span class="text-24 text-blue" style="text-decoration: underline"
                @click="$router.push('/cryptos/exchangePage')">{{ $t("划转") }}</span>
            </div>
            <div class="w-full relative box-border rounded-xl inputBackground1 input">
              <div class="w-full relative box-border rounded-xl inputBackground1 input">
                <span class="text-32 absolute left-5 text font-bold c2cColor">{{
                  currencySymbol
                }}</span>
                <input class="w-full text-36 h-28 rounded-xl border-none box-border pl-32 inputBackground1 c2cColor"
                  type="number" :placeholder="$t('最小金额') + 100" v-model="sellAmount" />
              </div>
            </div>
            <div class="flex justify-between mt-7 text-24">
              <div class="text-grey">
                {{ $t("参考单价") }}&nbsp;{{ currencySymbol }}{{ referPrice }}
              </div>
              <div class="flex items-center text-blue" @click="order_type = 'Num'">
                <span class="mr-11">{{ $t("按数量出售") }}</span>
                <img class="w-6 h-6" src="../../../assets/image/c2c/Group241.png" alt="" />
              </div>
            </div>
          </div>
          <div class="mt-20">
            <van-button class="w-full h-20 text-32 rounded-xl" type="primary" color="#E35461" @click="handleClick">{{
              $t("0手续费出售") }}
            </van-button>
          </div>
        </div>
      </div>

      <van-popup v-model:show="enterTrade" position="bottom">
        <enter-trade v-if="paymentMethod.length" :type="fullType" :payment-method="paymentMethod" :info="info"
          :session-token="session_token" :symbol="activeCurrency" :exchangeCurrency="exchangeCurrency"
          @updataToken="updataToken" @close="enterTrade = false" />
      </van-popup>
    </div>
  </div>
</template>

<script>
import { Icon, Popup, Popover, Button, showToast } from "vant";
import EnterTrade from "./EnterTrade.vue";
import { mapGetters } from "vuex";
import otcApi from "@/service/otc.api.js";

export default {
  name: "quickIndex",
  data() {
    return {
      referPrice: "--",
      paymentMethod: [],
      order_type: "Amount",
      enterTrade: false,
      isBuy: true, // 模式
      activeCurrency: "USDT",
      show: false,
      showPopover: false,
      currency: [
        {
          title: "USDT",
          label: this.$t("计价币种"),
        },
        {
          title: "BTC",
          label: this.$t("最知名币种"),
        },
        {
          title: "ETH",
          label: this.$t("以太坊"),
        },
      ],
      actions: [
        {
          text: this.$t("收款方式"),
          icon: "setting-o",
          path: "/cryptos/paymentMethod",
        },
        // {
        //   text: this.$t('c2c帮助中心'),
        //   icon: this.handleImage('../../../assets/image/otc/buy/help_icon.png'),
        //   path: '/c2cHelpCenter',
        // },
        // {
        //   text: 'c2c用户中心',
        //   icon: this.handleImage('../../../assets/image/otc/buy/user_icon.png'),
        //   path: '/c2c/c2cUser',
        // },
        // {
        //   text: this.$t('接单模式'),
        //   icon: this.handleImage('../../../assets/image/otc/buy/order_icon.png'),
        // },
      ],
      session_token: "",

      buyAmount: "",
      buyNum: "",
      sellAmount: "",
      sellNum: "",
      info: {},
      all_price: {},
      isPay: false,
    };
  },
  created() {
    otcApi
      .getorder_open({
        currency: this.exchangeCurrency,
      })
      .then((res) => {
        console.log(res);
        this.session_token = res.session_token;
        console.log(this.activeCurrency.toLowerCase());
        this.all_price = res.all_price;
        this.referPrice = this.all_price[this.activeCurrency.toLowerCase()];
      });

    otcApi.ctcPaymentMethodList({ language: this.$i18n.locale }).then((res) => {
      if (res.length) {
        this.isPay = true;
      } else {
        this.isPay = false;
      }
    });
  },
  methods: {
    handleImage(url) {
      return new URL(url, import.meta.url).href;
    },
    changeOrderType(order_type) {
      this.buyAmount = "";
      this.buyNum = "";
      this.sellAmount = "";
      this.sellNum = "";
      this.order_type = order_type;
    },
    getPriceList(type) {
      //获取承兑商支付方式列表
      otcApi
        .c2cgetBestPrice({
          order_type: this.order_type == "Amount" ? "by_amount" : "by_num",
          amount: this.isBuy ? this.buyAmount : this.sellAmount,
          coin_amount: this.isBuy ? this.buyNum : this.sellNum,
          direction: this.isBuy ? "buy" : "sell",
          currency: this.exchangeCurrency,
          symbol: this.activeCurrency.toLowerCase(),
          language: this.$i18n.locale,
        })
        .then((data) => {
          this.paymentMethod = data;
          if (this.paymentMethod.length == 0) {
            showToast(this.$t("无匹配的承兑商"));
            return;
          } else {
            this.enterTrade = true;
            this.info = {
              type: this.order_type,
              num: this[type + this.order_type],
            };
          }
        });
    },
    changeTab(item) {
      this.buyAmount = "";
      this.buyNum = "";
      this.sellAmount = "";
      this.sellNum = "";
      this.activeCurrency = item.title;
      this.referPrice = this.all_price[this.activeCurrency.toLowerCase()];
    },
    updataToken() {
      otcApi
        .getSessionToken({
          currency: this.exchangeCurrency,
        })
        .then((res) => {
          this.session_token = res.session_token;
        });
    },
    showPopup() {
      this.show = true;
    },
    onSelect(action) {
      if (action.text == this.$t("接单模式")) {
        this.showJie = true;
        return;
      }

      this.$router.push({
        path: action.path,
      });
    },
    handleClick() {
      const type = this.isBuy ? "buy" : "sell";
      if (!this[type + this.order_type]) {
        const text = this.order_type === "Amount" ? this.$t("金额") : this.$t("数量");
        showToast(this.$t("请输入") + text);
      } else {
        if (this.isPay) {
          this.getPriceList(type);
        } else {
          showToast(this.$t("请添加收款方式"));
          setTimeout(() => {
            this.$router.push("/cryptos/paymentMethod");
          }, 500);
        }
      }
    },
  },
  computed: {
    ...mapGetters("c2c", ["exchangeCurrency", "currencySymbol"]),
    fullType() {
      return this.isBuy ? "buy" : "sell";
    },
  },
  components: {
    [Icon.name]: Icon,
    [Popup.name]: Popup,
    [Popover.name]: Popover,
    [Button.name]: Button,
    EnterTrade,
  },
};
</script>

<style lang="scss" scoped>
#cryptos {
  font-size: 30px;

  .box-radius {
    border-radius: 40px;
  }

  .type {
    color: #b8bcc5;

    .active {
      color: $text_color;
    }
  }

  .currency {
    .active {
      p {
        color: $blue !important;
      }
    }

    &>div {
      padding: 10px 50px;
      margin-right: 30px;
      border-radius: 12px;
      background: $tab_background;

      &:last-child {
        margin: 0;
      }
    }

    p:first-child {
      margin-bottom: 8px;
      font-weight: 500;
      color: #595c61;
    }

    p:last-child {
      color: #868c9a;
    }
  }

  .item {
    box-shadow: 0px 0px 12px rgba(0, 0, 0, 0.1);
    border-radius: 10px;
  }

  .input {
    span {
      top: 50%;
      transform: translateY(-50%);
    }
  }

  .parting-line {
    background: $mainbgWhiteColor;
  }
}
</style>
