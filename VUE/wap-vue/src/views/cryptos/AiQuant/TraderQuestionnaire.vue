<template>
  <div id="cryptos" class="trader-questionnaire">
    <assets-head :title="t('traderQuestionnaireTitle')" :backFunc="goBack" />

    <div class="tq-shell">
      <h2 class="tq-subtitle">{{ t('traderApplyHeading') }}</h2>

      <div class="tq-banner">
        <span class="tq-banner-icon" aria-hidden="true">
          <svg viewBox="0 0 24 24" width="22" height="22" fill="none">
            <circle cx="12" cy="12" r="10" fill="currentColor" />
            <path d="M8 12l2.5 2.5L16 9" stroke="#ffffff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
          </svg>
        </span>
        <div class="tq-banner-text">
          <div class="tq-banner-line1">{{ t('traderReqMet') }}</div>
          <div class="tq-banner-line2">{{ t('traderReqDetail') }}</div>
        </div>
      </div>

      <section class="tq-block">
        <p class="tq-q">1. {{ t('traderQ1') }}</p>
        <div class="tq-yesno">
          <label class="tq-check-row">
            <input v-model="q1" type="radio" class="tq-radio" value="yes" />
            <span>{{ t('traderYes') }}</span>
          </label>
          <label class="tq-check-row">
            <input v-model="q1" type="radio" class="tq-radio" value="no" />
            <span>{{ t('traderNo') }}</span>
          </label>
        </div>
      </section>

      <section class="tq-block">
        <p class="tq-q">2. {{ t('traderQ2') }}</p>
        <div class="tq-yesno">
          <label class="tq-check-row">
            <input v-model="q2" type="radio" class="tq-radio" value="yes" />
            <span>{{ t('traderYes') }}</span>
          </label>
          <label class="tq-check-row">
            <input v-model="q2" type="radio" class="tq-radio" value="no" />
            <span>{{ t('traderNo') }}</span>
          </label>
        </div>
      </section>

      <section class="tq-block">
        <p class="tq-q">3. {{ t('traderQ3') }}</p>
        <div class="tq-yesno">
          <label class="tq-check-row">
            <input v-model="q3" type="radio" class="tq-radio" value="yes" />
            <span>{{ t('traderYes') }}</span>
          </label>
          <label class="tq-check-row">
            <input v-model="q3" type="radio" class="tq-radio" value="no" />
            <span>{{ t('traderNo') }}</span>
          </label>
        </div>
      </section>

      <section class="tq-block">
        <p class="tq-q">4. {{ t('traderQ4') }}</p>
        <textarea v-model="q4" class="tq-textarea" rows="4" :placeholder="t('traderEnterPlaceholder')" />
      </section>

      <section class="tq-block">
        <p class="tq-q">5. {{ t('traderQ5') }}</p>
        <textarea v-model="q5" class="tq-textarea" rows="4" :placeholder="t('traderEnterPlaceholder')" />
      </section>

      <label class="tq-agree">
        <input v-model="agreed" type="checkbox" class="tq-checkbox" />
        <span class="tq-agree-text">
          {{ t('traderAgreePrefix') }}
          <button type="button" class="tq-link" @click.prevent="goAgreement">{{ t('traderAgreementLink') }}</button>
        </span>
      </label>

      <button type="button" class="tq-submit" :disabled="!canSubmit" @click="submit">{{ t('traderSubmitInfo') }}</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { showToast } from 'vant'
import assetsHead from '@/components/Transform/assets-head/index.vue'
import { _insertQuantQuestion, _getQuantQuestionExist } from '@/service/cryptos.api'

defineOptions({ name: 'TraderQuestionnairePage' })

const router = useRouter()
const { t } = useI18n()

const q1 = ref('yes')
const q2 = ref('yes')
const q3 = ref('yes')
const q4 = ref('')
const q5 = ref('')
const agreed = ref(false)

const canSubmit = computed(
  () => q1.value && q2.value && q3.value && q4.value.trim() && q5.value.trim() && agreed.value
)

