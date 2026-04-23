<template>
  <div class="border-b-color entrust-item text-28">
    <div class="flex justify-between pt-10 pb-25">
      <div class="flex flex-col">
        <div class="flex items-center">
          <div class="text-32" :class="entrust.offset == 'open' ? ' text-green' : 'text-red'">
            {{ handleWord(entrust.order_price_type, entrust.offset) }}
          </div>
          <div class="ml-5 text-32 textColor">{{ entrust.name || '--' }}</div>
        </div>
      </div>
      <div class="text-grey text-26 flex flex-col justify-between">
        {{ entrust.create_time ? entrust.create_time : '--' }}
      </div>
    </div>
    <div class="flex justify-between pb-8">
      <div class="flex items-center flex-between">
        <div>
          <van-circle v-if="state == 'created'" v-model:current-rate="finishRate"
            :rate="(1 - entrust.volume / entrust.volume_open * 100)" :speed="100" :text="finishText"
            :layer-color="'#EAEBEF'" :stroke-width="60" :size="'60px'" />
          <van-circle v-else v-model:current-rate="currentRate" :rate="(1 - entrust.volume / entrust.volume_open * 100)"
            :speed="100" :text="text" :layer-color="'#EAEBEF'" :stroke-width="60" :size="'60px'" />
        </div>
        <div class="ml-5">
          <div class="flex items-center">
            <div class="text-grey text-28">
              <div>{{ $t('数量') }}</div>
            </div>
            <div class="ml-6 quantity text-26 textColor">
              0/{{ entrust.volume ? entrust.volume : '--' }}
            </div>
          </div>
          <div class="flex mt-5 items-center">
            <div class="text-grey text-28">{{ $t('价格') }}</div>
            <div class="ml-6 text-26 textColor">{{ entrust.price || '--' }}</div>
          </div>
        </div>
      </div>
      <div class="btn-wrap mt-16">
        <button class="detailBtn order-btn text-blue h-14 greyBg " @click.stop="goDetail(entrust.order_no)">
          {{ $t('详情') }}</button>
        <button v-if="state == 'submitted'" class="ml-5 order-btn border-none h-14 cancel-btn greyBg textColor"
          @click.stop="cancelSingle(entrust.order_no)">
          {{ $t('撤单') }}</button>
        <button v-if="state == 'created'" class="ml-5 order-btn border-none h-54 cancel-btn text-green greyBg">{{
          $t('已完成') }}</button>
        <button v-if="state == 'canceled'" class="ml-5 order-btn border-none h-54 cancel-btn greyBg textColor">{{
          $t('canceled') }}</button>
      </div>
    </div>
  </div>
</template>

<script>
import { Circle } from 'vant'
import dayjs from 'dayjs'
export default {
  name: 'EntrustItem', // 订单委托项
  components: {
    [Circle.name]: Circle
  },
  props: {
    entrust: {
      type: Object,
      default() {
        return {}
      }
    },
    state: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      text: '0',
      currentRate: 0,
      finishRate: 100,
      finishText: '100%',
      dayjs
    }
  },
  methods: {
    handleWord(type, offset) {
      let str1 = type == 'limit' ? this.$t('限价') : this.$t('市价');
      let str2 = offset == 'open' ? this.$t('买入') : this.$t('卖出');
      return str1 + '/' + str2
    },
    goDetail(order_no) { // 详情
      this.$router.push({ path: '/cryptos/symbolOrderDetail', query: { 'order_no': order_no } })
    },
    cancelSingle(order_no) { // 撤单
      this.$emit('cancelOrder', order_no)
    }
  }
}
</script>
<style lang="scss"  scoped>
#cryptos {
  .entrust-item {
    :deep(.van-circle__text) {
      color: $text_color;
    }

    :deep(.van-circle__hover) {
      stroke: $color_main
    }
  }

  .btn-wrap {
    button {
      padding: 0 26px;
      border-radius: 6px;
      font-size: 26px;
    }

    .detailBtn {
      background: $US_tabActice_background;
      color: $color_main;
    }
  }

  .quantity {
    max-width: 270px;
  }
}
</style>
