<!-- 加密货币 -->
<template>
  <menu-layout :collapseList="menu2List">
    <template v-slot:content>
      <section class="section1">
        <h2>{{ t("m2-product-6-s1-t1") }}</h2>
        <h5>{{ t("m2-product-6-s1-d1") }}</h5>
      </section>
      <section class="pt-8">
        <h2>{{ t("m2-product-6-s2-t1") }}</h2>
        <card
          v-for="(item, index) in list5"
          :key="index"
          :desc="t(`m2-product-6-s2-d${index + 1}`)"
        ></card>
      </section>
      <section class="pt-8">
        <h2>{{ t("m2-product-6-s3-t1") }}</h2>
        <!-- 表格 -->
        <el-tabs v-model="activeName" class="demo-tabs">
          <el-tab-pane
            :label="t(_item.label)"
            :name="_index"
            v-for="(_item, _index) in tabList"
            :key="_index"
          >
            <el-table :data="tableDataCY[_index]" class="self-table">
              <el-table-column
                v-for="(item, index) in tableListCY[_index]"
                :key="index"
                :prop="item.prop"
                :label="t(item.label)"
              />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </section>
      <section>
        <!-- 风险 -->
        <h2 class="mt-4">{{ t("m2-product-6-s4-t1") }}</h2>
        <h5 class="my-4" v-for="(item, index) in list4" :key="index">
          {{ t(`m2-product-6-s4-d${index + 1}`) }}
        </h5>
      </section>
      <section class="flex py-8">
        <img
          src="@/assets/images/compositeHome/menu2/image7.png"
          width="413"
          height="258"
        />
        <div class="ml-8">
          <h2 class="mb-4">{{ t("m2-product-6-s5-t1") }}</h2>
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
import { tableDataCY, tableListCY } from "./table-data";
const router = useRouter();
const { t } = useI18n();
const activeName = ref(0);
const list4 = Array(4);
const list5 = Array(5);
const tabList = [
  {
    label: "m5-wh-10tt-4-q1",
  },
  {
    label: "m5-wh-10tt-6-q1",
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
