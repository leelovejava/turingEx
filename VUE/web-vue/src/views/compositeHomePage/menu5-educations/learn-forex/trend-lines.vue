<!-- 学习外汇-趋势线 -->
<template>
  <menu-layout :collapseList="menu5List">
    <template v-slot:content>
      <div>
        <div class="section1">
          <h1>{{ t("m5-wh-tl-s1-t1") }}</h1>
          <h5>{{ t("m5-wh-tl-s1-d1") }}</h5>
        </div>
        <h2>{{ t("m5-wh-tl-s2-t1") }}</h2>
        <div v-for="(_, i) in list3" :key="i">
          <h2 class="fs-24 mt-8">{{ t(`m5-wh-tl-s2-${i + 1}-t1`) }}</h2>
          <img
            v-if="i == 2"
            :src="
              getImageUrl(
                `/src/assets/images/compositeHome/menu5/learnForex/tl1.png`
              )
            "
            width="834"
            height="414"
          />
          <h5 v-html="t(`m5-wh-tl-s2-${i + 1}-d1`)"></h5>
        </div>
        <ready class="mt-8"></ready>
      </div>
    </template>
  </menu-layout>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { getImageUrl } from "@/utils";
import { menu5List } from "@/utils/path";
import ready from "@comCompositeHome/ready.vue";
const route = useRoute();
const { t } = useI18n();

const list3 = Array(3);
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
