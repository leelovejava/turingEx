<template>
  <div class="mod-role">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data.sync="dataList"
      :option="tableOption"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('user:agent:revise')"
          @click.stop="addOrUpdateHandle()"
          >新增代理商</el-button
        >
      </template>
      <template slot-scope="scope" slot="userNamen">
        <el-button
          type="text"
          @click.stop="openIdmen(scope.row.userId)"
          >{{scope.row.userName}}</el-button
        >
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('user:agent:revise')"
          @click.stop="addOrUpdateHandle(scope.row, scope.row.id)"
          >修改</el-button
        >
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('user:agent:revise')"
          @click.stop="addPaUpdateHandle(scope.row.id)"
          >重置登录密码</el-button
        >
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('user:agent:revise')"
          @click.stop="
            updategoogleAuthCode(
              scope.row.googleAuthBind,
              scope.row.id,
              scope.row
            )
          "
          >谷歌验证</el-button
        >
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
    <!-- 谷歌验证 -->
    <add-or-gogle
      v-if="UpdateGogle"
      ref="UpdateGogle"
      @refreshDataList="getDataList"
    ></add-or-gogle>
    <!-- 密码修改 -->
    <add-pa-update
      v-if="addPassUpdateVisible"
      ref="addPrUpdate"
      @refreshDataList="getDataList"
    ></add-pa-update>
  </div>
</template>

<script>
import { tableOption } from "@/crud/user-relation/agent";
import AddOrUpdate from "./agent-add-or-update";
import AddPaUpdate from "./agent-password-update";
import AddOrGogle from "./system-suer-sys-googleAuthCode.vue";

export default {
  data() {
    return {
      dataList: [],
      dataListLoading: false,
      UpdateGogle: false,
      addPassUpdateVisible: false,
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
    AddOrGogle,
    AddPaUpdate,
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
        url: this.$http.adornUrl("/agent/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
              viewType: "list",
              userName: "",
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
    openIdmen(id,page, done){
      const params = {
        current: page == null ? this.page.currentPage : page.currentPage,
        size: page == null ? this.page.pageSize : page.pageSize,
        ...this.searchParams,
      };
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/agent/child"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              userId:id,
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
              viewType: "list",
              userName: "",
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
    // 谷歌验证
    addOrUpdateGogle(googleAuthBind, id, data) {
      this.UpdateGogle = true;
      this.$nextTick(() => {
        this.$refs.UpdateGogle.init(googleAuthBind, id, data);
      });
    },
    // 谷歌验证码绑定
    updategoogleAuthCode(googleAuthBind, id, data) {
      if (googleAuthBind) {
        this.$confirm("谷歌验证器已绑定", "谷歌验证器", {
          distinguishCancelAndClose: true,
          confirmButtonText: "确定",
          cancelButtonText: "解绑",
          type: "success",
        })
          .then(() => {})
          .catch((action) => {
            if (action === "cancel") {
              console.log(id);
              this.addOrUpdateGogle(googleAuthBind, id, data);
            }
          });
      } else {
        this.addOrUpdateGogle(googleAuthBind, id, data);
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
    addOrUpdateHandle(row, id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(row, id);
      });
    },
    // 密码修改
    addPaUpdateHandle(id) {
      this.addPassUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addPrUpdate.init(id);
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
