<template>
  <div class="checkBox">
    <van-radio-group v-model="radio" @change="onInput">
      <van-radio v-for="(item, index) in list" :key="index" :name="item.type">
        {{ item.name }}
        <template #icon="props">
          <div class="select" :class="props.checked ? 'selected' : ''">
            <div class="checked"></div>
          </div>
        </template>
      </van-radio>
    </van-radio-group>
  </div>
</template>

<script setup>
import { RadioGroup, Radio } from 'vant';
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
const $emit = defineEmits(['checkedSelect'])

const route = useRoute()

const radio = ref(0)

const props = defineProps({
  list: {
    type: Array,
    default: []
  },
  initRadio: {
    type: Number,
    default: 0
  }
})

onMounted(() => {
  let type = route.query.type || props.list[0].type;
  radio.value = Number(type);
})
const onInput = () => {
  $emit('checkedSelect', radio.value)
}

</script>

<style lang="scss" scoped>
.checkBox {
  width: 100%;
  box-sizing: border-box;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  font-size: 13px;
  color: $black;

  .select {
    width: 15px;
    height: 15px;
    box-sizing: border-box;
    border-radius: 50%;
    margin-right: 8px;
    border: 1px solid $text_color1;
    box-sizing: border-box;
    padding: 1px;

    .checked {
      width: 100%;
      height: 100%;
      border-radius: 50%;
    }
  }

  .selected {
    .checked {
      background: $btn_main;
    }
  }
}

:deep(.van-radio__icon) {
  display: flex;
  align-items: center;
  justify-content: center;
  height: auto !important;
}

.van-radio {
  font-size: 13px !important;
  color: $text_color !important;
  margin-top: 21px;
}

:deep(.van-radio__label) {
  font-size: 13px !important;
  color: $text_color;
}
</style>
