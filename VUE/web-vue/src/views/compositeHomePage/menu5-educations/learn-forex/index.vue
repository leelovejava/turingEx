<!-- 学习外汇-->
<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h2 class="mb-4">{{ t("m5-wh-i-s0-t1") }}</h2>
      <h5>{{ t("m5-wh-i-s0-d1") }}</h5>
    </pc-section>
    <menu-layout :collapseList="menu5List">
      <template v-slot:content>
        <div class="grid grid-cols-2 gap-x-4 mb-12">
          <div
            class="card-wrapper"
            v-for="(_, i) in list10"
            :key="i"
            @click="gotoPage(_.url)"
          >
            <img :src="getUrl(i)" width="416" height="180" />
            <div class="p-8 text-left">
              <span class="green-tag">{{ t("Beginner") }}</span>
              <h4 class="fw-400 mb-4">{{ t(`m5-wh-i-${i + 2}-t1`) }}</h4>
              <h6 class="mb-4">{{ t(`m5-wh-i-${i + 2}-d1`) }}</h6>
              <h5 class="cursor-pointer">
                <a>{{ t("m5-i-s1-b1") }}</a>
              </h5>
            </div>
          </div>
        </div>
      </template>
    </menu-layout>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { getImageUrl } from "@/utils";
import { menu5List } from "@/utils/path";

const router = useRouter();
const { t } = useI18n();

const list10 = [
  // {
  //   url: "/market-analysis/what-will-take-gold",
  // },
  {
    url: "/education/learn-forex/what-is-pip-value",
  },
  {
    url: "/education/learn-forex/risk-management",
  },
  {
    url: "/education/learn-forex/trend-lines",
  },

  {
    url: "/education/learn-forex/what-is-swap-in-forex",
  },
  {
    url: "/education/learn-forex/technical-indicators",
  },

  {
    url: "/education/learn-forex/how-forex-works",
  },
  {
    url: "/education/learn-forex/technical-analysis",
  },

  {
    url: "/education/learn-forex/trading-other-instruments",
  },
  {
    url: "/education/learn-forex/fundamental-analysis",
  },
  {
    url: "/education/learn-forex/10-trading-terms",
  },
];
const getUrl = (i) => {
  if (i == 0) {
    return getImageUrl(
      `/src/assets/images/compositeHome/menu5/learnForex/h1.png`
    );
  }
  if (i == 3) {
    return getImageUrl(
      `/src/assets/images/compositeHome/menu5/learnForex/h2.png`
    );
  }

  if (i == 5) {
    return getImageUrl(`/src/assets/images/compositeHome/menu5/h1.png`);
  }
  if (i == 6) {
    return getImageUrl(`/src/assets/images/compositeHome/menu5/h2.png`);
  }

  if (i == 7 || i == 4) {
    return getImageUrl(`/src/assets/images/compositeHome/menu5/h3.png`);
  }
  if (i == 11 || i == 9) {
    return getImageUrl(
      `/src/assets/images/compositeHome/menu5/learnForex/h2.png`
    );
  }
  return getImageUrl(
    `/src/assets/images/compositeHome/menu5/learnForex/h3.png`
  );
};

const gotoPage = (path) => {
  router.push(path);
};
</script>
<style lang="scss" scoped>
.green-tag {
  background: #9bdfce;
  border-radius: 2px;
  padding: 4px 12px;
  display: inline-block;
  margin-bottom: 24px;
}
.card-wrapper {
  box-shadow: 0px 2px 8px 4px rgba(0, 0, 0, 0.05);
}

.fw-400 {
  font-weight: 400;
}
</style>
