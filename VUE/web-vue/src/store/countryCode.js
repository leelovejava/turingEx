import { defineStore } from "pinia";

export const useCountryCodeStore = defineStore("countryCode", {
  // state 持久化
  persist: true,
  state: () => {
    return {
      code: "1",
      iso2: "us",
    };
  },
  getters: {},
  actions: {
    async updateCountry(val) {
      this.code = val;
    },
    async updateIso2(val) {
      this.iso2 = val;
    },
    async resetCountry() {
      this.code = "1";
      this.iso2 = "us";
    },
  },
});
