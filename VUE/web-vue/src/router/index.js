import { createRouter, createWebHashHistory } from "vue-router";
import { compositeHomeRoutes } from "./compositeHome"; //综合盘首页
import { loginRoutes } from "./login";
import { etfRoutes } from "./etf";
import { coinRoutes } from "./coin";
import { usStocksRoutes } from "./usStocks";
import { cnStocksRoutes } from "./cnStocks";
import { hkStocksRoutes } from "./hkStocks";
import { twStocksRoutes } from "./twStocks";
import { myRoutes } from "./my";
import { walletRoutes } from "./wallet";
import { orderRoutes } from "./order";
import { wealthRoutes } from "./wealth";
import { promoteRoutes } from "./promote";

import { marketRoutes } from "./market";
import { forexRoutes } from "./forex";
import { c2cRoutes } from "./c2c";
import { useLanguageStore } from "@/store/lang";
import { useUserStore } from "@/store/user";
import { newSharesRoutes } from './newShares';

const routes = [
  ...compositeHomeRoutes,
  ...loginRoutes,
  ...etfRoutes,
  ...myRoutes,
  ...orderRoutes,
  ...walletRoutes,
  ...wealthRoutes,
  ...coinRoutes,
  ...usStocksRoutes,
  ...hkStocksRoutes,
  ...cnStocksRoutes,
  ...twStocksRoutes,
  ...marketRoutes,
  ...forexRoutes,
  ...c2cRoutes,
  ...promoteRoutes,
  ...newSharesRoutes,
];
const router = createRouter({
  history: createWebHashHistory(),
  routes,
});
router.beforeEach((to, from, next) => {
  useUserStore();
  useLanguageStore();
  window.scrollTo(0, 0);
  next();
});
export default router;
