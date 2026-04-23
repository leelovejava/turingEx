<template>
  <div class="mod-stock-release">
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
    <template slot-scope="scope" slot="investment_min">
      <span>{{ scope.row.investment_min }}</span>-<span>{{ scope.row.investment_max }}</span>
    </template>
    <template slot="menuLeft">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('mining:stock-release:operate')"
          @click.stop="addOrUpdateHandle()"
          >新增</el-button
        >
      </template>
      <template slot-scope="scope" slot="methodImg">
        <img :src="scope.row.img" alt="" width="100"  />
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('mining:stock-release:operate')"
          @click.stop="addOrUpdateHandle(scope.row)"
          >编辑</el-button
        >
        <el-button
        type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('mining:stock-release:operate')"
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
  </div>
</template>

<script>
import { tableOption } from "@/crud/mining/stock-release";
import AddOrUpdate from "./stock-release-add-or-update";
export default {
  data() {
    return {
      searchParams:{},
      dataForm: {},
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      searchParams: {}, // 搜索条件
      addOrUpdateVisible: false,
      tableOption: tableOption,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      options:[],
    };
  },
  components: {
    AddOrUpdate,
  },
  created() {
    this.getAction();
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
        url: this.$http.adornUrl("/newSharesConfig/list"),
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
        this.$refs.addOrUpdate.init(row,this.options);
      });
    },
    addClasscolor({ column, row }) {//表单样式
      if (
        (column.property === "ipoStatus" && row.ipoStatus*1 == 2) || (column.property === "status" && row.status*1 == 2)
      ) {
        return "green";
      } else if (
        (column.property === "status" && row.state*1 == 3) 
      ) {
        return "red";
      } else if((column.property === "ipoStatus" && row.ipoStatus*1 == 1) || (column.property === "status" && row.status*1 == 1)) {
        return "yellow";
      }
    },
    // 删除
    deleteHandle(row) {
      // var ids = row.uuid
      //   ? row.uuid
      //   : this.dataListSelections.map((item) => {
      //       return item.roleId;
      //     });
      var ids = row.uuid
      console.log(row)
      this.$confirm(`确定要对[${row.name}]进行删除操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/newSharesConfig/delete"),
            method: "post",
            data: this.$http.adornData({
              id:ids
            }),
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
              message: data.msg,
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
    //币种列表
    getAction() {
      this.$http({
        url: this.$http.adornUrl("/newSharesConfig/getSharesTypeList"),
        method: "get",
      }).then(({ data }) => {
        console.log("newSharesConfig data => " + JSON.stringify(data));
        if (data.code == 0) {
          this.options = data.data.map((item, index) => {
            return Object.assign({}, { value: item.value, label: item.name });
          });
        }
        //console.log(this.options);
      });
    },
  },
};
</script>
