<template>
  <div id="CarItemPage">
    <div class="pl-8 pr-8 pt-10 pb-8 payment_item c2cColor" :class="{ 'border-b-grey': item.type === 'CN' }">
      <div class="title flex justify-between w-full">
        <div class="flex items-center">
          <div class="w-3 h-9 mr-5 rounded-2xl" :style="{ 'background': color }"></div>
          <div class="text-34">{{ $t(item.methodName) }}</div>
        </div>
        <div class="icon edit" @click="editClick(item, $event)">
          <img class="edit w-8 h-8" src="~@/assets/image/payment/edit.png" alt="">
        </div>
      </div>
      <div class="mt-5 text-32">{{ item.realName }}</div>
      <div class="number">
        <div class="font-bold mt-4 text-32" :class="{ 'ml-44': item.type === 'EN', 'text-32': item.type === 'EN' }">
          {{ item.paramValue1 ? item.paramValue1 : fullNumber(item.paramValue1) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getRandom, } from "@/utils/utis";

export default {
  name: "CarItem",
  props: ["item", "type"],
  data() {
    return {
      color: "#",
    }
  },
  created() {
    for (let i = 0; i < 6; i++) {
      this.color += getRandom();
    }
    console.log(this.color)
  },
  methods: {
    fullNumber(number) {
      if (this.item.type === 'CN') {
        return number.replace(/(\d{4})/g, "$1 ").trim()
        // return number;
      } else {
        return number;
      }
    },
    // 进入银行卡详情页面
    editClick(data) {
      this.$router.push({
        path: '/cryptos/wantBuy/bankCard',
        query: {
          id: data.uuid,
          type: 'CN',
          configType: 'edit'
        }
      })
    }
  },
  computed: {}
}
</script>

<style lang="scss" scoped>
#CarItemPage {
  font-size: 30px;

  .pt-55 {
    padding-top: 55px;
    ;
  }

  .payment_item {
    background: $tab_background;
    border-bottom: 1px solid $divi_line;
  }
}
</style>