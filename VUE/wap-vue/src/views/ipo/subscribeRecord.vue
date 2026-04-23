<template>
  <div class="lotteryRecord">
      <div class="px-5">
          <div class="flex  py-10 border-b-color border-t-color justify-between ">
            <div class="text-center">
                <div class="text_color6">{{t('中签额度')}}</div>
                <div>{{ topData.winningQuota }}</div>
            </div>
            <div class="text-center">
                <div class="text_color6">{{t('认缴额度')}}</div>
                <div :class="topData.subscriptionLimit > 0 ? 'red' : 'green'">{{ topData.subscriptionLimit }}</div>
            </div>
            <div class="text-center">
                <div class="text_color6">{{t('可用额度')}}</div>
                <div>{{ topData.availableLimit }}</div>
            </div>
          </div>
          <div class="tabWarp">
            <div class="tab-header flex py-5 text_color6">
              <div class="td-2 text-left">{{t('名称/代码')}}</div>
              <div class="td-1 text-center">{{t('现价/成本')}}</div>
              <div class="td-1 text-center">{{t('中签额度')}}</div>
              <div class="td-1 text-center">{{t('认缴金额')}}</div>
              <div class="td-1 text-center">{{t('状态')}}</div>
              <div class="td-2 text-right">{{t('时间')}}</div>
          </div>
          <van-list v-model:loading="loading" :finished="finished" :loading-text="$t('加载中') + '...'" :finished-text="$t('没有更多了')" @load="onLoad">
          <div v-for="(item, index) in list" :key="index" class="list-div flex">
              <div class="td-2 text-left">
                  <div class="list-title">{{ item.name }}</div>
                  <div class="text_color6">{{ item.productCode}}</div>
              </div>
              <div class="td-1 text-center">{{ item.subPrice + '/' +  item.subPrice }}</div>
              <div class="td-1 text-center">{{ item.requiredNumber }}</div>
              <div class="td-1 text-center">{{ item.requiredSubscribe }}</div>
              <div class="td-1 text-center" :class="item.status === 2 ? 'red' : ''">{{ item.status === 1 ? t('待确认') : t('已认缴') }}</div>
              <div class="td-2 text-right">{{ getTime(item.createTime) }}</div>
          </div>
      </van-list>
          </div>
      </div>
  </div>
</template>

<script setup>
import {inject, onMounted, provide, ref} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from "vue-i18n";
import { userPromiseList, getSubscribeTop } from '@/service/ipo.api'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const list = ref([]);
const loading = ref(false);
const finished = ref(false);
const index= ref(1)
const topData = ref({})
onMounted(() => {
  getTopData()
})
const getTopData = () => {
  getSubscribeTop({symbolType: stockType || 'US-stocks'}).then((res)=>{
    topData.value = res
 })
}

const stockType = inject('stockType')
provide('stockType', stockType)
const onLoad = () => {
  let params = {
    current: index.value,
    size: 10,
    symbolType: stockType || 'US-stocks'
  }
  userPromiseList(params).then(res => {
    if(res.length) {
      list.value = list.value.concat(res)
    }
    index.value++
    loading.value = false;
    if (res.length < 10) {
      finished.value = true;
    }
  })
}

const getTime = (time) => {
return time?.split(' ')[0];
}

</script>
<style lang="scss" scoped>
.lotteryRecord {
  font-size: 14px;

  .search-icon {
      width: 23px;
      height: 23px;
  }

  .tab-header {
      gap: 0 5px;
      font-size: 12px;
      color: #747A8F;
  }
  .td-1{
      flex-shrink: 0;
      width: 60px;
    }
    .td-2{
      flex-shrink: 0;
      width: 80px;
    }

    .tabWarp{
      width: 100%;
      padding: 10px 10px;
      overflow-x: scroll;
      // white-space: nowrap;
      box-sizing: border-box;
    }
  .list-div {
      gap: 0 5px;
      padding: 15px 0;
      font-size: 12px;
      align-items: center;
      border-bottom: 1px solid $border_color;

      .list-title {
          text-overflow: ellipsis;
          // white-space: nowrap;
          overflow: hidden;
      }
  }
}
</style>