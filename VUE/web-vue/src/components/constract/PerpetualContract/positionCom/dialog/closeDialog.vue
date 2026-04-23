<template>
  <el-dialog
    :title="$t('message.home.shijia') + $t('message.home.pingcang')"
    class="settable setcash"
    v-model="isShow"
    width="450px"
    :before-close="close"
  >
    <div class="body-wrapper">
      <!-- 币种 -->
      <div class="coin-info">
        <span class="name">
          {{ rowData?.symbol?.toUpperCase() }}USD
          {{ $t("message.home.yongxu") }}
        </span>
        <span class="direction" :class="`${rowData?.direction}-direction`"
          >{{
            rowData?.direction === "buy"
              ? $t("message.home.pingduo")
              : $t("message.home.pingkong")
          }}
        </span>
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
        <!-- 张数输入 平仓-->
        <amount-slider
          ref="sliderRef"
          :maxAmount="Math.min(rowData.volume, 1)"
          :symbol="rowData.symbol"
          :lever_rate="rowData.lever_rate"
          :typeNum="2"
        ></amount-slider>
      </div>
      <!-- 按钮 -->
      <div class="btns">
        <el-button @click="handleSubmit" class="btn-submit">
          {{ $t("message.home.queren") }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { mapState, mapActions, mapStores } from "pinia";
import { useUserStore } from "@/store/user";
import { useCurrencyStore } from "@/store/currency";
import Axios from "@/api/perpetualContract.js";
import amountSlider from "../../orderCom/amountSlider.vue";
import { ElMessage } from "element-plus";
import bus from "vue3-eventbus";
export default {
  name: "closeDialog",
  data() {
    return {
      isShow: false,
      rowData: {},
    };
  },
  components: {
    amountSlider,
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
    showInfoData: function () {
      return [
        {
          label: this.$t("message.home.chengjiaojunjia"),
          value: `${this.rowData.close_avg_price} USD`,
        },
        {
          label: this.$t("message.home.zuixinjiage"),
          value: `${this.rowData.mark_price} USD`,
        },
      ];
    },
  },

  methods: {
    open(data) {
      this.rowData = data;
      this.isShow = true;
    },
    close() {
      this.isShow = false;
      this.$refs.sliderRef.cleanAmount();
    },
    async getCloseSession() {
      //获取平仓初始化参数
      const res = await Axios._initClose({
        symbol: this.$route.params.id,
      });
      return res.data?.session_token;
    },
    async handleSubmit() {
      if (!this.$refs.sliderRef.amount) {
        const text = this.$t("message.home.qingshuruzhangshu");
        ElMessage(text);
        return;
      }
      const session_token = await this.getCloseSession();

      // 部分市价平仓
      const data = {
        direction: this.rowData.direction,
        price_type: "opponent",
        amount: this.$refs.sliderRef.amount,
        symbol: this.rowData.symbol,
        price: this.rowData.mark_price,
        session_token,
      };

      Axios.orderPartClose(data).then((res) => {
        //每次平仓之后都要获取新的sesstiontoken；
        bus.emit("getSesstionToken", "close");
        if (res.code == "0") {
          // TODO 平多成功的翻译
          this.$message({
            message:
              this.$t("message.home.gongxini") +
              `，${
                this.rowData.direction == "buy"
                  ? this.$t("message.home.pingduo")
                  : this.$t("message.home.pingkong")
              }`,
            type: "success",
          });
          this.close();
          this.$refs.sliderRef.cleanAmount(); // 清除下单区数据
        }
      });
    },
  },
};
</script>
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

.buy {
  color: #0db37c;
  background: #0db37c;
}
.sell {
  color: #db4355;
  background: #0db37c;
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

.btn-submit {
  width: 400px;
  background: #4373df;
  color: #fff;
}
</style>
