<template>
  <el-dialog
    :title="'停盘时间'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close = 'handClose'>
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="代码编号" prop="symbol">
        <el-input v-model="dataForm.symbol" placeholder="请输入"></el-input>
      </el-form-item>
      <el-form-item v-if="row" label="名称" prop="symbolName">
        <el-input v-model="dataForm.symbolName" disabled placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="开盘时间" prop="openBjDate">
        <!-- <el-input v-model="dataForm.rata" placeholder="请输入"></el-input> -->
        <el-date-picker
          v-model="dataForm.openBjDate"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          placeholder="选择日期时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="停盘时间" prop="closeBjDate">
        <!-- <el-input v-model="dataForm.rata" placeholder="请输入"></el-input> -->
        <el-date-picker
          v-model="dataForm.closeBjDate"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          placeholder="选择日期时间">
        </el-date-picker>
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
  export default {
    data () {
      return {
        visible: false,
        row:'',
        menuList: [],
        menuListTreeProps: {
          label: 'name',
          children: 'children'
        },
        dataForm: {
          symbol: '',
          uuid:'',
          roleName: '',
          closeBjDate: '',
          openBjDate:'',
        },
        dataRule: {
          symbol: [
            { required: true, message: '代码编码不能为空', trigger: 'blur' },
          ],
          openBjDate: [
            { required: true, message: '开盘时间不能为空', trigger: 'blur' },
          ],
          closeBjDate: [
            { required: true, message: '停盘时间不能为空', trigger: 'blur' },
          ],
          remark: [
            { required: false, pattern: /\s\S+|S+\s|\S/, message: '输入格式有误', trigger: 'blur' }
          ]
        },
        tempKey: -666666 // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
      }
    },
    methods: {
      init (row) {
        this.row = row || ''
        if(row){
          this.dataForm.openBjDate = row.openBjDate
          this.dataForm.closeBjDate = row.closeBjDate
          this.dataForm.symbol = row.symbol
          this.dataForm.uuid = row.uuid
          this.dataForm.symbolName = row.symbolName
        }

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
        console.log("dataformsubmit = " + JSON.stringify(this.dataForm));

        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/syspara/openClose/save`),
              method: 'post',
              data: this.$http.adornData({
                'symbol': this.dataForm.symbol,
                'openBjDate': this.dataForm.openBjDate,
                'closeBjDate': this.dataForm.closeBjDate,
                'uuid': this.dataForm.uuid,
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
                duration: 1500,
                onClose: () => {
                  this.visible = false
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
