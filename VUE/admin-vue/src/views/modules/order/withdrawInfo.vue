<template>
  <el-dialog
    title="修改用户提现订单收款地址"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close="handClose"
    width="38%"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="180px"
    >
      <el-form-item label="用户旧提现地址" prop="dress">
        <el-input
          v-model="dataForm.dress"
          disabled
          placeholder="用户旧提现地址"
        ></el-input>
      </el-form-item>
      <el-form-item label="修改后用户新提现地址" prop="changeAfterAddress">
        <el-input
          v-model="dataForm.changeAfterAddress"
          placeholder="修改后用户新提现地址"
        ></el-input>
      </el-form-item>
      <el-form-item label="资金密码" prop="safePasssword">
        <el-input
          v-model="dataForm.safePasssword"
          type="password"
          placeholder="为当前登录人资金密码"
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
import { isEmail, isMobile } from "@/utils/validate";
import { Debounce } from "@/utils/debounce";
import { encrypt } from "@/utils/crypto";
export default {
  data() {
    var validatePassword = (rule, value, callback) => {
      if (!this.dataForm.id && !/\S/.test(value)) {
        callback(new Error("密码不能为空"));
      } else {
        callback();
      }
    };
    var validateEmail = (rule, value, callback) => {
      if (!isEmail(value)) {
        callback(new Error("邮箱格式错误"));
      } else {
        callback();
      }
    };
    var validateAmount = (rule, value, callback) => {
      if (value < 0) {
        callback(new Error("充值数量不能小于0"));
      } else {
        callback();
      }
    };
    return {
      visible: false,
      id: "",
      dataForm: {
        safePasssword: "",
        dress: "",
        changeAfterAddress: "",
      },
      dataRule: {
        changeAfterAddress: [
          { required: true, message: "修改地址不能为空", trigger: "blur" },
        ],
        safePasssword: [
          { required: true, message: "资金密码不能为空", trigger: "blur" },
        ],
        // email: [
        //   { required: true, message: '邮箱不能为空', trigger: 'blur' },
        //   { validator: validateEmail, trigger: 'blur' }
        // ],
        // mobile: [
        //   { required: true, message: '手机号不能为空', trigger: 'blur' },
        //   { validator: validateMobile, trigger: 'blur' }
        // ]
      },
    };
  },
  methods: {
    init(id, dress) {
      this.resClear();
      this.id = id || "";
      this.dataForm.dress = dress;
      this.visible = true;
      this.$nextTick(() => {
        //this.$refs.dataForm.resetFields()
      });
    },
    resClear() {
      this.dataForm = {
        safePasssword: "",
        dress: "",
        changeAfterAddress: "",
      };
    },
    handClose() {
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/withdraw/changeAddress`), //修改地址
            method: "post",
            data: this.$http.adornData({
              id: this.id,
              changeAfterAddress: this.dataForm.changeAfterAddress,
              safeWord: encrypt(this.dataForm.safePasssword),
            }),
          }).then(({ data }) => {
            if (data.code == 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.resClear();
                  this.visible = false;
                  this.$emit("refreshDataList");
                },
              });
            } else {
              this.$message({
                message: data.msg,
                type: "error",
              });
            }
          });
        }
      });
    }),
  },
};
</script>
