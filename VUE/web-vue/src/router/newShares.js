const newSharesRoutes = [
  {
    meta: {
      commonHeader: true,
    },
    path: "/newShares",
    name: "newShares",
    component: () => import("@/views/newShares/index.vue"),
    redirect:'/newShares/newSharesBox',
    children: [
      {
        path: "/newShares/newSharesBox",
        name: "newSharesBox",
        component: () => import("@/views/newShares/newSharesBox.vue"),
        meta: { title: 'xingurengou' }, // 新股认购
      },
      {
        path: "/newShares/newSharesInventory",
        name: "newSharesInventory",
        component: () => import("@/views/newShares/newSharesInventory.vue"),
        meta: { title: 'xingukucun' }, // 新股库存
      },
      {
        path: "/newShares/nowSharesInventory",
        name: "nowSharesInventory",
        component: () => import("@/views/newShares/nowSharesInventory.vue"),
        meta: { title: 'xiangukucun' }, // 现股库存
      },
    ],
  },
  {
    path: "/newShareSrecord",
    name: "newShareSrecord",
    component: () => import("@/views/newShareSrecord/index.vue"),
    meta: { commonHeader: true },
    children: [
      {
        path: "/newShareSrecord/draw",
        name: "draw",
        component: () => import("@/views/newShareSrecord/draw.vue"),
        meta: { title: 'chouqian' }, // 抽签
      },
      {
        path: "/newShareSrecord/pay",
        name: "pay",
        component: () => import("@/views/newShareSrecord/pay.vue"),
        meta: { title: 'renjiao' }, // 认缴
      },
      {
        path: "/newShareSrecord/payRecord",
        name: "payRecord",
        component: () => import("@/views/newShareSrecord/payRecord.vue"),
        meta: { title: 'renjiaojilu' }, // 认缴记录
      },
      {
        path: "/newShareSrecord/drawRecord",
        name: "drawRecord",
        component: () => import("@/views/newShareSrecord/drawRecord.vue"),
        meta: { title: 'chouqianjilu' }, // 抽签记录
      },
    ],
  },
];

export { newSharesRoutes };
