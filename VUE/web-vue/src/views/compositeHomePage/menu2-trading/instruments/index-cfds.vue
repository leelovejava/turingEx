<!-- 指数差价合约 -->
<template>
  <menu-layout :collapseList="menu2List">
    <template v-slot:content>
      <section class="section1">
        <h2>{{ t("m2-product-2-s1-t1") }}</h2>
        <h5>{{ t("m2-product-2-s1-d1") }}</h5>
      </section>
      <!-- 为什么要在交易指数差价合约？ -->
      <section class="pt-8">
        <h2>{{ t("m2-product-2-s2-t1") }}</h2>
        <card
          v-for="(item, index) in list6"
          :key="index"
          :desc="t(`m2-product-2-s2-d${index + 1}`)"
        ></card>
      </section>
      <!-- 点差和交易时间 -->
      <section class="pt-8">
        <h2>{{ t("m2-product-2-s3-t1") }}</h2>
        <el-tabs v-model="activeName" class="demo-tabs">
          <el-tab-pane
            :label="t(`m2-product-2-s3-${i + 1}`)"
            :name="i"
            v-for="(_, i) in tabList"
            :key="i"
          >
            <el-table
              :data="tableDataIC[i]"
              :span-method="rowSpanMethod[i]"
              class="self-table"
            >
              <el-table-column
                v-for="(item, index) in tableList"
                :key="index"
                :prop="item.prop"
                :label="t(`m2-product-2-s3-h${index + 1}`)"
              />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </section>
      <!-- 随时了解有关指数的市场新闻，分析和见解 -->
      <section class="flex py-8">
        <img
          src="@/assets/images/compositeHome/menu2/image5.png"
          width="413"
          height="254"
        />
        <div class="ml-8">
          <h2>{{ t("m2-product-2-s4-t1") }}</h2>
          <el-button class="mb-8 gray-middle-btn" @click="gotoPage">{{
            t("zhaodaogengduao")
          }}</el-button>
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
import { tableDataIC, rowSpanMethod } from "./table-data";
const router = useRouter();
const { t } = useI18n();

const list6 = Array(6);
const tabList = Array(3);
const activeName = ref(0);

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
  {
    prop: "v4",
  },
  {
    prop: "v5",
  },
  {
    prop: "v6",
  },
];

const gotoPage = () => {
  router.push("/market-analysis");
};
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
