<template>
  <div class="ticker-top-box">
    <div class="ticker-item ml-41">
      <span
        :class="[
          'label',
          'fs-16',
          pageData?.change_ratio > 0 ? 'color-up' : 'color-down',
        ]"
        >{{ pageData?.close }}</span
      >
      <span class="value">${{ pageData?.close }}</span>
    </div>
    <div class="ticker-item ml-41">
      <span class="label">{{ t("message.home.24xiaoshizhangdie") }}</span
      ><span
        :class="[
          'value',
          pageData?.change_ratio > 0 ? 'color-up' : 'color-down',
        ]"
        >{{ pageData?.change_ratio > 0 ? "+" : ""
        }}{{ pageData?.change_ratio }}%</span
      >
    </div>
    <div class="ticker-item ml-41">
      <span class="label">24h{{ t("message.home.zuigao") }}</span>
      <span class="value">{{ pageData?.high }}</span>
    </div>
    <div class="ticker-item ml-41">
      <span class="label">24h{{ t("message.home.zuidi") }}</span>
      <span class="value">{{ pageData?.low }}</span>
    </div>
    <div class="ticker-item ml-41">
      <span class="label"
        >24h{{ t("message.home.liang") }}({{ getAmountUnit }})</span
      >

      <span class="value">{{ getAmount }} </span>
    </div>
    <div class="ticker-item ml-41">
      <span class="label"
        >24h{{ t("message.home.e") }}({{
          t(`${unitMap.volumn[pageType]}`)
        }})</span
      ><span class="value">{{ getVolume }}</span>
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
  pageType: {
    type: String,
    default: "etf",
  },
});

const unitMap = {
  amount: {
    etf: "message.home.wangu",
    usStocks: "message.home.wangu",
    twStocks: "message.home.wangu",
    cnStocks: "message.home.wangu",
    hkStocks: "message.home.wangu",
    coin: "",
    forex: "",
  }, //成交量，数字货币和外汇用当前币种
  volumn: {
    etf: "message.home.wan",
    usStocks: "message.home.wan",
    cnStocks: "message.home.wan",
    hkStocks: "message.home.wan",
    twStocks: "message.home.wan",
    forex: "USD",
    coin: "USDT",
  }, //成交额
};

const getAmountUnit = computed(() => {
  if (props.pageType === "coin") {
    return props.pageData?.symbol_data.toUpperCase();
  }
  if (["usStocks", "etf"].includes(props.pageType)) {
    return t(`${unitMap.amount[props.pageType]}`);
  }
  // etf
  return props.pageData?.symbol.toUpperCase();
});

const getVolume = computed(() => {
  const volume = props.pageData?.volume;

  if (volume) {
    const val = ["usStocks", "etf"].includes(props.pageType)
      ? volume / 10000
      : volume;
    return Number(val).toFixed(4);
  }
  return "--";
});

const getAmount = computed(() => {
  const amount = props.pageData?.amount;
  if (amount) {
    const val = ["usStocks", "etf"].includes(props.pageType)
      ? amount / 10000
      : amount;
    return Number(val).toFixed(4);
  }
  return "--";
});
</script>
<style>
/* TODO  */

.trade-header-box .ticker-xl-box .ticker-top-box .ml-41 {
  margin-left: 41px;
}
</style>
