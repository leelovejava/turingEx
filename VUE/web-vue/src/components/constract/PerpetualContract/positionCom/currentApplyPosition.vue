<!-- 当前委托 = 还未成交的限价单 -->
<template>
  <el-table
    class="current-apply-position"
    :data="showData"
    :cell-style="cellStyle"
    :empty-text="$t('message.home.noData')"
  >
    <el-table-column
      v-for="(item, index) in tables"
      :key="index"
      :prop="item.prop"
      :label="$t(item.label)"
      :formatter="item.formatter"
    />
    <el-table-column align="right">
      <template #header>
        <el-select
          v-model="selectVal"
          popper-class="position-select"
          placeholder="Select"
          size="small"
          @change="selectChange"
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="$t(item.label)"
            :value="item.value"
          />
        </el-select>
      </template>
      <template #default="scope">
        <el-button
          class="btn"
          size="small"
          @click="handleClose(scope.$index)"
          >{{ $t("message.home.chedan") }}</el-button
        >
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
import Axios from "@/api/perpetualContract.js";
import { ElMessage } from "element-plus";
import dayjs from "dayjs";
export default {
  emits: ["getCurNum"],
  name: "currentApplyPosition",
  props: {
    symbol: {
      type: String,
    },
  },
  data() {
    return {
      pageNum: 1,
      timer: null,
      options: [
        {
          value: "all",
          label: "message.home.quanbu",
        },
        {
          value: "limit",
          label: "message.home.xianjia",
        },

        {
          value: "opponent",
          label: "message.home.shijia",
        },
      ],
      tables: [
        {
          prop: "create_time_ts",
          label: "message.home.shijian",
          formatter: (row) =>
            dayjs.unix(row.create_time_ts).format("YYYY-MM-DD HH:mm:ss"),
        },
        {
          prop: "name",
          label: "message.jiaoyi.heyue",
        },

        {
          prop: "order_price_type",
          label: "message.home.type",
          formatter: this.priceFormatter,
        },
        // 这里需要组合 如平多  offset=close  direction=buy
        {
          prop: "direction",
          label: "message.home.fangxiang",
          formatter: this.directionFormatter,
        },

        {
          prop: "price",
          label: "message.home.jiage",
        },
        {
          prop: "volume_open",
          label: "message.home.weituoshuliang",
        },
        {
          prop: "volume",
          label: "message.user.chengjiaoshuliang",
          formatter: this.volumnFormatter,
        },
      ],
      selectVal: "all",
      tableData: [],
      showData: [],
    };
  },
  mounted() {
    this.getList();
    this.timer = setInterval(() => {
      this.getList();
    }, 2000);
  },

  unmounted() {
    clearInterval(this.timer);
    this.timer = null
  },
  methods: {
    cellStyle({ row, column, rowIndex, columnIndex }) {
      if (columnIndex == 3) {
        if (row.direction == "sell") {
          return { color: "#E05561" };
        } else {
          return { color: "#62C885" };
        }
      }
    },
    //获取列表数据
    getList() {
      const data = {
        page_no: this.pageNum,
        type: "orders",
        symbol: this.symbol,
      };

      Axios.contractApplyOrderOpenList(data).then((res) => {
        this.tableData = res.data;
        this.filterData();
        this.$emit("getCurNum", res.data?.length || "0");
      });
    },
    priceFormatter(row) {
      const text =
        row.order_price_type === "limit" ? "xianjiaweituo" : "shijiaweituo";
      return this.$t(`message.home.${text}`);
    },
    directionFormatter(row) {
      const map = {
        "close buy": "pingduo",
        "open buy": "kaiduo",
        "close sell": "pingkong",
        "open sell": "kaikong",
      };
      const { direction, offset } = row;
      const value = `${offset} ${direction}`;
      const text = map[value];
      return this.$t(`message.home.${text}`);
    },
    volumnFormatter(row) {
      //委托数量(剩余)(张),委托数量(张)
      const { volume, volume_open } = row;
      return volume_open - volume;
    },
    //过滤数据
    filterData() {
      if (this.selectVal == "all" || !this.selectVal) {
        this.showData = this.tableData;
        return;
      }
      this.showData = this.tableData.filter(
        (it) => it.order_price_type == this.selectVal
      );
    },

    selectChange() {
      this.filterData();
    },
    // 撤单
    handleClose(index) {
      const item = this.tableData[index];
      const data = {
        order_no: item.order_no,
      };
      const msg = `${this.$t("message.home.chedan")} ${this.$t(
        "message.user.chenggong"
      )}`;
      Axios.cancel(data).then((res) => {
        ElMessage({
          message: msg,
          type: "success",
        });
        this.getList();
      });
    },
  },
};
</script>
<style lang="css" scoped>
.btn {
  background: #409eff;
  color: #fff;
}
.btn:hover {
  background: #409eff;
  color: #fff;
}
</style>
