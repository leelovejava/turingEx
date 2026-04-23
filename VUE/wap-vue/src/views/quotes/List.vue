<template>
  <section class="pb-fix">
    <div class="quotes-container-box">
      <header class="header">
        <div class="flex-l">
          <span class="title">{{ t('quotes') }}</span>
        </div>
        <div class="icon-group">
          <div class="icon search" @click="handleSearch">
            <img :src="searchSrc" alt="search" class="w-12 h-12">
          </div>
        </div>
      </header>
      <section class="quotes-tab-container">
        <van-tabs v-model:active="tabActive" shrink @click-tab="onClickTab">
          <van-tab v-for="(item, index) in listTab" :key="item.tabIndex" :name="item.tabIndex" :title="item.title">
            <component @changeLetMego="handleChangeLetMego" :index="item.tabIndex" :tabActive="tabActive"
              :is="components.get(item.type)" :key="item.tabIndex" ref="tabRefs" />
          </van-tab>
        </van-tabs>
      </section>
    </div>
    <!-- 安装提示 -->
    <div class="addBox" v-show="showSave" @click="closeSaveBox">
      <div class="add">
        <div class="text-26">
          <div class="flex flex-col">
            <b>{{ $t('installApp') }}:</b>
            <div class="mt">{{ $t('press') }}“<img
                style="width:22px;vertical-align: middle;margin: 0 2px;display: inline-block;"
                src="../../assets/image/assets-center/press.png" />”&nbsp;{{ $t('and') }}&nbsp;<b>“{{
                  $t('addHomeScreen')
                }}”</b>
            </div>
          </div>
          <div class="mt">({{ $t('installedClose') }})</div>
        </div>
        <img class="closeAdd w-4 h-4" src="../../assets/image/assets-center/icon-close.png" alt="">
        <div class="angle"></div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, defineAsyncComponent, shallowRef } from 'vue';
import { onBeforeRouteLeave, useRoute, useRouter } from 'vue-router';
import { _getQuotes } from '@/service/quotes.api'
import { useI18n } from 'vue-i18n'
import { setStorage, getStorage } from '@/utils'

import { themeStore } from '@/store/theme';
import { TIME_OUT } from "@/config/index.js";

const thStore = themeStore()


const { t } = useI18n()


const router = useRouter()
const route = useRoute()
const tabActive = ref(0)
const TITLE = import.meta.env.VITE_APP__TITLE
const showSave = ref(false)
const searchSrc = new URL(`../../assets/theme/${thStore.theme}/image/search.png`, import.meta.url)

const symbolType = ref('US-stocks')
const tabRefs = ref([])

const components = shallowRef(new Map())

components.value.set(
  'Etf',
  defineAsyncComponent(() => import('../etf/index.vue'))
)
components.value.set(
  'Foreign',
  defineAsyncComponent(() => import('../foreign/index.vue'))
)
components.value.set(
  'Cryptos',
  defineAsyncComponent(() => import('../cryptos/index.vue'))
)
components.value.set(
  'UsStock',
  defineAsyncComponent(() => import('../usStock/List.vue'))
)
components.value.set(
  'HkStock',
  defineAsyncComponent(() => import('../hkStock/List.vue'))
)
components.value.set(
  'TWStock',
  defineAsyncComponent(() => import('../twStock/List.vue'))
)
components.value.set(
  'JPStock',
  defineAsyncComponent(() => import('../jpStock/List.vue'))
)
components.value.set(
  'AStock',
  defineAsyncComponent(() => import('../aStock/List.vue'))
)
components.value.set(
  'INDIAStock',
  defineAsyncComponent(() => import('../INDIAStock/List.vue'))
)
components.value.set(
  'UKStock',
  defineAsyncComponent(() => import('../UKStock/List.vue'))
)
components.value.set(
  'DEStock',
  defineAsyncComponent(() => import('../deStock/List.vue'))
)
components.value.set(
  'BZStock',
  defineAsyncComponent(() => import('../brazilStock/List.vue'))
)
const listTab = ref([
  {
    title: t('加密货币'),
    type: 'Cryptos',
    urlMatch: 'crypto',
    symbolType: 'cryptos',
    tabIndex: 0
  },
  {
    title: t('UsStocks'),
    type: 'UsStock',
    urlMatch: 'stock',
    symbolType: 'US-stocks',
    tabIndex: 1
  },
  {
    title: t('外汇'),
    type: 'Foreign',
    urlMatch: 'for',
    symbolType: 'forex',
    tabIndex: 2
  },
  {
    title: 'ETF',
    type: 'Etf',
    urlMatch: 'etf',
    symbolType: 'indices',
    tabIndex: 3
  },
  // {
  //   title: t('港股'),
  //   type: 'HkStock',
  //   urlMatch: 'HK-stocks',
  //   symbolType: 'HK-stocks',
  //   tabIndex: 4
  // },
  // {
  //   title: t('台股'),
  //   type: 'TWStock',
  //   urlMatch: 'TW-stocks',
  //   symbolType: 'TW-stocks',
  //   tabIndex: 5
  // },
  // {
  //   title: t('日股'),
  //   type: 'JPStock',
  //   urlMatch: 'JP-stocks',
  //   symbolType: 'JP-stocks',
  //   tabIndex: 6
  // },
  // {
  //   title: t('A股'),
  //   type: 'AStock',
  //   urlMatch: 'A-stocks',
  //   symbolType: 'A-stocks',
  //   tabIndex: 7
  // },
  // {
  //   title: t('印度股'),
  //   type: 'INDIAStock',
  //   urlMatch: 'INDIA-stocks',
  //   symbolType: 'INDIA-stocks',
  //   tabIndex: 0
  // },
  // {
  //   title: t('英股'),
  //   type: 'UKStock',
  //   urlMatch: 'UK-stocks',
  //   symbolType: 'UK-stocks',
  //   tabIndex: 9
  // },
  // {
  //   title: t('德股'),
  //   type: 'DEStock',
  //   urlMatch: 'DE-stocks',
  //   symbolType: 'DE-stocks',
  //   tabIndex: 10
  // },
  // {
  //   title: t('巴股'),
  //   type: 'BZStock',
  //   urlMatch: 'BZ-stocks',
  //   symbolType: 'BZ-stocks',
  //   tabIndex: 11
  // },
])


