import { defineStore } from 'pinia'
import { SET_THEME } from '@/store/types.store'
import { getStorage, setStorage } from '@/utils/index'
export const themeStore = defineStore('theme', {
    // state 持久化
    persist: true,
    state: () => ('theme', {
        theme: getStorage('theme') || 'drak'
    }),
    getters: {
    },
    actions: {
        [SET_THEME](val, isReload) { // 改变主题
            document.documentElement.setAttribute('theme', val);
            setStorage('theme', val)
            this.theme = val
            if (isReload) { //刷新改变主题
                location.reload()
            }
        }
    },
})
