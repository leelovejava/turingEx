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
      <template slot="menuLeft">
        <el-button type="primary"
                   icon="el-icon-plus"
                   size="small"
                   v-if="isAuth('mining:balance:income:operate')"
                   @click.stop="addOrUpdateHandle()">新增</el-button>

      </template>   
      <template slot-scope="scope" slot="img">
        <img :src="scope.row.titleImgUrl" alt="" width="100"  />
      </template> 
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('mining:balance:income:operate')"
          @click.stop="addOrUpdateHandle(scope.row)"
          >编辑</el-button
        >
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('sys:user:root')"
          @click.stop="deleteHandle(scope.row)"
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
    <dellet
      v-if="delletVisible"
      ref="delletUpdate"
      @refreshDataList="getDataList"
    ></dellet>
  </div>
</template>

<script>
import { tableOption } from "@/crud/mining/balance-income";
import AddOrUpdate from "./balance-income-add-or-update";
import dellet from "./balance-income-dellet";
export default {
  data() {
    return {
      dataList: [],
      dataListLoading: false,
      delletVisible:false,
      userName:'',
      dataListSelections: [],
      options: [],
      operator:'',
      log:'',
      roleName:'',
      addOrUpdateVisible: false,
      searchParams: {}, // 搜索条件
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
    dellet
  },
  methods: {
    // 获取数据列表
    getDataList(page, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/pledgeConfig/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {

              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            this.searchParams
          )
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.dataList = data.data.records;
          this.page.total = data.data.total;
          this.dataListLoading = false;
        } else {
          this.$message({
            message: data.msg,
            type: "error",
            duration: 1500,
          });
        }

        if (done) {
          done();
        }
      });
    },
    addClasscolor({ column, row }) {//表单样式
      if (
        (column.property === "roleName" && row.roleName == 'MEMBER')
      ) {
        return "green";
      } else if (
        (column.property === "roleName" && row.roleName == 'GUEST')
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
    addOrUpdateHandle(row) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(row);
      });
    },
    deleteHandle(row) {
      this.delletVisible = true;
      this.$nextTick(() => {
        this.$refs.delletUpdate.init(row);
      });
    },
  },
};
</script>
