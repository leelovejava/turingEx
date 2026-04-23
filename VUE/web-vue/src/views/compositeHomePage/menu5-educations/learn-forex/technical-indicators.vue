<!-- 学习外汇-技术指标 -->
<template>
  <menu-layout :collapseList="menu5List">
    <template v-slot:content>
      <div>
        <div class="section1">
          <h2>{{ t("m5-wh-ti-s1-t1") }}</h2>
          <h5>{{ t("m5-wh-ti-s1-d1") }}</h5>
        </div>
        <h2 class="fs-32 mt-4">{{ t("m5-wh-ti-s2-t1") }}</h2>
        <h5 v-html="t('m5-wh-ti-s2-d1')"></h5>
        <h2 class="fs-32">{{ t("m5-wh-ti-s3-t1") }}</h2>
        <h5>{{ t("m5-wh-ti-s3-d1") }}</h5>
        <div v-for="(_, i) in list4" :key="i">
          <h2 class="fs-24 mt-8">{{ t(`m5-wh-ti-s3-${i + 1}-t1`) }}</h2>
          <h5 v-html="t(`m5-wh-ti-s3-${i + 1}-d1`)"></h5>
          <h5 v-if="i == 2" v-html="t(`m5-wh-ti-s3-${i + 1}-d2`)"></h5>
          <img
            :src="
              getImageUrl(
                `/src/assets/images/compositeHome/menu5/learnForex/ti${
                  i + 1
                }.png`
              )
            "
            width="834"
            height="414"
          />
        </div>
        <h2 class="fs-24">{{ t("m5-wh-ti-s4-t1") }}</h2>
        <h5 v-html="t('m5-wh-ti-s4-d1')"></h5>
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

const list4 = Array(4);
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
