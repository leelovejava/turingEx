<template>
  <div id="cryptos">
    <div class="w-full h-full">
      <order-nav :back="false" @back="hide">
        <template #title>
          {{ $t('申述提交') }}
        </template>
      </order-nav>
      <div class="mt-70">
        <div class="text-center">
          <img class="w-122 h-122" src="~@/assets/image/c2c/Group100.png" alt="">
          <div class="mt-35 font-bold text-50 textColor">{{ $t('申诉成功') }}</div>
          <div class="mt-24 text-28 text-grey">
            <p class="mb-15 font-semibold">100 USDT</p>
            <p>{{ $t('已返还至您的账户') }}</p>
          </div>
        </div>
      </div>
      <div class="px-33 mt-92">
        <order-data :count="count" :total-price="totalPrice" :order-number="orderNumber" />
      </div>
    </div>
  </div>
</template>

<script>
import {
  mapState
} from "vuex";

import { Button } from "vant";
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import OrderData from "@/views/cryptos/c2cOrder/components/order-data/OrderData.vue";

export default {
  name: "AppealWaiting",
  methods: {
    hide() {
      console.log(213)
      this.$emit("back");
    }
  },
  computed: {
    ...mapState('c2cBuy', ["count", "totalPrice", "orderNumber"])
  },
  components: {
    [Button.name]: Button,
    OrderNav,
    OrderData,
  }
}
</script>

<style scoped></style>