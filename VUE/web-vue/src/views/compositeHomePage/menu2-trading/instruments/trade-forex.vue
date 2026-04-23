<!-- 外汇 -->
<template>
  <menu-layout :collapseList="menu2List">
    <template v-slot:content>
      <section class="section1">
        <h2 class="mb-4">{{ t("m2-product-1-s1-t1") }}</h2>
        <h5>{{ t("m2-product-1-s1-d1") }}</h5>
      </section>
      <section class="pt-8">
        <h2>{{ t("m2-product-1-s2-t1") }}</h2>
        <card
          v-for="(item, index) in list4"
          :key="index"
          :desc="t(`m2-product-1-s2-d${index + 1}`)"
        ></card>
      </section>
      <section class="pt-8">
        <h2>{{ t("m2-product-1-s3-t1") }}</h2>

        <el-tabs v-model="activeName" class="demo-tabs">
          <el-tab-pane
            :label="t(`m2-product-1-s3-${i + 1}`)"
            :name="i"
            v-for="(_, i) in tabList"
            :key="i"
          >
            <el-table
              :data="tableData[i]"
              style="width: 100%"
              class="self-table"
            >
              <el-table-column
                v-for="(item, index) in tableList"
                :key="index"
                :prop="item.prop"
                :label="t(item.label)"
              />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </section>
      <section class="flex py-8">
        <img
          src="@/assets/images/compositeHome/menu2/image4.png"
          width="413"
          height="276"
        />
        <div class="ml-8">
          <h2>
            {{ t("m2-product-1-s4-t1") }}
          </h2>
          <el-button class="mb-8 gray-middle-btn">{{
            t("zhaodaogengduao")
          }}</el-button>
        </div>
      </section>
      <ready class="py-8"></ready>
    </template>
  </menu-layout>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import card from "@comCompositeHome/menu2/card.vue";
import { menu2List } from "@/utils/path";
import ready from "@comCompositeHome/ready.vue";
const route = useRoute();
const { t } = useI18n();

const list4 = Array(4);
const tabList = Array(4);
const activeName = ref(0);
const data1 = [
  {
    v1: "EURUSD",
    v2: "0",
    v3: "0.12",
    v4: "1",
    v5: "1.12",
  },
  {
    v1: "GBPUSD",
    v2: "0",
    v3: "0.69",
    v4: "1",
    v5: "1.69",
  },
  {
    v1: "USDJPY",
    v2: "0",
    v3: "0.47",
    v4: "1",
    v5: "1.47",
  },
  {
    v1: "AUDUSD",
    v2: "0",
    v3: "0.22",
    v4: "1",
    v5: "1.22",
  },
  {
    v1: "USDCAD",
    v2: "0.10",
    v3: "0.50",
    v4: "1.10",
    v5: "1.50",
  },
  ,
  {
    v1: "USDCHF",
    v2: "0",
    v3: "0.52",
    v4: "1",
    v5: "1.52",
  },
  {
    v1: "EURGBP",
    v2: "0.20",
    v3: "0.57",
    v4: "1.20",
    v5: "1.57",
  },
];
const data2 = [
  {
    v1: "CADCHF",
    v2: "0.80",
    v3: "1.57",
    v4: "1.80",
    v5: "2.57",
  },
  {
    v1: "CADJPY",
    v2: "0.20",
    v3: "0.85",
    v4: "1.20",
    v5: "1.85",
  },
  {
    v1: "CHFJPY",
    v2: "0.20",
    v3: "1.41",
    v4: "1.20",
    v5: "2.41",
  },
  {
    v1: "EURCAD",
    v2: "0.60",
    v3: "1.57",
    v4: "1.60",
    v5: "2.57",
  },
  {
    v1: "EURCHF",
    v2: "0.20",
    v3: "0.76",
    v4: "1.20",
    v5: "1.76",
  },
  {
    v1: "EURJPY",
    v2: "0.20",
    v3: "0.82",
    v4: "1.20",
    v5: "1.82",
  },
  {
    v1: "GBPCAD",
    v2: "0.80",
    v3: "2.28",
    v4: "1.80",
    v5: "3.28",
  },
  {
    v1: "GBPCHF",
    v2: "1",
    v3: "2.50",
    v4: "2",
    v5: "3.50",
  },
  {
    v1: "GBPJPY",
    v2: "0.60",
    v3: "1.90",
    v4: "1.60",
    v5: "2.90",
  },
];

