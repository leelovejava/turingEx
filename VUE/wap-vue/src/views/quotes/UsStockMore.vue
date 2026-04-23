<template>
  <section class="pb-fix">
    <div class="container-box">
      <header class="header">
        <div class="flex-l">
          <div class="icon back">
            <!-- <img src="@/assets/image/back.png" alt="" @click="handleBack"> -->
            <van-icon name="arrow-left" size="20" @click="handleBack" />
          </div>
          <span class="title">{{ typName }}</span>
        </div>
        <div class="icon-group">
          <div class="icon search" @click="onRoute('/optional/search?symbolType=US-stocks')">
            <img src="@/assets/image/optional/search.png" alt="">
          </div>
        </div>
      </header>
      <section class="tab-container">

        <div class="all-etf-ranking">
          <div class="etf-table">
            <ul>
              <li class="title-line">
                <div class="flex-l">
                  <p>{{ t('nameCode') }}</p>
                </div>
                <div class="flex-r">
                  <div class="flex-r-item">
                    <p>{{ t('Premarketfall') }}</p>
                  </div>
                  <div class="flex-r-item">
                    <p>{{ t('premarketPrice') }}</p>
                  </div>
                </div>
              </li>
              <van-list v-model:loading="loading" :finished="finished" :loading-text="$t('loading')"
                :finished-text="$t('noMore')" @load="onLoad" :offset="130">
                <li v-for="(item, index) in list" :key="item.symbol" @click="itemClick(item)">
                  <div class="flex-l">
                    <p>{{ item.name }}</p>
                    <p class="gray-text">
                      {{ item.symbol }}
                    </p>
                  </div>
                  <div class="flex-r">
                    <div class="flex-r-item">
                      <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                        <span>{{ item.changeRatio }}%</span>
                      </p>
                    </div>
                    <div class="flex-r-item">
                      <p>{{ item.close }}</p>
                    </div>
                  </div>
                </li>
              </van-list>
            </ul>
          </div>
        </div>
      </section>
    </div>
  </section>
</template>
    
<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n'
import { _getRealtimeByType } from '@/service/quotes.api'



const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const list = ref([])
const typName = ref('')
const tabIndex = ref(0)
const symbolType = route.query.symbolType
const loading = ref(false);
const finished = ref(false);
const category = ref('')
const pageNo = ref(1); // 初始页码

onMounted(async () => {
  typName.value = route.query.typName
  tabIndex.value = route.query.tabIndex
  category.value = route.query.category


})


const getRealtimeByType = () => {
  _getRealtimeByType({
    type: symbolType,
    pageNo: pageNo.value,
    category: category.value,
  }).then(data => {
    list.value = [...list.value, ...data];
    // 如果返回的数据小于20条，表示已加载完全部数据，清除定时器
    loading.value = false
    if (data.length < 20) {
      finished.value = true
    }

    pageNo.value++
  })
}

const onLoad = () => {
  getRealtimeByType()
}

const itemClick = (item) => {
  if(category.value){
    router.push(`/quotes/usStockDetail?symbol=${item.symbol}&symbolType=${symbolType}&enName=${item.enName}&isMore=1&tabIndex=${tabIndex.value}&typName=${typName.value}&category=${category.value}`)
  }else{
    router.push(`/quotes/usStockDetail?symbol=${item.symbol}&symbolType=${symbolType}&enName=${item.enName}&isMore=1&tabIndex=${tabIndex.value}&typName=${typName.value}`)
  }
 
}

const onRoute = (path) => {
  router.push(path)
}

const handleBack = () => {
  if(route.query?.tabIndex){
    router.push(`/quotes/index?tabActive=${route.query.tabIndex}`)
  }else {
    onRoute('/quotes/index?tabActive=4')
  }
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

        img {
          filter: $filter;
        }
      }
    }


  }

  .tab-container {
    margin-top: 18px;
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
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
    line-height: 18px;
    border-bottom: 1px solid $border_color;

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
</style>