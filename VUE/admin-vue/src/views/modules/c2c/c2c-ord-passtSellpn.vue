<template>
  <el-dialog
    title="手动转账"
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
      label-width="130px"
      label-position="left"
    >
    <el-form-item label="选择用户支付方式" prop="id">
        <el-select
          v-model="uuid"
          class="speacSelect"
          @change="changeVal"
          placeholder="请选择"
        >
          <el-option
            v-for="item in payments"
            :key="item.uuid"
            :label="item.methodName + '['+item.paramValue1+']'"
            :value="item.uuid"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <div v-for="(item,index) in payments" :key="index" v-show="item.uuid == uuid">
        <el-form-item label="支付方式类型" prop="">
        <el-input v-model="item.methodTypeName" disabled
                  placeholder="支付方式类型">
        </el-input>
      </el-form-item>
      <el-form-item label="支付方式名称" prop="">
        <el-input v-model="item.methodName" disabled
                  placeholder="支付方式名称">
        </el-input>
      </el-form-item>
      <el-form-item label="支付方式图片" prop="">
        <img :src="item.methodImg" alt="" width="100">
      </el-form-item>
      <el-form-item label="真实姓名" prop="">
        <el-input v-model="item.realName" disabled
                  placeholder="真实姓名">
        </el-input>
      </el-form-item>
      <el-form-item :label="item.paramName1" v-if ='item.paramValue1' prop="">
        <el-input v-model="item.paramValue1" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item :label="item.paramName2" v-if ='item.paramValue2' prop="">
        <el-input v-model="item.paramValue2" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item :label="item.paramName3" v-if ='item.paramValue3' prop="">
        <el-input v-model="item.paramValue3" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item :label="item.paramName4" v-if ='item.paramValue4' prop="">
        <el-input v-model="item.paramValue4" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item :label="item.paramName5" v-if ='item.paramValue5' prop="">
        <el-input v-model="item.paramValue5" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item :label="item.paramName6" v-if ='item.paramValue6' prop="">
        <el-input v-model="item.paramValue6" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item :label="item.paramName7" v-if ='item.paramValue7' prop="">
        <el-input v-model="item.paramValue7" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item :label="item.paramName8" v-if ='item.paramValue8' prop="">
        <el-input v-model="item.paramValue8" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item :label="item.paramName9" v-if ='item.paramValue9' prop="">
        <el-input v-model="item.paramValue9" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item :label="item.paramName10" v-if ='item.paramValue10' prop="">
        <el-input v-model="item.paramValue10" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item :label="item.paramName11" v-if ='item.paramValue11' prop="">
        <el-input v-model="item.paramValue11" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item :label="item.paramName12" v-if ='item.paramValue12' prop="">
        <el-input v-model="item.paramValue12" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item :label="item.paramName13" v-if ='item.paramValue13' prop="">
        <el-input v-model="item.paramValue13" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item :label="item.paramName14" v-if ='item.paramValue14' prop="">
        <el-input v-model="item.paramValue14" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      <el-form-item :label="item.paramName15" v-if ='item.paramValue15' prop="">
        <el-input v-model="item.paramValue15" disabled
                  placeholder="">
        </el-input>
      </el-form-item>
      </div>
      <el-form-item label="支付金额" prop="">
        <el-input v-model="dataForm.amount" disabled
                  placeholder="支付金额">
                  <template v-slot:append>
                <span>{{dataForm.currency}}</span>
              </template>
        </el-input>
      </el-form-item>
      <el-form-item label="到账数量" prop="">
        <el-input v-model="dataForm.coin_amount" disabled
                  placeholder="到账数量">
                  <template v-slot:append>
                <span>{{dataForm.symbol}}</span>
              </template>
        </el-input>
      </el-form-item>
      <el-form-item label-position="left" label="登录人资金密码" prop="safeword">
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
      payments:[],
      order_no:'',
      methodConfigId:'',
      type: "", //type:'forex', //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
      menuList: [],
      options: [],
      row: "",
      uuid:'',
      dataForm: {
        safeword: "",
        amount:'',
        coin_amount:'',
        currency:'',
        symbol:'',
      },
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
      data: {},
    };
  },
  created() {},
  components: {},
  methods: {
    init(row) {
      this.order_no = row.order_no
      this.dataForm.amount = row.amount
      this.dataForm.coin_amount = row.coin_amount
      this.dataForm.currency = row.currency
      this.dataForm.symbol = row.symbol
      //this.dataForm = { ...row };
      this.getOrderPayments()
      this.visible = true;
      // this.dialogFormVisible = false
    },
    handClose() {
      //this.tableOption = {};
      this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
      this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate() // 清除表单验证
        })
      // this.optionsTwo.value = ''
      // this.options.value = ''
    },

    changeVal(val) {
      //console.log(val)
      this.$forceUpdate(); 
      //this.uid = data.data
    },
    getOrderPayments(){
      this.$http({
          url: this.$http.adornUrl(`/c2cOrder/getOrderPayments`),
          method:'post',
          data: this.$http.adornData({
            //safeword:encrypt(this.dataForm.safeword),
            order_no:this.order_no,
          })
        }).then(({ data }) => {
          if(data.code==0){
            this.payments = data.data.payments
            this.uuid=this.payments[0].uuid
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
    },
        // 表单提交
        dataFormSubmit: Debounce(function () {
        this.$refs['dataForm'].validate((valid) => {
        if (!valid) {
          return
        }
        this.$http({
          url: this.$http.adornUrl(`/c2cOrder/orderPay`),
          method:'post',
          data: this.$http.adornData({
            safeword:encrypt(this.dataForm.safeword),
            order_no:this.order_no,
            payment_method_id_order_pay:this.uuid
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
      })
    }),
  },
};
</script>

<style scoped>

.speacSelect{
  width: 530px;
}
</style>
