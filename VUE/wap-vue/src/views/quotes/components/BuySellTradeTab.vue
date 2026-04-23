<template>
  <van-loading color="#1194F7" class="loading-box" v-if="isLoading" />
  <section class="inner-tab-container">
    <div class="line-first-container flex">
      <div class="line-first flex">
        <div class="line-first-l">
          <p>
            <span>{{ t("最新价") }} {{ chartData.close }}</span>&nbsp; <span class="red">{{
              chartData?.changeRatio
            }}</span>&nbsp; <span class="red">{{ chartData?.netChange }}</span>&nbsp;
          </p>
        </div>
        <!-- <div class="line-first-r">
          <div class="mt-22 mb-22" style="position:relative;">
            <div class="flex justify-between items-center w-full h-70" @click="selectBtn">
              <div class="select-label">5x</div>
              <img src="@/assets/image/quotes/grey-select.png" alt="select-icon" class="select-icon" />
            </div>
            <div class="option-box" v-show="isShow">
              <div class="text-30" v-for="item in selectData" :key="item.type" @click="selectItem(item)">{{
                item.title }}
              </div>
            </div>
          </div>
        </div> -->
      </div>
      <div @click="handleChangeType">
        <div class="title-box">
          <img :src="handleImage(indexSetting)" alt="" class="select-icon" />
        </div>
      </div>
    </div>
    <!-- <div class="kline-container flex" v-if="showKlineChart">
      <div class="chart-index">
        <fx-kline :height="500" :symbol="symbol" :isShowsolid="true" :chartType="chartType" v-if="symbol" @data="onData"
          :key="`${symbol}-${timeValue}`" />
      </div>
    </div> -->
    <div class="orderbook-container flex justify-between px-30 py-30">
      <div
        :class="{ 'trade-order-area': true, 'sell-box': props.isSell, 'trade-order-area-full': ['JP-stocks', 'HK-stocks','UK-stocks','DE-stocks','BZ-stocks'].includes(symbolType) }">
        <!-- TODO: 搜索 -->
        <!-- <div class="name-input" @click="showSearch = true"> -->
        <div class="name-input">
          <van-field v-model="symbol" :placeholder="t('StockCodeShortPin')" autocomplete="off" readonly />
        </div>
        <div class="name-input type-input">
          <div class="mt-22 mb-22" style="position: relative">
            <div class="flex justify-between items-center w-full h-70" @click="selectBtn">
              <div class="select-label">{{ selectData[curNameIndex]?.title }}</div>
              <img src="@/assets/image/quotes/grey-select.png" alt="select-icon" class="select-icon" />
            </div>
            <div class="option-box" v-show="isShow">
              <div class="text-30" v-for="(item, index) in selectData" :key="item.type" @click="selectItem(item, index)">
                {{ item.title }}
              </div>
            </div>
          </div>
        </div>
        <div class="name-input current-type">
          <van-field v-if="curNameIndex === 0" v-model="form.price" autocomplete="off" :readonly="curNameIndex === 0"
            :placeholder="t('price')">
            <!-- <template #button>
              <span size="small" type="primary">USD</span>
            </template> -->
          </van-field>
          <van-field v-else v-model="form.price" autocomplete="off" :readonly="curNameIndex === 0"
            :placeholder="t('price')">
            <!-- <template #button>
              <span size="small" type="primary">USD</span>
            </template> -->
          </van-field>
        </div>
        <div class="flex total-list">
          <div class="total-div" :class="activeBtn === item.type ? 'active-bg' : ''" v-for="item in btnList"
            :key="item.type" @click="onTriggerBtn(item)">
            {{ item.title }}
          </div>
        </div>
        <div class="price-input" v-if="activeBtn === 2">
          <van-stepper v-model="form.total" input-width="142px" button-size="34px" class="price-stepper"
            :placeholder="`${t('lumpSum')}`" allow-empty integer />
        </div>
        <div v-else class="number-input">
          <van-stepper v-model="form.volume" input-width="142px" button-size="34px" class="price-stepper"
            :placeholder="t('数量')" allow-empty integer />
        </div>
        <p class="buy-number">
          {{ props.isSell ? t("可卖") : t("可用") }}
          <span class="num">{{ initOpen.volume ? Math.floor(initOpen.volume * 1) : 0 }}
          </span>
          {{ props.isSell ? symbol : "" }}
        </p>
        <div class="freight">
          <span :class="activePercenIndex === index ? 'active' : ''" v-for="(item, index) in percenList"
            @click="exchangeVal(item, index)" :key="index">{{ item }}%</span>
        </div>
        <van-button type="danger" class="buy-btn" @click="handleOrder()">{{
          props.isSell ? t("卖出") : t("买入")
        }}
        </van-button>
        <p class="desc">
          <span class="label">
            {{ t("IndividualStockPositions") }}:<span class="num">100%</span>
          </span>
          <span class="label">
            {{ t("totalPosition") }}:<span class="num">100%</span>
          </span>
        </p>
      </div>
      <div class="trade-deep-data" v-if="!['HK-stocks', 'JP-stocks','UK-stocks','DE-stocks','BZ-stocks'].includes(symbolType)">
        <keep-alive>
          <trade-deep-data :symbol="symbol" v-if="symbol" :price="form.price" class="trade-deep-container"
            @newPrice="getNewPrice" />
        </keep-alive>
      </div>
      <div class="trade-deep-data" v-else>
        <div class="index-card flex-r" v-if="!['HK-stocks', 'JP-stocks','UK-stocks','DE-stocks','BZ-stocks'].includes(symbolType)">
          <div class="flex-r-item">
            <p>
              <span class="label">{{ $t('最高') }}</span>
              <span class="value">{{ formatMoney(chartData?.high) }}</span>
            </p>
            <p>
              <span class="label">{{ $t('最低') }}</span>
              <span class="value">{{ formatMoney(chartData?.low) }}</span>
            </p>
            <p>
              <span class="label">{{ $t('成交量') }}</span>
              <span class="value">{{ formatMoney(chartData?.volume) }}</span>
            </p>
            <p>
              <span class="label">{{ $t('换手率') }}</span>
              <span class="value">{{
                chartData?.turnoverRate ? formatMoney(chartData?.turnoverRate) + '%' : '-'
              }}</span>
            </p>
            <p>
              <span class="label">{{ t('marketValue') }}</span>
              <span class="value">{{ formatMoney(chartData?.marketCapital) }}</span>
            </p>
            <p>
              <span class="label">{{ $t('amplitude') }}</span>
              <span class="value">{{ formatMoney(chartData?.changeRatio) }}</span>
            </p>
            <p>
              <span class="label">{{ $t('pb') }}</span>
              <span class="value">{{ formatMoney(chartData?.pb) }}</span>
            </p>
            <p>
              <span class="label">{{ $t('eps') }}</span>
              <span class="value">{{ formatMoney(chartData?.eps) }}</span>
            </p>
            <p>
              <span class="label">{{ $t('navps') }}</span>
              <span class="value">{{ formatMoney(chartData?.navps) }}</span>
            </p>
          </div>
        </div>
      </div>
    </div>
    <!-- <div class="orderbook-container flex justify-between px-30 py-30 hk-stocks-orderbook" v-else>
      <div :class="{ 'trade-order-area': true, 'sell-box': props.isSell }">
        <div class="name-input">
          <van-field v-model="symbol" :placeholder="t('StockCodeShortPin')" autocomplete="off" readonly />
        </div>
        <div class="name-input type-input">
          <div class="mt-22 mb-22" style="position:relative;">
            <div class="flex justify-between items-center w-full h-70" @click="selectBtn">
              <div class="select-label">{{ selectData[curNameIndex]?.title }}</div>
              <img src="@/assets/image/quotes/grey-select.png" alt="select-icon" class="select-icon" />
            </div>
            <div class="option-box" v-show="isShow">
              <div class="text-30" v-for="(item, index) in selectData" :key="item.type" @click="selectItem(item, index)">
                {{
                  item.title }}
              </div>
            </div>
          </div>
        </div>
        <div class="name-input current-type">
          <van-field v-if="curNameIndex === 0" v-model="form.price" autocomplete="off" :readonly="curNameIndex === 0"
            :placeholder="t('price')">
            <template #button>
              <span size="small" type="primary">USD</span>
            </template>
          </van-field>
          <van-field v-else v-model="form.price" autocomplete="off" :readonly="curNameIndex === 0"
            :placeholder="t('price')">
            <template #button>
              <span size="small" type="primary">USD</span>
            </template>
          </van-field>
        </div>
        <div class="flex total-list">
          <div class="total-div" :class="activeBtn === item.type ? 'active-bg' : ''" v-for="item in btnList"
            :key="item.type" @click="onTriggerBtn(item)"> {{ item.title }} </div>
        </div>
        <div class="price-input" v-if="activeBtn === 2">
          <van-stepper v-model="form.total" input-width="142px" button-size="34px" class="price-stepper"
            :placeholder="`${t('lumpSum')}`" allow-empty integer />
        </div>
        <div v-else class="number-input">
          <van-stepper v-model="form.volume" input-width="142px" button-size="34px" class="price-stepper"
            :placeholder="t('数量')" allow-empty integer />
        </div>
        <p class="buy-number">{{ props.isSell ? t('可卖') : t('可用') }}
          <span class="num">{{ initOpen.volume ?
            Math.floor(initOpen.volume * 1) : 0 }}
          </span>
          {{ props.isSell ? symbol : 'USD' }}
        </p>
        <div class="freight">
          <span :class="activePercenIndex === index ? 'active' : ''" v-for="(item, index) in percenList"
            @click="exchangeVal(item, index)">{{ item }}%</span>
        </div>
        <van-button type="danger" class="buy-btn" @click="handleOrder()">{{ props.isSell ? (t('卖出'))
          : (t('买入'))
        }}</van-button>
        <p class="desc">
          <span class="label">
            {{ t('IndividualStockPositions') }}:<span class="num">100%</span>
          </span>
          <span class="label">
            {{ t('totalPosition') }}:<span class="num">100%</span>
          </span>
        </p>
      </div>
    </div> -->
    <div class="indicator-index-container">
      <div class="indicator-index-box">
        <div class="flex flex-1">
          <ul class="flex">
            <li :class="tabType === 1 ? 'active' : ''" @click="tabClick(1)">
              {{ t("当前委托") }}
            </li>
            <li :class="tabType === 2 ? 'active' : ''" @click="tabClick(2)">
              {{ t("历史委托") }}
            </li>
            <li :class="tabType === 3 ? 'active' : ''" @click="tabClick(3)">
              {{ t("资产") }}
            </li>
            <!-- <li>{{ t('成交历史') }}</li> -->
          </ul>
        </div>
        <img src="../../../assets/image/public/record.png" alt="record-img" class="pr-5 record-img" @click="goHistory" />
      </div>
    </div>
    <section class="etf-container">
      <div class="all-etf-ranking">
        <div class="etf-table">
          <div style="padding: 0 15px 47px" v-if="tabType == '1'">
            <EntrustItem v-for="item in entrustList" :key="item.order_no" :entrust="item" state="submitted"
              @cancelOrder="cancelOrder" />
          </div>
          <div class="pl-10 pr-10 pb-50" v-else-if="tabType == '2'">
            <history-item :unit="currentUnit(symbolType)" v-for="item in entrustList" :key="item.order_no" :coinPrice="coinPrice"
              :entrust="item" :state="item.state" @cancelOrder="cancelOrder" />
          </div>
          <div class="pl-5 pr-5" v-else>
            <div class="mb-10 border-b assets-box">
              <p class="text-28 text-grey mt-4 mb-22">{{ $t("当前币对资产") }}</p>
              <ul>
                <li v-for="item in pairsList" :key="item.symbol" class="flex justify-between py-2">
                  <div class="flex items-center">
                    <img :src="item.symbol
                      ? `${HOST_URL}/symbol/${item.symbol.toLowerCase()}.png`
                      : require('@/assets/loading-default.png')
                      " class="w-15 h-15 rounded-full mr-5 usdt-icon" />
                    <p class="flex flex-col">
                      <strong class="text-28 textColor mb-6">{{
                        item.symbol.toUpperCase()
                      }}</strong>
                      <span class="text-28 text-grey">{{ item.full_name }}</span>
                    </p>
                  </div>
                  <div class="flex flex-col items-end text-grey">
                    <strong class="text-28 textColor mb-6">{{ item.usable }}</strong>
                    <span class="text-28 text-grey">
                      {{ currency.currency_symbol }}
                      {{ item.usableUsdt ? (item.usableUsdt * currency.rate).toFixed(2) : "0.0" }}
                    </span>
                  </div>
                </li>
              </ul>
            </div>
            <div class="mb-60 border-b">
              <p class="text-28 text-grey mt-14 mb-42">{{ $t("其他非0资产") }}</p>
              <ul>
                <li v-for="item in no_zeroList" :key="item.symbol" class="flex justify-between py-2">
                  <div class="flex items-center">
                    <img :src="item.symbol
                      ? `${HOST_URL}/symbol/${item.symbol.toLowerCase()}.png`
                      : require('@/assets/loading-default.png')
                      " class="w-13 h-13 rounded-full mr-5 usdt-icon" />
                    <p class="flex flex-col">
                      <strong class="text-28 textColor mb-6">{{
                        item.symbol.toUpperCase()
                      }}</strong>
                      <span class="text-28 text-grey">{{ item.full_name }}</span>
                    </p>
                  </div>
                  <div class="flex flex-col items-end">
                    <strong class="text-28 textColor mb-6">{{ item.usable }}</strong>
                    <span class="text-28 text-grey">
                      {{ currency.currency_symbol }}
                      {{ item.usableUsdt ? (item.usableUsdt * currency.rate).toFixed(2) : "0.0" }}
                    </span>
                  </div>
                </li>
              </ul>
            </div>
          </div>
          <div v-if="tabType == '1' && !entrustList.length"
            class="no-data flex flex-col justify-center items-center pt-185 pb-100">
            <img src="@/assets/image/assets-center/no-data.png" alt="no-date" />
            <p class="textColor">{{ $t("暂无数据") }}</p>
          </div>
        </div>
      </div>
    </section>
    <van-popup v-model:show="showSearch" :close-on-click-overlay="false" closeable overlay-class="overlay-container"
      position="bottom" class="popup-container" safe-area-inset-bottom>
      <template #overlay-content>
        <van-search class="search-input flex-1" v-model="searchVal" @input="onInput" :placeholder="t('stockName')"
          @clear="onClearSearch" background="rgba(0,0,0,0.5)" />
      </template>
      <p class="title">{{ t("searchResult") }}</p>
      <ul class="search-result">
        <li @click="handleClickSearchResultItem()">
          <div class="flex-l">
            <div class="icon shen">深A</div>
            <span>深物业A</span>
          </div>
          <div class="flex-r">
            <span>000011</span>
          </div>
        </li>
        <li>
          <div class="flex-l">
            <div class="icon us">美股</div>
            <span>特斯拉</span>
          </div>
          <div class="flex-r">
            <span>000011</span>
          </div>
        </li>
        <li>
          <div class="flex-l">
            <div class="icon module">板块</div>
            <span>创业板</span>
          </div>
          <div class="flex-r">
            <span>000011</span>
          </div>
        </li>
      </ul>
    </van-popup>
    <div class="fixed-box">
      <div class="flex title justify-between items-center px-30" @click.stop="handleClickShowKlineChart()">
        <span class="text-26 textColor">{{ symbol.toUpperCase() }}&nbsp;&nbsp;{{ $t("k线图表") }}</span>
        <van-icon class="textColor text-20" :name="showCharts ? 'arrow-down' : 'arrow-up'"></van-icon>
      </div>
      <section class="indicator-index-container" v-if="showKlineChart">
        <div class="indicator-index-box">
          <div class="flex-l">
            <ul>
              <li v-for="(item, index) in filterOne" :key="item" @click="handleClickSelectTime(item, index)"
                :class="[item.index === timeLabelActive ? 'active' : '']">
                {{ item.name }}
              </li>
              <li @click="handleClickMoreBtn">{{ t("更多") }}</li>
            </ul>
          </div>
        </div>
        <div class="indicator-index-box-second" v-if="showMore">
          <ul>
            <li v-for="(item, index) in filterTwo" :key="item" @click="handleClickSelectTime(item, index)"
              :class="[item.index === timeLabelActive ? 'active' : '']">
              {{ item.name }}
            </li>
          </ul>
        </div>
      </section>
      <div class="kline-container flex" v-if="showKlineChart">
        <div class="chart-index">
          <fx-kline :height="500" :symbol="symbol" :isShowsolid="true" :chartType="chartType" v-if="symbol" @data="onData"
            :key="`${symbol}-${timeValue}`" />
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import {
  ref,
  onMounted,
  onBeforeUnmount,
  computed,
  reactive,
  onUnmounted,
  watch,
} from "vue";
import { useRoute, useRouter } from "vue-router";
import { Tab, Tabs } from "vant";
import {
  _tradeBuy,
  _tradeSell,
  _getBalance,
  _openView,
  _closeView,
} from "@/service/trade.api.js";
import { _queryBySymbol } from "@/service/etf.api.js";
import { _getQuotes } from "@/service/quotes.api";
import { useI18n } from "vue-i18n";
import { useQuotesStore } from "@/store/quotes.store";
import { useUserStore } from "@/store/user";
import TradeDeepData from "@/components/trade-deep-data/index.vue";
import EntrustItem from "./EntrustItem.vue";
import HistoryItem from "@/components/Transform/history-item/index.vue";
import { showToast } from "vant";
import fxKline from "@/components/fx-kline/index.vue";
import { WS_URL, HOST_URL } from "@/config/index.js";
import Axios from "@/service/trading";
import TradeApi from "@/service/trading.js";
import { _getHomeList } from "@/service/home.api";
import { themeStore } from "@/store/theme";
import store from "@/store/store";
import { SET_STAGE } from "@/store/types.store";
import { formatMoney } from '@/utils/index.js'
import { currentUnit } from '@/utils/coinUnit.js'

