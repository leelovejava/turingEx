<template>
  <el-dialog
    :title="!dataForm.id ? '新增通知' : '修改通知'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close="handClose"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="open()"
      label-width="120px"
    >
      <el-form-item label="标题" prop="title">
        <el-input v-model="dataForm.title" placeholder="标题"></el-input>
      </el-form-item>
      <el-form-item label="通知类型">
        <el-input v-model="dataForm.noticeType" placeholder="通知类型"></el-input>
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <el-input
          type="textarea"
          placeholder="请输入内容"
          v-model="dataForm.content"
          :autosize="{ minRows: 8, maxRows: 8 }"
          show-word-limit
        >
        </el-input>
      </el-form-item>
      <el-form-item label="用户UID（空是所有用户）">
        <el-input
          v-model="dataForm.userCode"
          placeholder="请输入用户UID"
        ></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="open()">确定</el-button>
    </span>
    <el-dialog
      title="确认操作"
      :visible.sync="dialogFormVisible"
      :append-to-body="true"
    >
      <el-form
        :model="dataForm2"
        :rules="dataRule2"
        ref="dataForm2"
        label-width="120px"
      >
        <el-form-item
          label="登录人资金密码"
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
        <el-button type="primary" @click="dataFormSubmit()">确 定</el-button>
      </div>
    </el-dialog>
  </el-dialog>
</template>

<script>
import { Debounce } from '@/utils/debounce'
import { encrypt } from '@/utils/crypto'

export default {
  data() {
    return {
      visible: false,
      dialogFormVisible: false,
      row: '',
      dataForm: {
        id: 0,
        title: '',
        content: '',
        noticeType: '',
        userCode: ''
      },
      dataForm2: {
        loginSafeword: ''
      },
      dataRule: {
        title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
        content: [{ required: true, message: '内容不能为空', trigger: 'blur' }]
      },
      dataRule2: {
        loginSafeword: [{ required: true, message: '资金密码不能为空', trigger: 'blur' }]
      }
    }
  },
  methods: {
    init(row) {
      this.row = row || ''
      if (row) {
        this.dataForm.title = row.title
        this.dataForm.content = row.content
        this.dataForm.noticeType = row.noticeType
        this.dataForm.userCode = row.userCode || ''
        this.dataForm.id = row.id
      } else {
        this.dataForm.id = 0
        this.dataForm.title = ''
        this.dataForm.content = ''
        this.dataForm.noticeType = ''
        this.dataForm.userCode = ''
      }
      this.visible = true
      this.dialogFormVisible = false
    },
    open() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.dialogFormVisible = true
        } else {
          return false
        }
      })
    },
    handClose() {
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      )
      this.$data.dataForm2 = JSON.parse(
        JSON.stringify(this.$options.data().dataForm2)
      )
      this.$nextTick(() => {
        if (this.$refs['dataForm']) this.$refs['dataForm'].clearValidate()
        if (this.$refs['dataForm2']) this.$refs['dataForm2'].clearValidate()
      })
    },
    dataFormSubmit: Debounce(function () {
      if (this.row) {
        this.$refs['dataForm2'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl('/userNotice/update'),
              method: 'post',
              data: this.$http.adornData({
                id: this.dataForm.id,
                title: this.dataForm.title,
                content: this.dataForm.content,
                noticeType: this.dataForm.noticeType,
                userCode: this.dataForm.userCode,
                loginSafeword: encrypt(this.dataForm2.loginSafeword)
              })
            }).then(({ data }) => {
              if (data.code == 0) {
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
              } else {
                this.$message({
                  message: data.msg,
                  type: 'error',
                  duration: 1500,
                  onClose: () => {
                    this.dialogFormVisible = false
                    this.visible = false
                  }
                })
              }
            })
          }
        })
      } else {
        this.$refs['dataForm2'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl('/userNotice/add'),
              method: 'post',
              data: this.$http.adornData({
                title: this.dataForm.title,
                content: this.dataForm.content,
                noticeType: this.dataForm.noticeType,
                userCode: this.dataForm.userCode,
                loginSafeword: encrypt(this.dataForm2.loginSafeword)
              })
            }).then(({ data }) => {
              if (data.code == 0) {
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
              } else {
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
      }
    })
  }
}
</script>

<style scoped></style>
