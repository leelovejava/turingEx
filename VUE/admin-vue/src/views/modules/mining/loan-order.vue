<template>
  <div class="mod-role">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @on-load="getDataList"
      :cell-class-name="addClasscolor"
      @refresh-change="refreshChange"
    >
      <template slot="menuLeft">
          <!-- Tag页 -->
          <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="全部" name=0></el-tab-pane>
          <el-tab-pane label="未审" name=1></el-tab-pane>
          <el-tab-pane label="通过" name=2></el-tab-pane>
          <el-tab-pane label="驳回" name=3></el-tab-pane>
        </el-tabs>
        <!-- <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          @click.stop="addOrUpdateHandle()"
          >新增</el-button
        > -->
      </template>

      <template slot-scope="scope" slot="state">
        <span>{{ scope.row.state[1] }}</span>
      </template>

      <template slot-scope="scope" slot="repayment">
        <span>{{ scope.row.repayment[1] }}</span>
      </template>

      <template slot-scope="scope" slot="lendingInstitution">
        <span>{{ scope.row.lendingInstitution[1] }}</span>
      </template>

      <template slot-scope="scope" slot="menu">

        <el-select v-if="scope.row.state[0]==1 && isAuth('mining:loan-order:operate')"
          v-model="scope.row.select"
          class="celectSpeac"
          clearable
          placeholder="操作"
          @change="
            changeSelet(
              scope.row.userId,
              scope.row.select,
              scope.row.userCode,
              scope.row
            )
          "
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>

        <el-select v-if="scope.row.state[0]==2 && isAuth('mining:loan-order:operate')"
          v-model="scope.row.select"
          class="celectSpeac"
          clearable
          placeholder="操作"
          @change="
            changeSelet(
              scope.row.userId,
              scope.row.select,
              scope.row.userCode,
              scope.row
            )
          "
        >
          <el-option
            v-for="item in options2"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>

        <!-- <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          @click.stop="addOrUpdateHandle(scope.row)"
          >编辑</el-button
        > -->
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>

    <!-- 确认弹窗-start -->
    <el-dialog
      title="驳回原因"
      :visible.sync="dialogFormVisible"
      :append-to-body="true"
    >
      <el-form
        :model="dataForm2"
        ref="dataForm2"
        @keyup.enter.native="dataFormSubmit()"
        label-width="80px"
      >
        <el-form-item
          label="驳回原因"
          :label-width="formLabelWidth"
          prop="reason"
        >
          <el-input
            v-model="dataForm2.reason"
            placeholder="请输入驳回原因"
            autocomplete="off"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="dataFormSubmit()">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 确认弹窗-end -->

  </div>
</template>