const thStore = themeStore();
const THEME = thStore.theme;
const { t } = useI18n();
const props = defineProps({
  symbolFlag: {
    type: String,
    default: "",
  },
  isSell: {
    type: Boolean,
    default: false,
  },
  price: {
    type: [Number, String],
    default: "0.00",
  },
});
const filterOne = ref([
  { name: t("分时"), paramsValue: "timeSharing", seconds: 1 * 60 * 1000, index: 0 },
  {
    name: "1" + t("天"),
    paramsValue: "1day",
    seconds: 1 * 24 * 60 * 60 * 1000,
    index: 1,
  },
  {
    name: "1" + t("周"),
    paramsValue: "1week",
    seconds: 7 * 24 * 60 * 60 * 1000,
    index: 2,
  },
  {
    name: "1" + t("月"),
    paramsValue: "1mon",
    seconds: 30 * 24 * 60 * 60 * 1000,
    index: 3,
  },
  {
    name: "5" + t("天"),
    paramsValue: "5day",
    seconds: 5 * 24 * 60 * 60 * 1000,
    index: 4,
  },
  {
    name: t("season"),
    paramsValue: "1quarter",
    seconds: 3 * 30 * 24 * 60 * 60 * 1000,
    index: 5,
  },
  {
    name: t("Year"),
    paramsValue: "1year",
    seconds: 12 * 30 * 24 * 60 * 60 * 1000,
    index: 6,
  },
]);
const filterTwo = ref([
  { name: "120" + t("分"), paramsValue: "120min", seconds: 2 * 60 * 60 * 1000, index: 7 },
  { name: "60" + t("分"), paramsValue: "60min", seconds: 1 * 60 * 60 * 1000, index: 8 },
  { name: "30" + t("分"), paramsValue: "30min", seconds: 30 * 60 * 1000, index: 9 },
  { name: "15" + t("分"), paramsValue: "15min", seconds: 15 * 60 * 1000, index: 10 },
  { name: "5" + t("分"), paramsValue: "5min", seconds: 5 * 60 * 1000, index: 11 },
  { name: "1" + t("分"), paramsValue: "1min", seconds: 1 * 60 * 1000, index: 12 },
]);
const timeLabelActive = ref(1);
const chartType = ref("");
const quotesStore = useQuotesStore();
const userStore = useUserStore();
const timeValue = ref("");
const router = useRouter();
const route = useRoute();
const pairsList = ref([]);
const timeOut = ref(null);
const newPrice = ref(0); //最新价
// const amountValue = ref('') // 张数文本框
const symbolType = route.query.type;
const symbol = ref(route.query.symbol);
const showKlineChart = ref(false);
const showSearch = ref(false);
const isOpen = ref(false);
const searchVal = ref("");
const isShow = ref(false);
const currentType = ref(props.isSell ? "close" : "open");
const sessionToken = ref("");
const emits = defineEmits(["updateActive"]);
const showMore = ref(false);
const showCharts = ref(false);
const indexSetting = new URL(
  `../../../assets/theme/${thStore.theme}/image/index-setting.png`,
  import.meta.url
);
const selectData = ref([
  {
    type: "1",
    title: t("市价委托"),
  },
  {
    type: "2",
    title: t("限价委托"),
  },
]);
const btnList = ref([
  {
    type: 1,
    title: t("volumn"),
  },
  {
    type: 2,
    title: t("总额"),
  },
]);
const activeBtn = ref(1);
const curNameIndex = ref(0);
const percenList = ref([25, 50, 75, 100]);
const activePercenIndex = ref(-1);
const percentageVal = ref(0);
const initOpen = reactive({});
const no_zeroList = ref([]);
const entrustList = ref([]);
const tabType = ref(1); // 1: 当前委托 2: 历史委托, 3： 资产， 4：成交历史

