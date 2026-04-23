<template>
  <section class="pb-fix">
    <div class="container-box">
      <header class="header">
        <div class="flex-l">
          <div class="icon back">
            <van-icon name="arrow-left" size="20" @click="onRoute" />
          </div>
          <p class="title">
            <img :src="handleImage(leftIcon)" alt="convert-img" class="convert-img" @click="onSidebar" />
            <span class="text">{{ `${symbolName} (${symbol})` }}</span>
          </p>
        </div>
        <div class="icon-group">
          <div class="icon search" @click="router.go(0)">
            <img :src="handleImage(refreshIcon)" alt="">
          </div>
        </div>
      </header>
      <section class="tab-container">
        <van-tabs v-model:active="tabActive" @click-tab="onClickTab">
          <van-tab :title="t('买入')">
            <BuySellTradeTab :symbolFlag="symbol" :isSell="false" ref="buyRef" @updateActive="updateActive" />
          </van-tab>
          <van-tab :title="t('卖出')">
            <BuySellTradeTab :symbolFlag="symbol" :isSell="true" ref="sellRef" @updateActive="updateActive" />
          </van-tab>
          <van-tab :title="t('撤单')">
            <Withdrawal />
          </van-tab>
          <van-tab :title="t('持仓')">
            <Position />
          </van-tab>
          <van-tab :title="t('查询')">
            <Search />
          </van-tab>
        </van-tabs>
      </section>
    </div>
    <!-- 左侧边弹出菜单 -->
    <van-popup class="popup" round v-model:show="show" close-icon-position="top-left" position="left" @closed="onClose">
      <div class="flex pl-10 pr-10 justify-between mb-10 mt-10 popup-wrap ">
        <div class="flex items-center text-grey">
          <div class="mr-12 popup-title">{{ $t('名称') }}</div>
        </div>
        <div class="flex text-grey">
          <div class="flex items-center">
            <div class="popup-title">{{ $t('最新价格') }}</div>
          </div>
          <div class="flex items-center">
            <div class="mr-12 popup-title">/24H{{ $t('涨跌') }}</div>
          </div>
        </div>
      </div>
      <div class="pl-10 pr-10  popup-wrap">
        <div class="flex justify-between mb-50 mt-10" v-for="item in iconList" :key="item.name" @click="onRouteTwo(item)">
          <div>
            <div class="font-bold textColor popup-title">{{ item.name || '--' }}</div>
            <div v-if="!kineType" class="text-grey mt-4 popup-title">{{ title }}</div>
          </div>
          <div class="text-right">
            <div class="textColor popup-title">{{ item.close || '--' }}</div>
            <div class="mt-1 popup-title" :class="item.change_ratio > 0 ? 'text-green' : 'text-red'">{{
              item.change_ratio
            }}%
            </div>
          </div>
        </div>
      </div>
    </van-popup>
  </section>
</template>

