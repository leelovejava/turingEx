<!-- 股票差价合约 -->
<template>
  <menu-layout :collapseList="menu2List">
    <template v-slot:content>
      <section class="section1">
        <h2>{{ t("m2-product-5-s1-t1") }}</h2>
        <h5>{{ t("m2-product-5-s1-d1") }}</h5>
      </section>
      <!-- 为什么要在交易股票差价合约？ -->
      <section class="pt-8">
        <h2>{{ t("m2-product-5-s2-t1") }}</h2>
        <card
          v-for="(item, index) in list"
          :key="index"
          :desc="t(`m2-product-5-s2-d${index + 1}`)"
        ></card>
      </section>
      <!-- 可交易的股票 -->
      <section class="pt-8">
        <h2>{{ t("m2-product-5-s3-t1") }}</h2>
        <!-- 表格 -->
        <el-tabs v-model="activeName" class="demo-tabs mb-12">
          <el-tab-pane
            :label="t(`m2-product-5-s3-h${index + 1}`)"
            :name="index"
            v-for="(_, index) in list5"
            :key="index"
          >
            <div>
              <div class="tab-card-line">
                {{ t(`m2-product-5-s3-v${index + 1}`) }}
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
        <!-- 股票差价合约的佣金 -->
        <h2>{{ t("m2-product-5-s4-t1") }}</h2>
        <h5 class="mb-4">{{ t("m2-product-5-s4-d1") }}</h5>
        <el-table :data="tableData" class="self-table">
          <el-table-column
            v-for="(item, index) in tableList"
            :key="index"
            :prop="item.prop"
            :label="t(`m2-product-5-s4-v${index + 1}`)"
          />

          <!-- <el-table-column prop="v1" :label="t(`m2-product-5-s4-v1`)">
            <template #default="scope">
              <h5 v-html="t(scope.row.v1)"></h5>
            </template>
          </el-table-column>
          <el-table-column prop="v2" :label="t(`m2-product-5-s4-v2`)" />
          <el-table-column prop="v3" :label="t(`m2-product-5-s4-v3`)" /> -->
        </el-table>
        <h5 class="fs-14 mt-4">{{ t("m2-product-5-s4-d2") }}</h5>
        <h5 class="mb-4">{{ t("m2-product-5-s4-d3") }}</h5>
        <!-- 股票差价合约的市场数据费用 -->
        <h2>{{ t("m2-product-5-s5-t1") }}</h2>
        <h5>{{ t("m2-product-5-s5-d1") }}</h5>
        <!-- 股票差价合约交易平台 -->
        <h2>{{ t("m2-product-5-s6-t1") }}</h2>
        <h5 class="mb-4">{{ t("m2-product-5-s6-d1") }}</h5>
        <h5 class="mb-4">{{ t("m2-product-5-s6-d2") }}</h5>
        <p class="link">{{ t("m2-product-5-s6-d3") }}</p>
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
const router = useRouter();
const { t } = useI18n();
const activeName = ref(0);
const list = Array(3);
const list5 = Array(5);
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
    v1: "AU Share CFDs",
    v2: "0.07%",
    v3: "5 AUD",
  },
  {
    v1: "German Share CFDs",
    v2: "0.10%",
    v3: "7 EUR",
  },
  {
    v1: "HK Share CFDs",
    v2: "0.20%",
    v3: "45 HKD",
  },
  {
    v1: "UK Shares CFDs",
    v2: "0.10%",
    v3: "7 GBP",
  },
  {
    v1: "US Share and ETF CFDs",
    v2: "0.02 USD per share",
    v3: "0.02 USD",
  },
];

const gotoPage = () => {};
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}

.link {
  color: #2038d3;
  text-decoration: underline;
}
</style>
