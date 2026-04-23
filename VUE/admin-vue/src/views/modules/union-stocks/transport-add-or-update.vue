<template>
  <el-dialog :title="'调整'+title"
             :close-on-click-modal="false"
             :visible.sync="visible"
             @close = 'handClose'
             width="700px"
             class="transport-dialog"
  >
    <el-form :model="dataForm"
             ref="dataForm"
             @keyup.enter.native="dataFormSubmit()"
             label-width="0px">
      <el-form-item label=""
                    prop="transName">
                    <span>请输入正负调整值</span>
        <el-input v-model="dataForm.transName" type="number" @change="checkNumber()"
                  placeholder="请输入正负调整值"></el-input>
      </el-form-item>
      <el-form-item>
      <el-button v-if="isAuth('shop:transfee:update')"
                        type="primary"
                       size="middle"
                       class="el-icon-circle-plus-outline"
                       @click="addOrUpdateHandle()">{{pips}}</el-button>
            <el-button v-if="isAuth('shop:transfee:delete')"
                      type="primary"
                       size="middle"
                       class="el-icon-remove-outline"
                       @click="deleteHandle()">{{pips}}</el-button>
      </el-form-item>
      <el-form-item label=""
                    prop="second"
                    >
                    <span>生效趋势(秒，0秒为即时生效)</span>
        <el-input oninput="value=value.replace('-','')" v-model="second" type="number"
                  placeholder="0秒为即时生效"></el-input>
      </el-form-item>
      <div>调整值</div>
      <el-table :data="dataForm.transfees"
                border
                style="width: 100%;"
                class="table-con"
      >
        <el-table-column header-align="center"
                         align="center"
                         width="200"
                         label="原值">
          <template slot-scope="scope">
            <!-- <el-input type="number"
                        v-model="newPrice"
                        disabled></el-input> -->
     <div>{{newPrice}}</div>
          </template>
        </el-table-column>
        <el-table-column header-align="center"
                         align="center"
                         style="color:red;"
                         width="180"
                         label="调整后">
          <template slot-scope="scope">
            <el-form-item
                          label-width="0px"
                          >
                          <div class="spainput">{{afterValue}}</div>
              <!-- <el-input type="number" style="color:red !important;"
                        v-model="afterValue" class="spainput"
                        disabled></el-input> -->
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column header-align="center"
                         align="center"
                         label="累计修正值">
          <template slot-scope="scope">
            <el-form-item
                          label-width="0px"
                          >
              <!-- <el-input type="number"
                        v-model="adjustValue"
                        disabled></el-input> -->
                        <div>{{adjustValue}}</div>
            </el-form-item>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 30px;">生效趋势</div>
      <el-table :data="dataForm.transfeeFrees"
                border
                style="width: 100%;"
                class="table-con"
      >
        <el-table-column header-align="center"
                         align="center"
                         width="338"
                         label="待生效值">
          <template slot-scope="scope">
            <!-- <el-input type="number"
                        v-model="delayValue"
                        disabled></el-input> -->
                        <div>{{delayValue}}</div>
          </template>
        </el-table-column>
        <el-table-column header-align="center"
                         align="center"
                         width="320"
                         label="时间(秒)">
          <template slot-scope="scope">
            <el-form-item
                          label-width="0px"
                          >
              <!-- <el-input type="number"
                        v-model="delaySecond"
                        disabled></el-input> -->
                        <div>{{ delaySecond }}</div>
            </el-form-item>
          </template>
        </el-table-column>
      </el-table>
    </el-form>
    <span slot="footer"
          class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="loading"
                 @click="dataFormSubmit()">确定</el-button>
    </span>
    <!-- 弹窗, 新增 / 修改 -->
    <!-- <add-or-update v-if="addOrUpdateVisible"
                   ref="addOrUpdate"
                   @refreshDataList="getDataList"></add-or-update> -->
  </el-dialog>
</template>

