<!-- 社交平台交易-DupliTrade -->
<template>
  <div>
    <menu-layout :collapseList="menu3List">
      <template v-slot:content>
        <section class="section1">
          <h2>{{ t("m3-st-d-s1-t1") }}</h2>
          <h5>{{ t("m3-st-d-s1-d1") }}</h5>
        </section>
        <section>
          <!-- 您可轻松加入 -->
          <div class="flex mt-8">
            <div class="mr-4">
              <h2>{{ t(`m3-st-d-s2-t1`) }}</h2>
              <h5>{{ t(`m3-st-d-s2-d1`) }}</h5>
            </div>
            <img
              src="/src/assets/images/compositeHome/menu3/d1.png"
              width="413"
              height="370"
            />
          </div>
          <!-- 立即开始 -->
          <div class="blue-border my-8">
            <h2>{{ t("m3-st-d-s3-t1") }}</h2>
            <h5 v-html="t('m3-st-d-s3-d1')"></h5>
          </div>
          <!-- 免责声明 -->
          <p class="text-gray-tip" v-html="t('m3-st-d-s4-d1')"></p>
          <p class="text-gray-tip tip-top" v-html="t('m3-st-d-s4-d2')"></p>
        </section>
      </template>
    </menu-layout>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { menu3List } from "@/utils/path";
import ready from "@comCompositeHome/ready.vue";
const list3 = Array.from({ length: 3 });
const route = useRoute();
const { t } = useI18n();
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
.tip-top {
  margin-top: 8px;
}
</style>
