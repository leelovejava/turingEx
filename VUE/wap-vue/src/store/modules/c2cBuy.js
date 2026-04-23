import {
  SET_COUNT,
  SET_TOTAL_PRICE,
  SET_ORDER_NUMBER,
  SET_CREATE_ORDER_TIME,
} from "@/store/const.store"

const SET_ORDER_DETAIL = 'SET_ORDER_DETAIL'

export default {
  namespaced: true,
  state: {
    count: 0, // 数量
    totalPrice: 0, // 总价
    orderNumber: 0, // 订单号
    createOrderTime: 0,
    orderDetail: {} // 订单详情
  },
  mutations: {
    [SET_ORDER_DETAIL](state, detail) {
      state.orderDetail = detail
    },
    [SET_COUNT](state, {count}) {
      state.count = count;
    },
    [SET_TOTAL_PRICE](state, {price}) {
      state.totalPrice = price;
    },
    [SET_ORDER_NUMBER](state, {orderNumber}) {
      state.orderNumber = orderNumber;
    },
    [SET_CREATE_ORDER_TIME](state, {createOrderTime}) {
      state.createOrderTime = createOrderTime;
    },
  }
}