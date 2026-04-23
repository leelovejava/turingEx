<!-- 入金和出金 -->
<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h2>{{ t("m2-fw-s0-t1") }}</h2>
      <h5>{{ t("m2-fw-s0-d1") }}</h5>
    </pc-section>
    <menu-layout :collapseList="menu2List">
      <template v-slot:content>
        <section>
          <h2>{{ t("m2-fw-s1-t1") }}</h2>
          <p class="text-lg mb-4">{{ t("m2-fw-s1-d1") }}</p>
          <!-- 二级标题 -->
          <p class="text-second-title my-4">{{ t("m2-fw-s1-t2") }}</p>
          <li v-for="(_, i) in list9" :key="i">
            {{ t(`m2-fw-s1-t2-${i + 1}`) }}
          </li>
          <h5 class="my-4">{{ t("m2-fw-s1-d2") }}</h5>
          <!-- 重要信息 -->
          <h2 class="my-4">{{ t("m2-fw-s2-t1") }}</h2>
          <li v-for="(_, i) in list5" :key="i">
            {{ t(`m2-fw-s2-d${i + 1}`) }}
          </li>
          <!-- 常见问题  -->
          <h2 class="mb-8 mt-4">{{ t("m2-fw-s3-t1") }}</h2>
          <el-collapse v-model="questionActive" class="question-collapse">
            <el-collapse-item
              :title="t(`m2-fw-s3-q${i + 1}`)"
              :name="i"
              v-for="(_, i) in list3"
              :key="i"
            >
              <h6>{{ t(`m2-fw-s3-a${i + 1}`) }}</h6>
            </el-collapse-item>
          </el-collapse>
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
const questionActive = ref();
const list3 = Array(3);
const list5 = Array(5);
const list9 = Array(9);
const gotoPage = () => {};
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}

.text-second-title {
  font-size: 24px;
  margin-bottom: 24px;
  font-weight: 600;
}
li {
  font-size: 18px;
  color: #575c65;
  line-height: 36px;
}
</style>
