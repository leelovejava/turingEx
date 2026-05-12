<template>
  <div id="cryptos" class="earnings-list-page">
    <assets-head :title="t('aiQuantEarningsListTitle')" :backFunc="goBack" />

    <div class="earnings-list-shell">
      <div v-for="item in records" :key="item.id" class="earnings-card">
        <div class="earnings-cell">
          <span class="earnings-label">{{ t('aiQuantEarningsStartTime') }}</span>
          <span class="earnings-value">{{ formatDate(item.startTime) }}</span>
        </div>
        <div class="earnings-cell">
          <span class="earnings-label">{{ t('aiQuantEarningsEndTime') }}</span>
          <span class="earnings-value">{{ formatDate(item.endTime) }}</span>
        </div>
        <div class="earnings-cell">
          <span class="earnings-label">{{ t('aiQuantEarningsProfit') }}</span>
          <span class="earnings-value" :class="item.income >= 0 ? 'pnl-pos' : 'pnl-neg'">{{ item.income }}</span>
        </div>
        <div class="earnings-cell">
          <span class="earnings-label">{{ t('aiQuantEarningsStatus') }}</span>
          <span class="earnings-value">{{ item.status === 1 ? t('aiQuantEarningsStatusActive') : t('aiQuantEarningsStatusStopped') }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import assetsHead from '@/components/Transform/assets-head/index.vue'
import { getMinerIncomeList } from '@/service/financialManagement.api'

defineOptions({ name: 'AiQuantEarningsListPage' })

const route = useRoute()
const router = useRouter()
const { t } = useI18n()

const records = ref([])
const page = ref(1)
const totalPages = ref(1)
const loading = ref(false)

async function loadPage(pageNo) {
  if (loading.value) return
  loading.value = true
  const res = await getMinerIncomeList(route.params.id, pageNo)
  const data = res?.records ?? (Array.isArray(res) ? res : [])
  records.value.push(...data)
  totalPages.value = res?.pages ?? 1
  loading.value = false
}

onMounted(() => {
  loadPage(1)
  window.addEventListener('scroll', onScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
})

function onScroll() {
  if (loading.value || page.value >= totalPages.value) return
  const { scrollTop, scrollHeight, clientHeight } = document.documentElement
  if (scrollTop + clientHeight >= scrollHeight - 100) {
    page.value++
    loadPage(page.value)
  }
}

function goBack() {
  router.back()
}

function formatDate(d) {
  if (!d) return ''
  return new Date(d).toLocaleString()
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
