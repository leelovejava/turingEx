<template>
    <div class="lotteryRecord">
        <fx-header @back="back()" :back="false">
            <template #title>
                <van-dropdown-menu>
                  <van-dropdown-item v-model="curStock" :options="optionList" @change="changeType" />
                </van-dropdown-menu>
            </template>
            <!-- <template #right>
                <img class="search-icon" src="@/assets/image/optional/search.png" alt="">
            </template> -->
        </fx-header>
        <div class="new" v-if="curStock === 'newStock'">
          <NewStock />
        </div>
        <div class="now" v-if="curStock === 'nowStock'">
          <SpotStock />
        </div>
        <div class="record" v-if="curStock === 'record'">
          <LotteryRecord />
        </div>
        <div class="subscribe" v-if="curStock === 'subscribe'">
          <SubscribeRecord />
        </div>
    </div>
</template>

<script setup>
import {onMounted, ref, reactive, provide} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from "vue-i18n";

import LotteryRecord from './lotteryRecord.vue'
import SubscribeRecord from './subscribeRecord.vue'
import SpotStock from './spotStock.vue';
import NewStock from './newStock.vue';

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const list = ref([]);
const loading = ref(false);
const finished = ref(false);

const optionList = [
  { text: t('新股库存'), value: 'newStock' },
  { text: t('现股库存'), value: 'nowStock' },
  { text: t('抽签记录'), value: 'record' },
  { text: t('认缴记录'), value: 'subscribe' },
];
let curStock = ref('')
onMounted(() => {
  curStock.value = route.query?.type || 'newStock'
})

const stockType = route.query?.stock
const stockActive = route.query?.stockActive
provide('stockType', stockType)

const changeType = (e) => {
  router.push(`/ipo/stock?type=${e}&stock=${stockType}&stockActive=${stockActive}`)
}

console.log(route.query)
const back = () =>{
  router.push(`/quotes/index?tabActive=${stockActive}`)
}

</script>
<style lang="scss" scoped>
:deep(.van-cell){
  background-color: $van-bg;

  &.van-dropdown-item__option{
    color: $text_color;
  }

  &.van-dropdown-item__option--active{
    color: #1989fa;
  }

  &::after{
    display: none;
  }
}

:deep(.van-nav-bar__title){
  max-width: 70% !important;

  .van-dropdown-menu__bar{
    background: transparent;
  }
  .van-dropdown-menu__title{
    color: $text_color;

    &::after{
      right: 0;
      border-color: transparent transparent $text_color $text_color;
    }
  }
}
.lotteryRecord {
    font-size: 14px;

    .search-icon {
        width: 23px;
        height: 23px;
    }

    .tab-header {
        font-size: 13px;
        color: #747A8F;
    }
    .flex-2{
        flex: 2;
    }
    .list-div {
        padding: 20px 0;

        .list-title {
            width: 80px;
            text-overflow: ellipsis;
            white-space: nowrap;
            overflow: hidden;
        }
    }
}
</style>