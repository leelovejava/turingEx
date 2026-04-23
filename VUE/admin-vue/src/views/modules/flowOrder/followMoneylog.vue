<template>
  <div class="mod-followMoneylog">
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
    <template slot-scope="scope" slot="ndh">
       <!-- <span>{{scope.row.symbol?scope.row.symbol:scope.row.walletType}}</span> -->
       <span>{{scope.row.walletType}}</span>
      </template>
      <template slot="ndhmSearch">
        <el-input
          v-model="uid"
          placeholder="用户名、UID(完整)"
          clearable
        ></el-input>
      </template>
      <template slot="roleSearch">
        <el-select
          v-model="roleName"
          placeholder="请选择"
          clearable
          @change="changeVal()"
        >
          <el-option
            v-for="item in roleList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </template>
      <template slot="lognthSearch">
        <el-input
          v-model="log"
          placeholder="日志"
          clearable
        ></el-input>
      </template>
      <template slot-scope="scope" slot="userNamesolt">
        <span @click="searchName(scope.row.userName)" class="seachButton">{{
          scope.row.userName
        }}</span>
      </template>
      <template  slot="startTimendhSearch">
        <el-date-picker
      v-model="startTime"
      type="datetime"
      valueFormat="yyyy-MM-dd HH:mm:ss"
      placeholder="选择开始时间">
    </el-date-picker>
      </template>
      <template  slot="endTimendhSearch">
        <el-date-picker
      v-model="endTime"
      type="datetime"
      valueFormat="yyyy-MM-dd HH:mm:ss"
      placeholder="选择结束时间">
    </el-date-picker>
      </template>
      <template slot-scope="scope" slot="menu">
      </template>
    </avue-crud>
    <el-dialog
      title="完整用户名(完整钱包地址)"
      :close-on-click-modal="false"
      :visible.sync="userNamevisible"
    >
      <el-form :model="dataForm" ref="dataForm">
        <el-form-item label="" prop="">
          <div style="font-size: 20px">
            <span>{{ username }}</span
            ><a
              class="seachButton"
              target="_blank"
              :href="'https://etherscan.io/address/' + username"
              >在Etherscan上查看</a
            >
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="userNamevisible = false">取消</el-button>
        <el-button type="primary" @click="userNamevisible = false"
          >确定</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { tableOption } from "@/crud/flowOrder/followMoneylog";
// import AddOrUpdate from "./account-change-record-add-or-update";
export default {
  data() {
    return {
      uid: "",
      dataForm: {},
      dataList: [],
      roleName:'',
      startTime:'',
      endTime:'',
      log:'',
      roleList:[{
      label:'所有账号',
      value:''
    },{
      label:'正式账号',
      value:'MEMBER'
    },{
      label:'演示账号',
      value:'GUEST'
    },{
      label:'试用账号',
      value:'TEST'
    }],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      tableOption: tableOption,
      username: "",
      userNamevisible: false,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
    };
  },
  components: {
    // AddOrUpdate,
  },
  created() {
    // this.dataListLoading = true;
    // if (this.$route.query.uid) {
    //   this.uid = this.$route.query.uid;
    // } else {
    //   this.uid = "";
    // }
  },
  watch: {
    // $route(to, from) {
    //   this.dataListLoading = true;
    //   if (this.$route.query.uid) {
    //     this.uid = this.$route.query.uid;
    //   } else {
    //     this.uid = "";
    //   }
    //   this.getDataList(this.page);
    // },
  },
  methods: {
    // 获取数据列表
    getDataList(page, params, done) {
      console.log(params)
      this.$http({
        url: this.$http.adornUrl("/followmoneylog/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              userName: this.uid,
              roleName:this.roleName,
              log:this.log,
              startTime:this.startTime,
              endTime:this.endTime,
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
        (column.property === "amount" && row.amount * 1 > 0) ||
        (column.property === "amountAfter" && row.amountAfter * 1 > 0) ||
        (column.property === "amountBefore" && row.amountBefore * 1 > 0) ||
        (column.property === "roleName" && row.roleName == "MEMBER")
      ) {
        return "green";
      } else if (
        (column.property === "amount" && row.amount * 1 < 0) ||
        (column.property === "amountAfter" && row.amountAfter * 1 < 0) ||
        (column.property === "amountBefore" && row.amountBefore * 1 < 0)
      ) {
        return "red";
      } else if (column.property === "roleName" && row.roleName == "GUEST") {
        return "yellow";
      }
    },
    searchName(name) {
      this.username = name;
      this.userNamevisible = true;
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
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
        //显示隐藏操作
    // moneylog(row) {
    //   let isShow = ''
    //   if(row.show*1 == 1){
    //     isShow = 0
    //   }else {
    //     isShow = 1
    //   }
    //   this.$confirm(`确定进行操作?`, "提示", {
    //     confirmButtonText: "确定",
    //     cancelButtonText: "取消",
    //     type: "warning",
    //   })
    //     .then(() => {
    //       this.$http({
    //         url: this.$http.adornUrl("/moneylog/updateShow"),
    //         method: "post",
    //         data: this.$http.adornData(
    //           Object.assign({
    //             uuid: row.uuid,
    //             show:isShow,
    //           }),
    //           false
    //         ),
    //       }).then(({ data }) => {
    //         if (data.code == 0) {
    //           this.$message({
    //             message: "操作成功",
    //             type: "success",
    //             duration: 1500,
    //             onClose: () => {
    //               this.getDataList();
    //             },
    //           });
    //         } else {
    //           this.$message({
    //             message: data.msg,
    //             type: "error",
    //             duration: 1500,
    //             onClose: () => {},
    //           });
    //         }
    //       });
    //     })
    //     .catch(() => {});

    //   //
    // },
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
  },
};
</script>
