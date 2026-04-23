<template>
  <el-dialog
    title="详细信息"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close="handClose"
    width="1200px"
    class="transport-dialog"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="140px"
    >
      <el-form-item label="真实姓名" prop="">
        <el-input v-model="dataForm.realName" disabled placeholder="真实姓名">
        </el-input>
      </el-form-item>
      <el-form-item label="实名认证证件照" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="证件正面照" prop="">
           <img :src='dataForm.idcardPathFront' @click="open(dataForm.idcardPathFront)" alt="" width="200">
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="证件背面照" prop="">
           <img :src='dataForm.idcardPathBack' @click="open(dataForm.idcardPathBack)" alt="" width="200">
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="手持正面照" prop="">
           <img :src='dataForm.idcardPathHold'  @click="open(dataForm.idcardPathHold)" alt="" width="200">
          </el-form-item>
        </el-col>
      </el-row>
      <!-- <el-form-item label="重置申请证件照" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="证件正面照" prop="">
           <img :src='dataForm.idcardPathFront' alt="" width="100">
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="证件背面照" prop="">
           <img :src='dataForm.idcardPathBack' alt="" width="100">
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="手持正面照" prop="">
           <img :src='dataForm.idcardPathHold' alt="" width="100">
          </el-form-item>
        </el-col>
      </el-row> -->
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="visible = false">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { Debounce } from "@/utils/debounce";
import { encrypt } from "@/utils/crypto";
export default {
  data() {
    return {
      hasFreeCondition: 0,
      visible: false,
      addOrUpdateVisible: false,
      dataForm: {
        id: "",
      },
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      dataRule: {
        login_safeword: [
          { required: true, message: "资金密码不能为空", trigger: "blur" },
        ],
      },
    };
  },
  methods: {
    init(row) {
      console.log(row);
      this.dataForm = { ...row };
      this.visible = true;
      this.$nextTick(() => {});
    },
    handClose() {
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
    },
    open(img) {
      const image = new Image();
      image.src = img;
      const newWindow = window.open();
      newWindow.document.body.appendChild(image);
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          const formData = new FormData();
          const objDate = { ...this.dataForm };
          // 添加需要提交的表单字段及其值到 FormData 对象
          for (let key in objDate) {
            if (key == "login_safeword") {
              console.log(objDate[key]);
              objDate[key] = encrypt(objDate[key]);
            }
            formData.append(key, objDate[key]);
          }
          // 发送表单数据到后台接口
          this.$http({
            url: this.$http.adornUrl(`/c2cAdvert/backAllDeposit`),
            method: "post",
            data: formData,
            headers: {
              "Content-Type": "multipart/form-data", // 设置请求头为 multipart/form-data
            },
          })
            .then((response) => {
              if (response.data.code == 0) {
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
                  message: response.data.msg,
                  type: "error",
                  duration: 1500,
                  onClose: () => {},
                });
              }
            })
            .catch((error) => {});
        }
      });
    }),
  },
};
</script>

<style lang="scss" scoped>
.transport-dialog .table-con .el-form-item {
  margin-top: 16px;
  margin-bottom: 16px !important;
}
</style>
