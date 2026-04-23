<template>
  <div class="pb-fix">
    <div class="container-box">
      <div class="search-container flex items-center">
        <van-search class="search-input" v-model="searchVal" @update:model-value="onClickButton" :placeholder="$t('searchKeys')"
          @clear="onClearSearch" @cancel="cancelBack()" shape="round" show-action :action-text="$t('Cancel')">
        </van-search>
      </div>
      <div class="hot-search">
        <p class="title">{{ t('hotSearch') }}</p>
        <ul class="hot-box">
          <li class="hot-item" v-for="(item, index) in hotSymbolList" :key="index" @click="itemClick(item)">
            <p>
              <span class="num">{{ item.symbol.toUpperCase() }}</span>
              <span>{{ item.name }}</span>
            </p>
            <p>{{ getCurrencyName(item) }}</p>
          </li>
        </ul>
      </div>

      <div class="search-result" v-if="searchVal">
        <div class="title-box flex ">
          <p class="title">{{ t('searchResult') }}</p>
          <!-- <div class="icon">
            <img src="@/assets/image/optional/del.png" alt="">
          </div> -->
        </div>
        <div class="symbol-table">
          <ul>
            <li class="flex" v-for="(item, index) in searchAllList" @click.stop="itemClick(item)">
              <div class="flex-l">
                <div class="flex-l-item logo">
                  <img :src="`${HOST_URL}/symbol/${item.symbol.toLowerCase()}.png`" alt="">
                </div>
                <div class="flex-l-item">
                  <p class="name-short">{{ item.symbol.toUpperCase() }}</p>
                  <p class="name">{{ item.name }}</p>
                </div>
              </div>
              <div class="flex-r">
                <div class="flex-r-item data-box">
                  <div class="data-item">
                    <span v-if="item.hasAddGlobal">{{ t('alreadyMyfavorites') }}</span>
                    <img v-else src="@/assets/image/optional/add-icon.png" @click.stop="openCurrency(item)" alt="">
                  </div>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>

      <div v-else class="search-result">
        <div class="title-box flex ">
          <p class="title">{{ t('历史记录') }}</p>
          <div class="icon">
            <img src="@/assets/image/optional/del.png" @click="deleteHistoryList" alt="">
          </div>
        </div>
        <div class="symbol-table">
          <ul>
            <li class="flex" v-for="(item, index) in historyAllList" @click.stop="itemClick(item)">
              <div class="flex-l">
                <div class="flex-l-item logo">
                  <img :src="`${HOST_URL}/symbol/${item.symbol.toLowerCase()}.png`" alt="">
                </div>
                <div class="flex-l-item">
                  <p class="name-short">{{ item.symbol.toUpperCase() }}</p>
                  <p class="name">{{ item.name }}</p>
                </div>
              </div>
              <div class="flex-r">
                <div class="flex-r-item data-box">
                  <div class="data-item">
                    <span v-if="item.hasAddGlobal">{{ t('alreadyMyfavorites') }}</span>
                    <img v-else src="@/assets/image/optional/add-icon.png" @click.stop="openCurrency(item)" alt="">
                  </div>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <add-currency @updateItem="getIsItemHasAddGlobal" ref="addCurrencyRef"></add-currency>
  </div>
</template>
  
