<template>
  <div id="withdrawPagePage">
    <div class="withdrawl-page text-28">
      <assets-head title="">
        <img src="../../../assets/image/assets-center/exchange.png" alt="exchange-img" class="w-11 h-9"
          @click="goRecord" />
      </assets-head>
      <div class="px-8 pb-12">
        <h1 class="text-52 pt-10 textColor">{{ $t('提币 ') + value }} </h1>
        <p class="text-grey text-30 mt-7">{{ $t('提币 ') + value + $t(' 到数字币地址') }}</p>
        <div @click="showSymbol = true"
          class="flex border-grey items-center items-center justify-between rounded-lg mt-10 inputBackground textColor">
          <input disabled v-model="value" class="flex-1 h-24 border-none px-4 inputBackground textColor" />
          <p class="flex items-center">
            <span>{{ $t('选择币种') }}</span>
            <img src="@/assets/image/icon-left_arrow.png" alt="logo" class="w-5 h-8 ml-7 mr-10"
              style="transform: rotate(-90deg)" />
          </p>
        </div>
        <p class="text-35 mt-11 textColor">{{ $t('区块链网络') }}</p>
        <ul class="flex mt-5  flex-wrap">
          <li
            class="w-52 h-24 mb-5 mr-10 rounded rounded-lg border border-solid flex justify-center items-center textColor"
            :class="blockchainIndex == index ? 'active' : ''" v-for="(item, index) in blockList" :key="item.id"
            @click="changeBlockchain(item, index)">{{ item.blockchain_name }}</li>
        </ul>
        <p class="text-35 mt-5 textColor">{{ $t('地址') }}</p>
        <div class="inputBackground flex items-center justify-between h-24 rounded mt-11">
          <input :placeholder="$t('长按粘贴')" class="h-full flex-1 border-none bg-none px-4 textColor" v-model="address" />
          <div class="flex items-center">
            <span class="colorMain" @click="pastCont">{{ $t('粘贴') }}</span>
            <div>
              <img @click="clickCode()" src="@/assets/image/public/scan.png" alt="logo" class="w-16 h-16 ml-5 mr-8" />
            </div>
          </div>
        </div>
        <p class="text-35 mt-11 textColor">{{ $t('数量') }}</p>
        <div class="inputBackground flex items-center justify-between h-24 rounded mt-5">
          <input :placeholder="$t('请输入提币数量')" maxlength="10"
            class="h-full flex-1 border-none bg-none px-4 text-5 textColor" type="number" v-model="amount"
            @input="changeInput" />
          <p class="flex items-center">
            <!--<span class="text-grey">USDT</span>-->
            <span class="text-grey">{{ value }}</span>
            <span class="colorMain ml-5 mr-8" @click="allBtn">{{ $t('全部') }}</span>
          </p>
        </div>
        <p class="flex justify-between text-grey mt-5 text-28">
          <span>{{ $t('可用') }}</span>
          <!--<span>{{ usdtBalance }} USDT</span>-->
          <span>{{ getwei(usdtBalance * 1, numLenth) }} {{ value }}</span>
        </p>
        <p class="text-grey mt-16 text-7">{{ $t('可到账数量') }}</p>
        <div class="text-40 mt-5 fon-bold ">
          <span class="text-40 font-bold textColor"> {{ volume_last || '0.00' }}</span>
          <!--<span class="text-32 ml-5 text-grey">USDT</span>-->
          <span class="text-32 ml-1 text-grey">{{ value }}</span>
        </div>
        <p class="text-grey mt-5 text-28">{{ $t('提现费用') }}：{{ fee || '0.00' }} {{ value }}</p>
        <p class="text-white mt-11 textColor text-28">{{ $t('提币说明') }}：
        </p>
        <div class="text-grey mt-2 text-28" v-html="tip"></div>
        <van-button class="w-full h-25 rounded flex justify-center items-center mt-14  text-white btnMain border-none"
          @click="showDetail">
          {{ $t('提现') }}</van-button>

      </div>
      <van-popup v-model:show="show" position="bottom">
        <div class="pop-box relative text-28">
          <img src="@/assets/image/icon-close.png" class="w-9 h-9 absolute right-5 top-5" @click="show = false" />
          <p class="text-38 text-center pt-4 textColor">{{ $t('确认订单') }}</p>
          <p class="text-35 mt-12 text-grey text-center">{{ $t('实际到账') }}</p>
          <p class="text-50 mt-6 text-center textColor">{{ volume_last }} <span class="text-grey text-35">
              {{ value }}</span></p>
          <ul class="border-b-color px-8 pb-8">
            <li class="flex justify-between text-32 mt-6 items-center">
              <span class="textColor">{{ $t('提币地址') }}</span>
              <span class="text-grey text-26">{{ address }}</span>
            </li>
            <li class="flex justify-between text-32 mt-6">
              <span class="textColor">{{ $t('主网络') }}</span>
              <span class="text-grey">{{ blockName }}</span>
            </li>
            <li class="flex justify-between text-32 mt-6">
              <span class="textColor">{{ $t('提现来源账户') }}</span>
              <span class="text-grey">{{ $t('钱包账户') }}</span>
            </li>
          </ul>
          <ul class="px-8 mt-8">
            <li class="flex justify-between text-32 mt-6">
              <span class="textColor">{{ $t('币种') }}</span>
              <span class="text-grey">{{ value }}</span>
            </li>
            <li class="flex justify-between text-32 mt-6">
              <span class="textColor">{{ $t('金额') }}</span>
              <span class="text-grey">{{ amount }}&nbsp;{{ value }}</span>
            </li>
            <li class="flex justify-between text-32 mt-6">
              <span class="textColor">{{ $t('网络手续费') }}</span>
              <span class="text-grey">{{ fee }}&nbsp;{{ value }}</span>
            </li>
          </ul>
          <p class="px-8 mt-12 text-30 textColor">
            1. {{ $t('请确保您输入了正确的提币地址并且您选择的转账网络与地址相匹配') }}
          </p>
          <p class="px-8 mt-10 text-30 textColor">2. {{ $t('提币订单创建后不可取消。') }}</p>
          <p class="w-4/5 h-24 rounded flex justify-center items-center mt-14 mb-12 btnMain text-white m-auto"
            @click="onConfirm">
            {{ $t('确定') }}</p>
        </div>
      </van-popup>
      <!-- 币种 -->
      <van-action-sheet v-model:show="showSymbol" :actions="actions.filter(item => item.name !== value)"
        @select="onSelect" />
      <bar-scan v-if="scancode" @ok="getResult"></bar-scan>
    </div>
  </div>
