<template>
  <div class="DeliveryContractHistory">
    <fx-header>
      <template #title>{{ $t('交割合约历史') }}</template>
    </fx-header>
    <div>
      <van-tabs ref="tabs" v-model:active="tabType" swipeable sticky @change="onChange">
        <van-tab :title="item.title" v-for="item in selectData" :key="item.title" :name="item.tabType">
          <van-list v-model:loading="loading" :loading-text="$t('loading')" :finished="finished"
            :finished-text="dataList[tabType].length ? $t('已经全部加载完毕') : ''" @load="onLoad" :offset="30" class="px-8">
            <futrue-hold-list v-if="tabType === 'orders'" :list-data="dataList.orders">
            </futrue-hold-list>
            <futrue-histroy-position v-else :list-data="dataList.hisorders"></futrue-histroy-position>
            <div class="flex flex-col justify-center pt-6 pb-10 items-center"
              v-if="!dataList[tabType].length && !loading">
              <img src="@/assets/image/assets-center/no-data.png" alt="" class="w-48 h-48" />
              <p class="text-grey mt-3 text-26">{{ $t('暂无记录') }}</p>
            </div>
          </van-list>
        </van-tab>
      </van-tabs>
    </div>
  </div>
</template>
<script setup>
import { List, Tab, Tabs } from 'vant';
import futrueHoldList from '@/views/foreign/deliveryContract/hold.vue'
import futrueHistroyPosition from '@/views/foreign/deliveryContract/position.vue'
import { _futrueOrderList } from "@/service/trade.api.js";
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from "vue-router";
import { useI18n } from "vue-i18n";
const { t } = useI18n()
const router = useRouter()
const route = useRoute()

let tabType = ref("orders")
let dataList = ref(
  {
    orders: [],
    hisorders: []
  }
)
let symbol = ref('')
let selectData = ref(
  [
    { title: t('持有仓位'), tabType: 'orders' },
    { title: t('历史仓位'), tabType: 'hisorders' },
  ],
)

let loading = ref(false)
let finished = ref(false)
let page = ref(1)


onMounted(() => {
  symbol.value = route.query.symbol
})

const onChange = (e) => {
  dataList.value[e] = []
  finished.value = false
  page.value = 1
  tabType.value = e
  loading.value = true;
  if (loading.value) {
    fetchData(symbol.value, tabType.value)
  }
}

const fetchData = (currentSymbol) => {
  // TODO: 分页
  _futrueOrderList(currentSymbol, tabType.value, page.value).then(data => {
    dataList.value[tabType.value] = dataList.value[tabType.value].concat(data)
    loading.value = false
    if (data.length < 10) {
      finished.value = true
    }
    page.value++
  })
}

const onLoad = () => {
  fetchData(symbol.value, tabType.value)
}

</script>
<style lang="scss" scoped>
.DeliveryContractHistory {
  width: 100%;
  box-sizing: border-box;

  .position-padding {
    //border-bottom: 1px solid #F2F4F9;
    padding: 17px;
    box-sizing: border-box;
    width: 100%;
    float: left;

    .position-tag {
      display: flex;
      align-items: center;

      .position-tag-style {
        padding: 5px 14px;
        margin-right: 11px;
        font-style: normal;
        font-weight: 400;
        font-size: 14px;
        color: $main-btn-color;
      }

      .position-tag-title {
        font-style: normal;
        font-weight: 600;
        font-size: 15px;
        margin-right: 7px;
      }

      .position-tag-title2 {
        font-style: normal;
        font-weight: 400;
        font-size: 15px;
        color: $text_color5;
      }

      .position-tag-img {
        margin-left: 5px;
        width: 20px;
        height: 16px;
      }
    }

    .red {
      background: $red;
      border-radius: 3px;
    }

    .green {
      background: $green;
      border-radius: 3px;
    }

    .position-div1 {
      margin-top: 17px;
      width: 100%;
      float: left;

      .position-text1 {
        float: left;
        font-style: normal;
        font-weight: 400;
        font-size: 15px;
        color: $text_color5;
      }

      .position-text2 {
        float: right;
        font-style: normal;
        font-weight: 600;
        font-size: 15px;
      }

      .position-text3 {
        font-style: normal;
        font-weight: 400;
        font-size: 15px;
      }
    }
  }

  .color-red {
    color: $red !important;
  }

  .color-green {
    color: $green !important;
  }

  .color-blue {
    color: $color_main !important;
  }

  :deep(.van-tabs__nav) {
    background: transparent;
  }

  :deep(.van-tab--active) {
    color: $main-btn-color;
    background-color: $color_main;
    border-radius: 4px;
  }

  :deep(.van-tabs__line) {
    display: none;
  }

  :deep(.van-tabs__wrap) {
    padding: 0 10px;
  }

}
</style>
