<!-- 交易时间 -->
<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h2>{{ t("m2-th-s0-t1") }}</h2>
      <h5>{{ t("m2-th-s0-d1", { TITLE: $title }) }}</h5>
    </pc-section>
    <menu-layout :collapseList="menu2List">
      <template v-slot:content>
        <section class="section1">
          <h5 class="mb-4">{{ t("m2-th-s1-d1") }}</h5>
          <h5 class="mb-4">{{ t("m2-th-s1-d2") }}</h5>
          <h5 class="mb-4">{{ t("m2-th-s1-d3") }}</h5>
        </section>
        <hour-table
          :index="i"
          v-for="(_, i) in list7"
          :key="i"
          :beforeDesc="i === 4 ? 'm2-th-s6-d1' : ''"
          :afterDesc="
            i === 0
              ? 'm2-th-s2-d1'
              : i === 3
              ? 'm2-th-s5-d1'
              : i === 4
              ? 'm2-th-s6-d2'
              : ''
          "
        ></hour-table>
        <ready class="py-8" />
      </template>
    </menu-layout>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import hourTable from "@comCompositeHome/menu2/hour-table.vue";
import ready from "@comCompositeHome/ready.vue";
import { menu2List } from "@/utils/path";

const list7 = Array(7);
const route = useRoute();
const { t } = useI18n();

const gotoPage = () => {};
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
