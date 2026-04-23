<template>
  <!-- 查看地址二维码 -->
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
      label-width="120px"
    >
      <el-form-item label="到账数量" prop="volume">
        <el-input
          disabled
          v-model="dataForm.volume"
          controls-position="right"
          label="到账数量"
        ></el-input>
      </el-form-item>
      <el-form-item label="提现地址" prop="address">
        <el-input
          disabled
          v-model="dataForm.address"
          controls-position="right"
          label="提现地址"
        ></el-input>
      </el-form-item>
      <!-- <el-form-item label="提现地址二维码">
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
      </el-form-item> -->
      <el-form-item label="提现地址二维码" prop="img">
        <img :src="dataForm.qdcode" alt="" width="100" />
      </el-form-item>
      <!-- <el-form-item label="" prop="img">
        <a class="seachButton" :href="logoSrc" target="_blank">点击查看</a>
      </el-form-item> -->
      <el-form-item label="" prop="img">
        <div class="seachButton" @click="open(dataForm.qdcode)">点击查看</div>
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
        appSrc: "",
        volume:'',
        qdcode:'',
        address:'',
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
    init(volume,address,qdcode) {
      this.dataForm.appSrc = address;
      this.dataForm.address = address;
      this.dataForm.volume = volume;
      this.dataForm.qdcode = qdcode;
      this.visible = true;
      this.$nextTick(() => {
        //this.$refs['dataForm'].resetFields()
      });
    },
    open(img) {
      console.log(img)
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
