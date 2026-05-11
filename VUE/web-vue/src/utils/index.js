export const getImageUrl = (path) => {
  const modules = import.meta.glob("/src/assets/images/compositeHome/**/*.*", {
    eager: true,
  });
  return modules[path].default;
};

export const getImages = (path) => {
  return new URL(`/src/assets/images/${path}`, import.meta.url).href;
};

export const handleSymbolImg = (name) => {
  if (!name) {
    return;
  }
  // usdthb
  let url;
  const newName = name.toLowerCase();
  if (
    newName === "usdt" ||
    newName === "usd" ||
    newName === "usdcusdt" ||
    newName === "usdthb" ||
    newName === "usdtwd" ||
    newName === "usdc"
  ) {
    url = "/symbol/" + newName + ".png"; //不用过滤
  } else {
    const regex = /(\/){0,1}usd(t){0,1}(\/){0,1}/i;
    const filterUSDName = newName.replace(regex, "");
    url = "/symbol/" + filterUSDName + ".png";
  }
  return new URL(url, import.meta.url).href;
};

// 设置localStorage
export const setStorage = function (key, obj) {
  let json = JSON.stringify(obj);
  window.localStorage.setItem(key, json);
};

// 获取localStorage
export const getStorage = function (key) {
  const str = window.localStorage.getItem(key);
  if (!str) {
    return null;
  }
  return JSON.parse(str);
};

// 移除
export const removeStorage = function (key) {
  window.localStorage.removeItem(key);
};
// 获取浏览器默认语言
export const getBrowserLang = function () {
  let browserLang = navigator.language
    ? navigator.language
    : navigator.browserLanguage;

  let defaultBrowserLang = "en";
  if (
    browserLang.toLowerCase() === "cn" ||
    browserLang.toLowerCase() === "zh" ||
    browserLang.toLowerCase() === "zh-cn"
  ) {
    defaultBrowserLang = "zh";
  }
  return defaultBrowserLang;
};

export const dataTime = (data, isTrue) => {
  var date = new Date(data);
  let Y = date.getFullYear() + "-";
  let M =
    (date.getMonth() + 1 < 10
      ? "0" + (date.getMonth() + 1)
      : date.getMonth() + 1) + "-";
  let D = date.getDate() + " ";
  let h = date.getHours() + ":";
  let m = date.getMinutes() + ":";
  let s = date.getSeconds();
  let str = Y + M + D;
  if (isTrue) {
    str = Y + M + D + h + m + s;
  } else {
    str = Y + M + D;
  }
  return str;
};

export const debounce = (fn, delay = 500) => {
  // timer 是在闭包中的
  let timer = null;
  return function () {
    if (timer) {
      clearTimeout(timer);
    }
    timer = setTimeout(() => {
      fn.apply(this, arguments);
      timer = null;
    }, delay);
  };
};

export function filterArrayEmpty(val) {
  let isEmpty = val.every((val) => {
    return val != "";
  });
  return isEmpty;
}

export function strFirstBit(val) {
  let arr = val.map((val, index, arr) => {
    return val.currency;
  });
  return arr;
}

export function addAndSubtr(val) {
  if (val >= 0) {
    return "+" + val + "%";
  } else if (val < 0) {
    return val + "%";
  } else {
    return "--";
  }
}

export function mergeSort(arr) {
  if (arr.length < 2) {
    return arr;
  }
  var middle = parseInt(arr.length / 2);
  var left = arr.slice(0, middle);
  var right = arr.slice(middle);
  return merge(mergeSort(left), mergeSort(right));
}

function merge(left, right) {
  var result = [];
  var i = 0,
    j = 0;
  while (i < left.length && j < right.length) {
    if (left[i].create_time_ts > right[j].create_time_ts) {
      result.push(right[j++]);
    } else {
      result.push(left[i++]);
    }
  }
  while (i < left.length) {
    result.push(left[i++]);
  }
  while (j < right.length) {
    result.push(right[j++]);
  }

  return result;
}

