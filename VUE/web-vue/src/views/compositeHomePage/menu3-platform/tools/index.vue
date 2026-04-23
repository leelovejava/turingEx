<!-- 工具 -->
<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h2>{{ t("m3-st-t-h-s0-t1") }}</h2>
      <h5>{{ t("m3-st-t-h-s0-d1") }}</h5>
    </pc-section>
    <!-- menu-layout -->
    <menu-layout :collapseList="menu3List">
      <template v-slot:content>
        <!-- Smart Trader工具 -->
        <section class="flex mb-8">
          <img
            src="@/assets/images/compositeHome/menu3/tools/h1.png"
            width="413"
            height="342"
          />
          <div class="ml-12">
            <h2>{{ t("m3-st-t-h-s1-t1", { TITLE: $title }) }}</h2>
            <h5 class="border-b">
              {{ t("m3-st-t-h-s1-d1") }}
            </h5>
            <div class="border-b">{{ t("learn-more-info") }}</div>
          </div>
        </section>
        <!-- cTrader Automate -->
        <section class="flex mb-8">
          <div class="mr-12">
            <h2>{{ t("m3-st-t-h-s2-t1") }}</h2>
            <h5 class="border-b">
              {{ t("m3-st-t-h-s2-d1") }}
            </h5>
            <div class="border-b">{{ t("learn-more-info") }}</div>
          </div>
          <img
            src="@/assets/images/compositeHome/menu3/tools/h2.png"
            width="413"
            height="294"
          />
        </section>
        <!-- 自动图表分析系统（Autochartist） -->
        <section class="flex mb-8">
          <img
            src="@/assets/images/compositeHome/menu3/tools/h3.png"
            width="413"
            height="294"
          />
          <div class="ml-12">
            <h2>{{ t("m3-st-t-h-s3-t1") }}</h2>
            <h5 class="border-b">
              {{ t("m3-st-t-h-s3-d1") }}
            </h5>
            <div class="border-b">{{ t("learn-more-info") }}</div>
          </div>
        </section>
        <!-- API 交易 -->
        <section class="flex mb-8">
          <div class="mr-12">
            <h2>{{ t("m3-st-t-h-s4-t1") }}</h2>
            <h5 class="border-b">
              {{ t("m3-st-t-h-s4-d1") }}
            </h5>
            <div class="border-b">{{ t("learn-more-info") }}</div>
          </div>
          <img
            src="@/assets/images/compositeHome/menu3/tools/h4.png"
            width="413"
            height="278"
          />
        </section>
        <ready></ready>
      </template>
    </menu-layout>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { menu3List } from "@/utils/path";
import ready from "@comCompositeHome/ready.vue";
const route = useRoute();
const { t } = useI18n();
</script>
<style lang="scss" scoped>
.border-b {
  border-bottom: 1px solid #d5d5d5;
  padding: 20px 0;
}
</style>
