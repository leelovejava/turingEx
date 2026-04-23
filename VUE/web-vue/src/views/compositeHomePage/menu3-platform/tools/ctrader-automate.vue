<!-- cTrader Automate自动化交易 -->
<template>
  <menu-layout :collapseList="menu3List">
    <template v-slot:content>
      <section class="section1">
        <h2>{{ t("m3-st-t-ca-s1-t1") }}</h2>
        <h2>{{ t("m3-st-t-ca-s1-t2") }}</h2>
        <h5>{{ t("m3-st-t-ca-s1-d1") }}</h5>
      </section>
      <!-- cTrader Automate.API -->
      <h2>{{ t("m3-st-t-ca-s2-t1") }}</h2>
      <h5>{{ t("m3-st-t-ca-s2-d1") }}</h5>
      <h5>{{ t("m3-st-t-ca-s2-d2") }}</h5>
      <div class="flex flex-wrap">
        <card
          v-for="(item, index) in list4"
          :key="index"
          :desc="t(`m3-st-t-ca-s2-${index + 1}`)"
        ></card>
      </div>

      <!-- 做好交易准备了吗？ -->
      <div class="blue-border my-8">
        <h2>{{ t("m3-st-t-ca-s3-t1") }}</h2>
        <h2 class="fs-24">{{ t("m3-st-t-ca-s3-1-t1") }}</h2>
        <div v-html="t(`m3-st-t-ca-s3-1-d1`)"></div>
        <h2 class="fs-24 mt-4">{{ t("m3-st-t-ca-s3-2-t1") }}</h2>

        <div v-html="t(`m3-st-t-ca-s3-2-d1`)"></div>
      </div>
    </template>
  </menu-layout>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { menu3List } from "@/utils/path";
import card from "@comCompositeHome/menu2/card.vue";
const list4 = Array(4);

const { t } = useI18n();
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