</template>

<script>
import { Popup, ActionSheet, showToast } from "vant"
import Axios from "@/service/recharge.js"
import { _withdrawFee } from "@/service/withdraw.api.js"
import assetsHead from "@/components/Transform/assets-head/index.vue";
import BarScan from '@/components/Transform/scan/scan.vue';
import { _getAllWallet } from "@/service/fund.api";
export default {
  name: "withdrawPage",
  components: {
    assetsHead,
    [Popup.name]: Popup,
    [ActionSheet.name]: ActionSheet,
    BarScan
  },
  data() {
    return {
      tip: '',
      value: 'USDT', //选中的币种
      show: false,
      showSymbol: false,
      actions: [{ name: 'USDT' }, { name: 'BTC' }, { name: 'ETH' }],
      blockList: [],
      blockchainIndex: "",
      usdtBalance: "", //余额
      amount: "",//提现金额
      fee: "", //手续费
      volume_last: "",//到账金额
      blockName: "",//当前链名称
      address: "",// 二维码扫描结果
      scancode: false,//控制是否使用扫一扫功能
      withdraw_fee_type: '',
      percent: 0,
      numLenth: 3,
      routerType: '',
    }
  },
  created() {
    this.getBlock(this.value);
    this.getAvailable(this.actions[0].name);


    Axios.getRechargeTips({
      language: this.$i18n.locale,
      token: this.$store.state.user.userInfo.token,
      content_code: '003',
    }).then(res => {
      this.tip = res.content
    }).catch(err => {
      if (err.code === 'ECONNABORTED') { showToast(this.$t('网络超时！')); }
      else if (err.msg !== undefined) { showToast(this.$t(err.msg)); }
    })
    _withdrawFee({
      amount: 1,
      channel: this.value + '_' + this.blockName
    }).then((res) => {
      this.withdraw_fee_type = res.withdraw_fee_type
      if (this.withdraw_fee_type == 'rate') {
        this.percent = res.fee
      } else {
        this.fee = res.fee;
      }
    });

    if (this.$route.query.type) {
      this.routerType = this.$route.query.type;
    }
  },
  methods: {
    goRecord() {
      console.log(this.$route.query.type);
      this.routerType === 'exchange' ? this.$router.push('/Record/DepositAndWithdrawal') : this.goRouter('/cryptos/assetsCenter/rechargeWithdrawRecord');
    },

    getwei(value, numLenth) {
      console.log(numLenth)
      let str = value.toString();
      let strIndex = str.indexOf('.');
      if (strIndex === -1) return str;
      str = str.substring(0, strIndex + numLenth);
      console.log(str, numLenth)
      return str;
    },
    // 打开相机
    clickCode() {
      this.scancode = true;
    },
    //返回扫描结果并关闭摄像头
    getResult(result) {
      this.address = result;
      if (result !== "") {
        this.scancode = false;
      }
    },
    showDetail() {
      if (this.address.length > 63 || this.address.length == 0) {
        showToast(this.$t('提现地址格式错误'))
        return
      }
      if (!this.amount) {
        showToast(this.$t('请输入正确数量'))
        return
      }
      if (this.amount / 1 > this.usdtBalance / 1) {
        showToast(this.$t('金额不足'))
        return
      }
      this.show = true;
    },
    goRouter(params) {
      this.$router.push({
        path: params,
        query: {
          type: '2',
          back: "1"
        }
      });
    },
    // onConfirm() {
    //     this.$router.push('/withdraw/withdrawSumbit')
    // },
    //选中币种
    onSelect(item) {
      this.amount = ''
      this.volume_last = ''
      this.fee = ''
      this.showSymbol = false;
      this.value = item.name;
      this.withdraw_fee_type = ''
      this.getBlock(item.name);
      this.getAvailable(item.name)
      if (item.name == 'BTC' || item.name == 'ETH') {
        this.numLenth = 7
        _withdrawFee({
          amount: 1,
          channel: this.value + '_' + this.blockName
        }).then((res) => {
          this.withdraw_fee_type = res.withdraw_fee_type
          if (this.withdraw_fee_type == 'fixed') {
            this.fee = res.fee;
          } else {
            this.percent = res.fee
          }
        });
      } else {
        this.numLenth = 3
      }
    },
    //根据币种获取链地址
    getBlock(symbol) {
      Axios.getBlock({
        coin: symbol
      }).then((res) => {
        this.blockList = res;
        this.blockName = res[0].blockchain_name;
      });
    },
    //选中链名称
    changeBlockchain(item, index) {
      this.blockchainIndex = index;
      this.blockName = item.blockchain_name;
    },
    //获取钱包余额
    getAvailable(symbol) {
      _getAllWallet({ symbolType: 'cryptos' }).then((res) => {
        let walletList = res.extends;
        let initObj = walletList.find(item => {
          return item.symbol.toLowerCase() == symbol.toLowerCase()
        })
        this.usdtBalance = initObj.volume
      });
    },
    //点全部
    allBtn() {
      this.amount = this.getwei(this.usdtBalance, this.numLenth);
      this.changeInput()
    },
    //输入改变
    changeInput() {
      if (this.amount === '') {
        this.volume_last = '0.00'
        return
      }
      if (this.amount.length > 11) {
        this.amount = this.amount.slice(0, 11)
      }

      if (this.withdraw_fee_type == 'part') {
        _withdrawFee({
          amount: this.amount,
          channel: this.value + '_' + this.blockName
        }).then((res) => {
          console.log(res)
          this.fee = res.fee;
          this.volume_last = res.volume_last;
        });
      } else if (this.withdraw_fee_type == 'rate') {
        if (this.amount == '') {
          this.volume_last = 0
        } else {
          this.fee = this.getwei(this.percent * this.amount, this.numLenth)
          this.volume_last = this.getwei(this.amount * 1 - this.percent * this.amount, this.numLenth)
        }
      } else {
        if (this.amount * 1 < this.fee * 1) {
          console.log('a')
          this.volume_last = 0
        } else {
          this.volume_last = this.getwei(this.amount * 1 - this.fee * 1, this.numLenth)
        }
      }

    },
    //提币提交
    onConfirm() {
      if (!this.amount) {
        showToast(this.$t('请输入数量'));
        return;
      }

      let numReg = /^[0-9]+([.]{1}[0-9]+){0,1}$/;
      if (!numReg.test(this.amount)) {
        showToast(this.$t('请输入数字'));
        return;
      }

      if (!this.address) {
        showToast(this.$t('请输入提现地址'));
        return;
      }

      if (this.amount && this.address) {
        this.$router.push({
          path: '/cryptos/withdraw/securityVerification',
          query: {
            amount: this.amount,
            from: this.address,
            channel: this.value + '_' + this.blockName
          }
        })
        // AxiosWithdraw.WithdrawApply({
        //     session_token:this.sessionToken,
        //     amount:this.amount,
        //     from:this.address,
        //     channel:this.value+'_'+ this.blockName
        // }).then((res)=> {
        //     if(res.code==0) {
        //         this.$router.push({
        //             path:"/withdraw/withdrawSumbit"
        //         });
        //     }else{
        //       this.$toast(this.$t(res.msg));
        //     }
        //     this.getToken();
        // });
      } else {
        showToast(this.$t('请输入提币数量'));
      }
    },
    async pastCont() {
      this.address = await navigator.clipboard.readText();
    }
  }
}
</script>
<style lang="scss" scoped>
#withdrawPagePage {
  .withdrawl-page {
    width: 100%;
    box-sizing: border-box;
    text-size: 26px;
  }

  .van-popup.van-popup--bottom {
    border-top-left-radius: 30px;
    border-top-right-radius: 30px;
  }

  .pop-box {
    border-top-left-radius: 30px;
    border-top-right-radius: 30px;
  }

  .color-blue {
    color: $btn_main;
  }

  .bg-grey-light {
    background: $light-grey;
  }

  li {
    &.active {
      border-color: $color_main;
      color: $color_main;
    }
  }

  .van-button--default {
    color: $white;
  }

  .btnMain {
    background: $btn_main;
  }
}
</style>
