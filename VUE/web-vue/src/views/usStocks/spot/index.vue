<template>
  <spot
    :pageType="'usStocks'"
    :paramsType="'US-stocks'"
    :defaultSymbol="'AAPL'"
  ></spot>
</template>
<script setup>
import Spot from "@comSpot/index.vue";
</script>
