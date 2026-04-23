import i18n from '@/i18n'
export function getImageUrl(path) {
  return new URL(path, import.meta.url).href
}

// 设置localStorage
export const setStorage = function (key, obj) {
  let json = JSON.stringify(obj)
  window.localStorage.setItem(key, json)
  // console.log('设置语言', key, json)
}

// 获取localStorage
export const getStorage = function (key) {
  const str = window.localStorage.getItem(key) || ''
  if (!str) {
    return null
  }
  return JSON.parse(str)
}
// 获取浏览器默认语言
export const getBrowserLang = function () {
  let browserLang = navigator.language
    ? navigator.language
    : navigator.browserLanguage
  let defaultBrowserLang = ''
  if (
    browserLang.toLowerCase() === 'cn' ||
    browserLang.toLowerCase() === 'zh' ||
    browserLang.toLowerCase() === 'zh-cn'
  ) {
    defaultBrowserLang = 'CN'
  } else {
    defaultBrowserLang = 'en'
  }
  return defaultBrowserLang
}

export const dataTime = (data, isTrue) => {
  var date = new Date(data)
  let Y = date.getFullYear() + '-'
  let M =
    (date.getMonth() + 1 < 10
      ? '0' + (date.getMonth() + 1)
      : date.getMonth() + 1) + '-'
  let D = date.getDate() + ' '
  let h = date.getHours() + ':'
  let m = date.getMinutes() + ':'
  let s = date.getSeconds()
  let str = Y + M + D
  if (isTrue) {
    str = Y + M + D + h + m + s
  } else {
    str = Y + M + D
  }
  return str
}

export const debounce = (fn, delay = 500) => {
  // timer 是在闭包中的
  let timer = null

  return function () {
    if (timer) {
      clearTimeout(timer)
    }
    timer = setTimeout(() => {
      fn.apply(this, arguments)
      timer = null
    }, delay)
  }
}

export const throttle = (fn, wait = 50) => {
  // 上一次执行 fn 的时间
  let previous = 0
  // 将 throttle 处理结果当作函数返回
  return function (...args) {
    // 获取当前时间，转换成时间戳，单位毫秒
    let now = +new Date()
    // 将当前时间和上一次执行函数的时间进行对比
    // 大于等待时间就把 previous 设置为当前时间并执行函数 fn
    if (now - previous > wait) {
      previous = now
      fn.apply(this, args)
    }
  }
}

// 小数展示,保留五位小数
export const formatNumber = (num) => {
  if (typeof num !== 'number') {
    return '--'
  }
  let count
  if (num.toString().indexOf('.') < 0) {
    // 整数
    return num
  } else {
    // 小数
    count = num.toString().split('.').pop().length
    if (count > 5) {
      return num.toFixed(5)
    } else {
      return num
    }
  }
}


// 保留两位小数
export const fixDate = (val, language) => {
  const value = val / 1
  if (isNaN(value)) {
    return '--'
  }
  if (i18n.locale === 'CN' || language == "zh-CN" || language == "CN") {
    if ((value / 10000).toString().split('.')[0].length <= 4) {
      return (value / 10000).toFixed(2) + ' ' + i18n.global.t('万')
    } else {
      return (value / 100000000).toFixed(2) + ' ' + i18n.global.t('亿')
    }
  } else {
    if ((value / 1000000).toString().split('.')[0].length <= 4) {
      return (value / 1000000).toFixed(2) + ' ' + 'M'
    } else {
      return (value / 1000000000).toFixed(2) + ' ' + 'B'
    }
  }
}

export const formatMoney = (val) => {
  if (val === 0) {
    return 0
  }
  if (!val) {
    return '-'
  }
  const num = 1000 //byte
  if (val < num) {
    return val
  }
  if (val < Math.pow(num, 2)) {
    return (val / num).toFixed(2) + 'K' //k
  }
  if (val < Math.pow(num, 3)) {
    return (val / Math.pow(num, 2)).toFixed(2) + 'M' //M
  }
  if (val < Math.pow(num, 4)) {
    return (val / Math.pow(num, 3)).toFixed(2) + 'G' //G
  }
  return (val / Math.pow(num, 4)).toFixed(2) + 'T' //T
}

export const handleImage = (url) => {
  return new URL(url, import.meta.url).href
}

export const formatNews = (str) => {
  if (!str) {
    return ''
  }
  return str
    .replace(/<img [^>]*src=['"]([^'"]+)[^>]*>/g, '')
    .replace(/&nbsp;/gi, '')
}
