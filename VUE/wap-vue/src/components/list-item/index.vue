<template>
  <!--  @click="itemClick(props.item)"-->
  <van-cell>
    <template #title>
      <div class="flex items-center">
        <fxPair @click-item="itemClick(props.item)" :item="item" />
      </div>
    </template>
  </van-cell>
</template>

<script setup>
import fxPair from '@/components/fx-pair/index.vue'
import { ref } from 'vue'
const emits = defineEmits(['click-item'])
const props = defineProps({
  item: {
    type: Object,
    default() {
      return {}
    }
  },
})


const itemClick = (a) => {
  emits('click-item', props.item)
  // console.log(1111)
}

</script>