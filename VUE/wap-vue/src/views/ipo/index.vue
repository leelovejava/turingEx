<template>
    <div class="ipo-index" >
        <fx-header @back="back()" :back="false">
            <template #title>
                {{ t('IPO中心') }}
            </template>
        </fx-header>
        <van-tabs v-model:active="active" shrink :title-inactive-color="THEME === 'white' ? '#000' : '#fff'" title-active-color="#THEME === 'white' ? '#000' : '#fff'">
            <van-tab v-for="(item, index) in tabList" :key="index" :title="t(item.label)">
                <general v-if="active === 0" />
                <new-ipo v-if="active === 1" />
                <!-- <div v-if="active === 2">
                    <div class="tab-header flex px-5 py-5 text_color6">
                        <div class="tab1">{{t('股票名称')}}</div>
                        <div class="tab2">{{t('募集基金')}}</div>
                        <div class="tab3 text-right">{{t('提交招股书日期')}}</div>
                    </div>
                    <list-item />
                </div>
                <div v-if="active === 3">
                    <div class="tab-header flex px-5 py-5 text_color6">
                        <div class="tab1">{{t('股票名称')}}</div>
                        <div class="tab2">{{t('发行价')}}</div>
                        <div class="tab3 text-right">{{t('发行量')}}</div>
                    </div>
                    <list-item2 />
                </div> -->
                <div class="listing" v-if="active === 2">
                    <list-item1 />
                </div>
            </van-tab>
        </van-tabs>
    </div>
</template>

<script setup>
import {onMounted, ref, provide, watchEffect} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from "vue-i18n";
import newIpo from './components/newIpo.vue';
import listItem from './components/listItem.vue';
import listItem1 from './components/listItem1.vue';
import listItem2 from './components/listItem2.vue';
import general from './components/general.vue';
import { themeStore } from '@/store/theme';
const thStore = themeStore()
const THEME = thStore.theme
const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const defaultTabActive = +route.query.tabActive || 0
const active = ref(defaultTabActive)

const stockType = route.query?.stock
const stockActive = route.query?.stockActive
console.log(stockType)
provide('stockType', stockType)
provide('stockActive', stockActive)
watchEffect(() => {
    if (stockType && route.path === '/ipo/index') {
      // console.log('active', active.value)
      // console.log('route.path', route.path)
      router.replace({ query: { tabActive: active.value, stock: stockType,stockActive: stockActive } })
    }
})

const tabList = ref([
    { label: '概括' },
    { label: '新股认购' },
    // { label: '递交招股书' },
    // { label: '待上市' },
    { label: '已上市' },
])
onMounted(() => {
})



const back = () =>{
  router.go(-1)
}

</script>
<style lang="scss" scoped>
.ipo-index {
  font-size: 14px;

  :deep(.van-nav-bar__title){
    margin-left: 68px;
  }

  :deep(.van-tabs__nav) {
      background: $mainBgColor;
      padding: 0 15px 9px;
      border-bottom: 1px solid $border_color;
      box-sizing: border-box;
      overflow-x: auto;

      .van-tabs__line{
        bottom: 0;
      }
  }

  :deep(.van-tab){
    flex: none;
  }

  :deep(.van-tab--active) {
      font-size: 16px;
      font-weight: bold;
  }

  .tab1{
    width: 40%;
  }
  .tab2{
    width: 30%;
  }
  .tab3{
    width: 30%;
  }

  .flex-2 {
      flex: 2;
  }

  .tab-header {
      font-size: 13px;
      color: #747A8F;
  }
  .listing{
    overflow-x: auto;
  }
}
</style>