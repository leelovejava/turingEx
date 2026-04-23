import { createI18n } from "vue-i18n";
import { getStorage, getBrowserLang } from "@/utils/index";
import messages from "./langs/index";
const lang = getStorage("lang");

const i18n = createI18n({
  legacy: false,
  locale: lang,
  fallbackLocale: "en", // 设置备用语言
  messages, //对象，key为语言，
  specialCharacters: {
    "@": "@",
  },
  missingWarn: false, // 这四个是key未找到或者翻译出问题warn配置
  silentTranslationWarn: true, // 只是为了dev时控制台清静些，prod默认移除的
  silentFallbackWarn:true,
  fallbackWarn:false
});

export default i18n;
