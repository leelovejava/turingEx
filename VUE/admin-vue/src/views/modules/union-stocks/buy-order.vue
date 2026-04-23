<template>
  <div class="mod-transport">
    <el-tabs v-model="stockName" @tab-click="stockHandleClick">
      <el-tab-pane v-if="isAuth('union-stocks:us')" name="US-stocks">
        <span slot="label">
          美股
          <el-badge :value="countNum('美股_0')" v-if="countNum('美股_0') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:hk')" name="HK-stocks">
        <span slot="label">
          港股
          <el-badge :value="countNum('港股_0')" v-if="countNum('港股_0') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:tw')" name="TW-stocks">
        <span slot="label">
          台股
          <el-badge :value="countNum('台股_0')" v-if="countNum('台股_0') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:a')" name="A-stocks">
        <span slot="label">
          A股
          <el-badge :value="countNum('A股_0')" v-if="countNum('A股_0') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:jp')" name="JP-stocks">
        <span slot="label">
          日股
          <el-badge :value="countNum('日股_0')" v-if="countNum('日股_0') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:india')" name="INDIA-stocks">
        <span slot="label">
          印度股
          <el-badge :value="countNum('印度股_0')" v-if="countNum('印度股_0') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:uk')" name="UK-stocks">
        <span slot="label">
          英股
          <el-badge :value="countNum('英股_0')" v-if="countNum('英股_0') > 0"></el-badge>
        </span>
      </el-tab-pane>
    </el-tabs>
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
          <el-form-item label="订单号:" label-width="72px">
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
          </el-form-item>

          <el-form-item label="币对:" label-width="72px">
            <avue-select
              v-model="search.symbol"
              placeholder="请选择"
              :dic="options[this.stockName]"
            ></avue-select>
          </el-form-item>

          <el-form-item label="成交状态:" label-width="72px">
            <el-select v-model="search.OrderStatus" placeholder="请选择">
              <el-option label="全部" value=""></el-option>
              <el-option label="已提交" value="submitted"></el-option>
              <el-option label="委托完成" value="created"></el-option>
              <el-option label="已撤销" value="canceled"></el-option>
            </el-select>
          </el-form-item>

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

      <template slot-scope="scope" slot="TransactionEngineStatus">
        <span 
          v-if="isAuth('union-spots:operate')"
          class="speacButton" @click="TransactionEngineStatus(scope.row)"
          >查看</span
        >
      </template>

      <template slot-scope="scope" slot="menu">
        <el-button
          type="danger"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('union-spots:operate')&&scope.row.state=='submitted'"
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
    <!-- 交易参数管理 -->
    <parameters v-if="parametersFlag" ref="parameters"> </parameters>
  </div>
</template>

<script>
import { tableOption } from "@/crud/us-spots/buy-order";
import AddOrUpdate from "./buy-order-add-or-update";
import parameters from "./buy-parameters";
import { getStockName } from "./config";
import { isAuth } from '@/utils'
export default {
  data() {
    return {
      parametersFlag: false,
      dataForm: {},
      dataList2: [],
      dataList: [],
      options: {},
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
      search: {
        OrderID: "",
        userName: "",
        rolename: "0",
        symbol: "",
        OrderStatus: "",
      },
      stocksMap:{}
    };
  },
  components: {
    AddOrUpdate,
    parameters,
  },
  created() {
    this.stockName = getStockName();
    if(this.$store.state.common.stocksValue && this.$store.state.common.stocksValue !=""){
      this.stockName = this.$store.state.common.stocksValue;
      this.$store.commit("common/updateStocksValue", "")
    }
    let arr = ["union-stocks:us","union-stocks:hk","union-stocks:tw","union-stocks:a","union-stocks:jp","union-stocks:india","union-stocks:uk"];
    let name = ["US-stocks","HK-stocks","TW-stocks","A-stocks","JP-stocks","INDIA-stocks","UK-stocks"];

    for(let i=0 ; i < arr.length ; i++){
      if(isAuth(arr[i])){
        this.getAction(name[i]);
      }
    }



    this.stocksMap = {};
  },
  methods: {
    // 获取数据列表
    getDataList(page, params, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/exchangeApplyOrder/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
              type: this.stockName,
              offset: "open",
              userName:this.search.userName,
              orderNo:this.search.OrderID,
              symbol:this.search.symbol,
              state:this.search.OrderStatus,
            },
            params
          )
        ),
      }).then(({ data }) => {
        data = data.data;
        this.dataList = data.records;
        this.page.total = data.total;
        this.dataListLoading = false;

        // this.getList();
        // if (done) {
        //   done();
        // }
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
      //
      this.$confirm(`确定进行撤销操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/exchangeApplyOrder/close"),
            method: "post",
            data: this.$http.adornData(
              Object.assign({
                orderNo: row.orderNo,
              }),
              false
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

      //
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
      if (column.property === "OrderInterval") {
        return "green";
      } else if (
        column.property === "orderPriceType" ||
        column.property === "TransactionEngineStatus"
      ) {
        return "blue";
      }
      return "";
    },
    //查看更多参数
    TransactionEngineStatus(row) {
      this.parametersFlag = true;
      this.$nextTick(() => {
        this.$refs.parameters.init(row);
      });
    },
    // getList() {
    //   let call = (item) => {
    //     let flag0 =
    //       !this.search.OrderID ||
    //       item.orderNo.indexOf(this.search.OrderID) >= 0;

    //     let flag1 =
    //       !this.search.userName ||
    //       item.userName.indexOf(this.search.userName) >= 0;

    //     let flag2 =
    //       !this.search.OrderStatus ||
    //       item.state.indexOf(this.search.OrderStatus) >= 0;

    //     let flag3 =
    //       !this.options.value || item.symbol.indexOf(this.options.value) >= 0;

    //     return flag0 && flag1 && flag2 && flag3;
    //   };

    //   this.dataList2 = this.dataList.filter((item) => {
    //     return call(item);
    //   });

    //   //this.dataList2 = this.dataList;
    // },
    // 获取项目种类列表
    getAction(name) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/api/item!list.action?type="+name),
        method: "get",
      }).then(({ data }) => {
        if (data.code == 0) {
          let arr = data.data;
          this.options[name] = arr.map((item, index) => {
            return Object.assign({
              label: item.name,
              value: item.symbol,
            });
          });
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
    stockHandleClick(tab, event){

      // if(this.stocksMap[tab.name]!=1){
      //   this.stocksMap[tab.name]=1
      //   this.getAction();
      // }

      
      this.page.currentPage = 1; // 重置当前页为第一页
      this.getDataList();

    },
    countNum(type){
      if(!this.menuMap){
        // console.log(name + "-->");
        return 0;
      }
      // let type = this.menuMap[name];
      let num = this.main.tips[type]
      if(isNaN(num)){
        num = 0
      }
      // console.log(name + "-->" + type + "-->" + num);
      return num;
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