const isTotal = computed(() => {
  return activeBtn.value == 2;
});
const form = reactive({
  volume: "",
  session_token: "",
  price: "",
  total: "",
  order_price_type: "opponent", // 市价or限价
});
const chartData = ref({});
const currency = ref(store.state.home.currency);
let socket = null;
const timer = ref(null);
const isLoading = ref(false);

watch(
  () => props.symbolFlag,
  (nv) => {
    initPage(nv);
    if (socket) {
      closeSocket();
    }
    startQuoteSocket();
  }
);

onMounted(async () => {
  initPage(props.symbolFlag);
  handleClickSelectTime({
    paramsValue: "1day",
    seconds: 1 * 24 * 60 * 60 * 1000,
    index: 1,
  });
});

onBeforeUnmount(() => {
  if (timeOut.value) {
    clearTimeout(timeOut.value);
  }
});

const initPage = (nv) => {
  symbol.value = nv;
  if (!socket) {
    startQuoteSocket();
  }
  // TODO:未开盘不能下单，暂时不处理
  // queryBySymbol()
  initParam(symbol.value, currentType.value);
  tabClick(1);
  getCoinPrice(symbol.value);
};
const handleClickMoreBtn = () => {
  showMore.value = !showMore.value;
};

const handleClickSelectTime = (params) => {
  const { paramsValue, seconds, index } = params;
  timeLabelActive.value = index;
  quotesStore[SET_STAGE]({ stage: paramsValue, seconds });
  onSelectTime(paramsValue);
};
const onSelectTime = (evt) => {
  timeValue.value = evt;
  if (evt == "timeSharing") {
    chartType.value = "area";
  } else {
    chartType.value = "candle_solid";
  }
};

