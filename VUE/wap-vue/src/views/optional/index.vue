<template>
  <section class="pb-20">
    <div class="container-box">
      <header class="header">
        <div class="flex-l" @click="handleShowPopup">
          <div class="icon">
            <img src="@/assets/image/optional/left-icon.png" alt="">
          </div>
          <span class="title">{{ optionalType == 1 ? activeInfo.listName : optionalName }}</span>
        </div>
        <div class="icon-group">
          <div class="icon search" @click="onRoute('/optional/search?symbolType=indices')">
            <img :src="handleImage(searchIcon)" alt="">
          </div>
          <!-- <div class="icon setting">
            <img src="@/assets/image/optional/setting.png" alt="" @click="onRoute('/optional/setting')">
          </div> -->
        </div>
      </header>
      <div class="has-data">
        <section class="option-container">
          <div class="flex-l" @click="onRoute('/optional/editGroupList')">
            <div class="icon">
              <img src="@/assets/image/optional/edit.png" alt="">
            </div>
            <span class="title">{{ t('editList') }}</span>
          </div>
          <div class="flex-r flex items-center">
            <span class="title">{{ t('最新价') }}</span>
            <div class="search flex items-center">
              <div class="filter-box ml-10">
                <div class="w-14 h-12 " :class="[sortVal == 1 ? 'icon_top1_active' : 'icon_top1']" @click="listSort(1)">
                </div>
                <div class="w-14 h-12" :class="[sortVal == 2 ? 'icon_top2_active' : 'icon_top2']" @click="listSort(2)">
                </div>
              </div>
            </div>
          </div>
        </section>
        <section class="market-container">
          <div class="symbol-table">
            <ul>
              <li class="flex" @click="itemClick(item)" v-for="(item, index) in itemList" :key="index">
                <div class="flex-l">
                  <div class="flex-l-item logo">
                    <img :src="`${HOST_URL}/symbol/${item.symbol.toLowerCase()}.png`" alt="">
                  </div>
                  <div class="flex-l-item name-title">
                    <p v-if="item.type == 'cryptos'" class="name-short">{{ item.symbol.toUpperCase() }}</p>
                    <p v-else class="name-short">{{ item.symbol }}</p>
                    <p class="name">{{ item.name }}</p>
                  </div>
                </div>
                <div class="flex-r">
                  <div class="flex-r-item chart-box">
                    <m-echarts :dataObj="item" :ratio="item.change_ratio" :index="item.symbol" />
                  </div>
                  <div class="flex-r-item data-box">
                    <div class="data-item" :class="[item.change_ratio < 0 ? 'bg-red' : 'bg-green']">{{ item.close }}</div>
                    <p class="value" :class="[item.change_ratio < 0 ? 'color-red' : 'color-green']">
                      {{ item.change_ratio < 0 ? item.net_change : '+' + item.net_change }} &nbsp; {{ item.change_ratio
                      }}%</p>
                  </div>
                </div>
              </li>
            </ul>
          </div>
          <div class="add-btn-group">
            <div class="add-icon icon">
              <img src="@/assets/image/optional/add.png" alt="">
            </div>
            <span @click="onRoute('/optional/search?symbolType=indices')">{{ t('addStock') }}</span>
          </div>
        </section>
      </div>
      <div class="no-data" v-if="itemList.length == 0">
        <img src="@/assets/image/optional/no-data.png" alt="">
        <p class="text">{{ t('暂无股票') }}</p>
        <!-- <div class="add-btn">
          <span>+ {{ t('添加') }}</span>
        </div> -->
      </div>
      <van-popup overlay-class="left-modal" v-model:show="show" position="left" class="popup-bg"
        :style="{ width: '75%', height: '100%' }" round safe-area-inset-top safe-area-inset-bottom>
        <div class="modal-inner-box">
          <p>{{ t('myPortfolio') }}</p>
          <div class="sidebar">
            <van-sidebar v-model="activeSideBarIndex">
              <van-sidebar-item @click="openType('all', 'ETF')" :title="t('全部股票')" />
              <van-sidebar-item :title="`${item.listName}(${item.symbolCount})`" @click="openId(item)"
                v-for="(item, index) in optionalList" :key="index" />
              <van-sidebar-item @click="openType('indices', 'ETF')" :title="`ETF(${fixedData.indices || 0})`" />
              <van-sidebar-item @click="openType('US-stocks', t('UsStocks'))"
                :title="`${t('UsStocks')}(${fixedData['US-stocks'] || 0})`" />
              <van-sidebar-item @click="openType('HK-stocks', t('HkStocks'))"
                :title="`${t('HkStocks')}(${fixedData['HK-stocks'] || 0})`" />
              <van-sidebar-item @click="openType('TW-stocks', t('TwStocks'))"
                :title="`${t('台股')}(${fixedData['TW-stocks'] || 0})`" />
              <van-sidebar-item @click="openType('A-stocks', t('AStocks'))"
                :title="`${t('A股')}(${fixedData['A-stocks'] || 0})`" />
              <van-sidebar-item @click="openType('cryptos', t('digitalCurrency'))"
                :title="`${t('digitalCurrency')}(${fixedData.cryptos || 0})`" />
              <van-sidebar-item @click="openType('forex', t('外汇'))" :title="`${t('外汇')}(${fixedData.forex || 0})`" />
              <van-sidebar-item @click="openType('INDIA-stocks', t('印度股'))"
                                :title="`${t('印度股')}(${fixedData['INDIA-stocks'] || 0})`" />
            </van-sidebar>
          </div>
          <footer class="footer-container">
            <div class="btn-group flex">
              <div class="btn-item flex-1" @click="onRoute('/optional/groupListManagement')">
                <div class="icon">
                  <img src="@/assets/image/optional/edit.png" alt="">
                </div>
                <p>{{ t("managementPortfolio") }}</p>
              </div>
              <div class="line"></div>
              <div class="btn-item flex-1" @click="onRoute('/optional/groupAdd')">
                <div class="icon">
                  <img src="@/assets/image/optional/add-icon.png" alt="">
                </div>
                <p>{{ t("addList") }}</p>
              </div>
            </div>
          </footer>
        </div>
      </van-popup>
    </div>
  </section>
