<template>
  <section class="inner-tab-container">
    <van-loading color="#1194F7" class="loading-box" v-if="isLoading" />
    <section class="banner-container">
      <div class="banner-item" v-if="type === 'indices'">
        <div class="swipe-container">
          <h1 class="h1-title">{{ t('ETFAccount') }}</h1>
          <p class="flex justify-between">
            <span>{{ t('AccountAssets') }}</span>
          </p>
          <ul>
            <li class="flex line">
              <div class="flex-1">
                <div>{{ t('ETFTotalAssets') }}</div>
                <p class="value">{{ assets?.totalAssets ?? '--' }}</p>
              </div>
              <div class="flex-1">
                <p class="label">{{ t('ETFTotalLoss') }}</p>
                <p class="value">{{ assets?.profit ?? '--' }}</p>
              </div>
              <div class="flex-1">
                <div>
                  <p>{{ t('ETFTheDay') }} </p>
                </div>
                <p class="value">{{ assets?.profitTotal ?? '--' }}</p>
              </div>
            </li>
            <li class="flex line">
              <div class="flex-1">
                <p class="label"> {{ t('ETFAvailableBalance') }}</p>
                <p class="value">{{  Number(assets?.usdtBalance).toFixed(2) ?? '--' }}</p>
              </div>
              <div class="flex-1">
                <p>{{ t('ETFsAreDesirable') }}</p>
                <p class="value">{{  Number(assets?.usdtBalance).toFixed(2) ?? '--' }}</p>
              </div>
              <div class="flex-1">
              </div>
            </li>
          </ul>
        </div>
      </div>
      <div class="banner-item" v-if="type === 'US-stocks'">
        <div class="swipe-container">
          <h1 class="h1-title">{{ t('UStockAccount') }}</h1>
          <p class="flex justify-between  mt-5">
            <span>{{ t('AccountAssets') }}</span>
          </p>
          <ul>
            <li class="flex line">
              <div class="flex-1">
                <div>{{ t('USStockTotalAssets') }}</div>
                <p class="value">{{ assets?.totalAssets ?? '--' }}</p>
              </div>
              <div class="flex-1">
                <p class="label">{{ t('UStotalLoss') }}</p>
                <p class="value">{{ assets?.profit ?? '--' }}</p>
              </div>
              <div class="flex-1">
                <div>
                  <p>{{ t('USStockProfitDay') }}</p>
                </div>
                <p class="value">{{ assets?.profitTotal ?? '--' }}</p>
              </div>
            </li>
            <li class="flex line">
              <div class="flex-1">
                <p class="label">{{ t('USStockBalance') }}</p>
                <p class="value">{{  Number(assets?.usdtBalance).toFixed(2) ?? '--' }}</p>
              </div>
              <div class="flex-1">
                <p>{{ t('USdesirable') }}</p>
                <p class="value">{{  Number(assets?.usdtBalance).toFixed(2) ?? '--' }}</p>
              </div>
              <div class="flex-1">
              </div>
            </li>
          </ul>
        </div>
      </div>
      <div class="banner-item" v-if="type === 'HK-stocks'">
        <div class="swipe-container">
          <h1 class="h1-title">{{ t('HKStockAccount') }}</h1>
          <p class="flex justify-between  mt-5">
            <span>{{ t('AccountAssets') }}</span>
          </p>
          <ul>
            <li class="flex line">
              <div class="flex-1">
                <div>{{ t('HKStockTotalAssets') }}</div>
                <p class="value">{{ assets?.totalAssets ?? '--' }}</p>
              </div>
              <div class="flex-1">
                <p class="label">{{ t('HKtotalLoss') }}</p>
                <p class="value">{{ assets?.profit ?? '--' }}</p>
              </div>
              <div class="flex-1">
                <div>
                  <p>{{ t('HKStockProfitDay') }}</p>
                </div>
                <p class="value">{{ assets?.profitTotal ?? '--' }}</p>
              </div>
            </li>
            <li class="flex line">
              <div class="flex-1">
                <p class="label">{{ t('HKStockBalance') }}</p>
                <p class="value">{{  Number(assets?.usdtBalance).toFixed(2) ?? '--' }}</p>
              </div>
              <div class="flex-1">
                <p>{{ t('HKdesirable') }}</p>
                <p class="value">{{  Number(assets?.usdtBalance).toFixed(2) ?? '--' }}</p>
              </div>
              <div class="flex-1">
              </div>
            </li>
          </ul>
        </div>
      </div>
    </section>
    <section class="etf-container">
      <div class="all-etf-ranking">
        <div class="etf-table">
          <ul>
            <li class="title-line">
              <div class="flex-l flex items-center">
                <p>{{ t('marketValue') }}</p>
              </div>
              <div class="flex-r">
                <div class="flex-r-item">
                  <p>{{ t('totalLoss') }}</p>
                </div>
                <div class="flex-r-item">
                  <p>{{ t('openAvailable') }}</p>
                </div>
                <div class="flex-r-item">
                  <p>{{ t('costCurrentPrice') }}</p>
                </div>
                <div class="flex-r-item">
                  <p>{{ t('ProfitDay') }}</p>
                </div>
              </div>
            </li>
            <li v-for="(item) in etfList" :key="item.symbol" @click="itemClick(item)">
              <div class="flex-l">
                <p>{{ item.symbol }}</p>
                <p class="gray-text">{{ item.marketValue }}</p>
              </div>
              <div class="flex-r">
                <div class="flex-r-item">
                  <p :class="item.open < 1 ? 'text-up' : 'text-down'">{{
                    item.profitLoss
                  }}</p>
                  <p :class="item.open < 1 ? 'text-up' : 'text-down'">{{
                    item.profitLossPercentage && item.profitLossPercentage !== 0 ?
                    `${item.profitLossPercentage}%` : 0
                  }}</p>
                </div>
                <div class="flex-r-item">
                  <p :class="item.close < 1 ? 'text-up' : 'text-down'">{{
                    item.positionVolume
                  }}</p>
                  <p :class="item.close < 1 ? 'text-up' : 'text-down'">{{
                    item.volume
                  }}</p>
                </div>
                <div class="flex-r-item">
                  <p :class="item.close < 1 ? 'text-up' : 'text-down'">{{
                    item.price
                  }}</p>
                  <p :class="item.close < 1 ? 'text-up' : 'text-down'">{{
                    item.currentPrice
                  }}</p>
                </div>
                <div class="flex-r-item">
                  <p :class="item.close < 1 ? 'text-up' : 'text-down'">{{
                    item.toDayProfitLoss
                  }}</p>
                  <p :class="item.close < 1 ? 'text-up' : 'text-down'">{{
                    item.toDayProfitLossPercentage && item.toDayProfitLossPercentage !== 0 ?
                    `${item.toDayProfitLossPercentage}%` : 0
                  }}</p>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </section>
  </section>
