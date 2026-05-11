import { getImages } from "@/utils/index";
export const qianbaoList = [
  {
    urlPath: "/wallet/walletOverview",
    title: "qianbaozonglan",
    iconPath: getImages("headIcon/account/wallet-overview.png"),
    key: "usdt",
  },
  // 数字货币
  {
    urlPath: "/wallet/cryptosAccounts",
    title: "shuzihuobiZhanghu",
    iconPath: getImages("headIcon/account/cryptos.png"),
    key: "crypto",
  },
  // 美股
  {
    urlPath: "/wallet/usStocksAccounts",
    title: "meiguZhanghu",
    iconPath: getImages("headIcon/account/us.png"),
    key: "us",
  },
  // 外汇
  {
    urlPath: "/wallet/forexAccounts",
    title: "waihuiZhanghu",
    iconPath: getImages("headIcon/account/forex.png"),
    key: "forex",
  },
  // etf
  {
    urlPath: "/wallet/etfAccounts",
    title: "etfZhanghu",
    iconPath: getImages("headIcon/account/etf.png"),
    key: "etf",
  },
  // // A股
  // {
  //   urlPath: "/wallet/cnStocksAccounts",
  //   title: "AguZhanghu",
  //   iconPath: getImages("headIcon/account/cn.png"),
  //   key: "cn",
  // },
  // // 港股
  // {
  //   urlPath: "/wallet/hkStocksAccounts",
  //   title: "gangguZhanghu",
  //   iconPath: getImages("headIcon/account/hk.png"),
  //   key: "hk",
  // },
  // // 台股
  // {
  //   urlPath: "/wallet/twStocksAccounts",
  //   title: "taiguZhanghu",
  //   iconPath: getImages("headIcon/account/tw.png"),
  //   key: "tw",
  // },
  {
    urlPath: "/wallet/financialAccounts",
    title: "licaizhanghu",
    iconPath: getImages("headIcon/account/financial.png"),
    key: "financial",
  },
  {
    urlPath: "/wallet/assetOverview",
    title: "zichanzonglan",
    iconPath: getImages("headIcon/account/assetOverview.png"),
    key: "assetOverview",
  },
];

export const dingdanList = [
  {
    urlPath: "/order/coinOrder",
    title: "shuzihuobilishi",
    iconPath: getImages("headIcon/wallet-menu/cryptos.png"),
  },
  {
    urlPath: "/order/usStocksOrder",
    title: "meigulishi",
    iconPath: getImages("headIcon/wallet-menu/us.png"),
  },
  {
    urlPath: "/order/forexOrder",
    title: "waihuilishi",
    iconPath: getImages("headIcon/wallet-menu/forex.png"),
  },
  {
    urlPath: "/order/etfOrder",
    title: "etflishi",
    iconPath: getImages("headIcon/wallet-menu/etf.png"),
  },
  // {
  //   urlPath: "/order/cnStocksOrder",
  //   title: "Agulishi",
  //   iconPath: getImages("headIcon/wallet-menu/cn.png"),
  // },
  // {
  //   urlPath: "/order/hkStocksOrder",
  //   title: "ganggulishi",
  //   iconPath: getImages("headIcon/wallet-menu/hk.png"),
  // },
  // {
  //   urlPath: "/order/twStocksOrder",
  //   title: "taigulishi",
  //   iconPath: getImages("headIcon/wallet-menu/tw.png"),
  // },

  {
    urlPath: "/order/financialOrder",
    title: "licailishi",
    iconPath: getImages("headIcon/wallet-menu/financial-account.png"),
  },
  {
    urlPath: "/order/changeRecord",
    title: "zhangbianjilu",
    iconPath: getImages("headIcon/wallet-menu/record.png"),
  },
  {
    urlPath: "/order/walletHistory",
    title: "qianbaolishi",
    iconPath: getImages("headIcon/wallet-menu/wallet-history.png"),
  },
  {
    urlPath: "/order/exchangeOrder",
    title: "duihuanlishi",
    iconPath: getImages("headIcon/wallet-menu/exchange.png"),
  },
];
