const forexRoutes = [
  // {
  //   path: "/forex/spot/:id",
  //   label: "spot",
  //   meta: {
  //     commonHeader: true,
  //   },
  //   component: () => import("@/views/forex/spot/index.vue"),
  // },
  {
    path: "/forex/constract/:id",
    label: "constarct",
    meta: {
      commonHeader: true,
    },
    component: () => import("@/views/forex/constract/index.vue"),
  },
];

export { forexRoutes };
