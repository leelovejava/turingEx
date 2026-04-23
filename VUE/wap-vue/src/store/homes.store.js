import { defineStore } from 'pinia'
import { SET_KEFU } from '@/store/types.store'
import { _customer } from '@/service/user.api';



export const useHomesStore = defineStore('homes', {
    // state 持久化
    persist: true,
    state: () => ('homes', {
        kefu_url: ""
    }),

    actions: {
        async [SET_KEFU]() { //获取客服
            const data = await _customer().catch(err => Promise.reject(err))
            this.kefu_url = data.customer_service_url
        },
    },
})