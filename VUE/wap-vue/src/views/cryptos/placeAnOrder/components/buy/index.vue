<template>
  <div id="c2cIndexPage">
    <div style="overflow: hidden" class="c2cTabBackground absolute left-0 right-0 bottom-0 top-28 box-ra flex flex-col"
      :class="fullBottom">
      <div class="flex justify-between mt-11 mb-10 items-center">
        <div class="flex ml-8 text-grey">
          <div class="font-normal text-36 mr-20 " @click="switchDirection('buy')"
            :class="{ 'textColor': buyTab === 'buy' }">
            {{ $t('我要买') }}
          </div>
          <div class="font-normal text-36 " @click="switchDirection('sell')" :class="{ 'textColor': buyTab === 'sell' }">
            {{ $t('我要卖') }}
          </div>
        </div>
        <img @click="$router.push({ path: '/cryptos/wantBuy/c2cOrderList' })" class="w-5 h-8 mr-14"
          src="@/assets/image/otc/buy/subtract.png" alt="">
      </div>
      <!-- <div class="flex"> -->
      <!-- <div class="flex flex-col mr-80 ml-32" @click="tab = 0" :class="{'text-blue': tab === 0}" v-for="(item, index) in symbol" :key="index">
      <div class="font-normal text-26 mb-18">{{item}}</div>
      <div class="h-4 w-48 text-white" :class="{'text-blue': tab === 0}">一</div>
    </div> -->
      <!-- <div class="flex flex-col " @click="tab = 1" :class="{'text-blue': tab === 1}">
      <div class="font-normal text-26 mb-18 mr-80">BTC</div>
      <div class="h-4 w-48 text-white" :class="{'text-blue': tab === 1}">一</div>
    </div>
    <div class="flex flex-col " @click="tab = 2" :class="{'text-blue': tab === 2}">
      <div class="font-normal text-26 mb-18">ETH</div>
      <div class="h-4 w-48 text-white" :class="{'text-blue': tab === 2}">一</div>
    </div> -->
      <!-- </div> -->
      <!--    <van-tabs v-model="tab" @change="onchange">-->
      <!--      <van-tab :title="item" v-for="(item, index) in symbol" :key="index"></van-tab>-->
      <!--    </van-tabs>-->
      <div class="tabs flex px-8 items-center" style="overflow-x: auto;touch-action: auto;">
        <div :class="{ 'active': tab === index }" @click="onchange(index)"
          style="flex-shrink: inherit;touch-action: auto;" v-for="(item, index) in symbol" :key="index"
          class="mr-20 item flex flex-col items-center  textColor1">
          <span class="text-26">{{ item }}</span>
          <div v-if="tab === index" class="w-12 h-2 mt-5"></div>
          <div v-else class="w-12 h-1 mt-5"></div>
        </div>
      </div>
      <div class="mt-6 flex justify-between">
        <div class="flex ml-4">
          <div class="mr-20">
            <van-dropdown-menu active-color="#1989fa">
              <van-dropdown-item :title="$t('金额')" ref="amountItem">
                <div class="exchangeCurrency-input">
                  <van-field v-model="value1" :placeholder="$t('请输入总额')">
                    <template #extra>
                      {{ exchangeCurrency }}
                    </template>
                  </van-field>
                </div>
                <div class="flex flex-wrap justify-between px-5 box-border mb-14 mt-3">
                  <div
                    class="font-normal text-28 w-60 h-18 mb-3 rounded-md tabBackground c2cColor text-center mr-3 flex justify-center items-center"
                    :class="{ 'active_trade': index === numListActive }" v-for="(item, index) in numList"
                    :key="'num' + index" @click="onQuickAmount(item, index)">
                    {{ item.num }}
                  </div>
                </div>
                <div class="flex mb-9 justify-center ">
                  <button class="w-80 h-20 tabBackground c2cColor rounded-lg font-normal text-30 border-none mr-5"
                    :class="[{ 'bg-blue': tabindex === 0 }, { 'text-white': tabindex === 0 }]" @click="onResetMoney">{{
                      $t('重置')
                    }}
                  </button>
                  <button class="w-80 h-20 btnMain c2cColor rounded-lg font-normal text-30 border-none"
                    :class="[{ 'bg-blue': tabindex === 1 }, { 'text-white': tabindex === 1 }]" @click="onConfirmMoney">
                    {{ $t('确认') }}
                  </button>
                </div>
              </van-dropdown-item>
            </van-dropdown-menu>
          </div>
          <div class="  ">
            <van-dropdown-menu active-color="#1989fa">
              <van-dropdown-item :title="$t('交易方式')" ref="payTypeItem">
                <div class="flex mb-5 justify-center mt-11 flex-wrap">
                  <button v-show="showTab !== 0"
                    class="w-40 h-20 tabBackground c2cColor rounded-lg font-normal text-28 border-none mr-8"
                    @click="onAllType">
                    {{ $t('全部') }}
                  </button>
                  <div class="relative" v-show="showTab === 0" @click="showTab = 2">
                    <button class=" w-40 h-20 tabBackground c2cColor rounded-lg font-normal text-28 border-none mr-8">{{
                      $t('全部')
                    }}
                    </button>
                    <img class="absolute top-0 right-0 w-60 h-20 mr-8" src="@/assets/image/otc/wantBuyHead/trade_bg.png"
                      alt="">
                  </div>
                  <button class="w-60 minh-82 tabBackground c2cColor rounded-lg font-normal text-24 border-none mr-8 mb-5"
                    :class="{ 'active_trade': showTab == index + 1 }" @click="onShowTab(index)"
                    v-for="(item, index) in payMethods" :key="index">{{ item }}
                  </button>
                  <!-- <button class="w-232 h-82 bg-grey rounded-lg font-normal text-28 border-none "
                        :class="{'active_trade' : showTab === 2} " @click="showTab = 2">币收款
                </button> -->
                </div>
                <div class="text-24 text-center mb-8 text-grey">{{ $t('仅展示可用的交易方式') }}</div>
              </van-dropdown-item>
            </van-dropdown-menu>
          </div>
        </div>
        <div class="flex mr-8 items-center" @click="selectClick">
          <div class="mr-2 text-24 font-normal textColor">{{ $t('筛选') }}</div>
          <img class="w-5 h-55" src="@/assets/image/otc/buy/Vector.png" alt="">
        </div>
      </div>
      <div class="flex justify-center mb-14">
        <div class="w-full h-1 diviLine mt-7 box-border mx-8 "></div>
      </div>
      <!-- loading / empty -->
      <!--      <div class="flex justify-center" :class="{'pt-100': !buyList.length}">-->
      <!--        <van-loading color="#1989fa" v-if="loading"/>-->
      <!--        <van-empty description="没有广告" v-if="!loading && !buyList.length"/>-->
      <!--      </div>-->
      <div class="flex-1 roll-section" style="overflow-y: auto">
        <slot></slot>
      </div>

      <van-popup class="w-160 rounded-xl" v-model:show="showRemoved">
        <div class="flex flex-col items-center pt-12 pb-10 px-8  bg-white">
          <img class="w-19 h-18" src="~@/assets/image/c2c/Group41-3.png" alt="">
          <div>
            <p class="mt-9 mb-10 text-28">{{ $t('该广告已下架。请选择其他广告。') }}</p>
            <van-button @click.native="showRemoved = false" class="w-full h-20 rounded-lg bg-blue" type="info">
              {{ $t('确认') }}
            </van-button>
          </div>
        </div>
      </van-popup>

      <!--筛选-->
      <van-popup class="w-full popHeight" round v-model:show="showSelect" position="bottom">
        <ad-screening @back="handlerBack" @confirm="confirm" />
      </van-popup>
    </div>
  </div>
