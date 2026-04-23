<!-- 社交平台交易-MetaTrader信号 -->
<template>
  <menu-layout :collapseList="menu3List">
    <template v-slot:content>
      <section class="section1">
        <h2>{{ t("m3-st-t-ap-s1-t1") }}</h2>
        <h5>{{ t("m3-st-t-ap-s1-d1") }}</h5>
      </section>
      <!-- 使用API进行交易的好处 -->
      <h2 class="mt-4">{{ t("m3-st-t-ap-s2-t1") }}</h2>
      <div class="flex flex-wrap">
        <card
          v-for="(item, index) in list3"
          :key="index"
          :desc="t(`m3-st-t-ap-s2-${index + 1}`)"
        ></card>
      </div>

      <h5 class="mt-4" v-html="t('m3-st-t-ap-s2-d1')"></h5>
      <h5 v-html="t('m3-st-t-ap-s2-d2')"></h5>
    </template>
  </menu-layout>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { menu3List } from "@/utils/path";
import card from "@comCompositeHome/menu2/card.vue";
const list3 = Array(3);

const { t } = useI18n();
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
