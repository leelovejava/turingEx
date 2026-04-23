let language = JSON.parse(localStorage.getItem("lang")) || "en";
const lang = language == "en" ? language : "cn";

const childrenRoutes = [
  {
    path: "/home",
    name: "composite",
    label: "home",
    meta: {
      footer_theme: "black",
    },
    component: () => import("@/views/compositeHomePage/home.vue"),
  }, //标准模版
  // tradingview
  {
    path: "/tradingview",
    label: "tradingview",
    meta: {
      footer_theme: "black",
    },
    component: () => import("@/views/compositeHomePage/tradingview.vue"),
  },
  // header-支持
  {
    path: "/support",
    label: "support",
    meta: {
      footer_theme: "black",
      forexHeader: true,
    },
    component: () => import("@/views/compositeHomePage/header/support.vue"),
  },
  {
    path: "/refer-friend",
    label: "refer-friend",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/header/refer-friend.vue"),
  },
  {
    path: "/group",

    component: () => import("@/views/compositeHomePage/header/group.vue"),
  },
  {
    path: "/partners",

    component: () => import("@/views/compositeHomePage/header/partners.vue"),
  },
  // footer
  {
    path: "/legal-documentation",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/footer/legal-documentation.vue"),
  },
  {
    path: "/contact-us",
    meta: {
      forexHeader: true,
      footer_theme: "black",
    },
    component: () => import("@/views/compositeHomePage/footer/contact-us.vue"),
  },
  // m1
  {
    path: "/why-demo",
    label: "m1-0",
    meta: {
      forexHeader: true,
      footer_theme: "black",
    },
    component: () => import("@/views/compositeHomePage/menu1-why/index.vue"),
  },
  {
    path: "/why-demo/about",
    label: "m1-1",
    meta: {
      forexHeader: true,
    },
    component: () => import("@/views/compositeHomePage/menu1-why/who.vue"),
  },
  {
    path: "/why-demo/awards",
    label: "m1-2",
    meta: {
      forexHeader: true,
    },
    component: () => import("@/views/compositeHomePage/menu1-why/awards.vue"),
  },
  {
    path: "/why-demo/premium-clients",
    label: "m1-3",
    meta: {
      forexHeader: true,
    },
    component: () => import("@/views/compositeHomePage/menu1-why/vip.vue"),
  },
  {
    path: "/why-demo/active-trader-program",
    label: "m1-4",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu1-why/activeTrade.vue"),
  },
  {
    path: "/why-demo/legal-entity-identifier",
    label: "m1-5",
    meta: {
      forexHeader: true,
    },
    component: () => import("@/views/compositeHomePage/menu1-why/LEI.vue"),
  },
  // menu2
  {
    path: "/trading",
    label: "m2-0",
    meta: {
      forexHeader: true,
      footer_theme: "black",
    },
    component: () =>
      import("@/views/compositeHomePage/menu2-trading/index.vue"),
  },
  {
    path: "/trading/instruments",
    label: "m2-9",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu2-trading/instruments/index.vue"),
  },
  {
    path: "/trading/spreads-swaps-commissions",
    label: "m2-3",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu2-trading/spreads-swaps-commissions.vue"
      ),
  },
  {
    path: "/trading/trading-hours",
    label: "m2-3",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu2-trading/trading-hours.vue"),
  },
  {
    path: "/trading/accounts",
    label: "m2-5",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu2-trading/account.vue"),
  },
  {
    path: "/trading/funding-withdrawals",
    label: "m2-6",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu2-trading/funding-withdrawals.vue"),
  },
  {
    path: "/trading/get-started",
    label: "m2-7",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu2-trading/get-started.vue"),
  },
  //menu2-可用工具-子菜单
  {
    path: "/trading/instruments/trade-forex",
    label: "m2-1-1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu2-trading/instruments/trade-forex.vue"
      ),
  },
  {
    path: "/trading/instruments/index-cfds",
    label: "m2-1-2",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu2-trading/instruments/index-cfds.vue"
      ),
  },
  {
    path: "/trading/instruments/commodities",
    label: "m2-1-3",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu2-trading/instruments/commodities.vue"
      ),
  },
  {
    path: "/trading/instruments/share-cfds",
    label: "m2-1-4",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu2-trading/instruments/share-cfds.vue"
      ),
  },
  {
    path: "/trading/instruments/etfs",
    label: "m2-1-5",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu2-trading/instruments/etfs.vue"),
  },
  {
    path: "/trading/instruments/cryptocurrencies",
    label: "m2-1-6",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu2-trading/instruments/cryptocurrencies.vue"
      ),
  },
  {
    path: "/trading/instruments/currency-index-cfds",
    label: "m2-1-7",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu2-trading/instruments/currency-index-cfds.vue"
      ),
  },
  // menu3
  {
    path: "/trading-platforms",
    name: "platform",
    label: "m3-0",
    meta: {
      footer_theme: "black",
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu3-platform/index.vue"),
  },
  {
    path: "/trading-platforms/maintenance-schedule",
    label: "m2-4",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu3-platform/maintenance-schedule.vue"
      ),
  },
  {
    path: "/trading-platforms/platforms",
    label: "m3-1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu3-platform/platforms/index.vue"),
  },
  {
    path: "/trading-platforms/tools",
    label: "m3-2",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu3-platform/tools/index.vue"),
  },
  {
    path: "/trading-platforms/social-trading",
    label: "m3-3",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu3-platform/social-trading/index.vue"
      ),
  },

  //menu3-交易平台-子菜单
  {
    path: "/trading-platforms/platforms/metatrader4",
    label: "m3-1-1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu3-platform/platforms/metatrader4.vue"
      ),
  },
  {
    path: "/trading-platforms/platforms/metatrader5",
    label: "m3-1-2",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu3-platform/platforms/metatrader5.vue"
      ),
  },
  {
    path: "/trading-platforms/platforms/ctrader",
    label: "m3-1-3",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu3-platform/platforms/ctrader.vue"),
  },
  //menu3-工具-子菜单
  {
    path: "/trading-platforms/tools/capitalise-ai",
    label: "m3-2-1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu3-platform/tools/capitalise-ai.vue"
      ),
  },
  {
    path: "/trading-platforms/tools/smart-trader-tools",
    label: "m3-2-2",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu3-platform/tools/smart-trader-tools.vue"
      ),
  },
  {
    path: "/trading-platforms/tools/ctrader-automate",
    label: "m3-2-3",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu3-platform/tools/ctrader-automate.vue"
      ),
  },
  {
    path: "/trading-platforms/tools/autochartist",
    label: "m3-2-4",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu3-platform/tools/autochartist.vue"),
  },
  {
    path: "/trading-platforms/tools/api-trading",
    label: "m3-2-5",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu3-platform/tools/api-trading.vue"),
  },
  {
    path: "/trading-platforms/tools/vps-hosting",
    label: "m3-2-6",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu3-platform/tools/vps-hosting.vue"),
  },
  //menu3-社交平台平台-子菜单
  {
    path: "/trading-platforms/social-trading/myfxbook",
    label: "m3-3-1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu3-platform/social-trading/myfxbook.vue"
      ),
  },
  {
    path: "/trading-platforms/social-trading/metatrader-signals",
    label: "m3-3-2",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu3-platform/social-trading/metatrader-signals.vue"
      ),
  },
  {
    path: "/trading-platforms/social-trading/duplitrade",
    label: "m3-3-3",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu3-platform/social-trading/duplitrade.vue"
      ),
  },
  // menu4
  {
    path: "/market-analysis",
    name: "analysis",
    label: "m4-0",
    meta: {
      footer_theme: "black",
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu4-analysis/index.vue"),
  },
  {
    path: "/market-analysis/market-news",
    label: "m4-1",
    meta: {
      forexHeader: true,
      footer_theme: "black",
    },
    component: () =>
      import("@/views/compositeHomePage/menu4-analysis/market-news.vue"),
  },
  {
    path: "/market-analysis/analysts",
    label: "m4-3",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu4-analysis/analysts.vue"),
  },
  //menu4-市场新闻-文章
  {
    path: "/market-analysis/aug-nfp-preview",
    label: "h-s6-1-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu4-analysis/articles/aug-nfp-preview.vue"
      ),
  },
  {
    path: "/market-analysis/dollar-strength-in-august-gold-fell-sharply",
    label: "h-s6-2-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu4-analysis/articles/dollar-strength-in-august-gold-fell-sharply.vue"
      ),
  },
  {
    path: "/market-analysis/ecb-sep-meeting-preview",
    label: "h-s6-3-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu4-analysis/articles/ecb-sep-meeting-preview.vue"
      ),
  },
  {
    path: "/market-analysis/euro-rebound-as-gas-price-down-sharply",
    label: "h-s6-4-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu4-analysis/articles/euro-rebound-as-gas-price-down-sharply.vue"
      ),
  },
  {
    path: "/market-analysis/forex-market-unlimited-opportunities",
    label: "h-s6-5-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu4-analysis/articles/forex-market-unlimited-opportunities.vue"
      ),
  },
  {
    path: "/market-analysis/opec-cut-production-gas-surge-euro-down",
    label: "h-s6-6-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu4-analysis/articles/opec-cut-production-gas-surge-euro-down.vue"
      ),
  },
  {
    path: "/market-analysis/the-week-ahead-fed-remained-hawkish-dollar-20-year-high",
    label: "h-s6-7-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu4-analysis/articles/the-week-ahead-fed-remained-hawkish-dollar-20-year-high.vue"
      ),
  },
  {
    path: "/market-analysis/the-week-ahead-us-cpi",
    label: "h-s6-8-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu4-analysis/articles/the-week-ahead-us-cpi.vue"
      ),
  },
  {
    path: "/market-analysis/trade-global-equity-markets",
    label: "h-s6-9-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu4-analysis/articles/trade-global-equity-markets-with-pepperstone.vue"
      ),
  },
  {
    path: "/market-analysis/us-aug-cpi-review-risk-sentiment-changed",
    label: "h-s6-10-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu4-analysis/articles/us-aug-cpi-review-risk-sentiment-changed.vue"
      ),
  },
  {
    path: "/market-analysis/us-august-cpi-preview",
    label: "h-s6-11-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu4-analysis/articles/us-august-cpi-preview.vue"
      ),
  },
  {
    path: "/market-analysis/week-ahead-with-ecb-opec-rba-boc",
    label: "h-s6-12-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu4-analysis/articles/week-ahead-with-ecb-opec-rba-boc.vue"
      ),
  },
  {
    path: "/market-analysis/what-will-take-gold",
    label: "h-s6-13-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu4-analysis/articles/what-will-take-gold.vue"
      ),
  },
  {
    path: "/market-analysis/what-you-should-know-about-oil-trading",
    label: "h-s6-14-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu4-analysis/articles/what-you-should-know-about-oil-trading.vue"
      ),
  },
  // menu5
  {
    path: "/education",
    label: "m5-0",
    name: "education",
    meta: {
      forexHeader: true,
      footer_theme: "black",
    },
    component: () =>
      import("@/views/compositeHomePage/menu5-educations/index.vue"),
  },
  {
    path: "/education/learn-forex",
    label: "m5-1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/learn-forex/index.vue"
      ),
  },
  {
    path: "/education/learn-trade-cfds",
    label: "m5-2",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import("@/views/compositeHomePage/menu5-educations/learn-trade-cfds.vue"),
  },
  {
    path: "/education/trading-guides",
    label: "m5-3",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/trading-guides/index.vue"
      ),
  },
  //交易指南-子菜单
  {
    path: "/education/11-tips-improving-performance-expert-advisor",
    label: "m5-tg-1-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/trading-guides/11-tips-improving-performance-expert-advisor.vue"
      ),
  },
  {
    path: "/education/extreme-volatility",
    label: "m5-tg-2-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/trading-guides/extreme-volatility.vue"
      ),
  },
  {
    path: "/education/why-spread",
    label: "m5-tg-3-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/trading-guides/why-spread.vue"
      ),
  },
  {
    path: "/education/down-up",
    label: "m5-tg-4-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/trading-guides/down-up.vue"
      ),
  },
  {
    path: "/education/VIX",
    label: "m5-tg-5-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/trading-guides/VIX.vue"
      ),
  },
  {
    path: "/education/what-trader-type-are-you",
    label: "m5-tg-6-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/trading-guides/what-trader-type-are-you.vue"
      ),
  },
  {
    path: "/education/how-trade-gold",
    label: "m5-tg-7-t1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/trading-guides/how-trade-gold.vue"
      ),
  },

  // 学习外汇-子菜单
  {
    path: "/education/learn-forex/10-trading-terms",
    label: "m5-1-1",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/learn-forex/10-trading-terms.vue"
      ),
  },
  {
    path: "/education/learn-forex/what-is-pip-value",
    label: "m5-1-2",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/learn-forex/what-is-pip-value.vue"
      ),
  },
  {
    path: "/education/learn-forex/risk-management",
    label: "m5-1-3",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/learn-forex/risk-management.vue"
      ),
  },
  {
    path: "/education/learn-forex/trend-lines",
    label: "m5-1-4",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/learn-forex/trend-lines.vue"
      ),
  },
  {
    path: "/education/learn-forex/technical-analysis",
    label: "m5-1-5",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/learn-forex/technical-analysis.vue"
      ),
  },
  {
    path: "/education/learn-forex/trading-other-instruments",
    label: "m5-1-6",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/learn-forex/trading-other-instruments.vue"
      ),
  },
  {
    path: "/education/learn-forex/how-forex-works",
    label: "m5-1-7",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/learn-forex/how-forex-works.vue"
      ),
  },
  {
    path: "/education/learn-forex/fundamental-analysis",
    label: "m5-1-8",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/learn-forex/fundamental-analysis.vue"
      ),
  },

  {
    path: "/education/learn-forex/technical-indicators",
    label: "m5-1-9",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/learn-forex/technical-indicators.vue"
      ),
  },
  {
    path: "/education/learn-forex/what-is-swap-in-forex",
    label: "m5-1-10",
    meta: {
      forexHeader: true,
    },
    component: () =>
      import(
        "@/views/compositeHomePage/menu5-educations/learn-forex/what-is-swap-in-forex.vue"
      ),
  },
];
const compositeHomeRoutes = [
  {
    path: "/",
    redirect: `/home`,
    component: () => import("@/views/compositeHomePage/index.vue"),
    children: childrenRoutes,
  },
];

export { compositeHomeRoutes };
