<!-- 订单确认 -->
<template>
  <div>
    <el-dialog
      v-if="detailData"
      :title="$t('message.jiaoyi.dingdanqueren')"
      v-model="visible"
      :close-on-click-modal="false"
      width="500px"
      center
      :append-to-body="true"
      class="confirmWrapper"
    >
      <div class="dialog-box">
        <div class="delivery-text margin-top20">
          <div class="delivery-text-left">
            {{ $t("message.jiaoyi.jiaoyibizhong") }}
          </div>
          <div>{{ pageData?.name?.toUpperCase() }}</div>
        </div>
        <div class="delivery-text">
          <div class="delivery-text-left">
            {{ $t("message.home.fangxiang") }}
          </div>
          <div v-if="detailData.direction == 'buy'" class="green">
            {{ $t("message.jiaoyi.zuoduomairu") }}
          </div>
          <div v-if="detailData.direction == 'sell'" class="red">
            {{ $t("message.jiaoyi.zuokongmairu") }}
          </div>
        </div>

        <div class="delivery-text">
          <div class="delivery-text-left">
            {{ $t("message.home.gouamijia") }}
          </div>
          <div>{{ detailData.nowPrice }}</div>
        </div>

        <div class="delivery-text">
          <div class="delivery-text-left">
            {{ $t("message.home.shuliang") }}
          </div>
          <div>{{ detailData.amount }}</div>
        </div>

        <div class="delivery-text">
          <div class="delivery-text-left">
            {{ $t("message.home.jiaogeshijian") }}
          </div>
          <div>{{ detailData.time_num }} {{ detailData.time_unit }}</div>
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
import { mapState, mapActions, mapStores } from "pinia";
import { useUserStore } from "@/store/user";

export default {
  emits: ["confirmOrder", "closeDialog"],
  props: {
    dialogVisible: {
      // 弹窗是否展示
      type: Boolean,
      default: false,
    },
    buyAndSellData: {
      type: Object,
      default: function () {
        return {};
      },
    },
    pageData: {
      type: Object,
      default: function () {
        return {};
      },
    },
  },
  data() {
    return {
      detailData: {}, //订单详情
    };
  },
  watch: {
    buyAndSellData(val) {
      this.detailData = val;
    },
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
    visible: {
      get() {
        return this.dialogVisible;
      },
      set(val) {
        // 调用父组件的方法
        this.$emit("closeDialog");
      },
    },
  },
  methods: {
    confirmOrder() {
      this.visible = false;
      this.$emit("confirmOrder");
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
  flex: 1;
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
