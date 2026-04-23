<template>
  <!-- 发货信息，用于导出代发货订单的excel交给快递公司 -->
  <el-dialog
    title="充值凭证"
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
      <el-form-item label="充值数量" prop="channelAmount">
        <el-input
          disabled
          v-model="dataForm.channelAmount"
          controls-position="right"
          label="充值数量"
        ></el-input>
      </el-form-item>
      <el-form-item label="用户地址" prop="channelAddress">
        <el-input
          disabled
          v-model="dataForm.channelAddress"
          controls-position="right"
          label="用户地址"
        ></el-input>
      </el-form-item>
      <el-form-item label="充值截图" prop="img">
        <img :src="dataForm.img" alt="" width="100" />
      </el-form-item>
      <el-form-item label="" prop="img">
        <div class="seachButton" @click="open(dataForm.img)">点击查看</div>
      </el-form-item>
      <el-form-item label="充值hash" prop="consignmentAddr">
        <el-input
          disabled
          v-model="dataForm.consignmentAddr"
          controls-position="right"
          label="充值hash"
        ></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button size="small" @click="visible = false">取消</el-button>
      <el-button type="primary" size="small" @click="visible = false"
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
        channelAmount: "",
        channelAddress: "",
        img: "",
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
    init(row) {
      console.log(row);
      this.dataForm.channelAmount = row.channelAmount;
      this.dataForm.channelAddress = row.channelAddress;
      this.dataForm.img = row.img;
      //this.dataForm.appSrc = address;
      this.visible = true;
      this.$nextTick(() => {
        //this.$refs['dataForm'].resetFields()
      });
    },
    open(img) {
      const image = new Image();
      image.src = img;
      const newWindow = window.open();
      newWindow.document.body.appendChild(image);
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
