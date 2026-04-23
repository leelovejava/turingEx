<template>
  <el-dialog :title="!row ? '新增C2C广告配置' : '修改C2C广告配置'" :close-on-click-modal="false" width="1200px"
    :visible.sync="visible" @close = 'handClose'>
    <el-scrollbar class="vertical-scrollbar">
      <div class="scroll-content">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()"
      label-width="140px">
      <el-form-item class="titleDivTwo" label="基础信息" prop="">
      </el-form-item>
      <el-form-item label="" >
        <span class="green">优先填写承兑商UID，以便获取支付方式</span>
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="承兑商UID" prop="c2c_user_code">
            <el-input  v-model="dataForm.c2c_user_code" placeholder="承兑商UID" clearable  @change="openMsg(dataForm.c2c_user_code)"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="承兑商剩余保证金" prop="all_deposit">
            <el-input  v-model="dataForm.all_deposit" disabled placeholder="承兑商剩余保证金">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="广告保证金" prop="deposit_open">
            <el-input  v-model="dataForm.deposit_open" disabled placeholder="广告保证金">
              <template v-slot:append>
                <span>USDT</span>
              </template></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
      <el-form-item label="登录人资金密码" prop="login_safeword">
        <el-input class="speaInputTwo" v-model="dataForm.login_safeword" type="password" placeholder="登录人资金密码"></el-input>
      </el-form-item>
    </el-col>
    <el-col :span="8">
      <el-form-item label="买卖方式" prop="uuid">
            <el-select class="speaInputTwo" v-model="dataForm.direction" placeholder="请选择" @change="changeVal()">
              <el-option v-for="item in direction" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        </el-row>
  <el-form-item  class="titleDiv" label="温馨提示：" prop="">
        <div class="green">
          广告保证金：广告保证金不能大于承兑商剩余保证金
        </div>
      </el-form-item>
      <el-form-item class="titleDivTwo" label="支付方式" prop="">
      </el-form-item>
      <el-row >
        <el-col :span="8">
          <el-form-item label="方式一" prop="uuid">
            <el-select class="speaInputTwo" v-model="payment_method1" placeholder="请选择" clearable @change="changeVal()">
              <el-option v-for="(item,index) in payArr" :key="index" :label="item.methodName" :value="item.methodConfigId">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="方式二" prop="uuid">
            <el-select class="speaInputTwo" v-model="payment_method2" placeholder="请选择" clearable @change="changeVal()">
              <el-option v-for="(item,index) in payArr" :key="index" :label="item.methodName" :value="item.methodConfigId">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="方式三" prop="uuid">
            <el-select class="speaInputTwo" v-model="payment_method3" placeholder="请选择" clearable @change="changeVal()">
              <el-option v-for="(item,index) in payArr" :key="index" :label="item.methodName" :value="item.methodConfigId">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="币种设置" prop="">
      </el-form-item>
      <el-row >
        <el-col :span="8">
          <el-form-item label="支付币种" prop="">
            <el-select class="speaInputTwo" v-model="dataForm.currency" placeholder="请选择" @change="getCompute(1)">
              <el-option v-for="item in currencyMap" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="上架币种" prop="">
            <el-select class="speaInputTwo" v-model="dataForm.symbol" placeholder="请选择" @change="getCompute(2)">
              <el-option v-for="item in symbolMap" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="交易币种数量" prop="coin_amount">
            <el-input  v-model="dataForm.coin_amount"  placeholder="交易币种数量" @change="getCompute(3)">
              <template v-slot:append>
                <span>{{(dataForm.symbol).toUpperCase()}}</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row >
        <el-col :span="8">
          <el-form-item label="最大可交易数量" prop="coin_amount_max">
            <el-input  v-model="dataForm.coin_amount_max" disabled  placeholder="最大可交易数量">
              <template v-slot:append>
                <span>{{(dataForm.symbol).toUpperCase()}}</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="币种单价" prop="symbol_value">
            <el-input  v-model="dataForm.symbol_value"   placeholder="币种单价" @change="getCompute(4)">
              <template v-slot:append>
                <span>{{dataForm.currency}}</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="币种市价" prop="price">
            <el-input  v-model="dataForm.price" disabled  placeholder="币种市价">
              <template v-slot:append>
                <span>{{dataForm.currency}}</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 统计数据 -->
      <el-form-item class="titleDiv" label="温馨提示：" prop="">
        <div class="green">
          1.交易币种数量：交易币种数量=广告保证金/上架币种实时行情价;</br>
          2.币种单价：币种单价=支付币种汇率*上架币种实时行情价*支付比率；例如，支付比率95%，1USDT=7.3CNY*1*95%=6.935CNY；</br>
          3.币种市价：币种市价=支付币种汇率*上架币种实时行情价；
        </div>
      </el-form-item>
      <el-form-item class="titleDivTwo" label="订单设置" prop="">
      </el-form-item>
      <el-form-item label="" >
        <span>单笔订单支付金额区间</span>
          </el-form-item>
      <el-row>
        <el-col :span="10">
          <el-form-item label="" prop="investment_min">
            <el-input v-model="dataForm.investment_min" placeholder="">
              <template v-slot:append>
                <span>{{dataForm.currency}}</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="2">          
          <el-form-item label="— —" prop="">
          </el-form-item></el-col>
        <el-col :span="10">
          <el-form-item label="" prop="investment_max">
            <el-input v-model="dataForm.investment_max"  placeholder="">
              <template v-slot:append>
                <span>{{dataForm.currency}}</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="" >
        <span>最小最大限额</span>
          </el-form-item>
      <el-row>
        <el-col :span="10">
          <el-form-item label="" prop="investment_min_limit">
            <el-input v-model="dataForm.investment_min_limit" disabled placeholder="">
              <template v-slot:append>
                <span>{{dataForm.currency}}</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="2">          
          <el-form-item label="— —" prop="">
          </el-form-item></el-col>
        <el-col :span="10">
          <el-form-item label="" prop="investment_max_limit">
            <el-input v-model="dataForm.investment_max_limit" disabled placeholder="">
              <template v-slot:append>
                <span>{{dataForm.currency}}</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="操作及其他设置" prop="">
      </el-form-item>        
      <el-row>
        <el-col :span="8">
          <el-form-item label="是否上架" prop="uuid">
            <el-select class="speaInputTwo" v-model="dataForm.on_sale" placeholder="请选择" @change="changeVal()">
              <el-option v-for="item in on_sale" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="排序索引" prop="sort_index">
            <el-input v-model="dataForm.sort_index"  placeholder="">
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="支付时效（单位:分钟)" prop="" label-width="150px">
            <el-select class="speaInputTwo" v-model="dataForm.expire_time" placeholder="请选择"  @change="changeVal()">
              <el-option v-for="item in expireTimeMap" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="交易条款" prop="transaction_terms">
        <el-input v-model="dataForm.transaction_terms" type="textarea"  maxlength="500"
            :autosize="{ minRows: 5, maxRows: 5}"
            show-word-limit placeholder="交易条款"></el-input>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="dataForm.remark" type="textarea"
            show-word-limit placeholder="备注"></el-input>
      </el-form-item>
      <el-form-item class="titleDiv" label="温馨提示：" prop="">           
        <div class="green">
          1.排序索引: 默认0，值越大越靠前;</br>
          2.支付时效:（一）用户下单后，在支付时效内未付款，订单会自动取消；</br>
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          （二）订单付款后，在支付时效内未确认收款，订单会变成已超时状态，在后台取消或确认收款；
        </div>
      </el-form-item>
    </el-form>
  </div>
  </el-scrollbar>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { treeDataTranslate } from "@/utils";
