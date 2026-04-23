<template>
  <el-dialog
    :title="'限价提交'"
    :close-on-click-modal="false"
    @close = 'handClose'
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="120px">
      <el-form-item label="委托数量" prop="symbolValue">
       <div class="red">{{dataForm.symbolValue}}</div>
      </el-form-item>
      <!-- <el-form-item label="已成交委托数量" prop="volume">
       <div class="red">{{dataForm.successVolume}}</div>
      </el-form-item> -->
      <!-- <el-form-item label="剩余委托数量" prop="volume">
       <div class="green">{{dataForm.lastVprice}}</div>
      </el-form-item> -->
      <el-form-item label="当前成交数量" prop="number">
        <el-input v-model="dataForm.number" type="number" placeholder="委托总数"></el-input>
      </el-form-item>
      <el-form-item label="登录人资金密码" prop="loginSafeword">
        <el-input v-model="dataForm.loginSafeword" type="password" placeholder="登录人资金密码"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  import { treeDataTranslate } from '@/utils'
  import { encrypt } from '@/utils/crypto'
  import { Debounce } from '@/utils/debounce'
  export default {
    data () {
      let validatenumber = (rule, value, callback) => {
      if (value <=  0) {
        callback(new Error('成交数量不能小于0'))
      } else if(value>this.dataForm.symbolValue) {
        
          callback(new Error('成交数量不能大于委托数量'))

      }else {
          callback()
        }
    }
      return {
        visible: false,
        menuList: [],
        menuListTreeProps: {
          label: 'name',
          children: 'children'
        },
        dataForm: {
          orderNo: '',
          loginSafeword: '',
          symbolValue:'',
          number:'',
          lastVprice:'',//剩余委托数量
          successVolume:''//成交委托数量
        },
        dataRule: {
          loginSafeword: [
            { required: true, message: '资金密码不能为空', trigger: 'blur' },
          ],
          number: [
            { required: true, message: '成交数量不能为空', trigger: 'blur' },
            { validator: validatenumber, trigger: 'blur' }
          ],
          remark: [
            { required: false, pattern: /\s\S+|S+\s|\S/, message: '输入格式有误', trigger: 'blur' }
          ]
        },
        tempKey: -666666 // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
      }
    },
    methods: {
      init (id,symbolValue ) {
       this.dataForm.orderNo = id || ''
       this.dataForm.symbolValue = symbolValue || ''
      //  this.dataForm.successVolume = successVolume
      //  this.dataForm.lastVprice = (this.dataForm.volume*10000 -  this.dataForm.successVolume*10000)/10000
       this.dataForm.number = this.dataForm.symbolValue
       this.visible = true
      },
      // 表单提交
      dataFormSubmit: Debounce(function () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/exchangeApplyOrder/success`),
              method: 'post',
              data: this.$http.adornData({
                'orderNo': this.dataForm.orderNo,
                'loginSafeword': encrypt(this.dataForm.loginSafeword),
                'number':this.dataForm.number,
              })
            }).then(({data}) => {
              if(data.code == 0){
                this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.visible = false
                  this.$emit('refreshDataList')
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
      handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
     this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
      },
    }
  }
</script>
