<template>
  <div class="mid-gm">
    <div id="TabbedPanels2" class="TabbedPanels">
      <ul class="TabbedPanelsTabGroup">
        <li class="TabbedPanelsTab TabbedPanelsTabSelected" tabindex="0">
          {{ lanInfo.exchange }}
        </li>
      </ul>
      <div class="mar-b1">
        <span :class="priceType == 0 ? 'no' : ''" @click="changePriceType(0)">{{
          lanInfo.limit
        }}</span>
        <span
          :class="priceType == 1 ? 'no' : ''"
          @click="changePriceType(1)"
          class="margin2"
          >{{ lanInfo.oppont }}</span
        >
      </div>
      <div class="TabbedPanelsContentGroup">
        <div class="TabbedPanelsContent">
          <div class="mar-tab-con">
            <div class="mar-tab-l">
              <div class="pad10">
                <div class="mar-b2" v-if="priceType == 0">
                  <span>{{ lanInfo.b_price }}</span>
                  <input
                    class="mar-b2-ip"
                    v-model="paramsBuy.price"
                    type="text"
                    :placeholder="lanInfo.b_price_word"
                  />
                  <span class="mar-tr">USD</span>
                </div>
                <div class="mar-b2">
                  <span>{{ lanInfo.b_num }}</span>
                  <input
                    class="mar-b2-ip"
                    v-model="paramsBuy.volume"
                    type="text"
                    :placeholder="lanInfo.b_num_word"
                  />
                  <span class="mar-tr">USD</span>
                </div>
                <div class="mar-gdt">
                  <el-slider
                    @change="changeOpen"
                    class="slider-green"
                    v-model="sliderValueOpen"
                    :format-tooltip="(v) => `${v}%`"
                    :step="5"
                    :marks="marks"
                  ></el-slider>
                </div>
                <div class="mar-b3 c-g">
                  {{ lanInfo.can_buy }}
                  <span class="mar-tr c-g font12f">{{ maxVolume }}USD</span>
                </div>
                <div class="mar-b3">
                  {{ lanInfo.money }}
                  <span class="mar-tr font12f">{{ paramsBuy.volume }}USD</span>
                </div>
                <div class="mar-b3">
                  {{ lanInfo.fee }}
                  <span class="mar-tr font12f">{{ unit_fee }}USD</span>
                </div>
                <div
                  class="mar-b4 rate-style-green"
                  @click="createOrder(0)"
                  v-if="name"
                >
                  {{ lanInfo.buy }}
                </div>
                <div class="mar-b4" v-else>
                  <a href="#" @click="goLogin">{{ lanInfo.login }}</a>
                  {{ lanInfo.or }}
                  <a href="#" @click="goRegis">{{ lanInfo.regis }}</a>
                </div>
              </div>
            </div>
            <div class="mar-tab-r">
              <div class="pad10">
                <div class="mar-b2" v-if="priceType == 0">
                  <span>{{ lanInfo.s_price }}</span>
                  <input
                    class="mar-b2-ip"
                    v-model="paramsSell.price"
                    type="text"
                    :placeholder="lanInfo.b_price_word"
                  />
                  <span class="mar-tr">USD</span>
                </div>
                <div class="mar-b2">
                  <span>{{ lanInfo.s_num }}</span>
                  <input
                    class="mar-b2-ip"
                    v-model="paramsSell.volume"
                    type="text"
                    :placeholder="lanInfo.b_num_word"
                  />
                  <span class="mar-tr">{{ symbolUnit }}</span>
                </div>
                <div class="mar-gdt">
                  <el-slider
                    @change="changeClose"
                    class="slider-red"
                    v-model="sliderValueClose"
                    :format-tooltip="(v) => `${v}%`"
                    :step="5"
                    :marks="marks"
                  ></el-slider>
                </div>
                <div class="mar-b3 c-r">
                  {{ lanInfo.can_sell }}
                  <span class="mar-tr c-r font12f"
                    >{{ maxCloseVolume }}{{ symbolUnit }}</span
                  >
                </div>
                <div class="mar-b3">
                  {{ lanInfo.money }}
                  <span class="mar-tr font12f"
                    >{{ paramsSell.volume }}{{ symbolUnit }}</span
                  >
                </div>
                <div class="mar-b3">
                  {{ lanInfo.fee }}
                  <span class="mar-tr font12f"
                    >{{ unit_closeFee }}{{ symbolUnit }}</span
                  >
                </div>
                <div
                  class="mar-b4 rate-style-red"
                  @click="createOrder(1)"
                  v-if="name"
                >
                  {{ lanInfo.sell }}
                </div>
                <div class="mar-b4" v-else>
                  <a href="#" @click="goLogin">{{ lanInfo.login }}</a>
                  {{ lanInfo.or }}
                  <a href="#" @click="goRegis">{{ lanInfo.regis }}</a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="tab-r">
        <li>
          <a href="#">{{ symbolUnit }}{{ lanInfo.jz }} {{ closeValue }}</a>
        </li>
      </div>
    </div>
  </div>
</template>

<script>
import enInfo from "../assets/lan/en.js";
import zhCNInfo from "../assets/lan/zh-CN.js";
import CNInfo from "../assets/lan/CN.js";
import { ElMessage } from "element-plus";

var enlang = enInfo.exchange;
var zhCNlang = zhCNInfo.exchange;
var CNlang = CNInfo.exchange;

