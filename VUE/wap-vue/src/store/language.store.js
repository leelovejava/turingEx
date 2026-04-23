import { defineStore } from 'pinia'
import { SET_LANGUAGE } from '@/store/types.store'
import { getStorage, setStorage, getBrowserLang } from '@/utils/index'

export const useLanguageStore = defineStore('language', {
    // state 持久化
    persist: true,
    state: () => ('language', {
        language: getStorage('lang') || getBrowserLang() // 项目初始化时，默认为浏览器的语言,
    }),
    actions: {
        [SET_LANGUAGE](locale) {
            this.language = locale
            setStorage('lang', locale)
        }

    },
})
