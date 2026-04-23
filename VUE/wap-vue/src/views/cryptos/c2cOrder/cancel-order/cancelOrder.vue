<template>
  <div id="cancelOrderPage">
    <div class="w-full h-full">
      <div class="w-full h-full">
        <order-nav>
          <template #title>
            {{ $t('取消订单') }}
          </template>
        </order-nav>
        <div class="px-8 mt-8">
          <div class="flex rounded-xl box-border px-6 pt-6 pb-8 tabBackground">
            <img class="w-8 h-8 mr-4" src="~@/assets/image/c2c/Group41.png" alt="">
            <div class="text-26 c2cColor">
              <p class="text-28">{{ $t('温馨提示') }}</p>
              <p class="my-55">{{ $t('1. 如果您已经向卖家付款，请千万不要取消订单。') }}</p>
              <p>2. {{ $t('累计3笔取消，当日不可再购买。') }}</p>
            </div>
          </div>
          <div class="mt-11">
            <h2 class="text-30 font-normal c2cColor">{{ $t('请告诉我们您为什么要取消订单？') }}</h2>
            <van-radio-group v-model="radio">
              <van-radio class="mt-4" v-for="(item, index) in radioItems" :key="index" :name="item.title">
                <span>{{ item.title }}</span>
                <template #icon="props">
                  <img class="img-icon" :src="props.checked ? activeIcon : inactiveIcon" />
                </template>
              </van-radio>
              <div class="flex items-center h-20 mt-8 tabBackground">
                <van-radio name="其他">
                  <span>{{ $t('其他') }}</span>
                  <template #icon="props">
                    <img class="img-icon" :src="props.checked ? activeIcon : inactiveIcon" />
                  </template>
                </van-radio>
              </div>
            </van-radio-group>
            <div class="textarea-wrapper relative mt-8 tabBackground">
              <textarea @input="changeVal" class="rounded-xl textarea-text" :placeholder="$t('请输入取消理由')" maxlength="160"
                v-model="other"></textarea>
              <span class="absolute bottom-30 right-22 text-26 text-grey">{{ inputNum }}/160</span>
            </div>
          </div>
          <div class="w-full mt-16 pb-20">
            <van-button :disabled="isDisabled" class="w-full rounded-xl" color="#1D91FF" type="info"
              @click="enterCancelOrder">{{ $t('确认取消订单') }}
            </van-button>
          </div>
        </div>
      </div>
      <!-- 取消订单成功 -->
      <van-popup class="w-full h-full" position="right" v-model:show="show">
        <cancel-success :detail="detail" />
      </van-popup>
    </div>
  </div>
</template>

<script>
import { Button, Popup, Radio, RadioGroup, showToast } from 'vant';
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import CancelSuccess from "@/views/cryptos/c2cOrder/components/order-generation/CancelSuccess.vue";
import otcApi from '@/service/otc.api'

import {
  mapMutations
} from "vuex";

import {
  REASON_FOR_CANCELLATION
} from "@/store/const.store";

export default {
  name: "cancelOrder",
  data() {
    return {
      show: false, // 是否显示取消成功页面
      radio: this.$t('我不想交易了'),
      activeIcon: new URL('@/assets/image/c2c/Group1206.png', import.meta.url),
      inactiveIcon: new URL('@/assets/image/c2c/Ellipse112.png', import.meta.url),
      other: '', // 其他
      detail: {}, //取消后详情
      inputNum: 0,
      radioItems: [
        {
          title: this.$t('我不想交易了'),
        },
        {
          title: this.$t('我不满足广告交易条款的要求'),
        },
        {
          title: this.$t('卖家要额外收取费用'),
        },
        {
          title: this.$t('卖家收款方式右问题，无法成功打款'),
        },
      ]
    }
  },
  methods: {
    ...mapMutations('c2c', [REASON_FOR_CANCELLATION]),
    changeVal(e) {
      this.inputNum = e.target.value.length
    },
    enterCancelOrder() {
      let cancelText;
      if (this.radio === '其他') {
        cancelText = this.other;
      } else {
        cancelText = this.radio;
      }

      this[REASON_FOR_CANCELLATION](cancelText);

      const remark = this.other || this.radio
      const order_no = this.$store.state.c2c.order_no
      console.log(order_no, remark)
      // 取消订单
      otcApi.ctcOrderCancel({ order_no, remark }).then(async data => {
        const res = await otcApi.ctcOrderGetDetail({ order_no, language: this.$i18n.locale });
        this.detail = res
        this.show = true
        showToast(this.$t('取消成功'))

      })
    }
  },
  computed: {
    // ...mapState('c2cBuy', ['count', 'totalPrice', 'createOrderTime', 'orderNumber'])
    isDisabled() {
      if (this.radio === '其他') {
        return this.inputNum === 0
      } else {
        return false
      }
    }
  },
  created() {
    // console.log(this.count, this.totalPrice, this.createOrderTime, this.orderNumber)
    // const { detail } = this.$route.query
    // this.detail = JSON.parse(detail)
  },
  components: {
    [RadioGroup.name]: RadioGroup,
    [Radio.name]: Radio,
    [Button.name]: Button,
    [Popup.name]: Popup,
    OrderNav,
    CancelSuccess,
  }
}
</script>

<style lang="scss" scoped>
#cancelOrderPage {
  font-size: 30px;

  .img-icon {
    width: 32px;
    height: 32px;
  }

  .textarea-wrapper {
    textarea {
      width: 100%;
      height: 304px;
      padding: 30px 22px;
      box-sizing: border-box;
      background: transparent;

      &::placeholder {
        color: #B8BCC5;
      }
    }
  }

  .w-full {
    :deep(.order-data) {
      .title {
        color: #5EBA89;
      }
    }

    :deep(.van-radio) {
      align-items: flex-start;
      padding: 8px 0;
    }

    :deep(.van-radio__label) {
      color: $text_color1;
    }

    :deep(.van-button--info) {
      background: $btn_main;
      border-color: $btn_main;
      color: $main-btn-color;
    }

    .textarea-text {
      color: $text_color;
    }
  }
}
</style>
