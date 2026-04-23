<!-- 学习外汇-基本面分析 -->
<template>
  <menu-layout :needMenu="false">
    <template v-slot:content>
      <div class="section1">
        <h1>{{ t("m5-tg-a1-s1-t1") }}</h1>
        <h5>{{ t("m5-tg-a1-s1-d1") }}</h5>
      </div>
      <h5 class="my-8" v-html="t('m5-tg-a1-s1-d2')"></h5>
      <div v-for="(item, i) in list12" :key="i">
        <h2 class="fs-24 my-4">{{ t(`m5-tg-a1-s2-${i + 1}-t1`) }}</h2>
        <h5 v-html="t(`m5-tg-a1-s2-${i + 1}-d1`)"></h5>
      </div>
      <ready class="mt-8"></ready>
    </template>
  </menu-layout>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";

import ready from "@comCompositeHome/ready.vue";
const route = useRoute();
const { t } = useI18n();

const list12 = Array(12);
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
