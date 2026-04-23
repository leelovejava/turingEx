<template>
  <div class="list pb-20">
    <fx-header fixed @back="back()" :back="false">
      <template #title>{{ $t('paymentMethod') }}</template>
    </fx-header>
    <ul class="payment-list-ul">
      <li class="flex px-4 justify-between pt-5 pb-10" v-for="(item, index) in methodList" :key="index">
        <div class="bank-after pl-10 ">
          <div class="bank-name">{{ $t(item.methodName) }}</div>
          <div class="py-2 bank-realName">{{ item.realName }}</div>
          <div class="bank-no">{{ item.paramValue1 }}</div>
        </div>
        <div @click="editInfo(item)">
          <img class="edit-img" src="@/assets/image/Record/edit.png" />
        </div>
      </li>
    </ul>
    <div class="px-4 pt-6 fixed-wrap">
      <van-button class="w-full" type="primary" @click="submit">{{ $t('AddPaymentMethod') }}</van-button>
    </div>
  </div>
</template>

<script setup>
import { onBeforeMount, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { _getPaymentMethod } from "@/service/trade.api.js";
import { useUserStore } from "@/store/user.js";
import { useI18n } from "vue-i18n";
const { t } = useI18n()
const useStore = useUserStore();
const route = useRoute()
const router = useRouter()
const methodList = ref({})
onBeforeMount(async () => {
  // 获取支付列表
  _getPaymentMethod().then(res => {
    methodList.value = res
  })
})
const submit = () => {
  router.push('selectPay')
}
const editInfo = (val) => {
  console.log(val);
  sessionStorage.setItem("editAdd", JSON.stringify({ id: val.uuid, name: val.methodName, paramValue1: val.paramValue1, realName: val.realName, remark: val.remark }));
  router.push('add')
}
const back = () => {
  router.push('/my/index')
}
</script>
<style lang="scss" scoped>
.list {
  padding-top: var(--van-nav-bar-height);
  color: $text_color;

  .payment-list-ul {
    position: relative;
    padding-bottom: 85px;
    // overflow: auto;
    // height: calc(100vh - 170px);

    li {
      background: $border_color;
      border-bottom: 1px solid $border_color;
      position: relative;

      .edit-img {
        width: 17px;
        height: 17px;
      }

      .bank-no {
        font-size: 18px;
        font-weight: bold;
        letter-spacing: 1px;
      }

      .bank-name {
        font-size: 16px;
      }

      .bank-realName {
        font-size: 14px;
      }
    }

    .bank-after::after {
      width: 4px;
      height: 15px;
      background: #E7BB41;
      content: '';
      position: absolute;
      left: 15px;
      top: 15px;
    }

    .AI-after::after {
      width: 4px;
      height: 15px;
      background: #4BA6EB;
      content: '';
      position: absolute;
      left: 15px;
      top: 25px;
    }
  }

  .fixed-wrap {
    position: fixed;
    bottom: 0;
    height: 115px;
    // background: #fff;
    width: 100%;
  }
}
</style>