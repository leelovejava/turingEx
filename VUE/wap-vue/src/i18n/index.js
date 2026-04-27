import { createI18n } from 'vue-i18n'
import enLocale from './modules/en'
import cnLocale from './modules/CN'
import zhcnLocale from './modules/zh-CN'
import korcnLocale from './modules/Korean'
import japcnLocale from './modules/Japanese'
import Deutsch from './modules/de'
import fr from './modules/fr'
import vi from './modules/vi'
import th from './modules/th'
import gr from './modules/gr'
import Italy from './modules/Italy'
import SpanishLocal from './modules/es'
import PortugueseLocal from './modules/pt'

const SUPPORT_LOCALES = ['en', 'CN', 'zh-CN', 'Korean', 'Japanese', 'de', 'fr', 'vi', 'th', 'Italy', 'es', 'pt', 'gr']

const lang = (() => {
  try {
    const raw = window.localStorage.getItem('lang')
    const current = raw ? JSON.parse(raw) : 'en'
    return SUPPORT_LOCALES.includes(current) ? current : 'en'
  } catch (e) {
    return 'en'
  }
})()

const messages = {
  en: {
    ...enLocale
  },
  CN: {
    ...cnLocale
  },
  'zh-CN': {
    ...zhcnLocale
  },
  Korean: {
    ...korcnLocale
  },
  Japanese: {
    ...japcnLocale
  },
  de: {
    ...Deutsch
  },
  fr: {
    ...fr
  },
  vi: {
    ...vi
  },
  th: {
    ...th
  },
  Italy: {
    ...Italy
  },
  es: {
    ...SpanishLocal
  },
  pt: {
    ...PortugueseLocal
  },
  gr: {
    ...gr
  }
}

const i18n = createI18n({
  legacy: false,
  locale: lang,
  fallbackLocale: 'en',
  messages,
})

export default i18n
