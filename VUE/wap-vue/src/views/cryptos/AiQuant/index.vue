<template>
  <!-- AI量化页面根容器 -->
  <div id="cryptos" class="ai-quant-page">
    <!-- 顶部导航栏，标题取国际化文本，点击返回跳转行情首页 -->
    <assets-head :title="t('aiQuantCopy')" :backFunc="goBackQuotes" />

    <!-- 主 Tab 切换：产品列表 / 我的AI -->
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

    <!-- 产品列表 Tab 内容 -->
    <template v-if="mainTab === 'products'">
      <div class="products-layout">
        <!-- 左侧币种筛选栏，按 buy_currency 去重生成 -->
        <aside class="pair-sidebar">
          <button
            v-for="p in pairs"
            :key="p.id"
            type="button"
            :class="['pair-item', { active: (activePairId || pairs[0]?.id) === p.id }]"
            @click="activePairId = p.id"
          >
            {{ p.label }}
          </button>
        </aside>
        <!-- 右侧矿机产品卡片列表，展示当前选中币种的产品 -->
        <main class="strategy-main">
          <div
            v-for="s in activeStrategies"
            :key="s.uuid"
            class="strategy-card"
          >
            <!-- 卡片头部：产品名称（多语言）+ 新增按钮 -->
            <div class="strategy-card-head">
              <span class="strategy-name">{{ $i18n.locale === 'zh-CN' ? s.name : (s.name_en || s.name) }}</span>
              <button type="button" class="btn-add" @click="openDeposit(s)">
                {{ t('aiQuantAdd') }}
              </button>
            </div>
            <div class="strategy-divider" />
            <!-- 卡片详情行：预计日收益 / 周期 / 投资限额 -->
            <div class="strategy-rows">
              <!-- 预计日收益：优先展示区间值，否则展示单一日利率 -->
              <div class="row">
                <span class="label">{{ t('aiQuantEstimatedDaily') }}</span>
                <span class="value">{{ s.show_daily_rate_start && s.show_daily_rate_end ? s.show_daily_rate_start + '-' + s.show_daily_rate_end + '%' : s.daily_rate + '%' }}</span>
              </div>
              <!-- 周期：cycle=0 表示无限期 -->
              <div class="row">
                <span class="label">{{ t('aiQuantCycle') }}</span>
                <span class="value">{{ s.cycle === 0 ? t('aiQuantUnlimitedPurchase') : s.cycle + t('天') }}</span>
              </div>
              <!-- 投资限额：最低-最高 + 购买币种 -->
              <div class="row">
                <span class="label">{{ t('aiQuantServiceFee') }}</span>
                <span class="value">{{ s.investment_min + '-' + s.investment_max }} {{ (s.buy_currency || 'USDT').toUpperCase() }}</span>
              </div>
            </div>
          </div>
        </main>
      </div>
    </template>

    <!-- 我的AI Tab 内容 -->
    <template v-else>
      <div class="my-ai-wrap">
        <div v-for="o in myAiOrders" :key="o.order_no" class="strategy-card">
          <div class="strategy-card-head">
            <span class="strategy-name">{{ $i18n.locale === 'zh-CN' ? (o.miner_name_cn || o.miner_name) : (o.miner_name_en || o.miner_name) }}</span>
            <span :class="['order-state-badge', o.state === '1' ? 'state-active' : 'state-stopped']">
              {{ o.state === '1' ? t('aiQuantEarningsStatusActive') : t('aiQuantEarningsStatusStopped') }}
            </span>
          </div>
          <div class="strategy-divider" />
          <div class="strategy-rows">
            <div class="row">
              <span class="label">{{ t('aiQuantPurchaseAmount') }}</span>
              <span class="value">{{ o.amount }}</span>
            </div>
            <div class="row">
              <span class="label">{{ t('aiQuantTodayEarnings') }}</span>
              <span class="value">{{ o.day_income }}</span>
            </div>
            <div class="row">
              <span class="label">{{ t('aiQuantProfitAssets') }}</span>
              <span class="value">{{ o.total_income }}</span>
            </div>
            <div class="row">
              <span class="label">{{ t('aiQuantQuantifiableDays') }}</span>
              <span class="value">{{ o.quantifiable_days }}</span>
            </div>
            <div class="row">
              <span class="label">{{ t('aiQuantCountdownDays') }}</span>
              <span class="value">{{ o.countdown }}</span>
            </div>
          </div>
          <div class="my-ai-actions">
            <button type="button" class="pill-btn" @click="goEarnings(o.order_no)">{{ t('aiQuantIncome') }}</button>
            <button type="button" class="pill-btn" @click="goMyAiDetail(o.order_no)">{{ t('aiQuantDetails') }}</button>
          </div>
        </div>
      </div>
    </template>

    <!-- 认购弹窗：点击产品卡片「新增」按钮触发 -->
    <van-popup
      v-model:show="depositOpen"
      position="center"
      round
      :close-on-click-overlay="true"
      class="ai-quant-deposit-popup-root"
      teleport="body"
    >
      <div class="deposit-sheet">
        <!-- 弹窗标题 -->
        <div class="deposit-title">{{ t('aiQuantDeposit') }}</div>
        <!-- 当前选中的币种 -->
        <div class="deposit-row deposit-asset">
          <span class="asset-dot" aria-hidden="true" />
          <span class="asset-pair">{{ depositContext.pairLabel }}</span>
        </div>
        <!-- 预期收益区间 -->
        <div class="deposit-row">
          <span class="dep-label">{{ t('aiQuantExpected') }}</span>
          <span class="dep-value">{{ depositContext.earningsRange }}</span>
        </div>
        <div class="deposit-hr" />
        <!-- 周期天数步进器：点击 −/+ 调整天数，最小为 1 -->
        <div class="deposit-row deposit-stepper-row">
          <span class="dep-label">{{ t('aiQuantPeriodDays') }}</span>
          <div class="stepper">
            <button type="button" class="step-btn" @click="periodDays = Math.max(1, periodDays - 1)">−</button>
            <span class="step-val">{{ periodDays }}</span>
            <button type="button" class="step-btn" @click="periodDays += 1">+</button>
          </div>
        </div>
        <div class="deposit-hr" />
            <!-- 认购金额输入框，点击 MAX 填入可用余额（不超过最大投资额） -->
        <div class="deposit-row deposit-amount-row">
          <span class="dep-label">{{ t('aiQuantAmount') }}</span>
          <div class="amount-wrap">
            <input v-model.number="amount" type="number" class="amount-input" inputmode="decimal" />
            <button type="button" class="btn-max" @click="amount = Math.min(balance, maxAmount)">{{ t('aiQuantMax') }}</button>
          </div>
        </div>
        <!-- 底部操作：取消 / 确认认购 -->
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
import { getMachineList, machineMakeOrder, confirmMichineOrder, getMachineBought } from '@/service/financialManagement.api'
import { _getAllWallet } from '@/service/fund.api'

