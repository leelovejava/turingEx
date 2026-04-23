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
      @refresh-change="refreshChange"
    >
      <template slot="menuLeft">
        <!-- Tag页 -->
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="全部" name=""></el-tab-pane>
          <el-tab-pane label="计息中" name="1"></el-tab-pane>
          <el-tab-pane label="已结清" name="2"></el-tab-pane>
          <el-tab-pane label="强平结算" name="3"></el-tab-pane>
        </el-tabs>
      </template>
      <template slot-scope="scope" slot="orderNo">
        <span @click="searchOrderNo(scope.row)" class="seachButton">{{
          scope.row.orderNo
        }}</span>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('mining:pledge-order:operate')"
          @click.stop="addOrUpdateHandle(scope.row.roleId)"
          >编辑</el-button
        >

        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('mining:pledge-order:operate')"
          @click.stop="deleteHandle(scope.row.roleId)"
          >删除</el-button
        >
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
    <!-- 参数管理 -->
    <parameters
      v-if="parametersFlag"
      @refreshDataList="getDataList"
      ref="parameters"
    >
    </parameters>
  </div>
</template>

<script>
import { tableOption } from "@/crud/mining/pledge-order";
import AddOrUpdate from "./pledge-order-add-or-update";
import parameters from "./pledge-order-parameters";
export default {
  data() {
    return {
      dataForm: {},
      dataList: [],
      activeName: "",
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      parametersFlag: false,
      tableOption: tableOption,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
    };
  },
  components: {
    AddOrUpdate,
    parameters,
  },
  methods: {
    // 获取数据列表
    getDataList(page, done) {
      this.dataListLoading = true;
      let obj = Object.assign(
            {
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
              ...this.searchParams
            },    
          )
      let state = this.activeName;
      console.log("state = " + state);
      console.log("this.activeName = " + this.activeName);
      if(state > 0){
        obj = Object.assign(
            {
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
              ...this.searchParams,
              state:state
            },    
          )
      }

      this.$http({
        url: this.$http.adornUrl("/normal/adminLoanOrder!list.action"),
        method: "get",
        params: this.$http.adornParams(
          obj
        ),
      }).then(({data}) => {
        // console.log("data => " + JSON.stringify(data));
        this.dataList = data.data.page.records;
        this.page.total = data.data.page.total;
        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },
    addClasscolor({ column, row }) {
      //表单样式
      if (
        (column.property === "rolename" && row.rolename == "MEMBER") ||
        (column.property === "state" && row.state == "3") ||
        (column.property === "direction" && row.direction == "recharge")
      ) {
        return "green";
      } else if (
        (column.property === "rolename" && row.rolename == "GUEST") ||
        (column.property === "state" && row.state == "0")
      ) {
        return "yellow";
      } else if (
        (column.property === "state" && row.state == "5") ||
        (column.property === "direction" && row.direction == "withdraw")
      ) {
        return "red";
      }
    },
    handleClick(tab, event) {
      console.log(tab, event);
      this.getDataList();
    },
    // 条件查询
    searchChange(params, done) {
      this.page.currentPage = 1; // 重置当前页为第一页
      if (params["orderNo1"]) {
        params["orderNo"] = params["orderNo1"];
      }
      this.searchParams = params;
      this.getDataList(this.page, done);
      // this.getDataList(this.page, params, done);
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
    searchOrderNo(row) {
      //this.userNamevisible = true
      this.parametersFlag = true;
      this.$nextTick(() => {
        this.$refs.parameters.init(row);
      });
    },
    // 刷新回调用
    refreshChange () {
      console.log("refreshChange")
      this.page = this.$refs.crud.$refs.tablePage.defaultPage
      this.getDataList(this.page)
      this.dataListSelections = []
      this.$refs.crud.selectClear()
    },
  },
};
</script>
