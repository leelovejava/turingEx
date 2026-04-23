<!-- 社交平台交易 -->
<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h1>{{ t("m3-st-h-s0-t1") }}</h1>
      <h5>{{ t("m3-st-h-s0-d1") }}</h5>
    </pc-section>
    <!-- menu-layout -->
    <menu-layout :collapseList="menu3List">
      <template v-slot:content>
        <section>
          <h2>{{ t("m3-st-h-s1-t1", { TITLE: $title }) }}</h2>
          <h5>{{ t("m3-st-h-s1-d1") }}</h5>
          <div class="flex flex-wrap my-4">
            <div
              class="s1-block mb-4"
              v-for="(_, i) in list4"
              :key="i"
              :class="[0, 2].includes(i) ? 'mr-4' : ''"
            >
              <img
                :src="
                  getImageUrl(
                    `/src/assets/images/compositeHome/menu3/ct${i}.png`
                  )
                "
                width="380"
                height="254"
              />
              <div class="s1-block-content">
                <h2 class="fs-24">{{ t(`m3-st-h-s1-${i + 1}-t1`) }}</h2>
                <h5>
                  {{ t(`m3-st-h-s1-${i + 1}-d1`) }}
                </h5>
                <div class="link-middle-text mt-4 cursor-pointer">
                  {{ t("learn-more-info") }}
                </div>
              </div>
            </div>
          </div>
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
import { getImageUrl } from "@/utils/index";
const list4 = Array(4);
const route = useRoute();
const { t } = useI18n();
</script>
<style lang="scss" scoped>
.s1-block {
  width: 380px;

  &-content {
    padding: 32px;
    box-shadow: 0px 4px 6px 6px rgba(0, 0, 0, 0.05);
  }
}
</style>
