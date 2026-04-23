<template>
  <div class="m3-card">
    <div><img :src="getImageUrl(imgPath)" width="200" height="127" /></div>
    <h2 class="my-4 fs-24">{{ title }}</h2>
    <h5 class="mb-4">{{ desc }}</h5>
    <div class="link-middle-text cursor-pointer">
      {{ t("liaojiegengduao") }}
    </div>
  </div>
</template>
<script setup>
import { useI18n } from "vue-i18n";
import { getImageUrl } from "@/utils/index";
const { t } = useI18n();
const props = defineProps({
  desc: {
    type: String,
    default: "",
  },
  title: {
    type: String,
    default: "",
  },
  imgPath: {
    type: String,
    default: "",
  },
});
</script>
<style lang="scss" scoped>
.m3-card {
  text-align: center;
  width: 100%;
  padding: 24px;
}
</style>
