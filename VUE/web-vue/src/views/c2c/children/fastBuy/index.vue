<template>
  <layout>
    <template v-slot:menu>
      <!-- 菜单切换 -->
      <div class="b-tab">
        <!-- 订单头部 -->
        <otc-order-header />
        <!-- 购买form -->
        <div class="b-buy">
          <div v-if="procss === 1" class="buy-tab">
            <div
              :class="[!isBuyActive ? 'is-buy-active' : '']"
              @click.stop="tabFun(true)"
            >
              {{ $t("message.c2c.goumai") }}
            </div>
            <div
              :class="isBuyActive ? 'is-buy-active' : ''"
              @click.stop="tabFun(false)"
            >
              {{ $t("message.c2c.chushou") }}
            </div>
          </div>
          <div class="buy-main">
            <div v-if="procss === 1">
              <!-- 支付 -->
              <div class="zhifu">
                <p>
                  {{
                    isBuyActive
                      ? $t("message.c2c.woyaozhifu")
                      : $t("message.c2c.woyaomai")
                  }}
                </p>
                <div class="input-se">
                  <el-input
                    @input="buyInput"
                    @keyup.native="onlyNumber()"
                    :class="errorPrice ? 'error-buy' : ''"
                    v-model="buyValue"
                    @blur="changeBuy"
                  >
                    <template #append>
                      <fiat-currency
                        :isTwo="false"
                        :currencyType="2"
                        :isbg="true"
                        class="sele"
                        @selectItem="handleFiatSelect"
                        :class="errorPrice ? 'error-buy' : ''"
                      />
                    </template>
                  </el-input>

                  <div
                    v-show="!isBuyActive"
                    class="huazhuan-price"
                    style="right: 0; display: flex; align-items: center"
                  >
                    <span style="color: #78808e"
                      >{{ $t("message.c2c.zhanghuyue") }} {{ money }} USDT</span
                    >
                    <img
                      src="@/assets/images/c2c/fast-buy/Union.png"
                      style="position: relative; margin: 0 4px 0 8px"
                      alt=""
                    />
                    <span
                      style="color: #1a6ebd; cursor: pointer"
                      @click="openUrl('/exchange')"
                      >{{ $t("message.c2c.huazhuan") }}</span
                    >
                  </div>
                  <span class="err-price tips-box"
                    >{{ tip
                    }}<span
                      v-if="transfer"
                      @click="openUrl('/exchange')"
                      style="
                        color: #1a6ebd;
                        cursor: pointer;
                        text-decoration: underline;
                      "
                      >{{ $t("message.c2c.lijihuazhuan") }}</span
                    ></span
                  >
                </div>
              </div>

              <!-- 收到 -->
              <div class="zhifu">
                <p>{{ $t("message.c2c.wojiangshoudao") }}≈</p>
                <div class="input-se">
                  <el-input
                    v-model="receiveVal"
                    @keyup.native="onlyNumberTwo()"
                    @input="buyInputTwo"
                  >
                    <template #append>
                      <crypto-currency
                        :isbg="true"
                        @selectItem="handleCryptoSelect"
                        class="sele flex"
                      />
                    </template>
                  </el-input>
                </div>
              </div>
              <p class="buy-price">
                {{ $t("message.c2c.cankaodanjia") }}
                {{ currencyInfo.currency_symbol }}{{ price }}
              </p>
            </div>
            <!-- 下一步 -->
            <div v-if="procss === 2" class="confirm-buy">
              <p class="con-b">
                {{
                  procss === 2 && !isBuyActive
                    ? $t("message.c2c.quedingchushou")
                    : $t("message.c2c.querengoumai")
                }}
              </p>
              <p class="con-p">
                {{ currencyInfo.currency_symbol }} {{ buyValue }}
              </p>
              <p class="con-m">
                {{ $t("message.c2c.wojiangshoudao") }}{{ receiveVal }}
                {{ currencyTwo }}
              </p>
              <p class="con-f">
                {{
                  isBuyActive
                    ? $t("message.c2c.tip7")
                    : $t("message.c2c.tip186")
                }}
              </p>
              <!-- 支付方式列表 -->
              <div
                class="con-card"
                :class="[item.isCheck ? 'is-card' : '']"
                v-for="(item, index) in payList"
                @click="selectPay(item)"
                :key="index"
              >
                <div v-if="!isBuyActive">
                  <img
                    v-if="xiaBool"
                    class="Subtract"
                    src="@/assets/images/c2c/want-buy/Subtract.png"
                    @click="xiaBool = false"
                  />
                  <img
                    v-else
                    class="Subtract-r"
                    src="@/assets/images/c2c/want-buy/Subtract.png"
                    @click="xiaBool = true"
                  />
                </div>
                <div class="imgs" v-if="item.isCheck">
                  <img
                    class="img1"
                    src="@/assets/images/c2c/fast-buy/Subtract.png"
                    alt=""
                  />
                  <img
                    class="img2"
                    src="@/assets/images/c2c/fast-buy/Vector.png"
                    alt=""
                  />
                </div>
                <div class="con-ka">
                  <div class="con-ka-top">
                    <span class="shu"></span>
                    <span>{{ item.method_name }}</span>
                  </div>
                  <p>
                    {{ currencyInfo.currency_symbol }} {{ item.advert_price }}
                  </p>
                </div>
                <p class="zuiyou" v-if="index == 0">
                  {{ $t("message.c2c.jiagezuiyou") }}
                </p>

                <div v-if="payList.length == 0" class="card-xia">
                  <el-radio-group v-model="radio">
                    <el-radio :label="3">mr_c-1982</el-radio>
                  </el-radio-group>
                  <!--div class="card-add">
                    <img
                      src="@/assets/images/c2c/fast-buy/VectorA.png"
                      alt=""
                    />
                    <span>{{ $t("message.c2c.tip8") }}</span>
                  </div -->
                </div>
              </div>
              <!-- 添加收款方式 -->
              <div class="card-xia" @click.stop="openUrl('/c2c/user')">
                <div class="card-add flex">
                  <img
                    class="add-img"
                    src="@/assets/images/c2c/fast-buy/VectorA.png"
                    alt=""
                  />
                  <span>{{ $t("message.c2c.tip8") }}</span>
                </div>
              </div>
            </div>
            <!-- 操作按钮 -->
            <div
              class="buy-btn"
              :class="[!isBuyActive ? 'bg-red' : '']"
              @click="next"
            >
              {{
                procss === 2 && !isBuyActive
                  ? $t("message.c2c.quedingchushou")
                  : $t("message.c2c.tip9")
              }}
            </div>
          </div>
        </div>
      </div>
    </template>
  </layout>
