<!-- 学习外汇-基本面分析 -->
<template>
  <menu-layout :collapseList="menu5List">
    <template v-slot:content>
      <div>
        <div class="section1">
          <h1>{{ t("m5-wh-fa-s1-t1") }}</h1>
          <h5>{{ t("m5-wh-fa-s1-d1") }}</h5>
        </div>
        <div v-for="(_, i) in list4" :key="i">
          <h2 class="fs-32 mt-8">{{ t(`m5-wh-fa-s${i + 2}-t1`) }}</h2>
          <h5 v-html="t(`m5-wh-fa-s${i + 2}-d1`)"></h5>
        </div>
        <ready class="mt-8"></ready>
      </div>
    </template>
  </menu-layout>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { menu5List } from "@/utils/path";
import ready from "@comCompositeHome/ready.vue";
const route = useRoute();
const { t } = useI18n();

const list4 = Array(4);
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
