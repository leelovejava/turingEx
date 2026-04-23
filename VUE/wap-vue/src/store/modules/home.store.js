import { SET_COIN_LIST, SET_CURRENCY, SET_COIN_SYMBOL_ARR } from '@/store/const.store'
import { _getCoins } from '@/service/cryptos.api'
import { _getExchangeRate } from '@/service/home.api'

export default {
  namespaced: true,
  state: {
    currency: {}, // 当前汇率
    coinArr: [], // 解构出来的币种数组
    hotArr: [], // 热门币种
    coinList: [], // 品种
  },
  getters: {
    coinList: state => state.coinList,
    coinArr: state => state.coinArr,
    hotArr: state => state.hotArr,
    currency: state => state.currency,
  },
  mutations: {
    [SET_COIN_LIST](state, list) {
      state.coinList = list;
    },
    [SET_CURRENCY](state, currency) {
      state.currency = currency;
    },
    [SET_COIN_SYMBOL_ARR](state, list = []) {
      const arr = []
      const hots = []
      list.map(item => {
        arr.push(item.symbol)
        if (item.isTop === '1') { // 热门
          hots.push(item.symbol)
        }
      })
      state.coinArr = arr
      state.hotArr = hots
    },
  },
  actions: {
    async [SET_COIN_LIST]({ commit }, data) { // 获取配置的币种
      const list = await _getCoins({ type: data }).catch(err => { Promise.reject(err) })
      commit(SET_COIN_SYMBOL_ARR, list) // 原数据
      commit(SET_COIN_LIST, list) // 拆分的单个数据
      Promise.resolve(list)

    },
    async [SET_CURRENCY]({ commit }) { // 设置汇率
      const currency = await _getExchangeRate().catch(err => Promise.reject(err))
      commit(SET_CURRENCY, currency)
    },
    actionsToken({ commit }, data) {
      commit("SET_TOKEN", data);
    },
    actionsStatus({ commit }, data) {
      commit("SET_STATUS", data);
    },
  },
};
