<!-- 交割合约开仓页面 -->
<template>
  <div>
    <div class="place-order-inner-common" style="width: 1043px">
      <div class="order-top-box">
        <div>{{ $t("message.jiaoyi.jiaogeheyue") }}</div>
        <div class="but-box">
          <div
            class="buy-but but"
            @click="changeDeliveryType('buy')"
            :class="[deliveryType == 'buy' ? 'deliveryTypeActive' : '']"
          >
            {{ $t("message.jiaoyi.zuoduomairu") }}
          </div>
          <div
            class="sell-but but"
            @click="changeDeliveryType('sell')"
            :class="[deliveryType == 'sell' ? 'deliveryTypeActiveRed' : '']"
          >
            {{ $t("message.jiaoyi.zuokongmairu") }}
          </div>
        </div>
        <!-- 信息 -->
        <div class="order-title-item">
          <div class="order-left-title">
            {{ $t("message.jiaoyi.jiaoyizican") }}
          </div>
          <div class="order-right-title">{{ unit }}</div>
        </div>
        <div class="order-title-item">
          <div class="order-left-title">
            {{ $t("message.jiaoyi.keyongshuliang") }}
          </div>
          <div class="order-right-title">{{ volume }} {{ unit }}</div>
        </div>
        <div class="order-title-item">
          <div class="order-left-title">
            {{ $t("message.jiaoyi.shouxufei") }}
          </div>
          <div class="order-right-title">
            {{
              deliveryType == "buy"
                ? unit_fee * buyAmount
                : unit_fee * sellAmount
            }}
          </div>
        </div>
        <div class="order-title-item">
          <div class="order-left-title">
            {{ $t("message.jiaoyi.zuidimairu") }}
          </div>
          <div class="order-right-title">{{ buy_min }} {{ unit }}</div>
        </div>
      </div>
      <!-- 交易时段 -->
      <div class="trading-session-box" @click="isPopup = !isPopup">
        <div class="trading-title">
          {{ $t("message.jiaoyi.jiaoyishiduan") }}
        </div>
        <div class="trading-input-box">
          <div class="input-box">{{ deliveryValue }}</div>
          <div>
            <i class="el-icon-caret-bottom"></i>
          </div>
          <div class="trading-popup" v-if="isPopup">
            <ul>
              <li
                v-for="(item, index) in list"
                :key="index"
                @click.stop="timeBtn(item, index)"
                :class="[activeIndex == index ? 'active' : '']"
              >
                {{ item.time_num }}
                <span v-if="item.time_unit == 'second'">s</span>
                <span v-if="item.time_unit == 'minute'">m</span>
                <span v-if="item.time_unit == 'hour'">h</span>
                <span v-if="item.time_unit == 'day'">d</span>
                <div style="margin: 0 5px"></div>
                {{ item.profit_ratio }} %
              </li>
            </ul>
          </div>
        </div>
      </div>
      <!-- 交易数量输入 -->
      <div class="trading-session-box">
        <div class="trading-title">{{ $t("message.jiaoyi.shuliang") }}</div>
        <div class="trading-input-box">
          <div class="input-box">
            <input
              class="trading-input"
              v-if="deliveryType == 'buy'"
              @blur="blurBuyNum"
              v-model="buyAmount"
              type="text"
            />
            <input
              class="trading-input"
              v-else
              v-model="sellAmount"
              @blur="blurSellNum"
              type="text"
            />
          </div>
        </div>
      </div>
      <!-- 数量百分比 -->
      <div class="tab-box">
        <div
          class="tab-item"
          v-for="(item, index) in fastList"
          :key="index"
          @click="fastFun(item)"
        >
          {{ item }}%
        </div>
      </div>
      <!-- 买入按钮 -->
      <button
        v-if="existToken"
        @click="handleBtnClick"
        side="buy"
        type="button"
        class="buy-but-centent"
      >
        <span class="btn-content">{{ $t("message.jiaoyi.mairu") }}</span>
      </button>
      <!-- 按钮 登录注册-->
      <button
        v-if="!existToken"
        side="buy"
        type="button"
        class="buy-but-centent"
        @click="goRouter('/register')"
      >
        <span class="btn-content">{{ $t("message.home.zhuce") }}</span>
      </button>
      <button
        v-if="!existToken"
        side="buy"
        type="button"
        class="buy-but-cententTwo"
        @click="goRouter('/login')"
      >
        <span class="btn-content">{{ $t("message.home.denglu") }}</span>
      </button>
    </div>
    <!-- 订单页弹窗  -->
    <detail-dialog
      :typeValue="1"
      :dialogVisible="detailVisible"
      @closeDialog="handleCloseDetailDialog"
      :unit="unit"
    ></detail-dialog>
    <!-- 确认弹窗  -->
    <dialog-confirm
      @confirmOrder="handleOpenOrder"
      :buyAndSellData="buyAndSellData"
      :dialogVisible="confirmVisible"
      @closeDialog="handleCloseConfirmDialog"
      :pageData="currencyData"
    ></dialog-confirm>
  </div>