const tabClick = (type) => {
  tabType.value = type;
  entrustList.value = [];
  if (type == 3) {
    if (!userStore.userInfo.token) {
      router.push("/login");
    } else {
      getWallet();
    }
  } else {
    if (userStore.userInfo.token) {
      getOrderList();
    }
  }
};
const exchangeVal = (val, index) => {
  activePercenIndex.value = index;
  percentageVal.value = val;
  if (form.price === "0.00" || initOpen.volume === "0") return;
  if (!isTotal.value) {
    if (currentType.value == "open") {
      let sum = (parseFloat(initOpen.volume) * (val / 100)) / parseFloat(form.price);
      form.volume = Math.floor(sum * 100000) / 100000;
    } else {
      let sum = parseFloat(initOpen.volume);
      form.volume = Math.floor(sum * (val / 100) * 100000) / 100000;
    }
  } else {
    if (currentType.value == "open") {
      form.total = initOpen.volume * (val / 100);
    } else {
      form.total =
        Math.floor(initOpen.volume * (val / 100) * parseFloat(form.price) * 1000) / 1000;
    }
  }
};
const onTriggerBtn = (item) => {
  activeBtn.value = item.type;
  form.volume = "";
  form.total = "";
  activePercenIndex.value = -1;
};
const selectBtn = () => {
  isShow.value = !isShow.value;
};
const selectItem = (item, index) => {
  form.order_price_type = item.type === "1" ? "opponent" : "limit";
  curNameIndex.value = index;
  isShow.value = false;
};

