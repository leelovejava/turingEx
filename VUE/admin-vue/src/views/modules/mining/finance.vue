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
    <template slot-scope="scope" slot="investment_min">
      <span>{{ scope.row.investment_min }}</span>-<span>{{ scope.row.investment_max }}</span>
    </template>
    <template slot="menuLeft">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('mining:finance:operate')"
          @click.stop="addOrUpdateHandle()"
          >新增</el-button
        >
      </template>
      <template slot-scope="scope" slot="methodImg">
        <img :src="scope.row.img" alt="" width="100"  />
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('mining:finance:operate')"
          @click.stop="addOrUpdateHandle(scope.row)"
          >编辑</el-button
        >
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
  </div>
</template>

<script>
import { tableOption } from "@/crud/mining/finance";
import AddOrUpdate from "./finance-add-or-update";
export default {
  data() {
    return {
      searchParams:{},
      dataForm: {},
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
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
  },
  methods: {
    // 获取数据列表
    getDataList(page, done) {
      let obj = {
        current: 1,
        endTime: "",
        rolename: "",
        size: 10,
        startTime: "",
        userCode: "",
        userId: "",
      };

      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/normal/adminFinanceAction!list.action"),
        method: "get",
        params: this.$http.adornParams({
          current: this.page.currentPage,
          size: this.page.pageSize,
          ...this.searchParams,
          // name_para:this.searchParams.mame,
          rolename: this.dataForm.rolename,
          userCode: this.dataForm.userCode,
          userId: this.dataForm.userId,
          startTime: this.dataForm.startTime,
          endTime: this.dataForm.endTime,
        }),
      }).then(({ data }) => {
        console.log("data => " + JSON.stringify(data));
        this.dataList = data.data.records;
        this.page.total = data.data.pages;
        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },
    // 条件查询
    searchChange(params, done) {
      this.page.currentPage = 1; // 重置当前页为第一页
      if(params["name"]){
        params["name_para"] = params["name"]
      }
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
    addClasscolor({ column, row }) {//表单样式
      if (
        (column.property === "state" && row.state*1 == 1)
      ) {
        return "green";
      } else if (
        (column.property === "state" && row.state*1 == 0) 
      ) {
        return "red";
      } else {
        return "";
      }
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
  },
};
</script>
