import axios from 'axios'
// import qs from 'qs'
import { showToast, closeToast, showLoadingToast, showNotify } from 'vant'
import {
  BASE_URL,
  REQUEST_TIMEOUT,
  CONTENT_TYPE,
  CONTENT_TYPES,
  WITH_CREDENTIALS,
  METHODS
} from '@/config'
import i18n from '@/i18n'
import { getStorage } from '@/utils/index'
// import store from '@/store'
import { useUserStore } from '@/store/user.js'
import store from '@/store/store'
import router from '@/router'
import { signatureGenerate } from '@/utils/signatureUtil.js'

let isClose = false
const service = axios.create({
  baseURL: BASE_URL,
  withCredentials: WITH_CREDENTIALS,
  // timeout: REQUEST_TIMEOUT, // 请求超时时间
  headers: {
    [CONTENT_TYPE]: CONTENT_TYPES.URL_ENCODED
  }
})

// 请求拦截
service.interceptors.request.use(
  (config) => {
    if (!config) {
      config = {}
    }
    if (!config.headers) {
      config.headers = {}
    }
    if (config.method === METHODS.POST) {
      if (config.data) {
        // config.data = qs.stringify(config.data)
      }
    }
    const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone
    config.headers[' x-api-client-timezone'] = timeZone
    const userStore = useUserStore()
    const piniaToken = userStore?.userInfo?.token
    const vuexToken = store?.state?.user?.userInfo?.token
    const localUser = getStorage('user')
    const localToken = localUser?.userInfo?.token || localUser?.token
    const TOKEN = piniaToken || vuexToken || localToken
    const { systemRandom, timestamp, signature } = signatureGenerate()
    if (!config.params) {
      config.params = {}
    }
    if (systemRandom) {
      config.headers['systemRandom'] = systemRandom
    }
    if (timestamp) {
      config.headers['tissuePaper'] = timestamp
    }
    if (signature) {
      config.headers['sign'] = signature
    }
    if (TOKEN) {
      config.headers['token'] = TOKEN
    }
    if (config.url == '/api/api/cms!get.action') {
      if (config.params.content_code == '001') {
        if (getStorage('lang') == 'CN' || getStorage('lang') == 'zh-CN') {
          config.params['language'] = 'zh-CN'
        } else {
          config.params['language'] = 'en'
        }
      } else {
        config.params['language'] = getStorage('lang') || 'en'
      }
    } else {
      config.params['language'] = getStorage('lang') || 'en'
    }
    if (config.loading) {
      showLoadingToast({ forbidClick: true, duration: 0 })
      isClose = true
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截
service.interceptors.response.use(
  (res) => {
    const userStore = useUserStore()
    if (isClose) {
      closeToast()
      isClose = false
    }
    // console.log(res.config.returnType)
    const {
      data: { code, data, msg, token }
    } = res
    if (res.config['returnType'] === 'origin') {
      // 原样返回
      return Promise.resolve(res.data)
    }
    switch (code / 1) {
      case 0: // 正确响应
        return Promise.resolve(data)
      // case 401:
      // case 402:
      // case 407:
      case 403: // 登录状态已过期，您可以继续留在该页面，或者重新登录
        userStore.userInfo = {}
        store.state.user.userInfo = {}
        router.push({
          path: '/login'
        })
        return
      case 10:
      case 20:
        return Promise.reject(res.data)
      default:
        showToast(i18n.global.t(msg))
        return Promise.reject(msg)
    }
  },
  (error) => {
    // 网络状态监控
    if (error && error.request) {
      const status = error.request['status']
      switch (status) {
        case 401:
          break
        case 424:
          logout()
          break
        case 404:
          showToast({
            message: i18n.global.t('interfaceNotFound'),
            type: 'fail',
            duration: 2000
          })
          break
        case 415:
          showToast({
            message: i18n.global.t('httpProtocolMismatch'),
            type: 'fail',
            duration: 2000
          })
          break
        case 428:
          showToast({
            message: i18n.global.t('VerificationCodeIsIllegal'),
            type: 'fail',
            duration: 2000
          })
          break
        // case 500:
        //   showToast({ message: '服务未启动', type: 'fail', duration: 2000 })
        //   break
        // default:
        //   showToast({ message: '服务错误', type: 'fail', duration: 2000 })
        //   break
        default:
          // console.log(error)
          if (
            error.config.url != '/api/api/hobi!getRealtime.action' ||
            error.config.url != '/api/api/item!list.action'
          ) {
            showToast({
              message:
                i18n.global.t(error.message) || i18n.global.t('serviceError'),
              type: 'fail',
              duration: 2000
            })
          }
      }
    } else {
      showToast({
        message: i18n.global.t(error.message) || i18n.global.t('serviceError'),
        type: 'fail',
        duration: 2000
      })
    }

    return Promise.reject(error)
  }
)

export default service
