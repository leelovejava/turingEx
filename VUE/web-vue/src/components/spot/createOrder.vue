<template>
  <!-- 币币交易开仓页面 -->
  <div class="currency-page">
    <div class="currency-page-title">{{ $t("message.jiaoyi.xianhuo") }}</div>
    <div class="place-order-inner-common">
      <!-- 市价 限价 -->
      <div class="okui-tabs" style="height: auto">
        <div class="tabWrapper okui-tabs-pane-list">
          <div class="tabLeft">
            <div
              :class="type == 'opponent' ? 'tabLeftActive' : ''"
              class="tabText"
              @click="entrustBtn('opponent')"
            >
              {{ $t("message.home.shijia") }}
            </div>
            <div
              :class="type == 'limit' ? 'tabLeftActive' : ''"
              class="tabText"
              @click="entrustBtn('limit')"
            >
              {{ $t("message.home.xianjia") }}
            </div>
          </div>
          <div class="question-icon-wrapper" style="position: relative">
            <div class="currency">{{ unit }}</div>
            <span @click="openRecharge">{{ $t("message.jiaoyi.maibi") }}</span>
            <i @click="openRecharge" class="el-icon-arrow-right"></i>
          </div>
        </div>
      </div>
      <!-- 下单 -->
      <div class="place-order-form-box">
        <!-- 买入以法币为单位 -->
        <div class="common-form-box" id="leftPoForm" style="margin-right: 16px">
          <div class="place-order-input-box">
            <div class="input-label-box">
              <span style="color: #929aa5">{{
                $t("message.home.keyong")
              }}</span>
              <span style="margin: 0 4px 0 8px">{{ volume }}</span>
              <span>{{ unit }}</span>
            </div>
            <!-- 价格输入 -->
            <div class="input-item-input">
              <div class="okui-input input-sm input-item-number-input">
                <div class="okui-input-box">
                  <span class="name">{{ $t("message.home.jiage") }}</span>
                  <div class="input-wrapper">
                    <input
                      v-model="buyPrice"
                      inputmode="decimal"
                      max="Infinity"
                      step="0.1"
                      name="price"
                      autocomplete="off"
                      min="0"
                      type="text"
                      class="okui-input-input"
                      autocapitalize="off"
                      autocorrect="off"
                      :disabled="type == 'opponent' ? true : false"
                      style="transition: font-size 100ms ease 0s"
                    />
                    <span class="type">{{ unit }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- 输入 -->
          <div class="place-order-input-box mt-12">
            <!--数量输入  -->
            <div class="input-item-input">
              <div class="okui-input input-sm input-item-number-input">
                <div class="okui-input-box">
                  <input
                    v-model="buyNum"
                    @input="changeBuyNum"
                    inputmode="decimal"
                    max="Infinity"
                    step="0.01"
                    name="total"
                    min="0"
                    :placeholder="$t('message.home.shuliang')"
                    type="text"
                    class="okui-input-input amount-input"
                  />

                  <div class="input-wrapper">
                    <span class="type">{{ unit }} </span>
                  </div>
                </div>
              </div>
            </div>
            <!-- 滑块 -->
            <el-slider
              class="slider-block"
              tooltip-class="tooltip-box"
              @change="changeBuyValue"
              :format-tooltip="(v) => `${v}%`"
              v-model="buySliderValue"
              :marks="marks"
            >
            </el-slider>

            <!-- 成交额 -->
            <div class="input-item-input py-10" v-if="type == 'limit'">
              <div class="okui-input input-sm input-item-number-input">
                <div class="okui-input-box">
                  <input
                    v-model="buyMake"
                    @input="buyMakeFun"
                    inputmode="decimal"
                    max="Infinity"
                    step="0.01"
                    name="total"
                    min="0"
                    :placeholder="$t('message.jiaoyi.chengjiaoe')"
                    type="text"
                    class="okui-input-input amount-input"
                  />

                  <div class="input-wrapper">
                    <span class="type">{{ unit }} </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 按钮 -->
          <div class="submit-btn-box-container">
            <div class="submit-btn-box">
              <button
                v-if="existToken"
                @click="buyFun"
                side="buy"
                type="button"
                class="okui-btn btn-sm btn-fill-green submit-btn long"
              >
                <span class="btn-content">{{ $t("message.home.mairu") }}</span>
              </button>
              <button
                v-else
                side="buy"
                type="button"
                class="okui-btn btn-sm btn-fill-green submit-btn long"
                @click="goRouter('/login')"
              >
                <span class="btn-content">{{
                  $t("message.home.dengluhoujiaoyi")
                }}</span>
              </button>
            </div>
          </div>
        </div>
        <!-- 卖出 -->
        <div class="common-form-box" id="rightPoForm" style="margin-left: 16px">
          <div class="place-order-input-box">
            <div class="input-label-box">
              <span style="color: #929aa5">{{
                $t("message.home.keyong")
              }}</span>
              <span style="margin: 0 4px 0 8px">{{ currencyVolume }}</span>
              <span>{{ getSymbolUpper }}</span>
            </div>

            <div class="input-item-input">
              <div
                class="okui-input input-sm input-item-number-input input-has-value"
              >
                <!-- 价格输入 -->
                <div class="okui-input-box no-padding-right">
                  <span class="name">{{ $t("message.home.jiage") }}</span>
                  <div class="input-wrapper">
                    <input
                      v-model="sellPrice"
                      inputmode="decimal"
                      max="Infinity"
                      step="0.1"
                      name="price"
                      autocomplete="off"
                      min="0"
                      type="text"
                      class="okui-input-input"
                      :disabled="type == 'opponent' ? true : false"
                    />
                    <span class="type">{{ unit }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="place-order-input-box mt-12">
            <!-- 数量输入 -->
            <div class="input-item-input">
              <div class="okui-input input-sm input-item-number-input">
                <div class="okui-input-box">
                  <input
                    v-model="sellNum"
                    @input="changeSellNum"
                    inputmode="decimal"
                    max="Infinity"
                    step="0.01"
                    name="total"
                    autocomplete="off"
                    min="0"
                    :placeholder="$t('message.home.shuliang')"
                    type="text"
                    class="okui-input-input amount-input"
                    autocapitalize="off"
                    autocorrect="off"
                    style="transition: font-size 100ms ease 0s"
                  />
                  <div class="input-wrapper">
                    <span class="type">{{ getSymbolUpper }} </span>
                  </div>
                  <!--                  <div class="okui-input-suffix"></div>-->
                </div>
              </div>
            </div>
            <!-- 滑块 -->
            <el-slider
              class="slider-block"
              tooltip-class="tooltip-box"
              @change="changesSellValue"
              v-model="sellSliderValue"
              :format-tooltip="(v) => `${v}%`"
              :marks="marks"
            >
            </el-slider>

            <!-- 成交额 -->
            <div class="input-item-input py-10" v-if="type == 'limit'">
              <div class="okui-input input-sm input-item-number-input">
                <div class="okui-input-box">
                  <input
                    v-model="sellMake"
                    @input="sellMakeFun"
                    inputmode="decimal"
                    max="Infinity"
                    step="0.01"
                    name="total"
                    min="0"
                    :placeholder="$t('message.jiaoyi.chengjiaoe')"
                    type="text"
                    class="okui-input-input amount-input"
                  />

                  <div class="input-wrapper">
                    <span class="type">{{ unit }} </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- 卖出按钮 -->
          <div class="submit-btn-box-container">
            <div class="submit-btn-box">
              <button
                v-if="existToken"
                @click="sellFun"
                side="buy"
                type="button"
                class="okui-btn btn-sm btn-fill-red submit-btn long"
              >
                <span class="btn-content">{{ $t("message.home.maichu") }}</span>
              </button>
              <button
                v-else
                side="buy"
                type="button"
                class="okui-btn btn-sm btn-fill-red submit-btn long"
                @click="goRouter('/login')"
              >
                <span class="btn-content">{{
                  $t("message.home.dengluhoujiaoyi")
                }}</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 弹窗 -->
    <el-dialog
      :title="typeTitle"
      width="470px"
      class="dialog-box"
      :append-to-body="true"
      v-model="dialogFormVisible"
    >
      <div class="dialog-title">{{ pageData.name }}</div>
      <div class="dia-item">
        <!-- 当前价格 -->
        <div class="dia-item-left-title">
          {{ $t("message.jiaoyi.dangqianjiage") }}
        </div>
        <div class="dia-item-right-title">
          {{ typeSellOrBuy == 0 ? buyPrice : sellPrice }} {{ unit }}
        </div>
      </div>
      <div class="dia-item">
        <div class="dia-item-left-title">
          {{
            typeSellOrBuy == 0
              ? $t("message.jiaoyi.mairu")
              : $t("message.home.maichu")
          }}{{ $t("message.jiaoyi.fangshi") }}
        </div>
        <div class="dia-item-right-title">
          {{
            type == "opponent"
              ? $t("message.home.shijia")
              : $t("message.home.xianjia")
          }}
        </div>
      </div>
      <!-- 数量 -->
      <div class="dia-item">
        <div class="dia-item-left-title">
          {{ $t("message.jiaoyi.shuliang") }}
        </div>
        <div class="dia-item-right-title">
          <div class="dia-item-right-title">
            {{
              `${
                typeSellOrBuy == 0
                  ? ((buyNum - buyFee) / buyPrice).toFixed(8)
                  : sellNum
              }  ${getSymbolUpper}`
            }}
          </div>
        </div>
      </div>
      <!-- 总价 -->
      <div class="dia-item">
        <div class="dia-item-left-title">
          {{ $t("message.jiaoyi.zongjia") }}
        </div>
        <div class="dia-item-right-title">
          <div class="dia-item-right-title">
            {{ `${typeSellOrBuy == 0 ? buyNum : sellNum * sellPrice} ${unit}` }}
          </div>
        </div>
      </div>
      <div class="tip-box">
        <div @click="liveData(true)" v-if="!isCheck">
          <img class="select-img" src="@/assets/images/quotes/select.png" />
        </div>
        <div @click="liveData(false)" v-if="isCheck">
          <img
            class="select-img"
            src="@/assets/images/quotes/select-check.png"
          />
        </div>
        {{ $t("message.jiaoyi.xiacibuzaitishi") }}
      </div>
      <div class="buts-box">
        <button class="but-one" @click="dialogFormVisible = false">
          {{ $t("message.jiaoyi.quxiao") }}
        </button>
        <button class="but-two" @click="buyAndsell">
          {{ $t("message.jiaoyi.xianhuo")
          }}{{
            typeSellOrBuy == 0
              ? $t("message.jiaoyi.mairu")
              : $t("message.home.maichu")
          }}
        </button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import Axios from "@/api/currency.js";
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";
import bus from "vue3-eventbus";
import quotesAxios from "@/api/quotes.js";

export default {
  emits: ["refresh", "showlist"],
  name: "currencyTrade",
  props: {
    pageData: {
      type: Object,
      default: function () {
        return {};
      },
    },
    clickData: {
      type: String,
    },
    unit: {
      type: String,
    },
    pageType: {
      type: String,
    },
    paramsType:{
      type:String,
      default:'indices'
    }
  },
  data() {
    return {
      type: "opponent", //类型
      buyFee: "0", //买入手续费
      volume: "", //USDT钱包余额
      buySessionToken: "",
      sellFee: "0", //卖出手续费
      currencyVolume: "", //币种钱包余额
      sellSessionToken: "",
      symbol: "btc", //币种
      buyNum: "", //买入数量
      buyPrice: "", //买入的交易价格
      buyGetVolume: "", //买入可得币的数量
      sellNum: "", //卖出数量
      sellPrice: "", //卖出的交易价格
      sellGetVolume: "", //卖出可得USDT数量
      buySliderValue: 0, //买入的进度条初始值
      sellSliderValue: 0, //卖出的进度条初始值
      buyAmount: "",
      sellAmount: "",
      dialogFormVisible: false,
      isCheck: false,
      typeTitle: "",
      typeSellOrBuy: 0,
      marks: {
        0: "0",
        25: "25%",
        50: "50%",
        75: "70%",
        100: "100%",
      },
      buyMake: "", //成交额计算
      sellMake: "", //成交额计算
      timer: null,
      timeOutTimer1: null,
      timeOutTimer2: null,
    };
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),

    getSymbolUpper() {
      if (typeof this.pageData.symbol_data == "string") {
        // 数字货币用.symbol_data,其他用symbol
        return ["coin"].includes(this.pageType)
          ? this.pageData.symbol_data.toUpperCase()
          : this.pageData.symbol.toUpperCase();
      }
    },
  },
  unmounted() {
    // 页面销毁时清除定时器
    clearInterval(this.timer);
    this.timer = null

    if(this.timeOutTimer1){
      clearTimeout(this.timeOutTimer1)
      this.timeOutTimer1 = null
    }
    if(this.timeOutTimer2){
      clearTimeout(this.timeOutTimer2)
      this.timeOutTimer2 = null
    }
  },
  watch: {
    clickData(val) {
      this.buyPrice = val;
      this.sellPrice = val;
    },
    "$route.params.id"(val) {
      this.getSellView();
    },
    pageData(val) {
      // this.pageData = val; //干嘛
      if (this.type == "opponent") {
        this.buyPrice = val.close;
        this.sellPrice = val.close;
      }
    },
    buyPrice(val) {
      if (this.type == "limit") {
        this.entrustBtn();
      }
    },
  },
  mounted() {
    if (this.type !== "opponent") {
      this.buyPrice = this.pageData.close;
      this.sellPrice = this.pageData.close;
    }

    this.isCheck = localStorage.getItem("isCheck") || false;

    if (this.existToken) {
      this.timer = setInterval(() => {
        this.getBuyView();
        this.getSellView();
      }, 2000);
    }
    this.getExchangerateuser();
  },

  methods: {
    // 通过成交额卖出
    sellMakeFun() {
      this.sellMake = this.sellMake.match(/^\d*(\.?\d{0,5})/g)[0];
      this.sellNum = (this.sellMake / this.sellPrice).toFixed(5);
      if (parseFloat(this.sellNum) >= parseFloat(this.currencyVolume)) {
        this.sellNum = this.formatDecimal(this.currencyVolume, 5);
        this.sellMake = (
          parseFloat(this.sellNum) * parseFloat(this.sellPrice)
        ).toFixed(5);
      }
      this.sellSliderValue = (this.sellNum / this.currencyVolume) * 100;
    },
    //通过成交额买入
    buyMakeFun() {
      this.buyMake = this.buyMake.match(/^\d*(\.?\d{0,5})/g)[0];
      if (parseFloat(this.buyMake) >= parseFloat(this.volume)) {
        this.buyMake = this.volume.toFixed(5);
      }
      this.buyNum = this.buyMake; //成交额 = 成交数量，买入是以U为单位
      if (this.buyNum.toString().indexOf(".") != -1) {
        let str = this.buyNum
          .toString()
          .substring(
            this.buyNum.toString().indexOf("."),
            this.buyNum.toString().length
          );
        if (str.length > 5) {
          this.buyNum = this.formatDecimal(this.buyNum, 5);
        }
      }
      //算滑块的数值
      this.buySliderValue = (this.buyNum / parseFloat(this.volume)) * 100;
    },
    //获取汇率
    getExchangerateuser() {
      quotesAxios.getExchangerateuserconfigInfo().then((res) => {});
    },
    //路由跳转
    goRouter(params) {
      this.$router.push(params);
    },
    buyAndsell() {
      if (this.typeSellOrBuy == 0) {
        this.buyFun();
      } else {
        this.sellFun();
      }
    },
    liveData(val) {
      this.isCheck = val;
      if (val) {
        localStorage.setItem("isCheck", true);
      } else {
        localStorage.setItem("isCheck", false);
      }
    },
    //委托类型切换
    entrustBtn(type) {
      if (type) {
        this.type = type;
      }
      this.buySliderValue = 0; //买入的进度条初始值
      this.sellSliderValue = 0; //卖出的进度条初始值
      this.buyNum = "";
      this.sellNum = "";
      this.buyMake = ""; //成交额计算
      this.sellMake = ""; //成交额计算
    },
    //买入开仓页面参数
    getBuyView() {
      Axios.BuyOpenView({type:this.paramsType}).then((res) => {
        this.buyFee = res.data.fee;
        this.volume = res.data.volume;
        this.buySessionToken = res.data.session_token;
      });
    },
    //买入数量改变，计算可买入的币种数量
    changeBuyNum() {
      if (this.buyNum) {
        this.buyNum = this.buyNum.match(/^\d*(\.?\d{0,5})/g)[0]; //保留五位小数
      }

      if (parseFloat(this.buyNum) > parseFloat(this.volume)) {
        this.buyNum = this.formatDecimal(this.volume, 5); //允许输入的最大值
      }

      this.buySliderValue = ((this.buyNum / this.volume) * 100).toFixed(2);
    },
    //卖出数量改变
    changeSellNum() {
      if (this.sellNum) {
        this.sellNum = this.sellNum.match(/^\d*(\.?\d{0,5})/g)[0];
      }

      if (parseFloat(this.sellNum) > parseFloat(this.currencyVolume)) {
        this.sellNum = this.formatDecimal(this.currencyVolume, 5);
      }
      //计算滑块的值,保留两位小数
      this.sellSliderValue = (
        (this.sellNum / parseFloat(this.currencyVolume)) *
        100
      ).toFixed(2);

      //计算出成交额
      this.sellMake = this.formatDecimal(this.sellPrice * this.sellNum, 5);
    },

    // 卖币
    buyFun() {
      if (this.dialogFormVisible) {
        this.buyCurrency();
        return;
      }
      this.typeSellOrBuy = 0;
      this.typeTitle =
        this.type == "opponent"
          ? this.$t("message.jiaoyi.shijiamairuqueren")
          : this.$t("message.jiaoyi.xianjiamairuqueren");
      if (!this.isCheck) {
        this.beforeBuyCheck();
      } else {
        this.buyCurrency();
      }
    },
    beforeBuyCheck() {
      if (!this.buyPrice) {
        ElMessage.warning(this.$t("message.home.qingshurujiage"));
        return;
      }

      if (!this.buyNum) {
        ElMessage.warning(this.$t("message.home.qingshurushuliang"));
        return;
      }

      if (this.volume <= 0) {
        ElMessage.warning(this.$t("message.home.yuebuzuqingxianchongzhi"));
        return;
      }
      this.dialogFormVisible = true;
    },
    sellFun() {
      if (this.dialogFormVisible) {
        this.sellCurrency();
        return;
      }
      this.typeSellOrBuy = 1;
      this.typeTitle =
        this.type == "opponent"
          ? this.$t("message.jiaoyi.shijiamaichuqueren")
          : this.$t("message.jiaoyi.xianjiamaichuqueren");
      if (!this.isCheck) {
        this.beforeSellCheck();
      } else {
        this.sellCurrency();
      }
    },
    //调用买入接口
    buyCurrency() {
      this.beforeBuyCheck();
      Axios.currencyBuy({
        volume: this.buyNum, //以U为单位下单
        session_token: this.buySessionToken,
        symbol: this.pageData.symbol,
        price: this.buyPrice,
        order_price_type: this.type,
        total:(this.buyNum/this.buyPrice).toFixed(5)
      })
        .then((res) => {
          this.dialogFormVisible = false;
          if (res.code == 0) {
            ElMessage.success(this.$t("message.home.caozuochenggong"));
            bus.emit("showlist", { type: this.type });
            this.timeOutTimer1 = setTimeout(() => {
              this.$emit("refresh");
            }, 1000);
          }
        })
        .finally(() => {
          this.dialogFormVisible = false;
          this.buySliderValue = 0;
          this.buyNum = "";
          this.buyMake = "";
        });
    },
    //卖出页面参数
    getSellView() {
      if(this.$route.params.id == 'undefined'){
        return;
      }

      Axios.SellOpenView({
        symbol: this.$route.params.id,
      }).then((res) => {
        this.sellFee = res.data.fee;
        this.currencyVolume = res.data.volume;
        this.sellSessionToken = res.data.session_token;
      });
    },
    //卖出
    beforeSellCheck() {
      if (!this.sellPrice) {
        ElMessage.warning(this.$t("message.home.qingshurujiage"));
        return;
      }
      if (!this.sellNum) {
        ElMessage.warning(this.$t("message.home.qingshurushuliang"));
        return;
      }
      if (this.sellNum == 0 || this.sellNum == 0.0) {
        ElMessage.warning(this.$t("message.home.qingshurushuliang"));
        return;
      }
      this.dialogFormVisible = true;
    },
    // 卖币
    sellCurrency() {
      this.beforeSellCheck();
      Axios.currencySell({
        volume: this.sellNum,
        session_token: this.sellSessionToken,
        symbol: this.pageData.symbol,
        price: this.sellPrice,
        order_price_type: this.type,
      })
        .then((res) => {
          if (res.code == 0) {
            ElMessage.success(this.$t("message.home.caozuochenggong"));
            this.timeOutTimer2 = setTimeout(() => {
              this.$emit("refresh");
            }, 1000);
          }
        })
        .finally(() => {
          this.dialogFormVisible = false;
          this.sellSliderValue = 0; //卖出的进度条初始值
          this.sellNum = ""; //清空
          this.sellMake = "";
          // setTimeout(() => {
          //   this.getSellView();
          //   this.getBuyView();
          // }, 1000)
        });
    },

    // 滑块计算买的数量,以U为单位
    changeBuyValue() {
      const val = String((this.volume * this.buySliderValue) / 100);
      this.buyNum = val.match(/^\d*(\.?\d{0,5})/g)[0];
      this.buyMake = this.buyNum;
    },
    //滑块
    changesSellValue() {
      this.sellNum = (this.currencyVolume * this.sellSliderValue) / 100;
      this.sellNum = this.formatDecimal(this.sellNum, 5);
      this.changeSellNum();
    },
    //跳转充值
    openRecharge() {
      this.$router.push("/recharge");
    },
    formatDecimal(num, decimal) {
      num = num.toString();
      let index = num.indexOf(".");
      if (index !== -1) {
        num = num.substring(0, decimal + index + 1);
      } else {
        num = num.substring(0);
      }
      return parseFloat(num).toFixed(decimal);
    },
  },
};
</script>
<style scoped>
@import url("@/assets/css/commonTrade/perpeContract.css");

:deep(.el-popover),
.el-dropdown-menu {
  background: rgb(96, 102, 114);
  color: rgb(255, 255, 255);
}

.el-dropdown-menu {
  padding: 0;
}

.el-dropdown-menu__item {
  color: #fff;
}

.el-dropdown-menu__item:hover {
  background: #484d56;
  color: #fff;
}

.dia-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
}