onMounted(async () => {
  setTabActive()
  getIsSave()
})


const setTabActive = () => {
  if (route.query.tabActive) {
    tabActive.value = +route.query.tabActive
    let obj = listTab.value.find(item => {
      return item.tabIndex == tabActive.value
    })
    symbolType.value = obj?.symbolType
    return
  }
  let urlPath = GetUrlRelativePath()
  listTab.value.forEach(item => {
    if (urlPath.indexOf(item.urlMatch) != -1) {
      tabActive.value = item.tabIndex
      symbolType.value = item.symbolType
    }
  })
}

const handleSearch = () => {
  listTab.value.forEach(item => {
    if (tabActive.value == item.tabIndex) {
      symbolType.value = item.symbolType
    }
  })
  router.push({
    path: '/optional/search',
    query: {
      symbolType: symbolType.value
    }
  })
}

const GetUrlRelativePath = () => {
  var url = document.location.toString();
  var arrUrl = url.split("//");

  var start = arrUrl[1].indexOf("/");
  var relUrl = arrUrl[1].substring(start);//stop省略，截取从start开始到结尾的所有字符

  if (relUrl.indexOf("?") != -1) {
    relUrl = relUrl.split("?")[1];
  }
  return relUrl;
}


const closeSaveBox = () => {
  setStorage(`${TITLE}addtoClosed`, 1)
  showSave.value = false
}
const getIsSave = () => {
  const issafariBrowser = /Safari/.test(navigator.userAgent) && !/Chrome/.test(navigator.userAgent);
  if (issafariBrowser) {
    if (getStorage(`${TITLE}addtoClosed`)) {
      showSave.value = false;
    } else {
      showSave.value = true
    }
  } else {
    showSave.value = false
  }
  const isFull = window.navigator.standalone;
  if (isFull) {
    showSave.value = false
  }
}

const letMeGo = ref(() => { })

const handleChangeLetMego = (fn) => {
  letMeGo.value = fn
}

let timer = setInterval(() => {
  letMeGo.value()
}, TIME_OUT)

const onClickTab = ({ name, title }) => {
  console.log('name', name)
  if (tabActive.value !== name) {
    letMeGo.value = () => { }
  }
  tabActive.value = name
  router.push('/quotes/index?tabActive=' + tabActive.value)
};
onBeforeRouteLeave(() => {
  clearInterval(timer)
  timer = null
})

</script>
<style lang="scss" scoped>
:deep(.van-loading) {
  position: absolute;
  top: 30%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 999;
}

:deep(.van-tabs__nav) {
  background: $mainBgColor;
}

:deep(.van-tab) {
  font-size: 14px;
  color: $text_color;
  font-weight: 400;
}

:deep(.van-tab.van-tab--active) {
  font-weight: 700;
}

:deep(.van-tabs__line) {
  background: url('../../assets/image/active.png') no-repeat center;
  width: 9px;
  height: 8px;
  background-size: 100% 100%;
}

.soon-container {
  padding: 0 16px;
  color: #ccc;
  font-size: 14px;
  display: flex;
  justify-content: center;
  text-align: center;

  img {
    width: 200px;
    height: 220px;
  }
}

.quotes-container-box {
  font-size: 12px;

  .green {
    color: $green;
  }

  .red {
    color: $red;
  }

  .header {
    display: flex;
    height: 34px;
    padding: 0 12px;
    justify-content: space-between;

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
      text-align: right;

      .icon {
        display: flex;
        width: 28px;
        height: 28px;
        margin-left: 16px;
        justify-content: center;
        align-items: center;
      }
    }


  }

  .quotes-tab-container {
    margin-top: 18px;
  }


}

.addBox {
  border-radius: 10px;
  width: 300px;
  height: 100px;
  font-size: 12px;
  background: $fina-border;
  position: fixed;
  bottom: 70px;
  left: 50%;
  margin-left: -150px;
  z-index: 1000;

  .add {
    padding: 16px;
    box-sizing: border-box;
    height: 100%;
    position: relative;
    color: $text_color;

    .closeAdd {
      position: absolute;
      right: 10px;
      top: 10px;
      width: 10px;
      height: 10px;
    }

    .angle {
      position: absolute;
      bottom: -37px;
      width: 0;
      height: 0;
      left: 131px;
      border: 20px solid transparent;
      border-top: 24px solid $fina-border;
    }
  }
}
</style>
