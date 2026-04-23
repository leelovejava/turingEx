const promoteRoutes = [
  {
    meta: {
      commonHeader: true,
    },
    path: '/promote',name:'promote',component:()=>import("@/views/promote/index.vue"),
  },
];

export { promoteRoutes };
