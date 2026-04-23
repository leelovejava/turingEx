<template>
  <div class="mod-mange">
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
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('user:mange:addDemoAccount')"
          @click.stop="addOrUpdateHandle()"
          >新增演示账号</el-button
        >
      </template>
      <template slot-scope="scope" slot="userName">
        <span @click="searchName(scope.row)" class="seachButton">{{
          scope.row.userName
        }}</span>
      </template>
      <template slot-scope="scope" slot="recomUserName">
        <span @click="searchName(scope.row)" class="speacButton">{{
          scope.row.recomUserName
        }}</span>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('user:mange:addDemoAccount')"
          @click.stop="addOrUpdateHandle(scope.row, scope.row.userId)"
          >编辑</el-button
        >
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('user:mange:addDemoAccount')"
          @click.stop="follow(scope.row.userId, scope.row.follow)"
          >{{scope.row.follow == 0?'开启跟单':'关闭跟单'}}</el-button
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
    <parameters v-if="parametersFlag" ref="parameters"> </parameters>
  </div>
</template>
<script>
import { tableOption } from "@/crud/sys/mange";
import AddOrUpdate from "./mange-add-or-update";
import parameters from "./grid-parameters";
export default {
  data() {
    return {
      parametersFlag: false,
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      tableOption: tableOption,
      searchParams: {}, // 搜索条件
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
      const params = {
        current: page == null ? this.page.currentPage : page.currentPage,
        size: page == null ? this.page.pageSize : page.pageSize,
        ...this.searchParams,
      };
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/userData/list"),
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
    // 平仓
    follow(ID, follow) {
      this.$confirm(
        follow == 0
          ? "您确定要进行开启跟单操作吗"
          : "您确定要进行关闭跟单操作吗",
        follow == 0 ? "是否确认开启跟单" : "是否确认关闭跟单",
        {
          //系统管理用户是否已绑定
          distinguishCancelAndClose: true,
          confirmButtonText: "取消",
          cancelButtonText: follow == 0 ? "开启跟单" : "关闭跟单",
          type: "success",
        }
      )
        .then(() => {})
        .catch((action) => {
          if (action === "cancel") {
            this.$http({
              url: this.$http.adornUrl("/user/follow/" + ID),
              method: "post",
              data: this.$http.adornData(Object.assign({})),
            }).then(({ data }) => {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.getDataList();
                },
              });
            });
          } else {
          }
        });
    },
    addClasscolor({ column, row }) {
      //
      //表单样式
      if (
        (column.property === "rolename" && row.rolename == "MEMBER") ||
        (column.property === "online" && row.online == true) ||
        (column.property === "follow" && row.follow == 1)||
        (column.property === "realNameAuthority" && row.realNameAuthority == true)
      ) {
        return "green";
      } else if (
        (column.property === "rolename" && row.rolename == "GUEST") ||
        (column.property === "follow" && row.follow == 0)||
        (column.property === "realNameAuthority" && row.realNameAuthority == false)
      ) {
        return "yellow";
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
    addOrUpdateHandle(row, id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(row, id);
      });
    },
    // 删除
    deleteHandle(id) {
      var userIds = id
        ? [id]
        : this.dataListSelections.map((item) => {
            return item.userId;
          });
      this.$confirm(
        `确定对[id=${userIds.join(",")}]进行[${id ? "删除" : "批量删除"}]操作?`,
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
                this.getDataList();
              },
            });
          });
        })
        .catch(() => {});
    },
    searchName(row) {
      //this.userNamevisible = true
      this.parametersFlag = true;
      this.$nextTick(() => {
        this.$refs.parameters.init(row);
      });
    },
  },
};
</script>
<style lang="scss" scoped>
.mod-mange {
}

.speacButton {
  color: rgb(69, 147, 235);
  cursor: pointer;
}
.speacButton:hover {
  color: #356ced;
}
</style>
