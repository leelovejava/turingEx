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
    <template slot-scope="scope" slot="nth">
      <span 
        v-if="isAuth('c2c:c2c-pay-appeal:search')"
        class="seachButton" @click="searchBtn(scope.row)">点击查看</span>
    </template>
    <template slot-scope="scope" slot="user_name">
      <span class="seachButton" @click="searchName(scope.row)">{{scope.row.user_name}}</span>
    </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('c2c:c2c-pay-appeal:process') && scope.row.state*1 == 0"
          @click.stop="deleteHandle(scope.row.order_no)"
          >处理</el-button
        >

      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 --> 
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
    <appealmoremsg
      v-if="appealmoremsgVisible"
      ref="moremsgUpdate"
      @refreshDataList="getDataList"
    ></appealmoremsg>
    <!-- 参数管理 -->
    <parameters v-if="parametersFlag"
        ref="parameters">
    </parameters>
  </div>
</template>

<script>
import { tableOption } from "@/crud/c2c/c2c-pay-appeal";
import AddOrUpdate from "./c2c-pay-appeal-add-or-update";
import parameters from "./grid-parameters";
import appealmoremsg from "./c2c-appeal-more-msg";
export default {
  data() {
    return {
      dataForm: {},
      dataList: [],
      dataListLoading: false,
      appealmoremsgVisible:false,
      parametersFlag:false,
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
    appealmoremsg,
    parameters
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
        url: this.$http.adornUrl("/c2cAppeal/list"),
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
    addClasscolor({ column, row }) {//表单样式
      if (
        (column.property === "role_name" && row.role_name == "MEMBER") ||
        (column.property === "state" && row.state * 1 == 1)
      ) {
        return "green";
      } else if (
        (column.property === "role_name" && row.role_name == "GUEST") ||
        (column.property === "state" && row.state * 1 == 0)
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
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
    searchBtn(row){
      this.appealmoremsgVisible = true;
      this.$nextTick(() => {
        this.$refs.moremsgUpdate.init(row);
      });
    },
    searchName(row){
      //this.userNamevisible = true
      this.parametersFlag = true
      this.$nextTick(() => {
        this.$refs.parameters.init(row)
      })
    },
    // 删除
    deleteHandle(id) {
      this.$confirm(`是否确认[审核通过]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/c2cAppeal/handled"),
            method: "post",
            data: this.$http.adornData(          
            {
              'orderNo':id
            },
          ),
          }).then(({ data }) => {
            if(data.code == 0){
              this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.getDataList();
              },
            });
            }else{
              this.$message({
              message:data.msg,
              type: "error",
              duration: 1500,
              onClose: () => {
              },
            });
            }
          });
        })
        .catch(() => {});
    },
  },
};
</script>
