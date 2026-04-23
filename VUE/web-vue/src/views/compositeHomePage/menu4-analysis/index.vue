<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h1>{{ t("m4-i-s0-t1") }}</h1>
      <h5>{{ t("m4-i-s0-d1") }}</h5>
    </pc-section>
    <!-- 导航 -->
    <pc-section>
      <navigator></navigator>
    </pc-section>
    <pc-section class="mt-28 mb-12">
      <!-- 市场新闻 -->
      <div class="grid grid-cols-4 gap-x-6 gap-y-4">
        <div @click="gotoPage()" class="item1-grid">
          <h2>{{ t(`m4-mn-a1-t`) }}</h2>
          <h5>{{ t(`m4-mn-a1-d`) }}</h5>
        </div>

        <div @click="gotoPage()">
          <img
            :src="getImageUrl(`/src/assets/images/compositeHome/menu4/a1.png`)"
            width="295"
            height="180"
          />
        </div>
        <div v-for="(_, i) in list5" :key="i" @click="gotoPage(_.url)">
          <img
            v-if="i !== 4"
            :src="
              getImageUrl(
                `/src/assets/images/compositeHome/menu4/a${i + 2}.png`
              )
            "
            width="295"
            height="180"
          />
          <h2 class="fs-20">{{ t(`m4-mn-a${i + 2}-t`) }}</h2>
          <h5 class="mt-4">{{ t(`m4-mn-a${i + 2}-ti`) }}</h5>
        </div>
      </div>
    </pc-section>
    <readyWithBg2></readyWithBg2>
    <pc-section class="my-24">
      <h5>{{ t("tishi") }}</h5>
    </pc-section>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { getImageUrl } from "@/utils/index";
import readyWithBg2 from "@comCompositeHome/readyWithBg1.vue";
import navigator from "@comCompositeHome/navigator.vue";
const router = useRouter();
const { t } = useI18n();
// 除去第一个还有5个
const list5 = [
  {
    url: "/market-analysis/what-will-take-gold",
  },
  {
    url: "/market-analysis/us-aug-cpi-review-risk-sentiment-changed",
  },
  {
    url: "/market-analysis/us-august-cpi-preview",
  },
  {
    url: "/market-analysis/the-week-ahead-us-cpi",
  },
  {
    url: "/market-analysis/ecb-sep-meeting-preview",
  },
];
const gotoPage = (
  path = "/market-analysis/forex-market-unlimited-opportunities"
) => {
  router.push(path);
};
</script>
<style lang="scss" scoped>
.fs-20 {
  font-size: 20px;
}
.item1-grid {
  grid-column: 1 / 3;
}
</style>
