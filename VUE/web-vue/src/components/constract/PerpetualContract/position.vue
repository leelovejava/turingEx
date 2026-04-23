<template>
  <div>
    <div class="position-block">
      <!-- 表格-->
      <el-tabs v-model="activeName" v-if="existToken">
        <!-- 仓位个数-->
        <el-tab-pane
          :label="$t('message.jiaoyi.cangwei') + `( ${positionNum}  )`"
          name="first"
        >
          <position
            ref="positionRef"
            :symbol="symbol"
            @getPositionNum="getPositionNum"
            :paramsType="paramsType"
            v-if="activeName === 'first'"
          />
        </el-tab-pane>
        <el-tab-pane
          :label="$t('message.home.dangqianweituo') + `(${curNum})`"
          name="second"
        >
          <current-apply-position
            :symbol="symbol"
            @getCurNum="getCurNum"
            :paramsType="paramsType"
            v-if="activeName === 'second'"
          />
        </el-tab-pane>
        <!-- <el-tab-pane :label="$t('message.home.lishiweituo')" name="third">
          <history-apply-position
            :symbol="symbol"
            :paramsType="paramsType"
            v-if="activeName === 'third'"
          />
        </el-tab-pane> -->
        <el-tab-pane :label="$t('message.home.lishichengjiao')" name="fourth">
          <history-order
            :symbol="symbol"
            v-if="activeName == 'fourth'"
            :paramsType="paramsType"
          />
        </el-tab-pane>
        <el-tab-pane :label="$t('message.jiaoyi.zichan')" name="fifth">
          <asset :symbol="symbol" v-if="activeName == 'fifth'" />
        </el-tab-pane>
      </el-tabs>

      <div v-if="!existToken" class="logins">
        <span @click="gotoPage('/login')">{{ $t("message.home.denglu") }}</span>
        {{ $t("message.home.or")
        }}<span @click="gotoPage('/register')">
          {{ $t("message.home.lijizhuce") }}</span
        >
        {{ $t("message.home.trading") }}
      </div>
    </div>
    <close-dialog></close-dialog>
  </div>
</template>
<script>
import { mapState, mapActions, mapStores } from "pinia";
import { useUserStore } from "@/store/user";
import Axios from "@/api/perpetualContract.js";
import CloseDialog from "./positionCom/dialog/closeDialog.vue";
import Position from "./positionCom/currentPosition.vue";
import CurrentApplyPosition from "./positionCom/currentApplyPosition.vue";
import HistoryApplyPosition from "./positionCom/historyApplyPosition.vue";
import HistoryOrder from "./positionCom/historyOrder.vue";
import Asset from "./positionCom/asset.vue";
export default {
  name: "posotion",
  props: {
    keys: {
      type: String,
    },
    symbol: {
      type: String,
    },
    paramsType: {
      type: String,
      default: "indices",
    },
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  components: {
    Position,
    CurrentApplyPosition,
    HistoryApplyPosition,
    HistoryOrder,
    Asset,
    CloseDialog,
  },
  data() {
    return {
      showher: false,
      checked7: false,
      checked8: false,
      checked9: false,
      activeName: "first",
      positionNum: "0",
      curNum: "0",
    };
  },

  filters: {
    fliterDir(val) {
      if (val == "buy") {
        return this.$t("message.home.kaiduo");
      } else if (val == "sell") {
        return this.$t("message.home.kaiduo");
      }
    },
    leverFilter(val) {
      if (val) {
        var data = Number(val).toFixed(2);
        return data + "X";
      } else {
        return "1.00X";
      }
    },
    directionFilter(val) {
      if (val == "buy") {
        return this.$t("message.home.kaiduo");
      } else if (val == "sell") {
        return this.$t("message.home.kaikong");
      }
    },
    stateFilter(val) {
      switch (val) {
        case "submitted":
          return this.$t("message.home.yichengjiao");

        case "canceled":
          return this.$t("message.home.yichexiao");

        case "created":
          return this.$t("message.home.weituowancheng");
      }
    },
  },
  mounted() {
    this.host_url = window.location.hostname;

    if (this.existToken && this.symbol) {
      this.getCurList();
    }
  },
  watch: {
    checkedIndexType(val) {
      if (val != "currentOrders") {
        clearInterval(this.timerCurre);
      } else {
        this.initcurrentGetListTimer();
      }
    },
    symbol(val) {
      if (this.existToken && this.symbol) {
        this.getCurList();
      }
    },
  },
  methods: {
    // 初始获取
    getCurList() {
      const data = {
        page_no: 1,
        type: "orders",
        symbol: this.symbol,
      };
      Axios.contractApplyOrderOpenList(data).then((res) => {
        this.curNum = res.data.length || "0";
      });
    },
    //获取当前委托合计
    getCurNum(val) {
      this.curNum = val;
    },
    //获取仓位合计
    getPositionNum(val) {
      this.positionNum = val;
    },
    gotoPage(path) {
      this.$router.push(path);
    },
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  unmounted() {
    clearInterval(this.timerCurre);
  },
};
</script>
<style>
@import url("@/assets/css/commonTrade/global.css");
@import url("@/assets/css/commonTrade/constract/position/index.css");
@import url("@/assets/css/commonTrade/constract/position/select.css");
@import url("@/assets/css/commonTrade/constract/position/table.css");
@import url("@/assets/css/commonTrade/constract/position/datepicker.css");

.position-block {
  width: 100%;
  border-bottom: 1px solid #24272c;
}
.position-block ::-webkit-scrollbar {
  display: none; /* Chrome Safari */
}

.cheadbox {
  position: absolute;
  right: 0px;
  top: 5px;
  padding-right: 50px;
  z-index: 999;
}
.ticker-itemBox {
  position: absolute;
  top: -126px;
  right: -76px;
  z-index: 9999;
  background: #1f2328;
  color: #fff;
  padding: 10px 20px;
  border-radius: 5px;
}
.ticker-itemBox .el-checkbox {
  color: #fff !important;
}

.ticker-itemBox div {
  height: 30px;
}
.cheadbox .con {
  display: inline-block;
  position: relative;
  width: 40px;
  height: 25px;
  vertical-align: top;
}
.cheadbox .con i {
  font-size: 8px;
  color: #707a8a;
  position: absolute;
  top: 3px;
  right: 0px;
}
.cheadbox .con i:nth-child(2) {
  top: 8px;
}
.logins {
  line-height: 100px;
  text-align: center;
}
.logins span {
  color: #409eff;
  padding: 0px 5px;
  cursor: pointer;
}
/* 表格 */
.history-order,
.position,
.history-apply-position,
.current-apply-position,
.asset {
  overflow: scroll;
  width: 100%;
  height: 500px;
}
</style>
