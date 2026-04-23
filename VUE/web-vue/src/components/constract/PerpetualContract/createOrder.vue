<template>
  <div>
    <div class="perpeCreateOrder">
      <!-- 开仓+ 平仓 -->
      <div class="titles">
        <span
          v-for="(item, index) in title"
          :key="index"
          :class="{ active: openIndex == index }"
          @click="changeTitle(item, index)"
          >{{ item }}</span
        >
      </div>
      <!-- 全仓+杠杆 -->
      <div class="crossSettingBox">
        <div class="crossSettingItem" style="margin-right: 20px">
          {{ $t("message.home.quancang") }}
        </div>
        <div
          class="crossSettingItem"
          :style="{
            cursor:
              isActive == 0 && origin_lever.length > 1
                ? 'pointer'
                : 'not-allowed',
          }"
        >
          <div @click="openLeverDialog">
            <span> {{ current_lever_rate }}</span
            ><i class="icon iconfont icon-Mul_triangle lever-icon"></i>
          </div>
        </div>
      </div>
      <!-- 限价+市价 -->
      <el-tabs
        v-model="activeName"
        v-if="existToken"
        @tab-click="handleTabClick"
      >
        <el-tab-pane :label="$t('message.home.shijia')" name="opponent" />
        <el-tab-pane :label="$t('message.home.xianjia')" name="limit" />
      </el-tabs>
      <!-- 下单 -->
      <div class="place-order-form-box">
        <div class="common-form-box" id="leftPoForm">
          <div class="avaliable-USD">
            <div>{{ $t("message.jiaoyi.heyuezongzican") }}</div>
            <div>{{ availableMoney }} {{ unit }}</div>
          </div>
          <!-- 价格输入 -->
          <div class="place-order-input-box" v-show="activeName === 'limit'">
            <div class="input-label-box">
              {{ $t("message.home.jiage") }}
            </div>
            <div class="input-item-input">
              <div
                class="okui-input input-sm input-item-number-input input-has-value"
              >
                <div class="priceInput okui-input-box no-padding-right">
                  <input
                    inputmode="decimal"
                    max="Infinity"
                    step="0.1"
                    placeholder=""
                    name="price"
                    autocomplete="off"
                    min="0"
                    type="text"
                    class="okui-input-input"
                    v-model.number.trim="price"
                  />
                  <span class="cols">
                    <span @click="getNewPrice">{{
                      $t("message.home.zuixinjia1")
                    }}</span>
                    {{ unit }}
                  </span>
                </div>
              </div>
            </div>
          </div>
          <!-- 张数输入 -->
          <amount-slider
            ref="sliderRef"
            :maxAmount="getVolumnByLever()"
            :closeBuyAmount="closeBuyAmount"
            :closeSellAmount="closeSellAmount"
            :symbol="newPageData.symbol"
            :lever_rate="current_lever_rate_num"
            :typeNum="isActive"
            @getAmount="getAmount"
          ></amount-slider>
          <!-- 止盈止损-->
          <div v-show="isActive === 0">
            <el-checkbox v-model="isChecked" type="checkbox">
              {{ $t("message.jiaoyi.zhiyingzhisun") }}
            </el-checkbox>
            <div v-show="isChecked">
              <div class="trading-session-box">
                <div class="trading-input-box">
                  <div class="input-box">
                    <input
                      class="trading-input"
                      v-model.number="takeProfitPrice"
                      type="text"
                      :placeholder="$t('message.jiaoyi.zhiying')"
                    />
                  </div>
                </div>
              </div>
              <div class="trading-session-box" style="margin-top: 12px">
                <div class="trading-input-box">
                  <div class="input-box">
                    <input
                      class="trading-input"
                      v-model.number="stopProfitPrice"
                      type="text"
                      :placeholder="$t('message.jiaoyi.zhisun')"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- 操作按钮-->
          <div class="btns" v-if="existToken">
            <!-- 开仓显示做多买入，平仓显示平多 = 平掉做多的订单= 做空买出，平多就根据第一个订单来平张数 -->
            <div class="btn-wrapper" v-if="openIndex == 0">
              <div class="submit-btn-box">
                <button
                  side="buy"
                  type="button"
                  class="okui-btn btn-sm btn-fill-green submit-btn long"
                  @click="buyAndSell('buy')"
                >
                  <span class="btn-content">{{
                    existToken
                      ? isActive == 0
                        ? $t("message.home.kaiduo1")
                        : $t("message.home.pingduo")
                      : $t("message.home.dengluhoujiaoyi")
                  }}</span>
                </button>
              </div>
            </div>
            <!-- 做空 -->
            <div class="btn-wrapper" v-if="openIndex == 1">
              <div class="submit-btn-box">
                <button
                  side="sell"
                  type="button"
                  class="okui-btn btn-sm btn-fill-red submit-btn short"
                  @click="buyAndSell('sell')"
                >
                  <span class="btn-content">{{
                    existToken
                      ? isActive == 0
                        ? $t("message.home.kaikong1")
                        : $t("message.home.pingkong")
                      : $t("message.home.dengluhoujiaoyi")
                  }}</span>
                </button>
              </div>
            </div>
          </div>
          <!-- 提示数据 -->
          <div class="submit-info" v-if="existToken && isActive == 0">
            <!-- 合约金额=保证金-->
            <div class="submit-info-item">
              <div>{{ $t("message.home.heyuejine") }}:</div>
              <div>{{ getMargin }} {{ unit }}</div>
            </div>
            <!-- 保证金 -->
            <div class="submit-info-item">
              <div>{{ $t("message.home.baozhengjin") }}:</div>
              <div>{{ getMargin }} {{ unit }}</div>
            </div>
            <!-- 手续费 -->
            <div class="submit-info-item">
              <div>{{ $t("message.home.shouxufei") }}:</div>
              <div>{{ getFee }} {{ unit }}</div>
            </div>
          </div>

          <div class="noLogin" v-if="!existToken">
            <div @click="gologin(1)">{{ $t("message.home.lijizhuce") }}</div>
            <div @click="gologin(2)">{{ $t("message.home.denglu") }}</div>
          </div>
        </div>
      </div>
    </div>
    <!-- 杠杆弹窗,开仓才展示 -->
    <lever-dialog-vue
      :current_lever="current_lever_rate_num"
      ref="leverDialog"
      :symbol="newPageData.symbol"
      :lever_rate="origin_lever"
      :sessionObj="sessionObj"
      :unit="unit"
      v-if="isActive == 0 && origin_lever.length > 1"
    />
    <!-- 平仓弹窗 -->
    <close-dialog
      ref="closeDialog"
      :type="activeName"
      :sellOrBuy="directionName"
      :info="getSubmitInfo('close')"
      :newPrice="newPageData.close"
      :unit="unit"
      :pageData="pageData"
    ></close-dialog>
    <!-- 开仓弹窗 -->
    <open-dialog
      ref="openDialog"
      :type="activeName"
      :sellOrBuy="directionName"
      :info="getSubmitInfo('open')"
      :newPrice="newPageData.close"
      :unit="unit"
      :pageData="pageData"
    ></open-dialog>
  </div>
