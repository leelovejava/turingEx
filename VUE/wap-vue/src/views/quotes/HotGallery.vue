<template>
  <section class="pb-fix hot-gallery">
    <div class="hot-scroll">
      <div class="hero-card">
        <div class="hero-glow" />
        <div class="hero-beam" />
        <div class="hero-grid-bg" />
        <div class="hero-starfield" aria-hidden="true" />
        <div class="hero-topbar">
          <span class="hero-title">{{ t('热门') }}</span>
          <button type="button" class="hero-service" aria-label="service">
            <svg class="svc-ico" viewBox="0 0 24 24" aria-hidden="true">
              <path d="M4 14v2a2 2 0 0 0 2 2h1v-6H6a2 2 0 0 0-2 2zM20 14v2a2 2 0 0 1-2 2h-1v-6h1a2 2 0 0 1 2 2z" />
              <path d="M7 14V9a5 5 0 0 1 10 0v5" />
            </svg>
          </button>
        </div>
        <div class="hero-brand-block">
          <h1 class="hero-main-title">
            <span class="hero-brand">TuringEx</span><span class="hero-title-rest"> {{ t('投资者基金') }}</span>
          </h1>
        </div>
        <div class="hero-stats">
          <div class="hero-stat">
            <span class="hero-stat-label">{{ t('关联账户') }}</span>
            <span class="hero-stat-num">2514952<span class="plus"> +</span></span>
          </div>
          <div class="hero-stat-divider" aria-hidden="true" />
          <div class="hero-stat">
            <span class="hero-stat-label">{{ t('订单执行') }}</span>
            <span class="hero-stat-num">656082<span class="plus"> +</span></span>
          </div>
        </div>
      </div>

      <div class="block card-block grid-card">
        <svg class="icon-sprite" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
          <defs>
            <symbol id="hot-ico-assets" viewBox="0 0 24 24">
              <rect x="4" y="8" width="14" height="10" rx="1.5" fill="none" stroke="currentColor" stroke-width="1.6" />
              <path d="M7 8V6.5A1.5 1.5 0 0 1 8.5 5h9A1.5 1.5 0 0 1 19 6.5V14" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
            </symbol>
            <symbol id="hot-ico-flash" viewBox="0 0 24 24">
              <circle cx="12" cy="12" r="9" fill="none" stroke="currentColor" stroke-width="1.6" />
              <path d="M13.5 6.5L8 13h4.5l-1.5 6.5L17 12h-4.5l1-5.5z" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round" />
            </symbol>
            <symbol id="hot-ico-news" viewBox="0 0 24 24">
              <path d="M6 4h11a2 2 0 0 1 2 2v14H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z" fill="none" stroke="currentColor" stroke-width="1.6" />
              <path d="M8 8h8M8 12h5" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              <rect x="14" y="3" width="7" height="4" rx="1" fill="currentColor" />
            </symbol>
            <symbol id="hot-ico-service" viewBox="0 0 24 24">
              <path d="M4 14v2a2 2 0 0 0 2 2h1v-6H6a2 2 0 0 0-2 2zM20 14v2a2 2 0 0 1-2 2h-1v-6h1a2 2 0 0 1 2 2z" fill="none" stroke="currentColor" stroke-width="1.6" />
              <path d="M7 14V9a5 5 0 0 1 10 0v5" fill="none" stroke="currentColor" stroke-width="1.6" />
            </symbol>
            <symbol id="hot-ico-ieo" viewBox="0 0 24 24">
              <rect x="3.5" y="5" width="17" height="12" rx="2" fill="none" stroke="currentColor" stroke-width="1.6" />
              <path d="M8 19h8" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              <text x="12" y="14.2" text-anchor="middle" font-size="6" font-weight="700" fill="currentColor" stroke="none">IEO</text>
            </symbol>
            <symbol id="hot-ico-quant" viewBox="0 0 24 24">
              <path d="M4 18V6l4 6 4-4 4 7 4-5v8H4z" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round" />
              <path d="M16 5l1.2 2.5L20 9l-2.8.5L16 12l-1.2-2.5L12 9l2.8-.5L16 5z" fill="none" stroke="currentColor" stroke-width="1.4" stroke-linejoin="round" />
            </symbol>
            <symbol id="hot-ico-loan" viewBox="0 0 24 24">
              <circle cx="9" cy="11" r="3.5" fill="none" stroke="currentColor" stroke-width="1.6" />
              <circle cx="15" cy="11" r="3.5" fill="none" stroke="currentColor" stroke-width="1.6" />
              <path d="M11.5 12.5c.8.6 1.8 1 3 1s2.2-.4 3-1" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
            </symbol>
            <symbol id="hot-ico-ipo" viewBox="0 0 24 24">
              <rect x="4" y="6" width="16" height="14" rx="2" fill="none" stroke="currentColor" stroke-width="1.6" />
              <path d="M4 10h16M9 6V4.5M15 6V4.5" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" />
              <text x="12" y="17" text-anchor="middle" font-size="5.5" font-weight="700" fill="currentColor" stroke="none">IPO</text>
            </symbol>
          </defs>
        </svg>
        <div class="icon-grid">
          <button
            v-for="item in homeGridItems"
            :key="item.key"
            type="button"
            class="icon-grid-item"
            @click="onGridClick(item)"
          >
            <span class="grid-icon-ring">
              <svg class="grid-ico-svg" viewBox="0 0 24 24" aria-hidden="true">
                <use :href="`#hot-ico-${item.key}`" />
              </svg>
            </span>
            <span class="round-label">{{ item.label }}</span>
          </button>
        </div>
      </div>

      <div class="block card-block leader-block">
        <div class="leader-head">
          <div class="leader-head-l">
            <svg class="leader-star-ico" viewBox="0 0 24 24" aria-hidden="true">
              <path
                d="M12 2.2l2.6 5.5 6.1.7-4.4 4.3 1 6.2L12 16.8 4.7 18.9l1-6.2L1.3 8.4l6.1-.7L12 2.2z"
                fill="url(#leaderStarGrad)"
              />
              <defs>
                <linearGradient id="leaderStarGrad" x1="12" y1="2" x2="12" y2="20" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#c4b5fd" />
                  <stop offset="0.5" stop-color="#8b5cf6" />
                  <stop offset="1" stop-color="#5b21b6" />
                </linearGradient>
              </defs>
            </svg>
            <span class="leader-head-txt">{{ t('前3名投资者') }}</span>
          </div>
        </div>
        <div class="podium-wrap">
          <div
            v-for="p in podium"
            :key="p.rank"
            class="podium-col"
            :class="`pr-${p.rank}`"
          >
            <div class="podium-pillar">
              <span class="podium-watermark" aria-hidden="true">{{ p.watermark }}</span>
              <div class="avatar-wrap" :class="`medal-${p.rank}`">
                <img v-if="p.avatar" class="avatar-img" :src="p.avatar" alt="">
                <span v-else class="avatar-inner" />
                <span class="badge-rank">{{ p.rank }}</span>
              </div>
              <div class="podium-mail-card">{{ maskEmail(p.mail) }}</div>
              <span class="podium-usd">{{ p.usd }} USD</span>
              <span class="podium-pct">{{ p.pct }}</span>
            </div>
          </div>
        </div>

        <div class="investor-list">
          <div v-for="row in investorMore" :key="row.rank" class="investor-row">
            <span class="inv-rank">{{ row.rank }}</span>
            <span class="inv-av" :class="`tone-${row.rank % 3}`" />
            <span class="inv-mail">{{ maskEmail(row.mail) }}</span>
            <div class="inv-right">
              <span class="inv-usd">{{ row.usd }} USD</span>
              <span class="inv-pct">{{ row.pct }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="block card-block news-block">
        <div class="news-head">
          <span class="news-title">{{ t('news') }}</span>
          <button type="button" class="news-more" @click="goNews">
            {{ t('更多') }} &gt;
          </button>
        </div>
        <div class="news-list">
          <article
            v-for="(item, idx) in displayNewsItems"
            :key="idx"
            class="news-card"
            @click="goNews"
          >
            <p class="news-text">{{ item.title }}</p>
            <div class="news-meta">
              <span class="news-time">{{ item.time }}</span>
              
            </div>
          </article>
        </div>
      </div>

      <div class="rank-board">
        <div class="rank-board-head">
          <span class="rank-board-title">{{ t('排行榜') }}</span>
          <button type="button" class="rank-board-more" @click="goRankMore">
            {{ t('更多') }} &gt;
          </button>
        </div>
        <div class="rank-pills ex-tabs-clone">
          <button type="button" class="rank-pill" :class="{ 'rank-pill--on': rankActive === 0 }" @click="rankActive = 0">Hot</button>
          <button type="button" class="rank-pill" :class="{ 'rank-pill--on': rankActive === 1 }" @click="rankActive = 1">Gainers</button>
          <button type="button" class="rank-pill" :class="{ 'rank-pill--on': rankActive === 2 }" @click="rankActive = 2">Losers</button>
          <button type="button" class="rank-pill" :class="{ 'rank-pill--on': rankActive === 3 }" @click="rankActive = 3">24h Vol</button>
        </div>
        <div class="rank-col-h">
          <span>{{ t('交易对') }}</span>
          <span class="rank-col-mid">{{ t('最新价') }}</span>
          <span class="rank-col-end">{{ rankActive === 3 ? t('24h成交额') : t('24h涨跌幅') }}</span>
        </div>
        <div class="rank-card-list">
          <ul
            v-for="(r, idx) in rankDisplayList"
            :key="r.symbol || idx"
            class="rank-quote-card"
            @click="goTrade(r)"
          >
            <div class="rq-left">
              <img class="rq-symbol" :src="symbolIcon(r.symbol)" alt="logo" />
              <div class="rq-pair-wrap">
                <p class="rq-pair">
                  <strong>{{ (r.symbol || '--').toUpperCase() }}</strong>/USDT
                </p>
                <p class="rq-vol">{{ t('成交量') }} {{ formatNum(r.amount) }}</p>
              </div>
            </div>
            <div class="rq-mid">
              <p class="rq-price">{{ r.close || '--' }}</p>
              <p class="rq-usd">$ {{ r.close || '--' }}</p>
            </div>
            <div class="rq-right">
              <template v-if="rankActive === 3">
                <span class="rq-chg rq-vol-tag">{{ formatNum(r.volume) }}</span>
              </template>
              <template v-else>
                <span class="rq-chg" :class="Number(r.changeRatio) >= 0 ? 'is-up' : 'is-down'">
                  {{ Number(r.changeRatio) >= 0 ? '+' : '' }}{{ r.changeRatio || 0 }}%
                </span>
              </template>
            </div>
          </ul>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { _getInformationList } from '@/service/etf.api'
import { _getRealtimeByType } from '@/service/quotes.api'
import { FILE_URL } from '@/config'

const userStore = useUserStore()
const { t } = useI18n()
const router = useRouter()

const rankActive = ref(0)
const newsItems = ref([])
const rankList = ref([])

const rankDisplayList = computed(() => {
  const list = [...rankList.value]
  if (rankActive.value === 1) return list.sort((a, b) => Number(b.changeRatio) - Number(a.changeRatio))
  if (rankActive.value === 2) return list.sort((a, b) => Number(a.changeRatio) - Number(b.changeRatio))
  if (rankActive.value === 3) return list.sort((a, b) => Number(b.volume) - Number(a.volume))
  return list
})

function onGridClick(item) {
  if (!item.path) return
  if (item.isLogin && !userStore.userInfo?.token) {
    router.push('/login')
    return
  }
  router.push(item.path)
}

/** 设计稿 4×2 宫格（线稿图标） */
const homeGridItems = computed(() => [
  { key: 'assets', label: t('资产管理'), path: '/cryptos/funds?type=cryptos', isLogin: true },
  { key: 'flash', label: t('闪兑'), path: '/cryptos/exchangePage', isLogin: true },
  { key: 'news', label: t('news'), path: '/news', isLogin: false },
      { key: 'service', label: t('客服'), path: '/workerOrder', isLogin: false },
  { key: 'ieo', label: 'IEO', path: '/more', isLogin: false },
  { key: 'quant', label: t('AI量化基金'), path: '/cryptos/fm-home', isLogin: false },
  { key: 'loan', label: t('助力贷'), path: '/cryptos/loan', isLogin: true },
  { key: 'ipo', label: 'IPO', path: '/ipo/index', isLogin: false },
])

/** 与设计稿一致：中 1、左 2、右 3；头像为占位图（可换成本地资源） */
const podium = [
  {
    rank: 2,
    mail: 'hyptfnrk@twitter.com',
    usd: '13,225,424',
    pct: '+19.0%',
    watermark: '02',
    avatar: 'https://picsum.photos/id/1027/200/200',
  },
  {
    rank: 1,
    mail: 'hudrmtw@amazon.com',
    usd: '14,618,127',
    pct: '+12.4%',
    watermark: '01',
    avatar: 'https://picsum.photos/id/1005/200/200',
  },
  {
    rank: 3,
    mail: 'glbtr***@gmail.com',
    usd: '13,199,244',
    pct: '+15.8%',
    watermark: '03',
    avatar: 'https://picsum.photos/id/1074/200/200',
  },
]

const investorMore = [
  { rank: 4, mail: 'jqtkzprn@bytedance.com', usd: '12,776,880', pct: '+17.4%' },
  { rank: 5, mail: 'xkqve***@exchange.io', usd: '12,100,200', pct: '+9.2%' },
  { rank: 6, mail: 'mnpqr@outlook.com', usd: '11,842,110', pct: '+8.1%' },
  { rank: 7, mail: 'stuvwx@proton.me', usd: '11,205,333', pct: '+6.5%' },
  { rank: 8, mail: 'alpha@chain.dev', usd: '10,980,000', pct: '+5.9%' },
  { rank: 9, mail: 'beta@node.net', usd: '10,455,777', pct: '+4.3%' },
]

const fallbackNewsItems = [
  {
    title: '【美的集团：拟分拆安得智联至港交所主板上市】公司公告披露相关筹划细节，市场关注供应链板块估值重构。',
    time: '2025-12-08 18:44:57',
  },
  {
    title: '【Global markets】Fed watch: yields edge lower ahead of CPI; crypto majors hold range.',
    time: '2025-12-08 17:12:03',
  },
  {
    title: '【TuringEx 投资者基金】季度策略说明：稳健型组合加仓高评级固收与量化对冲仓位。',
    time: '2025-12-08 15:30:21',
  },
]

const stripHtml = (text = '') => text.replace(/<[^>]*>/g, ' ').replace(/\s+/g, ' ').trim()

const maskEmail = (email = '') => {
  const value = String(email || '').trim()
  const atIndex = value.indexOf('@')
  if (atIndex <= 0) return value
  const name = value.slice(0, atIndex)
  const domain = value.slice(atIndex)
  if (name.length <= 2) return `${name[0] || ''}***${domain}`
  return `${name.slice(0, 2)}***${name.slice(-1)}${domain}`
}

const normalizeNewsItem = (item) => ({
  title: stripHtml(item.description || item.title || item.content || ''),
  time: item.createdAt || item.createTime || item.time || '',
})

const displayNewsItems = computed(() => (
  newsItems.value.length ? newsItems.value : fallbackNewsItems
))

const getInformationList = async () => {
  try {
    const data = await _getInformationList('')
    newsItems.value = (Array.isArray(data) ? data : [])
      .slice(0, 3)
      .map((item) => normalizeNewsItem(item))
      .filter(item => item.title)
  } catch (error) {
    console.error('getInformationList error:', error)
  }
}

const getRankList = async () => {
  try {
    const data = await _getRealtimeByType({ type: 'cryptos', pageNo: 1 })
    rankList.value = (Array.isArray(data) ? data : []).slice(0, 8)
  } catch (error) {
    rankList.value = []
  }
}

const formatNum = (value) => {
  const num = Number(value)
  if (Number.isNaN(num)) return '--'
  return num.toFixed(2)
}

const symbolIcon = (symbol = '') => {
  const name = String(symbol || '').toLowerCase()
  if (!name) return new URL('../../assets/loading-default.png', import.meta.url).href
  return `${FILE_URL}/symbol/${name}.png`
}

onMounted(() => {
  getInformationList()
  getRankList()
})

function goNews() {
  router.push('/news')
}

function goRankMore() {
  router.push('/quotes/index')
}

function goTrade(item) {
  if (!item?.symbol) return
  router.push(`/cryptos/trade/${item.symbol}`)
}
</script>

<style lang="scss" scoped>
.hot-gallery {
  min-height: 100vh;
  box-sizing: border-box;
  background:
    radial-gradient(ellipse 120% 80% at 50% -20%, rgba(106, 90, 205, 0.22) 0%, transparent 55%),
    radial-gradient(ellipse 90% 50% at 80% 40%, rgba(75, 0, 130, 0.12) 0%, transparent 45%),
    linear-gradient(180deg, #0a0b1a 0%, #070814 45%, #05060f 100%);
}

.hot-scroll {
  padding: 12px 14px calc(14px + env(safe-area-inset-bottom));
  padding-bottom: calc(14px + constant(safe-area-inset-bottom));
  box-sizing: border-box;
  max-width: 100%;
}

.hero-card {
  position: relative;
  border-radius: 20px;
  padding: 16px 16px 18px;
  overflow: hidden;
  background: linear-gradient(
    165deg,
    rgba(12, 14, 28, 0.97) 0%,
    rgba(18, 16, 40, 0.95) 38%,
    rgba(26, 20, 48, 0.92) 68%,
    rgba(10, 11, 26, 0.98) 100%
  );
  border: 1px solid rgba(106, 90, 205, 0.4);
  box-shadow:
    0 24px 56px rgba(0, 0, 0, 0.55),
    0 0 0 1px rgba(139, 92, 246, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.06);
  margin-bottom: 12px;
}

.hero-glow {
  position: absolute;
  inset: -25% -35% 35%;
  background: radial-gradient(
    ellipse 85% 75% at 50% 0%,
    rgba(139, 92, 246, 0.5) 0%,
    rgba(75, 0, 130, 0.25) 35%,
    rgba(59, 130, 246, 0.12) 55%,
    transparent 72%
  );
  pointer-events: none;
}

.hero-beam {
  position: absolute;
  left: 50%;
  top: 0;
  transform: translateX(-50%);
  width: 42%;
  height: 100%;
  background: linear-gradient(
    180deg,
    rgba(106, 90, 205, 0.35) 0%,
    rgba(56, 189, 248, 0.2) 35%,
    rgba(0, 242, 255, 0.12) 55%,
    rgba(75, 0, 130, 0.08) 78%,
    transparent 100%
  );
  pointer-events: none;
  filter: blur(14px);
  opacity: 0.9;
}

.hero-grid-bg {
  position: absolute;
  inset: 0;
  opacity: 0.14;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.04) 1px, transparent 1px);
  background-size: 18px 18px;
  pointer-events: none;
}

