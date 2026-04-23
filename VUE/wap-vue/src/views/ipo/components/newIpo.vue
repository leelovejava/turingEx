<template>
  <div class="newIpo px-5">
      <van-list v-model:loading="loading" :finished="finished" :loading-text="$t('加载中') + '...'" :finished-text="$t('没有更多了')" @load="onLoad">
          <div v-for="(item, index) in list" :key="index" class="border-b-color list-div">
              <div class="flex justify-between mt-4">
                  <div>{{ item.name }}</div>
                  <!-- <div class="colorMain">
                    {{ t('承销价') }}
                    <span>{{ item.underwritingPrice }}</span>
                  </div> -->
              </div>
              <div class="flex justify-between text_color6 mt-10">
                  <div class="flex-1">{{ t('状态') }}</div>
                  <div class="flex-1 text-center">{{ t('差价') }}</div>
                  <div class="flex-1 text-right">{{ t('市价') }}</div>
              </div>
              <div class="flex justify-between">
                  <div :class="item.status === 1 || item.status === 2 ? 'red' : 'normal'" class="flex-1">{{ item.status === 1 ? t('未开始') :  item.status === 2 ? t('开放中') : t('已结束') }}</div>
                  <div class="red flex-1 text-center">{{ item.priceDifference }}</div>
                  <div class="flex-1 text-right">{{ item.marketPrice }}</div>
              </div>
              <div class="flex justify-between mt-10">
                  <div>
                      <div class="text_color6">{{ t('截止日') }}</div>
                      <div>{{ getTime(item.endSubscribeDate) }}</div>
                  </div>
                  <div>
                      <van-circle :layer-color="thStore.THEME === 'dark' ? '#27293B': '#D2D7E6'" :stroke-width="100" color="#F43368"
                          v-model:current-rate="currentRate" :rate="30" :speed="100" size="70px" :text="text">
                          <div class="circle-text">
                              <div>{{ item.priceDifferenceValue }}%</div>
                              <div>{{ t('溢差价') }}</div>
                          </div>
                      </van-circle>
                  </div>
                  <div class="text-right">
                      <div class="text_color6">{{ t('总抽签数') }}</div>
                      <div>{{ item.subscribeTotalNumber }}</div>
                      <div  v-if="item.status == 2 && (item.shareStatus === 1 || item.shareStatus === 2)" :class="item.shareStatus === 1 ? 'drawlots-but' : 'drawlots-but red'" @click="goToDetail(item.uuid,item.shareStatus)">{{ item.shareStatus === 1 ? t('抽签') : t('认缴') }} <van-icon name="arrow" /></div>
                  </div>
              </div>
          </div>
      </van-list>
  </div>
</template>

<script setup>
import {onMounted, ref, computed, inject} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { newSharesList } from '@/service/ipo.api.js'
import { useI18n } from "vue-i18n";
import { themeStore } from '@/store/theme';
const thStore = themeStore()

const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const list = ref([]);
const loading = ref(false);
const finished = ref(false);
const currentRate = ref(0);
const index = ref(1)

onMounted(() => {
})

const stockType = inject('stockType')
const stockActive = inject('stockActive')
const onLoad = () => {
  console.log('stockType', stockType)
  let params = {
    current: index.value,
    size: 10,
    type: stockType || 'US-stocks'
  }
  newSharesList(params).then(res => {
    if(res.length) {
      list.value = list.value.concat(res)
    }
    index.value++
    loading.value = false;
    if (res.length < 10) {
      finished.value = true;
    }
  })
};


const goToDetail = (id,type) => {
  console.log('id', id)
  router.push({
      path: `/ipo/${type == 1 ? 'drawLotsDetail' : 'subscribeDetail'}`,
      query: {
          id,
          stockType: stockType || 'US-stocks',
          stockActive: stockActive
      }
  })
}

const getTime = (time) => {
return time.split(' ')[0];
}

</script>
<style lang="scss" scoped>
.newIpo {
  .list-div {
      padding: 20px 0;

      .circle-text {
          font-size: 12px;
          padding-top: 10px;
      }

      .drawlots-but {
          display: flex;
          align-items: center;
          justify-content: center;
          border: 1px solid $active_line;
          width: 80px;
          height: 30px;
          margin-top: 5px;
          border-radius: 5px;
          color: $color-main;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;

          &.red{
            border-color: $red;
            color: $red;
          }


      }
      .normal{
        color: $text_color;
      }
  }
}
</style>