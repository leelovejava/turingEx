import { createApp } from 'vue'
import './assets/css/index.scss'
import 'vant/lib/index.css'
import fxHeader from '@/components/fx-header'
import 'default-passive-events'
// import 'amfe-flexible'
import App from './App.vue'
import i18n from '@/i18n'
import '@/assets/remNew.js'
import 'vant/es/toast/style';
import router from '@/router'
import pinia from '@/store'
import store from '@/store/store'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
pinia.use(piniaPluginPersistedstate)

const app = createApp(App)
const title = import.meta.env.VITE_APP__TITLE
app.config.globalProperties.$title = title
document.title = title
app.use(fxHeader)
app.use(i18n)
app.use(router)
app.use(pinia)
app.use(store)

app.mount('#app')