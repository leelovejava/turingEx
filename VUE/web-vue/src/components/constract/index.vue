<!-- 合约入口文件 -->
<template>
  <div class="constract-page">
    <div class="home-container" key="home-containe">
      <div id="app">
        <div class="trade-box trade-panel-box">
          <!-- 上部分 -->
          <div class="trade-box-top">
            <!-- 左边 币种和K线图 -->
            <div class="top-left" ref="topleft">
              <div class="top-left-wrapper">
                <!-- 收藏的币种 -->
                <div class="allgoods" v-if="collectList.length > 0">
                  <i
                    style="color: #e0b525"
                    class="icon iconfont icon-Mul_favorite"
                  ></i>
                  <div
                    v-for="(item, index) in collectList"
                    class="item"
                    :key="index"
                  >
                    <span class="name">{{ item.name }}</span>
                    <span
                      :class="[
                        'text-end',
                        item.change_ratio >= 0 ? 'color-up' : 'color-down',
                      ]"
                      >{{ addAndSubtr(item.change_ratio) }}</span
                    >
                  </div>
                </div>
                <div style="position: relative">
                  <!-- 单个币种-->
                  <single-currency-info
                    :isFullscreen="isFullscreen"
                    :pageData="pageData"
                    :pageType="pageType"
                    @showSerachCollect="showSerachCollect"
                  />

                  <!-- 市场 搜索币种弹窗-->
                  <div class="search-dialog" v-show="showSearchModal == true">
                    <coin-search-collect
                      @checkCurrency="checkCurrency"
                      @CurrencySort="CurrencySort"
                      @collectFun="collectFun"
                      @deleteCollectFun="deleteCollectFun"
                      :listData="searchCoinList"
                      :AllListData="allListData"
                      @searchFun="remoteSearchAllSymbol"
                      :pageType="pageType"
                    />
                  </div>
                </div>
                <!-- K线 -->
                <div class="top-left-k">
                  <div class="kline-box comb-trade-kline-box">
                    <div class="kline-table">
                      <div class="chart-tr">
                        <div class="chart-td" id="parent_trade_k1">
                          <div class="chart-box">
                            <div class="chart-kline" id="trade_k1">
                              <!-- 具体K线 -->
                              <div
                                class="kline-wrap"
                                :class="[isFullscreen ? 'wrap-full' : '']"
                              >
                                <!-- K线操作栏 -->
                                <div
                                  :class="[
                                    isFullscreen
                                      ? 'k-toolbar-wrap-full'
                                      : 'k-toolbar-wrap ',
                                  ]"
                                >
                                  <div class="k-toolbar dark toolbar-bg">
                                    <div class="k-toolbar-btn">
                                      <div class="k-toolbar-left">
                                        <!-- 时间选择 -->
                                        <div
                                          class="periods"
                                          v-if="mapIndex == 0"
                                        >
                                          <ul>
                                            <li
                                              v-for="(item, index) in kTimeArr"
                                              :key="index"
                                              :class="
                                                kTimeIndex == item.data
                                                  ? 'selected'
                                                  : ''
                                              "
                                              @click="changeKTime(item)"
                                            >
                                              {{ item.title }}
                                            </li>
                                            <li
                                              v-if="
                                                pageType == 'etf' ||
                                                pageType == 'usStocks'
                                              "
                                              class="kTimeDivider"
                                            ></li>
                                            <!-- 更多 -->
                                            <div
                                              v-if="
                                                pageType == 'etf' ||
                                                pageType == 'usStocks'
                                              "
                                            >
                                              <li
                                                v-for="(it, i) in kTimeArrMore"
                                                :key="i"
                                                :class="
                                                  kTimeIndex == it.data
                                                    ? 'selected'
                                                    : ''
                                                "
                                                @click="changeKTime(it)"
                                              >
                                                {{ it.title }}
                                              </li>
                                            </div>
                                          </ul>
                                        </div>
                                      </div>
                                      <div class="k-toolbar-right">
                                        <p
                                          class="status-info"
                                          v-if="
                                            symbolMarketInfo?.market?.status
                                          "
                                        >
                                          <span
                                            >{{
                                              symbolMarketInfo?.market
                                                ?.status &&
                                              $t(
                                                `message.jiaoyi.${symbolMarketInfo?.market?.status}`
                                              )
                                            }}&nbsp;</span
                                          >
                                          <span class="time">{{
                                            symbolMarketInfo?.market?.time_str
                                          }}</span
                                          >&nbsp;
                                          <span>{{
                                            symbolMarketInfo?.market
                                              ?.time_zone &&
                                            $t(
                                              `message.jiaoyi.${symbolMarketInfo?.market?.time_zone}`
                                            )
                                          }}</span>
                                        </p>

                                        <!-- 基本版和深度图切换 -->
                                        <div class="toggle">
                                          <span
                                            :class="
                                              mapIndex == index
                                                ? 'selected'
                                                : ''
                                            "
                                            v-for="(item, index) in mapClass"
                                            @click="tabMap(index)"
                                            :key="index"
                                            >{{ item }}</span
                                          >
                                          <div @click="fullScreen">
                                            <img
                                              style="width: 20px"
                                              src="@/assets/images/quotes/Group2892.png"
                                              alt=""
                                            />
                                          </div>
                                        </div>
                                      </div>
                                    </div>
                                  </div>
                                </div>
                                <div
                                  class="kline-body-wrap"
                                  id="okline-wrap"
                                  style="display: flex"
                                >
                                  <div
                                    v-loading="loading"
                                    element-loading-spinner="el-icon-loading"
                                    element-loading-background="#171a1e"
                                    :element-loading-text="
                                      $t('message.jiaoyi.jiazaizhong') + '...'
                                    "
                                    :class="[
                                      isFullscreen ? 'fullscreen-box' : '',
                                    ]"
                                    id="kline-Box"
                                    style="
                                      background: rgb(18, 18, 18);
                                      width: 100%;
                                      position: relative;
                                    "
                                  >
                                    <kline-Box
                                      :data="KLInedata"
                                      :klineIndex="klineIndex"
                                      :isFullscreen="isFullscreen"
                                      :kTimeIndex="kTimeIndex"
                                      v-if="mapIndex == 0"
                                    />
                                    <deep-chart
                                      v-else-if="mapIndex == 1 && !isFullscreen"
                                      :isFullscreen="isFullscreen"
                                      :deepBuy="deepBuy"
                                      :deepSell="deepSell"
                                    />
                                    <deep-chart-full-screen
                                      v-else-if="mapIndex == 1 && isFullscreen"
                                      :deepBuy="deepBuy"
                                      :deepSell="deepSell"
                                    />
                                    <draw-line />

                                    <div
                                      v-if="isShowLoading"
                                      class="kline-loading"
                                      style="
                                        width: 100%;
                                        height: 100%;
                                        position: absolute;
                                        background-color: #171a1e;
                                        top: 0%;
                                        left: 16px;
                                        z-index: 2;
                                      "
                                    >
                                      <div class="kline-loading-dot"></div>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- 中间 最新订单 -->
            <div
              v-if="!['hkStocks', 'forex'].includes(pageType)"
              class="top-middle"
              :style="{ display: isFullscreen ? 'none' : 'block' }"
            >
              <OrderBookNew
                :pageData="pageData"
                :pageType="pageType"
                :changeClickData="handleChangeClickData"
                :sellList="deepDataSort(deepSell)"
                :buyList="deepDataSort(deepBuy)"
                :bigIndex="bigIndex"
                :recentList="wsTradeData"
                :unit="unit"
              />
            </div>
            <!-- 右边 下单区 -->
            <div
              class="top-right"
              @click="changeRule"
              :style="{
                display: isFullscreen ? 'none' : 'block',
                height: pageType == 'forex' ? '822px' : '100%',
              }"
            >
              <div class="place-order-container-common place-order-xl-box">
                <component
                  :is="createOrderKey"
                  :paramsType="paramsType"
                  :pageData="pageData"
                  :clickData="clickData"
                  :unit="unit"
                />
                <!-- 保证金比率 -->
                <div
                  class="cashbi margin-block"
                  v-if="$route.query.RouterName != 'delivery'"
                >
                  <div class="cashHead">
                    {{ $t("message.home.baozhengjinbilv") }}
                  </div>
                  <div class="cashbtn">
                    {{ $t("message.home.danbibaozhengjinmoshi") }}
                  </div>
                  <ul>
                    <li>
                      <div class="cash_left">
                        {{ $t("message.home.baozhengjinbilv") }}
                      </div>
                      <div class="cash_right">
                        <!--下了单才有，没下单没有-->
                        <i class="el-icon-stopwatch"></i>
                        {{ marginRate }}
                      </div>
                    </li>
                    <!--维持保证金 -->
                    <li>
                      <div class="cash_left">
                        {{ $t("message.home.weichibaozhengjin") }}
                      </div>
                      <div class="cash_right">{{ walletMoney }} {{ unit }}</div>
                    </li>
                    <!--保证金余额 -->
                    <li>
                      <div class="cash_left">
                        {{ $t("message.home.baozhengjinyue") }}
                      </div>
                      <div class="cash_right">
                        {{ marginBalance }} {{ unit }}
                      </div>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
          <!-- 下部分-->
          <div
            class="trade-box-bottom"
            :style="{ display: isFullscreen ? 'none' : '' }"
          >
            <!-- 仓位区域 -->
            <component
              class="position-wrapper"
              @infos="getInfos"
              :is="positionKey"
              :keys="key"
              :symbol="pageData?.symbol"
              :pageData="pageData"
              :paramsType="paramsType"
              :unit="unit"
            />
            <!-- 资产 -->
            <div
              class="cashbi asset-block"
              v-if="$route.query.RouterName != 'delivery'"
            >
              <div class="cashHead">{{ $t("message.jiaoyi.zichan") }}</div>
              <div class="assets-btns">
                <el-button
                  type="info"
                  size="small"
                  @click="gotoPage('reCharge')"
                  >{{ $t("message.user.chongzhi") }}</el-button
                >
                <el-button
                  type="info"
                  size="small"
                  @click="gotoPage('exchange')"
                  >{{ $t("message.user.duihuan") }}</el-button
                >
                <el-button
                  type="info"
                  size="small"
                  @click="gotoPage('withdraw')"
                  >{{ $t("message.user.tixian") }}</el-button
                >
              </div>
              <div class="cashbilist" v-if="existToken">
                <p>
                  {{ $t("message.jiaoyi.qianbaoyue")
                  }}<span>{{ walletMoney }} {{ unit }}</span>
                </p>
                <p>
                  {{ $t("message.jiaoyi.weishixianyinkui1")
                  }}<span>{{ profitLoss }} {{ unit }}</span>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <kline-dialog ref="kline_dialog" />
    <footer-com :footerLeftList="footerLeftList"></footer-com>
  </div>
