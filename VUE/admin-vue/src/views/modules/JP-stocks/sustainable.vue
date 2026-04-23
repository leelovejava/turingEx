<template>
  <div class="mod-us-sustainable">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      :cell-class-name="addCellClass"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @on-load="getDataList"
    >
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('jp-spots:operate')"
          @click.stop="addOrUpdateHandle(scope.row)"
          >编辑</el-button
        >
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('jp-spots:operate')"
          @click.stop="leverHandle(scope.row)"
          >交易杠杆</el-button
        >
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
    <lever
      v-if="leverVisible"
      ref="lever"
      @refreshDataList="getDataList"
    ></lever>
  </div>
</template>

<script>
import { tableOption } from "@/crud/etf-spots/etf-sustainable";
import AddOrUpdate from "./sustainable-add-or-update";
import lever from "./lever";
export default {
  data() {
    return {
      dataForm: {},
      dataList: [],
      options: [],
      optionsTwo: [],
      dataListLoading: false,
      leverVisible: false,
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
    lever,
  },
  created() {
    this.getLanguage();
    this.getCmsModel();
  },
  methods: {
    // 获取数据列表
    getDataList(page, done) {
      this.dataListLoading = true;

      this.$http({
        url: this.$http.adornUrl("/normal/adminItemAction!/list"),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              type: "JP-stocks", //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
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
          });
        }
        if (done) {
          done();
        }
      });
    },
    getLanguage(params, done) {
      // 获取语言
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/news/getLanguage"),
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
    getCmsModel(params, done) {
      // 获取模块
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/cms/getCmsModel"),
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
            this.optionsTwo.push(obj);
          }
        }
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
    addOrUpdateHandle(row) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(row);
      });
    },
    leverHandle(row) {
      this.leverVisible = true;
      this.$nextTick(() => {
        this.$refs.lever.init(row);
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
    addCellClass({ row, column }) {
      // console.log("row = " + JSON.stringify(row))
      // console.log("column = " + JSON.stringify(column))
      if (row.outOrIn == "out") {
        row.outOrIn = "汇出";
      } else if (row.outOrIn == "in") {
        row.outOrIn = "汇入";
      }
    },
  },
};
</script>
