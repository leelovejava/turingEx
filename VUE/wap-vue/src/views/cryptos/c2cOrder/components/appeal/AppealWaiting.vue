<template>
  <div id="AppealWaitingPage">
    <div class="w-full h-full">
      <order-nav :back="false" @back="hide">
        <template #title>
          {{ $t('申述提交') }}
        </template>
      </order-nav>

      <div class="mt-16">
        <div class="text-center">
          <img class="w-32 h-32 group100-img" src="~@/assets/image/c2c/Group100.png" alt="">
          <div class="mt-9 font-bold text-50 textColor">{{ $t('提交成功') }}</div>
          <div class="mt-2 text-28 text-grey">
            <p class="mb-4">{{ $t('您的资料已成功上传!') }}</p>
            <p>{{ $t('系统将在三个工作日内审核完毕，请您耐心等候。') }}</p>
          </div>
        </div>
      </div>
      <div class="px-8 mt-24">
        <van-button color="#1D91FF" class="w-full h-100 rounded-xl" type="info" @click="tokefu">{{ $t('联系客服') }}
        </van-button>
        <div @click="hide" class="mt-8 text-36 text-center" style="color: #1D91FF">{{ $t('返回') }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { Button } from "vant";
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
export default {
  name: "AppealWaiting",
  components: {
    [Button.name]: Button,
    OrderNav,
  },
  methods: {
    tokefu() {
      this.$router.push('/workerOrder')
    },
    hide() {
      this.$emit("back");
    }
  }
}
</script>

<style lang="scss" scoped>
#AppealWaitingPage {
  font-size: 30px;

  .group100-img {
    margin: 0 auto;
  }
}
</style>

