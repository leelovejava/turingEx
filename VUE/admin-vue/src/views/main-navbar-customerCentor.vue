<template>
  <el-dialog
    title="客服个人中心页面"
    :visible.sync="visible"
    width="600px"
    :append-to-body="true">
    <el-form :model="dataForm" ref="dataForm" label-width="100px">
      <el-form-item label="用户名">
        <span>{{ userName }}</span>
      </el-form-item>
      <el-form-item label="当前在线状态" prop="online">
        <el-input  v-model="dataForm.online" disabled></el-input>
      </el-form-item>
      <el-form-item label="最后上线时间" prop="last_online_time">
        <el-input  v-model="dataForm.last_online_time" disabled></el-input>
      </el-form-item>
      <el-form-item label="最后下线时间" prop="last_offline_time">
        <el-input  v-model="dataForm.last_offline_time" disabled></el-input>
      </el-form-item>
      <el-form-item label="再次访问自动回复">
        <el-input  v-model="dataForm.auto_answer"></el-input>
      </el-form-item>

    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关闭</el-button>
      <el-button type="primary" @click="personalOpen()">{{dataForm.online_state==1 ? "下线" : "上线"}}</el-button>
      <el-button type="primary" @click="personalUpdateAutoAnswer1()">保存</el-button>
    </span>

    <!-- 确认弹窗-start -->
    <el-dialog
      title="验证资金密码"
      :visible.sync="dialogFormVisible"
      :append-to-body="true"
    >
      <el-form
        :model="dataForm2"
        ref="dataForm2"
        @keyup.enter.native="personalUpdateAutoAnswer()"
        label-width="80px"
      >
        <el-form-item
          label="登录人资金密码"
          :label-width="formLabelWidth"
          prop="loginSafeword"
        >
          <el-input
            v-model="dataForm2.loginSafeword"
            type="password"
            placeholder="登录人资金密码"
            autocomplete="off"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="personalUpdateAutoAnswer()">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 确认弹窗-end -->

  </el-dialog>
</template>

<script>
  import { clearLoginInfo } from '@/utils'
  import { Debounce } from '@/utils/debounce'
  import { encrypt } from '@/utils/crypto'
  export default {
    data () {
      var validateConfirmPassword = (rule, value, callback) => {
        if (this.dataForm.newPassword !== value) {
          callback(new Error('确认密码与新密码不一致'))
        } else {
          callback()
        }
      }
      return {
        visible: false,
        dataForm: {
          password: '',
          newPassword: '',
          confirmPassword: ''
        },
        dialogFormVisible:false,
        dataForm2:{},
        formLabelWidth: "120px",
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
    methods: {
      // 初始化
      init () {

        this.personalCustomer();

        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
        })
      },
      // 表单提交
      dataFormSubmit: Debounce(function () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl('/changeLoginPassword'),
              method: 'post',
              data: this.$http.adornData({
                'oldPassword': encrypt(this.dataForm.password),
                'newPassword': encrypt(this.dataForm.newPassword)
              })
            }).then(({data}) => {
              if(data.code == 0){
                this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.visible = false
                  this.$nextTick(() => {
                    // this.mainTabs = []
                     // clearLoginInfo()
                    // this.$router.replace({ name: 'login' })
                  })
                }
              })
              }else{
                this.$message({
                message:data.msg,
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
      personalCustomer() {
        this.$http({
          url: this.$http.adornUrl("/normal/adminPersonalCustomerAction!personalCustomer.action"),
          method: "get",
          data: this.$http.adornData({}),
        }).then(({ data }) => {
          if(data.code == 0){
            let kefuInfo = {}
            this.dataForm = data.data;
            if(this.dataForm.online_state == 1){
              this.dataForm.online = "上线"
              kefuInfo.isOnline = true;
            }else{
              this.dataForm.online = "下线"
              kefuInfo.isOnline = false;
            }
            
            this.$bus.$emit("updateKefuInfo",kefuInfo);
          }else{
            this.$message(data.msg);
          }
 
        });
      },
      personalOpen(){
        // this.dialogFormVisible = true;
        if(this.dataForm.online_state == 1){
          this.personalOffline();
        }else{
          this.personalOnline();
        }
      },
      personalOnline() {
        this.$http({
          url: this.$http.adornUrl("/normal/adminPersonalCustomerAction!personalOnline.action"),
          method: "get",
          data: this.$http.adornData({}),
        }).then(({ data }) => {
          if(data.code == 0){
            this.personalCustomer();
          }else{
            this.$message(data.msg);
          }
 
        });
      },
      personalOffline() {
        this.open("下线后将不会收到消息，如有新消息，用户将分配给其他在线客服","是否下线",
          ()=>{
          //
          this.$http({
            url: this.$http.adornUrl("/normal/adminPersonalCustomerAction!personalOffline.action"),
            method: "get",
            data: this.$http.adornData({}),
          }).then(({ data }) => {
            if(data.code == 0){
              this.personalCustomer();
            }else{
              this.$message(data.msg);
            }
  
          });
          //
        },()=>{});

      },
      open(message,title,yes,cancel) {
        this.$confirm(message, title, {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          center: true
        }).then(() => {
          if(yes){yes()}
          // this.$message({
          //   type: 'success',
          //   message: '删除成功!'
          // });
        }).catch(() => {
          if(cancel){cancel()}
          // this.$message({
          //   type: 'info',
          //   message: '已取消删除'
          // });
        });
      },
      personalUpdateAutoAnswer1(){
        this.dialogFormVisible = true;
      },
      personalUpdateAutoAnswer() {
        if(!this.dataForm2.loginSafeword){
          this.$message({
              message: "资金密码不能为空",
              type: 'error',
              duration: 1500,
              onClose: () => {
                // this.visible = false
                // this.$emit('refreshDataList')
              }
            })
          return
        }
        this.$http({
          url: this.$http.adornUrl("/normal/adminPersonalCustomerAction!personalUpdateAutoAnswer.action"),
          method: "get",
          params: this.$http.adornParams({
            auto_answer:this.dataForm.auto_answer,
            login_safeword:encrypt(this.dataForm2.loginSafeword)
          }),
        }).then(({ data }) => {
          if(data.code == 0){
            this.dialogFormVisible = false;
          }else{
            // this.$message(data.msg);
            this.$message({
              message: data.msg == "AES解密错误" ? "资金密码不正确" : data.msg,
              type: 'error',
              duration: 1500,
              onClose: () => {
                // this.visible = false
                // this.$emit('refreshDataList')
              }
            })
          }
 
        });
      },
    }
  }
</script>

