<template>
  <el-dialog
    :title="'交易对修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="120px"
     label-position="top">


      <el-form-item label="交易对ID">
        <el-input v-model="dataForm.uuid" placeholder=""></el-input>
      </el-form-item>

      <el-form-item label="报价货币">
        <el-select v-model="dataForm.quoteCurrency" placeholder="USDT">
            <el-option label="USDT" value="USDT"></el-option>
            <el-option label="BTC" value="BTC"></el-option>
            <el-option label="ETH" value="ETH"></el-option>
          </el-select>     
      </el-form-item>

      <el-form-item label="币种">
        <el-select v-model="dataForm.symbolFullName" placeholder="请选择币种">
            <el-option
                v-for="item in option"
                :key="item.symbol"
                :label="item.symbol"
                :value="item.symbol"
              >
            </el-option>
          </el-select>     
      </el-form-item>

      <el-form-item label="价格精度" prop="decimals">
        <el-input v-model="dataForm.decimals" placeholder=""></el-input>
      </el-form-item>
 
      <el-form :inline="true" label-position="top">          

          <el-form-item label="前端显示状态" style="width: 200px;">
            <el-switch
              v-model="showStatus3"
              active-color="#13ce66"
              @change="showStatus(showStatus3)">
            </el-switch>
          </el-form-item>
        
          <el-form-item label="交易状态" style="width: 200px;">
            <el-switch
              v-model="tradeStatus3"
              active-color="#13ce66"
              @change="tradeStatus(tradeStatus3)">
            </el-switch>
          </el-form-item>
    
      </el-form>


      <el-row>
        <el-col :span="12">
          <el-form-item label="最小交易量" style="width: 200px;" prop="pips">
            <el-input v-model="dataForm.pips" placeholder=""></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">      
          <el-form-item label="最小交易额" style="width: 200px;" prop="pipsAmount">
            <el-input v-model="dataForm.pipsAmount" placeholder=""></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- <el-form :inline="true" label-position="top">          
        



      </el-form> -->
      <!-- <el-row>
        <el-col :span="12">

        </el-col>
        <el-col :span="12">      
    
        </el-col>
      </el-row> -->
      <el-form-item label="资金密码" prop="loginSafeword">
        <el-input v-model="dataForm.loginSafeword" type="password" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="排序">
        <el-input v-model="dataForm.sorted" placeholder=""></el-input>
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
  import { encrypt } from "@/utils/crypto";
  export default {
    data () {
      return {
        showStatus3:true,
        tradeStatus3:true,
        visible: false,
        menuList: [],
        option: [
        ],
        menuListTreeProps: {
          label: 'name',
          children: 'children'
        },
        dataForm: {
          id: 0,
          roleName: '',
          remark: '',
          loginSafeword:'',
          // showStatus1: true,
          // showStatus2: false,
          // tradeStatus2: false,
        },
        dataRule: {
          roleName: [
            { required: true, message: '角色名称不能为空', trigger: 'blur' },
            { pattern: /\s\S+|S+\s|\S/, message: '请输入正确的角色名称', trigger: 'blur' }
          ],
          remark: [
            { required: false, pattern: /\s\S+|S+\s|\S/, message: '输入格式有误', trigger: 'blur' }
          ],
          decimals: [
            { required: true, message: '价格精度不能为空', trigger: 'blur' }
          ],
          pips: [
            { required: true, message: '最小交易量不能为空', trigger: 'blur' }
          ],
          pipsAmount: [
            { required: true, message: '最小交易额不能为空', trigger: 'blur' }
          ],
          loginSafeword: [
            { required: true, message: '资金密码不能为空', trigger: 'blur' }
          ]
        },
        tempKey: -666666 // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
      }
    },
    methods: {
      init (row) {
        this.getAction()
        if(row==null){
          this.dataForm = {}
          this.dataForm.add = true;
        }else{
          this.dataForm = {...row} || {}
        }

        this.showStatus3 = this.dataForm.showStatus == "1"
        this.tradeStatus3 = this.dataForm.tradeStatus == "1"

        this.visible = true
      },
      dataFormSubmit: Debounce(function () {
        this.UpdateDataForm();
      }),
      UpdateDataForm(){
          this.dataForm.showStatus = this.showStatus3 ? "1" : "0";
          this.dataForm.tradeStatus = this.tradeStatus3 ? "1" : "0";
          //start
          this.$refs['dataForm'].validate((valid) => {
            if (valid) {
              this.$http({
                url: this.$http.adornUrl(`/normal/adminItemAction!/update.action`),
                method: 'post',
                data: this.$http.adornData({
                  'symbol':this.dataForm.symbol,
                  'uuid':this.dataForm.uuid,            //ID
                  "quoteCurrency": this.dataForm.quoteCurrency,       //报价货币
                  "symbolFullName": this.dataForm.symbolFullName,           //币种
                  "decimals": this.dataForm.decimals,   //价格精度
                  "showStatus": this.dataForm.showStatus,             //前端显示状态
                  "tradeStatus": this.dataForm.tradeStatus, //交易状态
                  "pips": this.dataForm.pips,     //最小交易量
                  "pipsAmount": this.dataForm.pipsAmount,                 //最小交易额
                  "sorted": this.dataForm.sorted,         //排序
                  'loginSafeword': encrypt(this.dataForm.loginSafeword),
                })
              }).then(({data}) => {
                if(data.code == 0){
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
         //end
      },
      //币种
      getAction() {
        this.$http({
          url: this.$http.adornUrl(
            "/normal/adminItemAction!/list"
          ),
          method: "get",
          params: this.$http.adornParams({
            type: "JP-stocks",
          }),
        }).then(({ data }) => {
          if(data.data.records){
              this.option = data.data.records.map((item,index) =>{
                return Object.assign({},{'symbol':item.symbol,'name':item.name})
              })
          }
        });
      },
      showStatus(data){
      },
      tradeStatus(data){
      }
    }
  }
</script>

<style>
  /* .customWidth{
      width:80%;
  } */
</style>
