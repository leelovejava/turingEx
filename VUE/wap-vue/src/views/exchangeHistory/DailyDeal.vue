<template>
  <section class="pb-fix">
    <div class="container-box">
      <header class="header">
        <div class="flex-l">
          <div class="icon back">
            <van-icon name="arrow-left" size="20" @click="onRoute('/quotes/openTrade')" />
          </div>
          <span class="title">当日成交</span>
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
          <van-tab title="当日成交">
            <DealTab />
          </van-tab>
          <van-tab title="当日成交汇总">
            <DealTab2 />
          </van-tab>
        </van-tabs>
      </section>
    </div>
  </section>
</template>
    
<script setup>
import { ref, onMounted, onBeforeMount } from 'vue';
import { useUserStore } from '@/store/user';
import { useRoute, useRouter } from 'vue-router';
import { _getQuotes } from '@/service/quotes.api'
import { useI18n } from 'vue-i18n'
import { useQuotesStore } from '@/store/quotes.store'
import DealTab from './components/DealTab.vue'
import DealTab2 from './components/DealTab2.vue'

const quotesStore = useQuotesStore()
const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const allEtfTabIndex = ref(0)
const allEtfListData = ref([])
const interval = ref(null)
const symbolType = ref('forex') //默认查询外汇
const price = ref('')
const tabActive = ref('')

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

}
</style>