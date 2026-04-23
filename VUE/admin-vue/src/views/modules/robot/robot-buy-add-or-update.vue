<template>
  <el-dialog
    :title="dataForm.add ? '新增机器人' : '修改机器人'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    width='800px'>
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="120px">

      <el-form-item label="交易对" label-width="120px" style="background-color: white;" prop="symbol">
        <el-select v-model="dataForm.symbol" placeholder="请选择">
            <el-option label="NEEQ/USD" value="NEEQ/USD"></el-option>
            <el-option label="BTC/USDT" value="BTC/USDT"></el-option>
          </el-select>     
      </el-form-item>
      <el-form-item label="机器人ID" label-width="120px" prop="uuid">
        <el-input v-model="dataForm.uuid" placeholder=""></el-input>
      </el-form-item>

      <el-row>
        <el-col :span="8">
          <el-form-item label="机器人账号" prop="username">
            <el-input v-model="dataForm.username" placeholder="" :disabled="!dataForm.add"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="机器人密码" prop="password">
            <el-input v-model="dataForm.password" placeholder="" :disabled="!dataForm.add"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="机器人资金" prop="money">
            <el-input v-model="dataForm.money" placeholder=""></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="机器人状态"
                    size="mini"
                    label-width="120px" prop="runningStatus">
        <el-radio-group v-model="dataForm.runningStatus">
          <el-radio :label="1">启动</el-radio>
          <el-radio :label="0">停止</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="最低交易量" label-width="120px" prop="minmuanAmount">
        <el-input v-model="dataForm.minmuanAmount" placeholder=""></el-input>
      </el-form-item>


      <el-row>
        <el-col :span="8">
          <el-form-item label="交易量随机因子" prop="random1">
          <el-input v-model="dataForm.random1" placeholder=""></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="交易量随机因子" prop="random2">
          <el-input v-model="dataForm.random2" placeholder=""></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="交易量随机因子" prop="random3">
          <el-input v-model="dataForm.random3" placeholder=""></el-input>
        </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="8">
          <el-form-item label="交易量随机因子" prop="random4">
          <el-input v-model="dataForm.random4" placeholder=""></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="交易量随机因子" prop="random5">
          <el-input v-model="dataForm.random5" placeholder=""></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="交易量随机因子" prop="random6">
          <el-input v-model="dataForm.random6" placeholder=""></el-input>
        </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="价格精度要求" prop="priceDecimals">
        <el-input v-model="dataForm.priceDecimals" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="数量精度要求" prop="numDecimals">
        <el-input v-model="dataForm.numDecimals" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="买卖盘最高差价" prop="maxumPriceDiff">
        <el-input v-model="dataForm.maxumPriceDiff" placeholder=""></el-input>
      </el-form-item>

      <el-row>
        <el-col :span="12">
          <el-form-item label="买单挂单数量" prop="buyNum">
            <el-input v-model="dataForm.buyNum" placeholder=""></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="卖单挂单数量" prop="sellNum">
            <el-input v-model="dataForm.sellNum" placeholder=""></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="价格变化步长(%)" prop="step">
        <el-input v-model="dataForm.step" placeholder=""></el-input>
      </el-form-item>

      <el-row>
        <el-col :span="12">
          <el-form-item label="最大下单间隔(秒)" prop="maxmunInterval">
            <el-input v-model="dataForm.maxmunInterval" placeholder=""></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="最小下单间隔(秒)" prop="minmunInterval">
            <el-input v-model="dataForm.minmunInterval" placeholder=""></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="最大下单数量(手)" prop="maxmunNum">
            <el-input v-model="dataForm.maxmunNum" placeholder=""></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="最小下单数量(手)" prop="minmunNum">
            <el-input v-model="dataForm.minmunNum" placeholder=""></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="高频量比" prop="highFrequency">
            <el-input v-model="dataForm.highFrequency" placeholder=""></el-input>
          </el-form-item>

        </el-col>
        <el-col :span="12">
          <el-form-item label="低频量比" prop="lowFrequency">
            <el-input v-model="dataForm.lowFrequency" placeholder=""></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="8">
          <el-form-item label="涨幅权重" prop="weight">
            <el-input v-model="dataForm.weight" placeholder=""></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="大盘增长幅度(%)" prop="marketIncrease">
            <el-input v-model="dataForm.marketIncrease" placeholder=""></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="大盘" prop="followMarket">
            <el-select v-model="dataForm.followMarket" placeholder="请选择">
                <el-option label="道琼斯" value="道琼斯"></el-option>
                <el-option label="纳斯达克" value="纳斯达克"></el-option>
                <el-option label="标普500" value="标普500"></el-option>
                <el-option label="上证指数" value="上证指数"></el-option>
                <el-option label="深证指数" value="深证指数"></el-option>
                <el-option label="恒生指数" value="恒生指数"></el-option>
                <el-option label="国企指数" value="国企指数"></el-option>
              </el-select>     
          </el-form-item>
        </el-col>
      </el-row>


      
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
          symbol:[{ required: true, message: '不能为空', trigger: 'blur' },],
          uuid:[{ required: true, message: '不能为空', trigger: 'blur' },],
          username:[{ required: true, message: '不能为空', trigger: 'blur' },],
          password:[{ required: true, message: '不能为空', trigger: 'blur' },],
          money:[{ required: true, message: '不能为空', trigger: 'blur' },],
          minmuanAmount:[{ required: true, message: '不能为空', trigger: 'blur' },],

          random1:[{ required: true, message: '不能为空', trigger: 'blur' },],
          random2:[{ required: true, message: '不能为空', trigger: 'blur' },],
          random3:[{ required: true, message: '不能为空', trigger: 'blur' },],
          random4:[{ required: true, message: '不能为空', trigger: 'blur' },],
          random5:[{ required: true, message: '不能为空', trigger: 'blur' },],
          random6:[{ required: true, message: '不能为空', trigger: 'blur' },],

          priceDecimals:[{ required: true, message: '不能为空', trigger: 'blur' },],
          numDecimals:[{ required: true, message: '不能为空', trigger: 'blur' },],
          maxumPriceDiff:[{ required: true, message: '不能为空', trigger: 'blur' },],
          buyNum:[{ required: true, message: '不能为空', trigger: 'blur' },],
          sellNum:[{ required: true, message: '不能为空', trigger: 'blur' },],
          step:[{ required: true, message: '不能为空', trigger: 'blur' },],
          maxmunInterval:[{ required: true, message: '不能为空', trigger: 'blur' },],
          minmunInterval:[{ required: true, message: '不能为空', trigger: 'blur' },],
          maxmunNum:[{ required: true, message: '不能为空', trigger: 'blur' },],
          minmunNum:[{ required: true, message: '不能为空', trigger: 'blur' },],
          highFrequency:[{ required: true, message: '不能为空', trigger: 'blur' },],
          lowFrequency:[{ required: true, message: '不能为空', trigger: 'blur' },],
          weight:[{ required: true, message: '不能为空', trigger: 'blur' },],
          marketIncrease:[{ required: true, message: '不能为空', trigger: 'blur' },],

          runningStatus:[{ required: true, message: '不能为空', trigger: 'blur' },],
          followMarket:[{ required: true, message: '不能为空', trigger: 'blur' },],
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
          this.dataForm = {...row} || {}
        }
        if(this.$refs['dataForm']){
          this.$refs['dataForm'].clearValidate();
        }
        
        this.visible = true
      },
      // 表单提交
      dataFormSubmit: Debounce(function () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            // if(this.dataForm.add){
            //   delete this.dataForm.add
            //   this.$parent.AddData(this.dataForm);
            // }
            
            // this.$message({
            //     message: '操作成功',
            //     type: 'success',
            //     duration: 1500,
            //     onClose: () => {
            //       this.visible = false
            //       this.$emit('refreshDataList')
            //     }
            //   })
            //--------------------------------------------------------------------------
            this.$http({
              url: this.$http.adornUrl(`/etf/robot/save`),
              method: 'post',
              data: this.$http.adornData({
                'symbol': this.dataForm.symbol, //交易对
                'uuid': this.dataForm.uuid, //机器人ID
                'username': this.dataForm.username, //机器人账号
                'password': this.dataForm.password, //机器人密码
                'money': this.dataForm.money, //机器人资金
                'runningStatus': this.dataForm.runningStatus, //机器人状态
                'minmuanAmount': this.dataForm.minmuanAmount, //最低交易量

                'priceDecimals': this.dataForm.priceDecimals, //价格精度要求
                'numDecimals': this.dataForm.numDecimals, //数量精度要求
                'maxumPriceDiff': this.dataForm.maxumPriceDiff, //买卖盘最高差价
                'buyNum': this.dataForm.buyNum, //买单挂单数量
                'sellNum': this.dataForm.sellNum, //卖单挂单数量
                'step': this.dataForm.step, //价格变化步长(%)

                'maxmunInterval': this.dataForm.maxmunInterval, //最大下单间隔(秒)
                'minmunInterval': this.dataForm.minmunInterval, //最小下单间隔(秒)
                'maxmunNum': this.dataForm.maxmunNum, //最大下单数量(手)
                'minmunNum': this.dataForm.minmunNum, //最小下单数量(手)
                'highFrequency': this.dataForm.highFrequency, //高频量比
                'lowFrequency': this.dataForm.lowFrequency, //低频量比

                'weight': this.dataForm.weight, //涨幅权重
                'marketIncrease': this.dataForm.marketIncrease, //大盘增长幅度(%)

                'followMarket':this.dataForm.followMarket,

                'random1':this.dataForm.random1,
                'random2':this.dataForm.random2,
                'random3':this.dataForm.random3,
                'random4':this.dataForm.random4,
                'random5':this.dataForm.random5,
                'random6':this.dataForm.random6,
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

<style>
  .customWidth{
      width:700px;
  }
</style>
