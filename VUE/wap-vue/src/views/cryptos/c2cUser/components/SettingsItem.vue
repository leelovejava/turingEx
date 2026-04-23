<template>
  <div class="mt-20">
    <div class="pl-32 pr-32 mb-40 text-grey">
      <slot></slot>
    </div>
    <van-cell-group class="w-full pl-32 pr-32 border-b-grey border-light-grey box-border">
      <van-cell
          class="items-center mb-32"
          v-for="(option, index) in options"
          :key="index"
          :title="option.title"
      >
        <template #default>
          <van-switch v-if="!option.disabled" v-model="option.checked"   @change="$emit('changeSwitch',option)"/>
          <van-switch v-else disabled v-model="option.checked" />
        </template>
      </van-cell>
    </van-cell-group>
  </div>
</template>

<script>
import {Cell, CellGroup, Switch,} from 'vant';

export default {
  name: "SettingsItem",
  props: ['options'],
  components: {
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    [Switch.name]: Switch,
  }
}
</script>

<style lang="scss" scoped>

:deep(.van-cell__title){
  font-size: 30px;
  color: #000;
}
:deep(.van-cell__value){
  display: flex;
  align-items: center;
  justify-content: end;
}
:deep(.van-switch){
  transform: scale(.8);
  background: #E9EBEE;
}
:deep(.van-switch--on){
   background-color: #1989fa;
}

</style>