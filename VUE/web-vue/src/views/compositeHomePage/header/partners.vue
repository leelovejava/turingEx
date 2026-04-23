<template>
  <div>
    <pc-section class="headerbg" innerclass="flex items-center justify-between">
      <div class="flex items-center">
        <img
          class="mr-4"
          src="@/assets/images/compositeHome/logo.png"
          width="72"
          height="72"
          @click="goPage('/home')"
        />
        <h2 class="fs-32">{{ $title }}</h2>
      </div>
      <!-- 右边菜单 -->
      <div class="flex">
        <h5
          class="mr-8 cursor-pointer"
          v-for="(_, i) in list5"
          :key="i"
          @click="goPage(_.url)"
        >
          {{ _.label }}
        </h5>
      </div>
    </pc-section>
    <div class="flex">
      <img
        :src="getImageUrl(`${prefix}/image1.png`)"
        width="700"
        height="390"
      />

      <div class="gray-bg p-12">
        <h1>{{ t("p-s0-t1") }}</h1>
        <h5 class="mt-6">{{ t("p-s0-d1") }}</h5>
      </div>
    </div>
    <pc-section class="section2" innerclass="grid grid-cols-4">
      <div v-for="(item, i) in list4" :key="i">
        <h5 class="text-white mt-6">{{ t(`p-s0-${i + 1}-t1`) }}</h5>
        <h5 class="text-white">{{ t(`p-s0-${i + 1}-d1`) }}</h5>
      </div>
    </pc-section>
    <!-- 我们的合作伙伴计划 -->
    <pc-section innerclass="text-center pt-12">
      <h5 class="fs-32">{{ t("p-s1-t1") }}</h5>
      <h5>{{ t("p-s1-d1") }}</h5>
      <div class="grid grid-cols-3 gap-x-6">
        <div class="mt-8 shadow-wrapper" v-for="(_, i) in list3" :key="i">
          <div class="img-wrapper">
            <img :src="getImageUrl(`${prefix}/image${i + 1}.png`)" />
          </div>
          <div class="p-8">
            <h5 class="text-left font-normal text-xl">
              {{ t(`p-s1-${i + 1}-t1`) }}
            </h5>
            <h5 class="text-left mt-4">{{ t(`p-s1-${i + 1}-d1`) }}</h5>
          </div>
        </div>
      </div>
    </pc-section>

    <!-- 伙伴关系利益 -->
    <pc-section class="bd-gray py-12">
      <div class="text-center">{{ t("p-s2-t1") }}</div>
      <h2 class="text-center">{{ t("p-s2-d1") }}</h2>
      <div class="grid grid-cols-3 gap-x-4 mt-12">
        <div class="blue-border my-8" v-for="(_, i) in list7" :key="i">
          <img
            :src="getImageUrl(`${prefix}/icon${i + 1}.png`)"
            width="65"
            height="65"
          />
          <h5>{{ t(`p-s2-${i + 1}`) }}</h5>
        </div>
      </div>
    </pc-section>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { getImageUrl } from "@/utils/index";
import { gotoPage } from "@/utils/login";
const router = useRouter();
const { t } = useI18n();

const list7 = Array(7);
const list3 = Array(3);
const list4 = Array(4);
const prefix = "/src/assets/images/compositeHome/header/partners";
const goPage = (path) => {
  if (path) {
    router.push(path);
    return;
  }
  gotoPage();
};
const list5 = [
  {
    label: t("m1-1"), //我们是谁
    url: "/why-demo/about",
  },
  {
    label: t("m3-1"), //交易平台
    url: "",
  },
  {
    label: t("footer-s1-4-t1"), //联系我们
    url: "/contact-us",
  },
  // {
  //   label: t("m3-1"), //新闻与媒体
  //   url: "",
  // },
  // {
  //   label: t("footer-s1-4-t1"), //联系我们
  //   url: "",
  // },
];
</script>

<style lang="scss" scoped>
.headerbg {
  padding: 18px 0 16px 0;
}

.w-300 {
  width: 500px;
}
.gray-bg {
  background: #e1e1e1;
}

.section2 {
  background: #2032dc;
  height: 108px;
}
.shadow-wrapper {
  background: #ffffff;
  box-shadow: 0px 2px 9px 6px rgba(0, 0, 0, 0.04);
}
.img-wrapper {
  width: 100%;
  height: 200px;
  img {
    width: 100%;
    height: 100%;
  }
}
</style>
