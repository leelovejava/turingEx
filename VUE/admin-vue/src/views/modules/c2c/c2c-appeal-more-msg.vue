<template>
  <el-dialog title="详细信息"
             :close-on-click-modal="false"
             :visible.sync="visible"
             @close = 'handClose'
             width="800px"
             class="transport-dialog"
  >
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="140px">
      <el-form-item label="申述理由" prop="">
        <el-input v-model="dataForm.reason" disabled
                  placeholder="申述理由">
        </el-input>
      </el-form-item>
      <el-form-item label="申诉描述" prop="">
        <el-input v-model="dataForm.description" disabled
                  placeholder="申诉描述">
        </el-input>
      </el-form-item>
      <el-form-item label="申诉凭证" prop="">
        <img :src="dataForm.img" alt="" width="200" height="200">
      </el-form-item>
      <el-form-item label="联系人" prop="">
        <el-input v-model="dataForm.name" disabled
                  placeholder="联系人">
        </el-input>
      </el-form-item>
      <el-form-item label="联系电话" prop="">
        <el-input v-model="dataForm.phone" disabled
                  placeholder="联系电话">
        </el-input>
      </el-form-item>
    </el-form>
    <span slot="footer"
          class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary"
                  @click="visible = false">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { Debounce } from '@/utils/debounce'
import { encrypt } from '@/utils/crypto'
export default {
  data () {
    return {
      hasFreeCondition: 0,
      visible: false,
      addOrUpdateVisible: false,
      dataForm: {
       id:'',
      },
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10 // 每页显示多少条
      },
      dataRule: {
        login_safeword: [
          { required: true, message: "资金密码不能为空", trigger: "blur" },
        ],
      },
    }
  },
  methods: {
    init (row) {
      console.log(row)
      this.dataForm = row
      this.visible = true
      this.$nextTick(() => {
      })
    },
    handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
     this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
      },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const formData = new FormData();
          const objDate = {...this.dataForm}
            // 添加需要提交的表单字段及其值到 FormData 对象
            for (let key in objDate) {
              if(key == 'login_safeword'){
                console.log(objDate[key])
                objDate[key] = encrypt(objDate[key])
                
              }
              formData.append(key, objDate[key]);
            }
              // 发送表单数据到后台接口
              this.$http({
              url: this.$http.adornUrl(`/c2cAdvert/backAllDeposit`),
              method: 'post',
              data: formData,
              headers: {
                'Content-Type': 'multipart/form-data' // 设置请求头为 multipart/form-data
              }
            })
            .then(response  => {
              if(response.data.code==0){
                this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.$emit("refreshDataList");
                },
              });
              }else{
                  this.$message({
                  message:response.data.msg,
                  type: "error",
                  duration: 1500,
                  onClose: () => {
                  },
                });
              }
            })
            .catch(error => {
            });
        }
      })
    })
  }
}
</script>

<style lang="scss" scoped>
.transport-dialog .table-con .el-form-item {
  margin-top: 16px;
  margin-bottom: 16px!important;
}
</style>
