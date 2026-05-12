<template>
  <div id="cryptos" class="copy-choose-page">
    <assets-head :title="t('aiQuantChooseTitle')" :backFunc="goBack" />

    <div class="copy-choose-shell">
      <button type="button" class="choose-card" @click="goSpot">
        <span class="choose-card-icon choose-card-icon--chart" aria-hidden="true">
          <svg viewBox="0 0 24 24" width="28" height="28" fill="none">
            <path
              d="M4 18V6m4 12V10m4 8V8m4 10v-6m4 6V4"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
            />
          </svg>
        </span>
        <span class="choose-card-body">
          <span class="choose-card-title">{{ t('aiQuantSpotCopyTitle') }}</span>
          <span class="choose-card-desc">{{ t('aiQuantSpotCopyDesc') }}</span>
        </span>
        <span class="choose-card-chevron" aria-hidden="true">›</span>
      </button>

      <button type="button" class="choose-card" @click="goBot">
        <span class="choose-card-icon choose-card-icon--bot" aria-hidden="true">
          <svg viewBox="0 0 24 24" width="28" height="28" fill="none">
            <rect x="6" y="7" width="12" height="11" rx="2" stroke="currentColor" stroke-width="1.8" />
            <circle cx="10" cy="12" r="1.2" fill="currentColor" />
            <circle cx="14" cy="12" r="1.2" fill="currentColor" />
            <path d="M9 4h6l1 3H8l1-3z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round" />
          </svg>
        </span>
        <span class="choose-card-body">
          <span class="choose-card-title">{{ t('aiQuantBotCopyTitle') }}</span>
          <span class="choose-card-desc">{{ t('aiQuantBotCopyDesc') }}</span>
        </span>
        <span class="choose-card-chevron" aria-hidden="true">›</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { showToast } from 'vant'
import assetsHead from '@/components/Transform/assets-head/index.vue'
import { _getQuantQuestionExist } from '@/service/cryptos.api'
import { useUserStore } from '@/store/user'

defineOptions({ name: 'AiQuantChoosePage' })

const router = useRouter()
const { t } = useI18n()
const userStore = useUserStore()

function goBack() {
  router.push('/quotes/index?tabActive=0')
}

async function goSpot() {
  if (!userStore?.userInfo?.token) {
    router.push('/login')
    return
  }
  const res = await _getQuantQuestionExist()
  if (res?.exist) {
    showToast({ message: t('traderAlreadySubmitted'), position: 'middle' })
    return
  }
  router.push('/cryptos/aiQuant/questionnaire')
}

function goBot() {
  router.push('/cryptos/aiQuant')
}
</script>

<style lang="scss" scoped>
.copy-choose-page {
  min-height: 100vh;
  background: $main_background;
  box-sizing: border-box;
}

.copy-choose-shell {
  width: 100%;
  max-width: 520px;
  margin: 0 auto;
  padding: 8px 20px 40px;
  box-sizing: border-box;
}

.choose-card {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 22px 18px;
  margin-bottom: 16px;
  border: 1px solid $line_color;
  border-radius: 16px;
  background: $main2_background;
  cursor: pointer;
  text-align: left;
  box-sizing: border-box;
  transition: opacity 0.15s ease;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04);
}

.choose-card:active {
  opacity: 0.88;
}

.choose-card-icon {
  flex-shrink: 0;
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  color: $btn_main;
  background: rgba(22, 120, 255, 0.12);
  border: 1px solid rgba(22, 120, 255, 0.2);
}

.choose-card-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.choose-card-title {
  font-size: 30px;
  font-weight: 700;
  color: $text_color;
  line-height: 1.25;
}

.choose-card-desc {
  font-size: 24px;
  color: $text_color1;
  line-height: 1.35;
}

.choose-card-chevron {
  flex-shrink: 0;
  font-size: 40px;
  font-weight: 300;
  color: $text_color1;
  line-height: 1;
}

@media (min-width: 1024px) {
  .copy-choose-shell {
    padding-top: 16px;
  }

  .choose-card {
    padding: 26px 22px;
  }
}
</style>
