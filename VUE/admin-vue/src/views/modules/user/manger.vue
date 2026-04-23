<template>
  <div class="mod-manger">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      @search-change="searchChange"
      :cell-class-name="addClasscolor"
      @selection-change="selectionChange"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <!-- <el-button type="primary"
                   icon="el-icon-plus"
                   size="small"
                   v-if="isAuth('sys:user:save')"
                   @click.stop="addOrUpdateHandle()">新增</el-button> -->

        <!-- <el-button type="danger"
                   @click="deleteHandle()"
                   v-if="isAuth('sys:user:delete')"
                   size="small"
                   :disabled="dataListSelections.length <= 0">批量删除</el-button> -->
      </template>
      <template slot-scope="scope" slot="userName">
        <span @click="searchName(scope.row)" class="seachButton">{{
          scope.row.userName
        }}</span>
      </template>
      <template slot-scope="scope" slot="property">
        <span @click="lookProperty(scope.row)" class="seachButton">{{
          "查看资产"
        }}</span>
      </template>
      <template slot-scope="scope" slot="moneyslot">
        <span
          class="seachButton"
          @click="
            moneyCheack(scope.row.userId, '钱包', false, scope.row.userCode)
          "
          >{{ scope.row.money }}</span
        >
      </template>
      <template slot-scope="scope" slot="menu">
        <el-select
          v-model="scope.row.select"
          class="celectSpeac"
          clearable
          v-if="isAuth('user:manger:operate')"
          placeholder="操作"
          @change="
            changeSelet(
              scope.row.userId,
              scope.row.select,
              scope.row.userCode,
              scope.row
            )
          "
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
        <!-- <el-button type="primary"
                   icon="el-icon-edit"
                   size="small"
                   v-if="isAuth('sys:user:update')"
                   @click.stop="addOrUpdateHandle(scope.row.userId)">编辑</el-button>

        <el-button type="danger"
                   icon="el-icon-delete"
                   size="small"
                   v-if="isAuth('sys:user:delete')"
                   @click.stop="deleteHandle(scope.row.userId)">删除</el-button> -->
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
    <!-- 弹窗, 新增 / 修改 -->
    <otherdate
      v-if="otherdateVisible"
      ref="otherdate"
      @refreshDataList="getDataList"
    ></otherdate>
    <!-- 参数管理 -->
    <parameters v-if="parametersFlag" ref="parameters"> </parameters>
    <!-- 参数管理 -->
    <parameters2 v-if="parametersFlag2" ref="parameters2"> </parameters2>
  </div>
</template>
<script>
import { tableOption } from "@/crud/sys/manger";
import AddOrUpdate from "./mang-fix-update-message";
import otherdate from "./mangr-other-update";
import parameters from "./grid-parameters";
import parameters2 from "./property-parameters";
export default {
  data() {
    return {
      parametersFlag2: false,
      parametersFlag: false,
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      tableOption: tableOption,
      otherdateVisible: false,
      searchParams: {}, // 搜索条件
      options: [
        {
          value: "1",
          label: "修改账户余额",
        },
        {
          value: "2",
          label: "转移账户锁定金额",
        },
        {
          value: "3",
          label: "增加账户锁定金额",
        },
        {
          value: "4",
          label: "减少账户锁定金额",
        },
        {
          value: "5",
          label: "转移账户冻结金额",
        },
        {
          value: "6",
          label: "修改提现限制流水",
        },
        {
          value: "7",
          label: "重置登录密码",
        },
        {
          value: "8",
          label: "解绑谷歌验证器",
        },
        {
          value: "9",
          label: "重置资金密码",
        },
        {
          value: "10",
          label: "强制用户退出登录",
        },
        {
          value: "11",
          label: "账变日志",
        },
        {
          value: "12",
          label: "赠送用户USDT",
        },
      ],
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
    };
  },
  components: {
    AddOrUpdate,
    otherdate,
    parameters,
    parameters2,
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
        url: this.$http.adornUrl("/user/list"),
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
    addClasscolor({ column, row }) {
      //表单样式
      if (column.property === "rolename" && row.rolename == "MEMBER") {
        return "green";
      } else if (column.property === "rolename" && row.rolename == "GUEST") {
        return "yellow";
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
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
    // 新增 / 修改
    otherdateHand(uid, m, val, userCode) {
      this.otherdateVisible = true;
      this.$nextTick(() => {
        this.$refs.otherdate.init(uid, m, val, userCode);
      });
    },
    moneyCheack(uid, m, val, userCode) {
      //资金查询
      this.otherdateHand(uid, m, val, userCode);
    },
    changeSelet(uid, val, userCode, row) {
      if (val) {
        let m = this.options[val - 1].label; //弹窗标题
        if (
          val == 1 ||
          val == 8 ||
          val == 7 ||
          val == 9 ||
          val == 6 ||
          val == 10 ||
          val == 12
        ) {
          // 1资金账户 2转移账户锁定金额 3增加账户锁定金额 4减少账户锁定金额 5转移账户冻结金额 8谷歌验证 7密码 9资金密码 6提现限制流水 10强制退出 12 赠送USDT
          this.addOrUpdateVisible = true;
          this.$nextTick(() => {
            this.$refs.addOrUpdate.init(uid, m, val,row);
          });
        } else if (val == 2 || val == 5 || val == 3 || val == 4) {
          this.otherdateHand(uid, m, val, userCode);
        } else if (val == 11) {
          this.$router.push({
            path: "/sys-config-account-change-record-sys-config",
            query: { uid: userCode },
          });
          // router.push({ name: 'account-change-record-sys-config' })
        }
        row.select = "";
      }
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
    lookProperty(row) {
      //this.userNamevisible = true
      this.parametersFlag2 = true;
      this.$nextTick(() => {
        this.$refs.parameters2.init(row);
      });
    },
  },
};
</script>
<style>
/* .el-select-dropdown__wrap{
  max-height:none;
} */
</style>
<style lang="scss" scoped>
/* 设置下拉框的高度，根据需求自行调整 */
// ::v-deep .celectSpeac .el-select .el-input__inner .el-select-dropdown__wrap {
//   max-height: 434px !important;
// }
/* 以下是下拉框操作按钮样式 */
::v-deep .celectSpeac .el-input__inner {
  background: #1c4efa !important;
}
::v-deep .celectSpeac .el-input__inner::placeholder {
  color: #fff;
}
.speacButton {
  color: rgb(69, 147, 235);
  cursor: pointer;
}
.speacButton:hover {
  color: #356ced;
}

</style>