</template>
<script>
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";
import Axios from "@/api/perpetualContract.js";
import closeDialog from "./orderCom/closeDialog.vue";
import openDialog from "./orderCom/openDialog.vue";
import amountSlider from "./orderCom/amountSlider.vue";
import leverDialogVue from "./orderCom/leverDialog.vue";
import bus from "vue3-eventbus";
import { ElMessage } from "element-plus";

export default {
  name: "perpeCreateOrder",
  props: {
    // 当前币种的信息
    pageData: {
      type: Object,
      default: function () {
        return {};
      },
    },
    unit: {
      type: String,
    },
    // 最新的价格
    clickData: {
      type: String,
    },
  },
  data() {
    return {
      title: [
        this.$t("message.home.kaiduo1"),
        this.$t("message.home.kaikong1"),
      ],
      newPageData: {}, //替代pageData,因为不能修改props的数据
      isActive: 0, //开仓0or平仓1，弃用，默认一直是开仓
      openIndex: 0, //开多0or平空1
      activeName: "opponent", //限价or市价,默认市价
      directionName: "", //buy Or sell
      price: undefined, //输入的价格
      sessionObj: {}, //

      lever_rate: ["1.00X"], // 后端返回的杠杆 ['1.00X','2.00X',...]
      origin_lever: [], // 支持的杠杆 [1,2,3]
      current_lever_rate: "", //当前选中
      current_lever_rate_num: 1, //当前选中的杠杆数字 1

      initClose: {},
      initOpen: {},
      inputAmount: "0.00",
      availableMoney: "0.00",
      closeBuyAmount: 0,
      closeSellAmount: 0,
      isNewPrice: 0,
      timer: null,
      isChecked: false,
      takeProfitPrice: "",
      stopProfitPrice: "",
      timeOutTimer1:null,
      timeOutTimer2:null,
    };
  },
  components: {
    leverDialogVue,
    closeDialog,
    openDialog,
    amountSlider,
  },
  mounted() {
    // 登录之后才能调用
    if (this.existToken) {
      this.initContractparams();
      this.timer = setInterval(() => {
        this.initContractparams();
      }, 2000);
    }
    this.price = this.newPageData.close;

    // 下单成功后，清除下单区数据以及初始化参数
    bus.on("cleanOrderData", () => {
      this.$refs.sliderRef?.cleanAmount();
      this.$refs.sliderRef?.emptyValue();
      this.sliderAmount = undefined;
      this.takeProfitPrice = undefined;
      this.stopProfitPrice = undefined;
    });
    // params：type类型
    bus.on("getSesstionToken", (type) => {
      this.timeOutTimer1 = setTimeout(() => {
        this.initContractparams({ type });
      }, 2000); //延迟调用
    });
    bus.on("moneyContract", (val) => {
      this.availableMoney = val;
    }); //合约可用余额
  },
  //销毁定时器
  unmounted() {
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
  computed: {
    ...mapState(useUserStore, ["existToken"]),

    // 开仓弹窗的信息
    getSubmitInfo: function () {
      let self = this;
      const symbol = this.$route.params.id;
      return function (type) {
        return {
          price: self.price,
          lever_rate: self.current_lever_rate_num,
          amount: self.inputAmount, //从滑块组件取值
          symbol, //当前币种

          session_token:
            type === "close"
              ? self.initClose?.session_token
              : self.initOpen?.session_token,
          stop_price_profit: self.takeProfitPrice,
          stop_price_loss: self.stopProfitPrice,
          // volume: self.getVolumnByLever(),
        };
      };
    },
    // 获取合约金额
    getContractAmount: function () {
      const amount = this.inputAmount;
      let data = "0.00";
      if (amount != undefined && this.lever_rate != undefined) {
        if (this.lever_rate?.length > 0) {
          data = this.sessionObj?.amount * amount * this.current_lever_rate_num;
        } else if (this.lever_rate.length == 0) {
          data = this.sessionObj?.amount * amount * 1;
        }
      }
      return bigDecimal.round(data, 2);
    },
    // 获取保证金，开仓才需要
    getMargin: function () {
      const amount = this.inputAmount;
      let data = "0.00";
      if (
        this.isActive === 0 &&
        amount != undefined &&
        this.lever_rate.length != undefined &&
        this.sessionObj.amount
      ) {
        if (this.lever_rate.length > 0) {
          data = this.sessionObj.amount * amount * this.current_lever_rate_num;
        } else if (this.lever_rate.length == 0) {
          data = this.sessionObj.amount * amount * 1;
        }
      }
      return bigDecimal.round(data, 2);
    },
    // 获取手续费
    getFee: function () {
      const amount = this.inputAmount;
      let data = "0.00";
      if (amount && this.isActive == 0) {
        // 每手的数量*多少手（张）*每手的手续费
        data = amount * this.sessionObj.fee * this.current_lever_rate_num;
      }
      return bigDecimal.round(data, 2);
    },
  },
  watch: {
    // 当杠杆调整之后，张数输入框以及滑块归零
    current_lever_rate(val) {
      this.$refs.sliderRef?.emptyValue();
      this.$refs.sliderRef?.cleanAmount();
      this.inputAmount = "0.00";
    },
    // 市价单，主动赋值
    clickData(val) {
      //正好处在数据没变动的时候，那就不会被赋值
      if (this.activeName == "opponent") {
        this.price = val;
      }
    },
    // 币种数据
    pageData(val) {
      this.newPageData = val;
      if (this.isNewPrice == 0) {
        this.getNewPrice();
        this.isNewPrice = this.isNewPrice + 1;
      }
    },

    "$route.params.id"(val) {
      // 用于在路由变化之后更新数据
      this.timeOutTimer2 = setTimeout(() => {
        this.handleTabClick();
      }, 1000);

      this.initContractparams({
        needClearLever: true,
      });
    },
  },
  methods: {
    //获取张数
    getAmount(val) {
      this.inputAmount = val;
    },
    // 切换限价单和市价单
    handleTabClick(tab) {
      //tab, tab.index tab.name
      this.price = this.newPageData.close;
      this.$refs.sliderRef?.cleanAmount();
      this.$refs.sliderRef?.emptyValue();
      this.inputAmount = "0.00";
    },
    getNewPrice() {
      this.price = this.newPageData.close;
    },
    // 切换开仓和平仓
    changeTitle(item, index) {
      this.openIndex = index;
      this.$refs.sliderRef?.cleanAmount();
      this.$refs.sliderRef?.emptyValue();
    },
    // 获取张数,数据转换
    getVolumnByLever() {
      let amount = this.initOpen.volume;
      if (this.isActive == 1) {
        amount = Math.max(this.closeSellAmount, this.closeBuyAmount);
      }
      if (!amount) {
        return 0;
      } else {
        return amount*1;
      }
    },

    gologin(type) {
      //  跳登录注册
      if (type == 1) {
        this.$router.push("/register");
      } else if (type == 2) {
        this.$router.push("/login");
      }
    },
    // 买卖按钮
    buyAndSell(type) {
      this.directionName = type;
      if (!this.existToken) {
        this.$router.push("/login");
      } else {
        if (!this.price) {
          const text = this.$t("message.home.qingshurujine");
          ElMessage(text);
          return;
        }

        if (
          !this.$refs.sliderRef?.amount ||
          this.$refs.sliderRef?.amount == 0
        ) {
          const text = this.$t("message.home.qingshurushuliang");
          ElMessage(text);
          return;
        }
        if (this.isActive === 0) {
          // 开仓弹窗
          this.$refs.openDialog.open();
        } else {
          // 平仓弹窗
          this.$refs.closeDialog.open();
        }
      }
    },
    // 初始化合约
    initContractparams({ type, needClearLever = false } = {}) {
      if(this.$route.params.id == 'undefined'){
        return;
      }
      //平仓初始化参数
      const symbol = this.$route.params.id;
      if (type === "close" || !type) {
        Axios._initClose({
          symbol,
          direction: "sell",
        }).then((res) => {
          // 平仓的杠杆不能调整
          //返回数据只有session_token和amount，lever_rate
          this.initClose = res.data;
          this.closeSellAmount = res.data.amount;
        });

        Axios._initClose({
          symbol,
          direction: "buy",
        }).then((res) => {
          this.closeBuyAmount = res.data.amount;
        });
      }

      //开仓初始化参数
      if (type === "open" || !type) {
        Axios._initOpen({ symbol: this.$route.params.id }).then((res) => {
          if (res.code == "0") {
            this.initOpen = res.data;
            this.sessionObj = res.data; //展示数据都用开仓的，只有session取值不一样
            bus.emit("closeTrade", res.data?.open); //是否开仓
            const { lever } = res.data;
            const hasLever = lever && lever.length;
            if (hasLever) {
              this.lever_rate = lever.map((val) => {
                return Number(val.lever_rate).toFixed(2) + "X";
              });

              this.origin_lever = lever.map((val) => {
                return val.lever_rate;
              });
            }
            // 切换币种的时候杠杆需要初始化
            if (!this.current_lever_rate || needClearLever) {
              this.current_lever_rate = hasLever ? this.lever_rate[0] : "1.00X";
              this.current_lever_rate_num = hasLever
                ? this.origin_lever[0]
                : "1";
            }
          }
        });
      }
    },
    openLeverDialog() {
      this.$refs.leverDialog.open();
    },
    // 杠杆修改后
    changeLever(newLever) {
      const formatLever = `${newLever}.00X`;
      this.current_lever_rate = formatLever; //1.00X
      this.current_lever_rate_num = newLever;
    },
  },
};
</script>
<style lang="scss" scoped>
@import url("@/assets/css/commonTrade/perpeContract.css");
@import url("@/assets/css/commonTrade/global.css");

.perpeCreateOrder {
  background: #1f2328;
  padding: 0 12px;
  height: 610px;
}
.crossSettingBox {
  display: flex;
  justify-content: space-between;
}
.crossSettingItem {
  background: #2c3138;
  border-radius: 4px;
  width: 100%;
  text-align: center;
  height: 32px;
  line-height: 32px;
}

.btn-content {
  font-size: 13px;
}
.avaliable-USD {
  padding-top: 6px;
  display: flex;
  justify-content: space-between;
}

/* 以上是修改的样式 */
.titles {
  height: 42px;
  line-height: 36px;
}
.titles .active {
  color: #4373df;
  border-top: 2px solid #4373df;
}
.titles span {
  display: inline-block;
  width: 50%;
  text-align: center;
  cursor: pointer;
}
.submit-info-item {
  color: #707a8a;
  height: 32px;
  line-height: 32px;
  display: flex;
  justify-content: space-between;
}

/* 张数输入 */
.place-order-input-box {
  position: relative;
  color: #707a8a;
}
.place-order-input-box .input-label-box {
  position: absolute;
  top: 0px;
  left: 10px;
  display: inline-block;
  height: 30px;
  line-height: 30px;
  z-index: 9;
}
.place-order-input-box input {
  padding: 0px 10px;
  box-sizing: border-box;
  text-align: center;
}

.place-order-form-box .place-order-form-splitter {
  height: auto;
}
.place-order-inner-common {
  padding: 0px 16px 4px 13px;
}
.place-order-input-box .okui-input-box .cols {
  display: inline-block;
  position: absolute;
  right: 0px;
}

.place-order-input-box .okui-input-box .cols span {
  color: #4373df;
  padding-right: 10px;
  cursor: pointer;
}
.place-order-form-box {
  display: initial;
}
.place-order-form-box .submit-btn-box-container {
  margin-top: 0px;
}

/*  */
.btns {
  display: flex;
}
.btns .btn-wrapper {
  width: 100%;
  padding: 0px 5px;
  box-sizing: border-box;
  margin: 16px 0;
}
.place-order-form-box .place-order-form-splitter {
  background: none;
}

.noLogin div {
  width: 100%;
  height: 40px;
  line-height: 40px;
  border-radius: 5px;
  text-align: center;
  color: #fff;
}
.noLogin div:nth-child(1) {
  background: #1d91ff;
}
.noLogin div:nth-child(2) {
  background: #484d56;
  margin-top: 20px;
}

.trading-input-box {
  height: 40px;
  background: #2c3138;
  border-radius: 4px;
  display: flex;
  align-items: center;
  padding: 0 15px;
  position: relative;
}
.trading-input {
  color: #fff;
  font-size: 14px;
  width: 100%;
}

.priceInput {
  background: #1f2328 !important;
}
</style>
