<!-- 点差 -->
<template>
  <div>
    <pc-section class="index-section1 py-12">
      <p class="text-4xl font-semibold mb-4">{{ t("m2-product-s1-t1") }}</p>
      <p>{{ t("m2-product-s1-d1") }}</p>
    </pc-section>
    <menu-layout :collapseList="menu2List">
      <template v-slot:content>
        <section>
          <h2>{{ t("m2-ssc-s1-t1") }}</h2>
          <h5>{{ t("m2-ssc-s1-d1") }}</h5>
        </section>
        <section class="pt-8">
          <!-- 我们的点差 -->
          <h2>{{ t("m2-ssc-s2-t1") }}</h2>
          <div class="mr-20">
            <el-tabs v-model="activeName1" class="demo-tabs">
              <el-tab-pane
                :label="t(`m2-ssc-s2-${i + 1}`)"
                :name="i"
                v-for="(_, i) in list5"
                :key="i"
              >
                <el-table :data="table1Data[i]" class="self-table">
                  <el-table-column
                    v-for="(item, index) in table1List[i]"
                    :key="index"
                    :prop="item.prop"
                    :label="t(item.label)"
                  />
                </el-table>
              </el-tab-pane>
            </el-tabs>
          </div>
        </section>
        <section class="pt-8">
          <!-- 我们的佣金 -->
          <h2>{{ t("m2-ssc-s3-t1") }}</h2>
          <h5>{{ t("m2-ssc-s3-d1") }}</h5>
          <div class="mr-20">
            <el-tabs v-model="activeName2" class="demo-tabs">
              <el-tab-pane
                :label="t(`m2-ssc-s3-${index + 1}`)"
                :name="index"
                v-for="(_, index) in list2"
                :key="index"
              >
                <el-table
                  v-if="activeName2 == 0"
                  :data="table2Data"
                  class="self-table"
                >
                  <el-table-column
                    v-for="(item, i) in table2List"
                    :key="i"
                    :prop="item.prop"
                    :label="t(`m2-ssc-s3-h${i + 1}`)"
                  />
                </el-table>
              </el-tab-pane>
            </el-tabs>
            <h5 v-if="activeName2 == 1" class="mt-4">
              {{ t("m2-ssc-s3-2-c") }}
            </h5>
          </div>
        </section>
        <section class="pt-8">
          <!-- 我们的掉期利率 -->
          <h5 class="content" v-html="t('m2-ssc-s4-t2')"></h5>

          <h5 class="my-4">{{ t("m2-ssc-s5-3-t1") }}</h5>
          <h5 class="mt-4">{{ t("m2-ssc-s5-3-d1") }}</h5>
        </section>
        <!-- 做好交易准备了吗？ -->
        <ready class="py-8"></ready>
      </template>
    </menu-layout>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import pcSection from "@comCompositeHome/pcSection.vue";
import ready from "@comCompositeHome/ready.vue";
import { menu2List } from "@/utils/path";
import {
  table1Data,
  table2Data,
  table1List,
  table2List,
} from "./spreads-swaps-commissions.js";

const route = useRoute();
const { t } = useI18n();

const list2 = Array(2);
const list3 = Array(3);
const list5 = Array(5);
const activeName1 = ref(0);
const activeName2 = ref(0);

const gotoPage = () => {};
</script>
<style lang="scss" scoped>
.test {
  font-size: 12px;
}
</style>
