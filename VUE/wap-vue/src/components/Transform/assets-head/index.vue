<template>
  <div id="assetsHead">
    <van-nav-bar :border="false" :title="title" :left-arrow="showLeft" @click-left="onClickLeft">
      <template #right>
        
        <slot></slot>
      </template>
    </van-nav-bar>
  </div>
</template>

<script>
export default {
  name: "assets-head",
  props: {
    title: {
      type: String,
      default: ''
    },
    goHome: {
      type: Boolean,
      default: false
    },
    goAssetsCenter: {
      type: Boolean,
      default: false
    },
    goPerpetualContract: {
      type: Boolean,
      default: false
    },
    showLeft: {
      type: Boolean,
      default: true
    },
    backFunc: {
      type: Function,
    },
    clickFunc: {
      type: Function,
    },
  },
  methods: {
    onClickLeft() {
      if (this.goHome) {
        this.$router.push('/');
      } else if (this.goAssetsCenter) {
        this.$router.push('/cryptos/assetsCenter/index');
      } else if (this.goPerpetualContract) {
        this.$router.go(-2);
      } else if (this.backFunc) {
        this.backFunc()
      } else if (this.clickFunc) {
        this.clickFunc()
      } else {
        this.$router.go(-1);
      }
    }
  }
}
</script>

<style lang="scss" scoped>
:deep(.van-nav-bar) {
  // background-color: $selectSymbol_background !important;
  .van-nav-bar__content {
    height: 96px;
  }

  .van-nav-bar__left {
    padding-left: 30px;
  }

  .van-nav-bar-icon {
    color: #999999;
  }

  .van-nav-bar__arrow {
    font-size: 48px !important;
    color: $dark-grey;
  }

  .van-nav-bar__right{
    // margin-right: 30px !important;
  }

  .van-nav-bar__title {
    font-size: 36px;
    font-weight: 400;
    color: $text_color;
    max-width: 100% !important;
    margin: 0 auto !important;
  }

}
</style>
