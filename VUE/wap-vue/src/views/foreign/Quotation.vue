<template>
  <div class="pb-fix">
    <van-loading color="#1194F7" class="loading-box" v-if="isLoading && isFirst" />
    <div class="container-box">
      <header class="header">
        <div class="icon-group">
          <div class="icon back" @click="onRoute('/quotes/index?tabActive=2')">
            <van-icon name="arrow-left" size="20" />
          </div>
          <div class="icon search" @click="onRoute('/foreign/search')">
            <img src="@/assets/image/optional/search.png" alt="">
          </div>
        </div>
        <div class="title">{{ t('Market') }}</div>
      </header>
      <div class="tabs flex">
        <div class="tab-item flex-1" v-for="(item, index) in tabList" @click="selectIndex(index, item)"
          :class="[tabIndex === index ? 'active' : '']" :key="item">
          {{ item.title }}
        </div>
      </div>
      <div class="symbol-table">
        <ul>
          <li v-for="(item, index) in listData" :key="item.symbol" @click="itemClick(item)">
            <div class="flex-l">
              <p>
                <span class="mr-1" :class="item.changeRatio > 0 ? 'text-up' : 'text-down'" v-if="item.netChange > 0">+{{
                  item.netChange }}</span>
                <span class="mr-1" :class="item.changeRatio > 0 ? 'text-up' : 'text-down'" v-else>{{ item.netChange
                }}</span>&nbsp;
                <span :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">{{ `${item.changeRatio}%` }}</span>
              </p>
              <p>{{ item.symbol.toUpperCase() }}</p>
              <p class="gray-text">{{ item.current_time ? item.current_time.slice(11) : '-' }}</p>
            </div>
            <div class="flex-r">
              <div class="flex justify-end h-full flex-wrap">
                <div class="flex-r-item">
                  <p class="flex text-primary justify-end" :class="item.open < 1 ? 'text-up' : 'text-down'">
                    <span class="font-semibold text-lg">{{
                      item.open.toString().substr(0, item.open.toString().length
                        - 1)
                    }}</span>
                    <span class="text-xs">{{ item.open.toString().substr(item.open.toString().length - 1) }}</span>
                  </p>
                  <div class="text-gray">
                    <span class="mr-1.5">H: {{ item.high }}</span>
                  </div>
                </div>
                <div class="last flex-r-item">
                  <p class="flex text-primary justify-end" :class="item.close < 1 ? 'text-up' : 'text-down'">
                    <span class="font-semibold text-lg">{{
                      item.close.toString().substr(0, item.close.toString().length
                        - 1)
                    }}</span>
                    <span class="text-xs">{{ item.close.toString().substr(item.close.toString().length - 1) }}</span>
                  </p>
                  <div class="text-gray">
                    <span>L: {{ item.low }}</span>
                  </div>
                </div>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { _getRealtimeByType, _ItemUserOptionalItemAdd } from '@/service/quotes.api'
import { useQuotesStore } from '@/store/quotes.store'
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";

const { t } = useI18n()
const router = useRouter()
const tabIndex = ref(0)
const listData = ref([])
const interval = ref(null)
const type = ref('forex') //默认查询外汇
const category = ref('forex') //默认查询外汇
const tabList = ref([
  { title: t('外汇'), category: "forex", type: "forex" },
  { title: t('大宗商品'), category: "commodities", type: "forex" },
  { title: t('指数'), category: "indices", type: "forex" },
  { title: t('加密货币'), category: "cryptos", type: "cryptos" },
])
const isLoading = ref(false)
const isFirst = ref(true)

onMounted(() => {
  fetchQuotes()
  interval.value = setInterval(() => {
    fetchQuotes()
  }, 3000)
})

onBeforeUnmount(() => {
  if (interval.value) {
    clearInterval(interval.value)
  }
})

const fetchQuotes = () => {
  isLoading.value = true
  _getRealtimeByType({
    type: type.value,
    category: category.value,
    pageNo: 1
  }).then(data => {
    isLoading.value = false
    isFirst.value = false
    listData.value = data
  }).catch((e) => {
    isLoading.value = false
    isFirst.value = true
    listData.value = []
  })

}

const itemClick = (item) => {
  router.push({ path: '/foreign/coinChart', query: { symbol: item.symbol } })
}

const onRoute = (path) => {
  router.push(path)
}

const selectIndex = (index, item) => {
  tabIndex.value = index
  type.value = item.type
  category.value = item.category
  fetchQuotes()
}

</script>

<style lang="scss" scoped>
:deep(.van-loading) {
  position: absolute;
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 999;
}

:deep(.van-nav-bar__title) {
  position: relative;
  top: 2px;
  max-width: 72%;
}

:deep(.van-nav-bar__title) {
  flex: 1;
}

:deep(.van-field__control) {
  font-size: 16px;
  font-weight: 500;
  caret-color: #3157BE;
}

.container-box {
  padding: 0 16px;

  .header {
    .title {
      margin: 30px 0;
      font-weight: 600;
      font-size: 31px;
      color: $text_color;
    }

    .icon-group {
      display: flex;
      justify-content: space-between;

      .icon {
        display: inline-block;
        width: 36px;
        height: 36px;
        padding: 8px;
      }
    }
  }

  .tabs {
    background-color: $US_tab_background;
    border-radius: 20px;
    height: 42px;
    line-height: 26px;
    color: #BBBCBD;

    .tab-item {
      margin: 4px;
      text-align: center;
      padding: 4px 6px;
      font-size: 14px;
      color: $text_color5;
      font-weight: 700;
    }

    .active {
      color: #FBFCFD !important;
      background: $blue;
      border-radius: 16px;
      background-size: cover;
    }
  }

  .symbol-table {
    li {
      margin-top: 20px;
      display: flex;
      font-weight: 600;
      font-size: 14px;
      line-height: 18px;

      .gray-text {
        color: #BCBDC2;
        font-size: 12px;
      }

      .flex-l {
        width: 130px;
      }

      .flex-r {
        flex: 1;

        .flex-r-item {
          flex: 1;
          align-self: center;
          text-align: right;

          .last-item {
            padding: 0 6px;

            p {
              text-align: right;
              width: 70px;
              height: 24px;
              line-height: 24px;
              border-radius: 4px;
              color: $text_color;
              font-weight: 600;
              font-size: 14px;
            }
          }
        }

        .last {}
      }
    }
  }


}
</style>