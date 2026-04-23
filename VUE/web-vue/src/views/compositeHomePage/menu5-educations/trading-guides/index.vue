<template>
  <menu-layout :collapseList="menu5List">
    <template v-slot:content>
      <div class="grid grid-cols-2 gap-x-5">
        <div
          class="border-top"
          :style="{ 'border-top-color': `${getColor(i)[1]}` }"
          v-for="(_, i) in list7"
          :key="i"
          @click="gotoPage(_.url)"
        >
          <div class="mt-4 mb-8 text-left">
            <span :class="getColor(i)[0]" class="tag">{{
              t(`m5-tg-${i + 1}-b1`)
            }}</span>
          </div>
          <h5 class="font-normal mb-4">{{ t(`m5-tg-${i + 1}-t1`) }}</h5>
          <h5>{{ t(`m5-tg-${i + 1}-d1`) }}</h5>
        </div>
      </div>
    </template>
  </menu-layout>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { menu5List } from "@/utils/path";
import { useI18n } from "vue-i18n";
import { computed } from "vue";

const router = useRouter();
const { t } = useI18n();
const list7 = [
  {
    url: "/education/11-tips-improving-performance-expert-advisor",
  },
  {
    url: "/education/extreme-volatility",
  },
  {
    url: "/education/why-spread",
  },
  {
    url: "/education/down-up",
  },
  {
    url: "/education/VIX",
  },
  {
    url: "/education/what-trader-type-are-you",
  },
  {
    url: "/education/how-trade-gold",
  },
];
const getColor = computed(() => (i) => {
  if (i == 0) {
    return ["yellow", "#EACD37"];
  }
  if (i == 1 || i == 6) {
    return ["green", "#06AF85"];
  }

  return ["purple", "#5E54C7"];
});
const gotoPage = (path) => {
  router.push(path);
};
</script>
<style lang="scss" scoped>
.tag {
  background: #e7ebee;
  border-radius: 2px;
  padding: 10px 16px;
}

.yellow {
  background: #f7ebaf;
}
.green {
  background: #9bdfce;
}
.purple {
  background: #bfbbe9;
}

.border-top {
  box-shadow: 0px 2px 8px 10px rgba(0, 0, 0, 0.05);
  padding: 36px 32px;
  border-top-width: 4px;
  border-top-style: solid;
}
</style>
