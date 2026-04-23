<!-- 交易产品 -->
<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h2 class="mb-4">{{ t("m2-product-s1-t1") }}</h2>
      <h5>{{ t("m2-product-s1-d1") }}</h5>
    </pc-section>
    <!-- menu-layout -->
    <menu-layout :collapseList="menu2List">
      <template v-slot:content>
        <div v-for="(item, index) in list5" :key="index">
          <h2 class="mb-4">
            {{ t(`m2-product-s${index + 2}-t1`) }}
          </h2>
          <h5 class="mb-4">
            {{ t(`m2-product-s${index + 2}-d1`) }}
          </h5>
          <el-button class="gray-large-btn mb-8" @click="gotoPage(index)">{{
            t("learn-more-info")
          }}</el-button>
        </div>
        <ready></ready>
      </template>
    </menu-layout>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { menu2List } from "@/utils/path";
import ready from "@comCompositeHome/ready.vue";
const list5 = Array(5);
const router = useRouter();
const { t } = useI18n();

const gotoPage = (index) => {
  switch (index) {
    case 0:
      router.push("/trading/instruments/trade-forex");
      break;
    case 1:
      router.push("/trading/instruments/index-cfds");
      break;
    case 2:
      router.push("/trading/instruments/share-cfds");
      break;
    case 3:
      router.push("/trading/instruments/commodities");
      break;
    case 4:
      router.push("/trading/instruments/currency-index-cfds");
      break;
    default:
      break;
  }
};
</script>
<style lang="scss" scoped>
.test {
  font-size: 12px;
}
</style>
