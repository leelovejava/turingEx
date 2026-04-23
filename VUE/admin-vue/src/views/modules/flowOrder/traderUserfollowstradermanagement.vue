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
    >
      <template slot-scope="scope" slot="followType">
        <span>{{ scope.row.follow_type == '1'?'固定张数/':'固定比例/' }}</span
        ><span class="green">{{ scope.row.volume }}</span>
      </template>
      <template slot="menuLeft">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('sys:traderUserfollowstradermanagement:save')"
          @click.stop="addOrUpdateHandle()"
          >新增</el-button
        >
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('sys:traderUserfollowstradermanagement:save')"
          @click.stop="addOrUpdateHandle(scope.row.id)"
          >编辑</el-button
        >
        <el-button
        type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('sys:traderUserfollowstradermanagement:save')"
          @click.stop="deleteHandle(scope.row.id,scope.row.state)"
          >{{scope.row.state == '1'?'取消跟随':'跟随'}}</el-button
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
import { tableOption } from "@/crud/flowOrder/traderUserfollowstradermanagement";
import AddOrUpdate from "./traderUrfollowstraderm-add-or-update";
export default {
  data() {
    return {
      searchParams: {},
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
    getDataList(page, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/normal/adminTraderFollowUser!list.action"),
        method: "get",
        params: this.$http.adornParams({
          current: this.page.currentPage,
          size: this.page.pageSize,
          ...this.searchParams,

        }),
      }).then(({ data }) => {
        console.log("data => " + JSON.stringify(data));
        this.dataList = data.data.page.records;
        this.page.total = data.data.pageSize;
        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },
    // 条件查询
    searchChange(params, done) {
      this.page.currentPage = 1; // 重置当前页为第一页
      if (params["name"]) {
        params["name_para"] = params["name"];
      }
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
    addClasscolor({ column, row }) {
      //表单样式
      if (
        (column.property === "state" && row.state * 1 == 1) ||
        (column.property === "rolename" && row.rolename == "MEMBER")
      ) {
        return "green";
      } else if (column.property === "state" && row.state * 1 == 2) {
        return "red";
      } else if (column.property === "rolename" && row.rolename == "GUEST") {
        return "yellow";
      }
    },
    // 取消跟随状态
    deleteHandle(id,state) {
      let a=''
      let b =''
      if(state == 1){
        a= '取消跟随',
        b= 2
      }else(
        a = '跟随',
        b = 1
      )
      this.$confirm(`确定进行[${id ? a : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl(`/normal/adminTraderFollowUser!follow.action`),
            method: "get",
        params: this.$http.adornParams({
              uuid: id,
              state:b
            }),
          }).then(({ data }) => {
            if (data.code == 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.getDataList();
                },
              });
            } else {
              this.$message({
                message: data.msg,
                type: "error",
                duration: 1500,
                onClose: () => {},
              });
            }
          });
        })
        .catch(() => {});
    },
  },
};
</script>
