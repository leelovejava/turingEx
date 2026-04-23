/* eslint-disable vue/valid-v-bind */
<template>
  <div class="css-ph4aey">
    <div class="css-tq0shg">
      <main class="css-1wr4jig">
        <div class="css-1pysja1">
          <div class="css-6nqu2s">
            <!-- 标题 -->
            <div class="css-b22026">
              <div class="css-1vs5d4i">
                <div class="css-o32gok">
                  {{ $t("message.hangqing.hangqing") }}
                </div>
                <!-- 右上角 -->
                <div class="css-ykl0r1">
                  <div id="market_sector_overview" class="css-8puzml">
                    <div class="css-search-1">
                      <div class="css-search-2">
                        <div class="css-search-3">
                          <div class="bn-input-prefix css-search-4">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 24 24"
                              fill="none"
                              class="css-search-5"
                            >
                              <path
                                fill-rule="evenodd"
                                clip-rule="evenodd"
                                d="M11 6a5 5 0 110 10 5 5 0 010-10zm0-3a8 8 0 017.021 11.838l3.07 3.07-1.59 1.591-1.591 1.591-3.07-3.07A8 8 0 1111 3z"
                                fill="currentColor"
                              ></path>
                            </svg>
                          </div>
                          <el-select
                            class="css-search-6 search-select"
                            v-model="searchValue"
                            filterable
                            remote
                            reserve-keyword
                            :placeholder="$t('message.hangqing.sousuo')"
                            :remote-method="remoteSearchMethod"
                            :loading="searchLoading"
                          >
                            <el-option
                              v-for="(item,index) in searchOptions"
                              :key="item.symbol"
                              :label="item.label"
                              :value="item.symbol"
                              @click.stop="goDetail(item.symbol, item)"
                            />
                          </el-select>
                          <!-- <input
                            id="financial_main_search"
                            v-model="searchValue"
                            @input="onInputSearch"
                            :placeholder="$t('message.hangqing.sousuo')"
                            class="css-search-6"
                          /> -->
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div
              class="content-view-box item-box-bamarket"
              style="background-color: rgb(248, 248, 248); height: auto"
            >
              <div
                class="item-box-bamarket-1"
                style="background-color: rgb(248, 248, 248); height: auto;min-height:129px"
              >
                <!-- 新四个框 -->
                <div class="flex-container flex" scoped>
                  <div
                    class="flex-item content-view-box-img"
                    v-for="(item, index) in topListData"
                    :key="index"
                  >
                    <div class="flex justify-between">
                      <div class="flex-1">
                        <el-image
                          :src="handleSymbolImg(item.symbol)"
                          class="el-img-style flex-symbol"
                        >
                        <template #error>
                          <div class="image-slot">
                            <img
                              :src="`${ConfigURL.HOST_URL}/symbol/noCoins.png`"
                              class="el-img-style"
                              width="28"
                              height="28"
                            />
                          </div>
                        </template>
                        </el-image>
                        <div class="flex-title mt-1">
                          {{ getName(item) }}
                        </div>
                        <div class="flex items-center">
                          <div class="flex-price">
                            {{ item.close }}
                          </div>
                          <div class="flex-priceUSD">
                            {{ "≈$" + item.close }}
                          </div>
                        </div>
                      </div>
                      <div class="flex items-center">
                        <img
                          v-if="item.changeRatio <= 0"
                          src="../../assets/myImages/red.png"
                          alt=""
                          class="flex-kline"
                        />
                        <img
                          v-if="item.changeRatio > 0"
                          src="../../assets/myImages/green.png"
                          alt=""
                          class="flex-kline"
                        />
                      </div>
                    </div>
                    <div class="flex mt-1">
                      <div class="flex-transaction">
                        {{
                          $t("message.hangqing.transaction") +
                          " " +
                          fixData(item.volume)
                        }}
                      </div>
                      <div v-if="item.changeRatio < 0" class="flex-rise-red">
                        {{ item.changeRatio }}%
                      </div>
                      <div v-if="item.changeRatio > 0" class="flex-rise-green">
                        {{ item.changeRatio }}%
                      </div>
                    </div>
                    <!-- <div style="background-color:blueviolet; width: 30px;height: 100px;"></div> -->
                  </div>
                </div>
              </div>
            </div>

            <!-- 列表 -->
            <div
              class="content-view-box"
              style="background-color: white; height: auto"
            >
              <div
                class=""
                style="
                  background-color: white;
                  min-height: 1040px;
                  height: auto;
                "
              >
                <div
                  class="flex-container-1 flex-container-1-pos"
                  style="background-color: white"
                  scoped
                >
                  <!-- 行情列表 -->
                  <div
                    class="box1"
                    style="display: block; background-color: white"
                  >
                    <!-- tab -->
                    <div class="css-1vii3zc" style="background-color: #ffffff">
                      <div id="box1001" class="tab-box">
                        <div
                          class="tab-item"
                          @click="changeActiveType(item)"
                          v-for="item in marketList"
                          :key="item.type"
                          :class="[activeIndex == item.id ? 'tab-active' : '']"
                          style="margin-right: 30px; font-size: 14px"
                        >
                          {{ item.name }}
                        </div>
                      </div>
                    </div>
                    <div class="css-72ldqd" style="display: block">
                      <!-- 标题 -->
                      <div class="css-qbmpwo">
                        <div class="css-vurnku" style="min-width: 696px">
                          <!-- 标题 -->
                          <div class="css-13euu76">
                            <!-- 名称 -->
                            <div
                              data-area="left"
                              class="css-1e8pqe6"
                              style="width: 110px; flex: 110 1 0%"
                            >
                              <div class="css-lkqrqt">
                                <div
                                  title="名称"
                                  class="css-1i04fkn"
                                  style="
                                    overflow: hidden;
                                    -webkit-line-clamp: 1;
                                    line-height: 16px;
                                  "
                                >
                                  {{ $t("message.hangqing.mingcheng") }}
                                </div>
                              </div>
                            </div>
                            <!-- 价格 -->
                            <div
                              data-area="left"
                              class="css-1e8pqe6"
                              style="width: 60px; flex: 60 1 0%"
                            >
                              <div class="css-lkqrqt">
                                <div
                                  title="价格"
                                  class="css-1i04fkn"
                                  style="
                                    overflow: hidden;
                                    -webkit-line-clamp: 1;
                                    line-height: 16px;
                                  "
                                >
                                  {{ $t("message.hangqing.jiage") }}
                                </div>
                              </div>
                            </div>
                            <!-- 24H涨跌 -->
                            <div
                              data-area="right"
                              class="css-1e8pqe6"
                              style="width: 55px; flex: 55 1 0%"
                            >
                              <div class="css-lkqrqt">
                                <div
                                  title="24H涨跌"
                                  class="css-1i04fkn"
                                  style="
                                    overflow: hidden;
                                    -webkit-line-clamp: 1;
                                    line-height: 16px;
                                  "
                                >
                                  {{ $t("message.hangqing.24hzhangfu") }}
                                </div>
                              </div>
                            </div>

                            <!-- 24H交易量 -->
                            <div
                              data-area="right"
                              class="css-1e8pqe6"
                              style="width: 90px; flex: 90 1 0%"
                            >
                              <div class="css-lkqrqt">
                                <div
                                  title="24h交易量"
                                  class="css-1i04fkn"
                                  style="
                                    overflow: hidden;
                                    -webkit-line-clamp: 1;
                                    line-height: 16px;
                                  "
                                >
                                  {{ $t("message.hangqing.24hjiaoyiliang") }}
                                </div>
                              </div>
                            </div>
                            <!-- 交易按钮 -->
                            <div
                              data-area="right"
                              class="css-1e8pqe6"
                              style="width: 80px; flex: 80 1 0%"
                            >
                              <div
                                class="css-1sq9fz1"
                                style="
                                  overflow: hidden;
                                  -webkit-line-clamp: 1;
                                  line-height: 16px;
                                "
                              ></div>
                            </div>
                          </div>
                          <!-- 内容 -->
                          <div class="css-1vuj9rf" style="min-height: 830px">
                            <div
                              v-for="(item, index) in searchList"
                              :key="index"
                            >
                              <div
                                direction="ltr"
                                class="css-vlibs4"
                               
                              >
                                <div
                                  class="css-leyy1t"
                                  style="cursor: pointer; text-decoration: none"
                                >
                                  <!-- 名称 -->
                                  <div
                                    data-area="left"
                                    class="css-14yoi5o"
                                    style="width: 110px; flex: 110 1 0%"
                                  >
                                    <div
                                      v-if="
                                        collectNameList.indexOf(item.symbol) < 0
                                      "
                                    >
                                      <img
                                        src="../../assets/myImages/icon/ant-design_star-2.png"
                                        style="top: 0px"
                                        @click="collectFun(item)"
                                      />
                                    </div>
                                    <div
                                      v-if="
                                        collectNameList.indexOf(item.symbol) >=
                                        0
                                      "
                                    >
                                      <img
                                        src="../../assets/myImages/icon/ant-design_star-1.png"
                                        style="top: 0px"
                                        @click="deleteCollectFun(item)"
                                      />
                                    </div>
                                    <div class="">
                                      <!-- :src="handleSymbolImg(item.symbol)" -->
                                      <!-- :src="`${ConfigURL.HOST_URL}/symbol/${item.symbol}.png`" -->
                                      <el-image
                                        :src="handleSymbolImg(item.symbol)"
                                        class="el-img-style"
                                        style="margin-left: 10px"
                                      >
                                      <template #error>
                                        <div class="image-slot">
                                          <img
                                            :src="`${ConfigURL.HOST_URL}/symbol/noCoins.png`"
                                            class="el-img-style"
                                            width="26"
                                            height="26"
                                          />
                                        </div>
                                      </template>
                                      </el-image>
                                    </div>
                                    <div class="css-y492if">
                                      <div
                                        class="css-1wp9rgv"
                                        style="font-size: 14px"
                                      >
                                        {{ getName(item) }}
                                      </div>
                                    </div>
                                  </div>
                                  <!-- 价格 -->
                                  <div
                                    data-area="left"
                                    class="css-ydcgk2"
                                    style="width: 60px; flex: 60 1 0%"
                                  >
                                    <div
                                      style="direction: ltr; font-size: 14px"
                                      :class="
                                        item.changeRatio > 0
                                          ? 'black'
                                          : 'black'
                                      "
                                    >
                                      ${{ item.close }}
                                    </div>
                                  </div>
                                  <!-- 24H涨跌 -->
                                  <div
                                    data-area="right"
                                    class="css-18yakpx"
                                    style="width: 55px; flex: 55 1 0%"
                                  >
                                    <div
                                      :class="
                                        item.changeRatio > 0 ? 'green' : 'red'
                                      "
                                      style="direction: ltr; font-size: 14px"
                                    >
                                      {{ item.changeRatio }}%
                                    </div>
                                  </div>

                                  <!-- 24H交易量 -->
                                  <div
                                    data-area="right"
                                    title="29,773.07M"
                                    class="css-102bt5g"
                                    style="
                                      width: 90px;
                                      flex: 90 1 0%;
                                      direction: ltr;
                                    "
                                  >
                                    {{ fixData(item.amount) }}
                                  </div>
                                  <!-- 交易按钮 -->
                                  <div
                                    data-area="right"
                                    class="css-4cffwv"
                                    style="width: 80px; flex: 80 1 0%"
                                  >
                                    <button
                                      class="buy-button-bamarket"
                                      @click="goDetail(item.symbol, item)"
                                    >
                                      {{ $t("message.hangqing.jiaoyi") }}
                                    </button>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      <!-- 分页器 -->
                      <div class="css-1ttcc5">
                        <div class="css-b0tuh4">
                          <button
                            data-bn-type="button"
                            class="mirror css-11xno5y"
                            disabled=""
                            aria-label="Previous page"
                          >
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 24 24"
                              fill="none"
                              class="css-1iztezc"
                            >
                              <path
                                fill-rule="evenodd"
                                clip-rule="evenodd"
                                d="M11.934 12l3.89 3.89-1.769 1.767L8.398 12l1.768-1.768 3.89-3.889 1.767 1.768-3.889 3.89z"
                                fill="currentColor"
                              ></path>
                            </svg>
                          </button>
                          <div v-for="pageItem of allPage" :key="pageItem">
                            <button
                              data-bn-type="button"
                              aria-label="Page number 1"
                              :class="
                                currentPage == pageItem
                                  ? 'css-5jht7o-1'
                                  : 'css-hlqxzb-1'
                              "
                              @click="OnclickPage(pageItem)"
                            >
                              {{ pageItem }}
                            </button>
                          </div>
                          <button
                            data-bn-type="button"
                            class="mirror css-11xno5y"
                            disabled=""
                            aria-label="Next page"
                          >
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 24 24"
                              fill="none"
                              class="css-1iztezc"
                            >
                              <path
                                fill-rule="evenodd"
                                clip-rule="evenodd"
                                d="M12.288 12l-3.89 3.89 1.768 1.767L15.823 12l-1.768-1.768-3.889-3.889-1.768 1.768 3.89 3.89z"
                                fill="currentColor"
                              ></path>
                            </svg>
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                  <!-- 榜单 -->
                  <div
                    class="box2"
                    style="display: block; background-color: white"
                  >
                    <!-- tab -->
                    <div class="css-1vii3zc">
                      <div id="box1002" class="tab-box">
                        <div
                          class="tab-item"
                          v-for="(item, i) in rankTypeList"
                          :key="i"
                          @click="changeTypeRank(item)"
                          :class="[rankIndex == item.id ? 'tab-active' : '']"
                          style="margin-left: 20px; font-size: 14px"
                        >
                          {{ item.name }}
                        </div>
                      </div>
                    </div>

                    <div class="css-72ldqd" style="display: block">
                      <!-- 标题 -->
                      <div class="css-qbmpwo">
                        <div class="css-vurnku" style="min-width: 350px">
                          <div class="css-13euu76">
                            <!-- 名称 -->
                            <div
                              data-area="left"
                              class="css-1e8pqe6"
                              style="width: 110px; flex: 110 1 0%"
                            >
                              <div class="css-lkqrqt">
                                <div
                                  style="
                                    overflow: hidden;
                                    -webkit-line-clamp: 1;
                                    line-height: 16px;
                                  "
                                >
                                  {{ $t("message.hangqing.mingcheng") }}
                                </div>
                              </div>
                            </div>
                            <!-- 价格 -->
                            <div
                              data-area="left"
                              class="css-1e8pqe6"
                              style="width: 60px; flex: 60 1 0%"
                            >
                              <div class="css-lkqrqt">
                                <div
                                  title="价格"
                                  class="css-1i04fkn"
                                  style="
                                    overflow: hidden;
                                    -webkit-line-clamp: 1;
                                    line-height: 16px;
                                  "
                                >
                                  {{ $t("message.hangqing.jiage") }}
                                </div>
                              </div>
                            </div>
                            <!-- 涨跌 或交易额-->
                            <div
                              data-area="right"
                              class="css-1e8pqe6"
                              style="width: 55px; flex: 55 1 0%"
                            >
                              <div class="css-lkqrqt">
                                <div
                                  class="css-1i04fkn"
                                  style="
                                    overflow: hidden;
                                    -webkit-line-clamp: 1;
                                    line-height: 16px;
                                  "
                                >
                                  {{
                                    rankIndex === 2
                                      ? $t("message.home.chengjiaoe")
                                      : $t("message.home.zhangdie")
                                  }}
                                </div>
                              </div>
                            </div>
                          </div>
                          <!-- 内容 -->
                          <div class="css-1vuj9rf" style="min-height: 830px">
                            <div
                              direction="ltr"
                              class="css-vlibs4"
                              v-for="(item, index) in rankDataList"
                              :key="index"
                            >
                              <div
                                class="css-leyy1t"
                                style="cursor: pointer; text-decoration: none"
                              >
                                <!-- 名称 -->
                                <div
                                  data-area="left"
                                  class="css-14yoi5o"
                                  style="width: 110px; flex: 110 1 0%"
                                >
                                  <div
                                    v-if="
                                      collectNameList.indexOf(item.symbol) < 0
                                    "
                                  >
                                    <img
                                      src="../../assets/myImages/icon/ant-design_star-2.png"
                                      style="top: 0px"
                                      @click="collectFun(item)"
                                    />
                                  </div>
                                  <div
                                    v-if="
                                      collectNameList.indexOf(item.symbol) >= 0
                                    "
                                  >
                                    <img
                                      src="../../assets/myImages/icon/ant-design_star-1.png"
                                      style="top: 0px"
                                      @click="deleteCollectFun(item)"
                                    />
                                  </div>
                                  <div class="css-y492if">
                                    <div class="css-1wp9rgv">
                                      {{ getName(item) }}
                                    </div>
                                  </div>
                                </div>
                                <!-- 价格 -->
                                <div
                                  data-area="left"
                                  class="css-ydcgk2"
                                  style="width: 60px; flex: 60 1 0%"
                                >
                                  <div
                                    style="direction: ltr"
                                    :class="
                                      item.changeRatio > 0 ? 'black' : 'black'
                                    "
                                  >
                                    ${{ item.close }}
                                  </div>
                                </div>
                                <!-- 涨跌 或交易额-->
                                <div
                                  data-area="right"
                                  class="css-18yakpx"
                                  style="width: 55px; flex: 55 1 0%"
                                >
                                  <div
                                    v-if="rankIndex === 2"
                                    style="direction: ltr"
                                  >
                                    {{ fixData(item.volume) }}
                                  </div>
                                  <div
                                    v-else
                                    :class="
                                      item.changeRatio > 0 ? 'green' : 'red'
                                    "
                                    style="direction: ltr"
                                  >
                                    {{ `${item.changeRatio}%` }}
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      <!-- 分页器 -->
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { fixData } from "@/utils/utils";
import { mapState } from "pinia";
import ConfigURL from "@/config/index";
import Axios from "@/api/currency";
import { handleSymbolImg } from "@/utils";
import { useUserStore } from "@/store/user";
import { useLanguageStore } from "@/store/lang";

