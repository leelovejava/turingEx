import { defineStore } from "pinia";
export const useUserStore = defineStore("user", {
  persist: true,
  state: () => {
    return {
      userInfo: {
        token: "",
        username: "",
        usercode: "",
      },
    };
  },
  getters: {
    existToken: (state) => {
      if (state.userInfo.token || localStorage.getItem("spToken")) {
        return true;
      } else {
        return false;
      }
    },
  },
  actions: {
    async updateUserInfo(userInfoObj) {
      // 发送请求获取信息
      console.log("用户信息action", userInfoObj);
      this.userInfo = { ...this.userInfo, ...userInfoObj };
      // let data = await _info() // 获取用户信息,如身份验证，email等
      // this.userInfo = {  ...data, ...this.userInfo } // demo账户，usename和code不替换
      // let res = await _getBalance() // 获取用户余额
      // let obj = { 'balance': res.money }
      // this.userInfo = { ...this.userInfo, ...obj }
    },
    async resetUserInfo() {
      this.userInfo = {
        token: "",
        username: "",
        usercode: "",
      };
    },
  },
});
