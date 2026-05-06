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
          v-if="isAuth('tzUserOld:save')"
          type="primary"
          icon="el-icon-plus"
          size="small"
          @click.stop="addOrUpdateHandle()"
        >Add</el-button>
        <el-button
          v-if="isAuth('tzUserOld:delete')"
          type="danger"
          icon="el-icon-delete"
          size="small"
          :disabled="dataListSelections.length <= 0"
          @click.stop="deleteHandle()"
        >Batch Delete</el-button>
      </template>
      <template slot="menu" slot-scope="scope">
        <el-button
          v-if="isAuth('tzUserOld:update')"
          type="primary"
          icon="el-icon-edit"
          size="small"
          @click.stop="addOrUpdateHandle(scope.row)"
        >Edit</el-button>
        <el-button
          v-if="isAuth('tzUserOld:delete')"
          type="danger"
          icon="el-icon-delete"
          size="small"
          @click.stop="deleteHandle(scope.row.id)"
        >Delete</el-button>
      </template>
    </avue-crud>

    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    />
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
      tableOption,
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
  methods: {
    getDataList(page, done) {
      const params = {
        current: page == null ? this.page.currentPage : page.currentPage,
        size: page == null ? this.page.pageSize : page.pageSize,
        ...this.searchParams,
      };
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/admin/userOld/list"),
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
    searchChange(params, done) {
      this.searchParams = params;
      this.page.currentPage = 1;
      this.getDataList(null, done);
    },
    refreshChange() {
      this.getDataList();
    },
    selectionChange(val) {
      this.dataListSelections = val;
    },
    addOrUpdateHandle(row) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(row);
      });
    },
    deleteHandle(id) {
      const ids = id ? [id] : this.dataListSelections.map((item) => item.id);
      if (ids.length === 0) {
        this.$message.warning("Please select records to delete");
        return;
      }
      this.$confirm("Confirm delete selected records?", "Tip", {
        confirmButtonText: "OK",
        cancelButtonText: "Cancel",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/admin/userOld/deleteBatch"),
            method: "post",
            data: this.$http.adornData(ids),
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message.success("Deleted");
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
