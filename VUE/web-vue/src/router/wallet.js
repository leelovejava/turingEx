const walletRoutes = [
  {
    meta: {
      commonHeader: true,
    },
    path: "/wallet",
    name: "walletIndex",
    component: () => import("@/views/wallet/index.vue"),
    children: [
      {
        path: "/wallet/walletOverview",
        name: "walletOverview",
        component: () => import("@/views/wallet/menu/walletOverview.vue"),
      }, //钱包总览
      {
        path: "/wallet/financialAccounts",
        name: "financialAccounts",
        component: () => import("@/views/wallet/menu/financial.vue"),
      }, //理财账户

      {
        path: "/wallet/etfAccounts",
        name: "etfAccounts",
        component: () => import("@/views/wallet/menu/etf.vue"),
      }, //etf
      {
        path: "/wallet/cryptosAccounts",
        name: "cryptosAccounts",
        component: () => import("@/views/wallet/menu/cryptos.vue"),
      }, //cryptos
      {
        path: "/wallet/forexAccounts",
        name: "forexAccounts",
        component: () => import("@/views/wallet/menu/forex.vue"),
      }, //forex
      {
        path: "/wallet/usStocksAccounts",
        name: "usStocksAccounts",
        component: () => import("@/views/wallet/menu/usStocks.vue"),
      }, //us
      {
        path: "/wallet/cnStocksAccounts",
        name: "cnStocksAccounts",
        component: () => import("@/views/wallet/menu/cnStocks.vue"),
      }, //cn
      {
        path: "/wallet/hkStocksAccounts",
        name: "hkStocksAccounts",
        component: () => import("@/views/wallet/menu/hkStocks.vue"),
      }, //hk
      {
        path: "/wallet/twStocksAccounts",
        name: "twStocksAccounts",
        component: () => import("@/views/wallet/menu/twStocks.vue"),
      }, //tw
      {
        path: "/wallet/assetOverview",
        name: "assetOverview",
        component: () => import("@/views/wallet/menu/assetOverview.vue"),
      }, //资产总览
    ],
  },

  {
    meta: {
      commonHeader: true,
    },
    path: "/recharge",
    name: "recharge",
    component: () => import("@/views/wallet/recharge/index.vue"),
  }, //充值
  {
    meta: {
      commonHeader: true,
    },
    path: "/recharge/record",
    name: "recharge-record",
    component: () => import("@/views/wallet/recharge/record.vue"),
  }, //充值记录
  {
    meta: {
      commonHeader: true,
    },
    path: "/recharge/detail",
    name: "recharge-detail",
    component: () => import("@/views/wallet/recharge/detail.vue"),
  }, //充值详情
  {
    meta: {
      commonHeader: true,
    },
    path: "/recharge/bank",
    name: "recharge-bank",
    component: () => import("@/views/wallet/recharge/bank.vue"),
  }, //银行卡充值
  {
    meta: {
      commonHeader: true,
    },
    path: "/bank/orderSuccess",
    name: "recharge-bank-success",
    component: () => import("@/views/wallet/recharge/successOrder.vue"),
  }, //银行卡充值成功页面

  {
    meta: {
      commonHeader: true,
    },
    path: "/recharge/usdt",
    name: "recharge-usdt",
    component: () => import("@/views/wallet/recharge/usdt.vue"),
  }, //usdt充值
  {
    meta: {
      commonHeader: true,
    },
    path: "/withdraw",
    name: "withdraw",
    component: () => import("@/views/wallet/withdraw/index.vue"),
  }, //提现
  {
    meta: {
      commonHeader: true,
    },
    path: "/withdraw/record",
    name: "withdraw-record",
    component: () => import("@/views/wallet/withdraw/record.vue"),
  }, //提现记录
  {
    meta: {
      commonHeader: true,
    },
    path: "/withdraw/detail",
    name: "withdraw-detail",
    component: () => import("@/views/wallet/withdraw/detail.vue"),
  }, //提现详情
  {
    meta: {
      commonHeader: true,
    },
    path: "/withdraw/bank",
    name: "withdraw-bank",
    component: () => import("@/views/wallet/withdraw/bank.vue"),
  }, //银行卡提现
  {
    meta: {
      commonHeader: true,
    },
    path: "/withdraw/usdt",
    name: "withdraw-usdt",
    component: () => import("@/views/wallet/withdraw/usdt.vue"),
  }, //usdt提现
  {
    meta: {
      commonHeader: true,
    },
    path: "/exchange",
    name: "exchange",
    component: () => import("@/views/wallet/exchange.vue"),
  }, //兑换

  {
    meta: {
      commonHeader: true,
    },
    path: "/addressma",
    name: "address-management",
    component: () => import("@/views/wallet/addressManagement.vue"),
  }, //地址管理
  {
    meta: {
      commonHeader: true,
    },
    path: "/addressAddBank",
    name: "addressAddBank",
    component: () => import("@/views/wallet/addressAddBank.vue"),
  }, //编辑/添加 银行卡
];

export { walletRoutes };
