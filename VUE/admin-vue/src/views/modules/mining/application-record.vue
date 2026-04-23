<template>
  <div class="mod-application-record">
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
          v-if="isAuth('mining:application-record:operate')"
          :disabled="dataListSelections.length <= 0"
          @click.stop="batchSubscription()"
          >批量认缴</el-button
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
          v-if="isAuth('mining:application-record:operate')&&scope.row.status!==2"
          @click.stop="batchSubscription(scope.row.uuid,scope.row.userName)"
          >同意认缴</el-button
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
import { tableOption } from "@/crud/mining/application-record";
import AddOrUpdate from "./stock-subscription-add-or-update";
export default {
  data() {
    return {
      searchParams:{},
      dataForm: {},
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],//批量选择数组
      searchParams: {}, // 搜索条件
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
      const params = {
        current: page == null ? this.page.currentPage : page.currentPage,
        size: page == null ? this.page.pageSize : page.pageSize,
        ...this.searchParams,
      };
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/userPromise/list"),
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
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
    addClasscolor({ column, row }) {//表单样式
      if (
        (column.property === "status" && row.status*1 == 1)
      ) {
        return "yellow";
      } else if (
        (column.property === "status" && row.status*1 == 2) 
      ) {
        return "green";
      } else {
        return "";
      }
    },
    selectionChange(val) {
      this.dataListSelections = val;
    },
    //改签
    batchRescheduling(orderNo,name){
      var orderNo = orderNo
        ? [orderNo]
        : this.dataListSelections.map((item) => {
            return item.orderNo;
          });
      var userName = name
        ? [name]
        : this.dataListSelections.map((item) => {
            return item.userName;
          });
      this.$confirm(
        `确定对[用户${userName.join(",")}]进行[${
          id ? "改签" : "批量改签"
        }]操作?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/applyNewSharesOrder/batchRescheduling"),
            method: "post",
            data: this.$http.adornData({
              orderNo:orderNo
            }),
          }).then(({ data }) => {
            if(data.code == 0){
              this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.page.currentPage = 1;
                this.getDataList(this.page);
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
        //认缴
        batchSubscription(id,name){
      var ids = id
        ? [id]
        : this.dataListSelections.map((item) => {
            return item.uuid;
          });
      var userName = name
        ? [name]
        : this.dataListSelections.map((item) => {
            return item.userName;
          });
      this.$confirm(
        `确定对[用户${userName.join(",")}]进行[${
          id ? "认缴" : "批量认缴"
        }]操作?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/userPromise/batchSubscription"),
            method: "post",
            data: this.$http.adornData({
              ids:ids
            }),
          }).then(({ data }) => {
            if(data.code == 0){
              this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.page.currentPage = 1;
                this.getDataList(this.page);
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
  },
};
</script>