//
export const langOptions = [
  {
    value: "en",
    label: "English",
  },
  {
    value: "cht",
    label: "繁体中文",
  },
  {
    value: "fr",
    label: "Français",
  },
  {
    value: "de",
    label: "Deutsch",
  },
  {
    value: "it",
    label: "Italia",
  },
  {
    value: "ko",
    label: "한국 사람",
  },
  {
    value: "ja",
    label: "日本語",
  },
  {
    value: "ro", // 罗马尼亚语
    label: "Română",
  },
  {
    value: "es", // 西班牙语
    label: "español",
  },
  {
    value: "tk", // 土耳其语
    label: "Türkçe",
  },
  {
    value: "pt", // 葡萄牙语
    label: "Português",
  },
  {
    value: "el", // 希腊语
    label: "Ελληνικά",
  },
  {
    value: "zh-CN",
    label: "简体中文",
  },
];

export function getParamsLang() {
  let language = JSON.parse(localStorage.getItem("lang"));
  if (language == "ko") {
    language = "Korean";
  } else if (language == "cht") {
    language = "CN";
  } else if (language == "it" || language == "de" || language == "fr") {
    language = "en";
  }

  return language;
}

// 根据本地语言从接口获取相应的name字段
export function getWealthLangToName() {
  let lang = JSON.parse(localStorage.getItem("lang"));
  const nameMap = {
    en: "name_en",
    "zh-CN": "name",
    cht: "name_cn", //繁体
    // ja: "name_jn", //没有配置
    // ko: "name_kn",
  };

  const res = nameMap[lang] || nameMap["en"];

  return res;
}

export const nowUrl =
  import.meta.env.MODE === "development"
    ? "https://sjiepc.com"
    : window.location.origin;

// 根据页面类型，做页面跳转
export function constantMap(type, symbol) {
  const pageTypeMap = {
    forex: "forex",
    indices: "eft",
    cryptos: "coin",
    "US-stocks": "usStocks",
  };

  const defaultSymbol = {
    forex: symbol || "BTCUSD",
    indices: symbol || "GlobalETF500",
    cryptos: symbol || "btc",
    "US-stocks": symbol || "AAPL",
  };

  const defaultUrlMap = {
    forex: `/forex/constract/${
      defaultSymbol[type]
    }?timestamp=${Date.now()}&RouterName=sustainable`,
    indices: `/etf/spot/${defaultSymbol[type]}`,
    cryptos: `/coin/spot/${defaultSymbol[type]}`,
    "US-stocks": `/usStocks/spot/${defaultSymbol[type]}`,
  };

  const defaultUrl = defaultUrlMap[type];

  return {
    pageTypeMap,
    defaultUrlMap,
    defaultSymbol,
    defaultUrl,
  };
}

// 把数字转化为K,M,B
export function numberFormat(val, lotFraction = 2) {
  let res = Number(val);
  let unit = "";
  // 如果小于1000则直接返回
  if (val > 1e3 && val < 1e6) {
    res = val / 1e3;
    unit = "K";
  } else if (val > 1e6 && val < 1e10) {
    res = val / 1e6;
    unit = "M";
  } else if (val > 1e10) {
    res = val / 1e10;
    unit = "B";
  }
  // 最后加逗号,默认保留2位小数
  return `${res.toFixed(2)} ${unit}`;
}

export const isMobile = () => {
  return navigator.userAgent.match(
    /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i
  );
};

// export const isMobile = () => {
//   if (typeof window !== "undefined") {
//     const baseWidth = document.body.clientWidth;
//     const mobile = navigator.userAgent.match(
//       /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i
//     );
//     return baseWidth <= 767 && mobile;
//   }
//   return false;
// };

export const getSymbolTypeContent = (type) => {
  switch (type) {
    case "forex":
      return "外汇";
      break;
    case "indices":
      return "指数";
      break;
    case "commodities":
      return "大宗商品";
      break;
    case "UK-metals":
      return "贵金属";
      break;
    case "cryptos":
      return "虚拟货币";
      break;
    case "US-stocks":
      return "美股";
      break;
    case "HK-stocks":
      return "港股";
      break;
    case "TW-stocks":
      return "台股";
      break;
    case "A-stocks":
      return "A股";
      break;
    case "JP-stocks":
      return "日股";
      break;
    case "INDIA-stocks":
      return "印度股";
      break;
    case "UK-stocks":
      return "英股";
      break;
    case "DE-stocks":
      return "德股";
      break;
    case "BZ-stocks":
      return "巴股";
      break;
    case "global":
      return "全球ETF";
      break;
    case "gold":
      return "黄金ETF";
      break;
    case "ai":
      return "人工智能ETF";
      break;
    case "energy":
      return "能源ETF";
      break;
    default:
      return type;
      break;
  }
};
