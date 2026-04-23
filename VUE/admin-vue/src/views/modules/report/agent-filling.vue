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
        <!-- Tag页 -->
        <!-- <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="ETF" name="1"></el-tab-pane>
          <el-tab-pane label="数字货币" name="2"></el-tab-pane>
          <el-tab-pane label="外汇" name="3"></el-tab-pane>
          <el-tab-pane label="美股" name="4"></el-tab-pane>
        </el-tabs> -->

        <!-- 搜索 -->
        <!-- <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList(this.page)">
          
          <el-form-item label="用户名/UID:">
            <el-input v-model="dataForm.orderNumber2"
                      placeholder="用户名/UID"
                      clearable></el-input>
          </el-form-item>

          <el-form-item label="起始时间:">
            <el-date-picker v-model="dateRange"
                            type="datetimerange"
                            range-separator="至"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期">
            </el-date-picker>
          </el-form-item>

          <el-form-item>
            <el-button type="primary"
                      icon="el-icon-search"
                      size="small"
                      @click="getDataList()">查询</el-button>
            <el-button @click="clearDatas()"
                      size="small">清空</el-button>
          </el-form-item>
        </el-form> -->
      </template>
      <template slot-scope="scope" slot="reco_member">
        <el-button class="left" type="text" @click.stop="quarePath(scope.row.partyId)">{{
          scope.row.reco_member
        }}</el-button>
        <el-button class="right" type="text" @click.stop="getReconNumNet(scope.row.partyId)">
          网络
        </el-button>
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
    <otherFill
      v-if="otherFillVisible"
      ref="otherOrUpdate"
      @refreshDataList="getDataList"
    ></otherFill>
  </div>
</template>
<script>
import { tableOption } from "@/crud/report/agent-filling";
import AddOrUpdate from "./agent-filling-add-or-update";
import otherFill from "./all-filling-other";
export default {
  data() {
    return {
      dataList: [],
      activeName: "1",
      dataListLoading: false,
      otherFillVisible:false,
      dateRange: "",
      dataListSelections: [],
      addOrUpdateVisible: false,
      tableOption: tableOption,
      searchParams: {}, // 搜索条件
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      dataForm: {},
    };
  },
  components: {
    AddOrUpdate,
    otherFill
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
        url: this.$http.adornUrl("/statistics/agentAllStatistics"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            params
          )
        ),
      }).then(({ data }) => {
        this.dataList = data.data.records;
        this.page.total = data.data.total;
        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },
    addClasscolor({ column, row }) {//表单样式
      if (
        (column.property === "business_profit" && row.business_profit * 1 > 0) ||
        (column.property === "totle_fee" && row.totle_fee * 1 > 0) ||
        (column.property === "totle_income" && row.totle_income * 1 > 0)
      ) {
        return "green";
      } else if (
        (column.property === "business_profit" && row.business_profit * 1 < 0) ||
        (column.property === "totle_fee" && row.totle_fee * 1 < 0) ||
        (column.property === "totle_income" && row.totle_income * 1 < 0)
      ) {
        return "red";
      } else {
        return "";
      }
    },
    // 条件查询
    searchChange(params, done) {
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
    getReconNumNet(id){
      this.otherFillVisible = true;
      this.$nextTick(() => {
        this.$refs.otherOrUpdate.init(id);
      });
    },
    quarePath(id) {
      this.$router.push({ path: "/report-income", query: { partyId: id } });
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
      //this.getDataList();
    },
  },
};
</script>
<style lang="less" scoped>
.left{
  float: left;
}
.right{
  float: right;
}
</style>