<script setup>
import { ref, onMounted } from "vue";
import { _ItemUserOptionalItemAdd, _isItemHasAddGlobal, _getQuotes } from '@/service/quotes.api'
import { useQuotesStore } from '@/store/quotes.store'
import { useRouter, useRoute } from "vue-router";
import { useI18n } from "vue-i18n";
import { Search } from 'vant';
import { SET_HISTORY_LIST } from '@/store/types.store'
import { _getCoins } from '@/service/cryptos.api.js'
import store from '@/store/store'
import { HOST_URL } from '@/config'
import addCurrency from '@/components/add-currency/index.vue'
import { _getETFItemList, _getHkStocksItemList } from "@/service/etf.api.js";

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const quotesStore = useQuotesStore()
const allList = ref([])
console.log(route.query, 'route.query')
const symbolType = route.query?.symbolType || "US-stocks"
const searchList = ref([])
// 搜索
const searchVal = ref('');
const hotSymbol = ref([])
const hotSymbolList = ref([])
const searchAllList = ref([])
const addCurrencyRef = ref(null)
const selectInfo = ref({})
const historyAllList = ref([])
onMounted(() => {
  getETFItemList()
  allList.value = quotesStore.coins
  searchList.value = allList.value
  historyAllList.value = quotesStore.historyList
  // console.log(historyAllList.value);
  if (historyAllList.value.length > 0) {
    historyAllList.value.map((item) => {
      let obj = {
        symbol: item.symbol
      }
      _isItemHasAddGlobal(obj).then((data) => {
        item.hasAddGlobal = data
      })
    })
  }
})

const getETFItemList = () => {
  _getETFItemList({
    type: symbolType
  }).then(data => {
    if (data === null) {
      data = []
    }
    if (data.length > 0) {
      const result = data.slice(0, 9).map(item => item.symbol) || []
      hotSymbol.value = result
      getHotSymbol()
    }
  })
}

const onClearSearch = () => {
  searchVal.value = ''
  searchAllList.value = []
}

const itemClick = (data) => {
  let arryData = historyAllList.value.filter((item) => {
    return item.symbol == data.symbol
  })
  if (arryData.length == 0) {
    historyAllList.value.push(data)
  }
  quotesStore[SET_HISTORY_LIST](historyAllList.value)
  if (data.type == "cryptos") { //跳转需你币
    router.push('/cryptos/trendDetails/' + data.symbol + '?isOptional=2&type=cryptos')
  } else if (data.type == "indices") {
    router.push('/quotes/detail?symbol=' + data.symbol + '&isOptional=2&symbolType=indices&type=indices')
  } else if (data.type == "US-stocks") {
    router.push(`/quotes/usStockDetail?symbol=${data.symbol}&isOptional=2&symbolType=US-stocks`)
  } else if (data.type == "HK-stocks") {
    router.push(`/quotes/usStockDetail?symbol=${data.symbol}&isOptional=2&symbolType=HK-stocks`)
  } else if (data.type == "TW-stocks") {
    router.push(`/quotes/detail?symbol=${data.symbol}&isOptional=2&symbolType=TW-stocks&type=TW-stocks`)
  } else if (data.type == "A-stocks") {
    router.push(`/quotes/detail?symbol=${data.symbol}&isOptional=2&symbolType=A-stocks&type=A-stocks`)
  } else {
    router.push('/foreign/coinChart?symbol=' + data.symbol + '&isOptional=2')
  }
}
const onClickButton = () => {
  if (!searchVal.value) {
    searchAllList.value = []
    return
  }
  let obj = {
    name: searchVal.value
  }
  _getCoins(obj).then((res) => {
    console.log(res)
    searchAllList.value = res
    // searchAllList.value.map((item) => {
    //   let obj = {
    //     symbol: item.symbol
    //   }
    //   _isItemHasAddGlobal(obj).then((data) => {
    //     item.isCollect = data
    //   })
    // })
  })
}
const getIsItemHasAddGlobal = () => {
  // console.log(selectInfo.value);
  selectInfo.value.hasAddGlobal = true
}
//打开自选弹窗
const openCurrency = (item) => {
  selectInfo.value = item
  addCurrencyRef.value.openCurrency(selectInfo.value.symbol)
}
//删除自选
const deleteHistoryList = () => {
  historyAllList.value = []
  quotesStore[SET_HISTORY_LIST]([])
}
//获取热门行情
const getHotSymbol = () => {
  let arry = []
  hotSymbol.value.map((item) => {
    let objData = {
      symbol: item
    }
    arry.push(objData)
  })
  _getQuotes(arry).then((data) => {
    hotSymbolList.value = data
  })
}