.dia-item-left-title {
  color: #929aa5;
}

.dia-item-right-title {
  color: #fff;
  font-size: 16px;
}

.dialog-title {
  font-size: 16px;
  color: #fff;
  font-weight: bold;
  padding: 10px 0;
}

.tip-box {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #929aa5;
  margin-top: 50px;
}

.select-img {
  width: 16px;
  height: 16px;
  margin-right: 5px;
}

.buts-box {
  display: flex;
  margin-top: 20px;
}

.but-one {
  background: #484d56;
  border-radius: 5px;
  width: 150px;
  height: 40px;
  color: #fff;
}

.but-two {
  background: #1d91ff;
  border-radius: 5px;
  margin-left: 15px;
  flex: 1;
  color: #fff;
}

.py-10 {
  margin: 10px 0;
}

.make-input {
  width: 200px;
  text-align: right;
}

.slider-block {
  height: 68px;
  width: 96%;
}

:deep(.el-slider__bar) {
  height: 2px;
}

:deep(.el-slider__runway) {
  height: 2px;
  background: rgb(64, 64, 64);
}

:deep(.el-slider__button) {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-top: -4px;
}

:deep(.el-slider__marks-stop) {
  height: 8px;
  width: 8px;
  margin-top: -3px;
  border-radius: 50%;
}

