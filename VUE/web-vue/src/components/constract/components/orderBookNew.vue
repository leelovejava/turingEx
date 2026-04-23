<template>
  <div class="market-box">
    <div class="order-book-title">
      {{ $t("message.home.dingdanbu") }}
    </div>

    <div class="order-book-box">
      <div class="order-book-wrap">
        <div class="order-book-head">
          <div class="position-wrap">
            <div
              class="position-item"
              v-for="(item, index) in graphicsList"
              :key="index"
              @click="positionChange(index)"
            >
              <img
                v-if="!item.check"
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
        </div>
        <!-- 下单簿主体 -->
        <div class="order-book-body">
          <!-- 标题 -->
          <div v-if="pageType === 'usStocks'" class="title-wrap">
            <span class="price">{{ $t("message.jiaoyi.jiaoyishijian") }}</span>
            <span class="price"
              >{{ $t("message.home.jiage") }}({{ unit }})</span
            >
            <span class="amount">{{ $t("message.home.shuliang") }}</span>
          </div>
          <div v-else class="title-wrap">
            <span class="price"
              >{{ $t("message.home.jiage") }}({{ unit }})</span
            >
            <!-- TODO 单位当前币种 -->
            <span class="amount">{{ $t("message.home.shuliang") }}</span>
            <span class="amount">{{ $t("message.jiaoyi.chengjiaoe") }}</span>
          </div>
          <div class="book-body-wrap">
            <!-- 下单簿列表 -->
            <div
              class="virtual-list"
              style="
                width: 100%;
                overflow: auto;
                will-change: transform;
                direction: ltr;
              "
            >
              <ul style="height: auto; width: 100%">
                <!-- sell -->
                <div
                  v-if="orderBookType !== 'buy'"
                  :style="
                    orderBookType === 'sell' ? 'height: 470px' : 'height: 220px'
                  "
                >
                  <div
                    v-if="!graphicsList[0].check"
                    class="ticker-box"
                    style="height: 32px; width: 100%"
                  >
                    <div class="ticker-wrap">
                      <div
                        :class="[
                          'ticker-last-box',
                          pageData.change_ratio > 0 ? 'color-up' : 'color-down',
                        ]"
                      >
                        <span class="ticker-price">{{ pageData.close }}</span>
                        <span style="padding-left: 20px" class="local-price"
                          ><span class="current-local-price">{{
                            pageData.close
                          }}</span></span
                        >
                      </div>
                    </div>
                  </div>
                  <li
                    class="order-book-item asks"
                    style=""
                    v-for="(item, index) in orderBookType === 'sell'
                      ? sellList.slice(0, 20)
                      : sellList.slice(0, 10)"
                    :key="'a' + index"
                    @click="changeClickData(item)"
                  >
                    <span data-position="price" class="content price"
                      ><em>{{
                        pageType === "usStocks"
                          ? item?.current_time
                          : item.price
                      }}</em></span
                    ><span data-position="amount" class="content amount"
                      >{{ pageType === "usStocks" ? item.price : item.amount }}
                    </span>
                    <span data-position="amount" class="content amount">
                      {{
                        pageType === "usStocks"
                          ? item.amount
                          : (item.amount * item.price).toFixed(2)
                      }}
                    </span>
                    <!-- 进度 -->
                    <div
                      class="process-bar"
                      :style="{
                        transform: isCloseTrade
                          ? ''
                          : 'translateX(-' + compuBar(item.amount) + ')',
                      }"
                    ></div>
                  </li>
                </div>
                <!-- buy -->
                <div
                  v-if="orderBookType !== 'sell'"
                  :style="
                    orderBookType === 'buy' ? 'height: 470px' : 'height: 250px'
                  "
                >
                  <div class="ticker-box" style="height: 32px; width: 100%">
                    <div class="ticker-wrap">
                      <div
                        :class="[
                          'ticker-last-box',
                          pageData.change_ratio > 0 ? 'color-up' : 'color-down',
                        ]"
                      >
                        <span class="ticker-price">{{ pageData.close }}</span>
                        <span style="padding-left: 20px" class="local-price"
                          ><span class="current-local-price">{{
                            pageData.close
                          }}</span></span
                        >
                      </div>
                    </div>
                  </div>
                  <li
                    class="order-book-item bids"
                    v-for="(item, index) in orderBookType === 'buy'
                      ? buyList.slice(0, 20)
                      : buyList.slice(0, 10)"
                    :key="'x' + index"
                    @click="changeClickData(item)"
                  >
                    <span data-position="price" class="content price"
                      ><em>{{
                        pageType === "usStocks"
                          ? item?.current_time
                          : item.price
                      }}</em></span
                    ><span data-position="amount" class="content amount"
                      >{{ pageType === "usStocks" ? item.price : item.amount }}
                    </span>
                    <span data-position="amount" class="content amount">
                      {{
                        pageType === "usStocks"
                          ? item.amount
                          : (item.amount * item.price).toFixed(2)
                      }}
                    </span>

                    <div
                      class="process-bar"
                      :style="{
                        transform: isCloseTrade
                          ? ''
                          : 'translateX(-' + compuBar(item.amount) + ')',
                      }"
                    ></div>
                  </li>
                </div>
                <!--  最新成交 -->
                <div class="new-order-price ticker-wrap">
                  {{ $t("message.jiaoyi.zuixinchengjiao") }}
                </div>
                <div class="title-wrap">
                  <span class="price recent-header"
                    >{{ $t("message.home.jiage") }}({{ unit }})</span
                  >
                  <!-- TODO 单位当前币种 -->
                  <span class="amount recent-header">{{
                    $t("message.home.shuliang")
                  }}</span>
                  <span class="amount recent-header">{{
                    $t("message.home.kaicangshijian")
                  }}</span>
                </div>
                <div style="height: 150px; overflow-y: scroll">
                  <li
                    class="order-book-item asks"
                    v-for="(item, index) in recentList"
                    :key="'a' + index"
                    @click="changeClickData(item)"
                  >
                    <span
                      data-position="price"
                      class="content"
                      :class="item.direction == 'sell' ? 'active' : 'actives'"
                      ><em>{{ item.price }}</em></span
                    >
                    <span data-position="amount" class="content amount"
                      >{{ item.amount }}
                    </span>

                    <span data-position="amount" class="content amount"
                      >{{ item.current_time }}
                    </span>
                  </li>
                </div>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { onMounted } from "vue";