</template>
<script>
import { mapState, mapActions, mapStores } from "pinia";
import { useUserStore } from "@/store/user";
import Axios from "@/api/delivery.js";
import { ElMessage } from "element-plus";
import bus from "vue3-eventbus";
import DetailDialog from "./DetailDialog.vue";
import DialogConfirm from "./DialogConfirm.vue";
export default {
  components: { DetailDialog, DialogConfirm },
  name: "deliveryTrade",
  props: {
    pageData: {
      type: Object,
      default: function () {
        return {};
      },
    },
    unit: {
      type: String,
    },
  },
  data() {
    return {
      currencyData: {},

      symbol: "btc", //币种
      volume: "", //可用数量
      sessionToken: "",
      list: [], //交割时间
      activeIndex: 0,
      buy_min: "", //最低金额
      buyAmount: "", //买入输入数量
      unit_fee: "0", //买入手续费
      sellAmount: "", //卖出输入数量
      sell_unit_fee: "0", //卖出手续费
      para_id: "", //交割时间id
      open_price: "", //开仓价格
      now_price: "", //当前币种实时价格
      detailVisible: false,
      confirmVisible: false,
      nowType: "", //当前方向
      amount: "", //当前数量
      deliveryTime: 100, //选中的交割时间值
      deliveryUnit: "", //选择中的交易时间单位
      isPopup: false,
      deliveryValue: "", //交割时段
      fastList: [25, 50, 75, 100],

      deliveryType: "buy",
      buyAndSellData: {},
      canOpen: true, //能否开仓，false表示处于休市状态
      timeOutTimer1:null
    };
  },
  watch: {
    "$route.params.id"(val) {
      this.getDeliveryInfo();
    },
    pageData(val) {
      this.currencyData = val;
      this.nowPrice = val.close;
    },
  },
  computed: {
    ...mapState(useUserStore, ["existToken", "userInfo"]),
  },
  mounted() {
    this.getDeliveryInfo();
  },
  methods: {
    //路由跳转
    goRouter(params) {
      this.$router.push(params);
    },
    //获取开仓页面参数
    async getDeliveryInfo() {
      if(this.$route.params.id === 'undefined') {
        return
      }
      Axios.getFutures({
        symbol: this.$route.params.id,
        token: this.userInfo.token,
      }).then((res) => {
        this.volume = res.data.amount;
        this.sessionToken = res.data.session_token;
        this.list = res.data.para; //交易时段
        this.canOpen = res.data.open;
        //  交割时段清空
        this.deliveryValue = "";
        // this.buyAndSellData = res.data.para[this.activeIndex]
        if (res.data.para.length > 0) {
          this.buy_min = res.data.para[this.activeIndex].buy_min;
          this.unit_fee = res.data.para[this.activeIndex].unit_fee;
          this.para_id = res.data.para[this.activeIndex].para_id;
          this.buyAmount = this.buy_min;
          this.sellAmount = this.buy_min;
          let deliveryUnit = "";
          if (res.data.para[this.activeIndex].time_unit == "second") {
            deliveryUnit = "s";
          } else if (res.data.para[this.activeIndex].time_unit == "minute") {
            deliveryUnit = "m";
          } else if (res.data.para[this.activeIndex].time_unit == "hour") {
            this.deliveryUnit = "h";
          } else if (res.data.para[this.activeIndex].time_unit == "day") {
            deliveryUnit = "d";
          }
          this.deliveryUnit = deliveryUnit;
          this.deliveryTime = res.data.para[this.activeIndex].time_num;
          this.deliveryValue =
            res.data.para[this.activeIndex].time_num +
            deliveryUnit +
            "  " +
            res.data.para[this.activeIndex].profit_ratio +
            "%";
        }
      });
    },
    //点击交割时间
    timeBtn(item, index) {
      this.isPopup = false;
      this.activeIndex = index;
      this.buy_min = item.buy_min;
      this.unit_fee = item.unit_fee;
      this.para_id = item.para_id;
      this.buyAmount = this.buy_min;
      this.sellAmount = this.buy_min;
      if (item.time_unit == "second") {
        this.deliveryUnit = "s";
      } else if (item.time_unit == "minute") {
        this.deliveryUnit = "m";
      } else if (item.time_unit == "hour") {
        this.deliveryUnit = "h";
      } else if (item.time_unit == "day") {
        this.deliveryUnit = "d";
      }
      this.deliveryTime = item.time_num;
      this.deliveryValue =
        item.time_num + this.deliveryUnit + "  " + item.profit_ratio + "%";
    },
    //买入数量失去焦点
    blurBuyNum() {
      if (this.buyAmount <= this.buy_min) {
        this.buyAmount = this.buy_min;
      }
    },
    //做空数量失去焦点
    blurSellNum() {
      if (this.sellAmount <= this.buy_min) {
        this.sellAmount = this.buy_min;
      }
    },
    // 点击确认下单
    handleOpenOrder() {
      Axios.futuresOpen({
        symbol: this.currencyData.symbol,
        session_token: this.sessionToken,
        direction: this.nowType,
        amount: this.amount,
        para_id: this.para_id,
      }).then((res) => {
        if (res.code == 0) {
          this.open_price = res.data.open_price;

          this.nowType = this.type;
          let obj = {
            order_no: res.data.order_no,
            symbol: this.currencyData.symbol,
          };
          bus.emit("showDetail", obj);
          ElMessage({
            message: this.$t("message.jiaoyi.kaicangchenggong"),
            type: "success",
          });
          this.timeOutTimer1 = setTimeout(() => {
            this.detailVisible = true;
            this.getDeliveryInfo();
          }, 1000);
        }
      });
    },
    //买入
    buyCurrency() {
      if (!this.buyAmount) {
        ElMessage.warning(this.$t("message.home.qingshurujine"));
        return;
      }
      if (this.volume <= 0 || this.volume < this.buy_min) {
        ElMessage.warning(this.$t("message.home.yuebuzu"));
        return;
      }
      this.nowType = "buy";
      this.amount = this.buyAmount;
      this.buyAndSellData = {
        symbol: this.currencyData.symbol,
        session_token: this.sessionToken,
        direction: this.nowType,
        amount: this.amount,
        para_id: this.para_id,
        time_num: this.deliveryTime,
        time_unit: this.deliveryUnit,
        nowPrice: this.nowPrice, //购买价
      };
      this.confirmVisible = true;
    },
    //卖出
    sellCurrency() {
      if (!this.sellAmount) {
        ElMessage.warning(this.$t("message.home.qingshurujine"));
        return;
      }
      if (this.volume <= 0) {
        ElMessage.warning(this.$t("message.home.yuebuzuqingxianchongzhi"));
        return;
      }
      this.nowType = "sell";
      this.amount = this.sellAmount;
      this.buyAndSellData = {
        symbol: this.currencyData.symbol,
        session_token: this.sessionToken,
        direction: this.nowType,
        amount: this.amount,
        para_id: this.para_id,
        time_num: this.deliveryTime,
        time_unit: this.deliveryUnit,
        nowPrice: this.nowPrice,
      };
      this.confirmVisible = true;
    },
    // 快速输入
    fastFun(item) {
      if (this.deliveryType == "buy") {
        this.buyAmount = this.volume * (item / 100);
      } else {
        this.sellAmount = this.volume * (item / 100);
      }
    },

    changeDeliveryType(val) {
      this.deliveryType = val;
    },
    // 买入按钮
    async handleBtnClick() {
      await this.getDeliveryInfo();
      if (!this.canOpen) {
        ElMessage.warning(this.$t("message.jiaoyi.closeDeliveryTradeTip"));
        return;
      }
      // 请选择交易时段
      if (!this.deliveryValue) {
        ElMessage.warning(this.$t("message.jiaoyi.pls_xuanzejiaoyishiduan"));
        return;
      }
      if (this.deliveryType == "buy") {
        this.buyCurrency();
      } else {
        this.sellCurrency();
      }
    },
    handleCloseConfirmDialog() {
      this.confirmVisible = false;
    },
    handleCloseDetailDialog() {
      this.detailVisible = false;
    },
    // 清除定时器
    unbindTimerHandle(){
      if(this.timeOutTimer1){
        clearTimeout(this.timeOutTimer1)
        this.timeOutTimer1 = null
      }
    }
  },
  unmounted() {
    this.unbindTimerHandle()
  }
};
</script>
<style scoped>
@import url("@/assets/css/commonTrade/perpeContract.css");

