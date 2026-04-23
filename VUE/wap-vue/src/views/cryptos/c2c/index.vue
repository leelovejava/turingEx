<template>
  <div class="w-full h-full">
    <div class="w-full h-full relative">
      <router-view />
    </div>
    <order-footer />
  </div>
</template>

<script>
import OrderFooter from "@/views/cryptos/c2cOrder/components/footer/OrderFooter.vue";

export default {
  name: "c2c",
  data() {
    return {}
  },
  components: {
    OrderFooter,
  }
}
</script>

<style lang="scss" scoped></style>