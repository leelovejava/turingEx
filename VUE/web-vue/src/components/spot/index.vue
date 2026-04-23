<!-- 现货入口 -->
<template>
  <div class="spot-page">
    <div class="home-container">
      <!-- 上部分 -->
      <div class="trade-box">
        <div class="trade-box-wrapper">
          <!--左边 -->
          <div style="flex: 1">
            <!-- 头部 币种信息 -->
            <div
              :class="[isFullscreen ? 'trade-header-box-full' : '']"
              class="trade-header-box"
            >
              <div class="ticker-xl-box">
                <div class="watch-drop-box">
                  <span class="ticker-title" style="width: 259px">{{
                    pageData?.name
                  }}</span>
                </div>
                <div class="line"></div>
                <TopInfo :page-data="pageData" :page-type="pageType"></TopInfo>
              </div>
            </div>
            <!--下部分 订单簿+ 交易区 -->
            <div class="trade-panel-box">
              <div
                class="react-grid-layout layout-xl-box"
                style="display: flex"
              >
                <!--左边订单簿-->
                <OrderBook
                  style="width: 280px"
                  :page-data="pageData"
                  :changeClickData="handleChangeClickData"
                  :filterDown="deepDataSort(deepSell)"
                  :filterTop="deepDataSort(deepBuy)"
                  :bigIndex="bigIndex"
                  :unit="unit"
                ></OrderBook>
                <!--中间-->
                <div style="flex: 1">
                  <!--上部分 图表+成分股+简况F10 -->
                  <div class="chart-wrapper" v-if="pageType === 'etf'">
                    <div
                      :class="`chart-item ${
                        chartIndex == 1 && 'chart-item-active'
                      }`"
                      @click="handleChartType(1)"
                    >
                      {{ $t("message.user.tubiao") }}
                    </div>
                    <div
                      :class="`chart-item ${
                        chartIndex == 2 && 'chart-item-active'
                      }`"
                      @click="handleChartType(2)"
                    >
                      {{ $t("message.user.chengfengu") }}
                    </div>
                    <div
                      :class="`chart-item ${
                        chartIndex == 3 && 'chart-item-active'
                      }`"
                      @click="handleChartType(3)"
                    >
                      {{ $t("message.user.jiankuangF10") }}
                    </div>
                  </div>

                  <!--中间 图表-->
                  <share-info
                    v-if="chartIndex == 2 && pageType === 'etf'"
                    @CurrencySort="CurrencySort"
                    style="height: 500px"
                  ></share-info>
                  <base-info
                    v-if="chartIndex == 3 && pageType === 'etf'"
                  ></base-info>

                  <div
                    v-if="chartIndex == 1"
                    class="kline-box comb-trade-kline-box"
                    style="z-index: 99; width: 100%"
                  >
                    <div class="kline-table">
                      <div class="chart-tr">
                        <div class="chart-td" id="parent_trade_k1">
                          <div class="chart-box">
                            <div class="chart-kline" id="trade_k1">
                              <div class="kline-wrap">
                                <!-- toolbar -->
                                <div
                                  :class="[
                                    isFullscreen
                                      ? 'k-toolbar-wrap-full'
                                      : 'k-toolbar-wrap ',
                                  ]"
                                >
                                  <div class="k-toolbar dark">
                                    <div class="k-toolbar-btn">
                                      <!-- 时间 -->
                                      <div class="k-toolbar-left">
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
                                      <!-- 切换 -->
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

                                        <div class="toggle">
                                          <span
                                            style="margin-right: 5px"
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

                                <div class="kline-body-wrap" id="okline-wrap">
                                  <div
                                    id="kline-Box"
                                    v-loading="loading"
                                    element-loading-spinner="el-icon-loading"
                                    element-loading-background="#171a1e"
                                    :class="[
                                      'kline-closest-wrapper',
                                      isFullscreen ? 'fullscreen-box' : '',
                                    ]"
                                  >
                                    <kline-box
                                      v-if="mapIndex == 0"
                                      :isSpotGoods="true"
                                      :isFullscreen="isFullscreen"
                                      :data="KLInedata"
                                      :klineIndex="klineIndex"
                                      :kTimeIndex="kTimeIndex"
                                    />

                                    <deep-chart
                                      :isSpotGoods="true"
                                      v-else-if="mapIndex == 1 && !isFullscreen"
                                      :deepBuy="deepBuy"
                                      :deepSell="deepSell"
                                    />
                                    <deep-chart-full-screen
                                      v-else-if="mapIndex == 1 && isFullscreen"
                                      :deepBuy="deepBuy"
                                      :deepSell="deepSell"
                                    />
                                    <div
                                      v-if="isShowLoading"
                                      class="kline-loading"
                                      style="
                                        width: 100%;
                                        height: 100%;
                                        position: absolute;
                                        background-color: #000000;
                                        top: 0%;
                                        left: 16px;
                                        z-index: 99;
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

                  <!--下部分 下单区-->
                  <div
                    v-if="!isFullscreen"
                    class="place-order-container-common place-order-xl-box"
                    :class="chartIndex == 1 ? 'mt-200' : ''"
                  >
                    <currencyTrade
                      @refresh="refreshApi"
                      :pageData="pageData"
                      :clickData="clickData"
                      :unit="unit"
                      :pageType="pageType"
                      :paramsType="paramsType"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!--右边-->
          <div style="width: 280px; border-left: 1px solid #24272c">
            <!-- 查询+最新成交 -->
            <makeADeal
              :style="{ display: isFullscreen ? 'none' : 'block' }"
              @checkCurrency="checkCurrency"
              @filterFun="filterFun"
              @CurrencySort="CurrencySort"
              @collectFun="collectFun"
              @deleteCollectFun="deleteCollectFun"
              :listData="left_List_Data"
              :AllListData="allListData"
              @searchFun="searchFun"
              :dealList="rightData"
              :unit="unit"
              @getSelectList="getSelectList"
            />
          </div>
        </div>
      </div>
      <!--下部分-持仓区 -->
      <spot-position
        :unit="unit"
        :paramsType="paramsType"
        :pageType="pageType"
        ref="refresh"
        :style="{ display: isFullscreen ? 'none' : 'block' }"
      />
    </div>
    <!-- 底部 -->
    <!-- <quotes-state :style="{ display: isFullscreen ? 'none' : 'block' }" /> -->
    <kline-dialog
      ref="kline_dialog"
      :style="{ display: isFullscreen ? 'none' : 'block' }"
    />
  </div>
</template>
<script>
import Axios from "@/api/currency.js";
import AxiosKline from "@/api/kline.js";
import { WhrWebSocket } from "@/utils/WhrWebSocket";
import klineDialog from "@comTrade/klineDialog.vue";
import klineBox from "@comTrade/kline.vue";
import deepChart from "@comTrade/deepChart.vue";
import deepChartFullScreen from "@comTrade/deepChartFullScreen.vue";
import currencyTrade from "@comSpot/createOrder.vue"; //当前币种下单
import makeADeal from "@comSpot/MakeADeal/index.vue";
import SpotPosition from "@comSpot/SpotPosition/index.vue";
import quotesState from "@comSpot/QuotesState/index.vue";
import TopInfo from "@comSpot/TopInfo.vue";
import OrderBook from "@comSpot/OrderBook.vue";
import ShareInfo from "@comSpot/ShareInfo.vue";
import BaseInfo from "@comSpot/BaseInfo.vue";
import { mapState, mapActions, mapStores } from "pinia";
import { useUserStore } from "@/store/user";
import { useCurrencyStore } from "@/store/currency";
import { ElMessage } from "element-plus";
import ConfigURL from "@/config/index";

export default {
  name: "spot",
  errorCaptured(err, vm, info) {
    console.log(`错误捕获: ${err.toString()}\ninfo: ${info}`);
    return true; //当捕获一个来自子孙组件的错误时被调用,此钩子可以返回 false 以阻止该错误继续向上传播
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
      default: "GlobalETF500", //默认的symbol
    },
  },
  data() {
    return {
      times: ["分时"],
      ConfigURL,
      host_url: "",
      isError: false,
      searcIndexhData: [],
      searchData: "",
      deep: undefined,
      deepBuy: [],
      deepSell: [],
      mapClass: [this.$t("message.jiaoyi.jibenban")],
      mapIndex: 0,
      listData: [], //币种
      left_List_Data: [],
      currentTimer: undefined,
      lineTimer: undefined,
      pageTimer: undefined,
      pageData: {}, //页面数据
      Hotcurrency: [],
      tabArr: [
        this.$t("message.jiaoyi.quanbu"),
        this.$t("message.jiaoyi.remenbizhong"),
        this.$t("message.jiaoyi.zixuan"),
      ],
      Trade: undefined, //最新成交，websocket实例
      openTime: undefined, //ws实例
      symbolMarketInfo: {},
      rightData: [], //成交数据
      filterTop: [],
      filterDown: [],
      bigIndex: undefined,
      KLInedata: undefined,
      klineIndex: "", //面积图还是蜡烛图
      kParamsTime: "", // K线查询时间
      kTimeIndex: "", // 选中那个时间，etf默认1D，数字货币默认15min
      isShowLoading: true,
      unit: "USD",

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
      allListData: [],
      collectList: [],
      sortIndex: 0,
      isFullscreen: false, //是否全屏
      loading: true,
      chartIndex: 1,
      isFilter: false,
      filterValue: "",
      searchValue: "",
      timeOutTimer1: null,
      timeOutTimer2: null,
      currencySymbol:''
    };
  },
  components: {
    klineBox,
    deepChart,
    klineDialog,
    currencyTrade,
    BaseInfo,
    ShareInfo,
    makeADeal,
    SpotPosition,
    quotesState,
    deepChartFullScreen,
    TopInfo,
    OrderBook,
  },
  computed: {
    ...mapState(useCurrencyStore, [
      "etfCurrency",
      "usStocksCurrency",
      "coinCurrency",
      "forexCurrency",
      "hkStocksCurrency",
      "cnStocksCurrency",
      "twStocksCurrency",
    ]),
    ...mapState(useUserStore, ["existToken"]),
  },

  // 监听路由名称的变化
  watch: {
    searchData(val) {
      if (val) {
        this.isError = true;
      } else {
        this.isError = false;
      }
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
    hkStocksCurrency(val, oldval) {
      if (this.pageType === "hkStocks") {
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
    console.log('meigu');
    this.host_url = window.location.hostname;
    await this.getAllCurrency(); //获取市场的币种
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

    // 外汇没有深度图
    if (this.pageType === "coin") {
      this.unit = "USDT";
      this.mapClass = [
        this.$t("message.jiaoyi.jibenban"),
        this.$t("message.jiaoyi.shendutu"),
      ];
    }

    if (this.existToken) {
      this.getItemOptionalList();
    }
    clearInterval(this.lineTimer);
    this.getKlineData();
    this.lineTimer = setInterval(() => {
      this.getKlineData();
    }, 1000);
  },
  unmounted() {
    clearInterval(this.currentTimer);
    clearInterval(this.lineTimer);
    this.Trade.close();
    this.deep.close();
    this.openTime.close();

    if(this.timeOutTimer1){
      clearTimeout(this.timeOutTimer1)
      this.timeOutTimer1 = null
    }
    if(this.timeOutTimer2){
      clearTimeout(this.timeOutTimer2)
      this.timeOutTimer2 = null
    }
  },
  methods: {
    ...mapActions(useCurrencyStore, ["updateCurrency"]),
    async getAllCurrency() {
      let res = await Axios.getAllSymbolDetails({ type: this.paramsType });
      if (res.code == "0") {
        await this.getSymbolData("", res.data); //http请求当前类型的所有币种
        this.updateCurrency(res.data, this.pageType);
      }
    },

    // 切换图标
    handleChartType(val) {
      this.chartIndex = val;
    },
    //点击usft过滤
    filterFun(val) {
      this.isFilter = true;
      this.filterValue = val;
    },
    //刷新api
    refreshApi() {
      this.$refs.refresh.refreshApi();
    },
    //是否获取收藏列表
    getSelectList(val) {
      this.isCollectShow = val;
    },
    //切换币种
    checkCurrency(item) {
      this.clickData = item.close;
      this.tabItemLeft(item);
    },
    //收藏
    collectFun(item) {
      let obj = {
        symbol: item.symbol,
      };
      Axios.addItemUserOptiona(obj).then((res) => {
        if (res.code == "0") {
          ElMessage({
            message: this.$t("message.jiaoyi.shoucangchenggong"),
            type: "success",
          });
          item.isCollect = true;
          this.getItemOptionalList();
        }
      });
    },
    //取消收藏
    deleteCollectFun(item) {
      let obj = {
        symbol: item.symbol,
      };
      Axios.deleteItemUserOptiona(obj).then((res) => {
        if (res.code == "0") {
          ElMessage({
            message: this.$t("message.jiaoyi.quxiaoshoucangchenggong"),
            type: "success",
          });
          item.isCollect = false;
          this.getItemOptionalList();
        }
      });
    },
    //币种排序
    CurrencySort(obj) {
      this.sortIndex = obj;
    },
    //全屏
    fullScreen() {
      this.isFullscreen = !this.isFullscreen;
    },
    //币种搜索
    searchFun(val) {
      this.searchValue = val;
      this.left_List_Data = this.allListDataTwo.filter((item) => {
        return item.name.indexOf(this.searchValue.toUpperCase()) !== -1;
      });
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
    addTime(item) {
      if (!this.times.includes(item)) {
        this.times.push(item);
      } else {
        const index = this.times.findIndex((el) => el === item);
        this.times.splice(index, 1);
      }
    },
    async getSymbolData(val, curPageTypeCurrency) {
      const data = curPageTypeCurrency || this[`${this.pageType}Currency`];
      this.listData = data.map((val) => val?.symbol).join(","); //获取查询币种
      this.Hotcurrency = data.filter((vals) => vals.isTop == "1");
      clearInterval(this.currentTimer);
      await this.getRealTimeData(this.listData, val);

      this.currentTimer = setInterval( () => {
         this.getRealTimeData(this.listData, val);
      }, 1000); //轮训pageData会变化
    },
    //  获取币种的实时数据
    async getRealTimeData(val, search) {
        const res = await Axios.getRealtime({ symbol: val });
        if (res.code == "0") {
          var data = res.data;
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
          console.log(1);
          const curCoin = this.currencySymbol || this.defaultSymbol;
          var filtersVal = data.filter((val) => val.symbol == curCoin);
          this.pageData = filtersVal[0]; //页面展示的数据

          this.allListData = data; //所有的交易对
          this.allListDataTwo = data; //热门搜索
          for (let i = 0; i < this.allListData.length; i++) {
            this.allListData[i].isCollect = false;
          }
          this.allListData.map((item1) => {
            // 收藏
            this.collectList.map((item2) => {
              if (item1.symbol == item2.symbol) {
                item1.isCollect = true;
              }
            });
          });
          for (let i = 0; i < this.allListDataTwo.length; i++) {
            this.allListData[i].isCollect = false;
          }
          this.allListDataTwo.map((item1) => {
            // 历史搜索
            this.collectList.map((item2) => {
              if (item1.symbol == item2.symbol) {
                item1.isCollect = true;
              }
            });
          });
          //是否过滤
          if (this.isFilter) {
            this.allListData = this.allListData.filter((item) => {
              return item.name.indexOf(this.filterValue.toUpperCase()) !== -1;
            });
          }

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
          if (this.isCollectShow) {
            //获取收藏列表
            this.allListData = this.allListData.filter((item) => {
              return item.isCollect;
            });
          }
        }
    },
    search() {
      if (this.searchData) {
        if (this.listData.indexOf(this.searchData.toLowerCase()) == -1) {
          clearInterval(this.currentTimer);
          this.left_List_Data.length = 0;
        } else {
          this.getSymbolData(this.searchData.toLowerCase());
        }
      }
    },
    technoIndex() {
      this.$refs.kline_dialog.close();
    },
    tabMap(index) {
      this.mapIndex = index;
      this.loading = true;
    },
    // 币种信息头部
    tabItemLeft(item) {
      this.Trade.close();
      this.deep.close();
      this.openTime.close();
      this.initWebSocket(item.symbol);
      this.$router.push(`/${this.pageType}/spot/${item.symbol}`); //刷新页面
    },
    changeKTime(item) {
      this.loading = true;
      if (item.data != "minHour") {
        this.kParamsTime = item.data;
        this.klineIndex = "";
      } else {
        // 分时图，klineIndex为面积，参数kParamsTime不变保留上次的参数，this.kTimeIndex且为分时
        this.klineIndex = "minHour";
      }
      this.kTimeIndex = item.data; //
    },
    // 获取K线数据
    getKlineData() {
      let symbol = this.$route.params.id || this.defaultSymbol;

      if(symbol =='undefined') return

      AxiosKline.getKline({
        symbol: symbol,
        line: this.kParamsTime,
      }).then((res) => {
        if (this.mapIndex == 0) {
         this.timeOutTimer1 = setTimeout(() => {
            this.loading = false;
          }, 500);
        }
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

    // 监听买卖数据=orderbook
    getTradeMessage({ data }) {
      if (data) {
        var TradeData = JSON.parse(data);
        if (TradeData.code == "0") {
          this.rightData = [...TradeData.data.data,...this.rightData];
          var filterData = this.rightData.map((val) => {
            return val.amount;
          });
          this.bigIndex = Math.max(...filterData);
          // this.filterTop = this.rightData.filter((val) => {
          //   return val.direction == "buy";
          // });
          // this.filterDown = this.rightData.filter((val) => {
          //   return val.direction == "sell";
          // });
          // this.filterTop = this.filterTop.slice(0, 20);
          // this.filterDown = this.filterDown.slice(0, 20);
        }
      }
    },
    // 监听买卖价格 数据
    getDeepMessage({ data }) {
      this.timeOutTimer2 = setTimeout(() => {
        this.loading = false;
      }, 500);
      var deepData = JSON.parse(data);
      if (deepData.code == "0") {
        this.deepBuy = deepData?.data?.bids;
        this.deepSell = deepData?.data?.asks;
      }
    },
    mathDatarandomFun(val) {
      var a = Number(val);
      var b = Math.round(Math.random() * 10) / 100;
      var data = math.format(a + b, 6);
      return data;
    },
    initWebSocket(val) {
      console.log(2);
      const paramVal =  this.currencySymbol || this.defaultSymbol;
      const param = !val ? paramVal : val;
      console.log(this.currencySymbol,'val',param);
      this.Trade = new WhrWebSocket({
        path: `${ConfigURL.WS_URL}/2/${param}`,
        onmessage: this.getTradeMessage,
      });
      this.deep = new WhrWebSocket({
        path: `${ConfigURL.WS_URL}/3/${param}`,
        onmessage: this.getDeepMessage,
      });
      this.Trade.init();
      this.deep.init();
      this.openTime = new WhrWebSocket({
        path: `${ConfigURL.WS_URL}/1/${param}`,
        onmessage: this.getOpenTmeMessage,
      });
      this.openTime.init();
    },
    getOpenTmeMessage({ data }) {
      var openTimeData = JSON.parse(data);
      if (openTimeData.code == "0") {
        this.symbolMarketInfo = openTimeData.data[0];
      }
    },

    handleChangeClickData(val) {
      this.clickData = val;
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

<style>
@import url("@/assets/css/commonTrade/quotes.css");

.home-container {
  background: #171a1e;
  width: 100%;
  margin: 0 auto;
  border: 1px solid #24272c;
  border-bottom: none;
}

.trade-header-box {
  background: #171a1e !important;
  border-bottom: 1px solid #24272c !important;
}

.trade-header-box-full {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 65px;
  background: #171a1e;
  z-index: 3000000;
  padding: 10px 0;
}

.k-toolbar-wrap-full {
  position: fixed;
  top: 85px;
  left: 0;
  width: 100%;
  height: 32px;
  background: #171a1e;
  z-index: 30;
}

.line {
  width: 1px;
  height: 42px;
  margin: 0 20px;
  background: #24272c;
}

.fs-16 {
  font-size: 16px;
}

.drop-down-time {
  box-sizing: border-box;
  position: absolute;
  width: 300px;
  height: 220px;
  top: 33px;
  background: #1f2328;
  border: 1px solid #30353c;
  border-radius: 5px;
  z-index: 10;
  padding: 15px 12px;
  font-size: 12px;
  display: block;
}
.kTimeDivider {
  width: 1px;
  height: 30px;
  margin-right: 20px;
  background: #24272c;
}

.select-cycle {
  width: 100%;
  display: flex;
  justify-content: space-between;
  color: #929aa5;
  margin-bottom: 5px;
}

.select-cycle span {
  cursor: pointer;
}

.is-cycle {
  color: #1d91ff !important;
}

.is-times {
  color: #1d91ff !important;
  background: #243046 !important;
}

.times {
  width: 100%;
  justify-content: space-between;
  flex-wrap: wrap;
}

.times span {
  cursor: pointer;
  margin-top: 10px;
  width: 60px;
  height: 24px;
  text-align: center;
  line-height: 24px;
  background: #363b41;
  border-radius: 2px;
  color: #ffffff;
}
.status-info {
  padding: 4px 8px;
  border-radius: 4px;
  margin-right: 4px;
  background: #171a1e;
  color: #fff;
}

.value-select {
  position: absolute;
  top: 20px;
  z-index: 100;
}

.value-select p {
  width: 64px;
  height: 43px;
  background: #1f2328;
  border-radius: 3px;
  text-align: center;
  line-height: 43px;
}

.zhibiao {
  position: absolute;
  left: -60px;
  top: 30px;
  width: 380px;
  background: #1f2328;
  border-radius: 5px;
  z-index: 1000;
  flex-wrap: wrap;
}

.zhi-hd {
  padding: 26px 20px;
  font-size: 20px;
  color: #b3b5bd;
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.zhi-search {
  width: 100%;
  padding: 10px 0;
  border-top: 1px solid rgb(54, 56, 65);
  border-bottom: 1px solid rgb(54, 56, 65);
}

:deep(.zhi-search) .el-input__inner {
  background: #1f2328;
  border-radius: 0;
  border: none;
}

.zhi-main {
  padding: 26px 20px;
  flex-wrap: wrap;
}

.jb-title {
  font-size: 12px !important;
  color: #797b85 !important;
}

.zhi-main p {
  width: 100%;
  color: #b3b5bd;
  font-size: 14px;
  margin: 10px 0;
}

.mt-200 {
  margin-top: 200px;
}

.react-grid-layout {
  height: 1055px;
}

.fullscreen-box {
  position: fixed !important;
  left: 0;
  top: 0;
  width: 100% !important;
  height: 100% !important;
}

:deep(.el-loading-spinner) i {
  color: #fff !important;
  font-size: 40px;
}

.chart-wrapper {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #24272c;
}

.chart-item {
  cursor: pointer;
  background: #363b41;
  border-radius: 2px;
  color: #ccc;
  padding: 6px 18px;
  margin-left: 12px;
}

.chart-item-active {
  color: #1d91ff;
}
</style>