</template>
<script>
import bus from "vue3-eventbus";
import { mapState, mapActions, mapStores } from "pinia";
import { ElMessage } from "element-plus";
import Axios from "@/api/currency.js";
import { addAndSubtr } from "@/utils/index";
import AxiosKline from "@/api/kline.js";
import { WhrWebSocket } from "@/utils/WhrWebSocket";
import klineDialog from "@comTrade/klineDialog.vue";
import klineBox from "@comTrade/kline.vue";
import deepChart from "@comTrade/deepChart.vue";
import deepChartFullScreen from "@comTrade/deepChartFullScreen.vue";
import DrawLine from "@comTrade/drawLine.vue";
import SingleCurrencyInfo from "@comConstract/components/singleCurrencyInfo.vue";
import OrderBookNew from "@comConstract/components/orderBookNew.vue";
import PerpeOrderVue from "@comConstract/PerpetualContract/createOrder.vue"; //下单组件
import PerpePositionVue from "@comConstract/PerpetualContract/position.vue";
import DeliveryOrderVue from "@comConstract/DeliveryContract/createOrder.vue";
import DeliveryPositionVue from "@comConstract/DeliveryContract/position.vue";
import FooterCom from "@comConstract/components/footer.vue";
import coinSearchCollect from "@comConstract/components/coinSearchCollect.vue";
import { useUserStore } from "@/store/user";
import { useCurrencyStore } from "@/store/currency";
import ConfigURL from "@/config/index";

