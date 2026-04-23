import { defineStore } from 'pinia'
import { GET_USERLIST } from '@/store/types.store'
import { _itemUserOptionalList } from '@/service/quotes.api'
export const useMyListStore = defineStore('myList', {
    // state 持久化
    persist: true,
    state: () => ('myList', {
        userList: []
    }),
    getters: {
    },
    actions: {
        async [GET_USERLIST]() { // 发送请求获取信息
            _itemUserOptionalList().then((res) => {
                this.userList = res
            })
        },
    },
})
