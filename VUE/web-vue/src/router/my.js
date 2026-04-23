const myRoutes = [
  {
    meta: {
      commonHeader: true,
    },
    path: '/my',name:'my',component:()=>import("@/views/my/index.vue"),
		children: [
      { path: '/my/dashboard',name:'dashboard', component:()=>import("@/views/my/dashboard.vue")},//我的资产
      { path: '/my/security',name:'security', component:()=>import('@/views/my/mysecurity.vue')},//帐户安全
      { path: '/my/payment',name:'payment', component:()=>import('@/views/my/payment.vue')},//收款方式
      { path: '/my/universal', name:'universal',component:()=>import('@/views/my/universal.vue')},//通用
      { path:'/my/helpCenter',name:'helpCenter',component:()=>import("@/views/my/helpCenter.vue")}, //帮助中心
      { path:'/my/announcement',name:'Announcement',component:()=>import("@/views/my/announcement.vue")},//公告中心
		]
  },
];

export { myRoutes };
