<template>
  <div class="mod-transport">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @close="handClose"
      @on-load="getDataList"
    >
      <template slot-scope="scope" slot="log">
        <span 
          class="speacButton" @click="log(scope.row)" v-if="show == 0 && isAuth('sys-config:verification-code-log:search')"
          >点击查看</span
        >
        <span class="speacButton1" v-if="show == 1">{{ scope.row.log }}</span>
      </template>
    </avue-crud>

    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>

    <!-- 确认弹窗-start -->
    <el-dialog
      title="验证资金密码"
      :visible.sync="dialogFormVisible"
      :append-to-body="true"
    >
      <el-form
        :model="dataForm2"
        ref="dataForm2"
        @keyup.enter.native="dataFormSubmit()"
        label-width="80px"
      >
        <el-form-item
          label="登录人资金密码"
          :label-width="formLabelWidth"
          prop="loginSafeword"
        >
          <el-input
            v-model="dataForm2.loginSafeword"
            type="password"
            placeholder="登录人资金密码"
            autocomplete="off"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="dataFormSubmit()">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 确认弹窗-end -->
  </div>
</template>

<script>
import { tableOption } from "@/crud/sys/verification-code-log";
import AddOrUpdate from "./verification-code-log-add-or-update";
import { encrypt } from "@/utils/crypto";
export default {
  data() {
    return {
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
      formLabelWidth: "120px",
      dialogFormVisible: false,
      dataForm2: {},
      searchParams: {}, // 搜索条件
      rowData: {},
      show: 0,
    };
  },
  components: {
    AddOrUpdate,
  },
  methods: {
    // 获取数据列表
    getDataList(page, done) {
      let obj = {
        current: this.pageIndex,
        size: this.pageSize,
      };
      const params = {
        current: page == null ? this.page.currentPage : page.currentPage,
        size: page == null ? this.page.pageSize : page.pageSize,
        ...this.searchParams,
      };
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/codeLog/list"),
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
    handClose() {
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
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
    //查看更多参数
    log(row) {
      this.rowData = row || {};
      this.dialogFormVisible = true;
      // this.$nextTick(() => {
      //   this.$refs.addOrUpdate.init(row)
      // })
    },
    dataFormSubmit() {
      this.$http({
        url: this.$http.adornUrl("/checkLoginSafePassword"),
        method: "post",
        data: this.$http.adornData(
          Object.assign({
            loginSafeword: encrypt(this.dataForm2.loginSafeword),
          })
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.$message({
            message: "操作成功",
            type: "success",
            duration: 1500,
            onClose: () => {
              this.dialogFormVisible = false;
              this.dataList = [this.rowData];
              this.show = 1;
            },
          });
        } else {
          this.$message({
            message: data.msg,
            type: "error",
          });
        }
      });
    },
  },
};
</script>

<style scoped>
.speacButton {
  color: rgb(69, 147, 235);
  cursor: pointer;
}
.speacButton:hover {
  color: rgb(8, 63, 134);
}
</style>
