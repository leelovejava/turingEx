<template>
  <div>
    <pc-section class="gray-bg" innerclass="flex">
      <div class="flex1 pr-12 flex flex-col justify-center">
        <h1>{{ t("m1-why-s1-t1") }}</h1>
        <h5>{{ t("m1-why-s1-d1") }}</h5>
      </div>
      <div class="flex1 text-left">
        <img
          src="@/assets/images/compositeHome/menu1/why/image1.png"
          width="626"
          height="268"
        />
      </div>
    </pc-section>
    <!-- 我们是谁 -->
    <pc-section class="py-24" innerclass="flex justify-center">
      <div class="w-768">
        <h2 class="mb-8">{{ t("m1-why-s2-t1") }}</h2>
        <h5>{{ t("m1-why-s2-d1") }}</h5>
      </div>
    </pc-section>
    <!-- 与屡获殊荣的经纪人进行交易 -->
    <pc-section class="section3 text-white" innerclass="flex">
      <div class="flex1 pr-16 flex flex-col justify-center">
        <h2 class="text-white">{{ t("m1-why-s3-t1") }}</h2>
        <h5 class="text-white">{{ t("m1-why-s3-d1") }}</h5>
      </div>
      <div class="flex1 text-left">
        <img
          class="inline-block"
          src="@/assets/images/compositeHome/menu1/why/image2.png"
          width="626"
          height="315"
        />
      </div>
    </pc-section>
    <!-- 为什么 交易？ -->
    <pc-section class="py-28">
      <h1 class="text-4xl mb-20 text-center">
        {{ t("m1-why-s4-t1") }}
      </h1>
      <div class="grid grid-cols-3 gap-x-16 gap-y-8">
        <div class="blue-border" v-for="(item, index) in list9" :key="index">
          <img
            :src="getImageUrl(`${prePath}/icon${index + 1}.png`)"
            width="40"
            height="40"
          />
          <h5 class="fs-24 my-4 text-left">
            {{ t(`m1-why-s5-t${index + 1}`) }}
          </h5>
          <h5
            class="text-base text-left"
            v-html="t(`m1-why-s5-d${index + 1}`)"
          ></h5>
        </div>
      </div>
    </pc-section>
    <!-- 我们随时为您提供帮助 -->
    <pc-section class="gray-bg py-20" innerclass="flex justify-center">
      <div class="w-768 text-center">
        <h2 class="mb-12">{{ t("m1-why-s6-t1") }}</h2>
        <div class="grid grid-cols-2 gap-x-8">
          <div class="w-370 bg-white" v-for="(item, i) in list2" :key="i">
            <img
              alt=""
              :src="getImageUrl(`${prePath}/image${i + 3}.png`)"
              width="370"
              height="180"
            />
            <div class="px-12 text-left">
              <h4 class="my-4">{{ t(`m1-why-s6-${i + 1}-t1`) }}</h4>
              <h6>{{ t(`m1-why-s6-${i + 1}-d1`) }}</h6>
              <h5 class="my-12" v-html="t(`m1-why-s6-${i + 1}-l1`)"></h5>
            </div>
          </div>
        </div>
      </div>
    </pc-section>

    <!-- 做好交易准备了吗？ -->
    <ready-bg1></ready-bg1>
    <!-- 提示 -->
    <pc-section class="pt-24 pb-20 pl-32">
      <h5 class="text-left text-sm">{{ t("m1-why-s7-1") }}</h5>
      <h5 class="text-left text-sm r">{{ t("m1-why-s7-2") }}</h5>
      <h5 class="text-left text-sm">{{ t("m1-why-s7-3") }}</h5>
    </pc-section>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { getImageUrl } from "@/utils/index";
import readyBg1 from "@comCompositeHome/readyWithBg1.vue";
import router from "@/router";
const route = useRoute();
const { t } = useI18n();
const prePath = "/src/assets/images/compositeHome/menu1/why";

const list9 = Array(9);
const list2 = Array(2);
</script>
<style lang="scss" scoped>
.gray-bg {
  background: #f2f4f6;
}
.section3 {
  background: #142056;
}

.w-370 {
  width: 370px;
}

.w-768 {
  width: 768px;
}

// .card-why {
//   background: #ffffff;
//   box-shadow: 0px 3px 10px 9px rgba(0, 0, 0, 0.05);
//   width: 310px;
//   // height: 374px;
//   border-top: 2px solid #2038d3;
// }
</style>
