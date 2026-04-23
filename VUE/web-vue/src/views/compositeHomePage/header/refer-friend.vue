<template>
  <div>
    <pc-section class="index-section1" innerclass="grid grid-cols-2">
      <div class="pr-20 mt-10">
        <h1>{{ t("header-rf-s0-t1") }}</h1>
        <h5>{{ t("header-rf-s0-d1") }}</h5>
      </div>
      <img
        src="/src/assets/images/compositeHome/header/refer-friend.png"
        width="626"
        height="312"
      />
    </pc-section>
    <menu-layout :collapseList="menu2List">
      <template v-slot:content>
        <section>
          <h2>{{ t("header-rf-s1-t1") }}</h2>
          <h5 class="mb-4">{{ t("header-rf-s1-d1") }}</h5>
          <!-- 表格 -->
          <el-table :data="tableData" class="self-table">
            <el-table-column
              v-for="(item, i) in tableList"
              :key="i"
              :prop="item.prop"
              :label="t(`header-rf-s1-h${i + 1}`)"
            />
          </el-table>
          <!--常见的问题 -->
          <h2 class="mt-4">{{ t("header-rf-s2-t1") }}</h2>
          <el-collapse v-model="questionActive" class="question-collapse">
            <el-collapse-item
              :title="t(`header-rf-s2-q${i + 1}`)"
              :name="i"
              v-for="(_, i) in list2"
              :key="i"
            >
              <h6>{{ t(`header-rf-s2-a${i + 1}`) }}</h6>
            </el-collapse-item>
          </el-collapse>
          <!-- 提示 -->
          <h5 class="mt-20" v-html="t('header-rf-s3-tip1')"></h5>
        </section>
      </template>
    </menu-layout>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { menu2List } from "@/utils/path";
const questionActive = ref();

const route = useRoute();
const { t } = useI18n();
const list2 = Array(2);
const tableList = [
  {
    prop: "v1",
  },
  {
    prop: "v2",
  },
];
const tableData = [
  {
    v1: t("header-rf-s1-r1"),
    v2: t("header-rf-s1-r2"),
  },
];

const gotoPage = () => {};
</script>
<style lang="scss" scoped>
.test {
  font-size: 12px;
}
</style>
