<template>
  <el-dialog
    :title="type=='n'? '人工充值' : '驳回'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule"  ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item v-if="type=='n'" label="原始币种" prop="coin">
        <el-input v-model="dataForm.coin" disabled placeholder="用户充值币种"></el-input>
      </el-form-item>
      <el-form-item v-if="type=='n'" label="到账币种" prop="targetCoin">
        <el-select v-model="dataForm.targetCoin" @change="onTargetCoinChange" style="width:100%">
          <el-option label="BTC" value="BTC"></el-option>
          <el-option label="ETH" value="ETH"></el-option>
          <el-option label="USDT" value="USDT"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item v-if="type=='n'" label="充值数量" prop="amount">
        <el-input v-model="dataForm.amount" type="number" placeholder="充值币种数量">
          <template slot="append">{{ dataForm.targetCoin }}</template>
        </el-input>
      </el-form-item>
      <el-form-item v-if="type=='n' && rateInfo" label="汇率参考">
        <span style="color:#909399;font-size:12px">{{ rateInfo }}</span>
      </el-form-item>
      <el-form-item v-if="type=='n'" label="资金密码"  prop="safePasssword">
        <el-input v-model="dataForm.safePasssword" type="password" placeholder="资金密码"></el-input>
      </el-form-item>
      <el-form-item v-if="type=='m'" label="驳回原因"  prop="content">
        <el-input type="textarea" v-model="dataForm.content"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  import { isEmail, isMobile } from '@/utils/validate'
  import { Debounce } from '@/utils/debounce'
  import { encrypt } from '@/utils/crypto'
  export default {
    data () {
      var validatePassword = (rule, value, callback) => {
        if (!this.dataForm.id && !/\S/.test(value)) {
          callback(new Error('密码不能为空'))
        } else {
          callback()
        }
      }
      var validateEmail = (rule, value, callback) => {
        if (!isEmail(value)) {
          callback(new Error('邮箱格式错误'))
        } else {
          callback()
        }
      }
      var validateAmount = (rule, value, callback) => {
        if (value<0) {
          callback(new Error('充值数量不能小于0'))
        } else {
          callback()
        }
      }
      return {
        visible: false,
        id:'',
        type:'',//n.手动转   m驳回
        rateInfo: '',
        originalAmount: '',
        dataForm: {
          amount: '',
          safePasssword: '',
          coin:'',
          targetCoin:'',
          content:'',
        },
        dataRule: {
          amount: [
            { required: true, message: '充值数量不能为空', trigger: 'blur' },
            { validator: validateAmount, trigger: 'blur' }
          ],
          safePasssword: [
          { required: true, message: '资金密码不能为空', trigger: 'blur' },
          ],
          // email: [
          //   { required: true, message: '邮箱不能为空', trigger: 'blur' },
          //   { validator: validateEmail, trigger: 'blur' }
          // ],
          // mobile: [
          //   { required: true, message: '手机号不能为空', trigger: 'blur' },
          //   { validator: validateMobile, trigger: 'blur' }
          // ]
        }
      }
    },
    methods: {
      init (type,id,coin,channelAmount) {
        this.resClear()
        this.id = id || ''
        this.type = type || ''
        this.originalAmount = channelAmount || ''
        this.dataForm.amount = channelAmount
        this.dataForm.coin = coin || ''
        this.dataForm.targetCoin = coin ? coin.toUpperCase() : 'USDT'
        this.rateInfo = ''
        this.visible = true
      },
      resClear(){
        this.dataForm = {
          amount: '',
          safePasssword: '',
          coin:'',
          targetCoin:'',
          content:'',
        }
        this.rateInfo = ''
        this.originalAmount = ''
      },
      onTargetCoinChange(targetCoin) {
        const sourceCoin = (this.dataForm.coin || '').toUpperCase()
        if (!sourceCoin || !this.originalAmount) return
        if (sourceCoin === targetCoin) {
          this.dataForm.amount = this.originalAmount
          this.rateInfo = ''
          return
        }
        // 查询两个币种的 USDT 汇率
        this.$http({
          url: this.$http.adornUrl('/rate/exchangeRate/list'),
          method: 'get',
          params: this.$http.adornParams({ current: 1, size: 100 })
        }).then(({ data }) => {
          if (data.code !== 0) return
          const list = data.data.records || data.data || []
          const getRate = (coin) => {
            const item = list.find(r => r.name && r.name.toUpperCase() === coin)
            return item ? parseFloat(item.rata) : null
          }
          // 若源币种是 USDT，直接用目标汇率换算
          let convertedAmount
          if (sourceCoin === 'USDT') {
            const targetRate = getRate(targetCoin)
            if (!targetRate) { this.$message.error('未找到 ' + targetCoin + ' 汇率'); return }
            convertedAmount = (parseFloat(this.originalAmount) / targetRate).toFixed(8)
            this.rateInfo = `1 ${targetCoin} ≈ ${targetRate} USDT`
          } else if (targetCoin === 'USDT') {
            const sourceRate = getRate(sourceCoin)
            if (!sourceRate) { this.$message.error('未找到 ' + sourceCoin + ' 汇率'); return }
            convertedAmount = (parseFloat(this.originalAmount) * sourceRate).toFixed(2)
            this.rateInfo = `1 ${sourceCoin} ≈ ${sourceRate} USDT`
          } else {
            const sourceRate = getRate(sourceCoin)
            const targetRate = getRate(targetCoin)
            if (!sourceRate) { this.$message.error('未找到 ' + sourceCoin + ' 汇率'); return }
            if (!targetRate) { this.$message.error('未找到 ' + targetCoin + ' 汇率'); return }
            const usdtAmount = parseFloat(this.originalAmount) * sourceRate
            convertedAmount = (usdtAmount / targetRate).toFixed(8)
            this.rateInfo = `1 ${sourceCoin} ≈ ${sourceRate} USDT，1 ${targetCoin} ≈ ${targetRate} USDT`
          }
          this.dataForm.amount = convertedAmount
        })
      },
      // 表单提交
      dataFormSubmit: Debounce(function () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            if(this.type == 'n'){
              this.$http({
              url: this.$http.adornUrl(`/rechargeOrder/manualReceipt`), //人工
              method: 'post',
              data: this.$http.adornData({
                'id': this.id,
                'safePasssword':encrypt(this.dataForm.safePasssword),
                'amount': this.dataForm.amount,
                'coinType': this.dataForm.targetCoin,
              })
            }).then(({data}) => {
              if(data.code == 0){
                  this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.resClear()
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              }else{
                this.$message({
                  message: data.msg,
                  type: 'error',
                })
              }

            })
            }else{
              this.$http({
              url: this.$http.adornUrl(`/rechargeOrder/refusalApply`), //驳回
              method: 'post',
              data: this.$http.adornData({
                'id': this.id,
                'content': this.dataForm.content,
              })
            }).then(({data}) => {
              if(data.code == 0){
                  this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.resClear()
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              }else{
                this.$message({
                  message: data.msg,
                  type: 'error',
                })
              }

            })
            }
          }
        })
      })
    }
  }
</script>
