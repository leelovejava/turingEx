<!-- 学习外汇-什么是点值 -->
<template>
  <menu-layout :collapseList="menu5List">
    <template v-slot:content>
      <div>
        <div class="section1">
          <h2>{{ t("m5-wh-dz-s1-t1") }}</h2>
          <h5 v-html="t('m5-wh-dz-s1-d1')"></h5>
        </div>
        <h5 v-html="t('m5-wh-dz-s1-d2')"></h5>
        <h5 v-html="t('m5-wh-dz-s1-d3')"></h5>
        <h2>{{ t("m5-wh-dz-s2-t1") }}</h2>
        <h2 class="fs-32 mt-8">{{ t("m5-wh-dz-s2-t2") }}</h2>
        <el-table :data="tableData1" class="self-table">
          <el-table-column
            v-for="(item, i) in list4"
            :key="i"
            :prop="`v${i + 1}`"
            :label="t(`m5-wh-dz-s2-h${i + 1}`)"
          />
        </el-table>
        <h2 class="fs-32 mt-16">{{ t("m5-wh-dz-s3-t1") }}</h2>
        <el-table :data="tableData2" class="self-table">
          <el-table-column
            v-for="(item, i) in list4"
            :key="i"
            :prop="`v${i + 1}`"
            :label="t(`m5-wh-dz-s3-h${i + 1}`)"
          />
        </el-table>
        <h2 class="fs-32 mt-16">{{ t("m5-wh-dz-s4-t1") }}</h2>
        <el-table :data="tableData3" class="self-table">
          <el-table-column
            v-for="(item, i) in list2"
            :key="i"
            :prop="`v${i + 1}`"
            :label="t(`m5-wh-dz-s4-h${i + 1}`)"
          />
        </el-table>
      </div>
    </template>
  </menu-layout>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { menu5List } from "@/utils/path";

