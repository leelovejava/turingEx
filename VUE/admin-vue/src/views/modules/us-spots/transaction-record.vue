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
        <!-- 搜索 -->
        <el-form
          :inline="true"
          :model="dataForm"
          @keyup.enter.native="getDataList(this.page)"
        >
          <el-form-item label="订单号:">
            <el-input
              v-model="dataForm.userName"
              placeholder="订单号"
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item label="用户名:">
            <el-input
              v-model="dataForm.userName"
              placeholder="用户名、UID"
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item label="账户类型:">
            <el-select v-model="dataForm.OrderStatus" placeholder="全部">
              <el-option label="正式账号" value="0"></el-option>
              <el-option label="演示账号" value="1"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="币对:">
            <el-select v-model="dataForm.OrderStatus" placeholder="全部">
              <el-option label="全部" value="0"></el-option>
              <el-option label="交易中" value="1"></el-option>
              <el-option label="已完成" value="2"></el-option>
              <el-option label="已撤单" value="3"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="成交状态:">
            <el-select v-model="dataForm.OrderStatus" placeholder="全部成交">
              <el-option label="全部成交" value="0"></el-option>
              <el-option label="部分成交" value="1"></el-option>
              <el-option label="未成交" value="2"></el-option>
              <el-option label="已撤单" value="3"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              icon="el-icon-search"
              size="small"
              @click="getList()"
              >查询</el-button
            >
          </el-form-item>
        </el-form>
      </template>

      <template slot-scope="scope" slot="RandomFactorOfTradingVolume">
        <span
          class="speacButton"
          @click="RandomFactorOfTradingVolume(scope.row)"
          >设置</span
        >
      </template>

      <template slot-scope="scope" slot="menu">
        <el-button
          type="danger"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('sys:user:update')"
          @click.stop="addOrUpdateHandle(scope.row)"
          >撤销</el-button
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
import { tableOption } from "@/crud/us-spots/transaction-record";
import AddOrUpdate from "./transaction-record-add-or-update";
export default {
  data() {
    return {
      dataForm: {},
      dataList2: [],
      dataList: [],
      options1: [
        {
          value: "1",
          label: "修改",
        },
        {
          value: "2",
          label: "运行引擎",
        },
        {
          value: "3",
          label: "重置引擎",
        },
      ],
      options2: [
        {
          value: "1",
          label: "修改",
        },
        {
          value: "2",
          label: "停止引擎",
        },
        {
          value: "3",
          label: "重置引擎",
        },
      ],
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
    getDataList(page, params, done) {
      // this.getList();
      // return;
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/exchangeApplyOrder/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
              type: "US-stocks",
              offset: "open",
            },
            params
          )
        ),
      }).then(({ data }) => {
        data = data.data;
        this.dataList = data.records;
        this.page.total = data.total;
        this.dataListLoading = false;
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
    addOrUpdateHandle(row) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(row);
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
    changeSelet(row) {
      console.log("row => " + JSON.stringify(row));
      switch (row.select) {
        case "1": //修改
          {
            this.addOrUpdateVisible = true;
            this.$nextTick(() => {
              this.$refs.addOrUpdate.init(row);
            });
          }
          break;
        case "2": //
          {
            if (row.RobotStatus == 0) {
              //启动引擎
              //
              this.$confirm(`确定启动引擎?`, "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
              })
                .then(() => {
                  //
                  row.RobotStatus = 1;
                  row.TransactionEngineStatus = 1;
                  this.$message({
                    message: "操作成功",
                    type: "success",
                    duration: 1500,
                    onClose: () => {},
                  });
                  //
                })
                .catch(() => {});
              //
            } else if (row.RobotStatus == 1) {
              //停止引擎
              //
              this.$confirm(`确定停止引擎?`, "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
              })
                .then(() => {
                  //
                  row.RobotStatus = 0;
                  row.TransactionEngineStatus = 0;
                  this.$message({
                    message: "操作成功",
                    type: "success",
                    duration: 1500,
                    onClose: () => {},
                  });
                  //
                })
                .catch(() => {});
              //
            }
          }
          break;
        case "3": //重置
          {
            //
            this.$confirm(`确定重置引擎?`, "提示", {
              confirmButtonText: "确定",
              cancelButtonText: "取消",
              type: "warning",
            })
              .then(() => {
                //
                this.$message({
                  message: "操作成功",
                  type: "success",
                  duration: 1500,
                  onClose: () => {},
                });
                //
              })
              .catch(() => {});
            //
          }
          break;
      }

      row.select = "";
    },
    addClasscolor({ column, row }) {
      //表单样式
      if (
        column.property === "RobotStatus" ||
        column.property === "TransactionEngineStatus"
      ) {
        if (row.RobotStatus == 1 || row.TransactionEngineStatus == 1) {
          return "green";
        } else {
          return "red";
        }
      }
      return "";
    },
    //查看更多参数
    RandomFactorOfTradingVolume(row) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(row);
      });
    },
    AddData(data) {
      this.dataList[this.dataList.length] = data;
      console.log(JSON.stringify(this.dataList));
      this.getList();
    },
    getList() {
      this.dataList2 = this.dataList.filter((item) => {
        return (
          !item.name ||
          item.name.indexOf(this.dataForm.userName) >= 0 ||
          !this.dataForm.userName
        );
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.mod-mange {
}
.speacButton {
  color: rgb(69, 147, 235);
  cursor: pointer;
}
.speacButton:hover {
  color: rgb(8, 63, 134);
}
</style>
