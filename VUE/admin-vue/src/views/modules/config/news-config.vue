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
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('config:news-config:operate')"
          @click.stop="addOrUpdateHandle('', options)"
          >新增新闻</el-button
        >
      </template>
      <template slot-scope="scope" slot="httpImgUrl">
        <img :src="scope.row.httpImgUrl" alt="" />
      </template>
      <template slot-scope="scope" slot="userCodeHdc">
        <span>{{scope.row.userCode}}</span>
      </template>
      <template slot="ndhSearch">
        <avue-select
          v-model="options.id"
          placeholder="请选择语言"
          :dic="options"
        ></avue-select>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('config:news-config:operate')"
          @click.stop="addOrUpdateHandle(scope.row, options)"
          >修改</el-button
        >
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('config:news-config:operate')"
          @click.stop="deleteHandle(scope.row.uuid)"
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

    <!-- 确认弹窗-start -->
    <el-dialog
      title="验证资金密码"
      :visible.sync="dialogFormVisible"
      :append-to-body="true"
    >
      <el-form
        :model="dataForm"
        ref="dataForm"
        @keyup.enter.native="dataFormSubmit()"
        label-width="80px"
      >
        <el-form-item
          label="登录人资金密码"
          :label-width="formLabelWidth"
          prop="loginSafeword"
        >
          <el-input
            v-model="loginSafeword"
            type="password"
            placeholder="登录人资金密码"
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
import { tableOption } from "@/crud/config/news-config";
import AddOrUpdate from "./news-add-or-update";
import { encrypt } from "@/utils/crypto";
import { Debounce } from "@/utils/debounce";
export default {
  data() {
    return {
      dataList: [],
      options: [],
      loginSafeword: "",
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      tableOption: tableOption,
      searchParams: {}, // 搜索条件
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      deleteId:"",
      dialogFormVisible:false,
      formLabelWidth: "120px",
      dataForm:{}
    };
  },
  components: {
    AddOrUpdate,
  },
  created() {
    this.getLanguage();
  },
  methods: {
    netcall(call, data) {
      if (data.code == 0) {
        if (call) {
          call(data);
        }
      } else {
        this.$message({
          message: data.msg,
          type: "error",
        });
      }
    },
    addClasscolor({ column, row }) {//表单样式
      if (
        (column.property === "roleName" && row.roleName == 'MEMBER')
      ) {
        return "green";
      } else if (
        (column.property === "roleName" && row.roleName == 'GUEST')
      ) {
        return "yellow";
      } else {
        return "";
      }
    },
    // 获取数据列表
    getDataList(page, done) {
      const params = {
        current: page == null ? this.page.currentPage : page.currentPage,
        size: page == null ? this.page.pageSize : page.pageSize,
        ...this.searchParams,
      };
      this.dataListLoading = true;
      let language = "";
      if (this.options.id) {
        language = this.options.id;
      } else {
        language = "";
      }
      this.$http({
        url: this.$http.adornUrl("/news/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              language: language,
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
    getLanguage(params, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/news/getLanguage"),
        method: "get",
        params: this.$http.adornParams(Object.assign({}, params), false),
      }).then(({ data }) => {
        if (data.code == 0) {
          let keys = Object.keys(data.data);
          let value = Object.values(data.data);
          for (let i = 0; i < keys.length; i++) {
            let obj = {};
            obj.label = value[i];
            obj.value = keys[i];
            this.options.push(obj);
          }
        }
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
    addOrUpdateHandle(row, arr) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(row, arr);
      });
    },
    // 删除
    deleteHandle(id) {
      this.loginSafeword = ''
      this.deleteId = id;
      this.dialogFormVisible = true;
      return;
      this.Open(() => {
        //start
        this.$http({
          url: this.$http.adornUrl(`/news/delete`),
          method: "post",
          data: this.$http.adornData({
            id: id,
            loginSafeword: encrypt(this.loginSafeword), //资金密码
          }),
        }).then(({ data }) => {
          if (data.code == 0) {
            this.getDataList(this.page);
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1000,
              onClose: () => {
                this.visible = false;
              },
            });
          }else{
            this.$notify({
              title: '消息',
              message: data.msg,
              type: 'warning',
            })
          }
        });
        //end
      });
    },
    Open(call) {
      this.$prompt("登录人资金密码", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
      })
        .then(({ value }) => {
          this.loginSafeword = value;
          if (call) {
            call();
          }
        })
        .catch((e) => {
          console.log("error = " + e);
          this.$message({
            type: "info",
            message: "取消输入",
          });
        });
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          //start
          this.$http({
            url: this.$http.adornUrl(`/news/delete`),
            method: "post",
            data: this.$http.adornData({
              id: this.deleteId,
              loginSafeword: encrypt(this.loginSafeword), //资金密码
            }),
          }).then(({ data }) => {
            if (data.code == 0) {
              this.getDataList(this.page);
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1000,
                onClose: () => {
                  this.visible = false;
                },
              });
              this.dialogFormVisible = false;
            }else{
              this.$notify({
                title: '消息',
                message: data.msg,
                type: 'warning',
              })
            }
          });
          //end
        }
      });
    }),
  },
};
</script>
<style scoped></style>
