<template>
  <el-dialog
    :title="row?'修改公告':'新增公告'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close = 'handClose'
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
    >
      <el-form-item label="标题" prop="title">
        <el-input v-model="dataForm.title" placeholder="标题"></el-input>
      </el-form-item>
      <el-form-item label="登录人资金密码" prop="loginSafeword">
        <el-input v-model="dataForm.loginSafeword" type="password" placeholder="登录人资金密码"></el-input>
      </el-form-item>
      <el-form-item label="业务代码" prop="contentCode">
        <el-input
          v-model="dataForm.contentCode"
          placeholder="业务代码"
        ></el-input>
      </el-form-item>

      <el-form-item label="语言">
        <el-select
          v-model="langug.value"
          placeholder="请选择"
          @change="changeVal()"
        >
          <el-option
            v-for="item in langug"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="模块">
        <el-select
          v-model="options.value"
          placeholder="请选择"
          @change="changeVal()"
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <el-input
          type="textarea"
          placeholder="请输入内容"
          v-model="dataForm.content"
          maxlength="500"
          :autosize="{ minRows: 8, maxRows: 8 }"
          show-word-limit
        >
        </el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { treeDataTranslate } from "@/utils";
import { Debounce } from "@/utils/debounce";
import { encrypt } from "@/utils/crypto";
export default {
  data() {
    return {
      visible: false,
      menuList: [],
      menuListTreeProps: {
        label: "name",
        children: "children",
      },
      row:'',
      options: [], // 模块
      langug: [], // 语言
      dataForm: {
        roleName: "",
        content: "",
        contentCode: "",
        id: "",
        loginSafeword: "",
        title: "",
      },
      dataRule: {
        
        title:[
          { required: true, message: "标题不能为空", trigger: "blur" },
        ],
        loginSafeword:[
          { required: true, message: "登录人资金密码不能为空", trigger: "blur" },
        ],
        contentCode:[
          { required: true, message: "业务代码不能为空", trigger: "blur" },
        ],
        content:[
          { required: true, message: "内容不能为空", trigger: "blur" },
        ],
        roleName: [
          { required: true, message: "角色名称不能为空", trigger: "blur" },
          {
            pattern: /\s\S+|S+\s|\S/,
            message: "请输入正确的角色名称",
            trigger: "blur",
          },
        ],
        remark: [
          {
            required: false,
            pattern: /\s\S+|S+\s|\S/,
            message: "输入格式有误",
            trigger: "blur",
          },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(arr,arr2, row) {
      this.langug = arr;
      this.options = arr2
      this.row = row || ''
      if (row) {
        this.dataForm.title = row.title
          this.options.value = row.model
          this.langug.value = row.language
          this.dataForm.content = row.content
          this.dataForm.contentCode = row.contentCode
          this.dataForm.id = row.uuid
      }else{
        this.options.value =  this.options[0].value
        this.langug.value = this.langug[0].value
      }
      this.visible = true;
    },
    // Open(call) {
    //   this.$prompt("登录人资金密码", "提示", {
    //     confirmButtonText: "确定",
    //     cancelButtonText: "取消",
    //   })
    //     .then(({ value }) => {
    //       this.dataForm.loginSafeword = value;
    //       if (call) {
    //         call();
    //       }
    //     })
    //     .catch(() => {
    //       this.$message({
    //         type: "info",
    //         message: "取消输入",
    //       });
    //     });
    // },
    changeVal(val) {
      this.$forceUpdate();
    },
    handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
        this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
        this.langug.value = ''
        this.options.value = ''
      },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      if (this.row) {
          this.$refs["dataForm"].validate((valid) => {
            if (valid) {
              this.$http({
                // 修改
                url: this.$http.adornUrl(`/cms/update`),
                method: "post",
                data: this.$http.adornData({
                  content: this.dataForm.content,
                  contentCode: this.dataForm.contentCode,
                  language: this.langug.value,
                  loginSafeword: encrypt(this.dataForm.loginSafeword),
                  model: this.options.value,
                  title: this.dataForm.title,
                  id: this.dataForm.id,
                }),
              }).then(({ data }) => {
                if (data.code == 0) {
                  this.$message({
                    message: "操作成功",
                    type: "success",
                    duration: 1500,
                    onClose: () => {
                      this.visible = false;
                      this.$emit("refreshDataList");
                    },
                  });
                } else {
                  this.$message({
                    message: data.msg,
                    type: "error",
                    duration: 1500,
                    onClose: () => {
                      
                    },
                  });
                }
              });
            }
          });
      } else {
          this.$refs["dataForm"].validate((valid) => {
            if (valid) {
              this.$http({
                // 新增
                url: this.$http.adornUrl(`/cms/add`),
                method: "post",
                data: this.$http.adornData({
                  content: this.dataForm.content,
                  contentCode: this.dataForm.contentCode,
                  language: this.langug.value,
                  loginSafeword: encrypt(this.dataForm.loginSafeword),
                  model: this.options.value,
                  title: this.dataForm.title,
                }),
              }).then(({ data }) => {
                if (data.code == 0) {
                  this.$message({
                    message: "操作成功",
                    type: "success",
                    duration: 1500,
                    onClose: () => {
                      this.visible = false;
                      this.$emit("refreshDataList");
                    },
                  });
                } else {
                  this.$message({
                    message: data.msg,
                    type: "error",
                    duration: 1500,
                    onClose: () => {
                     
                    },
                  });
                }
              });
            }
          });
      }
    }),
  },
};
</script>