export default {
  name: "constractPage",
  errorCaptured(err, vm, info) {
    console.log(`错误捕获: ${err.toString()}\ninfo: ${info}`);
    return true;
  },
  props: {
    pageType: {
      type: String,
      default: "etf", //页面类型
    },
    paramsType: {
      type: String,
      default: "indices",
    },
    defaultSymbol: {
      type: String,
      default: "SH513520", //默认的symbol
    },
  },
  data() {
    return {
      allListData: [],
      collectList: [], //自选币种
      sortIndex: 0,
      footerLeftList: [], // 底部数据
      searchCoinList: [], //币种搜索列表数据
      // 以上是币种收藏
      isFullscreen: false, //全屏
      show_cash: false, // 平仓提示

      showSearchModal: false, // 显示搜索
      showrule: false,
      ConfigURL,
      createOrderKey: "PerpeOrderVue", //下单的组件
      positionKey: "PerpePositionVue", //持仓的组件
      isError: false,
      searcIndexhData: [],
      searchData: "",
      tradeData: undefined, //ws实例
      deepData: undefined, //ws实例
      realData: undefined, //ws实例
      symbolMarketInfo: {},
      deepBuy: [],
      deepSell: [],
      mapClass: [this.$t("message.jiaoyi.jibenban")],
      mapIndex: 0,
      listData: [],
      currentTimer: undefined,
      lineTimer: undefined,
      pageTimer: undefined,
      pageData: {}, //当前币种的行情数据
      showName: "", //当前币种的名字
      Hotcurrency: [],
      unit: "USD",

      totalAsset: "0.00", //总资产
      walletMoney: "0.00", //钱包余额
      profitLoss: "0.00", //未实现盈亏
      marginRate: "0.0000", //保证金比率
      maintenanceMargin: "0.0000", //维持保证金 = 钱包余额

      wsTradeData: [], // 所有成交数据
      buyList: [],
      sellList: [],

      bigIndex: undefined,
      KLInedata: undefined,
      klineIndex: "", //面积图还是蜡烛图
      kParamsTime: "", // K线查询时间
      kTimeIndex: "", // 选中那个时间，etf默认1D，数字货币默认15min
      isShowLoading: true,
      clickData: undefined,

      kTimeArrCoin: [
        { data: "minHour", title: this.$t("message.jiaoyi.fenshi") },
        { data: "1min", title: this.$t("message.jiaoyi.1fenzhong") },
        { data: "5min", title: this.$t("message.jiaoyi.5fenzhong") },
        { data: "15min", title: this.$t("message.jiaoyi.15fenzhong") },
        { data: "30min", title: this.$t("message.jiaoyi.30fenzhong") },
        { data: "60min", title: this.$t("message.jiaoyi.1xiaoshi") },
        { data: "4hour", title: this.$t("message.jiaoyi.4xiaoshi") },
        { data: "1day", title: this.$t("message.jiaoyi.1ri") },
        { data: "1week", title: this.$t("message.jiaoyi.1zhou") },
        { data: "1mon", title: this.$t("message.jiaoyi.1yue") },
      ],
      kTimeArrETF: [
        { data: "minHour", title: this.$t("message.jiaoyi.fenshi") },
        { data: "1day", title: this.$t("message.jiaoyi.1ri") },
        { data: "5day", title: this.$t("message.jiaoyi.5ri") },
        { data: "1week", title: this.$t("message.jiaoyi.1zhou") },
        { data: "1mon", title: this.$t("message.jiaoyi.1yue") },
        { data: "1quarter", title: this.$t("message.jiaoyi.season") },
        { data: "1year", title: this.$t("message.jiaoyi.year") },
      ],
      kTimeArrMore: [
        { data: "1min", title: this.$t("message.jiaoyi.1fenzhong") },
        { data: "5min", title: this.$t("message.jiaoyi.5fenzhong") },
        { data: "15min", title: this.$t("message.jiaoyi.15fenzhong") },
        { data: "30min", title: this.$t("message.jiaoyi.30fenzhong") },
        { data: "60min", title: this.$t("message.jiaoyi.60fenzhong") },
        { data: "120min", title: this.$t("message.jiaoyi.120fenzhong") },
      ],
      kTimeArr: [],
      loading: false,
      timer: undefined,
      currencySymbol: "",
      timeOutTimer1: null,
      timeOutTimer2: null,
      key: "",
      searchSymbols:'',
      intervalTimer1: null,
      controller: new AbortController(),
    };
  },
  components: {
    SingleCurrencyInfo,
    OrderBookNew,
    DrawLine,
    klineBox,
    deepChart,
    klineDialog,
    PerpeOrderVue,
    PerpePositionVue,
    DeliveryOrderVue,
    DeliveryPositionVue,
    coinSearchCollect,
    deepChartFullScreen,
    FooterCom,
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
    ...mapState(useCurrencyStore, [
      "etfCurrency",
      "usStocksCurrency",
      "coinCurrency",
      "forexCurrency",
      "hkStocksCurrency",
      "cnStocksCurrency",
      "twStocksCurrency",
    ]),
    marginBalance: function () {
      return bigDecimal.add(this.walletMoney, this.profitLoss); // 钱包余额 + 未实现盈亏
    },
  },
  watch: {
    "$route.query.RouterName"(val) {
      this.initRouteComponent();
    },
    searchData(val) {
      this.isError = !!val;
    },
    etfCurrency(val, oldval) {
      if (this.pageType === "etf") {
        this.$nextTick(() => {
          this.updateCurrency(val, this.pageType);
        });
      }
    },
    usStocksCurrency(val, oldval) {
      if (this.pageType === "usStocks") {
        this.$nextTick(() => {
          this.updateCurrency(val, this.pageType);
        });
      }
    },
    twStocksCurrency(val, oldval) {
      if (this.pageType === "twStocks") {
        this.$nextTick(() => {
          this.updateCurrency(val, this.pageType);
        });
      }
    },
    hkStocksCurrency(val, oldval) {
      if (this.pageType === "hkStocks") {
        this.$nextTick(() => {
          this.updateCurrency(val, this.pageType);
        });
      }
    },
    cnStocksCurrency(val, oldval) {
      if (this.pageType === "cnStocks") {
        this.$nextTick(() => {
          this.updateCurrency(val, this.pageType);
        });
      }
    },
    coinCurrency(val, oldval) {
      if (this.pageType === "coin") {
        this.$nextTick(() => {
          this.updateCurrency(val, this.pageType);
        });
      }
    },
    forexCurrency(val, oldval) {
      if (this.pageType === "forex") {
        this.$nextTick(() => {
          this.updateCurrency(val, this.pageType);
        });
      }
    },
    KLInedata(val) {
      if (val.length > 0) {
        this.isShowLoading = false;
      }
    },
  },
  async created() {
    await this.getAllCurrency(); //获取当前页面类型的所有币种
    this.initWebSocket(); // 最新成交
  },
  mounted() {
    if (this.pageType === "forex" || this.pageType === "coin") {
      this.kTimeArr = this.kTimeArrCoin;
      this.kTimeIndex = "15min";
      this.kParamsTime = "15min";
    } else {
      this.kTimeArr = this.kTimeArrETF;
      this.kTimeIndex = "1day";
      this.kParamsTime = "1day";
    }

    if (this.pageType === "coin") {
      this.unit = "USDT";
    }
    // 外汇没有深度图
    if (this.pageType === "coin") {
      this.mapClass = [
        this.$t("message.jiaoyi.jibenban"),
        this.$t("message.jiaoyi.shendutu"),
      ];
    }

    this.initRouteComponent();
    if (this.existToken) {
      this.getItemOptionalList();
      this.timer = setInterval(() => {
        this.getAssetTotal();
      }, 2000);
    }

    this.lineTimer = setInterval(() => {
      this.getKlineData();
    }, 1000);

    bus.on("MarginRefresh", (rate, mm) => {
      this.marginRate = rate;
    });
  },

  unmounted() {
    clearInterval(this.currentTimer);
    clearInterval(this.lineTimer);
    clearInterval(this.timer);
    this.unbindTimerHandle();
    this.controller.abort();

    if (this.tradeData) {
      this.tradeData.close();
    }
    this.deepData.close();
    this.realData.close();
  },
  methods: {
    ...mapActions(useCurrencyStore, ["updateCurrency"]),
    addAndSubtr,
    async getAllCurrency() {
      let res = await Axios.getAllSymbolDetails({ type: this.paramsType });
      if (res.code == "0") {
        const data = res.data || this[`${this.pageType}Currency`];
        this.listData = data.map((item) => item?.symbol).join(","); //获取查询币种
        this.Hotcurrency = data.filter((vals) => vals.isTop == "1");
        if (this.$route.params.id === "undefined" && res.data.length) {
          this.currencySymbol = res.data[0].symbol;
          let routerUrl = this.$route.fullPath.replace(
            /undefined/,
            this.currencySymbol
          );
          this.$router.push(routerUrl);
        } else {
          this.currencySymbol = this.$route.params.id;
        }
        await this.getRealTimeData(this.listData); //http请求当前类型的所有币种实时数据
        this.updateCurrency(res.data, this.pageType);
      }
    },

    //获取资产总余额
    getAssetTotal() {
      Axios.currencyPaypal().then((res) => {
        const { total, money_wallet, money_contract, money_contract_profit } =
          res.data;
        this.totalAsset = total; //总资产
        this.walletMoney = bigDecimal.round(money_wallet, 2); //钱包余额
        this.profitLoss = bigDecimal.round(money_contract_profit, 2); //永续合约总未实现盈亏
        bus.emit("moneyContract", money_contract); //合约可用余额
      });
    },
    //切换币种以及路由跳转
    checkCurrency(item) {
      this.showSerachCollect();
      if (this.pageType !== "forex") {
        this.tradeData.close();
      }
      this.deepData.close();
      this.realData.close();
      this.initWebSocket(item.symbol);
      const routerName = this.$route.query.RouterName;
      const time = new Date().getTime();
      this.showName = item.name;
      this.$router.push(
        `/${this.pageType}/constract/${item.symbol}?timestamp=${time}&RouterName=${routerName}`
      );
    },

    showSerachCollect() {
      this.showSearchModal = !this.showSearchModal;
      // 打开所有币种弹窗
      if (this.showSearchModal) {
        clearInterval(this.currentTimer);
        this.currentTimer = setInterval(() => {
          this.getRealTimeData(this.listData);
        }, 1000);
      } else {
        clearInterval(this.currentTimer);
      }
    },
    //收藏
    collectFun(item) {
      Axios.addItemUserOptiona({
        symbol: item.symbol,
      }).then((res) => {
        ElMessage({
          message: this.$t("message.jiaoyi.shoucangchenggong"),
          type: "success",
        });
        this.getItemOptionalList();
      });
    },
    //取消收藏
    deleteCollectFun(item) {
      Axios.deleteItemUserOptiona({
        symbol: item.symbol,
      }).then((res) => {
        ElMessage({
          message: this.$t("message.jiaoyi.quxiaoshoucangchenggong"),
          type: "success",
        });
        this.getItemOptionalList();
      });
    },
    //币种排序
    CurrencySort(obj) {
      this.sortIndex = obj;
    },
    //币种搜索
    searchFun(val) {
      this.searchCoinList = this.allListData.filter((item) => {
        const upperName = item.name.toUpperCase();
        return upperName.indexOf(val.toUpperCase()) !== -1;
      });
    },
    // 远端币种搜索
    remoteSearchAllSymbol(query){
      if(query){
        // this.searchLoading = true
        const params = {
          type: this.paramsType,
          name:query
        }
        Axios.getAllSymbolDetails(params).then((res) => {
          if (res.code == "0") {

            this.searchSymbols = res.data.map((item) => item?.symbol).join(","); //获取查询币种
            this.remoteSearchRealTime(this.searchSymbols)

            clearInterval(this.intervalTimer1);
            this.intervalTimer1 = setInterval(() => {
              this.remoteSearchRealTime(this.searchSymbols);
            }, 2000);

          }
        })
      }else{
        clearInterval(this.intervalTimer1);
        this.searchCoinList = []
        this.searchSymbols = ''
      }
    },
    // 远端币种搜索实时数据更新
    remoteSearchRealTime(val){
      Axios.getRealtime({ symbol: val },this.controller).then((res) => {
        if (res.code == "0") {
          this.searchCoinList = res.data.map((item)=>{
            item.isCollect = false;
            return item
          })
          // 对已收藏的数据进行打tag
          this.searchCoinList.map((item1) => {
            this.collectList.map((item2) => {
              if (item1.symbol == item2.symbol) {
                item1.isCollect = true;
              }
            });
          });

          if (this.sortIndex == 1) {
            //字母正序
            this.searchCoinList = this.searchCoinList.sort(
              this.sortLetterList("symbol")
            );
          }
          if (this.sortIndex == 2) {
            this.searchCoinList = this.searchCoinList.sort(
              this.sortLetterTwoList("symbol")
            );
          }
          // 最新价格
          if (this.sortIndex == 3) {
            this.searchCoinList = this.searchCoinList.sort(
              this.orderListAsc("close")
            );
          }
          if (this.sortIndex == 4) {
            this.searchCoinList = this.searchCoinList.sort(
              this.orderListAsc("close", "ask")
            );
          }
          // 24小时涨跌
          if (this.sortIndex == 5) {
            this.searchCoinList = this.searchCoinList.sort(
              this.orderListAsc("change_ratio")
            );
          }
          if (this.sortIndex == 6) {
            this.searchCoinList = this.searchCoinList.sort(
              this.orderListAsc("change_ratio", "ask")
            );
          }
          // 成交量
          if (this.sortIndex == 7) {
            this.searchCoinList = this.searchCoinList.sort(
              this.orderListAsc("amount")
            );
          }
          if (this.sortIndex == 8) {
            this.searchCoinList = this.searchCoinList.sort(
              this.orderListAsc("amount", "ask")
            );
          }

        }
      })
    },
    //获取自选币种列表
    getItemOptionalList() {
      Axios.getItemOptionalStatus().then((res) => {
        this.collectList = res.data;
      });
    },
    //字母排序
    sortLetterList(propertyName) {
      return function sortList(object1, object2) {
        const value1 = object1[propertyName];
        const value2 = object2[propertyName];
        if (value2 < value1) {
          return 1;
        }
        if (value2 > value1) {
          return -1;
        }
        return 0;
      };
    },
    sortLetterTwoList(propertyName) {
      return function sortList(object1, object2) {
        const value1 = object1[propertyName];
        const value2 = object2[propertyName];
        if (value2 > value1) {
          return 1;
        }
        if (value2 < value1) {
          return -1;
        }
        return 0;
      };
    },
    //数字排序
    orderListAsc(filed, type = "asc") {
      return (a, b) => {
        if (type == "asc") return a[filed] > b[filed] ? 1 : -1;
        return a[filed] > b[filed] ? -1 : 1;
      };
    },
    // 以上为币种搜索函数
    fullScreen() {
      this.isFullscreen = !this.isFullscreen;
    },
    gotoPage(type) {
      switch (type) {
        case "reCharge":
          this.$router.push({ path: "/recharge" });
          break;
        case "exchange":
          this.$router.push({ path: "/exchange" });
          break;
        case "withdraw":
          this.$router.push({ path: "/withdraw" });
          break;
      }
    },
    //  显示规则
    changeRule() {
      this.showrule = !this.showrule;
    },
    getInfos(type) {
      this.show_cash = true;
    },

    handleChangeClickData(val) {
      this.clickData = val.price;
    },

    initRouteComponent() {
      switch (this.$route.query.RouterName) {
        case "sustainable":
          this.createOrderKey = "PerpeOrderVue";
          this.positionKey = "PerpePositionVue";
          break;
        case "delivery":
          this.createOrderKey = "DeliveryOrderVue";
          this.positionKey = "DeliveryPositionVue";
          break;
      }
    },

    technoIndex() {
      this.$refs.kline_dialog.close();
    },
    tabMap(index) {
      this.mapIndex = index;
      this.loading = true;
    },

    changeKTime(item) {
      this.loading = true;
      if (item.data != "minHour") {
        this.kParamsTime = item.data;
        this.klineIndex = "";
      } else {
        this.klineIndex = "minHour";
      }
      this.kTimeIndex = item.data;
    },

    //定时轮训的，获取K线数据
    getKlineData() {
      // 网速慢时获取币种接口未获取内容，不进行获取K线图数据
      if(this.$route.params.id == 'undefined'){
        return;
      }
      AxiosKline.getKline({
        symbol: this.$route.params.id,
        line: this.kParamsTime,
      }).then((res) => {
        this.timeOutTimer1 = setTimeout(() => {
          this.loading = false;
        }, 500);
        if (res.code == "0") {
          this.KLInedata = res.data.map((val) => {
            return {
              close: val.close,
              high: val.high,
              low: val.low,
              open: val.open,
              timestamp: val.ts,
              volume: val.volume,
            };
          });
        }
      });
    },
    initWebSocket(val) {
      const paramVal = this.currencySymbol || this.defaultSymbol;
      const param = !val ? paramVal : val;
      //  获取交易数据
      if (this.pageType !== "forex") {
        this.wsTradeData = []; // 清除之前币种/股票订单薄价格
        this.tradeData = new WhrWebSocket({
          path: `${ConfigURL.WS_URL}/2/${param}`,
          onmessage: this.getTradeMessage,
        });
        this.tradeData.init();
      }
      // 获取深度图
      this.deepData = new WhrWebSocket({
        path: `${ConfigURL.WS_URL}/3/${param}`,
        onmessage: this.getDeepMessage,
      });
      this.deepData.init();

      // 获取当前币种的实时价格和币种的开盘价格
      this.realData = new WhrWebSocket({
        path: `${ConfigURL.WS_URL}/1/${param}`,
        onmessage: this.getRealMessage,
      });
      this.realData.init();
    },
    getRealMessage({ data }) {
      const openTimeData = JSON.parse(data);
      if (openTimeData.code == "0") {
        const data = openTimeData.data[0];
        this.symbolMarketInfo = data;

        this.pageData = {
          ...data,
          name: this.showName,
        };
      }
    },

    // 获取交易数据,最近成交以及orderbook
    getTradeMessage({ data }) {
      var TradeData = JSON.parse(data);
      if (TradeData.code == "0") {
        // 用于最近成交
        this.wsTradeData = [...TradeData.data.data, ...this.wsTradeData];
        var filterData = this.wsTradeData.map((val) => {
          return val.amount;
        });
        this.bigIndex = Math.max(...filterData);
        if (this.wsTradeData.length > 300) {
          this.wsTradeData = this.wsTradeData.slice(0, 201);
        }

        // 要保持跟h5一致，需要用3
        // this.buyList = this.wsTradeData
        //   .filter((val) => {
        //     return val.direction == "buy";
        //   })
        // this.sellList = this.wsTradeData
        //   .filter((val) => {
        //     return val.direction == "sell";
        //   })

      }
    },
    // 用于深度图
    getDeepMessage({ data }) {
      const deepList = JSON.parse(data);
      this.timeOutTimer2 = setTimeout(() => {
        this.loading = false;
      }, 500);
      if (deepList.code == "0") {
        this.deepBuy = deepList.data?.bids;
        this.deepSell = deepList.data?.asks;
      }
    },
    // 用于币种的切换弹窗，获取所有币种的行情
    async getRealTimeData(val) {
     const res = await Axios.getRealtime({ symbol: val })
        if (res.code == "0") {
          this.footerLeftList = res.data.slice(0, 6);
          this.allListData = res.data; //所有的交易对
          if (this.allListData.length) {
            this.showName = this.allListData.filter(
              (val) => val.symbol == this.$route.params.id
            )[0]?.name;
          }
          // 对已收藏的数据进行打tag
          for (let i = 0; i < this.allListData.length; i++) {
            this.allListData[i].isCollect = false;
          }
          this.allListData.map((item1) => {
            this.collectList.map((item2) => {
              if (item1.symbol == item2.symbol) {
                item1.isCollect = true;
              }
            });
          });

          if (this.sortIndex == 1) {
            //字母正序
            this.allListData = this.allListData.sort(
              this.sortLetterList("symbol")
            );
          }
          if (this.sortIndex == 2) {
            this.allListData = this.allListData.sort(
              this.sortLetterTwoList("symbol")
            );
          }
          // 最新价格
          if (this.sortIndex == 3) {
            this.allListData = this.allListData.sort(
              this.orderListAsc("close")
            );
          }
          if (this.sortIndex == 4) {
            this.allListData = this.allListData.sort(
              this.orderListAsc("close", "ask")
            );
          }
          // 24小时涨跌
          if (this.sortIndex == 5) {
            this.allListData = this.allListData.sort(
              this.orderListAsc("change_ratio")
            );
          }
          if (this.sortIndex == 6) {
            this.allListData = this.allListData.sort(
              this.orderListAsc("change_ratio", "ask")
            );
          }
          // 成交量
          if (this.sortIndex == 7) {
            this.allListData = this.allListData.sort(
              this.orderListAsc("amount")
            );
          }
          if (this.sortIndex == 8) {
            this.allListData = this.allListData.sort(
              this.orderListAsc("amount", "ask")
            );
          }
        }
    },
    // 清除定时器
    unbindTimerHandle() {
      if (this.timeOutTimer1) {
        clearTimeout(this.timeOutTimer1);
        this.timeOutTimer1 = null;
      }

      if (this.timeOutTimer2) {
        clearTimeout(this.timeOutTimer2);
        this.timeOutTimer2 = null;
      }

      if(this.intervalTimer1){
        clearInterval(this.intervalTimer1);
        this.intervalTimer1 = null;
      }
    },
    deepDataSort(target){
      if(target && target.length){
        const arr = [...target]
        return arr.sort((a, b) => b.price - a.price)
      }
    }
  },
};
</script>
<style lang="scss" scoped>
.position-wrapper {
  :deep(.position-block) {
    .el-tabs__nav-scroll {
      padding: 24px;
      padding-bottom: 0;
    }
    .el-tabs__content {
      padding-left: 24px;
    }
    .el-tabs__nav {
      z-index: 0 !important;
    }
    .el-tabs__item:hover {
      color: #fff !important;
    }
  }
}

