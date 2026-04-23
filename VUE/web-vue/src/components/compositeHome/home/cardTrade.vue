<template>
  <div class="card-trade relative">
    <img v-lazy="getImageUrl(imgPath)" width="388" height="210" />
    <div class="px-12">
      <div class="mt-10 mb-6 text-left">
        <span class="tag">{{ tag }}</span>
      </div>
      <div class="text-xl font-normal text-left mb-4">{{ title }}</div>
      <h5 class="text-base text-gray-400 text-left">{{ desc }}</h5>
      <!--  -->
      <div class="mt-32 mb-20 absolute bottom-8 left-12">
        <div class="text-blue-700">
          {{ t("click-view") }}
          <el-icon>
            <Right />
          </el-icon>
        </div>
      </div>
    </div>
    <!--  -->
    <div class="bottom flex absolute bottom-8 left-12 pt-4">
      <div class="flex mr-24">
        <img class="logo-img" v-lazy="getImages('compositeHome/logo.png')" />
        <div class="text-left ml-4">
          <div>{{ $title }}</div>
          <div>{{ t("Market Analysis") }}</div>
        </div>
      </div>
      <div>
        <img
          v-lazy="getImages('compositeHome/home/twitter.png')"
          width="24"
          height="18"
        />
      </div>
    </div>
  </div>
</template>
<script setup>
import { useI18n } from "vue-i18n";
import { getImageUrl,getImages } from "@/utils/index";
import { Right } from "@element-plus/icons-vue";

const { t } = useI18n();
const props = defineProps({
  tag: {
    type: String,
    default: "",
  },
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
.logo-img {
  width: 48px;
  height: 28px;
}

.card-trade {
  height: 700px;
  background: #ffffff;
  border: 1px solid #e7ebee;
  border-radius: 20px;

  .tag {
    background: #e7ebee;
    border-radius: 52px;
    padding: 10px 16px;
    font-size: 12px;
  }

  .bottom {
    border-top: 1px solid #d6dee1;
  }
}
</style>
