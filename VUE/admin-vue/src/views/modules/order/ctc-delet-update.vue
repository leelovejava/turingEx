<template>
  <el-dialog
    title="确认删除"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule"  ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="登录人资金密码" label-width="220px" prop="loginSafeword">
        <el-input v-model="dataForm.loginSafeword" type="password" placeholder="登录人资金密码"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary"  v-loading="dataListLoading" @click="dataFormSubmit()">确定</el-button>
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
        dataListLoading: false,
        roleList: {},
        id:'',
        dataForm: {
          loginSafeword:'',
        },
        dataRule: {
          loginSafeword: [
            { required: true, message: '登录人资金密码不能为空', trigger: 'blur' },
            // { pattern: /\s\S+|S+\s|\S/, message: '请输入正确的用户名', trigger: 'blur' }
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
        this.resClear()
        this.id = id || ''
        this.visible = true
        this.$nextTick(() => {
        //this.$refs.dataForm.resetFields()  
      })
      },
      resClear(){
        this.dataForm = {
          loginSafeword:'',
        }
      },
      // 表单提交
      dataFormSubmit: Debounce(function () {
        this.$refs['dataForm'].validate((valid) => {
          this.dataListLoading = true;
          if (valid) {
              this.$http({
              url: this.$http.adornUrl(`/paymentMethodConfig/delete`), //修改
              method: 'post',
              data: this.$http.adornData({
                'id':this.id,
                'loginSafeword':encrypt(this.dataForm.loginSafeword)
              })
            }).then(({data}) => {
              if(data.code == 0){
                  this.$message({
                  message: '删除成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.resClear()
                    this.visible = false
                    this.$emit('refreshDataListThree')
                  }
                })
                this.dataListLoading = false;
              }else{
                this.$message({
                  message: data.msg,
                  type: 'error',
                })
                this.dataListLoading = false;
              }

            })
          }
        })
      })
    }
  }
</script>
