<template>
  <!-- AI量化收益列表页面 -->
  <div id="cryptos" class="earnings-list-page">
    <!-- 头部组件：显示标题和返回按钮 -->
    <assets-head :title="t('aiQuantEarningsListTitle')" :backFunc="goBack" />

    <!-- 收益列表容器 -->
    <div class="earnings-list-shell">
      <div v-if="!records.length && !loading" class="earnings-empty">{{ t('aiQuantEarningsEmpty') }}</div>
      <!-- 遍历收益记录，渲染收益卡片 -->
      <div v-for="item in records" :key="item.id" class="earnings-card">
        <!-- 收益开始时间 -->
        <div class="earnings-cell">
          <span class="earnings-label">{{ t('aiQuantEarningsStartTime') }}</span>
          <span class="earnings-value">{{ formatDate(item.startTime) }}</span>
        </div>
        <!-- 收益结束时间 -->
        <div class="earnings-cell">
          <span class="earnings-label">{{ t('aiQuantEarningsEndTime') }}</span>
          <span class="earnings-value">{{ formatDate(item.endTime) }}</span>
        </div>
        <!-- 收益金额（根据正负显示不同颜色） -->
        <div class="earnings-cell">
          <span class="earnings-label">{{ t('aiQuantEarningsProfit') }}</span>
          <span class="earnings-value" :class="item.income >= 0 ? 'pnl-pos' : 'pnl-neg'">{{ item.income }}</span>
        </div>
        <!-- 收益状态（1=进行中，其他=已结束） -->
        <div class="earnings-cell">
          <span class="earnings-label">{{ t('aiQuantEarningsStatus') }}</span>
          <span class="earnings-value">{{ item.status === 1 ? t('aiQuantEarningsStatusActive') : t('aiQuantEarningsStatusStopped') }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 导入Vue组合式API
import { ref, onMounted, onUnmounted } from 'vue'
// 导入路由相关
import { useRoute, useRouter } from 'vue-router'
// 导入国际化
import { useI18n } from 'vue-i18n'
// 导入头部组件
import assetsHead from '@/components/Transform/assets-head/index.vue'
// 导入收益列表API
import { getMinerIncomeList } from '@/service/financialManagement.api'

// 定义组件名称
defineOptions({ name: 'AiQuantEarningsListPage' })

// 获取路由实例
const route = useRoute()
// 获取路由导航实例
const router = useRouter()
// 获取国际化翻译函数
const { t } = useI18n()

// 收益记录列表
const records = ref([])
// 当前页码
const page = ref(1)
// 总页数
const totalPages = ref(1)
// 加载状态
const loading = ref(false)

/**
 * 加载指定页码的收益数据
 * @param {number} pageNo - 页码
 */
async function loadPage(pageNo) {
  // 如果正在加载，直接返回
  if (loading.value) return
  // 设置加载状态为true
  loading.value = true
  // 调用API获取收益列表
  const res = await getMinerIncomeList(route.params.id, pageNo)
  // 处理返回数据：优先取records，否则判断是否为数组
  const data = res?.records ?? (Array.isArray(res) ? res : [])
  // 将新数据追加到列表
  records.value.push(...data)
  // 更新总页数
  totalPages.value = res?.pages ?? 1
  // 设置加载状态为false
  loading.value = false
}

/**
 * 组件挂载时执行
 */
onMounted(() => {
  // 加载第一页数据
  loadPage(1)
  // 添加滚动事件监听（用于下拉加载更多）
  window.addEventListener('scroll', onScroll)
})

/**
 * 组件卸载时执行
 */
onUnmounted(() => {
  // 移除滚动事件监听，防止内存泄漏
  window.removeEventListener('scroll', onScroll)
})

/**
 * 滚动事件处理函数：实现下拉加载更多
 */
function onScroll() {
  // 如果正在加载或已经是最后一页，直接返回
  if (loading.value || page.value >= totalPages.value) return
  // 获取滚动相关参数
  const { scrollTop, scrollHeight, clientHeight } = document.documentElement
  // 当滚动到距离底部100px时，加载下一页
  if (scrollTop + clientHeight >= scrollHeight - 100) {
    page.value++
    loadPage(page.value)
  }
}

/**
 * 返回上一页
 */
function goBack() {
  router.back()
}

/**
 * 格式化日期时间
 * @param {string|number} d - 日期字符串或时间戳
 * @returns {string} 格式化后的日期时间字符串
 */
function formatDate(d) {
  if (!d) return ''
  return new Date(d).toLocaleString()
}
</script>

<style lang="scss" scoped>
.earnings-empty {
  text-align: center;
  padding: 60px 0;
  color: $text_color1;
  font-size: 28px;
}

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
  border: 1px solid $line_color;
  border-radius: 14px;
  background: $main2_background;
  text-align: left;
  box-sizing: border-box;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04);
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
