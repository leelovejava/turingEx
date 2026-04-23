<template>
  <div class="container-box">
    <header class="header">
      <div class="flex-l">
        <div class="icon back">
          <van-icon name="arrow-left" size="20" @click="onRoute('/quotes/openTrade')" />
        </div>
        <span class="title">当日委托</span>
      </div>
      <div class="icon-group">
        <div class="icon refresh">
          <img src="@/assets/image/quotes/refresh.png" alt="">
        </div>
      </div>
    </header>
    <section class="inner-tab-container">
      <section class="etf-container">
        <div class="all-etf-ranking">
          <div class="etf-table">
            <ul>
              <li class="title-line">
                <div class="flex-r">
                  <div class="flex-r-item">
                    <p>{{ t('委托时间') }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p>{{ t('委托/均价') }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p>{{ t('委托/成交') }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p>{{ t('状态') }}</p>
                  </div>
                </div>
              </li>
              <li v-for="(item) in allEtfListData" :key="item.symbol" @click="itemClick(item)">
                <div class="flex-r">
                  <div class="flex-r-item">
                    <p>{{ item.symbol }}</p>
                    <p class="gray-text">{{ item.current_time ? item.current_time.slice(11) : '-' }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p :class="item.open < 1 ? 'text-up' : 'text-down'">{{
                      item.open
                    }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p :class="item.close < 1 ? 'text-up' : 'text-down'">{{
                      item.close
                    }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p :class="item.close < 1 ? 'text-up' : 'text-down'">{{
                      item.close
                    }}</p>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </section>
    </section>
  </div>
</template>
    
<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useUserStore } from '@/store/user';
import { useRoute, useRouter } from 'vue-router';
import { _getQuotes } from '@/service/quotes.api'
import { useI18n } from 'vue-i18n'
import { useQuotesStore } from '@/store/quotes.store'

const quotesStore = useQuotesStore()
const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const allEtfTabIndex = ref(0)
const allEtfListData = ref([])
const interval = ref(null)
const symbolType = ref('forex') //默认查询外汇

onMounted(async () => {
  fetchQuotes()
  // interval.value = setInterval(() => {
  //   fetchQuotes()
  // }, 1000)
})

onBeforeUnmount(() => {
  // if (interval.value) {
  //   clearInterval(interval.value)
  // }
})

const fetchQuotes = () => {
  const params = filterCoins(symbolType.value)
  _getQuotes(params).then(data => {
    allEtfListData.value = data
  })
}

const filterCoins = (type) => {
  let arr = [...quotesStore.coins]
  let result = []
  if (type === "all") {
    result = arr;
  } else {
    result = arr.filter(item => item.type === type)
  }
  return result;
}

const onRoute = (path) => {
  router.push({
    path,
    query: {
      tabActive: 3
    }
  })
}

const itemClick = () => {
  router.push('/quotes/detail')
}

const handleClickHotSymbol = (value) => {
  console.log(value, 'value')
}

</script>
<style lang="scss" scoped>
.container-box {
  margin-top: 8px;

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
</style>