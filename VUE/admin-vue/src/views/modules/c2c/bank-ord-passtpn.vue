<template>
  <el-dialog
    :title="n?'手动到账':'手动放行'"
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
      label-width="200"
      label-position="top"
    >
    <el-form-item v-if="n" label="用户支付方式" >
            <el-select class="speaInputTwo" v-model="payment_method_id_order_pay" placeholder="请选择" @change="changeVal()">
              <el-option v-for="item in roleList" :key="item.uuid" :label="item.methodName+'['+item.paramValue1+']'" :value="item.uuid">
              </el-option>
            </el-select>
    </el-form-item>
      <el-form-item v-if="n" label="支付方式名称" prop="method_type_name">
        <el-input v-model="dataForm.method_type_name" disabled 
                  placeholder="支付方式名称">
        </el-input>
      </el-form-item>
      <el-form-item v-if="n" label="支付方式图片" prop="method_img">
        <img :src="dataForm.method_img" width="100" alt="">
      </el-form-item>
      <el-form-item v-if="n" label="真实姓名" prop="real_name">
        <el-input v-model="dataForm.real_name" disabled 
                  placeholder="真实姓名">
        </el-input>
      </el-form-item>
       <!-- 参数1 -->
       <div v-if="dataForm.param_name1 && n" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput">{{dataForm.param_name1 }}</span>
              <el-input
                class="lanInput"
                v-model="dataForm.param_value1"
                disabled
              ></el-input>
            </div>
          </div>
          <!-- 参数2 -->
          <div v-if="dataForm.param_name2 && n" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ dataForm.param_name2}}</span>
              <el-input
                class="lanInput"
                v-model="dataForm.param_value2"
                disabled
              ></el-input>
            </div>
          </div>
          <!-- 参数3 -->
          <div v-if="dataForm.param_name3 && n" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{dataForm.param_name3}}</span>
              <el-input
                class="lanInput"
                v-model="dataForm.param_value3"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数4 -->
                    <div v-if="dataForm.param_name4 && n" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ dataForm.param_name4 }}</span>
              <el-input
                class="lanInput"
                v-model="dataForm.param_value4"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数5 -->
                    <div v-if="dataForm.param_name5 && n" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{dataForm.param_name5}}</span>
              <el-input
                class="lanInput"
                v-model="dataForm.param_value5"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数6 -->
                    <div v-if="dataForm.param_name6 && n" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ dataForm.param_name6}}</span>
              <el-input
                class="lanInput"
                v-model="dataForm.param_value6"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数7 -->
                    <div v-if="dataForm.param_name7 && n" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ dataForm.param_name7}}</span>
              <el-input
                class="lanInput"
                v-model="dataForm.param_value7"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数8 -->
                    <div v-if="dataForm.param_name8 && n" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ dataForm.param_name8}}</span>
              <el-input
                class="lanInput"
                v-model="dataForm.param_value8"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数9 -->
          <div v-if="dataForm.param_name9 && n" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{dataForm.param_name9}}</span>
              <el-input
                class="lanInput"
                v-model="dataForm.param_value9"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数10 -->
            <div v-if="dataForm.param_name10 && n" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ dataForm.param_name10}}</span>
              <el-input
                class="lanInput"
                v-model="dataForm.param_value10"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数11 -->
          <div v-if="dataForm.param_name11 && n" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{dataForm.param_name11}}</span>
              <el-input
                class="lanInput"
                v-model="dataForm.param_value11"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数12 -->
                    <div v-if="dataForm.param_name12 && n" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{dataForm.param_name12}}</span>
              <el-input
                class="lanInput"
                v-model="dataForm.param_value12"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数13 -->
                    <div v-if="dataForm.param_name13 && n" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{dataForm.param_name13}}</span>
              <el-input
                class="lanInput"
                v-model="dataForm.param_value13"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数14 -->
                    <div v-if="dataForm.param_name14 && n" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{dataForm.param_name14}}</span>
              <el-input
                class="lanInput"
                v-model="dataForm.param_value14"
                disabled
              ></el-input>
            </div>
          </div>
                    <!-- 参数15 -->
            <div v-if="dataForm.param_name15 && n" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{dataForm.param_name15}}</span>
              <el-input
                class="lanInput"
                v-model="dataForm.param_value15"
                disabled
              ></el-input>
            </div>
          </div>
          <el-form-item v-if="n" label="支付金额" prop="">
        <el-input v-model="dataForm.amount" disabled
                  placeholder="支付金额">
        </el-input>
      </el-form-item>
      <el-form-item  v-if="n" label="到账数量" prop="">
        <el-input v-model="dataForm.coin_amount" disabled
                  placeholder="到账数量">
        </el-input>
      </el-form-item>
      <el-form-item label="登录人资金密码" prop="safeword">
        <el-input v-model="dataForm.safeword" type="password"
                  placeholder="登录人资金密码">
        </el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关闭</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { Debounce } from "@/utils/debounce";
import { encrypt } from '@/utils/crypto'
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
        safeword: "",
      },
      payment_method_id_order_pay:'',
      n:'',
      dataRule: {
        safeword:[
          { required: true, message: '资金密码不能为空', trigger: 'blur' },
          ],
      },
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      dataList: [],
      roleList:[],
      data: {},
    };
  },
  created() {},
  components: {},
  methods: {
    init(row,n) {
      this.n = n
      this.dataForm = { ...row };
      if(n){
        this.getOrderPayments()
      }
      this.visible = true;
      // this.dialogFormVisible = false
    },
    handClose() {
      this.tableOption = {};
      this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
      this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate() // 清除表单验证
        })
        this.payment_method_id_order_pay  = ''
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
        if(this.n){
          this.$http({ // 手动到账
          // url: this.$http.adornUrl(`/bankCardOrder/orderPay`),
          url: this.$http.adornUrl(`/bankCardOrder/orderPass`),
          method:'post',
          data: this.$http.adornData({
            //payment_method_id_order_pay:this.payment_method_id_order_pay,
            safeword:encrypt(this.dataForm.safeword),
            orderNo:this.dataForm.order_no,
            // order_no:this.dataForm.order_no,
          })
        }).then(({ data }) => {
          if(data.code==0){
            this.$message({
            message: '操作成功',
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
        }else{
          this.$http({ // 手动放行
          url: this.$http.adornUrl(`/bankCardOrder/orderPass`),
          method:'post',
          data: this.$http.adornData({
            safeword:encrypt(this.dataForm.safeword),
            orderNo:this.dataForm.order_no,
          })
        }).then(({ data }) => {
          if(data.code==0){
            this.$message({
            message: '操作成功',
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
        }

      })
    }),
    getOrderPayments() {
      //获取 银行卡订单的支付方式
      this.$http({
        url: this.$http.adornUrl("/bankCardOrder/getOrderPayments"),
        method: "get",
        params: this.$http.adornParams(Object.assign({
          order_no:this.dataForm.order_no
        })),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.roleList = data.data
          this.payment_method_id_order_pay = this.roleList[0].uuid
        }
      });
    },
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
  width: 658px;
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
.langAllBox{
  margin-top: 20px;
}
</style>
