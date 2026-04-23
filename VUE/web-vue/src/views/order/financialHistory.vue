<template>
  <el-tabs v-model="activeName" class="order-tab">
    <el-tab-pane :label="$t('message.user.jijinlicai')" name="financial">
      <el-table :data="tableData" class="width100">
        <el-table-column
          prop="closeTimeStr"
          :label="$t('message.user.shuhuishijian')"
          :empty-text="$t('message.home.noData')"
        ></el-table-column>
        <el-table-column
          prop="orderNo"
          :label="$t('message.user.dingdanhao')"
        ></el-table-column>
        <el-table-column
          prop="amount"
          :label="
            $t('message.user.tuoguanjine') +
            '/' +
            $t('message.user.tuoguanshijian')
          "
        >
          <template #default="scope">
            <div>
              <span class="green"
                >{{ scope.row.amount }}（{{
                  scope.row.buyCurrency?.toUpperCase()
                }})</span
              >
              <span> /{{ scope.row.cycle }} {{ $t("message.user.tian") }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="daily_rate"
          :label="$t('message.user.dangrishouyi')"
        >
          <template #default="scope">
            <div>{{ scope.row.dailyRate }} %</div>
          </template>
        </el-table-column>
        <el-table-column prop="symbol_to" :label="$t('message.user.yihuoli')">
          <template #default="scope">
            <div>
              {{ scope.row.profit }}（{{
                scope.row.buyCurrency?.toUpperCase()
              }})
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="symbol_to"
          :label="$t('message.user.shengyutianshu')"
        >
          <template #default="scope">
            <div>{{ scope.row.days }} {{ $t("message.user.tian") }}</div>
          </template>
        </el-table-column>
      </el-table>
    </el-tab-pane>
    <el-tab-pane :label="$t('message.user.kuangchisuocang')" name="miner">
      <el-table
        :data="mineData"
        class="width100"
        :header-row-class-name="getRowClass"
        :empty-text="$t('message.home.noData')"
      >
        <el-table-column
          prop="order_no"
          :label="$t('message.user.dingdanhao')"
        ></el-table-column>
        <el-table-column
          prop="create_time"
          :label="$t('message.user.tuoguanshijian')"
        ></el-table-column>
        <!-- 简体中文：miner_name，繁体：miner_name_cn 英文：miner_name_en -->
        <el-table-column
          :prop="getLocalLan()"
          :label="$t('message.user.kuangjimingcheng')"
        ></el-table-column>
        <el-table-column prop="amount" :label="$t('message.user.suocangjine')">
          <template #default="scope">
            <div>
              <span
                >{{ scope.row.amount }} ({{
                  scope.row.buyCurrency?.toUpperCase()
                }})</span
              >
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="cycle" :label="$t('message.user.zhouqi')">
          <template #default="scope">
            <div>
              {{
                scope.row.cycle == 0
                  ? $t("message.user.wuqixian")
                  : scope.row.cycle
              }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="daily_rate"
          :label="$t('message.user.dangrishouyi')"
        >
          <template #default="scope">
            <div>{{ scope.row.daily_rate }} %</div>
          </template>
        </el-table-column>
        <el-table-column prop="symbol_to" :label="$t('message.user.yihuoli')">
          <template #default="scope">
            <div>
              {{ scope.row.profit }} （{{
                scope.row.outputCurrency?.toUpperCase()
              }}）
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="symbol_to"
          :label="$t('message.user.shengyutianshu')"
        >
          <template #default="scope">
            <div>{{ scope.row.days }} {{ $t("message.user.tian") }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="state" :label="$t('message.user.jiesuo')">
          <template #default="scope">
            <div>
              {{
                scope.row.state == "1"
                  ? $t("message.user.shoudongjiesuo")
                  : $t("message.user.yijiesuo")
              }}
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-tab-pane>
  </el-tabs>
</template>

<script>
import { getFinanceOrder, getMinerOrder } from "@/api/order.js";
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";
export default {
  name: "financialHistory",
  data() {
    return {
      activeName: "financial",
      tableData: [],
      mineData: [],
    };
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  mounted() {
    if (this.$route.query.type) {
      this.activeName = this.$route.query.type;
    }

    if (this.existToken) {
      this.getList();
      this.getMineList();
    }
  },
  methods: {
    // TODO 接口404
    //基金理财列表，state=2为已赎回的 state=4全包括
    async getList() {
      const res = await getFinanceOrder({
        state: 4,
        page_no: 1,
      });
      this.tableData = res.data;
    },
    //矿池锁仓列表,state=2为已解锁的
    async getMineList() {
      const res = await getMinerOrder({
        state: 2,
        page_no: 1,
      });
      this.mineData = res.data;
    },
    //理财详情
    goDetail() {},
    //矿池详情
    goMineDetail() {},
    //给表头设置边框线
    getRowClass({ rowIndex, columnIndex }) {
      if (rowIndex == 0) {
        return "border-top:1px solid #EBEEF5";
      }
    },

    getLocalLan() {
      var lang = JSON.parse(localStorage.getItem("lang"));
      // 简体中文：miner_name，繁体：miner_name_cn miner_name_en
      if (lang == "en") {
        return "miner_name_en";
      } else if (lang == "cht") {
        return "miner_name_cn";
      } else if (lang == "zh-CN") {
        return "miner_name";
      }
      return "miner_name_en";
    },
  },
};
</script>
