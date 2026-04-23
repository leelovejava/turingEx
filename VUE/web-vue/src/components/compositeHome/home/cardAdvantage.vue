<template>
  <div class="card-advantage flex justify-between relative">
    <div class="text-left pl-16 py-12">
      <h2 class="text-2xl text-black mb-4">{{ title }}</h2>
      <div class="text-gray-400 mb-12">{{ desc }}</div>
      <div class="btn">
        <span>{{ t("liaojiegengduao") }}</span>
        <el-icon class="right-icon"><Right /></el-icon>
      </div>
    </div>
    <div>
      <img v-lazy="getImageUrl(imgPath)" width="300" height="270" />
    </div>
  </div>
</template>
<script setup>
import { useI18n } from "vue-i18n";
import { getImageUrl } from "@/utils/index";
import { Right } from "@element-plus/icons-vue";
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
    type: String,
    default: "",
  },
});
</script>
<style lang="scss" scoped>
.card-advantage {
  background: #f0f2f4;
  border-radius: 20px;
  height: 285px;
  .btn {
    position: absolute;
    bottom: 30px;
    color: black;
    .right-icon {
      bottom: -2px;
      left: 4px;
    }
  }
}
</style>
