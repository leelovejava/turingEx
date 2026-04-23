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
      <template slot="usendhSearch">
        <el-input v-model="username" placeholder="用户名" clearable></el-input>
      </template>
      <template slot="menuLeft">
        <el-button
          v-if="isAuth('sys:config:system-user-sys-config:operate')"
          type="primary"
          icon="el-icon-plus"
          size="small"
          @click.stop="addOrUpdateHandle()"
          >新增</el-button
        >

        <!-- <el-button
          type="danger"
          @click="deleteHandle()"
          v-if="isAuth('sys:user:delete')"
          size="small"
          :disabled="dataListSelections.length <= 0"
          >批量删除</el-button
        > -->
      </template>
      <template slot-scope="scope" slot="menu">
        <!-- <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('sys:config:system-user-sys-config:operate') && scope.row.username !== 'admin' && userName !== 'root'"
          @click.stop="addOrUpdateHandle(scope.row.userId)"
          >编辑</el-button
        > -->
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('sys:config:system-user-sys-config:operate')"
          @click.stop="addOrUpdateHandle(scope.row.userId)"
          >编辑</el-button
        >
        <!-- <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('sys:user:update') && scope.row.username !== 'admin' && userName !== 'root'"
          @click.stop="
            updategoogleAuthCode(
              scope.row.googleAuthBind,
              scope.row.userId,
              scope.row
            )
          "
          >谷歌验证</el-button
        > -->
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('sys:config:system-user-sys-config:operate')"
          @click.stop="
            updategoogleAuthCode(
              scope.row.googleAuthBind,
              scope.row.userId,
              scope.row
            )
          "
          >谷歌验证</el-button
        >
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('sys:user:root')"
          @click.stop="deleteHandle(scope.row.userId, scope.row.username)"
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
    <!-- 谷歌验证 -->
    <add-or-gogle
      v-if="UpdateGogle"
      ref="UpdateGogle"
      @refreshDataList="getDataList"
    ></add-or-gogle>
  </div>
</template>

<script>
import { tableOption } from "@/crud/sys/maneus";
import AddOrUpdate from "./userMange-add-or-update";
import AddOrGogle from "./system-suer-sys-googleAuthCode";

export default {
  data() {
    return {
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      username: "",
      addOrUpdateVisible: false,
      UpdateGogle: false,
      tableOption: tableOption,
      page: {
        total: 0,
        currentPage: 1,
        pageSize: 10,
      },
    };
  },
  components: {
    AddOrUpdate,
    AddOrGogle,
  },
  computed: {
    userName: { // 获取到登录用户名
      get() {
        return this.$store.state.user.name;
      },
      set(val) {
        this.$store.commit("user/updateName", val);
      },
    },
  },
  methods: {
    getDataList(page, params, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/sys/user/page"),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              username: this.username,
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            params
          )
        ),
      }).then(({ data }) => {
        this.dataList = data.records;
        this.page.total = data.total;
        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },
    searchChange(params, done) {
      this.page.currentPage = 1;
      this.getDataList(this.page, params, done);
    },
    selectionChange(val) {
      this.dataListSelections = val;
    },
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
    addOrUpdateGogle(googleAuthBind, id, data) {
      this.UpdateGogle = true;
      this.$nextTick(() => {
        this.$refs.UpdateGogle.init(googleAuthBind, id, data);
      });
    },
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
              this.addOrUpdateGogle(googleAuthBind, id, data);
            }
          });
      } else {
        this.addOrUpdateGogle(googleAuthBind, id, data);
      }
    },
    deleteHandle(id, name) {
      var userIds = id
        ? [id]
        : this.dataListSelections.map((item) => {
            return item.userId;
          });
      var username = name
        ? [name]
        : this.dataListSelections.map((item) => {
            return item.username;
          });
      this.$confirm(
        `确定对[用户${username.join(",")}]进行[${
          id ? "删除" : "批量删除"
        }]操作?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/sys/user"),
            method: "delete",
            data: this.$http.adornData(userIds, false),
          }).then(({ data }) => {
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.page.currentPage = 1;
                this.getDataList(this.page);
              },
            });
          });
        })
        .catch(() => {});
    },
  },
};
</script>