const getNewPrice = (value) => {
  newPrice.value = value;
};

const handleClickSearchResultItem = (value) => {
  console.log(value, "value");
};

const handleClickShowKlineChart = () => {
  showKlineChart.value = !showKlineChart.value;
};

const onClearSearch = () => {
  searchVal.value = "";
};

const handleChangeType = () => {
  if (symbolType === "indices") {
    router.push(`/quotes/detail?symbol=${symbol.value}&tabActive=0`)
  } else if (symbolType === "US-stocks") {
    router.push(`/quotes/usStockDetail?symbol=${symbol.value}&tabActive=3&symbolType=US-stocks`)
  } else if (symbolType === "HK-stocks") {
    router.push(`/quotes/usStockDetail?symbol=${symbol.value}&tabActive=4&symbolType=HK-stocks`)
  } else if (symbolType === "TW-stocks") {
    router.push(`/quotes/detail?symbol=${symbol.value}&tabActive=5&symbolType=TW-stocks&type=TW-stocks`)
  } else if (symbolType === "JP-stocks") {
    router.push(`/quotes/usStockDetail?symbol=${symbol.value}&tabActive=6&symbolType=JP-stocks`)
  } else if (symbolType === "A-stocks") {
    router.push(`/quotes/detail?symbol=${symbol.value}&tabActive=7&symbolType=A-stocks&type=A-stocks`)
  } else if (symbolType === "UK-stocks") {
    router.push(`/quotes/usStockDetail?symbol=${symbol.value}&tabActive=9&symbolType=UK-stocks`)
  }else if (symbolType === "DE-stocks") {
    router.push(`/quotes/usStockDetail?symbol=${symbol.value}&tabActive=10&symbolType=DE-stocks`)
  }else if (symbolType === "BZ-stocks") {
    router.push(`/quotes/usStockDetail?symbol=${symbol.value}&tabActive=11&symbolType=BZ-stocks`)
  } else {
    router.push(`/quotes/usStockDetail?symbol=${symbol.value}&tabActive=4&symbolType=HK-stocks`)
  }
};