:deep(.el-loading-spinner) i {
  color: #fff !important;
  font-size: 40px;
}
</style>
<style lang="scss">
@import url("@/assets/css/coin/trade.css");
.home-container {
  background: #171a1e;
  width: 100vw;
  border: 1px solid #24272c;
  border-bottom: none;
  color: #fafafa;
  padding-top: 1px;

  .new-trade-box {
    width: 100vw;
    min-height: calc(100vh - 96px);
  }
  .trade-box-left {
    width: calc(100% - 300px);
  }
  .trade-box-right {
    width: 300px;
  }
}
.kline-wrap {
  z-index: 0;
}

.wrap-full {
  z-index: 2;
}

.k-toolbar-wrap-full {
  position: fixed;
  top: 78px;
  left: 0;
  width: 100%;
  height: 30px;
  background: #171a1e;
  z-index: 300000;
}

.trade-box-bottom {
  border-top: 1px solid #24272c;
  width: 100%;
  background: #171a1e;
  margin-bottom: 30px;
  height: 600px;
  display: flex;
}

.full-screen {
  cursor: pointer;
}

.new-order-price {
  font-size: 18px;
  font-weight: 600;

  padding-top: 16px;
  color: #fafafa;
  border-top: 1px solid #24272c;
  margin-top: 5px;
}

