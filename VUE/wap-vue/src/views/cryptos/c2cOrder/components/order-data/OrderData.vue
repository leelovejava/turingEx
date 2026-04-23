<template>
  <div id="OrderDataPage">
    <div class="order-data">
      <div class="flex items-center text-32 mb-10 c2cColor pl-4">
        <span class="title" :style="{ 'color': detail.direction === 'buy' ? '#2EBD85' : '#E35461' }">
          {{ detail.direction === 'buy' ? $t('购买') : $t('卖出') }}
        </span>
        <span class="mx-4">{{ detail.symbol && detail.symbol.toUpperCase() }}</span>
        <img class="w-10 h-10" src="~@/assets/image/c2c/U1.png" alt="">
      </div>
      <van-cell-group>
        <van-cell :title="$t('总额')">
          <template #default>
            <h2 class="font-bold text-36">{{ currencySymbol }} {{ detail.amount }}</h2>
          </template>
        </van-cell>
        <van-cell :title="$t('单价')">
          <template #default>
            <span>{{ currencySymbol }} </span>
            <span>{{ detail.symbolValue }}</span>
          </template>
        </van-cell>
        <van-cell :title="$t('数量')"
          :value="`${detail.symbol == 'usdt' ? Math.floor(detail.coinAmount * 100) / 100 : Math.floor(detail.coinAmount * 1000000) / 1000000} ${detail.symbol && detail.symbol.toUpperCase()}`" />
        <slot name="default"></slot>
        <van-cell class="order-number flex items-center">
          <template #title>
            <div class="w-24">{{ $t('订单号') }}</div>
          </template>
          <template #value>
            <div class="flex justify-end items-center">
              <span class="mr-3">{{ detail.orderNo }}</span>
              <img class="relative  w-8 h-8" src="~@/assets/image/c2c/Group1168.png" alt="" @click="copy(detail.orderNo)">
            </div>
          </template>
        </van-cell>
        <van-cell v-if="detail.createTime" :title="$t('创建时间')" :value="detail.createTime" />
        <van-cell v-if="detail.sellerName">
          <template #title>
            <span>{{ clientType }}</span>
          </template>
          <template #default>
            <span class="mr-5">{{ sellerName }}</span>
            <van-icon class="font-bold text-grey" name="arrow" />
          </template>
        </van-cell>
      </van-cell-group>
    </div>
  </div>
</template>

<script>
import {
  mapState,
  mapGetters
} from "vuex";
import { Cell, CellGroup, Icon, showToast } from "vant"

export default {
  name: "OrderDate",
  // props: ['count', 'totalPrice', 'orderNumber', 'sellerName', 'createOrderTime'],
  props: {
    clientType: {
      default: '卖家昵称'
    },
    detail: {
      type: Object,
      default() {
        return {}
      }
    },
    // // count: {},
    // totalPrice: {},
    // // orderNumber: {},
    // sellerName: {},
    // createOrderTime: {},
    // unitPrice: {
    //   // required: true,
    // }
  },
  filters: {
    format(time) {
      const _time = new Date(time)
      return [_time.getFullYear(), _time.getMonth() + 1, _time.getDate()].join('/') + ' ' + [(_time.getHours() + '').padStart(2, '0'), (_time.getMinutes() + '').padStart(2, '0'), (_time.getSeconds() + '').padStart(2, '0')].join(":")
    }
  },
  methods: {
    copy(text) {
      // 复制
      if (!text) {
        return
      }
      navigator.clipboard.writeText(text).then(() => {
        showToast(this.$t('复制成功'));
      })
    },
  },
  computed: {
    ...mapState('home', ['currency']),
    ...mapGetters('c2c', ['currencySymbol'])
  },
  components: {
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    [Icon.name]: Icon,
  }
}
</script>

<style lang="scss" scoped>
#OrderDataPage {
  font-size: 30px;

  :deep(.van-cell) {
    margin-bottom: 46px;
  }

  :deep(.order-number) {
    .van-cell__title {
      flex: inherit;
    }
  }

  .order-data {

    :deep(.van-cell-group, .van-cell) {
      margin-bottom: 46px;
      background: $main_background;
      color: $text_color;
    }

    :deep(.van-cell__value) {
      color: $text_color;
    }
  }
}
</style>
