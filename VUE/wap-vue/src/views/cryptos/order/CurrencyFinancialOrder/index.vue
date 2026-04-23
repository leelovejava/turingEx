<template>
  <div id="hosting">
    <assets-head :title="$t('订单详情')"></assets-head>
    <div v-for="(item, index) in list" :key="index" class="box-border px-33 ">
      <div class="flex flex-col justify-center items-center">
        <div class="text-32 font-normal my-42">{{ $t('托管金额') }}（USDT）</div>
        <div class="text-66">{{ item.Escrow_amount }}</div>
      </div>
      <div class="mt-90">
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('订单号') }}</div>
          <div class="text-32 font-normal cl33">{{ item.order_number }}</div>
        </div>
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('订单时间') }}</div>
          <div class="text-32 font-normal cl33">{{ item.order_time }}</div>
        </div>
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('起息时间') }}</div>
          <div class="text-32 font-normal cl33">{{ item.start_time }}</div>
        </div>
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('到期时间') }}</div>
          <div class="text-32 font-normal cl33">{{ item.Expire_date }}</div>
        </div>
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('赎回时间') }}</div>
          <div class="text-32 font-normal cl33">{{ item.redemption_time }}</div>
        </div>
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('托管时间') }}</div>
          <div class="text-32 font-normal cl33">{{ item.hosting_time }}</div>
        </div>
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('获利时间') }}</div>
          <div class="text-32 font-normal cl33">{{ item.Profitable_time }}</div>
        </div>
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('日收益率') }}</div>
          <div class="text-32 font-normal " :class="item.Daily_efficiency > 0 ? 'text-green' : 'text-red'">
            {{ item.Daily_efficiency }}</div>
        </div>
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('预期收益') }}</div>
          <div class="text-32 font-normal " :class="item.expected_profit / 1 > 0 ? 'text-green' : 'text-red'">
            {{ item.expected_profit }}</div>
        </div>
      </div>
    </div>
    <div class="text-white btnMain h-97 lh-97 w-762 rounded-lg text-center fixed wz ">{{ $t('我要赎回') }}</div>

  </div>
</template>

<script>
import assetsHead from '@/components/Transform/assets-head/index.vue';
export default {
  name: "CurrencyFinancialOrder",
  components: {
    assetsHead
  },
  data() {
    return {
      list: [
        {
          Escrow_amount: '1000',
          order_number: '5453132198456132',
          order_time: '2022.03.02 12:00:00',
          start_time: '2022.03.02 ',
          Expire_date: '2022.03.02 ',
          redemption_time: '2022.03.02 12:00:00',
          hosting_time: '2022.03.02 12:00:00',
          Profitable_time: '5' + this.$t('天'),
          Daily_efficiency: '3%',
          expected_profit: '100.0',
        }
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
.cl {
  color: $text_color5;
}

.cl33 {
  color: $text_color4;
}

.wz {
  bottom: 44px;
  left: 32px;
  right: 32px;
}
</style>