import Vue from 'vue'
import Vuex from 'vuex'
import cloneDeep from 'lodash/cloneDeep'
import common from './modules/common'
import user from './modules/user'
import prod from './modules/prod'


import talks from './modules/talk'
import notify from './modules/notify'
import settings from './modules/settings'
import emoticon from './modules/emoticon'
import dialogue from './modules/dialogue'
import note from './modules/note'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    common,
    user,
    prod,

    notify,
    talks,
    settings,
    emoticon,
    dialogue,
    note,
  },
  mutations: {
    // 重置vuex本地储存状态
    resetStore (state) {
      if(this.common){
        Object.keys(state).forEach((key) => {
          state[key] = cloneDeep(process.env.VUE_APP_RESOURCES_URL['storeState'][key])
        })
      }
    }
  },
  strict: process.env.NODE_ENV !== 'production'
})
