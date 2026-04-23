<template>
  <el-dialog
    :title="m"
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
      label-width="80px"
    >
      <el-form-item v-if="val == 1" label="资金密码" prop="safePasssword">
        <el-input
          v-model="dataForm.safePasssword"
          type="password"
          placeholder="为当前登录人资金密码"
        ></el-input>
      </el-form-item>
      <el-form-item v-if="val == 2" label="驳回原因" prop="content">
        <el-input
          type="textarea"
          v-model="dataForm.content"
          placeholder="驳回原因"
        ></el-input>
      </el-form-item>
      <el-form-item v-if="val == 4" label="备注描述" prop="remarks">
        <el-input
          type="textarea"
          v-model="dataForm.remarks"
          placeholder="备注描述"
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
      val: "",
      dataForm: {
        safePasssword: "",
        content: "",
      },
      m: "",
      dataRule: {
        changeAfterAddress: [
          { required: true, message: "修改地址不能为空", trigger: "blur" },
        ],
        safePasssword: [
          { required: true, message: "资金密码不能为空", trigger: "blur" },
        ],
        content: [
          { required: true, message: "驳回原因不能为空", trigger: "blur" },
        ],
        remarks: [
          { required: true, message: "备注描述不能为空", trigger: "blur" },
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
    init(id, val, m) {
      this.resClear();
      this.id = id || "";
      this.val = val || "";
      this.m = m || "";
      this.visible = true;
      this.$nextTick(() => {
        //this.$refs.dataForm.resetFields()
      });
    },
    resClear() {
      this.dataForm = {
        safePasssword: "",
        content: "",
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
        if (this.val == 2) {
          //驳回
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/withdraw/reject`), //驳回
              method: "post",
              data: this.$http.adornData({
                id: this.id,
                content: this.dataForm.content,
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
        } else if (this.val == 4) {
          //驳回
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/withdraw/remarks`), //驳回
              method: "post",
              data: this.$http.adornData({
                id: this.id,
                remarks: this.dataForm.remarks,
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
        }else if (this.val == 1) {
          // 通过审核
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/withdraw/examineOk`), //驳回
              method: "post",
              data: this.$http.adornData({
                id: this.id,
                safePasssword: encrypt(this.dataForm.safePasssword),
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
        }
      });
    }),
  },
};
</script>
