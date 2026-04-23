<template>
  <div>
    <van-cell class="mb-28 items-center " @click.native="$emit('cell-click')">
      <template #icon>
        <img class="w-28 h-28 mr-34" :src="leftImg" alt="">
      </template>
      <template #title>
        <span class="text-black text-28">{{ title }}</span>
      </template>
      <template #default>
        <span class="mr-20 text-28" style="color: #A7ADB8">{{ value }}</span>
      </template>
      <template #right-icon>
        <van-icon name="arrow" class="font-bold" color="#868D9A" />
      </template>
    </van-cell>
  </div>
</template>

<script>
import {
  Cell,
  Icon,
} from 'vant'

export default {
  name: "UserCell",
  props: ['leftImg', 'title', 'value'],
  components: {
    [Cell.name]: Cell,
    [Icon.name]: Icon,
  }
}
</script>

<style scoped></style>