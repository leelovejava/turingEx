<template>
  <menu-layout :needMenu="false">
    <template v-slot:content>
      <div class="section1">
        <h1>{{ t("m5-tg-a5-s1-t1") }}</h1>
        <h5>{{ t("m5-tg-a5-s1-d1") }}</h5>
      </div>
      <div class="content mt-8" v-html="t('m5-tg-a5-s2-c')"></div>
      <ready></ready>
    </template>
  </menu-layout>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import ready from "@comCompositeHome/ready.vue";

const { t } = useI18n();
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}

.content {
  :deep(h5) {
    margin-bottom: 18px;
  }
}
</style>