// 事件
const onData = (data) => {
  chartData.value = data;
};

const queryBySymbol = () => {
  _queryBySymbol(symbol.value).then((data) => {
    isOpen.value = data.open;
  });
};

const openView = () => {
  console.log("openView");
  _openView({
    token: userStore.userInfo.token,
  }).then((res) => {
    console.log(sessionToken.value, "sessionToken.value");
    sessionToken.value = res.session_token;
  });
};

const closeView = () => {
  console.log("closeView");
  _closeView({
    token: userStore.userInfo.token,
    symbol: symbol.value,
  }).then((res) => {
    (sessionToken.value = res.session_token || ""), (volume.value = res.volume || 0);
  });
};

// 开仓
const handleOrder = () => {
  if (!userStore.userInfo?.token) {
    router.push("/login");
    return;
  }
  let volume = "";
  if (isTotal.value) {
    if (!form.total) {
      showToast(t("请输入总额"));
      return;
    }
    if (currentType.value === "open") {
        form.volume = parseFloat(form.total).toFixed(5);
        form.total = ((parseFloat(form.total) / parseFloat(form.price))).toFixed(5)
    } else {
      form.volume = parseFloat(form.total) / parseFloat(form.price);
      form.volume = form.volume.toFixed(5);
    }
    volume = form.volume;
  } else {
    if (!form.volume) {
      showToast(t("请输入数量"));
      return;
    }
    if (currentType.value === "open") {
        form.total = form.volume
      volume = parseFloat(form.volume) * parseFloat(form.price);
    } else {
      volume = form.volume;
    }
  }
  // if (currentType.value === 'open') {
  //   form.session_token = this.initOpen.session_token
  // } else {
  //   form.session_token = this.initClose.session_token
  // }
  const emitFunc = currentType.value;

  let _order = currentType.value === "open" ? _tradeBuy : _tradeSell;
  const params = {
    volume,
    session_token: sessionToken.value,
    symbol: symbol.value, // 币种
    price: form.price,
    total: form.total,
    order_price_type: form.order_price_type, // 市价or限价
  };
  isLoading.value = true;
  _order(params)
    .then((res) => {
      isLoading.value = false;
      showToast(t("successfullyOrdered"));
      activePercenIndex.value = -1;
      form.volume = "";
      form.total = "";
      _getBalance({
        token: userStore.userInfo.token,
      }).then((data) => {
        // 刷新余额
        userStore["SET_USERINFO"]({ balance: data.money });
        store.commit("user/SET_USERINFO", { balance: data.money });
      });
      initParam(symbol.value, emitFunc);
    })
    .catch((e) => {
      // 也需要重新初始化
      showToast(t(e));
      isLoading.value = false;
      timeOut.value = setTimeout(() => {
        initParam(symbol.value, emitFunc);
      }, 2000);
    });
};
const closeSocket = () => {
  socket && socket.close();
  socket = null;
};
const startQuoteSocket = () => {
  // 行情socket
  if (!symbol.value) {
    return;
  }
  socket = new WebSocket(`${WS_URL}/1/${symbol.value}`);

  socket.onmessage = (evt) => {
    const { data } = evt;
    const { code, data: _data } = JSON.parse(data);
    if (code / 1 === 0) {
      handleQoutes(_data);
    }
  };
};
const handleQoutes = (data) => {
  if (data && data.length) {
    const cur = data[0];
    chartData.value = cur;
    if (curNameIndex.value === 0) {
      form.price = cur.close;
    }
  }
};

const initParam = (symbol, type) => {
  // 初始化参数
  if (userStore.userInfo.token) {
    getWallet();
    getOrderList();
    if (type === "open") {
      //console.log('开仓数据')
      Axios.tradeBuyToken({ type : symbolType}).then((res) => {
        initOpen.volume = res.volume;
        initOpen.fee = res.fee;
        initOpen.session_token = res.session_token;
        sessionToken.value = res.session_token;
      });
    }
    if (type === "close") {
      Axios.tradeSellToken({
        symbol,
      }).then((res) => {
        console.log("close res:", res);
        initOpen.volume = res.volume;
        initOpen.fee = res.fee;
        initOpen.session_token = res.session_token;
        sessionToken.value = res.session_token;
      });
    }
  }
};

const getWallet = () => {
  TradeApi.getPairs({
    pairs: `${symbol.value}/usdt`,
    symbolType,
  }).then((res) => {
    pairsList.value = res.pairs;
    no_zeroList.value = res.no_zero;
  });
};
const loadInit = () => {
  activePercenIndex.value = -1;
  form.total = "";
  form.volume = "";
  initParam(symbol.value, currentType.value);
};

// 定时器
const clearTimer = () => {
  clearInterval(timer.value);
  timer.value = null;
};

const getOrderList = () => {
  clearTimer();
  let type = tabType.value == 1 ? "orders" : "hisorders";
  apiGetOrderList(type);
  timer.value = setInterval(() => {
    apiGetOrderList(type);
  }, 1000);
};

const apiGetOrderList = (type) => {
  TradeApi.tradeRecord({
    page_no: 1,
    symbol: symbol.value,
    type: type,
  })
    .then((res) => {
      // console.log(res.data);
      entrustList.value = res;
    })
    .catch(() => {
    });
};

