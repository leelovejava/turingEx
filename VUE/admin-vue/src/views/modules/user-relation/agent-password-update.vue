<template>
  <el-dialog
    title="重置代理商登录密码"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close = 'handClose'>
    <el-form :model="dataForm" :rules="dataRule"  ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="120px">
      <el-form-item  label="代理商重置密码"  prop="password">
        <el-input v-model="dataForm.password" type="password" placeholder="请输入代理商重置密码"></el-input>
      </el-form-item>
      <el-form-item  label="登录人资金密码"  prop="safeword">
        <el-input v-model="dataForm.safeword" type="password" placeholder="请输入登录人资金密码"></el-input>
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
      var validateMobile = (rule, value, callback) => {
        if (!isMobile(value)) {
          callback(new Error('手机号格式错误'))
        } else {
          callback()
        }
      }
      return {
        visible: false,
        roleList: {},
        id:'',
        row:'',
        dataForm: {
          parentsUseCode:'',
          remarks:'',
          userName: '',
          password: '',
          email: '',
          mobile: '',
          safeword:'',
          status: 1
        },
        options:[
          {
            label:'正常',
            value:true,
          },
          {
            label:'限制登录',
            value:false,
          }
        ],
        optionsTwo:[
          {
            label:'正常',
            value:true,
          },
          {
            label:'业务锁定(登录不受影响,锁定后无法购买订单和提现)',
            value:false,
          }
        ],
        dataRule: {
          userName: [
            { required: true, message: '用户名不能为空', trigger: 'blur' },
            // { pattern: /\s\S+|S+\s|\S/, message: '请输入正确的用户名', trigger: 'blur' }
          ],
          password: [
          { required: true, message: '代理商重置密码不能为空', trigger: 'blur' },
          ],
          safeword: [
          { required: true, message: '登录人资金密码不能为空', trigger: 'blur' },
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
      init (id) {
        this.dataForm.remarks= ''
        this.id = id || ''
        this.visible = true
        this.$nextTick(() => {
        //this.$refs.dataForm.resetFields()  
      })
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
              url: this.$http.adornUrl(`/agent/restPassword`), //修改
              method: 'post',
              data: this.$http.adornData({
                'id': this.id,
                'password': encrypt(this.dataForm.password),
                'safeword': encrypt(this.dataForm.safeword),
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
                })
              }

            })
          }
        })
      })
    }
  }
</script>
