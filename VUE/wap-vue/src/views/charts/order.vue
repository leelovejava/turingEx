<template>
  <div class="order">
    <van-loading color="#1194F7" v-if="isLoading" />
    <van-sticky>
      <div class="flex justify-between py-4 px-4" style="background: #fff;">
        <van-icon name="arrow-left" @click="$router.go(-1)"></van-icon><span>{{ chartData.symbol }}</span>
        <div class="flex items-center"><img class="img" src="@/assets/image/chart/Group3758.png" />
          <van-icon @click="close" name="cross"></van-icon>
        </div>
      </div>
    </van-sticky>
    <div class="flex justify-center">
      <p class="inline-block text-white px-4 rounded mx-auto flex">
        <van-dropdown-menu>
          <van-dropdown-item v-model="value" :options="options"></van-dropdown-item>
        </van-dropdown-menu>
      </p>
    </div>
    <div class="h-4 border mx-4 border-t-0 border-bd-gray"></div>
    <div class="px-4 mt-4 flex justify-between px-8">
      <div class="flex-1 flex justify-between"><span class="text-gray" @click="onCalc(-10)">-10</span><span
          class="text-gray" @click="onCalc(-1)">-1</span></div>
      <div class="flex justify-between flex-1">
        <!-- {{ amount.toFixed(2) }} -->
        <input class="text-center chatBg" type="number" v-model="deviation" />
      </div>
      <div class="flex-1 flex justify-between"><span class="text-gray" @click="onCalc(1)">+1</span><span class="text-gray"
          @click="onCalc(10)">+10</span></div>
    </div>
    <div class="h-4 border mx-4 border-t-0 border-bd-gray"></div>
    <div class="flex-1 px-4" v-if="value != 0">
      <div class="text-center pt-2  pb-2"><span class="text-base">{{ $t('price') }}</span>
        <!--span.text-lg 85-->
        <!--span.text-xs 4-->
      </div>
      <div class="flex justify-between px-4 flex-1">
        <input class="w-full text-center chatBg" readonly type="number" v-model="chartData.close" />
      </div>
      <div class="h-4 border border-t-0 border-bd-gray"> </div>
    </div>
    <div class="px-4 mt-4 flex justify-between gap-6">
      <div class="flex-1">
        <div class="text-right text-down flex  justify-end">
          <span class="font-semibold text-lg">
            {{ chartData.ask ? chartData.ask.toString().substr(0, chartData.ask.toString().length - 3) : '' }}
          </span>
          <span class="font-semibold text-lg text-24">
            {{
              chartData.ask ? chartData.ask.toString().substr(chartData.ask.toString().length - 3, 2) : '--'
            }}
          </span>
          <span class="text-xs">
            {{ chartData.ask ? chartData.ask.toString().substr(chartData.ask.toString().length - 1) : '' }}
          </span>
        </div>
        <div class="flex justify-between px-4 flex-1 mt-3">
          <span @click="sellReducePrice" :class="value == 0 ? 'text-gray' : ''">-</span>
          <div class="text-gray" v-if="value == 0">{{ chartData.ask ?? '--' }}</div>
          <input v-else class="text-black w-full text-center chatBg" placeholder="SL" type="number" v-model="sellPrice" />
          <span @click="sellAddPrice" :class="value == 0 ? 'text-gray' : ''">+</span>
        </div>
        <div class="h-4 border border-t-0 border-down"> </div>
      </div>
      <div class="flex-1">
        <div class="text-up flex justify-start">
          <span class="font-semibold text-lg">
            {{ chartData.bid ? chartData.bid.toString().substr(0, chartData.bid.toString().length - 3) : '' }}
          </span>
          <span class="font-semibold text-lg text-24">
            {{
              chartData.bid ? chartData.bid.toString().substr(chartData.bid.toString().length - 3, 2) : '--'
            }}
          </span>
          <span class="text-xs">
            {{ chartData.bid ? chartData.bid.toString().substr(chartData.bid.toString().length - 1) : '' }}
          </span>
        </div>
        <div class="flex justify-between px-4 flex-1 mt-3">
          <span @click="buyReducePrice" :class="value == 0 ? 'text-gray' : ''">-</span>
          <div class="text-gray" v-if="value == 0">{{ chartData.bid ?? '--' }}</div>
          <input v-else class="text-black w-full text-center chatBg" placeholder="TP" type="number" v-model="buyPrice" />
          <span @click="buyAddPrice" :class="value == 0 ? 'text-gray' : ''">+</span>
        </div>
        <div class="h-4 border border-t-0 border-up"> </div>
      </div>
    </div>
    <div class="px-4 mt-4  flex ">
      <div class="dad flex-1">
        <div> <span @click="reduceNum">-</span></div>
        <div class="input-center"> <span class="text-gray "> <span>{{ $t('Numberofsheets') }}&nbsp;</span>
            <input type="number" class="text-leftr chatBg" v-model="deviation" /></span>
        </div>
        <div class="input-right"> <span @click="addNum">+</span></div>
        <div class="h-4 lever-border border  border-t-0 border-bd-gray flex-1"></div>
      </div>


      <div class="lever-box flex align-center" @click.stop="openSelect">
        {{ listData[activeIndex].lever_rate }}x<img class="down-img" src="@/assets/image/down.png" />
        <div class="select-div" v-if="showSelect">
          <ul>
            <li v-for="(item, index) in listData" :key="index" @click.stop="onSelectSheet(item, index)"
              :class="[index == activeIndex ? 'active' : '']">{{ item.lever_rate }}x
            </li>
          </ul>
        </div>
      </div>
      <!-- <span @click="reduceNum">-</span>
      <span class="text-gray ">张数
        <input type="number" class="text-gray  text-leftr chatBg" v-model="deviation" /></span>

      <span @click="addNum">+</span> -->
    </div>
    <!-- <div class="h-4 lever-border border mx-4 border-t-0 border-bd-gray flex-1"></div> -->
    <!-- <div class="flex justify-between px-4 mt-4 px-8 ">
      <span @click="reduceNum">-</span>
      <span class="text-gray ">张数
        <input type="number" class="text-gray  text-leftr chatBg" v-model="deviation" /></span>

      <span @click="addNum">+</span>
    </div> -->
    <fx-kline :showBottom="false" :height="320" chartType="candle_stroke" :symbol="symbol" @data="onData"
      @loading="onLoading">
    </fx-kline>
    <div class="px-4 mt-4 flex justify-between gap-4">
      <van-button class="flex-1" type="danger" @click="onSubmit('sell')">{{ $t('sell') }} </van-button>
      <van-button class="flex-1" type="primary" @click="onSubmit('buy')">{{ $t('buy') }}</van-button>
      <!--van-button.flex-1(type="primary") 下单-->
    </div>
  </div>
