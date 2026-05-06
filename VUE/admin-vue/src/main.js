import Vue from 'vue'
import ElementUI from 'element-ui'
import App from '@/App'
import router from '@/router'                 // api: https://github.com/vuejs/vue-router
import store from '@/store'                   // api: https://github.com/vuejs/vuex
import VueCookie from 'vue-cookie'            // api: https://github.com/alfhen/vue-cookie
import '@/icons'                              // api: http://www.iconfont.cn/
import '@/element-ui-theme/style.css'
import Avue from '@smallwei/avue'             // api: https://avue.top
import '@smallwei/avue/lib/index.css'
import '@/assets/scss/index.scss'
import httpRequest from '@/utils/httpRequest' // api: https://github.com/axios/axios
import { isAuth } from '@/utils'
import VueClipboard from 'vue-clipboard2'

import { menuMap } from './config'


//

// import './core/lazy-use'
import './core/global-component'
// import './core/filter'
// import './core/directives'
// import '@/permission'
// import '@/icons'

// 引入自定义全局css
// import '@/assets/css/global.less'

// import cloneDeep from 'lodash/cloneDeep'
VueClipboard.config.autoSetContainer = true // add this line
Vue.use(VueClipboard)
Vue.use(Avue)
Vue.use(VueCookie)
Vue.use(ElementUI)
Vue.config.productionTip = false


import Contextmenu from 'vue-contextmenujs'
Vue.use(Contextmenu)

// 挂载全局
Vue.prototype.$http = httpRequest // ajax请求方法
Vue.prototype.isAuth = isAuth     // 权限方法

Vue.prototype.menuMap = menuMap

Vue.prototype.$login = false

Vue.prototype.IS_DEBUG = false;

Vue.prototype.$isKefu = false;
console.log("================================");

// 保存整站vuex本地储存初始状态
// process.env.VUE_APP_RESOURCES_URL['storeState'] = cloneDeep(store.state)

// 引入音频文件
import mute from './assets/music/mute.mp3'

Vue.prototype.playAudioOfMute = () => {
  let buttonAudio = document.getElementById('eventAudio');
  buttonAudio.setAttribute('src',mute)
  buttonAudio.play()
}

// 引入音频文件
import withdraw from './assets/music/withdraw.mp3'

Vue.prototype.playAudioOfWithdraw = () => {
  let buttonAudio = document.getElementById('eventAudio');
  buttonAudio.setAttribute('src',withdraw)
  buttonAudio.play()
}

// 引入音频文件
import recharge from './assets/music/recharge.mp3'

Vue.prototype.playAudioOfRecharge = () => {
  let buttonAudio = document.getElementById('eventAudio');
  buttonAudio.setAttribute('src',recharge)
  buttonAudio.play()
}

// 引入音频文件
import rzzt from './assets/music/rzzt.mp3'

Vue.prototype.playAudioOfRzzt = () => {
  let buttonAudio = document.getElementById('eventAudio');
  buttonAudio.setAttribute('src',rzzt)
  buttonAudio.play()
}

// 引入客服
import mutekf from './assets/music/mutekf.mp3'

Vue.prototype.playAudioOfMuteKeFu = () => {
  let buttonAudio = document.getElementById('eventAudio');
  buttonAudio.setAttribute('src',mutekf)
  buttonAudio.play()
}

// 银行充值
import muteBank from './assets/music/bank.mp3'

Vue.prototype.playAudioOfBank = () => {
  let buttonAudio = document.getElementById('eventAudio');
  buttonAudio.setAttribute('src',muteBank)
  buttonAudio.play()
}

// C2C
import muteC2c from './assets/music/c2c.mp3'

Vue.prototype.playAudioOfC2c = () => {
  let buttonAudio = document.getElementById('eventAudio');
  buttonAudio.setAttribute('src',muteC2c)
  buttonAudio.play()
}

//=====设置 el-upload  全局headers =======

Vue.prototype.$signatureHeaders=()=>{
  const signature=signatureGenerate()
  return {
    Sign:signature.signature,
    Systemrandom:signature.systemRandom,
    Tissuepaper:signature.timestamp
  }

}


/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App),
  beforeCreate() {
    // bus 总线
    // $ 为了迎合 vue 的命名习惯
    // 安装全局事件总线，$bus就是当前应用的vm
    Vue.prototype.$bus = this
  }
})
