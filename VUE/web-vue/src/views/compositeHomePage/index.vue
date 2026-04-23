<template>
  <div>
    <forex-header v-if="route.meta.forexHeader"></forex-header>
    <router-view />
    <forex-footer
      v-if="!route.meta.noFooter"
      :theme="route.meta.footer_theme"
    ></forex-footer>
  </div>
</template>

<script setup>
import ForexFooter from "@comCompositeHome/footer.vue";
import ForexHeader from "@comCompositeHome/header.vue";
import { useRoute } from "vue-router";

const route = useRoute();
</script>
