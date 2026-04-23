<!-- 学习外汇-外汇中的掉期是什么？ -->
<template>
  <menu-layout :collapseList="menu5List">
    <template v-slot:content>
      <div>
        <div class="section1">
          <h2>{{ t("m5-wh-dq-s1-t1") }}</h2>
          <h5 v-html="t('m5-wh-dq-s1-d1')"></h5>
        </div>
        <h5
          class="mt-4"
          :class="
            [4, 6, 8, 10, 12, 14, 18, 21, 25].includes(i + 1)
              ? 'font-normal'
              : ''
          "
          v-for="(_, i) in list26"
          :key="i"
          v-html="t(`m5-wh-dq-${i + 1}-d1`)"
        ></h5>
      </div>
    </template>
  </menu-layout>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { menu5List } from "@/utils/path";
const route = useRoute();
const { t } = useI18n();
const list26 = Array(26);
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
