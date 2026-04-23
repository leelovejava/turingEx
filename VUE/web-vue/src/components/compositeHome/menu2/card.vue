<template>
  <div class="flex" :class="!lineWidth && 'w-100'">
    <div class="py-4">
      <img
        src="@/assets/images/compositeHome/menu1/vip/choice.png"
        width="30"
        height="30"
      />
    </div>
    <div class="pt-4 ml-4 w-100">
      <h5 class="mb-4" :style="{ width: lineWidth }" v-html="desc"></h5>
      <div class="desc-line" :style="{ width: lineWidth }"></div>
    </div>
  </div>
</template>
<script setup>
const props = defineProps({
  desc: {
    type: String,
    default: "",
  },
  lineWidth: {
    type: String,
    default: "",
  },
});
</script>
<style lang="scss" scoped>
.desc-line {
  height: 1px;
  background: #d8dee1;
  width: 100%;
}

.w-100 {
  width: 100%;
}
</style>
