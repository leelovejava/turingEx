<template>
  <div
    class="card-why px-12 pt-8 pb-12 mb-8"
    :class="needClassMR && [1, 3, 5, 7].includes(index) ? 'mr-12' : ''"
  >
    <img v-if="imgPath" :src="getImageUrl(imgPath)" width="40" height="40" />
    <h5 class="my-4 text-left black-color">{{ title }}</h5>
    <h5 class="text-base text-left black-color">{{ desc }}</h5>
  </div>
</template>
<script setup>
import { useI18n } from "vue-i18n";
import { getImageUrl } from "@/utils/index";
const { t } = useI18n();
const props = defineProps({
  title: {
    type: String,
    default: "",
  },
  desc: {
    type: String,
    default: "",
  },
  imgPath: {
    type: [String,Boolean],
    default: "/src/assets/images/compositeHome/menu1/award.png",
  },
  index: {
    type: Number,
    default: 1,
  },
  needClassMR: {
    type: Boolean,
    default: true,
  },
});
</script>
<style lang="scss" scoped>
.card-why {
  background: #ffffff;
  box-shadow: 0px 3px 10px 9px rgba(0, 0, 0, 0.05);
  width: 310px;
  // height: 374px;
  border-top: 2px solid #2038d3;
}
</style>
