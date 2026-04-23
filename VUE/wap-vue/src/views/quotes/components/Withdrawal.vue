<template>
  <section class="inner-tab-container">
    <van-loading color="#1194F7" class="loading-box" v-if="isLoading" />
    <section class="etf-container">
      <div class="all-etf-ranking">
        <div class="etf-table">
          <ul>
            <li class="title-line">
              <div class="flex-l">
                <p>{{ t('委托时间') }}</p>
              </div>
              <div class="flex-r">
                <div class="flex-r-item">
                  <p>{{ t('EntrustmentTransactionPrice') }}</p>
                </div>
                <div class="flex-r-item">
                  <p>{{ t('OrderQuantity') }}</p>
                </div>
                <div class="flex-r-item">
                  <p>{{ t('状态') }}</p>
                </div>
              </div>
            </li>
            <li v-for="(item) in listData" :key="item.symbol" @click="itemClick(item)">
              <div class="flex-l">
                <p>{{ item.symbol }}</p>
                <p class="gray-text">
                  <span class="tip-text" :class="item.open === 'close' ? 'text-down' : 'text-up'">{{ item.offset ===
                    'open' ? t('买') : t('卖')
                  }}</span>
                  {{ item.create_time?.slice(11) }}
                </p>
              </div>
              <div class="flex-r">
                <div class="flex-r-item">
                  <p :class="item.open < 1 ? 'text-up' : 'text-down'">{{
                    item.price
                  }}</p>
                  <p :class="item.open < 1 ? 'text-up' : 'text-down'">{{
                    item.closePrice
                  }}</p>
                </div>
                <div class="flex-r-item">
                  <p :class="item.close < 1 ? 'text-up' : 'text-down'">{{
                    item.volume
                  }}</p>
                </div>
                <div class="flex-r-item">
                  <p>{{ item.state === 'created' ? t('createdNew') : t(`${item.state}`) }}</p>
                </div>
              </div>
            </li>
          </ul>
          <div v-if="listData.length === 0" class="flex flex-col justify-center items-center pt-185 no-data">
            <img src="@/assets/image/assets-center/no-data.png" alt="no-date" class="w-100 h-100 no-data-img" />
            <p class="textColor">{{ $t('暂无数据') }}</p>
          </div>
        </div>
      </div>
    </section>
  </section>
</template>
    
<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { _getExchangeCanceledHisList } from '@/service/etf.api'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/store/user';

const userStore = useUserStore()
const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const listData = ref([])
const interval = ref(null)
const { type } = route.query
let isLoading = ref(false)

onMounted(async () => {
  if (!userStore.userInfo.token) {
    router.push('/login')
    return
  }
  getExchangeCanceledHisList()
  // interval.value = setInterval(() => {
  //   fetchQuotes()
  // }, 1000)
})

onBeforeUnmount(() => {
  // if (interval.value) {
  //   clearInterval(interval.value)
  // }
})

const getExchangeCanceledHisList = () => {
  isLoading.value = true
  _getExchangeCanceledHisList(type).then(data => {
    isLoading.value = false
    listData.value = data
  }).catch((e) => {
    isLoading.value = false
  })
}

const itemClick = (item) => {
  if (type === 'indices') {
    router.push(`/quotes/detail?symbol=${item.symbol}&from=position`)
  } else {
    router.push(`/quotes/usStockDetail?symbol=${item.symbol}&from=position`)
  }
}

</script>
<style lang="scss" scoped>
.no-data {
  margin-top: 185px;
}

.loading-box {
  position: absolute;
  top: 200px;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 999;
}

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

:deep(.van-button--danger) {
  border: none;
  background-color: $red;
}

:deep(.sell-box .van-button--danger) {
  border: none;
  background-color: $btn_main;
}

:deep(.trade-order-area .van-field) {
  padding: 4px 6px;
  background: $selectSymbol_background;
  border: 1px solid $red;
  border-radius: 2px;
}

:deep(.trade-order-area.sell-box .van-field) {
  padding: 4px 6px;
  background: $selectSymbol_background;
  border: 1px solid $color_main;
  border-radius: 2px;
}

