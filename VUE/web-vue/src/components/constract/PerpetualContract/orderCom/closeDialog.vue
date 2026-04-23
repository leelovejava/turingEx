<template>
  <el-dialog
    :title="getTitle"
    class="dialog-box"
    v-model="isShow"
    width="450px"
    :append-to-body="true"
    :before-close="close"
  >
    <div class="body-wrapper">
      <!-- 币种 -->
      <div class="coin-info">
        <span class="name">
          {{ pageData?.name?.toUpperCase() }} {{ $t("message.home.yongxu") }}
        </span>
        <span class="direction" :class="`${sellOrBuy}-direction`">{{
          sellOrBuy == "buy"
            ? $t("message.home.pingduo")
            : $t("message.home.pingkong")
        }}</span>
      </div>

      <!-------------- 限价平仓 ------------->
      <div v-if="type === 'limit'">
        <!-- 提交信息 -->
        <div class="submit-info">
          <div
            class="submit-info-item"
            v-for="(item, index) in showLimitInfoData"
            :key="index"
          >
            <div>{{ item.label }}</div>
            <div :class="item.class">
              {{ item.value }}
            </div>
          </div>
        </div>
        <!-- 按钮 -->
        <div class="btns">
          <el-button @click="close" class="btn-cancle">
            {{ $t("message.home.quxiao") }}
          </el-button>
          <el-button v-click="handleSubmit" class="btn-limit-submit">{{
            $t("message.home.queren")
          }}</el-button>
        </div>
      </div>
      <!-------------- 市价平仓 ------------->
      <div v-else>
        <!-- 提交信息 -->
        <div class="submit-info">
          <div
            class="submit-info-item"
            v-for="(item, index) in showInfoData"
            :key="index"
          >
            <div>{{ item.label }}</div>
            <div :class="item.class">
              {{ item.value }}
            </div>
          </div>
        </div>
        <!-- 按钮 -->
        <div class="btns">
          <el-button v-click="handleSubmit" class="btn-submit">{{
            $t("message.home.queren")
          }}</el-button>
        </div>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { mapState, mapActions, mapStores } from "pinia";
import { useUserStore } from "@/store/user";
import Axios from "@/api/perpetualContract.js";
import bus from "vue3-eventbus";

export default {
  name: "closeDialog",
  props: {
    // 限价or市价
    type: {
      type: String,
      default: "",
    },
    sellOrBuy: {
      type: String,
      default: "",
    },
    newPrice: {
      type: [String, Number],
      default: "",
    },
    unit: {
      type: String,
      default: "",
    },
    info: {
      type: Object,
      default(rawProps) {
        return {
          price: "",
          lever_rate: "",
          amount: "",
          symbol: "",
          session_token: "",
          volume: "",
        };
      },
    },
    pageData: {
      type: Object,
      default: {},
    },
  },
  data() {
    return {
      isShow: false,
    };
  },

  computed: {
    ...mapState(useUserStore, ["existToken"]),
    getTitle: function () {
      //如限价平多确认
      const map = {
        "limit buy": "xianjiapingduo",
        "limit sell": "xianjiapingkong",
        "opponent buy": "shijiapingduo",
        "opponent sell": "shijiapingkong",
      };
      const text = `${this.type} ${this.sellOrBuy}`;
      return this.$t(`message.home.${map[text]}`);
    },
    showLimitInfoData: function () {
      return [
        {
          label: this.$t("message.user.pingcangjiage"),
          value: `${this.info.price} ${this.unit}`,
        },
        {
          label: this.$t("message.home.zhangshu"),
          value: `${this.info.amount} ${this.$t("message.home.zhang")}`,
        },
        {
          label: this.$t("message.home.caozuo"),
          value: this.getTitle,
          class: this.sellOrBuy === "buy" ? "buy-color" : "sell-color",
        },
        {
          label: this.$t("message.home.chengjiaojunjia"), //=平仓价格
          value: `${this.info.price} ${this.unit}`,
        },
        {
          label: this.$t("message.home.zuixinjiage"),
          value: `${this.newPrice} ${this.unit}`,
        },
        {
          label: this.$t("message.home.yujiyingkui"),
          value: `${bigDecimal.round(this.newPrice * this.info.amount, 4)} ${
            this.unit
          }`,
          class: this.sellOrBuy === "buy" ? "buy-color" : "sell-color",
        },
      ];
    },
    showInfoData: function () {
      return [
        {
          label: this.$t("message.home.chengjiaojunjia"),
          value: `${this.newPrice} ${this.unit}`,
        },
        {
          label: this.$t("message.home.zuixinjiage"),
          value: `${this.newPrice} ${this.unit}`,
        },
        {
          label: this.$t("message.home.zhangshu"),
          value: `${this.info.amount} ${this.$t("message.home.zhang")}`,
        },
      ];
    },
  },

  methods: {
    open() {
      this.isShow = true;
    },
    close() {
      this.isShow = false;
    },
    handleSubmit() {
      const { amount, symbol, price, session_token } = this.info;
      const data = {
        direction: this.sellOrBuy,
        price_type: this.type,
        amount,
        symbol,
        price, //价格，数量，symbol 平仓没有杠杆
        session_token,
      };
      Axios.orderPartClose(data).then((res) => {
        //重新获取token
        bus.emit("getSesstionToken", "close");
        if (res.code == "0") {
          this.$message({
            message:
              this.$t("message.home.gongxini") +
              `，${
                this.sellOrBuy == "buy"
                  ? this.$t("message.home.pingduo")
                  : this.$t("message.home.pingkong")
              } ${this.$t("message.user.chenggong")}`,
            type: "success",
          });
          this.close();
          bus.emit("cleanOrderData");
        }
      });
    },
  },
};
</script>
<style lang="scss">
.dialog-box {
  background: #1f2328 !important;
  border-radius: 5px !important;
  // .el-input__inner {
  //   font-size: 40px;
  // }
  .el-dialog__title {
    color: #fff;
  }
}
</style>
<style scoped>
.coin-info {
  color: #fff;
}
.direction {
  margin: 0 8px;
  display: inline-block;
  padding: 4px;
}
.sell-direction {
  color: #e05561;
  background: #392525;
}
.buy-direction {
  color: #62c885;
  background: #25392c;
}

.submit-info {
  border-bottom: 1px solid #707a8a;
}
.submit-info-item {
  color: #707a8a;
  height: 32px;
  line-height: 32px;
  display: flex;
  justify-content: space-between;
}
.btns {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}
.btn-cancle {
  width: 140px;
  background: #484d56;
  color: #fff;
}
.btn-limit-submit {
  width: 200px;
  background: #4373df;
  color: #fff;
}

.btn-submit {
  width: 400px;
  background: #4373df;
  color: #fff;
}
.buy-color {
  color: #0db37c;
}
.sell-color {
  color: #db4355;
}
</style>
