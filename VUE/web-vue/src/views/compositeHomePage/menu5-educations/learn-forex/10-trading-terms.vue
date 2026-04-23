<!-- 学习外汇-您需要了解的10个交易术语 -->
<template>
  <menu-layout :collapseList="menu5List">
    <template v-slot:content>
      <div>
        <div class="section1">
          <h1>{{ t("m5-wh-10tt-s1-t1") }}</h1>
          <h5>{{ t("m5-wh-10tt-s1-d1") }}</h5>
        </div>
        <h5 class="mt-8">{{ t("m5-wh-10tt-s1-d2") }}</h5>
        <h5 class="mb-8 font-normal">{{ t("m5-wh-10tt-s1-d3") }}</h5>
        <el-collapse v-model="questionActive" class="question-collapse">
          <el-collapse-item
            :title="t(`m5-wh-10tt-${i + 1}-q1`)"
            :name="i"
            v-for="(_, i) in list10"
            :key="i"
          >
            <h6 v-html="t(`m5-wh-10tt-${i + 1}-a1`)"></h6>
          </el-collapse-item>
        </el-collapse>
        <div class="blue-border my-8">
          <h2 class="mb-4">{{ t("m5-wh-10tt-s2-t1") }}</h2>
          <h5>{{ t("m5-wh-10tt-s2-d1") }}</h5>
        </div>
      </div>
    </template>
  </menu-layout>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { menu5List } from "@/utils/path";

const { t } = useI18n();
const questionActive = ref();
const list10 = Array(10);
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