</template>


<script setup>
import fxKline from '@/components/fx-kline/index.vue'
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router';
import { _initClose, _initOpen } from "@/service/trade.api.js";
import { showToast } from 'vant';
import { useI18n } from "vue-i18n";
import { useUserStore } from "@/store/user.js";
const { t } = useI18n()
const useStore = useUserStore();

const value = ref(0)
const isLoading = ref(false)
const showSelect = ref(false)
const router = useRouter()
const route = useRoute()

const chartData = ref({})

const symbol = ref('')
const deviation = ref(1)
const buyPrice = ref(null)
const sellPrice = ref(null)
const price = ref(0)
const initOpen = ref({})
const initClose = ref(null)
const maxAmount = ref(0)
const activeIndex = ref(0)
const listData = ref([{ lever_rate: 1 }])
onMounted(() => {
  symbol.value = route.params.symbol
  initOpenFun()
})

const options = [
  { text: t('marketPrice'), value: 0 },
  { text: t('priceLimit'), value: 1 }
]


const onData = (evt) => {
  console.log(evt)
  chartData.value = evt
}

const onLoading = (val) => {
  isLoading.value = val
}
const openSelect = () => {
  showSelect.value = !showSelect.value
}
const onSelectSheet = (item, index) => {
  activeIndex.value = index
  showSelect.value = false

}

