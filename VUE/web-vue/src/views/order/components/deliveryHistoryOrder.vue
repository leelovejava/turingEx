<!-- 交割历史订单 -->
<template>
  <div>
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
          prop="name"
          :label="$t('message.home.bizhong')"
        ></el-table-column>

        <el-table-column
          prop="close_time"
          :label="$t('message.home.kaicangshijian')"
          :formatter="getFormatTime"
          :width="180"
        ></el-table-column>
        <el-table-column
          prop="direction"
          :label="$t('message.home.fangxiang')"
          :width="60"
        >
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
          prop="amount"
          :label="$t('message.home.zhangshu')"
        ></el-table-column>

        <el-table-column
          prop="fee"
          :label="$t('message.home.shouxufei')"
        ></el-table-column>

        <el-table-column :label="$t('message.home.jiaogeshijian')">
          <template #default="scope">
            <div>{{ scope.row.time_num }} {{ scope.row.time_unit }}</div>
          </template>
        </el-table-column>

        <el-table-column
          prop="open_price"
          :label="$t('message.home.goumaijia')"
        ></el-table-column>
        <el-table-column
          prop="close_price"
          :label="$t('message.home.jiesuanjia')"
        ></el-table-column>

        <el-table-column prop="profit" :label="$t('message.home.yingkui')">
          <template #default="scope">
            <div :class="scope.row.profit > 0 ? 'green' : 'red'">
              {{ scope.row.profit }}
            </div>
          </template>
        </el-table-column>

        <el-table-column
          prop="settlement_time"
          :label="$t('message.home.daoqishijian')"
          :formatter="getFormatTime"
          :width="180"
        ></el-table-column>
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
import { getDeliveryOrder } from "@/api/order.js";
import dayjs from "dayjs";
export default {
  name: "deliveryHistoryOrder",
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
    getFormatTime(row, column, cellValue, index) {
      return dayjs.unix(cellValue).format("YYYY-MM-DD HH:mm:ss");
    },
    //获取列表数据
    async getList() {
      const data = {
        page_no: this.pageNum,
        type: "hisorders",
        symbol: "",
        symbolType: this.type,
      };
      const res = await getDeliveryOrder(data);
      this.tableData = res.data;
      this.tableLength = res.data.length;
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
