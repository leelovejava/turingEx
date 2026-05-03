<template>
  <el-dialog :title="'用户状态管理'"
             :close-on-click-modal="false"
             :visible.sync="visible"
             width="500px"
             :append-to-body="true"
             @close="handClose">
    <el-form :model="dataForm"
             :rules="dataRule"
             ref="dataForm"
             @keyup.enter.native="dataFormSubmit()"
             label-width="140px">
      <el-form-item label="用户ID" prop="userId">
        <el-input v-model="dataForm.userId"
                  :disabled="true"
                  placeholder="用户ID"></el-input>
      </el-form-item>
      <el-form-item label="用户名" prop="userName">
        <el-input v-model="dataForm.userName"
                  :disabled="true"
                  placeholder="用户名"></el-input>
      </el-form-item>
      <el-form-item label="借贷状态" prop="loanStatus">
        <el-radio-group v-model="dataForm.loanStatus">
          <el-radio :label="1">正常</el-radio>
          <el-radio :label="2">禁止</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="可贷金额(借贷)" prop="loanCanAmount">
        <el-input-number v-model="dataForm.loanCanAmount"
                        :precision="2"
                        :step="100"
                        :min="0"
                        placeholder="可贷金额"></el-input-number>
      </el-form-item>
      <el-form-item label="已贷金额(借贷)" prop="loanAlreadyAmount">
        <el-input-number v-model="dataForm.loanAlreadyAmount"
                        :precision="2"
                        :step="100"
                        :min="0"
                        placeholder="已贷金额"></el-input-number>
      </el-form-item>
      <el-form-item label="是否老客户" prop="isOldUser">
        <el-radio-group v-model="dataForm.isOldUser">
          <el-radio :label="1">老客户</el-radio>
          <el-radio :label="2">新客户</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="购买量化机器状态" prop="createRobotStatus">
        <el-radio-group v-model="dataForm.createRobotStatus">
          <el-radio :label="1">正常</el-radio>
          <el-radio :label="2">禁止</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="提币状态" prop="txState">
        <el-radio-group v-model="dataForm.txState">
          <el-radio :label="1">正常</el-radio>
          <el-radio :label="2">禁止</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="期权预设结果" prop="optionPreResult">
        <el-radio-group v-model="dataForm.optionPreResult">
          <el-radio :label="-1">亏损</el-radio>
          <el-radio :label="0">未设置</el-radio>
          <el-radio :label="1">盈利</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <span slot="footer"
          class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary"
                 @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { Debounce } from '@/utils/debounce'
export default {
  data () {
    return {
      visible: false,
      title: '',
      isShow: 0,
      uid: '',
      dataForm: {
        userId: '',
        userName: '',
        loanStatus: 1,
        loanCanAmount: 0,
        loanAlreadyAmount: 0,
        isOldUser: 2,
        createRobotStatus: 1,
        txState: 1,
        optionPreResult: 0
      },
      dataRule: {}
    }
  },
  methods: {
    init (uid, title, isShow, row) {
      this.visible = true
      this.title = title
      this.isShow = isShow
      this.uid = uid
      this.$nextTick(() => {
        this.$refs.dataForm.resetFields()
      })
      if (row) {
        this.dataForm.userId = row.userId
        this.dataForm.userName = row.userName || row.nickName || ''
        this.dataForm.loanStatus = row.loanStatus || 1
        this.dataForm.loanCanAmount = row.loanCanAmount ? Number(row.loanCanAmount) : 0
        this.dataForm.loanAlreadyAmount = row.loanAlreadyAmount ? Number(row.loanAlreadyAmount) : 0
        this.dataForm.isOldUser = row.isOldUser || 2
        this.dataForm.createRobotStatus = row.createRobotStatus || 1
        this.dataForm.txState = row.txState || 1
        this.dataForm.optionPreResult = row.optionPreResult || 0
      }
    },
    handClose () {
      this.visible = false
    },
    dataFormSubmit: Debounce(function () {
      this.$http({
        url: this.$http.adornUrl('/admin/user/updateUserStatus'),
        method: 'post',
        data: this.$http.adornData({
          userId: this.dataForm.userId,
          loanStatus: this.dataForm.loanStatus,
          loanCanAmount: this.dataForm.loanCanAmount,
          loanAlreadyAmount: this.dataForm.loanAlreadyAmount,
          isOldUser: this.dataForm.isOldUser,
          createRobotStatus: this.dataForm.createRobotStatus,
          txState: this.dataForm.txState,
          optionPreResult: this.dataForm.optionPreResult
        })
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$message({
            message: '操作成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              this.visible = false
              this.$emit('refreshDataList')
            }
          })
        } else {
          this.$message.error(data.msg)
        }
      })
    })
  }
}
</script>