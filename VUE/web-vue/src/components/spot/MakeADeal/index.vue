<template>
  <div class="makeADeal-wrapper">
    <!-- 搜索框 -->
    <div
      class="search-wrapper"
      style="display: flex; align-items: center; position: relative"
    >
      <div class="search" style="flex: 1">
        <div class="search-img">
          <img src="@/assets/images/quotes/Group2987.png" alt="" />
        </div>
        <input
          type="text"
          :placeholder="$t('message.home.chaxun')"
          @focus="focusSearch"
          @input="$emit('searchFun', this.searchValue)"
          v-model="searchValue"
        />
      </div>
      <!-- 取消 -->
      <span
        v-if="showSearch"
        @click="cancelSearch"
        style="color: #1d91ff; font-size: 12px; margin: 0 12px; cursor: pointer"
        >{{ $t("message.jiaoyi.quxiao") }}</span
      >
    </div>
    <div v-if="showSearch">
      <!-- 搜索历史 -->
      <div class="" v-if="historyList.length > 0">
        <p style="padding-left: 18px; padding-bottom: 8px">
          {{ $t("message.jiaoyi.sousuolishi") }}
        </p>
        <div class="history-list">
          <ul>
            <li
              v-for="(item, index) in historyList"
              @click="checkCurrency(item)"
              :key="index"
            >
              {{ item.name }}
            </li>
          </ul>
          <div class="delelte-icon">
            <i class="el-icon-delete" @click="deleteAll"></i>
          </div>
        </div>
      </div>
      <!-- 标题-热门搜索 -->
      <p style="padding: 18px">
        {{ $t("message.jiaoyi.renmensousuo") }}
      </p>
      <div class="list-wrapper over-auto">
        <div
          class="list-item"
          v-for="(item, index) in listData"
          :key="index"
          @click.stop="checkCurrencyTwo(item)"
        >
          <div class="name" style="padding-right: 5px">
            <span>{{ index + 1 }}</span>
            <div class="symbol">{{ item.name }}</div>
            <!-- <span class="xxx">3x</span> -->
          </div>
          <div class="price text-right">{{ item.volume }}</div>
          <div
            class="ratio text-right"
            :class="[item.change_ratio > 0 ? 'color-up' : 'color-down']"
            style="width: 50px"
          >
            {{ item.change_ratio > 0 ? "+" : "" }}{{ item.change_ratio }}
          </div>
          <span v-if="!item.isCollect" @click.stop="collect(item)">
            <img
              src="@/assets/images/quotes/get_select.png"
              style="margin-left: 20px"
              width="12"
              height="12"
            />
          </span>
          <span v-else @click.stop="collect(item)">
            <img
              src="@/assets/images/quotes/get_select-1.png"
              style="margin-left: 20px"
              width="12"
              height="12"
            />
          </span>
        </div>
      </div>
    </div>
    <div v-if="!showSearch">
      <!-- 标题： 交易对、价格、涨跌 -->
      <div class="ticker-table-box">
        <div class="title">
          <div class="title-item" v-for="(it, i) in titleList" :key="i">
            <span>{{ $t(`message.home.${it.label}`) }}</span>
            <div class="to-sort">
              <el-icon
                ><CaretTop
                  class="el-icon-caret-top"
                  :class="[sortIndex == it.sort[0] ? 'color-red' : '']"
                  @click="CurrencySort(it.sort[0])"
              /></el-icon>
              <el-icon
                ><CaretBottom
                  class="el-icon-caret-bottom"
                  :class="[sortIndex == it.sort[1] ? 'color-red' : '']"
                  @click="CurrencySort(it.sort[1])"
              /></el-icon>
            </div>
          </div>
        </div>
      </div>
      <!-- 交易对数据 -->
      <div class="list-wrapper over-auto">
        <div
          class="list-item"
          v-for="(item, index) in AllListData"
          :key="index"
          @click.stop="checkCurrency(item)"
        >
          <div class="name">
            <div @click.stop="collect(item)">
              <img
                v-if="!item.isCollect"
                src="@/assets/images/quotes/get_select.png"
                width="12"
                height="12"
              />
              <img
                v-else
                src="@/assets/images/quotes/get_select-1.png"
                width="12"
                height="12"
              />
            </div>
            <div class="symbol">{{ item.name }}</div>
          </div>
          <div class="price text-right">{{ item.close }}</div>
          <div
            class="ratio text-right"
            :class="[item.change_ratio > 0 ? 'color-up' : 'color-down']"
          >
            {{ item.change_ratio > 0 ? "+" : "" }}{{ item.change_ratio }}
          </div>
        </div>
      </div>
    </div>
    <!-- 最新成交 -->
    <div class="deal">
      <div class="deal-title">
        <div
          :class="{ active: currentDeal === 'new' }"
          @click="checkTab('new')"
        >
          {{ $t("message.home.zuixinchengjiao") }}
        </div>
        <div :class="{ active: currentDeal === 'my' }" @click="checkTab('my')">
          {{ $t("message.home.wodechengjiao") }}
        </div>
      </div>
      <div class="deal-main">
        <div class="deal-main-title">
          <div>{{ $t("message.home.jiage") }}({{ unit }})</div>
          <div class="text-right">
            {{ $t("message.home.shuliang") }}
          </div>
          <div class="text-right">{{ $t("message.home.shijian") }}</div>
        </div>
        <div class="deal-item-wrapper">
          <template v-if="currentDeal === 'new'">
            <div
              class="deal-item"
              v-for="(item, index) in dealList"
              :key="index"
            >
              <div
                class="price"
                :class="[item.direction == 'sell' ? 'color-up' : 'color-down']"
              >
                {{ item.price }}
              </div>
              <div class="number text-right">{{ item.amount }}</div>
              <div class="time text-right">{{ item.current_time }}</div>
            </div>
          </template>
          <template v-if="currentDeal === 'my'">
            <template v-if="!existToken">
              <div class="no-login">
                {{ $t("message.home.dengluhouchakan") }}
              </div>
            </template>
            <template v-else>
              <div
                class="deal-item"
                v-for="(item, index) in myDataList"
                :key="index"
              >
                <div
                  class="price"
                  :class="[item.offset == 'open' ? 'color-up' : 'color-down']"
                >
                  {{ item.price }}
                </div>
                <div
                  class="number text-right"
                  :class="[item.offset == 'open' ? 'color-up' : 'color-down']"
                >
                  {{
                    item.offset == "open"
                      ? "+" + item.volume
                      : "-" + item.volume
                  }}
                </div>
                <div class="time text-right">
                  {{ item.create_time.substring(11, item.create_time.length) }}
                </div>
              </div>
              <template v-if="myDataList.length == 0">
                <div class="empty">{{ $t("message.home.noData") }}</div>
              </template>
            </template>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from "pinia";
