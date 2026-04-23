<!-- 交易账户 -->
<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h2>{{ t("m2-ac-s0-t1") }}</h2>
      <h5>{{ t("m2-ac-s0-d1") }}</h5>
    </pc-section>
    <menu-layout :collapseList="menu2List">
      <template v-slot:content>
        <section>
          <h2>{{ t("m2-ac-s1-t1") }}</h2>
          <div class="flex flex-wrap">
            <card
              v-for="(item, index) in list10"
              :class="[0, 2, 4, 6, 8].includes(index) ? 'mr-8' : ''"
              :key="index"
              :desc="t(`m2-ac-s1-d${index + 2}`)"
              :lineWidth="'300px'"
            ></card>
          </div>
        </section>
        <section class="pt-8">
          <h5 class="text-lg mb-4">{{ t("m2-ac-s2-d1") }}</h5>
          <h5 class="text-lg mb-4">{{ t("m2-ac-s2-d2") }}</h5>
          <el-table :data="tableData" style="width: 100%" class="self-table">
            <el-table-column
              v-for="(item, index) in tableList"
              :key="index"
              :prop="item.prop"
              :label="t(`m2-ac-s2-h${index + 1}`)"
            >
            <template #default="scope">
              <span>{{ t(scope.row[item.prop]) }}</span>
            </template>
            </el-table-column>
          </el-table>
          <div class="text-lg my-4" v-html="t('m2-ac-s2-d3')"></div>
        </section>
        <section class="py-8">
          <!-- 常见问题 -->
          <h2 class="mb-8">{{ t("m2-ac-s3-t1") }}</h2>
          <el-collapse v-model="questionActive" class="question-collapse">
            <el-collapse-item
              :title="t(`m2-ac-s3-q${i + 1}`)"
              :name="i"
              v-for="(_, i) in list5"
              :key="i"
            >
              <div v-if="i == 4">
                <h6 class="mb-4 font-semibold">{{ t("m2-ac-s3-a5-t1") }}</h6>
                <h6 class="mb-4">{{ t("m2-ac-s3-a5-d1") }}</h6>
                <h6 class="mb-4">{{ t("m2-ac-s3-a5-d2") }}</h6>
                <h6 class="mb-4 font-semibold">{{ t("m2-ac-s3-a5-t2") }}</h6>
                <h6 class="mb-4">{{ t("m2-ac-s3-a5-d3") }}</h6>
                <h6 class="mb-4">{{ t("m2-ac-s3-a5-d4") }}</h6>
              </div>
              <h6 v-else>{{ t(`m2-ac-s3-a${i + 1}`) }}</h6>
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
const questionActive = ref();
const route = useRoute();
const { t } = useI18n();
const list5 = Array(5);
const list10 = Array(10);
const tableList = [
  {
    prop: "v1",
  },
  {
    prop: "v2",
  },
  {
    prop: "v3",
  },
];
const tableData = [
  {
    v1: "m2-ac-s2-r1-v1",
    v2: "m2-ac-s2-r1-v2",
    v3: "m2-ac-s2-r1-v3",
  },
  {
    v1: "m2-ac-s2-r2-v1",
    v2: "m2-ac-s2-r2-v2",
    v3: "m2-ac-s2-r2-v3",
  },
  {
    v1: "m2-ac-s2-r3-v1",
    v2: "m2-ac-s2-r3-v2",
    v3: "m2-ac-s2-r3-v3",
  },
  {
    v1: "m2-ac-s2-r4-v1",
    v2: "m2-ac-s2-r4-v2",
    v3: "m2-ac-s2-r4-v3",
  },
  {
    v1: "m2-ac-s2-r5-v1",
    v2: "m2-ac-s2-r5-v2",
    v3: "m2-ac-s2-r5-v3",
  },
];

const gotoPage = () => {};
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
