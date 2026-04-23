<template>
  <div class="mod-transport">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      :cell-class-name="addCellClass"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @refresh-change="refreshChange"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('config:user:operate')"
          @click.stop="addOrUpdateHandle(options, optionsTwo)"
          >新增公告</el-button
        >
      </template>
      <template slot="ndhSearch">
        <avue-select
          v-model="options.id"
          placeholder="请选择语言"
          :dic="options"
        ></avue-select>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('config:user:operate')"
          @click.stop="addOrUpdateHandle(options, optionsTwo, scope.row)"
          >编辑</el-button
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
import { tableOption } from "@/crud/config/user";
import AddOrUpdate from "./user-add-or-update";
export default {
  data() {
    return {
      dataForm: {},
      dataList: [],
      options: [],
      optionsTwo: [],
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
  },
  created() {
    this.getLanguage();
    this.getCmsModel();
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
      let language = "";
      if (this.options.id) {
        language = this.options.id;
      } else {
        language = "";
      }
      this.$http({
        url: this.$http.adornUrl("/cms/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              language: language,
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            params
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
    addOrUpdateHandle(arr, arr2, row) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(arr, arr2, row);
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
