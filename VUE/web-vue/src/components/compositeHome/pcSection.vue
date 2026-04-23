<template>
  <section class="single-block">
    <div class="single-block-w px-8" :class="innerclass">
      <slot></slot>
    </div>
  </section>
</template>

<script setup>
const props = defineProps({
  innerclass: {
    type: String,
    default: "",
  },
});
</script>

<style lang="scss" scoped>
// 核心区域1300
.single-block {
  width: 100%;
  display: flex;
  justify-content: center;
  &-w {
    width: 1300px;
  }
}
</style>