.position-wrapper {
  display: flex;
  flex: 1;
  width: calc(100% - 300px);
}

.search-dialog {
  transform: translate(0px, 3px);
  width: 630px;
  position: absolute;
  z-index: 10000;
  border-top: 1px solid #24272c;
  border-right: 1px solid #24272c;

  top: 64px;
  background: #171a1e;
}

/*  */
.main-box_header {
  height: 35px;
  line-height: 35px;
  position: relative;
  padding-left: 25px;
}

.main-box_header div {
  position: absolute;
  right: 20px;
  top: 5px;
  color: #519aff;
}

.main-box_header span {
  color: #519aff;
  padding-left: 20px;
}

.margin-block {
  border-top: 1px solid #24272c;
}

.trade-box-top {
  display: flex;
  justify-content: center;
  width: 100vw;
}
.kTimeDivider {
  width: 1px;
  height: 30px;
  margin-right: 20px;
  background: #24272c;
}

.top-left {
  background: #171a1e;
  display: flex;
  flex: 1;
  width: calc(100% - 300px);
  border-right: 1px solid #24272c;
}

.top-left-wrapper {
  width: 100%;
}

.top-left-k {
  z-index: 99;
  /* height: 660px; */
  width: 100%;
  .comb-trade-kline-box {
    height: auto;
    .chart-kline {
      position: static !important;
    }
  }
}

