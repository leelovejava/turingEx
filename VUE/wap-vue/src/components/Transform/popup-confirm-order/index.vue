<template>
  <div class="popup-delivery w-350 rounded-xl overflow-hidden p-8 box-border pb-25 text-28">
    <div class="border-b-color pt-27 pb-18 relative ">
      <h1 class="text-30">{{ $t('订单确认') }}</h1>
      <img src="@/assets/image/icon-close.png" class="w-14 h-14 absolute right-0 top-0" @click="onClose" />
    </div>
    <div class="flex flex-wrap pt-16 tabBackground p-10 box-border">
      <div class="w-half flex justify-between items-center pr-10 box-border"><span class="text-grey">{{ $t('交易品种')
      }}</span><span class="textColor">{{ symbolName }}</span></div>
      <div class="w-half flex justify-between items-center box-border"><span class="text-grey">{{ $t('方向') }}</span><span
          :class="direction === 'buy' ? 'text-green' : 'text-red'">{{ direction === 'buy' ? $t('开多') :
            $t('开空') }}</span></div>
      <div class="w-half flex justify-between items-center pr-10 box-border mt-16"><span class="text-grey">{{ $t('当前价格')
      }}</span><span class="textColor">{{ price }}</span></div>
    </div>
    <h2 class="text-30 mt-16">{{ $t('交货时间') }}</h2>
    <ul class="flex flex-wrap w-full">
      <li v-for="(item, index) in paras" :key="item.para_id" class="h-92 flex items-center mt-17 w-half"
        @click="select(index)">
        <p class="w-95 h-full flex justify-center items-center text-22 flex-1"
          :class="active === item.para_id ? 'bg-light-blue text-white' : 'delivery_left_tab_background textColor'">{{
            item.time_num +
            item.time_unit.substr(0, 1) }}</p>
        <p class="w-125 h-full flex justify-center items-center text-22 flex-1"
          :class="active === item.para_id ? 'bg-dark-blue text-white' : 'delivery_tab_background textColor'">{{
            item.profit_ratio }}%</p>
      </li>
    </ul>
    <div class="flex justify-between items-center mt-8 text-grey tabBackground mt-20 py-15 px-10 textColor">
      <input :placeholder="$t('最少') + paras[index].buy_min" class="h-full" style="border: none; background: none;"
        type="number" v-model="amount" />
      <span>{{ queryType == 'cryptos' ? '(USDT)' : '(USD)' }}</span>
    </div>
    <div class="flex justify-between items-center mt-8 text-grey">
      <span>{{ $t('可用的') }}</span>
      <span>{{ balance }}</span>
    </div>
    <div class="flex justify-between items-center mt-8 text-grey">
      <span>{{ $t('费用') }}</span>
      <span>{{ amount * paras[index].unit_fee }}</span>
    </div>
    <div class="h-20 rounded w-full btnMain text-white flex justify-center items-center mt-24" @click="onConfirm">
      {{ $t('确认订单') }}</div>
  </div>
</template>
<script>
import { showToast } from 'vant'
export default {
  name: 'PopupConfirmOrder',
  props: {
    paras: {
      type: Array,
      default() {
        return []
      }
    },
    symbol: {
      type: String,
    },
    direction: {
      type: String
    },
    balance: {
    },
    paraIndex: {},
    price: {},
    symbolName: {
      type: String
    }
  },
  data() {
    return {
      amount: '', // 金额
      active: '',
      index: 0,
      queryType: 'cryptos'
    }
  },
  created() {
    this.active = this.paras[this.index].para_id
    if (this.$route.query.type) {
      this.queryType = this.$route.query.type
    }
  },
  methods: {
    select(index) {
      this.index = index
      this.active = this.paras[this.index].para_id
    },
    onClose() { // 关闭
      this.$emit('close')
    },
    onConfirm() { // 确认订单
      if (this.amount == '') {
        showToast(this.$t('请输入金额'))
        return
      }
      if (this.amount / 1 > this.balance / 1) {
        showToast(this.$t('Insufficient balance'))
        return
      }
      this.$emit('confirm', {
        amount: this.amount,
        para_id: this.active
      })
    }
  }
}
</script>