const cancelOrder = (order) => {
  TradeApi.tradeCancel({
    order_no: order,
  })
    .then((res) => {
      initParam(symbol.value, currentType.value);
      showToast(t("操作成功"));
    })
    .catch(() => {
    });
};
const coinPrice = ref(0);
const getCoinPrice = (val) => {
  isLoading.value = true;
  _getHomeList(val)
    .then((res) => {
      isLoading.value = false;
      coinPrice.value = res[0].close;
    })
    .catch((e) => {
      isLoading.value = false;
    });
};
const goHistory = () => {
  emits("updateActive");
};
const handleImage = (url) => {
  return new URL(url, import.meta.url).href;
};
onUnmounted(() => {
  closeSocket();
  clearTimer();
});
defineExpose({ loadInit });
</script>
<style lang="scss" scoped>
.loading-box {
  position: absolute;
  top: 30%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 999;
}

:deep(.van-popup) {
  height: 75%;
}

:deep(.van-overlay) {
  background: rgba(0, 0, 0, 0.5);
}

:deep(.van-popup) {
  background-color: $main2_background;
}

:deep(.van-search__content) {
  height: 32px;
  background: $border_color;
}

:deep(.van-field__control) {
  color: $text_color;
}

:deep(.van-field__control::placeholder) {
  color: #747a8f;
  font-size: 12px;
}

:deep(.van-search__field.search-input) {
  line-height: 32px;
}

:deep(.van-search__action:active) {
  background: $selectSymbol_background;
  color: $color_main;
  font-size: 12px;
}

:deep(.search-result .van-cell) {
  background: $mainBgColor;
}

:deep(.van-tabs__nav) {
  background: $selectSymbol_background;
}

:deep(.van-tab) {
  font-size: 14px;
  color: $text_color;
  font-weight: 400;
}

:deep(.van-tabs__content) {
  margin-top: 20px;
}

:deep(.van-tab.van-tab--active) {
  font-weight: 700;
}

:deep(.van-button--danger) {
  border: none;
  background-color: $red;
}

:deep(.sell-box .van-button--danger) {
  border: none;
  background-color: $btn_main;
}

:deep(.trade-order-area .van-field) {
  padding: 4px 6px;
  background: $selectSymbol_background;
  border: 1px solid $red;
  border-radius: 2px;
}

:deep(.trade-order-area.sell-box .van-field) {
  padding: 4px 6px;
  background: $selectSymbol_background;
  border: 1px solid $color_main;
  border-radius: 2px;
}

:deep(.trade-order-area .van-field__control) {
  color: $text_color;
}

:deep(.van-stepper__minus) {
  background: $input_background;
  border: 1px solid $red;
  border-radius: 2px;
  color: $red;
}

:deep(.van-stepper__plus) {
  background: $input_background;
  border: 1px solid $red;
  border-radius: 2px;
  color: $red;
}

:deep(.van-stepper__input) {
  background: $input_background;
  color: $text_color;
  border-top: 1px solid $red;
  border-bottom: 1px solid $red;
  margin: 0;
}

:deep(.sell-box .van-stepper__minus) {
  background: $input_background;
  border: 1px solid $color_main;
  border-radius: 2px;
  color: $color_main;
}

:deep(.sell-box .van-stepper__plus) {
  background: $input_background;
  border: 1px solid $color_main;
  border-radius: 2px;
  color: $color_main;
}

:deep(.sell-box .van-stepper__input) {
  background: $input_background;
  color: $text_color;
  border-top: 1px solid $color_main;
  border-bottom: 1px solid $color_main;
  margin: 0;
}

:deep(.orderbook) {
  margin-bottom: 14px !important;
}

:deep(.van-popup.popup-container) {
  padding: 12px;

  .title {
    color: #747a8f;
    font-size: 12px;
  }

  .search-result {
    li {
      display: flex;
      padding: 12px;
      border-bottom: 1px solid #1f2944;
      font-size: 14px;

      .flex-l {
        width: 160px;
        display: flex;
        align-items: center;

        .icon {
          font-size: 10px;
          border-radius: 3px;
          line-height: 16px;
          height: 18px;
          width: 30px;
          text-align: center;
          margin-right: 10px;
        }

        .us,
        .sh {
          color: #893559;
          border: 1px solid #893559;
        }

        .shen {
          color: #db9461;
          border: 1px solid #db9461;
        }

        .module {
          color: #5f5f5f;
          border: 1px solid #5f5f5f;
        }
      }
    }
  }
}

.sell-box {
  .type-input {
    padding: 4px 6px;
    background: $selectSymbol_background;
    border: 1px solid $color_main !important;
    border-radius: 2px;
  }

  .total-div {
    flex: 1;
    text-align: center;
    padding: 4px 0;
    border: 1px solid $color_main !important;
  }

  .active-bg {
    background-color: $btn_main !important;
    color: $white;
  }
}