.top-middle,
.top-right {
  width: 300px;
  /* border-top: 1px solid #9090; */
}

.top-middle {
  background: #171a1e;
}

.top-right {
  border-left: 1px solid #24272c;
  /* border-bottom: 1px solid #24272c; */
}

.footers {
  text-align: center;
  display: flex;
}

.footers button {
  width: 49%;
}

.overbottom {
  position: fixed;
  width: 100%;
  bottom: 0px;
  left: 0px;
  display: flex;
  background: #171a1e;
  font-size: 12px;
  /* height: 15px; */
  height: 28px;
  line-height: 28px;
  color: #909090;
}

.overright {
  position: absolute;
  right: 60px;
  width: 366px;
}

.overright span {
  display: inline-block;
  width: 120px;
  text-align: center;
}

.overbottom i {
  font-size: 10px;
}

.overbottom img {
  width: 28px;
  height: 28px;
  vertical-align: middle;
}

.overbottom span {
  display: inline-block;
}

.overbottom .item {
  width: 200px;
  text-align: center;
  border-right: 1px solid #909090;
}

.overbottom .item span {
  padding-right: 10px;
}

.overbottom span.overtitle {
  width: 180px;
  text-align: center;
  border-right: 1px solid #909090;
}
.status-info {
  padding: 4px 8px;
  border-radius: 4px;
  margin-right: 4px;
  background: #171a1e;
  color: #fff;
}

