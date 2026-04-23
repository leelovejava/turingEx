<template>
  <div>
    <!-- 我要支付 -->
    <div class="font-14">
      <p class="mb-4 font-600 color-788">{{ $t("message.c2c.woyaozhifu") }}</p>
      <div class="h-12">
        <otc-input
          :isNumber="isNumber"
          :inputValue="payment"
          @input="handleInputCb"
          :placeholder="placeholder"
        >
          <template #right>
            <div>
              <span class="mr-4 blue cursor-pointer" @click="allFun">{{
                $t("message.c2c.quanbu")
              }}</span>
              <span v-if="type === 'buy'" style="color: #b8bdc5">{{
                fiatCurrency.title
              }}</span>
              <!-- <span v-if="type === 'buy'" style="color: #B8BDC5">{{ exchangeCurrency.currency_symbol }}</span> -->
              <span v-else style="color: #b8bdc5">
                <span v-if="detail.symbol">{{
                  detail.symbol.toUpperCase()
                }}</span></span
              >
            </div>
          </template>
        </otc-input>
      </div>
      <!-- 余额 -->
      <div
        v-if="type === 'sell'"
        class="flex justify-end items-center font-12 color-788"
      >
        <span>{{ $t("message.c2c.zhanghuyue") }}:</span>
        <span>{{ money }}</span>
        <span>{{ exchangeCurrency.symbol }}</span>
        <span class="ml-4 blue cursor-pointer" @click="openUrl">{{
          $t("message.c2c.huazhuan")
        }}</span>
      </div>
      <!-- 验证tips -->
      <div v-if="type === 'buy'">
        <p class="mt-5 font-12" style="color: #e05561">{{ tips }}</p>
      </div>
      <div v-if="type === 'sell'">
        <p class="mt-5 font-12 font-500 color-788">{{ company }}</p>
      </div>
    </div>
    <!-- 我将收到 -->
    <div class="font-14">
      <p class="mb-4 font-600 color-788">
        {{ $t("message.c2c.wojiangshoudao") }}
      </p>
      <div class="relative h-12">
        <otc-input
          :isNumber="isNumber"
          :inputValue="received"
          placeholder="0.00"
        >
          <template #right>
            <div>
              <span v-if="type === 'buy'" style="color: #b8bdc5">{{
                exchangeCurrency.symbol
              }}</span>
              <span v-else style="color: #b8bdc5">{{ detail.currency }}</span>
            </div>
          </template>
        </otc-input>

        <div
          v-if="type === 'sell'"
          class="absolute left-0"
          style="bottom: -32px"
        >
          <p class="mt-5 font-12" style="color: #e05561">{{ tips2 }}</p>
        </div>
      </div>
    </div>
    <slot></slot>
    <div class="flex items-center mt-8">
      <el-button class="cancel mr-10" @click="cancel">{{
        $t("message.c2c.quxiao")
      }}</el-button>
      <el-button v-if="type === 'buy'" class="buy" @click="buyClick"
        >{{ $t("message.c2c.goumai") }}{{ exchangeCurrency.symbol }}</el-button
      >
      <el-button
        v-else
        class="buy"
        style="background: #e05461"
        @click="buyClick"
        >{{ $t("message.c2c.chushou") }}{{ exchangeCurrency.symbol }}</el-button
      >
    </div>
  </div>
</template>