export default {
  data() {
    return {
      ConfigURL,
      fixData,
      allSymbol: "", //所有symbol
      topListData: [], //热门列表
      btnChooseIndex: 0,
      marketList: [
        {
          id: 0,
          name: this.$t("message.jiaoyi.zixuan"),
          type: "collect",
        },
        {
          id: 2,
          name: this.$t("message.user.shuzihuobi"),
          type: "cryptos",
        },
        {
          id: 1,
          name: this.$t("message.user.meigu"),
          type: "US-stocks",
        },

        {
          id: 3,
          name: this.$t("message.user.etf"),
          type: "indices",
        },
        {
          id: 4,
          name: this.$t("message.user.waihui"),
          type: "forex",
        },
      ],
      marketListType: "", //市场选择类型 美股 外汇，虚拟币...
      jsonArray: [], //所有行情
      activeList: [], //选择类型后的列表数据，用于展示
      activeIndex: 0, //选中类型的index
      allPage: 1, //总共页码
      maxItemPage: 13, //单页最大展示条数
      currentPage: 1, // 当前页码
      total:0, // 总条数
      //涨幅榜 跌幅榜，成交量榜列表标题
      rankTypeList: [
        {
          id: 0,
          name: this.$t("message.hangqing.zhangfubang"),
          type: "changeRatio",
          sort: "up",
        },
        {
          id: 1,
          name: this.$t("message.hangqing.diefubang"),
          type: "changeRatio",
          sort: "down",
        },
        {
          id: 2,
          name: this.$t("message.hangqing.chengjiaobang"),
          type: "volume",
          sort: "up",
        },
      ],
      rankDataList: [], // 当前所属榜列表数据
      rankItem: {
        type: "changeRatio",
        sort: "up",
      }, //当前所选中的榜单对象,初始默认为涨幅榜
      rankIndex: 0, //当前所选中的榜单index
      searchValue: "",
      searchList: [],
      collectNameList: [],
      timer: [],
      controller:new AbortController(),
      intervalTimer1:[],
      searchLoading:false,
      searchOptions:[]
    };
  },
  computed: {
    ...mapState(useUserStore, ["existToken", "userInfo"]),
    ...mapState(useLanguageStore, ["language"]), // 直接获取state和getters
  },
  mounted() {
    this.marketListType = this.marketList[1].type;
    this.activeIndex = this.marketList[1].id;
    this.controller = new AbortController();
    this.getPublicRealtimeByType()
    this.initGetPublicRealtimeTop()
    // this.getSymbol();
    // this.getItemOptionalList();
    window.scrollTo(0, 0);
  },
  methods: {
    handleSymbolImg,
    initGetPublicRealtimeByType(){
      this.cleanTime();
      this.getPublicRealtimeByType()
    },
    getPublicRealtimeByType(){
      if (this.marketListType == "collect") {
        this.fliterTypeData();
        return
      }

      const params = {
        type:this.marketListType,
        // category:'',
        pageNo:this.currentPage,
        pageSize:this.maxItemPage
      }
      Axios.publicRealtimeByType({...params},this.controller).then((res)=>{
        if(res.code === 0){
          this.jsonArray = res.data;
          // 获取总条数
          this.total = res.total;
        }else{
        }
      }).finally(()=>{
         //筛选对应type
        this.fliterTypeData();
        this.cleanTime(); // 清除定时器
        if(this.controller.signal.aborted){ 
          return; // 中断接口之后退出事件不做轮询
        }
        this.getTimeTimer();
      })
    },
    // 获取所有symbol
    getSymbol() {
      Axios.getAllSymbolDetails({type:this.marketListType,current:this.currentPage,size:this.maxItemPage}).then((res) => {
        this.allSymbol = res.data.map((val) => {
          return val.symbol;
        }).join(",");
        
        // 获取总条数
        this.total = res.total;

        this.cleanTime(); // 清除定时器

        if(this.controller.signal.aborted){ 
          return; // 中断接口之后退出事件不做轮询
        }
        this.getTimeTimer();
      });
    },
    // 轮训获取symbol的行情
    getTimeTimer() {
      let timerId = setInterval(() => {
        // this.getList(this.allSymbol);
        this.getPublicRealtimeByType()
      }, 1000);
      this.timer.push(timerId);
    },
    getList(symbol) {
      let t = this;
      if(symbol){
        Axios.getRealtime({ symbol },this.controller).then((res) => {
          this.jsonArray = res.data;
          //筛选对应type
          this.fliterTypeData();
        });
      }else{
        this.fliterTypeData();
      }
    },
    async fliterTypeData() {
      if (this.marketListType == "collect") {
        await this.getItemOptionalList();
      } else {
        // 行情的展示数据
        // this.activeList = this.jsonArray.filter((val) => {
        //   return val.type == this.marketListType;
        // });
        this.activeList = [...this.jsonArray] 
      }
      // //获取顶部热门
    //  await this.getPublicRealtimeTop()

      this.getPageSize();
      this.onInputSearch();
      // 右边榜单的数据
      let rankDataList = [...this.activeList].sort(
        this.compare(this.rankItem.type, this.rankItem.sort)
      );
      this.rankDataList = rankDataList.slice(0, this.maxItemPage);
    },
    compare(p, type) {
      //这是比较函数
      return function (m, n) {
        var a = m[p];
        var b = n[p];
        if (a == b) {
          return;
        }
        if (type == "up") {
          return b - a; //升序
        } else if (type == "down") {
          return a - b; //降序
        } else {
          return a - b;
        }
      };
    },
    initGetPublicRealtimeTop(){
      this.unbindTimerHandle();
      if(this.controller.signal.aborted){ 
        return; // 中断接口之后退出事件不做轮询
      }
      let intervalTimer1Id = setInterval(() => {
        this.getPublicRealtimeTop()
      }, 1000);
      this.intervalTimer1.push(intervalTimer1Id);
    },
    getPublicRealtimeTop(){
      return new Promise((reslove)=>{
        if(this.marketListType=== 'collect'){
          this.topListData = this.activeList.slice(0,4)
          reslove()
          return
        }

        const params = {
          type:this.marketListType,
        }
        Axios.publicRealtimeTop(params,this.controller).then((res)=>{
          if(res.data.length>0){
              this.topListData = res.data.slice(0,4)
              reslove()
          }
        }).finally(()=>{
          this.initGetPublicRealtimeTop();
        })

      })
    },
    getPageSize() {
      let size = Math.ceil(this.total / this.maxItemPage);
      this.allPage = size < 1 ? 1 : size;
    },
    OnclickPage(page) {
      if (this.currentPage == page) {
        return;
      }
      this.currentPage = page;
      // this.getSymbol();
      this.initGetPublicRealtimeByType();
      this.initGetPublicRealtimeTop()
    },
    matchPage(index, currentPage) {
      let flag = index < currentPage * this.maxItemPage && index >= (currentPage - 1) * this.maxItemPage;
      return flag;
    },
    changeActiveType(item) {
      this.marketListType = item.type;
      this.activeIndex = item.id;
      this.currentPage = 1;
      this.rankItem = this.rankTypeList[0];
      this.rankIndex = this.rankItem.id;
      // this.getSymbol();
      this.initGetPublicRealtimeByType();
      this.initGetPublicRealtimeTop()
      // this.fliterTypeData();
    },
    // 榜单切换
    changeTypeRank(item) {
      this.rankItem = item;
      this.rankIndex = item.id;
      this.rankDataList = [...this.activeList].sort(
        this.compare(this.rankItem.type, this.rankItem.sort)
      );
    },
    onInputSearch() {
      if (this.searchValue != "") {
        this.searchList = this.activeList.filter((val) => {
          return (
            val.symbol.toUpperCase().indexOf(this.searchValue.toUpperCase()) !=
            -1
          );
        });
        this.OnclickPage(1);
      } else {
        this.getPageSize();
        this.searchList = this.activeList;
      }
    },
    // 远端搜索
    remoteSearchMethod(query){
      if(query){
        this.searchLoading = true
        const params = {
          name:query
        }
        Axios.getAllSymbolDetails(params).then((res) => {
        this.searchOptions = res.data.map((item)=>{
          return {
            symbol:item.symbol,
            name:item.name,
            type:item.type
          }
        })
      }).finally(()=>{
        this.searchLoading = false
      });
      }else{
        this.searchOptions = []
      }
    },
    //获取自选币种列表
    getItemOptionalList() {
      return new Promise((resolve,reject)=>{
        if (this.existToken) {
          Axios.getItemOptionalStatus().then((res) => {
            this.collectNameList = [];
            this.jsonArray = res.data.map((item) => {
              this.collectNameList.push(item.symbol);
              item.changeRatio = item.change_ratio
              return item
            });
            this.activeList =this.jsonArray
            resolve()
          });
          // if (this.jsonArray.length) {
          //   this.activeList = this.jsonArray.filter((val) => {
          //     return this.collectNameList.includes(val.symbol);
          //   });
          // }
        } else {
          this.activeList = [];
          resolve()
        }

      })
    },
    //收藏
    collectFun(item) {
      let obj = {
        symbol: item.symbol,
      };

      if (this.existToken) {
        Axios.addItemUserOptiona(obj).then((res) => {
          this.$message({
            message: this.$t("message.jiaoyi.shoucangchenggong"),
            type: "success",
          });
          this.getItemOptionalList();
        });
      } else {
        this.$router.push("/login");
      }
    },
    //取消收藏
    deleteCollectFun(item) {
      let obj = {
        symbol: item.symbol,
      };
      Axios.deleteItemUserOptiona(obj).then((res) => {
        this.$message({
          message: this.$t("message.jiaoyi.quxiaoshoucangchenggong"),
          type: "success",
        });
        this.getItemOptionalList();
      });
    },
    getName(item) {
      return item.symbol.toUpperCase();
    },
    goDetail(symbol, item) {
      let RouterName = "sustainable"; //永续
      if (item.type == "US-stocks") {
        this.$router.push({
          path: `usStocks/constract/${symbol}`,
          query: { timestamp: Date.now(), RouterName: RouterName },
        });
      } else if (item.type == "cryptos") {
        this.$router.push({
          path: `coin/constract/${symbol}`,
          query: { timestamp: Date.now(), RouterName: RouterName },
        });
      } else if (item.type == "indices") {
        this.$router.push({
          path: `etf/constract/${symbol}`,
          query: { timestamp: Date.now(), RouterName: RouterName },
        });
      } else if (item.type == "forex") {
        this.$router.push({
          path: `forex/constract/${symbol}`,
          query: { timestamp: Date.now(), RouterName: RouterName },
        });
      }
    },
    // 清除定时器
    cleanTime(){
      this.timer.forEach(item=>{
        if(item){
          clearInterval(item);
        }
      });
    },
    // 清除定时器
    unbindTimerHandle(){
      this.intervalTimer1.forEach(item=>{
        if(item){
          clearInterval(item);
        }
      });
    }
  },
  unmounted() {
    this.cleanTime();
    this.unbindTimerHandle();
    this.controller.abort();
  },
};
</script>

<style scoped lang="scss">
@import url("@/assets/css/market/bamarket.css");

.tab-item {
  padding-top: 20px !important;
  padding-bottom: 10px !important;
  cursor: pointer;
}

.tab-active {
  border-bottom: 4px solid #1d91ff;
}
.content-view-box-img{
  margin-right: 19px;
  &:nth-last-child(1){
    margin-right: 0;
  }
}
:deep(.search-select){
  .el-input.is-focus{
    .el-input__wrapper{
      box-shadow: none!important;;
    }
  }
  .el-input{
    height: 42px;
    .el-input__wrapper{
      box-shadow: none!important;;
    }
  }
}
</style>
