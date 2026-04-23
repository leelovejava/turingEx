<template>
  <div>
    <date-search />
    <el-table
      class="history-order"
      :data="tableData"
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
    </el-table>
    <Pagination
      class="pagitation-wrapper"
      :noPre="noPre"
      :noNext="noNext"
      :pageNum="pageNum"
      @changePageNum="changePageNum"
    />
  </div>
</template>

<script>
import Axios from "@/api/perpetualContract.js";
import Pagination from "@/components/common/pagination.vue";
import dateSearch from "./dateSearch.vue";
export default {
  name: "historyOrder",
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
      startTime: "",
      endTime: "",
      noNext: false,
      noPre: false,
      tables: [
        {
          prop: "create_time",
          label: "message.home.shijian",
        },
        {
          prop: "name",
          label: "message.jiaoyi.heyue",
          // formatter: this.nameFormatter,
        },

        {
          prop: "direction",
          label: "message.home.fangxiang",
          formatter: this.directionFormatter,
        },
        {
          prop: "trade_avg_price",
          label: "message.home.chengjiaojunjia",
        },

        {
          prop: "volume_open",
          label: "message.home.shuliang",
        },
        {
          prop: "fee",
          label: "message.home.shouxufei",
        },
        // 止盈止损价
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
          label: "message.home.yishixianyingkui",
          formatter: this.profitFormatter,
        },
      ],
      tableData: [],
    };
  },
  components: {
    dateSearch,
    Pagination,
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
    profitFormatter(row) {
      return Number(row.profit).toFixed(4);
    },
    //分页
    changePageNum(type) {
      if (type == "next") {
        if (!this.noNext) {
          this.pageNum = this.pageNum + 1;
        }
      } else {
        if (!this.noPre && this.pageNum > 1) {
          this.pageNum = this.pageNum - 1;
        }
      }
      this.getList();
    },
    cellStyle({ row, column, rowIndex, columnIndex }) {
      if (columnIndex == 2) {
        if (row.direction == "sell") {
          return { color: "#E05561" };
        } else {
          return { color: "#62C885" };
        }
      }
      // // 未实现盈亏
      if (columnIndex == 6) {
        if (row.profit < 0) {
          return { color: "#E05561" };
        } else {
          return { color: "#62C885" };
        }
      }
    },

    directionFormatter(row) {
      const { direction } = row;
      return direction == "buy"
        ? this.$t("message.home.pingduo")
        : this.$t("message.home.pingkong");
    },
    //获取列表数据
    getList(startTime = "", endTime = "", isReset) {
      // 轮训的时候，把之前的数据存一下
      //重置，时间也没有，但必须赋值
      if (startTime || endTime || isReset) {
        this.startTime = startTime;
        this.endTime = endTime;
      }
      const data = {
        page_no: this.pageNum,
        type: "hisorders",
        symbol: this.symbol,
        endTime: this.endTime,
        startTime: this.startTime,
        symbolType: this.paramsType,
      };

      Axios.contractOrderList(data).then((res) => {
        this.tableData = res.data;
        const noNext = res.data.length == 0 || res.data.length < 10;
        this.noNext = noNext;
      });
    },
  },
};
</script>
