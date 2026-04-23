<template>
  <!-- 账变记录 -->
  <div id="cryptos">
    <div class="accountChange">
      <assets-head :title="$t('账变记录')" />
      <van-tabs ref="tabs" v-model:active="active" swipeable @change="onChange" sticky>
        <van-tab :title="item.title" v-for="item in selectData" :key="item.title" :name="item.type">
          <van-list v-model:loading="loading" :loading-text="$t('加载中...')" :finished="finished"
            :finished-text="list.length ? $t('已经全部加载完毕') : ''" @load="onLoad" :offset="130" class="px-4">
            <div class="flex justify-between items-center record-list" v-for="(item, index) in list" :key="index">
              <div class="flex items-center">
                <img v-if="item.amount > 0" src="@/assets/image/assets-center/in.png" class="w-11 h-11 mr-5" />
                <img v-else src="@/assets/image/assets-center/out.png" class="w-11 h-11 mr-5" />
                <div>
                  <div class="text-32 textColor">{{ category[item.category] }}</div>
                  <div class="text-grey text-26 mt-3" :class="getColor(item.content_type) ? 'text-red' : ''">{{ orderTypes[item.content_type] }}</div>
                </div>
              </div>
              <div>
                <div class="text-32 text-right" :class="item.amount > 0 ? 'text-green' : 'text-red'">{{
                  item.amount.toFixed(5) }}&nbsp;{{ item.wallet_type ? item.wallet_type.toUpperCase() : '' }}</div>
                <div class="text-grey text-28 mt-3">{{ item.createTime }}</div>
              </div>
            </div>
            <div class="flex flex-col justify-center pt-12 pb-5 items-center" v-if="!list.length && !loading">
              <img src="@/assets/image/assets-center/no-data.png" alt="" class="w-24 h-24 no-data-img" />
              <p class="text-grey mt-3">{{ $t('暂无记录') }}</p>
            </div>
          </van-list>
        </van-tab>
      </van-tabs>
    </div>
  </div>
</template>

