<template>
  <div id="cryptos">
    <div id="financialOrder" class="fundBuy">
      <assets-head :title="$i18n.locale === 'CN' || $i18n.locale === 'zh-CN' ? data.name_cn : data.name_en"></assets-head>
      <div class="box-border  ">
        <div class="w-full h-1 bgDark"></div>
        <div class="mt-12 text-36 font-semibold px-8 textColor">{{ $t('购买金额') }}</div>
        <div class="flex justify-center mt-4">
          <div class="flex flex-1 px-8">
            <input :placeholder="$t('输入金额')" type="number" v-model="form.amount"
              class="h-20 pl-8 w-full border-none inputBackground textColor text-28" @input="debounceFn">
            <div class="h-20 w-48 flex inputBackground textColor text-28 items-center pr-1" @click="form.amount = bal">
              USDT <span class="colorMain pl-2" @click="handleAll">{{ $t('全部') }}</span></div>
          </div>
        </div>

        <div class="px-8">
          <div class="flex justify-between flex-row items-center h-18">
            <div class="text-32 font-normal text-grey">{{ $t('可用余额') }}</div>
            <div class="text-32 font-bold textColor">{{ bal }} USDT</div>
          </div>
          <div class="flex justify-between items-center h-18">
            <div class="text-32 font-normal text-grey">{{ $t('数量限制') }}</div>
          </div>
          <div class="flex justify-between items-center h-18">
            <div class="text-32 font-normal text-grey">{{ $t('最少可投') }}</div>
            <div class="text-32 font-bold textColor">{{ data.investment_min }} USDT</div>
          </div>
          <div class="flex justify-between items-center h-18">
            <div class="text-32 font-normal text-grey">{{ $t('最大可投') }}</div>
            <div class="text-32 font-bold textColor">{{ data.investment_max }} USDT</div>
          </div>
        </div>
        <div class="h-3 px-8 mt-20 mb-5 textColor text-28">{{ $t('概览') }}</div>
        <div class="px-8">
          <div class="flex justify-between items-center h-20">
            <div class="text-32 font-normal text-grey">{{ $t('购买日') }}</div>
            <div class="text-32 font-bold textColor">{{ dateBuy }}</div>
          </div>
          <div class="flex justify-between items-center h-20">
            <div class="text-32 font-normal text-grey">{{ $t('起息日') }}</div>
            <div class="text-32 font-bold textColor">{{ earn_time ? earn_time : '--' }}</div>
          </div>
          <div class="flex justify-between items-center h-20">
            <div class="text-32 font-normal text-grey">{{ $t('派息时间') }}</div>
            <div class="text-32 font-bold textColor">{{ $t('每天') }}</div>
          </div>
          <div class="flex justify-between items-center h-20">
            <div class="text-32 font-normal text-grey">{{ $t('计息结束日') }}</div>
            <div class="text-32 font-bold textColor">{{ dayjs().add(data.cycle, 'day').format('YYYY-MM-DD') }}</div>
          </div>
          <div class="flex justify-between items-center h-20">
            <div class="text-32 font-normal text-grey">{{ $t('提前赎回') }}</div>
            <div class="text-32 font-bold textColor">{{ data.default_ratio }}%</div>
          </div>
          <div class="flex justify-between items-center h-20">
            <div class="text-32 font-normal text-grey">{{ $t('预计收益') }}</div>
            <div class="text-32 font-bold textColor">{{ ((data.daily_rate.split(' ~ ')[0]) / 1 * form.amount /
              100).toFixed(2) }} ~
              {{ ((data.daily_rate.split(' ~ ')[1]) / 1 * form.amount / 100).toFixed(2) }} USDT</div>
          </div>
          <div class="flex justify-between items-center h-20">
            <div class="text-32 font-normal text-grey">{{ $t('今日（日收益）') }}</div>
            <div class="text-32 font-bold textColor">{{ data.daily_rate_max }}%</div>
          </div>
        </div>
      </div>
      <div class="text-white btnMain h-24 w-4/5 rounded-md text-center fixed wz m-auto flex justify-center items-center"
        @click="handleBuy">{{ $t('购买') }}
      </div>
    </div>
  </div>
</template>

<script>
import assetsHead from '@/components/Transform/assets-head/index.vue';
import { _getBalance } from "@/service/user.api.js";
import { fundMakeOrder } from '@/service/financialManagement.api.js'
import dayjs from 'dayjs'
import { showToast } from 'vant';
import {debounce} from "@/utils/utis.js";

export default {
  name: "funBuy",
  components: {
    assetsHead
  },
  data() {
    return {
      dayjs,
      earn_time: '',
      dateBuy: dayjs().format('YYYY-MM-DD'),
      bal: 0,
      data: {},
      form: {
        financeId: '',
        amount: '',
      },
    }
  },
  methods: {
    debounceFn: debounce(function () {
      this.changeMount()
    }, 500),
    changeMount() {
      if (this.form.amount == '') {
        return
      }
      if (this.form.amount < this.data.investment_min) {
        showToast(this.$t('最小支付金额不能低于')+this.data.investment_min )
        return
      }
      if (this.form.amount > this.data.investment_max) {
        showToast(this.$t('最大支付金额不能超过')+this.data.investment_max )
        return
      }
      fundMakeOrder(this.form).then(res => {
        this.earn_time = res.earn_time
      })
    },
    handleBuy() {
      if (!this.form.amount) {
        showToast(this.$t('请输入金额'))
        return
      }
      fundMakeOrder(this.form).then(res => {
        //console.log('认购', res)
        this.$router.push(
          { path: '/cryptos/financial-confirm', query: { ...res } })
      })
    },
    handleAll() {
      this.input.value = this.form.amount
    }

  },
  created() {
    _getBalance().then(res => {
      //console.log('可用余额', res)
      this.bal = res.money
    })
    this.data = this.$route.query
    this.form.financeId = this.data.id
    console.log(this.data)
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  font-size: 30px;

  .fundBuy {
    width: 100%;
    box-sizing: border-box;
  }

  .wz {
    bottom: 44px;
    left: 32px;
    right: 32px;
  }
}
</style>
