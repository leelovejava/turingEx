<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h1>{{ t("m1-who-s0-t1") }}</h1>
      <h5 class="w-500">{{ t("m1-who-s0-d1") }}</h5>
    </pc-section>
    <!-- menu-layout -->
    <menu-layout :collapseList="menu1List">
      <template v-slot:content>
        <!-- 我们的历史 -->
        <section class="flex">
          <div class="mr-12">
            <h2>{{ t("m1-who-s1-t1") }}</h2>
            <h5>{{ t("m1-who-s1-d1") }}</h5>
          </div>
          <div>
            <img
              src="@/assets/images/compositeHome/menu1/who/image1.png"
              width="413"
              height="435"
            />
          </div>
        </section>
        <!-- 赢得全球各地交易者的信赖 -->
        <section>
          <h2 class="mb-4">{{ t("m1-who-s2-t1") }}</h2>
          <div class="flex flex-wrap">
            <card
              v-for="(item, index) in whyList"
              :index="index + 1"
              :key="index"
              :desc="item.desc"
              :imgPath="item.imgPath"
            ></card>
          </div>
        </section>
        <!-- 屡获殊荣的经纪商 -->
        <section>
          <h2 class="mb-4">{{ t("m1-who-s3-t1") }}</h2>
          <div class="flex mb-8">
            <div class="mr-12">
              <h5 class="mb-4">{{ t("m1-who-s3-d1") }}</h5>
              <el-button
                class="gray-middle-btn"
                @click="gotoPage('/why-demo/awards')"
                >{{ t("m1-award-s4-b1") }}</el-button
              >
            </div>
            <img
              src="/src/assets/images/compositeHome/menu1/who/image2.png"
              width="410"
              height="224"
            />
          </div>
        </section>
        <!-- 还想了解更多吗？ -->
        <h2>{{ t("m1-who-s4-t1") }}</h2>
        <h5 v-html="t('m1-who-s4-d1')"></h5>
        <!-- 准备好了吗 -->
        <ready class="py-8"></ready>
        <h5 class="fs-14 my-16" v-html="t('m1-who-s6-d1')"></h5>
      </template>
    </menu-layout>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import card from "@comCompositeHome/cardCommon.vue";
import ready from "@comCompositeHome/ready.vue";
import { menu1List } from "@/utils/path";
const router = useRouter();
const { t } = useI18n();

const whyList = [
  {
    desc: t("m1-who-s2-d1"),
    imgPath: "/src/assets/images/compositeHome/menu1/who/icon1.png",
  },
  {
    desc: t("m1-who-s2-d2"),
    imgPath: "/src/assets/images/compositeHome/menu1/who/icon2.png",
  },
  {
    desc: t("m1-who-s2-d3"),
    imgPath: "/src/assets/images/compositeHome/menu1/who/icon3.png",
  },
];

const gotoPage = (path) => {
  router.push(path);
};
</script>
<style lang="scss" scoped>
.w-500 {
  width: 500px;
}
.fs-14 {
  font-size: 14px;
}
</style>
