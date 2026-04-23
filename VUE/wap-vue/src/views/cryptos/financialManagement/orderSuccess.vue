<template>
  <div id="cryptos">
    <div class="resetSuccess">
      <assets-head :title="title" :clickFunc="clickFunc" />
      <div class="content">
        <div class="imgBox"><img src="@/assets/image/success.png" alt=""></div>
        <div class="mt-8 font-bold text-48 textColor">{{ $t('购买成功') }}</div>
        <div class="text-grey mt-10 text-24">{{ $t('即日起可获得收益分成') }}!</div>
        <div class="btn mt-36 btnMain" @click="$router.push({
          path: '/cryptos/funds',
          query: {
            tab: 3,
            index: index, // 0: 查看理财订单 1: 矿机
            type: 'cryptos'
          }
        })">{{ $t('查看订单') }}</div>
        <div class="mt-10 text-36 textColor" @click="$router.go(-3)">{{ $t('返回交易') }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import assetsHead from '@/components/Transform/assets-head/index.vue';
export default {
  props: {

  },
  components: {
    assetsHead
  },
  data() {
    return {
      title: ''
    }
  },
  created() {
    if (this.$route.query) {
      this.index = this.$route.query.type
    }
  },
  methods: {
    clickFunc() {
      this.$router.push('/')
    }
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  .resetSuccess {
    width: 100%;
    box-sizing: border-box;
  }

  .content {
    font-size: 24px;
    padding: 32px;
    text-align: center;
  }

  .imgBox {
    width: 122px;
    height: 122px;
    margin: auto;

    img {
      width: 100%;
      height: 100%;
    }
  }

  .btn {
    color: $white;
    height: 88px;
    line-height: 88px;
    text-align: center;
    font-size: 32px;
    border-radius: 10px;
  }
}
</style>
