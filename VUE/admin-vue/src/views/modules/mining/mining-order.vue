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
      @refresh-change="refreshChange"
    >
      <template slot="menuLeft">
        <template>
          <div class="allBox"  v-if="isAuth('mining:mining:order:operate')">
            <div class="leDiv speacRead">操作</div>
            <div class="leDiv">
              <el-date-picker
                v-model="datetimePick"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="选择日期时间"
              >
              </el-date-picker>
            </div>
            <div class="leDiv">
              <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('sys:user:root')"
          @click.stop="addProfit()"
          >利息重计</el-button
        >
            </div>
          </div>
        </template>
        <template>
          <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('mining:mining:order:operate')"
          @click.stop="addOrUpdateHandle()"
          >新增</el-button
        >
        </template>
        <!-- Tag页 -->
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="全部" name="3"></el-tab-pane>
          <el-tab-pane label="赎回" name="0"></el-tab-pane>
          <el-tab-pane label="托管中" name="1"></el-tab-pane>
        </el-tabs>
      </template>

      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          size="small"
          plain
          @click.stop="showIncomeHandle(scope.row)"
        >收益</el-button>
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          @click.stop="withdrawHandle(scope.row)"
          v-if="isAuth('mining:mining:order:operate')"
          >赎回</el-button
        
        >
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>

    <el-dialog title="收益记录" :visible.sync="incomeDialogVisible" width="900px">
      <el-table :data="incomeList" border v-loading="incomeLoading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="number" label="下单金额"></el-table-column>
        <el-table-column prop="income" label="收益"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template slot-scope="scope">
            <span>{{ scope.row.status === 1 ? "已使用" : "未使用" }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间"></el-table-column>
        <el-table-column prop="endTime" label="结束时间"></el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="editIncomeHandle(scope.row)">修改收益</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; text-align: right;">
        <el-pagination
          @current-change="incomePageChange"
          :current-page="incomePage.currentPage"
          :page-size="incomePage.pageSize"
          layout="total, prev, pager, next"
          :total="incomePage.total"
        >
        </el-pagination>
      </div>
    </el-dialog>
    <el-dialog title="修改收益" :visible.sync="editIncomeVisible" width="400px">
      <el-form>
        <el-form-item label="收益">
          <el-input v-model="editIncomeValue" type="number"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="editIncomeVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEditIncome">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { tableOption } from "@/crud/mining/mining-order";
import AddOrUpdate from "./mining-order-add-or-update";
export default {
  data() {
    return {
      datetimePick:'',
      searchParams:{},
      dataForm: {},
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      tableOption: tableOption,
      activeName: "3",
      incomeDialogVisible: false,
      incomeLoading: false,
      incomeList: [],
      currentIncomeOrderId: "",
      editIncomeVisible: false,
      editIncomeValue: "",
      editIncomeRow: null,
      incomePage: {
        total: 0,
        currentPage: 1,
        pageSize: 10,
      },
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
    this.$http({
          url: this.$http.adornUrl(`/normal/adminMinerOrderAction!toAddOrder.action`),
          method: "get",
          params: this.$http.adornParams({
          }),
    }).then(({ data }) => {
      let algorithms = []
      let list = data.data.miner_list;
      for(let i = 0 ; i < list.length ; i++){
        let data = list[i];
        algorithms[i] = {label:data.name,value:data.name}
      }
      this.tableOption.column[2].dicData = algorithms

    });
  },
  methods: {
    addProfit(){
      //--
      this.$confirm(
        `确认重新计算补上矿机收益`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        //
        this.$http({
          url: this.$http.adornUrl('/normal/adminMinerOrderAction!addProfit.action'),
          method: 'get',
          params: this.$http.adornParams(Object.assign({
            system_time:this.datetimePick
          })),
        }).then(({data}) => {
          if(data.code=="0"){
            //
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1500,
              onClose: () => {
                // this.getDataList(this.page)
                this.getDataList()
              }
            })
            //
          }else{
            this.$message({
              message: data.msg,
              type: 'error',
              duration: 1500,
              onClose: () => {
              }
            })
          }

        })
        //
      }).catch((e) => {
        console.log('e: ', e)
      })
      //--
    },
    // 获取数据列表
    getDataList(page, done) {
      let obj = {
        current: 1,
        endTime: "",
        rolename: "",
        size: 10,
        startTime: "",
        userCode: "",
        userId: "",
      };

      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/normal/adminMinerOrderAction!list.action"),
        method: "get",
        params: this.$http.adornParams({
          current: this.page.currentPage,
          size: this.page.pageSize,
          ...this.searchParams,
          system_time:this.datetimePick,
          status_para:this.activeName
        }),
      }).then(({data}) => {
        console.log("data = > " + JSON.stringify(data));
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
        (column.property === "rolename" && row.rolename == "MEMBER") ||
        (column.property === "state" && row.state == "1") ||
        (column.property === "profit" && row.profit >= 0)
      ) {
        return "green";
      } else if (
        (column.property === "rolename" && row.rolename == "GUEST") ||
        (column.property === "state" && row.state == "0")
      ) {
        return "yellow";
      } else if (
        (column.property === "rolename" && row.rolename == "TEST") ||
        (column.property === "state" && row.state == "2") ||
        (column.property === "direction" && row.direction == "sell")||
        (column.property === "profit" && row.profit < 0)
      ) {
        return "red";
      }
    },
    // 条件查询
    searchChange(params, done) {
      this.page.currentPage = 1; // 重置当前页为第一页
      if(params["rolename"]){
        params["rolename_para"] = params["rolename"]
      }
      this.searchParams = params;
      this.getDataList(this.page, done);
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
    },
    handleClick() {
      this.getDataList();
    },
    showIncomeHandle(row) {
      this.currentIncomeOrderId = row.uuid;
      this.incomePage.currentPage = 1;
      this.incomeDialogVisible = true;
      this.getIncomeList();
    },
    incomePageChange(page) {
      this.incomePage.currentPage = page;
      this.getIncomeList();
    },
    getIncomeList() {
      if (!this.currentIncomeOrderId) {
        return;
      }
      this.incomeLoading = true;
      this.$http({
        url: this.$http.adornUrl("/normal/adminMinerOrderAction!incomeList.action"),
        method: "get",
        params: this.$http.adornParams({
          quantOrderId: this.currentIncomeOrderId,
          current: this.incomePage.currentPage,
          size: this.incomePage.pageSize,
        }),
      }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          this.incomeList = data.data.records || [];
          this.incomePage.total = data.data.total || 0;
        }
        this.incomeLoading = false;
      }).catch(() => {
        this.incomeLoading = false;
      });
    },
    withdrawHandle(row){
      //
      this.$confirm(`是否确认赎回?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/normal/adminMinerOrderAction!close.action"),
            method: "get",
            params: this.$http.adornParams(
              Object.assign({
                order_no: row.order_no,
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
    // 新增 / 修改
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
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
    editIncomeHandle(row) {
      this.editIncomeRow = row;
      this.editIncomeValue = row.income;
      this.editIncomeVisible = true;
    },
    submitEditIncome() {
      this.$http({
        url: this.$http.adornUrl("/normal/adminMinerOrderAction!updateIncome.action"),
        method: "get",
        params: this.$http.adornParams({ id: this.editIncomeRow.id, income: this.editIncomeValue }),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.editIncomeVisible = false;
          this.$message({ message: "操作成功", type: "success", duration: 1500 });
          this.getIncomeList();
        } else {
          this.$message({ message: data.msg, type: "error", duration: 1500 });
        }
      });
    },
    // 刷新回调用
    refreshChange () {
      console.log("refreshChange")
      this.page = this.$refs.crud.$refs.tablePage.defaultPage
      this.getDataList(this.page)
      this.dataListSelections = []
      this.$refs.crud.selectClear()
    },
  },
};
</script>

<style scoped>
.mod-role {
}
.allBox {
  overflow: hidden;
  height: 40px;
  line-height: 40px;
  margin: 30px 0;
}
.leDiv{
  float: left;
  margin-left: 20px;
  line-height: 40px;
}
.speacRead{
  font-weight: 500;
}
</style>
