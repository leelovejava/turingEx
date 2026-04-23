<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h1>{{ t("m4-mn-s0-t1") }}</h1>
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
        <div v-for="(_, i) in list13" :key="i" @click="gotoPage(_.url)">
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
      <h5 class="my-24">{{ t("tishi") }}</h5>
    </pc-section>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { getImageUrl } from "@/utils/index";
const router = useRouter();
const { t } = useI18n();
// 除去第一个还有13个
const list13 = [
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
  {
    url: "/market-analysis/trade-global-equity-markets",
  },
  {
    url: "/market-analysis/opec-cut-production-gas-surge-euro-down",
  },
  {
    url: "/market-analysis/week-ahead-with-ecb-opec-rba-boc",
  },
  {
    url: "/market-analysis/aug-nfp-preview",
  },
  {
    url: "/market-analysis/dollar-strength-in-august-gold-fell-sharply",
  },
  {
    url: "/market-analysis/what-you-should-know-about-oil-trading",
  },
  {
    url: "/market-analysis/euro-rebound-as-gas-price-down-sharply",
  },
  {
    url: "/market-analysis/the-week-ahead-fed-remained-hawkish-dollar-20-year-high",
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
// .item2-grid {
//   grid-column: 4/ 7;
// }
</style>
