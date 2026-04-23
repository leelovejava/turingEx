<template>
  <el-dialog
    :title="!id ? '新增代理商' : '修改代理商'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    width="700px"
    @close = 'handClose'>
    <el-form :model="dataForm" :rules="dataRule"  ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="140px">
      <div v-if="!id" style="margin:20px 0;color: green;padding-left: 120px">资金密码默认为000000，需要修改的代理商号可以登录后在后台右上角下拉框里选择修改资金密码修改一下</div>
      <el-form-item v-if="!id" label="用户名" label-width="120px" prop="userName">
        <el-input v-model="dataForm.userName" :disabled="id?true:false" placeholder="登录帐号"></el-input>
      </el-form-item>
      <el-form-item v-if="!id" label="登录密码" label-width="120px" prop="password">
        <el-input v-model="dataForm.password" type="password" placeholder="登录密码"></el-input>
      </el-form-item>
      <el-form-item v-if="!id" label="登录人资金密码" label-width="120px" prop="safeword">
        <el-input v-model="dataForm.safeword" type="password" placeholder="请输入登录人资金密码"></el-input>
      </el-form-item>  
      <el-form-item v-if="!id" label="UID(选填)" prop="parentsUseCode" label-width="120px">
        <el-input v-model="dataForm.parentsUseCode" placeholder="上级用户或上级代理商UID(选填)"></el-input>
      </el-form-item>
      <!-- <el-form-item label="手机号" prop="mobile">
        <el-input v-model="dataForm.mobile" placeholder="手机号"></el-input>
      </el-form-item> -->
      <el-form-item label="登录权限" label-width="120px">
        <el-select v-model="options.value" @change="changeVal()" class="spasect"                   
                     placeholder="请选择">
            <el-option v-for="item in options"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value">
            </el-option>
          </el-select>
      </el-form-item>
      <el-form-item label="操作权限" label-width="120px">
        <el-select v-model="optionsTwo.value" @change="changeVal()"  class="spasect"   
                     placeholder="请选择">
            <el-option v-for="item in optionsTwo"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value">
            </el-option>
          </el-select>
      </el-form-item>
      <el-form-item label="备注" label-width="120px" prop="remarks">
        <el-input type="textarea" v-model="dataForm.remarks" 
          :autosize="{ minRows: 4, maxRows: 8 }"
          show-word-limit></el-input>
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
          { required: true, message: '登录密码不能为空', trigger: 'blur' },
          ],
          safeword: [
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
      init (row,id) {
        this.dataForm.remarks= ''
        this.roleList = row || ""
        this.id = id || ''
        if(row){
          this.options.value = row.loginAuthority
          this.optionsTwo.value = row.operaAuthority
          this.dataForm.remarks =row.remarks
        }else {
          this.options.value = this.options[0].value
          this.optionsTwo.value = this.optionsTwo[0].value
        }
        this.visible = true
        this.$nextTick(() => {
        //this.$refs.dataForm.resetFields()  
      })
      },
      changeVal(val){
        this.$forceUpdate()
      },
      handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
        this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
        this.options.value=""
        this.optionsTwo.value=""
      },
      // 表单提交
      dataFormSubmit: Debounce(function () {
        console.log("this.optionsTwo.value2 = " + this.optionsTwo.value);
        console.log("this.optionsTwo.value2 = " + this.options.value);
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            if(this.id){
              this.$http({
              url: this.$http.adornUrl(`/agent/update`), //修改
              method: 'post',
              data: this.$http.adornData({
                'operaAuthority': this.optionsTwo.value,
                'loginAuthority': this.options.value,
                'id': this.id,
                'remarks': this.dataForm.remarks,
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
            }else{
              this.$http({
              url: this.$http.adornUrl(`/agent/add`), //新增
              method: 'post',
              data: this.$http.adornData({
                'userName': this.dataForm.userName,
                'operaAuthority': this.optionsTwo.value,
                'loginAuthority': this.options.value,
                'parentsUseCode': this.dataForm.parentsUseCode,
                'password': encrypt(this.dataForm.password),
                'safeword': encrypt(this.dataForm.safeword),
                'remarks': this.dataForm.remarks,
              })
            }).then(({data}) => {
              if(data.code == 0){
                  this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    // this.resClear()
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
<style scoped>
.spasect{
  width: 540px;
}
</style>