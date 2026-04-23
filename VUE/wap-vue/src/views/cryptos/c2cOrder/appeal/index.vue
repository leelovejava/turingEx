<template>
  <div id="appealIndexPage">
    <div id="full">
      <order-nav>
        <template #title>
          {{ $t('对订单申诉') }}
        </template>
      </order-nav>
      <div class="px-8 mt-5">
        <h2 class="text-30 font-normal c2cColor">{{ $t('联系对方协商解决') }}</h2>
        <div class="rounded-lg px-8 pb-8 pt-12 mt-8 box-border tabBackground c2cColor">
          <div class="mb-14 text-center flex justify-center">
            <img class="w-32" src="~@/assets/image/c2c/Vector1.png" alt="">
          </div>
          <div class="text-28 text-center">
            <p>{{ $t('如存在交易问题，直接与对联系处理是最有效的方式。联系对方协商解决') }}</p>
            <p class="py-1">{{ $t('您可在聊天窗口上传凭证及账号信息，联系对方协商解决') }}</p>
            <p>{{ $t('以双方协商核实。联系对方协商解决') }}</p>
          </div>
          <van-button color="#1D91FF" class="w-full h-20 text-30 rounded-xl" type="info" @click="contact">{{
            $t('联系对方协商解决') }}</van-button>
        </div>
        <div class="mt-14">
          <div class="mb-5 text-grey text-26" @click="$router.push('/cryptos/chat')">{{ $t('申诉联系对方协商解决') }}</div>
          <van-divider class="diviLine" />
          <div class="mb-14 text-grey text-26 ">{{ $t('本次交易已结束，资产已不在平台托管中。平台无法帮您直接追回 资产，请知晓。联系对方协商解决') }}
          </div>
          <van-cell-group>
            <van-cell @click="show = true">
              <template #title>
                <span class="textColor text-30">{{ $t('对订单有疑问联系对方协商解决') }}</span>
              </template>
              <template #default>
                <van-icon class="font-bold text-grey" name="arrow" />
              </template>
            </van-cell>
            <!-- <van-cell class="mt-20">
            <template #title>
              <span class="text-black text-30">{{$t('其他联系对方协商解决')}}</span>
            </template>
            <template #default>
              <van-icon class="font-bold text-grey" name="arrow"/>
            </template>
          </van-cell> -->
          </van-cell-group>
        </div>
      </div>

      <!--  申诉  -->
      <van-popup v-model:show="show">
        <appeal-msg />
      </van-popup>
    </div>
  </div>
</template>

<script>
import {
  Button,
  Divider,
  CellGroup,
  Cell,
  Icon,
  Popup,
} from "vant";
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import AppealMsg from "@/views/cryptos/c2cOrder/components/appeal/AppealMsg.vue";

export default {
  name: "AppealPage",
  data() {
    return {
      show: false,
    }
  },
  methods: {
    contact() { //
      // console.log(11111)
      this.$router.push('/cryptos/chat')
    }
  },
  components: {
    [Button.name]: Button,
    [Divider.name]: Divider,
    [CellGroup.name]: CellGroup,
    [Cell.name]: Cell,
    [Icon.name]: Icon,
    [Popup.name]: Popup,
    OrderNav,
    AppealMsg,
  }
}
</script>

<style lang="scss" scoped>
#appealIndexPage {
  font-size: 30px;

  .van-popup {
    border-radius: 10px;
  }


  .van-button--info {
    background: $btn_main;
    border-color: $btn_main;
    color: $main-btn-color;
  }

  :deep(.van-cell__title) {
    padding: 10px;
  }

  :deep(.van-cell) {
    padding: 10px;
    background: $main_background;
    display: flex;
    align-items: center;
  }

}
</style>