<!-- etf -->
<template>
  <menu-layout :collapseList="menu2List">
    <template v-slot:content>
      <section class="section1">
        <h2>{{ t("m2-product-4-s1-t1") }}</h2>
        <h5>{{ t("m2-product-4-s1-d1") }}</h5>
      </section>
      <!-- 为什么要交易 ETF 差价合约？ -->
      <section class="pt-8">
        <h2>{{ t("m2-product-4-s2-t1") }}</h2>
        <card
          v-for="(item, index) in list3"
          :key="index"
          :desc="t(`m2-product-4-s2-d${index + 1}`)"
        ></card>
      </section>
      <!-- 可交易的ETF -->
      <section class="pt-8">
        <h2>{{ t("m2-product-4-s3-t1") }}</h2>
        <el-tabs
          v-model="activeName"
          class="demo-tabs"
          @tab-click="handleClick"
        >
          <el-tab-pane
            :label="t(`m2-product-4-s3-h${i + 1}`)"
            :name="i"
            v-for="(_, i) in list9"
            :key="i"
          >
            <el-table :data="eTableData[i]" class="self-table">
              <el-table-column
                v-for="(item, index) in tableList"
                :key="index"
                :prop="item.prop"
                :label="t(`m2-product-4-s3-v${index + 1}`)"
              />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </section>
      <!-- 我们的佣金 -->
      <section class="py-8">
        <h2>{{ t("m2-product-4-s4-t1") }}</h2>
        <h5 class="my-4">
          {{ t("m2-product-4-s4-d1") }}
        </h5>
        <card
          v-for="(item, index) in list3"
          :key="index"
          :desc="t(`m2-product-4-s4-${index + 1}`)"
        ></card>
        <!-- 问题 -->
        <el-collapse v-model="questionActive" class="mt-8 question-collapse">
          <el-collapse-item
            :title="t(`m2-product-4-s4-4-t${i + 1}`)"
            :name="i"
            v-for="(_, i) in list2"
            :key="i"
          >
            <h6 v-html="t(`m2-product-4-s4-4-d${i + 1}`)"></h6>
          </el-collapse-item>
        </el-collapse>
      </section>
      <!-- 为什么交易ETF差价合约？ -->
      <section>
        <h2 class="my-4">{{ t("m2-product-4-s5-t1") }}</h2>
        <h5 v-html="t('m2-product-4-s5-d1')"></h5>
        <!-- 优势和风险 -->
        <div class="flex mt-8">
          <div class="flex1 mr-12">
            <p class="text-4xl font-semibold mb-4">
              {{ t("m2-product-4-s5-1-t1") }}
            </p>
            <card
              v-for="(item, index) in list6"
              :lineWidth="'350px'"
              :key="index"
              :desc="t(`m2-product-4-s5-1-d${index + 1}`)"
            ></card>
          </div>
          <div class="flex1">
            <p class="text-4xl font-semibold mb-4">
              {{ t("m2-product-4-s5-2-t1") }}
            </p>
            <card
              v-for="(item, index) in list4"
              :lineWidth="'350px'"
              :key="index"
              :desc="t(`m2-product-4-s5-2-d${index + 1}`)"
            ></card>
          </div>
        </div>
      </section>
      <ready class="py-8"></ready>
    </template>
  </menu-layout>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import card from "@comCompositeHome/menu2/card.vue";
import { menu2List } from "@/utils/path";
import ready from "@comCompositeHome/ready.vue";
import { eTableData } from "./table-data";
const route = useRoute();
const { t } = useI18n();
const activeName = ref(0);
const questionActive = ref();
const list2 = Array(2);
const list3 = Array(3);
const list4 = Array(4);
const list6 = Array(6);
const list11 = Array(11); //TODO
const list9 = Array(9);
const tableList = [
  {
    prop: "v1",
  },
  {
    prop: "v2",
  },
];

const gotoPage = () => {};

const handleClick = () => {};
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
.border-wrapper {
  border: 1px solid #d9d9d9;
}
</style>