</template>
<script>
import { Cell, Icon, Popup, DropdownMenu, DropdownItem, Field, Button, Tabs, Tab } from 'vant';
import adScreening from "../../page/adScreening.vue";
import { mapGetters } from 'vuex'

export default {
  name: "BuyPage",
  components: {
    [Icon.name]: Icon,
    [Field.name]: Field,
    [Popup.name]: Popup,
    [Cell.name]: Cell,
    [DropdownMenu.name]: DropdownMenu,
    [DropdownItem.name]: DropdownItem,
    [Button.name]: Button,
    [Tabs.name]: Tabs,
    [Tab.name]: Tab,
    adScreening,
  },
  props: {
    buyList: { // 承兑商列表
      type: Array,
      default() {
        return []
      }
    },
    symbol: {
      type: Array,
      default() {
        return []
      }
    },
    loading: {
      type: Boolean,
      default: false
    },
    finished: {
      type: Boolean,
      default: false
    },
    isLoading: {
      type: Boolean,
      default: false
    },
  },
  created() {
    // 设置选中tab
    const index = this.symbol && this.symbol.length > 0 ? this.symbol.findIndex(item => item === this.$store.state.c2c.symbol) : 0
    this.tab = index
    // direction
    this.buyTab = this.$store.state.c2c.direction

  },
  data() {
    return {
      showSelect: false, // 显示筛选
      showRemoved: false, // 显示已下架弹窗
      tabindex: 1,
      tab: 0,
      showTab: 0,
      buyTab: 0,
      // buyList1: [
      //   {
      //     name: 'BitcomTxm',
      //     unit_price: '0.960',
      //     quantity: '498.6 USDT',
      //     limit: '$200.50 - $483.64',
      //     volume: '158',
      //     Transaction_rate: '78%',
      //     bgm: require('@/assets/image/otc/buy/B.png'),
      //     bgm2: require('@/assets/image/otc/buy/star.png'),
      //     uid: 'dsadas',
      //   },
      //   {
      //     name: 'ETHCoin',
      //     unit_price: '0.960',
      //     quantity: '498.6 USDT',
      //     limit: '$200.50 - $483.64',
      //     volume: '158',
      //     Transaction_rate: '78%',
      //     bgm: require('@/assets/image/otc/buy/E.png'),
      //     bgm2: require('@/assets/image/otc/buy/star.png'),
      //     uid: 'sss',
      //     isRemoved: true, // 已下架
      //   },
      //   {
      //     name: 'BitcomTxm',
      //     unit_price: '0.960',
      //     quantity: '498.6 USDT',
      //     limit: '$200.50 - $483.64',
      //     volume: '158',
      //     Transaction_rate: '78%',
      //     bgm: require('@/assets/image/otc/buy/B.png'),
      //     bgm2: require('@/assets/image/otc/buy/star.png'),
      //     uid: '333',
      //   },
      //   {
      //     name: 'BitcomTxm',
      //     unit_price: '0.960',
      //     quantity: '498.6 USDT',
      //     limit: '$200.50 - $483.64',
      //     volume: '158',
      //     Transaction_rate: '78%',
      //     bgm: require('@/assets/image/otc/buy/B.png'),
      //     bgm2: require('@/assets/image/otc/buy/star.png'),
      //     uid: '5444',
      //   },
      // ],
      value: 0,
      value1: '',
      switch1: false,
      switch2: false,
      numListActive: -1,
      numList: [
        {
          num: '100',
          value: '100'
        }, {
          num: '1000',
          value: '1000'
        }, {
          num: '5000',
          value: '5000'
        }, {
          num: this.$i18n.locale === 'CN' || this.$i18n.locale === 'zh-CN' ? '1万' : '10k',
          value: '10000'
        }, {
          num: this.$i18n.locale === 'CN' || this.$i18n.locale === 'zh-CN' ? '10万' : '100k',
          value: '100000'
        }, {
          num: this.$i18n.locale === 'CN' || this.$i18n.locale === 'zh-CN' ? '20万' : '200k',
          value: '200000'
        }
      ]
    }
  },
  methods: {
    onAllType() { //全部
      this.showTab = 0
      this.$emit('change', { method_type: '' })
      this.$refs.payTypeItem.toggle()
    },
    onShowTab(index) { // 选择交易方式
      this.showTab = index + 1
      this.$emit('change', { method_type: index })
      this.$refs.payTypeItem.toggle()
    },
    onResetMoney() { // 重置金额
      this.value1 = ''
      this.numListActive = -1
    },
    onConfirmMoney() { // 确定金额
      this.$emit('change', { amount: this.value1 })
      this.$refs.amountItem.toggle()
    },
    onQuickAmount(item, index) { // 快捷金额
      this.numListActive = index
      this.value1 = item.value
      // console.log(item)
    },
    // 筛选
    selectClick() {
      this.showSelect = true;
    },
    // 隐藏筛选
    handlerBack() {
      this.showSelect = false;
    },
    // 筛选确定
    confirm(params) {
      console.log(params)
      this.$emit('confirm', params)
    },

    switchDirection(direction) { // 我要买/卖
      this.buyTab = direction
      this.$store.commit('c2c/SET_DIRECTION', direction)
      this.$emit('change', { direction })
    },
    onchange(index) { // tab改变
      this.tab = index
      this.$store.commit('c2c/SET_SYMBOL', this.symbolList[index])
      this.$emit('change', { symbol: this.symbolList[index] })
    }
  },
  computed: {
    ...mapGetters('c2c', ['symbolList', 'payMethods', 'exchangeCurrency']),
    fullBottom() {
      return this.$route.path === '/cryptos/wantBuy' ? 'bottom-0' : 'bottom-160';
    }
  }
}
</script>

<style lang="scss" scoped>
#c2cIndexPage {

  ::-webkit-scrollbar {
    width: 0;
    height: 0;
    color: transparent;
  }

  .tabs {
    .item:last-child {
      margin-right: 0;
    }
  }

  .popHeight {
    height: calc(100% - 122px);
  }

  .box-ra {
    border-radius: 80px 80px 0 0;
  }

  .col {
    color: #E7BB41;
  }

  .cl {
    color: var(--theme-color);
  }

  .active {
    color: var(--theme-color);
  }

  .active_trade {
    color: $main-btn-color;
    background-color: $color_main;
  }

  :deep(.van-dropdown-menu__bar) {
    background-color: transparent;
    box-shadow: none;
  }

  .minh-82 {
    min-height: 82px;
  }

  .mb-52 {
    margin-bottom: 52px;
  }

  .c2cTabBackground {
    .van-cell {
      background: $input_background;
      color: $text_color;
    }

    :deep(.van-field__control) {
      color: $c2c_color;
    }
  }

  .exchangeCurrency-input {
    padding: 10px 0;
    margin-top: 10px;
    overflow: hidden;

    :deep(.van-cell) {
      padding: 20px;
    }
  }
}
</style>
