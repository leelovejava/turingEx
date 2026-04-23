/*!
 * @author atongmu <zhounianlai@teacher.com.cn>
 * date 11/07/2019
 * description The project tool function file.
 */

/**
 * @description 时间格式处理
 */

//import { Toast } from "vant";

export const formatData = (data) => {
    let thisData = formatTime(new Date(), "yyyy-MM-dd");
    let myData = data.substr(0, 10);
    if (thisData == myData) {
        let Time = data.substr(11, 5);
        return Time
    } else {
        if (thisData.substr(0, 4) == myData.substr(0, 4) && thisData != myData) {
            let Data = data.substr(5, 11);
            return Data
        } else {
            let Year = myData;
            return Year
        }
    }
}

/**
 * @description 时间格式转化
 * @param {String} date 日期
 * @param {String} fmt 需要的格式
 */
export const formatTime = (date, fmt) => {
    let o = {
        "M+": date.getMonth() + 1, //月份
        "d+": date.getDate(), //日
        "h+": date.getHours(), //小时
        "m+": date.getMinutes(), //分
        "s+": date.getSeconds(), //秒
        "q+": Math.floor((date.getMonth() + 3) / 3), //季度
        "S": date.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (let k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}


export const dataTime = (data, isTrue) => {
    var date = new Date(data);
    let Y = date.getFullYear() + '-';
    let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
    let D = date.getDate() + ' ';
    let h = date.getHours() + ':';
    let m = date.getMinutes() + ':';
    let s = date.getSeconds();
    let str = Y + M + D
    if (isTrue) {
        str = Y + M + D + h + m + s
    } else {
        str = Y + M + D
    }
    return str
}

export const dataTimeEx = (data, isTrue) => {
    var date = new Date(data);
    let Y = date.getFullYear() + '-';
    let M = ((date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '').padStart(2, '0') + '-';
    let D = (date.getDate() + '').padStart(2, '0') + ' ';
    let h = (date.getHours() + '').padStart(2, '0') + ':';
    let m = (date.getMinutes() + '').padStart(2, '0') + ':';
    let s = (date.getSeconds() + '').padStart(2, '0');
    let str = Y + M + D
    if (isTrue) {
        str = Y + M + D + h + m + s
    } else {
        str = Y + M + D
    }
    return str
}

/**
 * @description 验证手机号格式是否正确
 * @param {String} mobile 电话号码
 */
export const checkMobileformat = mobile => /^1[345789]\d{9}$/.test(mobile);

/**
 * @description Array clear empty item.
 * @param {Array} array
 */
export const arrayClearEmptyItem = array => {
    return array.filter(item => item);
}

/**
 * @description 判断一个日期是过去还是未来
 * @param {String} d 要判断的日期
 * @returns past => false   future => true
 */
export const judgeDateIsPastOrFuture = (d) => {
    if (!d) {
        return false;
    }
    const nowDate = new Date();
    const nowTimeStamp = nowDate.getTime();
    const date = new Date(d.replace(/-/g, "/"));
    const dateTimeStamp = date.getTime();
    return nowTimeStamp < dateTimeStamp;
}

/**
 * @description 判断一个字符串中是否有重复的项（0-9, a-z, A-Z）
 * @param {String} str 判断的目标字符串
 * @returns {Boolean} true: 有重复项   false: 无重复项
 */
export const strIsReplace = str => {
    const passwordRule = /[0-9a-zA-Z]/;
    const arr = str.split("");
    for (let i = 0; i < arr.length; i++) {
        const element = arr[i];
        if (passwordRule.test(element)) {
            if (i === arr.length - 1) {
                return false;
            }
            if (str.includes(element, i + 1)) {
                return true;
            }
        }
    }
    return false;
}
// 设置localStorage
export const setStorage = function (key, obj) {
    let json = JSON.stringify(obj)
    window.localStorage.setItem(key, json)
    // console.log('设置语言', key, json)
}

// 获取localStorage
export const getStorage = function (key) {
    const str = window.localStorage.getItem(key)
    if (!str) {
        return null
    }
    return JSON.parse(str)
}

// 移除localStorage
export const removeStorage = function (key) {
    window.localStorage.removeItem(key)
}
// 设置sessionStorage
export const setSessionStorage = function (key, obj) {
    let json = JSON.stringify(obj)
    window.sessionStorage.setItem(key, json)
    // console.log('设置语言', key, json)
}

// 获取sessionStorage
export const getSessionStorage = function (key) {
    const str = window.sessionStorage.getItem(key)
    if (!str) {
        return null
    }
    return JSON.parse(str)
}
// 获取浏览器默认语言
export const getBrowserLang = function () {
    let browserLang = navigator.language ? navigator.language : navigator.browserLanguage
    let defaultBrowserLang = ''
    if (browserLang.toLowerCase() === 'cn' || browserLang.toLowerCase() === 'zh' || browserLang.toLowerCase() === 'zh-cn') {
        defaultBrowserLang = 'CN'
    } else {
        defaultBrowserLang = 'en'
    }
    return defaultBrowserLang
}

export const fixDate = (val, i18n) => { // 保留两位小数
    const value = val / 1
    if (isNaN(value)) {
        return '--'
    }
    if (i18n.locale === 'CN' || i18n.locale === 'zh-CN') {
        if ((value / 10000).toString().split('.')[0].length <= 4) {
            return (value / 10000).toFixed(3) + ' ' + i18n.global.t('万')
        } else {
            return (value / 100000000).toFixed(3) + ' ' + i18n.global.t('亿')
        }

    } else {
        if ((value / 1000000).toString().split('.')[0].length <= 4) {
            return (value / 1000000).toFixed(3) + ' ' + 'M'
        } else {
            return (value / 1000000000).toFixed(3) + ' ' + 'B'
        }
    }
}

export const debounce = (fn, delay = 500) => {
    // timer 是在闭包中的
    let timer = null;

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


export const changeTheme = (theme) => {
    if ((navigator.userAgent).indexOf('Html5Plus') > -1) {
        if (theme == 'white') {
            plus.navigator.setStatusBarBackground('#ffffff');
            plus.navigator.setStatusBarStyle('dark'); // 只能是dark 和 light
        } else {
            plus.navigator.setStatusBarBackground('#131A2E');
            plus.navigator.setStatusBarStyle('light'); // 只能是dark 和 light
        }
    }
}

/**
 * 生成随机数
 */
export function getRandom() {
    return Math.floor(Math.random() * 10)
}


export const handleImage = (url) => {
    return new URL(url, import.meta.url).href
}