import { useUserStore } from "@/store/user";
import Axios from "@/api/currency.js";
import { CaretTop, CaretBottom } from "@element-plus/icons-vue";
export default {
  emits: [
    "searchFun",
    "CurrencySort",
    "getSelectList",
    "checkCurrency",
    "filterFun",
    "deleteCollectFun",
  ],
  components: { CaretTop, CaretBottom },
  name: "index",
  props: ["listData", "AllListData", "dealList", "unit"], //这个props拿不到最新的
  data() {
    return {
      showSearch: false,
      currentDeal: "new", //切换最新成交和我的成交
      searchValue: "",
      sortIndex: 0,
      myDataList: [],
      isCollectShow: false,
      historyList: [],
      isUSD: false,
      titleList: [
        {
          label: "jiaoyidui",
          sort: [1, 2],
        },
        {
          label: "jiage",
          sort: [3, 4],
        },
        {
          label: "zhangdie",
          sort: [5, 6],
        },
      ],
    };
  },

  watch: {
    "$route.params.id"() {
      if (this.currentDeal == "my") {
        if (this.existToken) {
          this.getMyDataList();
        }
      }
    },
  },
  mounted() {
    this.historyList = JSON.parse(localStorage.getItem("historyList")) || [];
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  methods: {
    checkCurrencyTwo(item) {
      if (JSON.stringify(this.historyList).indexOf(item.name) == -1) {
        this.historyList.push(item);
      }
      localStorage.setItem("historyList", JSON.stringify(this.historyList));
      this.checkCurrency(item);
    },
    focusSearch() {
      this.showSearch = true;
      this.$emit("searchFun", this.searchValue);
    },
    cancelSearch() {
      this.showSearch = false;
      this.searchValue = "";
      this.$emit("searchFun", this.searchValue);
    },

    checkTab(index) {
      this.currentDeal = index;
      if (this.currentDeal == "my") {
        if (this.existToken) {
          this.getMyDataList();
        }
      }
    },
    // onInput() {
    //   this.$emit("searchFun", this.searchValue);
    // },
    CurrencySort(val) {
      this.sortIndex = val;
      this.$emit("CurrencySort", val);
    },
    collect(item) {
      console.log("item", item.isCollect);
      if (item.isCollect) {
        this.deleteCollectFun(item);
        item.collect = false;
      } else {
        this.collectFun(item);
        item.collect = true;
      }
    },
    //收藏
    collectFun(item) {
      this.$emit("collectFun", item);
    },

    //取消收藏
    deleteCollectFun(item) {
      this.$emit("deleteCollectFun", item);
    },
    //切换币种
    checkCurrency(item) {
      this.$emit("checkCurrency", item);
    },
    //获取收藏列表
    getSelectList() {
      this.isCollectShow = !this.isCollectShow;
      if (this.isCollectShow) {
        this.isUSD = false;
      }
      this.$emit("getSelectList", this.isCollectShow);
    },
    // 清空历史记录
    deleteAll() {
      this.historyList = [];
      localStorage.setItem("historyList", []);
    },
    getMyDataList() {
      let obj = {
        page_no: 1,
        type: "opponent",
        symbol: this.$route.params.id,
      };
      Axios.currencyTradeRecord(obj).then((res) => {
        if (res.code == 0) {
          this.myDataList = res.data;
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.color-down {
  color: #e05561;
}

.color-up {
  color: #62c885;
}

.active {
  color: #1d91ff !important;
}

.text-right {
  text-align: right;
}

.mr-3 {
  margin-right: 3px;
}

.makeADeal-wrapper {
  padding: 21px 0 0;
}

.search-wrapper {
  padding: 0 16px;
}

.search {
  position: relative;
  height: 24px;

  background: #2c3138;
  border-radius: 4px;
}

.search .search-img {
  position: absolute;
  top: 50%;
  left: 8px;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  width: 14px;
}

.search .search-img img {
  width: 100%;
}

.search input {
  width: 100%;
  height: 100%;
  padding-left: 31px;
  box-sizing: border-box;
  font-size: 12px;
  color: #fff;
}

.tabs-wrapper {
  display: flex;
  justify-content: space-between;
  padding: 0 16px;
  margin-top: 17px;
  font-size: 12px;
  color: #929aa5;
}

.tabs-wrapper > div {
  cursor: pointer;
}

.tabs-wrapper img {
  width: 14px;
}

:deep(.tabs-wrapper) .el-icon-arrow-right {
  margin-left: 13px;
  font-size: 16px;
}

.ticker-table-box {
  padding: 8px 16px;
  margin-top: 14px;
}

.ticker-table-box .title {
  display: flex;
  justify-content: space-between;
}

.ticker-table-box .title-item {
  display: flex;
  font-size: 12px;
  color: #929aa5;
}

.ticker-table-box .title-item:first-child {
  width: 40%;
}

.ticker-table-box .title-item .to-sort {
  display: flex;
  flex-direction: column;
  /*font-size: 8px;*/
}

:deep(.to-sort) .el-icon {
  width: 10px !important;
  height: 10px !important;
  .el-icon-caret-top,.el-icon-caret-bottom {
    width: 10px !important;
    height: 10px !important;
  }
  .el-icon-caret-top {
    margin-top: 2px;
  }

  .el-icon-caret-bottom {
    margin-top: -4px;
  }
}

.control {
  /*display: flex;*/
  /*align-items: center;*/
  margin-left: 3px;
}

.control img {
  position: relative;
  top: 3px;
  display: block;
  width: 14px;
}
.over-auto {
  overflow: auto;
}
.list-wrapper {
  /* height: 310px; */
  height: 390px;
  padding: 0 16px;
  border-bottom: 1px solid #24272c;
}

.list-wrapper,
.deal-item-wrapper {
  scrollbar-width: none;
  /* firefox */
  -ms-overflow-style: none;
  /* IE 10+ */
}

.list-wrapper::-webkit-scrollbar,
.deal-item-wrapper::-webkit-scrollbar {
  display: none;
  /* Chrome Safari */
}

.list-item {
  display: flex;
  margin-bottom: 10px;
  font-size: 12px;
}

.list-item > div {
  flex: 1;
}

.list-item img {
  width: 12px;
  height: 12px;
}

.list-item .symbol {
  margin-left: 5px;
}

.list-item .name {
  display: flex;
  align-items: center;
  cursor: pointer;
  img {
    max-width: 12px;
  }
}

.deal {
  padding: 19px 16px 0;
  /*border-bottom: 1px solid #24272C;*/
}

.deal-title {
  display: flex;
  margin-bottom: 14px;
}

.deal-title > div {
  /*flex: 1;*/
  font-size: 14px;
  /*text-align: center;*/
  color: #707a8a;
  cursor: pointer;
}

.deal-title > div:first-child {
  margin-right: 26px;
}

.deal-main-title {
  display: flex;
  margin-bottom: 7px;
  font-size: 12px;
  color: #929aa5;
}

.deal-main-title > div {
  flex: 1;
}

.deal-item-wrapper {
  height: 475px;
  overflow: auto;
}

.deal-item-wrapper .deal-item {
  display: flex;
  width: 100%;
  margin-bottom: 8px;
  font-size: 12px;
  color: #bfc4cb;
}

.deal-item-wrapper .deal-item > div {
  flex: 1;
}

.deal-item-wrapper .deal-item .price {
  color: #e05561;
}

.no-login {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  font-size: 12px;
  color: #929aa5;
}

.search-data {
  position: absolute;
  top: 37px;
  left: 0;
  background: #171a1e;
  z-index: 20;
  width: 100%;
  height: 390px;
  overflow: auto;
}

.xxx {
  width: 22px;
  height: 14px;
  text-align: center;
  line-height: 14px;
  background: #243046;
  border-radius: 2px;
  color: #1d91ff;
  font-size: 12px;
}

.color-red {
  color: #1d91ff;
}

.empty {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 206px;
  font-size: 14px;
  color: #929aa5;
}

.get_select {
  display: flex;
  flex: 1;
  align-items: center;
}
.tabactive {
  color: #1d91ff;
}

.get_select_img {
  margin-right: 5px;
}

.history-list {
  display: flex;
  padding: 0 15px;
}

.history-list ul {
  flex: 1;
  overflow: hidden;
}

.history-list ul li {
  padding: 4px 6px;
  background: rgb(43, 49, 57);
  border-radius: 4px;
  margin-right: 5px;
  margin-bottom: 5px;
  float: left;
  font-size: 12px;
}

.delelte-icon {
  font-size: 18px;
  position: relative;
  top: 2px;
}
</style>
