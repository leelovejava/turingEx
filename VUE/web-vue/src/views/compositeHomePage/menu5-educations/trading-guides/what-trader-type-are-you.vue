<!-- 您属于哪种交易类型？ -->
<template>
  <menu-layout :needMenu="false">
    <template v-slot:content>
      <div class="section1">
        <h1>{{ t("m5-tg-a6-s1-t1") }}</h1>
        <h5>{{ t("m5-tg-a6-s1-d1") }}</h5>
      </div>
      <div class="content mt-8" v-html="t('m5-tg-a6-s2-c')"></div>
      <ready></ready>
    </template>
  </menu-layout>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import ready from "@comCompositeHome/readyWithBg.vue";

const { t } = useI18n();
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}

.content {
  :deep(blockquote) {
    // font-family: Titillium Web;
    font-size: 0.875rem;
    font-weight: 300;
    letter-spacing: 0.4px;
    line-height: 1.375rem;
    margin-bottom: 3rem;
    padding: 0 2rem;
    position: relative;
  }
}
</style>