</template>

<script>
import OtcOrderHeader from "@/views/c2c/components/otc-order-header/index.vue";
import OtcInput from "@/views/c2c/components/OtcInput.vue";
import FiatCurrency from "../wantBuy/components/fiatCurrency.vue";
import CryptoCurrency from "../wantBuy/components/cryptoCurrency.vue";
import { getParamsLang } from "@/utils/index";
import Axios from "@/api/c2c.js";
import AxiosCurrency from "@/api/currency.js";
import Layout from "@/views/c2c/layout.vue";

export default {
  name: "fastBuy",
  data() {
    return {
      sellError: false,
      priceError: false,
      xiaBool: true,
      procss: 1, //买币进度
      radio: 3,
      errorPrice: false,
      buyValue: "",

      isBuyActive: true, //为true表示购买，为false表示出售

      activeNames: [],
      fiatCurrency: "", // 法币
      showAmount: false, // 显示金额选择
      type: "buy",
      listIndex: 0,
      activeAmount: "",
      initData: {},
      currencyInfo: {},
      currencyTwo: "",
      price: 0, //汇率的价格
      receiveVal: "",
      payList: [],
      session_token: "",
      money: 0,
      tip: "",
      transfer: false,
      timeOutTimer1:null,
      timeOutTimer2:null,
      timeOutTimer3:null,
    };
  },
  // computed: {
  //   ...mapState({
  //     exchangeRateData: (state) => state.exchangeRateData,
  //   }),
  // },
  methods: {
    getParamsLang,
    tabFun(val) {
      this.isBuyActive = val;
      this.buyValue = "";
      this.receiveVal = "";
      this.tip = "";
      this.transfer = false;
    },
    c2cOrderOpenFun(val) {
      let obj = {
        currency: val,
      };
      Axios.c2cOrderOpen(obj).then((res) => {
        if (res.code == "0") {
          this.session_token = res.data.session_token;
        }
      });
    },
    //选择支付方式
    selectPay(item) {
      this.payList.map((item) => {
        item.isCheck = false;
      });
      item.isCheck = true;
    },
    // 选择法币
    handleFiatSelect(val) {
      this.currencyInfo = val;

      let obj = {
        currency: val.title,
      };
      Axios.c2cOrderOpen(obj).then((res) => {
        if (res.code == "0") {
          this.c2cOrderOpenFun(val.title);
          this.initData = res.data;
          this.timeOutTimer1 = setTimeout(() => {
            this.price = this.initData.all_price[this.currencyTwo];
            if (this.buyValue) {
              this.buyInput();
            }
          }, 1000);
          this.getAllMoeny(val.title);
        }
      });
    },
    // 选择虚拟币
    handleCryptoSelect(val) {
      this.currencyTwo = val.currency_symbol;
      this.timeOutTimer2 = setTimeout(() => {
        this.price = this.initData.all_price[this.currencyTwo];
        if (this.buyValue) {
          this.buyInput();
        }
      }, 1000);
    },
    //获取最优价格列表
    getBestPriceAdvert() {
      let language = this.getParamsLang();
      let obj = {
        order_type: "by_amount",
        amount: this.buyValue,
        direction: this.isBuyActive ? "buy" : "sell",
        currency: this.currencyInfo.title,
        symbol: this.currencyTwo,
        language,
      };

      Axios.get_best_price_advert(obj).then((res) => {
        if (res.code == "0") {
          this.payList = res.data; //广告商
          this.payList = this.payList.sort(this.orderListAsc("advert_price"));
          this.payList.map((item) => {
            item.isCheck = false;
            return item;
          });
          this.payList[0].isCheck = true;
        }
      });
    },
    // 快速后买或者出售操作
    open_quick_applyFun() {
      let data = this.payList.filter((item) => {
        return item.isCheck;
      });
      if (data.length == 0) {
        this.$message.error(this.$t("message.c2c.tip156"));
        return;
      }

      let obj = {
        session_token: this.session_token,
        payment_method_id: data[0].payment_method_id,
        order_type: this.isBuyActive ? "by_num":"by_amount",
        amount: this.buyValue,
        coin_amount: this.receiveVal,
        direction: this.isBuyActive ? "buy" : "sell",
        currency: this.currencyInfo.title,
        symbol: this.currencyTwo,
        c2c_advert_id: data[0].advert_id,
      };
      Axios.openQuickApply(obj)
        .then((res) => {
          if (res.code == "0") {
            let isBuy = 1;
            if (this.isBuyActive) {
              isBuy = 1;
            } else {
              isBuy = 2;
            }
            this.$router.push(
              "/c2c/orderSuccess?id=" +
                res.data.order_no +
                "&isBuy=" +
                isBuy +
                "&fiatCurrency=" +
                JSON.stringify(this.currencyInfo)
            );
          }
        })
        .finally(() => {
          // 获取session_token
          this.c2cOrderOpenFun(this.currencyInfo.title);
        });
    },
    //购买输入
    buyInput() {
      if (this.buyValue > 0) {
        if (this.buyValue < 100) {                                                                                 
          if (this.isBuyActive) {
            this.tip =
              this.$t("message.c2c.tip4") + "100" + this.currencyInfo.title;
          } else {
            this.tip =
              this.$t("message.c2c.tip5") + "100" + this.currencyInfo.title;
          }
        } else {
          this.tip = "";
        }
        if (!this.isBuyActive) {
          if (parseFloat(this.buyValue) > parseFloat(this.money)) {
            this.transfer = true;
            this.tip = this.$t("message.c2c.tip6");
          } else {
            this.transfer = false;
          }
        } else {
          this.transfer = false;
        }
        this.changeBuy();
        this.receiveVal = (
          parseFloat(this.buyValue) / parseFloat(this.price)
        ).toFixed(5);
      } else {
        this.receiveVal = "";
      }
    },
    onlyNumber() {
      if (this.buyValue != null) {
        this.buyValue = this.buyValue.replace(/[^\.\d]/g, "");
      }
    },
    onlyNumberTwo() {
      if (this.receiveVal != null) {
        this.receiveVal = this.receiveVal.replace(/[^\.\d]/g, "");
      } else {
        this.receiveVal = "";
      }
    },
    //收到输入
    buyInputTwo() {
      this.receiveVal = this.receiveVal.replace(/[^\.\d]/g, "");
      if (this.receiveVal) {
        this.buyValue = this.receiveVal * this.price;
      } else {
        this.buyValue = "";
      }
    },
    // 按钮操作
    next() {
      if (this.procss === 1) {
        if (!this.buyValue) {
          this.$message.error(this.$t("message.c2c.tip157"));
          return;
        }
        if (this.tip) {
          return;
        }
        this.getBestPriceAdvert();
        this.procss = 2;
        return;
      }
      if (this.procss === 2) {
        this.open_quick_applyFun();
        // this.$router.push({
        //   path: '/c2c/orderSuccess',
        //   query: {
        //     isBuy: this.isBuyActive ? '1' : '0'
        //   }
        // })
      }
    },
    changeBuy(val) {
      const value = this.buyValue;
      if (!this.isBuyActive) {
        this.sellError = this.errorPrice = value < 100;
        return;
      }
      this.errorPrice = value < 100;
    },
    amountBlur() {
      this.timeOutTimer3 = setTimeout(() => {
        this.showAmount = false;
      }, 200);
    },
    orderListAsc(filed, type = "asc") {
      return (a, b) => {
        if (type == "asc")
          return parseFloat(a[filed]) > parseFloat(b[filed]) ? 1 : -1;
        return parseFloat(a[filed]) > parseFloat(b[filed]) ? -1 : 1;
      };
    },
    //全部数量
    getAllMoeny(val) {
      AxiosCurrency.currencyPaypal().then((res) => {
        if (res.code == "0") {
          this.money = res.data.money_wallet;
        }
      });
    },
    openUrl(val) {
      this.$router.push(val);
    },
     // 清除定时器
    unbindTimerHandle(){
      if(this.timeOutTimer1){
        clearTimeout(this.timeOutTimer1)
        this.timeOutTimer1 = null
      }
      if(this.timeOutTimer2){
        clearTimeout(this.timeOutTimer2)
        this.timeOutTimer2 = null
      }
      if(this.timeOutTimer3){
        clearTimeout(this.timeOutTimer3)
        this.timeOutTimer3 = null
      }
    }
  },
  unmounted() {
    this.unbindTimerHandle()
  },
  watch: {
    refreshActive() {
      this.refreshShow = false;
    },
  },
  components: {
    OtcInput,
    FiatCurrency,
    CryptoCurrency,
    OtcOrderHeader,
    Layout,
  },
};
</script>

