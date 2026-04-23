<template>
  <section class="pb-10">
    <div class="container-box">
      <header class="header mb-2">
        <div class="flex-l" @click="handleBack">
          <div class="icon back">
            <van-icon name="arrow-left" size="20" />
          </div>
        </div>
        <div class="icon-group">
          <div class="icon record" @click="onRoute('/history')">
            <img src="@/assets/image/foreign/history.png" alt="">
          </div>
        </div>
      </header>
      <div class="tabs flex">
        <div class="tab-item flex-1" v-for="(item, index) in tabList" @click="selectIndex(index)"
          :class="[tabIndex === index ? 'active' : '']" :key="item">
          {{ item.title }}
        </div>
      </div>
      <div class="profit" v-if="tabIndex === 0">
        <p>{{ t('ProfitAndLoss') }}</p>
        <p :class="{ 'value': true, 'green': profitAndLoss > 0, 'red': profitAndLoss <= 0 }">{{ profitAndLoss }}</p>
      </div>
      <div class="profit" v-if="tabIndex === 1">
        <p>{{ t('ExpectedProfitAndLoss') }}</p>
        <p :class="{ 'value': true, 'green': expectedProfitAndLoss > 0, 'red': expectedProfitAndLoss <= 0 }">{{
          expectedProfitAndLoss }}
        </p>
      </div>
      <div class="card-info">
        <ul>
          <li class="flex">
            <div class="flex-l flex-1">
              <!-- 净值 -->
              {{ t('netWorth') }}
            </div>
            <div class="flex-r w-100">
              {{ money_wallet ?? '--' }}
            </div>
          </li>
          <li class="flex">
            <div class="flex-l flex-1">
              <!-- 预付款比, 净值/已用保证金*100% -->
              {{ t('advancePaymentRatio') }}
            </div>
            <div class="flex-r w-100">
              {{ `${((money_wallet ?? 0) / (typeof (allDeposit) === 'number' && allDeposit !== 0 ? allDeposit : 1) *
                100).toFixed(2)}%` ??
                '--' }}
            </div>
          </li>
          <li class="flex">
            <div class="flex-l flex-1">
              <!-- 可用预付款 -->
              {{ t('prepaymentAvailable') }}
            </div>
            <div class="flex-r w-100">
              {{ money_wallet ?? 0 * 1 + money_contract_profit ?? 0 * 1 }}
            </div>
          </li>
        </ul>
      </div>
      <!-- <van-pull-refresh v-model="refreshing" @refresh="onRefresh"> -->
      <div class="symbol-list">
        <ul>
          <li class="symbol-list-item" v-for="item in list" :key="item" @click="openDetailModal(item)">
            <div class="symbol-list-top">
              <div class="item-l flex-1">
                <p class="symbol-name">{{ item.symbol }}</p>
                <p class="price-change" v-if="tabIndex !== 1">
                  <span class="price-l">{{ item.trade_avg_price }}</span>
                  <span class="arrow">→</span>
                  <span
                    :class="{ 'price-r': true, 'green': (item.mark_price - item.trade_avg_price) > 0, 'red': (item.mark_price - item.trade_avg_price) < 0 }">{{
                      item.mark_price }}</span>
                </p>
                <p class="price-change" v-if="tabIndex === 1">
                  <span class="price-l">{{ item.price }}</span>
                  <span class="arrow">→</span>
                  <span
                    :class="{ 'price-r': true, 'green': (item.mark_price - item.price) > 0, 'red': (item.mark_price - item.price) < 0 }">{{
                      item.mark_price }}</span>
                </p>
              </div>
              <div class="item-r flex-1">
                <p class="tile">{{ item?.order_price_type === 'opponent' ? t('市价') : t("限价") }}</p>
                <div class="volume-box">
                  <div
                    :class="{ 'volume': true, 'green-border': item.direction === 'buy', 'red-border': item.direction !== 'buy' }">
                    <span
                      :class="{ 'volume-l': true, 'green-bg': item.direction === 'buy', 'red-bg': item.direction !== 'buy' }">{{
                        item.direction == 'buy' ? t('Buy') : t('Sell') }}</span>
                    <span
                      :class="{ 'volume-r': true, 'green': item.direction === 'buy', 'red': item.direction !== 'buy' }">
                      {{ `${item.volume_open / (item.lever_rate ?? 1)} * ${(item.lever_rate ?? 1)} x` }} {{ t('volume')
                      }}</span>

                  </div>
                </div>
              </div>
            </div>
            <div class="symbol-list-bottom">
              <div class="bottom-l flex-1">
                <div class="order-info">
                  <p class="margin">{{ t('margin') }}: {{ item.deposit }}</p>
                  <p class="id">{{ t('orderId') }}: #{{ item.order_no }}</p>
                  <p class="date">{{ item.create_time }}</p>
                </div>
              </div>
              <div class="bottom-r">
                <p :class="{ 'profit-num': true, 'green': item.profit > 0, 'red': item.profit < 0 }">{{ item.profit }}
                </p>
              </div>
            </div>
          </li>
        </ul>
      </div>
      <!-- </van-pull-refresh> -->
      <div class="no-data" v-if="list.length === 0">
        <img src="@/assets/image/no-data.png" alt="">
      </div>
      <van-popup v-model:show="isShow" round position="bottom" closeable close-icon-position="top-left"
        :style="{ height: '450px' }">
        <div class="popup-container">
          <p class="title">{{ t("orderInfo") }}</p>
          <div class="symbol-list-item">
            <div class="symbol-list-top">
              <div class="item-l flex-1">
                <p class="symbol-name">{{ details.symbol }}</p>
                <p class="price-change" v-if="tabIndex === 0">
                  <span class="price-l">{{ details.trade_avg_price }}</span>
                  <span class="arrow">→</span>
                  <span
                    :class="{ 'price-r': true, 'green': (details.mark_price - details.trade_avg_price) > 0, 'red': (details.mark_price - details.trade_avg_price) < 0 }">{{
                      details.mark_price }}</span>
                </p>
                <p class="price-change" v-if="tabIndex === 1">
                  <span class="price-l">{{ details.price }}</span>
                  <span class="arrow">→</span>
                  <span
                    :class="{ 'price-r': true, 'green': (details.mark_price - details.price) > 0, 'red': (details.mark_price - details.price) < 0 }">{{
                      details.mark_price }}</span>
                </p>
              </div>
              <div class="item-r flex-1">
                <p class="tile">{{ details?.order_price_type === 'opponent' ? t('市价') : t("限价") }}</p>
                <div class="volume-box">
                  <div
                    :class="{ 'volume': true, 'green-border': details.direction === 'buy', 'red-border': details.direction !== 'buy' }">
                    <span
                      :class="{ 'volume-l': true, 'green-bg': details.direction === 'buy', 'red-bg': details.direction !== 'buy' }">{{
                        details.direction == 'buy' ? t('Buy') : t('Sell') }}</span>
                    <span
                      :class="{ 'volume-r': true, 'green': details.direction === 'buy', 'red': details.direction !== 'buy' }">
                      {{ `${details.volume_open / (details.lever_rate ?? 1)} * ${(details.lever_rate ?? 1)} x` }}
                      {{ t('volume') }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="card-info flex">
              <div class="card-item flex-1">
                <p class="name">{{ t('Interest') }}</p>
                <p class="num">{{ details.profit }}</p>
              </div>
              <div class="card-item flex-1">
                <p class="name">{{ t('fee') }}</p>
                <p class="num">{{ details.fee }}</p>
              </div>
            </div>
            <div class="symbol-list-bottom">
              <div class="bottom-l flex-1">
                <div class="order-info">
                  <p class="margin">{{ t('margin') }}: {{ details.deposit }}</p>
                  <p class="id">{{ t('orderId') }}: {{ details.order_no }}</p>
                  <p class="date">{{ details.create_time }}</p>
                </div>
              </div>
              <div class="bottom-r">
                <p :class="{ 'profit-num': true, 'green': details.profit > 0, 'red': details.profit < 0 }">{{
                  details.profit
                }}
                </p>
              </div>
            </div>
            <div class="custom-button" @click="handleClosePosition(details.order_no)" v-if="tabIndex === 0">
              <span class="text">{{ t('cover') }}</span>
              <span class="arrow">
                <van-icon name="success" />
              </span>
            </div>
            <div class="custom-button" @click="cancelOrder(details.order_no)" v-else>
              <span class="text">{{ t('cancel') }}</span>
              <span class="arrow">
                <van-icon name="success" />
              </span>
            </div>
          </div>
        </div>
      </van-popup>
    </div>
  </section>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from "vue";
import { showToast } from "vant";
import {
  useRouter,
  useRoute
} from 'vue-router';
import {
  contractOrder,
  _contractApplyOrderCancel,
  _contractOrderClose,
  _contractApplyOrderList
} from "@/service/trade.api.js";
import { _getAllAssets, _getContractOrderAssets, _getEntrustOrderAssets } from "@/service/user.api.js";
import { useI18n } from "vue-i18n";
import { REQUEST_TIMER } from '@/config'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const tabIndex = ref(0)
const tabList = ref([
  { title: t('持仓') },
  { title: t('entrustOrder') },
])
const noData = ref(false)
const list = ref([])
const loading = ref(false);
const finished = ref(false)
const page = ref(1)
let orderType = ref('orders')
const total = ref(0) //总值
const allDeposit = ref('∞') //总值
const money_wallet = ref(0) //净值
const money_contract_profit = ref(0) //未结盈利
const profitAndLoss = ref(0)
const expectedProfitAndLoss = ref(0)
const interval = ref(null)
const isPoll = ref(false)
const isShow = ref(false)
const details = ref({})

onMounted(async () => {
  getAllAssets()
  getContractOrderAssets()
  getEntrustOrderAssets()
  getContractOrder()
  interval.value = setInterval(() => {
    getContractOrder()
    getAllAssets()
    tabIndex.value === 0 ? getContractOrderAssets() : getEntrustOrderAssets()
  }, 5000)
})

onBeforeUnmount(() => {
  if (interval.value) {
    clearInterval(interval.value)
  }
})

const handleBack = () => {
  if (route.query.from === 'trade') {
    router.push(`/trade/index?tabActive=2&navActive=0`)
  } else {
    router.back()
  }
}

const openDetailModal = (item) => {
  details.value = item
  console.log(item, 'item11111')
  isShow.value = true
}

const selectIndex = (index) => {
  tabIndex.value = index
  orderType.value = 'orders'
  page.value = 1
  list.value = []
  getContractOrder()
}

const getContractOrder = () => {
  let obj = {
    type: orderType.value,
    page_no: page.value,
    page_size: 'all',
    symbolType: 'forex'
  }
  let api = tabIndex.value === 0 ? contractOrder : _contractApplyOrderList
  api(obj).then((res) => {
    loading.value = false;
    // if (!isPoll.value) {
    //   page.value++;
    // }
    list.value = res
    // 如果没有数据，显示暂无数据
    if (list.value.length === 0 && page.value === 1) {
      noData.value = true
    }
    // 如果加载完毕，显示没有更多了
    if (res.length === 0) {
      finished.value = true
    }
  })
}

// 获取资产
const getAllAssets = () => {
  _getAllAssets()
    .then(res => {
      total.value = res.total
      money_wallet.value = res.money_wallet
      money_contract_profit.value = res.money_contract_profit
    })
}

// 获取持仓资产
const getContractOrderAssets = () => {
  let params = {
    type: orderType.value,
    page_no: 1,
    page_size: "all"
  }
  _getContractOrderAssets(params)
    .then(res => {
      allDeposit.value = res.deposit_all ?? '--'
      profitAndLoss.value = res.profitAndLoss ?? '--'
    })
}

// 获取委托订单资产
const getEntrustOrderAssets = () => {
  let params = {
    type: orderType.value,
    page_no: 1,
    page_size: "all"
  }
  _getEntrustOrderAssets(params)
    .then(res => {
      expectedProfitAndLoss.value = res.expectedProfitAndLoss ?? '--'
    })
}

// 平仓
const handleClosePosition = (order_no) => {
  let obj = { order_no }
  _contractOrderClose(obj).then((res) => {
    showToast(t('SuccessfulOperation'))
    isShow.value = false
    getContractOrder()
  })
}

// 取消订单
const cancelOrder = (order_no) => {
  let obj = {
    order_no
  }
  orderType.value = 'orders'
  page.value = 1
  list.value = []
  _contractApplyOrderCancel(obj).then((res) => {
    showToast(t('submitSuccess'))
    isShow.value = false
    getContractOrder()
  })
}

const onRoute = (path, tabActive) => {
  router.push(path)
}

</script>

<style lang="scss" scoped>
:deep(.van-icon-cross.van-popup__close-icon.van-popup__close-icon--top-left.van-haptics-feedback) {
  background: #1B2134;
  border-radius: 10px;
  color: #8D9099;
  width: 36px;
  height: 36px;
  padding: 10px;
  font-size: 16px;
  font-weight: 700;
}

:deep(.van-popup--round) {
  background: $selectSymbol_background;
}

.container-box {
  padding: 0 16px;

  .red {
    color: #DE5D56 !important;

  }

  .green {
    color: $green !important;
  }

  .header {
    display: flex;
    align-items: center;

    .flex-l {
      flex: 1;
      display: inline-flex;
      align-items: center;

      .icon {
        margin-right: 10px;
        display: inline-block;
        width: 20px;
        height: 20px;

        img {
          height: 20px;
          width: 20px;
        }
      }

      .name-box {
        .title {
          font-size: 16px;
          font-weight: 700;
          line-height: 16px;
        }

        .type {
          font-size: 12px;
          color: $text_color6;
        }
      }
    }

    .icon-group {
      width: 100px;
      text-align: right;

      .icon {
        display: inline-block;
        width: 24px;
        height: 24px;
        padding: 4px;

        img {
          filter: $filter;
        }
      }
    }


  }


  .tabs {
    background: $tab-second-bg;
    border-radius: 22px;
    height: 52px;
    line-height: 34px;
    color: #BBBCBD;

    .tab-item {
      margin: 4px;
      text-align: center;
      padding: 4px 6px;
      font-size: 14px;
      font-size: 16px;
      color: $text_color5;
    }

    .active {
      color: $main-btn-color !important;
      background: $blue;
      border-radius: 16px;
      background-size: cover;
      font-weight: 700;
    }
  }

  .profit {
    margin: 20px 0 40px;
    display: flex;
    flex-direction: column;
    text-align: center;

    p {
      font-weight: 600;
      font-size: 14px;
      line-height: 32px;
      color: #BBBCBD;
    }

    .value {
      font-size: 32px;
    }
  }

  .card-info {
    background: $tab-second-bg;
    box-shadow: 0px 2px 8px 0px #00000036;
    border-radius: 12px;
    padding: 16px 14px;
    font-size: 14px;


    .flex-l {
      color: #B8BDD1;
      font-size: 13px;
    }

    .flex-r {
      font-weight: 600;
      color: $text_color;
      font-size: 15px;
      ;
    }
  }

  .symbol-list {
    display: flex;
    flex-direction: column;
    margin-top: 10px;
  }

  .van-popup {
    position: relative;

    .popup-container {
      margin-top: 50px;

      .card-info {
        margin: 20px 0;
        padding: 10px 14px;

        .card-item {
          text-align: center;

          .name {
            font-size: 12px;
          }

          .num {
            font-weight: 700;
            font-size: 16px;
            color: $text_color;
          }
        }
      }

      .custom-button {
        position: absolute;
        margin-left: 40px;
        border-radius: 10px 0px 0px 10px;
        left: 0px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 25px;
        width: calc(100vw - 40px);
        height: 50px;
        line-height: 50px;
        color: $text_color;
        font-size: 16px;
        padding: 0 20px;
        background-size: cover;
        background-repeat: no-repeat;
        overflow: hidden;
        background-color: #2c7dff;

        .text::before {}

        .arrow {
          font-size: 24px;
        }
      }
    }

    .title {
      font-size: 24px;
      padding: 20px;
    }

    .symbol-list-item {
      margin: 10px 0;
      border: none;
      padding: 0px 20px 20px;
    }
  }

  .symbol-list-item {
    margin: 10px 0;
    justify-content: space-between;
    padding: 10px;
    font-size: 12px;
    color: #989899;
    font-weight: 600;
    border-bottom: 1px solid $border_color;
  }

  .symbol-list-top {
    display: flex;

    .item-l {
      text-align: left;

      .symbol-name {
        font-size: 16px;
        color: $text_color;
      }

      .price-change {
        color: $text_color;

        .price-l {
          font-weight: 600;
        }

        .arrow {
          margin: 0 6px;
        }
      }

    }

    .item-r {
      text-align: right;

      .volume-box {
        .volume {
          display: inline-flex;
          border: 1px solid $green;
          border-radius: 5px;

          .volume-l {
            width: 40px;
            background-color: $green;
            color: $main-btn-color;
            font-size: 12px;
            text-align: center;
          }

          .volume-r {
            padding: 0 6px;
            color: $green;
            font-weight: 400;
          }
        }

        .red-bg {
          background-color: #DE5D56 !important;
        }

        .green-bg {
          background-color: $green !important;
        }

        .green-border {
          border: 1px solid $green !important;
        }

        .red-border {
          border: 1px solid #DE5D56 !important;
        }
      }
    }
  }

  .symbol-list-bottom {
    display: flex;
    margin-top: 4px;

    .order-info {
      line-height: 18px;
    }

    .bottom-r {
      text-align: right;
      display: flex;

      .profit-num {
        line-height: 54px;
        font-weight: 600;
        font-size: 20px;
      }
    }
  }

  .no-data {
    margin: 20px auto;
    width: 200px;
    height: 200px;
  }

}
</style>