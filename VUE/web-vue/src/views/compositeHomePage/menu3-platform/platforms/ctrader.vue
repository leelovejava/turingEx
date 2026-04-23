<!-- cTrader -->
<template>
  <menu-layout :collapseList="menu3List">
    <template v-slot:content>
      <section>
        <h1>{{ t("m3-ct-s1-t1") }}</h1>
        <h4>{{ t("m3-ct-s1-d1") }}</h4>
      </section>
      <section class="pt-8">
        <!-- 为什么选择cTrader？ -->
        <h2>{{ t("m3-ct-s2-t1") }}</h2>
        <h5>{{ t("m3-ct-s2-d1") }}</h5>
        <card
          v-for="(item, index) in list5"
          :key="index"
          :desc="t(`m3-ct-s2-${index + 1}`)"
        ></card>
        <!-- 下载cTrader -->
        <h2 class="mt-4">{{ t("m3-ct-s3-t1") }}</h2>
        <el-tabs v-model="activeName">
          <el-tab-pane :label="t('m3-ct-s3-1')" :name="0">
            <h5 v-html="t('m3-ct-s3-1-d1')"></h5>
          </el-tab-pane>
          <el-tab-pane :label="t('m3-ct-s3-2')" :name="1">
            <h5 v-html="t('m3-ct-s3-2-d1')"></h5>
          </el-tab-pane>
        </el-tabs>
        <!-- cTrader专为哪些用户设计 -->
        <div class="flex my-4">
          <div class="mr-12">
            <h2>{{ t("m3-ct-s4-t1") }}</h2>
            <h5>{{ t("m3-ct-s4-d1") }}</h5>
          </div>
          <img
            src="@/assets/images/compositeHome/menu3/mt4-1.png"
            width="414"
            height="363"
          />
        </div>
        <!-- 主要功能和优势 -->
        <h2 class="fs-32">{{ t("m3-ct-s4-t2") }}</h2>
        <div class="flex flex-wrap">
          <card
            :class="[0, 2, 4, 6].includes(index) ? 'mr-8' : ''"
            v-for="(item, index) in list5"
            :key="index"
            :desc="t(`m3-ct-s4-${index + 1}`)"
            :lineWidth="'350px'"
          ></card>
        </div>
        <!-- 支持的语言 -->
        <h2 class="mt-4 fs-32">{{ t("m3-ct-s5-t1") }}</h2>
        <h5>{{ t("m3-ct-s5-d1") }}</h5>
        <!-- 用户指南和视频 -->
        <div class="blue-border my-4">
          <h2 class="fs-32">{{ t("m3-ct-s6-t1") }}</h2>
          <h5 v-html="t('m3-ct-s6-d1')"></h5>
        </div>
        <!-- 常见问题 -->
        <h2 class="mt-8 mb-4 fs-32">{{ t("m3-ct-s7-t1") }}</h2>
        <el-collapse v-model="questionActive" class="question-collapse">
          <el-collapse-item
            :title="t(`m3-ct-s7-${i + 1}-q1`)"
            :name="i"
            v-for="(_, i) in list5"
            :key="i"
          >
            <h6 v-html="t(`m3-ct-s7-${i + 1}-a1`)"></h6>
          </el-collapse-item>
        </el-collapse>
      </section>
      <ready class="py-8"></ready>
    </template>
  </menu-layout>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import card from "@comCompositeHome/menu2/card.vue";
import { menu3List } from "@/utils/path";
import ready from "@comCompositeHome/ready.vue";
const route = useRoute();
const { t } = useI18n();
const activeName = ref(0);
const questionActive = ref();
const list5 = Array(5);

const gotoPage = () => {};
</script>
<style lang="scss" scoped>
.blue-border {
  box-shadow: 0px 2px 8px 10px rgba(0, 0, 0, 0.05);
  padding: 42px 32px;
  border-top: 4px solid #2465f1;
}
</style>
