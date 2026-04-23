<template>
  <div>
    <pc-section class="headerbg" innerclass=" flex items-center">
      <img
        class="mr-4"
        src="@/assets/images/compositeHome/logo.png"
        width="72"
        height="72"
        @click="gotoPage('/home')"
      />
      <div class="text-2xl text-white">{{ $title }}</div>
    </pc-section>
    <!--  在 TradingView 上进行交易 -->
    <pc-section class="section1">
      <div class="w-600">
        <h1 class="mt-12">{{ t("tv-s1-t1") }}</h1>
        <h5 class="mt-8 mb-12">{{ t("tv-s1-d1") }}</h5>
        <el-button class="blue-middle-btn" @click="gotoPage()">{{
          t("xianzaijiaoyi")
        }}</el-button>
      </div>
    </pc-section>
    <!-- 连接到 TradingView-->
    <pc-section class="py-20" innerclass="flex">
      <div class="section2-left mr-20">
        <el-tabs
          v-model="activeName"
          class="demo-tabs"
          @tab-click="handleClick"
        >
          <el-tab-pane
            :label="t('h-s2-t1')"
            :name="_index"
            v-for="(_item, _index) in list1"
            :key="_index"
          >
            <el-table :data="tableData" class="self-table">
              <el-table-column
                v-for="(item, index) in tableList"
                :key="index"
                :prop="item.prop"
                :label="t(`h-s2-h${index + 1}`)"
              />
              <el-table-column>
                <template #default="scope">
                  <el-button
                    class="blue-small-btn"
                    @click="handleTrade(scope.row)"
                    >{{ t("jiaoyi") }}</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
            <p class="text-gray-400">{{ t("h-s2-1-d1") }}</p>
          </el-tab-pane>
        </el-tabs>
      </div>
      <div class="section2-content">
        <h5>{{ t("tv-s2-d2") }}</h5>
        <h2>{{ t("tv-s2-t1") }}</h2>
        <h5 class="w-300 my-4">{{ t("tv-s2-d1") }}</h5>
        <el-button
          class="gray-middle-btn"
          @click="gotoPage('/trading/spreads-swaps-commissions')"
        >
          {{ t("xianzaijiaoyi") }}
        </el-button>
      </div>
    </pc-section>
    <pc-section>
      <!-- 为什么选择 -->
      <h2 class="text-center mb-8">{{ t("tv-s3-t1") }}</h2>
      <div class="grid grid-cols-4 gap-x-8">
        <div
          v-for="(item, i) in list4"
          :key="i"
          class="flex flex-col justify-center items-center"
        >
          <img
            :src="
              getImageUrl(
                `/src/assets/images/compositeHome/home/tradingview/icon${
                  i + 1
                }.png`
              )
            "
            width="32"
            height="32"
          />
          <h2 class="fs-24 my-2">{{ t(`tv-s3-${i + 1}-t1`) }}</h2>
          <h6>{{ t(`tv-s3-${i + 1}-d1`) }}</h6>
        </div>
      </div>

      <!-- 做好交易准备了吗？ -->
      <div class="flex mt-20">
        <div class="blackbg">
          <h1 class="text-white">{{ t("ready-t1") }}</h1>
          <h5 class="text-white">{{ t("ready-d3") }}</h5>
          <el-button class="blue-middle-btn mt-6" @click="gotoPage()">{{
            t("xianzaijiaoyi")
          }}</el-button>
        </div>
        <div class="readybg"></div>
      </div>
      <!-- TradingView FAQs -->
      <h2 class="text-center my-8">{{ t("tv-s4-t1") }}</h2>
      <div class="grid grid-cols-3 grid-x-12">
        <common-card
          v-for="(item, i) in list8"
          :key="i"
          :title="t(`tv-s4-${i + 1}-t1`)"
          :desc="t(`tv-s4-${i + 1}-d1`)"
          :imgPath="false"
        ></common-card>
      </div>
    </pc-section>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import commonCard from "@comCompositeHome/cardCommon.vue";
import { getImageUrl } from "@/utils/index";

const router = useRouter();
const { t } = useI18n();
const activeName = ref(0);
const list1 = Array(1);
const tableList = [
  {
    prop: "name",
  },
  {
    prop: "buyPrice",
  },
  {
    prop: "sellPrice",
  },
  {
    prop: "change",
  },
];
const tableData = [
  {
    name: "HK50",
    buyPrice: "20224.9",
    sellPrice: "23769.1",
    change: "20.00",
  },
  {
    name: "HK50",
    buyPrice: "20224.9",
    sellPrice: "23769.1",
    change: "20.00",
  },
  {
    name: "HK50",
    buyPrice: "20224.9",
    sellPrice: "23769.1",
    change: "20.00",
  },
  {
    name: "HK50",
    buyPrice: "20224.9",
    sellPrice: "23769.1",
    change: "20.00",
  },
];
const handleClick = (tab, e) => {};
const handleTrade = (row) => {};
const list8 = Array(8);
const list4 = Array(4);
const gotoPage = (path) => {
  router.push(path);
};
</script>

<style lang="scss" scoped>
.w-300 {
  width: 500px;
}

.w-600 {
  width: 600px;
}

//覆盖global.scss，home-tab单独样式
.demo-tabs :deep {
  .el-tabs__content {
    box-shadow: none;
    border-radius: 0;
  }

  .el-tabs__header {
    background: #fff;
    border-radius: 10px 10px 0px 0px;
    margin: 0;
  }
}

// home-table样式
.self-table {
  box-shadow: none;
  border-radius: 0;
}

.section1 {
  background: url(@/assets/images/compositeHome/home/tradingview/banner1.png);
  background-size: 100%;
  height: 430px;
}

.section2 {
  &-left {
    filter: drop-shadow(0px 0px 15px rgba(0, 0, 0, 0.1));
    width: 650px;

    p {
      background: #f6f7f8;
      border-radius: 0px 0px 10px 10px;
      padding: 18px 32px;
    }
  }

  &-content {
    max-width: 650px;
  }
}

.blackbg {
  background: black;
  height: 468px;
  width: 500px;
  padding: 80px 40px;
}

.headerbg {
  background: black;
  // height: 100px;
  padding: 18px 0 16px 0;
}

.readybg {
  background: url(@/assets/images/compositeHome/home/tradingview/image1.png)
    no-repeat center;
  background-size: auto 100%;
  height: 468px;
  width: 800px;
}
</style>
