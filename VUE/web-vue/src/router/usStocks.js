const usStocksRoutes = [
  {
    path: "/usStocks/spot/:id",
    label: "spot",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/usStocks/spot/index.vue"),
  },
  {
    path: "/usStocks/constract/:id",
    label: "constarct",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/usStocks/constract/index.vue"),
  },
];

export { usStocksRoutes };