<script>
import { Debounce } from '@/utils/debounce'
// import AddOrUpdate from './transcity-add-or-update'
export default {
  data () {
    return {
      // hasFreeCondition: 0,
      visible: false,
      title:'',
      pips:'',
      loading:false,
      adjustValue:'',//累计修正的值
      afterValue:'',//调整后值
      delaySecond:'',//生效趋势
      delayValue:'',// 待生效的值
      newPrice:'',//原值
      second:0,
      mafterValue:0,
      addOrUpdateVisible: false,
      dataForm: {
        // hasFreeCondition: false,
        transName: 0,
       
        // createTime: '',
        // chargeType: 0,
        // transportId: 0,
        // isFreeFee: 0,
        transfees: [{ cityList: [], status: 1 }],
        transfeeFrees: [{ freeCityList: [], freeType: 0 }]
      },
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10 // 每页显示多少条
      },
      editVisible: false
    }
  },
  created(){
   // this.init(id)
  },
  components: {
    // AddOrUpdate
  },
  watch: {
    
    // 如果当前对话框不可见，则关闭选择城市的对话框
    visible: function (val) {
      if (!val) {
        this.addOrUpdateVisible = false
      }
    }
  },
  computed: {
    tableTitle () {
      var titles = [['调整后', '累计修正值', '续件(个)', '续费(元)'], ['首重(kg)', '运费(元)', '续重(kg)', '续费(元)'], ['首体积(m³)', '运费(元)', '续体积(m³)', '续费(元)']]
      if (this.dataForm.chargeType) {
        return titles[this.dataForm.chargeType]
      }
      return titles[0]
    }
  },
  methods: {
    init (id) {
      this.getDataList (id)
      this.title = id
      this.visible = true
      this.dataForm.transportId = id || 0
      this.$nextTick(() => {
        this.$refs.dataForm.resetFields()
        this.dataForm = {
          // hasFreeCondition: false,
          transName: 0,
          createTime: '',
          chargeType: 0,
          transportId: 0,
          isFreeFee: 0,
          transfees: [{ cityList: [], status: 1 }],
          transfeeFrees: [{ freeCityList: [], freeType: 0 }]
        }
      })
      // if (this.dataForm.transportId) {
      //   this.$http({
      //     // 获取运费模板数据
      //     url: this.$http.adornUrl(`/shop/transport/info/${this.dataForm.transportId}`),
      //     method: 'get'
      //   }).then(({ data }) => {
      //     if (data.isFreeFee) {
      //       data.transfees[0].status = 0
      //     } else {
      //       data.transfees[0].status = 1
      //     }
      //     this.dataForm = data
      //     this.dataForm.hasFreeCondition = !!data.hasFreeCondition
      //   })
      // }
    },
    handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
        this.second = 0
        this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
      },
    getDataList (id) {
      this.$http({
              url: this.$http.adornUrl(
                "/normal/adminMarketQuotationsManageAction!/showModal.action"
              ),
              method: "get",
              params: this.$http.adornParams({
                symbol:id
              }),
            }).then(({ data }) => {
              if(data){    
                if(data.data.afterValue){
                  this.afterValue = data.data.afterValue
                }else{
                  this.afterValue = data.data.newPrice
                }
                if(data.data.pips*1 == 0){
                  this.pips = 0.01
                }else{
                  this.pips = data.data.pips
                }
                this.mafterValue =  this.afterValue        
                this.adjustValue = data.data.adjustValue
                this.delaySecond = data.data.delaySecond
                this.delayValue = data.data.delayValue
                this.newPrice = data.data.newPrice
              }
              // if(data.records){
              //     this.option = data.records.map((item,index) =>{
              //       return Object.assign({},{'symbol':item.symbol,'name':item.name})
              //     })
              // }
            });
    },
    // 添加运费项
    addTransfee () {
      this.editVisible = true
      this.dataForm.transfees.push({ cityList: [], status: 1 })
    },

    // 添加指定包邮条件
    // addTransfeeFree () {
    //   if (this.dataForm.hasFreeCondition) {
    //     this.dataForm.transfeeFrees.push({ freeCityList: [], freeType: 0 })
    //   }
    // },
    // 删除指定包邮条件
    deleteTransfeeFree (rowIndex) {
      this.dataForm.transfeeFrees.splice(rowIndex, 1)
    },
    
    checkNumber () { //正负值计算
      // if(!this.dataForm.transName){
      //     this.afterValue = this.mafterValue
      //   }
        let m = this.adjustValue
        let n = this.afterValue
        //this.afterValue = this.mafterValue
        //this.afterValue = this.dataForm.transName
        this.afterValue = (n*1000000000 + this.dataForm.transName*1000000000)/1000000000
        this.adjustValue = (m*1000000000 + this.dataForm.transName*1000000000)/1000000000
    },
      
    addOrUpdateHandle (rowIndex) {//正值添加按钮
      let m = this.adjustValue
      let n = this.afterValue
      let p = this.dataForm.transName
      this.dataForm.transName = (p*1000000000 + this.pips*1000000000)/1000000000
      this.afterValue = (n*1000000000 + this.pips*1000000000)/1000000000
      this.adjustValue = (m*1000000000 + this.pips*1000000000)/1000000000
      //this.afterValue = (this.afterValue*1000000000 + this.pips*1000000000)/1000000000
    },
        // 删除运费项
    deleteHandle (rowIndex) {//负值添加按钮
      let m = this.adjustValue
      let n = this.afterValue
      let p = this.dataForm.transName
      this.dataForm.transName = (p*1000000000 - this.pips*1000000000)/1000000000
      this.afterValue = (n*1000000000 - this.pips*1000000000)/1000000000
      this.adjustValue = (m*1000000000 - this.pips*1000000000)/1000000000
      //this.afterValue = (this.afterValue*1000000000 - this.pips*1000000000)/1000000000
    },
    /**
     * 保留整数并小于零的数设为0
     */
    getNumber (num) {
      num = Math.round(num)
      return num < 0 ? 0 : num
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.loading = true
          this.dataForm.transfees[0].cityList = []
          this.$http({
            url: this.$http.adornUrl(`/normal/adminMarketQuotationsManageAction!/adjust.action`),
            method: 'post',
            data: this.$http.adornData({
              'second': this.second,
              'symbol': this.title,
              'value': this.dataForm.transName,
            })
          }).then(({ data }) => {
            if(data.code == 0){
              this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1000,
              onClose: () => {
                this.visible = false
                this.loading = false
                this.$emit('refreshDataList', this.page)
              }
            })
            }else{
              this.$message({
              message: data.msg,
              type: 'error',
              duration: 1000,
              onClose: () => {
                this.loading = false
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

<style lang="scss" scoped>
.transport-dialog .table-con .el-form-item {
  margin-top: 16px;
  margin-bottom: 16px!important;
}
::v-deep.spainput{
  color: red;
}
::v-deep th {
  background: rgb(38,50,56);
  color: #fff;
}
</style>
