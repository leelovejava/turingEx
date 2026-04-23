<!-- 社交平台交易-MetaTrader信号 -->
<template>
  <menu-layout :collapseList="menu3List">
    <template v-slot:content>
      <section class="section1">
        <h2>{{ t("m3-st-mt-s1-t1") }}</h2>
        <h5>{{ t("m3-st-mt-s1-d1") }}</h5>
      </section>
      <!-- MetaTrader信号能够帮助您： -->
      <h2>{{ t("m3-st-mt-s2-t1") }}</h2>
      <div class="flex flex-wrap">
        <card
          v-for="(item, index) in list6"
          :key="index"
          :desc="t(`m3-st-mt-s2-${index + 1}`)"
        ></card>
      </div>

      <!-- 免责声明 -->
      <p class="text-gray-tip mt-36 pr-4" v-html="t('m3-st-mt-s3-d1')"></p>
    </template>
  </menu-layout>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { menu3List } from "@/utils/path";
import card from "@comCompositeHome/menu2/card.vue";
const list6 = Array(6);

const { t } = useI18n();
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