.dilivery-box {
  display: flex;
  flex-wrap: wrap;
}

.delivery-time-content {
  flex: 1;
  min-width: 200px;
  margin-bottom: 20px;
  height: 35px;
  line-height: 35px;
  display: flex;
  align-content: center;
  justify-content: center;
  border-radius: 3px;
  flex-wrap: wrap;
  margin-right: 20px;
  cursor: pointer;
}

.delivery-time-active {
  background-color: #1d91ff;
  color: black;
}

.delivery-time-grey {
  border-left: 1px solid #e5e5e5;
  border-right: 1px solid #e5e5e5;
  border-top: 1px solid #e5e5e5;
  border-bottom: 1px solid #e5e5e5;
  border-right: none;
}

.delivery-border-active {
  border: 1px solid #1d91ff !important;
}

.delivery-border-grey {
  border: 1px solid #e5e5e5;
}

.delivery-time-content > div {
  flex: 1;
  text-align: center;
}

.but-box {
  display: flex;
  margin-bottom: 15px;
  margin-top: 15px;
}

.but-box .but {
  flex: 1;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  border-radius: 2px;
}

.buy-but {
  background: #484d56;
}

.sell-but {
  background: #484d56;
  margin-left: 10px;
}

.order-title-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
}

.order-left-title {
  color: #929aa5;
}

