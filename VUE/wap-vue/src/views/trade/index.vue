<template>
  <section class="pb-fix">
    <van-loading color="#1194F7" class="loading-box" v-if="isLoading" />
    <div class="trade-container-box">
      <header class="header">
        <div class="flex-l">
          <span class="title">{{ t("trade") }}</span>
        </div>
      </header>
      <section class="trade-tab-container">
        <van-tabs v-model:active="tabActive" shrink @click-tab="onClickTab">
          <van-tab v-for="(item, index) in listTab" :key="item.tabIndex" :name="item.tabIndex" :title="item.title">
            <div class="content-container">
              <div class="user-info px-4 mt-4">
                <div class="mt-8 flex">
                  <img class="w-24 h-24" src="../../assets/image/avatar.png" alt="avatar" />
                  <div class="ml-2 flex flex-col justify-center">
                    <div class="font-bold text-lg name">
                      {{ userStore.userInfo && userStore.userInfo.username }}
                    </div>
                    <div class="text-sm text-gray-400 ID mt-1 flex items-center">
                      ID：{{
                        userStore.userInfo && userStore.userInfo.usercode
                      }}<img class="w-8 h-8 ml-4" src="../../assets/image/idcopy.png" alt="id" @click="copy" />
                    </div>
                  </div>
                </div>
              </div>
              <div class="asset">
                <div class="line">
                  <div class="flex-l">
                    <p v-if="item.type === 'Etf'">{{ t("ETFTotalAssets") }}</p>
                    <p v-if="item.type === 'Cryptos'">{{ t("TotalCryptocurrencyAssets") }}</p>
                    <p v-if="item.type === 'Foreign'">{{ t("TotalForeignExchangeAssets") }}</p>
                    <p v-if="item.type === 'UsStock'">{{ t("Stock Total assets") }}</p>
                    <p v-if="item.type === 'HkStock'">{{ t('Stock Total assets') }}</p>
                    <p v-if="item.type === 'TWStock'">{{ t('Stock Total assets') }}</p>
                    <p v-if="item.type === 'JPStock'">{{ t('Stock Total assets') }}</p>
                    <p v-if="item.type === 'AStock'">{{ t('Stock Total assets') }}</p>
                    <p v-if="item.type === 'INDIAStock'">{{ t('Stock Total assets') }}</p>
                    <p v-if="item.type === 'UK-stocks'">{{ t('Stock Total assets') }}</p>
                    <p v-if="item.type === 'DE-stocks'">{{ t('Stock Total assets') }}</p>
                    <p v-if="item.type === 'BZ-stocks'">{{ t('Stock Total assets') }}</p>
                    <p class="value">
                      {{
                        currency.currency_symbol
                      }}
                      {{
                        assetsObj.totalAssets
                        ? (assetsObj.totalAssets * (currency.rate ?? 0)).toFixed(2)
                        : "0"
                      }}
                    </p>
                  </div>
                  <div class="flex-r">
                    <p v-if="item.type === 'Etf'">{{ t("ETFFloatingProfitAndLoss") }}</p>
                    <p v-if="item.type === 'Cryptos'">{{ t("AmoCryptoLssunt") }}</p>
                    <p v-if="item.type === 'Foreign'">{{ t("ForeignExchangeLoss") }}</p>
                    <p v-if="item.type === 'UsStock'">{{ t("Stock Loss") }}</p>
                    <p v-if="item.type === 'HkStock'">{{ t('Stock Loss') }}</p>
                    <p v-if="item.type === 'TWStock'">{{ t('Stock Loss') }}</p>
                    <p v-if="item.type === 'JPStock'">{{ t('Stock Loss') }}</p>
                    <p v-if="item.type === 'AStock'">{{ t('Stock Loss') }}</p>
                    <p v-if="item.type === 'INDIAStock'">{{ t('Stock Loss') }}</p>
                    <p v-if="item.type === 'UK-stocks'">{{ t('Stock Loss') }}</p>
                    <p v-if="item.type === 'DE-stocks'">{{ t('Stock Loss') }}</p>
                    <p v-if="item.type === 'BZ-stocks'">{{ t('Stock Loss') }}</p>
                    <p class="profit red">
                      {{
                        currency.currency_symbol
                      }}
                      {{
                        assetsObj.profit
                        ? (assetsObj.profit * (currency.rate ?? 0)).toFixed(2)
                        : "0"
                      }}
                    </p>
                  </div>
                </div>
                <div class="line">
                  <div class="flex-l">
                    <p v-if="item.type === 'Etf'">{{ t("ETFAvailableBalance") }}</p>
                    <p v-if="item.type === 'Cryptos'">{{ t("CryptocurrenciesBalance") }}</p>
                    <p v-if="item.type === 'Foreign'">{{ t("foreignBlance") }}</p>
                    <p v-if="item.type === 'UsStock'">{{ t("Stock Balance") }}</p>
                    <p v-if="item.type === 'HkStock'">{{ t('Stock Balance') }}</p>
                    <p v-if="item.type === 'TWStock'">{{ t('Stock Balance') }}</p>
                    <p v-if="item.type === 'JPStock'">{{ t('Stock Balance') }}</p>
                    <p v-if="item.type === 'AStock'">{{ t('Stock Balance') }}</p>
                    <p v-if="item.type === 'INDIAStock'">{{ t('Stock Balance') }}</p>
                    <p v-if="item.type === 'UK-stocks'">{{ t('Stock Balance') }}</p>
                    <p v-if="item.type === 'DE-stocks'">{{ t('Stock Balance') }}</p>
                    <p v-if="item.type === 'BZ-stocks'">{{ t('Stock Balance') }}</p>
                    <p class="value">
                      {{
                        currency.currency_symbol
                      }}
                      {{
                        assetsObj.usdtBalance
                        ? (assetsObj.usdtBalance * (currency.rate ?? 0)).toFixed(2)
                        : "0"
                      }}
                    </p>
                  </div>
                  <div class="flex-r">
                    <p v-if="item.type === 'Etf'">{{ t("ETFTheDay") }}</p>
                    <p v-if="item.type === 'Cryptos'">{{ t("Cryptocurrencyday") }}</p>
                    <p v-if="item.type === 'Foreign'">{{ t("ForeignDay") }}</p>
                    <p v-if="item.type === 'UsStock'">{{ t("Stock Profit Day") }}</p>
                    <p v-if="item.type === 'HkStock'">{{ t('Stock Profit Day') }}</p>
                    <p v-if="item.type === 'TWStock'">{{ t('Stock Profit Day') }}</p>
                    <p v-if="item.type === 'JPStock'">{{ t('Stock Profit Day') }}</p>
                    <p v-if="item.type === 'AStock'">{{ t('Stock Profit Day') }}</p>
                    <p v-if="item.type === 'INDIAStock'">{{ t('Stock Profit Day') }}</p>
                    <p v-if="item.type === 'UK-stocks'">{{ t('Stock Profit Day') }}</p>
                    <p v-if="item.type === 'DE-stocks'">{{ t('Stock Profit Day') }}</p>
                    <p v-if="item.type === 'BZ-stocks'">{{ t('Stock Profit Day') }}</p>
                    <p class="profit red">
                      {{
                        currency.currency_symbol
                      }}
                      {{
                        assetsObj.profitToday
                        ? (assetsObj.profitToday * (currency.rate ?? 0)).toFixed(2)
                        : "0"
                      }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </van-tab>
        </van-tabs>
      </section>
      <section class="content-container">
        <ex-nav :symbolType="symbolType" />
        <div class="quickly">
          <div class="quickBox chongbi" :class="[thStore.theme == 'dark' ? 'dark' : 'white']"
            @click="$router.push('/exchange/channel-in')">
            <div class="left">
              <div class="leftBox">
                <img src="@/assets/theme/dark/image/chongicon.png" alt="" />
              </div>
              <div class="leftCont">
                <p style="max-width: 100px" class="color-white">{{ $t("快捷充值") }}</p>
              </div>
            </div>
            <div class="right">
              <img v-if="thStore.theme == 'dark'" src="@/assets/theme/dark/image/goto.png" alt="" />
              <img v-else src="@/assets/theme/white/image/goto.png" alt="" />
            </div>
          </div>
          <div class="quickBox tibi ml-10" :class="[thStore.theme == 'dark' ? 'dark' : 'white']"
            @click="$router.push('/exchange/channel-out')">
            <div class="left">
              <div class="leftBox">
                <img src="@/assets/theme/dark/image/tiicon.png" alt="" />
              </div>
              <div class="leftCont">
                <p style="max-width: 100px">{{ $t("FastWithdrawal") }}</p>
              </div>
            </div>
            <div class="right">
              <img v-if="thStore.theme == 'dark'" src="@/assets/theme/dark/image/goto.png" alt="" />
              <img v-else src="@/assets/theme/white/image/goto.png" alt="" />
            </div>
          </div>
        </div>
      </section>
      <div class="indicator-index-container">
        <div class="indicator-index-box">
          <div class="flex-l">
            <ul>
              <li :class="{ active: navActive === index }" v-for="(item, index) in navTabList" :key="index"
                @click="handleChangeNav(item.index)">
                {{ item.text }}
              </li>
            </ul>
          </div>
        </div>
      </div>
      <section class="etf-container">
        <div class="all-etf-ranking" v-if="!([1, 2].includes(tabActive))">
          <div class="etf-table">
            <ul v-if="navActive === 0">
              <li class="title-line">
                <div class="flex-l">
                  <p>{{ t("marketValue") }}</p>
                </div>
                <div class="flex-r">
                  <div class="flex-r-item">
                    <p>{{ t("盈亏") }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p>{{ t("openAvailable") }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p>{{ t("costCurrentPrice") }}</p>
                  </div>
                </div>
              </li>
            </ul>
            <ul v-if="navActive === 1">
              <li class="title-line">
                <div class="flex-l">
                  <p>{{ t("委托时间") }}</p>
                </div>
                <div class="flex-r">
                  <div class="flex-r-item">
                    <p>{{ t("EntrustmentTransactionPrice") }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p>{{ t("OrderQuantity") }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p>{{ t("状态") }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p>{{ t("操作") }}</p>
                  </div>
                </div>
              </li>
            </ul>
            <ul v-if="navActive === 2">
              <li class="title-line">
                <div class="flex-l">
                  <p>{{ t("成交时间") }}</p>
                </div>
                <div class="flex-r">
                  <div class="flex-r-item">
                    <p>{{ t("成交价格") }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p>{{ t("成交量") }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p>{{ t("状态") }}</p>
                  </div>
                </div>
              </li>
            </ul>
            <ul v-if="navActive === 3">
              <li class="title-line">
                <div class="flex-l flex items-center">
                  <p>{{ t("stockCode") }}</p>
                </div>
                <div class="flex-r">
                  <div class="flex-r-item">
                    <p>{{ t("现价") }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p>{{ t("涨跌幅") }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p>{{ t("changeHands") }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p>{{ t("aavolumeRatioa") }}</p>
                  </div>
                </div>
              </li>
            </ul>
            <ul v-if="etfList.length !== 0">
              <li v-for="(item, index) in etfList" :key="`${item.symbol}${index}`"
                @click="itemClickOneOrThirdly(item, navActive)">
                <template v-if="navActive == 0 || navActive == 5">
                  <div class="flex-l">
                    <p>{{ item.symbol }}</p>
                    <p class="gray-text">{{ item.marketValue }}</p>
                  </div>
                  <div class="flex-r">
                    <div class="flex-r-item">
                      <p :class="item.profitLoss < 1 ? 'text-up' : 'text-down'">
                        {{ item.profitLoss }}
                      </p>
                      <p :class="item.profitLossPercentage < 1 ? 'text-up' : 'text-down'">
                        {{
                          item.profitLossPercentage && item.profitLossPercentage !== 0
                          ? `${item.profitLossPercentage}%`
                          : 0
                        }}
                      </p>
                    </div>
                    <div class="flex-r-item">
                      <p :class="item.close < 1 ? 'text-up' : 'text-down'">
                        {{ item.positionVolume }}
                      </p>
                      <p :class="item.close < 1 ? 'text-up' : 'text-down'">
                        {{ item.volume }}
                      </p>
                    </div>
                    <div class="flex-r-item">
                      <p :class="item.close < 1 ? 'text-up' : 'text-down'">
                        {{ item.price }}
                      </p>
                      <p :class="item.close < 1 ? 'text-up' : 'text-down'">
                        {{ item.currentPrice }}
                      </p>
                    </div>
                  </div>
                </template>
                <template v-if="navActive == 1">
                  <div class="flex-l">
                    <p>{{ item.symbol }}</p>
                    <p class="gray-text">
                      <span class="tip-text" :class="item.offset === 'close' ? 'text-down' : 'text-up'">{{
                        item.offset ===
                        "open" ? t("买") : t("卖")
                      }}</span>
                      {{
                        item.create_time_ts
                        ? dayjs(item.create_time_ts * 1000)
                          .format("YYYY-MM-DD HH:mm:ss")
                          .slice(11)
                        : "--"
                      }}
                    </p>
                  </div>
                  <div class="flex-r">
                    <div class="flex-r-item">
                      <p :class="item.open < 1 ? 'text-up' : 'text-down'">
                        {{ item.price }}
                      </p>
                      <p :class="item.open < 1 ? 'text-up' : 'text-down'">
                        {{ item.closePrice }}
                      </p>
                    </div>
                    <div class="flex-r-item">
                      <p :class="item.close < 1 ? 'text-up' : 'text-down'">
                        {{ item.volume }}
                      </p>
                    </div>
                    <div class="flex-r-item">
                      <p>
                        {{ item.state === "created" ? t("createdNew") : t(item.state) }}
                      </p>
                    </div>
                    <div class="flex-r-item operate-btn" @click="cancelSingle(item.order_no)">
                      <span>{{ t("撤单") }}</span>
                    </div>
                  </div>
                </template>
                <template v-if="navActive == 2">
                  <div class="flex-l">
                    <p>{{ item.symbol }}</p>
                    <p class="gray-text">
                      <span class="tip-text" :class="item.offset === 'close' ? 'text-down' : 'text-up'">{{
                        item.offset ===
                        "open" ? t("买") : t("卖")
                      }}</span>
                      {{
                        item.create_time_ts
                        ? dayjs(item.create_time_ts * 1000)
                          .format("YYYY-MM-DD HH:mm:ss")
                          .slice(11)
                        : "--"
                      }}
                    </p>
                  </div>
                  <div class="flex-r">
                    <div class="flex-r-item">
                      <p :class="item.open < 1 ? 'text-up' : 'text-down'">
                        {{ item.price }}
                      </p>
                    </div>
                    <div class="flex-r-item">
                      <p :class="item.close < 1 ? 'text-up' : 'text-down'">
                        {{ item.volume }}
                      </p>
                    </div>
                    <div class="flex-r-item">
                      <p>
                        {{ item.state === "created" ? t("委托完成") : t(item.state) }}
                      </p>
                    </div>
                  </div>
                </template>
                <template v-if="navActive == 3">
                  <div class="flex-l">
                    <p>{{ item.name }}</p>
                    <p class="gray-text">{{ item.symbol }}</p>
                  </div>
                  <div class="flex-r">
                    <div class="flex-r-item">
                      <p :class="item.close < 200 ? 'text-up' : 'text-down'">
                        {{ item.close }}
                      </p>
                    </div>
                    <div class="flex-r-item">
                      <p :class="item.changeRatio < 1 ? 'text-up' : 'text-down'">
                        {{
                          item.changeRatio === 0
                          ? 0
                          : item.changeRatio
                            ? item.changeRatio + "%"
                            : "--"
                        }}
                      </p>
                    </div>
                    <div class="flex-r-item">
                      <p>
                        {{
                          item.turnoverRate === 0
                          ? 0
                          : item.turnoverRate
                            ? item.turnoverRate + "%"
                            : "--"
                        }}
                      </p>
                    </div>
                    <div class="flex-r-item" :class="item.volumeRatio < 0.5 ? 'text-up' : 'text-down'">
                      <p>
                        {{
                          item.volumeRatio === 0
                          ? 0
                          : item.volumeRatio
                            ? item.volumeRatio + "%"
                            : "--"
                        }}
                      </p>
                    </div>
                  </div>
                </template>
              </li>
            </ul>
            <div class="flex flex-col justify-center pt-50 pb-20 items-center" v-else>
              <img src="@/assets/image/assets-center/no-data.png" alt="" class="no-data-img" />
              <p class="text-grey mt-10 text-24">{{ $t("暂无记录") }}</p>
            </div>
          </div>
        </div>
      </section>
      <div class="symbol-list" v-if="tabActive === 1 || tabActive === 2">
        <div class="flex flex-col justify-center pt-50 pb-20 items-center" v-if="symbolList.length === 0">
          <img src="@/assets/image/assets-center/no-data.png" alt="" class="no-data-img" />
          <p class="text-grey mt-10 text-24">{{ $t("暂无记录") }}</p>
        </div>
        <ul v-else>
          <li class="symbol-list-item" v-for="item in symbolList" :key="item" @click="itemClickSecondOrFourth(item)">
            <div class="symbol-list-top">
              <div class="item-l flex-1">
                <p class="symbol-name">{{ item.name }}</p>
                <p class="price-change" v-if="navActive !== 1">
                  <span class="price-l">{{ item.trade_avg_price }}</span>
                  <span class="arrow">→</span>
                  <span :class="{
                    'price-r': true,
                    green: item.mark_price - item.trade_avg_price > 0,
                    red: item.mark_price - item.trade_avg_price < 0,
                  }">{{ item.mark_price }}</span>
                </p>
                <p class="price-change" v-else>
                  <span class="price-l">{{ item.price }}</span>
                  <span class="arrow">→</span>
                  <span :class="{
                    'price-r': true,
                    green: item.mark_price - item.price > 0,
                    red: item.mark_price - item.price < 0,
                  }">{{ item.mark_price ?? "--" }}</span>
                </p>
              </div>
              <div class="item-r flex-1">
                <p class="tile">
                  {{ item?.order_price_type === "opponent" ? t("市价") : t("限价") }}
                </p>
                <div class="volume-box">
                  <div :class="{
                    volume: true,
                    'green-border': item.direction === 'buy',
                    'red-border': item.direction !== 'buy',
                  }">
                    <span :class="{
                      'volume-l': true,
                      'green-bg': item.direction === 'buy',
                      'red-bg': item.direction !== 'buy',
                    }">{{ item.direction == "buy" ? t("Buy") : t("Sell") }}</span>
                    <span :class="{
                      'volume-r': true,
                      green: item.direction === 'buy',
                      red: item.direction !== 'buy',
                    }">
                      {{
                        `${item.volume_open / (item.lever_rate ?? 1)} * ${item.lever_rate ?? 1
                        } x`
                      }}
                      {{ t("volume") }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="symbol-list-bottom">
              <div class="bottom-l flex-1">
                <div class="order-info">
                  <p class="margin">{{ t("margin") }}: {{ item.deposit }}</p>
                  <p class="id">{{ t("orderId") }}: #{{ item.order_no }}</p>
                  <p class="date">{{ item.create_time }}</p>
                </div>
              </div>
              <div class="bottom-r">
                <p :class="{
                  'profit-num': true,
                  green: item.profit > 0,
                  red: item.profit < 0,
                }">
                  {{ item.profit }}
                </p>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, computed } from "vue";
import { useUserStore } from "@/store/user";
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import ExNav from "@/components/trade/ex-nav/index.vue";
import useClipboard from "vue-clipboard3";
import { showToast } from "vant";
import {
  _getETFList,
  _contractApplyOrder,
  getExchangeApplyHisList,
  getExchangeApplyOrderList,
  _getOptionalList,
} from "@/service/etf.api";
import { watch } from "vue";
import { contractOrder } from "@/service/trade.api";
import TradeApi from "@/service/trading.js";
import { _assetsTradeTop } from "@/service/user.api";
import dayjs from "dayjs";
import { themeStore } from "@/store/theme";
import { useStore } from 'vuex';

const thStore = themeStore();
const store = useStore()
const { t } = useI18n();
const { toClipboard } = useClipboard();
const router = useRouter();
const route = useRoute();
const defaultTabActive = +route.query.tabActive || 0;
const tabActive = ref(defaultTabActive);
const navActive = ref(0);
const userStore = useUserStore();
const symbolType = ref("indices"); //默认etf
const etfList = ref([]);
const symbolList = ref([]);
const currency = ref({});
const isLoading = ref(false);
const assetsObj = ref({})
const assets = ref({})
const navTabV1 = ref([
  {
    text: t("持仓"),
    index: 0,
  },
  {
    text: t("entrust"),
    index: 1,
  },
  {
    text: t("成交"),
    index: 2,
  },
  {
    text: t("自选"),
    index: 3,
  },
]);

const navTabV2 = ref([
  {
    text: t("持仓"),
    index: 0,
  },
  {
    text: t("entrustOrder"),
    index: 1,
  },
  {
    text: t("历史记录"),
    index: 2,
  },
]);

const listTab = ref([
  // {
  //   title: 'ETF',
  //   type: 'Etf',
  //   urlMatch: 'etf',
  //   symbolType: 'indices',
  //   tabIndex: 0
  // },
  // {
  //   title: t('加密货币'),
  //   type: 'Cryptos',
  //   urlMatch: 'crypto',
  //   symbolType: 'cryptos',
  //   tabIndex: 1
  // },
  // {
  //   title: t('外汇'),
  //   type: 'Foreign',
  //   urlMatch: 'for',
  //   symbolType: 'forex',
  //   tabIndex: 2
  // },
  {
    title: t('印度股'),
    type: 'INDIAStock',
    urlMatch: 'INDIA-stocks',
    symbolType: 'INDIA-stocks',
    tabIndex: 8
  },
  {
    title: t('UsStocks'),
    type: 'UsStock',
    urlMatch: 'stock',
    symbolType: 'US-stocks',
    tabIndex: 3
  },
  {
    title: t('港股'),
    type: 'HkStock',
    urlMatch: 'HK-stocks',
    symbolType: 'HK-stocks',
    tabIndex: 4
  },
  // {
  //   title: t('台股'),
  //   type: 'TWStock',
  //   urlMatch: 'TW-stocks',
  //   symbolType: 'TW-stocks',
  //   tabIndex: 5
  // },
  // {
  //   title: t('日股'),
  //   type: 'JPStock',
  //   urlMatch: 'JP-stocks',
  //   symbolType: 'JP-stocks',
  //   tabIndex: 6
  // },
  // {
  //   title: t('A股'),
  //   type: 'AStock',
  //   urlMatch: 'A-stocks',
  //   symbolType: 'A-stocks',
  //   tabIndex: 7
  // },
  // {
  //   title: t('印度股'),
  //   type: 'INDIAStock',
  //   urlMatch: 'INDIA-stocks',
  //   symbolType: 'INDIA-stocks',
  //   tabIndex: 8
  // },
  {
    title: t('英股'),
    type: 'UKStock',
    urlMatch: 'UK-stocks',
    symbolType: 'UK-stocks',
    tabIndex: 9
  },
  {
    title: t('德股'),
    type: 'DEStock',
    urlMatch: 'DE-stocks',
    symbolType: 'DE-stocks',
    tabIndex: 10
  },
  // {
  //   title: t('巴股'),
  //   type: 'BZStock',
  //   urlMatch: 'BZ-stocks',
  //   symbolType: 'BZ-stocks',
  //   tabIndex: 11
  // },
])

const navTabList = computed(() => {
  return [1, 2].includes(tabActive.value) ? navTabV2.value : navTabV1.value;
});

const getCurrency = async () => {
  _getExchangeRate()
    .then((res) => {
      currency.value = res;
    })
    .catch((err) => Promise.reject(err));
};

onMounted(async () => {
  await store.dispatch('home/SET_CURRENCY')
  currency.value = store.state.home.currency
  setSomeBackParams();
  getETFList();
  assetsTradeTopFn();
  // interval.value = setInterval(() => {
  //   getETFItemList()
  // }, 1000)
});

onBeforeUnmount(() => {
  // if (interval.value) {
  //   clearInterval(interval.value)
  // }
});

watch(symbolType, (newVal, oldVal) => {
  if (newVal === 'indices' || newVal === 'US-stocks' || newVal === 'HK-stocks'
    || newVal === 'TW-stocks' || newVal === 'JP-stocks' || newVal === 'A-stocks'
    || newVal === 'INDIA-stocks') {
    getETFList()

  } else {
    getContractOrder();
  }
  assetsTradeTopFn()
});

const handleChangeNav = (index) => {
  navActive.value = index;
  etfList.value = [];
  // 加密货币&外汇
  if ([1, 2].includes(tabActive.value)) {
    // navTabV2
    switch (index) {
      case 0:
        getContractOrder();
        break;
      case 1:
        getContractApplyOrder();
        break;
      case 2:
        getHisContractOrder();
        break;
      default:
        break;
    }
  } else {
    // navTabV1
    switch (index) {
      case 0:
        getETFList();
        break;
      case 1:
        getETFOrderList();
        break;
      case 2:
        getETFHisList();
        break;
      case 3:
        getOptionalList();
        break;
      default:
        break;
    }
  }
};

const getETFList = () => {
  _getETFList(symbolType.value).then((data) => {
    etfList.value = data ?? [];
  });
};

// 成交列表
const getETFHisList = () => {
  getExchangeApplyHisList(symbolType.value).then((data) => {
    etfList.value = data ?? [];
  });
};

// 委托列表
const getETFOrderList = () => {
  getExchangeApplyOrderList(symbolType.value).then((data) => {
    etfList.value = data ?? [];
  });
};


const getOptionalList = () => {
  _getOptionalList(symbolType.value).then((data) => {
    etfList.value = data ?? [];
  });
};

const getContractOrder = () => {
  let obj = {
    type: "orders",
    page_no: 1,
    page_size: "all",
    symbolType: symbolType.value,
  };
  contractOrder(obj).then((res) => {
    symbolList.value = res ?? [];
  });
};

// 历史
const getHisContractOrder = () => {
  let obj = {
    type: "hisorders",
    page_no: 1,
    page_size: "all",
    symbolType: symbolType.value,
  };
  contractOrder(obj).then((res) => {
    symbolList.value = res ?? [];
  });
};

// 委托列表
const getContractApplyOrder = () => {
  let obj = {
    type: "orders",
    page_no: 1,
    page_size: "all",
    symbolType: symbolType.value,
  };
  _contractApplyOrder(obj).then((res) => {
    symbolList.value = res ?? [];
  });
};

const copy = async () => {
  try {
    await toClipboard(userStore.userInfo && userStore.userInfo.usercode);
    showToast(t("copySuccess"));
  } catch (e) {
    console.error(e);
  }
};

// 跳转回来的一些反显参数
const setSomeBackParams = () => {
  switch (tabActive.value) {
    case 0:
      symbolType.value = "indices";
      break;
    case 1:
      symbolType.value = "cryptos";
      break;
    case 2:
      symbolType.value = "forex";
      break;
    case 3:
      symbolType.value = "US-stocks";
      break;
    case 4:
      symbolType.value = 'HK-stocks';
      break;
    case 5:
      symbolType.value = 'TW-stocks';
      break;
    case 6:
      symbolType.value = 'JP-stocks';
      break;
    case 7:
      symbolType.value = 'A-stocks';
      break;
    case 8:
      symbolType.value = 'INDIA-stocks';
      break;
    case 9:
      symbolType.value = 'UK-stocks';
      break;
    case 10:
      symbolType.value = 'DE-stocks';
      break;
    case 11:
      symbolType.value = 'BZ-stocks';
      break;
    default:
      break;
  }
};

const onClickTab = ({ name }) => {
  tabActive.value = name;
  navActive.value = 0;

  listTab.value.forEach(item => {
    if (name == item.tabIndex) {
      symbolType.value = item.symbolType
    }
  })
  router.push('/trade/index?tabActive=' + tabActive.value)
};

const itemClickOneOrThirdly = (item, index) => {
  // index: navActive
  switch (index) {
    case 0:
      switch (symbolType.value) {
        case "indices":
          router.push(
            `/quotes/openTrade?tabActive=3&symbol=${item.symbol}&from=trade&type=indices&tradeTabActive=0&navActive=0`
          );
          break;
        case "US-stocks":
          router.push(
            `/quotes/openTrade?tabActive=3&symbol=${item.symbol}&from=trade&type=US-stocks&tradeTabActive=3&navActive=0`
          );
          break;
        case 'HK-stocks':
          router.push(`/quotes/openTrade?tabActive=3&symbol=${item.symbol}&from=trade&type=HK-stocks&tradeTabActive=3&navActive=0`)
          break;
        case 'JP-stocks':
          router.push(`/quotes/openTrade?tabActive=3&symbol=${item.symbol}&from=trade&type=JP-stocks&tradeTabActive=3&navActive=0`)
          break;
        case 'TW-stocks':
          router.push(`/quotes/openTrade?tabActive=3&symbol=${item.symbol}&type=TW-stocks`)
          break;
        case 'A-stocks':
          router.push(`/quotes/openTrade?tabActive=3&symbol=${item.symbol}&from=trade&type=A-stocks&tradeTabActive=0&navActive=0`)
          break;
        case 'INDIA-stocks':
          router.push(`/quotes/openTrade?tabActive=3&symbol=${item.symbol}&from=trade&type=INDIA-stocks&tradeTabActive=3&navActive=0`)
          break;
        case 'UK-stocks':
          router.push(`/quotes/openTrade?tabActive=3&symbol=${item.symbol}&from=trade&type=UK-stocks&tradeTabActive=3&navActive=0`)
          break;
        case 'DE-stocks':
          router.push(`/quotes/openTrade?tabActive=3&symbol=${item.symbol}&from=trade&type=DE-stocks&tradeTabActive=3&navActive=0`)
          break;
        case 'BZ-stocks':
          router.push(`/quotes/openTrade?tabActive=3&symbol=${item.symbol}&from=trade&type=BZ-stocks&tradeTabActive=3&navActive=0`)
          break;
      }
      break;
    case 1:
      switch (symbolType.value) {
        case "indices":
          router.push(
            `/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=indices&tradeTabActive=0&navActive=1`
          );
          break;
        case "US-stocks":
          router.push(
            `/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=US-stocks&tradeTabActive=3&navActive=1`
          );
          break;
        case 'HK-stocks':
          router.push(`/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=HK-stocks&tradeTabActive=3&navActive=1`)
          break;
        case 'JP-stocks':
          router.push(`/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=JP-stocks&tradeTabActive=3&navActive=1`)
          break;
        case 'TW-stocks':
          router.push(`/quotes/openTrade?tabActive=4&symbol=${item.symbol}&type=TW-stocks`)
          break;
        case "A-stocks":
          router.push(
            `/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=A-stocks&tradeTabActive=0&navActive=1`
          );
          break;
        case 'INDIA-stocks':
          router.push(`/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=INDIA-stocks&tradeTabActive=3&navActive=1`)
          break;
        case 'UK-stocks':
          router.push(`/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=UK-stocks&tradeTabActive=3&navActive=1`)
          break;
        case 'DE-stocks':
          router.push(`/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=DE-stocks&tradeTabActive=3&navActive=1`)
          break;
        case 'BZ-stocks':
          router.push(`/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=BZ-stocks&tradeTabActive=3&navActive=1`)
          break;
      }
      break;
    case 2:
      switch (symbolType.value) {
        case "indices":
          router.push(
            `/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=indices&tradeTabActive=0&navActive=2&selectIndex=2`
          );
          break;
        case "US-stocks":
          router.push(
            `/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=US-stocks&tradeTabActive=3&navActive=2&selectIndex=2`
          );
          break;
        case 'HK-stocks':
          router.push(`/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=HK-stocks&tradeTabActive=3&navActive=2&selectIndex=2`)
          break;
        case 'JP-stocks':
          router.push(`/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=JP-stocks&tradeTabActive=3&navActive=2&selectIndex=2`)
          break;
        case 'TW-stocks':
          router.push(`/quotes/openTrade?tabActive=4&symbol=${item.symbol}&type=TW-stocks`)
          break;
        case "A-stocks":
          router.push(
            `/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=A-stocks&tradeTabActive=0&navActive=2&selectIndex=2`
          );
          break;
        case 'INDIA-stocks':
          router.push(`/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=INDIA-stocks&tradeTabActive=3&navActive=2&selectIndex=2`)
          break;
        case 'UK-stocks':
          router.push(`/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=UK-stocks&tradeTabActive=3&navActive=2&selectIndex=2`)
          break;
        case 'DE-stocks':
          router.push(`/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=DE-stocks&tradeTabActive=3&navActive=2&selectIndex=2`)
          break;
        case 'BZ-stocks':
          router.push(`/quotes/openTrade?tabActive=4&symbol=${item.symbol}&from=trade&type=BZ-stocks&tradeTabActive=3&navActive=2&selectIndex=2`)
          break;
      }
      break;
    case 3:
      switch (symbolType.value) {
        case "indices":
          router.push(
            `/quotes/detail?symbol=${item.symbol}&from=trade&type=indices&tradeTabActive=0&navActive=3`
          );
          break;
        case 'US-stocks':
          router.push(`/quotes/usStockDetail?symbol=${item.symbol}&from=trade&symbolType=US-stocks&tradeTabActive=3&navActive=3`)
          break;
        case 'HK-stocks':
          router.push(`/quotes/usStockDetail?symbol=${item.symbol}&from=trade&symbolType=HK-stocks&tradeTabActive=3&navActive=3`)
          break;
        case 'JP-stocks':
          router.push(`/quotes/usStockDetail?symbol=${item.symbol}&from=trade&symbolType=JP-stocks&tradeTabActive=3&navActive=3`)
          break;
        case 'TW-stocks':
          router.push(`/quotes/openTrade?tabActive=0&symbol=${item.symbol}&type=TW-stocks`)
          break;
        case 'A-stocks':
          router.push(
            `/quotes/detail?symbol=${item.symbol}&from=trade&type=A-stocks&tradeTabActive=0&navActive=3`
          );
          break;
        case 'INDIA-stocks':
          router.push(`/quotes/usStockDetail?symbol=${item.symbol}&from=trade&symbolType=INDIA-stocks&tradeTabActive=3&navActive=3`)
          break;
        case 'UK-stocks':
          router.push(`/quotes/usStockDetail?symbol=${item.symbol}&from=trade&symbolType=UK-stocks&tradeTabActive=3&navActive=3`)
          break;
        case 'DE-stocks':
          router.push(`/quotes/usStockDetail?symbol=${item.symbol}&from=trade&symbolType=DE-stocks&tradeTabActive=3&navActive=3`)
          break;
        case 'BZ-stocks':
          router.push(`/quotes/usStockDetail?symbol=${item.symbol}&from=trade&symbolType=BZ-stocks&tradeTabActive=3&navActive=3`)
          break;
      }
      break;
  }
};

const itemClickSecondOrFourth = (item) => {
  // 加密货币
  if (tabActive.value === 1) {
    switch (navActive.value) {
      case 0:
        router.push(
          `/cryptos/perpetualContract/${item.symbol}?from=trade&type=cryptos&tradeTabActive=1&navActive=0`
        );
        break;
      case 1:
        router.push(
          `/cryptos/perpetualContract/${item.symbol}?from=trade&type=cryptos&tradeTabActive=1&navActive=1`
        );
        break;
      case 2:
        router.push(
          `/cryptos/perpetualHistory?symbol=${item.symbol}&type=cryptos&from=trade&tradeTabActive=1&navActive=2`
        );
        break;
    }
  } else if (tabActive.value === 2) {
    // 外汇
    switch (navActive.value) {
      case 0:
        router.push(
          `/position/index?symbol=${item.symbol}&from=trade&type=forex&tabIndex=0&tradeTabActive=2&navActive=0`
        );
        break;
      case 1:
        router.push(
          `/position/index?symbol=${item.symbol}&from=trade&type=forex&tabIndex=1&tradeTabActive=2&navActive=1`
        );
        break;
      case 2:
        router.push(
          `/history/list?symbol=${item.symbol}&from=trade&type=forex&tradeTabActive=2&navActive=2`
        );
        break;
    }
  }
};

const cancelSingle = (order) => {
  console.log("order:", order);
  TradeApi.tradeCancel({
    order_no: order,
  })
    .then((res) => {
      console.log(res, "res");
      showToast(this.$t("成功"));
    })
    .catch(() => {
    });
};

const assetsTradeTopFn = (tradeType) => {
  console.log(symbolType.value)
  isLoading.value = true;
  _assetsTradeTop({
    symbolType: symbolType.value,
    tradeType: symbolType.value == 'cryptos' ? 'contract' : 'exchange'
  }).then(res => {
    isLoading.value = false;
    assetsObj.value = res
  }).catch((e) => {
    isLoading.value = false;
  });
}
</script>
<style lang="scss" scoped>
:deep(.van-loading) {
  position: absolute;
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 999;
}

:deep(.van-tabs__nav) {
  background: $mainBgColor;
}

:deep(.van-tab) {
  font-size: 14px;
  color: $text_color;
  font-weight: 400;
}

:deep(.van-tab.van-tab--active) {
  font-weight: 700;
}

.operate-btn {
  width: 49px;
  height: 20px;
  line-height: 20px;
  background: $delivery_left_tab_background;
  text-align: center;
  color: #818181;
  font-family: "Helvetica";
  font-style: normal;
  font-weight: 400;
  font-size: 11px;
}

.trade-container-box {
  .green {
    color: $green;
  }

  .red {
    color: $red;
  }

  .header {
    display: flex;
    height: 28px;
    padding: 0 12px;

    .flex-l {
      flex: 1;
      display: inline-flex;

      .icon {
        display: inline-block;
        width: 24px;
        height: 28px;
        padding: 6px 4px;

        img {
          height: 16px;
          width: 12px;
        }
      }
    }

    .title {
      font-weight: 700;
      font-size: 20px;
      line-height: 28px;
      color: $mainTextColor;
    }
  }

  .trade-tab-container {
    margin-top: 18px;
  }

  .no-data-img {
    margin-top: 20px;
    width: 100px;
    height: 100px;
  }

  .content-container {
    padding: 0 12px;

    .name {
      font-size: 14px;
    }

    .ID {
      font-size: 12px;
    }

    .line {
      padding: 14px 12px;
      display: flex;
      align-items: center;
      font-size: 12px;
      line-height: 18px;

      .gray-text {
        color: #bcbdc2;
        font-size: 12px;
      }

      .value {
        margin-top: 10px;
        font-weight: 700;
        font-size: 20px;
        line-height: 24x;
        color: $text_color;
      }

      .profit {
        margin-top: 10px;
        font-size: 14px;
      }

      .flex-l {
        flex: 1;
      }

      .flex-r {
        flex: 1;
        text-align: right;
      }
    }
  }
}

.indicator-index-container {
  .indicator-index-box {
    padding: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .flex-l {
      flex: 1;

      ul {
        display: flex;

        li {
          flex: 1;
          text-align: center;
          margin: 0 4px;
          font-size: 12px;
          border-radius: 4px;
        }
      }
    }

    .flex-r {
      display: flex;
      justify-content: flex-end;
      width: 80px;

      img {
        width: 12px;
        height: 16px;
      }
    }
  }

  .active {
    background: $btn_main;
    color: $white;
  }
}

.quickly {
  width: 100%;
  height: 72px;
  display: flex;
  justify-content: space-between;
  margin-bottom: 32px;

  .quickBox {
    flex: 1;
    height: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px;
    border-radius: 6px;
    border: 1px solid $color_main;

    .left {
      display: flex;
      align-items: center;
      flex: 1;

      .leftBox {
        width: 50px;
        height: 50px;

        img {
          width: 100%;
          height: 100%;
        }
      }
    }

    .leftCont {
      margin-left: 12px;

      p {
        font-size: 12px;
        color: $text_color;
        line-height: 14px;
      }
    }

    .right {
      width: 16px;
      height: 16px;

      img {
        width: 100%;
        height: 100%;
      }
    }
  }

  .chongbi {
    background: url("@/assets/theme/white/image/chongb.png");
    background-size: cover;

    &.dark {
      background: url("@/assets/theme/dark/image/chongb.png");
      background-size: cover;
    }
  }

  .tibi {
    background: url("@/assets/theme/white/image/tib.png");
    background-size: cover;

    &.dark {
      background: url("@/assets/theme/dark/image/tib.png");
      background-size: cover;
    }
  }
}

.etf-container {
  padding: 0;

  .hot-container {
    margin: 20px 0;
    padding: 0 8px;

    .header-box {
      display: flex;

      .title {
        flex: 1;
        font-size: 16px;
        padding: 0 8px;
        font-weight: 700;
      }

      .icon-group {
        display: flex;
        align-items: center;

        .icon.arrow {
          margin-left: 10px;
          width: 7px;
          height: 9px;
        }
      }
    }

    .hot-box {
      display: grid;
      grid-template-columns: 33.33% 33.33% 33.33%;
      grid-template-rows: repeat(2, 100px);
    }

    .hot-item {
      padding: 6px 2px;
      margin: 4px;
      text-align: center;
      font-size: 12px;
      line-height: 18px;
      color: #b8bdd1;
      background: #1b2134;
      border-radius: 4px;

      .value {
        font-weight: 700;
        color: $text_color;
        line-height: 24px;
      }

      .num {
        .num-left {
          margin-right: 6px;
        }
      }
    }
  }

  .all-etf-ranking,
  .other-etf-ranking {
    margin-top: 10px;

    .title {
      font-weight: 700;
      padding: 0 12px;
    }

    .tabs {
      padding: 0 12px;
      margin-top: 10px;
      height: 40px;
      line-height: 24px;
      color: #bbbcbd;

      .tab-item {
        margin: 4px;
        text-align: center;
        padding: 4px 6px;
        font-size: 12px;
        color: $text_color5;
        background: $US_tab_background;
        border-radius: 10px;
        background-size: cover;
      }

      .active {
        font-weight: 700;
        color: $color_main !important;
        background: $US_tabActice_background;
        border-radius: 10px;
        background-size: cover;
      }
    }

    .etf-table {
      .right {
        text-align: right;
      }

      ul {
        margin-top: 10px;
      }

      .title-line {
        color: #747a8f;
        font-size: 12px;
        font-weight: 400;
        padding: 0 12px;
        border: none;
      }

      li {
        padding: 14px 12px;
        display: flex;
        font-size: 12px;
        line-height: 18px;
        border-bottom: 1px solid $border_color;

        .gray-text {
          color: #bcbdc2;
          font-size: 12px;

          .tip-text {
            width: 27px;
            height: 16px;
            line-height: 16px;
            text-align: center;
            border-radius: 4px;
            color: $white;
            display: inline-block;

            &.text-up {
              background: $red;
            }

            &.text-down {
              background: $green;
            }
          }
        }

        .flex-l {
          width: 100px;
        }

        .flex-r {
          display: inline-flex;
          flex: 1;

          .flex-r-item {
            flex: 1;
            align-self: center;
            text-align: center;
          }
        }
      }
    }
  }
}

.symbol-list {
  display: flex;
  flex-direction: column;
  margin-top: 10px;

  .symbol-list-item {
    margin: 0 0 10px;
    justify-content: space-between;
    padding: 10px;
    font-size: 12px;
    color: #989899;
    font-weight: 600;
    border-bottom: 1px solid $border_color;
  }

  .symbol-list-top {
    display: flex;

    .item-l {
      text-align: left;

      .symbol-name {
        font-size: 16px;
        color: $text_color;
      }

      .price-change {
        color: $text_color;

        .price-l {
          font-weight: 600;
        }

        .arrow {
          margin: 0 6px;
        }
      }
    }

    .item-r {
      text-align: right;

      .volume-box {
        .volume {
          display: inline-flex;
          border: 1px solid $green;
          border-radius: 5px;

          .volume-l {
            width: 40px;
            background-color: $green;
            color: $main-btn-color;
            font-size: 12px;
            text-align: center;
          }

          .volume-r {
            padding: 0 6px;
            // color: $green;
            font-weight: 400;
          }

          .red {
            color: $red;
          }
        }

        .red-bg {
          background-color: $red !important;
        }

        .green-bg {
          background-color: $green !important;
        }

        .green-border {
          border: 1px solid $green !important;
        }

        .red-border {
          border: 1px solid $red !important;
        }
      }
    }
  }

  .symbol-list-bottom {
    display: flex;
    margin-top: 4px;

    .order-info {
      line-height: 18px;
    }

    .bottom-r {
      text-align: right;
      display: flex;

      .profit-num {
        line-height: 54px;
        font-weight: 600;
        font-size: 20px;
      }
    }
  }

  .no-data {
    margin: 20px auto;
    width: 200px;
    height: 200px;
  }
}
</style>
