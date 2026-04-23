<template>
  <el-dialog
    :title="'修改货币汇率'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="汇率" prop="rata">
        <el-input v-model="dataForm.rata" placeholder="请输入"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
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
          roleName: '',
          remark: ''
        },
        dataRule: {
          roleName: [
            { required: true, message: '角色名称不能为空', trigger: 'blur' },
            { pattern: /\s\S+|S+\s|\S/, message: '请输入正确的角色名称', trigger: 'blur' }
          ],
          remark: [
            { required: false, pattern: /\s\S+|S+\s|\S/, message: '输入格式有误', trigger: 'blur' }
          ]
        },
        tempKey: -666666 // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
      }
    },
    methods: {
      init (obj) {
        console.log("init = " + JSON.stringify(obj));
        
        this.dataForm = JSON.parse(JSON.stringify(obj))
        this.visible = true
        
      },
      // 表单提交
      dataFormSubmit: Debounce(function () {

        // {
        //   "currency": "",
        //   "currencySymbol": "",
        //   "name": "",
        //   "outOrIn": "",
        //   "rata": 0,
        //   "uuid": ""
        // }

        console.log("dataformsubmit = " + JSON.stringify(this.dataForm));

        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/rate/exchangeRate/save`),
              method: 'post',
              data: this.$http.adornData({
                'rata': this.dataForm.rata,
                'uuid': this.dataForm.uuid,
              })
            }).then(({data}) => {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.visible = false
                  this.$emit('refreshDataList')
                }
              })
            })
          }
        })
      })
    }
  }
</script>
