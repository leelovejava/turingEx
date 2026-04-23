<template>
  <el-dialog
    :title="'更多信息'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    width="700px"
    @close="handClose"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="90px"
    >
      <el-form-item label="订单号" prop="">
        <el-input v-model="dataForm.order_no" disabled placeholder="订单号">
        </el-input>
      </el-form-item>
      <el-form-item label="创建时间" prop="">
        <el-input v-model="dataForm.create_time" disabled placeholder="创建时间">
        </el-input>
      </el-form-item>
      <el-form-item label="处理时间" prop="">
        <el-input v-model="dataForm.handle_time" disabled placeholder="处理时间">
        </el-input>
      </el-form-item>
      <el-form-item label="关闭时间" prop="">
        <el-input v-model="dataForm.close_time" disabled placeholder="关闭时间">
        </el-input>
      </el-form-item>
      <el-form-item label="支付时间" prop="">
        <el-input v-model="dataForm.pay_time" disabled placeholder="支付时间">
        </el-input>
      </el-form-item>
      <el-form-item label="取消时间" prop="">
        <el-input v-model="dataForm.cancel_time" disabled placeholder="取消时间">
        </el-input>
      </el-form-item>
      <el-form-item label="备注" prop="">
        <el-input v-model="dataForm.remark" disabled placeholder="备注">
        </el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关闭</el-button>
      <!-- <el-button type="primary" @click="dataFormSubmit()">确定</el-button> -->
    </span>
  </el-dialog>
</template>

<script>
import { Debounce } from "@/utils/debounce";
export default {
  data() {
    return {
      stock: "",
      visible: false,
      dialogFormVisible: true,
      paramsDelet: false,
      parameters: false,
      formLabelWidth: "120px",
      type: "", //type:'forex', //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
      menuList: [],
      options: [],
      menuListTreeProps: {
        label: "name",
        children: "children",
      },
      row: "",
      dataForm: {
        symbol: "",
      },
      dataRule: {},
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      dataList: [],
      data: {},
    };
  },
  created() {},
  components: {},
  methods: {
    init(row) {
      this.dataForm = { ...row };
      this.visible = true;
      // this.dialogFormVisible = false
    },
    handClose() {
      this.tableOption = {};
      // this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
      // this.$nextTick(() => {
      //     this.$refs['dataForm'].clearValidate() // 清除表单验证
      //   })
      // this.optionsTwo.value = ''
      // this.options.value = ''
    },

    changeVal(val) {
      this.$forceUpdate();
    },
    dataFormSubmit: Debounce(function () {}),
    // 条件查询
    searchChange(params, done) {
      this.getDataList(this.page, params, done);
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
    },
    handleAvatarSuccess(res, file) {
      this.dataForm.imageUrl = URL.createObjectURL(file.raw); //显示地址
      this.dataForm.imgUrl = res.data.path; //接口传递
      console.log(this.dataForm.imageUrl);
    },
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
.speaInputTwo {
  width: 250px;
}
.speaInputThtree {
  width: 120px;
}
.speaInputFive {
  width: 87px;
}
</style>

<style lang="scss" scoped>
::v-deep .el-dialog__body {
  padding: 0px 20px;
}

::v-deep .avue-crud__menu {
  height: auto;
  min-height: 0px;
}
</style>
