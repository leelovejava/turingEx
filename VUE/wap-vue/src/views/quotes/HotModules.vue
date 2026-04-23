<template>
  <section class="pb-fix">
    <div class="container-box">
      <header class="header">
        <div class="flex-l">
          <div class="icon back">
            <van-icon name="arrow-left" size="20" @click="handleGoBack" />
          </div>
          <span class="title">{{ typName }}</span>
        </div>
        <div class="icon-group">
          <div class="icon search" @click="onRoute('/optional/search')">
            <img :src="handleImage(searchIcon)" alt="">
          </div>
        </div>
      </header>
      <section class="tab-container">
        <section class="etf-container">
          <div class="all-etf-ranking">
            <div class="etf-table">
              <ul>
                <li class="title-line">
                  <div class="flex-l">
                    <p>{{ t('nameCode') }}</p>
                  </div>
                  <div class="flex-r">
                    <div class="flex-r-item">
                      <p>{{ t('uptodate') }}</p>
                    </div>
                    <div class="flex-r-item">
                      <p>{{ t('increase') }}</p>
                    </div>
                    <div class="flex-r-item">
                      <p>{{ t('SparkDiagram') }}</p>
                    </div>
                    <div class="flex-r-item right">
                      <p>{{ t('Amount') }}</p>
                    </div>
                  </div>
                </li>
                <van-list v-model:loading="loading" :finished="finished" :loading-text="$t('加载中') + '...'"
                  :finished-text="$t('没有更多了')" @load="onLoad">
                  <li v-for="(item, index) in allEtfListData" :key="item.symbol" @click="handleClickHotSymbol(item)">
                    <div class="flex-l">
                      <p>{{ item.name }}</p>
                      <p class="gray-text">
                        {{ item.symbol }}
                      </p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p :class="item.close < 1 ? 'text-up' : 'text-down'">{{
                          item.close
                        }}</p>
                      </div>
                      <div class="flex-r-item">
                        <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                          <span>{{ item.changeRatio }}%</span>
                        </p>
                      </div>
                      <div class="flex-r-item">
                        <div class="mecharts-box">
                          <m-echarts :dataObj="item" :ratio="item.changeRatio" :index="item.symbol" />
                        </div>
                      </div>
                      <div class="flex-r-item">
                        <div class="last-item right">
                          <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                            <span>{{ item.amount }}</span>
                          </p>
                        </div>
                      </div>
                    </div>
                  </li>
                </van-list>
              </ul>
            </div>
          </div>
        </section>
      </section>
    </div>
  </section>
</template>
    
<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useUserStore } from '@/store/user';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n'
import { useQuotesStore } from '@/store/quotes.store'
import { _getRealtimeByType } from '@/service/quotes.api'
import MEcharts from "@/components/ex-echarts/index.vue";
import { themeStore } from '@/store/theme';
const thStore = themeStore()

const { t } = useI18n()
const router = useRouter()
const route = useRoute();
const allEtfListData = ref([])
const interval = ref(null)
const symbolType = ref(route.query.symbolType) || "indices";
const typName = ref('')
const tabIndex = ref(0)
const loading = ref(false);
const finished = ref(false);
const pageNo = ref(1)
const category = ref('')

const searchIcon = new URL(
  `../../assets/theme/${thStore.theme}/image/search.png`,
  import.meta.url
);

onMounted(async () => {
  typName.value = route.query.typName
  tabIndex.value = route.query.index
  category.value = route.query.category
})

const onLoad = () => {
  getRealtimeByType()
}

onBeforeUnmount(() => {
  if (interval.value) {
    clearInterval(interval.value)
  }
})


const getRealtimeByType = () => {
  _getRealtimeByType({
    type: route.query.symbolType,
    pageNo: pageNo.value,
    category: category.value,
  }).then((data) => {
    allEtfListData.value = allEtfListData.value.concat(data)

    loading.value = false;
    if (data.length < 20) {
      finished.value = true;
    } else {
      pageNo.value++
    }
  });
};


const onRoute = (path) => {
  if (path === '/optional/search') {
    router.push({
      path,
      query: {
        symbolType: symbolType.value
      },
    })
  } else {
    router.push(path)
  }
}

const handleGoBack = () => {
  if(route.query.tabIndex){
    console.log(1)
    router.push(`/quotes/index?tabActive=${route.query.tabIndex}&symbolType${route.query.symbolType}`)
  } else {
    console.log(2)
    router.push('/quotes/index?tabActive=0')
  }
}

const handleClickHotSymbol = (item) => {
  if(category.value){
    router.push(`/quotes/detail?symbol=${item.symbol}&from=hot&symbolType=${symbolType.value}&typName=${typName.value}&category=${category.value}&tabIndex=${route.query.tabIndex}`)
  }else{
    router.push(`/quotes/detail?symbol=${item.symbol}&from=hot&symbolType=${symbolType.value}&typName=${typName.value}&tabIndex=${route.query.tabIndex}`)
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

:deep(.van-tabs__content) {
  margin-top: 20px;
}

:deep(.van-tab.van-tab--active) {
  font-weight: 700;
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
      flex: 1;
      text-align: center;
      font-weight: 700;
      font-size: 20px;
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
    margin-top: 18px;
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

            .mecharts-box {
              margin-right: 10px;
            }

            .flex-r-item {
              flex: 1;
              align-self: center;

              .last-item {
                text-align: right;
                color: $text_color;
                font-weight: 700;
                font-size: 12px;
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