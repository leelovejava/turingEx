<template>
  <section class="pb-fix">
    <div class="container-box">
      <header class="header">
        <div class="flex-l">
          <div class="icon back">
            <van-icon name="arrow-left" size="20" @click="onRoute('/quotes/openTrade')" />
          </div>
          <span class="title">{{ t('历史委托') }}</span>
        </div>
        <div class="icon-group">
          <div class="icon refresh">
            <img src="@/assets/image/quotes/refresh.png" alt="">
          </div>
          <div class="icon search" @click="onRoute('/exchangeHistory/search')">
            <img src="@/assets/image/optional/search.png" alt="">
          </div>
        </div>
      </header>
      <section class="tab-container">
        <van-tabs v-model:active="tabActive">
          <van-tab title="近7日" />
          <van-tab title="近30日" />
          <van-tab title="自定义" />
        </van-tabs>
        <div class="select-date-container flex" v-if="tabActive === 2">
          <div class="flex-l">
            <div class="flex-l-item" @click="showCalendar = true">
              <span>{{ startTime }}</span>
            </div>
            <span class="symbol">～</span>
            <div class="flex-l-item" @click="showCalendar = true">
              <span>{{ endTime }}</span>
            </div>
          </div>
          <div class="flex-r">
            <van-button type="primary" size="small">确定</van-button>
          </div>
          <van-calendar v-model:show="showCalendar" type="range" @confirm="onConfirm" :confirm-text="$t('confirm')"
            :confirm-disabled-text="$t('confirm')" :formatter="formatter" :title="$t('selectDate')" :min-date="minDate"
            :max-date="maxDate" />
        </div>
        <HistoricalEntrustTab ref="tabRef" :tabActive="tabActive" :startTime="startTime" :endTime="endTime" />
      </section>
    </div>
  </section>
</template>
    
<script setup>
import { ref, onMounted, onBeforeMount } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { _getQuotes } from '@/service/quotes.api'
import { useI18n } from 'vue-i18n'
import HistoricalEntrustTab from './components/HistoricalEntrustTab.vue'
import dayjs from 'dayjs';

const { t } = useI18n()
const router = useRouter()
const tabRef = ref()
const tabActive = ref(0)
const minDate = ref(new Date(new Date().getFullYear(), 0, 1));
const maxDate = ref(new Date());
const showCalendar = ref(false)   // 自定义日期筛选组件
const startTime = ref(dayjs().add(-90, 'd').format('YYYY-MM-DD')) // 默认最近三个月
const endTime = ref(dayjs().format('YYYY-MM-DD'))

onMounted(async () => {


})

onBeforeMount(() => {

})

const onRoute = (path) => {
  router.push({
    path,
    query: {
      tabActive: 3
    }
  })
}
const onConfirm = (prop) => {
  startTime.value = dayjs(prop[0]).format('YYYY-MM-DD')
  endTime.value = dayjs(prop[1]).format('YYYY-MM-DD')
  tabRef.value.getOrderList()
  showCalendar.value = false
}

</script>
<style lang="scss" scoped>
:deep(.van-tabs__nav) {
  background: $selectSymbol_background;
}

:deep(.van-tab) {
  font-size: 14px;
  color: $text_color;
  font-weight: 400;
}

:deep(.van-tab.van-tab--active) {
  font-weight: 700;
}


.container-box {
  .header {
    position: relative;
    display: flex;
    height: 28px;
    padding: 0 12px;

    .flex-l {
      flex: 1;
      display: inline-flex;

      .icon {
        display: inline-block;
        width: 24px;
        height: 28px;
        padding: 6px 4px;

        img {
          height: 20px;
          width: 20px;
        }
      }
    }

    .title {
      position: absolute;
      left: 50%;
      transform: translateX(-50%);
      flex: 1;
      text-align: center;
      font-weight: 700;
      font-size: 16px;
      line-height: 28px;
      color: $mainTextColor;
    }

    .icon-group {
      text-align: right;

      .icon {
        display: inline-block;
        width: 28px;
        height: 28px;
        padding: 4px;
        margin-left: 16px;
      }
    }


  }

  .tab-container {
    margin-top: 8px;
  }

  .select-date-container {
    margin-top: 8px;
    padding: 0 30px;
    justify-content: space-between;

    .flex-l {
      flex: 1;
      display: flex;
      align-items: center;
      font-size: 12px;

      .flex-l-item {
        border: 1px solid $border_color;
        border-radius: 2px;
        padding: 2px 8px;
      }

      .symbol {
        margin: 0 10px;
      }
    }

    .flex-r {
      width: 80px;
      text-align: right;
    }
  }


}
</style>