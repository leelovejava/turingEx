<template>
  <spot
    :pageType="'coin'"
    :paramsType="'cryptos'"
    :defaultSymbol="'btcusdt'"
  ></spot>
</template>
<script setup>
import Spot from "@comSpot/index.vue";
</script>
