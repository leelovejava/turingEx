<!-- menu2入口 -->
<template>
  <div>
    <pc-section class="section1" innerclass="flex">
      <div class="flex1 pt-12 px-24">
        <h1 class="text-4xl mb-4">{{ t("m2-index-s1-t1") }}</h1>
        <h5 class="w-532">{{ t("m2-index-s1-d1") }}</h5>
      </div>
      <div class="flex1 text-left">
        <img
          src="@/assets/images/compositeHome/menu2/image0.png"
          width="665"
          height="296"
        />
      </div>
    </pc-section>
    <!-- 导航 -->
    <pc-section>
      <navigator></navigator>
    </pc-section>
    <!-- 可用工具 -->
    <pc-section class="px-48 py-12" innerclass="text-center">
      <h2 class="mb-4">{{ t("m2-index-s2-t1") }}</h2>
      <h5 class="mb-12">{{ t("m2-index-s2-d1") }}</h5>
      <div
        class="flex flex-wrap justify-center"
        style="width: 1000px; margin: auto"
      >
        <card-why
          v-for="(item, index) in list4"
          :index="index + 1"
          :key="index"
          :desc="`${t(`m2-index-s2-${index + 1}-d1`)}`"
          :title="`${t(`m2-index-s2-${index + 1}-t1`)}`"
          :imgPath="false"
        >
        </card-why>
      </div>
    </pc-section>
    <!-- 掉期、点差和佣金 -->
    <pc-section class="section3" innerclass="flex">
      <div class="flex1 text-right">
        <img
          src="@/assets/images/compositeHome/menu2/image1.png"
          width="711"
          height="408"
        />
      </div>
      <div class="flex flex-col justify-center pl-8">
        <h1 class="mb-4 text-white">{{ t("m2-index-s3-t1") }}</h1>
        <h5 class="mb-4 text-white">{{ t("m2-index-s3-d1") }}</h5>
        <div>
          <el-button class="gray-large-btn mt-4">{{
            t("m2-index-s3-b1")
          }}</el-button>
        </div>
      </div>
    </pc-section>
    <pc-section class="section4 py-20" innerclass="text-center">
      <h2 class="mb-4">{{ t("m2-index-s4-t1") }}</h2>
      <h5>{{ t("m2-index-s4-d1") }}</h5>
      <div class="flex justify-center mt-4">
        <div class="section4-w mr-12">
          <h3 class="mb-4">{{ t("m2-index-s4-1-t1") }}</h3>
          <h5>{{ t("m2-index-s4-1-d1") }}</h5>
        </div>
        <div class="section4-w">
          <h3 class="text-4xl mb-4">{{ t("m2-index-s4-2-t1") }}</h3>
          <h5>{{ t("m2-index-s4-2-d1") }}</h5>
        </div>
      </div>
      <el-button class="gray-large-btn mt-8">{{
        t("m2-index-s4-b1")
      }}</el-button>
    </pc-section>
    <pc-section class="section5 py-20" innerclass="flex justify-center">
      <div class="mr-12">
        <h2>{{ t("m2-index-s5-t1") }}</h2>
        <h5 class="w-460">{{ t("m2-index-s5-d1") }}</h5>
        <el-button class="white-middle-link-btn mt-8">{{
          t("learn-more-info")
        }}</el-button>
      </div>
      <img
        src="@/assets/images/compositeHome/menu2/image2.png"
        width="620"
        height="258"
      />
    </pc-section>

    <pc-section class="section6" innerclass="flex">
      <img
        src="@/assets/images/compositeHome/menu2/image3.png"
        width="620"
        height="322"
      />
      <div class="pl-16">
        <h2 class="mb-4 mt-12">{{ t("m2-index-s6-t1") }}</h2>
        <p>{{ t("m2-index-s6-d1") }}</p>
        <el-button class="gray-middle-btn mt-4">{{
          t("learn-more-info")
        }}</el-button>
      </div>
    </pc-section>
    <ready :needBg="true"></ready>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import pcSection from "@comCompositeHome/pcSection.vue";
import cardWhy from "@comCompositeHome/cardCommon.vue";
import ready from "@comCompositeHome/ready.vue";
import navigator from "@comCompositeHome/navigator.vue";
const list4 = Array(4);
const route = useRoute();
const { t } = useI18n();
</script>
<style lang="scss" scoped>
.section1,
.section4,
.section6 {
  background: #f2f4f6;
}

.section7 {
  background: #192989;
  height: 330px;
}

.section4-w {
  width: 320px;
}

.section3 {
  background: #192989;
}

// 公用
.w-460 {
  width: 460px;
}

.w-532 {
  width: 532px;
}
</style>
