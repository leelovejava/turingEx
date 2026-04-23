<template>
  <div id="profitLoss">
    <assets-head :title="$t('订单详情')"></assets-head>
    <div v-for="(item, index) in list" :key="index" class="box-border px-33 ">
      <div class="flex flex-col justify-center items-center">
        <div class="text-32 font-normal my-42">{{ $t('盈亏金额') }}（USDT）</div>
        <div class="text-66" :class="item.Profit_loss_amount / 1 > 0 ? 'text-green' : 'text-red'">
          {{ item.Profit_loss_amount / 1 > 0 ? '+' + item.Profit_loss_amount : item.Profit_loss_amount }}</div>
      </div>
      <div class="mt-90">
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('订单号') }}</div>
          <div class="text-32 font-normal cl33">{{ item.order_number }}</div>
        </div>
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('开仓时间') }}</div>
          <div class="text-32 font-normal cl33">{{ item.Opening_time }}</div>
        </div>
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('平仓时间') }}</div>
          <div class="text-32 font-normal cl33">{{ item.Closing_time }}</div>
        </div>
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('开仓金额') }}</div>
          <div class="text-32 font-normal cl33">{{ item.Amount_open_position }}</div>
        </div>
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('可平金额') }}</div>
          <div class="text-32 font-normal cl33">{{ item.Balance_amount }}</div>
        </div>
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('保证金') }}</div>
          <div class="text-32 font-normal cl33">{{ item.Margin }}</div>
        </div>
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('建仓成本') }}</div>
          <div class="text-32 font-normal cl33">{{ item.Cost_opening_warehouse }}</div>
        </div>
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('平仓价格') }}</div>
          <div class="text-32 font-normal cl33">{{ item.Closing_price }}</div>
        </div>
        <div class="flex justify-between mb-70">
          <div class="text-32 font-normal cl">{{ $t('手续费') }}</div>
          <div class="text-32 font-normal cl33">{{ item.handling_fee }}</div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import assetsHead from '@/components/Transform/assets-head/index.vue';
export default {
  name: "profitLoss",
  components: {
    assetsHead
  },
  data() {
    return {
      list: [
        {
          Profit_loss_amount: '20',
          order_number: '5453132198456132',
          Opening_time: '2022.03.02 12:00:00',
          Closing_time: '2022.03.02 12:00:00',
          Amount_open_position: '1000.00',
          Balance_amount: '1000.00',
          Margin: '1000.00',
          Cost_opening_warehouse: '447.32',
          Closing_price: '321.00',
          handling_fee: '30.00 USDT',
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
</style>