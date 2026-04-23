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
      @refresh-change="refreshChange"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <!-- 搜索 -->
        <el-form
          :inline="true"
          :model="dataForm"
          @keyup.enter.native="getDataList(this.page)"
        >
          <!-- <el-form-item label="订单号:" label-width="72px">
            <el-input
              v-model="search.OrderID"
              placeholder="订单号"
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item label="用户名:" label-width="72px">
            <el-input
              v-model="search.userName"
              placeholder="用户名"
              clearable
            ></el-input>
          </el-form-item> -->

          <el-form-item label="交易对:" label-width="72px">
            <avue-select
              v-model="search.symbol"
              placeholder="请选择"
              :dic="options"
            ></avue-select>
          </el-form-item>

          <el-form-item label="显示状态:" label-width="72px">
            <avue-select
              v-model="search.showStatus"
              placeholder="请选择"
              :dic="options1"
            ></avue-select>
          </el-form-item>

          <el-form-item label="交易状态:" label-width="72px">
            <avue-select
              v-model="search.tradeStatus"
              placeholder="请选择"
              :dic="options1"
            ></avue-select>
          </el-form-item>

          <!-- <el-form-item label="成交状态:" label-width="72px">
            <el-select v-model="search.OrderStatus" placeholder="请选择">
              <el-option label="全部" value=""></el-option>
              <el-option label="已提交" value="submitted"></el-option>
              <el-option label="委托完成" value="created"></el-option>
              <el-option label="已撤销" value="canceled"></el-option>
            </el-select>
          </el-form-item> -->

          <el-form-item>
            <el-button
              type="primary"
              icon="el-icon-search"
              size="small"
              @click="getDataList()"
              >查询</el-button
            >
          </el-form-item>
        </el-form>
      </template>

      <template slot-scope="scope" slot="showStatus">
        <!-- <span class="speacButton" @click="showStatus(scope.row)">设置</span> -->
        <el-switch
          v-model="scope.row.showStatus1"
          active-color="#13ce66"
          @change="showStatus(scope.row)"
        >
        </el-switch>
      </template>

      <template slot-scope="scope" slot="tradeStatus">
        <el-switch
          v-model="scope.row.tradeStatus1"
          active-color="#13ce66"
          @change="tradeStatus(scope.row)"
        >
        </el-switch>
      </template>

      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('india-spots:operate')"
          @click.stop="addOrUpdateHandle(scope.row)"
          >修改</el-button
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
import { tableOption } from "@/crud/us-spots/transaction-pairs";
import AddOrUpdate from "./transaction-pairs-add-or-update";
export default {
  data() {
    return {
      dataForm: {},
      dataList2: [],
      dataList: [],
      options:[],
      options1: [
        {
          value: "",
          label: "全部",
        },
        {
          value: "1",
          label: "显示",
        },
        {
          value: "0",
          label: "隐藏",
        }
      ],
      options2: [
        {
          value: "",
          label: "全部",
        },
        {
          value: "1",
          label: "显示",
        },
        {
          value: "0",
          label: "隐藏",
        }
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
      search: {
        OrderID: "",
        userName: "",
        rolename: "0",
        symbol: "",
        OrderStatus: "",
        showStatus:"",
        tradeStatus:"",
      },
    };
  },
  components: {
    AddOrUpdate,
  },
  created() {
    this.getSymbol();
  },
  methods: {
    // 获取数据列表
    getDataList(page, params, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/normal/adminItemAction!/list"),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
              type: "INDIA-stocks",
              userName:this.search.userName,
              orderNo:this.search.OrderID,
              symbol:this.search.symbol,
              state:this.search.OrderStatus,
              tradeStatus:this.search.tradeStatus,
              showStatus:this.search.showStatus,
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

      row.showStatus1 = row.showStatus == "1";
      row.tradeStatus1 = row.tradeStatus == "1";

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
    showStatus(data) {
      console.log("data: " + JSON.stringify(data));
      //前端
      this.$http({
        url: this.$http.adornUrl("/normal/adminItemAction!/setShowStatus"),
        method: "get",
        params: this.$http.adornParams(
          Object.assign({
            symbol: data.symbol,
            showStatus: data.showStatus == "1" ? "0" : "1",
          })
        ),
      }).then(({ data }) => {
        this.getDataList();
        this.$message({
          message: "操作成功",
          type: "success",
          duration: 1500,
          onClose: () => {
            console.log(data);
          },
        });
      });
      //
    },
    tradeStatus(data) {
      //
      this.$http({
        url: this.$http.adornUrl("/normal/adminItemAction!/setTradeStatus"),
        method: "get",
        params: this.$http.adornParams(
          Object.assign({
            symbol: data.symbol,
            tradeStatus: data.tradeStatus == "1" ? "0" : "1",
          })
        ),
      }).then(({ data }) => {
        this.getDataList();
        this.$message({
          message: "操作成功",
          type: "success",
          duration: 1500,
          onClose: () => {
            console.log(data);
          },
        });
      });
    },
     // 获取项目种类列表
    getSymbol(page, params, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/api/item!list.action?type=INDIA-stocks"),
        method: "get",
        params: this.$http.adornParams(Object.assign({}, params), false),
      }).then(({ data }) => {
        if (data.code == 0) {
          let arr = data.data;
          this.options = arr.map((item, index) => {
            return Object.assign({
              label: item.name,
              value: item.symbol,
            });
          });
        }
        if (done) {
          done();
        }
      });
    },
    // 刷新回调用
    refreshChange () {
      this.page = this.$refs.crud.$refs.tablePage.defaultPage
      this.getDataList(this.page)
      this.dataListSelections = []
      this.$refs.crud.selectClear()
    },
  }
}
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
