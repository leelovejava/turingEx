<!-- 工具-Capitalise.ai -->
<template>
  <menu-layout :collapseList="menu3List">
    <template v-slot:content>
      <section class="section1">
        <h2>{{ t("m3-st-t-s-s1-t1") }}</h2>
        <h5>{{ t("m3-st-t-s-s1-t2") }}</h5>
      </section>
      <!-- 为什么交易者都使用Smart Trader工具 -->
      <div class="my-4">
        <h2>{{ t("m3-st-t-s-s2-t1") }}</h2>
        <h5>{{ t("m3-st-t-s-s2-d1") }}</h5>
      </div>

      <!-- 我们最钟爱的工具-->
      <section>
        <h2>{{ t("m3-st-t-s-s3-t1") }}</h2>
        <card
          v-for="(item, index) in list3"
          :key="index"
          :desc="t(`m3-st-t-s-s3-${index + 1}-d1`)"
        ></card>
      </section>

      <section>
        <!-- 包括哪些？ -->
        <h2 class="mt-4">{{ t("m3-st-t-s-s4-t1") }}</h2>
        <h5>{{ t("m3-st-t-s-s4-d1") }}</h5>
        <!-- 交易顾问（EA） -->
        <h2 class="text-center">{{ t("m3-st-t-s-s5-t1") }}</h2>

        <div class="grid grid-cols-3 gap-x-5 gap-y-3">
          <m-card
            v-for="(item, i) in list12"
            :key="i"
            :title="t(`m3-st-t-s-s5-${i + 1}-t1`)"
            :imgPath="`/src/assets/images/compositeHome/menu3/tools/st${
              i + 1
            }.png`"
            :desc="t(`m3-st-t-s-s5-${i + 1}-d1`)"
          ></m-card>
        </div>
        <!-- 指标 -->
        <h2 class="text-center">{{ t("m3-st-t-s-s6-t1") }}</h2>
        <div class="grid grid-cols-3 gap-x-5 gap-y-3">
          <m-card
            v-for="(item, i) in list15"
            :key="i"
            :title="t(`m3-st-t-s-s6-${i + 1}-t1`)"
            :imgPath="`/src/assets/images/compositeHome/menu3/tools/st-2-${
              i + 1
            }.png`"
            :desc="t(`m3-st-t-s-s6-${i + 1}-d1`)"
          ></m-card>
        </div>
      </section>
      <!-- 立刻咨询我们 -->
      <h5>{{ t("m3-st-t-s-s7-d1") }}</h5>
      <div class="blue-border my-8">
        <h5>{{ t("m3-st-t-s-s7-d2") }}</h5>
      </div>
      <ready class="py-8"></ready>
    </template>
  </menu-layout>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { menu3List } from "@/utils/path";
import card from "@comCompositeHome/menu2/card.vue";
import mCard from "@comCompositeHome/menu3/card.vue";
import ready from "@comCompositeHome/ready.vue";
const list3 = Array(3);
const list12 = Array(12);
const list15 = Array(15);

const { t } = useI18n();
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
.shadow-wrapper {
  box-shadow: 0px 2px 8px 10px rgba(0, 0, 0, 0.05);
}

.w-327 {
  width: 327px;
}
.border-line {
  border-bottom: 1px solid rgba(0, 0, 0, 0.15);
}
</style>
