<template>
  <el-dialog
    :title="'取消订单'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    :append-to-body="true"
    width="700px"
    @close="handClose"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width=""
    >
    <el-form-item label="请填写取消理由" prop="remark" label-width="150px">
      </el-form-item>
      <el-form-item label="" prop="remark">
        <el-input
          type="textarea"
          placeholder="请输入内容"
          v-model="dataForm.remark"
          maxlength="500"
          :autosize="{ minRows: 8, maxRows: 8 }"
          show-word-limit
        >
        </el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关闭</el-button>
      <el-button type="primary" @click="dataFormSubmit()">取消订单</el-button>
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
        // 表单提交
        dataFormSubmit: Debounce(function () {
        this.$refs['dataForm'].validate((valid) => {
        if (!valid) {
          return
        }
        this.$http({
          url: this.$http.adornUrl(`/c2cOrder/orderCancel`),
          method:'post',
          data: this.$http.adornData({
            remark:this.dataForm.remark,
            order_no:this.dataForm.order_no,
          })
        }).then(({ data }) => {
          if(data.code==0){
            this.$message({
            message: '取消订单成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              this.visible = false
              this.$emit('refreshDataList', this.page)
            }
          })
          }else{
            this.$message({
            message: data.msg,
            type: 'error',
            duration: 1500,
            onClose: () => {
            }
          })
          }

        })
      })
    }),
    // 条件查询
    searchChange(params, done) {
      this.getDataList(this.page, params, done);
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
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
