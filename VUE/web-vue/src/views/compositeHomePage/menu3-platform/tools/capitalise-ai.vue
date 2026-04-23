<!-- 工具-Capitalise.ai -->
<template>
  <menu-layout :collapseList="menu3List">
    <template v-slot:content>
      <section class="section1">
        <h2>{{ t("m3-st-t-c-s1-t1") }}</h2>
        <h2 class="fs-32">{{ t("m3-st-t-c-s1-t2") }}</h2>
        <h5>{{ t("m3-st-t-c-s1-d1") }}</h5>
      </section>
      <!-- 立即开始 -->
      <div class="my-4">
        <h2>{{ t("m3-st-t-c-s1-t3") }}</h2>
        <h5>{{ t("m3-st-t-c-s1-d3") }}</h5>
        <el-button class="gray-middle-btn mt-4">{{
          t("m3-st-t-c-s1-b1")
        }}</el-button>
      </div>

      <!-- 为什么使用 Capitalise.ai? -->
      <section>
        <h2>{{ t("m3-st-t-c-s2-t1") }}</h2>
        <div class="shadow-wrapper">
          <div
            v-for="(_, i) in list5"
            :key="i"
            class="flex items-center border-line p-8"
          >
            <div class="mr-8 w-327">
              <h2>{{ t(`m3-st-t-c-s2-${i + 1}-t1`) }}</h2>
              <h5>{{ t(`m3-st-t-c-s2-${i + 1}-d1`) }}</h5>
              <h5>{{ t(`m3-st-t-c-s2-${i + 1}-d2`) }}</h5>
            </div>
            <img
              :src="
                getImageUrl(
                  `/src/assets/images/compositeHome/menu3/ca-${i}.png`
                )
              "
              width="455"
              height="324"
            />
          </div>
        </div>
      </section>
      <!-- 对客户免费-->
      <div class="blue-border my-8">
        <h2 class="fs-32">{{ t("m3-st-t-c-s3-t1") }}</h2>
        <h5>{{ t("m3-st-t-c-s3-d1") }}</h5>
        <h5>{{ t("m3-st-t-c-s3-d2") }}</h5>
      </div>
      <!-- 使用 Capitalise.ai 平台时的贴心提示： -->
      <section>
        <h2>{{ t("m3-st-t-c-s2-t1") }}</h2>
        <div class="shadow-wrapper">
          <div
            v-for="(_, i) in list4"
            :key="i"
            class="flex items-center border-line p-8"
          >
            <div class="mr-8 w-327">
              <h2>{{ t(`m3-st-t-c-s4-${i + 1}-t1`) }}</h2>
              <h5>{{ t(`m3-st-t-c-s4-${i + 1}-d1`) }}</h5>
            </div>
            <img
              :src="
                getImageUrl(
                  `/src/assets/images/compositeHome/menu3/ca-${i + 5}.png`
                )
              "
              width="339"
              height="275"
            />
          </div>
        </div>
      </section>
      <ready class="py-8"></ready>
    </template>
  </menu-layout>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import { menu3List } from "@/utils/path";
import { getImageUrl } from "@/utils/index";
import ready from "@comCompositeHome/ready.vue";
const list4 = Array(4);
const list5 = Array(5);

const { t } = useI18n();
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}

.shadow-wrapper {
  box-shadow: 0px 2px 8px 10px rgba(0, 0, 0, 0.05);
}

.w-327 {
  width: 327px;
}

.border-line {
  border-bottom: 1px solid rgba(0, 0, 0, 0.15);
}
</style>
