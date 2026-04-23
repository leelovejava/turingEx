<template>
  <div class="quotes-state">
    <div class="left">
      <div class="signal">
        <img src="@/assets/images/quotes/Group3036.png" alt="" />
        <!-- <span>{{ $t("message.home.xitonglianjiewending") }}</span> -->
      </div>
      <div class="line"></div>
      <div class="control">
        <img src="@/assets/images/quotes/Group2889.png" alt="" />
      </div>
      <div v-for="(item, index) in symbol" :key="index" class="symbol-item">
        <span class="name">{{ item.name }}</span>
        <span
          class="ratio"
          :class="[item.change_ratio > 0 ? 'color-up' : 'color-down']"
          >{{ item.change_ratio }}</span
        >
        <span class="price">{{ item.price }}</span>
      </div>
    </div>
    <div class="right">
      <div class="announcement">
        <img src="@/assets/images/quotes/heroicons-solid_bell.png" alt="" />
        <span>{{ $t("message.home.announcement") }}</span>
      </div>
      <div class="download">
        <img src="@/assets/images/quotes/fa6-solid_download.png" alt="" />
        <span>{{ $t("message.home.download") }}</span>
      </div>
      <div>
        <img src="@/assets/images/quotes/bi_chat-right-dots-fill.png" alt="" />
        <span>{{ $t("message.home.service") }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "index",
  data() {
    return {
      symbol: [
        { name: "ETHUSDT", change_ratio: "-1.29", price: "1230.82" },
        { name: "BTCUSDT", change_ratio: "+1.13", price: "1230.82" },
        { name: "ETHUSDT", change_ratio: "-1.29", price: "1230.82" },
        { name: "BTCUSDT", change_ratio: "+1.13", price: "1230.82" },
        { name: "ETHUSDT", change_ratio: "-1.29", price: "1230.82" },
        { name: "BTCUSDT", change_ratio: "+1.13", price: "1230.82" },
      ],
    };
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

.quotes-state {
  display: flex;
  justify-content: space-between;
  height: 28px;
  padding: 2px 20px;
  background: #171a1e;
  border-top: 1px solid #24272c;
  color: #fff;
}

.line {
  width: 1px;
  height: 40%;
  margin: 0 16px;
  background: #484d56;
}

.quotes-state > div {
  display: flex;
  align-items: center;
}

.signal {
  display: flex;
  align-items: center;
  padding-right: 15px;
}

.signal img {
  width: 14px;
  margin-right: 13px;
}

.control {
  width: 12px;
  margin-right: 13px;
}

.symbol-item {
  display: flex;
  margin-right: 26px;
  font-size: 12px;
  color: #b7bdc6;
}

.symbol-item .ratio {
  margin: 0 6px;
}

.symbol-item .price {
  position: relative;
  color: #929aa5;
}

.symbol-item .price:after {
  position: absolute;
  top: 50%;
  right: -13px;
  transform: translateY(-50%);
  content: " ";
  height: 60%;
  border-right: 1px solid #484d56;
}

.quotes-state .right {
  display: flex;
  font-size: 12px;
}

.quotes-state .right > div {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 115px;
  cursor: pointer;
}

.quotes-state .right img {
  width: 14px;
  margin-right: 13px;
}

.quotes-state .right .download {
  border-left: 1px solid #24272c;
  border-right: 1px solid #24272c;
}

.quotes-state .right .announcement {
  border-left: 1px solid #24272c;
}
</style>
