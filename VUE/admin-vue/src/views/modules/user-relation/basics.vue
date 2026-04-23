<template>
  <div class="mod-role">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="sortedDataList"
      :option="tableOption"
      @search-change="searchChange"
      @selection-change="selectionChange"
      :cell-class-name="addClasscolor"
      @on-load="getDataList"
      @refresh-change="getDataList"
    >
      <template slot="menuLeft">
        <!-- 搜索 -->
        <!-- <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList(this.page)">
          
          <el-form-item label="用户名/UID:">
            <el-input v-model="dataForm.userName"
                      placeholder="用户名/UID"
                      clearable></el-input>
          </el-form-item>
          <el-form-item label="证件号码:">
            <el-input v-model="dataForm.idNumber"
                      placeholder="证件号码查询"
                      clearable></el-input>
          </el-form-item>
          <el-form-item label="账号类型">
            <el-select v-model="dataForm.roleName" placeholder="所有账号">
              <el-option label="所有账号" value=""></el-option>
              <el-option label="正式账号" value="MEMBER"></el-option>
              <el-option label="演示账号" value="MEMBER1"></el-option>
              <el-option label="试用账号" value="MEMBER2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary"
                      icon="el-icon-search"
                      size="small"
                      @click="getList()">查询</el-button>
            <el-button @click="clearDatas()"
                      size="small">清空</el-button> -->
        <!-- </el-form-item>
        </el-form> -->
        <!-- Tag页 -->
        <!-- <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="全部" name="1"></el-tab-pane>
          <el-tab-pane label="待审核" name="2"></el-tab-pane>
          <el-tab-pane label="审核通过" name="3"></el-tab-pane>
          <el-tab-pane label="未通过" name="4"></el-tab-pane>
        </el-tabs> -->
        <avue-tabs :option="option" @change="handleChange"></avue-tabs>
        <span v-if="type.prop === 'tab1'"></span>
        <span v-else-if="type.prop === 'tab2'"></span>
        <span v-else-if="type.prop === 'tab3'"></span>
        <span v-else-if="type.prop === 'tab4'"></span>
      </template>
      <template slot-scope="scope" slot="userName">
        <span @click="searchName(scope.row)" class="speacButton">{{
          scope.row.userName
        }}</span>
      </template>
      <template slot-scope="scope" slot="recomUserName">
        <span @click="searchName(scope.row)" class="speacButton">{{
          scope.row.recomUserName
        }}</span>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="scope.row.status !== 2 && isAuth('user:basics:review')"
          @click.stop="addOrUpdateHandle(scope.row.uuid)"
          >审核通过</el-button
        >

        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('user:basics:review')"
          @click.stop="deleteHandle(scope.row.uuid)"
          >驳回</el-button
        >
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
    <!-- 参数管理 -->
    <parameters v-if="parametersFlag" ref="parameters"> </parameters>
  </div>
</template>
<script>
import { tableOption } from "@/crud/user-relation/basics";
import AddOrUpdate from "./basics-add-or-update";
import parameters from "./basics-variety-add-or-update";
export default {
  data() {
    return {
      parametersFlag: false,
      activeName: "1",
      dataList: [],
      dataList2: [],
      dataListLoading: false,
      dataListSelections: [],
      searchParams: {}, // 搜索条件
      addOrUpdateVisible: false,
      tableOption: tableOption,
      status: "",
      type: {},
      option: {
        column: [
          {
            label: "全部",
            prop: "",
          },
          {
            label: "待审核",
            prop: "1",
          },
          {
            label: "审核通过",
            prop: "2",
          },
          {
            label: "未通过",
            prop: "3",
          },
        ],
      },
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      dataForm: {
        roleName: "",
      },
    };
  },
  components: {
    AddOrUpdate,
    parameters,
  },
  computed: {
    sortedDataList() {
      const dataListCopy = [...this.dataList];
      const statusZeroData = dataListCopy.filter((item) => item.status !== 2);
      const otherData = dataListCopy.filter((item) => item.status === 2);
      return [...statusZeroData, ...otherData];
    },
  },
  created() {
    this.type = this.option.column[0];
  },
  mounted() {
    this.$bus.$on("updateOfBasics", (data) => {
      this.getDataList();
    });
  },
  beforeDestroy() {
    // 组件被销毁了，不能进行数据传输
    // 解绑事件
    this.$bus.$off("updateOfBasics");
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
        url: this.$http.adornUrl("/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              status: this.status,
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
          this.getList();
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
    addClasscolor({ column, row }) {
      //表单样式
      if (
        (column.property === "roleName" && row.roleName == "MEMBER") ||
        (column.property === "status" && row.status == 2)
      ) {
        return "green";
      } else if (column.property === "roleName" && row.roleName == "GUEST") {
        return "yellow";
      } else if (column.property === "status" && row.status == 3) {
        return "red";
      }
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
    //审核通过
    addOrUpdateHandle(id) {
      // this.addOrUpdateVisible = true
      // this.$nextTick(() => {
      //   this.$refs.addOrUpdate.init(id)
      // })
      this.$confirm(`提示：您确认要审核通过吗？`, "是否确认审核通过", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/examine"),
            method: "post",
            data: this.$http.adornData({
              content: "",
              id: id,
              type: 1,
            }),
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
    // 驳回
    deleteHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
    // tab切换
    handleChange(column, params, done) {
      this.type = column;
      this.status = column.prop;
      this.getDataList(this.page, params, done);
    },
    // 选项卡
    handleClick(tab, event) {
      this.getList();
    },
    getList() {
      //dataForm.userName
      //dataForm.idNumber
      //dataForm.roleName
      let call = (item, status) => {
        let flag0 =
          item.userName.indexOf(this.dataForm.userName) >= 0 ||
          item.userCode.indexOf(this.dataForm.userName) >= 0;
        let flag1 = item.status == status || status == -1;
        let flag2 =
          !this.dataForm.userName || (this.dataForm.userName && flag0);
        let flag3 =
          !this.dataForm.idNumber ||
          (this.dataForm.idNumber && item.idNumber == this.dataForm.idNumber);
        let flag4 =
          !this.dataForm.roleName ||
          (this.dataForm.roleName && item.roleName == this.dataForm.roleName);
        return flag1 && flag2 && flag3 && flag4;
      };
      //0已申请未审核 ，1.审核中 2 审核通过,3审核未通过
      switch (this.activeName) {
        case "1":
          {
            // this.dataList2 = [].concat(this.dataList);
            this.dataList2 = this.dataList.filter((item) => {
              return call(item, -1);
            });
          }
          break;
        case "2": //待审核
          {
            this.dataList2 = this.dataList.filter((item) => {
              return call(item, 1);
            });
          }
          break;
        case "3": //审核通过
          {
            this.dataList2 = this.dataList.filter((item) => {
              return call(item, 2);
            });
          }
          break;
        case "4": //未通过
          {
            this.dataList2 = this.dataList.filter((item) => {
              return call(item, 3);
            });
          }
          break;
      }
    },
    searchName(row) {
      //this.userNamevisible = true
      this.parametersFlag = true;
      this.$nextTick(() => {
        this.$refs.parameters.init(row);
      });
    },
  },
};
</script>
<style scoped>
.speacButton {
  color: rgb(69, 147, 235);
  cursor: pointer;
}
.speacButton:hover {
  color: #356ced;
}
</style>
