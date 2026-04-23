<!-- 布局入口 -->
<template>
  <div class="app-box">
    <div class="box-view">
      <!-- left -->
      <div class="left-view">
        <div
          v-for="(it, i) in dingdanList"
          :key="i"
          class="menu-list"
          :class="selectPath == it.urlPath ? 'menu-active-color' : ''"
          @click="goRouter(it.urlPath)"
        >
          <div
            :class="selectPath == it.urlPath ? 'item-active-line' : ''"
          ></div>
          <img :src="it.iconPath" />
          <span>{{ $t(`message.user.${it.title}`) }}</span>
        </div>
      </div>
      <!-- right -->
      <div class="right-view">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { dingdanList } from "@/utils/menuConfig";
const router = useRouter();
const route = useRoute();
const selectPath = ref("");

const list = [
  {
    imgKey: "cryptos",
    label: "shuzihuobilishi",
    url: "/order/coinOrder",
  },
  {
    imgKey: "us",
    label: "meigulishi",
    url: "/order/usStocksOrder",
  },
  {
    imgKey: "forex",
    label: "waihuilishi",
    url: "/order/forexOrder",
  },
  {
    imgKey: "etf",
    label: "etflishi",
    url: "/order/etfOrder",
  },

  {
    imgKey: "financial-account",
    label: "licailishi",
    url: "/order/financialOrder",
  },
  {
    imgKey: "record",
    label: "zhangbianjilu",
    url: "/order/changeRecord",
  },
  {
    imgKey: "wallet-history",
    label: "qianbaolishi",
    url: "/order/walletHistory",
  },
  {
    imgKey: "exchange",
    label: "duihuanlishi",
    url: "/order/exchangeOrder",
  },
];

watch(
  () => route.path,
  (newPath, oldPath) => {
    selectPath.value = newPath;
  },
  { immediate: true }
);

const goRouter = (parmas) => {
  router.push(parmas);
};
</script>
<style lang="scss">
@import url("@/assets/css/order/index.scss");
</style>
