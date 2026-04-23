<template>
  <div class="mod-transport">
    <!-- <el-button @click="handle"
             style="margin-left: 20px">{{title}}</el-button>
    <br /><br /> -->
    <!-- <avue-form :option="option"
              v-model="obj"
              @submit="submit"></avue-form> -->
    <el-form :model="dataForm" ref="dataForm" label-width="auto" size="mini">
      <!-- <el-form-item label="" prop="roleName" class="item" label-width="20px">        
      </el-form-item> -->
      <div class="item2">
        <div style="height: auto;position: relative;top: 25%;">
          APP
        </div>
      </div>
      <div style="background-color: white;height: 10px;"></div>
      
      <el-form-item label="当前版本" prop="roleName" label-width="70px">
        <el-input v-model="dataForm.sys_version" placeholder="当前APP版本"></el-input>
      </el-form-item>

      <!-- <el-form-item label="充值" prop="roleName" class="item" label-width="40px"></el-form-item> -->
      <!-- :inline="true" -->
      <div class="item2">
        <div style="height: auto;position: relative;top: 25%;">
          充值
        </div>
      </div>
      <!-- <div style="background-color: white;height: 10px;"></div> -->
      <el-form :inline="true">  
          <el-form-item label="">
            <el-form-item label="充值最低数量，其他币种价值会被换算成USDT判断"></el-form-item>
            <el-input v-model="dataForm.recharge_limit_min" placeholder="USDT"></el-input>
          </el-form-item>
        
          <el-form-item label="">
            <el-form-item label="充值最高数量，其他币种价值会被换算成USDT判断"></el-form-item>
            <el-input v-model="dataForm.recharge_limit_max" placeholder="USDT"></el-input>
          </el-form-item>
      </el-form>

      <!-- <el-form-item label="提现" prop="roleName" class="item" label-width="40px"></el-form-item> -->
      <div class="item2">
        <div style="height: auto;position: relative;top: 25%;">
          提现
        </div>
      </div>
      <!-- <div style="background-color: white;height: 10px;"></div> -->
      <el-form :inline="true">  
          <el-form-item label="">
            <el-form-item label="是否开启基础认证后才能提现"></el-form-item>
            <br/>
            <el-select v-model="dataForm.withdraw_by_kyc" placeholder="关闭">
                <el-option label="关闭" value="false"></el-option>
                <el-option label="开启" value="true"></el-option>
              </el-select>     
          </el-form-item>
        
          <el-form-item label="">
            <el-form-item label="提现流水限制是否开启"></el-form-item>
            <br/>
            <el-select v-model="dataForm.withdraw_limit_open" placeholder="关闭">
              <el-option label="关闭" value="false"></el-option>
              <el-option label="开启" value="true"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="">
            <el-form-item label="提现限制流水百分比"></el-form-item>
            <el-input v-model="dataForm.withdraw_limit_turnover_percent" placeholder="%"></el-input>
          </el-form-item>
        
          <el-form-item label="">
            <el-form-item label="单次USDT提现限额最低金额"></el-form-item>
            <el-input v-model="dataForm.withdraw_limit_dapp" placeholder="USDT"></el-input>
          </el-form-item>

          <el-form-item label="">
            <el-form-item label="单次USDT提现限额最高金额"></el-form-item>
            <el-input v-model="dataForm.withdraw_limit_max" placeholder="USDT"></el-input>
          </el-form-item>

          <el-form-item label="">
            <el-form-item label="单次BTC提现最低金额"></el-form-item>
            <el-input v-model="dataForm.withdraw_limit_btc" placeholder="USDT"></el-input>
          </el-form-item>
        
          <el-form-item label="">
            <el-form-item label="单次ETH提现最低金额"></el-form-item>
            <el-input v-model="dataForm.withdraw_limit_eth" placeholder="USDT"></el-input>
          </el-form-item>

          <el-form-item label="">
            <el-form-item label="每日可提现次数，若为0或空则不做限制"></el-form-item>
            <el-input v-model="dataForm.withdraw_limit_num" placeholder="USDT"></el-input>
          </el-form-item>
        
          <el-form-item label="">
            <el-form-item label="每日可提现时间段,若为空则不做限制"></el-form-item>
            <br/>
            <el-time-picker
              is-range
              arrow-control
              v-model="dataForm.dateRange"
              type="datetimerange"
              range-separator="至"
              value-format="HH:mm:ss"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              placeholder="选择时间范围">
            </el-time-picker>
          </el-form-item>

      </el-form>
     

      <el-form :inline="true">  
        <el-form-item label="" label-position="top">
      
          <!-- <el-form-item label="交割合约" prop="roleName" class="item"></el-form-item> -->
          <div class="item2" style="width: 520px;">
            <div style="height: auto;position: relative;top: -15%;">
              交割合约
            </div>
          </div>
      <!-- <div style="background-color: white;height: 10px;"></div> -->
          <el-form-item label="24小时内交割合约客户最高赢率(设置了场控的不受影响)，高于设定的值时客户必亏" style="display: flex;flex-wrap:wrap"></el-form-item>
          <el-form-item label="低于时则不限制（范例：10，为最高赢10%），为0则不限制" style="display: flex;flex-wrap:wrap"></el-form-item>
          <!-- <br /> -->
          <el-input-number v-model="dataForm.futures_most_prfit_level" :step="2"></el-input-number>

          
        </el-form-item>

        <el-form-item>
          <!-- <el-form-item label="永续合约" prop="roleName" class="item"></el-form-item> -->
          <div class="item2" style="width: 480px;">
            <div style="height: auto;position: relative;top: -15%;">
              永续合约
            </div>
          </div>
          <el-form-item label="交易状态" style="display: flex;flex-wrap:wrap"></el-form-item>
          <br/>
          <el-select v-model="dataForm.order_open" placeholder="关闭">
              <el-option label="关闭" value="false"></el-option>
              <el-option label="开启" value="true"></el-option>
          </el-select>   
        </el-form-item>
        
      </el-form>
      

      <div class="item2">
        <div style="height: auto;position: relative;top: 25%;">
          币币(现货)交易
        </div>
      </div>
      <!-- <div style="background-color: white;height: 10px;"></div> -->
      <!-- <el-form-item label="币币(现货)交易" prop="roleName" class="item"></el-form-item> -->
      <el-form :inline="true" label-position="top">
        
        <el-form-item label="交易状态" prop="roleName">
          <el-select v-model="dataForm.exchange_order_open" placeholder="关闭">
            <el-option label="关闭" value="false"></el-option>
            <el-option label="开启" value="true"></el-option>
          </el-select>  
          <!-- <el-input v-model="dataForm.roleName" placeholder="当前APP版本"></el-input> -->
        </el-form-item>

        <el-form-item label="卖出手续费" prop="exchange_apply_order_sell_fee">
          <el-input v-model="dataForm.exchange_apply_order_sell_fee" placeholder="%"></el-input>
        </el-form-item>

        <el-form-item label="买入手续费" prop="exchange_apply_order_buy_fee">
          <el-input v-model="dataForm.exchange_apply_order_buy_fee" placeholder="%"></el-input>
        </el-form-item>

      </el-form>


      <el-form label-position="top">
        <div class="item2">
          <div style="height: auto;position: relative;top: 25%;">
            在线客服
          </div>
        </div>
        <!-- <div style="background-color: white;height: 10px;"></div> -->
        <!-- <el-form-item label="在线客服" prop="roleName" class="item"></el-form-item> -->
        <el-form-item label="第三方在线客服链接地址(为空则为自研客服)" prop="roleName">
          <el-input
              type="textarea"
              placeholder="请输入内容"
              v-model="dataForm.customer_service_url"
              :autosize="{ minRows: 2, maxRows: 2}"
              show-word-limit
            >
          </el-input>
        </el-form-item>
      </el-form>


      <el-form label-position="top">
        <!-- <el-form-item label="自研客服用户名黑名单" prop="roleName" class="item"></el-form-item> -->
        <div class="item2">
          <div style="height: auto;position: relative;top: 25%;">
            自研客服用户名黑名单
          </div>
        </div>
        <!-- <div style="background-color: white;height: 10px;"></div> -->
        <el-form-item label="客服系统用户名黑名单，对多个用户名用逗号隔开，例如：aaa,bbb,ccc" prop="roleName">
          <el-input
              type="textarea"
              placeholder="请输入内容"
              v-model="dataForm.online_username_black_menu"
              :autosize="{ minRows: 8, maxRows: 8}"
              show-word-limit
            >
          </el-input>
        </el-form-item>
      </el-form>

      <el-form label-position="top">
        <div class="item2">
          <div style="height: auto;position: relative;top: 25%;">
            前端用户黑名单
          </div>
        </div>
        <!-- <el-form-item label="前端用户黑名单" prop="roleName" class="item"></el-form-item> -->
        <el-form-item label="前端用户黑名单，对多个用户名用逗号隔开，例如：aaa,bbb,ccc" prop="roleName">
          <el-input
              type="textarea"
              placeholder="请输入内容"
              v-model="dataForm.stop_user_internet"
              :autosize="{ minRows: 8, maxRows: 8}"
              show-word-limit
            >
          </el-input>
        </el-form-item>
      </el-form>

      <el-form label-position="top">
        <div class="item2">
          <div style="height: auto;position: relative;top: 25%;">
            后台系统登录IP白名单
          </div>
        </div>
        <!-- <el-form-item label="后台系统登录IP白名单" prop="roleName" class="item"></el-form-item> -->
        <el-form-item label="为空则不限制，多个IP之间以，(英文逗号)隔开，也可设置IP段，例如：127.0.*.*,192.168.0.1" prop="roleName">
          <el-input
              type="textarea"
              placeholder="请输入内容"
              v-model="dataForm.filter_ip"
              :autosize="{ minRows: 8, maxRows: 8}"
              show-word-limit
            >
          </el-input>
        </el-form-item>
      </el-form>
    </el-form>
    <el-button 
      v-if="isAuth('sys:config-root2-sys-config:operate')"
      type="primary" @click="save()">保存</el-button>

    <!-- 确认弹窗-start -->
    <el-dialog title="确认修改" :visible.sync="dialogFormVisible" :append-to-body="true"  @close = 'handClose'>
      <el-form :model="dataForm2" ref="dataForm2" @keyup.enter.native="dataFormSubmit()" label-width="80px">
        <el-form-item label="登录人资金密码"  :label-width="formLabelWidth" prop="loginSafeword">
          <el-input v-model="dataForm2.loginSafeword" type="password" placeholder="登录人资金密码" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="超级谷歌验证码" :label-width="formLabelWidth" prop="superGoogleAuthCode">
          <el-input v-model="dataForm2.superGoogleAuthCode" placeholder="超级谷歌验证码" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="dataFormSubmit()">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 确认弹窗-end -->

  </div>

