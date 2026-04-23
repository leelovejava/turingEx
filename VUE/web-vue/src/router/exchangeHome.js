const homeChildRoutes = [
  {
    path: "/index",
    name: "exchange-homepage",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/exchangeHomePage/index.vue"),
  }, //交易页首页
  {
    path: "/aboutUs",
    name: "aboutUs",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/exchangeHomePage/other/Introduction.vue"),
  }, //关于我们
  {
    path: "/serviceTerms",
    name: "serviceTerms",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/exchangeHomePage/other/serviceTerms.vue"),
  }, //服务协议
  {
    path: "/privacy",
    name: "privacy",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/exchangeHomePage/other/privacy1.vue"),
  }, //隐私声明
  {
    path: "/clause",
    name: "clause",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/exchangeHomePage/other/clause.vue"),
  }, //使用条款

  {
    path: "/riskStatement",
    name: "riskStatement",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/exchangeHomePage/other/riskStatement.vue"),
  }, //风险声明
  {
    path: "/reminder",
    name: "reminder",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/exchangeHomePage/other/reminder.vue"),
  }, //温馨提示
  {
    path: "/purchase",
    name: "purchase",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/exchangeHomePage/other/purchase.vue"),
  }, //购置与转换
  {
    path: "/help",
    name: "help",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/exchangeHomePage/other/help.vue"),
  }, //帮助
];

const exchangeHomeRoutes = [
  {
    path: "/",
    redirect: "/index",
    component: () => import("@/views/exchangeHomePage/index.vue"),
    children: homeChildRoutes,
  },
];

export { exchangeHomeRoutes };
