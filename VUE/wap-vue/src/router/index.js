import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from "@/store/user.js";

// 路由规则
const routes = [
  {
    path: '/',
    children: [
      { path: '', redirect: '/quotes' },
      {
        path: '/login',
        name: 'Login',
        component: () => import(/* webpackChunkName: "login" */ '@/views/login/index.vue'),
      },
      {
        path: '/optional',
        name: 'optional',
        meta: {
          tarbar: true,
        },
        redirect: '/optional/index',
        component: () => import('@/views/Layout.vue'),
        children: [
          { path: 'index', meta: { tarbar: true, }, component: () => import(/* webpackChunkName: "optional" */ '@/views/optional/index.vue') },
          { path: 'search', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "optionalsearch" */ '@/views/optional/Search.vue') },
          { path: 'groupListManagement', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "optionalgroupListManagement" */ '@/views/optional/GroupListManagement.vue') },
          { path: 'groupEdit', meta: { tarbar: true }, component: () => import(/* webpackChunkName: "optionalgroupEdit" */ '@/views/optional/GroupEditOrAdd.vue') },
          { path: 'groupAdd', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "optionalgroupAdd" */ '@/views/optional/GroupEditOrAdd.vue') },
          { path: 'selectSymbol', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "optionalselectSymbol" */ '@/views/optional/SelectSymbol.vue') },
          { path: 'editGroupList', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "optionaleditGroupList" */ '@/views/optional/EditGroupList.vue') },
          { path: '/order/:symbol', component: () => import(/* webpackChunkName: "optionalorder" */ '@/views/charts/order.vue') },
          { path: 'result', component: () => import(/* webpackChunkName: "optionalresult" */ '@/views/charts/result.vue') },
        ]
      },
      {
        path: '/quotes',
        name: 'Quotes',
        redirect: '/quotes/index',
        component: () => import('@/views/Layout.vue'),
        children: [
          { path: 'index', meta: { tarbar: true, }, name: 'quotesIndex', component: () => import(/* webpackChunkName: "quotes" */ '@/views/quotes/List.vue') },
          { path: 'hotGallery', meta: { tarbar: true }, name: 'quotesHotGallery', component: () => import(/* webpackChunkName: "quotesHotGallery" */ '@/views/quotes/HotGallery.vue') },
          { path: 'detail', component: () => import(/* webpackChunkName: "detail" */ '@/views/quotes/Detail.vue') },
          { path: 'constituentDetail', meta: { tarbar: true }, component: () => import(/* webpackChunkName: "constituentDetail" */ '@/views/quotes/ConstituentDetail.vue') },
          { path: 'usStockDetail', component: () => import(/* webpackChunkName: "usStockDetail" */ '@/views/usStock/Detail.vue') },
          { path: 'usStockIndexDetail', meta: { tarbar: true, }, component: () => import(/* webpackChunkName: "usStockIndexDetail" */ '@/views/usStock/IndexDetail.vue') },
          { path: 'hotModules', meta: { tarbar: true }, component: () => import(/* webpackChunkName: "hotModules" */ '@/views/quotes/HotModules.vue') },
          { path: 'openTrade', meta: { tarbar: false, }, component: () => import(/* webpackChunkName: "openTrade" */ '@/views/quotes/OpenTrade.vue') },
          { path: 'UsStockMore', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "UsStockMore" */ '@/views/quotes/UsStockMore.vue') },

        ]
      },
      {
        path: '/trade',
        name: 'Trade',
        meta: {
          tarbar: true,
          requireAuth: true
        },
        redirect: '/trade/index',
        component: () => import('@/views/Layout.vue'),
        children: [
          { path: 'index', meta: { tarbar: true }, name: 'tradeIndex', component: () => import(/* webpackChunkName: "trade" */ /* webpackPrefetch: true */'@/views/trade/index.vue') },
        ]
      },
      {
        path: '/foreign',
        name: 'Foreign',
        redirect: '/quotes/index?tabActive=2',
        meta: {
          tarbar: false,
        },
        component: () => import('@/views/Layout.vue'),
        children: [
          { path: 'search', name: 'search', component: () => import(/* webpackChunkName: "search" */ '@/views/foreign/Search.vue') },
          { path: 'quotation', name: 'quotation', component: () => import(/* webpackChunkName: "quotation" */ '@/views/foreign/Quotation.vue') },
          { path: 'coinChart', name: 'coinChart', component: () => import(/* webpackChunkName: "coinChart" */ '@/views/foreign/CoinChart.vue') },
          { path: 'opening', name: 'opening', component: () => import(/* webpackChunkName: "opening" */ '@/views/foreign/Open.vue'), props: true },
          { //交割合约
            path: 'deliveryContract/:symbol',
            name: 'deliveryContract',
            component: () => import(/* webpackChunkName: "deliveryContract" */ '@/views/foreign/foreignPerpetualContract/deliveryContract.vue'),
            meta: { tarbar: false, }
          },
          {
            //交割合约历史
            path: 'ForexDeliveryContractHistory',
            name: 'ForexDeliveryContractHistory',
            component: () => import(/* webpackChunkName: "ForexDeliveryContractHistory" */ '@/views/foreign/deliveryContractHistory/index.vue')
          },
        ]
      },
      {
        path: '/position',
        name: 'position',
        redirect: '/position/index',
        meta: {
          tarbar: false,
        },
        component: () => import('@/views/Layout.vue'),
        children: [
          { path: 'index', meta: { tarbar: false, }, component: () => import(/* webpackChunkName: "position" */'@/views/position/index.vue') },
        ]
      },
      {
        path: '/history',
        name: 'History',
        component: () => import('@/views/Layout.vue'),
        meta: { tarbar: false },
        redirect: '/history/list',
        children: [
          { path: 'list', meta: { tarbar: false, }, component: () => import(/* webpackChunkName: "history" */ '@/views/history/List.vue') }
        ]
      },
      {
        path: '/news',
        name: 'News',
        redirect: '/news/index',
        component: () => import('@/views/Layout.vue'),
        children: [
          { path: 'index', meta: { tarbar: true }, component: () => import(/* webpackChunkName: "news" */ '@/views/news/index.vue') },
        ]
      },
      {
        path: '/funds',
        name: 'Funds',
        redirect: '/funds/index',
        component: () => import('@/views/Layout.vue'),
        children: [
          { path: 'index', meta: { tarbar: true }, component: () => import(/* webpackChunkName: "Funds" */'@/views/cryptos/Funds/list.vue') },
        ]
      },
      {
        path: '/chart',
        name: 'Chart',
        redirect: '/chart/index',
        component: () => import('@/views/Layout.vue'),
        children: [
          { path: 'index', meta: { tarbar: true }, component: () => import(/* webpackChunkName: "chart" */ '@/views/charts/index.vue') },
          { path: '/order/:symbol', component: () => import(/* webpackChunkName: "chart" */ '@/views/charts/order.vue') },
          { path: 'result', component: () => import(/* webpackChunkName: "chart" */ '@/views/charts/result.vue') },
        ]
      },
      {
        path: '/exchangeHistory',
        name: 'ExchangeHistory',
        redirect: '/exchangeHistory/dailyDeal',
        component: () => import('@/views/Layout.vue'),
        children: [
          { path: 'dailyDeal', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "dailyDeal" */ '@/views/exchangeHistory/DailyDeal.vue') },
          { path: 'dailyEntrust', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "dailyEntrust" */ '@/views/exchangeHistory/DailyEntrust.vue') },
          { path: 'historicalEntrust', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "historicalEntrust" */ '@/views/exchangeHistory/HistoricalEntrust.vue') },
          { path: 'historicalTransaction', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "historicalTransaction" */ '@/views/exchangeHistory/HistoricalTransaction.vue') },
          { path: 'search', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "search" */ '@/views/exchangeHistory/Search.vue') },
          { path: 'tradeRecord', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "tradeRecord" */ '@/views/exchangeHistory/tradeRecord.vue') },
        ]
      },
      {
        path: '/exchange',
        name: 'Exchange',
        redirect: '/exchange/list',
        // meta: { tarbar: true },
        component: () => import('@/views/Layout.vue'),
        children: [
          { path: 'list', meta: { tarbar: true, keepAlive: true, requireAuth: true }, component: () => import(/* webpackChunkName: "list" */ '@/views/exchange/List.vue') },
          { path: 'channel-in', name: 'channelIn', component: () => import(/* webpackChunkName: "channel-in" */ '@/views/exchange/Channel.vue') },
          { path: 'channel-out', name: 'channelOut', component: () => import(/* webpackChunkName: "channel-out" */ '@/views/exchange/Channel.vue') },
          { path: 'charge-bank', component: () => import(/* webpackChunkName: "charge-bank" */ '@/views/exchange/charge-bank.vue') },
          { path: 'charge-crypto', component: () => import(/* webpackChunkName: "charge-crypto" */ '@/views/exchange/charge-crypto.vue') },
          { path: 'warehouse', component: () => import(/* webpackChunkName: "warehouse" */ '@/views/exchange/warehouse.vue') },
          { path: 'withdraw-bank', component: () => import(/* webpackChunkName: "withdraw-bank" */ '@/views/exchange/withdraw-bank.vue') }, //银行卡提现
          { path: 'fund-password-verify', component: () => import(/* webpackChunkName: "fund-password-verify" */ '@/views/exchange/FundPasswordVerify.vue') }, //资金密码验证
          { path: 'withdraw-usdt', component: () => import(/* webpackChunkName: "withdraw-usdt" */ '@/views/exchange/withdraw-usdt.vue') }, //usdt提现
        ]
      },
      {
        //加密货币
        path: '/cryptos',
        name: 'Cryptos',
        redirect: '/quotes/index?tabActive=1',
        meta: {
          tarbar: false,
        },
        component: () => import('@/views/Layout.vue'),
        children: [
          {
            path: 'aiQuant/choose',
            name: 'aiQuantChoose',
            meta: { tarbar: false },
            component: () => import(/* webpackChunkName: "aiQuantChoose" */ '@/views/cryptos/AiQuant/Choose.vue'),
          },
          {
            path: 'aiQuant/questionnaire',
            name: 'aiQuantQuestionnaire',
            meta: { tarbar: false },
            component: () =>
              import(
                /* webpackChunkName: "aiQuantQuestionnaire" */ '@/views/cryptos/AiQuant/TraderQuestionnaire.vue'
              ),
          },
          {
            path: 'aiQuant/traderAgreement',
            name: 'aiQuantTraderAgreement',
            meta: { tarbar: false },
            component: () =>
              import(
                /* webpackChunkName: "aiQuantTraderAgreement" */ '@/views/cryptos/AiQuant/TraderAgreement.vue'
              ),
          },
          {
            path: 'aiQuant/earnings/:id',
            name: 'aiQuantEarningsDetail',
            meta: { tarbar: false },
            component: () =>
              import(/* webpackChunkName: "aiQuantEarningsDetail" */ '@/views/cryptos/AiQuant/EarningsDetail.vue'),
          },
          {
            path: 'aiQuant/earnings/:id/income',
            name: 'aiQuantEarnings',
            meta: { tarbar: false },
            component: () =>
              import(/* webpackChunkName: "aiQuantEarnings" */ '@/views/cryptos/AiQuant/EarningsList.vue'),
          },
          {
            path: 'aiQuant',
            name: 'aiQuant',
            meta: { tarbar: false },
            component: () => import(/* webpackChunkName: "aiQuant" */ '@/views/cryptos/AiQuant/index.vue'),
          },
          { path: 'announce', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "cryptosannounce" */'@/views/cryptos/Announce/index.vue') },
          { path: 'announceDetail', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "cryptosannounceDetail" */'@/views/cryptos/Announce/announceDetail.vue') },
          { path: 'exchangePage', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "cryptosexchangePage" */'@/views/cryptos/Exchange/exchangePage.vue') },
          { path: 'exchangeHistory', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "cryptosexchangeHistory" */'@/views/cryptos/Exchange/exchangeHistory.vue') },
          { path: 'exchangeSubmit', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "cryptosexchangeSubmit" */'@/views/cryptos/Exchange/exchangeSubmit.vue') },
          { path: 'exchangeRate', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "cryptosexchangeRate" */'@/views/cryptos/Exchange/exchangeRate.vue') },
          { path: 'accountChange', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "cryptosaccountChange" */'@/views/cryptos/AccountChange/index.vue') },
          { path: 'funds', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "cryptosfunds" */'@/views/cryptos/Funds/index.vue') },
          { path: 'assetsCenter', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "cryptosassetsCenter" */'@/views/cryptos/AssetsCenter/index.vue') },
          { path: 'trade/:symbol', name: 'trade', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "cryptostrade" */ /* webpackPrefetch: true */'@/views/cryptos/Trade/index.vue') },
          { path: 'trendDetails/:symbol', name: 'tradeDetail', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "cryptostrendDetails" */ /* webpackPrefetch: true */'@/views/cryptos/TrendDetails/index.vue') },
          { path: 'tradeRecord/:symbol', name: 'tradeRecord', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "cryptostradeRecord" */'@/views/cryptos/TradeRecord/index.vue') },
          { //永续合约
            path: 'perpetualContract/:symbol',
            name: 'perpetualContract',
            meta: { tarbar: false },
            component: () => import(/* webpackChunkName: "perpetualContract" */ /* webpackPrefetch: true */'@/views/cryptos/PerpetualContract/index.vue'),
          },
          { //充值列表
            path: 'recharge/rechargeList',
            name: 'rechargeList',
            meta: { tarbar: false },
            component: () => import(/* webpackChunkName: "rechargeList" */ '@/views/cryptos/Recharge/rechargeList.vue'),
          },
          {
            //充值页面
            path: "recharge/rechargePage",
            name: "rechargePage",
            meta: { tarbar: false },
            component: () =>
              import(
                /* webpackChunkName: "rechargePage" */ "@/views/cryptos/Recharge/rechargePage.vue"
              ),
          },
          {
            //充值提交
            path: "recharge/rechargeSubmit",
            name: "rechargeSubmit",
            meta: { tarbar: false },
            component: () =>
              import(
                /* webpackChunkName: "rechargeSubmit" */ "@/views/cryptos/Recharge/rechargeSubmit.vue"
              ),
          },
          {
            //充值详情
            path: "recharge/rechargeDetail",
            name: "rechargeDetail",
            meta: { tarbar: false },
            component: () =>
              import(
                /* webpackChunkName: "rechargeDetail" */ "@/views/cryptos/Recharge/rechargeDetail.vue"
              ),
          },
          {
            path: "assetsCenter/index",
            name: "Assets",
            meta: { tarbar: false },
            component: () =>
              import(
                    /* webpackChunkName: "Assets" */ "@/views/cryptos/AssetsCenter/index.vue"
              ),
          },
          {
            //充提记录
            path: "assetsCenter/rechargeWithdrawRecord",
            name: "rechargeWithdrawRecord",
            meta: { tarbar: false },
            component: () =>
              import(
                /* webpackChunkName: "rechargeWithdrawRecord" */ "@/views/cryptos/AssetsCenter/rechargeWithdrawRecord.vue"
              ),
          },
          {
            //提现页面
            path: "withdraw/withdrawPage",
            name: "withdrawPage",
            meta: { tarbar: false },
            component: () =>
              import(
                /* webpackChunkName: "withdrawPage" */ "@/views/cryptos/Withdraw/withdrawPage.vue"
              ),
          },
          {
            //输入资金密码
            path: "withdraw/securityVerification",
            name: "securityVerification",
            meta: { tarbar: false },
            component: () =>
              import(
                /* webpackChunkName: "securityVerification" */ "@/views/cryptos/Withdraw/withdrawalSecurityVerification.vue"
              ),
          },
          {
            //提交成功
            path: "withdraw/withdrawSumbit",
            name: "withdrawSumbit",
            meta: { tarbar: false },
            component: () =>
              import(
                /* webpackChunkName: "withdrawSumbit" */ "@/views/cryptos/Withdraw/withdrawSumbit.vue"
              ),
          },
          {
            //详情
            path: "withdraw/withdrawDetail",
            name: "withdrawDetail",
            meta: { tarbar: false },
            component: () =>
              import(
                /* webpackChunkName: "withdrawDetail" */ "@/views/cryptos/Withdraw/withdrawDetail.vue"
              ),
          },
          {
            //充值详情
            path: "recharge/rechargeDetail",
            name: "rechargeDetail",
            meta: { tarbar: false },
            component: () =>
              import(
                /* webpackChunkName: "rechargeDetail" */ "@/views/cryptos/Recharge/rechargeDetail.vue"
              ),
          },
          { //U本位历史
            path: 'perpetualHistory',
            name: 'perpetualHistory',
            component: () => import(/* webpackChunkName: "perpetualHistory" */ '@/views/cryptos/PerpetualContract/perpetualHistory.vue')
          },
          {
            //交割合约历史
            path: 'deliveryContractHistory',
            name: 'DeliveryContractHistory',
            component: () => import(/* webpackChunkName: "DeliveryContractHistory" */ '@/views/cryptos/DeliveryContractHistory/index.vue')
          },
          { //永续合约订单详情
            path: 'orderDetail',
            name: 'orderDetail',
            component: () => import(/* webpackChunkName: "orderDetail" */ '@/views/cryptos/PerpetualContract/orderDetail.vue')
          },
          { //永续合约委托详情
            path: 'entrustDetail',
            name: 'entrustDetail',
            component: () => import(/* webpackChunkName: "entrustDetail" */ '@/views/cryptos/PerpetualContract/entrustDetail.vue')
          },
          { //永续合约订单详情
            path: 'symbolOrderDetail',
            name: 'symbolOrderDetail',
            component: () => import(/* webpackChunkName: "symbolOrderDetail" */ '@/views/cryptos/SymbolOrderDetail/index.vue')
          },
          {
            //理财历史
            path: 'financialHistory',
            name: 'FinancialHistory',
            component: () => import(/* webpackChunkName: "FinancialHistory" */ '@/views/cryptos/FinancialHistory/index.vue')
          },

          {
            //货币理财盈亏订单详情
            path: "profitLoss",
            name: "profitLoss",
            component: () => import(/* webpackChunkName: "profitLoss" */ "@/views/cryptos/order/CurrencyOrder/profitLoss.vue")
          },
          {
            //理财订单详情
            path: "financialOrder",
            name: "FinancialOrder",
            component: () => import(/* webpackChunkName: "FinancialOrder" */ "@/views/cryptos/order/FinancialOrder/index.vue")
          },
          {
            //矿机订单详情
            path: "miningMachineOrder",
            name: "MiningMachineOrder",
            component: () => import(/* webpackChunkName: "MiningMachineOrder" */ "@/views/cryptos/order/MiningMachineOrder/index.vue")
          },
          {
            //矿机理财购买确认
            path: "machine-confirm",
            name: "MachineConfirm",
            component: () => import(/* webpackChunkName: "MachineConfirm" */ "@/views/cryptos/financialManagement/machineConfirm.vue")
          },
          {
            //基金理财购买确认
            path: "financial-confirm",
            name: "FinancialConfirm",
            component: () => import(/* webpackChunkName: "FinancialConfirm" */ "@/views/cryptos/financialManagement/FinancialManagementConfirm.vue")
          },
          {
            path: "fund-buy",
            name: "FundBuy",
            component: () =>
              import(/* webpackChunkName: "FundBuy" */ "@/views/cryptos/financialManagement/fundBuy.vue"),
          },
          {
            path: "fund",
            name: "Fund",
            component: () =>
              import(/* webpackChunkName: "Fund" */ "@/views/cryptos/financialManagement/fundHome.vue"),
          },
          {
            path: "fund-rule",
            name: "Fundrule",
            component: () =>
              import(/* webpackChunkName: "Fundrule" */ "@/views/cryptos/financialManagement/fundRule.vue"),
          },
          {
            path: "fm-home",
            name: "financialManagement",
            component: () =>
              import(/* webpackChunkName: "financialManagement" */ "@/views/cryptos/financialManagement/index.vue"),
          },
          {
            path: "machine-buy",
            name: "MachineBuy",
            component: () =>
              import(/* webpackChunkName: "MachineBuy" */ "@/views/cryptos/financialManagement/machineBuy.vue"),
          },
          {
            path: "machine-rule",
            name: "Machinerule",
            component: () =>
              import(/* webpackChunkName: "Machinerule" */ "@/views/cryptos/financialManagement/machineRule.vue"),
          },
          {
            path: "machine",
            name: "Machine",
            component: () =>
              import(/* webpackChunkName: "Machine" */ "@/views/cryptos/financialManagement/miningMachineHome.vue"),
          },

          {//
            path: 'order-success',
            name: 'orderSuccess',
            component: () =>
              import(/* webpackChunkName: "orderSuccess" */ '@/views/cryptos/financialManagement/orderSuccess.vue')
          },
          {
            path: "pool-lock",
            name: "PooLock",
            component: () =>
              import(/* webpackChunkName: "PooLock" */ "@/views/cryptos/financialManagement/poolLock.vue"),
          },
          {//质押借币
            path: 'pledgeLoan',
            name: 'PledgeLoan',
            component: () => import(/* webpackChunkName: "PledgeLoan" */ '@/views/cryptos/pledgeLoan/index.vue')
          },
          {//质押订单
            path: 'pledgeLoanOrder',
            name: 'pledgeLoanOrder',
            component: () => import(/* webpackChunkName: "pledgeLoanOrder" */ '@/views/cryptos/pledgeLoan/pledgeLoanOrder.vue')
          },
          {//质押订单详情
            path: 'pledgeLoanOrderDetail',
            name: 'PledgeLoanOrderDetail',
            component: () => import(/* webpackChunkName: "PledgeLoanOrderDetail" */ '@/views/cryptos/pledgeLoan/pledgeLoanOrderDetail.vue')
          },
          {//新增质押
            path: 'addPledge',
            name: 'AddPledge',
            component: () => import(/* webpackChunkName: "AddPledge" */ '@/views/cryptos/pledgeLoan/addPledge.vue')
          },
          {//续借
            path: 'pledgeLoanRenew',
            name: 'PledgeLoanRenew',
            component: () => import(/* webpackChunkName: "PledgeLoanRenew" */ '@/views/cryptos/pledgeLoan/pledgeLoanRenew.vue')
          },
          {//质押记录
            path: 'pledgeRecord',
            name: 'PledgeRecord',
            component: () => import(/* webpackChunkName: "PledgeRecord" */ '@/views/cryptos/pledgeLoan/pledgeRecord.vue')
          },
          {//还款
            path: 'repayment',
            name: 'Repayment',
            component: () => import(/* webpackChunkName: "Repayment" */ '@/views/cryptos/pledgeLoan/repayment.vue')
          },
          {//
            path: 'loan',
            name: 'loan',
            component: () => import(/* webpackChunkName: "loan" */ "@/views/cryptos/loan/index.vue")
          },
          {
            path: 'repayCrypto',
            name: 'repayCrypto',
            component: () => import(/* webpackChunkName: "repayCrypto" */ "@/views/cryptos/loan/repay-crypto.vue")
          },
          {
            path: 'repayList',
            name: 'repayList',
            component: () => import(/* webpackChunkName: "repayList" */ "@/views/cryptos/loan/repay-list.vue")
          },
          {//
            path: 'loanHistory',
            name: 'loanHistory',
            component: () => import(/* webpackChunkName: "loanHistory" */ "@/views/cryptos/loan/loanHistory.vue")
          },
          {//
            path: 'loanRule',
            name: 'loanRule',
            component: () => import(/* webpackChunkName: "loanRule" */ "@/views/cryptos/loan/loanRule.vue")
          },
          {//c2c自选区我要买
            path: 'wantBuy',
            name: 'WantBuy',
            component: () => import(/* webpackChunkName: "WantBuy" */ '@/views/cryptos/placeAnOrder/page/wantBuy/index.vue'),
          },
          {//广告筛选
            path: 'wantBuy/adScreening',
            name: 'adScreening',
            component: () => import(/* webpackChunkName: "adScreening" */ '@/views/cryptos/placeAnOrder/page/adScreening.vue'),
          },
          {//c2c购买
            path: 'wantBuy/c2cBuy',
            name: 'c2cBuy',
            component: () => import(/* webpackChunkName: "c2cBuy" */ '@/views/cryptos/c2c-trade/page/c2cBuy.vue'),
          },
          {// c2c买卖交易
            path: 'c2cTrade',
            name: 'c2cTrade',
            props(route) {
              return {
                ...route.query,
              }
            },
            component: () => import(/* webpackChunkName: "c2cTrade" */ '@/views/cryptos/c2c-trade/index.vue'),
          },
          {// c2c订单列表
            path: 'wantBuy/c2cOrderList',
            name: 'c2cOrderList',
            component: () => import(/* webpackChunkName: "c2cOrderList" */ "@/views/cryptos/c2c-order-list/index.vue"),
            props(route) {
              return {
                ...route.query
              }
            }
          },
          {// c2c订单详情
            path: 'tradeOrderDetail',
            name: 'tradeOrderDetail',
            component: () => import(/* webpackChunkName: "tradeOrderDetail" */ "@/views/cryptos/c2c-trade/page/tradeOrderDetail.vue"),
          },
          {// 快捷区
            path: 'wantBuy/quick',
            name: 'quick',
            component: () => import(/* webpackChunkName: "quick" */ "@/views/cryptos/quick/index.vue")
          },
          {// 选择法币
            path: 'selectLegalCurrency',
            name: 'selectLegalCurrency',
            component: () => import(/* webpackChunkName: "selectLegalCurrency" */ '@/views/cryptos/selectLegalCurrency/index.vue'),
          },
          {// 收款方式
            path: "paymentMethod",
            name: 'paymentMethod',
            component: () => import(/* webpackChunkName: "paymentMethod" */ "@/views/cryptos/placeAnOrder/page/payment-method/PaymentMethod.vue")
          },
          {// 银行卡详情
            path: 'wantBuy/bankCard',
            name: 'bankCardDetail',
            component: () =>
              import(/* webpackChunkName: "bankCardDetail" */ '@/views/cryptos/placeAnOrder/components/bankCardDetail/index.vue'),
            props(route) {
              return {
                ...route.query
              }
            }
          },
          {// 添加收款方式
            path: 'wantBuy/addPaymentMethod',
            name: 'addPaymentMethod',
            component: () =>
              import(/* webpackChunkName: "addPaymentMethod" */ '@/views/cryptos/placeAnOrder/page/addPaymentMethod/AddPaymentMethod.vue'),
          },
          {
            // 广告商个人信息详情
            path: 'advertiserDetail',
            component: () => import(/* webpackChunkName: "advertiserDetail" */ '@/views/cryptos/advertiserDetail/index.vue'),
            props(route) {
              return {
                ...route.query
              }
            }
          },
          {// c2c出售订单生成
            path: 'sellGenerate',
            component: () => import(/* webpackChunkName: "sellGenerate" */ "@/views/cryptos/c2c-trade/page/SellGenerate.vue"),
            props(route) {
              return {
                ...route.query
              }
            }
          },
          {
            // 生成订单详情页
            path: 'orderGeneration',
            name: 'orderGeneration',
            component: () => import(/* webpackChunkName: "orderGeneration" */ '@/views/cryptos/c2cOrder/order-generation/index.vue'),
            props(route) {
              return {
                ...route.query,
              }
            },
          },
          {// 付款
            path: 'paymentBuy',
            name: 'paymentBuy',
            component: () => import(/* webpackChunkName: "paymentBuy" */ "@/views/cryptos/c2cOrder/payment/PaymentBuy.vue"),
          },
          {// 联系买家/卖家
            path: 'chat',
            component: () => import(/* webpackChunkName: "chat" */ "@/views/cryptos/chat/index.vue"),
            props(route) {
              return {
                ...route
              }
            }
          },
          {
            // 取消订单
            path: 'cancelOrder',
            name: 'cancelOrder',
            component: () => import(/* webpackChunkName: "cancelOrder" */ '@/views/cryptos/c2cOrder/cancel-order/cancelOrder.vue'),
          },
          {
            // 申诉
            path: 'appeal',
            name: 'appeal',
            component: () => import(/* webpackChunkName: "appeal" */ '@/views/cryptos/c2cOrder/appeal/index.vue'),
          },
          {
            // 申诉成功
            path: 'appeal/page',
            name: 'appealSuccess',
            component: () => import(/* webpackChunkName: "appealSuccess" */ '@/views/cryptos/c2cOrder/appeal/Appeal.vue'),
          },
          {
            // 付款详情
            path: 'paymentDetail',
            name: 'paymentDetail',
            component: () => import(/* webpackChunkName: "paymentDetail" */ '@/views/cryptos/c2cOrder/payment/PaymentDetail.vue'),
          },
          {
            //c2c帮助中心
            path: 'c2cHelpCenter',
            name: 'HelpCenter',
            component: () => import(/* webpackChunkName: "c2cHelpCenter" */ '@/views/cryptos/c2cHelpCenter/index.vue'),
          },
          {
            // c2c收款方式
            path: 'c2cCollection',
            component: () => import(/* webpackChunkName: "c2cCollection" */ '@/views/cryptos/c2c-trade/page/c2cCollection.vue'),
            props(route) {
              return {
                ...route.query,
              }
            },
          },
          {// c2c帮助
            path: 'c2cHelp',
            name: 'C2cHelp',
            meta: { requireAuth: true },
            component: () => import(/* webpackChunkName: "c2cHelp" */ "@/views/cryptos/c2c-trade/page/c2cHelp.vue")
          },
          {// 确认收款
            path: 'confirmedPaid',
            component: () => import(/* webpackChunkName: "confirmedPaid" */ "@/views/cryptos/c2cOrder/payment/ConfirmedPaid.vue")
          },
          {// 卖家交易
            path: 'tradeSuccessSell',
            component: () => import(/* webpackChunkName: "tradeSuccessSell" */ "@/views/cryptos/c2c-trade/page/TradeSuccessSell.vue")
          },
          {// 买家交易成功详情
            path: 'tradeSuccessDetailBuyer',
            component: () => import(/* webpackChunkName: "tradeSuccessDetailBuyer" */ "@/views/cryptos/c2c-trade/page/TradeSuccessDetailBuyer.vue")
          },
          {// 卖家交易成功详情
            path: 'tradeSuccessDetailSell',
            component: () => import(/* webpackChunkName: "tradeSuccessDetailSell" */ "@/views/cryptos/c2c-trade/page/TradeSuccessDetailSell.vue")
          },
          {// 买家交易成功
            path: 'tradeSuccessBuyer',
            component: () => import(/* webpackChunkName: "tradeSuccessBuyer" */ "@/views/cryptos/c2c-trade/page/TradeSuccessBuyer.vue")
          },
        ]
      },
      {
        path: '/my',
        name: 'My',
        redirect: '/my/index',
        component: () => import('@/views/Layout.vue'),
        meta: { tarbar: true },
        children: [
          { path: 'index', meta: { tarbar: true, }, component: () => import(/* webpackChunkName: "my" */ /* webpackPrefetch: true */'@/views/my/index.vue') }
        ]
      },
      {
        path: '/register',
        name: 'Register',
        meta: { keepAlive: true },
        component: () => import(/* webpackChunkName: "register" */ '@/views/register/index.vue'),
      },
      { //验证码
        path: '/verify',
        name: 'verify',
        component: () => import(/* webpackChunkName: "verify" */ '@/views/register/verify.vue')
      },
      { //设置资金密码
        path: '/setFond',
        name: 'setFond',
        component: () => import(/* webpackChunkName: "setFond" */ '@/views/register/setFond.vue')
      },
      { //注册身份认证
        path: '/identity',
        name: 'identity',
        component: () => import(/* webpackChunkName: "identity" */ '@/views/register/identity.vue')
      },
      { //谷歌验证
        path: '/gooleVerify',
        name: 'gooleVerify',
        component: () => import(/* webpackChunkName: "gooleVerify" */ '@/views/register/gooleVerify.vue')
      },
      { //注册完成
        path: '/finish',
        name: 'finish',
        component: () => import(/* webpackChunkName: "finish" */ '@/views/register/finish.vue')
      },
      {   //语言设置
        path: '/language',
        name: 'language',
        component: () => import(/* webpackChunkName: "language" */ '@/views/language/index.vue')
      },
      { //客服
        path: '/customerService',
        name: 'customerService',
        component: () => import(/* webpackChunkName: "customerService" */ '@/views/customerService/index.vue')
      },
      { //工单列表
        path: '/workerOrder',
        name: 'workerOrder',
        component: () => import(/* webpackChunkName: "workerOrder" */ '@/views/workerOrder/index.vue')
      },
      { //发起工单
        path: '/workerOrder/create',
        name: 'workerOrderCreate',
        component: () => import(/* webpackChunkName: "workerOrderCreate" */ '@/views/workerOrder/create.vue')
      },
      { //工单详情
        path: '/workerOrder/detail',
        name: 'workerOrderDetail',
        component: () => import(/* webpackChunkName: "workerOrderDetail" */ '@/views/workerOrder/detail.vue')
      },
      { //身份认证
        path: '/certificationCenter',
        name: 'certificationCenter',
        component: () => import(/* webpackChunkName: "certificationCenter" */ '@/views/certificationCenter/index.vue')
      },
      {
        path: '/advancedCtf',
        name: 'advancedCtf',
        component: () => import(/* webpackChunkName: "advancedCtf" */ '@/views/certificationCenter/advancedCtf.vue')
      },
      {
        path: '/verified',
        name: 'verified',
        component: () => import(/* webpackChunkName: "verified" */ '@/views/verified/index.vue')
      },
      {
        path: '/authentication',
        name: 'authentication',
        component: () => import(/* webpackChunkName: "authentication" */ '@/views/authentication/index.vue')
      },
      {//修改登录密码
        path: '/changePassword',
        name: 'changePassword',
        component: () => import(/* webpackChunkName: "changePassword" */ '@/views/changePassword/index.vue')
      },
      {//修改资金密码
        path: '/changeFundsPassword',
        name: 'changeFundsPassword',
        component: () => import(/* webpackChunkName: "changeFundsPassword" */ '@/views/changeFundsPassword/index.vue')
      },
      {//绑定验证
        path: '/bindVerify',
        name: 'bindVerify',
        component: () => import(/* webpackChunkName: "bindVerify" */ '@/views/bindVerify/index.vue')
      },
      {//重置绑定
        path: '/resetVerify',
        name: 'resetVerify',
        component: () => import(/* webpackChunkName: "resetVerify" */ '@/views/resetVerify/index.vue')
      },
      {//安全中心
        path: '/safety',
        name: 'safety',
        component: () => import(/* webpackChunkName: "safety" */ '@/views/safety/index.vue')
      },
      {//更换绑定
        path: '/changeVerify',
        name: 'changeVerify',
        component: () => import(/* webpackChunkName: "changeVerify" */ '@/views/safety/changeVerify.vue')
      },
      {//
        path: '/resetSuccess',
        name: 'resetSuccess',
        component: () => import(/* webpackChunkName: "resetSuccess" */ '@/views/resetVerify/resetSuccess.vue')
      },
      {//忘记密码
        path: '/forget',
        name: 'forget',
        component: () => import(/* webpackChunkName: "forget" */ '@/views/forget/index.vue')
      },
      {//重置登录密码
        path: '/resetPassword',
        name: 'resetPassword',
        component: () => import(/* webpackChunkName: "resetPassword" */ '@/views/forget/resetPassword.vue')
      },
      {//忘记密码修改成功
        path: '/passSuccess',
        name: 'passSuccess',
        component: () => import(/* webpackChunkName: "passSuccess" */ '@/views/forget/passSuccess.vue')
      },
      {//安全验证
        path: '/safeVerify',
        name: 'safeVerify',
        component: () => import(/* webpackChunkName: "safeVerify" */ '@/views/forget/safeVerify.vue')
      },
      {
        path: '/:pathMatch(.*)*',
        name: '404',
        component: () => import(/* webpackChunkName: "no404" */'@/views/404.vue')
      },
      {
        path: '/order',
        name: 'order',
        // meta: { tarbar: true },
        component: () => import('@/views/Layout.vue'),
        children: [
          { path: 'submit', component: () => import(/* webpackChunkName: "order" */'@/views/order/order-submit.vue') },
          { path: 'success', component: () => import(/* webpackChunkName: "order" */'@/views/order/success.vue') }, //成功
          { path: 'apply-success', component: () => import(/* webpackChunkName: "order" */'@/views/order/apply-success.vue') }, //申请成功
        ]
      },
      {
        path: '/Record',
        name: 'Record',
        // meta: { tarbar: true },
        component: () => import('@/views/Layout.vue'),
        children: [
          { path: 'DepositAndWithdrawal', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "Record" */'@/views/Record/DepositAndWithdrawal.vue') },
          { path: 'RecordDetails', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "Record" */'@/views/Record/RecordDetails.vue') }
        ], //充值和提现记录
      },
      {
        path: '/payMentMethod',
        name: 'payMentMethod',
        // meta: { tarbar: true },
        component: () => import('@/views/Layout.vue'),
        children: [
          { path: 'list', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "payMentMethod" */'@/views/payMentMethod/list.vue') },
          { path: 'add', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "payMentMethod" */'@/views/payMentMethod/add.vue') },
          { path: 'selectPay', meta: { tarbar: false }, component: () => import(/* webpackChunkName: "payMentMethod" */'@/views/payMentMethod/selectPay.vue') },
        ], //收款方式
      },
      {
        //推广中心
        path: '/promote',
        name: 'promote',
        meta: { requireAuth: true },
        component: () => import(/* webpackChunkName: "promote" */ '@/views/cryptos/promote/index.vue')
      },
      {
        //推广规则
        path: '/promote/rules',
        name: 'promoteRules',
        component: () => import(/* webpackChunkName: "promoteRules" */ '@/views/cryptos/promote/Rules.vue')
      },
      {
        //分享二维码
        path: '/ShareQRCode',
        name: 'ShareQRCode',
        component: () => import(/* webpackChunkName: "ShareQRCode" */ '@/views/cryptos/promote/ShareQRCode.vue')
      },
      {
        //分享海报
        path: '/SharePoster',
        name: 'SharePoster',
        // meta: { keepAlive: true},
        component: () => import(/* webpackChunkName: "SharePoster" */ '@/views/cryptos/promote/SharePoster.vue')
      },
      {
        //帮助中心
        path: '/helpCenter',
        name: 'helpCenter',
        component: () => import(/* webpackChunkName: "helpCenter" */ '@/views/cryptos/HelpCenter/index.vue')
      },
      {
        //帮助中心详情
        path: '/helpDetail',
        name: 'helpDetail',
        component: () => import(/* webpackChunkName: "helpDetail" */ '@/views/cryptos/HelpCenter/detail.vue')
      },
      {
        //关于我们
        path: '/aboutUs',
        name: 'aboutUs',
        component: () => import(/* webpackChunkName: "aboutUs" */ '@/views/cryptos/AboutUs/index.vue')
      },
      {
        //导航更多
        path: '/more',
        name: 'more',
        component: () => import(/* webpackChunkName: "more" */ '@/views/morePage/index.vue')
      },
      {
        path: '/ipo',
        name: 'ipo',
        // meta: { tarbar: true },
        redirect: '/ipo/index',
        component: () => import('@/views/Layout.vue'),
        children: [
          { path: 'index', meta: { tarbar: false }, component: () => import('@/views/ipo/index.vue') },
          { path: 'drawLotsDetail', meta: { tarbar: false }, component: () => import('@/views/ipo/drawLotsDetail.vue') },
          { path: 'subscribeDetail', meta: { tarbar: false }, component: () => import('@/views/ipo/subscribeDetail.vue') },
          { path: 'lotteryRecord', meta: { tarbar: false }, component: () => import('@/views/ipo/lotteryRecord.vue') },
          { path: 'stock', meta: { tarbar: false }, component: () => import('@/views/ipo/stock.vue') },
          { path: 'progress', meta: { tarbar: false }, component: () => import('@/views/ipo/progress.vue') },
          { path: 'spotStock', meta: { tarbar: false }, component: () => import('@/views/ipo/spotStock.vue') },
        ], //ipo中心
      },
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    return { top: 0 }
  },
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requireAuth) {
    // 判断该路由是否需要登录权限
    if (userStore.userInfo && userStore.userInfo.token) {
      // 通过vuex state获取当前的token是否存在
      next()
    } else {
      next({
        path: '/login',
      })
    }
  } else {
    next()
  }
})

export default router
