import { defineStore } from 'pinia'
import { SET_THEME } from '@/store/types.store'
import { getStorage, setStorage } from '@/utils/index'

/** @param {string|null|undefined} val */
export function normalizeTheme(val) {
    if (val === 'white') return 'white'
    if (val === 'dark') return 'dark'
    return 'dark'
}

export const themeStore = defineStore('theme', {
    persist: {
        afterRestore: (ctx) => {
            const next = normalizeTheme(ctx.store.theme)
            ctx.store.theme = next
            document.documentElement.setAttribute('theme', next)
        },
    },
    state: () => ({
        theme: normalizeTheme(getStorage('theme')),
    }),
    getters: {},
    actions: {
        [SET_THEME](val, isReload) {
            const theme = normalizeTheme(val)
            document.documentElement.setAttribute('theme', theme)
            setStorage('theme', theme)
            this.theme = theme
            if (isReload) {
                location.reload()
            }
        },
    },
})
