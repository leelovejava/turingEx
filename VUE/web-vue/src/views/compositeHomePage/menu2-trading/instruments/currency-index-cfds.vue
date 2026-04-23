<!-- 货币指数差价合约（CFD） -->
<template>
  <menu-layout :collapseList="menu2List">
    <template v-slot:content>
      <section class="section1">
        <p class="text-4xl font-semibold mb-4">{{ t("m2-product-7-s1-t1") }}</p>
        <p class="desc-color leading-normal">{{ t("m2-product-7-s1-d1") }}</p>
      </section>
      <p class="desc-color leading-normal my-8">
        {{ t("m2-product-7-s1-d2") }}
      </p>
      <el-table :data="tableData" style="width: 100%" class="self-table">
        <el-table-column
          v-for="(item, index) in tableList"
          :key="index"
          :prop="item.prop"
          :label="t(`m2-product-7-s2-h${index + 1}`)"
        />
      </el-table>
    </template>
  </menu-layout>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { menu2List } from "@/utils/path";
const route = useRoute();
const { t } = useI18n();

const tableList = [
  {
    prop: "product",
  },
  {
    prop: "symbol",
  },
  {
    prop: "size",
  },
  {
    prop: "change",
  },
  {
    prop: "lever",
  },
  {
    prop: "margin",
  },
];
const tableData = [
  {
    product: "US Dollar Index",
    symbol: "USDX",
    size: "100",
    change: "6",
    lever: "5:1",
    margin: "1%",
  },
  {
    product: "Euro Index",
    symbol: "EURX",
    size: "100",
    change: "1",
    lever: "5:1",
    margin: "1%",
  },
  {
    product: "Japanese Yen Index",
    symbol: "JPYX",
    size: "100",
    change: "1",
    lever: "5:1",
    margin: "1%",
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
