<template>
  <div id="c2cOrderSearch">
    <div>
      <ul class="flex justify-between flex-wrap">
        <li v-for="(item, index) in list" :key="index"
          class="relative flex justify-center items-center w-60 h-20 rounded-xl box-border tabBackground c2cColor"
          @click="itemClick(item)" :class="{ 'active': value === item.title }">
          <img v-show="value === item.title" class="absolute top-0 right-0 w-full h-full"
            src="@/assets/image/c2c/Group317.png" alt="">
          {{ item.title }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script>

export default {
  name: "SelectItem",
  props: ['list', 'value'],
  methods: {
    itemClick(data) {
      console.log(data);
      this.$emit('select', data);
    }
  }
}
</script>

<style lang="scss" scoped>
#c2cOrderSearch {
  font-size: 30px;

  li {
    margin-bottom: 30px;
  }

  .active {
    border: 1px solid #1D91FF;
  }
}
</style>