<script>
import OtcInput from "@/views/c2c/components/OtcInput.vue";
import Axios from "@/api/c2c.js";
import { getParamsLang } from "@/utils/index";
export default {
  name: "InputItem",
  props: [
    "type",
    "fiatCurrency",
    "dataInfo",
    "exchangeCurrency",
    "PaymentMethodInfo",
    "PaymentMethodList",
    "isNumber",
  ],
  data() {
    return {
      payment: "",
      received: "",
      tips: "",
      tips2: "",
      company: "",
      detail: {},
      money: 0,
      placeholder: "",
    };
  },
  watch: {
    "dataInfo.trade"(val) {
      if (val) {
        this.payment = "";
        this.received = "";
        if (this.type == "buy") {
          this.placeholder =
            this.dataInfo.investment_min + "-" + this.dataInfo.investment_max;
        } else {
          let num = 2;
          if (this.dataInfo.symbol == "btc") {
            num = 8;
          }
          this.placeholder =
            (
              parseFloat(this.dataInfo.investment_min) /
              parseFloat(this.dataInfo.symbol_value)
            ).toFixed(num) +
            "-" +
            (
              parseFloat(this.dataInfo.investment_max) /
              parseFloat(this.dataInfo.symbol_value)
            ).toFixed(num);
        }
        this.getDetail();
        if (this.type == "sell") {
          this.getAllMoeny();
        }
      }
    },
    "exchangeCurrency.symbol"(val) {
      if (val) {
        this.tips = "";
        this.tips2 = "";
      }
    },
  },

  methods: {
    getParamsLang,
    getDetail() {
      let language = this.getParamsLang();
      Axios.c2cAdvertGetDetail({
        id: this.dataInfo.id,
        language,
      }).then((res) => {
        this.detail = res.data;
        this.$emit("getDetail", this.detail);
      });
    },
    cancel() {
      this.$emit("cancel");
    },
    //全部数量
    allFun() {
      if (this.type == "buy") {
        this.tips = "";
        this.payment = this.detail.investment_max;
        this.received = this.detail.investment_max;
        if (this.detail.symbol !== "USDT") {
          this.received =
            Math.floor((this.payment / this.detail.symbol_value) * 1000000) /
            1000000;
        } else {
          this.received = (this.payment * this.detail.symbol_value).toFixed(2);
        }
      } else {
        this.tips2 = "";
        if (this.symbol !== "USDT") {
          this.payment =
            Math.floor(
              (this.detail.investment_max / this.detail.symbol_value) * 1000000
            ) / 1000000;
          this.received = (this.payment * this.detail.symbol_value).toFixed(6);
        } else {
          this.payment =
            Math.floor(
              (this.detail.investment_max / this.detail.symbol_value) * 100
            ) / 100;
          this.received =
            Math.floor(
              (this.detail.investment_max / this.detail.symbol_value) * 100
            ) / 100;
        }
      }
    },
    // 我要支付 input事件
    handleInputCb(val) {
      this.payment = val;
      if (this.type == "buy") {
        if (this.payment * 1 < this.detail.investment_min * 1) {
          this.tips =
            this.$t("message.c2c.zuixiaojine") + this.detail.investment_min;
        } else if (this.payment * 1 > this.detail.investment_max * 1) {
          this.tips =
            this.$t("message.c2c.zuidajine") + this.detail.investment_max;
        } else {
          this.tips = "";
          if (this.detail.symbol !== "USDT") {
            this.received =
              Math.floor((this.payment / this.detail.symbol_value) * 1000000) /
              1000000;
          } else {
            this.received = (this.payment * this.detail.symbol_value).toFixed(
              2
            );
          }
        }
      } else {
        let minNum =
          Math.floor(
            (this.detail.investment_min / this.detail.symbol_value) * 1000000
          ) / 1000000;
        let maxNum =
          Math.floor(
            (this.detail.investment_max / this.detail.symbol_value) * 1000000
          ) / 1000000;
        if (this.payment * 1 < minNum * 1) {
          this.tips2 = this.$t("message.c2c.zuixiaoshuliang") + minNum + " ";
        } else if (this.payment * 1 > maxNum * 1) {
          this.tips2 = this.$t("message.c2c.zuidashuliang") + maxNum + " ";
        } else {
          this.tips2 = "";
          if (this.detail.symbol !== "USDT") {
            this.received = (this.payment * this.detail.symbol_value).toFixed(
              6
            );
          } else {
            this.received = (this.payment * this.detail.symbol_value).toFixed(
              2
            );
          }
        }
      }
    },
    // 购买
    buyClick() {
      if (this.tip) {
        return;
      }
      if (this.payment == "") {
        this.$message.error(this.$t("message.c2c.tip149"));
        return;
      } else {
        this.submitOrder();
      }
    },
    //全部数量
    getAllMoeny() {
      Axios.getAssets({ symbol: this.exchangeCurrency.value }).then((res) => {
        if (res.code == "0") {
          this.money = res.data.extends[0].volume;
        }
      });
    },
    submitOrder() {
      // 发起订单
      Axios.c2cOrderOpen({ currency: this.fiatCurrency.title }).then((res) => {
        const params = {
          session_token: res.data.session_token, // session_token
          c2c_advert_id: this.detail.id,
          payment_method_id: this.PaymentMethodInfo.uuid,
          direction: this.type,
          order_type: this.type == "buy" ? "by_amount" : "by_num", // 'by_num'
          amount: this.type == "buy" ? this.payment : "", // 金额
          coin_amount: this.type == "buy" ? "" : this.payment, // 数量
        };

        Axios.c2cOrderOpenOrder(params).then((res) => {
          if (res.code == "0") {
            let type = 1;
            if (this.type == "buy") {
              type = 1;
            } else {
              type = 2;
            }
            this.$router.push(
              "/c2c/orderSuccess?id=" +
                res.data.order_no +
                "&isBuy=" +
                type +
                "&fiatCurrency=" +
                JSON.stringify(this.fiatCurrency)
            );
          }
        });
      });
    },
    openUrl() {
      this.$router.push("/exchange");
    },
  },
  computed: {},
  components: {
    OtcInput,
  },
};
</script>

<style lang="scss" scoped>
.blue {
  color: #1a6ebd;
}

.color-788 {
  color: #78808e;
}

:deep {
  .otc-input {
    input {
      padding-left: 9px;

      &::placeholder {
        color: #b8bdc5;
      }
    }
  }

  .el-button {
    height: 40px;
    border: none;
    font-weight: 600;
    box-sizing: border-box;
  }

  .cancel.el-button {
    width: 140px;
    background: #eaecef;
    color: #000;
  }

  .buy.el-button {
    flex: 1;
    background: #62c887;
    color: #fff;
  }
}
</style>