.twoj {
  position: relative;
  display: inline-block;
  width: 50px;
  text-align: center;
}

.twoj i {
  font-size: 8px;
  position: absolute;
}

.twoj i:nth-child(1) {
  top: 0px;
}

.twoj i:nth-child(2) {
  top: 6px;
}

.setcash .boxpin {
  color: #fff;
  text-align: center;
}

.setcash .boxpin i {
  font-size: 70px;
  color: #519aff;
  font-weight: bold;
}

.setcash .boxpin p {
  height: 50px;
  line-height: 50px;
  font-size: 18px;
}

.linepin {
  height: 35px;
  line-height: 35px;
  font-size: 15px;
  color: #fff;
  letter-spacing: 1px;
}

.linepin span {
  color: #62c885;
}

.ticker-item,
.ladder-wrap {
  position: relative;
}

.ticker-itemBox {
  position: absolute;
  top: 40px;
  z-index: 9999;
  background: #1f2328;
  color: #707a8a;
  padding: 20px;
  border: 1px solid #909090;
  border-radius: 5px;
}

.ladder-wrap .ticker-itemBox {
  top: 20px;
  left: -20px;
}

.ladder-wrap .ticker-itemBox div:hover {
  color: #409eff;
}

.ticker-itemBox div {
  height: 30px;
}

