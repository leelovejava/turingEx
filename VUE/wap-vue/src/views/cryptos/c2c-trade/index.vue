<template>
  <div class="w-full">
    <c2c-buy v-if="direction === 'buy'" />
    <c2c-sell v-else />
  </div>
</template>

<script>
import c2cBuy from "./page/c2cBuy.vue";
import c2cSell from "./page/c2cSell.vue";
import { mapGetters } from "vuex";
export default {
  name: "c2cTradeBridge",
  computed: {
    ...mapGetters('c2c', ['direction'])
  },
  components: {
    c2cBuy,
    c2cSell,
  },
  created() {
    console.log(this.direction)
  }
}
</script>

<style scoped></style>