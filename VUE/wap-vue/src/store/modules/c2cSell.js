import {
  SET_COUNT,
  SET_TOTAL_PRICE,
  SET_ORDER_NUMBER,
  SET_CREATE_ORDER_TIME,
  SET_ORDER_INFO,
} from "@/store/const.store"

export default {
  namespaced: true,
  state: {
    orderInfo: {},
    count: 0, // 数量
    totalPrice: 0, // 总价
    orderNumber: 0, // 订单号
    createOrderTime: 0, // 创建时间
  },
  mutations: {
    [SET_ORDER_INFO](state, payload) {
      state.orderInfo = payload.info;
    },
  }
}