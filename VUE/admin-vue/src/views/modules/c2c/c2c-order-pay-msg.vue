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
      label-width="90px"
    >
      <!-- 修改多语言 -->
      <template >
        <div class="langAllBox">
          <div class="lanOneBox">
            <span class="lanSpan speaInput">{{ "支付方式类型 " }}</span>
            <el-input
              class="lanInput"
              v-model="roleList.method_type_name"
              disabled
            ></el-input>
          </div>
          <div class="lanOneBox">
            <span class="lanSpan speaInput">{{ "支付方式名称 " }}</span>
            <el-input
              class="lanInput"
              v-model="roleList.method_name"
              disabled
            ></el-input>
          </div>
          <div class="lanOneBox">
            <span class="lanSpan">{{ "支付方式图片" }}</span>
            <img :src="roleList.method_img" width="100" alt="">     
          </div>
          <div class="lanOneBox">
            <span class="lanSpan speaInput">{{ "真实姓名 " }}</span>
            <el-input
              class="lanInput"
              v-model="roleList.real_name"
              disabled
            ></el-input>
          </div>
          <div class="lanOneBox">
            <span class="lanSpan">{{ "支付二维码" }}</span>
            <img :src="roleList.qrcode" width="100" alt="">     
          </div>
          <!-- 参数1 -->
          <div v-if="roleList.param_name1" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput">{{roleList.param_name1 }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.param_value1"
                disabled
              ></el-input>
            </div>
          </div>
          <!-- 参数2 -->
          <div v-if="roleList.param_name2" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ roleList.param_name2}}</span>
              <el-input
                class="lanInput"
                v-model="roleList.param_value2"
                disabled
              ></el-input>
            </div>
          </div>
          <!-- 参数3 -->
          <div v-if="roleList.param_name3" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{roleList.param_name3}}</span>
              <el-input
                class="lanInput"
                v-model="roleList.param_value3"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数4 -->
                    <div v-if="roleList.param_name4" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ roleList.param_name4 }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.param_value4"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数5 -->
                    <div v-if="roleList.param_name5" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{roleList.param_name5}}</span>
              <el-input
                class="lanInput"
                v-model="roleList.param_value5"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数6 -->
                    <div v-if="roleList.param_name6" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ roleList.param_name6}}</span>
              <el-input
                class="lanInput"
                v-model="roleList.param_value6"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数7 -->
                    <div v-if="roleList.param_name7" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ roleList.param_name7}}</span>
              <el-input
                class="lanInput"
                v-model="roleList.param_value7"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数8 -->
                    <div v-if="roleList.param_name8" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ roleList.param_name8}}</span>
              <el-input
                class="lanInput"
                v-model="roleList.param_value8"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数9 -->
          <div v-if="roleList.param_name9" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{roleList.param_name9}}</span>
              <el-input
                class="lanInput"
                v-model="roleList.param_value9"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数10 -->
            <div v-if="roleList.param_name10LangTrans !== null&& roleList.param_name10" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ roleList.param_name10}}</span>
              <el-input
                class="lanInput"
                v-model="roleList.param_value10"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数11 -->
          <div v-if="roleList.param_name11" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{roleList.param_name11}}</span>
              <el-input
                class="lanInput"
                v-model="roleList.param_value11"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数12 -->
                    <div v-if="roleList.param_name12" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{roleList.param_name12}}</span>
              <el-input
                class="lanInput"
                v-model="roleList.param_value12"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数13 -->
                    <div v-if="roleList.param_name13" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{roleList.param_name13}}</span>
              <el-input
                class="lanInput"
                v-model="roleList.param_value13"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数14 -->
                    <div v-if="roleList.param_name14" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{roleList.param_name14}}</span>
              <el-input
                class="lanInput"
                v-model="roleList.param_value14"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数15 -->
            <div v-if="roleList.param_name15" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{roleList.param_name15}}</span>
              <el-input
                class="lanInput"
                v-model="roleList.param_value15"
                disabled
              ></el-input>
            </div>
          </div>
        </div>
      </template>
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
    init(id) {
      console.log(id)
      this.id = id;
      this.getC2cManagerInfo()
      this.getOrderPayments()
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
    getC2cManagerInfo() {
      //获取详情
      this.$http({
        url: this.$http.adornUrl("/c2cOrder/detail"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              order_no:this.id
            },
          )
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.roleList = data.data;
        }
      });
    },
    getOrderPayments() {
      //获取详情
      this.$http({
        url: this.$http.adornUrl("/c2cOrder/getOrderPayments"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              order_no:this.id
            },
          )
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          
        }
      });
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
