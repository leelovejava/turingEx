import { createStore } from 'vuex'
import language from "./modules/language";
import user from "./modules/user";
import home from '@/store/modules/home.store'
import payment from "@/store/modules/payment";
import c2cBuy from "@/store/modules/c2cBuy";
import c2cSell from "@/store/modules/c2cSell";
import c2c from "@/store/modules/c2c";
// import exchangeRate from './modules/exchangeRate'
import createPersistedState from "vuex-persistedstate";

const store = {
  plugins: [createPersistedState()],
  state: {
    moveDire: "forward",
    imgURI: '', // 图片地址域名
  },
  getters: {
    imgURI: state => state.imgURI,
    // coins: state => state.home.coinArr // 配置的币种
  },
  modules: {
    language,
    user,
    home,
    payment,
    c2cBuy,
    c2cSell,
    c2c,
    // exchangeRate
  },
};
export default createStore(store);
