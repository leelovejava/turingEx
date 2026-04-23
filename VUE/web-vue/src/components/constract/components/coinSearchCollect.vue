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
          @focus="showSearch = true"
          @blur="showSearch = false"
          @input="onInput"
          v-model="searchValue"
        />
      </div>
    </div>
    <!-- 标题 -->
    <div class="title-wrapper">
      <!-- 交易对 -->
      <div class="title-item">
        <span>{{ $t("message.home.jiaoyidui") }}</span>
        <div class="to-sort">
          <el-icon
            ><CaretTop
              class="el-icon-caret-top"
              :class="[sortIndex == 1 ? 'icon-active-color' : '']"
              @click="CurrencySort(1)"
          /></el-icon>
          <el-icon
            ><CaretBottom
              class="el-icon-caret-bottom"
              :class="[sortIndex == 2 ? 'icon-active-color' : '']"
              @click="CurrencySort(2)"
          /></el-icon>
        </div>
      </div>
      <!-- 最新价格 -->
      <div class="title-item">
        <span>{{ $t("message.home.jiage") }}</span>
        <div class="to-sort">
          <el-icon
            ><CaretTop
              class="el-icon-caret-top"
              :class="[sortIndex == 3 ? 'icon-active-color' : '']"
              @click="CurrencySort(3)"
          /></el-icon>
          <el-icon
            ><CaretBottom
              class="el-icon-caret-bottom"
              :class="[sortIndex == 4 ? 'icon-active-color' : '']"
              @click="CurrencySort(4)"
          /></el-icon>
        </div>
      </div>
      <!-- 24小时涨跌 -->
      <div class="title-item">
        <span>{{ $t("message.home.zhangdie") }}</span>
        <div class="to-sort">
          <el-icon
            ><CaretTop
              class="el-icon-caret-top"
              :class="[sortIndex == 5 ? 'icon-active-color' : '']"
              @click="CurrencySort(5)"
          /></el-icon>
          <el-icon
            ><CaretBottom
              class="el-icon-caret-bottom"
              :class="[sortIndex == 6 ? 'icon-active-color' : '']"
              @click="CurrencySort(6)"
          /></el-icon>
        </div>
      </div>
      <!-- 成交量 -->
      <div class="title-item" v-if="pageType !== 'forex'">
        <span>{{ $t("message.home.chengjiaoliang") }}</span>
        <div class="to-sort">
          <el-icon
            ><CaretTop
              class="el-icon-caret-top"
              :class="[sortIndex == 7 ? 'icon-active-color' : '']"
              @click="CurrencySort(7)"
          /></el-icon>
          <el-icon
            ><CaretBottom
              class="el-icon-caret-bottom"
              :class="[sortIndex == 8 ? 'icon-active-color' : '']"
              @click="CurrencySort(8)"
          /></el-icon>
        </div>
      </div>
    </div>
    <!-- 数据 -->
    <div class="list-wrapper">
      <div
        class="list-item"
        v-for="(item, index) in searchValue ? listData : AllListData"
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
          <!-- TODO 是都用symbol展示 -->
          <div class="symbol">{{ item.name }}</div>
        </div>
        <div class="price text-right">{{ item.close }}</div>
        <div
          class="ratio text-right"
          :class="[item.change_ratio > 0 ? 'color-up' : 'color-down']"
        >
          {{ item.change_ratio > 0 ? "+" : "" }}{{ item.change_ratio }}%
        </div>
        <div class="amount text-right" v-if="pageType !== 'forex'">
          {{ Number(item.amount || 0).toFixed(4) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapActions, mapStores } from "pinia";
import { CaretTop, CaretBottom } from "@element-plus/icons-vue";
import { useUserStore } from "@/store/user";

export default {
  emits: [
    "searchFun",
    "CurrencySort",
    "collectFun",
    "deleteCollectFun",
    "checkCurrency",
  ],
  name: "coinSearchCollect",
  props: ["listData", "AllListData", "pageType"],
  components: { CaretTop, CaretBottom },
  data() {
    return {
      showSearch: false,
      searchValue: "",
      sortIndex: 0,
    };
  },

  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  methods: {
    selectChcek(item, bool) {
      item.checked = bool;
    },
    onInput() {
      this.$emit("searchFun", this.searchValue);
    },
    CurrencySort(val) {
      this.sortIndex = val;
      this.$emit("CurrencySort", val);
    },
    collect(item) {
      if (item.isCollect) {
        this.deleteCollectFun(item);
      } else {
        this.collectFun(item);
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
    //切换币种以及路由
    checkCurrency(item) {
      this.$emit("checkCurrency", item);
    },
  },
};
</script>

<style scoped>
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

.title-wrapper {
  padding: 0 16px;
  margin-top: 14px;
  display: flex;
}

.title-wrapper .title-item {
  display: flex;
  justify-content: flex-end;
  font-size: 12px;
  color: #929aa5;
  flex: 1;
}

.title-wrapper .title-item:first-child {
  justify-content: flex-start;
}

.title-wrapper .title-item .to-sort {
  display: flex;
  flex-direction: column;
  /*font-size: 8px;*/
}

/* 排序的icon  */
:deep(.to-sort) .el-icon-caret-top,
:deep(.to-sort) .el-icon-caret-bottom {
  width: 12px !important;
  height: 12px !important;
}

:deep(.to-sort) .el-icon-caret-top {
  margin-top: -2px;
}

:deep(.to-sort) .el-icon-caret-bottom {
  margin-top: -10px;
}

.list-wrapper {
  height: 310px;
  padding: 0 16px;
  overflow: auto;
  border-bottom: 1px solid #24272c;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.list-wrapper::-webkit-scrollbar {
  display: none;
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
}

.icon-active-color {
  color: #1d91ff;
}
</style>
