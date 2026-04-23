<template>
  <div class="market-box">
    <div class="flexlayout__layout order-history-layout">
      <div
        class="flexlayout__tab tab-layout"
        style="
          left: 0;

          width: 100%;
          height: 100%;
          position: absolute;
          border-right: 1px solid rgb(36, 39, 44);
        "
      >
        <div class="order-book-box">
          <div class="order-book-wrap">
            <!-- 切换 -->
            <div class="order-book-head">
              <div class="position-wrap">
                <div
                  class="position-item"
                  v-for="(item, index) in graphicsList"
                  :key="index"
                  @click="quotesSort(index)"
                >
                  <img
                    v-if="!item"
                    :src="$getImages(`quotes/graphics-${index + 1}.png`)"
                    alt=""
                  />
                  <img
                    v-else
                    :src="$getImages(`quotes/graphics-${index + 1}-avtive.png`)"
                    alt=""
                  />
                </div>
              </div>
              <div class="ladder-wrap">
                <div class="okui-select text-select">
                  <div class="okui-select-value-box" style="display: flex">
                    <div
                      class="okui-select-text-value input-md align-right"
                      style="position: relative"
                      @click="handleShowLadder"
                    >
                      <div v-show="isValue" class="value-select">
                        <p
                          :class="{
                            'is-cycle': item === btValue,
                          }"
                          v-for="(item, index) in valueList"
                          :key="index"
                          @click="btValue.value = item"
                        >
                          {{ item }}
                        </p>
                      </div>

                      <div class="value" style="margin-right: 10px"></div>

                      <!-- <i class="el-icon-caret-bottom"></i> -->
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- 列表 -->
            <div class="order-book-body">
              <div class="title-wrap">
                <span class="price"
                  >{{ t("message.home.jiage") }}({{ unit }})</span
                >
                <!-- TODO单位 -->
                <span class="amount" style="text-align: center">{{
                  t("message.home.shuliang")
                }}</span>
                <span style="text-align: right">{{
                  t("message.home.chengjiaoe")
                }}</span>
              </div>
              <div class="book-body-wrap">
                <div
                  class="virtual-list"
                  style="
                    position: relative;
                    width: 100%;
                    will-change: transform;
                    direction: ltr;
                  "
                >
                  <ul style="height: auto; width: 100%">
                    <div
                      class="ticker-box"
                      v-if="quotesSortIndex == 1 || quotesSortIndex == 2"
                      style="height: 32px; width: 100%"
                    >
                      <div class="ticker-wrap">
                        <div
                          :class="[
                            'ticker-last-box',
                            pageData?.change_ratio > 0 || quotesSortIndex == 2
                              ? 'color-up'
                              : 'color-down',
                          ]"
                        >
                          <span class="ticker-price">{{ pageData?.close }}</span
                          ><i
                            :class="[
                              'icon',
                              'iconfont',
                              'icon-Mul_Arrowup',
                              pageData?.change_ratio > 0
                                ? 'icon-up'
                                : 'icon-down',
                            ]"
                          ></i
                          ><span class="local-price"
                            ><span class="current-local-price">{{
                              pageData?.close
                            }}</span></span
                          >
                        </div>
                      </div>
                    </div>
                    <div
                      class="top-hight-box-two"
                      v-if="quotesSortIndex == 0 || quotesSortIndex == 1"
                    >
                      <li
                        class="order-book-item asks"
                        style=""
                        v-for="(item, index) in filterDown"
                        :key="'a' + index"
                        @click="clickDownData(item)"
                      >
                        <span data-position="price" class="content price">
                          <em>{{ item.price }}</em>
                        </span>
                        <span
                          style="text-align: center"
                          data-position="amount"
                          class="content amount"
                          >{{ item.amount }}</span
                        >
                        <span
                          style="text-align: right"
                          class="content turnover"
                          >{{ (item.price * item.amount).toFixed(2) }}</span
                        >
                        <div
                          class="process-bar"
                          :style="
                            'transform:translateX(-' +
                            compuBar(item.amount) +
                            ')'
                          "
                        ></div>
                      </li>
                    </div>
                    <!-- 最低价展示 -->
                    <div
                      class="ticker-box"
                      v-if="quotesSortIndex == 0"
                      style="height: 32px; width: 100%"
                    >
                      <div class="ticker-wrap">
                        <div
                          :class="[
                            'ticker-last-box',
                            pageData?.change_ratio > 0
                              ? 'color-up'
                              : 'color-down',
                          ]"
                        >
                          <span class="ticker-price">{{ pageData?.close }}</span
                          ><i
                            :class="[
                              'icon',
                              'iconfont',
                              'icon-Mul_Arrowup',
                              pageData?.change_ratio > 0
                                ? 'icon-up'
                                : 'icon-down',
                            ]"
                          ></i
                          ><span class="local-price"
                            ><span class="current-local-price">{{
                              pageData?.close
                            }}</span></span
                          >
                        </div>
                      </div>
                    </div>
                    <!-- 挂单数据展示 -->
                    <div
                      class="top-hight-box"
                      v-if="quotesSortIndex == 0 || quotesSortIndex == 2"
                    >
                      <li
                        data-index="0"
                        class="order-book-item bids"
                        style="height: 20px"
                        v-for="(item, index) in filterTop"
                        :key="'x' + index"
                        @click="clickTopData(item)"
                      >
                        <span data-position="price" class="content price">
                          <em>{{ item.price }}</em>
                        </span>
                        <span
                          style="text-align: center"
                          data-position="amount"
                          class="content amount"
                          >{{ item.amount }}</span
                        >
                        <span
                          style="text-align: right"
                          class="content turnover"
                          >{{ (item.price * item.amount).toFixed(2) }}</span
                        >
                        <div
                          class="process-bar"
                          :style="
                            'transform:translateX(-' +
                            compuBar(item.amount) +
                            ')'
                          "
                        ></div>
                      </li>
                    </div>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { useI18n } from "vue-i18n";
const { t } = useI18n();
const props = defineProps({
  pageData: {
    type: Object,
    default: {},
  },

  changeClickData: {
    type: Function,
  },
  filterDown: {
    type: Array,
    default: [],
  },
  filterTop: {
    type: Array,
    default: [],
  },
  bigIndex: {
    type: [String, Number],
    default: "",
  },
  unit: {
    type: String,
  },
});

const graphicsList = ref([true, false, false]);
const quotesSortIndex = ref(0);
const isValue = ref(false);
const valueList = ["0.01", "0.1", "1", "10", "0.50", "100"]; //步长选择
const btValue = ref("0.01");

//行情排序

const quotesSort = (index) => {
  graphicsList.value.map((item, index) => {
    graphicsList.value[index] = false;
  });
  graphicsList.value[index] = true;
  quotesSortIndex.value = index;
};

const handleShowLadder = () => {
  isValue.value = !isValue.value;
};

const clickDownData = (val) => {
  changeClickData(val.price);
};
const clickTopData = (val) => {
  changeClickData(val.price);
};

const compuBar = (val) => {
  var data = val / props.bigIndex / 100;
  return data + Math.random() * 100 + "%";
};
</script>

<style>
.position-wrap img {
  display: block;
  cursor: pointer;
}

.position-wrap .position-item {
  flex: 1;
  width: 40px;
}

.top-hight-box {
  height: 410px;
}

.top-hight-box-two {
  height: 410px;
  margin-top: 10px;
}
</style>