<script>
import { _fundRecord } from '@/service/fund.api'
import { List } from 'vant'
import assetsHead from "@/components/Transform/assets-head/index.vue";
import { Tab, Tabs } from 'vant';
export default {
  name: "accountChange",
  data() {
    return {
      active: '',
      page: 1,
      loading: false, // 当loading为true时，转圈圈
      finished: false,  // 数据是否请求结束，结束会先显示'已经全部加载完毕'
      noData: false,// 如果没有数据，显示暂无数据
      // loading:false,   // 下拉的加载图案
      isShow: false,
      title: this.$t('选择账户类型'),
      selectData: [
        { title: this.$t('全部'), type: '' },
        { title: this.$t('法币交易'), type: 'coin' },
        { title: this.$t('合约交易'), type: 'contract' },
        { title: this.$t('现货交易'), type: 'exchange' },
      ],
      type: "",//选中类型
      list: [
        // { order_no:'11111', category:this.$t('合约交易'),content_type:this.$t('永续合约平仓'),amount:1000,wallettype:'USDT',createTimeStr:'2022-07-16 22:23:04'},
        // { order_no:'22222', category:this.$t('合约交易'),content_type:this.$t('永续合约平仓'),amount:-200,wallettype:'USDT',createTimeStr:'2022-07-16 22:23:04'},
        // { order_no:'3333333', category:this.$t('合约交易'),content_type:this.$t('永续合约平仓'),amount:1000,wallettype:'USDT',createTimeStr:'2022-07-16 22:23:04'},
      ],
      symbolType: ''
    }
  },
  mounted() {
    setTimeout(() => {
      this.$refs.tabs.resize();
    }, 500)
  },
  components: {
    assetsHead,
    [List.name]: List,
    [Tabs.name]: Tabs,
    [Tab.name]: Tab
  },
  computed: {
    orderTypes() {
      return {
        withdraw: this.$t('提币'),
        recharge: this.$t('充币'),
        contract_close: this.$t('永续合约平仓'),
        contract_open: this.$t('永续合约建仓'),
        contract_cancel: this.$t('永续合约撤单'),
        fee: this.$t('手续费'),
        exchange_open: this.$t('币币买入'),
        exchange_close: this.$t('币币卖出'),
        exchange_cancel: this.$t('币币撤单'),
        finance: this.$t('理财'),
        finance_profit: this.$t('理财收益'),
        finance_recom_profit: this.$t('理财推广收益'),
        miner_buy: this.$t('锁仓矿机'),
        miner_recom_profit: this.$t('矿机推广收益'),
        miner_profit: this.$t('矿机收益'),
        miner_back: this.$t('矿机赎回'),
        fund_close: this.$t('赎回'),
        fund_open: this.$t('购买'),
        otc_sell: this.$t('otc卖币'),
        otc_buy: this.$t('otc买币'),
        otc_cancel: this.$t('otc订单取消'),
        loan_frozen: this.$t('冻结'),
        loan_thaw: this.$t('解冻'),
        loan_closeout: this.$t('强平'),
        loan_add: this.$t('借款'),
        loan_repay: this.$t('还款'),
        finance_buy: this.$t('购买'),
        finance_back: this.$t('赎回'),
        delivery_contract_open: this.$t('交割合约建仓'),
        delivery_contract_close: this.$t('交割合约平仓'),
        bank_card_withdraw: this.$t('银行卡提现'),
        bank_card_order_cancel: this.$t('银行卡订单取消'),
        bank_card_recharge: this.$t('银行卡充值'),
        c2c_sell: this.$t('c2c卖币'),
        c2c_buy: this.$t('c2c买币'),
        ipo_promise: this.$t('认缴'),
        ipo_sell: this.$t('卖出'),
        stocks_open:this.$t('股票买入'),
        stocks_close:this.$t('股票卖出'),
        stocks_cancel:this.$t('股票取消'),
        exchange_open:this.$t('加密货币买入'),
        exchange_close:this.$t('加密货币卖出'),
        exchange_cancel:this.$t('加密货币取消'),
        etf_open:this.$t('ETF买入'),
        etf_close:this.$t('ETF卖出'),
        etf_cancel:this.$t('ETF取消'),
        rebate:this.$t('返佣'),
        rebate_1:this.$t('一级返佣'),
        rebate_2:this.$t('二级返佣'),
        rebate_3:this.$t('三级返佣'), 
      }
    },
    category() {
      return {
        coin: this.$t('法币交易'),
        contract: this.$t('合约交易'),
        exchange: this.$t('现货交易'),
        finance: this.$t('理财'),
        miner: this.$t('矿机'),
        otc: this.$t('otc'),
        loan: this.$t('质押借币'),
        bank_card: this.$t('银行卡'),
        c2c: 'C2C',
        ipo_promise: this.$t('新股认购'),
        ipo_sell: this.$t('新股认购'),
      }
    }
  },
  methods: {
    onChange(e) {
      this.list = []
      this.finished = false
      this.page = 1
      this.type = e
      this.loading = true;
      if (this.loading) {
        this.fetchData()
      }
    },
    getColor(item){
      return item == 'rebate_1' || item == 'rebate_2' || item == 'rebate_3'
    },
    onLoad() {
      this.fetchData()
    },
    fetchData() { // 获取数据
      if (this.$route.query.type) {
        this.symbolType = this.$route.query.type
      }
      _fundRecord(this.type, this.page).then(data => {
        this.list = [...this.list, ...data]
        // console.log(logs)
        this.loading = false
        if (data.length < 20) {
          this.finished = true
        } else {
          this.page++
        }
      })
    },
    onClickLeft() {
      this.$router.go(-1);
    },
    selectBtn() {
      this.isShow = !this.isShow;
    },
    selectItem(item) {
      this.title = item.title;
      this.type = item.type;
      this.isShow = false;
    },
    onSumbit() { // 查询
      this.page = 1
      this.finished = false
      this.list = []
      this.fetchData()
    }
  }
}
</script>
<style lang="scss" scoped>
#cryptos {
  :deep(.van-tabs__wrap) {
    // height: 88px !important;
  }

  :deep(.van-tabs__nav) {
    background: $tab_background;
  }

  :deep(.van-tabs__line) {
    bottom: 10px;
  }

  .accountChange {
    width: 100%;
    box-sizing: border-box;

    :deep(.van-tab--active .van-tab__text) {
      color: $active_line;
      font-weight: 700;
    }

    :deep(.van-tab__text) {
      color: $text_color1;
    }
  }



  // select样式
  .select-view {
    position: relative;
    width: 320px;
  }

  .select-box {
    width: 100%;
    height: 70px;
    line-height: 70px;
    background-color: $light-grey;
    cursor: pointer;
  }

  .option-box {
    position: absolute;
    left: 0;
    right: 0;
    top: 90px;
    width: 100%;
    background-color: $grey_bg;
    text-align: center;
    box-shadow: 0px 0px 3px 3px $grey_bg;
    border-radius: 4px;
    color: $text-color;
    z-index: 10;
  }

  .option-box>div {
    padding: 30px 0;
  }

  .option-box>div:hover {
    background-color: $grey_bg;
  }

  // 按钮
  .search-btn {
    width: 140px;
    height: 55px;
    line-height: 55px;
    text-align: center;
    border: none;
    outline: none;
  }

  .record-list {
    padding: 38px 30px;
    border-bottom: 1px solid $border_color;
  }

  .mt12 {
    margin-top: 12px;
  }

  .dividing-line {
    height: 10px;
    background-color: $divi_line;

  }

  .mtb30 {
    margin: 30px 0;
  }

  .select-icon {
    position: relative;
    bottom: 8px;
  }

  .accountChange {
    :deep(.van-tab__text) {
      font-size: 30px !important;
    }
  }

}
</style>
