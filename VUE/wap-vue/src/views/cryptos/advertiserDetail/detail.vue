<template>
  <div id="advertiserDetailInfoPage">
    <div v-if="detail" class="main w-full h-full">
      <order-nav :back="false" @back="back">
        <template #title>
          {{ detail.nickName }}
        </template>
      </order-nav>
      <div class="mt-10 px-8">
        <p class="flex justify-between p-li">
          <span>{{ $t('30日成单数') }}</span>
          <span><span class="text_color">{{ detail.thirtyDaysOrder }}</span>{{ $t('次') }}</span>
        </p>
        <p class="flex justify-between p-li bor">
          <span>{{ $t('30日成单率') }}</span>
          <span class="text_color">{{ detail.thirtyDaysOrderRatio }}%</span>
        </p>
        <p class="flex justify-between p-li pd-t">
          <span>{{ $t('平均放行') }}</span>
          <span><span class="text_color">{{ detail.thirtyDaysPassAverageTime }}</span>{{ $t('分钟') }}</span>
        </p>
        <p class="flex justify-between p-li bor">
          <span>{{ $t('平均放款') }}</span>
          <span><span class="text_color">{{ detail.thirtyDaysPayAverageTime }}</span>{{ $t('分钟') }}</span>
        </p>
        <p class="flex justify-between p-li pd-t">
          <span>{{ $t('账户已创建') }}</span>
          <span><span class="text_color">{{ detail.accountCreateDays }}</span>{{ $t('天') }}</span>
        </p>
        <p class="flex justify-between p-li">
          <span>{{ $t('首次交易至今') }}</span>
          <span><span class="text_color">{{ detail.firstExchangeDays }}</span>{{ $t('天') }}</span>
        </p>
        <p class="flex justify-between p-li">
          <span>{{ $t('交易人数') }}</span>
          <span class="text_color">{{ detail.exchangeUsers }}</span>
        </p>
        <p class="flex justify-between p-li">
          <span>{{ $t('总成单数') }}</span>
          <span><span class="text_color">{{ detail.totalSuccessOrders }}</span>{{ $t('次') }}</span>
        </p>
        <p class="flex justify-between p-li">
          <span>{{ $t('数据') }}</span>
          <span>
            {{ $t('购买') }}
            <span class="text_color">{{ detail.buySuccessOrders }}</span>｜
            {{ $t('出售') }}
            <span class="text_color">{{ detail.sellSuccessOrders }}</span>
          </span>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import {
  formatTime
} from "@/utils/utis";
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
export default {
  props: ['detail'],
  data() {
    return {
      info: {
        singularNumber: 1045,
        singularBai: '99.24%',
        release: 1.87,
        payment: 0.93,
        establish: 101,
        first: 231,
        num: 123,
        total: 12345,
        buy: 824,
        sell: 1093,
      },
    }
  },
  methods: {
    linkToBack() {
      this.$router.go(-1)
    },
    back() {
      this.$emit('back')
    }
  },
  components: {
    OrderNav,
  }
}
</script>

<style lang="scss" scoped>
#advertiserDetailInfoPage {
  font-size: 30px;

  .main {
    //padding: 0 30px;
    color: #868d9a;

    .p-li {
      padding-bottom: 44px;
    }

    .bor {
      border-bottom: 1px solid $divi_line;
    }

    .pd-t {
      padding-top: 40px;
    }
  }

  .nav {
    display: flex;
    align-items: center;
    font-size: 33px;
    font-weight: bold;
    color: #21262f;
    padding: 54px 0 80px;

    img {
      width: 32px;
      height: 32px;
      margin-right: 264px;
    }
  }
}
</style>
