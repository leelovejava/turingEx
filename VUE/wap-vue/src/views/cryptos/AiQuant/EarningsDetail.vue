<template>
  <div id="cryptos" class="earnings-detail-page">
    <assets-head :title="t('aiQuantEarningsDetailTitle')" :backFunc="goBack" />

    <div v-if="record" class="earnings-detail-shell">
      <div class="detail-top">
        <div class="detail-brand">
          <span class="detail-logo" aria-hidden="true">
            <svg viewBox="0 0 40 40" width="40" height="40" fill="none" stroke="currentColor">
              <circle cx="20" cy="20" r="18" stroke-width="2" />
              <path d="M12 20h16M20 12v16" stroke-width="2" stroke-linecap="round" />
            </svg>
          </span>
          <span class="detail-pair">{{ record.pair }}</span>
        </div>
        <button type="button" class="btn-redeem" @click="onRedeem">{{ t('aiQuantEarningsRedeem') }}</button>
      </div>

      <div class="detail-panel">
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsPurchaseAmount') }}</span>
          <span class="detail-value">{{ record.purchaseAmount }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsStartTime') }}</span>
          <span class="detail-value">{{ record.startTime }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsEndTime') }}</span>
          <span class="detail-value">{{ record.endTime }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsTradeCount') }}</span>
          <span class="detail-value">{{ record.tradeCount }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsAvgPrice') }}</span>
          <span class="detail-value">{{ record.avgPrice }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsProfit') }}</span>
          <span class="detail-value">{{ record.profit }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsStatus') }}</span>
          <span class="detail-value">{{ t(record.statusKey) }}</span>
        </div>
      </div>
    </div>

    <div v-else class="earnings-detail-empty">
      <p class="empty-text">{{ t('aiQuantEarningsNotFound') }}</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { showToast } from 'vant'
import assetsHead from '@/components/Transform/assets-head/index.vue'
import { getEarningsById } from './earningsMock'

defineOptions({ name: 'AiQuantEarningsDetailPage' })

const route = useRoute()
const router = useRouter()
const { t } = useI18n()

const record = computed(() => getEarningsById(route.params.id))

function goBack() {
  router.push({ path: '/cryptos/aiQuant', query: { tab: 'myAi' } })
}

function onRedeem() {
  showToast({ message: t('aiQuantEarningsRedeemToast'), position: 'middle' })
}
</script>

<style lang="scss" scoped>
.earnings-detail-page {
  min-height: 100vh;
  background: $main_background;
  padding-bottom: 40px;
  box-sizing: border-box;
}

.earnings-detail-shell {
  width: 100%;
  max-width: 720px;
  margin: 0 auto;
  padding: 16px 16px 24px;
  box-sizing: border-box;
}

.detail-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.detail-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.detail-logo {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 14px;
  color: $btn_main;
  background: rgba(22, 120, 255, 0.12);
}

.detail-pair {
  font-size: 32px;
  font-weight: 700;
  color: $text_color;
}

.btn-redeem {
  flex-shrink: 0;
  padding: 14px 28px;
  border: none;
  border-radius: 12px;
  background: $btn_main;
  color: $main-btn-color;
  font-size: 26px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(22, 120, 255, 0.25);
}

.btn-redeem:active {
  opacity: 0.9;
}

.detail-panel {
  background: $main2_background;
  border: 1px solid $border_color;
  border-radius: 14px;
  padding: 8px 18px 16px;
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.04),
    0 4px 16px rgba(0, 0, 0, 0.12);
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  padding: 18px 0;
  border-bottom: 1px solid $line_color;

  &:last-child {
    border-bottom: none;
  }
}

.detail-label {
  font-size: 26px;
  color: $text_color1;
  flex-shrink: 0;
}

.detail-value {
  font-size: 26px;
  font-weight: 500;
  color: $text_color;
  text-align: right;
  word-break: break-all;
}

.earnings-detail-empty {
  padding: 48px 24px;
  text-align: center;
}

.empty-text {
  font-size: 28px;
  color: $text_color1;
}

@media (min-width: 1024px) {
  .earnings-detail-shell {
    max-width: 760px;
    padding-top: 24px;
  }

  .detail-panel {
    padding: 12px 24px 24px;
  }
}
</style>
