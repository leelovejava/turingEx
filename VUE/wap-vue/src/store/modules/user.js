// import { getStorage, setStorage, getBrowserLang } from "@/utils/utis";
import { _userInfo } from '@/service/user.api'
import { SET_USERINFO, GET_USERINFO, SET_OUT, SET_STATUS } from "@/store/const.store";
import { _getBalance } from '@/service/user.api.js'
export default {
  namespaced: true,
  state: {
    status: 0, // 状态
    gasObj: {}, //
    userInfo: {
      usercode: '', // 
      token: '', // 登录token
      username: ''
    }
  },
  getters: {
    mingStatus: state => state.status,
    isToken: state => state.token,
    userInfo: state => state.userInfo
  },
  mutations: {
    // SET_GAS_OBJ(state,gasObj){
    //   state.gasObj=gasObj
    // },
    [SET_STATUS](state, status) { // 质押状态
      state.status = status;
    },
    [SET_USERINFO](state, info) {
      state.userInfo = { ...state.userInfo, ...info }
    },
    [SET_OUT](state) { // 退出
      state.userInfo = {}
    }
  },
  actions: {
    // actionsGasObj({commit},data){
    //   commit("SET_GAS_OBJ", data);
    // },
    // actionsLogin({ commit }, data) {
    //   commit("SET_UESR_OBJ", data);
    // },
    async [GET_USERINFO]({ commit }, accounts) { // 发送请求获取信息
      commit(SET_USERINFO, accounts) // 登陆
      let data = await _userInfo()
      commit(SET_USERINFO, data) // 用户信息
      data = await _getBalance()
      commit(SET_USERINFO, { balance: data.money }) // 余额
    },
  },
};
