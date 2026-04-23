import { createApp } from "vue";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
// css公用文件
import "@/assets/css/compositeHome/index.css";
import "element-plus/dist/index.css";
import "vue3-country-intl/lib/vue3-country-intl.css";
import "@/assets/css/common.css";
import "vue-slider-component/theme/default.css";
import "vue3-slide-verify/dist/style.css";

// composite-home公用组件
import menuLayout from "@comCompositeHome/menuLayout.vue";
import pcSection from "@comCompositeHome/pcSection.vue";

//element-plus组件
import zh from "element-plus/lib/locale/lang/zh-cn";
import en from "element-plus/lib/locale/lang/en";
import { ElMessage } from "element-plus";
import ElementPlus from "element-plus";

// 公用组件
import Vue3CountryIntl from "vue3-country-intl";
import SlideVerify from "vue3-slide-verify";
import eventBus from "vue3-eventbus";
import lazyPlugin from "vue3-lazy";

// 自定义指令
import {setupAuthDirective} from './utils/directives';

import { getImages } from "@/utils/index";
import router from "@/router";
import pinia from "@/store";
import i18n from "@/i18n";
import App from "./App.vue";

const lang = localStorage.getItem("lang")?.includes("zh-CN");
const app = createApp(App).use(lazyPlugin, {
  // loading: require('@/assets/images/default.png'), // 图片加载时默认图片
  // error: require('@/assets/images/error.png')// 图片加载失败时默认图片
});

pinia.use(piniaPluginPersistedstate); //开启持久化
setupAuthDirective(app); // 阻止鼠标多次点击指令
app.use(i18n);
app.use(router);
app.use(ElementPlus, {
  locale: lang ? zh : en,
}); //解决翻页组件多语言问题
app.use(pinia);
app.use(eventBus);
app.config.globalProperties.$getImages = getImages; //挂载全局
app.config.globalProperties.$title = import.meta.env.VITE_APP__TITLE;
app.config.globalProperties.$email = import.meta.env.VITE_APP__EMAIL;

app.component(Vue3CountryIntl.name, Vue3CountryIntl);
app.component(SlideVerify);
app.mount("#app");
app.config.globalProperties.$message = ElMessage;
