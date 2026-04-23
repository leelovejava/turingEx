<template>
  <van-popup round :value="props.showPopup" @input="(val) => this.$emit('input', val)" :close-on-click-overlay="false"
    position="bottom">
    <div class="relative px-15 pt-23 px-4 pb-10">
      <div class="flex justify-end py-4 items-center">
        <img @click="onClose" class="z-20 w-8 h-8" src="./close.png" alt="" />
      </div>
      <h4 class="text-20 font-medium text-black title">
        {{ $t('confirmOrder') }}
      </h4>
      <div class="flex flex-col justify-center pay-info textColor">
        <p class="text-center mt-4 textColor">{{ $t('Actual') }}</p>
        <h2 class="font-bold text-base text-center mt-2 mb-2 textColor">
          {{ bankType == 'recharge' ? pay.coin_amount : volume_last }}
          {{ bankType == 'recharge' ? 'USDT' : fiatValue }}
        </h2>
        <!-- <section>
                    <ul class="mt-2 mb-4 border-b-1">
                        <li class="flex justify-between pt-1 pb-1">
                            <span class="ash">{{ $t('bankName') }}</span>
                            <span>{{ $t('BankAmerica') }}</span>
                        </li>
                        <li class="flex justify-between pt-1 pb-1">
                            <span class="ash">{{ $t('payAccount') }}</span>
                            <span>{{ $t('foreignAccount') }}</span>
                        </li>
                    </ul>
                </section> -->
        <section>
          <ul class="mt-2 mb-4 border-b-1">
            <li class="flex justify-between pt-1 pb-1 textColor1">
              <span class="ash">{{ $t('currencyOne') }}</span>
              <span>{{
                bankType == 'recharge' ? pay.currency : fiatValue
              }}</span>
            </li>
            <li class="flex justify-between pt-1 pb-1 textColor1">
              <span class="ash">{{ $t('amount') }}</span>
              <span>{{ pay.fa_amount }}</span>
            </li>
            <li class="flex justify-between pt-1 pb-1 textColor1">
              <span class="ash">{{ $t('free') }}</span>
              <span>{{ fee || '--' }}</span>
            </li>
          </ul>
        </section>
        <van-button type="primary" @click="onConfirm">{{
          $t('confirmOrder')
        }}</van-button>
      </div>
    </div>
  </van-popup>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref, watch, onBeforeMount } from 'vue'
import { useI18n } from 'vue-i18n'
import { c2cOrder } from '@/service/recharge.api'
const { t } = useI18n()
const router = useRouter()
const props = defineProps({
  rate: {
    type: Number,
    default: 1
  },
  fiatValue: {
    type: String,
    default: 'USD'
  },
  showPopup: {
    type: Boolean,
    default: false
  },
  bankId: {
    type: String,
    default: ''
  },
  payInfo: {
    type: Object,
    default: {}
  },
  bankType: {
    type: String,
    default: 'Withdraw'
  },
  fee: {
    default: ''
  },
  volume_last: {
    default: 0
  }
})
let pay = ref({})

const emits = defineEmits(['close'])

const onClose = () => {
  emits('close')
}

const onConfirm = () => {
  const { direction } = props.payInfo
  onClose()
  // 银行卡充值无需输入密码，直接走接口
  if (direction === 'recharge') {
    const params = { ...props.payInfo, safeword: '' }
    c2cOrder(params).then((res) => {
      if (props.bankType == 'recharge') {
        router.push('/order/submit?orderNo=' + res.order_no)
      } else {
        router.push('/order/apply-success?currentType=withdraw&type=bank')
      }
    })
  } else {
    router.push(
      '/exchange/fund-password-verify?type=bank&payInfo=' +
      JSON.stringify(props.payInfo) +
      '&bankType=' +
      props.bankType
    )
  }
}
onBeforeMount(() => {
  pay.value = props.payInfo
  console.log(props.payInfo, 'props.payInfo')
})
</script>
<style lang="scss" scoped>
.pay-info {
  color: $black;
  font-size: 14px;
}

.title {
  text-align: center;
  color: #1f2025;
  font-weight: bold;
  font-size: 20px;
}

.ash {
  color: $text_color1;
}

.money-title {
  font-size: 24px;
  font-weight: bold;
}
</style>