defineOptions({ name: 'AiQuantPage' })

const router = useRouter()
const route = useRoute()
const { t } = useI18n()

// 当前主 Tab：products=产品列表，myAi=我的AI
const mainTab = ref('products')

// 返回行情首页
function goBackQuotes() {
  router.push('/quotes/index?tabActive=0')
}

// 跳转收益列表页
function goEarnings(orderNo) {
  if (orderNo) router.push('/cryptos/aiQuant/earnings/' + orderNo + '/income')
}

// 跳转我的AI持仓详情页
function goMyAiDetail(orderNo) {
  const target = orderNo || myAiOrders.value[0]?.order_no
  if (target) router.push('/cryptos/aiQuant/earnings/' + target)
}

// 根据路由参数 tab=myAi 自动切换到「我的AI」tab
function syncMyAiTab() {
  if (route.query.tab === 'myAi') {
    mainTab.value = 'myAi'
  }
}

// 页面挂载时：同步 tab 状态 + 加载矿机产品列表 + 加载我的AI统计
onMounted(() => {
  syncMyAiTab()
  loadProducts()
  loadMyAiStats()
})

// 监听路由 tab 参数变化，动态切换 tab
watch(
  () => route.query.tab,
  () => { syncMyAiTab() }
)

// 我的AI统计数据
const myAiStats = ref({ amount_sum: 0, today_profit: 0, aready_profit: 0, order_sum: 0, countdown_days: 0 })
// 我的AI订单列表
const myAiOrders = ref([])

async function loadMyAiStats() {
  const res = await getMachineBought()
  const orders = Array.isArray(res) ? res : (res?.data || [])
  myAiOrders.value = orders
  myAiStats.value.amount_sum = orders.reduce((s, o) => s + (Number(o.amount) || 0), 0)
  myAiStats.value.today_profit = orders.reduce((s, o) => s + (Number(o.day_income) || 0), 0)
  myAiStats.value.aready_profit = orders.reduce((s, o) => s + (Number(o.total_income) || 0), 0)
  myAiStats.value.order_sum = orders.length
  const minCountdown = orders.reduce((min, o) => {
    const d = Number(o.countdown)
    return d < min ? d : min
  }, Infinity)
  myAiStats.value.countdown_days = isFinite(minCountdown) ? Math.max(0, minCountdown) : 0
}

