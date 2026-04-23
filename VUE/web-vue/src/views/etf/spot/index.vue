<template>
  <spot
    :pageType="'etf'"
    :paramsType="'indices'"
    :defaultSymbol="'GlobalETF500'"
  ></spot>
</template>
<script setup>
import Spot from "@comSpot/index.vue";
</script>