</template>
    
<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useUserStore } from '@/store/user';
import { useRoute, useRouter } from 'vue-router';
import { _getETFList } from "@/service/etf.api";
import { _assetsTradeTop } from "@/service/user.api";
import { _getQuotes } from '@/service/quotes.api'
import { useI18n } from 'vue-i18n'

const props = defineProps({
  isSell: {
    type: Boolean,
    default: false
  }
})
const userStore = useUserStore()
const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const etfList = ref([])
const assets = ref({})
const interval = ref(null)
const { type } = route.query
const isLoading = ref(false)

onMounted(async () => {
  if (!userStore.userInfo.token) {
    router.push('/login')
    return
  }
  getETFList()
  getETFSum()
  // interval.value = setInterval(() => {
  //   getETFList()
  // }, 1000)
})

onBeforeUnmount(() => {
  // if (interval.value) {
  //   clearInterval(interval.value)
  // }
})

const getETFList = () => {
  _getETFList(type).then(data => {
    etfList.value = data
  })
}

const getETFSum = () => {
  isLoading.value = true;
  _assetsTradeTop({
    symbolType: type,
    tradeType: type == 'cryptos' ? 'contract' : 'exchange'
  }).then(res => {
    isLoading.value = false;
    assets.value = res
  }).catch((e) => {
    isLoading.value = false;
  });
}

const itemClick = (item) => {
  if (type === 'indices') {
    router.push(`/quotes/detail?symbol=${item.symbol}&from=position&symbolType=indices`)
  } else if (type === 'US-stocks') {
    router.push(`/quotes/usStockDetail?symbol=${item.symbol}&from=position&symbolType=US-stocks`)
  } else if (type === 'HK-stocks') {
    router.push(`/quotes/usStockDetail?symbol=${item.symbol}&from=position&symbolType=HK-stocks`)
  } else {
    router.push(`/quotes/detail?symbol=${item.symbol}&from=position&symbolType=A-stocks&type=A-stocks`)
  }
}

</script>
<style lang="scss" scoped>
.loading-box {
  position: absolute;
  top: 200px;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 999;
}

.container-box {
  .h1-title {
    font-size: 16px;
  }

  .inner-tab-container {
    margin-top: 8px;
  }

  .banner-container {
    height: 100%;

    .banner-item {
      box-sizing: border-box;
      padding: 12px;
      color: $text_color;
      font-size: 12px;
      background-color: $selectSymbol_background;

      .line {
        margin-top: 10px;
      }
    }
  }

  .etf-container {
    padding: 0;

    .all-etf-ranking {
      margin-top: 10px;

      .title {
        font-weight: 700;
        padding: 0 12px;
      }

      .tabs {
        padding: 0 12px;
        margin-top: 10px;
        // height: 40px;
        min-height: 40px;
        line-height: 24px;
        color: #BBBCBD;

        .tab-item {
          margin: 4px;
          text-align: center;
          padding: 4px 6px;
          font-size: 12px;
          color: $text_color5;
          background: $US_tab_background;
          border-radius: 10px;
          background-size: cover;
        }

        .active {
          font-weight: 700;
          color: $color_main !important;
          background: $US_tabActice_background;
          border-radius: 10px;
          background-size: cover;
        }
      }

      .etf-table {
        .right {
          text-align: right;
        }

        ul {
          margin-top: 10px;
        }

        .title-line {
          color: #747A8F;
          font-size: 12px;
          font-weight: 400;
          padding: 0 12px;
          border: none;
        }

        li {
          padding: 14px 12px;
          display: flex;
          font-size: 12px;
          line-height: 18px;
          border-bottom: 1px solid $border_color;

          .gray-text {
            color: #BCBDC2;
            font-size: 12px;
          }

          .flex-l {
            width: 100px;
          }

          .flex-r {
            display: inline-flex;
            flex: 1;

            .flex-r-item {
              flex: 1;
              align-self: center;
              text-align: center;
            }
          }
        }
      }
    }
  }
}
</style>