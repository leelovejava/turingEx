<!-- 合约历史订单 -->
<template>
  <div>
    <!-- <div class="right-title">
      {{ $t("message.user.heyuelishi") }}
    </div> -->
    <!-- 列表内容 -->
    <!-- 
        :row-style="rowStyles" -->
    <div class="margin-top-bottom20">
      <el-table
        :data="tableData"
        class="width100"
        :header-row-class-name="getRowClass"
        :empty-text="$t('message.home.noData')"
      >
        <el-table-column
          prop="close_time"
          :label="$t('message.user.shijian')"
          :formatter="formatterDate"
          :width="180"
        ></el-table-column>
        <!-- <el-table-column prop="order_no" label="订单号"></el-table-column>  -->
        <el-table-column
          prop="name"
          :label="$t('message.user.bizhong')"
        ></el-table-column>
        <el-table-column prop="direction" :label="$t('message.user.leixing')">
          <template #default="scope">
            <div :class="scope.row.direction == 'buy' ? 'green' : 'red'">
              {{
                scope.row.direction == "buy"
                  ? $t("message.home.kaiduo")
                  : $t("message.home.kaikong")
              }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="trade_avg_price"
          :label="$t('message.user.kaicangjiage')"
        ></el-table-column>
        <el-table-column
          prop="amount_open"
          :label="$t('message.user.chengjiaoshuliang')"
        ></el-table-column>
        <el-table-column
          prop="close_avg_price"
          :label="$t('message.user.pingcangjiage')"
        ></el-table-column>
        <el-table-column
          prop="fee"
          :label="$t('message.user.shouxufei')"
        ></el-table-column>
        <el-table-column prop="profit" :label="$t('message.user.shijiyingkui')">
          <template #default="scope">
            <div :class="scope.row.profit > 0 ? 'green' : 'red'">
              {{ scope.row.profit }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="change_radio"
          :label="$t('message.user.shouyilv') + '(%)'"
        >
          <template #default="scope">
            <div :class="scope.row.change_ratio > 0 ? 'green' : 'red'">
              {{ scope.row.change_ratio }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="state" :label="$t('message.user.zhuangtai')">
          <template #default="scope">
            <div>
              {{
                scope.row.state == "created"
                  ? $t("message.user.yipingcang")
                  : $t("message.user.chicang")
              }}
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <el-pagination
        class="pagination-box"
        v-model:current-page="pageNum"
        :default-page-size="20"
        layout="total, prev, pager, next, jumper"
        :total="tableLength"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>
<script>
import { getContractOrder } from "@/api/order.js";
import dayjs from "dayjs";
export default {
  name: "contractHistoryOrder",
  props: {
    type: String,
  },
  data() {
    return {
      tableData: [], //列表数组
      pageNum: 1,
      tableLength: 0,
    };
  },
  mounted() {
    let spToken = localStorage.getItem("spToken");
    if (spToken) {
      this.getList();
    }
  },
  methods: {
    formatterDate(row) {
      return dayjs.unix(row.create_time_ts).format("YYYY-MM-DD HH:mm:ss");
    },

    //获取列表数据
    async getList() {
      const data = {
        page_no: this.pageNum,
        type: "hisorders",
        symbol: "",
        symbolType: this.type,
      };
      const res = await getContractOrder(data);
      this.tableData = res.data;
      this.tableLength = res.data.length;
      if (this.tableLength == 0 || this.tableLength < 10) {
        this.isNext = true;
      } else {
        this.isNext = false;
      }
    },

    handleCurrentChange(val) {
      this.pageNum = val;
      this.getList();
    },

    //给表头设置上边框线
    getRowClass({ rowIndex, columnIndex }) {
      if (rowIndex == 0) {
        return "border-top:1px solid #EBEEF5";
      }
    },
    //行高修改，需以对象形式返回
    rowStyles({ row, rowIndex }) {
      let styleJson = {
        height: "50px",
      };
      return styleJson;
    },
  },
};
</script>
