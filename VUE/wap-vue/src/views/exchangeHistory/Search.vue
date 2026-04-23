<template>
  <div class="pb-fix">
    <div class="container-box">
      <div class="search-container flex">
        <div class="icon back">
          <van-icon name="arrow-left" size="20"   @click="router.back()"/>
        </div>
        <van-search class="search-input flex-1" v-model="searchVal" @input="onInput" :placeholder="$t('stockName')"
          @clear="onClearSearch" shape="round" background="#131A2E" />
      </div>
      <div class="hot-search">
        <p class="title">{{ t('最近搜索') }}</p>
        <!-- <ul class="hot-box">
          <li class="hot-item" v-for="value in hotSymbol" :key="value" @click="handleClickHotSymbol(value)">
            <p>
              <span class="num">000001</span>
              <span>IDXSHA</span>
            </p>
            <p>上证指数</p>
          </li>
        </ul> -->
        <p class="tips">近期暂无搜索</p>
      </div>
      <div class="search-result">
        <div class="title-box flex">
          <p class="title">{{ t('操作类型') }}</p>
        </div>
        <div class="btn-group">
          <van-button type="default" size="small" :class="[selected[0] ? 'active buy-btn' : 'buy-btn']"
            @click="handleSelect(0)">买入</van-button>
          <van-button type="default" size="small" :class="[selected[1] ? 'active sell-btn' : 'sell-btn']"
            @click="handleSelect(1)">卖出</van-button>
        </div>
      </div>
    </div>
  </div>
</template>
  
<script setup>
import { ref, onMounted } from "vue";
import { _ItemUserOptionalItemAdd } from '@/service/quotes.api'
import { useQuotesStore } from '@/store/quotes.store'
import { useRouter, useRoute } from "vue-router";
import { useI18n } from "vue-i18n";
import { Search } from 'vant';

const { t } = useI18n()
const router = useRouter()
const route = useRouter()
const quotesStore = useQuotesStore()
const allList = ref([])
const searchList = ref([])
// 搜索
const searchVal = ref('');
const hotSymbol = ["USD", "GBP", "AUD", "HKD", "USD", "GBP", "AUD", "HKD", "BTC"]
const selected = ref([false, false])

onMounted(() => {
  allList.value = quotesStore.coins
  searchList.value = allList.value
})

const onClickItem = (evt) => {
  router.push({ path: '/foreign/coinChart', query: { symbol: evt.symbol } })
  // if (route.query?.id) {
  //   ItemUserOptionalItemAddFn(evt.symbol, route.query.id)
  // }
}

const handleClickHotSymbol = (value) => {
  searchVal.value = value
  searchList.value = allList.value.filter((item) => {
    return item.symbol.indexOf(searchVal.value.toUpperCase()) !== -1
  })
}

const ItemUserOptionalItemAddFn = (symbol, list_id) => {
  _ItemUserOptionalItemAdd({
    symbol: symbol,
    list_id: list_id
  }).then(res => {
    router.push('/home/index')
  })
}
const onInput = () => {
  if (searchVal.value == '') {
    searchList.value = allList.value
  } else {
    searchList.value = allList.value.filter((item) => {
      return item.symbol.indexOf(searchVal.value.toUpperCase()) !== -1
    })
  }
}

const onClearSearch = () => {
  searchVal.value = ''
  searchList.value = allList.value
}

const onRoute = (path) => {
  router.push(path)
}

const itemClick = (item) => {
  console.log(item, 'item')
}

const handleSelect = (index) => {
  selected.value[index] = !selected.value[index]
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
  height: 32px;
  background: $border_color;
}

:deep(.van-field__control::placeholder) {
  color: #747A8F;
  font-size: 12px;
}

:deep(.van-search__field.search-input) {
  line-height: 32px;
}

:deep(.van-search__action) {
  color: $color_main;
  font-size: 12px;
}

:deep(.van-search__action:active) {
  background: $selectSymbol_background;
  color: $color_main;
  font-size: 12px;
}

:deep(.search-result .van-cell) {
  background: $mainBgColor;
}

.container-box {
  padding: 0 16px;

  .search-container {
    padding: 0 12px;
    align-items: center;

    .icon {
      display: inline-block;
      width: 20px;
      height: 20px;
    }
  }

  .hot-search {
    margin-top: 20px;

    .tips {
      text-align: center;
      font-size: 12px;
      color: #747A8F;
    }

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

      .icon {
        width: 16px;
        height: 18px;
      }
    }

  }

  .btn-group {
    margin-top: 10px;

    .buy-btn {
      margin-right: 10px;
      width: 70px;
      background-color: rgba(255, 255, 255, 0.7);
      border: none;
    }

    .sell-btn {
      width: 70px;
      background-color: rgba(255, 255, 255, 0.7);
      border: none;
    }

    .active {
      color: $text_color;
      background-color: $btn_main !important;
    }
  }
}
</style>