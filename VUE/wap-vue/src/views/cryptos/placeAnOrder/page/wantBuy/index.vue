<template>
  <div id="wantBuyPage">
    <div id="wantBuy" class="c2cBackground fixed left-0 right-0 top-0 bottom-0 wantBuy">
      <div class="w-full h-32 pl-10 pr-8 box-border c2cBackground flex items-center c2cHeader">
        <van-icon name="arrow-left" class="text-40" @click="goback" />
        <div class="flex-1 text-right mr-20">
          <span @click="showPopup">
            <span class="text-36 mr-2">{{ $t('自选区') }}</span>
            <van-icon name="arrow-down" />
          </span>
        </div>
        <van-popup round position="top" class="pt-8" v-model:show="show" :closeable="true">
          <div class="flex justify-around c2cColor mt-10 pb-12">
            <div class="flex flex-col items-center" @click="$router.push({ path: '/cryptos/wantBuy/quick' })">
              <img class="w-14 h-14" src="../../../../../assets/image/otc/wantBuyHead/fast.png" alt="" />
              <span class="text-24 mt-2 h-8 font-medium">{{ $t('快捷区') }}</span>
            </div>
            <div class="flex flex-col items-center" @click="show = false">
              <img class="w-14 h-14" src="../../../../../assets/image/otc/wantBuyHead/optional.png" alt="" />
              <span class="text-24 mt-2 font-medium">{{ $t('自选区') }}</span>
            </div>
          </div>
        </van-popup>
        <div class="w-60 h-16 px-6 box-border box-radius text-black flex justify-around items-center"
          style="background: #c2e1ff">
          <div class="flex items-center" @click="$router.push({ path: '/cryptos/selectLegalCurrency' })">
            <span class="mr-4 text-32 font-normal">{{ exchangeCurrency }}</span>
            <img class="w-6 h-5" src="../../../../../assets/image/otc/wantBuyHead/Group.png" alt="" />
          </div>
          <span class="mx-7 w-1 h-10 ">|</span>
          <van-popover get-container="#quick" v-model="showPopover" :actions="actions" @select="onSelect"
            placement="bottom-end" theme="dark" :offset="[20, 20]">
            <template #reference>
              <img @click="showPopover = !showPopover" class="relative bottom-4 w-6 h-1 list-img"
                src="../../../../../assets/image/otc/wantBuyHead/Group2.png" alt="" />
            </template>
          </van-popover>
        </div>
      </div>
      <buy :loading="loading" :symbol="symbolList" @change="onChange" @change-direction="handleDirection"
        @confirm="selectConfirm">
        <van-pull-refresh :pulling-text="$t('下拉即可刷新')" :loosing-text="$t('释放即可刷新')" :loading-text="$t('加载中')"
          v-model="isLoading" @refresh="onRefresh">
          <template v-if="list.length > 0">
            <van-list v-model="loading" :immediate-check="false" :finished="finished" :finished-text="$t('没有更多了')"
              @load="onLoad">
              <div class="flex flex-col ml-8  mr-8" v-for="(item, index) in list" :key="'buy' + index">
                <div class="flex">
                  <div class="flex-1 textColor">
                    <div class="flex items-center">
                      <img class="w-8 h-8 mr-2" @click="handleGoUserHome(item)" :src="item.head_img" alt="">
                      <div class="font-normal text-26 mr-2">{{ item.nick_name }}</div>
                      <img class="w-8 h-8" src="@/assets/image/otc/buy/star.png" alt="">
                    </div>
                    <div class="text-28 text-grey mt-8">{{ $t('单价') }}</div>
                    <div class="mt-2">
                      <span class="text-28 mr-2">{{ item.currency }}</span>
                      <span class="text-40">{{ (item.symbol_value / 1).toFixed(2) }}</span>
                    </div>
                    <div class="mt-6">
                      <span class="text-28 text-grey mr-2">{{ $t('数量') }}</span>
                      <span class="text-28">{{ item.coin_amount && (item.coin_amount /
                        1).toFixed(item.symbol.toUpperCase() === 'USDT' ? 2
                          :
                          4) }}</span>
                    </div>
                    <div class="mt-3">
                      <span class="text-28 text-grey mr-2">{{ $t('限额') }}</span>
                      <span class="text-28 ">{{ currencySymbol }} {{ (item.investment_min / 1).toFixed(2) }} - {{
                        currencySymbol }} {{ (item.investment_max / 1).toFixed(2) }}</span>
                    </div>
                    <div class="flex mt-9 text-30">
                      <section v-for="(_item, _index) in (item.pay_type_name.split(','))" :key="_index" class="flex">
                        <div class="w-1 h-5 col mr-2">|</div>
                        <div class="mr-5">{{ _item }}</div>
                      </section>
                      <!-- <div class="w-5 h-20 cl mr-9">|</div>
                    <div>币交易</div> -->
                    </div>
                  </div>
                  <div class="flex flex-col justify-between items-end">
                    <div class="text-grey flex text-24 ">
                      <div class="flex items-center">
                        <span>{{ $t('成交量') }} {{ item.thirty_days_order }}</span>
                        <span class="w-1 h-4 mx-2" style="background: #EAEBEE"></span>
                        <span>{{ item.thirty_days_order_ratio }}%</span>
                      </div>
                    </div>
                    <div class="w-64 h-54 text-24 greyBg text-center textColor px-10"
                      v-if="item.direction === 'sell' && !methodAvaiable(item.pay_type_name, payMethods)">{{
                        $t('不满足广告方要求')
                      }}
                    </div>
                    <div v-if="item.direction === 'sell'" @click="trade('sell', item)"
                      class="w-40 h-14 text-30 text-white rounded-lg bg-red text-center">{{ $t('出售') }}
                    </div>
                    <div v-if="item.direction === 'buy'" @click="trade('buy', item)"
                      class="w-40 h-14 text-30 text-white rounded-lg bg-green text-center">{{ $t('购买') }}
                    </div>
                  </div>
                </div>
                <div class="flex justify-center mb-3">
                  <div class="w-full h-2 diviLine mt-10 mb-11 box-border"></div>
                </div>
              </div>
            </van-list>
          </template>
          <template v-if="list.length === 0 && loading === false && isLoading === false">
            <div>
              <van-empty class="custom-image" :description="$t('暂无广告')">
                <template #image>
                  <img class="no-data-img" src="@/assets/image/otc/nodatas.png" />
                </template>
              </van-empty>
            </div>

          </template>
        </van-pull-refresh>
      </buy>
      <van-dialog v-model="showJie" :showConfirmButton="false">
        <div class="dia-main">
          <p class="dia-change">{{ $t('切换到接单模式') }}</p>
          <p class="dia-text">{{ $t('接单模式适用于有发布广告交易需求的用户。') }}</p>
          <div class="dia-btn">
            <div @click="showJie = false">{{ $t('取消') }}</div>
            <div style="background: #1d91ff; color: #ffffff" @click="linkToC2C">
              {{ $t('确认') }}
            </div>
          </div>
        </div>
      </van-dialog>
    </div>
  </div>
