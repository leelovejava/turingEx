<template>
  <div class="mod-role">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      @search-change="searchChange"
      @selection-change="selectionChange"
      :cell-class-name="addClasscolor"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <!-- <el-checkbox class="chexDiv" v-model="checked">无上级代理商推广用户</el-checkbox> -->
        <!-- <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="ETF" name="1"></el-tab-pane>
          <el-tab-pane label="数字货币" name="2"></el-tab-pane>
          <el-tab-pane label="外汇" name="3"></el-tab-pane>
          <el-tab-pane label="美股" name="4"></el-tab-pane>
        </el-tabs> -->
      </template>
      <template slot-scope="scope" slot="moneyslot">
        <span class="seachButton" @click="moneyCheack(scope.row.user_code)">{{
          scope.row.money
        }}</span>
      </template>
      <template slot-scope="scope" slot="recoNum">
        <span class="seachButton" @click="searchSumName(scope.row.user_id)">{{
          scope.row.reco_num
        }}</span>
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
    <moneyUpdate
      v-if="moneyUpdateVisible"
      ref="moneyUpdat"
      @refreshDataList="getDataList"
    ></moneyUpdate>
  </div>
</template>
<script>
import { tableOption } from "@/crud/report/income";
import AddOrUpdate from "./income-add-or-update";
import moneyUpdate from "./income-money-update";
export default {
  data() {
    return {
      activeName: "1", //选项卡
      checked: true,
      dateRange: [],
      dataList: [],
      partyId: "",
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      moneyUpdateVisible: false,
      tableOption: tableOption,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      dataForm: {},
      searchParams: {}, // 搜索条件
    };
  },
  components: {
    AddOrUpdate,
    moneyUpdate,
  },
  created() {
    this.dataListLoading = true;
    if (this.$route.query.partyId) {
      this.partyId = this.$route.query.partyId;
    } else {
      this.partyId = "";
    }
  },
  watch: {
    $route(to, from) {
      this.dataListLoading = true;
      if (this.$route.query.partyId) {
        this.partyId = this.$route.query.partyId;
      } else {
        this.partyId = "";
      }
      this.getDataList(this.page);
    },
  },
  methods: {
    // 获取数据列表
    getDataList(page, done) {
      const params = {
        current: page == null ? this.page.currentPage : page.currentPage,
        size: page == null ? this.page.pageSize : page.pageSize,
        ...this.searchParams,
      };
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/statistics/userList"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              userId: this.partyId,
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            params
          )
        ),
      }).then(({ data }) => {
        if(data.code == 0){
          this.dataList = data.data.records;
          this.page.total = data.data.total;
        }    
        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },
    addClasscolor({ column, row }) {
      //表单样式
      if (
        (column.property === "role_name" && row.role_name == "MEMBER") ||
        (column.property === "gift_money" && row.gift_money * 1 > 0) ||
        (column.property === "difference" && row.difference * 1 > 0) ||
        (column.property === "business_profit" && row.business_profit * 1 > 0) ||
        (column.property === "miner_income" && row.miner_income * 1 > 0) ||
        (column.property === "totle_fee" && row.totle_fee * 1 > 0) ||
        (column.property === "totle_income" && row.totle_income * 1 > 0)
      ) {
        return "green";
      } else if (
        (column.property === "business_profit" && row.business_profit * 1 < 0) ||
        (column.property === "miner_income" && row.miner_income * 1 < 0) ||
        (column.property === "totle_income" && row.totle_income * 1 < 0)
      ) {
        return "red";
      } else if (column.property === "role_name" && row.role_name == "MEMBER1") {
        return "yellow";
      } else {
        return "";
      }
    },
    searchSumName(userId, page, params, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/statistics/userList"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              userId: userId,
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            params
          )
        ),
      }).then(({ data }) => {
        if(data.code == 0){
          this.dataList = data.data.records;
          this.page.total = data.data.total;
        }
        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },
    moneyCheack(uid) {
      //资金查询
      this.moneyUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.moneyUpdat.init(uid);
      });
    },
    // 条件查询
    searchChange(params, done) {
      this.partyId = "";
      this.page.currentPage = 1; // 重置当前页为第一页
      this.searchParams = params;
      this.getDataList(this.page, done);
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
    },
    // 新增 / 修改
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
    // 删除
    deleteHandle(id) {
      var ids = id
        ? [id]
        : this.dataListSelections.map((item) => {
            return item.roleId;
          });
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/sys/role"),
            method: "delete",
            data: this.$http.adornData(ids, false),
          }).then(({ data }) => {
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.getDataList();
              },
            });
          });
        })
        .catch(() => {});
    },
    // 选项卡
    handleClick(tab, event) {
      //console.log(this.activeName)
      if (this.activeName == 1) {
        this.state = "submitted";
      } else {
        this.state = "";
      }
      this.getDataList();
    },
    handleClick2(tab, event) {
      this.dataForm.paraTime = this.getTag2();
      //console.log(this.activeName2)
      // if (this.activeName2 == 1) {
      //   this.state = "";
      // } else if (this.activeName2 == 2) {
      //   this.state = "submitted";
      // } else {
      //   this.state = "created";
      // }
      this.getDataList();
    },
    getTag2() {
      //当天 day 当周 week 当月 month 全部 all
      let m = {
        1: "day",
        2: "week",
        3: "month",
        4: "all",
      };
      return m[this.activeName2];
    },
  },
};
</script>
<style scoped>
.chexDiv {
  margin-left: 20px;
}
</style>
