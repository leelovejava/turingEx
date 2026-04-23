<template>
  <div class="pb-fix">
    <div class="container-box">
      <header class="header">
        <div class="icon-group">
          <div class="icon back" @click="router.go(-1)">
            <van-icon name="arrow-left" size="20" />
          </div>
        </div>
        <div class="title">{{ t('search') }}</div>
      </header>
      <div class="search-container">
        <van-search class="search-input" v-model="searchVal" @input="onInput" :placeholder="$t('searchKeys')"
          @clear="onCancelSearch" background="#131A2E" />
      </div>
      <div class="hot-search">
        <p class="title">{{ t('hotSearch') }}</p>
        <ul class="flex hot-box">
          <li class="flex-1 hot-item" v-for="value in hotSymbol" :key="value" @click="handleClickHotSymbol(value)">{{
            value }}
          </li>
        </ul>
      </div>
      <div class="search-result">
        <p class="title">{{ t('searchResult') }}</p>
        <list-item v-for="(item, index) in searchList" :item="item" :key="index" @click-item="onClickItem"></list-item>
      </div>
    </div>
  </div>
</template>
  
<script setup>
import ListItem from "@/components/list-item/index.vue";
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
const hotSymbol = ["USD", "GBP", "AUD", "HKD"]

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

const onCancelSearch = () => {
  searchVal.value = ''
  searchList.value = allList.value
}


</script>
  
<style lang="scss" scoped>
:deep(.van-nav-bar__title) {
  position: relative;
  top: 2px;
  max-width: 72%;
}

:deep(.van-nav-bar__title) {
  flex: 1;
}

:deep(.van-field__control) {
  font-size: 16px;
  font-weight: 500;
  caret-color: #3157BE;
  color: $text_color;
}

:deep(.van-search__content) {
  border-radius: 10px;
  height: 42px;
  background: $border_color;
}

:deep(.van-search__field.search-input) {
  line-height: 42px;
}

:deep(.search-result .van-cell) {
  background: $selectSymbol_background;
}

:deep(.search-result .van-cell::after) {
  border-bottom: 1px solid $border_color;
}

.container-box {
  padding: 0 16px;

  .header {
    .title {
      margin: 20px 0;
      font-weight: 600;
      font-size: 20px;
      color: $text_color;
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
    }
  }

  .hot-search {
    margin-top: 20px;
    padding: 0 12px;

    p.title {
      font-size: 18px;
      margin-bottom: 20px;
    }

    .hot-item {
      background-color: hsla(0, 0%, 92.9%, .46);
      border-radius: 10px;
      padding: 7px 0;
      margin: 0 6px;
      text-align: center;
    }
  }

  .search-result {
    margin-top: 30px;
    padding: 0 12px;

    p {
      font-size: 18px;
    }
  }
}
</style>