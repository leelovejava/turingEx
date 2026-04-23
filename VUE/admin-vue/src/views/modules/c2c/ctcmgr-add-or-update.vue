<template>
  <el-dialog
    :title="!id ? '新增支付方式' : '修改支付方式'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close="close()"
    @open="open()"
  >
    <el-form
      :model="dataFormOne"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
    >
      <el-form-item label="用户名UID" prop="userCode">
        <el-input
          v-model="dataFormOne.userCode"
          :disabled="id ? true : false"
          placeholder="用户名UID"
        ></el-input>
      </el-form-item>
      <el-form-item v-if="id" label="用户名" prop="">
        <el-input
          v-model="dataForm.userName"
          disabled
          placeholder="用户名"
        ></el-input>
      </el-form-item>
      <el-form-item label="资金密码" prop="loginSafeword">
        <el-input
          v-model="dataFormOne.loginSafeword"
          type="password"
          placeholder="请输入登录人资金密码"
        ></el-input>
      </el-form-item>
      <el-form-item v-if="id" label="支付方式类型" prop="">
        <el-input
          v-model="dataForm.methodTypeName"
          disabled
          placeholder="支付方式类型"
        ></el-input>
      </el-form-item>
      <el-form-item v-if="!id" label="支付方式模板" prop="id">
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
      <el-form-item label="支付方式图片" prop="methodImg">
        <img
          :src="id ? dataForm.methodImgUrl : dataForm.methodImgUrl"
          alt=""
          width="100"
          height="100"
        />
      </el-form-item>
      <el-form-item label="支付方式名称" prop="">
        <el-input
          v-model="dataForm.methodName"
          disabled
          placeholder="支付方式名称"
        ></el-input>
      </el-form-item>
      <el-form-item label="真实姓名" prop="realName">
        <el-input
          v-model="dataFormOne.realName"
          placeholder="真实姓名"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="dataForm.paramName1"
        :label="dataForm.paramName1"
        prop=""
      >
        <el-input
          v-model="dataForm.paramValue1"
          placeholder="参数值1"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="dataForm.paramName2"
        :label="dataForm.paramName2"
        prop=""
      >
        <el-input
          v-model="dataForm.paramValue2"
          placeholder="参数值2"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="dataForm.paramName3"
        :label="dataForm.paramName3"
        prop=""
      >
        <el-input
          v-model="dataForm.paramValue3"
          placeholder="参数值3"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="dataForm.paramName4"
        :label="dataForm.paramName4"
        prop=""
      >
        <el-input
          v-model="dataForm.paramValue14"
          placeholder="参数值4"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="dataForm.paramName5"
        :label="dataForm.paramName5"
        prop=""
      >
        <el-input
          v-model="dataForm.paramValue5"
          placeholder="参数值5"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="dataForm.paramName6"
        :label="dataForm.paramName6"
        prop=""
      >
        <el-input
          v-model="dataForm.paramValue6"
          placeholder="参数值6"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="dataForm.paramName7"
        :label="dataForm.paramName7"
        prop=""
      >
        <el-input
          v-model="dataForm.paramValue7"
          placeholder="参数值7"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="dataForm.paramName8"
        :label="dataForm.paramName8"
        prop=""
      >
        <el-input
          v-model="dataForm.paramValue8"
          placeholder="参数值8"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="dataForm.paramName9"
        :label="dataForm.paramName9"
        prop=""
      >
        <el-input
          v-model="dataForm.paramValue9"
          placeholder="参数值9"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="dataForm.paramName10"
        :label="dataForm.paramName10"
        prop=""
      >
        <el-input
          v-model="dataForm.paramValue10"
          placeholder="参数值10"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="dataForm.paramName11"
        :label="dataForm.paramName11"
        prop=""
      >
        <el-input
          v-model="dataForm.paramValue11"
          placeholder="参数值11"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="dataForm.paramName12"
        :label="dataForm.paramName12"
        prop=""
      >
        <el-input
          v-model="dataForm.paramValue12"
          placeholder="参数值12"
        ></el-input>
      </el-form-item>

      <el-form-item
        v-if="dataForm.paramName13"
        :label="dataForm.paramName13"
        prop=""
      >
        <el-input
          v-model="dataForm.paramValue13"
          placeholder="参数值13"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="dataForm.paramName14"
        :label="dataForm.paramName14"
        prop=""
      >
        <el-input
          v-model="dataForm.paramValue14"
          placeholder="参数值14"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="dataForm.paramName15"
        :label="dataForm.paramName15"
        prop=""
      >
        <el-input
          v-model="dataForm.paramValue15"
          placeholder="参数值15"
        ></el-input>
      </el-form-item>
      <el-form-item label="支付二维码">
        <el-upload
          class="avatar-uploader"
          :action="$http.adornUrl('/api/uploadFile')"
          :headers="{ Authorization: $cookie.get('Authorization') }"
          :on-success="handleAvatarSuccess"
          :show-file-list="false"
          :before-upload="beforeAvatarUpload"
        >
          <img
            v-if="dataFormOne.imageUrl"
            :src="dataFormOne.imageUrl"
            class="avatar"
          />
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="dataForm.remark"
          type="textarea"
          placeholder="备注"
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
    return {
      visible: false,
      roleList: {},
      id: "",
      imageUrl: "",
      dataFormOne: {
        userCode: "",
        realName: "",
        remark: "",
        imageUrl: "",
        methodImg: "",
        userName: "",
        userId: "",
      },
      dataForm: {
        userId: "",
        userCode: "",
        realName: "",
        userName: "",
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
        ],
        userCode: [
          { required: true, message: "用户名UID不能为空", trigger: "blur" },
        ],
        loginSafeword: [
          { required: true, message: "资金密码不能为空", trigger: "blur" },
        ],
        value1: [
          { required: true, message: "请选择支付方式", trigger: "blur" },
        ],
        realName: [
          { required: true, message: "真实姓名不能为空", trigger: "blur" },
        ],
        // mobile: [
        //   { required: true, message: '手机号不能为空', trigger: 'blur' },
        //   { validator: validateMobile, trigger: 'blur' }
        // ]
      },
    };
  },
  created() {
    //this.getC2cPaymentMethodType()
  },
  methods: {
    init(id) {
      //this.resClear()
      this.id = id || "";
      //   console.log(id)
      //   if(id){
      //     this.getChangeMassge()
      //   }
      this.visible = true;
      this.$nextTick(() => {
        //this.$refs.dataForm.resetFields()
      });
    },
    resClear() {
      //this.arr.id='',
      (this.id = ""),
        (this.dataFormOne = {
          realName: "",
          remark: "",
          imageUrl: "",
          methodImg: "",
        }),
        (this.dataForm = {
          methodConfigId: "",
          methodTypeName: "",
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
        });
    },
    handleAvatarSuccess(res, file) {
      this.dataFormOne.methodImg = res.data.path;
      console.log(file);
      this.dataFormOne.imageUrl = URL.createObjectURL(file.raw);
      console.log(this.dataFormOne.imageUrl);
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
      console.log(val);
      this.getDesc(val);
      this.$forceUpdate();
    },
    getDesc(id) {
      //修改列表
      this.$http({
        url: this.$http.adornUrl("/paymentMethodConfig/getDesc"),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              id: id,
            },
            {
              prodName: this.dataForm.prodName,
            }
          )
        ),
      }).then(({ data }) => {
        this.dataForm = data.data;
      });
    },
    open() {
      if (this.id) {
        this.getChangeMassge();
      } else {
        this.getC2cPaymentMethodType();
      }
    },
    close() {
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.$data.dataFormOne = JSON.parse(
        JSON.stringify(this.$options.data().dataFormOne)
      );
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
    },
    // 获取详情数据列表
    getC2cPaymentMethodType() {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/paymentMethod/getPaymentMethodConfig"),
        method: "get",
        params: this.$http.adornParams({
          type:2,// 1.银行卡  2.c2c
        }
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.arr = Object.keys(data.data.methodConfigMap).map((item) => ({
            id: item,
            name: data.data.methodConfigMap[item],
          }));
          this.arr.id = this.arr[0].id;
          this.$nextTick(() => {
            this.getDesc(this.arr.id);
            this.dataFormOne.imageUrl = data.data.qrcodeImg;
            this.dataFormOne.methodImg = data.data.qrcode;
            this.dataFormOne.realName = data.data.realName;
            this.dataFormOne.userCode = data.data.userCode;
            //this.$refs.dataForm.resetFields()
          });
          console.log(this.arr);
        }
      });
    },
    getChangeMassge() {
      //修改详情
      this.$http({
        url: this.$http.adornUrl(`/paymentMethod/get`),
        method: "post",
        data: this.$http.adornData({
          id: this.id,
        }),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.dataForm = data.data;
          this.dataFormOne.imageUrl = data.data.qrcodeImg;
          this.dataFormOne.methodImg = data.data.qrcode;
          this.dataFormOne.realName = data.data.realName;
          this.dataFormOne.userCode = data.data.userCode;
          // console.log(data)
          //   this.$message({
          //   message: '操作成功',
          //   type: 'success',
          //   duration: 1500,
          //   onClose: () => {
          //     this.resClear()
          //     this.visible = false
          //     this.$emit('refreshDataList')
          //   }
          // })
        } else {
          this.$message({
            message: data.msg,
            type: "error",
          });
        }
      });
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          if (!this.id) {
            this.$http({
              url: this.$http.adornUrl(`/paymentMethod/add`), //新增
              method: "post",
              data: this.$http.adornData({
                type:2,// 1.银行卡  2.c2c
                loginSafeword: encrypt(this.dataFormOne.loginSafeword),
                methodConfigId: this.arr.id,
                qrcode: this.dataFormOne.methodImg,
                realName: this.dataFormOne.realName,
                remark: this.dataForm.remark,
                userCode: this.dataFormOne.userCode,
                paramValue1: this.dataForm.paramValue1,
                paramValue2: this.dataForm.paramValue2,
                paramValue3: this.dataForm.paramValue3,
                paramValue4: this.dataForm.paramValue4,
                paramValue5: this.dataForm.paramValue5,
                paramValue6: this.dataForm.paramValue6,
                paramValue7: this.dataForm.paramValue7,
                paramValue8: this.dataForm.paramValue8,
                paramValue9: this.dataForm.paramValue9,
                paramValue10: this.dataForm.paramValue10,
                paramValue11: this.dataForm.paramValue11,
                paramValue12: this.dataForm.paramValue12,
                paramValue13: this.dataForm.paramValue13,
                paramValue14: this.dataForm.paramValue14,
                paramValue15: this.dataForm.paramValue15,
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
                });
              }
            });
          } else {
            this.$http({
              url: this.$http.adornUrl(`/paymentMethod/update`), //修改
              method: "post",
              data: this.$http.adornData({
                id: this.id,
                loginSafeword: encrypt(this.dataFormOne.loginSafeword),
                methodConfigId: this.dataForm.methodConfigId,
                qrcode: this.dataFormOne.methodImg,
                realName: this.dataFormOne.realName,
                remark: this.dataForm.remark,
                userCode: this.dataFormOne.userCode,
                paramValue1: this.dataForm.paramValue1,
                paramValue2: this.dataForm.paramValue2,
                paramValue3: this.dataForm.paramValue3,
                paramValue4: this.dataForm.paramValue4,
                paramValue5: this.dataForm.paramValue5,
                paramValue6: this.dataForm.paramValue6,
                paramValue7: this.dataForm.paramValue7,
                paramValue8: this.dataForm.paramValue8,
                paramValue9: this.dataForm.paramValue9,
                paramValue10: this.dataForm.paramValue10,
                paramValue11: this.dataForm.paramValue11,
                paramValue12: this.dataForm.paramValue12,
                paramValue13: this.dataForm.paramValue13,
                paramValue14: this.dataForm.paramValue14,
                paramValue15: this.dataForm.paramValue15,
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
