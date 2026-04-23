import { defineStore } from 'pinia'
import { GET_USERINFO, SET_USERINFO } from '@/store/types.store'
import { _info, _getBalance } from "@/service/user.api.js";
export const useUserStore = defineStore('user', {
    // state 持久化
    persist: true,
    state: () => ('user', {
        userInfo: {
            token: ''
        }
    }),
    getters: {
    },
    actions: {
        async [GET_USERINFO](userInfoObj) { // 发送请求获取信息
            this.userInfo = userInfoObj
            let data = await _info()
            this.userInfo = { ...this.userInfo, ...data }
            let res = await _getBalance()
            let obj = { 'balance': res.money }
            this.userInfo = { ...this.userInfo, ...obj }
        },
        [SET_USERINFO](info) { // 发送请求获取信息
            this.userInfo = {
                ...this.userInfo,
                ...info
            }
        }
    },
})
