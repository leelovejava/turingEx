<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h1 class="w-500">{{ t("m1-at-s0-t1") }}</h1>
      <h5 class="w-500">{{ t("m1-at-s0-d1") }}</h5>
    </pc-section>
    <menu-layout :collapseList="menu1List">
      <template v-slot:content>
        <section>
          <h2>{{ t("m1-at-s1-t1") }}</h2>
          <h5 class="mb-4" v-for="(_, i) in list4" :key="i">
            {{ t(`m1-at-s1-d${i + 1}`) }}
          </h5>
          <!-- 为大额交易者提供更好的折扣 -->
          <h2>{{ t("m1-at-s2-t1") }}</h2>
          <el-table :data="tableData" style="width: 100%" class="self-table">
            <el-table-column
              v-for="(item, index) in tableList"
              :key="index"
              :prop="item.prop"
              :label="t(`m1-at-s2-n${index + 1}`)"
            />
          </el-table>
          <h5 class="my-4">{{ t("m1-at-s2-d1") }}</h5>
          <!-- 活跃交易者的额外专属权益 -->
          <h2>{{ t("m1-at-s3-t1") }}</h2>
          <div class="grid grid-cols-2 gap-8">
            <div class="card p-8" v-for="(_, i) in list3" :key="i">
              <img
                :src="getImageUrl(`${path}/icon${i + 1}.png`)"
                width="40"
                height="40"
              />
              <h5 class="my-4 text-left fs-24 font-normal">
                {{ t(`m1-at-s3-${i + 1}-t1`) }}
              </h5>
              <h5 class="text-base text-left">
                {{ t(`m1-at-s3-${i + 1}-d1`) }}
              </h5>
            </div>
          </div>
          <!-- 我如何加入Active Trader计划？ -->
          <h2 class="my-4">{{ t("m1-at-s4-t1") }}</h2>
          <!-- TODO -->
          <!-- <h5 class="mb-4" v-html="t('m1-at-s4-d1')"></h5> -->
          <h5 class="mb-4">{{ t("m1-at-s4-d2") }}</h5>
        </section>
      </template>
    </menu-layout>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { menu1List } from "@/utils/path";
import { getImageUrl } from "@/utils/index";
const route = useRoute();
const { t } = useI18n();
const path = "/src/assets/images/compositeHome/menu1/at";
const list3 = Array(3);
const list4 = Array(4);
const tableList = [
  {
    prop: "level",
  },
  {
    prop: "count",
  },
  {
    prop: "change",
  },
  {
    prop: "amount1",
  },
  {
    prop: "amount2",
  },

  {
    prop: "amount3",
  },
];
const tableData = [
  {
    level: t("m1-at-s2-h1"),
    count: t("m1-at-s2-h2"),
    change: t("m1-at-s2-h3"),
    amount1: t("m1-at-s2-h4"),
    amount2: t("m1-at-s2-h5"),
    amount3: t("m1-at-s2-h6"),
  },
  {
    level: "1",
    count: "$2",
    change: "10%",
    amount1: "< 200 ",
    amount2: "< 400 ",
    amount3: "< 2,000",
  },
  {
    level: "2",
    count: "$2",
    change: "20%",
    amount1: "200 < 1,500 ",
    amount2: "400 < 3,000 ",
    amount3: "2,000 < 15,000",
  },
  {
    level: "3",
    count: "$2",
    change: "30%",
    amount1: "> 1,501",
    amount2: "> 3,001",
    amount3: "> 15,001",
  },
];

const gotoPage = () => {};
</script>
<style lang="scss" scoped>
.w-500 {
  width: 500px;
}

.card {
  background: #ffffff;
  box-shadow: 0px 3px 10px 9px rgba(0, 0, 0, 0.05);
  border-top: 2px solid #2038d3;
}
</style>