const data3 = [
  {
    v1: `AUDCAD(${t("AUDCAD")}`,
    v2: "0.80",
    v3: "1.57",
    v4: "1.80",
    v5: "2.57",
  },
  {
    v1: `AUDCHF(${t("AUDCHF")}`,
    v2: "0.40",
    v3: "1.07",
    v4: "1.40",
    v5: "2.07",
  },
  {
    v1: `AUDJPY(${t("AUDJPY")}`,
    v2: "0",
    v3: "0.55",
    v4: "1",
    v5: "1.55",
  },
  {
    v1: `AUDNZD(${t("AUDNZD")}`,
    v2: "0.80",
    v3: "1.69",
    v4: "1.80",
    v5: "2.69",
  },
  {
    v1: `AUDSGD(${t("AUDSGD")}`,
    v2: "1.60",
    v3: "3.59",
    v4: "2.60",
    v5: "4.59",
  },
  {
    v1: `CHFSGD(${t("CHFSGD")}`,
    v2: "0",
    v3: "3.55",
    v4: "1",
    v5: "4.55",
  },
  {
    v1: `EURAUD(${t("EURAUD")}`,
    v2: "0",
    v3: "0.95",
    v4: "1",
    v5: "1.95",
  },
  {
    v1: `EURNOK(${t("EURNOK")}`,
    v2: "14",
    v3: "62.62",
    v4: "15",
    v5: "63.62",
  },
  {
    v1: `EURNZD(${t("EURNZD")}`,
    v2: "0.60",
    v3: "3.31",
    v4: "1.60",
    v5: "4.31",
  },

  {
    v1: `EURSEK(${t("EURSEK")}`,
    v2: "34.20",
    v3: "67.54",
    v4: "35.2",
    v5: "68.54",
  },
  {
    v1: `EURSGD(${t("EURSGD")}`,
    v2: "1.20",
    v3: "4.09",
    v4: "2.20",
    v5: "5.094",
  },
  {
    v1: `EURZAR(${t("EURZAR")}`,
    v2: "7.20",
    v3: "141.28",
    v4: "8.20",
    v5: "142.28",
  },
  {
    v1: `GBPAUD(${t("GBPAUD")}`,
    v2: "0",
    v3: "3.01",
    v4: "1",
    v5: "4.01",
  },
  {
    v1: `GBPNOK(${t("GBPNOK")}`,
    v2: "0",
    v3: "67.22",
    v4: "1",
    v5: "68.22",
  },
  {
    v1: `GBPNZD(${t("GBPNZD")}`,
    v2: "1.20",
    v3: "4.97",
    v4: "2.20",
    v5: "5.97",
  },
  {
    v1: `GBPSEK(${t("GBPSEK")}`,
    v2: "18",
    v3: "61.52",
    v4: "19",
    v5: "62.52",
  },
  {
    v1: `GBPSGD(${t("GBPSGD")}`,
    v2: "7.21",
    v3: "61.52",
    v4: "2.40",
    v5: "8.21",
  },
  {
    v1: `NOKSEK(${t("NOKSEK")}`,
    v2: "9.20",
    v3: "13.73",
    v4: "10.20",
    v5: "14.73",
  },
  {
    v1: `NZDCAD(${t("NZDCAD")}`,
    v2: "1.20",
    v3: "2.14",
    v4: "2.20",
    v5: "3.14",
  },
  {
    v1: `NZDCHF(${t("NZDCHF")}`,
    v2: "0.60",
    v3: "1.36",
    v4: "1.60",
    v5: "2.36",
  },
  {
    v1: `NZDJPY(${t("NOKJPY")}`,
    v2: "0.40",
    v3: "1.13",
    v4: "1.40",
    v5: "2.13",
  },
  {
    v1: `NZDUSD(${t("NZDUSD")}`,
    v2: "0.10",
    v3: "0.53",
    v4: "1.10",
    v5: "1.53",
  },
  {
    v1: `SGDJPY(${t("SGDJPY")}`,
    v2: "1",
    v3: "1.70",
    v4: "2",
    v5: "2.70",
  },
  {
    v1: `USDNOK(${t("USDNOK")}`,
    v2: "0.30",
    v3: "51.07",
    v4: "0.40",
    v5: "52.07",
  },
  {
    v1: `USDSEK(${t("USDSEK")}`,
    v2: "15.60",
    v3: "54.05",
    v4: "16.600",
    v5: "55.05",
  },
  {
    v1: `USDSGD(${t("USDSGD")}`,
    v2: "0",
    v3: "2.36",
    v4: "1",
    v5: "3.36",
  },
  {
    v1: `USDZAR(${t("USDZAR")}`,
    v2: "5",
    v3: "103.56",
    v4: "6",
    v5: "104.56",
  },
  {
    v1: `ZARJPY(${t("ZARJPY")}`,
    v2: "1",
    v3: "1.40",
    v4: "2",
    v5: "2.40",
  },
];

const tableData = [data1, data2, data3, data3];
const tableList = [
  {
    prop: "v1",
    label: "m2-product-1-s3-h1",
  },
  {
    prop: "v2",
    label: "m2-product-1-s3-h2",
  },
  {
    prop: "v3",
    label: "m2-product-1-s3-h3",
  },
  {
    prop: "v4",
    label: "m2-product-1-s3-h4",
  },
  {
    prop: "v5",
    label: "m2-product-1-s3-h5",
  },
];

const gotoPage = () => {};
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
