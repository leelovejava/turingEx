<template>
  <div class="mod-index-img">
    <el-dialog :title="tittle"
               :close-on-click-modal="false"
               :visible.sync="visible"
               @close = 'handClose'
               >
      <el-form :model="dataForm"
               ref="dataForm"
               :rules="dataRule"
               label-width="100px">
        <el-form-item v-if="row" label="用户名" prop="userName"> 
          <el-input v-model="dataForm.userName" disabled
                  placeholder="用户名"></el-input>
        </el-form-item>
        <el-form-item v-if="userOption" label="" prop="">
         <div class="green">
         1.多个UID请用英文逗号隔开</br>
         2.全选默认选择全部用户，如有重复，新增数据会覆盖已有用户数据
         </div>
        </el-form-item>
        <el-row v-if="userOption">
          <el-col :span="21"><el-form-item label="用户UID" prop="userCode">
          <el-input v-model="dataForm.userCode" :disabled="row?true:false"
                  placeholder="用户UID" clearable></el-input>
        </el-form-item ></el-col>
          <el-col :span="1" style="margin-left:20px"> <el-checkbox border v-model="checked" @change="checkboxChanged">全选</el-checkbox></el-col>
        </el-row>  
        <el-form-item v-else label="用户UID" prop="userCode">
          <el-input v-model="dataForm.userCode" :disabled="row?true:false"
                  placeholder="用户UID"></el-input>
         
        </el-form-item>  
        <el-form-item label="类型">
          <el-select v-model="options.value" @change="changeVal"
                     placeholder="请选择">
            <el-option v-for="item in options"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value">
            </el-option>
          </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="">
        <el-input
          type="textarea"
          placeholder="请输入内容"
          v-model="dataForm.remark"
          maxlength="500"
          :autosize="{ minRows: 8, maxRows: 8 }"
          show-word-limit
        >
        </el-input>
      </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()" v-loading="dataListLoading">确定</el-button>
    </span>
    </el-dialog>
    <!-- 商品选择弹窗-->
    <prods-select v-if="prodsSelectVisible"
                   ref="prodsSelect"
                   :isSingle="true"
                   @refreshSelectProds="selectCouponProds"></prods-select>
  </div>
</template>

