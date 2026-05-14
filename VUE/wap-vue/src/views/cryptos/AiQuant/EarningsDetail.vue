<template>
  <!-- AI量化收益详情页面 -->
  <div id="cryptos" class="earnings-detail-page">
    <!-- 头部组件：显示标题和返回按钮 -->
    <assets-head :title="t('aiQuantEarningsDetailTitle')" :backFunc="goBack" />

    <!-- 收益详情内容（有数据时显示） -->
    <div v-if="record" class="earnings-detail-shell">
      <!-- 顶部区域：品牌标识和赎回按钮 -->
      <div class="detail-top">
        <div class="detail-brand">
          <!-- 品牌Logo图标 -->
          <span class="detail-logo" aria-hidden="true">
            <svg viewBox="0 0 40 40" width="40" height="40" fill="none" stroke="currentColor">
              <circle cx="20" cy="20" r="18" stroke-width="2" />
              <path d="M12 20h16M20 12v16" stroke-width="2" stroke-linecap="round" />
            </svg>
          </span>
          <!-- 交易对/购买币种 -->
          <span class="detail-pair">{{ record.symbol || record.buyCurrency }}</span>
        </div>
        <!-- 赎回按钮（仅状态为进行中时显示） -->
        <button v-if="record.state === '1'" type="button" class="btn-redeem" @click="onRedeem">{{ t('aiQuantEarningsRedeem') }}</button>
      </div>

      <!-- 详情信息面板 -->
      <div class="detail-panel">
        <!-- 购买金额 -->
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsPurchaseAmount') }}</span>
          <span class="detail-value">{{ record.amount }}</span>
        </div>
        <!-- 开始时间 -->
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsStartTime') }}</span>
          <span class="detail-value">{{ record.earn_timeStr }}</span>
        </div>
        <!-- 结束时间 -->
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsEndTime') }}</span>
          <span class="detail-value">{{ record.stop_timeStr }}</span>
        </div>
        <!-- 累计收益(已产生收益) -->
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsProfit') }}</span>
          <span class="detail-value">{{ Number(record.total_income).toFixed(4) }}</span>
        </div>
        <!-- 预计总收益 -->
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsExpectedIncome') }}</span>
          <span class="detail-value">{{ record.expected_total_income }}</span>
        </div>
        <!-- 今日收益 -->
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantTodayEarnings') }}</span>
          <span class="detail-value">{{ record.day_income }}</span>
        </div>
        <!-- 倒计时天数 -->
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantCountdownDays') }}</span>
          <span class="detail-value">{{ record.days }}</span>
        </div>
        <!-- 收益状态（1=进行中，其他=已结束） -->
        <div class="detail-row">
          <span class="detail-label">{{ t('aiQuantEarningsStatus') }}</span>
          <span class="detail-value">{{ record.state === '1' ? t('aiQuantEarningsStatusActive') : t('aiQuantEarningsStatusStopped') }}</span>
        </div>
      </div>
    </div>

    <!-- 空状态（无数据时显示） -->
    <div v-else class="earnings-detail-empty">
      <p class="empty-text">{{ t('aiQuantEarningsNotFound') }}</p>
    </div>
  </div>
</template>

<script setup>
// 导入Vue组合式API
import { ref, onMounted } from 'vue'
// 导入路由相关
import { useRoute, useRouter } from 'vue-router'
// 导入国际化
import { useI18n } from 'vue-i18n'
// 导入vant提示组件
import { showToast } from 'vant'
// 导入头部组件
import assetsHead from '@/components/Transform/assets-head/index.vue'
// 导入矿机订单API
import { getMinerorder, ransomMachineProduct } from '@/service/financialManagement.api'

// 定义组件名称
defineOptions({ name: 'AiQuantEarningsDetailPage' })

// 获取路由实例
const route = useRoute()
// 获取路由导航实例
const router = useRouter()
// 获取国际化翻译函数
const { t } = useI18n()

// 收益订单详情记录
const record = ref(null)

/**
 * 组件挂载时执行
 */
onMounted(async () => {
  // 根据订单号获取矿机订单详情
  const res = await getMinerorder({ order_no: route.params.id })
  record.value = res || null
})

/**
 * 返回上一级页面（我的AI量化页面）
 */
function goBack() {
  router.push({ path: '/cryptos/aiQuant', query: { tab: 'myAi' } })
}

/**
 * 赎回收益
 */
async function onRedeem() {
  // 调用赎回API
  await ransomMachineProduct({ order_no: route.params.id })
  // 显示赎回成功提示
  showToast({ message: t('aiQuantEarningsRedeemToast'), position: 'middle' })
  // 返回我的AI量化页面
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
