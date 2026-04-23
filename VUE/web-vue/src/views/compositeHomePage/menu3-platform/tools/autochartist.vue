<!-- 工具-自动图表分析系统（Autochartist）-->
<template>
  <menu-layout :collapseList="menu3List">
    <template v-slot:content>
      <section class="section1">
        <h2>{{ t("m3-st-t-ac-s1-t1") }}</h2>
        <h5>{{ t("m3-st-t-ac-s1-d1") }}</h5>
      </section>
      <!-- 市场扫描仪 -->
      <h2 class="mt-4">{{ t("m3-st-t-ac-s2-t1") }}</h2>
      <h5 class="mb-4">{{ t("m3-st-t-ac-s2-d1") }}</h5>
      <!-- 概率过滤器 -->
      <h2>{{ t("m3-st-t-ac-s3-t1") }}</h2>
      <h5 class="mb-4">{{ t("m3-st-t-ac-s3-d1") }}</h5>
      <!-- 风险管理工具-->
      <h2>{{ t("m3-st-t-ac-s4-t1") }}</h2>
      <h5 class="mb-4">{{ t("m3-st-t-ac-s4-d1") }}</h5>
      <!-- 技术指标-->
      <h2>{{ t("m3-st-t-ac-s5-t1") }}</h2>
      <h5 class="mb-4">{{ t("m3-st-t-ac-s5-d1") }}</h5>
      <!-- 波动性分析-->
      <h2>{{ t("m3-st-t-ac-s6-t1") }}</h2>
      <h5 class="mb-4">{{ t("m3-st-t-ac-s6-d1") }}</h5>
      <!-- 每日市场报告-->
      <h2>{{ t("m3-st-t-ac-s7-t1") }}</h2>
      <h5 class="mb-4">{{ t("m3-st-t-ac-s7-d1") }}</h5>
      <!-- 我们MetaTrader交易平台的一部分 -->
      <div class="flex">
        <div class="mr-4">
          <h2>{{ t("m3-st-t-ac-s8-t1") }}</h2>
          <h5 class="mb-4">{{ t("m3-st-t-ac-s8-d1") }}</h5>
        </div>
        <img
          src="/src/assets/images/compositeHome/menu3/tools/au1.png"
          width="413"
          height="370"
        />
      </div>
      <!-- 做好交易准备了吗？ -->
      <div class="blue-border my-8">
        <h2 class="fs-24">{{ t("m3-st-t-ac-s9-1-t1") }}</h2>
        <h5 class="mb-4">{{ t("m3-st-t-ac-s9-1-d1") }}</h5>
        <h2 class="fs-24">{{ t("m3-st-t-ac-s9-2-t1") }}</h2>
        <h5>{{ t("m3-st-t-ac-s9-2-d1") }}</h5>
      </div>
    </template>
  </menu-layout>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { menu3List } from "@/utils/path";

const { t } = useI18n();
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}

.border-line {
  border-bottom: 1px solid rgba(0, 0, 0, 0.15);
}
</style>