export default {
  emits: ["doFresh"],
  name: "trade",
  props: ["symbol", "close", "userName"],
  data() {
    return {
      sliderValueOpen: 0,
      sliderValueClose: 0,
      marks: {
        0: "",
        20: "",
        40: "",
        60: "",
        80: "",
        100: "",
      },
      name: "",
      symbolUnit: "",
      closeValue: 0,
      maxCloseVolume: 0,
      unit_closeFee: 0,
      maxVolume: 0,
      unit_fee: 0,
      priceType: 0,
      paramsBuy: {
        price: "",
        volume: "",
      },
      paramsSell: {
        price: "",
        volume: "",
      },
      localLan: "zh-CN",
      lanInfo: {},
    };
  },
  watch: {
    userName: {
      handler: function (newValue) {
        this.name = newValue;
      },
      deep: true,
    },
    symbol: {
      handler: function (newValue) {
        this.symbolUnit = newValue;
        this.getOpenview();
        this.getCloseview();
        this.initLan();
        this.sliderValueOpen = 0;
        this.sliderValueClose = 0;
      },
      deep: true,
    },
    close: {
      handler: function (newValue) {
        this.closeValue = newValue;
      },
      deep: true,
    },
  },
  mounted() {},
  methods: {
    initLan() {
      if (localStorage.getItem("lang")) {
        this.localLan = localStorage.getItem("lang");
      }
      if (this.localLan == "en") {
        this.lanInfo = enlang;
      } else if (this.localLan == "zh-CN") {
        this.lanInfo = zhCNlang;
      } else if (this.localLan == "CN") {
        this.lanInfo = CNlang;
      }
    },
    changeOpen(value) {
      this.paramsBuy.volume = parseFloat((this.maxVolume * value) / 100);
    },
    changeClose(value) {
      this.paramsSell.volume = parseFloat((this.maxCloseVolume * value) / 100);
    },
    //创建订单
    createOrder(buyType) {
      var me = this;
      var orderParams = {};
      if (buyType == 0) {
        orderParams = this.paramsBuy;
      } else if (buyType == 1) {
        orderParams = this.paramsSell;
      }
      orderParams.symbol = this.symbolUnit;
      if (this.priceType == 1) {
        orderParams.order_price_type = "opponent";
      } else if (this.priceType == 0) {
        orderParams.order_price_type = "limit";
      }
      this.$confirm(this.lanInfo.can_order, this.lanInfo.tips, {
        confirmButtonText: this.lanInfo.comfirm,
        cancelButtonText: this.lanInfo.cancel,
        type: "warning",
      })
        .then(() => {
          if (buyType == 0) {
            me.$fetch("api/exchangeapplyorder!open.action", orderParams).then(
              (res) => {
                if (res.code == 0) {
                  me.paramsBuy.volume = 0;
                  me.paramsBuy.price = "";
                  ElMessage({
                    message: this.lanInfo.do_succ,
                    type: "success",
                  });
                }
                this.$emit("doFresh");
                me.getOpenview();
                me.getCloseview();
              }
            );
          } else if (buyType == 1) {
            me.$fetch("api/exchangeapplyorder!close.action", orderParams).then(
              (res) => {
                if (res.code == 0) {
                  me.paramsSell.volume = 0;
                  me.paramsSell.price = "";
                  ElMessage({
                    message: this.lanInfo.do_succ,
                    type: "success",
                  });
                }
                this.$emit("doFresh");
                me.getOpenview();
                me.getCloseview();
              }
            );
          }
        })
        .catch(() => {});
    },

    getOpenview() {
      this.$fetch("api/exchangeapplyorder!openview.action", {
        symbol: this.symbolUnit,
      }).then((res) => {
        var jsonArray = res;
        this.paramsBuy.session_token = jsonArray.data.session_token;
        this.maxVolume = jsonArray.data.volume;
        this.unit_fee = jsonArray.data.fee;
      });
    },
    getCloseview() {
      this.$fetch("api/exchangeapplyorder!closeview.action", {
        symbol: this.symbolUnit,
      }).then((res) => {
        var jsonArray = res;
        this.paramsSell.session_token = jsonArray.data.session_token;
        this.maxCloseVolume = jsonArray.data.volume;
        this.unit_closeFee = jsonArray.data.fee;
      });
    },
    openLogin() {
      // this.$emit("openLogin")
    },
    goLogin() {
      if (this.localLan == "CN") {
        window.location.href = "../cn/login.html";
      } else if (this.localLan == "zh-CN") {
        window.location.href = "../zh/login.html";
      } else if (this.localLan == "en") {
        window.location.href = "../login.html";
      }
    },
    goRegis() {
      if (this.localLan == "CN") {
        window.location.href = "../cn/register.html";
      } else if (this.localLan == "zh-CN") {
        window.location.href = "../zh/register.html";
      } else if (this.localLan == "en") {
        window.location.href = "../register.html";
      }
    },
    changePriceType(index) {
      this.priceType = index;
    },
  },
};
</script>

<style>
.el-slider__runway {
  margin: 5px 0 10px 0;
}

.el-slider__stop {
  width: 12px;
  height: 12px;
  top: -3px;
  border: 1px solid #171b2b;
}

.slider-green .el-slider__button {
  border-color: #093;
}

.slider-green .el-slider__bar {
  background-color: #093;
}

.slider-red .el-slider__button {
  border-color: #c30;
}

.slider-red .el-slider__bar {
  background-color: #c30;
}
</style>
