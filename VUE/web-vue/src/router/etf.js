const etfRoutes = [
  {
    path: "/etf/spot/:id",
    label: "etf",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/etf/spot/index.vue"),
  },
  {
    path: "/etf/constract/:id",
    label: "etfconstarct",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/etf/constract/index.vue"),
  },
];

export { etfRoutes };
