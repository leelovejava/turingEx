const twStocksRoutes = [
  {
    path: "/twStocks/spot/:id",
    label: "spot",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/twStocks/spot/index.vue"),
  },
  {
    path: "/twStocks/constract/:id",
    label: "constarct",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/twStocks/constract/index.vue"),
  },
];

export { twStocksRoutes };