.hero-starfield {
  position: absolute;
  inset: 0;
  pointer-events: none;
  opacity: 0.35;
  background-image:
    radial-gradient(1px 1px at 10% 20%, rgba(255, 255, 255, 0.55) 50%, transparent 51%),
    radial-gradient(1px 1px at 75% 15%, rgba(255, 255, 255, 0.4) 50%, transparent 51%),
    radial-gradient(1px 1px at 32% 45%, rgba(200, 220, 255, 0.45) 50%, transparent 51%),
    radial-gradient(1px 1px at 88% 42%, rgba(255, 255, 255, 0.35) 50%, transparent 51%),
    radial-gradient(1px 1px at 55% 8%, rgba(255, 255, 255, 0.5) 50%, transparent 51%),
    radial-gradient(1px 1px at 20% 70%, rgba(180, 200, 255, 0.4) 50%, transparent 51%),
    radial-gradient(1px 1px at 92% 78%, rgba(255, 255, 255, 0.35) 50%, transparent 51%);
}

.hero-topbar {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.hero-title {
  font-size: 20px;
  font-weight: 700;
  color: #f8fafc;
}

.hero-service {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  cursor: pointer;
}

.svc-ico {
  width: 22px;
  height: 22px;
  fill: none;
  stroke: #f1f5f9;
  stroke-width: 1.5;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.hero-brand-block {
  position: relative;
  z-index: 1;
  margin-bottom: 18px;
  text-align: center;
}

.hero-main-title {
  margin: 0;
  font-size: inherit;
  font-weight: inherit;
  line-height: 1.35;
}

.hero-brand {
  font-family: Georgia, 'Times New Roman', ui-serif, serif;
  font-size: 27px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.03em;
  text-shadow:
    0 0 20px rgba(0, 242, 255, 0.45),
    0 0 40px rgba(106, 90, 205, 0.35);
}

.hero-title-rest {
  font-family: system-ui, -apple-system, 'Segoe UI', sans-serif;
  font-size: 16px;
  font-weight: 500;
  color: rgba(248, 250, 252, 0.95);
  letter-spacing: 0.02em;
}

.hero-stats {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: stretch;
  margin-bottom: 16px;
  padding: 16px 10px;
  border-radius: 16px;
  background: rgba(8, 10, 22, 0.55);
  border: 1px solid rgba(106, 90, 205, 0.15);
  backdrop-filter: blur(6px);
}

.hero-stat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 0 6px;
}

.hero-stat-divider {
  width: 1px;
  align-self: stretch;
  margin: 4px 0;
  background: linear-gradient(180deg, transparent, rgba(255, 255, 255, 0.18), transparent);
}

.hero-stat-label {
  display: block;
  font-size: 13px;
  color: rgba(203, 213, 225, 0.75);
  margin-bottom: 8px;
}

.hero-stat-num {
  font-size: 24px;
  font-weight: 800;
  color: #00f2ff;
  letter-spacing: 0.02em;
  text-shadow:
    0 0 12px rgba(0, 242, 255, 0.75),
    0 0 28px rgba(0, 242, 255, 0.35);
}

.plus {
  font-size: 19px;
  font-weight: 700;
  opacity: 0.95;
}

.icon-sprite {
  position: absolute;
  width: 1px;
  height: 1px;
  margin: -1px;
  padding: 0;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

.grid-card {
  position: relative;
  overflow: hidden;
  background: rgba(10, 11, 28, 0.82);
  border-color: rgba(106, 90, 205, 0.32);

  &::before {
    content: '';
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    height: 72px;
    background: radial-gradient(
      ellipse 100% 130% at 50% 0%,
      rgba(106, 90, 205, 0.38) 0%,
      rgba(75, 0, 130, 0.15) 42%,
      transparent 70%
    );
    pointer-events: none;
    z-index: 0;
  }

  .icon-grid {
    position: relative;
    z-index: 1;
  }
}

.card-block {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(106, 90, 205, 0.14);
  border-radius: 18px;
  padding: 16px 14px;
  margin-bottom: 14px;
  backdrop-filter: blur(10px);
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 22px 12px;
}

.icon-grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 11px;
  margin: 0;
  padding: 0;
  border: none;
  background: transparent;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
  font: inherit;
  color: inherit;
}

.grid-icon-ring {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(167, 139, 250, 0.2);
  box-shadow:
    0 4px 14px rgba(0, 0, 0, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.06);
  color: #f8fafc;
}

.grid-ico-svg {
  width: 30px;
  height: 30px;
  overflow: visible;
}

.round-label {
  font-size: 12px;
  color: #e2e8f0;
  line-height: 1.3;
  max-width: 82px;
}

.leader-star-ico {
  width: 22px;
  height: 22px;
  flex-shrink: 0;
  filter: drop-shadow(0 0 8px rgba(139, 92, 246, 0.65));
}

.leader-block {
  padding: 14px 10px 18px;
  background:
    radial-gradient(120% 80% at 50% 0%, rgba(82, 103, 182, 0.25) 0%, rgba(20, 29, 56, 0) 60%),
    linear-gradient(180deg, #263152 0%, #202942 56%, #1c2237 100%);
  border: 1px solid rgba(153, 169, 216, 0.16);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.04);
}

.leader-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.leader-head-l {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  font-weight: 600;
  color: #fff;
}

.leader-head-txt {
  letter-spacing: 0.02em;
}

.podium-wrap {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  gap: 0;
  min-height: 238px;
  padding-top: 18px;
}

.podium-col {
  flex: 1;
  max-width: 33%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.podium-col.pr-1 {
  order: 2;
}

.podium-col.pr-2 {
  order: 1;
}

.podium-col.pr-3 {
  order: 3;
}

.podium-pillar {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #252d46;
  border-radius: 20px 20px 16px 16px;
  padding: 46px 8px 14px;
  border: 1px solid rgba(173, 186, 220, 0.16);
  position: relative;
  overflow: visible;
  box-shadow:
    0 16px 30px rgba(8, 12, 24, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
}

.podium-watermark {
  position: absolute;
  left: 50%;
  bottom: -8px;
  transform: translateX(-50%);
  font-size: 64px;
  font-weight: 800;
  line-height: 1;
  letter-spacing: -0.06em;
  color: rgba(255, 255, 255, 0.04);
  pointer-events: none;
  user-select: none;
}

.pr-1 .podium-pillar {
  min-height: 218px;
  border-color: rgba(251, 221, 142, 0.28);
  background: #2a324d;
}

.pr-2 .podium-pillar {
  min-height: 184px;
  border-radius: 20px 0 16px 16px;
}

.pr-3 .podium-pillar {
  min-height: 184px;
  border-radius: 0 20px 16px 16px;
}

.avatar-wrap {
  position: relative;
  z-index: 2;
  width: 62px;
  height: 62px;
  border-radius: 50%;
  padding: 3px;
  margin-bottom: 10px;
  box-sizing: border-box;
  margin-top: -78px;
}

.avatar-wrap.medal-1 {
  background: linear-gradient(145deg, #fcd34d, #d97706);
  width: 74px;
  height: 74px;
  margin-top: -86px;
}

.avatar-wrap.medal-2 {
  background: linear-gradient(145deg, #e2e8f0, #94a3b8);
}

.avatar-wrap.medal-3 {
  background: linear-gradient(145deg, #fdba74, #c2410c);
}

.avatar-inner {
  display: block;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(180deg, #64748b 0%, #334155 100%);
}

.avatar-img {
  display: block;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.badge-rank {
  position: absolute;
  right: -2px;
  bottom: -2px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #64748b;
  color: #fff;
  font-size: 11px;
  font-weight: 800;
  line-height: 20px;
  text-align: center;
  border: 2px solid #1a1c24;
}

.medal-1 .badge-rank {
  background: linear-gradient(145deg, #fbbf24, #d97706);
}

.medal-3 .badge-rank {
  background: linear-gradient(145deg, #ea580c, #9a3412);
}

.podium-mail-card {
  margin-top: 2px;
  width: 96%;
  min-height: 20px;
  padding: 0 4px;
  border-radius: 0;
  font-size: 11px;
  color: #d9ddea;
  text-align: center;
  line-height: 1.2;
  max-width: 100%;
  background: transparent;
  border: 0;
  position: relative;
  z-index: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.podium-usd {
  margin-top: 10px;
  font-size: 18px;
  font-weight: 500;
  color: #fff;
  line-height: 1.15;
  text-align: center;
  letter-spacing: 0;
  position: relative;
  z-index: 1;
}

.podium-pct {
  margin-top: 4px;
  font-size: 14px;
  font-weight: 500;
  color: #3bc99f;
  position: relative;
  z-index: 1;
}

.investor-list {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.investor-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  border-radius: 18px;
  background: rgba(47, 58, 89, 0.45);
  border: 1px solid rgba(173, 186, 220, 0.22);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.03);
}

.inv-rank {
  width: 22px;
  font-size: 20px;
  font-weight: 500;
  color: #d4dae9;
  flex-shrink: 0;
}

.inv-av {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  flex-shrink: 0;
  background: linear-gradient(145deg, #6366f1, #4338ca);
}

.inv-av.tone-1 {
  background: linear-gradient(145deg, #ec4899, #be185d);
}

.inv-av.tone-2 {
  background: linear-gradient(145deg, #14b8a6, #0f766e);
}

.inv-mail {
  flex: 1;
  min-width: 0;
  font-size: 14px;
  color: #dfe5f2;
  line-height: 1.3;
  word-break: break-word;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.inv-right {
  text-align: right;
  flex-shrink: 0;
}

.inv-usd {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #e7ecf8;
}

.inv-pct {
  display: block;
  margin-top: 2px;
  font-size: 13px;
  font-weight: 500;
  color: #3bc99f;
}

.news-block {
  padding-bottom: 14px;
}

.news-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.news-title {
  font-size: 17px;
  font-weight: 700;
  color: #fff;
}

.news-more {
  border: none;
  background: transparent;
  padding: 4px 0;
  font-size: 14px;
  color: #888;
  cursor: pointer;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.news-card {
  padding: 16px 16px 14px;
  border-radius: 16px;
  background: #1e1e26;
  border: 1px solid rgba(255, 255, 255, 0.06);
  cursor: pointer;
  transition: background 0.15s ease;

  &:active {
    background: #252530;
  }
}

.news-text {
  margin: 0 0 14px;
  font-size: 15px;
  line-height: 1.55;
  color: #fff;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-meta {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 10px;
}

.news-time {
  font-size: 13px;
  color: #888;
}

.rank-board {
  margin-top: 8px;
  margin-bottom: 8px;
  padding: 8px 10px 12px;
  border-radius: 14px;
  background: #0f1d46;
  border: 1px solid rgba(106, 126, 176, 0.28);
}

.rank-board-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.rank-board-title {
  font-size: 30px;
  font-weight: 600;
  color: #f4f7ff;
}

.rank-board-more {
  border: none;
  background: transparent;
  padding: 4px 0;
  font-size: 14px;
  color: #a2adbf;
  cursor: pointer;
}

.rank-pills {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
  overflow-x: auto;
  scrollbar-width: none;

  &::-webkit-scrollbar {
    display: none;
  }
}

.rank-pill {
  flex: 1;
  min-width: 74px;
  border: 1px solid rgba(166, 181, 220, 0.24);
  border-radius: 14px;
  padding: 6px 8px;
  font-size: 12px;
  color: #9aabc9;
  background: rgba(255, 255, 255, 0.08);
  cursor: pointer;
}

.rank-pill--on {
  background: #f2f4f7;
  color: #171f31;
  font-weight: 600;
  border-color: #f2f4f7;
}

.rank-col-h {
  display: grid;
  grid-template-columns: 1fr auto auto;
  align-items: center;
  column-gap: 10px;
  padding: 4px 6px 8px;
  font-size: 12px;
  color: #8e9db9;
}

.rank-col-mid {
  text-align: left;
}

.rank-col-end {
  text-align: right;
  min-width: 64px;
}

.rank-card-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rank-quote-card {
  display: grid;
  grid-template-columns: 1fr auto auto;
  align-items: center;
  column-gap: 12px;
  padding: 10px 8px;
  border-radius: 12px;
  background: rgba(30, 45, 88, 0.55);
  border: 1px solid rgba(124, 146, 198, 0.24);
  cursor: pointer;
  transition: background 0.12s ease;
  margin: 0;
  list-style: none;

  &:active {
    background: rgba(60, 70, 98, 0.8);
  }
}

.rq-left {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.rq-coin {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  flex-shrink: 0;
  background: radial-gradient(circle at 32% 28%, #fbbf24 0%, #d97706 45%, #92400e 100%);
}

.rq-symbol {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  flex-shrink: 0;
  object-fit: cover;
}

.rq-coin.coin-1 {
  background: radial-gradient(circle at 32% 28%, #a78bfa 0%, #6366f1 50%, #4338ca 100%);
}

.rq-coin.coin-2 {
  background: radial-gradient(circle at 32% 28%, #fbbf24 0%, #eab308 40%, #ca8a04 100%);
}

.rq-coin.coin-3 {
  background: radial-gradient(circle at 32% 28%, #22d3ee 0%, #0891b2 45%, #0e7490 100%);
}

.rq-pair-wrap {
  min-width: 0;
}

.rq-pair {
  margin: 0;
  font-size: 15px;
  color: #f7f9fd;
  font-weight: 600;
  line-height: 1.2;

  strong {
    font-weight: 800;
  }
}

.rq-vol {
  margin: 4px 0 0;
  font-size: 12px;
  color: #8e9db9;
}

.rq-mid {
  text-align: left;
  padding-left: 4px;
}

.rq-price {
  margin: 0;
  font-size: 15px;
  font-weight: 700;
  color: #f5f7fc;
  line-height: 1.2;
}

.rq-usd {
  margin: 4px 0 0;
  font-size: 12px;
  color: #8e9db9;
}

.rq-right {
  text-align: right;
  min-width: 64px;
}

.rq-chg {
  font-size: 13px;
  font-weight: 700;
  white-space: nowrap;
  padding: 6px 10px;
  border-radius: 6px;
  min-width: 74px;
  text-align: center;
}

.rq-chg.is-up {
  color: #0de7b6;
  background: transparent;
}

.rq-chg.is-down {
  color: #ffffff;
  background: linear-gradient(180deg, #ff3f83 0%, #ff2b76 100%);
}

.rq-vol-tag {
  color: #d7deed;
  background: rgba(255, 255, 255, 0.08);
}

</style>

