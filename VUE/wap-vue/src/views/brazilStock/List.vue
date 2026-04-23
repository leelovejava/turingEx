<template>
  <div class="foreign-container-box">
    <!-- 火花图 -->
    <section class="hots-container">
      <div class="hots-symbol flex">
        <div class="hots-symbol-item" v-for="(item, index) in indexCardInfo" :key="item.symbol"
             @click="itemClick(item)">
          <p class="name">
            <span class="name-l">{{ item.name }}</span>
            <span class="name-r" :class="item.changeRatio > 0 ? 'text-down' : 'text-up'">{{ item.close }}</span>
          </p>
          <div class="trad-info-box">
            <div class="trad-info">
              <div class="buy">
                <span :class="item.changeRatio > 0 ? 'text-down' : 'text-up'">
                  {{
                    item.changeRatio === 0 ? 0 : item.changeRatio > 0 ? `+${item.changeRatio}%` : `${item.changeRatio}%`
                  }}
                </span>
              </div>
              <div class="sell">
                <span class="mr-1" :class="item.changeRatio > 0 ? 'text-down' : 'text-up'">
                  {{ item.chg > 0 ? `+${item.chg}` : item.chg }}
                </span>
              </div>
            </div>
          </div>
          <div class="trend">
            <m-echarts :dataObj="item" :ratio="Number(item.changeRatio)" :index="item.symbol + 1" />
          </div>
        </div>
      </div>
    </section>

    <section class="line-box">
      <div class="line-content">
        <div class="green-line"></div>
        <div class="mind-line"></div>
        <div class="red-line"></div>
      </div>
      <div class="text-info">
        <div class="left-text">
          <p>{{ t('上涨') }}：</p>
          <span>3,339</span>
        </div>
        <div class="right-text">
          <p>{{ t('下跌') }}：</p>
          <span>2,965</span>
        </div>
      </div>
    </section>
    <div class="divider mt-4"></div>
    <!-- 轮播 -->
    <section class="banner-container">
      <van-swipe class="swipe-box" :autoplay="5000" indicator-color="white" v-if="isZh">
        <van-swipe-item>
          <img src="@/assets/image/bzStock/1-zh.png" alt="">
        </van-swipe-item>
        <van-swipe-item>
          <img src="@/assets/image/bzStock/2-zh.png" alt="">
        </van-swipe-item>
      </van-swipe>
      <van-swipe class="swipe-box" :autoplay="5000" indicator-color="white" v-else>
        <van-swipe-item>
          <img src="@/assets/image/bzStock/1-en.png" alt="">
        </van-swipe-item>
        <van-swipe-item>
          <img src="@/assets/image/bzStock/2-en.png" alt="">
        </van-swipe-item>
      </van-swipe>
    </section>
    <!-- 公告 -->
    <div class="pt-5 px-6">
      <van-notice-bar class="text-26 textColor" left-icon="" :scrollable="false" background="transparent">
        <template #left-icon slot="left-icon" class=" flex items-center more-img">
          <img class="w-10 h-10  more-img" src="../../assets/Horn.png" alt="">
        </template>
        <van-swipe vertical class="notice-swipe" :autoplay="2000" :show-indicators="false">
          <van-swipe-item v-for="item in announceList" :key="item.id" @click="toAnnounceDetail(item.uuid)">{{
              item.title
            }}
          </van-swipe-item>
        </van-swipe>
        <div class="ml-20 flex items-center " slot="right-icon" @click.stop="$router.push('/cryptos/announce')"><img
            class="w-10 h-10 more-img" src="../../assets/more.png" alt="">
        </div>
      </van-notice-bar>
    </div>
    <!-- 快捷 -->
    <section class="nav">
      <bz-stock-ex-nav :defaultEtfListData="defaultListData" />
    </section>
    <div class="divider"></div>
    <!-- 行情 -->
    <section class="market-container">
      <div class="header">
        <div class="title">{{ t('quotes') }}</div>
      </div>
      <div class="tabs flex">
        <div class="tab-item flex-1" v-for="(item, index) in tabList" @click="selectIndex(index, item.value)"
             :class="[tabIndex === index ? 'active' : '']" :key="index">
          {{ item.title }}
        </div>
      </div>
      <div class="symbol-table">
        <div class="technology">
          <div class="other-etf-ranking">
            <div class="etf-table">
              <ul class="">
                <li class="title-line flex">
                  <div class="item1">
                    <p>{{ t('名称代码') }}</p>
                  </div>
                  <div class="item2 text-center">
                    <p>{{ t('最新价') }}</p>
                  </div>
                  <div class="item3 text-center">
                    <p>{{ t('涨幅') }}</p>
                  </div>
                  <div class="item4 text-center">
                    <p>{{ t('SparkDiagram') }}</p>
                  </div>
                  <div class="item5 text-right">
                    <p>{{ t('金额') }}</p>
                  </div>
                </li>
                <li class="flex quotesItem" v-for="(item, index) in defaultListData.slice(0, 5)" :key="index"
                    @click="itemClick(item)">
                  <div class="item1">
                    <p>{{ item.name }}</p>
                    <p class="code mt-2">{{ item.symbol }}</p>
                  </div>
                  <div class="item2 text-center">
                    <p>{{ item.close }}</p>
                  </div>
                  <div class="item3 text-center">
                    <p :class="item.changeRatio > 0 ? 'text-down' : 'text-up'">
                      {{
                        item.changeRatio === 0 ? 0 : item.changeRatio > 0 ? `+${item.changeRatio}%` :
                            `${item.changeRatio}%`
                      }}
                    </p>
                  </div>
                  <div class="item4 text-center">
                    <p>
                      <m-echarts :dataObj="item" :ratio="item.changeRatio" :index="item.symbol + 3" />
                    </p>
                  </div>
                  <div class="item5 text-right">
                    <p>{{ item.amount > 0 ? formatMoney(item.amount) : 0 }}</p>
                  </div>
                </li>
              </ul>
            </div>
            <div class="more-box">
              <div class="icon-group" @click="moreOpen(0, $t('台股'))">
                <p class="">{{ t('更多') }}</p>
                <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow">
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import {ref, onMounted, watch} from "vue";
import {useRouter} from 'vue-router';
import {useQuotesStore} from '@/store/quotes.store'
import {useLanguageStore} from '@/store/language.store'
import {SET_COIN_LIST, SET_LANGUAGE} from '@/store/types.store'
import {useI18n} from "vue-i18n";
import MEcharts from "@/components/ex-echarts/index.vue";
import BzStockExNav from "@/components/trade/bz-stock-ex-nav/index.vue"
import {_getNewsList1} from '@/service/user.api'
import {formatMoney} from "@/utils";
import {_getRealtimeByType, _publicRealtimeTop} from '@/service/quotes.api'

