<!-- 交易平台 -->
<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h2>{{ t("m3-pf-s0-t1") }}</h2>
      <h5>{{ t("m3-pf-s0-d1") }}</h5>
    </pc-section>
    <!-- menu-layout -->
    <menu-layout :collapseList="menu3List">
      <template v-slot:content>
        <section class="flex mb-4">
          <div class="mr-12">
            <h2>{{ t("m3-pf-s1-t1") }}</h2>
            <h5 class="border-b">
              {{ t("m3-pf-s1-d1") }}
            </h5>
            <div class="border-b flex items-center justify-between">
              {{ t("learn-more-info") }}
              <img
                src="@/assets/images/more.png"
                width="28"
                height="28"
                alt=""
                srcset=""
              />
            </div>
          </div>
          <img
            src="@/assets/images/compositeHome/menu3/image1.png"
            width="415"
            height="451"
          />
        </section>
        <section class="flex">
          <img
            class="mt-20"
            src="@/assets/images/compositeHome/menu3/image2.png"
            width="415"
            height="360"
          />
          <div class="ml-12">
            <h2>{{ t("m3-pf-s2-t1") }}</h2>
            <h5 class="border-b">
              {{ t("m3-pf-s2-d1") }}
            </h5>
            <div class="border-b flex items-center justify-between">
              {{ t("learn-more-info") }}
              <img
                src="@/assets/images/more.png"
                width="28"
                height="28"
                alt=""
                srcset=""
              />
            </div>
            <div class="border-b flex items-center justify-between">
              {{ t("m3-pf-s2-d2") }}
              <img
                src="@/assets/images/more.png"
                width="28"
                height="28"
                alt=""
                srcset=""
              />
            </div>
            <div class="border-b flex items-center justify-between">
              {{ t("m3-pf-s2-d3") }}
              <img
                src="@/assets/images/more.png"
                width="28"
                height="28"
                alt=""
                srcset=""
              />
            </div>
          </div>
        </section>
        <section class="flex">
          <div class="mr-12">
            <h2>{{ t("m3-pf-s3-t1") }}</h2>
            <h5 class="border-b">
              {{ t("m3-pf-s3-d1") }}
            </h5>
            <div class="border-b flex items-center justify-between">
              {{ t("learn-more-info") }}
              <img
                src="@/assets/images/more.png"
                width="28"
                height="28"
                alt=""
                srcset=""
              />
            </div>
            <div class="border-b flex items-center justify-between">
              {{ t("m3-pf-s3-d2") }}
              <img
                src="@/assets/images/more.png"
                width="28"
                height="28"
                alt=""
                srcset=""
              />
            </div>
          </div>
          <img
            src="@/assets/images/compositeHome/menu3/image3.png"
            width="415"
            height="512"
          />
        </section>
        <ready></ready>
      </template>
    </menu-layout>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { menu3List } from "@/utils/path";
import ready from "@comCompositeHome/ready.vue";
const route = useRoute();
const { t } = useI18n();

const gotoPage = () => {};
</script>
<style lang="scss" scoped>
.border-b {
  border-bottom: 1px solid #d5d5d5;
  padding: 20px 0;
}
</style>
