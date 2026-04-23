<!-- 交易页首页 -->
<template>
  <div class="home-box">
    <div class="introduce-box" style="background-color: White; height: auto">
      <div class="introduce-background content-view-box">
        <div class="trade-content-box">
          <div class="right-trade">
            <div
              :class="
                getLocalLan() == 0
                  ? 'introduce-title-box'
                  : 'introduce-title-box-3'
              "
            >
              <h3 class="font-size62" style="color: #171a1e">
                {{ $t("message.home.jiamizhilv") }}
              </h3>
              <div class="div-blank-30"></div>
              <p class="font-size20" style="color: #474d57; padding-top: 10px">
                {{ $t("message.home.jiamizhilv_1") }}
              </p>
              <div class="div-blank-30"></div>
              <div class="input-box" v-if="!existToken">
                <input
                  :placeholder="
                    $t('message.home.shouji') + '/' + $t('youxiang')
                  "
                  class="input-style"
                />

                <button
                  class="font-size20 theme-button"
                  style="
                    width: 130px;
                    font-weight: 400;
                    height: 50px;
                    line-height: 50px;
                    position: relative;
                    top: 3px;
                  "
                  @click="goRouter('/register')"
                >
                  {{ $t("message.home.zhuce") }}
                </button>
              </div>
            </div>
          </div>
          <div
            class="right-trade"
            style="position: relative; margin-right: 10px"
          >
            <img
              src="@/assets/images/exchangeHome/bitcoin.png"
              alt=""
              width="335"
              height="379"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 公告 -->
    <div
      style="
        background-color: White;
        height: 80px;
        z-index: 100;
        margin: 30px 0;
      "
      scope
    >
      <notice-bar style="background-color: White"></notice-bar>
    </div>

    <!-- 行情列表 -->
    <div>
      <div
        class="market-box content-view-box"
        style="background-color: #ffffff"
      >
        <!-- <h3 class="black-color font-size40 font-bold" style="margin-top:0px;margin-bottom:50px;">{{ $t('message.home.home1') }}</h3> -->
        <div class="market-title">
          <div class="market-item">
            <div>{{ $t("message.hangqing.mingcheng") }}</div>
            <div class="market-item-icon">
              <div>
                <i
                  class="el-icon-caret-top icon1"
                  :class="[isSortOne == 1 ? 'activeColor' : '']"
                  @click="sortLetter(1)"
                ></i>
              </div>
              <div>
                <i
                  class="el-icon-caret-bottom icon2"
                  :class="[isSortOne == 2 ? 'activeColor' : '']"
                  @click="sortLetter(2)"
                ></i>
              </div>
            </div>
          </div>
          <div class="market-item">
            <div>{{ $t("message.hangqing.jiage") }}</div>
            <div class="market-item-icon">
              <div>
                <i
                  class="el-icon-caret-top icon1"
                  :class="[isSortTwo == 1 ? 'activeColor' : '']"
                  @click="sortNewPice(1)"
                ></i>
              </div>
              <div>
                <i
                  class="el-icon-caret-bottom icon2"
                  :class="[isSortTwo == 2 ? 'activeColor' : '']"
                  @click="sortNewPice(2)"
                ></i>
              </div>
            </div>
          </div>
          <div class="market-item">
            <div>{{ $t("message.home.zhangdie") }}</div>
            <div class="market-item-icon">
              <div>
                <i
                  class="el-icon-caret-top icon1"
                  :class="[isSortThree == 1 ? 'activeColor' : '']"
                  @click="sortPice(1)"
                ></i>
              </div>
              <div>
                <i
                  class="el-icon-caret-bottom icon2"
                  :class="[isSortThree == 2 ? 'activeColor' : '']"
                  @click="sortPice(2)"
                ></i>
              </div>
            </div>
          </div>
          <div class="market-item">
            <div>{{ $t("message.hangqing.24hjiaoyiliang") }}</div>
          </div>
          <div class="market-item">
            <div>{{ $t("message.home.hangqing") }}</div>
          </div>
          <div class="market-item"></div>
        </div>
        <div
          class="market-content font-size20"
          v-for="(item, index) in list"
          :key="index"
          @click="goDetail(item)"
        >
          <!-- 第一列名称 :src="`${ConfigURL.HOST_URL}/symbol/${item.symbol}.png`"-->
          <div class="flex-row-center">
            <el-image :src="handleSymbolImg(item.symbol)" class="el-img-style">
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
            <div class="margin-left10">{{ item.name }}</div>
          </div>
          <!-- 第二列最新价 -->
          <div style="margin-left: 40px; margin-top: 35px">
            $ {{ item.close }}
          </div>
          <!-- 第三列24H涨跌 -->
          <div
            class="red"
            v-if="item.change_ratio < 0"
            style="margin-left: 40px; margin-top: 35px"
          >
            {{ item.change_ratio }} %
          </div>

          <div class="green" v-else style="margin-left: 40px; margin-top: 35px">
            {{ item.change_ratio }} %
          </div>
          <!-- 第四列24H交易量 -->
          <div style="margin-left: 40px; margin-top: 35px">
            $ {{ fixDate(item.volume) }}
          </div>
          <!-- 第五列行情-->
          <div
            class="red"
            v-if="item.change_ratio < 0"
            style="margin-left: 40px; margin-top: 25px"
          >
            <img
              src="@/assets/images/exchangeHome/red.png"
              alt=""
              width="88"
              height="40"
            />
          </div>
          <div class="green" v-else style="margin-left: 40px; margin-top: 25px">
            <img
              src="@/assets/images/exchangeHome/green.png"
              alt=""
              width="88"
              height="40"
            />
          </div>
          <!-- 第六列去交易-->
          <div class="text-right" style="margin-left: 0px; margin-top: 25px">
            <button :class="getLocalLan() == 0 ? 'buy-button' : 'buy-button-3'">
              {{ $t("message.home.home6") }}
            </button>
          </div>
        </div>
        <!-- 查看更多 -->
        <div
          class="flex items-center justify-center font-size20 linght-grey-color margin-top50 mouse-cursor"
          @click="checkMore"
          style="margin-left: 0px"
        >
          <span>{{ $t("message.home.home7") }}</span>
          <svg
            style="margin-left: 10px"
            width="10"
            height="17"
            viewBox="0 0 10 17"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              fill-rule="evenodd"
              clip-rule="evenodd"
              style="fill: #707a8a"
              d="M3.2597e-09 3.14658C0.762071 2.34353 1.52386 1.54046 2.28574 0.737305C4.85709 3.44754 7.42864 6.15828 10 8.86851C7.42864 11.5788 4.85709 14.2895 2.28574 16.9997C1.52386 16.1967 0.762071 15.3937 1.8743e-08 14.5904C1.80022 12.6929 3.60034 10.7957 5.42843 8.86851C3.57752 6.91747 1.78886 5.03213 3.2597e-09 3.14658Z"
            />
          </svg>
        </div>
      </div>
    </div>

    <!-- 立刻创建您的加密组合 -->
    <div>
      <div
        class="content-view-box item-box-index introduce-background"
        style="background-color: #ffffff; height: 750px"
      >
        <div class="item-box-0">{{ $t("message.home.touzizuhe_1") }}</div>
        <div class="item-box-0-1">{{ $t("message.home.touzizuhe_2") }}</div>
        <div class="item-box-0-icon0">
          <img src="@/assets/images/exchangeHome/index_0_0.png" />
        </div>
        <div class="item-box-0-title0">
          {{ $t("message.home.touzizuhe_3") }}
        </div>
        <div class="item-box-0-content0">
          {{ $t("message.home.touzizuhe_4") }}
        </div>
        <div class="item-box-0-icon1">
          <img src="@/assets/images/exchangeHome/index_0_1.png" />
        </div>
        <div class="item-box-0-title1">
          {{ $t("message.home.touzizuhe_5") }}
        </div>
        <div class="item-box-0-content1">
          {{ $t("message.home.touzizuhe_6") }}
        </div>
        <div class="item-box-0-icon2">
          <img src="@/assets/images/exchangeHome/index_0_2.png" />
        </div>
        <div class="item-box-0-title2">
          {{ $t("message.home.touzizuhe_7") }}
        </div>
        <div class="item-box-0-content2">
          {{ $t("message.home.touzizuhe_8") }}
        </div>
        <div class="item-box-0-icon3">
          <img src="@/assets/images/exchangeHome/index_0_3.png" />
        </div>
        <div class="item-box-0-title3">
          {{ $t("message.home.touzizuhe_9") }}
        </div>
        <div class="item-box-0-content3">
          {{ $t("message.home.touzizuhe_10") }}
        </div>
        <div class="item-box-0-icon4">
          <img src="@/assets/images/exchangeHome/index_0_4.png" />
        </div>
      </div>
    </div>

    <!-- 赚取价值100万美元的加密货币 -->
    <div>
      <div
        class="content-view-box introduce-background"
        style="background-color: #f6f8fe; height: 460px; width: 1920px"
      >
        <div
          class="content-view-box introduce-background"
          style="
            background-color: #f6f8fe;
            position: absolute;
            margin: auto;
            top: 0;
            left: 0;
            bottom: 0;
            right: 0;
          "
        >
          <div class="item-box-1">{{ $t("message.home.jiamihuobi_1") }}</div>
          <div class="item-box-1-1">{{ $t("message.home.jiamihuobi_2") }}</div>
          <div
            :class="
              getLocalLan() == 0 ? 'item-box-1-rect' : 'item-box-1-rect-226'
            "
          >
            <p>{{ $t("message.home.jiamihuobi_3") }}</p>
          </div>
          <img
            src="@/assets/images/exchangeHome/MaskGroup.png"
            class="item-box-1-img"
          />
        </div>
      </div>
    </div>

    <!-- 最值得信赖的加密货币平台 -->
    <div>
      <div
        class="content-view-box item-box-index introduce-background"
        style="background-color: #ffffff; height: 500px"
      >
        <div class="item-box-2">{{ $t("message.home.huobipingtai_1") }}</div>
        <div class="item-box-2-1">{{ $t("message.home.huobipingtai_2") }}</div>
        <div class="item-box-2-p0">
          <!-- <div class="introduce-background"> -->
          <div class="item-box-2-icon0">
            <img src="@/assets/images/exchangeHome/index_2_0.png" />
          </div>
          <div class="item-box-2-title0">
            {{ $t("message.home.huobipingtai_3") }}
          </div>
          <div class="item-box-2-content0">
            {{ $t("message.home.huobipingtai_4") }}
          </div>
          <!-- </div> -->
        </div>
        <div class="item-box-2-p1">
          <div class="item-box-2-icon0">
            <img src="@/assets/images/exchangeHome/index_2_1.png" />
          </div>
          <div class="item-box-2-title0">
            {{ $t("message.home.huobipingtai_5") }}
          </div>
          <div class="item-box-2-content0">
            {{ $t("message.home.huobipingtai_6") }}
          </div>
        </div>
        <div class="item-box-2-p2">
          <div class="item-box-2-icon0">
            <img src="@/assets/images/exchangeHome/index_2_2.png" />
          </div>
          <div class="item-box-2-title0">
            {{ $t("message.home.huobipingtai_7") }}
          </div>
          <div class="item-box-2-content0">
            {{ $t("message.home.huobipingtai_8") }}
          </div>
        </div>
      </div>
    </div>

    <!-- 信息 -->
    <div>
      <div
        class="content-view-box introduce-background"
        style="background-color: #1d91ff; height: 216px; width: auto"
      >
        <div
          style="
            width: 1200px;
            position: absolute;
            margin: auto;
            top: 0;
            left: 0;
            bottom: 0;
            right: 0;
          "
        >
          <div class="item-box-3-p0 introduce-background">
            <div class="item-box-3-title0">$217B</div>
            <div class="item-box-3-content0">
              {{ $t("message.home.xnxi_1") }}
            </div>
          </div>
          <div class="item-box-3-p1 introduce-background">
            <div class="item-box-3-title0">100+</div>
            <div class="item-box-3-content0">
              {{ $t("message.home.xnxi_2") }}
            </div>
          </div>
          <div class="item-box-3-p2 introduce-background">
            <div class="item-box-3-title0">103M+</div>
            <div class="item-box-3-content0">
              {{ $t("message.home.xnxi_3") }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 交易，随时随地 -->
    <div>
      <div
        class="content-view-box item-box-index introduce-background"
        style="background-color: #ffffff; height: 600px"
      >
        <div class="item-box-4-p0">
          <div class="item-box-4-title0">
            {{ $t("message.home.jiaoyi_1") }}
          </div>
          <div class="item-box-4-icon0">
            <img src="@/assets/images/exchangeHome/download/coder.png" />
          </div>
          <div class="item-box-4-title1">
            {{ $t("message.home.jiaoyi_2") }}
          </div>
          <div class="item-box-4-title2">
            {{ $t("message.home.jiaoyi_3") }}
          </div>

          <div class="flex-container-index item-box-4-list" scoped>
            <div class="flex-item-index" scoped>
              <img
                src="@/assets/images/exchangeHome/download/ios.png"
                alt=""
                @click="goApp"
                width="94"
                height="76"
              />
            </div>
            <div class="flex-item-index" scoped>
              <img
                src="@/assets/images/exchangeHome/download/android.png"
                alt=""
                @click="goApp"
                width="94"
                height="76"
              />
            </div>
            <div class="flex-item-index" scoped>
              <img
                src="@/assets/images/exchangeHome/download/mac.png"
                alt=""
                width="94"
                height="76"
              />
            </div>
            <div class="flex-item-index" scoped>
              <img
                src="@/assets/images/exchangeHome/download/windows.png"
                alt=""
                width="94"
                height="76"
              />
            </div>
          </div>
        </div>

        <div class="item-box-4-p1">
          <div class="item-box-4-icon1">
            <img src="@/assets/images/exchangeHome/index_4.png" />
          </div>
        </div>
      </div>
    </div>

    <!-- footer -->
    <footer-view></footer-view>
  </div>
</template>

<script>
import NoticeBar from "@comCommon/noticeBar.vue";
import { mapState } from "pinia";
import ConfigURL from "@/config/index";
import { useUserStore } from "@/store/user";
import { useLanguageStore } from "@/store/lang";

import FooterView from "@/components/layout/footerView.vue";
import Axios from "@/api/currency.js";
import AxiosMy from "@/api/my.js";
import { handleSymbolImg, constantMap } from "@/utils";

export default {
  components: { NoticeBar, FooterView },

  data() {
    return {
      ConfigURL,
      intervalId: null, //定时器
      symbolList: [],
      list: [],
      showCode: false,
      helpData: [
        {
          isShow: false,
        },
      ],
      host_url: "", //域名
      isSortOne: 0,
      isSortTwo: 0,
      isSortThree: 0,
      searchValue: "",
      allList: [],
    };
  },
  computed: {
    ...mapState(useUserStore, ["existToken", "userInfo"]),
    ...mapState(useLanguageStore, ["language"]), // 直接获取state和getters
  },
  mounted() {
    this.host_url = window.location.hostname;
    if (this.$route.query.usercode) {
      localStorage.setItem("usercode", this.$route.query.usercode);
    }

    this.getSymbol();
    if (this.existToken) {
      this.getHelpInfo();
    }
  },

  unmounted() {
    clearInterval(this.intervalId); //生效
    this.intervalId = null;
  },

  methods: {
    handleSymbolImg,
    mathDatarandom(val) {
      var a = Number(val);
      var b = Math.round(Math.random() * 10) / 100;
      var data = bigDecimal.add(a, b);

      return data;
    },
    goRouter(parmas) {
      clearInterval(this.intervalId);
      this.$router.push(parmas);
    },

    searchFun() {
      if (this.searchValue) {
        this.list = this.allList.filter((item) => {
          return (
            item.symbol.toUpperCase().indexOf(this.searchValue.toUpperCase()) !=
            -1
          );
        });
      } else {
        this.list = this.allList.slice(0, 5);
      }
    },
    //币种列表
    async getSymbol() {
      let res = await Axios.getAllSymbolDetails({ type: "cryptos" });
      this.symbol = res.data
        .map((val) => {
          return val.symbol;
        })
        .join(",");
      clearInterval(this.intervalId);
      this.intervalId = setInterval(() => {
        this.getList(this.symbol);
      }, 2000);
    },
    //币种行情列表
    async getList(symbol) {
      let res = await Axios.getRealtime({ symbol });
      this.allList = res.data;

      this.symbolList = res.data.slice(0, 5);
      for (var i = 0; i < this.symbolList.length; i++) {
        var jiage = +this.symbolList[i]["close"] * 10000;
        jiage = jiage / 10000;
        this.symbolList[i]["close"] = jiage.toFixed(5);
      }
      this.list = res.data.slice(0, 5);
      for (var i = 0; i < this.list.length; i++) {
        var jiage = +this.list[i]["close"] * 10000;
        jiage = jiage / 10000;
        this.list[i]["close"] = jiage.toFixed(5);
      }
      this.searchFun();
      if (this.isSortOne == 2) {
        this.list = this.list.sort(this.sortLetterList("symbol"));
      }
      if (this.isSortOne == 1) {
        this.list = this.list.sort(this.sortLetterTwoList("symbol"));
      }
      if (this.isSortTwo == 1) {
        this.list = this.list.sort(this.orderListAsc("close"));
      }
      if (this.isSortTwo == 2) {
        this.list = this.list.sort(this.orderListAsc("close", "ask"));
      }
      if (this.isSortThree == 1) {
        this.list = this.list.sort(this.orderListAsc("change_ratio"));
      }
      if (this.isSortThree == 2) {
        this.list = this.list.sort(this.orderListAsc("change_ratio", "ask"));
      }
    },
    // 去交易
    goDetail(item) {
      const { type, symbol } = item;
      const { defaultUrlMap } = constantMap(type, symbol);
      const url = defaultUrlMap[type];

      this.$router.push(url);
    },
    //帮助中心图标切换
    helpBtn(item) {
      item.isShow = !item.isShow;
    },

    async getHelpInfo() {
      const res = await AxiosMy.getCms({
        model: "help_center",
      });
      this.helpData = res.data.slice(0, 5);
    },
    showQr() {
      this.showCode = true;
    },
    hideQr() {
      this.showCode = false;
    },
    checkMore() {
      this.$router.push("/market");
    },
    // TODO
    goApp() {
      if (this.language == "en") {
        window.location.href = "https://" + this.host_url + "/app.html";
      } else if (this.language == "CN") {
        window.location.href = "https://" + this.host_url + "/app-ft.html";
      } else if (this.language == "zh-CN") {
        window.location.href = "https://" + this.host_url + "/app-cn.html";
      }
    },
    fixDate(val, i18n) {
      // 保留两位小数
      const value = val / 1;
      if (isNaN(value)) {
        return "--";
      }
      if ((value / 1000000).toString().split(".")[0].length <= 4) {
        return (value / 1000000).toFixed(3) + " " + "M";
      } else {
        return (value / 1000000000).toFixed(3) + " " + "B";
      }
    },
    getLocalLan() {
      if (localStorage.getItem("localLan") == "en") {
        return 1;
      } else if (localStorage.getItem("localLan") == "cht") {
        return 0;
      } else if (localStorage.getItem("localLan") == "zh-CN") {
        return 0;
      }
      return 1;
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
        if (type == "asc")
          return parseFloat(a[filed]) > parseFloat(b[filed]) ? 1 : -1;
        return parseFloat(a[filed]) > parseFloat(b[filed]) ? -1 : 1;
      };
    },
    sortLetter(val) {
      this.isSortOne = val;
      this.isSortTwo = 0;
      this.isSortThree = 0;
    },
    sortNewPice(val) {
      this.isSortTwo = val;
      this.isSortOne = 0;
      this.isSortThree = 0;
    },
    sortPice(val) {
      this.isSortThree = val;
      this.isSortTwo = 0;
      this.isSortOne = 0;
    },
  },
};
</script>

<style scoped>
@import url("./index.css");
.market-item {
  display: flex;
  align-items: center;
  font-size: 20px;
  color: #707a8a;
  width: 16.8%;
}

.market-item-icon .icon1 {
  font-size: 14px;
  position: relative;
  top: 3px !important;
  cursor: pointer;
  color: #707a8a;
  display: block;
}

.market-item-icon .icon2 {
  font-size: 14px;
  position: relative;
  top: -3px !important;
  cursor: pointer;
  color: #707a8a;
  display: block;
}
.activeColor {
  color: #1e2329 !important;
}
.search-box {
  width: 1200px;
  margin: 0 auto;
  display: flex;
  margin-bottom: 20px;
  justify-content: flex-end;
}
</style>
