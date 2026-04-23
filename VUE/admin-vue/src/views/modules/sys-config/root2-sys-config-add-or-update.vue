<template>
  <el-dialog
    :title="'修改'"
    :close-on-click-modal="false"
    @close = 'handClose'
    :visible.sync="visible">
    <el-form :model="dataForm" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="0px">
      <el-form-item :label="dataForm.notes" label-width="auto">
      </el-form-item>
      <el-form-item label="">
        <el-input type="textarea" v-model="dataForm.svalue"></el-input>
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
        roleList: [],
        old:{},
        dataForm: {
          vaule: '',
          code: ''
        }
      }
    },
    methods: {
      init (id) {
        this.old = id;
        this.dataForm = JSON.parse(JSON.stringify(id))
        this.visible = true
      },
      handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
     this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
      },
      dataFormCancel(){
        this.visible = false
      },
      // 表单提交
      dataFormSubmit: Debounce(function () {

        let sysparaDTO = {
          'code': this.dataForm.code,
          'value': this.dataForm.svalue
        };

        this.$http({
          url: this.$http.adornUrl(`/normal/adminSysparaAction!/update.action`),
          method: 'post',
          data: this.$http.adornData(sysparaDTO)
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
        

      })
    }
  }
</script>
