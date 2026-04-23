<template>
  <div class="right-padding">
    <div class="right-title">{{ $t("message.user.zhangbainjilu") }}</div>
    <!-- 列表内容 -->
    <div>
      <div class="margin-top-bottom20">
        <el-select
          v-model="selectValue"
          clearable
          :placeholder="$t('message.user.qingxuanzezhanghuleixing')"
          class="select-style"
          style="width: 250px"
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
        <el-button type="primary" class="search-button" @click="getList">{{
          $t("message.user.chaxun")
        }}</el-button>
      </div>

      <el-table
        :data="tableData"
        class="width100"
        :header-row-class-name="getRowClass"
        :empty-text="$t('message.home.noData')"
      >
        <el-table-column
          prop="createTime"
          :label="$t('message.user.shijian')"
          :formatter="getFormatTime"
        ></el-table-column>
        <!-- 账户类型 -->
        <el-table-column
          prop="category"
          :label="$t('message.user.zhanghu') + $t('message.user.leixing')"
        >
          <template #default="scope">
            <div>{{ filterCategory(scope.row.category) }}</div>
          </template>
        </el-table-column>
        <!-- 描述 -->
        <el-table-column
          prop="content_type"
          :label="$t('message.user.miaoshu')"
        >
          <template #default="scope">
            <div>{{ filterDescrible(scope.row.content_type) }}</div>
          </template>
        </el-table-column>
        <!-- 金额 -->
        <el-table-column prop="amount" :label="$t('message.user.jine')">
          <template #default="scope">
            <span :style="{ color: scope.row.amount < 0 ? 'red' : 'green' }">
              {{ Number(scope.row.amount).toFixed(8) }}&nbsp;</span
            >
            <span> {{ scope.row?.wallet_type?.toUpperCase() }}</span>
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
import { getChangeRecord } from "@/api/order.js";
import dayjs from "dayjs";
export default {
  name: "changeRecord",
  data() {
    return {
      //账户类型
      options: [
        {
          value: "coin",
          label: this.$t("message.user.fabijiaoyi"),
        },
        {
          value: "contract",
          label: this.$t("message.user.heyuejiaoyi"),
        },
        {
          value: "exchange",
          label: this.$t("message.user.bibijiaoyi"),
        },
        {
          value: "finance",
          label: this.$t("message.user.licai"),
        },
        {
          value: "miner_buy",
          label: this.$t("message.user.kuangji"),
        },
      ],
      selectValue: "", //下拉选中的值
      tableData: [], //列表数组
      pageNum: 1,
      tableLength: 0,
      isNext: false,
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    getFormatTime(row, column, cellValue, index) {
      const { createTime, createTimeStr } = row;
      // let res = createTimeStr;
      // if (!createTimeStr) {
      //   res = dayjs(createTime).format("YYYY-MM-DD hh:mm:ss");
      // }
      // return res;
      return createTime;
    },
    //处理账户类型值
    filterCategory(name) {
      switch (name) {
        case "coin":
          return this.$t("message.user.fabijiaoyi");
        case "contract":
          return this.$t("message.user.heyuejiaoyi");
        case "exchange":
          return this.$t("message.user.现货交易");
        case "finance":
          return this.$t("message.user.licai");
        case "miner":
          return this.$t("message.user.kuangji");
        case "otc":
          return 'OTC';
        case "loan":
          return this.$t("message.user.zhiyajiebi");
        case "bank_card":
          return this.$t("message.user.yinhangka");
        case "c2c":
          return 'C2C';
        case "ipo_promise":
          return this.$t("message.user.新股认购");
        case "ipo_sell":
          return this.$t("message.user.新股认购");
        default:
          return name;
      }
    },
    //处理描述的返回值
    filterDescrible(name) {
      switch (name) {
        case "withdraw":
          return this.$t("message.user.tibi");
        case "recharge":
          return this.$t("message.user.chongbi");
        case "contract_close":
          return this.$t("message.user.yongxuheyuepingcang");
        case "contract_open":
          return this.$t("message.user.yongxuheyuejiancang");
        case "contract_cancel":
          return this.$t("message.user.yongxuheyuechedan");
        case "fee":
          return this.$t("message.user.shouxufei");
        case "exchange_open":
          return this.$t("message.user.bibimairu");
        case "exchange_close":
          return this.$t("message.user.bibimaichu");
        case "exchange_cancel":
          return this.$t("message.user.bibichedan");
        case "finance":
          return this.$t("message.user.licai");
        case "finance_profit":
          return this.$t("message.user.licaishouyi");
        case "finance_recom_profit":
          return this.$t("message.user.licaituiguangshouyi");
        case "miner_buy":
          return this.$t("message.user.suocangkuangji");
        case "miner_recom_profit":
          return this.$t("message.user.kuangjituiguangshouyi");
        case "miner_profit":
          return this.$t("message.user.kuangjishouyi");
        case "miner_back":
          return this.$t("message.user.kuangjishuhui");
        case "fund_close":
          return this.$t("message.user.shuhui");
        case "fund_open":
          return this.$t("message.user.goumai");
        case "otc_sell":
          return this.$t("message.user.otcmaibi");
        case "otc_buy":
          return this.$t("message.user.otcmaibi2");
        case "otc_cancel":
          return this.$t("message.user.otcdingdanquxiao");
        case "loan_frozen":
          return this.$t("message.user.dongjie");
        case "loan_thaw":
          return this.$t("message.user.jiedong");
        case "loan_closeout":
          return this.$t("message.user.qiangping");
        case "loan_add":
          return this.$t("message.user.jiedai");
        case "loan_repay":
          return this.$t("message.user.haikuan");
        case "finance_buy":
          return this.$t("message.user.goumai");
        case "finance_back":
          return this.$t("message.user.shuhui");
        case "delivery_contract_open":
          return this.$t("message.user.jiaogeheyuekaicang");
        case "delivery_contract_close":
          return this.$t("message.user.jiaogeheyuepingcang");
        case "bank_card_withdraw":
          return this.$t("message.user.tixian");
        case "bank_card_order_cancel":
          return this.$t("message.user.银行卡订单取消");
        case "bank_card_recharge":
          return this.$t("message.user.yinhangkachongzhi");
        case "c2c_sell":
          return this.$t("message.user.c2cmaibi");
        case "c2c_buy":
          return this.$t("message.user.c2c买币");
        case "ipo_promise":
          return this.$t("message.user.认缴");
        case "ipo_sell":
          return this.$t("message.user.卖出");
        case "stocks_open":
          return this.$t("message.user.股票买入");
        case "stocks_close":
          return this.$t("message.user.股票卖出");
        case "stocks_cancel":
          return this.$t("message.user.股票取消");
        case "exchange_open":
          return this.$t("message.user.加密货币买入");
        case "exchange_close":
          return this.$t("message.user.加密货币卖出");
        case "exchange_cancel":
          return this.$t("message.user.加密货币取消");
        case "etf_open":
          return this.$t("message.user.ETF买入");
        case "etf_close":
          return this.$t("message.user.ETF卖出");
        case "etf_cancel":
          return this.$t("message.user.ETF取消");
        case "follow_up_fee":
          return this.$t("message.user.gendanshouxufei");
        case "exchange_lever_close":
          return this.$t("message.user.quancanggangganpingcang");
        case "exchange_lever_open":
          return this.$t("message.user.quancanggangganjiancang");
        case "exchange_lever_interest":
          return this.$t("message.user.quancanggangganlixi");
        default:
          return "--";
      }
    },
    //获取列表数据
    async getList() {
      const res = await getChangeRecord({
        page_no: this.pageNum,
        category: this.selectValue,
      });
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
  },
};
</script>

<style scoped>
.search-button {
  width: 100px;
  margin-left: 10px;
}

.select-style {
  width: 200px;
  border-radius: 4px;
}
</style>
