<template>
  <div class="footer">
    <!-- 顶边波浪：中间下凹，衬托中间 FAB -->
    <div class="footer-wave-wrap" aria-hidden="true">
      <svg class="footer-wave-svg" viewBox="0 0 400 22" preserveAspectRatio="none">
        <path
          fill="#000000"
          d="M0,22 L0,6 C28,6 55,8 85,11 C115,14 155,18 175,19.5 C188,20.5 200,21 212,19.5 C232,17 265,13 295,10 C335,6 368,4 400,6 L400,22 Z"
        />
      </svg>
    </div>
    <van-tabbar
      class="dock-tabbar"
      route
      v-model="active"
      :border="false"
      active-color="#3d8cff"
      inactive-color="#ebebf5"
      @change="changeIndex"
      fixed
      safe-area-inset-bottom
    >
      <van-tabbar-item name="quotes" to="/quotes/index">
        <span :class="['tab-label', active === 'quotes' ? 'active' : '']">{{ $t("quotes") }}</span>
        <template #icon>
          <svg class="tab-icon" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M4 18V8M4 18h14" />
            <path d="M6 18l3.5-8 3 6 3.5-10 3.5 12" />
          </svg>
        </template>
      </van-tabbar-item>

      <van-tabbar-item name="hot" to="/quotes/hotGallery">
        <span :class="['tab-label', active === 'hot' ? 'active' : '']">{{ $t("热门") }}</span>
        <template #icon>
          <svg class="tab-icon" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M4 11.5 12 5l8 6.5V19a1.5 1.5 0 0 1-1.5 1.5H15v-5.5H9V20.5H5.5A1.5 1.5 0 0 1 4 19v-7.5z" />
          </svg>
        </template>
      </van-tabbar-item>

      <van-tabbar-item name="trade" to="/trade/index">
        <span :class="['tab-label', 'tab-label--fab', active === 'trade' ? 'active' : '']">{{ $t("trade") }}</span>
        <template #icon>
          <div class="fab-wrap">
            <svg class="tab-icon fab-icon" viewBox="0 0 24 24" aria-hidden="true">
              <path d="M7.5 10.5h9M7.5 13.5h9" />
              <path d="M7.8 8.6a4.2 4.2 0 0 1 8.4 0" />
              <path d="M16.2 15.4a4.2 4.2 0 0 1-8.4 0" />
              <path d="M15.8 6.8l2.4 1.6-2.8.5M8.2 17.2l-2.4-1.6 2.8-.5" />
            </svg>
          </div>
        </template>
      </van-tabbar-item>

      <van-tabbar-item name="funds" to="/funds/index">
        <span :class="['tab-label', active === 'funds' ? 'active' : '']">{{ $t("资金") }}</span>
        <template #icon>
          <svg class="tab-icon tab-icon--coins" viewBox="0 0 24 24" aria-hidden="true">
            <ellipse cx="12" cy="5.2" rx="6.5" ry="2" />
            <path d="M5.5 5.2v3.2c0 1 2.9 2 6.5 2s6.5-1 6.5-2V5.2" />
            <ellipse cx="12" cy="10.6" rx="6.5" ry="2" />
            <path d="M5.5 10.6v3.2c0 1 2.9 2 6.5 2s6.5-1 6.5-2v-3.2" />
            <ellipse cx="12" cy="16" rx="6.5" ry="2" />
            <path d="M5.5 16v2.8c0 .9 2.9 1.8 6.5 1.8s6.5-.9 6.5-1.8V16" />
          </svg>
        </template>
      </van-tabbar-item>

      <van-tabbar-item name="mine" to="/my">
        <span :class="['tab-label', active === 'mine' ? 'active' : '']">{{ $t("my") }}</span>
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
import { ref, watch } from "vue";
import { useRoute } from "vue-router";

const active = ref("quotes");
const route = useRoute();

