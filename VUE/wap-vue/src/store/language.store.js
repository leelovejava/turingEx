import { defineStore } from 'pinia'
import { SET_LANGUAGE } from '@/store/types.store'
import { getStorage, setStorage, getBrowserLang } from '@/utils/index'

export const useLanguageStore = defineStore('language', {
    persist: true,
    state: () => ({
        language: getStorage('lang') || getBrowserLang(),
    }),
    actions: {
        [SET_LANGUAGE](locale) {
            this.language = locale
            setStorage('lang', locale)
        }
    },
})