<style lang="scss" scoped>
@import "@/assets/css/c2c/init.scss";

.is-tab-active {
  color: #fff;
  border-bottom: 1px solid #1d91ff;
}

.is-card {
  border-color: #1b82e4 !important;
}

.bg-red {
  background: #e05461 !important;
}

.is-buy-active {
  color: #afb4bb;
  background: linear-gradient(0deg, #f5f5f5, #f5f5f5),
    linear-gradient(0deg, #ffffff, #ffffff), #ffffff;
  border-radius: 6px 6px 0px 0px;
}

.banner {
  padding: 20px 0 70px;
  background: #14151a;
  position: relative;
  //
  .b-tab {
    height: 560px;
    border-bottom: 1px solid #252627;
    margin-bottom: 47px;
    position: relative;
    .b-buy {
      width: 450px;
      background: #fff;
      border-radius: 6px;
      margin: 0 auto;
      margin-top: 45px;
      z-index: 9;
      position: relative;

      .buy-tab {
        color: #000000;
        height: 48px;
        line-height: 48px;
        display: flex;
        z-index: 100000;
        position: relative;

        div {
          width: 50%;
          font-size: 18px;
          text-align: center;
          cursor: pointer;
        }
      }

      .buy-main {
        padding: 32px 20px;

        .confirm-buy {
          color: #000;
          text-align: center;

          .con-b {
            font-size: 18px;
            margin-bottom: 17px;
          }

          .con-p {
            font-size: 22px;
            font-weight: bold;
            margin-bottom: 5px;
          }

          .con-m {
            font-size: 12px;
            margin-bottom: 35px;
          }

          .con-f {
            font-size: 14px;
            color: #494e57;
            margin-bottom: 10px;
            text-align: left;
          }

          .con-card {
            border: 0.8px solid #ebedf0;
            border-radius: 3px;
            padding: 10px 30px 10px 20px;
            font-size: 12px;
            margin-bottom: 10px;
            position: relative;

            .Subtract {
              position: absolute;
              top: 18px;
              right: 6px;
              transform: translateY(-50%);
              width: 10px;
              height: 5px;
              padding: 10px;
            }

            .Subtract-r {
              position: absolute;
              top: 18px;
              right: 6px;
              transform: translateY(-50%) rotate(180deg);
              width: 10px;
              height: 5px;
              padding: 10px;
            }

            .imgs {
              // position: relative;
              img {
                position: absolute;
              }

              .img1 {
                width: 20px;
                height: 20px;
                right: 0;
                top: 0;
              }

              .img2 {
                right: 2px;
                top: 3px;
                width: 8px;
                height: 6px;
              }
            }

            .con-ka {
              display: flex;
              justify-content: space-between;
              font-size: 14px;

              .shu {
                display: inline-block;
                width: 2px;
                height: 10px;
                margin-right: 6px;
                background: #e6ba41;
                border-radius: 10px;
              }
            }

            .zuiyou {
              margin-top: 5px;
              text-align: right;
              color: #1a6ebd;
              transform: scale(0.8);
              margin-right: -28px;
            }

            .card-xia {
              border-top: 1px dashed #ebedf0;
              margin-top: 5px;
              padding-top: 7px;
              text-align: left;

              .card-add {
                margin-top: 15px;
                display: flex;
                align-items: center;

                img {
                  position: relative !important;
                  margin-right: 11px;
                }
              }
            }
          }
        }

        .zhifu {
          margin-bottom: 35px;
          font-size: 12px;
          color: #494e57;

          p {
            margin-bottom: 5px;
          }

          .input-se {
            position: relative;
            display: flex;

            .sele {
              border-left: none;
              padding: 2px;
              border-radius: 0 3px 3px 0;
            }

            .input-all {
              position: absolute;
              color: #1a6ebd;
              right: 135px;
              top: 18px;
            }

            .err-price {
              color: #bd3f4d;
              position: absolute;
              bottom: -34px;
            }
          }
        }

        .buy-price {
          font-size: 12px;
          color: #727a89;
          margin: 10px 0;
        }

        .buy-btn {
          height: 30px;
          background: #62c887;
          cursor: pointer;
          border-radius: 3px;
          line-height: 30px;
          text-align: center;
          color: #fff;
          font-size: 12px;
        }
      }
    }
  }
}

.buy-active {
  background: #62c887;
  color: #fff;
}

.sell-active {
  background: #e05461;
  color: #fff;
}

.border-1px {
  border: 1px solid #e9ebf1;
}
// 核心区域
.nav {
  background: #ffffff;
  box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.1);
}

.nav-active {
  position: relative;
  color: $blue;

  &:after {
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    content: "";
    border-bottom: 2px solid $blue;
  }
}
:deep(.zhifu) .el-input-group__append {
  padding: 0 1px;
  background-color: transparent;
}
:deep(.zhifu) .el-input__inner {
  height: 53px;
  line-height: 46px;
  border-radius: 3px 0 0 3px;
  border-right: none;
}
</style>
<style lang="scss">
.error-buy {
  border-color: #e05561 !important;

  .el-input__inner {
    border-color: #e05561 !important;
  }
}

.card-xia .el-radio__input.is-checked + .el-radio__label {
  color: #409eff;
}

.card-xia .el-radio__input.is-checked .el-radio__inner {
  border-color: #409eff;
  background: #409eff;
}

.card-add {
  align-items: center;
  padding: 10px 0px;
  color: #494e57;
  font-size: 14px;
  cursor: pointer;
}

.add-img {
  position: relative !important;
  width: 13px;
  height: 13px;
  margin-right: 5px;
}
.huazhuan-price {
  position: absolute;
  bottom: -54px;
}
.tips-box {
  word-break: break-all;
  word-wrap: break-word;
}
</style>
