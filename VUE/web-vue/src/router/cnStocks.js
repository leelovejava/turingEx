const cnStocksRoutes = [
  {
    path: "/cnStocks/spot/:id",
    label: "spot",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/cnStocks/spot/index.vue"),
  },
  {
    path: "/cnStocks/constract/:id",
    label: "constarct",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/cnStocks/constract/index.vue"),
  },
];

export { cnStocksRoutes };
