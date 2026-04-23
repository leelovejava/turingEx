const wealthRoutes = [
    {
      meta: {
        commonHeader: true,
      },
      path: '/fundMa',name:'fundMa',component:()=>import("@/views/wealth/financialManage.vue"),//基金理财
    },
    {
      meta: {
        commonHeader: true,
      },
      path: '/fundMakc',name:'FundManageKuangChi',component:()=>import("@/views/wealth/lockMiner.vue"),//矿池锁仓
    },
  ];
  
  export { wealthRoutes };