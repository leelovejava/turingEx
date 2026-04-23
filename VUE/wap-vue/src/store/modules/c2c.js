import { SET_CURRENCY, SET_ORDER_MODE, SET_CURRENCY_SYMBOL, REASON_FOR_CANCELLATION } from "@/store/const.store";

const SET_SYMBOL_LIST = 'SET_SYMBOL_LIST'
const SET_FIAT_LIST = 'SET_FIAT_LIST'
const SET_PAY_METHODS = 'SET_PAY_METHODS'

const SET_SYMBOL = 'SET_SYMBOL'

const SET_ADV_ID = 'SET_ADV_ID'
const SET_ORDER_NO = 'SET_ORDER_NO'
const SET_DIRECTION = 'SET_DIRECTION'

import { c2cGetCurrencyList, c2cGetPayCurrencyList, ctcPaymentMethodType } from "@/service/otc.api.js";

export default {
  namespaced: true,
  state: {
    orderMode: false, // 是否在接单模式
    symbolList: null,
    fiatList: null, // 法币列表
    payMethods: null,
    currency: null, // 当前法币
    symbol: 'USDT', // 当前加密货币
    currency_symbol: '', // 币种符号
    adv_id: '', // 广告id
    order_no: '', // 订单id
    direction: 'buy', // 买，卖
    reasonForCancellation: '', // 用户取消订单理由
  },
  getters: {
    symbolList: state => state.symbolList,
    fiatList: state => state.fiatList,
    payMethods: state => state.payMethods,
    exchangeCurrency: state => state.currency,
    currencySymbol: state => state.currency_symbol,
    symbol: state => state.symbol,
    direction: state => state.direction,
    advId: state => state.adv_id,
    orderNo: state => state.order_no,
    getReasonForCancellation: state => state.reasonForCancellation,
  },
  mutations: {
    [SET_ADV_ID](state, id) {
      state.adv_id = id
    },
    [SET_ORDER_NO](state, no) {
      state.order_no = no
    },
    [SET_DIRECTION](state, direction) {
      state.direction = direction
    },
    [SET_ORDER_MODE](state, payload) {
      state.orderMode = payload.state;
    },
    [SET_SYMBOL_LIST](state, list) {
      state.symbolList = list
    },
    [SET_FIAT_LIST](state, list) {
      state.fiatList = list
    },
    [SET_PAY_METHODS](state, list) {
      state.payMethods = list
    },
    [SET_CURRENCY](state, currency) {
      state.currency = currency
    },
    [SET_CURRENCY_SYMBOL](state, symbol) {
      state.currency_symbol = symbol
    },
    [SET_SYMBOL](state, symbol) {
      state.symbol = symbol
    },
    [REASON_FOR_CANCELLATION](state, text) {
      state.reasonForCancellation = text;
    }
  },
  actions: {
    async [SET_SYMBOL_LIST]({ commit }) { // 设置加密货币币种
      const data = await c2cGetCurrencyList()
      commit(SET_SYMBOL_LIST, Object.values(data))
    },
    async [SET_FIAT_LIST]({ commit }) { // 设置法币
      // if (!state.fiatList) {
        const data = await c2cGetPayCurrencyList()
        const arr = {}
        data.map(item => {
          const label = item.currency.substr(0, 1).toUpperCase()
          if (!arr[label]) {
            arr[label] = []
          }
          arr[label].push({ ...item, fLabel: item.currency_symbol, label: item.currency, type: 'EN' })
        })
        const keys = Object.keys(arr).sort()
        const result = []
        keys.map(key => {
          result.push({
            title: key,
            labels: arr[key]
          })
        })
        commit(SET_FIAT_LIST, result)
      // }
    },
    async [SET_PAY_METHODS]({ commit, state }, obj) { // 设置支付方式
      const res = await ctcPaymentMethodType(obj)
      commit(SET_PAY_METHODS, res)
    }
  }
}
