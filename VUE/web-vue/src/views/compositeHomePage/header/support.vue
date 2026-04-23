<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h1>{{ t("header-s-s0-t1") }}</h1>
      <h5 class="w-500">{{ t("header-s-s0-d1") }}</h5>
    </pc-section>
    <pc-section><navigator></navigator></pc-section>
    <pc-section innerclass="flex justify-center">
      <div class="w-768">
        <h1 class="mt-8 text-center">{{ t("header-s-s1-t1") }}</h1>
        <h5 class="mb-8 text-center">{{ t("header-s-s1-d1") }}</h5>
        <el-collapse v-model="questionActive" class="question-collapse">
          <el-collapse-item
            :title="t(`header-s-s1-q${i + 1}`)"
            :name="i"
            v-for="(_, i) in list5"
            :key="i"
          >
            <div v-html="t(`header-s-s1-a${i + 1}`)"></div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </pc-section>

    <div class="ready-bg mt-12">
      <h2 class="text-white">{{ t("ready-t1") }}</h2>
      <h5 class="text-white">{{ t("ready-d4") }}</h5>
      <el-button class="blue-middle-btn my-4">{{ t("ready-b1") }}</el-button>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import readyBg from "@comCompositeHome/readyWithBg1.vue";
import navigator from "@comCompositeHome/navigator.vue";
const list5 = Array(5);

const { t } = useI18n();
const questionActive = ref();
</script>
<style lang="scss" scoped>
.ready-bg {
  background: #18268f;

  height: 394px;
  width: 100%;
  display: flex;
  color: white;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.w-768 {
  width: 768px;
}
.question-collapse :deep {
  .rich-text__container h5 {
    font-size: 16px;
    margin-bottom: 12px;
  }
}
</style>
