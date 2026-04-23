<template>
  <div class="card-platform bg-white px-8 pt-16 relative">
    <div class="flex justify-center">
      <div class="imgwrapper">
        <img v-lazy="getImageUrl(imgPath)" />
      </div>
    </div>
    <h2 class="mt-8 mb-4 fs-30 text-center">{{ title }}</h2>
    <div class="text-gray-400 text-center">{{ desc }}</div>
    <div class="learn-more">
      {{ t("liaojiegengduao") }}
      <el-icon class="right-icon"><Right /></el-icon>
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
.card-platform {
  height: 498px;
  border-radius: 20px;
  .right-icon {
    bottom: -2px;
  }
}
.learn-more {
  margin-left: 25%;
  position: absolute;
  bottom: 32px;
  color: black;
}
.fs-30 {
  font-size: 30px;
}
.imgwrapper {
  width: 88px;
  height: 88px;
  img {
    width: 100%;
    height: 100%;
  }
}
</style>
