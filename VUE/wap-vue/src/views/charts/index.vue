<template>
  <div class="chart-index pb-fix">
    <div class="px-2 py-4  chart-info left-0 top-0 border-b-1">
      <div class="text-sm" @click.stop="openSelect">{{ symbol || '--' }}
        <van-icon :name="!showSelect ? 'arrow-down' : 'arrow-up'" />
        <div class="select-div" v-if="showSelect">
          <ul>
            <li v-for="(item, index) in listData" :key="index" @click.stop="onSelectSheet(item, index)"
              :class="[index == active ? 'active' : '']">{{ item.name }}
            </li>
          </ul>
        </div>
      </div>
      <p class="text-xs my-2" :class="chartData.change_ratio > 0 ? 'text-up' : 'text-down'">
        <span class="mr-2">{{ chartData.close || '--' }}</span>
        <span>{{ chartData.netChange || '--' }}({{ chartData.change_ratio || '--' }}%)</span>
      </p>
      <p class="flex items-center">
        <van-button type="danger" plain size="small" class="w-16 h-4 text-xs">{{ formatNumber(chartData.ask) }}</van-button>
        <span class="mx-2 text-base">{{ (chartData.ask - chartData.bid || 0).toFixed(5) }}</span>
        <van-button type="success" size="small" plain class="w-16 h-4">{{ formatNumber(chartData.bid)}}</van-button>
      </p>
      <!-- <p class=" text-xs my-2">
        <span>成交量(VoL)</span>
        <span class="ml-2" :class="chartData.change_ratio > 0 ? 'text-up' : 'text-down'">{{
          chartData.volume || '--'
        }}</span>
      </p> -->
    </div>
    <fx-kline :height="550" :symbol="symbol" :isShowsolid="true" :chartType="chartType" v-if="symbol" @data="onData"
      :key="`${symbol}-${timeValue}`" />
    <div class="flex justify-between px-5 py-2 sticky-box">
      <div @click="showPopup = true" class="flex items-center">
        <img :src="`${IMG_PATH}/symbol/${symbol}.png`" alt="" class="w-6 h-6 mr-2">
        <span class="text-base font-bold">{{ symbol }}</span>
        <span class="ml-4 text-base font-bold">{{ quotesStore.stage }}</span>
      </div>
      <div>
        <van-icon name="todo-list-o" size="25" @click="onRoute"></van-icon>
      </div>
    </div>
    <fx-popup v-model:show="showPopup" @close="showPopup = false" @select="onSelect" />
    <!-- <van-action-sheet v-model:show="show" :actions="listData" @select="onSelectSheet" /> -->
  </div>
</template>

<script setup>
import { Sticky } from 'vant';
import fxKline from '@/components/fx-kline/index.vue'
import fxPopup from '@/components/fx-popup/charts-cycle.vue'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router';
import { IMG_PATH } from '@/config'
import { useQuotesStore } from '@/store/quotes.store';
import { _getQuotes } from '@/service/quotes.api'

const showSelect = ref(false)
const quotesStore = useQuotesStore()
const router = useRouter()
const route = useRoute()
const symbol = ref('')
const timeValue = ref('')
const chartData = ref({})
const listData = ref([])
const active = ref(0)
const chartType = ref('')
onMounted(() => {
  if (route.query.symbol) {
    symbol.value = route.query.symbol
  } else {
    symbol.value = quotesStore.coins.length ? quotesStore.coins[0].symbol : 'EURUSD'
  }
  fetchQuotes()
})

const showPopup = ref(false)
onMounted(() => {
  if (quotesStore.stage == '1min') {
    chartType.value = 'area'
  } else {
    chartType.value = 'candle_solid'
  }
})
const onSelect = (evt) => {
  timeValue.value = evt
  if (evt == '1min') {
    chartType.value = 'area'
  } else {
    chartType.value = 'candle_solid'
  }
}
const onSelectSheet = (item, index) => {
  showSelect.value = false
  symbol.value = item.symbol
  active.value = index
}

const onRoute = () => {
  router.push(`../order/${symbol.value}`)
}
const openSelect = () => {
  showSelect.value = !showSelect.value
}
// 事件
const onData = (data) => {
  chartData.value = data
}
const fetchQuotes = () => {
  _getQuotes(quotesStore.coins).then(data => {
    data.map(item => {
      item.name = item.symbol
    })
    listData.value = data
  })
}

// 小数展示
const formatNumber = (num) => {
  if (typeof (num) !== 'number') {
    return '--'
  }
  let count;
  if (num.toString().indexOf('.') < 0) {
    // 整数
    return num
  } else {
    // 小数
    count = num.toString().split('.').pop().length
    if (count > 5) {
      return num.toFixed(5)
    } else {
      return num
    }
  }

}




</script>
<style lang="scss" scoped>
.sticky-box {
  background-color: rgba(255, 255, 255, 0.9);
  position: fixed;
  bottom: 50px;
  width: 100%;
}

.indicator-box {}

.chart-info {
  z-index: 100;
  border-bottom: 1px solid  $border-grey;
}

.text-sm {
  position: relative;
}

.select-div {
  width: 100px;
  position: absolute;
  top: 30px;
  left: 0;
  z-index: 100;

  ul {
    box-shadow: 0px 3px 11px 0px rgb(0 0 0 / 10%);

    li {
      background:  $mainbgWhiteColor;
      text-align: center;
      padding: 10px 0;
      font-size: 16px;

      // border-bottom: 1px solid #f6f4f4;
      // box-shadow: 0px 3px 11px 0px rgb(0 0 0 / 10%);
    }

    li:not(:last-child) {
      border-bottom: 1px solid $border-grey;
    }
  }
}

.active {
  background: $btn_main !important;
  color: $text_color;
}
</style>