</template>
    
<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useUserStore } from '@/store/user';
import { useRoute, useRouter } from 'vue-router';
import { Popup, Sidebar, SidebarItem } from 'vant';
import { _itemUserOptionalList, _itemUserOptionalListAdd, _listItemsById } from '@/service/quotes.api'
import { _getQuotes, _listItemsByType } from '@/service/quotes.api'
import { OPCIONA_LIST, CITAS_LIST, IS_LOAD } from '@/store/types.store'
import { useQuotesStore } from '@/store/quotes.store'
import { showLoadingToast, closeToast } from 'vant';
import { useI18n } from 'vue-i18n'
import MEcharts from "@/components/ex-echarts/index.vue";
import { HOST_URL } from '@/config'
import { themeStore } from '@/store/theme';
const thStore = themeStore()

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const show = ref(false)
const activeSideBarIndex = ref(0)
const optionalList = ref([])
const activeInfo = ref({})
const itemList = ref([])
const interval = ref(null)
const fixedData = ref({})
const optionalType = ref(1)
const optionalName = ref('')
const sortVal = ref(0)
const quotesStore = useQuotesStore()
const useStore = useUserStore();

const searchIcon = new URL(`../../assets/theme/${thStore.theme}/image/search.png`, import.meta.url)

onMounted(async () => {
  // if (!useStore.userInfo.token) {
  //   router.push('/login')
  //   return false
  // }
  if (quotesStore.$state.isLoad === 1) {
    showLoadingToast({
      message: t('加载中...'),
      duration: 0
    });
    quotesStore[IS_LOAD](2)
  }
  await getMyCoinsList()
  itemList.value = quotesStore.$state.citasList
  IntervalFun()

})

const handleImage = (url) => {
  return new URL(url, import.meta.url).href
}

const openType = (val, name) => {
  quotesStore[IS_LOAD](3)
  openByTypeItem(val, name)
}
const openId = (item) => {
  quotesStore[IS_LOAD](3)
  quotesStore[OPCIONA_LIST]([])
  quotesStore[CITAS_LIST]([])
  activeInfo.value = item
  byIdItem(item.listId)
  show.value = false
}

const handleShowPopup = () => {
  show.value = true;
}

