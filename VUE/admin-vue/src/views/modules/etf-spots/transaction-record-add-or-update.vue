<template>
  <el-dialog
    :title="dataForm.add ? '新增机器人' : '修改机器人'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    customClass="customWidth">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="120px">
      <el-form-item label="交易对">
        <el-select v-model="dataForm.symbol" placeholder="NEEQ/USD">
            <el-option label="NEEQ/USD" value="NEEQ/USD"></el-option>
            <el-option label="BTC/USDT" value="BTC/USDT"></el-option>
          </el-select>     
      </el-form-item>

      <el-form-item label="机器人ID">
        <el-input v-model="dataForm.uuid" placeholder=""></el-input>
      </el-form-item>

      <el-form :inline="true">          

          <el-form-item label="机器人账号">
            <el-input v-model="dataForm.username" placeholder=""></el-input>
          </el-form-item>
        
          <el-form-item label="机器人密码">
            <el-input v-model="dataForm.password" placeholder=""></el-input>
          </el-form-item>

          <el-form-item label="机器人资金(USDT)">
            <el-input v-model="dataForm.withdraw_limit_num" placeholder=""></el-input>
          </el-form-item>
    
      </el-form>

      <el-form-item label="机器人状态"
                    size="mini"
                    prop="status">
        <el-radio-group v-model="dataForm.RobotStatus">
          <el-radio :label="1">启动</el-radio>
          <el-radio :label="0">停止</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="最低交易量">
        <el-input v-model="dataForm.zuidi" placeholder=""></el-input>
      </el-form-item>


      <el-form :inline="true">          

        <el-form-item label="交易量随机因子(%)">
          <el-input v-model="dataForm.suiji1" placeholder=""></el-input>
        </el-form-item>

        <el-form-item label="交易量随机因子(%)">
          <el-input v-model="dataForm.suiji2" placeholder=""></el-input>
        </el-form-item>

        <el-form-item label="交易量随机因子(%)">
          <el-input v-model="dataForm.suiji3" placeholder=""></el-input>
        </el-form-item>

      </el-form>

      <el-form :inline="true">          

        <el-form-item label="交易量随机因子(%)">
          <el-input v-model="dataForm.suiji4" placeholder=""></el-input>
        </el-form-item>

        <el-form-item label="交易量随机因子(%)">
          <el-input v-model="dataForm.suiji5" placeholder=""></el-input>
        </el-form-item>

        <el-form-item label="交易量随机因子(%)">
          <el-input v-model="dataForm.suiji6" placeholder=""></el-input>
        </el-form-item>

      </el-form>


      <el-form-item label="价格精度要求" prop="remark">
        <el-input v-model="dataForm.CurrencyPricePrecision" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="数量精度要求" prop="remark">
        <el-input v-model="dataForm.CurrencyQuantityPrecision" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="买卖盘最高差价" prop="remark">
        <el-input v-model="dataForm.MaximumPriceDifferenceBetweenBuyingAndSellingOrders" placeholder=""></el-input>
      </el-form-item>

      <el-form :inline="true">          
        <el-form-item label="买单挂单数量">
          <el-input v-model="dataForm.buy_num" placeholder=""></el-input>
        </el-form-item>

        <el-form-item label="卖单挂单数量">
          <el-input v-model="dataForm.sell_num" placeholder=""></el-input>
        </el-form-item>
      </el-form>

      <el-form-item label="价格变化步长(%)" prop="remark">
        <el-input v-model="dataForm.StepSizeOfPriceChange" placeholder=""></el-input>
      </el-form-item>

      <el-form :inline="true">          
        <el-form-item label="最大下单时间间隔(秒)">
          <el-input v-model="dataForm.max_OrderInterval" placeholder=""></el-input>
        </el-form-item>

        <el-form-item label="最小下单时间间隔(秒)">
          <el-input v-model="dataForm.OrderInterval" placeholder=""></el-input>
        </el-form-item>
      </el-form>

      <el-form :inline="true">          
        <el-form-item label="最大下单数量(手)">
          <el-input v-model="dataForm.max_InitialNumberOfOrders" placeholder=""></el-input>
        </el-form-item>

        <el-form-item label="最小下单数量(手)">
          <el-input v-model="dataForm.InitialNumberOfOrders" placeholder=""></el-input>
        </el-form-item>
      </el-form>

      <el-form :inline="true">          
        <el-form-item label="高频量比">
          <el-input v-model="dataForm.max_frequency" placeholder=""></el-input>
        </el-form-item>

        <el-form-item label="低频量比">
          <el-input v-model="dataForm.min_frequency" placeholder=""></el-input>
        </el-form-item>
      </el-form>

      <el-form :inline="true">          
        <el-form-item label="涨幅权重">
          <el-input v-model="dataForm.Increase_weight" placeholder=""></el-input>
        </el-form-item>

        <el-form-item label="大盘增长幅度(%)">
          <el-input v-model="dataForm.Increase_percentage" placeholder=""></el-input>
        </el-form-item>

        <el-form-item label="大盘">
          <el-select v-model="dataForm.name2" placeholder="道琼斯">
              <el-option label="道琼斯" value="0"></el-option>
              <el-option label="纳斯达克" value="1"></el-option>
              <el-option label="标普500" value="2"></el-option>
              <el-option label="上证指数" value="3"></el-option>
              <el-option label="深证指数" value="4"></el-option>
              <el-option label="恒生指数" value="5"></el-option>
              <el-option label="国企指数" value="6"></el-option>
            </el-select>     
        </el-form-item>

      </el-form>


      
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
      init (row) {
        if(row==null){
          this.dataForm = {}
          this.dataForm.add = true;
        }else{
          this.dataForm = row || {}
        }
        
        this.visible = true

        // this.$http({
        //   url: this.$http.adornUrl('/sys/menu/table'),
        //   method: 'get',
        //   params: this.$http.adornParams()
        // }).then(({data}) => {
        //   this.menuList = treeDataTranslate(data, 'menuId', 'parentId')
        // }).then(() => {
        //   this.visible = true
        //   this.$nextTick(() => {
        //     this.$refs['dataForm'].resetFields()
        //     this.$refs.menuListTree.setCheckedKeys([])
        //   })
        // }).then(() => {
        //   if (this.dataForm.id) {
        //     this.$http({
        //       url: this.$http.adornUrl(`/sys/role/info/${this.dataForm.id}`),
        //       method: 'get',
        //       params: this.$http.adornParams()
        //     }).then(({data}) => {
        //       this.dataForm.roleName = data.roleName
        //       this.dataForm.remark = data.remark
        //       var idx = data.menuIdList.indexOf(this.tempKey)
        //       if (idx !== -1) {
        //         data.menuIdList.splice(idx, data.menuIdList.length - idx)
        //       }
        //       this.$refs.menuListTree.setCheckedKeys(data.menuIdList)
        //     })
        //   }
        // })
        //
      },
      // 表单提交
      dataFormSubmit: Debounce(function () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            if(this.dataForm.add){
              delete this.dataForm.add
              this.$parent.AddData(this.dataForm);
            }
            
            this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.visible = false
                  this.$emit('refreshDataList')
                }
              })            //
            // this.$http({
            //   url: this.$http.adornUrl(`/sys/role`),
            //   method: this.dataForm.id ? 'put' : 'post',
            //   data: this.$http.adornData({
            //     'roleId': this.dataForm.id || undefined,
            //     'roleName': this.dataForm.roleName,
            //     'remark': this.dataForm.remark,
            //     'menuIdList': [].concat(this.$refs.menuListTree.getCheckedKeys(), [this.tempKey], this.$refs.menuListTree.getHalfCheckedKeys())
            //   })
            // }).then(({data}) => {
            //   this.$message({
            //     message: '操作成功',
            //     type: 'success',
            //     duration: 1500,
            //     onClose: () => {
            //       this.visible = false
            //       this.$emit('refreshDataList')
            //     }
            //   })
            // })
            //
          }
        })
      })
    }
  }
</script>

<style>
  .customWidth{
      width:80%;
  }
</style>
