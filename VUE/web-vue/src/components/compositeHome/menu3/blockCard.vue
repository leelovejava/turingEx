<template>
  <pc-section class="pt-8" :class="bg === 'gray' ? 'gray-bg' : 'white-bg'">
    <h2>{{ t(`m3-i-s2-${num}-t1`) }}</h2>
    <div class="flex justify-between">
      <div class="w-480">
        <h5>{{ t(`m3-i-s2-${num}-d1`) }}</h5>
        <card
          v-for="(item, i) in list"
          :key="i"
          :desc="t(`m3-i-s2-${num}-${i + 1}`)"
          :lineWidth="'480px'"
        ></card>
        <el-button v-if="needBtn" class="gray-large-btn my-8 fw-400">{{
          t("learn-more-info")
        }}</el-button>
      </div>
      <div>
        <img
          :src="
            getImageUrl(
              `/src/assets/images/compositeHome/menu3/home/h${num}.png`
            )
          "
          width="620"
          height="464"
        />
      </div>
    </div>
  </pc-section>
</template>
<script setup>
import { useI18n } from "vue-i18n";
import { getImageUrl } from "@/utils/index";
const { t } = useI18n();
console.log(11, `@/assets/images/compositeHome/menu3/home/h${props.num}.png`);
const props = defineProps({
  list: {
    type: Array,
    default: [],
  },
  bg: {
    type: String,
    default: "gray",
  },
  needBtn: {
    type: Boolean,
    default: true,
  },
  num: {
    type: Number,
    default: 1,
  },
});
</script>
<style lang="scss" scoped>
.white-bg {
  background: #fff;
}
.gray-bg {
  background: #f2f4f6;
}
.w-480 {
  width: 480px;
}

.fw-400 {
  font-weight: 400;
}
</style>
