<!-- 学习外汇-外汇交易如何操作-->
<template>
  <menu-layout :collapseList="menu5List">
    <template v-slot:content>
      <div>
        <h2>{{ t("m5-wh-hfw-s1-t1") }}</h2>
        <h5 v-html="t('m5-wh-hfw-s1-d1')"></h5>
        <h5 v-html="t('m5-wh-hfw-s1-d2')"></h5>
        <!-- 我应该何时买入？ -->
        <h2 class="fs-32 my-6">{{ t("m5-wh-hfw-s2-t1") }}</h2>
        <h5 v-html="t('m5-wh-hfw-s2-d1')"></h5>
        <!-- 我应该何时卖出？？ -->
        <h2 class="fs-32 my-6">{{ t("m5-wh-hfw-s3-t1") }}</h2>
        <h5 v-html="t('m5-wh-hfw-s3-d1')"></h5>
        <!-- 货币何时可以交易？ -->
        <h2 class="fs-32 my-6">{{ t("m5-wh-hfw-s4-t1") }}</h2>
        <h5 v-html="t('m5-wh-hfw-s4-d1')"></h5>
        <h5 v-html="t('m5-wh-hfw-s4-d2')"></h5>
        <el-table :data="tableData1" class="self-table">
          <el-table-column
            v-for="(item, i) in list4"
            :key="i"
            :prop="`v${i + 1}`"
            :label="t(`m5-wh-hfw-s4-h${i + 1}`)"
          >
          <template #default="scope">
            <span>{{ `${t(scope.row[`v${i + 1}`])}` }}</span>
          </template>
          </el-table-column>
        </el-table>
        <h5 v-html="t('m5-wh-hfw-s4-d3')"></h5>
        <el-table :data="tableData2" class="self-table">
          <el-table-column
            v-for="(item, i) in list4"
            :key="i"
            :prop="`v${i + 1}`"
            :label="t(`m5-wh-hfw-s4-h${i + 1}`)"
        >
        <template #default="scope">
          <span>{{ `${t(scope.row[`v${i + 1}`])}` }}</span>
        </template>
        </el-table-column>
        </el-table>
        <h5 v-html="t('m5-wh-hfw-s4-d4')"></h5>
        <!--接下来是什么？ -->
        <h2 class="my-6">{{ t("m5-wh-hfw-s5-t1") }}</h2>
        <h5 v-html="t('m5-wh-hfw-s5-d1')"></h5>
        <h5 v-html="t('m5-wh-hfw-s5-d2')"></h5>
        <ready class="mt-8"></ready>
      </div>
    </template>
  </menu-layout>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { menu5List } from "@/utils/path";
import ready from "@comCompositeHome/ready.vue";
const { t } = useI18n();

const list4 = Array(4);

const tableData1 = [
  {
    v1: "m5-wh-hfw-s4-v1",
    v2: "m5-wh-hfw-s4-v2",
    v3: "8:00 am",
    v4: "5:00 pm",
  },
  {
    v1: "m5-wh-hfw-s4-v1",
    v2: "m5-wh-hfw-s4-v3",
    v3: "7:00 am",
    v4: "4:00 pm",
  },
  {
    v1: "m5-wh-hfw-s4-v4",
    v2: "m5-wh-hfw-s4-v5",
    v3: "1:00 pm",
    v4: "10:00 pm",
  },
  {
    v1: "m5-wh-hfw-s4-v4",
    v2: "m5-wh-hfw-s4-v6",
    v3: "2:00 pm",
    v4: "11:00 pm",
  },
  {
    v1: "m5-wh-hfw-s4-v7",
    v2: "m5-wh-hfw-s4-v8",
    v3: "0:00 am",
    v4: "9:00 am",
  },
  {
    v1: "m5-wh-hfw-s4-v7",
    v2: "m5-wh-hfw-s4-v9",
    v3: "1:00 am",
    v4: "10:00 am",
  },
  {
    v1: "m5-wh-hfw-s4-v10",
    v2: "m5-wh-hfw-s4-v11",
    v3: "10:00 pm",
    v4: "7:00 am",
  },
  {
    v1: "m5-wh-hfw-s4-v10",
    v2: "m5-wh-hfw-s4-v12",
    v3: "10:00 pm",
    v4: "6:00 am",
  },
];
const tableData2 = [
  {
    v1: "m5-wh-hfw-s4-v1",
    v2: "m5-wh-hfw-s4-v2",
    v3: "3:00 am",
    v4: "12:00 am",
  },
  {
    v1: "m5-wh-hfw-s4-v1",
    v2: "m5-wh-hfw-s4-v3",
    v3: "2:00 am",
    v4: "11:00 am",
  },
  {
    v1: "m5-wh-hfw-s4-v4",
    v2: "m5-wh-hfw-s4-v5",
    v3: "8:00 am",
    v4: "5:00 pm",
  },
  {
    v1: "m5-wh-hfw-s4-v4",
    v2: "m5-wh-hfw-s4-v6",
    v3: "9:00 am",
    v4: "6:00 pm",
  },
  {
    v1: "m5-wh-hfw-s4-v7",
    v2: "m5-wh-hfw-s4-v8",
    v3: "7:00 pm",
    v4: "4:00 am",
  },
  {
    v1: "m5-wh-hfw-s4-v7",
    v2: "m5-wh-hfw-s4-v9",
    v3: "8:00 pm",
    v4: "5:00 am",
  },
  {
    v1: "m5-wh-hfw-s4-v10",
    v2: "m5-wh-hfw-s4-v11",
    v3: "5:00 pm",
    v4: "2:00 am",
  },
  {
    v1: "m5-wh-hfw-s4-v10",
    v2: "m5-wh-hfw-s4-v12",
    v3: "5:00 pm",
    v4: "1:00 am",
  },
];
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
