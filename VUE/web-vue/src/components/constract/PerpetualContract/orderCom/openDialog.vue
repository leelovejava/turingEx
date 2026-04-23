<template>
  <div>
    <!-- :append-to-body="true" 绑在body上解决穿透的问题，但scoped样式不生效-->
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
          <span class="name"
            >{{ pageData?.name?.toUpperCase() }}
            {{ $t("message.home.yongxu") }}</span
          >
        </div>
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
          <el-button @click="close" class="btn-cancle">
            {{ $t("message.home.quxiao") }}
          </el-button>
          <el-button
            v-click="handleSubmit"
            class="btn-submit"
            :class="sellOrBuy === 'buy' ? 'buy-btn' : 'sell-btn'"
          >
            {{ getTitle }}
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";
import Axios from "@/api/perpetualContract.js";
import bus from "vue3-eventbus";
export default {
  name: "openDialog",
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
    unit: {
      type: String,
      default: "",
    },
    pageData: {
      type: Object,
      default: {},
    },
    newPrice: {
      type: [String, Number],
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
        };
      },
    },
  },
  data() {
    return {
      isShow: false,
    };
  },
  // watch: {
  //   info(val) {
  //     console.log("333watch info33333", val);
  //   },
  // },

  computed: {
    ...mapState(useUserStore, ["existToken"]),
    getTitle: function () {
      //如限价卖多确认 open long limit
      const map = {
        "limit buy": "xianjiakaiduo",
        "limit sell": "xianjiakaikong",
        "opponent buy": "shijiakaiduo",
        "opponent sell": "shijiakaikong",
      };
      const text = `${this.type} ${this.sellOrBuy}`;
      return this.$t(`message.home.${map[text]}`);
    },
    showInfoData: function () {
      return [
        {
          label: this.$t("message.user.kaicangjiage"),
          value: `${this.newPrice} ${this.unit}`,
        },
        {
          label: this.$t("message.home.gangganbeishu"),
          value: `${this.info.lever_rate}X`,
        },
        {
          label: this.$t("message.home.fangxiang"),
          value:
            this.sellOrBuy === "buy"
              ? this.$t("message.jiaoyi.zuoduomairu")
              : this.$t("message.jiaoyi.zuokongmairu"),
          class: this.sellOrBuy === "buy" ? "buy-color" : "sell-color",
        },
        {
          label: this.$t("message.home.zhangshu"),
          value: `${this.info.amount}${this.$t("message.home.zhang")} `,
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
      const data = {
        direction: this.sellOrBuy,
        price_type: this.type,
        ...this.info, //价格，数量，杠杆,symbol
      };
      data.price = this.newPrice;
      Axios.contractApplyOrderOpen(data).then((res) => {
        //重新获取token
        bus.emit("getSesstionToken", "open");
        if (res.code == "0") {
          this.$message({
            message:
              this.$t("message.home.gongxini") +
              `，${
                this.sellOrBuy == "buy"
                  ? this.$t("message.home.kaiduo")
                  : this.$t("message.home.kaikong")
              }  ${this.$t("message.user.chenggong")}`,
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

<style lang="scss" scoped>
// https://github.com/element-plus/element-plus/issues/10515
.coin-info {
  color: #fff;
}
.submit-info {
  margin: 10px 0 40px 0;
}
.submit-info-item {
  color: #707a8a;
  height: 40px;
  line-height: 40px;
  display: flex;
  justify-content: space-between;
}
.btns {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}
.btn-cancle {
  width: 140px;
  background: #484d56;
  color: #fff;
}
.btn-submit {
  width: 200px;
  color: #fff;
}
.buy-color {
  color: #0db37c;
}
.sell-color {
  color: #db4355;
}
.buy-btn {
  background: #0db37c;
}
.sell-btn {
  background: #db4355;
}
</style>
