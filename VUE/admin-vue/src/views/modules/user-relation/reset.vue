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
      @refresh-change="getDataList"
    >
      <template slot="menuLeft">
        <avue-tabs :option="option" @change="handleChange"></avue-tabs>
        <span v-if="type.prop === 'tab1'"></span>
        <span v-else-if="type.prop === 'tab2'"></span>
        <span v-else-if="type.prop === 'tab3'"></span>
        <span v-else-if="type.prop === 'tab4'"></span>
      </template>
      <template slot-scope="scope" slot="userName">
        <el-button type="text" @click.stop="searchName(scope.row)">{{
          scope.row.userName
        }}</el-button>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="scope.row.status == 1 && isAuth('user:reset:review')"
          @click.stop="addOrUpdateHandle(scope.row.uuid, 1)"
          >审核通过</el-button
        >

        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="scope.row.status == 1 && isAuth('user:reset:review')"
          @click.stop="addOrUpdateHandle(scope.row.uuid, 2)"
          >驳回</el-button
        >
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
    <moreMsg
      v-if="moreMsgVisible"
      ref="moreMsgUpdate"
      @refreshDataList="getDataList"
    ></moreMsg>
  </div>
</template>

<script>
import { tableOption } from "@/crud/user-relation/reset";
import AddOrUpdate from "./reset-add-or-update";
import moreMsg from "./reset-more-msg";
export default {
  data() {
    return {
      activeName: "1",
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      moreMsgVisible: false,
      tableOption: tableOption,
      option: {
        column: [
          {
            label: "全部",
            prop: "",
          },
          {
            label: "待审核",
            prop: 1,
          },
          {
            label: "审核通过",
            prop: 2,
          },
          {
            label: "未通过",
            prop: 3,
          },
        ],
      },
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      dataForm: {},
      searchParams: {}, // 搜索条件
    };
  },
  components: {
    AddOrUpdate,
    moreMsg,
  },
  created() {
    this.type = this.option.column[0];
  },
  mounted() {
    this.$bus.$on("updateOfReset", (data) => {
      this.getDataList();
    });
  },
  beforeDestroy() {
    // 组件被销毁了，不能进行数据传输
    // 解绑事件
    this.$bus.$off("updateOfReset");
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
        url: this.$http.adornUrl("/userSafewordApply/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              status: this.status,
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
              // type:"indices",
              // offset:"open",
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
      if (
        (column.property === "roleName" && row.roleName == "MEMBER") ||
        (column.property === "status" && row.status == 2) ||
        (column.property === "realNameAuthority" &&
          row.realNameAuthority == true)
      ) {
        return "green";
      } else if (column.property === "roleName" && row.roleName == "GUEST") {
        return "yellow";
      } else if (column.property === "status" && row.status == 3) {
        return "red";
      }
    },
    // 选项卡
    handleClick(tab, event) {},
    // tab切换
    handleChange(column, params, done) {
      this.type = column;
      this.status = column.prop;
      this.getDataList(this.page, params, done);
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
    addOrUpdateHandle(id, type) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id, type);
      });
    },
    searchName(row) {
      this.moreMsgVisible = true;
      this.$nextTick(() => {
        this.$refs.moreMsgUpdate.init(row);
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