const onRoute = (path) => {
  router.push(path)
}
//获取我的自选列表
const getMyCoinsList = () => {
  let params = {}
  optionalList.value = []
  if (!useStore.userInfo.token) { //没登陆
    optionalType.value = 3
    optionalName.value = t('全部股票')
    let obj = [
      { symbol: 'AAPL' },
      { symbol: 'MSFT' },
      { symbol: 'btc' },
      { symbol: 'eth' },
      { symbol: 'SH518880' },
      { symbol: '.IXIC' },
      { symbol: '.DJI' },
    ]
    quotesStore[OPCIONA_LIST](obj)
    activeSideBarIndex.value = 0
  } else {
    activeSideBarIndex.value = 1
    _itemUserOptionalList(params).then(data => {
      closeToast()
      fixedData.value = data.count
      optionalList.value = data.list || []
      if (optionalList.value.length > 0) {
        activeInfo.value = optionalList.value[0]
        byIdItem(activeInfo.value.listId)
      }
    })
  }
}
//根据id获取列表
const byIdItem = (id) => {
  if (!useStore.userInfo.token) {
    return
  }
  optionalType.value = 1
  let obj = {
    id: id
  }
  if (quotesStore.$state.isLoad === 3) {
    showLoadingToast({
      message: t('加载中...'),
      duration: 0
    });
    quotesStore[IS_LOAD](2)
  }
  _listItemsById(obj).then((res) => {
    closeToast()
    let arry = []
    quotesStore[OPCIONA_LIST]([])
    res.map((item) => {
      let objData = {
        symbol: item.symbol
      }
      arry.push(objData)
    })
    console.log(arry, 222)
    quotesStore[OPCIONA_LIST](arry)
    if (arry.length == 0) {
      quotesStore[CITAS_LIST]([])
    }
  })
}
//根据type获取列表
const openByTypeItem = (val, name) => {
  if (!useStore.userInfo.token) {
    return
  }
  if (quotesStore.$state.isLoad === 3) {
    showLoadingToast({
      message: t('加载中...'),
      duration: 0
    });
    quotesStore[IS_LOAD](2)
  }
  show.value = false
  if (val === 'all') {
    optionalType.value = 3
    optionalName.value = t('全部股票')
    let obj = [
      { symbol: 'AAPL' },
      { symbol: 'MSFT' },
      { symbol: 'btc' },
      { symbol: 'eth' },
      { symbol: 'SH518880' },
      { symbol: '.IXIC' },
      { symbol: '.DJI' },
    ]
    quotesStore[OPCIONA_LIST](obj)
    activeSideBarIndex.value = 0

  } else {
    quotesStore[OPCIONA_LIST]([])
    quotesStore[CITAS_LIST]([])
    optionalType.value = 2
    optionalName.value = name
    let obj = {
      type: val
    }
    _listItemsByType(obj).then((res) => {
      closeToast()
      let arry = []
      quotesStore[OPCIONA_LIST]([])
      res.map((item) => {
        let objData = {
          symbol: item.symbol
        }
        arry.push(objData)
      })
      console.log(arry, 2222)
      quotesStore[OPCIONA_LIST](arry)
      if (arry.length == 0) {
        quotesStore[CITAS_LIST]([])
      }
    })
  }
}

// const openByIdItem = (item) => {
//   quotesStore[IS_LOAD](3)
//   quotesStore[OPCIONA_LIST]([])
//   quotesStore[CITAS_LIST]([])
//   activeInfo.value = item
//   byIdItem(item.listId)
//   show.value = false
// }
//跳转界面
const itemClick = (item) => {
  console.log(item)
  if (item.type == "cryptos") { //跳转虚拟币
    router.push('/cryptos/trendDetails/' + item.symbol + '?isOptional=1&type=cryptos')
  } else if (item.type == "indices") { //efg
    router.push('/quotes/detail?symbol=' + item.symbol + '&isOptional=1&symbolType=indices&type=indices')
  } else if (item.type == "US-stocks") { //美股
    router.push(`/quotes/usStockDetail?symbol=${item.symbol}&isOptional=1&symbolType=US-stocks&enName=${item.name}`)
  } else if (item.type == "HK-stocks") { //港股
    router.push(`/quotes/usStockDetail?symbol=${item.symbol}&isOptional=1&symbolType=HK-stocks&enName=${item.name}`)
  } else if (item.type == "TW-stocks") { //台股
    router.push('/quotes/detail?symbol=' + item.symbol + '&isOptional=1&symbolType=TW-stocks&type=TW-stocks')
  } else if (item.type == "A-stocks") { //A股
    router.push(`/quotes/detail?symbol=${item.symbol}&isOptional=1&symbolType=A-stocks&type=A-stocks&enName=${item.name}`)
  } else {
    router.push('/foreign/coinChart?symbol=' + item.symbol + '&isOptional=1')
  }
}
//数字排序
const orderListAsc = (filed, type = "asc") => {
  return (a, b) => {
    if (type == "asc") return parseFloat(a[filed]) > parseFloat(b[filed]) ? 1 : -1;
    return parseFloat(a[filed]) > parseFloat(b[filed]) ? -1 : 1;
  };
}
const listSort = (val) => {
  sortVal.value = val
}
const IntervalFun = () => {
  interval.value = setInterval(() => {
    closeToast()
    if (quotesStore.$state.opcionalList.length > 0) {
      console.log(quotesStore.$state.opcionalList)
      _getQuotes(quotesStore.$state.opcionalList).then((data) => {
        itemList.value = data
        if (sortVal.value == 1) {
          itemList.value = itemList.value.sort(orderListAsc('close'))
          quotesStore[CITAS_LIST](itemList.value)
        } else {
          itemList.value = itemList.value.sort(orderListAsc('close', 'ask'))
          quotesStore[CITAS_LIST](itemList.value)
        }
      })
    } else {
      itemList.value = []
    }
  }, 2000)
}

