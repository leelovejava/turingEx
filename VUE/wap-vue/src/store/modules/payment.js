import {
  SET_NAV_HEIGHT,
} from "@/store/const.store"

export default {
  namespaced: true,
  state: {
    navHeight: 0
  },
  mutations: {
    [SET_NAV_HEIGHT](state, {height}) {
      state.navHeight = height;
    }
  }
}