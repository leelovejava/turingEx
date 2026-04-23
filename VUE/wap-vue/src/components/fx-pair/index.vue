<template>
  <section class="fx-pair">
    <div class="flex" @click="itemClick">
      <div class="relative mx-2" ref="htmlRefHook">
        <!-- <img class="w-8 h-8 absolute left-0 top-0 z-10" src="./EUR.png" alt="EUR" />
                <img class="w-8 h-8 absolute bottom-0 right-0" src="./GBP.png" alt="GBP" /> -->
        <!-- <img :src="`${IMG_PATH}/symbol/${item.symbol}.png`" alt=""> -->
      </div>
      <div class="flex flex-col">
        <p class="text-primary text-lg font-semibold title">{{ item.symbol || '--' }}<span class=" text-xs ml-2">{{
          type
        }}</span></p>
        <p class="text-24 text-gray whitespace-nowrap long-text-fix">{{ item.name || '--' }}</p>
      </div>
    </div>
  </section>
</template>

<script setup>
import { IMG_PATH } from '@/config'
const props = defineProps({
  type: {
    type: String,
    default: ''
  },
  item: {
    type: Object,
    default() {
      return {}
    }
  }
})

const handImg = (symbol) => {
  return new URL(`../../assets/image/exchange/${symbol}.png`, import.meta.url).href
}

const emits = defineEmits(['click-item'])

const itemClick = () => {
  emits('click-item')
}
</script>

<style lang="scss" scoped>
.long-text-fix {
  width: 160px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.title {
  color: $text_color;
}
</style>