<script>
import { tableOption } from "@/crud/mining/loan-order";
import AddOrUpdate from "./loan-add-or-update";
export default {
  data() {
    return {
      searchParams:{},
      dataForm: {},
      dataList: [],
      datetimePick:'',
      activeName: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      tableOption: tableOption,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      options: [
        {
          value: "1",
          label: "修改",
        },
        {
          value: "2",
          label: "通过",
        },
        {
          value: "3",
          label: "驳回",
        },
      ],
      options2: [
        {
          value: "1",
          label: "修改",
        },
        {
          value: "4",
          label: "还款",
        },
      ],
      dialogFormVisible: false,
      dataForm2: {},
      formLabelWidth: "120px",
      row:{},
    };
  },
  components: {
    AddOrUpdate,
  },
  methods: {
    addClasscolor({ column, row }) {
      //表单样式
      if (
        (column.property === "direction" && row.direction == "buy") ||
        (column.property === "profitLoss" && row.profitLoss * 1 > 0) ||
        (column.property === "rolename" && row.rolename == "MEMBER") ||
        (column.property === "volume" && row.volume * 1 >= 0) ||
        (column.property === "deposit" && row.deposit * 1 >= 0) ||
        (column.property === "state" && row.state == "created")
      ) {
        return "green";
      } else if (
        (column.property === "direction" && row.direction == "sell") ||
        (column.property === "profitLoss" && row.profitLoss * 1 < 0) ||
        (column.property === "state" && row.state == "canceled")
      ) {
        return "red";
      } else if (
        (column.property === "rolename" && row.rolename == "GUEST") || 
        (column.property === "state" && row.state == "submitted")
      ) {
        return "yellow";
      }

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
        url: this.$http.adornUrl("/normal/loanadmin!list.action"),
        method: "get",
        params: this.$http.adornParams({
          current: this.page.currentPage,
          size: this.page.pageSize,
          ...this.searchParams,
          status:this.activeName
        }),
      }).then(({ data }) => {
        console.log("data => " + JSON.stringify(data));
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
    handleClick(tab, event) {
      console.log(tab, event);
      this.getDataList();
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
    changeSelet(uid, val, userCode, row) {
      this.row = row;
      console.log("row = " + JSON.stringify(row));
      if (val) {
  
        // let m = this.options[val - 1].label; //弹窗标题
        console.log("changeSelet = " + val);
        switch(val)
        {
        case "1": //修改
          {
            this.addOrUpdateHandle(row);
          }    
          break;
        case "2": //通过
          {
            console.log("AAA-changeSelet = " + val);
            //
            this.$confirm('', '是否确认通过?', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              //https://hajhiug.com/384732342/normal/loanadmin!change.action
              //reason=&orderId=202cc6a1d6173c1ebd4abdfb00b5462e&statusStr=2
              console.log("/normal/loanadmin!change.action");
              this.$http({
                url: this.$http.adornUrl("/normal/loanadmin!change.action"),
                method: "get",
                params: this.$http.adornParams(Object.assign({
                  reason: "", 
                  orderId: row.uuid,
                  statusStr: 2
                })),
              }).then(({ data }) => {
                console.log("data => " + JSON.stringify(data));
                this.getDataList();
                if (data.code == 0) {
                  this.dataForm = data.data;
                }
              });
              
            }).catch((e) => {
              console.log("/normal/loanadmin!change.action fail " + JSON.stringify(e));
            });
            //
          }    
          break;
        case "3": //驳回
          {
            this.dialogFormVisible = true;
          }    
          break;
        case "4": //还款
          {
            //
            this.$confirm('', '是否确认还款?', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              //https://hajhiug.com/384732342/normal/loanadmin!change.action
              //reason=&orderId=202cc6a1d6173c1ebd4abdfb00b5462e&statusStr=2
              console.log("/normal/loanadmin!change.action");
              this.$http({
                url: this.$http.adornUrl("/normal/loanadmin!change.action"),
                method: "get",
                params: this.$http.adornParams(Object.assign({
                  reason: "", 
                  orderId: row.uuid,
                  statusStr: 5
                })),
              }).then(({ data }) => {
                console.log("data => " + JSON.stringify(data));
                this.getDataList();
                if (data.code == 0) {
                  this.dataForm = data.data;
                }
              });
              
            }).catch((e) => {
              console.log("/normal/loanadmin!change.action fail " + JSON.stringify(e));
            });
            //
          }    
          break;
        }
        row.select = "";
      }
    },
    dataFormSubmit(){
      //驳回原因
      //
      this.$http({
          url: this.$http.adornUrl("/normal/loanadmin!change.action"),
          method: "get",
          params: this.$http.adornParams(Object.assign({
            reason: this.dataForm2.reason, 
            orderId: this.row.uuid,
            statusStr: 3
          })),
        }).then(({ data }) => {
          console.log("data => " + JSON.stringify(data));
          this.getDataList();
          this.dialogFormVisible = false;
          // if (data.code == 0) {
          //   this.dataForm = data.data;
          // }
        });
      //
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
::v-deep .celectSpeac .el-input__inner {
  background: #1c4efa !important;
}
::v-deep .celectSpeac .el-input__inner::placeholder {
  color: #fff;
}
</style>
