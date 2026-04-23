<template>
  <el-dialog
    title='确认删除'
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close = 'handClose'
    append-to-body
    width="800px"
    class="transport-dialog"
  >
    <el-form
      :model="dataForm"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
      :rules="dataRule"
    >
    <el-form-item label="资金密码" prop="loginSafeword" >
        <el-input
          v-model="dataForm.loginSafeword"
          placeholder="请输入登录人资金密码"
          type="password"
        ></el-input>
      </el-form-item>
      <el-form-item label="超级谷歌验证码" prop="superGoogleAuthCode" >
        <el-input
          v-model="dataForm.superGoogleAuthCode"
          placeholder="请输入超级谷歌验证码"
        ></el-input>
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
// import AddOrUpdate from './transcity-add-or-update'
export default {
  data() {
    return {
      // hasFreeCondition: 0,
      visible: false,
      addOrUpdateVisible: false,
      dataForm: {
        loginSafeword: "",
        futuresId:'',
        superGoogleAuthCode:''
      },
      dataRule: {
        loginSafeword: [
          { required: true, message: "资金密码不能为空", trigger: "blur" },
        ],
        superGoogleAuthCode: [
          { required: true, message: "超级谷歌验证码不能为空", trigger: "blur" },
        ],
      },
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      editVisible: false,
    };
  },

  components: {
    // AddOrUpdate
  },
  computed: {},
  methods: {
    init(id) { 
      this.dataForm.futuresId = id
      // this.$nextTick(() => {
      //   this.$refs['dataForm'].resetFields()
      // });
      this.visible = true;
    },
    handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
     this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
      },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(
              `/normal/adminContractManageAction!/toDeleteFuturesPara.action`
            ),
            method: "get",
            params: this.$http.adornParams({
            //新增
            loginSafeword: encrypt(this.dataForm.loginSafeword),
            futuresId: this.dataForm.futuresId,
            profitRatioMax: this.dataForm.profitRatioMax,
            superGoogleAuthCode: this.dataForm.superGoogleAuthCode,
          })
          }).then(({ data }) => {
            if (data.code == 0) {
              this.$message({
                message: "已删除",
                type: "success",
                duration: 1000,
                onClose: () => {
                  this.visible = false;
                  this.$emit("refreshDataList", this.page);
                },
              });
            } else {
              this.$message({
                message: data.msg,
                type: "error",
                duration: 1000,
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

<style lang="scss" scoped>
.transport-dialog .table-con .el-form-item {
  margin-top: 16px;
}
.spamm {
  width: 200px;
}
.spammm {
  width: 500px;
}
.spann {
  width: 80px;
}
.spannn {
  width: 50px;
}
</style>
