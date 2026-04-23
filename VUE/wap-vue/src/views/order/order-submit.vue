<template>
  <div class="pb-12 order-submit">
    <!-- <fx-header @back="backFun">

    </fx-header> -->
    <van-nav-bar :left-arrow="true" @click-left="onClickLeft">
    </van-nav-bar>
    <van-loading color="#1194F7" v-if="isLoading" class="loading-box" />
    <div v-if="payInfo.uuid">
      <div class="order-top" v-if="payInfo.state == 0">
        <div class="order-top">
          <div class="title">{{ $t('orderGenerated') }}</div>
          <!-- <div class="text mt-2">{{ $t('pleaseAt') }} <span>{{ dayjs.duration(remainingTime, 'seconds').format('mm:ss')}}</span> {{ $t('paymentSeller')}}{{ payInfo.direction == 'recharge' ? $t('买家') : $t('卖家') }}</div> -->
          <div class="customer mt-5" @click="onRoute">
            <div class="flex">
              <div class="flex flex-1 service">
                <img class="serve" src="@/assets/image/order/serve.png" alt="" />
                <span class="mr-2 ml-2">{{ $t('onLineService') }}</span>
                <img class="gold" src="@/assets/image/order/gold.png" alt="" />
              </div>
              <div>
                <div class="support-box">
                  <img src="@/assets/image/order/contact.png" alt="" class="contact-icon" />
                  <span>{{ $t('contactService') }}</span>
                </div>
              </div>
            </div>
            <!-- <div class="flex step mt-5"> -->
              <!-- <img class="success mr-1" src="@/assets/image/order/success.png" alt="" /> -->
              {{ $t('Payconfidence') }}
              <!-- {{ payInfo.direction == 'recharge' ? $t('买家') : $t('卖家') }} {{ payInfo.currency }} {{ $t('已存入交易所账户，请放心付款')}} -->
            <!-- </div> -->
            <div class="flex mt-5">
              <div class="flex step flex-1">
                <img class="mr-2" src="@/assets/image/order/success.png" alt="" />
                {{ $t('customerSupport') }}
              </div>
              <div class="pr-2">
                <van-icon name="arrow" size="16" color="#9399A4" />
              </div>
            </div>
          </div>
        </div>
        <div class="px-4 py-4 flex quxiao" v-if="payInfo.state == 4">{{ $t('Cancelled') }} <img class="icon4" src="@/assets/image/Record/icon4.png" /></div>
        <div class="px-4 py-4" v-if="payInfo.state == 4">{{ $t('orderbeenCanceled') }}</div>
        <div class="height-line-1"></div>
        <div class="order-info">
          <div class="info-title pt-3 pb-3">{{ $t('purchasing') }} <span>USD</span></div>
          <div class="flex cell-item pt-3 pb-3">
            <div class="left-title">
              {{ $t('lumpSum') }}
            </div>
            <div class="money">
              {{ payInfo.currency }} {{ payInfo.amount }}
            </div>
          </div>
          <div class="flex cell-item pt-3 pb-3" v-if="payInfo.paramValue2">
            <div class="left-title">
              {{ $t('bankName') }}
            </div>
            <div class="white">
              {{ $t(payInfo.paramValue2) }}
            </div>
          </div>
          <div class="flex cell-item pt-3 pb-3" v-if="payInfo.paramValue1">
            <div class="left-title">
              {{ $t('accountAddress') }}
            </div>
            <div class="white">
              {{ $t(payInfo.paramValue1) }}
            </div>
          </div>
          <div class="flex cell-item pt-3 pb-3">
            <div class="left-title">
              {{ $t('orderNumber') }}
            </div>
            <div class="flex align-center" @click="copy">
              {{ payInfo.orderNo }}
              <img class="copy-img ml-1" src="@/assets/image/order/copy.png" alt="" />
            </div>
          </div>
        </div>
        <div class="height-line-1"></div>
        <div class="order-info">
          <div class="info-title pt-3 pb-3"><span>{{ $t('payType') }}</span></div>
          <div class="flex cell-item pt-3 pb-3">
            <div class="left-title payTitle">
              {{ $t('BankCard') }}
            </div>
          </div>
        </div>
        <div class="height-line-3"></div>
        <div class="flex but-wrap">
          <!-- <van-button class="w-full cancel-but" @click="cancelC2cOrderApply()">{{ $t('cancelOrder') }} </van-button> -->
          <van-button class="w-full submit-but" @click="onRoute" type="primary">{{ $t('toPay') }} </van-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import fxKline from '@/components/fx-kline/index.vue'
import { ref, onMounted, watch, onUnmounted } from "vue";
import { useRoute, useRouter } from 'vue-router';
import { getc2cOrder, c2cOrderApply } from "@/service/recharge.api.js";
import {
  showToast
} from 'vant';
import useClipboard from "vue-clipboard3";
import { useI18n } from "vue-i18n";
import dayjs from 'dayjs';
import duration from 'dayjs/plugin/duration';
dayjs.extend(duration);