const getCurrencyName = (item) => {
  let str = ''
  switch (item.type) {
    case 'forex':
      str = t('外汇')
      break;
    case 'US-stocks':
      str = t('UsStocks')
      break;
    case 'HK-stocks':
      str = t('HkStocks')
      break;
    case 'TW-stocks':
      str = t('TwStocks')
      break;
    case 'A-stocks':
      str = t('AStocks')
      break;
    default:
      break;
  }
  return str
}
//取消
const cancelBack = () => {
  router.go(-1)
}
</script>
  
<style lang="scss" scoped>
:deep(.van-field__control) {
  font-size: 14px;
  font-weight: 500;
  caret-color: #3157BE;
  color: $text_color;
}

:deep(.van-search__content) {

  background: $input_background;
  overflow: hidden;
}

:deep(.van-search) {
  display: flex;
  align-items: center;
  width: 100%;
}


:deep(.van-search__action) {
  color: $color_main;
  font-size: 12px;
}

:deep(.van-cell) {
  padding: 0 !important;
}

:deep(.van-search__action:active) {
  background: $selectSymbol_background;
  color: $color_main;
  font-size: 12px;
}

:deep(.search-result .van-cell) {
  background: $mainBgColor;
}

:deep(.van-search__field) {
  background: $input_background !important;
}

.search-container {
  width: 100%;
}

.container-box {
  padding: 16px;

  .search-input {
    height: 42px;
    display: flex;
    align-items: center;
    width: 100%;
    background-color: $mainBgColor;
  }

  .header {
    .title {
      margin: 30px 0;
      font-weight: 600;
      font-size: 31px;
      color: #303133;
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

      .icon-circle {
        border-radius: 10px;
        background-color: #F6F7F7;
      }
    }
  }

  .hot-search {
    margin-top: 20px;

    p.title {
      font-size: 16px;
    }

    .hot-box {
      display: grid;
      grid-template-columns: 33.33% 33.33% 33.33%;
      grid-template-rows: repeat(3, 60px);
    }

    .hot-item {
      padding: 6px;
      margin: 6px;
      text-align: left;
      font-size: 12px;
      line-height: 18px;
      color: #B8BDD1;
      word-wrap: break-word;
      word-break: break-all;

      .num {
        font-size: 14px;
        font-weight: 600;
        margin-right: 4px;
        color: $text_color;
      }
    }
  }

  .search-result {
    margin-top: 30px;

    .title-box {
      justify-content: space-between;
      align-items: center;
      font-size: 14px;

      .icon {
        width: 16px;
        height: 18px;
      }
    }

    .symbol-table {
      line-height: 22px;

      li {
        padding: 16px 0;
        border-bottom: 1px solid $border_color;
      }

      .flex-l {
        display: flex;
        flex: 1;
        align-items: center;

        .logo {
          width: 28px;
          height: 28px;
          margin-right: 6px;
        }

        .name-short {
          font-size: 14px;

          .title {
            font-weight: 600;
            font-size: 14px;
          }

          .type {
            font-size: 12px;
            color: #B8BDD1;
          }
        }

        .name {
          font-size: 12px;
          color: #B8BDD1;
        }
      }

      .flex-r {
        display: flex;
        align-items: center;
        font-size: 12px;
        text-align: center;

        .flex-r-item {
          flex: 1;
          align-self: center;
        }

        .data-box {
          padding: 0 6px;

          .data-item {
            display: flex;
            justify-content: flex-end;
            align-items: center;
            text-align: right;
            height: 24px;
            line-height: 24px;
            border-radius: 4px;
            color: #B8BDD1;
            font-size: 12px;

            img {
              margin-left: 10px;
              height: 24px;
              width: 24px;
            }
          }

          .value {
            font-weight: 300;
            margin-top: 4px;
          }

        }
      }
    }
  }
}

.left-icon {
  font-size: 20px;
}

:deep(.van-field__control) {
  background: transparent !important;
  height: 100% !important;
}
</style>