<script>
import PicUpload from '@/components/pic-upload'
import ProdsSelect from '@/components/prods-select'
import { Debounce } from '@/utils/debounce'
export default {
  data () {
    return {
      dataForm: {
        userCode: '',
        userName:'',
        remark:'',
        uuid:'',
      },
      tittle:'',
      row:'',
      checked:false,
      newUserCode:[],
      options: [], // 模块
      userOption:'',//获取用户
      dataListLoading: false,
      dataRule: {
        userCode: [
          {required: true, message: 'UID不能为空', trigger: 'blur'}
        ],
        remark: [
          {required: true, message: '备注不能为空', trigger: 'blur'}
        ],
      },
      // 关联数据
      card: {
        id: 0,
        pic: '',
        name: '',
        realData: {
          prod: [],
          shop: [],
          activity: []
        }
      },
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10 // 每页显示多少条
      },
      prodsSelectVisible: false,
      visible: false
    }
  },
  components: {
    PicUpload,
    ProdsSelect
  },
  methods: {
    // 获取分类数据
    init (arr,row,user) {
      this.options = arr
      this.row = row || ''
      this.userOption = user || ''
      if(row){
        this.dataForm.remark = row.remark
        this.dataForm.uuid = row.uuid
        this.dataForm.userCode = row.userCode
        this.dataForm.userName = row.userName
        this.options.value = row.type
        this.tittle = '修改'
      }else if(user){
        this.newUserCode = this.userOption.map(item => item.userCode);
        this.tittle = '批量新增'
        this.options.value = this.options[0].value
      } else {
        this.tittle = '新增'
        this.options.value = this.options[0].value
      }
      this.visible = true   
      // if (this.dataForm.imgId) {
      //   // 获取产品数据
      //   this.$http({
      //     url: this.$http.adornUrl(`/admin/indexImg/info/${this.dataForm.imgId}`),
      //     method: 'get',
      //     params: this.$http.adornParams()
      //   }).then(({ data }) => {
      //     this.dataForm = data
      //     if (data.relation) {
      //       this.card.pic = data.pic
      //       this.card.name = data.prodName
      //       this.card.id = data.relation
      //     }
      //   })
      // } else {
      //   this.$nextTick(() => {
      //     this.$refs['dataForm'].resetFields()
      //     this.dataForm.imgUrl = ''
      //     this.relation = null
      //   })
      // }
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      if(this.row){ // 修改
        this.$refs['dataForm'].validate((valid) => {
        if (!valid) {
          return
        }
        this.$http({
          url: this.$http.adornUrl(`/normal/adminProfitAndLossConfigAction!/update.action`),
          method:'post',
          data: this.$http.adornData({
            remark:this.dataForm.remark,
            type:this.options.value,
            uuid:this.dataForm.uuid,
            symbolType: "INDIA-stocks", //forex->外汇,commodities->大宗商品，指数/ETF->indices, INDIA-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
          })
        }).then(({ data }) => {
          if(data.code==0){
            this.$message({
            message: '操作成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              this.visible = false
              this.$emit('refreshDataList', this.page)
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
      })
      }else if(this.userOption){ //批量新增
        this.$refs['dataForm'].validate((valid) => {
        if (!valid) {
          return
        }
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl(`/normal/adminProfitAndLossConfigAction!/addBatch.action`),
          method:'post',
          data: this.$http.adornData({
            remark:this.dataForm.remark,
            type:this.options.value,
            userCode:this.dataForm.userCode,
            symbolType: "INDIA-stocks", //forex->外汇,commodities->大宗商品，指数/ETF->indices, INDIA-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
          })
        }).then(({ data }) => {
          if(data.code==0){
            this.dataListLoading = false
            this.$message({
            message: '操作成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              this.visible = false
              this.$emit('refreshDataList', this.page)
            }
          })
          }else{
            this.dataListLoading = false
            this.$message({
            message: data.msg,
            type: 'error',
            duration: 1500,
            onClose: () => {
            }
          })
          }

        })
      })
      } else{
        this.$refs['dataForm'].validate((valid) => {
        if (!valid) {
          return
        }
        this.$http({
          url: this.$http.adornUrl(`/normal/adminProfitAndLossConfigAction!/add.action`),
          method:'post',
          data: this.$http.adornData({
            remark:this.dataForm.remark,
            type:this.options.value,
            userCode:this.dataForm.userCode,
            symbolType: "INDIA-stocks", //forex->外汇,commodities->大宗商品，指数/ETF->indices, INDIA-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
          })
        }).then(({ data }) => {
          if(data.code==0){
            this.$message({
            message: '操作成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              this.visible = false
              this.$emit('refreshDataList', this.page)
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
      })
      }

    }),
    handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
        this.checked = false
        this.userOption = ''
     this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
      },
      checkboxChanged() { //复选框  //newUserCode 全部的用户UID数组   //this.userOption全部的用户数组对象
      if (this.checked) {
        this.dataForm.userCode = this.newUserCode.join(",")
        console.log(this.dataForm.userCode);
      } else {
        this.dataForm.userCode = ''
        // 复选框被取消选中时的处理逻辑
        console.log(this.dataForm.userCode);
      }
    },
    // 删除关联数据
    deleteRelation () {
      this.dataForm.relation = null
    },
    // 打开选择商品
    addProd () {
      this.prodsSelectVisible = true
      this.$nextTick(() => {
        this.$refs.prodsSelect.init(this.card.realData.prod)
      })
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    // 添加指定商品
    selectCouponProds (prods) {
      this.card.realData.prods = prods
      if (prods.length) {
        let selectProd = prods[0]
        this.dataForm.relation = selectProd.prodId
        this.card.pic = selectProd.pic
        this.card.name = selectProd.prodName
        this.card.id = selectProd.prodId
      } else {
        this.card = {}
        this.relation = null
      }
    },
    addShop () {
      alert('选择店铺')
    },
    addActivity () {
      alert('选择活动')
    }
  }
}
</script>
<style lang="scss" scoped>
//card样式
.card-prod-bottom {
  position: relative;
  text-align: left;
  .card-prod-name {
    margin: auto;
    padding: 0 6px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    width: 118px;
    display: inline-block;
  }
  .card-prod-name-button {
    position: absolute;
    top: 24px;
    right: 10px;
  }
}
</style>
