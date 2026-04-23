<!-- 现货订单 -->
<template>
  <div class="margin-top-bottom20">
    <!-- :row-style="rowStyles"-->
    <el-table
      :data="tableData"
      class="width100"
      :header-row-class-name="getRowClass"
      :empty-text="$t('message.home.noData')"
    >
      <el-table-column
        prop="create_time"
        :label="$t('message.home.shijian')"
        :width="180"
      ></el-table-column>
      <!-- <el-table-column prop="order_no" label="订单号"></el-table-column>  -->
      <el-table-column
        prop="name"
        :label="$t('message.home.tradingPair')"
      ></el-table-column>
      <!-- 类型 -->
      <el-table-column
        prop="order_price_type"
        :label="$t('message.jiaoyi.leixing')"
      >
        <template #default="scope">
          <div>
            {{
              scope.row.order_price_type == "limit"
                ? $t("message.home.xianjiaweituo")
                : $t("message.home.shijiaweituo")
            }}
          </div>
        </template>
      </el-table-column>
      <!-- 方向 -->

      <el-table-column prop="direction" :label="$t('message.home.direction')">
        <template #default="scope">
          <div :class="scope.row.offset == 'open' ? 'green' : 'red'">
            {{
              scope.row.offset == "open"
                ? $t("message.jiaoyi.mairu")
                : $t("message.home.maichu")
            }}
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="price"
        :label="$t('message.home.chengjiaojunjia')"
      ></el-table-column>
      <el-table-column
        prop="volume"
        :label="$t('message.user.chengjiaoshuliang')"
      ></el-table-column>
      <el-table-column
        prop="fee"
        :label="$t('message.home.shouxufei')"
      ></el-table-column>
      <el-table-column prop="fee" :label="$t('message.home.chengjiaojine')">
        <template #default="scope">
          {{ (scope.row.price * scope.row.volume).toFixed(5) }}
        </template>
      </el-table-column>
      <!-- <el-table-column prop="profit" :label="$t('message.user.shijiyingkui')">
                    <template  #default="scope">
                        <div :class="scope.row.profit > 0 ? 'green' : 'red'">
                            {{ scope.row.profit }}
                        </div>
                    </template>
                    </el-table-column> -->
      <!-- <el-table-column prop="change_radio" :label="$t('message.user.shouyilv') + '(%)'">
                    <template  #default="scope">
                        <div :class="scope.row.change_ratio > 0 ? 'green' : 'red'">
                            {{ scope.row.change_ratio }}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="state" :label="$t('message.user.zhuangtai')">
                    <template  #default="scope">
                        <div>
                            {{
                                scope.row.state == "created"
                                ? $t("message.user.yipingcang")
                                : $t("message.user.chicang")
                            }}
                        </div>
                    </template>
                    </el-table-column> -->
    </el-table>
    <el-pagination
      class="pagination-box"
      v-model:current-page="pageNum"
      :default-page-size="20"
      layout="total, prev, pager, next, jumper"
      :total="tableLength"
      @current-change="handleCurrentChange"
    />
  </div>
</template>
<script>
import { getSpotOrder } from "@/api/order.js";
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";
export default {
  name: "spotOrder",
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
      const data = {
        page_no: this.pageNum,
        type: "hisorders",
        isAll: true,
        symbolType: this.type,
      };
      const res = await getSpotOrder(data);
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
