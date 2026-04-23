<template>
  <div id="adScreeningSelectItemPage">
    <div class="px-8">
      <div class="flex justify-between box-border items-center">
        <div class="text-grey text-26">{{ $t(title) }}</div>
        <div class="text-26" @click="show = !show">
          <span class="mr-4 text-color">{{ $t('全部') }}</span>
          <van-icon name="arrow-down" class="text-color" />
        </div>
      </div>
      <div class="select-wrapper mt-9" :class="{ 'show': show }">
        <div v-if="dataType() === '[object Object]'"
          class="relative h-20 text-center rounded-xl text-28 c2cColor box-border tabBackground flex justify-center items-center"
          @click="handlerClick({ code: 99 })" :class="{ 'active': value === 99 }">
          <img v-show="value === 99" class="absolute top-0 left-0 w-full h-full" src="@/assets/image/c2c/Group317.png"
            alt="">
          {{ $t('全部') }}
        </div>
        <div
          class="relative text-center rounded-xl text-28 c2cColor box-border tabBackground px-1 py-2 flex justify-center items-center"
          v-for="(item, index) in options" :key="index" @click="handlerClick(item)" :class="{ 'active': a(value, item) }">
          <img v-show="a(value, item)" class="absolute top-0 left-0 w-full h-full" src="@/assets/image/c2c/Group317.png"
            alt="">
          {{ item.title || item.name }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  Icon
} from "vant";

export default {
  name: "SelectItem",
  props: ['title', 'options', 'value'],
  data() {
    return {
      activeIndex: 0,
      show: false,
    }
  },
  created() {
    // const type = Object.prototype.toString.call(this.options)
    // if (type === '[object Array]') {
    //   this.activeIndex = 0;
    // } else {
    //   const keys = Object.keys(this.options)
    //   console.log(keys[0]);
    //   this.activeIndex = this.options[keys[0]].code;
    // }
  },
  methods: {
    handlerClick(item) {
      let val;
      if (item.method_type || item.method_type === 0) {
        console.log(item.method_type);
        val = item.method_type
      } else {
        val = item.code
      }
      this.$emit('input', val)
    },
    dataType() {
      return Object.prototype.toString.call(this.options)
    },
    a(a, item) {
      let val;
      if (item.method_type || item.method_type === 0) {
        val = item.method_type
      } else {
        val = item.code
      }
      return a === val;
    }
  },
  components: {
    [Icon.name]: Icon,
  }
}
</script>

<style lang="scss" scoped>
#adScreeningSelectItemPage {
  .text-color {
    color: #868D9A;
  }

  .show {
    height: 310px !important;
  }

  .active {
    border-color: #1D91FF !important;
  }

  .select-wrapper {
    overflow-y: scroll;
    height: 82px;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 32px;
    transition: all ease .4s;
    //background: #00ff33;

    &>div {
      border: 1px solid $tab_background;
    }
  }
}
</style>