defineOptions({
  name: 'homeIndex'
});

const language = JSON.parse(localStorage.getItem('lang'))
const isZh = language == "zh-CN" || language == "CN"
const {locale} = useI18n()
const {t} = useI18n()
const quotesStore = useQuotesStore()
const languageStore = useLanguageStore()
const scene = ref("gains");
const tabIndex = ref(0)
const tabList = ref([
  {title: t('市场排行'), value: "gains"},
  {title: t('涨幅榜'), value: "changeRatio"},
  {title: t('跌幅榜'), value: "decrease"},
  {title: t('成交额'), value: "amount"},
])
const router = useRouter()
const announceList = ref([])
const indexCardInfo = ref([]);
const defaultListData = ref([])

const props = defineProps({
  index: {
    type: Number,
    default: 0
  },
  tabActive: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['changeLetMego'])
const letMeGo = () => {
  emit('changeLetMego', () => {
    getRealtimeByType()
    publicRealtimeTop()
  })
}

const loading = ref(false)
onMounted(async () => {
  languageStore[SET_LANGUAGE](locale.value)
  loading.value = true
  await quotesStore[SET_COIN_LIST]()
  loading.value = false
  getNews()
  letMeGo()
})


const getNews = () => {
  _getNewsList1({
    // language: this.$i18n.locale,
  }).then(res => {
    announceList.value = res
  })
}

const toAnnounceDetail = (announceId) => {
  if (announceId) {
    router.push({path: '/cryptos/AnnounceDetail', query: {id: announceId}})
  }
}

const selectIndex = (index, value) => {
  console.log(value)
  tabIndex.value = index
  scene.value = value
  let result = []
  if (scene.value === 'decrease') {
    result = defaultListData.value.sort((a, b) => a.changeRatio - b.changeRatio)
  } else if (scene.value === 'amount') {
    result = defaultListData.value.sort((a, b) => b.amount - a.amount)
  } else if (scene.value === 'changeRatio') {
    result = defaultListData.value.sort((a, b) => b[scene.value] - a[scene.value])
  } else {
    getRealtimeByType()
    result = defaultListData.value
  }
  defaultListData.value = result
}


const publicRealtimeTop = () => {
  _publicRealtimeTop({
    type: 'BZ-stocks',
  }).then(data => {
    indexCardInfo.value = data || []
  }).catch((e) => {
  })
}


const getRealtimeByType = () => {
  _getRealtimeByType({
    type: 'BZ-stocks',
    pageNo: 1
  }).then(data => {
    if (data === null) {
      data = []
    }
    defaultListData.value = data
    let result = []
    if (scene.value === 'decrease') {
      result = defaultListData.value.sort((a, b) => a.changeRatio - b.changeRatio)
    } else if (scene.value === 'amount') {
      result = defaultListData.value.sort((a, b) => b.amount - a.amount)
    } else if (scene.value === 'changeRatio') {
      result = defaultListData.value.sort((a, b) => b[scene.value] - a[scene.value])
    } else {
      result = data
    }
    defaultListData.value = result
  })
}

const getSwiperList = (list, length, pageSize) => {
  let arr = list.slice(0, length)
  const result = [];
  for (let i = 0; i < arr.length; i += pageSize) {
    const group = arr.slice(i, i + pageSize);
    result.push(group);
  }
  return result
}

const onRoute = (path) => {
  router.push(path)
}

const itemClick = (item) => {
  router.push(`/quotes/detail?symbol=${item.symbol}&type=BZ-stocks&symbolType=BZ-stocks`)
  // router.push(`/quotes/usStockDetail?symbol=${item.symbol}&symbolType=BZ-stocks&enName=${item.enName}`)
}

const moreOpen = (index, name) => {
  router.push(`/quotes/hotModules?index=${index}&typName=${name}&symbolType=BZ-stocks&tabIndex=${props.tabActive}`)

}


watch(() => props.tabActive, (val) => {
  if (props.index === val) {
    letMeGo()
  }
})
</script>

<style lang="scss" scoped>
.foreign-container-box {
  padding-bottom: 50px;

  .font-semibold {
    font-size: 14px;
  }

  .header {
    display: flex;

    .title {
      flex: 1;
      font-weight: 700;
      font-size: 20px;
      line-height: 28px;
      color: $text_color;
    }

    .icon-group {
      display: flex;
      text-align: right;
      align-items: center;

      .icon {
        display: inline-block;
        width: 28px;
        height: 28px;
        padding: 4px;
        margin-left: 2px;
      }

      .icon-circle {
        border-radius: 50%;
        background-color: #F6F7F7;
      }
    }
  }

  .banner-container {
    margin-top: 20px;
    height: 160px;
    padding: 0 12px;

    .swipe-box {
      border-radius: 8px;
    }

    .van-swipe-item {
      color: $text_color;
      font-size: 20px;
      line-height: 160px;
      text-align: center;
      background-color: $selectSymbol_background;

      img {
        display: block;
        height: 160px;
        width: 100%;
        object-fit: cover;
      }
    }
  }

  .hots-container {
    padding: 15px;
    overflow-x: scroll;
    background-color: $second-bg;

    .hots-symbol {
      gap: 0 3.3%;

      &::after {
        content: ".";
        display: block;
        height: 0;
        clear: both;
        visibility: hidden; //占位但不可见
      }
    }

    .hots-symbol-item {
      font-size: 12px;
      font-weight: 600;
      padding: 4px;
      background: $bz-bg;
      box-shadow: 0px 0px 8px 0px #0000001A;
      border-radius: 10px;
      flex-shrink: 0;
      width: 124px;

      .trad-info-box {
        .trad-info {
          display: flex;
          justify-content: space-around;
          align-items: center;
        }
      }

      .name {
        padding: 0 4px;
        display: flex;
        flex-direction: column;
        align-content: center;
        justify-content: center;
        text-align: center;

        .name-l {
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      .trend {
        height: 65px;
      }

      .direction {
        // padding: 0 10px;
        justify-content: space-between;

        span {
          font-size: 10px;
          line-height: 16px;
        }

        .buy {
          text-align: right;
        }

        .sell {
          text-align: left;
        }

        .sell-text {
          color: $hot-text-color1;
        }

        .sell-num {
          color: $hot-text-color2;
        }

        &.amount {
          .sell {
            display: flex;
            align-items: center;
            justify-content: center;

            span {
              transform: scale(0.8);
            }
          }

          .buy {
            display: flex;
            align-items: center;

            span {
              transform: scale(0.8);
            }
          }
        }

      }
    }
  }

  .line-box {
    background: $mainBgColor;
    padding: 15px 15px 0 15px;
    width: 100%;
    display: flex;
    flex-direction: column;

    .line-content {
      display: flex;
      height: 5px;
      position: relative;

      div {
        width: 188px;
        height: 5px;
      }

      .green-line {
        background: #05CDA5;
        border-radius: 30px 0 0 30px;
        width: calc(50% + 57px);
      }

      .mind-line {
        background: #747A8F;
        transform: skew(-40deg);
        border-left: 5px solid $mainBgColor;
        border-right: 5px solid $mainBgColor;
        width: 57px;
        position: absolute;
        left: 50%;
        top: 0;
      }

      .red-line {
        background: #F33368;
        border-radius: 0 30px 30px 0;
        width: 50%;
      }
    }

    .text-info {
      margin-top: 10px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 14px;

      div {
        display: flex;
        justify-content: flex-start;
        align-items: center;
      }

      .left-text {
        color: #06CDA5
      }

      .right-text {
        color: #F43368
      }
    }
  }

  .hot-swiper {
    padding: 20px 12px;

    .container {
      .swipe-box {
        padding-bottom: 20px;

        :deep(.van-swipe__indicators) {
          bottom: 0;
        }
      }

      .swipe-item {

        .child {
          display: flex;
          border-bottom: 1px solid $border_color;
          padding: 10px 10px;
          align-items: center;
          justify-content: space-between;

          .left {
            width: 45%;
            display: flex;
            flex-direction: column;

            .name {
              font-size: 15px;
              font-weight: 700;
              color: $text_color;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }

            .hot {
              display: flex;
              align-items: center;

              .num {
                font-size: 12px;
                margin-right: 10px;
                color: $text_color1;
              }

              .hotIcon {
                display: flex;

                img {
                  width: 20px;
                  height: 20px;
                }
              }
            }
          }

          .right {
            width: 25%;
            display: flex;
            flex-direction: column;
          }

          .mid {
            width: 20%;
          }
        }

      }
    }
  }

  .market-container {
    margin-top: 20px;
    padding: 0 12px;

    .title {
      font-size: 20px;
      font-weight: 700;
    }

    .icon.arrow {
      width: 14px;
      height: 28px;

      img {
        margin-top: 6px;
      }
    }

    .tabs {
      // padding: 0 12px;
      margin-top: 10px;
      // height: 40px;
      min-height: 40px;
      line-height: 24px;
      color: #BBBCBD;

      .tab-item {
        margin: 4px;
        text-align: center;
        padding: 4px 4px;
        font-size: 12px;
        color: $text_color5;
        background: $US_tab_background;
        border-radius: 10px;
        background-size: cover;
      }

      .active {
        font-weight: 400;
        color: $quotes-btn-text !important;
        background: $quotes-btn-bg;
        border-radius: 10px;
        background-size: cover;
      }
    }

    .symbol-table {
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
          padding: 10px 0;
          border: none;
        }

        li {
          display: flex;
          align-items: center;
          font-size: 12px;
          line-height: 18px;
          border-bottom: 1px solid $border_color;
          gap: 0 1.5%;

          .item1 {
            width: 25%;
          }

          .item2 {
            width: 20%;
          }

          .item3 {
            width: 15%;
          }

          .item4 {
            width: 15%;
          }

          .item5 {
            width: 20%;
          }


          .gray-text {
            color: #BCBDC2;
            font-size: 12px;
          }

          .flex-l {
            width: 40%;
          }

          .flex-r {
            display: inline-flex;
            flex: 1;

            .flex-r-item {
              flex: 1;
              align-self: center;
              text-align: center;

              &:last-child {
                text-align: right;
              }
            }
          }
        }
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
    color: $text_color6;

    .icon.arrow {
      margin-left: 10px;
      width: 15px;
      height: 15px;
      color: $text_color6;
    }
  }
}

.code {
  color: $text_color1;
}

.flex-2 {
  flex: 2;
}

:deep(.van-notice-bar__content) {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.van-notice-bar {
  padding: 0;
  height: 30px;
}

.notice-swipe {
  flex: 1;
  margin-left: 10px;
  height: 30px;
  line-height: 30px;
}

.quotesItem {
  div {
    flex-shrink: 0;

    p {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

.text-up {
  color: $red;
}

.text-down {
  color: $green;
}
</style>
