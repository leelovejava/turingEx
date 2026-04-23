<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h1>{{ t("ld-s0-t1") }}</h1>
      <h5 class="w-500">{{ t("ld-s0-d1") }}</h5>
    </pc-section>
    <menu-layout :needMenu="false">
      <template v-slot:content>
        <h5 class="mb-8">{{ t("ld-s1-d1") }}</h5>
        <el-collapse v-model="questionActive" class="question-collapse">
          <el-collapse-item :title="t('ld-s1-1-q1')" :name="1">
            <h6 v-html="t('ld-s1-1-a1')"></h6>
          </el-collapse-item>
        </el-collapse>
        <h5 class="mt-8">{{ t("ld-s1-d2") }}</h5>
      </template>
    </menu-layout>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";

const questionActive = ref();
const route = useRoute();
const { t } = useI18n();

const gotoPage = () => {};
</script>
<style lang="scss" scoped>
.w-500 {
  width: 500px;
}
</style>
