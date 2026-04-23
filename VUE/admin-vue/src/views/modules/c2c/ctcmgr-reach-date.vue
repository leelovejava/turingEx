<template>
  <el-dialog
    title="更多参数"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule"  ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="150px">
      <el-form-item label="参数名4:参数值4"  prop="">
        <el-input v-model="dataForm.n4" disabled></el-input>
      </el-form-item>
      <el-form-item label="参数名5:参数值5"  prop="paramName2">
        <el-input v-model="dataForm.n5" disabled></el-input>
      </el-form-item>
      <el-form-item label="参数名6:参数值6"  prop="paramName3">
        <el-input v-model="dataForm.n6" disabled></el-input>
      </el-form-item>
      <el-form-item label="参数名7:参数值7"  prop="paramName4">
        <el-input v-model="dataForm.n7" disabled></el-input>
      </el-form-item>
      <el-form-item label="参数名8:参数值8"  prop="paramName5">
        <el-input v-model="dataForm.n8"  disabled></el-input>
      </el-form-item>
      <el-form-item label="参数名9:参数值9"  prop="paramName6">
        <el-input v-model="dataForm.n9"  disabled></el-input>
      </el-form-item>
      <el-form-item label="参数名10:参数值10"  prop="paramName7">
        <el-input v-model="dataForm.n10"  disabled></el-input>
      </el-form-item>
      <el-form-item label="参数名11:参数值11"  prop="paramName8">
        <el-input v-model="dataForm.n11"  disabled></el-input>
      </el-form-item>
      <el-form-item label="参数名12:参数值12"  prop="paramName9">
        <el-input v-model="dataForm.n12"  disabled></el-input>
      </el-form-item>
      <el-form-item label="参数名13:参数值13"  prop="paramName10">
        <el-input v-model="dataForm.n13"  disabled></el-input>
      </el-form-item>
      <el-form-item label="参数名14:参数值14"  prop="paramName11">
        <el-input v-model="dataForm.n14"  disabled></el-input>
      </el-form-item>
      <el-form-item label="参数名15:参数值15"  prop="paramName12">
        <el-input v-model="dataForm.n15"  disabled></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="visible = false">确定</el-button>
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
        imageUrl:'',
        dataForm: {
          n4:'',
          n5:'',
          n6:'',
          n7:'',
          n8:'',
          n9:'',
          n10:'',
          n11:'',
          n12:'',
          n13:'',
          n14:'',
          n15:'',
        },
        arr:[],
        dataRule: {
          paramName1: [
            { required: true, message: '参数1不能为空', trigger: 'blur' },
            // { pattern: /\s\S+|S+\s|\S/, message: '请输入正确的用户名', trigger: 'blur' }
          ],
          password: [
          { required: true, message: '密码不能为空', trigger: 'blur' },
          ],
          value1: [
          { required: true, message: '请选择支付方式', trigger: 'blur' },
          ],
          loginSafeword: [
            { required: true, message: '资金密码不能为空', trigger: 'blur' },
          ],
          // mobile: [
          //   { required: true, message: '手机号不能为空', trigger: 'blur' },
          //   { validator: validateMobile, trigger: 'blur' }
          // ]
        }
      }
    },
    methods: {
      init (row) {
        this.resClear()
        this.dataForm = row || {}
        if(row){
          this.n4 = row.paramName4 +':'+ row.paramValue4
          this.n5 = row.paramName5 +':'+ row.paramValue5
          this.n6 = row.paramName6 +':'+ row.paramValue6
          this.n7 = row.paramName7 +':'+ row.paramValue7
          this.n8 = row.paramName8 +':'+ row.paramValue8
          this.n9 = row.paramName9 +':'+ row.paramValue9
          this.n10 = row.paramName10 +':'+ row.paramValue10
          this.n11 = row.paramName11 +':'+ row.paramValue11
          this.n12 = row.paramName12 +':'+ row.paramValue12
          this.n13 = row.paramName13 +':'+ row.paramValue13
          this.n14 = row.paramName14 +':'+ row.paramValue14
          this.n15 = row.paramName15 +':'+ row.paramValue15
        }
        this.visible = true
        this.$nextTick(() => {
        //this.$refs.dataForm.resetFields()  
      })
      },
      resClear(){
        this.arr.id='',
        this.dataForm = {
          n4:'',
          n5:'',
          n6:'',
          n7:'',
          n8:'',
          n9:'',
          n10:'',
          n11:'',
          n12:'',
          n13:'',
          n14:'',
          n15:'',
        }
      },
    }
  }
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
    border-color: #409EFF;
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
</style>