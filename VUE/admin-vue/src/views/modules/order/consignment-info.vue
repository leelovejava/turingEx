<template>
  <!-- 发货信息，用于导出代发货订单的excel交给快递公司 -->
  <el-dialog
    title="提现地址"
    :close-on-click-modal="false"
    :visible.sync="visible"
    width="38%"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
    >
      <el-form-item label="到账数量" prop="consignmentName">
        <el-input
          disabled
          v-model="dataForm.consignmentName"
          controls-position="right"
          label="到账数量"
        ></el-input>
      </el-form-item>
      <el-form-item label="提现地址" prop="consignmentMobile">
        <el-input
          disabled
          v-model="dataForm.consignmentMobile"
          controls-position="right"
          label="提现地址"
        ></el-input>
      </el-form-item>
      <el-form-item label="提现地址二维码" prop="consignmentMobile">
        <vue-qr
          :logo-src="logoSrc"
          :size="191"
          :margin="0"
          :auto-color="true"
          :dot-scale="1"
          :text="dataForm.appSrc"
          colorDark="black"
          colorLight="white"
        />
      </el-form-item>
      <el-form-item label="提现hash值" prop="consignmentAddr">
        <el-input
          disabled
          v-model="dataForm.consignmentAddr"
          controls-position="right"
          label="提现hash值"
        ></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button size="small" @click="visible = false">取消</el-button>
      <el-button type="primary" size="small" @click="dataFormSubmit()"
        >确定</el-button
      >
    </span>
  </el-dialog>
</template>
<script>
import VueQr from "vue-qr";
export default {
  data() {
    return {
      visible: false,
      logoSrc: "",
      dataForm: {
        consignmentName: "",
        consignmentMobile: "",
        consignmentAddr: "",
        appSrc: "",
      },
      dataRule: {
        // consignmentName: [
        //   { required: true, message: '不能为空', trigger: 'blur' }
        // ],
        // consignmentMobile: [
        //   { required: true, message: '不能为空', trigger: 'blur' }
        // ],
        // consignmentAddr: [
        //   { required: true, message: '不能为空', trigger: 'blur' }
        // ]
      },
    };
  },
  methods: {
    init(address) {
      this.dataForm.appSrc = address;
      this.visible = true;
      this.$nextTick(() => {
        //this.$refs['dataForm'].resetFields()
      });
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.visible = false;
          this.$emit("inputCallback", this.dataForm);
        }
      });
    },
  },
};
</script>