const onCalc = (value) => {
  if (deviation.value + value >= 0) {
    deviation.value += value
  }
}
const initCloseFun = async () => {
  initClose.value = await _initClose({ symbol: symbol.value })
  maxAmount.value = initClose.value.amount
}
const initOpenFun = async () => {
  initOpen.value = await _initOpen({ symbol: symbol.value })
  if (initOpen.value.lever.length !== 0) {
    listData.value = initOpen.value.lever
  }
}
const onSubmit = (direction) => {
  if (!useStore.userInfo.token) {
    router.push('/login')
    return false
  }
  if (value.value == 0) {
    price.value = direction === 'sell' ? chartData.value.ask : chartData.value.bid
    if (price.value === undefined || price.value === null || price.value === '') {
      return false
    }
  } else {
    if (direction == 'sell') {
      if (!sellPrice.value) {
        showToast(t('enterPrice'))
        return
      } else {
        price.value = sellPrice.value
      }
    } else {
      if (!buyPrice.value) {
        showToast(t('enterPrice'))
        return
      } else {
        price.value = buyPrice.value
      }
    }
  }
  router.push({ path: '/chart/result', query: { chartData: JSON.stringify(chartData.value), direction: direction, symbol: symbol.value, deviation: deviation.value, price_type: value.value, price: price.value, lever_rate: listData.value[activeIndex.value].lever_rate } })
}
const sellReducePrice = () => {
  if (value.value != 0) {
    sellPrice.value = sellPrice.value ? (sellPrice.value * 1 - 0.00001).toFixed(5) : chartData.value.ask
  }
}
const sellAddPrice = () => {
  if (value.value != 0) {
    sellPrice.value = sellPrice.value ? (sellPrice.value * 1 + 0.00001).toFixed(5) : chartData.value.ask
  }
}
const buyReducePrice = () => {
  if (value.value != 0) {
    buyPrice.value = buyPrice.value ? (buyPrice.value * 1 - 0.00001).toFixed(5) : chartData.value.bid
  }
  //price.value = (price.value * 1 + amount.value).toFixed(2)
}
const buyAddPrice = () => {
  if (value.value != 0) {
    buyPrice.value = buyPrice.value ? (buyPrice.value * 1 + 0.00001).toFixed(5) : chartData.value.bid
  }
  //price.value = (price.value * 1 + amount.value).toFixed(2)
}
const addNum = () => {
  deviation.value = deviation.value + 1
}
const reduceNum = () => {
  if (deviation.value - 1 < 1) {
    deviation.value = 1
  } else {
    deviation.value = deviation.value - 1
  }

}
watch(value, (val, oldVal) => {
  if (value.value === 1) {
    sellPrice.value = ''
    buyPrice.value = ''
  }
})
// 返回
const close = () => {
  router.back();
}
</script>

<style lang="scss" scoped>
.order {
  padding-bottom: 20px;
}

.img {
  width: 15px;
  height: 15px;
  margin-right: 15px;
}

:deep(.van-dropdown-menu__bar) {
  height: 32px;
}

.items-center {
  align-items: center;
}

.van-loading {

  position: fixed;

  z-index: 9999;

  width: 100%;

  height: 100%;

  background: rgba(255, 255, 255, 0.5);

  display: flex;

  justify-content: center;

  align-items: center;

}

.dad {}

.dad>div {
  display: inline-block;
  width: 33.3%;

}

.input-center {
  display: table;
  text-align: center;

  span {
    width: 10px;
  }

  input {
    width: 50%;
  }

}

.input-right {
  text-align: right
}

.text-up {
  color: $active_line;
}

.lever-box {
  width: 90px;
  height: 42px;
  margin-left: 15px;
  background: #F1F3F9;
  justify-content: center;
  align-items: center;
  position: relative;

  .down-img {
    width: 15px;
    height: 15px;
    margin-left: 10px;
  }
}

.select-div {
  width: 90px;
  position: absolute;
  top: 42px;
  left: 0;
  z-index: 100;

  ul {
    box-shadow: 0px 3px 11px 0px rgb(0 0 0 / 10%);

    li {
      background: $mainbgWhiteColor;
      text-align: center;
      padding: 5px 0;
      font-size: 16px;
      font-weight: bold;

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

.lever-border {
  width: 100% !important
}
</style>