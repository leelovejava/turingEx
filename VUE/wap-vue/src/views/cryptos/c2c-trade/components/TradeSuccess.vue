<template>
  <div id="TradeSuccessPage">
    <div class="w-full h-full">
      <order-nav :back="false" @back="$router.push('/cryptos/wantBuy')" />
      <div class="mt-10">
        <div class="flex justify-center">
          <img class="w-32 h-32" src="~@/assets/image/c2c/Group100.png" alt="">
        </div>
        <div class="flex justify-center flex-col items-center mt-11 font-bold text-50">
          <div>
            <slot name="price"></slot>
          </div>
        </div>
      </div>
      <div class="flex flex-col items-center mt-44">
        <div>
          <van-button class="w-160 h-20 rounded-lg bg-blue" type="info"
            @click="$router.push('/cryptos/tradeSuccessDetailBuyer')">{{
              $t('完成') }}</van-button>
        </div>
        <div class="mt-11 text-32 font-normal text-blue " @click="$router.push('/cryptos/funds')">{{ $t('查看资产') }}</div>
      </div>
      <!--  <div class="absolute bottom-0 left-0 w-full text-center">-->
      <!--    <van-divider />-->
      <!--    <evaluation class="pb-80">-->
      <!--      <template #desc>-->
      <!--        <slot name="desc"></slot>-->
      <!--      </template>-->
      <!--    </evaluation>-->
      <!--  </div>-->
    </div>
  </div>
</template>

<script>
import {
  Button,
  Divider,
} from "vant";
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import Evaluation from "@/views/cryptos/c2cOrder/payment/components/Evaluation.vue";
export default {
  name: "TradeSuccess",
  components: {
    [Button.name]: Button,
    [Divider.name]: Divider,
    OrderNav,
    Evaluation,
  }
}
</script>

<style lang="scss" scoped></style>