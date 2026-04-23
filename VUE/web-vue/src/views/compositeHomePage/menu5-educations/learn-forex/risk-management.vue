<!-- 学习外汇-风险管理 -->
<template>
  <menu-layout :collapseList="menu5List">
    <template v-slot:content>
      <div>
        <div class="section1">
          <h1>{{ t("m5-wh-rm-s1-t1") }}</h1>
          <h5 v-html="t('m5-wh-rm-s1-d1')"></h5>
        </div>
        <h2 class="fs-32 mt-8">{{ t("m5-wh-rm-s2-t1") }}</h2>
        <h5 v-html="t('m5-wh-rm-s2-d1')"></h5>
        <h2 class="fs-32 mt-8">{{ t("m5-wh-rm-s3-t1") }}</h2>
        <h5 v-html="t('m5-wh-rm-s3-d1')"></h5>
        <h5 v-html="t('m5-wh-rm-s3-d2')"></h5>
        <h5 v-html="t('m5-wh-rm-s3-d3')"></h5>
        <h2 class="fs-32 mt-8">{{ t("m5-wh-rm-s4-t1") }}</h2>
        <h5 v-html="t('m5-wh-rm-s4-d1')"></h5>
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

const gotoPage = () => {};
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
