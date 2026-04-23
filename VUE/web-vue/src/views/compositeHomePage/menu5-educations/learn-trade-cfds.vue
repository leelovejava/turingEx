<!-- 学习交易差价合约 -->
<template>
  <menu-layout :collapseList="menu5List">
    <template v-slot:content>
      <div>
        <div class="section1">
          <h2>{{ t("m5-ltc-s1-t1") }}</h2>
          <h5>{{ t("m5-ltc-s1-d1") }}</h5>
        </div>
        <div v-for="(_, i) in list6" :key="i">
          <h2 class="fs-32 mt-4" v-html="t(`m5-ltc-s2-${i + 1}-t1`)"></h2>
          <h5 v-html="t(`m5-ltc-s2-${i + 1}-d1`)"></h5>
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

const { t } = useI18n();

const list6 = Array(6);
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
