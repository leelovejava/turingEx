const marketRoutes = [
    {
      meta: {
        commonHeader: true,
      },
      path: '/market',name:'market',component:()=>import("@/views/market/market.vue"),//行情
    },
  ];
  
  export { marketRoutes };