import { Debounce } from "@/utils/debounce";
import { encrypt } from '@/utils/crypto'
export default {
  data() {
    return {
      visible: false,
      menuList: [],
      menuListTreeProps: {
        label: "name",
        children: "children",
      },
      payment_method1:'',	//		支付方式1
      payment_method2:'',	//		支付方式2
      payment_method3:'',	//		支付方式3
      imageUrl: "",
      dataForm: {
        all_deposit:'',	//		承兑商剩余保证金
        c2c_user_code:'',	//		承兑商UID
        coin_amount:'',	//		交易币种数量
        coin_amount_max:'',	//		最大可交易数量
        currency:'',	//		支付币种
        deposit_open:'',	//		广告保证金
        deposit_total:'',	//	
        direction:'',	//		买卖方式 buy 买入 sell 卖出
        expire_time:'',	//		支付时效
        id:'',	//	
        investment_max:'',	//		单笔订单支付金额区间
        investment_max_limit:'',	//		最小最大限额
        investment_min:'',	//		单笔订单支付金额区间
        ent_min_limit:'',	//		最小最大限额
        login_safeword:'',	//	
        on_sale:'',	//		是否上架 0 下架 1 上架		
        order_msg:'',	//		订单自动消息
        pay_rate:'',	//		支付比率
        price:'',	//	币种市价
        remark:'',	//备注
        sort_index:'',	//排序索引
        symbol:'',	//上架币种
        symbol_close:'',
        symbol_value:'',//币种单价		
        transaction_terms:'',	//交易条款
      },
      options: [],
      payType:[],
      row:'',
      currencyMap:[],//支付币种
      symbolMap:[],//上架币种
      expireTimeMap:[],//支付时效
      direction:[ {
          label: "买入",
          value: 'buy',
        },
        {
          label: "卖出",
          value: 'sell',
        },],
        payArr:[],//支付方式
        on_sale:[ {
          label: "上架",
          value: 1,
        },
        {
          label: "下架",
          value: 0,
        },],
        expire_time:[
        {
          label: "15分钟",
          value: 1,
        },
        {
          label: "30分钟",
          value: 2,
        },  {
          label: "45分钟",
          value: 1,
        },
        {
          label: "60分钟",
          value: 2,
        },
        {
          label: "120分钟",
          value: 2,
        },
        ],
      type_front: [
        {
          label: "手机",
          value: 1,
        },
        {
          label: "邮箱",
          value: 2,
        },
      ],
      c2c_user_type: [
        {
          label: "后台承兑商",
          value: 1,
        },
        // {
        //   label: "用户承兑商",
        //   value: 2,
        // },
      ],
      optionsTwo: [
        {
          label: "未验证",
          value: 'N',
        },
        {
          label: "已验证",
          value: 'Y',
        },
      ],
      dataRule: {
        roleName: [
          { required: true, message: "角色名称不能为空", trigger: "blur" },
          {
            pattern: /\s\S+|S+\s|\S/,
            message: "请输入正确的角色名称",
            trigger: "blur",
          },
        ],
        login_safeword: [
          { required: true, message: "资金密码不能为空", trigger: "blur" },
        ],
        c2c_user_code: [
          { required: true, message: "承兑商UID不能为空", trigger: "blur" },
        ],
        remark: [
          {
            required: false,
            pattern: /\s\S+|S+\s|\S/,
            message: "输入格式有误",
            trigger: "blur",
          },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(currencyMap,symbolMap,expireTimeMap,row) {
      this.row = row || '';
      this.currencyMap = currencyMap || []
      this.symbolMap = symbolMap || []
      this.expireTimeMap = expireTimeMap || []
      if(row){
        this.dataForm = {...row}
        this.openMsg(this.dataForm.c2c_user_code)
        if(row.pay_typeId){
          this.payType = row.pay_typeId.split(',')
          if(this.payType.length == 3){
            this.payment_method1 = this.payType[0]
            this.payment_method2 = this.payType[1]
            this.payment_method3 = this.payType[2]
          }else if(this.payType.length == 1){
            this.payment_method1 = this.payType[0]
          }else if(this.payType.length == 2){
            this.payment_method1 = this.payType[0]
            this.payment_method2 = this.payType[1]
          }
        }
      }else{
      this.dataForm.currency = this.currencyMap[0].value
      this.dataForm.symbol =this.symbolMap[0].value
      this.getCompute()
      }
      
      this.visible = true;
    },
    handleAvatarSuccess(res, file) {
      this.dataForm.head_img = res.data.path;
      this.imageUrl = URL.createObjectURL(file.raw);
    },
    beforeAvatarUpload(file) {
      // const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 10;

      // if (!isJPG) {
      //   this.$message.error('上传头像图片只能是 JPG 格式!');
      // }
      if (!isLt2M) {
        this.$message.error("上传图片大小不能超过 10MB!");
      }
      // return isJPG && isLt2M;
      return isLt2M;
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    openMsg(uid){ //获取 承兑商 剩余保证金 支付方式\
      if(uid){
        this.$http({
        url: this.$http.adornUrl("/c2cAdvert/getC2cUserDeposit"),
        method: "get",
        params: this.$http.adornParams(Object.assign({
            'c2cUserCode':uid
        })),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.payArr = data.data.paymentMethodList
          this.dataForm.all_deposit = data.data.all_deposit
          this.dataForm.deposit = data.data.deposit
          // if(this.payArr.length == 3){
          //   this.payment_method1 = this.payArr[0].methodConfigId
          //   this.payment_method2 = this.payArr[1].methodConfigId
          //   this.payment_method3 = this.payArr[2].methodConfigId
          // }else if(this.payArr.length == 1){
          //   this.payment_method1 = this.payArr[0].methodConfigId
          // }else if(this.payArr.length == 2){
          //   this.payment_method1 = this.payArr[0].methodConfigId
          //   this.payment_method2 = this.payArr[1].methodConfigId
          // }
          //this.options = data.data;
        }else{
          this.$message({
              message: data.msg,
              type: "error",
              duration: 1500,
              onClose: () => {
              },
            });
        }
      });
      }
    },
    getCompute(){ //获取数据  1支付币种currency  2上架币种symbol 3交易币种数量coin_amount  4币种单价symbol_value
      // let dataType = {}
      // if(n == 1){
      //   dataType = {
      //     'c2c_user_code':this.dataForm.c2c_user_code,
      //     'currency':this.dataForm.currency
      //   }
      // }else if( n == 2){
      //   dataType = {
      //     'c2c_user_code':this.dataForm.c2c_user_code,
      //     'symbol':this.dataForm.symbol
      //   }
      // }else if( n == 3){
      //   dataType = {
      //     'c2c_user_code':this.dataForm.c2c_user_code,
      //     'coin_amount':this.dataForm.coin_amount
      //   }
      // }else if( n == 4){
      //   dataType ={
      //     'c2c_user_code':this.dataForm.c2c_user_code,
      //     'symbol_value':this.dataForm.symbol_value
      //   }
      // }
        this.$http({
        url: this.$http.adornUrl("/c2cAdvert/compute"),
        method: "get",
        // params: this.$http.adornParams(Object.assign({
        //     'c2cUserCode':uid
        // })),
        params: this.$http.adornParams(Object.assign({
          'symbol_value':this.dataForm.symbol_value,
          'currency':this.dataForm.currency,
          'coin_amount':this.dataForm.coin_amount,
          'symbol':this.dataForm.symbol,
          'deposit_total':this.dataForm.all_deposit,
          'symbol_close':0,
        })),
      }).then(({ data }) => {
        if (data.code == 0) {
          console.log(data.data.coin_amount_max)
          // 支付比率 =pay_rate   
          // 广告保证金=deposit_open
          // 承兑商剩余保证金=all_deposit
          // 币种市价=price
          // 最大可交易数量=coin_amount_max
          // 最小最大限额=investment_min_limit
          // 最小最大限额=investment_max_limit
          //this.options = data.data;
          this.dataForm.pay_rate = data.data.pay_rate
          this.dataForm.deposit_open = data.data.deposit_open
          this.dataForm.price = data.data.price
          this.dataForm.coin_amount_max = data.data.coin_amount_max
          this.dataForm.investment_min_limit = data.data.investment_min_limit
          this.dataForm.investment_max_limit = data.data.investment_max_limit
          // this.dataForm.pay_rate = data.pay_rate
        }else{
          this.$message({
              message: data.msg,
              type: "error",
              duration: 1500,
              onClose: () => {
              },
            });
        }
      });
    },
    getC2cManagerInfo() {
      //获取详情
      this.$http({
        url: this.$http.adornUrl("/c2cUser/getDesc"),
        method: "get",
        params: this.$http.adornParams(Object.assign({
          id:this.dataForm.id
        })),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.dataForm = data.data;
        }
      });
    },
    handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
        this.payArr = []
        this.payment_method1 = ''
        this.payment_method2 = ''
        this.payment_method3 = ''
     this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
      },
        // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
            //   const formData = new FormData();
            //   const objDate = {...this.dataForm}
            // // 添加需要提交的表单字段及其值到 FormData 对象
            // for (let key in objDate) {
            //   if(key == 'login_safeword'){
            //     console.log(objDate[key])
            //     objDate[key] = encrypt(objDate[key])
                
            //   }
            //   formData.append(key, objDate[key]);
            // }
            if(this.row){//修改
              this.$http({
              url: this.$http.adornUrl(`/c2cAdvert/update`), //新增
              method: 'post',
              data: this.$http.adornData({
                'all_deposit':this.dataForm.all_deposit,	//		承兑商剩余保证金
                'c2c_user_code':this.dataForm.c2c_user_code,	//		承兑商UID
                'coin_amount':this.dataForm.coin_amount,	//		交易币种数量
                'coin_amount_max':this.dataForm.coin_amount_max,	//		最大可交易数量
                'currency':this.dataForm.currency,	//		支付币种
                'deposit_open':this.dataForm.deposit_open,	//		广告保证金
                'deposit_total':this.dataForm.deposit_total,	//	
                'direction':this.dataForm.direction,	//		买卖方式 buy 买入 sell 卖出
                'expire_time':this.dataForm.expire_time,	//		支付时效
                'id':this.dataForm.id,	//	
                'investment_max':this.dataForm.investment_max,	//		单笔订单支付金额区间
                'investment_max_limit':this.dataForm.investment_max_limit,	//		最小最大限额
                'investment_min':this.dataForm.investment_min,	//		单笔订单支付金额区间
                'ent_min_limit':this.dataForm.ent_min_limit,	//		最小最大限额
                'login_safeword':encrypt(this.dataForm.login_safeword),	//	
                'on_sale':this.dataForm.on_sale,	//		是否上架 0 下架 1 上架		
                'order_msg':this.dataForm.order_msg,	//		订单自动消息
                'pay_rate':this.dataForm.pay_rate,	//		支付比率
                'payment_method1':this.payment_method1,	//		支付方式1
                'payment_method2':this.payment_method2,	//		支付方式2
                'payment_method3':this.payment_method3,	//		支付方式3
                'price':this.dataForm.price,	//	币种市价
                'remark':this.dataForm.remark,	//备注
                'sort_index':this.dataForm.sort_index,	//排序索引
                'symbol':this.dataForm.symbol,	//上架币种
                'symbol_close':this.dataForm.symbol_close,
                'symbol_value':this.dataForm.symbol_value,//币种单价		
                'transaction_terms':this.dataForm.transaction_terms,	//交易条款
              })
            }).then(({data}) => {
              if(data.code == 0){
                  this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    //this.resClear()
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              }else{
                this.$message({
                  message: data.msg,
                  type: 'error',
                })
              }

            })
            }else{
              this.$http({
              url: this.$http.adornUrl(`/c2cAdvert/add`), //新增
              method: 'post',
              data: this.$http.adornData({
                'all_deposit':this.dataForm.all_deposit,	//		承兑商剩余保证金
                'c2c_user_code':this.dataForm.c2c_user_code,	//		承兑商UID
                'coin_amount':this.dataForm.coin_amount,	//		交易币种数量
                'coin_amount_max':this.dataForm.coin_amount_max,	//		最大可交易数量
                'currency':this.dataForm.currency,	//		支付币种
                'deposit_open':this.dataForm.deposit_open,	//		广告保证金
                'deposit_total':this.dataForm.deposit_total,	//	
                'direction':this.dataForm.direction,	//		买卖方式 buy 买入 sell 卖出
                'expire_time':this.dataForm.expire_time,	//		支付时效
                //'id':this.dataForm.id,	//	
                'investment_max':this.dataForm.investment_max,	//		单笔订单支付金额区间
                'investment_max_limit':this.dataForm.investment_max_limit,	//		最小最大限额
                'investment_min':this.dataForm.investment_min,	//		单笔订单支付金额区间
                'ent_min_limit':this.dataForm.ent_min_limit,	//		最小最大限额
                'login_safeword':encrypt(this.dataForm.login_safeword),	//	
                'on_sale':this.dataForm.on_sale,	//		是否上架 0 下架 1 上架		
                'order_msg':this.dataForm.order_msg,	//		订单自动消息
                'pay_rate':this.dataForm.pay_rate,	//		支付比率
                'payment_method1':this.payment_method1,	//		支付方式1
                'payment_method2':this.payment_method2,	//		支付方式2
                'payment_method3':this.payment_method3,	//		支付方式3
                'price':this.dataForm.price,	//	币种市价
                'remark':this.dataForm.remark,	//备注
                'sort_index':this.dataForm.sort_index,	//排序索引
                'symbol':this.dataForm.symbol,	//上架币种
                'symbol_close':this.dataForm.symbol_close,
                'symbol_value':this.dataForm.symbol_value,//币种单价		
                'transaction_terms':this.dataForm.transaction_terms,	//交易条款
              })
            }).then(({data}) => {
              if(data.code == 0){
                  this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    //this.resClear()
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              }else{
                this.$message({
                  message: data.msg,
                  type: 'error',
                })
              }

            })
            }
 
          }
        });
    }),
    // 表单提交
    // dataFormSubmit: Debounce(function () {
    //   this.$refs["dataForm"].validate((valid) => {
    //     if (valid) {
    //       this.$http({
    //         url: this.$http.adornUrl(`/c2cUser/add`),
    //         method:"post",
    //         data: 
    //         //this.dataForm
    //         this.$http.adornData({
    //           //this.dataForm,
    //           account_create_days: this.dataForm.account_create_days, //账号创建天数统计值
    //           account_create_days_base: this.dataForm.account_create_days_base, //账号创建天数基础设置
    //           appraise_bad: this.dataForm.appraise_bad, //差评数统计值
    //           appraise_bad_base: this.dataForm.appraise_bad_base, //差评数基础设置
    //           appraise_good: this.dataForm.appraise_good, //好评数统计值
    //           appraise_good_base: this.dataForm.appraise_good_base, //好评数基础设置
    //           buy_amount: this.dataForm.buy_amount, //买交易量统计值
    //           buy_amount_base: this.dataForm.buy_amount_base, //	买交易量基础设置
    //           buy_success_orders: this.dataForm.buy_success_orders, //	买成单数统计值
    //           buy_success_orders_base: this.dataForm.buy_success_orders_base, //	买成单数基础设置
    //           c2c_manager_party_id: this.dataForm.c2c_manager_party_id, //	C2C管理员
    //           c2c_user_party_code: this.dataForm.c2c_user_party_code, //	承兑商uuid
    //           c2c_user_type: this.dataForm.c2c_user_type, //	承兑商类型
    //           email_authority: this.dataForm.email_authority, //	邮箱验证状态统计值
    //           email_authority_base: this.dataForm.email_authority_base, //	邮箱验证状态基础设置
    //           exchange_users: this.dataForm.exchange_users, //	交易人数统计值
    //           exchange_users_base: this.dataForm.exchange_users_base, //	交易人数基础设置
    //           first_exchange_days: this.dataForm.first_exchange_days, //	首次交易至今天数基础设置
    //           first_exchange_days_base: this.dataForm.first_exchange_days_base, //	首次交易至今天数统计值
    //           head_img: this.dataForm.head_img, //	承兑商头像
    //           kyc_authority: this.dataForm.kyc_authority, //	身份认证状态统计值
    //           kyc_authority_base: this.dataForm.kyc_authority_base, //	身份认证状态基础设置
    //           kyc_highlevel_authority: this.dataForm.kyc_highlevel_authority, //	高级认证状态统计值
    //           kyc_highlevel_authority_base: this.dataForm.kyc_highlevel_authority_base, //	高级认证状态基础设置
    //           login_safeword: encrypt(this.dataForm.login_safeword), //	登录人密码
    //           nick_name: this.dataForm.nick_name, //	承兑商昵称
    //           password_front: encrypt(this.dataForm.password_front), //	前端登录密码
    //           phone_authority: this.dataForm.phone_authority, //	手机验证状态统计值
    //           phone_authority_base: this.dataForm.phone_authority_base, //	手机验证状态基础设置
    //           re_password_front: encrypt(this.dataForm.re_password_front), //	前端登录密码
    //           remark: this.dataForm.remark, //	备注
    //           sell_amount: this.dataForm.sell_amount, //	卖交易量统计值
    //           sell_amount_base: this.dataForm.sell_amount_base, //	卖交易量基础设置
    //           sell_success_orders: this.dataForm.sell_success_orders, //	卖成单数统计值
    //           sell_success_orders_base: this.dataForm.sell_success_orders_base, //	卖成单数基础设置
    //           thirty_days_amount: this.dataForm.thirty_days_amount, //	30日交易量统计值
    //           thirty_days_amount_base: this.dataForm.thirty_days_amount_base, //	30日交易量基础设置
    //           thirty_days_order: this.dataForm.thirty_days_order, //	30日成单数统计值
    //           thirty_days_order_base: this.dataForm.thirty_days_order_base, //	30日成单数基础设置
    //           thirty_days_order_ratio: this.dataForm.thirty_days_order_ratio, //	30日成单率统计值
    //           thirty_days_order_ratio_base: this.dataForm.thirty_days_order_ratio_base, //	30日成单率基础设置
    //           thirty_days_pass_average_time: this.dataForm.thirty_days_pass_average_time, //	30日平均放行时间统计值
    //           thirty_days_pass_average_time_base: this.dataForm.thirty_days_pass_average_time_base, //	30日平均放行时间基础设置
    //           thirty_days_pay_average_time: this.dataForm.thirty_days_pay_average_time, //	30日平均付款时间统计值
    //           thirty_days_pay_average_time_base: this.dataForm.thirty_days_pay_average_time_base, //	30日平均付款时间基础设置
    //           total_amount: this.dataForm.total_amount, //	总交易量统计值
    //           total_amount_base: this.dataForm.total_amount_base, //	总交易量基础设置
    //           total_success_orders: this.dataForm.total_success_orders, //	总成单数统计值
    //           total_success_orders_base: this.dataForm.total_success_orders_base, //	总成单数基础设置
    //           type_front: this.dataForm.type_front, //	1 手机 2.邮箱
    //           usercode_front: this.dataForm.usercode_front, //	推荐码
    //           username_front: this.dataForm.username_front, //	前端登录用户名
    //         }),
    //       }).then(({ data }) => {
    //         if(data.code == 0){
    //           this.$message({
    //           message: "操作成功",
    //           type: "success",
    //           duration: 1500,
    //           onClose: () => {
    //             this.visible = false;
    //             this.$emit("refreshDataList");
    //           },
    //         });
    //         }else{
    //           this.$message({
    //           message: data.msg,
    //           type: "error",
    //           duration: 1500,
    //           onClose: () => {
    //           },
    //         });
    //         }

    //       });
    //     }
    //   });
    // }),
  },
};
</script>
<style scoped>
.titleDiv {
  margin: 30px 0;
  border-bottom: 1px solid #e6e6e6;
}

.titleDivTwo {
  height: 38px;
  border-left: 3px solid #1c4efa;
  background: #f4f7ff;
}

.addButton {
  float: right;
  margin-right: 20px;
}

.speaInputTwo {
  width: 250px;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.vertical-scrollbar .scroll-content {
  max-height: 600px;
  overflow-y: auto;
  overflow-x: hidden;
}
.vertical-scrollbar .scroll-content::-webkit-scrollbar {
  width: 4px;
}

.vertical-scrollbar .scroll-content::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

.vertical-scrollbar .scroll-content::-webkit-scrollbar-thumb {
  background-color: #888;
  border-radius: 4px;
}

.vertical-scrollbar .scroll-content::-webkit-scrollbar-thumb:hover {
  background-color: #555;
}
</style>
