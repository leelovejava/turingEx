<template>
  <div id="cryptos">
    <div id="buy_nav" class="buy_nav">
      <van-nav-bar :title="title" @click-left="onClickLeft" class="text-36 border-none">
        <template #left>
          <slot name="left">
            <van-icon name="arrow-left" />
          </slot>
        </template>
        <template #title>
          <slot name="title">
          </slot>
        </template>
        <template #right>
          <slot name="right"></slot>
        </template>
      </van-nav-bar>
    </div>
  </div>
</template>

<script>
import { NavBar, Icon } from 'vant';
export default {
  name: "OrderNav",
  props: {
    title: {

    },
    state: {

    },
    // 是否要路由返回
    back: {
      default: true,
    }
  },
  data() {
    return {

    }
  },
  methods: {
    // 返回
    onClickLeft() {
      if (this.back) {
        this.$router.back();
      } else {
        this.$emit('back')
      }
    }
  },
  components: {
    [NavBar.name]: NavBar,
    [Icon.name]: Icon,
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  .buy_nav {
    :deep(.van-nav-bar) {
      &:after {
        border: none;
      }
    }

    :deep(.van-icon) {
      font-size: 40px;
      color: $c2c_color;
    }

    :deep(.van-nav-bar__title) {
      font-size: 37px;
    }
  }
}
</style>
