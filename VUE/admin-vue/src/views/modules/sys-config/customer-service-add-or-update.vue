<template>
  <el-dialog
    :title="!dataForm.id ? '新增客服信息' : '修改客服信息'"
    :close-on-click-modal="false"
    @close = 'handClose'
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="open()" label-width="80px">
      <el-form-item label="用户名" prop="userName">
        <el-input v-model="dataForm.userName" :disabled="row?true:false" placeholder="请输入后台登录用户名"></el-input>
      </el-form-item>
      <el-form-item v-if="!row" label="密码" prop="password">
        <el-input v-model="dataForm.password" type="password" placeholder="请输入密码"></el-input>
      </el-form-item>
      <el-form-item v-if="!row" label="资金密码" prop="safePassword">
        <el-input v-model="dataForm.safePassword" type="password" placeholder="请输入资金密码"></el-input>
      </el-form-item>

      <el-form-item label="登录权限">
        <el-select v-model="enabled.value" @change="changeVal()"                    
                     placeholder="请选择">
            <el-option v-for="item in enabled"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value">
            </el-option>
          </el-select>
      </el-form-item>

      <el-form-item label="自动回复" prop="">
        <el-input
            type="textarea"
            placeholder=""
            v-model="dataForm.autoAnswer"
          >
        </el-input>
      </el-form-item>

      <el-form-item label="备注" prop="">
        <el-input
            type="textarea"
            placeholder=""
            v-model="dataForm.remarks"
          >
        </el-input>
      </el-form-item>

    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="open()">确定</el-button>
    </span>
    <!-- 确认弹窗-start -->
    <el-dialog title="确认增加" :visible.sync="dialogFormVisible" :append-to-body="true">
      <el-form :model="dataForm2" :rules="dataRule2" ref="dataForm2" @keyup.enter.native="dataFormSubmit()" label-width="80px">
        <el-form-item label="登录人资金密码"  :label-width="formLabelWidth" prop="loginSafeword">
          <el-input v-model="dataForm2.loginSafeword" type="password" placeholder="登录人资金密码" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item v-if="!row" label="超级谷歌验证码" :label-width="formLabelWidth" prop="superGoogleAuthCode">
          <el-input v-model="dataForm2.superGoogleAuthCode" placeholder="超级谷歌验证码" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="dataFormSubmit()">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 确认弹窗-end -->
  </el-dialog>
</template>

<script>
  import { treeDataTranslate } from '@/utils'
  import { Debounce } from '@/utils/debounce'
  import { encrypt } from "@/utils/crypto";
  export default {
    data () {
      return {
        // form: {
        //   loginSafeword	: '',     //登录人资金密码	
        //   superGoogleAuthCode: '' //超级管理员谷歌验证码	
        // },
        formLabelWidth: '120px',
        dialogFormVisible: false,
        visible: false,
        row:'',
        menuList: [],
        enabled:[{
          value:true,
          label:'开启',
        },{
          value:false,
          label:'关闭',
        }],
        menuListTreeProps: {
          label: 'name',
          children: 'children'
        },
        dataForm: {
          id: '',
          userName:'',
          roleName: '',
          autoAnswer:'',
          remarks: '',
          password:'',
          safePassword:'',
          loginSafeword:'',
          superGoogleAuthCode:'',
        },
        dataForm2: {
          loginSafeword:'',
          superGoogleAuthCode:'',
        },
        dataRule: {
          roleName: [
            { required: true, message: '角色名称不能为空', trigger: 'blur' },
            { pattern: /\s\S+|S+\s|\S/, message: '请输入正确的角色名称', trigger: 'blur' }
          ],
          userName: [
            { required: true, message: '用户名不能为空', trigger: 'blur' },
          ],safePassword: [
            { required: true, message: '资金密码不能为空', trigger: 'blur' },
          ],password: [
            { required: true, message: '密码不能为空', trigger: 'blur' },
          ],
          loginSafeword: [
            { required: true, message: '登录人资金密码不能为空', trigger: 'blur' },
          ],
          superGoogleAuthCode: [
            { required: true, message: '超级谷歌验证码不能为空', trigger: 'blur' },
          ],
          autoAnswer:[
            { required: true, message: '自动回复不能为空', trigger: 'blur' },
          ],
          remarks:[
            { required: true, message: '备注不能为空', trigger: 'blur' },
          ],
        },
        dataRule2:{
          loginSafeword: [
            { required: true, message: '登录人资金密码不能为空', trigger: 'blur' },
          ],
          superGoogleAuthCode: [
            { required: true, message: '超级谷歌验证码不能为空', trigger: 'blur' },
          ],
        },
      }
    },
    methods: {
      init (row) {
        this.row = row || ''
        this.visible = true
        if(this.row){
          this.dataForm.userName = row.userName
          this.dataForm.remarks = row.remarks
          this.dataForm.autoAnswer = row.autoAnswer
          this.enabled.value = row.status == "1"
          this.dataForm.id = row.id
        }else{
          this.enabled.value = this.enabled[0].value
        }
      },
      open(){
        this.$refs['dataForm'].validate((valid) => {
          if(valid){
            this.dialogFormVisible = true
          }else{
            return false
          }
          
        })
      },
      handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
        this.$data.dataForm2=JSON.parse(JSON.stringify(this.$options.data().dataForm2))
        this.$nextTick(() => {
            if(this.$refs['dataForm'])
              this.$refs['dataForm'].clearValidate() // 清除表单验证
            if(this.$refs['dataForm2'])
              this.$refs['dataForm2'].clearValidate() // 清除表单验证
          })
      },
      changeVal(val){
        this.$forceUpdate()
      },
      // 表单提交
      dataFormSubmit: Debounce(function () {
          if(this.row){
            this.$refs['dataForm2'].validate((valid) => {
            if (valid) {
              this.$http({ // 修改
                url: this.$http.adornUrl(`/customer/update`),
                method: 'post',
                // data:data1
                data: this.$http.adornData({
                  "autoAnswer": this.dataForm.autoAnswer, //自动回复
                  "enabled": this.enabled.value, //登录权限 true 开启 false 禁用
                  "loginSafeword": encrypt(this.dataForm2.loginSafeword), //loginSafeword	登录人资金密码
                  "remarks": this.dataForm.remarks, //remarks	备注	
                  "id":this.dataForm.id, 
                })
              }).then(({data}) => {
                if(data.code == 0){
                  this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.dialogFormVisible = false
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
                    this.dialogFormVisible = false
                  }
                })
                
                }
              })
            }
          })
          }else{
            this.$refs['dataForm2'].validate((valid) => {
            if (valid) {
              this.$http({
                url: this.$http.adornUrl(`/customer/add`),
                method: 'post',
                // data:data1
                data: this.$http.adornData({
                  "autoAnswer": this.dataForm.autoAnswer, //自动回复
                  "userName": this.dataForm.userName, //用户名
                  "enabled": this.enabled.value, //登录权限 true 开启 false 禁用
                  "loginSafeword": encrypt(this.dataForm2.loginSafeword), //loginSafeword	登录人资金密码
                  "password": encrypt(this.dataForm.password), //password	密码
                  "remarks": this.dataForm.remarks, //remarks	备注	
                  "safePassword": encrypt(this.dataForm.safePassword), //safePassword	资金密码
                  "superGoogleAuthCode": this.dataForm2.superGoogleAuthCode, //superGoogleAuthCode	超级管理员谷歌验证码		false	
                })
              }).then(({data}) => {
                if(data.code == 0){
                  this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.dialogFormVisible = false
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
          }

          //end

      })
    }
  }
</script>
