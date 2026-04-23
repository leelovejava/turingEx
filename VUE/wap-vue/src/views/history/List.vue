<template>
  <section class="pb-20">
    <div class="container-box">
      <header class="header">
        <div class="flex-l flex">
          <div class="icon back" @click="handleBack">
            <van-icon name="arrow-left" size="20" />
          </div>
          <span class="header-title">{{ t('historyOrders') }}</span>
        </div>
      </header>
      <div class="profit">
        <p>{{ t('ProfitAndLoss') }}</p>
        <p :class="{ 'value': true, 'green': profitAndLoss > 0, 'red': profitAndLoss <= 0 }">{{ profitAndLoss }}</p>
      </div>
      <!-- <van-pull-refresh v-model="refreshing" @refresh="onRefresh"> -->
      <van-list v-model:loading="loading" :finished="finished" :loading-text="$t('loading')" :finished-text="$t('noMore')"
        @load="getHistoryList" class="exchange-list-box">
        <div class="symbol-list">
          <ul>
            <li class="symbol-list-item" v-for="item in list" :key="item" @click="openDetailModal(item)">
              <div class=" symbol-list-top">
                <div class="item-l flex-1">
                  <p class="symbol-name">{{ item.symbol }}</p>
                  <p class="price-change">
                    <span class="price-l">{{ item.trade_avg_price }}</span>
                    <span class="arrow">→</span>
                    <span
                      :class="{ 'price-r': true, 'green': (item.mark_price - item.close_avg_price) > 0, 'red': (item.mark_price - item.close_avg_price) < 0 }">{{
                        item.close_avg_price }}</span>
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
                    <p class="id">{{ t('orderId') }}: {{ item.order_no }}</p>
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
      </van-list>
      <!-- </van-pull-refresh> -->
      <div class="no-data" v-if="list.length === 0">
        <img src="@/assets/image/no-data.png" alt="">
      </div>
      <van-popup v-model:show="isShow" round position="bottom" closeable close-icon-position="top-left"
        :style="{ height: '400px' }">
        <div class="popup-container">
          <p class="title">{{ t("orderInfo") }}</p>
          <div class="symbol-list-item">
            <div class="symbol-list-top">
              <div class="item-l flex-1">
                <p class="symbol-name">{{ details.symbol }}</p>
                <p class="price-change">
                  <span class="price-l">{{ details.trade_avg_price }}</span>
                  <span class="arrow">→</span>
                  <span
                    :class="{ 'price-r': true, 'green': (details.close_avg_price - details.trade_avg_price) > 0, 'red': (details.close_avg_price - details.trade_avg_price) < 0 }">{{
                      details.close_avg_price }}</span>
                </p>
              </div>
              <div class="item-r flex-1">
                <p class="tile">{{ t('Market Price') }}</p>
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
                  <p class="margin">Margin: {{ details.deposit }}</p>
                  <p class="id">Order Id: {{ details.order_no }}</p>
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
import { contractOrder } from "@/service/trade.api.js";
import { _getContractOrderAssets } from "@/service/user.api.js";
import { useI18n } from "vue-i18n";

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const noData = ref(false)
const list = ref([])
const loading = ref(false);
const finished = ref(false)
const refreshing = ref(false)
const page = ref(1)
let orderType = ref('hisorders')
const profitAndLoss = ref(0)
const isPoll = ref(false)
const isShow = ref(false)
const details = ref({})

onMounted(async () => {
  getContractOrderAssets()
})

const handleBack = () => {
  if (route.query.from === 'trade') {
    router.push(`/trade/index?tabActive=2&navActive=2`)
  } else {
    router.back()
  }
}

const getHistoryList = () => {
  let obj = {
    type: orderType.value,
    page_no: page.value,
    page_size: '10',
    symbolType: 'forex'
  }
  contractOrder(obj).then((res) => {
    loading.value = false;
    refreshing.value = false;
    if (!isPoll.value) {
      page.value++;
    }
    list.value = list.value.concat(res)
    // let result = list.value.reduce((prev, cur) => {
    //   prev = prev + ((cur.mark_price - cur.trade_avg_price) * cur.volume)
    //   return prev
    // }, 0) || 0
    // profitAndLoss.value = result.toString().indexOf(".") > 0 ? result.toFixed(2) : result
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

const onRefresh = () => {
  finished.value = false
  loading.value = true
  list.value = []
  page.value = 1
  noData.value = false
  getHistoryList()
}

const openDetailModal = (item) => {
  details.value = item
  isShow.value = true
}

// 获取资产,新增字段
const getContractOrderAssets = () => {
  let params = {
    type: orderType.value,
    page_no: 1,
    page_size: "all"
  }
  _getContractOrderAssets(params)
    .then(res => {
      profitAndLoss.value = res.profitAndLoss ?? '--'
    })
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

    .header-title {
      font-weight: 600;
      font-size: 20px;
      color: $text_color;
    }

    .flex-l {
      align-items: center;

      .icon {
        margin-right: 10px;
        width: 20px;
        height: 20px;
      }
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
        background: $selectSymbol_background;
        box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.21);
        border-radius: 12px;
        padding: 16px 14px;
        font-size: 14px;

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