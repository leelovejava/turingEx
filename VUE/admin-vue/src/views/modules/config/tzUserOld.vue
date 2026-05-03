<template>
  <div class="mod-transport">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @refresh-change="refreshChange"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('tzUserOld:save')"
          @click.stop="addOrUpdateHandle()"
          >新增</el-button
        >
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('tzUserOld:delete')"
          @click.stop="deleteHandle()"
          :disabled="dataListSelections.length <= 0"
          >批量删除</el-button
        >
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('tzUserOld:update')"
          @click.stop="addOrUpdateHandle(scope.row)"
          >编辑</el-button
        >
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('tzUserOld:delete')"
          @click.stop="deleteHandle(scope.row.id)"
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
import { tableOption } from "@/crud/config/tzUserOld";
import AddOrUpdate from "./tzUserOld-add-or-update";
export default {
  data() {
    return {
      dataForm: {},
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      tableOption: tableOption,
      searchParams: {},
      page: {
        total: 0,
        currentPage: 1,
        pageSize: 10,
      },
    };
  },
  components: {
    AddOrUpdate,
  },
  created() {},
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
        url: this.$http.adornUrl("/admin/tzUserOld/list"),
        method: "post",
        data: this.$http.adornData(params),
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.data.records;
          this.page.total = data.data.total;
        } else {
          this.$message.error(data.msg);
        }
        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },
    // 搜索触发
    searchChange(params, done) {
      this.searchParams = params;
      this.page.currentPage = 1;
      this.getDataList(null, done);
    },
    // 刷新回调
    refreshChange() {
      this.getDataList();
    },
    // 多选
    selectionChange(val) {
      this.dataListSelections = val;
    },
    // 新增 / 修改
    addOrUpdateHandle(row) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(row);
      });
    },
    // 删除
    deleteHandle(id) {
      var ids = id
        ? [id]
        : this.dataListSelections.map((item) => item.id);
      if (ids.length === 0) {
        this.$message.warning("请选择要删除的记录");
        return;
      }
      this.$confirm(`确定要删除选中的记录吗?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/admin/tzUserOld/deleteBatch"),
            method: "post",
            data: this.$http.adornData(ids),
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message.success("删除成功");
              this.getDataList();
            } else {
              this.$message.error(data.msg);
            }
          });
        })
        .catch(() => {});
    },
  },
};
</script>

<style scoped>
.mod-transport {
  padding: 20px;
}
</style>