</template>

<script>
import {
  SET_ORDER_MODE
} from "@/store/const.store";
import { _getExchangeRate } from "@/service/home.api";
import { Icon, Popup, Popover, Dialog, List, PullRefresh, Empty, showLoadingToast, closeToast, Notify } from 'vant'
import listLoadMixins from '@/utils/list-load-mixins.js'
import Buy from '../../components/buy/index.vue'
import otcApi from "@/service/otc.api.js";
import { mapActions, mapGetters, mapMutations } from 'vuex'
import { showToast } from 'vant';
// import payment from "@/store/modules/payment";


export default {
  beforeRouteLeave(to, from, next) {
    // 保存离开页面时的列表的位置
    const position = document.querySelector('.roll-section').scrollTop
    window.sessionStorage.setItem('position', JSON.stringify(position))
    next()
  },
  props: {
    showDot: {
      type: Boolean,
      default: true
    }
  },
  name: 'wantBuy',
  mixins: [listLoadMixins],
  components: {
    [Icon.name]: Icon,
    [Popup.name]: Popup,
    [Popover.name]: Popover,
    [Dialog.name]: Dialog,
    [List.name]: List,
    [Empty.name]: Empty,
    [PullRefresh.name]: PullRefresh,
    Buy,
  },
  computed: {
    ...mapGetters('c2c', ['symbolList', 'exchangeCurrency', 'symbol', 'currencySymbol']),
    ...mapGetters('home', ['currency'])
  },
  async created() {
    console.log(this.exchangeCurrency, '币种')
    if (!this.exchangeCurrency) {
      this.getExchangeRate()
    }
    await this.SET_SYMBOL_LIST()
    await this.SET_PAY_METHODS({ language: this.$i18n.locale })
    // this.SET_CURRENCY(this.currency.currency)
    // TODO: 临时解决方案，后续需要优化this.currency.currency和this.exchangeCurrency需要处理成统一的
    // if (!this.exchangeCurrency) {
    //   console.log(this.currency.currency);
    //   this.SET_CURRENCY(this.currency.currency)
    // }

    if (!this.currencySymbol) {
      this.SET_CURRENCY_SYMBOL(this.currency.currency_symbol)
    }

    otcApi.ctcPaymentMethodList({ language: this.$i18n.locale }).then(res => {
      res.map(item => {
        this.payMethods.push(item.methodName)
      })
    })
    this.params = {
      page_no: this.page_no, // 页码
      direction: this.$store.state.c2c.direction, // 买 or 卖
      currency: this.exchangeCurrency, // 法币
      symbol: this.symbol, // 配置的加密货币
      amount: '', // 兑换金额
      method_type: '', // 支付方式
      language: this.$i18n.locale
    }
    this.get()
  },
  data() {
    return {
      showJie: false,
      show: false,
      showPopover: false,
      page_no: 1,
      list: [],
      direction: 'buy', //
      params: {},
      loading: false,
      payMethods: [],
      actions: [
        {
          text: this.$t('收款方式'),
          icon: 'setting-o',
          path: '/cryptos/paymentMethod',
        },
        // {
        //   text: this.$t('c2c帮助中心'),
        //   icon: this.handleImage('../../../../../assets/image/otc/buy/help_icon.png'),
        //   path: '/c2cHelpCenter',
        // },
        // {
        //   text: 'c2c用户中心',
        //   icon: this.handleImage('../../../../../assets/image/otc/buy/user_icon.png'),
        //   path: '/c2c_normal_user',
        // },
        // {
        //   text: this.$t('接单模式'),
        //   icon: this.handleImage('../../../../../assets/image/otc/buy/order_icon.png'),
        // },
      ],
    }
  },
  methods: {
    ...mapActions('c2c', ['SET_SYMBOL_LIST', 'SET_PAY_METHODS']),
    ...mapMutations('c2c', ['SET_CURRENCY', 'SET_CURRENCY_SYMBOL']),
    handleImage(url) {
      return new URL(url, import.meta.url).href
    },
    getExchangeRate() {
      _getExchangeRate().then((res) => {
        this.SET_CURRENCY(res.currency)
      })
    },
    onChange(params) { // 买卖变化
      this.params = Object.assign(this.params, params)
      this.list = []
      this.get()
    },
    methodAvaiable(payStr, arr) {
      console.log(payStr, arr)
      let exist = false
      for (let i = 0; i < arr.length; i++) {
        if (payStr.indexOf(arr[i]) > -1) {
          exist = true
          break
        }
      }
      return exist
    },
    get() {
      showLoadingToast({
        duration: 0,
        forbidClick: true,
        message: this.$t('加载中')
      })
      otcApi.ctcAdvertList(this.params).then(res => {
        closeToast();
        if (res && res.length > 0) {
          this.handleData(res)
        }
      })
    },
    linkToC2C() {
      // if (this.$store.state.user.userInfo.c2c_user_type === 0) { // 普通用户
      //   Toast('你不是平台认证商家')
      //   return
      // }
      this.$router.push('/c2c/receivingBuy')
      this.$store.commit(`c2c/${SET_ORDER_MODE}`, {
        state: true,
      })
    },
    showPopup() {
      this.show = true
    },
    onSelect(action) {
      if (action.text === '接单模式') {
        this.showJie = true
        return
      }
      this.$router.push({
        path: action.path,
      })
    },
    handleDirection(direction) {
      console.log(direction);
      this.direction = direction;
      this.params.direction = this.direction;
      // 发送请求
      this.get()
    },
    handleGoUserHome(item) {
      console.log('去买卖用户首页', item)
      this.$router.push({
        path: '/cryptos/advertiserDetail',
        query: {
          uid: item.c2c_user_id
        }
      })
    },
    trade(type, data) {
      // 支付方式是否满足
      if (type == 'sell' && !this.methodAvaiable(data.pay_type_name, this.payMethods)) {
        console.log(data.pay_type_name)
        console.log(this.payMethods)
        showToast(this.$t('请添加相应的收款方式'))
        setTimeout(() => {
          this.$router.push('/cryptos/paymentMethod')
        }, 500);
        showToast(this.$t('不满足广告方要求'))
        //Notify({ type: 'danger', message: '不满足广告方要求' })
        return
      }
      //
      if (data && data.isRemoved) {
        this.showRemoved = true;
        return;
      }
      this.$store.commit('c2c/SET_ADV_ID', data.id) // 保存广告id
      this.$router.push({
        name: 'c2cTrade'
        // query: {
        //   type,
        //   id: data.id
        // }
      })
    },
    selectConfirm(params) {
      this.params = Object.assign(this.params, params)
      this.list = []
      this.get();
    },
    goback() {
      let frompath = this.$route.query.frompath
      if (frompath) {
        this.$router.push(frompath)
      } else {
        this.$router.push('/quotes/index?tabActive=1')
      }
    }
  },
}
</script>

<style lang="scss" scoped>
#wantBuyPage {
  font-size: 30px;

  .c2cHeader {
    color: $text_color;
  }

  .wantBuy {
    width: 100%;
    box-sizing: border-box;
  }

  .dia-main {
    padding: 50px;
    color: #000;

    .dia-change {
      text-align: center;
      margin-bottom: 32px;
      font-size: 35px;
      font-weight: bold;
    }

    .dia-text {
      margin-bottom: 40px;
    }

    .dia-btn {
      display: flex;
      justify-content: space-between;

      div {
        width: 240px;
        height: 80px;
        text-align: center;
        line-height: 80px;
        background: #eaebee;
        color: #21262f;
        font-size: 32px;
        border-radius: 8px;
      }
    }
  }

  .box-radius {
    border-radius: 40px;
  }

  .list-img {
    top: 0px;
  }

  :deep(.van-empty__image) {
    width: 180px;
    height: 180px;
  }
}
</style>
