const c2cRoutes = [
  {
    // c2c
    path: "/c2c",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/c2c/index.vue"),
    children: [
      {
        // 一键买币自选区
        path: "/c2c/wantBuy",
        component: () => import("@/views/c2c/children/wantBuy/index.vue"),
      },

      {
        // 一键买币 - c2c订单
        path: "/c2c/order",
        component: () => import("@/views/c2c/children/c2c-order/index.vue"),
      },

      {
        // 一键买币 - 快捷区
        path: "/c2c/fastBuy",
        component: () => import("@/views/c2c/children/fastBuy/index.vue"),
      },

      {
        // 一键买币 - 下单成功
        path: "/c2c/orderSuccess",
        component: () => import("@/views/c2c/children/orderSuccess/index.vue"),
      },
      {
        // 用户中心
        path: "/c2c/user",
        component: () => import("@/views/c2c/children/user/index.vue"),
      },
    ],
  },
];

export { c2cRoutes };
