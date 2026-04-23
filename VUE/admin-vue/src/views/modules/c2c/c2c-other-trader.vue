<template>
  <el-dialog
    title="修改 承兑商保证金"
    :close-on-click-modal="false"
    @close = 'handClose'
    :visible.sync="visible">
    <el-tabs v-model="activeName" @tab-click="handleClick">
    <el-tab-pane label="充值" name="recharge"></el-tab-pane>
    <el-tab-pane label="提现" name="withdraw"></el-tab-pane>
  </el-tabs>
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="130px">
      <el-form-item :label="activeName=='recharge'?'充值数量':'提现数量'" prop="money_change">
            <el-input  v-model="dataForm.money_change" type="number" placeholder="数量">
              <template v-slot:append>
                <span>USDT</span>
              </template></el-input>
          </el-form-item>
          <el-form-item label="当前的总保证金" prop="deposit_open">
            <el-input  v-model="dataForm.deposit_open" disabled placeholder="剩余保证金">
              <template v-slot:append>
                <span>USDT</span>
              </template></el-input>
          </el-form-item>
          <el-form-item label="修改后总保证金" prop="deposit_open_update">
            <el-input  v-model="activeName=='recharge'?dataForm.deposit_open*1+dataForm.money_change*1:dataForm.deposit_open*1-dataForm.money_change*1" disabled placeholder="剩余保证金">
              <template v-slot:append>
                <span>USDT</span>
              </template></el-input>
          </el-form-item>
          <el-form-item label="当前的剩余保证金" prop="deposit">
            <el-input  v-model="dataForm.deposit" disabled placeholder="剩余保证金">
              <template v-slot:append>
                <span>USDT</span>
              </template></el-input>
          </el-form-item>
          <el-form-item label="修改后剩余保证金" prop="deposit_update">
            <el-input  v-model="activeName=='recharge'?dataForm.deposit*1+dataForm.money_change*1:dataForm.deposit*1-dataForm.money_change*1" disabled placeholder="剩余保证金">
              <template v-slot:append>
                <span>USDT</span>
              </template></el-input>
          </el-form-item>
          <el-form-item label="资金密码" prop="safe_password">
            <el-input  v-model="dataForm.safe_password" type="password"  placeholder="当前登录人资金密码"></el-input>
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
  import { Debounce } from '@/utils/debounce'
  import { encrypt } from '@/utils/crypto'
  export default {
    data () {
      return {
        visible: false,
        menuList: [],
        activeName: 'recharge',
        menuListTreeProps: {
          label: 'name',
          children: 'children'
        },
        dataForm: {
          id: '',
          money_change:0,
          recharge_withdraw:'',
          deposit_open:'',//当前保证金
          deposit:'',//剩余保证金
          roleName: '',
          deposit_open_update:'',
          deposit_update:'',
          remark: '',
          safe_password:'',
        },
        row:'',
        dataRule: {
          roleName: [
            { required: true, message: '角色名称不能为空', trigger: 'blur' },
            { pattern: /\s\S+|S+\s|\S/, message: '请输入正确的角色名称', trigger: 'blur' }
          ],
          safe_password:[
          { required: true, message: '资金密码不能为空', trigger: 'blur' },
          ],
          remark: [
            { required: false, pattern: /\s\S+|S+\s|\S/, message: '输入格式有误', trigger: 'blur' }
          ]
        },
        tempKey: -666666 // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
      }
    },
    methods: {
      init (row) {
        this.row =  row|| ''
        this.dataForm.deposit_open = row.deposit_open
        this.dataForm.deposit = row.deposit
        this.dataForm.id = row.id
        this.visible = true;

      },
      handleClick(tab, event) {
        console.log(tab, event);
      },
      handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
     this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
      },
      // 表单提交
      dataFormSubmit: Debounce(function () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/c2cUser/reset`),
              method:'post',
              data: this.$http.adornData({
                'id': this.dataForm.id,
                'recharge_withdraw': this.activeName,
                'safe_password': encrypt(this.dataForm.safe_password),
                'money_change':this.dataForm.money_change,
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
      })
    }
  }
</script>