<script setup>
import { ref, onMounted, onBeforeMount, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { _queryBySymbol } from '@/service/etf.api'
import { useI18n } from 'vue-i18n'
import { useQuotesStore } from '@/store/quotes.store'
import BuySellTradeTab from './components/BuySellTradeTab.vue'
import Withdrawal from './components/Withdrawal.vue'
import Position from './components/Position.vue'
import Search from './components/Search.vue'
import { _getHomeList } from "@/service/home.api";
import { _getCoins } from '@/service/cryptos.api'
import { themeStore } from '@/store/theme';

const thStore = themeStore()
const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const defaultTabActive = +route.query.tabActive || 0
const tabActive = ref(defaultTabActive)
const symbolType = route.query.type
console.log('symbolType', symbolType)
const symbol = ref('')
const symbolName = ref(route.query.symbolName)
const quotesStore = useQuotesStore()

const show = ref(false)
const iconList = ref([])
const coins = ref([])
const timeout = ref(null)
const buyRef = ref()
const sellRef = ref()

const leftIcon = new URL(`../../assets/theme/${thStore.theme}/image/black-convert.png`, import.meta.url)
const refreshIcon = new URL(`../../assets/theme/${thStore.theme}/image/refresh.png`, import.meta.url)

onMounted(async () => {
  if (route.query.symbol) {
    symbol.value = route.query.symbol
  } else {
    symbol.value = quotesStore.coins.length ? quotesStore.coins[0].symbol : 'GlobalETF500'
  }
  querySymbolName()
})

onBeforeMount(() => {
  if (timeout.value) {
    clearTimeout(timeout.value)
  }
})

watch(show, (newVal) => {
  if (!newVal) {
    if (timeout.value) {
      clearTimeout(timeout.value)
    }
  }
})
//获取币种
const getCoins = () => {
  _getCoins({ type: symbolType }).then((res) => {
    coins.value = res.map(item => item.symbol)
    fetchList()
  })
}
const onSidebar = () => { // 侧边栏打开
  show.value = true
  getCoins()
}
const fetchList = () => { // 获取行情
  _getHomeList(coins.value.join(',')).then(list => {
    iconList.value = list
    if (timeout.value) {
      clearTimeout(timeout.value)
      timeout.value = null
    }
    timeout.value = setTimeout(() => {
      fetchList()
    }, 2000)
  })
}

const querySymbolName = () => {
  _queryBySymbol(symbol.value).then(data => {
    symbolName.value = data.enName ? data.enName : data.name
  })
}

const onRoute = () => {
  router.go(-1)
}

const onRouteTwo = (item) => {
  symbol.value = item.symbol
  router.push(`/quotes/openTrade?tabActive=${tabActive.value}&symbol=${item.symbol}&type=${symbolType}`)
  show.value = false
  querySymbolName()
}


const updateActive = () => {
  tabActive.value = 4
}

const onClose = () => {
  show.value = false
}
const onClickTab = () => {
  if (tabActive.value === 0) {
    buyRef.value.loadInit()
  } else if (tabActive.value === 1) {
    sellRef.value.loadInit()
  }
}
const handleImage = (url) => {
  return new URL(url, import.meta.url).href
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

:deep(.van-button--danger) {
  background-color: $red;
}

:deep(.trade-order-area .van-field) {
  padding: 4px 6px;
  background: $selectSymbol_background;
  border: 1px solid $red;
  border-radius: 2px;
}

:deep(.trade-order-area .van-field__control) {
  color: $text_color;
}

:deep(.van-stepper__minus) {
  background: $input_background;
  border: 1px solid $red;
  border-radius: 2px;
  color: $red;
}

:deep(.van-stepper__plus) {
  background: $input_background;
  border: 1px solid $red;
  border-radius: 2px;
  color: $red;
}

:deep(.van-stepper__input) {
  background: $input_background;
  color: $text_color;
  border-top: 1px solid $red;
  border-bottom: 1px solid $red;
  margin: 0;
}

.container-box {
  .green {
    color: $green;
  }

  .red {
    color: $red;
  }

  .header {
    display: flex;
    height: 28px;
    padding: 0 12px;
    margin-top: 10px;

    .convert-img {
      width: 20px;
      height: 20px;
      margin-right: 10px;
    }


    .flex-l {
      flex: 1;
      display: inline-flex;

      .icon {
        margin-right: 10px;
        display: inline-block;
        width: 24px;
        height: 28px;
        padding: 4px 4px;

        img {
          height: 20px;
          width: 20px;
        }
      }
    }

    .title {
      flex: 1;
      text-align: center;
      font-weight: 700;
      font-size: 14px;
      line-height: 28px;
      color: $mainTextColor;
      display: flex;
      align-items: center;
      justify-content: center;
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
    margin-top: 18px;
  }

  .orderbook-container {
    padding: 0 12px 16px;

    .trade-order-area {
      display: flex;
      flex-direction: column;
      width: 210px;
      font-size: 12px;
      margin-right: 12px;

      .line-first {
        display: flex;
      }

      .line-first-l {
        flex: 1;
      }

      .line-first-r {
        width: 50px;
        padding: 0 2px;
        border: 1px solid $border_color;
        border-radius: 2px;
        color: $text_color6;
      }

      .buy-btn {
        height: 32px;
      }

      .name-input {
        margin-top: 10px;
      }

      .price-input {
        margin-top: 10px;
      }

      .number-input {
        margin-top: 10px;
      }

      .value-input {
        margin-top: 10px;
      }

      .buy-number {
        margin-top: 10px;
      }

      .num {
        color: #E19841;
        margin: 0 4px;
      }

      .freight {
        margin: 10px 0;
        display: flex;

        span {
          flex: 1;
          background: #1B2134;
          border-radius: 2px;
          text-align: center;
        }

        span:not(:last-child) {
          margin-right: 8px;
        }
      }

      .desc {
        margin-top: 10px;
        display: flex;
        justify-content: space-between;
      }
    }

    .trade-deep-data {
      flex: 1;
    }

    .line-first {
      display: flex;
    }

    .select-label {
      padding-left: 4px;
    }

    .select-icon {
      height: 11px;
      width: 14px;
      padding-right: 4px;
    }

    .option-box {
      position: absolute;
      left: 0;
      right: 0;
      top: 5.625rem;
      width: 100%;
      background-color: $border-grey;
      text-align: center;
      box-shadow: 0 0 0.1875rem 0.1875rem $border-grey;
      border-radius: 0.25rem;
      color: $text_color4;

      >div {
        padding: 1.875rem 0;
      }
    }
  }

  .indicator-index-container {
    .indicator-index-box {
      padding: 12px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .flex-l {
        flex: 1;

        ul {
          display: flex;

          li {
            flex: 1;
            text-align: center;
            margin: 0 4px;
            font-size: 12px;
            border-radius: 4px;
          }
        }
      }

      .flex-r {
        display: flex;
        justify-content: flex-end;
        width: 80px;

        img {
          width: 12px;
          height: 16px;
        }

      }
    }

    .active {
      background: $btn_main;
      color: $white;
    }
  }

  .etf-container {
    padding: 0;

    .hot-container {
      margin: 20px 0;
      padding: 0 8px;

      .header-box {
        display: flex;

        .title {
          flex: 1;
          font-size: 16px;
          padding: 0 8px;
          font-weight: 700;
        }

        .icon-group {
          display: flex;
          align-items: center;

          .icon.arrow {
            margin-left: 10px;
            width: 7px;
            height: 9px;
          }
        }

      }

      .hot-box {
        display: grid;
        grid-template-columns: 33.33% 33.33% 33.33%;
        grid-template-rows: repeat(2, 100px);
      }

      .hot-item {
        padding: 6px 2px;
        margin: 4px;
        text-align: center;
        font-size: 12px;
        line-height: 18px;
        color: #B8BDD1;
        background: #1B2134;
        border-radius: 4px;

        .value {
          font-weight: 700;
          color: $text_color;
          line-height: 24px;
        }

        .num {
          .num-left {
            margin-right: 6px;
          }
        }
      }
    }

    .all-etf-ranking,
    .other-etf-ranking {
      margin-top: 10px;

      .title {
        font-weight: 700;
        padding: 0 12px;
      }

      .tabs {
        padding: 0 12px;
        margin-top: 10px;
        height: 40px;
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

      .more-box {
        height: 40px;

        .icon-group {
          display: flex;
          justify-content: center;
          align-items: center;
          height: 40px;
          line-height: 40px;
          font-size: 14px;

          .icon.arrow {
            margin-left: 10px;
            width: 7px;
            height: 9px;
          }
        }
      }

    }

  }
}

:deep(.van-popup) {
  height: 100%;
  width: 300px;
  background: $main_background;
}

.text-green {
  color: $green;
}

.text-red {
  color: $red;
}

.popup-wrap {
  font-size: 16px;

  .popup-title {
    font-size: 14px;
  }
}
</style>
