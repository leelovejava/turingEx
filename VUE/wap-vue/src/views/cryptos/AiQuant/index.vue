<template>
  <div id="cryptos" class="ai-quant-page">
    <assets-head :title="t('aiQuantCopy')" :backFunc="goBackQuotes" />

    <div class="sub-tabs">
      <button
        type="button"
        :class="['sub-tab', { active: mainTab === 'products' }]"
        @click="mainTab = 'products'"
      >
        {{ t('aiQuantProducts') }}
      </button>
      <button
        type="button"
        :class="['sub-tab', { active: mainTab === 'myAi' }]"
        @click="mainTab = 'myAi'"
      >
        {{ t('aiQuantMyAi') }}
      </button>
    </div>

    <template v-if="mainTab === 'products'">
      <div class="products-layout">
        <aside class="pair-sidebar">
          <button
            v-for="p in pairs"
            :key="p.id"
            type="button"
            :class="['pair-item', { active: activePairId === p.id }]"
            @click="activePairId = p.id"
          >
            {{ p.label }}
          </button>
        </aside>
        <main class="strategy-main">
          <div
            v-for="s in activeStrategies"
            :key="s.id"
            class="strategy-card"
          >
            <div class="strategy-card-head">
              <span class="strategy-name">{{ t(s.nameKey) }}</span>
              <button type="button" class="btn-add" @click="openDeposit(s)">
                {{ t('aiQuantAdd') }}
              </button>
            </div>
            <div class="strategy-divider" />
            <div class="strategy-rows">
              <div class="row">
                <span class="label">{{ t('aiQuantEstimatedDaily') }}</span>
                <span class="value">{{ s.earningsRange }}</span>
              </div>
              <div class="row">
                <span class="label">{{ t('aiQuantCycle') }}</span>
                <span class="value">{{ s.cycle }}</span>
              </div>
              <div class="row">
                <span class="label">{{ t('aiQuantServiceFee') }}</span>
                <span class="value">{{ s.fee }}</span>
              </div>
              <div class="row">
                <span class="label">{{ t('aiQuantUsageCount') }}</span>
                <span class="value">{{ s.usage }}</span>
              </div>
            </div>
          </div>
        </main>
      </div>
    </template>

    <template v-else>
      <div class="my-ai-wrap">
        <div class="dashboard-card">
          <span class="badge">{{ t('aiQuantStrategyCustomization') }}</span>
          <div class="metrics">
            <div class="metric">
              <div class="metric-num">300</div>
              <div class="metric-label">{{ t('aiQuantPurchaseAmount') }}</div>
            </div>
            <div class="metric">
              <div class="metric-num">23.7032</div>
              <div class="metric-label">{{ t('aiQuantTodayEarnings') }}</div>
            </div>
            <div class="metric">
              <div class="metric-num">0</div>
              <div class="metric-label">{{ t('aiQuantProfitAssets') }}</div>
            </div>
            <div class="metric">
              <div class="metric-num">2</div>
              <div class="metric-label">{{ t('aiQuantQuantifiableDays') }}</div>
            </div>
            <div class="metric">
              <div class="metric-num">0</div>
              <div class="metric-label">{{ t('aiQuantCountdownDays') }}</div>
            </div>
          </div>
        </div>
        <div class="my-ai-actions">
          <button type="button" class="pill-btn" @click="goEarnings">{{ t('aiQuantIncome') }}</button>
          <button type="button" class="pill-btn" @click="goMyAiDetail">{{ t('aiQuantDetails') }}</button>
        </div>
      </div>
    </template>

    <van-popup
      v-model:show="depositOpen"
      position="center"
      round
      :close-on-click-overlay="true"
      class="ai-quant-deposit-popup-root"
      teleport="body"
    >
      <div class="deposit-sheet">
        <div class="deposit-title">{{ t('aiQuantDeposit') }}</div>
        <div class="deposit-row deposit-asset">
          <span class="asset-dot" aria-hidden="true" />
          <span class="asset-pair">{{ depositContext.pairLabel }}</span>
        </div>
        <div class="deposit-row">
          <span class="dep-label">{{ t('aiQuantExpected') }}</span>
          <span class="dep-value">{{ depositContext.earningsRange }}</span>
        </div>
        <div class="deposit-hr" />
        <div class="deposit-row deposit-stepper-row">
          <span class="dep-label">{{ t('aiQuantPeriodDays') }}</span>
          <div class="stepper">
            <button type="button" class="step-btn" @click="periodDays = Math.max(1, periodDays - 1)">−</button>
            <span class="step-val">{{ periodDays }}</span>
            <button type="button" class="step-btn" @click="periodDays += 1">+</button>
          </div>
        </div>
        <div class="deposit-hr" />
        <div class="deposit-row deposit-amount-row">
          <span class="dep-label">{{ t('aiQuantAmount') }}</span>
          <div class="amount-wrap">
            <input v-model.number="amount" type="number" class="amount-input" inputmode="decimal" />
            <button type="button" class="btn-max" @click="amount = maxAmount">{{ t('aiQuantMax') }}</button>
          </div>
        </div>
        <div class="deposit-footer">
          <button type="button" class="link-cancel" @click="depositOpen = false">{{ t('aiQuantCancel') }}</button>
          <button type="button" class="link-confirm" @click="onConfirmDeposit">{{ t('aiQuantConfirm') }}</button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { showToast } from 'vant'
