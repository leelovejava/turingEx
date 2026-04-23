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
    <!-- 欢迎访问集团 -->
    <pc-section class="section1">
      <div class="w-600">
        <h2 class="mt-12">{{ t("g-s1-t1") }}</h2>
        <h2 class="my-8 fs-32">{{ t("g-s1-d1") }}</h2>
        <h5>{{ t("g-s1-d2") }}</h5>
      </div>
    </pc-section>
    <!-- 了解更多有关  集团的信息-->
    <pc-section class="py-12">
      <h2 class="text-center mb-8">{{ t("g-s2-t1") }}</h2>
      <div class="flex justify-center">
        <div class="w-820 grid grid-cols-2 gap-8">
          <div class="card p-8" v-for="(_, i) in list2" :key="i">
            <h5 class="text-left">{{ t(`g-s2-${i + 1}-t1`) }}</h5>
            <h5 class="text-base text-left">{{ t(`g-s2-${i + 1}-d1`) }}</h5>
          </div>
          <div class="card-wrapper" v-for="(_, i) in list2" :key="i">
            <img
              :src="
                getImageUrl(
                  `/src/assets/images/compositeHome/menu5/h${i + 1}.png`
                )
              "
              width="393"
              height="180"
            />

            <div class="p-8 text-left">
              <h5 class="fs-24">{{ t(`g-s3-${i + 1}-t1`) }}</h5>
              <h6 class="mb-4">{{ t(`g-s3-${i + 1}-d1`) }}</h6>
              <div class="link-middle-text cursor-pointer">
                {{ t("liaojiegengduao") }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </pc-section>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { gotoPage } from "@/utils/login";
import { getImageUrl } from "@/utils/index";

const router = useRouter();
const { t } = useI18n();

const goPage = (path) => {
  if (path) {
    router.push(path);
    return;
  }
  gotoPage();
};
const list2 = Array(2);
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

.w-600 {
  width: 600px;
}

.w-820 {
  width: 820px;
}

.card-wrapper {
  box-shadow: 0px 3px 10px 9px rgba(0, 0, 0, 0.05);
}

.section1 {
  background: url(@/assets/images/compositeHome/header/group/banner.png);
  background-size: 100% 100%;
  height: 438px;
}

.card {
  background: #ffffff;
  box-shadow: 0px 3px 10px 9px rgba(0, 0, 0, 0.05);
  border-top: 2px solid #2038d3;
}
</style>
