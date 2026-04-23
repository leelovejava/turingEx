<!-- 维持计划 -->
<template>
  <menu-layout :collapseList="menu3List">
    <template v-slot:content>
      <h2>{{ t("m3-ms-s1-t1") }}</h2>
      <div class="ml-half">
        <img
          src="@/assets/images/compositeHome/menu3/ms1.png"
          width="413"
          height="300"
        />
      </div>
      <h2>{{ t("m3-ms-s2-t1") }}</h2>
      <h2 class="my-4">{{ t("m3-ms-s2-t2") }}</h2>
      <el-collapse v-model="questionActive" class="question-collapse">
        <el-collapse-item
          :title="t(`m3-ms-s2-${i + 1}-d1`)"
          :name="i"
          v-for="(_, i) in list2"
          :key="i"
        >
          <h6>{{ t(`m3-ms-s2-${i + 1}-d2`) }}</h6>
        </el-collapse-item>
      </el-collapse>
    </template>
  </menu-layout>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { menu3List } from "@/utils/path";
import { useI18n } from "vue-i18n";
const questionActive = ref();
const route = useRoute();
const { t } = useI18n();
const list2 = Array(2);
</script>
<style lang="scss" scoped>
.ml-half {
  margin-left: 50%;
}
</style>