.ticker-itemBox .el-checkbox {
  color: #707a8a;
}

.el-button--default {
  background: #484d56 !important;
  border: 1px solid #484d56;
  color: #fff;
}

.el-button--primary {
  background-color: #1d91ff;
  border-color: #1d91ff;
  color: #fff !important;
}

.asset-block {
  width: 300px;
  border: 1px solid #24272c;
  border-top: none;
  border-right: none;
  height: 100%;
  box-sizing: border-box;
}
.el-button + .el-button {
  margin-left: 0 !important;
}
.asset-block .assets-btns {
  display: grid;
  grid-template-columns: auto auto auto;
  grid-column-gap: 12px;
}

.cashbi {
  padding: 5px 12px;
}

.cashbi .cashHead {
  font-size: 16px;
  height: 36px;
  line-height: 36px;
  padding-top: 10px;
  margin-bottom: 15px;
}

.cashbi .cashbtn {
  width: 100%;
  height: 40px;
  line-height: 40px;
  background: #2c3138;
  text-align: center;
  border-radius: 5px;
  margin: 10px 0px;
  color: #707a8a;
}

.cashbi .cashbilist {
  color: #707a8a;
  padding-top: 10px;
}

.cashbi .cashbilist p {
  height: 35px;
  line-height: 35px;
  position: relative;
}

.cashbi .cashbilist p span {
  position: absolute;
  right: 0px;
}

.cashbi ul li {
  display: flex;
  color: #707a8a;
  height: 36px;
  line-height: 36px;
  justify-content: space-between;
}

.cashbi button {
  background: #2c3138;
  border: none;
  color: #707a8a;
  font-size: 12px;
}

.allgoods {
  box-sizing: border-box;
  line-height: 36px;
  position: relative;
  border-bottom: 1px solid #24272c;
}

.allgoods div {
  display: inline-block;
  vertical-align: top;
  margin-left: 20px;
}

.allgoods div .name {
  padding-right: 10px;
}

.allgoods span.sethave {
  position: absolute;
  right: 0px;
  font-size: 18px;
  color: #707a8a;
}

.watch-list-box .slide-tab-box.grade—1 .type-item.checked {
  border-bottom: 2px solid #519aff;
  color: #519aff;
}

.rgl-drag-zone {
  position: relative;
}

.rgl-drag-zone .ticker-itemBox {
  position: absolute;
  right: 0px;
  top: 28px;
}

.place-order-xl-box {
  z-index: 0;
}

.trade-panel-box {
  width: 100%;
  /* margin: 0 auto; */
}

.order-book-item span.active {
  color: #0db27c !important;
}

.order-book-item span.actives {
  color: #db4355 !important;
}

.fullscreen-box {
  position: fixed !important;
  left: 0;
  top: 0;
  width: 100% !important;
  height: 100% !important;
}

.toolbar-bg {
  height: 36px;
}

.constract-page{
  padding-bottom: 25px;
}
</style>