import assetsHead from '@/components/Transform/assets-head/index.vue'

defineOptions({ name: 'AiQuantPage' })

const router = useRouter()
const route = useRoute()
const { t } = useI18n()

const mainTab = ref('products')

function goBackQuotes() {
  router.push('/quotes/index?tabActive=0')
}

function goEarnings() {
  router.push('/cryptos/aiQuant/earnings')
}

/** 与「我的 AI」当前持仓一致的演示详情，对应 earningsMock id */
function goMyAiDetail() {
  router.push('/cryptos/aiQuant/earnings/1')
}

function syncMyAiTab() {
  if (route.query.tab === 'myAi') {
    mainTab.value = 'myAi'
  }
}

onMounted(syncMyAiTab)
watch(
  () => route.query.tab,
  () => {
    syncMyAiTab()
  }
)

const activePairId = ref('btc')

const pairs = [
  { id: 'btc', label: 'BTC/USDT' },
  { id: 'eth', label: 'ETH/USDT' },
  { id: 'sol', label: 'SOL/USDT' },
]

const strategyCatalog = {
  btc: [
    {
      id: 'b1',
      nameKey: 'aiQuantStrategyReward',
      earningsRange: '3-5%',
      cycle: '3Day',
      fee: '0%',
      usage: '0',
    },
    {
      id: 'b2',
      nameKey: 'aiQuantStrategyCustomization',
      earningsRange: '4-5%',
      cycle: '1Day',
      fee: '0%',
      usageKey: 'unlimited',
    },
    {
      id: 'b3',
      nameKey: 'aiQuantStrategyBalanced',
      earningsRange: '5-6%',
      cycle: '1Day',
      fee: '0%',
      usageKey: 'unlimited',
    },
    {
      id: 'b4',
      nameKey: 'aiQuantStrategyGrowth',
      earningsRange: '6-8%',
      cycle: '1Day',
      fee: '0%',
      usageKey: 'unlimited',
    },
  ],
  eth: [
    {
      id: 'e1',
      nameKey: 'aiQuantStrategyReward',
      earningsRange: '2-4%',
      cycle: '3Day',
      fee: '0%',
      usage: '0',
    },
    {
      id: 'e2',
      nameKey: 'aiQuantStrategyBalanced',
      earningsRange: '4-6%',
      cycle: '1Day',
      fee: '0%',
      usageKey: 'unlimited',
    },
  ],
  sol: [
    {
      id: 's1',
      nameKey: 'aiQuantStrategyGrowth',
      earningsRange: '5-9%',
      cycle: '1Day',
      fee: '0%',
      usageKey: 'unlimited',
    },
  ],
}

const activeStrategies = computed(() => {
  const list = strategyCatalog[activePairId.value] || []
  return list.map((item) => ({
    ...item,
    usage: item.usageKey === 'unlimited' ? t('aiQuantUnlimitedPurchase') : item.usage,
  }))
})

const depositOpen = ref(false)
const periodDays = ref(3)
const amount = ref(100)
const maxAmount = ref(100000)
const depositContext = ref({
  pairLabel: 'BTC/USDT',
  earningsRange: '3-5%',
})

function parseCycleDays(cycleStr) {
  const n = parseInt(String(cycleStr).replace(/\D/g, ''), 10)
  return Number.isFinite(n) && n > 0 ? n : 3
}

function openDeposit(strategy) {
  const pair = pairs.find((p) => p.id === activePairId.value)
  depositContext.value = {
    pairLabel: pair?.label || '',
    earningsRange: strategy.earningsRange,
  }
  periodDays.value = parseCycleDays(strategy.cycle)
  amount.value = 100
  depositOpen.value = true
}

function onConfirmDeposit() {
  depositOpen.value = false
  showToast({ message: t('aiQuantSubmitOk'), position: 'middle' })
}
</script>

<style lang="scss" scoped>
.ai-quant-page {
  min-height: 100vh;
  padding-bottom: 32px;
  box-sizing: border-box;
  background: $main_background;
}

.sub-tabs {
  display: flex;
  justify-content: space-around;
  gap: 40px;
  padding: 8px 24px 0;
  border-bottom: 1px solid $line_color;
}

.sub-tab {
  flex: 0 0 auto;
  padding: 20px 0 16px;
  background: none;
  border: none;
  font-size: 30px;
  color: $text_color1;
  position: relative;
  cursor: pointer;
}

.sub-tab.active {
  color: $text_color;
  font-weight: 600;
}

.sub-tab.active::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 6px;
  background: $btn_main;
  border-radius: 3px 3px 0 0;
}

