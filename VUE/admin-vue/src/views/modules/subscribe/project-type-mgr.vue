<template>
  <div class="mod-role">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @on-load="getDataList"
    >
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('sys:role:update')"
          @click.stop="addOrUpdateHandle(scope.row.roleId)"
          >编辑</el-button
        >

        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('sys:role:delete')"
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
  </div>
</template>

<script>
import { tableOption } from "@/crud/subscribe/project-type-mgr";
import AddOrUpdate from "./project-type-mgr-add-or-update";
export default {
  data() {
    return {
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
    getDataList(page, params, done) {
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
        url: this.$http.adornUrl("/moneylog/list"),
        method: "post",
        data: this.$http.adornData({
          current: this.page.currentPage,
          size: this.page.pageSize,
          rolename: this.dataForm.rolename,
          userCode: this.dataForm.userCode,
          userId: this.dataForm.userId,
          startTime: this.dataForm.startTime,
          endTime: this.dataForm.endTime,
        }),
      }).then(({ data }) => {
        this.dataList = data.data.records;
        this.page.total = data.total;
        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },
    // 条件查询
    searchChange(params, done) {
      this.getDataList(this.page, params, done);
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
  },
};
</script>
