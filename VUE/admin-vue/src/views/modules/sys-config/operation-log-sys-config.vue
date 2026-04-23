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
    >
      <template slot="usendhSearch">
        <el-input
          v-model="userName"
          placeholder="用户名、UID（完整）"
          clearable
        ></el-input>
      </template>
      <template slot="ndhSearch">
        <avue-select
          v-model="options.id"
          placeholder="请选择支付方式"
          :dic="options"
        ></avue-select>
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
      <template slot="logndhSearch">
        <el-input
          v-model="log"
          placeholder="日志"
          clearable
        ></el-input>
      </template>
      <template slot="operatorndhSearch">
        <el-input
          v-model="operator"
          placeholder="操作者"
          clearable
        ></el-input>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('sys:role:update')"
          @click.stop="addOrUpdateHandle(scope.row.roleId)"
          >编辑</el-button
        >

        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('sys:role:delete')"
          @click.stop="deleteHandle(scope.row.roleId)"
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
import { tableOption } from "@/crud/sys/operation-log";
import AddOrUpdate from "./operation-log-add-or-update";
export default {
  data() {
    return {
      dataList: [],
      dataListLoading: false,
      userName:'',
      dataListSelections: [],
      options: [],
      operator:'',
      log:'',
      roleName:'',
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
    this.getCategory();
  },
  methods: {
    // 获取数据列表
    getDataList(page, params, done) {
      this.dataListLoading = true;
      let category = "";
      if (this.options.id) {
        category = this.options.id;
      } else {
        category = "";
      }
      this.$http({
        url: this.$http.adornUrl("/log/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              operator:this.operator,
              log:this.log,
              userName:this.userName,
              roleName:this.roleName,
              category: category,
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
        } else {
          this.$message({
            message: data.msg,
            type: "error",
            duration: 1500,
          });
        }

        if (done) {
          done();
        }
      });
    },
    // 获取操作类型数据列表
    getCategory(page, params, done) {
      this.$http({
        url: this.$http.adornUrl("/log/getCategory"),
        method: "get",
        params: this.$http.adornParams(Object.assign({}, params), false),
      }).then(({ data }) => {
        if (data.code == 0) {
          data.data.forEach((item, index) => {
            data.data[index].label = item.lable; //把后端请求过来的名字份放在label里
            data.data[index].value = item.value; //把后端请求过来的值放在value里
          });
          this.options = data.data;
          console.log(this.options);
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
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
    addClasscolor({ column, row }) {//表单样式
      if (
        (column.property === "rolename" && row.rolename == 'MEMBER')
      ) {
        return "green";
      } else if (
        (column.property === "rolename" && row.rolename == 'GUEST')
      ) {
        return "yellow";
      } else {
        return "";
      }
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
  },
};
</script>
