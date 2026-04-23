<template>
  <div>
    <el-table
      :data="tableData"
      class="position"
      :cell-style="cellStyle"
      :empty-text="$t('message.home.noData')"
    >
      <!-- 合约会显示buy和杠杆 -->
      <el-table-column :label="$t('message.jiaoyi.heyue')" width="280">
        <template #default="scope">
          <div class="first-column">
            <span
              class="direction"
              :class="`direction-${scope.row.direction}`"
            ></span>
            <span class="name"
              >{{ scope.row.name }} {{ $t("message.home.yongxu") }}</span
            >
            <span class="lever_rate">{{
              scope.row.lever_rate ? `${scope.row.lever_rate}x` : "1x"
            }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        v-for="(item, index) in tables"
        :key="index"
        :prop="item.prop"
        :label="$t(item.label)"
        :formatter="item.formatter"
      />
      <el-table-column :label="$t('message.home.caozuo')" min-width="100">
        <template #default="scope">
          <!-- <el-button
            class="btn"
            size="small"
            @click="handleClose(scope.$index)"
            >{{ $t("message.home.pingcang") }}</el-button
          > -->

          <el-button class="btn" size="small" @click="handleAllClose(scope)">{{
            $t("message.home.pingcang")
          }}</el-button>
          <div>
            <el-button
              class="btn margin-top20"
              size="small"
              @click="onProfitDialog(scope)"
              >{{ $t("message.jiaoyi.zhiyingzhisun") }}</el-button
            >
          </div>
        </template>
      </el-table-column>
    </el-table>
    <close-all-dialog ref="closeAllDialog"></close-all-dialog>
    <close-dialog ref="closeDialog"></close-dialog>
    <!-- 确认弹窗  -->
    <profit-loss-dialog ref="closeProfitDialog"></profit-loss-dialog>
  </div>
</template>

<script>
import CloseAllDialog from "./dialog/closeAllDialog.vue";
import CloseDialog from "./dialog/closeDialog.vue";
import Axios from "@/api/perpetualContract.js";
import bus from "vue3-eventbus";
import profitLossDialog from "@/components/constract/DeliveryContract/profitLossDialog.vue";

export default {
  emits: [
    "getPositionNum",
    "CurrencySort",
    "getSelectList",
    "checkCurrency",
    "filterFun",
    "deleteCollectFun",
  ],
  name: "position",
  props: {
    symbol: {
      type: String,
    },
    paramsType: {
      type: String,
      default: "indices",
    },
  },
  data() {
    return {
      pageNum: 1,
      timer: null,
      tables: [
        {
          prop: "volume", //volume_open持仓总张数，volume表示剩余
          label: "message.home.zhangshu",
        },
        {
          prop: "trade_avg_price",
          label: "message.user.kaicangjiage",
        },
        // {
        //   prop: "mark_price",
        //   label: "message.home.biaojijiage",
        // },
        {
          prop: "direction",
          label: "message.home.fangxiang",
          formatter: this.directionFormatter,
        },

        // {
        //   prop: "change_ratio",
        //   label: "message.home.baozhengjinbilv",
        //   formatter: this.ratioFormatter,
        // },
        {
          prop: "deposit",
          label: "message.home.baozhengjin",
        },
        {
          prop: "stop_price_loss",
          label: "message.jiaoyi.price_loss",
        },
        {
          prop: "stop_price_profit",
          label: "message.jiaoyi.price_profit",
        },
        {
          prop: "profit",
          label: "message.user.weishixianyingkui(shouyilv)",
          formatter: this.formatterData,
        },
      ],
      tableData: [],
      confirmVisible: false,
    };
  },
  mounted() {
    this.getList();
    this.timer = setInterval(() => {
      this.getList();
    }, 2000);
  },
  components: {
    profitLossDialog,
    CloseAllDialog,
    CloseDialog,
  },

  unmounted() {
    clearInterval(this.timer);
    this.timer = null
  },
  methods: {
    onProfitDialog(data) {
      this.$refs.closeProfitDialog.open(data);
    },
    ratioFormatter(row) {
      const { change_ratio } = row;
      // const ratio = `${bigDecimal.divide(change_ratio, 100, 4)}%`;
      const ratio = `${change_ratio}%`;
      return ratio;
    },
    directionFormatter(row, column, val) {
      return val === "buy"
        ? this.$t("message.home.kaiduo")
        : this.$t("message.home.kaikong");
    },
    formatterData(row) {
      const ratio = this.ratioFormatter(row);
      const profit = Number(row.profit).toFixed(4);
      return `${profit}(${ratio})`;
    },
    //设置单个单元格样式   行下标：rowIndex    列下标：columnIndex
    cellStyle({ row, column, rowIndex, columnIndex }) {
      if (columnIndex == 6) {
        if (Number(row.profit) > 0) {
          return { color: "#62C885" };
        } else {
          return { color: "#E05561" };
        }
      }
      if (columnIndex == 3) {
        return row.direction === "buy"
          ? { color: "#62C885" }
          : { color: "#E05561" };
      }
    },
    getList() {
      const data = {
        page_no: this.pageNum,
        type: "orders",
        symbol: this.symbol,
        symbolType: this.paramsType,
      };
      Axios.contractOrderList(data).then((res) => {
        this.tableData = res.data;
        this.$emit("getPositionNum", res.data.length);
        // 未实现盈亏
        const profitLoss = res.data.reduce(
          (accumulator, cur) => Number(accumulator) + Number(cur.profit),
          0
        );
        const dataloss = bigDecimal.round(profitLoss, 2);
        // 保证金率
        const marginRate = res.data.reduce(
          (accumulator, cur) => Number(accumulator) + Number(cur.change_ratio),
          0
        );
        const datarate = `${bigDecimal.round(marginRate, 2)}%`;
        bus.emit("ProfitLossRefresh", dataloss);
        bus.emit("MarginRefresh", datarate);
      });
    },
    handleClose(index) {
      const data = this.tableData[index];
      this.$refs.closeDialog.open(data);
    },
    handleAllClose(data) {
      this.$refs.closeAllDialog.open(data.row);
    },
  },
};
</script>
<style scoped>
@import url("@/assets/css/commonTrade/constract/position/table.css");
.first-column {
  display: flex;
  align-items: center;
}
.name {
  margin: 0 2px;
}
.direction {
  display: inline-block;
  height: 20px;
  width: 2px;
}
.direction-buy {
  background: #62c885;
}
.direction-sell {
  background: #e05561;
}
.lever_rate {
  color: #1d91ff;
  background: #243046;
  border-radius: 2px;
  padding: 2px 6px;
}
.btn {
  background: #409eff;
  color: #fff;
}
.el-button + .el-button {
  margin-left: 16px !important;
}
</style>
