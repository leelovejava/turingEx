<!-- 简况 -->
<template>
  <div class="wrapper">
    <div class="baseinfo">{{ t("message.user.jibenxinxi") }}</div>
    <div class="list-item" v-for="(item, index) in data" :key="index">
      <div class="label">{{ t(`message.user.${item.label}`) }}</div>
      <div class="value">{{ item.value }}</div>
    </div>
  </div>
</template>

<script setup>
import Axios from "@/api/etf.js";
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { onMounted } from "vue";

const route = useRoute();
const { t } = useI18n();

const defaultData = [
  {
    label: "chengliri",
    key: "foundDate",
    value: "2021-08-03",
  },
  {
    label: "shangshiriqi",
    key: "listingDate",
    value: "2021-08-03",
  },
  {
    label: "jijinguimo",
    key: "fundSize",
    value: "2021-08-03",
  },
  {
    label: "jijinfene",
    key: "fundShares",
    value: "2021-08-03",
  },
  {
    label: "genzongzhishu",
    key: "indexTracking",
    value: "2021-08-03",
  },
  {
    label: "hangyegainian",
    key: "belongingConcept",
    value: "2021-08-03",
  },
  {
    label: "jijinjingli",
    key: "managingDirector",
    value: "2021-08-03",
  },
  {
    label: "jijingongsi",
    key: "orgName",
    value: "2021-08-03",
  },
  {
    label: "jijinleixing",
    key: "securityType",
    value: "2021-08-03",
  },
  {
    label: "fengxiandengji",
    key: "riskLevel",
    value: "2021-08-03",
  },
  {
    label: "yejibijiaojizhun",
    key: "performanceBenchmark",
    value: "2021-08-03",
  },
  {
    label: "touzileixing",
    key: "investmentType",
    value: "2021-08-03",
  },
  {
    label: "jiaoyifangshi",
    key: "tradingMethod",
    value: "2021-08-03",
  },
  {
    label: "jiaoyifeiyong",
    key: "transactionFee",
    value: "2021-08-03",
  },
  {
    label: "jiaoyiyongjin",
    key: "tradingCommission",
    value: "2021-08-03",
  },
];

const data = ref(defaultData);
onMounted(() => {
  getList();
});

const getList = async () => {
  const symbol = route.params?.id || "GlobalETF500";
  let res = await Axios.getSummary({ symbol });
  if (res.code == "0") {
    const showData = defaultData.map((it) => {
      it.value = res.data[it.key] || "--";
      return it;
    });
    data.value = showData;
  }
};
</script>

<style scoped>
.wrapper {
  padding: 24px;
}
.baseinfo {
  margin-bottom: 18px;
}
.list-item {
  display: flex;
  margin-bottom: 10px;
  font-size: 12px;
}
.label {
  color: #929aa5;
  font-size: 12px;
  width: 18%;
}
</style>
