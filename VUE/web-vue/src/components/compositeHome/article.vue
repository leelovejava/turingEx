<template>
  <div class="flex justify-center pt-12">
    <div class="w-768">
      <h1>{{ t(`m4-mn-a${aNum}-t`) }}</h1>
      <h6>{{ t(`m4-mn-a${aNum}-ti`) }}</h6>
      <h5 class="d-line">{{ t(`m4-mn-a${aNum}-d`) }}</h5>
      <div class="content" v-html="t(`m4-mn-a${aNum}-c`)"></div>
      <div
        v-if="aNum == 2 || aNum == 1"
        class="content"
        v-html="t(`m4-mn-a${aNum}-c2`)"
      ></div>
      <div
        v-if="aNum == 2 || aNum == 1"
        class="content"
        v-html="t(`m4-mn-a${aNum}-c3`)"
      ></div>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from "vue-i18n";
const { t } = useI18n();
const props = defineProps({
  aNum: {
    type: Number,
    default: 1,
  },
});
</script>
<style lang="scss" scoped>
.d-line {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 24px;
  margin: 16px 0 32px;
}
.w-768 {
  width: 768px;
}

.content {
  :deep(h5) {
    margin-bottom: 18px;
  }
}
</style>
