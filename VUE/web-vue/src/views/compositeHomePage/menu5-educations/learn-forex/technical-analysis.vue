<!-- 学习外汇-技术分析 -->
<template>
  <menu-layout :collapseList="menu5List">
    <template v-slot:content>
      <div class="section1">
        <h2>{{ t("m5-wh-ta-s1-t1") }}</h2>
        <h5 v-html="t('m5-wh-ta-s1-d1')"></h5>
      </div>
      <div class="content mt-8" v-html="t('m5-wh-ta-s2-c')"></div>
      <el-collapse v-model="questionActive" class="question-collapse">
        <el-collapse-item
          :title="t(`m5-wh-ta-s2-${i + 1}-q`)"
          :name="i"
          v-for="(_, i) in list4"
          :key="i"
        >
          <h6 v-html="t(`m5-wh-ta-s2-${i + 1}-a`)"></h6>
        </el-collapse-item>
      </el-collapse>
    </template>
  </menu-layout>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { menu5List } from "@/utils/path";

const { t } = useI18n();
const list4 = Array(4);
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
