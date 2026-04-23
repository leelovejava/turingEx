<template>
  <el-dialog
    :title="'支付方式'"
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
      label-width="110px"
    >
    <el-form-item label="支付方式名称" prop="">
        <el-input v-model="dataForm.method_type_name" disabled
                  placeholder="支付方式名称">
        </el-input>
      </el-form-item>
      <el-form-item label="支付方式图片" prop="">
        <img :src="dataForm.method_img" alt="" width="150">
      </el-form-item>
      <el-form-item label="真实姓名" prop="">
        <el-input v-model="dataForm.real_name" disabled
                  placeholder="真实姓名">
        </el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.param_name1" :label=dataForm.param_name1 prop="">
        <el-input v-model="dataForm.param_value1" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.param_name2" :label=dataForm.param_name2 prop="">
        <el-input v-model="dataForm.param_value2" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.param_name3" :label=dataForm.param_name3 prop="">
        <el-input v-model="dataForm.param_value3" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.param_name4" :label=dataForm.param_name4 prop="">
        <el-input v-model="dataForm.param_value4" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.param_name5" :label=dataForm.param_name5 prop="">
        <el-input v-model="dataForm.param_value5" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.param_name6" :label=dataForm.param_name6 prop="">
        <el-input v-model="dataForm.param_value6" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.param_name7" :label=dataForm.param_name7 prop="">
        <el-input v-model="dataForm.param_value7" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.param_name8" :label=dataForm.param_name8 prop="">
        <el-input v-model="dataForm.param_value8" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.param_name9" :label=dataForm.param_name9 prop="">
        <el-input v-model="dataForm.param_value9" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.param_name10" :label=dataForm.param_name10 prop="">
        <el-input v-model="dataForm.param_value10" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.param_name11" :label=dataForm.param_name11 prop="">
        <el-input v-model="dataForm.param_value11" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.param_name12" :label=dataForm.param_name12 prop="">
        <el-input v-model="dataForm.param_value12" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.param_name13" :label=dataForm.param_name13 prop="">
        <el-input v-model="dataForm.param_value13" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.param_name14" :label=dataForm.param_name14 prop="">
        <el-input v-model="dataForm.param_value14" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.param_name15" :label=dataForm.param_name15 prop="">
        <el-input v-model="dataForm.param_value15" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item label="支付二维码" prop="">
        <img :src="dataForm.qrcode" alt="" width="200">
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
      roleList:{},
      type: "", //type:'forex', //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
      menuList: [],
      options: [],
      menuListTreeProps: {
        label: "name",
        children: "children",
      },
      row: "",
      id:'',
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
      this.row = row || '';
      this.dataForm = {...row}
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

.langAllBox {
  overflow: hidden;
  margin: 10px 0;
}
.lanOneBox {
  overflow: hidden;
  margin-bottom: 20px;
}
.lanInput {
  width: 70%;
}
.lanSpan,
.lanInput,
.lanButton {
  float: left;
}
.lanSpan {
  width: 100px;
  margin-right: 3%;
}
.lanInput {
  margin-right: 2%;
}
.speaInput {
  margin-right: 3%;
}
.lanOneBox {
  line-height: 31px;
}
.nasspan{
  width: 100px;
  margin-right: 3%;
}
</style>
