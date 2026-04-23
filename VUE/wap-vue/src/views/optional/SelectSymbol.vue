<template>
  <div class="pb-fix">
    <div class="container-box">
      <header class="header flex" @click="router.back()">
        <div class="icon back">
          <van-icon name="arrow-left" size="20" />
        </div>
        <p class="title">{{ t('选择币种') }}</p>
      </header>
      <p class="title-box">{{ t('Hots') }}</p>
      <!-- <van-index-bar v-for="(item, index) in currencies" :key="index" :index-list="newLetter">
        <van-index-anchor class="index-anchor" :index="item.name" />
        <div @click="openAdd(items.name)" class="item-cell ml-4 py-4" v-for="(items, index) in item.list" :key="index">
          {{ items }}
        </div>
      </van-index-bar> -->
      <!-- <van-index-bar> -->
      <!-- <van-index-anchor index="A" /> -->
      <van-cell v-for="(item, index) in currencyList" @click="itemClick(item)" :key="index">
        <template #title>
          {{ item.currency }}
        </template>
      </van-cell>

      <!-- <van-index-anchor index="B" /> -->
      <!-- </van-index-bar> -->
    </div>
  </div>
</template>
  
<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { IndexBar, IndexAnchor } from 'vant';
import { _getCurrencyList } from "@/service/trade.api.js";
import { _listExchanges } from '@/service/quotes.api'
import { useQuotesStore } from '@/store/quotes.store';

const { t } = useI18n()
const router = useRouter()
const currencies = ref([]);
const currencyList = ref([])
const quotesStore = useQuotesStore()
onMounted(() => {
  // getCurrencyList()
  listExchanges()
})

const getCurrencyList = () => {
  // 获取法币列表
  _getCurrencyList().then(res => {
    currencies.value = res.map(item => {
      return {
        name: item.currency
      }
    });
    console.log(currencies.value, 'currencies.value')
  })
}

const itemClick = (item) => {
  console.log(item, 'item')
  quotesStore.$state.currency = item.currency
  router.go(-1)

}
//获取币种
const listExchanges = () => {
  _listExchanges().then((res) => {
    currencyList.value = res.slice(1, res.length)
    console.log(currencyList.value[1].name)
  })
}
</script>
  
<style lang="scss" scoped>
:deep(.van-cell) {
  background: $mainBgColor;
  color: $text_color;
  border-bottom: 1px solid $border_color;
}

:deep(.van-cell::after) {
  border: none;
}

:deep(.van-index-anchor--sticky) {
  background: $border_color;
}

:deep(.van-index-bar__index) {
  color: $color_main;
  font-size: 12px;
  margin-top: 6px;
}


.container-box {
  .header {
    padding: 0 12px;
    align-items: center;

    .title {
      font-size: 16px;
      color: $text_color;
      text-align: center;
      flex: 1;
      transform: translate(-20px, 0px);
    }

    .icon {
      display: inline-block;
      width: 20px;
      height: 20px;
    }
  }

  .title-box {
    margin-top: 10px;
    padding: 0 12px;
    height: 32px;
    background: $selectSymbol_background;
    line-height: 32px;
    font-weight: 400;
    font-size: 12px;
    color: #747A8F;
  }

  .selectPay {
    .index-anchor {
      background: $recommend_bg;
    }
  }

  .item-cell {
    border-bottom: 1px solid $border_color;
  }

}
</style>