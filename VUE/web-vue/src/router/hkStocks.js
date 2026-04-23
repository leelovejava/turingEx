const hkStocksRoutes = [
  {
    path: "/hkStocks/spot/:id",
    label: "spot",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/hkStocks/spot/index.vue"),
  },
  {
    path: "/hkStocks/constract/:id",
    label: "constarct",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/hkStocks/constract/index.vue"),
  },
];

export { hkStocksRoutes };
