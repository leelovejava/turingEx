<!-- 立即开始 -->
<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h2>{{ t("m2-gs-s0-t1") }}</h2>
      <h5>{{ t("m2-gs-s0-d1") }}</h5>
    </pc-section>
    <menu-layout :collapseList="menu2List">
      <template v-slot:content>
        <section>
          <h2>{{ t("m2-gs-s1-t1") }}</h2>
          <h5 class="mb-4">{{ t("m2-gs-s1-d1") }}</h5>
          <!--  -->
          <h2>{{ t("m2-gs-s2-t1") }}</h2>
          <h5 class="mb-4">{{ t("m2-gs-s2-d1") }}</h5>
          <li v-for="(_, i) in list3" :key="i">
            {{ t(`m2-gs-s2-${i + 1}`) }}
          </li>
          <h5 class="my-4">{{ t("m2-gs-s2-d2") }}</h5>
          <li v-for="(_, i) in list6" :key="i">
            {{ t(`m2-gs-s2-${i + 3}`) }}
          </li>
          <!--  -->
          <h2 class="mt-4">{{ t("m2-gs-s3-t1") }}</h2>
          <li v-for="(_, i) in list4" :key="i">
            {{ t(`m2-gs-s3-${i + 1}`) }}
          </li>
          <h5 class="my-4">{{ t("m2-gs-s3-d1") }}</h5>
          <li v-for="(_, i) in list2" :key="i">
            {{ t(`m2-gs-s3-${i + 4}`) }}
          </li>
          <!--  -->
          <div class="blue-border my-8">
            <p class="text-sm mb-4">{{ t("m2-gs-s3-d2") }}</p>
            <p class="text-sm">{{ t("m2-gs-s3-d3") }}</p>
          </div>
          <!-- 准备好申请了吗？ -->
          <h2>{{ t("m2-gs-s4-t1") }}</h2>
          <div class="question-wrapper" v-for="(_, i) in list4" :key="i">
            <p class="text-gray-question">{{ t(`m2-gs-s4-q${i + 1}`) }}</p>
            <h6>{{ t(`m2-gs-s4-a${i + 1}`) }}</h6>
          </div>
          <!-- 交易平台支持 -->
          <div class="blue-border">
            <h2 class="mb-4">{{ t("m2-gs-s5-t1") }}</h2>
            <li class="link" v-for="(_, i) in list3" :key="i">
              {{ t(`m2-gs-s5-${i + 1}`) }}
            </li>
          </div>
        </section>
        <ready class="py-8"></ready>
      </template>
    </menu-layout>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import ready from "@comCompositeHome/ready.vue";
import { menu2List } from "@/utils/path";

const route = useRoute();
const { t } = useI18n();
const list2 = Array(2);
const list3 = Array(3);
const list4 = Array(4);
const list6 = Array(6);
const gotoPage = () => {};
</script>
<style lang="scss" scoped>
.blue-border {
  box-shadow: 0px 2px 8px 10px rgba(0, 0, 0, 0.05);
  padding: 42px 32px;
  border-top: 4px solid #2465f1;
}
.question-wrapper {
  padding-bottom: 24px;
}

li {
  font-size: 16px;
  margin-bottom: 10px;
}
.link {
  color: #2038d3;
  // border-bottom: 1px solid #2038d3;
  text-decoration: underline;
}
</style>
