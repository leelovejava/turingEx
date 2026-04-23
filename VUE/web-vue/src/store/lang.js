import { defineStore } from "pinia";
import { getStorage, setStorage, getBrowserLang } from "@/utils/index";

export const curlang = getStorage("lang") || "en";
export const useLanguageStore = defineStore("language", {
  // state 持久化
  persist: true,
  state: () => {
    return {
      language: curlang,
      // language: getStorage("lang") || getBrowserLang(), // 项目初始化时，默认为浏览器的语言,
    };
  },

  actions: {
    updateLang(locale) {
      this.language = locale;
      setStorage("lang", locale);
    },
  },
});