.container-box {
  .green {
    color: $green;
  }

  .red {
    color: $red;
  }

  .line-first-container {
    padding: 0 12px;
    justify-content: space-between;
    align-items: center;

    .line-first {
      display: flex;
      width: 210px;
      margin-right: 12px;
      justify-content: space-between;
      font-size: 12px;

      .select-label {
        padding-left: 4px;
      }

      .select-icon {
        height: 11px;
        width: 14px;
        padding-right: 4px;
      }
    }

    .line-first-l {
      flex: 1;
    }

    .line-first-r {
      padding: 0 2px;
      color: $text_color6;
      display: flex;
      align-items: center;
    }

    .title-box {
      flex: 1;
      display: flex;
      justify-content: flex-end;
      align-items: center;
      font-size: 12px;
      text-align: right;

      span {
        color: #747a8f;
      }

      img {
        margin-left: 10px;
        width: 12px;
        height: 12px;
      }
    }
  }

  .option-box {
    position: absolute;
    left: 0;
    right: 0;
    top: 4.625rem;
    width: 100%;
    background: $grey_bg;
    text-align: center;
    border-radius: 0.25rem;
    color: $text_color4;
    z-index: 9;
    font-size: 14px;
    font-weight: 700;
    color: $text_color;

    >div {
      padding: 1.875rem 0;
    }
  }

  .kline-container {
    margin-top: 10px;

    .order-book-container {
      padding: 100px 2px 0 6px;
      width: 130px;
      border-left: 1px solid #2e364f;
    }

    .chart-index {
      flex: 1;
      min-width: 200px;
    }

    .text-sm {
      position: relative;
    }

    .select-div {
      width: 100px;
      position: absolute;
      top: 30px;
      left: 0;
      z-index: 100;

      ul {
        box-shadow: 0px 3px 11px 0px rgb(0 0 0 / 10%);

        li {
          background: $mainbgWhiteColor;
          text-align: center;
          padding: 10px 0;
          font-size: 16px;
        }

        li:not(:last-child) {
          border-bottom: 1px solid $border-grey;
        }
      }
    }

    .active {
      background: $btn_main !important;
      color: $text_color;
    }
  }

  .inner-tab-container {
    margin-top: 8px;

    .flex-r {
      padding: 0 8px;
      flex: 1;
      display: flex;
      color: $text_color;

      .flex-r-item {
        display: inline-block;
        width: 100%;

        p {
          display: flex;
          justify-content: space-between;
        }

        .label {
          color: $lable_color;
          margin-right: 6px;
          text-align: left;
          padding-left: 5px;
        }
      }
    }
  }

  .orderbook-container {
    padding: 0 12px 16px;

    .trade-order-area {
      display: flex;
      flex-direction: column;
      width: 210px;
      font-size: 12px;
      margin-right: 12px;

      &-full {
        width: 100%;
        margin-right: 0;
      }

      .buy-btn {
        height: 32px;
      }

      .type-input {
        padding: 4px 6px;
        background: $selectSymbol_background;
        border: 1px solid $red;
        border-radius: 2px;
      }

      .name-input {
        margin-top: 10px;
      }

      .price-input {
        margin-top: 10px;
      }

      .number-input {
        margin-top: 10px;
      }

      .value-input {
        margin-top: 10px;
      }

      .buy-number {
        margin-top: 10px;
      }

      .num {
        color: #e19841;
        margin: 0 4px;
      }

      .freight {
        margin: 10px 0 14px;
        display: flex;

        span {
          flex: 1;
          background: $input_background;
          border-radius: 2px;
          text-align: center;

          &.active {
            color: red;
          }
        }

        span:not(:last-child) {
          margin-right: 8px;
        }
      }

      .desc {
        margin-top: 10px;
        display: flex;
        justify-content: space-between;
      }
    }

    :deep(.van-stepper) {
      width: 100%;
    }

    :deep(.van-stepper__input) {
      width: calc(100% - 68px) !important;
    }

    .trade-deep-data {
      flex: 1;
      margin-top: 10px;
    }

    .total-list {
      display: flex;
      font-size: 14px;
      align-items: center;
      justify-content: center;
      margin-bottom: 1.25rem;
      position: relative;
      margin-top: 10px;

      .total-div {
        flex: 1;
        text-align: center;
        padding: 4px 0;
        border: 1px solid $red;
      }

      .active-bg {
        background-color: $red;
        color: $white;
      }
    }
  }

  .hk-stocks-orderbook {
    padding: 0 16px 16px;

    .trade-order-area {
      width: 100%;
      margin-right: 0;
    }

    :deep(.van-stepper) {
      width: 100%;
    }

    :deep(.van-stepper__input) {
      width: calc(100% - 68px) !important;
    }

  }

  .indicator-index-container {
    .indicator-index-box {
      padding: 12px;
      display: flex;
      // justify-content: space-between;
      align-items: center;

      ul {
        display: flex;
        flex-wrap: wrap;

        li {
          text-align: center;
          margin: 0 4px;
          padding: 4px 10px;
          font-size: 14px;
          border-radius: 4px;
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

  .etf-container {
    padding: 0;

    .no-data {
      img {
        width: 85px;
        height: 85px;
      }
    }

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
        background: $input_background;
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
          padding: 12px 6px;
          display: flex;
          font-size: 12px;
          line-height: 18px;
          border-bottom: 1px solid $border_color;

          .gray-text {
            color: #bcbdc2;
            font-size: 12px;
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

  .overlay-container {
    .search-input {
      width: 100%;
      position: absolute;
      top: 148px;
    }
  }
}

.border-b {
  border-color: $border_color;
}

.record-img {
  display: block;
  width: 26px;
  height: 18px;
}

.fixed-box {
  position: fixed;
  width: 100%;
  bottom: 0;
  left: 0;
  z-index: 9;
  background: $recommend_bg;
  font-size: 14px;

  .title {
    padding: 10px 10px;
  }
}

.indicator-index-box-second {
  ul {
    display: flex;
    border: 1px solid $border_color;
    align-items: center;
    border-right: none;
  }

  li {
    flex: 1;
    height: 32px;
    line-height: 32px;
    text-align: center;
    font-size: 12px;
    border-right: 1px solid $border_color;
  }
}

.usdt-icon {
  width: 30px;
  height: 30px;
}
</style>
