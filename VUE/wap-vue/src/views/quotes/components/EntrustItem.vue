<template>
  <div class="entrust-item">
    <div class="flex justify-between pb-20">
      <div class="flex flex-col">
        <div class="flex items-center">
          <div style="min-width: 70px;" class="text-32" :class="entrust.offset == 'open' ? 'text-green' : 'text-red'">
            {{ handleWord(entrust.order_price_type, entrust.offset) }}
          </div>
          <div class="ml-40 text-32 font-semibold textColor">{{ entrust.name || '--' }}</div>
        </div>
      </div>
      <div class="text-grey text-26 flex flex-col justify-between">
        {{ entrust.create_time ? entrust.create_time : '--' }}
      </div>
    </div>
    <div class="flex justify-between pb-34">
      <div class="flex items-center flex-between">
        <div class="w-100">
          <van-circle v-if="state == 'created'" v-model:current-rate="finishRate"
            :rate="(1 - entrust.volume / entrust.volume_open * 100)" :speed="100" :text="finishText" layer-color='#EAEBEF'
            :color="entrust.offset == 'open' ? 'red' : '#1194F7'" :stroke-width="60" :size="'50px'">
            <div class="textColor circle-box  flex items-center justify-center">100%</div>
          </van-circle>
          <van-circle v-else v-model:current-rate="currentRate" :rate="(1 - entrust.volume / entrust.volume_open * 100)"
            :speed="100" :text="text" :color="entrust.offset == 'open' ? 'red' : '#1194F7'" layer-color='#EAEBEF'
            :stroke-width="60" :size="'60px'">
            <div class="textColor circle-box  flex items-center justify-center">0%</div>
          </van-circle>
        </div>
        <div class="ml-40">
          <div class="flex items-center">
            <div class="text-grey">
              <div>{{ $t('数量') }}</div>
              <!-- <di(USDT)v></div> -->
            </div>
            <div class="ml-10 text-26 textColor volume-title">0/{{ entrust.volume }}
            </div>
          </div>
          <div class="flex mt-20 items-center">
            <div class="text-grey">{{ $t('价格') }}</div>
            <div class="ml-10 text-26 textColor">{{ entrust.price || '--' }}</div>
          </div>
        </div>
      </div>
      <div class="btn-wrap mt-64">
        <button class="detailBtn order-btn text-blue  h-54 lh-54 greyBg " @click.stop="goDetail(entrust.order_no)">
          {{ $t('详情') }}</button>
        <button v-if="state == 'submitted'" class="ml-19 order-btn border-none h-54 lh-54 cancel-btn"
          @click.stop="cancelSingle(entrust.order_no)">
          {{ $t('撤单') }}</button>
        <button v-if="state == 'created'" class="ml-19 order-btn border-none h-54 lh-54 cancel-btn text-green">{{
          $t('已完成') }}</button>
        <button v-if="state == 'canceled'" class="ml-19 order-btn border-none h-54 lh-54 cancel-btn">{{
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
      item: {},
      dayjs
    }
  },
  watch: {
    entrust(val) {
      // console.log(val)
    }
  },
  mounted() {

  },
  methods: {
    handleWord(type, offset) {
      let str1 = type == 'limit' ? this.$t('限价') : this.$t('市价');
      let str2 = offset == 'open' ? this.$t('买入') : this.$t('卖出');
      return str1 + '/' + str2
    },
    goDetail(order_no) { // 详情
      this.$router.push({ path: "/cryptos/symbolOrderDetail", query: { order_no } });
    },
    cancelSingle(order_no) { // 撤单
      this.$emit('cancelOrder', order_no)
    }
  }
}
</script>
<style lang="scss"  scoped>
.ml-40 {
  margin-left: 20px !important;
}

.ml-19 {
  margin-left: 9px;
}

.pt-42 {
  padding-top: 21px;
}

.pb-20 {
  padding-bottom: 10px;
}

.mt-20 {
  margin-top: 10px;
}

.mt-64 {
  margin-top: 32px;
}

.h-54 {
  height: 27px;
}

.lh-54 {
  line-height: 27px;
}

.pb-34 {
  padding-bottom: 17px;
}

.text-green {
  color: $green;
}

.text-red {
  color: $red;
}

.entrust-item {
  font-size: 13px;
  border-bottom: 1px solid $border_color;

  :deep(.van-circle__text) {
    color: $text_color6;
  }

  s .greyBg {
    background: transparent;
  }

  .volume-title {
    width: 80px;
  }
}

.btn-wrap {
  display: flex;

  button {
    padding: 0 13px;
    border-radius: 3px;
    font-size: 13px;
    background: $light-grey;
    color: #818181;

  }

  .detailBtn {
    background: $US_tabActice_background !important;
    color: $color_main;
  }

  .cancel-btn {
    background: $light-grey;
    color: #818181;
  }

}

.w-100 {
  min-width: 70px;
}

.circle-box {
  height: 100%;
}

.ml-62 {
  margin-left: 10px;
}
</style>