</template>

<script>
import { tableOption } from '@/crud/sys/root2'
import AddOrUpdate from './root2-sys-config-add-or-update'
import { encrypt } from '@/utils/crypto'
export default {
  computed: {
    title () {
      return this.option.detail ? '编 辑' : '保 存'
    }
  },
  data () {
    return {
      form: {
        loginSafeword	: '',     //登录人资金密码	
        superGoogleAuthCode: '' //超级管理员谷歌验证码	
      },
      formLabelWidth: '120px',
      dialogFormVisible:false,
      dataForm:{},
      dataForm2:{},
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      option: tableOption,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10 // 每页显示多少条
      },
      type: 0,
      obj: {}
    }
  },
  components: {
    AddOrUpdate
  },
  mounted(){
    this.getDataList()
    // 为全局事件总线绑定自定义事件
		this.$bus.$on('root2-sys-config', (data)=>{
			this.getDataList()
		})
  },
	beforeDestroy() {
		// 组件被销毁了，不能进行数据传输
		// 解绑事件
		this.$bus.$off('root2-sys-config')
	},
  methods: {
    // 获取数据列表
    getDataList (page, params, done) {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/normal/adminSysparaAction!/toUpdate.action'),
        method: 'get',
        params: this.$http.adornParams(
          Object.assign(
            {
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize
            },
            params
          )
        )
      }).then(({ data }) => {
        console.log("data = " + JSON.stringify(data));
        if(data.code == 0){
          this.dataForm = data.data || {}
          this.dataList = data.data || []
          this.page.total = data.total

          this.dataForm.dateRange = []

          this.dataForm.dateRange[0] = this.dataForm.withdraw_limit_time_min;
          this.dataForm.dateRange[1] = this.dataForm.withdraw_limit_time_max;

          this.dataListLoading = false
        }else{
          this.$message.error(data.msg)
        }
        
        if (done) {
          done()
        }
      })
    },
    handClose(){
        this.$data.dataForm2=JSON.parse(JSON.stringify(this.$options.data().dataForm2))
     this.$nextTick(() => {
            this.$refs['dataForm2'].clearValidate() // 清除表单验证
          })
      },
    // 条件查询
    searchChange (params, done) {
      this.getDataList(this.page, params, done)
    },
    // 多选变化
    selectionChange (val) {
      this.dataListSelections = val
    },
    // 新增 / 修改
    addOrUpdateHandle (id) {
      this.addOrUpdateVisible = true
      console.log("addOrUpdateVisible = " + this.addOrUpdateVisible);
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id)
      })
    },
    // 删除
    deleteHandle (id) {
      var userIds = id ? [id] : this.dataListSelections.map(item => {
        return item.userId
      })
      this.$confirm(`确定对[id=${userIds.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/sys/user'),
          method: 'delete',
          data: this.$http.adornData(userIds, false)
        }).then(({ data }) => {
          this.$message({
            message: '操作成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              this.getDataList()
            }
          })
        })
      }).catch(() => { })
    },
    handle () {
      this.option.detail = !this.option.detail
    },
    submit () {
      this.$message.success(JSON.stringify(this.obj))
    },
    save(){
      this.dialogFormVisible = true;
    },
    dataFormSubmit(){
      //
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/normal/adminSysparaAction!/updateAdmin.action'),
        method: 'post',
        data: this.$http.adornData(
          Object.assign(
            {
              "withdraw_limit_max":this.dataForm.withdraw_limit_max,
              "sys_version":this.dataForm.sys_version,//App版本
              "recharge_limit_min":this.dataForm.recharge_limit_min,//充值最低数量
              "recharge_limit_max":this.dataForm.recharge_limit_max,//充值最高数量
              "withdraw_by_kyc":this.dataForm.withdraw_by_kyc,//是否开启基础认证后才能提现
              "withdraw_limit_open":this.dataForm.withdraw_limit_open,//提现流水限制是否开启
              "withdraw_limit_turnover_percent":this.dataForm.withdraw_limit_turnover_percent,//提现限制流水百分比
              "withdraw_limit":this.dataForm.withdraw_limit_dapp,//单次USDT提现限额
              "withdraw_limit_dapp":this.dataForm.withdraw_limit_dapp,//单次USDT提现限额
              "withdraw_limit_btc":this.dataForm.withdraw_limit_btc,//单次BTC提现最低金额
              "withdraw_limit_eth":this.dataForm.withdraw_limit_eth,//单次ETH提现最低金额
              "withdraw_limit_num":this.dataForm.withdraw_limit_num,//每日可提现次数，若为0或空则不做限制
              "withdraw_limit_time_min":this.dataForm.withdraw_limit_time_min||"0",
              "withdraw_limit_time_max":this.dataForm.withdraw_limit_time_max||'0',//每日可提现时间段，若为空则不做限制
              "futures_most_prfit_level":this.dataForm.futures_most_prfit_level,//交割合约
              "order_open":this.dataForm.order_open,//永续合约
              "exchange_order_open":this.dataForm.exchange_order_open,//币币交易-交易状态
              "exchange_apply_order_sell_fee":this.dataForm.exchange_apply_order_sell_fee,//卖出手续费
              "exchange_apply_order_buy_fee":this.dataForm.exchange_apply_order_buy_fee,//买入手续费
              "customer_service_url":this.dataForm.customer_service_url,//在线客服链接地址
              "online_username_black_menu":this.dataForm.online_username_black_menu,//客服系统黑名单
              "stop_user_internet":this.dataForm.stop_user_internet,//前端用户黑名单
              "filter_ip":this.dataForm.filter_ip,//IP白名单
              "login_safeword": encrypt(this.dataForm2.loginSafeword), //safePassword	资金密码
              "super_google_auth_code": this.dataForm2.superGoogleAuthCode, //superGoogleAuthCode	超级管理员谷歌验证码		false
            }
          )
        )
      }).then(({ data }) => {
        console.log("data = " + JSON.stringify(data));
        // this.dataForm = data.data
        // this.dataList = data.data
        // this.page.total = data.total
        // this.dataListLoading = false
        
        if(data&&data.code == 0){
          this.$message({
            message: '操作成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              this.dialogFormVisible = false;
              this.getDataList()
            }
          })
          
        }else{
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

<style scoped>
  .item{
    background-color:#4d9ec4;
    height:10px;
    margin-top: 0px;
    top:0px;
    position:relative;
  }

  .item2{
    padding-left: 20px;
    background-color:rgb(202, 205,204);
    height:30px;
    position:relative;
  }

  .item > label{
    color: #020202;
  }

  /* .el-form-item label{
    color: #14da6d;
  } */

  /* .el-form-item__label {
    word-wrap: break-word;
  } */
</style>
