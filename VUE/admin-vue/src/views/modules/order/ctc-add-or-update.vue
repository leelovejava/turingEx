<template>
  <el-dialog
    :title="!id ? '添加支付方式模板' : '修改支付方式模板'"
    :close-on-click-modal="false"
    :visible.sync="visible"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
    >
      <el-form-item label="支付方式类型" prop="id">
        <el-select
          v-model="arr.id"
          @change="changeVal(arr.id)"
          placeholder="请选择"
        >
          <el-option
            v-for="item in arr"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="支付方式名称" prop="methodName">
        <el-input
          v-model="dataForm.methodName"
          placeholder="支付方式名称"
        ></el-input>
      </el-form-item>
      <el-form-item v-if="id" label="登录人资金密码" prop="loginSafeword">
        <el-input
          v-model="dataForm.loginSafeword"
          type="password"
          placeholder="登录人资金密码"
        ></el-input>
      </el-form-item>
      <el-form-item label="支付方式图片" prop="methodImg">
        <el-upload
          class="avatar-uploader"
          :action="$http.adornUrl('/api/uploadFile')"
          :headers="{ Authorization: $cookie.get('Authorization') }"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
        >
          <img v-if="imageUrl" :src="imageUrl" class="avatar" />
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
      </el-form-item>
      <el-form-item label="" prop="">
        <div style="color: green">
          参数名1：
          配关键数据，如微信账号、支付宝账号、银行卡号、虚拟货币地址等等;
        </div>
      </el-form-item>
      <el-form-item label="参数名1(必填)" prop="paramName1">
        <el-input
          v-model="dataForm.paramName1"
          placeholder="参数名1"
        ></el-input>
      </el-form-item>
      <el-form-item label="参数名2(选填)" prop="paramName2">
        <el-input
          v-model="dataForm.paramName2"
          placeholder="参数名2"
        ></el-input>
      </el-form-item>
      <el-form-item label="参数名3(选填)" prop="paramName3">
        <el-input
          v-model="dataForm.paramName3"
          placeholder="参数名3"
        ></el-input>
      </el-form-item>
      <el-form-item label="参数名4(选填)" prop="paramName4">
        <el-input
          v-model="dataForm.paramName4"
          placeholder="参数名4"
        ></el-input>
      </el-form-item>
      <el-form-item label="参数名5(选填)" prop="paramName5">
        <el-input
          v-model="dataForm.paramName5"
          placeholder="参数名5"
        ></el-input>
      </el-form-item>
      <el-form-item label="参数名6(选填)" prop="paramName6">
        <el-input
          v-model="dataForm.paramName6"
          placeholder="参数名6"
        ></el-input>
      </el-form-item>
      <el-form-item label="参数名7(选填)" prop="paramName7">
        <el-input
          v-model="dataForm.paramName7"
          placeholder="参数名7"
        ></el-input>
      </el-form-item>
      <el-form-item label="参数名8(选填)" prop="paramName8">
        <el-input
          v-model="dataForm.paramName8"
          placeholder="参数名8"
        ></el-input>
      </el-form-item>
      <el-form-item label="参数名9(选填)" prop="paramName9">
        <el-input
          v-model="dataForm.paramName9"
          placeholder="参数名9"
        ></el-input>
      </el-form-item>
      <el-form-item label="参数名10(选填)" prop="paramName10">
        <el-input
          v-model="dataForm.paramName10"
          placeholder="参数名10"
        ></el-input>
      </el-form-item>
      <el-form-item label="参数名11(选填)" prop="paramName11">
        <el-input
          v-model="dataForm.paramName11"
          placeholder="参数名11"
        ></el-input>
      </el-form-item>
      <el-form-item label="参数名12(选填)" prop="paramName12">
        <el-input
          v-model="dataForm.paramName12"
          placeholder="参数名12"
        ></el-input>
      </el-form-item>
      <el-form-item label="参数名13(选填)" prop="paramName13">
        <el-input
          v-model="dataForm.paramName13"
          placeholder="参数名13"
        ></el-input>
      </el-form-item>
      <el-form-item label="参数名14(选填)" prop="paramName14">
        <el-input
          v-model="dataForm.paramName14"
          placeholder="参数名14"
        ></el-input>
      </el-form-item>
      <el-form-item label="参数名15(选填)" prop="paramName15">
        <el-input
          v-model="dataForm.paramName15"
          placeholder="参数名15"
        ></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="dataFormSubmit()"
        >确定</el-button
      >
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
    var validateMobile = (rule, value, callback) => {
      if (!isMobile(value)) {
        callback(new Error("手机号格式错误"));
      } else {
        callback();
      }
    };
    return {
      visible: false,
      roleList: {},
      id: "",
      loading: false,
      imageUrl: "",
      dataForm: {
        methodImg: "",
        methodName: "",
        paramName1: "",
        paramName2: "",
        paramName3: "",
        paramName4: "",
        paramName5: "",
        paramName6: "",
        paramName7: "",
        paramName8: "",
        paramName9: "",
        paramName10: "",
        paramName11: "",
        paramName12: "",
        paramName13: "",
        paramName14: "",
        paramName15: "",
        loginSafeword: "",
      },
      arr: [],
      dataRule: {
        paramName1: [
          { required: true, message: "参数1不能为空", trigger: "blur" },
          // { pattern: /\s\S+|S+\s|\S/, message: '请输入正确的用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: "密码不能为空", trigger: "blur" },
        ],
        value1: [
          { required: true, message: "请选择支付方式", trigger: "blur" },
        ],
        loginSafeword: [
          { required: true, message: "资金密码不能为空", trigger: "blur" },
        ],
        // mobile: [
        //   { required: true, message: '手机号不能为空', trigger: 'blur' },
        //   { validator: validateMobile, trigger: 'blur' }
        // ]
      },
    };
  },
  created() {},
  methods: {
    init(row, id, arr) {
      this.resClear();
      this.arr = { ...arr };
      this.id = id;
      this.arr.id = row.methodType;
      this.dataForm = { ...row } || {};
      if (id == "") {
        console.log(id);
        this.arr.id = this.arr[0].id;
      }
      if (row) {
        this.dataForm.username = row.userName;
        this.imageUrl = row.methodImgUrl;
        // this.options.value1 = row.loginAuthority
        // this.optionsTwo.value2 = row.enabled
        // this.optionsThree.value3 = row.withdrawAuthority
        this.dataForm.remarks = row.remarks;
      } else {
        this.dataForm.username = "";
        this.imageUrl = "";
        this.dataForm.remarks = "";
      }
      this.visible = true;
      this.$nextTick(() => {
        //this.$refs.dataForm.resetFields()
      });
    },
    resClear() {
      this.dataForm = {
        methodImg: "",
        methodName: "",
        paramName1: "",
        paramName2: "",
        paramName3: "",
        paramName4: "",
        paramName5: "",
        paramName6: "",
        paramName7: "",
        paramName8: "",
        paramName9: "",
        paramName10: "",
        paramName11: "",
        paramName12: "",
        paramName13: "",
        paramName14: "",
        paramName15: "",
        loginSafeword: "",
      };
    },
    handleAvatarSuccess(res, file) {
      this.dataForm.methodImg = res.data.path;
      console.log(file);
      this.imageUrl = URL.createObjectURL(file.raw);
      console.log(this.imageUrl);
    },
    beforeAvatarUpload(file) {
      // const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 10;

      // if (!isJPG) {
      //   this.$message.error('上传头像图片只能是 JPG 格式!');
      // }
      if (!isLt2M) {
        this.$message.error("上传图片大小不能超过 10MB!");
      }
      // return isJPG && isLt2M;
      return isLt2M;
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.loading = true;
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          if (this.id) {
            this.$http({
              url: this.$http.adornUrl(`/paymentMethodConfig/update`), //修改
              method: "post",
              data: this.$http.adornData({
                id: this.id,
                methodImg: this.dataForm.methodImg,
                methodName: this.dataForm.methodName,
                methodType: this.arr.id,
                paramName1: this.dataForm.paramName1,
                paramName2: this.dataForm.paramName2,
                paramName3: this.dataForm.paramName3,
                paramName4: this.dataForm.paramName4,
                paramName5: this.dataForm.paramName5,
                paramName6: this.dataForm.paramName6,
                paramName7: this.dataForm.paramName7,
                paramName8: this.dataForm.paramName8,
                paramName9: this.dataForm.paramName9,
                paramName10: this.dataForm.paramName10,
                paramName11: this.dataForm.paramName11,
                paramName12: this.dataForm.paramName12,
                paramName13: this.dataForm.paramName13,
                paramName14: this.dataForm.paramName14,
                paramName15: this.dataForm.paramName15,
                loginSafeword: encrypt(this.dataForm.loginSafeword),
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
                    this.loading = false;
                  },
                });
              } else {
                this.$message({
                  message: data.msg,
                  type: "error",
                });
                this.loading = false;
              }
            });
          } else {
            console.log(this.id);
            this.$http({
              url: this.$http.adornUrl(`/paymentMethodConfig/add`), //新增
              method: "post",
              data: this.$http.adornData({
                type: 2, // 1.银行卡  2.c2c
                methodImg: this.dataForm.methodImg,
                methodName: this.dataForm.methodName,
                methodType: this.arr.id,
                paramName1: this.dataForm.paramName1,
                paramName2: this.dataForm.paramName2,
                paramName3: this.dataForm.paramName3,
                paramName4: this.dataForm.paramName4,
                paramName5: this.dataForm.paramName5,
                paramName6: this.dataForm.paramName6,
                paramName7: this.dataForm.paramName7,
                paramName8: this.dataForm.paramName8,
                paramName9: this.dataForm.paramName9,
                paramName10: this.dataForm.paramName10,
                paramName11: this.dataForm.paramName11,
                paramName12: this.dataForm.paramName12,
                paramName13: this.dataForm.paramName13,
                paramName14: this.dataForm.paramName14,
                paramName15: this.dataForm.paramName15,
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
                    this.loading = false;
                  },
                });
              } else {
                this.$message({
                  message: data.msg,
                  type: "error",
                });
                this.loading = false;
              }
            });
          }
        }
      });
    }),
  },
};
</script>
<style scoped>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
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