onBeforeUnmount(() => {
  if (interval.value) {
    console.log(interval.value)
    clearInterval(interval.value)
  }
})
</script>
<style lang="scss" scoped>
:deep(.van-sidebar) {
  width: 100%;
}

:deep(.van-sidebar-item) {
  background-color: $main2_background;
  color: $text_color;
  padding: 12px;
}

:deep(.van-sidebar-item--select) {
  background-color: $select-bg;
  color: $color_main;
}

.container-box {
  padding: 0 12px 50px 12px;

  .name-title {
    width: 100px;
  }

  .green {
    color: $green;
  }

  .red {
    color: $red;
  }

  .header {
    display: flex;
    height: 28px;

    .flex-l {
      flex: 1;
      display: inline-flex;

      .icon {
        display: inline-block;
        width: 24px;
        height: 28px;
        padding: 6px 4px;

        img {
          height: 16px;
          width: 12px;
          filter: $filter;
        }
      }
    }

    .title {
      font-weight: 700;
      font-size: 20px;
      line-height: 28px;
      color: $mainTextColor;
    }

    .icon-group {
      width: 100px;
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

  .no-data {
    margin-top: 30%;
    display: flex;
    flex-direction: column;
    align-items: center;

    img {
      width: 98px;
      height: 108px;
    }

    .text {
      margin: 30px 0;
      font-size: 14px;
      color: #747A8F;
    }

    .add-btn {
      width: 134px;
      height: 38px;
      border: 1px solid #0083DA;
      border-radius: 6px;
      color: #0083DA;
      text-align: center;
      line-height: 36px;
    }
  }

  .option-container {
    margin-top: 20px;
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #747A8F;

    .flex-l {
      display: inline-flex;
      align-items: center;

      .icon {
        width: 20px;
        height: 20px;
        margin-right: 8px;
      }
    }

    .flex-r {
      display: inline-flex;
      align-items: center;

      .icon {
        width: 20px;
        height: 20px;
        margin-left: 8px;
      }
    }
  }

  .market-container {
    padding: 10px 0 60px;

    .symbol-table {
      line-height: 22px;

      li {
        padding: 16px 0;
        border-bottom: 1px solid $border_color;
      }

      .flex-l {
        display: flex;
        width: 130px;
        align-items: center;

        .logo {
          width: 28px;
          height: 28px;
          margin-right: 6px;
        }

        .name-short {
          font-weight: 600;
          font-size: 14px;
        }

        .name {
          font-size: 12px;
          color: #B8BDD1;
        }
      }

      .flex-r {
        display: flex;
        flex: 1;
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
            text-align: center;
            height: 24px;
            line-height: 24px;
            border-radius: 4px;
            color: $text_color;
            font-weight: 600;
            font-size: 14px;
          }

          .bg-red {
            background: $red;
            color: $main-btn-color;
          }

          .bg-green {
            background: $green;
            color: $main-btn-color;
          }

          .value {
            font-weight: 300;
            margin-top: 4px;
          }

        }
      }
    }

    .add-btn-group {
      display: flex;
      align-items: center;
      margin-top: 20px;
      font-size: 12px;
      line-height: 24px;
      color: $color_main;

      .icon {
        width: 16px;
        height: 16px;
        margin-right: 8px;
      }
    }

  }

  .modal-inner-box {
    padding: 30px 0 50px;

    >p {
      padding-left: 12px;
      font-weight: 700;
      font-size: 16px;
    }

    .sidebar {
      margin-top: 10px;
      width: 100%;
    }
  }

  .footer-container {
    position: absolute;
    width: 100%;
    bottom: 0px;
    border-top: 1px solid $foot-border;

    .line {
      margin: 10px 0;
      width: 1px;
      height: 40px;
      background-color: $foot-border;
    }

    .btn-item {
      display: flex;
      justify-content: center;
      align-items: center;
      font-size: 13px;
      height: 60px;
    }

    .icon {
      margin-right: 4px;
      width: 18px;
      height: 18px;
    }
  }

}

.color-red {
  color: $red;
}

.color-green {
  color: $green;
}

.filter-box {
  .icon_top1 {
    background: url('../../assets/image/icon_top1.png') no-repeat center;
    background-size: 100% 100%;
    width: 10px;
    height: 6px;
  }

  .icon_top2 {
    background: url('../../assets/image/icon_top2.png') no-repeat center;
    background-size: 100% 100%;
    margin-top: 1px;
    width: 10px;
    height: 6px;
  }

  .icon_top1_active {
    background: url('../../assets/image/icon_top3.png') no-repeat center;
    background-size: 100% 100%;
    width: 10px;
    height: 6px;
  }

  .icon_top2_active {
    background: url('../../assets/image/icon_top4.png') no-repeat center;
    background-size: 100% 100%;
    margin-top: 1px;
    width: 10px;
    height: 6px;
  }
}
</style>