import bus from "vue3-eventbus";
const isCloseTrade = ref(false); //表示已经休市
const props = defineProps({
  pageData: {
    type: Object,
    default: {},
  },
  pageType: {
    type: String,
  },
  changeClickData: {
    type: Function,
  },
  sellList: {
    type: Array,
    default: [], //卖
  },
  buyList: {
    type: Array, //买
    default: [],
  },
  bigIndex: {
    type: [String, Number],
    default: "",
  },
  unit: {
    type: String,
  },
  recentList: {
    type: Array,
    default: [],
  }, // 最近成交数据
});
const graphicsList = ref([
  { value: "both", check: true },
  { value: "sell", check: false },
  { value: "buy", check: false },
]);

onMounted(() => {
  bus.on("closeTrade", (val) => {
    isCloseTrade.value = val;
  });
});
const orderBookType = ref("both");
//行情排序
const positionChange = (index) => {
  graphicsList.value.map((item) => {
    item.check = false;
  });
  graphicsList.value[index].check = true;
  orderBookType.value = graphicsList.value[index].value;
};

const compuBar = (val) => {
  var data = val / props.bigIndex / 100;
  return data + Math.random() * 100 + "%";
};
</script>

<style scoped>
.position-item img {
  width: 20px !important;
  height: 20px !important;
  margin-right: 15px !important;
}
.ticker-last-box {
  margin-right: 10px;
}

.ticker-wrap .mone {
  padding-right: 15px;
  font-size: 12px;
  color: #707a8a;
}

.order-book-head .bottom {
  background-position: -171px 0;
}

.order-book-head .ladder-wrap {
  line-height: 16px;
}
.order-book-title {
  font-size: 18px;
  font-weight: 600;
  padding: 20px 16px;
}

.order-book-item {
  height: 22px !important;
}

.order-book-item span.active {
  color: #0db27c !important;
}

.order-book-item span.actives {
  color: #db4355 !important;
}

.order-book-body .title-wrap .recent-header {
  margin: 12px 0 8px 0;
}
</style>
