<template>
  <menu-layout :collapseList="menu1List" :needMenu="false">
    <template v-slot:content>
      <section>
        <h2>{{ t("m1-LEI-s1-t1") }}</h2>
        <h5 class="mb-4">{{ t("m1-LEI-s1-d1") }}</h5>
        <h2>{{ t("m1-LEI-s2-t1") }}</h2>
        <el-collapse v-model="questionActive" class="question-collapse">
          <el-collapse-item :title="t(`m1-LEI-s2-${i + 1}-t1`)" :name="i" v-for="(_, i) in list5" :key="i">
            <h6 v-text="t(`m1-LEI-s2-${i + 1}-d1`)"></h6>
          </el-collapse-item>
        </el-collapse>
      </section>
    </template>
  </menu-layout>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { menu1List } from "@/utils/path";
const questionActive = ref();

const route = useRoute();
const { t } = useI18n();
const list5 = Array(5);

const gotoPage = () => { };
</script>
<style lang="scss" scoped>
.test {
  font-size: 12px;
}
</style>
