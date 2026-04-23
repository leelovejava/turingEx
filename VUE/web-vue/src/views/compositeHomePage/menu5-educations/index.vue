<template>
  <div>
    <pc-section class="index-section1" innerclass="flex">
      <div class="flex flex-col justify-center">
        <h2>{{ t("m5-i-s0-t1") }}</h2>
        <h5>{{ t("m5-i-s0-d1") }}</h5>
      </div>
      <img
        src="@/assets/images/compositeHome/menu5/h0.png"
        width="626"
        height="296"
      />
    </pc-section>
    <!-- 导航 -->
    <pc-section>
      <navigator></navigator>
    </pc-section>
    <pc-section innerclass="text-center">
      <!-- 学习交易外汇 -->
      <div>
        <h2 class="mt-8">{{ t("m5-i-s1-t1") }}</h2>
        <h5 class="mb-8">{{ t("m5-i-s1-d1") }}</h5>
        <div class="grid grid-cols-3 gap-x-3">
          <div class="card-wrapper" v-for="(_, i) in list3" :key="i">
            <img
              :src="
                getImageUrl(
                  `/src/assets/images/compositeHome/menu5/h${i + 1}.png`
                )
              "
              width="404"
              height="180"
            />
            <div class="p-8 text-left">
              <span class="green-tag">{{ t("Beginner") }}</span>
              <h5 class="fs-24 fw-400 mb-4">{{ t(`m5-i-s1-${i + 1}-t1`) }}</h5>
              <h6 class="mb-4">{{ t(`m5-i-s1-${i + 1}-d1`) }}</h6>
              <div class="link-middle-text cursor-pointer">
                {{ t("m5-i-s1-b1") }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 查看全部 -->
      <el-button class="gray-middle-btn my-8 fw-400">{{
        t("m5-i-s2-b1")
      }}</el-button>
    </pc-section>
    <ready-bg></ready-bg>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import readyBg from "@comCompositeHome/readyWithBg.vue";
import navigator from "@comCompositeHome/navigator.vue";
import { getImageUrl } from "@/utils";

const route = useRoute();
const { t } = useI18n();
const list3 = Array(3);
</script>
<style lang="scss" scoped>
.green-line {
}
.green-tag {
  background: #9bdfce;
  border-radius: 2px;
  padding: 4px 12px;
  display: inline-block;
  margin-bottom: 24px;
}
.card-wrapper {
  box-shadow: 0px 2px 8px 4px rgba(0, 0, 0, 0.05);
}

.fw-400 {
  font-weight: 400;
}
</style>
