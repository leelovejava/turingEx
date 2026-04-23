<template>
  <div id="cryptos">
    <div class="flex flex-col w-full h-full">
      <order-nav>
        <template #title>
          {{ ctc_user.nickName }}
        </template>
      </order-nav>
      <div class="pl-32 pr-32 flex-1 mt-30" style="overflow-y: auto">
        <van-cell-group class="mb-40 border-b-grey border-light-grey">
          <van-cell :title="$t('30天成单数')">
            <template #default>
              <span class="mr-10 text-black">{{ ctc_user.thirtyDaysOrder }}</span>
              <span class="text-grey">{{ $t('次') }}</span>
            </template>
          </van-cell>
          <van-cell :title="$t('30日成单率')" :value="`${ctc_user.thirtyDaysOrderRatio}%`" />
        </van-cell-group>
        <van-cell-group class="mb-40 border-b-grey border-light-grey">
          <van-cell :title="$t('30天平均放行时间')">
            <template #default>
              <span class="mr-10 text-black">{{ ctc_user.thirtyDaysPassAverageTime }}</span>
              <span class="text-grey">{{ $t('分钟') }}</span>
            </template>
          </van-cell>
          <van-cell class="" :title="$t('30日平均付款')">
            <template #default>
              <span class="mr-10 text-black">{{ ctc_user.thirtyDaysPayAverageTime }}</span>
              <span class="text-grey">{{ $t('分钟') }}</span>
            </template>
          </van-cell>
        </van-cell-group>
        <!--      <van-cell-group class="mb-40 border-b-grey border-light-grey">-->
        <!--        <van-cell  :title="$t('好评率')" :value="`${ctc_user.isGoodRaio}%`"/>-->
        <!--        <van-cell  :title="$t('好评数')" :value="ctc_user.appraiseGood"/>-->
        <!--        <van-cell  :title="$t('差评数')" :value="ctc_user.appraiseBad"/>-->
        <!--      </van-cell-group>-->
        <van-cell-group class="mb-40 border-b-grey border-light-grey">
          <van-cell :title="$t('账户已创建')">
            <template #default>
              <span class="mr-10 text-black">{{ ctc_user.accountCreateDays }}</span>
              <span class="text-grey">{{ $t('天') }}</span>
            </template>
          </van-cell>
          <van-cell class="" :title="$t('首次交易至今')">
            <template #default>
              <span class="mr-10 text-black">{{ ctc_user.firstExchangeDays }}</span>
              <span class="text-grey">{{ $t('天') }}</span>
            </template>
          </van-cell>
          <van-cell :title="$t('交易人数')" :value="ctc_user.exchangeUsers" />
          <van-cell class="" :title="$t('总成单数')">
            <template #default>
              <div>
                <span class="mr-10 text-black">{{ ctc_user.totalSuccessOrders }}</span>
                <span class="text-grey">{{ $t('次') }}</span>
              </div>
              <div>
                <span class="mr-10 text-grey">{{ $t('买') }}</span>
                <span class="pr-15 text-black border-r-grey" style="border-color: #868D9A">{{ ctc_user.buySuccessOrders
                }}</span>
                <span class="pl-15 mr-10 text-grey">{{ $t('卖') }}</span>
                <span class=" text-black">{{ ctc_user.sellSuccessOrders }}</span>
              </div>
            </template>
          </van-cell>
        </van-cell-group>
        <van-cell-group class="mb-40">
          <van-cell :title="$t('30天交易量折合')">
            <template #default>
              <span class="mr-10 text-black">{{ ctc_user.thirtyDaysAmount }}</span>
              <span class="text-grey">{{ $t('BTC') }}</span>
            </template>
          </van-cell>
          <van-cell class="" :title="$t('总交易量折合')">
            <template #default>
              <span class="mr-10 text-black">{{ ctc_user.totalAmount }}</span>
              <span class="text-grey">{{ $t('BTC') }}</span>
            </template>
          </van-cell>
        </van-cell-group>
      </div>
    </div>
  </div>
</template>

<script>
import { Cell, CellGroup } from 'vant';
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
export default {
  name: "c2cUserMore",
  data() {
    return {
      email: "cn*****.com",
      ctc_user: {}
    }
  },
  mounted() {
    let ctc_user = this.$route.query.ctc_user
    this.ctc_user = JSON.parse(ctc_user)
    if (this.ctc_user.appraiseGood == 0) {
      this.ctc_user.isGoodRaio = 0
    } else if (this.appraiseBad == 0) {
      this.ctc_user.isGoodRaio = 100
    } else {
      this.ctc_user.isGoodRaio = (this.ctc_user.appraiseGood * 1 / (this.ctc_user.appraiseGood * 1 + this.ctc_user.appraiseBad * 1)) * 100
    }
  },
  components: {
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    OrderNav,
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  :deep(.van-cell) {
    padding: 0 0 48px;
    font-size: 29px;
    color: #868D9A;
  }

  :deep(.van-cell__value) {
    color: $text_color;
  }

  :deep(.van-cell, .van-cell-group) {
    &::after {
      border: none;
    }
  }

}
</style>