const route = useRoute();
const { t } = useI18n();
const list4 = Array(4);
const list2 = Array(2);
const tableData1 = [
  {
    v1: `AUDCAD(${t("AUDCAD")})`,
    v2: "10 CAD",
    v3: "1 CAD",
    v4: "0.10 CAD",
  },
  {
    v1: `AUDCHF(${t("AUDCHF")})`,
    v2: "10 CHF",
    v3: "1 CHF",
    v4: "0.10 CHF",
  },
  {
    v1: `AUDJPY(${t("AUDJPY")})`,
    v2: "1000 JPY",
    v3: "100 JPY",
    v4: "10 JPY",
  },
  {
    v1: `AUDNZD(${t("AUDNZD")})`,
    v2: "10 NZD",
    v3: "1 NZD",
    v4: "0.10 NZD",
  },
  {
    v1: `AUDSGD(${t("AUDSGD")})`,
    v2: "10 SGD",
    v3: "1 SGD",
    v4: "0.10 SGD",
  },
  {
    v1: `AUDUSD(${t("AUDUSD")})`,
    v2: "10 USD",
    v3: "1 USD",
    v4: "0.10 USD",
  },
  {
    v1: `CADCHF(${t("CADCHF")})`,
    v2: "10 CHF",
    v3: "1 CHF",
    v4: "0.10 CHF",
  },
  {
    v1: `CADJPY(${t("CADJPY")})`,
    v2: "1000 JPY",
    v3: "100 JPY",
    v4: "10 JPY",
  },
  {
    v1: `CHFJPY(${t("CHFJPY")})`,
    v2: "1000 JPY",
    v3: "100 JPY",
    v4: "10 JPY",
  },
  {
    v1: `CHFSGD(${t("CHFSGD")})`,
    v2: "10 SGD",
    v3: "1 SGD",
    v4: "0.10 SGD",
  },
  {
    v1: `EURAUD(${t("EURAUD")})`,
    v2: "10 AUD",
    v3: "1 AUD",
    v4: "0.10 AUD",
  },
  {
    v1: `EURCAD(${t("EURCAD")})`,
    v2: "10 CAD",
    v3: "1 CAD",
    v4: "0.10 CAD",
  },
  {
    v1: `EURCHF(${t("EURCHF")})`,
    v2: "10 CHF",
    v3: "1 CHF",
    v4: "0.10 CHF",
  },
  {
    v1: `EURCZK(${t("EURCZK")})`,
    v2: "10 CZK",
    v3: "1 CZK",
    v4: "0.10 CZK",
  },
  {
    v1: `EURGBP(${t("EURGBP")})`,
    v2: "10 GBP",
    v3: "1 GBP",
    v4: "0.10 GBP",
  },
  {
    v1: `EURJPY(${t("EURJPY")})`,
    v2: "1000 JPY",
    v3: "100 JPY",
    v4: "10 JPY",
  },
  {
    v1: `EURNOK(${t("EURNOK")})`,
    v2: "10 NOK",
    v3: "1 NOK",
    v4: "0.10 NOK",
  },
  {
    v1: `EURNZD(${t("EURNZD")})`,
    v2: "10 NZD",
    v3: "1 NZD",
    v4: "0.10 NZD",
  },
  {
    v1: `EURPLN(${t("EURPLN")})`,
    v2: "10 PLN",
    v3: "1 PLN",
    v4: "0.10 PLN",
  },
  {
    v1: `EURSEK(${t("EURSEK")})`,
    v2: "10 SEK",
    v3: "1 SEK",
    v4: "0.10 SEK",
  },
  {
    v1: `EURSGD(${t("EURSGD")})`,
    v2: "10 SGD",
    v3: "1 SGD",
    v4: "0.10 SGD",
  },
  {
    v1: `EURTRY(${t("EURTRY")})`,
    v2: "10 TRY",
    v3: "1 TRY",
    v4: "0.10 TRY",
  },
  {
    v1: `EURUSD(${t("EURUSD")})`,
    v2: "10 USD",
    v3: "1 USD",
    v4: "0.10 USD",
  },
  {
    v1: `EURZAR(${t("EURZAR")})`,
    v2: "10 ZAR",
    v3: "1 ZAR",
    v4: "0.10 ZAR",
  },
  {
    v1: `GBPAUD(${t("GBPAUD")})`,
    v2: "10 AUD",
    v3: "1 AUD",
    v4: "0.10 AUD",
  },
  {
    v1: `GPBCAD(${t("GPBCAD")})`,
    v2: "10 CAD",
    v3: "1 CAD",
    v4: "0.10 CAD",
  },
  {
    v1: `GPBCHF(${t("GPBCHF")})`,
    v2: "10 CHF",
    v3: "1 CHF",
    v4: "0.10 CHF",
  },
  {
    v1: `GPBJPY(${t("GPBJPY")})`,
    v2: "1000 JPY",
    v3: "100 JPY",
    v4: "10 JPY",
  },

  {
    v1: `GBPNOK(${t("GBPNOK")})`,
    v2: "10 NOK",
    v3: "1 NOK",
    v4: "0.10 NOK",
  },
  {
    v1: `GBPNZD(${t("GBPNZD")})`,
    v2: "10 NZD",
    v3: "1 NZD",
    v4: "0.10 NZD",
  },
  {
    v1: `GBPSEK(${t("GBPSEK")})`,
    v2: "10 SEK",
    v3: "1 SEK",
    v4: "0.10 SEK",
  },
  {
    v1: `GBPSGD(${t("GBPSGD")})`,
    v2: "10 SGD",
    v3: "1 SGD",
    v4: "0.10 SGD",
  },

  {
    v1: `GBPTRY(${t("GBPTRY")})`,
    v2: "10 TRY",
    v3: "1 TRY",
    v4: "0.10 TRY",
  },
  {
    v1: `GBPUSD(${t("GBPUSD")})`,
    v2: "10 USD",
    v3: "1 USD",
    v4: "0.10 USD",
  },
  {
    v1: `NOKJPY(${t("NOKJPY")})`,
    v2: "1000 JPY",
    v3: "100 JPY",
    v4: "10 JPY",
  },
  {
    v1: `NOKSEK(${t("NOKSEK")})`,
    v2: "10 SEK",
    v3: "1 SEK",
    v4: "0.10 SEK",
  },

  {
    v1: `NZDCAD(${t("NZDCAD")})`,
    v2: "10 CAD",
    v3: "1 CAD",
    v4: "0.10 CAD",
  },
  {
    v1: `NZDCHF(${t("NZDCHF")})`,
    v2: "10 CHF",
    v3: "1 CHF",
    v4: "0.10 CHF",
  },
  {
    v1: `NZDJPY(${t("NOKJPY")})`,
    v2: "1000 JPY",
    v3: "100 JPY",
    v4: "10 JPY",
  },
  {
    v1: `NZDUSD(${t("NZDUSD")})`,
    v2: "10 USD",
    v3: "1 USD",
    v4: "0.10 USD",
  },

  {
    v1: `SEKJPY(${t("SEKJPY")})`,
    v2: "1000 JPY",
    v3: "100 JPY",
    v4: "10 JPY",
  },
  {
    v1: `SGDJPY(${t("SGDJPY")})`,
    v2: "1000 JPY",
    v3: "100 JPY",
    v4: "10 JPY",
  },
  {
    v1: `USDCAD(${t("USDCAD")})`,
    v2: "10 CAD",
    v3: "1 CAD",
    v4: "0.10 CAD",
  },
  {
    v1: `USDCHF(${t("USDCHF")})`,
    v2: "10 CHF",
    v3: "1 CHF",
    v4: "0.10 CHF",
  },

  {
    v1: `USDCNH(${t("USDCNH")})`,
    v2: "10 CNH",
    v3: "1 CNH",
    v4: "0.10 CNH",
  },
  {
    v1: `USDCZK(${t("USDCZK")})`,
    v2: "10 CZK",
    v3: "1 CZK",
    v4: "0.10 CZK",
  },
  {
    v1: `USDJPY(${t("USDJPY")})`,
    v2: "1000 JPY",
    v3: "100 JPY",
    v4: "10 JPY",
  },
  {
    v1: `USDMXN(${t("USDMXN")})`,
    v2: "10 MXN",
    v3: "1 MXN",
    v4: "0.10 MXN",
  },
  {
    v1: `USDNOK(${t("USDNOK")})`,
    v2: "10 NOK",
    v3: "1 NOK",
    v4: "0.10 NOK",
  },
  {
    v1: `USDPLN(${t("USDPLN")})`,
    v2: "10 PLN",
    v3: "1 PLN",
    v4: "0.10 PLN",
  },
  {
    v1: `USDSEK(${t("USDSEK")})`,
    v2: "10 SEK",
    v3: "1 SEK",
    v4: "0.10 SEK",
  },
  {
    v1: `USDSGD(${t("USDSGD")})`,
    v2: "10 SGD",
    v3: "1 SGD",
    v4: "0.10 SGD",
  },

  {
    v1: `USDTHB(${t("USDTHB")})`,
    v2: "10 THB",
    v3: "1 THB",
    v4: "0.10 THB",
  },
  {
    v1: `USDTRY(${t("USDTRY")})`,
    v2: "10 TRY",
    v3: "1 TRY",
    v4: "0.10 TRY",
  },
  {
    v1: `USDZAR(${t("USDZAR")})`,
    v2: "10 ZAR",
    v3: "1 ZAR",
    v4: "0.10 ZAR",
  },
  {
    v1: `ZARJPY(${t("ZARJPY")})`,
    v2: "1000 JPY",
    v3: "100 JPY",
    v4: "10 JPY",
  },
];
const tableData2 = [
  {
    v1: "XTIUSD",
    v2: "10 USD",
    v3: "1 USD",
    v4: "0.10 USD",
  },
  {
    v1: "XBRUSD",
    v2: "10 USD",
    v3: "1 USD",
    v4: "0.10 USD",
  },
  {
    v1: "XAGUSD",
    v2: "50 USD",
    v3: "5 USD",
    v4: "0.50 USD",
  },
  {
    v1: "XAUUSD",
    v2: "10 USD",
    v3: "1 USD",
    v4: "0.10 USD",
  },
  {
    v1: "XAUEUR",
    v2: "10 EUR",
    v3: "1 EUR",
    v4: "0.10 EUR",
  },
  {
    v1: "XAGEUR",
    v2: "50 EUR",
    v3: "5 EUR",
    v4: "0.50 EUR",
  },
  {
    v1: "XNGUSD",
    v2: "10 USD",
    v3: "1 USD",
    v4: "0.10 USD",
  },
  {
    v1: "XPDUSD",
    v2: "10 USD",
    v3: "1 USD",
    v4: "0.10 USD",
  },
  {
    v1: "XPTUSD",
    v2: "10 USD",
    v3: "1 USD",
    v4: "0.10 USD",
  },
];
const tableData3 = [
  {
    v1: "US500",
    v2: "1 USD",
  },
  {
    v1: "US30",
    v2: "1 USD",
  },
  {
    v1: "NAS100",
    v2: "1 USD",
  },
  {
    v1: "AUS200",
    v2: "1 AUD",
  },
  {
    v1: "EUSTX50",
    v2: "1 EUR",
  },
  {
    v1: "GER30",
    v2: "1 EUR",
  },
  {
    v1: "FRA40",
    v2: "1 EUR",
  },
  {
    v1: "IT40",
    v2: "1 EUR",
  },
  {
    v1: "SPA35",
    v2: "1 EUR",
  },
  {
    v1: "UK100",
    v2: "1 GBP",
  },
  {
    v1: "JPN225",
    v2: "100 JPY",
  },
  {
    v1: "HK50",
    v2: "1 HKD",
  },
  {
    v1: "US2000",
    v2: "1 USD",
  },
  {
    v1: "CN500",
    v2: "1 USD",
  },
];
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
