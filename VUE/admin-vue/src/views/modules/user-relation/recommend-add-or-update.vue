<template>
  <el-dialog
    title="修改推荐关系"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close = 'handClose'
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="110px"
    >
      <el-form-item label="用户名" prop="userName">
        <el-input
          v-model="dataForm.userName"
          disabled
          placeholder="用户名"
        ></el-input>
      </el-form-item>
      <el-form-item label="原推荐人用户名" prop="recomUserName">
        <el-input
          v-model="dataForm.recomUserName"
          disabled
          placeholder="原推荐人用户名"
        ></el-input> </el-form-item
      ><el-form-item label="原推荐人UID" prop="">
        <el-input
          v-model="dataForm.recomUserCode"
          disabled
          placeholder="原推荐人UID"
        ></el-input> </el-form-item
      ><el-form-item label="新推荐人UID" prop="userCodem" >
        <el-input
          v-model="dataForm.userCodem"
          placeholder="新推荐人UID"
        ></el-input> </el-form-item
      ><el-form-item label="资金密码" prop="loginSafeword">
        <el-input v-model="dataForm.loginSafeword" type="password" placeholder="资金密码"></el-input>
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
import { encrypt } from '@/utils/crypto'
export default {
  data() {
    return {
      visible: false,
      menuList: [],
      row:'',
      menuListTreeProps: {
        label: "name",
        children: "children",
      },
      dataForm: {
        id: '',
        recomUserCode: "",
        recomUserName: "",
        userName:'',
        loginSafeword:'',
        userCode:'',
        userCodem:'',
        userId:'',
      },
      dataRule: {
        roleName: [
          { required: true, message: "角色名称不能为空", trigger: "blur" },
          {
            pattern: /\s\S+|S+\s|\S/,
            message: "请输入正确的角色名称",
            trigger: "blur",
          },
        ],
        userCodem:[
        { required: true, message: "新推荐人UID不能为空", trigger: "blur" },
        ],
        loginSafeword:[
        { required: true, message: "资金密码不能为空", trigger: "blur" },
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
    init(row) {
      this.row= row || '';
      this.visible = true
      if(this.row){
        this.dataForm.id = row.uuid
        this.dataForm.userName = row.userName
        this.dataForm.recomUserName = row.recomUserName
        this.dataForm.recomUserCode = row.recomUserCode
        //this.dataForm.userCode = row.userCode
        this.dataForm.userId = row.userId
      }
      
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/userAllRecom/update`),
            method: "post",
            data: this.$http.adornData({
              loginSafeword:encrypt(this.dataForm.loginSafeword),
              userCode:this.dataForm.userCodem,
              userId:this.dataForm.userId
            }),
          }).then(({ data }) => {
            if(data.code == 0){
              this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.visible = false;
                this.$emit("refreshDataList");
              },
            });
            }else{
              this.$message({
              message: data.msg,
              type: "error",
              duration: 1500,
              onClose: () => {
                this.visible = false;
              },
            });
            }

          });
        }
      });
    }),
    handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm));//清除表单数据 拷贝数据
        this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
      },
  },
};
</script>
