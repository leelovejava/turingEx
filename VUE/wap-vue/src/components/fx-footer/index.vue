<template>
  <div class="relative z-30 footer">
    <van-tabbar route v-model="active" active-color="#1194F7" @change="changeIndex" fixed safe-area-inset-bottom>
      <van-tabbar-item name="optional" to="/optional">
        <span :class="[active === 'optional' ? 'active' : '']">{{ $t("Optional") }}</span>
        <template #icon="props">
          <img :src="active == 'optional' ? icon.optional.active : icon.optional.inactive" alt="optional" />
        </template>
      </van-tabbar-item>
      <van-tabbar-item name="quotes" to="/quotes/index">
        <span :class="[active === 'quotes' ? 'active' : '']">{{ $t("quotes") }}</span>
        <template #icon>
          <img :src="active == 'quotes' ? icon.quotes.active : icon.quotes.inactive" alt="quotes" />
        </template>
      </van-tabbar-item>
      <van-tabbar-item name="trade" to="/trade/index">
        <span :class="[active === 'trade' ? 'active' : '']">{{ $t("trade") }}</span>
        <template #icon>
          <img :src="active == 'trade' ? icon.trade.active : icon.trade.inactive" alt="trade" />
        </template>
      </van-tabbar-item>
      <!-- <van-tabbar-item name="funds" to="/funds"> -->
        <!-- <span :class="[active === 'funds' ? 'active' : '']">{{ $t('资金') }}</span> -->
        <!-- <template #icon="props"> -->
          <!-- <img :src="props.active ? icon.funds.active : icon.funds.inactive" alt="funds" /> -->
        <!-- </template> -->
      <!-- </van-tabbar-item> -->
      <van-tabbar-item name="news" to="/news">
        <span :class="[active === 'news' ? 'active' : '']">{{ $t("news") }}</span>
        <template #icon>
          <img :src="active == 'news' ? icon.news.active : icon.news.inactive" alt="news" />
        </template>
      </van-tabbar-item>
      <!-- <van-tabbar-item name="trade" to="/exchange">
        <span>{{ $t('trade') }}</span>
        <template #icon="props">
          <img :src="props.active ? icon.trade.active : icon.trade.inactive"  alt="exchange"/>
        </template>
      </van-tabbar-item> -->
      <van-tabbar-item name="mine" to="/my">
        <span :class="[active === 'mine' ? 'active' : '']">{{ $t("my") }}</span>
        <template #icon>
          <img :src="active == 'mine' ? icon.mine.active : icon.mine.inactive" alt="mine" />
        </template>
      </van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useQuotesStore } from "@/store/quotes.store.js";
import { useI18n } from "vue-i18n";
import { useRoute } from "vue-router";
import { watch } from "vue";
import { themeStore } from "@/store/theme";
const thStore = themeStore();
const { t } = useI18n();
const active = ref("home");
const route = useRoute();
if (route.path == "/trade/index") {
  active.value = "trade";
} else if (route.path.indexOf("/quotes") != -1) {
  active.value = "quotes";
} else if (route.path == "/news/index") {
  active.value = "news";
} else if (route.path == "/my/index") {
  active.value = "mine";
} else if (route.path == "/optional/index") {
  active.value = "optional";
} else if (route.path == "/funds/index") {
  active.value = "funds";
}
let quotesStore = useQuotesStore();

watch(
  () => route.path,
  (nv) => {
    if (route.path == "/trade/index") {
      active.value = "trade";
    } else if (route.path.indexOf("/quotes") != -1) {
      active.value = "quotes";
    } else if (route.path == "/news/index") {
      active.value = "news";
    } else if (route.path == "/my/index") {
      active.value = "mine";
    } else if (route.path == "/optional/index") {
      active.value = "optional";
    } else if (route.path == "/funds/index") {
      active.value = "funds";
    }
  }
);
// 底部列表
const icon = {
  optional: {
    active: new URL(
      "@/assets/theme/dark/image/footer/optional-active.png",
      import.meta.url
    ),
    inactive: new URL(
      `../../assets/theme/${thStore.theme}/image/footer/optional.png`,
      import.meta.url
    ),
  },
  quotes: {
    active: new URL(
      "@/assets/theme/dark/image/footer/quotes-active.png",
      import.meta.url
    ),
    inactive: new URL(
      `../../assets/theme/${thStore.theme}/image/footer/quotes.png`,
      import.meta.url
    ),
  },
  news: {
    active: new URL("@/assets/theme/dark/image/footer/news-active.png", import.meta.url),
    inactive: new URL(
      `../../assets/theme/${thStore.theme}/image/footer/news.png`,
      import.meta.url
    ),
  },
  trade: {
    active: new URL("@/assets/theme/dark/image/footer/trade-active.png", import.meta.url),
    inactive: new URL(
      `../../assets/theme/${thStore.theme}/image/footer/trade.png`,
      import.meta.url
    ),
  },
  funds: {
    active: new URL('@/assets/theme/dark/image/footer/funds-active.png', import.meta.url),
    inactive: new URL(`../../assets/theme/${thStore.theme}/image/footer/funds.png`, import.meta.url),
  },
  mine: {
    active: new URL("@/assets/theme/dark/image/footer/menu-active.png", import.meta.url),
    inactive: new URL(
      `../../assets/theme/${thStore.theme}/image/footer/menu.png`,
      import.meta.url
    ),
  },
};
const changeIndex = (index) => {
  // console.log(index)
};
</script>

<style lang="scss" scoped>
:deep(.van-tabbar-item__text) {
  font-size: 12px;
  color: $footer_color !important;
}

:deep(.van-tabbar-item--active) {
  background-color: $footer_bg;
  color: $color_main !important;
}

.van-tabbar--fixed {
  z-index: 10;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
  background-color: $footer_bg;
  box-shadow: 5px 5px 5px 5px $footer-border;
}

.van-hairline--top-bottom::after {
  border: none;
}

.blue {
  color: $blue !important;
}

.active {
  color: $active_line !important;
}

.footer {
  img {
    width: 20px;
    height: 20px;
  }
}
</style>
