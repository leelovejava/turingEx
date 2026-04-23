<template>
  <div class="mod-transport">
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
          v-if="isAuth('config:ad-config:operate')"
          @click.stop="addOrUpdateHandle(options)"
          >新增横幅</el-button
        >
      </template>
      <template slot="ndhSearch">
        <avue-select
          v-model="options.id"
          placeholder="请选择语言"
          :dic="options"
        ></avue-select>
      </template>
      <template slot-scope="scope" slot="image">
        <img :src="scope.row.image" alt="" width="60" />
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('config:ad-config:operate')"
          @click.stop="addOrUpdateHandle(options, scope.row)"
          >修改</el-button
        >
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('config:ad-config:operate')"
          @click.stop="deleteHandle(scope.row.uuid)"
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
import { tableOption } from "@/crud/config/ad-config";
import AddOrUpdate from "./ad-add-or-update";
export default {
  data() {
    return {
      dataList: [],
      options: [],
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
  created() {
    this.getLanguage();
  },
  methods: {
    // 获取数据列表
    getDataList(page, params, done) {
      let language = "";
      if (this.options.id) {
        language = this.options.id;
      } else {
        language = "";
      }
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/banner/list"),
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
        (column.property === "click" && row.click * 1 == 1) ||
        (column.property === "onShow" && row.onShow * 1 == 1)
      ) {
        return "green";
      } else if (
        (column.property === "click" && row.click * 1 == 0) ||
        (column.property === "onShow" && row.onShow * 1 == 0)
      ) {
        return "red";
      } else {
        return "";
      }
    },
    getLanguage(params, done) {
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
    // 条件查询
    searchChange(params, done) {
      this.getDataList(this.page, params, done);
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
    },
    // 新增 / 修改
    addOrUpdateHandle(arr, row) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(arr, row);
      });
    },
    // 删除
    deleteHandle(id) {
      // var ids = id ? [id] : this.dataListSelections.map(item => {
      //   return item.roleId
      // })
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/banner/delete"),
            method: "post",
            data: this.$http.adornData(
              Object.assign({
                id: id,
              })
            ),
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

<style scoped></style>
