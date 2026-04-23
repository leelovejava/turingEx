<template>
  <el-dialog
    :title = "googleAuthBind?'解绑客服谷歌验证器':'绑定客服谷歌验证器'"
    :visible.sync="visible"
    :append-to-body="true">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="120px">
      <div class="gogTop">
        <div v-if="!getInput" class="gogRefresgh" @click="getGoogleAuthSecret()"><i class="el-icon-refresh"></i></div>
      </div>
      <el-form-item v-if="getInput && !googleAuthBind" label="密钥"  style="position:relative">
        <el-input type="number"  :disabled="true" placeholder="请点击右侧 生成密钥和二维码"></el-input>
        <span class="maTreButton" @click="getGoogleAuthSecret()">生成密钥和二维码</span>
      </el-form-item>
      <el-form-item v-show="!getInput" label="密钥"  style="position:relative">
        <el-input type="text"  :disabled="true" v-model="dataForm.secret"></el-input>
        <el-button class="spaButton" type="button"
          v-clipboard:copy="dataForm.secret"  
          v-clipboard:success="onCopy" 
          v-clipboard:error="onError">
          复制
 	      </el-button>
      </el-form-item>
      <div v-show="!getInput" class="qrCode">
        <img :src=gleimg alt="">
      </div>
      <el-form-item v-if="!googleAuthBind" label="谷歌验证" prop="code">
        <el-input type="number" placeholder="请输入当前管理员谷歌验证码" v-model="dataForm.code"></el-input>
      </el-form-item>
      <el-form-item label="超级谷歌验证码" prop="rootGoogleAuthCode">
        <el-input type="number" placeholder="请输入超级谷歌验证码" v-model="dataForm.rootGoogleAuthCode"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  import { Debounce } from '@/utils/debounce'
  import { encrypt } from '@/utils/crypto'
  export default {
    data () {
      return {
        visible: false,
        dataForm: {
          password: '',
          newPassword: '',
          urlencoded:'',
          rootGoogleAuthCode:'',
          code:'',
          secret:'',//谷歌密钥
        },
        googleAuthBind:'',//系统用户是否绑定谷歌验证器
        getInput:true,
        gleimg:'',
        gogleUs:'',//用户列表进入
        dataRule: {
          code: [
            { required: true, message: '谷歌验证码不能为空', trigger: 'blur' },
          ],
          rootGoogleAuthCode: [
            { required: true, message: '超级谷歌验证码不能为空', trigger: 'blur' },
          ],
        },
        data:{}
      }
    },
    computed: {
      userName: {
        get () { return this.$store.state.user.name }
      },
      mainTabs: {
        get () { return this.$store.state.common.mainTabs },
        set (val) { this.$store.commit('common/updateMainTabs', val) }
      }
    },
    created(){
      console.log(this.googleAuthBind)
    },
    methods: {
      // 初始化
      init (googleAuthBind,id,data) {
        this.visible = true
        this.gogleUs = id
        this.googleAuthBind = googleAuthBind
        this.data = {...data};
        this.$nextTick(() => {
          this.getInput = true
          this.$refs['dataForm'].resetFields()
        })
      },
      // 表单提交
      dataFormSubmit: Debounce(function () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            if(this.googleAuthBind){ //解绑
              this.$http({
                url: this.$http.adornUrl('/customer/unbindGoogleAuthCode'),
                method: 'post',
                data: this.$http.adornData({
                  'rootGoogleAuthCode': this.dataForm.rootGoogleAuthCode,
                  'rootGoogleCode':this.dataForm.rootGoogleAuthCode,
                  'userId': this.gogleUs,
                })
              }).then(({data}) => {
                this.$message({
                  message: '解绑成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              })
            }else{
                this.$http({
                url: this.$http.adornUrl('/customer/bindGoogleAuthCode'),
                method: 'post',
                data: this.$http.adornData({
                  'googleAuthCode': this.dataForm.code,
                  'userId': this.gogleUs,
                  'rootGoogleAuthCode':this.dataForm.rootGoogleAuthCode,
                  'rootGoogleCode':this.dataForm.rootGoogleAuthCode,
                  'googleAuthSecret': this.dataForm.secret
                })
              }).then(({data}) => {
                if(data.code == 0){
                  this.$message({
                    message: '绑定成功',
                    type: 'success',
                    duration: 1500,
                    onClose: () => {
                      this.visible = false
                      this.$emit('refreshDataList')
                    }
                  })
                }
                else
                {
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

          }
        })
      }),
      //获取密钥
      getGoogleAuthSecret () {         
            this.$http({
              url: this.$http.adornUrl('/getLoginGoogleAuthSecret'),
              method: 'get',
              params: this.$http.adornParams(Object.assign({
                name:this.data.userName || this.data.username
              }))
            }).then(({data}) => {
              if(data.code == 0){
                this.getInput= false
                this.dataForm.secret = data.data.googleAuthSecret
                this.gleimg = data.data.googleAuthImg
                this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
              })
              }else{
                this.$message({
                message: '生成失败',
                type: 'error',
                duration: 1500,
              })
              }
            })      
      },
      onCopy: function (e) {
        this.$message({
                message: '复制成功',
                type: 'success',
              })
    },
    onError: function (e) {
      this.$message({
                message: '复制失败',
                type: 'error',
              })
    }
    }
  }
</script>
<style scoped>
.gogText{
  float: left;
}
.gogRefresgh{
  float: right;
  cursor: pointer;
}
.el-icon-refresh:hover{
  color: #02A1E9;
}
.gogTop{
  overflow: hidden;
    padding: 0 15px;
    height: 43px;
}
.maTreButton{
  position: absolute;
  width: 123px;
  right: 10px;
  color: #02A1E9;
  cursor: pointer;
}
.maTreButton:hover{
  color: rgb(59, 226, 184);
}
.qrCode{
  width: 219px;
  height: 228px;
  border: 1px solid #e1e4eb;
  margin: 0 auto;
  margin-bottom: 20px;
}
.spaButton{
  position: absolute;
  right: 2px;
  border: none;
  background: #eff2f5;
  color: #02A1E9;
}
</style>
