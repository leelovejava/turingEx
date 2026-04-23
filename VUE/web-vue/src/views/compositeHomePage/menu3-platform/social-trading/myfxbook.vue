<!-- myfxbook -->
<template>
  <menu-layout :collapseList="menu3List">
    <template v-slot:content>
      <section class="section1">
        <h2>{{ t("m3-st-mf-s1-t1") }}</h2>
        <h5>{{ t("m3-st-mf-s1-d1") }}</h5>
      </section>
      <section class="pt-8">
        <!-- 如何操作 -->
        <h2>{{ t("m3-st-mf-s2-t1") }}</h2>
        <h5>{{ t("m3-st-mf-s2-d1") }}</h5>
        <!-- Myfxbook功能 -->
        <h2 class="fs-32 mt-4">{{ t("m3-st-mf-s3-t1") }}</h2>
        <card
          v-for="(item, index) in list3"
          :key="index"
          :desc="t(`m3-st-mf-s3-${index + 1}`)"
        ></card>

        <!-- 第一步 - 创建您的交易账户 -->
        <div class="blue-border my-8">
          <h2 class="fs-32">{{ t("m3-st-mf-s4-1-t1") }}</h2>
          <p class="fs-18">{{ t("m3-st-mf-s4-1-q1") }}</p>
          <h5 v-html="t('m3-st-mf-s4-1-a1')"></h5>
          <p class="fs-18">{{ t("m3-st-mf-s4-1-q2") }}</p>
          <h5 v-html="t('m3-st-mf-s4-1-a2')"></h5>
        </div>
        <!-- 第二步 - 将您的交易账户连接到Myfxbook AutoTrade -->
        <div class="blue-border my-8">
          <h2 class="fs-32">{{ t("m3-st-d-s3-t1") }}</h2>
          <h5 v-html="t('m3-st-mf-s4-2-a1')"></h5>
        </div>
        <!-- 免责声明 -->
        <p class="text-gray-tip" v-html="t('m3-st-mf-s5-d1')"></p>
        <p class="text-gray-tip tip-top" v-html="t('m3-st-mf-s5-d2')"></p>
      </section>
    </template>
  </menu-layout>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import card from "@comCompositeHome/menu2/card.vue";
import { menu3List } from "@/utils/path";
import ready from "@comCompositeHome/ready.vue";
const route = useRoute();
const { t } = useI18n();

const list3 = Array(3);

const gotoPage = () => {};

const handleClick = () => {};
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}

.fs-18 {
  font-size: 18px;
  margin: 12px 0;
}

.tip-top {
  margin-top: 8px;
}
</style>
