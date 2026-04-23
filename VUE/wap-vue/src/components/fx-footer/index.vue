<template>
  <div class="relative z-30 footer">
    <van-tabbar class="arc-tabbar" route v-model="active" active-color="#1194F7" @change="changeIndex" fixed safe-area-inset-bottom>
      <van-tabbar-item name="optional" to="/optional">
        <span :class="[active === 'optional' ? 'active' : '']">{{ $t("Optional") }}</span>
        <template #icon>
          <svg class="tab-icon" viewBox="0 0 24 24" aria-hidden="true">
            <rect x="3" y="7" width="18" height="12" rx="3" />
            <path d="M3 11h18" />
            <path d="M8 5h8" />
          </svg>
        </template>
      </van-tabbar-item>

      <van-tabbar-item name="quotes" to="/quotes/index">
        <span :class="[active === 'quotes' ? 'active' : '']">{{ $t("quotes") }}</span>
        <template #icon>
          <svg class="tab-icon" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 3l2.8 4.7 5.2.9-3.6 3.8.8 5.3L12 15.7 7 17.7l.8-5.3L4.2 8.6l5.2-.9L12 3z" />
            <path d="M12 7v6" />
          </svg>
        </template>
      </van-tabbar-item>

      <van-tabbar-item name="trade" to="/trade/index">
        <span :class="[active === 'trade' ? 'active' : '']">{{ $t("trade") }}</span>
        <template #icon>
          <div class="center-icon-wrap">
            <svg class="tab-icon center-tab-icon" viewBox="0 0 24 24" aria-hidden="true">
              <path d="M7 8h10" />
              <path d="M14 5l3 3-3 3" />
              <path d="M17 16H7" />
              <path d="M10 13l-3 3 3 3" />
            </svg>
          </div>
        </template>
      </van-tabbar-item>

      <van-tabbar-item name="news" to="/news">
        <span :class="[active === 'news' ? 'active' : '']">{{ $t("news") }}</span>
        <template #icon>
          <svg class="tab-icon" viewBox="0 0 24 24" aria-hidden="true">
            <rect x="4" y="4" width="16" height="16" rx="2.5" />
            <path d="M8 9h8" />
            <path d="M8 13h8" />
            <path d="M8 17h5" />
          </svg>
        </template>
      </van-tabbar-item>

      <van-tabbar-item name="mine" to="/my">
        <span :class="[active === 'mine' ? 'active' : '']">{{ $t("my") }}</span>
        <template #icon>
          <svg class="tab-icon" viewBox="0 0 24 24" aria-hidden="true">
            <circle cx="12" cy="8" r="3.2" />
            <path d="M5.5 19.5c1.7-3.2 4-4.8 6.5-4.8s4.8 1.6 6.5 4.8" />
          </svg>
        </template>
      </van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRoute } from "vue-router";
import { watch } from "vue";
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

watch(
  () => route.path,
  () => {
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

const changeIndex = () => {};
</script>

<style lang="scss" scoped>
:deep(.van-tabbar-item__text) {
  font-size: 12px;
  color: #bfc5d6 !important;
}

:deep(.van-tabbar-item--active) {
  background-color: transparent;
  color: #2f66ff !important;
}

.footer :deep(.arc-tabbar.van-tabbar) {
  z-index: 10;
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 72px;
  padding: 8px 10px calc(env(safe-area-inset-bottom) + 4px);
  padding-bottom: calc(constant(safe-area-inset-bottom) + 4px);
  background: linear-gradient(180deg, #131a2a 0%, #0c1322 100%);
  border-top: 1px solid rgba(125, 148, 190, 0.24);
  border-radius: 20px 20px 0 0;
  box-shadow: 0 -8px 24px rgba(0, 0, 0, 0.45);
  overflow: visible;
}

.footer :deep(.arc-tabbar.van-tabbar)::before {
  content: "";
  position: absolute;
  left: 50%;
  top: -14px;
  transform: translateX(-50%);
  width: 164px;
  height: 68px;
  border-radius: 0 0 82px 82px;
  background: radial-gradient(ellipse at 50% 0,
      rgba(86, 119, 214, 0.24) 0%,
      rgba(36, 54, 97, 0.16) 42%,
      rgba(12, 19, 34, 0) 74%);
  mix-blend-mode: screen;
  pointer-events: none;
}

.footer :deep(.arc-tabbar .van-tabbar-item) {
  padding-top: 6px;
}

.footer :deep(.arc-tabbar .van-tabbar-item:nth-child(3)) {
  margin-top: -24px;
}

.footer :deep(.arc-tabbar .van-tabbar-item:nth-child(3) .van-tabbar-item__icon) {
  width: 58px;
  height: 58px;
  margin-bottom: 2px;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 25%, #2d6dff 0%, #1e54f2 45%, #153fd3 100%);
  border: 4px solid #121a2c;
  box-shadow: 0 10px 20px rgba(10, 38, 114, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer :deep(.arc-tabbar .van-tabbar-item:nth-child(3) .van-tabbar-item__text) {
  margin-top: 2px;
}

.tab-icon {
  width: 20px;
  height: 20px;
  fill: none;
  stroke: #c6ccdc;
  stroke-width: 1.85;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.footer :deep(.van-tabbar-item--active) .tab-icon {
  stroke: #2f66ff;
  filter: drop-shadow(0 0 3px rgba(47, 102, 255, 0.35));
}

.center-tab-icon {
  width: 24px;
  height: 24px;
  stroke: #eef3ff;
}

.center-icon-wrap {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.active {
  color: #2f66ff !important;
}
</style>
