<!-- 大宗商品 -->
<template>
  <menu-layout :collapseList="menu2List">
    <template v-slot:content>
      <section class="section1">
        <h2>{{ t("m2-product-3-s1-t1") }}</h2>
        <h5>{{ t("m2-product-3-s1-d1") }}</h5>
      </section>
      <!-- 为什么要在交易商品差价合约？ -->
      <section class="pt-8">
        <h2>{{ t("m2-product-3-s2-t1") }}</h2>
        <card
          v-for="(item, index) in list5"
          :key="index"
          :desc="t(`m2-product-3-s2-d${index + 1}`)"
        ></card>
      </section>
      <!-- 商品点差和可交易产品 -->
      <section class="pt-8">
        <h2>{{ t("m2-product-3-s3-t1") }}</h2>
        <el-tabs v-model="activeName" class="demo-tabs">
          <el-tab-pane
            :label="t(`m2-product-3-s3-${i + 1}`)"
            :name="i"
            v-for="(_, i) in list4"
            :key="i"
          >
            <el-table :data="tableDataC[i]" class="self-table">
              <el-table-column
                v-for="(item, index) in tableLsitC[i]"
                :key="index"
                :prop="item.prop"
                :label="t(item.label)"
              />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </section>
      <!-- 随时了解有关商品的市场新闻，分析和见解 -->
      <section class="flex py-8">
        <img
          src="@/assets/images/compositeHome/menu2/image6.png"
          width="413"
          height="275"
        />
        <div class="ml-8">
          <p class="text-4xl font-semibold my-4">
            {{ t("m2-product-3-s4-t1") }}
          </p>
          <el-button class="gray-middle-btn mb-8" @click="gotoPage">{{
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
import { tableDataC, tableLsitC } from "./table-data";
const router = useRouter();
const { t } = useI18n();

const list5 = Array(5);
const list4 = Array(4);
const activeName = ref(0);

const tableList = [
  {
    prop: "product",
  },
  {
    prop: "size",
  },
  {
    prop: "value",
  },
  {
    prop: "min",
  },
  {
    prop: "ave",
  },

  {
    prop: "lever",
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
