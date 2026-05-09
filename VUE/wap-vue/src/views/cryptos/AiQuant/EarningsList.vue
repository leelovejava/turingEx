<template>
  <div id="cryptos" class="earnings-list-page">
    <assets-head :title="t('aiQuantEarningsListTitle')" :backFunc="goBack" />

    <div class="earnings-list-shell">
      <div
        v-for="item in earningsRecords"
        :key="item.id"
        class="earnings-card"
      >
        <div class="earnings-cell">
          <span class="earnings-label">{{ t('aiQuantEarningsName') }}</span>
          <span class="earnings-value">{{ item.name }}</span>
        </div>
        <div class="earnings-cell">
          <span class="earnings-label">{{ t('aiQuantEarningsPair') }}</span>
          <span class="earnings-value">{{ item.pair }}</span>
        </div>
        <div class="earnings-cell">
          <span class="earnings-label">{{ t('aiQuantEarningsQuantity') }}</span>
          <span class="earnings-value">{{ item.quantity }}</span>
        </div>
        <div class="earnings-cell">
          <span class="earnings-label">{{ t('aiQuantEarningsPnl') }}</span>
          <span class="earnings-value" :class="pnlClass(item.pnl)">{{ formatPnl(item.pnl) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import assetsHead from '@/components/Transform/assets-head/index.vue'
import { earningsRecords } from './earningsMock'

defineOptions({ name: 'AiQuantEarningsListPage' })

const router = useRouter()
const { t } = useI18n()

function goBack() {
  router.push({ path: '/cryptos/aiQuant', query: { tab: 'myAi' } })
}

function formatPnl(v) {
  if (typeof v !== 'number') return String(v)
  return v >= 0 ? `${v}` : `${v}`
}

function pnlClass(v) {
  if (typeof v !== 'number') return ''
  if (v > 0) return 'pnl-pos'
  if (v < 0) return 'pnl-neg'
  return ''
}
</script>

<style lang="scss" scoped>
.earnings-list-page {
  min-height: 100vh;
  background: $main_background;
  padding-bottom: 32px;
  box-sizing: border-box;
}

.earnings-list-shell {
  width: 100%;
  max-width: 920px;
  margin: 0 auto;
  padding: 16px 16px 24px;
  box-sizing: border-box;
}

.earnings-card {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px 12px;
  padding: 20px 18px;
  margin-bottom: 14px;
  border: 1px solid $border_color;
  border-radius: 14px;
  background: $main2_background;
  text-align: left;
  box-sizing: border-box;
}

.earnings-cell {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}

.earnings-label {
  font-size: 24px;
  color: $text_color1;
}

.earnings-value {
  font-size: 28px;
  font-weight: 600;
  color: $text_color;
}

.earnings-value.pnl-pos {
  color: $green;
}

.earnings-value.pnl-neg {
  color: $red;
}

@media (min-width: 1024px) {
  .earnings-list-shell {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 14px 16px;
    padding: 20px 20px 32px;
  }

  .earnings-card {
    margin-bottom: 0;
  }
}

@media (min-width: 1440px) {
  .earnings-list-shell {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>