.products-layout {
  display: flex;
  align-items: stretch;
  min-height: 400px;
}

.pair-sidebar {
  width: 200px;
  flex-shrink: 0;
  padding: 16px 0;
  background: $tab_background;
  border-right: 1px solid $line_color;
}

.pair-item {
  display: block;
  width: 100%;
  padding: 24px 16px;
  text-align: left;
  border: none;
  background: transparent;
  color: $text_color;
  font-size: 28px;
  cursor: pointer;
}

.pair-item.active {
  background: $btn_main;
  color: $white;
  font-weight: 600;
}

.strategy-main {
  flex: 1;
  padding: 16px 16px 24px;
  min-width: 0;
}

.strategy-card {
  border: 1px solid $border_color;
  border-radius: 12px;
  padding: 0;
  margin-bottom: 20px;
  background: $main2_background;
  overflow: hidden;
}

.strategy-card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
}

.strategy-name {
  font-size: 30px;
  font-weight: 600;
  color: $text_color;
}

.btn-add {
  padding: 12px 28px;
  border-radius: 8px;
  border: none;
  background: $btn_main;
  color: $white;
  font-size: 26px;
  font-weight: 600;
  cursor: pointer;
}

.strategy-divider {
  height: 1px;
  background: $line_color;
  margin: 0 24px;
}

.strategy-rows {
  padding: 16px 24px 24px;
}

.strategy-rows .row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 0;
  font-size: 26px;
}

.strategy-rows .label {
  color: $text_color1;
}

.strategy-rows .value {
  color: $text_color;
  font-weight: 500;
}

.my-ai-wrap {
  padding: 24px 16px;
}

.dashboard-card {
  position: relative;
  border-radius: 24px;
  padding: 48px 24px 32px;
  background: linear-gradient(145deg, #48b38e 0%, #3a9a78 100%);
  color: $white;
}

.badge {
  position: absolute;
  top: 20px;
  left: 20px;
  background: #e8c547;
  color: $black;
  font-size: 22px;
  font-weight: 700;
  padding: 8px 20px;
  border-radius: 999px;
}

.metrics {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28px 16px;
  margin-top: 36px;
  text-align: center;
}

.metric-num {
  font-size: 36px;
  font-weight: 700;
  line-height: 1.2;
}

.metric-label {
  font-size: 22px;
  opacity: 0.92;
  margin-top: 8px;
  line-height: 1.3;
}

.my-ai-actions {
  display: flex;
  gap: 16px;
  margin-top: 24px;
}

.pill-btn {
  flex: 1;
  padding: 22px 16px;
  border-radius: 999px;
  border: none;
  background: linear-gradient(145deg, #48b38e 0%, #3a9a78 100%);
  color: $white;
  font-size: 28px;
  font-weight: 600;
  cursor: pointer;
}

@media (min-width: 1024px) {
  .strategy-main {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
    align-content: start;
  }

  .strategy-card {
    margin-bottom: 0;
  }

  .pair-sidebar {
    width: 220px;
  }
}

@media (min-width: 1440px) {
  .strategy-main {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>

<style lang="scss">
.ai-quant-deposit-popup-root.van-popup {
  width: 90%;
  max-width: 620px;
  background: transparent;
  overflow: visible;
}

.deposit-sheet {
  background: $main_background;
  border: 1px solid $border_color;
  border-radius: 16px;
  padding: 32px 28px 24px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.35);
}

.deposit-title {
  text-align: center;
  font-size: 32px;
  font-weight: 600;
  color: $text_color;
  margin-bottom: 28px;
}

.deposit-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 0;
  font-size: 28px;
  color: $text_color;
}

.deposit-asset {
  justify-content: flex-start;
  gap: 12px;
}

.asset-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: $btn_main;
  flex-shrink: 0;
}

.asset-pair {
  font-weight: 600;
}

.dep-label {
  color: $text_color1;
}

.dep-value {
  font-weight: 600;
  color: $text_color;
}

.deposit-hr {
  height: 1px;
  background: $line_color;
}

.stepper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.step-btn {
  width: 56px;
  height: 52px;
  border-radius: 8px;
  border: 1px solid $border_color;
  background: $input_background;
  color: $text_color;
  font-size: 32px;
  line-height: 1;
  cursor: pointer;
}

.step-val {
  min-width: 48px;
  text-align: center;
  font-weight: 600;
  color: $text_color;
}

.amount-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
}

.amount-input {
  width: 120px;
  border: none;
  background: transparent;
  color: $text_color;
  font-size: 30px;
  text-align: right;
}

.btn-max {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  background: #06cda5;
  color: $white;
  font-size: 24px;
  font-weight: 700;
  cursor: pointer;
}

.deposit-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 28px;
  padding-top: 8px;
}

.link-cancel {
  background: none;
  border: none;
  color: $text_color1;
  font-size: 30px;
  cursor: pointer;
}

.link-confirm {
  background: none;
  border: none;
  color: $red;
  font-size: 30px;
  font-weight: 600;
  cursor: pointer;
}
</style>
