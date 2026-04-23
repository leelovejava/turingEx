<template>
  <div class="px-4 py-2 bg-white shadow-md">
    <h2 class="text-3xl font-bold">{{ $t('quotes') }}</h2>
    <div class="overflow-auto flex tab mt-4 filter-box">
      <!-- <div class="item" @click="hanlerShowAllList">
        <img class="w-4 h-4" src="./all.png" alt="all">
      </div> -->
      <div class="item" :class="{ 'active': activeIndex === 0 }" @click="tabItemClick(0)">
        <img class="w-4 h-4" src="./collect.png" alt="collect">
      </div>
      <div class="item" v-for="(item, index) in tabList" :key="item + index"
        :class="{ 'active': activeIndex === index + 1 }" @click="tabItemClick(index + 1, item.id, item.value)">
        {{ item.name }}
      </div>
      <!-- <div class="item" @click="handlerShowAddList" v-if="userStore.userInfo && userStore.userInfo.token">
        {{ $t('addList') }}
      </div> -->
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { showToast } from "vant"
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';
import { _itemUserOptionalList } from '@/service/quotes.api'
import { useI18n } from "vue-i18n";
const { t } = useI18n()
const userStore = useUserStore()
const emits = defineEmits(['advanced-mode', 'change-list'])

// tab 列表点击
const activeIndex = ref(1)
const router = useRouter()
const tabList = ref([
  {
    name: t("外汇"),
    value: "forex",
    id: "",
  }, {
    name: t("大宗商品"),
    value: "commodities",
    id: "",
  }, {
    name: t("指数"),
    value: "indices",
    id: "",
  }, {
    name: t("加密货币"),
    value: "cryptos",
    id: "",
  }
]);
onMounted(() => {
  if (userStore.userInfo && userStore.userInfo.token) {
    // itemUserOptionalListFn()
  }
})

// 添加
const handlerShowAddList = () => {
  if (tabList.value.length >= 5) {
    showToast(t('limitMaxList'));
  } else {
    router.push('/quotes/add')
  }
}
const hanlerShowAllList = () => {
  router.push('/quotes/allList')
}

const tabItemClick = (index, id, value) => {
  activeIndex.value = index
  emits('change-list', { id, value })
}
const itemUserOptionalListFn = () => {
  _itemUserOptionalList().then((res) => {
    tabList.value = res
  })
}
</script>

<style lang="scss" scoped>
.filter-box {
  padding-bottom: 10px;
}
.tab {
  .item {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    margin-right: 12px;
    padding: 3px 15px;
    border-radius: 10px;
    background: #F1F3F9;
    font-size: 12px;
    color: #1F2025;

    &:last-child {
      margin-right: 0;
    }
  }

  .active {
    background:  $red;
    color: $text_color;
  }
}

// :deep(.add.van-button) {
//   // height: 36px;
// }
</style>
