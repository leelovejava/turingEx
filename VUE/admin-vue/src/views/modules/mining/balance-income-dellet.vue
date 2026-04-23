<template>
  <el-dialog
    title="删除配置"
    :close-on-click-modal="false"
    width="1100px"
    @close = 'handClose'
    :visible.sync="visible"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="150px"
    >   
          <el-form-item  label="登录人资金密码"  prop="loginSafeword">
        <el-input v-model="dataForm.loginSafeword" type="password" placeholder="请输入登录人资金密码"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { Debounce } from "@/utils/debounce";
import { encrypt } from "@/utils/crypto";
export default {
  data() {
    return {
      visible: false,
      row:'',
      imageUrl: "",
      imageUrl2:'',
      dataForm: {
        id: '',
        config: "",
        content: "",
        contentImg: "",
        eth: "",
        limitDays: "",
        loginSafeword: "",
        title: "",
        titleImg: "",
        usdt: "",
        userCode: "",
      },
      direction: [
        {
          label: "启用",
          value: "1",
        },
        {
          label: "停用",
          value: "0",
        },
      ],
      dataRule: {
        loginSafeword: [
            { required: true, message: '登录人资金密码不能为空', trigger: 'blur' },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(row) {
      if(row){
        this.row = row,
        this.dataForm = row; 
      }     
      this.visible = true;
    },
    handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
        this.row = ''
     this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
      },
    changeVal(val) {
      this.$forceUpdate();
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl( `/pledgeConfig/delete`),
            method: "post",
            data: this.$http.adornData({
              loginSafeword:encrypt(this.dataForm.loginSafeword),
              id:this.dataForm.id
            }),
          }).then(({ data }) => {
            if(data.code == 0){
              this.$message({
              message: "删除成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.visible = false;
                this.$emit("refreshDataList");
              },
            });
            }else{
              this.$message({
              message:data.msg,
              type: "error",
              duration: 1500,
              onClose: () => {
              },
            });
            }

          });
        }
      });
    }),
  },
};
</script>
<style lang="less"  scoped>
.titleDivTwo {
  height: 40px;
  border-left: 3px solid #1c4efa;
  background: #f4f7ff;
}
/deep/.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9 !important;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
/deep/.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