/* 头部 */
.tabWrapper {
  line-height: 36px;
  height: 36px;
}

.tabLeft {
  flex: 1;
  display: flex;
}

.tabLeftActive {
  color: #1d91ff !important;
}

.tabText {
  margin-right: 24px;
  color: #707a8a;
  cursor: pointer;
}

.currency-page-title {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 48px;
  font-size: 14px;
  color: #fff;
  /* background: #1e2025;
  border-right: 1px solid #24272c; */
}

.currency-page-title:before {
  content: " ";
  position: absolute;
  top: 0;
  left: 0;
  display: block;
  width: 100%;
  height: 2px;
  background: #1d91ff;
}

.place-order-inner-common {
  /*padding: */
  padding: 30px 20px !important;
  /* background: #1e2025 !important; */
  height: 370px;
}

.okui-tabs-pane {
  font-size: 14px;
  border: none !important;
}

.okui-tabs-pane-grey.okui-tabs-pane-underline-active,
.okui-tabs-pane-grey.okui-tabs-pane-underline:hover {
  color: #1d91ff !important;
}

.okui-tabs-pane-list {
  border: none !important;
}

.question-icon-wrapper {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.question-icon-wrapper .currency {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 32px;
  height: 16px;
  background: #243046;
  border-radius: 2px;
  font-size: 12px;
  color: #1d91ff;
}

.question-icon-wrapper span {
  margin-left: 5px;
  margin-right: 10px;
  text-decoration: underline;
  color: #929aa5;
}

:deep(.question-icon-wrapper) .el-icon-arrow-right {
  position: relative;
  top: 2px;
  color: #929aa5;
}

.place-order-form-box .place-order-input-box {
  margin-top: 5px;
}

.input-label-box {
  margin-bottom: 8px;
  font-size: 12px;
}

.okui-input.input-sm .okui-input-box {
  position: relative;
  height: 40px;
  padding: 0 12px;
  background: #2a2d34;
  border: none;
}

.okui-input.input-sm .okui-input-box .type {
  margin-left: 5px;
  font-size: 14px;
  color: #fff;
}

.okui-input-box .input-wrapper {
  position: absolute;
  top: 50%;
  right: 12px;
  transform: translateY(-50%);
}

.okui-input-box .name {
  font-size: 14px !important;
  color: #929aa5 !important;
}

.okui-input-input {
  width: 80px;
  text-align: right;
  font-size: 14px !important;
}
.amount-input {
  text-align: left;
}

.okui-input-input::placeholder {
  font-size: 14px !important;
  color: #929aa5 !important;
}

.place-order-form-box .mt-12 {
  margin-top: 12px;
}

.place-order-form-box .submit-btn-box-container {
  margin-top: 12px;
}

.shuju {
  width: 110px;
  height: 322px;
  background: #1f2328;
  border: 1px solid #30353c;
  border-radius: 3px;
  box-sizing: border-box;
  font-size: 14px;
  color: #fff;
  overflow-y: auto;
}

.shuju-title {
  background: #2c3138;
  color: #868e9b;
}

.shuju-main {
  cursor: pointer;
  padding: 5px 0;
}

.shuju-main:hover {
  background: #484d56;
}

.shuju p {
  height: 28px;
  line-height: 28px;
  margin-bottom: 10px;
  padding-left: 15px;
}
</style>

<style lang="scss">
.dialog-box {
  background: #1f2328 !important;
  border-radius: 5px !important;
  .el-dialog__title {
    color: #fff;
  }
}
</style>
