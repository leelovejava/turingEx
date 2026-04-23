<template>
  <div class="right-padding">
    <div class="right-title">{{ $t("message.user.duihuanlishi") }}</div>
    <div class="margin-top-bottom20">
      <el-table
        :data="tableData"
        class="width100"
        :header-row-class-name="getRowClass"
        :empty-text="$t('message.home.noData')"
      >
        <el-table-column
          prop="create_time"
          :label="$t('message.user.shijian')"
        ></el-table-column>
        <el-table-column prop="symbol" :label="$t('message.user.maichu')">
          <template #default="scope">
            <div>
              {{ scope.row.amount }} {{ scope.row.symbol?.toUpperCase() }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="symbol_to" :label="$t('message.user.mairu')">
          <template #default="scope">
            <div>
              {{ scope.row.amount_to }} {{ scope.row.symbol_to?.toUpperCase() }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="state" :label="$t('message.user.zhuangtai')">
          <template #default="scope">
            <div
              :class="{
                green: scope.row.state == 'created',
                blue: scope.row.state == 'submitted',
                red: scope.row.state == 'canceled',
              }"
            >
              {{ status[scope.row.state] }}
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
// TODO 接口出参变了，兑换列表，
import { getExchangeOrder } from "@/api/order.js";
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";
export default {
  name: "exchangeHistory",
  data() {
    return {
      tableData: [], //列表数组
      total: 0,
      pageNum: 1,
      tableLength: 0,
      isNext: false,
      status: {
        submitted: this.$t("message.user.yitijiao"),
        canceled: this.$t("message.user.yichexiao"),
        created: this.$t("message.user.yiwancheng"),
      },
    };
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  mounted() {
    if (this.existToken) {
      this.getList();
    }
  },
  methods: {
    //获取列表数据
    async getList() {
      const res = await getExchangeOrder({
        page_no: this.pageNum,
      });
      this.tableData = res.data;
      this.tableLength = res.data.length;
    },

    handleCurrentChange(val) {
      this.pageNum = val;
      this.getList();
    },

    //给表头设置边框线
    getRowClass({ rowIndex, columnIndex }) {
      if (rowIndex == 0) {
        return "border-top:1px solid #EBEEF5";
      }
    },
  },
};
</script>

