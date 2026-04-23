<template>
  <div id="cryptos">
    <div id="normalHead">
      <van-nav-bar :border="false" :title="title" left-arrow @click-left="onClickLeft">
        <template #right>
          <slot></slot>
        </template>
      </van-nav-bar>
    </div>
  </div>
</template>

<script>
export default {
  name: "normal-head",
  props: ['title', 'goHome', 'goAssetsCenter', 'goPerpetualContract', 'backFunc'],
  methods: {
    onClickLeft() {
      if (this.goHome) {
        this.$router.push('/');
      } else if (this.goAssetsCenter) {
        this.$router.push('/cryptos/assetsCenter/assets');
      } else if (this.goPerpetualContract) {
        this.$router.go(-2);
      } else if (this.backFunc) {
        this.backFunc()
      } else {
        this.$router.go(-1);
      }
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.van-icon-arrow-left) {
  font-size: 40px !important;
}
</style>