function syncActive() {
  const p = route.path;
  if (p === "/trade/index") active.value = "trade";
  else if (p.indexOf("/quotes/hotGallery") !== -1) active.value = "hot";
  else if (p.indexOf("/quotes") !== -1) active.value = "quotes";
  else if (p === "/funds/index") active.value = "funds";
  else if (p === "/my/index" || p === "/my") active.value = "mine";
}

syncActive();

watch(
  () => route.path,
  () => syncActive()
);

const changeIndex = () => {};
</script>

<style lang="scss" scoped>
.footer {
  position: relative;
  z-index: 30;
}

.footer-wave-wrap {
  position: fixed;
  left: 0;
  right: 0;
  /* 与底栏多叠 2px，避免顶缝出现浅色细线 */
  bottom: calc(62px + constant(safe-area-inset-bottom) - 3px);
  bottom: calc(62px + env(safe-area-inset-bottom) - 3px);
  height: 22px;
  pointer-events: none;
  /* 必须低于 .dock-tabbar(10)，否则整块波浪盖在底栏上，SVG 底边会在中间 FAB 上切出一条黑线 */
  z-index: 9;
}

.footer-wave-svg {
  display: block;
  width: 100%;
  height: 100%;
}

.footer :deep(.dock-tabbar.van-tabbar) {
  z-index: 10;
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 62px;
  padding: 6px 4px calc(6px + constant(safe-area-inset-bottom));
  padding-bottom: calc(6px + env(safe-area-inset-bottom));
  background: #000000;
  border: none !important;
  border-top: none !important;
  outline: none;
  border-radius: 24px 24px 0 0;
  /* 仅保留向上柔和阴影，不要顶边亮线 */
  box-shadow: 0 -12px 32px rgba(0, 0, 0, 0.5);
  overflow: visible;
  --van-tabbar-background: #000000;
  /* 选中项不要浅色底，只保留主题色用于文字（图标用自定义 SVG stroke） */
  --van-tabbar-item-active-background: transparent;
}

/* 防止 Vant hairline 残留顶线（::after 1px 边框） */
.footer :deep(.dock-tabbar.van-hairline--top-bottom::after),
.footer :deep(.dock-tabbar.van-hairline--top-bottom::before) {
  display: none !important;
  border: none !important;
  content: none !important;
}

.footer :deep(.dock-tabbar .van-tabbar-item) {
  padding-top: 4px;
  background-color: transparent !important;
}

.footer :deep(.dock-tabbar .van-tabbar-item--active) {
  background-color: transparent !important;
}

.footer :deep(.dock-tabbar .van-tabbar-item:nth-child(3)) {
  margin-top: -28px;
}

.footer :deep(.dock-tabbar .van-tabbar-item:nth-child(3) .van-tabbar-item__icon) {
  width: 58px;
  height: 58px;
  margin-bottom: 0;
  border-radius: 50%;
  background: linear-gradient(180deg, #3d8cff 0%, #1a6cff 40%, #0d5aed 100%);
  border: 5px solid #000000;
  box-shadow: 0 10px 26px rgba(13, 90, 237, 0.45);
  outline: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer :deep(.dock-tabbar .van-tabbar-item:nth-child(3) .van-tabbar-item__text) {
  margin-top: 4px;
}

.tab-label {
  font-size: 11px;
  color: rgba(235, 235, 245, 0.82) !important;
  font-weight: 500;
}

.tab-label.active {
  color: #3d8cff !important;
  font-weight: 600;
}

.tab-label--fab.active {
  color: #3d8cff !important;
}

.fab-wrap {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tab-icon {
  width: 22px;
  height: 22px;
  fill: none;
  stroke: rgba(235, 235, 245, 0.88);
  stroke-width: 1.55;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.tab-icon--coins {
  stroke-width: 1.45;
}

.footer :deep(.van-tabbar-item--active) .tab-icon:not(.fab-icon) {
  stroke: #3d8cff;
}

.fab-icon {
  width: 25px;
  height: 25px;
  stroke: #ffffff;
  fill: none;
  stroke-width: 1.45;
}

.footer :deep(.van-tabbar-item--active) .fab-icon {
  stroke: #fff;
}
</style>
