<!-- 张数选择器 -->
<template>
  <div>
    <div class="place-order-input-box">
      <div class="amount-label">
        {{ $t("message.home.zhangshu") }}
        <!-- ({{ symbol?.toUpperCase() }}) -->
      </div>
      <!-- 输入值只能为整数 step-strictly表示只能是step的倍数，step默认为1-->
      <el-input-number
        class="slider-input-amount"
        v-model="amount"
        :step-strictly="true"
        :min="0"
        :max="maxAmount"
        @change="inputChange"
      />

      <el-slider
        class="slider-block"
        tooltip-class="tooltip-box"
        @change="sliderAmountChange"
        v-model="sliderAmount"
        :format-tooltip="(v) => `${v.toFixed(2)}%`"
        :marks="marks"
      />
    </div>
    <!-- 订单 -->
    <div v-if="existToken">
      <div v-if="typeNum == 0" class="submit-info-item">
        <div>{{ $t("message.home.kekaizhangshu") }}</div>
        <div>{{ `${maxAmount} ${$t("message.home.zhang")}` }}</div>
      </div>

      <div v-if="typeNum == 1" class="submit-info-item">
        <div>{{ $t("message.home.kepingduo") }}</div>
        <div>{{ `${closeBuyAmount} ${$t("message.home.zhang")}` }}</div>
      </div>
      <div v-if="typeNum == 1" class="submit-info-item">
        <div>{{ $t("message.home.kepingkong") }}</div>
        <div>{{ `${closeSellAmount} ${$t("message.home.zhang")}` }}</div>
      </div>
    </div>
    <!-- 持仓区 -->
    <div v-if="typeNum == 2" class="submit-info-item">
      <div>{{ $t("message.home.kepingzhangshu") }}</div>
      <div>{{ `${maxAmount} ${$t("message.home.zhang")}` }}</div>
    </div>
  </div>
</template>
<script>
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";
export default {
  emits: ["getAmount"],
  name: "amountSlider",
  props: {
    maxAmount: {
      type: Number,
      default: 1, //可开或者可平的总数
    },
    symbol: {
      type: String,
      default: "",
    },
    lever_rate: {
      type: [Number,String],
      default: 1,
    },
    typeNum: {
      type: Number,
      default: 0,
    },
    closeSellAmount: {
      type: Number,
      default: 0,
    },
    closeBuyAmount: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      amount: undefined, //用户输入的张数
      sliderAmount: undefined, //滑块的数量
      marks: {
        0: "0",
        25: "25%",
        50: "50%",
        75: "70%",
        100: "100%",
      },
    };
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  watch: {
    amount(val) {
      this.sliderAmount = (val / this.maxAmount) * 100;
    },
  },
  methods: {
    //输入框事件
    inputChange(val) {
      this.$emit("getAmount", val);
    },
    // 清除输入框
    cleanAmount() {
      this.amount = undefined;
    },
    //滑块事件
    sliderAmountChange(val) {
      let data;
      if (this.maxAmount) {
        if (val == 0) {
          this.amount = undefined;
        } else {
          const rate = val / 100; //如0.25,val为25
          data = this.maxAmount * rate;
          // if (this.lever_rate) {
          //   data = math.format((this.maxAmount * rate), 2);
          // } else {
          //   data = math.format((this.maxAmount * rate) / 1, 2);
          // }
          this.amount = parseInt(data);
        }
        this.$emit("getAmount", this.amount);
      }
    },
    emptyValue() {
      this.sliderAmount = undefined;
    },
  },
};
</script>
<style>
@import url("@/assets/css/commonTrade/constract/order/amountSliderInput.css");
@import url("@/assets/css/commonTrade/el-slider.css");
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
/* 前缀 */
.amount-label {
  position: absolute;
  font-size: 12px;
  top: 2px;
  left: 10px;
  display: inline-block;
  height: 30px;
  line-height: 30px;
  z-index: 9;
}

/* d张数 */
.input-unit {
  position: absolute;
  top: 4px;
  right: 10px;
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

.place-order-form-box .submit-btn-box-container {
  margin-top: 0px;
}
</style>
