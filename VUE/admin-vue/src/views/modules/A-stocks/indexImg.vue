<template>
  <div class="mod-cryptos-indexImg">
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
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('a-spots:operate')"
          @click.stop="addOrUpdateHandle(options)"
          >新增</el-button
        >
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('a-spots:operate')"
          @click.stop="addOrUpdateHandle(options,'',userOption)"
          >批量新增</el-button
        >
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('a-spots:operate')"
          @click.stop="addOrUpdateHandle(options,scope.row)"
          >编辑</el-button
        >
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('a-spots:delete')"
          @click.stop="deleteHandle(scope.row.uuid, scope.row.userName)"
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
  </div>
</template>
<script>
import AddOrUpdate from "./indexImg-add-or-update";
import { tableOption } from "@/crud/admin/indexImg";
export default {
  data() {
    return {
      dataList: [],
      options: [],//获取新增修改类型
      userOption:[],//获取用户
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      tableOption: tableOption,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      searchParams: {}, // 搜索条件
    };
  },
  components: {
    AddOrUpdate,
  },
  created() {
    this.getConfig();
    this.getuser()
  },
  methods: {
    // 获取数据列表
    getDataList(page, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl(
          "/normal/adminProfitAndLossConfigAction!/list.action"
        ),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              symbolType: "A-stocks", //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            this.searchParams
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
    getConfig(params, done) {
      this.$http({
        url: this.$http.adornUrl(
          "/normal/adminProfitAndLossConfigAction!/config.action"
        ),
        method: "get",
        params: this.$http.adornParams(Object.assign({}, params), false),
      }).then(({ data }) => {
        if (data.code == 0) {
          let keys = Object.keys(data.data);
          let value = Object.values(data.data);
          for (let i = 0; i < keys.length; i++) {
            let obj = {};
            obj.label = value[i];
            obj.value = keys[i];
            this.options.push(obj);
          }
        }
        if (done) {
          done();
        }
      });
    },
        // 获取数据列表
    getuser(page, done) {
      this.$http({
        url: this.$http.adornUrl("/user/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              current: 1,
              size: 1000000
            },  
          )
        ),
      }).then(({ data }) => {
        this.userOption = data.data.records
        if (done) {
          done();
        }
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
    addOrUpdateHandle(arr,row,user) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(arr, row,user);
      });
    },

    // 删除
    deleteHandle(id, name) {
      this.$confirm(
        `确定对[用户${name}]进行[${id ? "删除" : "批量删除"}]操作?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          this.$http({
            url: this.$http.adornUrl(
              "/normal/adminProfitAndLossConfigAction!/toDelete.action"
            ),
            method: "get",
            params: this.$http.adornParams(
              Object.assign({
                uuid: id,
                symbolType: "A-stocks", //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
              })
            ),
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
