<template>
  <van-nav-bar :title="title" :left-arrow="showLeft" @click-left="onClickLeft">
    <template #title>
      <slot name="title"></slot>
    </template>
    <template #right>
      <slot name="right"></slot>
    </template>
  </van-nav-bar>
</template>

<script setup>
import { useRouter } from 'vue-router'
const emit = defineEmits(['back'])
const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  // 是否路由返回
  back: {
    default: true
  },
  showLeft: {
    type: Boolean,
    default: true
  },
})

const router = useRouter();

// 返回
const onClickLeft = () => {
  return props.back ? router.back() : emit('back')
}

</script>

<style lang="scss" scoped>
:deep(.van-icon) {
  font-size: 23px;
  color: $text_color;
}
:deep(.van-nav-bar__title) {
  color: $text_color;
}
</style>