onMounted(async () => {
  const res = await _getQuantQuestionExist()
  const status = res?.status
  if (status === 'NOPASS') {
    showToast({ message: t('traderStatusNoPass'), position: 'middle' })
  } else if (status === 'N') {
    showToast({ message: t('traderStatusPending'), position: 'middle' })
  } else if (status === 'PASS') {
    showToast({ message: t('traderStatusPass'), position: 'middle' })
  }
})

function goBack() {
  router.push('/quotes/index?tabActive=0')
}

function goAgreement() {
  router.push('/cryptos/aiQuant/traderAgreement')
}

async function submit() {
  await _insertQuantQuestion({
    question1Answer: q1.value,
    question2Answer: q2.value,
    question3Answer: q3.value,
    question4Answer: q4.value,
    question5Answer: q5.value
  })
  showToast({ message: t('traderSubmitOk'), position: 'middle' })
  router.back()
}
</script>

<style lang="scss" scoped>
.trader-questionnaire {
  min-height: 100vh;
  background: $main_background;
  padding-bottom: 40px;
  box-sizing: border-box;
}

.tq-shell {
  width: 100%;
  max-width: 720px;
  margin: 0 auto;
  padding: 12px 20px 32px;
  box-sizing: border-box;
}

.tq-subtitle {
  margin: 0 0 24px;
  font-size: 34px;
  font-weight: 700;
  color: $text_color;
  line-height: 1.3;
}

.tq-banner {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px 18px;
  border-radius: 14px;
  background: $tab_background;
  border: 1px solid $line_color;
  margin-bottom: 28px;
}

.tq-banner-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  color: $green;
  background: rgba(6, 205, 165, 0.15);
}

.tq-banner-text {
  flex: 1;
  min-width: 0;
}

.tq-banner-line1 {
  font-size: 28px;
  font-weight: 600;
  color: $text_color;
  margin-bottom: 8px;
}

.tq-banner-line2 {
  font-size: 24px;
  color: $text_color1;
  line-height: 1.4;
}

.tq-block {
  margin-bottom: 28px;
}

.tq-q {
  margin: 0 0 16px;
  font-size: 28px;
  color: $text_color;
  line-height: 1.45;
}

.tq-yesno {
  display: flex;
  flex-wrap: wrap;
  gap: 24px 40px;
}

.tq-check-row {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  font-size: 28px;
  color: $text_color;
  cursor: pointer;
}

.tq-radio {
  width: 22px;
  height: 22px;
  accent-color: $btn_main;
  cursor: pointer;
}

.tq-textarea {
  width: 100%;
  padding: 16px 18px;
  border-radius: 12px;
  border: 1px solid $border_color;
  background: $input_background;
  color: $text_color;
  font-size: 26px;
  line-height: 1.5;
  resize: vertical;
  min-height: 120px;
  box-sizing: border-box;
}

.tq-textarea::placeholder {
  color: $text_color3;
}

.tq-agree {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin: 32px 0 28px;
  cursor: pointer;
}

.tq-checkbox {
  width: 22px;
  height: 22px;
  margin-top: 6px;
  flex-shrink: 0;
  accent-color: $btn_main;
  cursor: pointer;
}

.tq-agree-text {
  font-size: 26px;
  color: $text_color1;
  line-height: 1.5;
}

.tq-link {
  display: inline;
  padding: 0;
  border: none;
  background: none;
  color: $btn_main;
  font-size: inherit;
  text-decoration: underline;
  cursor: pointer;
}

.tq-submit {
  width: 100%;
  max-width: 100%;
  padding: 22px 24px;
  border-radius: 14px;
  border: none;
  background: $btn_main;
  color: $main-btn-color;
  font-size: 30px;
  font-weight: 700;
  cursor: pointer;
  box-sizing: border-box;
  box-shadow: 0 4px 14px rgba(22, 120, 255, 0.22);
}

.tq-submit:disabled {
  opacity: 0.45;
  cursor: not-allowed;
  box-shadow: none;
}

@media (min-width: 1024px) {
  .tq-shell {
    padding-top: 20px;
  }
}
</style>
