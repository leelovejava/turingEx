<template>
  <el-dialog
    :title="'请输入驳回原因'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="0px">
      <el-form-item label="">
        <el-input
            type="textarea"
            placeholder="驳回原因"
            v-model="dataForm.content"
            maxlength="500"
            :autosize="{ minRows: 8, maxRows: 8}"
            show-word-limit
          >
        </el-input>
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
      init (id) {
        if(this.$refs["dataForm"]){
          this.$refs["dataForm"].clearValidate();
          this.$refs["dataForm"].resetFields()
        }
        this.dataForm = {};
        this.dataForm.id = id || 0
        // this.dataForm.content = "";
        this.visible = true
      },
      // 表单提交
      dataFormSubmit: Debounce(function () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            //
            this.$http({
              url: this.$http.adornUrl('/highLevelAuth/examine'),
              method: 'post',
              data: this.$http.adornData({
                "content": this.dataForm.content,
                "id": this.dataForm.id,
                "type": 2
              })
            }).then(({ data }) => {
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
                message:data.msg,
                type: 'error',
                duration: 1500,
                onClose: () => {
                  this.visible = false
                }
              })
              }

            })
            //
            
          }
        })
      }),
      // 获取数据列表
      getDataList () {
        //
        this.$http({
          url: this.$http.adornUrl('/highLevelAuth/list'),
          method: 'post',
          data:this.$http.adornData({
                'current': this.page.currentPage,
                'size': this.page.pageSize,
                'rolename': this.dataForm.rolename,
                'userCode': this.dataForm.userCode,
                'userId': this.dataForm.userId,
                'startTime': this.dataForm.startTime,
                'endTime': this.dataForm.endTime
          })
        }).then(({ data }) => {
          if(data.code==0){
            this.dataList = data.data.records
            this.page.total = data.data.total
            this.dataListLoading = false
          }else {
            this.$message({
                  message: data.msg,
                  type: "error",
                });
          }
        })
        //
      }

    }
  }
</script>