:deep(.trade-order-area .van-field__control) {
  color: $text_color;
}

:deep(.van-stepper__minus) {
  background: $input_background;
  border: 1px solid $red;
  border-radius: 2px;
  color: $red;
}

:deep(.van-stepper__plus) {
  background: $input_background;
  border: 1px solid $red;
  border-radius: 2px;
  color: $red;
}

:deep(.van-stepper__input) {
  background: $input_background;
  color: $text_color;
  border-top: 1px solid $red;
  border-bottom: 1px solid $red;
  margin: 0;
}

:deep(.sell-box .van-stepper__minus) {
  background: $input_background;
  border: 1px solid $color_main;
  border-radius: 2px;
  color: $color_main;
}

:deep(.sell-box .van-stepper__plus) {
  background: $input_background;
  border: 1px solid $color_main;
  border-radius: 2px;
  color: $color_main;
}

:deep(.sell-box .van-stepper__input) {
  background: $input_background;
  color: $text_color;
  border-top: 1px solid $color_main;
  border-bottom: 1px solid $color_main;
  margin: 0;
}


.container-box {
  .green {
    color: $green;
  }

  .red {
    color: $red;
  }

  .inner-tab-container {
    position: relative;
    margin-top: 8px;
  }

  .orderbook-container {
    padding: 0 12px 16px;

    .trade-order-area {
      display: flex;
      flex-direction: column;
      width: 210px;
      font-size: 12px;
      margin-right: 12px;

      .line-first {
        display: flex;
      }

      .line-first-l {
        flex: 1;
      }

      .line-first-r {
        width: 50px;
        padding: 0 2px;
        border: 1px solid$border_color;
        border-radius: 2px;
        color: $text_color6;
      }

      .buy-btn {
        height: 32px;
      }

      .name-input {
        margin-top: 10px;
      }

      .price-input {
        margin-top: 10px;
      }

      .number-input {
        margin-top: 10px;
      }

      .value-input {
        margin-top: 10px;
      }

      .buy-number {
        margin-top: 10px;
      }

      .num {
        color: #E19841;
        margin: 0 4px;
      }

      .freight {
        margin: 10px 0;
        display: flex;

        span {
          flex: 1;
          background: #1B2134;
          border-radius: 2px;
          text-align: center;
        }

        span:not(:last-child) {
          margin-right: 8px;
        }
      }

      .desc {
        margin-top: 10px;
        display: flex;
        justify-content: space-between;
      }
    }

    .trade-deep-data {
      flex: 1;
    }

    .line-first {
      display: flex;
    }

    .select-label {
      padding-left: 4px;
    }

    .select-icon {
      height: 11px;
      width: 14px;
      padding-right: 4px;
    }

    .option-box {
      position: absolute;
      left: 0;
      right: 0;
      top: 5.625rem;
      width: 100%;
      background-color: $border-grey;
      text-align: center;
      box-shadow: 0px 0px 0.1875rem 0.1875rem $border-grey;
      border-radius: 0.25rem;
      color: $text_color4;

      >div {
        padding: 1.875rem 0;
      }
    }
  }

  .indicator-index-container {
    .indicator-index-box {
      padding: 12px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .flex-l {
        flex: 1;

        ul {
          display: flex;

          li {
            flex: 1;
            text-align: center;
            margin: 0 4px;
            font-size: 12px;
            border-radius: 4px;
          }
        }
      }

      .flex-r {
        display: flex;
        justify-content: flex-end;
        width: 80px;

        img {
          width: 12px;
          height: 16px;
        }

      }
    }

    .active {
      background: $btn_main;
      color: $white;
    }
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

            .tip-text {
              width: 27px;
              height: 16px;
              line-height: 16px;
              text-align: center;
              border-radius: 4px;
              color: $white;
              display: inline-block;

              &.text-up {
                background: $red;
              }

              &.text-down {
                background: $green;
              }
            }
          }

          .flex-l {
            width: 100px;
          }

          .flex-r {
            display: inline-flex;
            flex: 1;

            .flex-r-item {
              flex: 1;
              align-self: center;
              text-align: center;
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