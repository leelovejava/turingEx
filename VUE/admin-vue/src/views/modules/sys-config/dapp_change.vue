<template>
  <div class="mod-transport">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <!-- 搜索 -->
        <el-form
          :inline="true"
          :model="dataForm"
          @keyup.enter.native="getDataList(this.page)"
        >
          <el-form-item label="用户名/UID:">
            <el-input
              v-model="dataForm.orderNumber2"
              placeholder="用户名/UID"
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item label="账号">
            <el-select v-model="dataForm.orderNumber2" placeholder="所有账号">
              <el-option label="正式账号" value="shanghai"></el-option>
              <el-option label="演示账号" value="beijing"></el-option>
              <el-option label="试用账号" value="beijing"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="类型">
            <el-select v-model="dataForm.orderNumber2" placeholder="所有类型">
              <el-option label="转账" value="shanghai"></el-option>
              <el-option label="转换(提现)" value="beijing"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="起始时间:">
            <el-date-picker
              v-model="dateRange"
              type="datetimerange"
              range-separator="至"
              value-format="yyyy-MM-dd HH:mm:ss"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            >
            </el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              icon="el-icon-search"
              size="small"
              @click="getDataList()"
              >查询</el-button
            >
            <el-button @click="clearDatas()" size="small">清空</el-button>
          </el-form-item>
        </el-form>
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
import { tableOption } from "@/crud/sys/dapp_change";
import AddOrUpdate from "./dapp_change-add-or-update";
export default {
  data() {
    return {
      dataForm: {},
      dateRange: [],
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
        current: this.pageIndex,
        size: this.pageSize,
      };

      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/dAppLog/list"),
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

<style scoped></style>