.order-right-title {
  color: #fff;
}

.order-top-box {
  border-bottom: 1px solid #24272c;
}

.trading-title {
  color: #929aa5;
  padding: 20px 0;
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

.input-box {
  flex: 1;
}

.trading-input {
  color: #fff;
  font-size: 14px;
  width: 100%;
}

.trading-session-box {
  position: relative;
  border-bottom: 1px solid #24272c;
  padding: 20px 0;
}

.tab-box {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.tab-item {
  width: 60px;
  height: 35px;
  background: #2c3138;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 5px;
  margin-top: 20px;
  cursor: pointer;
}

.place-order-inner-common {
  /* border: 1px solid #ffdfdf; */
}

.assets-title {
  color: #fff;
  font-weight: 400;
  padding: 20px 0;
}

.assets-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.money-box {
  display: flex;
  justify-content: space-between;
  padding: 15px 0;
}

.money-left {
  color: #929aa5;
}

.money-right {
  color: #fff;
}

.trading-popup {
  width: 100%;
  background: #fff;
  border-radius: 4px;
  position: absolute;
  overflow-y: scroll;
  left: 0;
  height: 260px;
  z-index: 2;
  top: 45px;
}

.trading-popup ul {
  /* border: 1px solid #B8BFD1; */
  overflow: hidden;
  left: 16px;
  position: relative;
  top: 17px;
}

.trading-popup li {
  height: 45px;
  color: #62636d;
  display: flex;
  width: 45%;
  float: left;
  align-items: center;
  cursor: pointer;
  border: 1px solid #ddd;
  text-align: center;
  justify-content: center;
}

/* .trading-popup li:hover{
  background: #1D91FF;
  color: #fff;
} */

.trading-popup .active {
  border: 1px solid #1d91ff;
  background: #1d91ff;
  color: #fff;
}

.deliveryTypeActive {
  background: #62c885 !important;
}

.deliveryTypeActiveRed {
  background: #db4355 !important;
}

.buy-but-centent {
  height: 40px;
  background: #1d91ff;
  width: 100%;
  border-radius: 4px;
  color: #fff;
  margin-top: 20px;
}

.buy-but-cententTwo {
  height: 40px;
  background: #484d56;
  width: 100%;
  border-radius: 4px;
  color: #fff;
  margin-top: 20px;
}

/* .trading-popup li:not(:last-child){
  border-bottom: 1px solid red;
} */
</style>
