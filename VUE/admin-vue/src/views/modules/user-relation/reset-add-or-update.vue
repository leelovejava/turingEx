<template>
  <el-dialog
    :title="dataForm.type == 1 ? '审核通过' : '驳回'"
    :close-on-click-modal="false"
    @close = 'handClose'
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item v-if="dataForm.type == 1" label="资金密码" prop="loginSafeword">
        <el-input v-model="dataForm.loginSafeword" type="password" placeholder="资金密码"></el-input>
      </el-form-item>
      <el-form-item v-if="dataForm.type == 2" label="驳回原因" prop="content">
        <el-input v-model="dataForm.content" placeholder="驳回原因"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  import { encrypt } from '@/utils/crypto'
  import { treeDataTranslate } from '@/utils'
  import { Debounce } from '@/utils/debounce'
  export default {
    data () {
      return {
        visible: false,
        menuList: [],
        menuListTreeProps: {
          label: 'name',
          children: 'children'
        },
        dataForm: {
          id: 0,
          loginSafeword: '',
          content: '',
          type:''
        },
        dataRule: {
          roleName: [
            { required: true, message: '角色名称不能为空', trigger: 'blur' },
            { pattern: /\s\S+|S+\s|\S/, message: '请输入正确的角色名称', trigger: 'blur' }
          ],
          loginSafeword:[
          { required: true, message: '资金密码不能为空', trigger: 'blur' },
          ],
          content: [
          { required: true, message: '驳回原因不能为空', trigger: 'blur' },
          ]
        },
        tempKey: -666666 // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
      }
    },
    methods: {
      init (id,type) {
        this.dataForm.id = id || 0
        this.dataForm.type = type || 0
        this.visible = true
      },
      handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
     this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
      },
      // 表单提交
      dataFormSubmit: Debounce(function () {
        let a = {
          'id': this.dataForm.id,
          'loginSafeword': encrypt(this.dataForm.loginSafeword),
          'type':this.dataForm.type,
        }
        let b = {
          'id': this.dataForm.id,
          'content': this.dataForm.content,
          'type':this.dataForm.type,
        }
        let data
        if(this.dataForm.type == 1){
          data = a
        }else{
          data = b
        }
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/userSafewordApply/examine`),
              method:'post',
              data: this.$http.adornData(
                data
              )
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
