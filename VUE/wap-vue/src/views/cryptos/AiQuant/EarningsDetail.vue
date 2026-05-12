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
          <span class="detail-pair">{{ record.symbol || record.buyCurrency }}</span>
        </div>
        <button v-if="record.state === '1'" type="button" class="btn-redeem" @click="onRedeem">{{ t('aiQuantEarningsRedeem') }}</button>
      </div>

      <div class="detail-panel">
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsPurchaseAmount') }}</span>
          <span class="detail-value">{{ record.amount }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsStartTime') }}</span>
          <span class="detail-value">{{ record.earn_timeStr }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsEndTime') }}</span>
          <span class="detail-value">{{ record.stop_timeStr }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsProfit') }}</span>
          <span class="detail-value">{{ record.total_income }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantTodayEarnings') }}</span>
          <span class="detail-value">{{ record.day_income }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantCountdownDays') }}</span>
          <span class="detail-value">{{ record.days }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsStatus') }}</span>
          <span class="detail-value">{{ record.state === '1' ? t('aiQuantEarningsStatusActive') : t('aiQuantEarningsStatusStopped') }}</span>
        </div>
      </div>
    </div>

    <div v-else class="earnings-detail-empty">
      <p class="empty-text">{{ t('aiQuantEarningsNotFound') }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { showToast } from 'vant'
import assetsHead from '@/components/Transform/assets-head/index.vue'
import { getMinerorder, ransomMachineProduct } from '@/service/financialManagement.api'

defineOptions({ name: 'AiQuantEarningsDetailPage' })

const route = useRoute()
const router = useRouter()
const { t } = useI18n()

const record = ref(null)

onMounted(async () => {
  const res = await getMinerorder({ order_no: route.params.id })
  record.value = res || null
})

function goBack() {
  router.push({ path: '/cryptos/aiQuant', query: { tab: 'myAi' } })
}

async function onRedeem() {
  await ransomMachineProduct({ order_no: route.params.id })
  showToast({ message: t('aiQuantEarningsRedeemToast'), position: 'middle' })
  router.push({ path: '/cryptos/aiQuant', query: { tab: 'myAi' } })
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

.btn-income {
  flex-shrink: 0;
  padding: 14px 28px;
  border: 1px solid $btn_main;
  border-radius: 12px;
  background: transparent;
  color: $btn_main;
  font-size: 26px;
  font-weight: 600;
  cursor: pointer;
}

.detail-panel {
  background: $main2_background;
  border: 1px solid $line_color;
  border-radius: 14px;
  padding: 8px 18px 16px;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.06);
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
