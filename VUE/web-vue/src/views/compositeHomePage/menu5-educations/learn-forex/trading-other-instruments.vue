<!-- 学习外汇-交易其他工具 -->
<template>
  <menu-layout :collapseList="menu5List">
    <template v-slot:content>
      <div>
        <div class="section1">
          <h1>{{ t("m5-wh-toi-s1-t1") }}</h1>
          <h5 v-html="t('m5-wh-toi-s1-d1')"></h5>
        </div>
        <h5 class="mt-8" v-html="t('m5-wh-toi-s1-d2')"></h5>
        <!-- 美元的影响 -->
        <h2 class="fs-24 mt-8">{{ t("m5-wh-toi-s2-t1") }}</h2>
        <h5 class="mb-4" v-html="t('m5-wh-toi-s2-d1')"></h5>
        <img
          src="/src/assets/images/compositeHome/menu5/learnForex/toi1.png"
          width="834"
          height="625"
        />
        <h5 v-html="t('m5-wh-toi-s2-d2')"></h5>
        <!-- 商品价格的其他关键驱动因素。 -->
        <h2 class="fs-32 mt-8">{{ t("m5-wh-toi-s3-t1") }}</h2>
        <div v-for="(_, i) in list9" :key="i">
          <h2 class="fs-32 mt-4">{{ t(`m5-wh-toi-s3-${i + 1}-t1`) }}</h2>
          <h5 v-html="t(`m5-wh-toi-s3-${i + 1}-d1`)"></h5>
          <img
            v-if="i == 5"
            class="mt-4"
            src="/src/assets/images/compositeHome/menu5/learnForex/toi2.png"
            width="834"
            height="695"
          />
          <h5
            v-if="[5, 6, 8].includes(i)"
            v-html="t(`m5-wh-toi-s3-${i + 1}-d2`)"
          ></h5>
          <img
            v-if="i == 6"
            class="mt-4"
            src="/src/assets/images/compositeHome/menu5/learnForex/toi3.png"
            width="834"
            height="388"
          />
          <h5
            v-if="[5, 6, 8].includes(i)"
            v-html="t(`m5-wh-toi-s3-${i + 1}-d3`)"
          ></h5>
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

const list9 = Array(9);
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
