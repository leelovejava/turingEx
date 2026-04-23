<!-- 工具-VPS托管 -->
<template>
  <menu-layout :collapseList="menu3List">
    <template v-slot:content>
      <section class="section1">
        <h2>{{ t("m3-st-t-vp-s1-t1") }}</h2>
        <h5>{{ t("m3-st-t-vp-s1-d1") }}</h5>
      </section>

      <div class="my-4">
        <h5>{{ t("m3-st-t-vp-s1-d2") }}</h5>
        <h2 class="fs-24 mt-4">{{ t("m3-st-t-vp-s2-t1") }}</h2>
        <h5>{{ t("m3-st-t-vp-s2-d1") }}</h5>
        <p class="link-middle-text my-4 cursor-pointer">
          {{ t("m3-st-t-vp-s2-d2") }}
        </p>

        <h5 class="font-normal">{{ t("m3-st-t-vp-s2-d3") }}</h5>
        <h5 class="my-4">{{ t("m3-st-t-vp-s2-d4") }}</h5>
        <p class="link-middle-text cursor-pointer">
          {{ t("m3-st-t-vp-s2-d5") }}
        </p>
      </div>
    </template>
  </menu-layout>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { menu3List } from "@/utils/path";
const { t } = useI18n();
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
