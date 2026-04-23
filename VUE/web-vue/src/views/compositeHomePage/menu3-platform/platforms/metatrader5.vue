<!-- MetaTrader 5 -->
<template>
  <menu-layout :collapseList="menu3List">
    <template v-slot:content>
      <section>
        <h1>{{ t("m3-mt5-s1-t1") }}</h1>
        <h5>{{ t("m3-mt5-s1-d1") }}</h5>
      </section>
      <section class="pt-8">
        <!-- 为什么选择MetaTrader 5？ -->
        <h2>{{ t("m3-mt5-s2-t1") }}</h2>
        <h5>{{ t("m3-mt5-s2-d1") }}</h5>
        <div class="flex flex-wrap">
          <card
            v-for="(item, index) in list5"
            :class="[0, 2, 4, 6].includes(index) ? 'mr-8' : ''"
            :key="index"
            :desc="t(`m3-mt5-s2-${index + 1}`)"
            :lineWidth="'350px'"
          ></card>
        </div>
        <!-- 下载MetaTrader 5 -->
        <h2 class="mt-8">{{ t("m3-mt5-s3-t1") }}</h2>
        <el-tabs v-model="activeName" class="download-tabs">
          <el-tab-pane :label="t('m3-mt5-s3-1')" :name="0">
            <h5 v-html="t('m3-mt5-s3-1-d1')"></h5>
          </el-tab-pane>
          <el-tab-pane :label="t('m3-mt5-s3-2')" :name="1">
            <h5 v-html="t('m3-mt5-s3-2-d1')"></h5>
          </el-tab-pane>
        </el-tabs>
        <h5>{{ t("m3-mt5-s3-d1") }}</h5>
        <!-- MetaTrader5专为哪些用户设计？ -->
        <div class="flex mt-8">
          <div class="mr-12">
            <h2 class="text-4xl font-semibold mb-4">{{ t("m3-mt5-s4-t1") }}</h2>
            <h5 class="desc-color leading-normal">{{ t("m3-mt5-s4-d1") }}</h5>
          </div>
          <img
            src="@/assets/images/compositeHome/menu3/mt5-1.png"
            width="500"
            height="328"
          />
        </div>
      </section>
      <section class="py-8">
        <!--功能和优势 -->
        <h2>{{ t("m3-mt5-s4-t2") }}</h2>
        <div class="flex flex-wrap">
          <card
            :class="[0, 2, 4, 6, 8].includes(index) ? 'mr-8' : ''"
            v-for="(item, index) in list7"
            :key="index"
            :desc="t(`m3-mt5-s4-${index + 1}`)"
            :lineWidth="'350px'"
          ></card>
        </div>
        <!--MQL5与MQL4对比-->
        <h2>{{ t("m3-mt5-s5-t1") }}</h2>
        <div class="flex flex-wrap">
          <card
            :class="[0, 2, 4].includes(index) ? 'mr-8' : ''"
            v-for="(item, index) in list4"
            :key="index"
            :desc="t(`m3-mt5-s5-${index + 1}`)"
            :lineWidth="'350px'"
          ></card>
        </div>
        <!-- 支持的语言 -->
        <h2 class="mt-8">{{ t("m3-mt4-s6-t1") }}</h2>
        <h5>{{ t("m3-mt4-s6-d1") }}</h5>
        <!-- 用户指南和视频 -->
        <div class="blue-border my-8">
          <h5 v-html="t('m3-mt5-s6-t1')"></h5>
        </div>
        <!-- 常见问题 -->
        <h2 class="my-4">{{ t("m3-mt5-s7-t1") }}</h2>
        <el-collapse v-model="questionActive" class="question-collapse">
          <el-collapse-item
            :title="t(`m3-mt5-s7-${i + 1}-q1`)"
            :name="i"
            v-for="(_, i) in list5"
            :key="i"
          >
            <h6 v-html="t(`m3-mt5-s7-${i + 1}-a1`)"></h6>
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
const list4 = Array(4);
const list5 = Array(5);
const list7 = Array(7);

const gotoPage = () => {};
</script>
<style lang="scss" scoped>
.test {
  font-size: 12px;
}
</style>