const { toClipboard } = useClipboard();
const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const showPopup = ref(false)
const orderNo = ref(null)
const onRoute = () => {
  // router.push('/order/success')
  router.push('/customerService?order_no=' + orderNo.value)
}
const onClickLeft = () => {
  router.push('/exchange/channel-in')
}

const payInfo = ref({})
const isLoading = ref(true)
const timer = ref(null)
const remainingTime = ref('')

onMounted(async () => {
  console.log(route.query.orderNo)
  if (route.query.orderNo) {
    orderNo.value = route.query.orderNo
    getc2cOrderDetails(orderNo.value)
  }
  timer.value = setInterval(() => {
    if (route.query.orderNo) {
      orderNo.value = route.query.orderNo
      getc2cOrderDetails(orderNo.value)
    }
  }, 3000)

})

const clearInterFn = (() => {
  clearInterval(timer.value)
  timer.value = null
})

onUnmounted(() => {
  clearInterFn()
})

//获取订单详情
const getc2cOrderDetails = (orderNo) => {
  getc2cOrder({ order_no: orderNo }).then((res) => {
    payInfo.value = res
    isLoading.value = false
    if (payInfo.value.state == 0) {
      remainingTime.value = res.autoCancelTimeRemain
    } else if (payInfo.value.state == 3) { //已完成
      router.push('/order/success?payInfo=' + JSON.stringify(payInfo.value))
    } else if (payInfo.value.state == 4) { //已取消
      showToast(t('订单已取消'))
      router.push('/exchange/channel-in')
    }
  })

}
//取消订单
const cancelC2cOrderApply = () => {
  c2cOrderApply({ order_no: orderNo.value }).then(() => {
    showToast(t('CancelSuccess'))
  })
}
const copy = async () => {
  try {
    await toClipboard(payInfo.value.orderNo);
    showToast(t('copySuccess'));
  } catch (e) {
    console.error(e);
  }
}
</script>
<style lang="scss" scoped>
.loading-box {
  position: absolute;
  top: 30%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.order-submit {
  color: $text_color;
  font-size: 15px;

  .order-top {
    padding: 20px 5px 20px 5px;
  }

  .title {
    font-size: 24px;
    color: $text_color;
  }

  .text {
    font-size: 12px;

    span {
      color: $active_line;
    }
  }

  .customer {
    background: $main2_background;
    border-radius: 10.0022px;
    padding: 20px 0 20px 20px;
    color: $text_color;

    .service {
      align-items: center;

      .serve {
        width: 19px;

      }

      .gold {
        width: 13px;
      }

      img {
        display: block;
      }
    }

    .support-box {
      position: relative;
      padding: 4px 8px 4px 16px;
      height: 32px;
      background-color: $tab_background;
      color: $text_color;
      font-size: 14px;

      &::before {
        content: '';
        position: absolute;
        left: -15px;
        top: 0;
        width: 16px;
        height: 32px;
        border-radius: 16px 0 0 16px;
        line-height: 32px;
        background-color: $tab_background;
      }

      .contact-icon {
        position: absolute;
        left: -6px;
        top: 6px;
        display: inline-block;
        width: 20px;
        height: 20px;
        margin-right: 6px;
      }
    }

    .step {
      align-items: center;

      img {
        width: 17px;
      }
    }
  }

  .order-info {
    padding: 20px 15px 20px 15px;

    .info-title {
      color: $green;
      font-size: 16px;

      span {
        color: $text_color;
      }
    }

    .cell-item {
      justify-content: space-between;

      .left-title {
        color: #8A919E;
      }

      .payTitle {
        padding-left: 10px;
        position: relative;

        &::after {
          position: absolute;
          left: 0;
          top: 6px;
          height: 14px;
          width: 3px;
          background: #E7BB41;
          content: '';
        }
      }

      .money {
        font-weight: bold;
        font-size: 18px;
        color: $text_color;
      }

      .copy-img {
        width: 13px;
      }

      .align-center {
        align-items: center;
      }

      .white {
        color: $text_color;
      }
    }
  }

  .height-line-1 {
    height: 10px;
    background: $input_background;
  }

  .height-line-3 {
    height: 4px;
    background: $selectSymbol_background;
  }


}

.but-wrap {
  padding: 40px 15px 50px 15px;
  justify-content: center;

  .submit-but {
    width: 240px;
    display: block;
    margin-left: 10px;
  }

  .cancel-but {
    width: 100px;
  }
}

.quxiao {
  align-items: center;
  color: $text_color;
}

.icon4 {
  width: 25px;
  height: 25px;
  margin-left: 10px;
}

:deep(.van-icon) {
  font-size: 18px;
  color: $text_color;
}
</style>
