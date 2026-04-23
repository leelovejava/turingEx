<!-- 订单确认 -->
<template>
  <div>
    <el-dialog
        :title="$t('message.jiaoyi.zhiyingzhisun')"
        v-model="isShow"
        :close-on-click-modal="false"
        width="500px"
        center
        :append-to-body="true"
        class="confirmWrapper"
    >
      <div class="dialog-box">
        <div class="delivery-text margin-top20">
          <div class="delivery-text-left">
            {{ $t("message.jiaoyi.price_profit") }}
          </div>
          <el-input-number
              class="slider-input-amount"
              v-model="profitAmount"
              :min="0"
          />
        </div>
        <div class="delivery-text margin-top20">
          <div class="delivery-text-left">
            {{ $t("message.jiaoyi.price_loss") }}
          </div>
          <el-input-number
              class="slider-input-amount"
              v-model="lossAmount"
          />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="info" @click="confirmOrder" class="continueOrder">{{
              $t("message.home.queren")
            }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import Axios from "@/api/perpetualContract.js"
export default {
  emits: ["confirmOrder", "closeDialog"],
  props: {
    dialogVisible: {
      // 弹窗是否展示
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      orderData: {}, //订单详情
      profitAmount:undefined, //这样设置输入框就可以是空的
      lossAmount:undefined,
      isShow:false
    };
  },

  computed: {

  },
  methods: {
    confirmOrder() {
      const data ={
        order_no: this.orderData.row.order_no,
        stop_price_profit: this.profitAmount,
        stop_price_loss: this.lossAmount,
      }
      Axios.stopLossAndProfit(data).then(res=>{
        this.$message({
          message: this.$t("message.user.tijiaochenggong"),
          type: "success",
        });
        this.isShow = false;
      })
    },
    open(data) {
      this.orderData = data;
      this.isShow = true;
      this.profitAmount = this.orderData.row.stop_price_profit === 0 ? undefined : this.orderData.row.stop_price_profit
      this.lossAmount = this.orderData.row.stop_price_loss === 0 ? undefined : this.orderData.row.stop_price_loss
    },
  },
};
</script>
<style lang="scss">
.confirmWrapper {
  background: #1f2328 !important;
  border-radius: 5px !important;

  .el-dialog__title {
    color: #fff !important;
  }
}
</style>

<style scoped>
.dialog-box {
  width: 430px;
  margin: 0 auto;
}

.circleBox {
  position: relative;
  text-align: center;
}

.circleCenter {
  width: 150px;
  text-align: center;
  position: absolute;
  top: 60px;
  left: 50px;
}

.delivery-text {
  display: flex;
  align-items: center;
}

.delivery-text > div {
  /* flex: 1; */
  padding: 10px 0;
}

.countdown {
  display: flex;
  align-items: center;
  justify-content: center;
}

.time-div {
  width: 65px;
  height: 42px;
  background: #1d91ff;
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.dot {
  color: #da9226;
  font-size: 20px;
  font-weight: bold;
  margin: 0 10px;
}

.delivery-text-left {
  color: #929aa5;
  min-width: 100px;
  max-width: 200px;
}

.yingkui {
  padding: 15px 0 0 0;
}

.continueOrder {
  width: 100%;
  height: 50px;
  background: #1d91ff;
  border: none;
}
</style>
