// 路由规则
const loginRoutes = [
  {
    path: "/login",
    label: "login",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/login/index.vue"),
  },
  {
    path: "/resetpwd",
    label: "resetpwd",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/login/resetPwd.vue"),
  },
  {
    path: "/register",
    label: "register",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/login/register.vue"),
  },
  {
    path: "/idSet", //实名认证
    label: "idSet",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/login/step2/idSet.vue"),
  },
  {
    path: "/secureBind", //安全绑定
    name: "secureBind",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/login/step3/secureBind.vue"),
  },
  {
    path: "/googleSet",
    name: "googleSet",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/login/step3/googleSet.vue"),
  }, //设置谷歌验证

  // {
  //   path: "/phoneSet",
  //   name: "phoneSet",
  //   meta: {
  //     commonHeader: true,
  //   },
  //   component: () => import("@/views/login/step3/phoneSet.vue"),
  // }, //电话设置
  // {
  //   path: "/emailSet",
  //   name: "emailSet",
  //   meta: {
  //     commonHeader: true,
  //   },
  //   component: () => import("@/views/login/step3/emailSet.vue"),
  // }, //邮箱设置
  // 设置完成去交易
  {
    path: "/setDone",
    name: "setDone",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/login/step4/goTrade.vue"),
  }, //完成列表
];

export { loginRoutes };