// 矿机产品列表（从接口获取）
const products = ref([])

// 调用矿机产品列表接口，兼容数组和 {data:[]} 两种返回格式
async function loadProducts() {
  const res = await getMachineList()
  products.value = Array.isArray(res) ? res : (res?.data || [])
}

// 当前选中的购买币种 id（左侧 tab），空字符串时默认取第一个
const activePairId = ref('')

// 左侧币种 tab 固定
const pairs = [
  { id: 'btc', label: 'BTC/USDT' },
  { id: 'eth', label: 'ETH/USDT' },
  { id: 'sol', label: 'SOL/USDT' },
]

// 展示所有矿机产品（不按币种过滤）
const activeStrategies = computed(() => products.value)

// 认购弹窗是否显示
const depositOpen = ref(false)
// 认购周期（天）
const periodDays = ref(3)
// 认购金额
const amount = ref(100)
// 最大可认购金额（由产品 investment_max 决定）
const maxAmount = ref(100000)
// 当前购买币种的可用余额
const balance = ref(0)
// 当前产品的购买币种
const buyCurrency = ref('usdt')
// 当前选中的矿机产品 id
const currentMinerId = ref('')
// 当前选中的交易对，如 BTC/USDT
const currentSymbol = ref('')
// 弹窗展示的上下文信息（币种标签、预期收益区间）
const depositContext = ref({
  pairLabel: 'BTC/USDT',
  earningsRange: '3-5%',
})

// 点击「新增」按钮，打开认购弹窗并填充当前产品信息
async function openDeposit(strategy) {
  // 取当前选中币种，未选中时取第一个
  const cur = activePairId.value || pairs[0]?.id || ''
  const pair = pairs.find((p) => p.id === cur)
  // 优先使用展示利率区间，否则使用日利率
  const earningsRange = strategy.show_daily_rate_start && strategy.show_daily_rate_end
    ? strategy.show_daily_rate_start + '-' + strategy.show_daily_rate_end + '%'
    : strategy.daily_rate + '%'
  depositContext.value = {
    pairLabel: pair?.label || '',
    earningsRange,
  }
  // 周期：0 表示无限期，默认填 3 天
  periodDays.value = strategy.cycle > 0 ? strategy.cycle : 3
  // 默认金额取产品最低投资额
  amount.value = strategy.investment_min || 100
  maxAmount.value = strategy.investment_max || 100000
  buyCurrency.value = strategy.buy_currency || 'usdt'
  currentMinerId.value = strategy.id
  currentSymbol.value = pair?.label || ''
  // 获取 USDT 可用余额
  balance.value = 0
  const walletRes = await _getAllWallet()
  balance.value = walletRes?.usdt || 0
  depositOpen.value = true
}

// 确认认购：先调 getOpen 获取 session_token，再调 open 实际下单
async function onConfirmDeposit() {
  const preview = await machineMakeOrder({ minerId: currentMinerId.value, amount: amount.value, cycle: periodDays.value })
  await confirmMichineOrder({
    session_token: preview?.session_token,
    minerId: currentMinerId.value,
    amount: amount.value,
    symbol: currentSymbol.value,
    cycle: periodDays.value,
  })
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

.order-state-badge {
  font-size: 22px;
  font-weight: 600;
  padding: 6px 16px;
  border-radius: 999px;
}

.state-active {
  background: rgba(6, 205, 165, 0.15);
  color: #06cda5;
}

.state-stopped {
  background: rgba(150, 150, 150, 0.15);
  color: $text_color1;
}

.my-ai-actions {
  display: flex;
  gap: 16px;
  padding: 0 24px 24px;
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

.order-list {
  margin-top: 20px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 16px;
  background: $main2_background;
  border: 1px solid $border_color;
  border-radius: 12px;
  margin-bottom: 12px;
  cursor: pointer;
}

.order-item-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.order-symbol {
  font-size: 28px;
  font-weight: 600;
  color: $text_color;
}

.order-no {
  font-size: 22px;
  color: $text_color1;
}

.order-item-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.order-amount {
  font-size: 28px;
  font-weight: 600;
  color: $text_color;
}

.order-countdown {
  font-size: 22px;
  color: $text_color1;
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
