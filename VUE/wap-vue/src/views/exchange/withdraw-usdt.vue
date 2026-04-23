<template>
  <div class="charge-bank pb-20">
    <van-sticky>
      <fx-header>
        <template #title>
          USDT {{ $t('withdraw') }}
        </template>
        <template #right>
          <van-icon @click="openRecord" name="orders-o"> </van-icon>
        </template>
      </fx-header>
    </van-sticky>

    <div class="pt-6 px-8">
      <h1 class="title">{{ $t('WithdrawUSDT') }}</h1>
      <h4 class=" ahs pt-2 pb-2">{{ $t('WithdrawUSDTAddress') }}</h4>
      <van-form>
        <van-cell-group inset>
          <p class="pt-6 pb-2 usdt-title">{{ $t('FrenchCurrency') }}</p>
          <van-field class="select-item" readonly v-model="coin" name="pattern">
          </van-field>
        </van-cell-group>
        <van-cell-group inset>
          <p class="pt-6 pb-2">{{ $t('network') }}</p>
          <div class="tab-wrap flex">
            <div class="tab-item mr-4" v-for="(item, index) in usdtList" :key="index"
              :class="[selectIndex == index ? 'active' : '']" @click="changeIndex(index)">{{
                item.blockchain_name
              }}</div>
          </div>
        </van-cell-group>

        <van-cell-group inset>
          <p class="pt-6 pb-2  usdt-title">{{ $t('Address') }}</p>
          <van-field class="select-item" v-model="address" name="picker">
            <template #extra>
              <span class="all" @click="pastCont">{{ $t('paste') }}</span>
              <!-- <img class="ml-2 sweep-img"
                                src="@/assets/image/order/sweep.png" /> -->
            </template>
          </van-field>
        </van-cell-group>
        <van-cell-group inset>
          <p class="pt-6 pb-2  usdt-title">{{ $t('number') }}</p>
          <van-field class="select-item" v-model="amount" type="number" @input="debounceInput" name="picker"
            :placeholder="$t('enterwithdrawalAmount')">
            <template #extra>
              <span class="currency-title">USD</span>
              <span class="ml-2 all" @click="allFun">{{ $t('all') }}</span>
            </template>
          </van-field>
        </van-cell-group>
      </van-form>
      <div class="flex  pt-4 pb-8 justify-between ">
        <div class="ahs  usdt-title">
          {{ $t('available') }}
        </div>
        <div class="money">
          {{ money_wallet }} USD
        </div>
      </div>
      <div class="mx-4 pb-10 pt-10 quantity-wrap">
        <div class="flex justify-between">
          <div class="ahs text-24">
            {{ $t('AvailableaccountAmount') }}
          </div>
          <div class="money  usdt-title">
            {{ volume_last || '0.00' }}
          </div>
        </div>
        <div class="flex justify-between  pt-10">
          <div class="ahs  usdt-title text-24">
            {{ $t('WithdrawalFee') }}
          </div>
          <div class="money  usdt-title">
            {{ fee || '0.00' }} USD
          </div>
        </div>
      </div>
    </div>
    <div class="px-8 mt-4 centent">
      <h2>{{ $t('WithdrawalInstructions') }}</h2>
      <p class="mt-2 text-xs">{{ $t('WithdrawTime') }}</p>
      <p class="mt-2 text-xs">{{ $t('WithdrawalTitle1') }}</p>
      <p class="mt-2 text-xs">{{ $t('WithdrawalTitle2') }}</p>
      <p class="mt-2 text-xs">{{ $t('WithdrawalTitle3') }}</p>
    </div>
    <div class="mt-6 px-30">
      <van-button class="w-full" type="primary" @click="submit">{{ $t('submit') }} </van-button>
    </div>
    <fx-popup v-model:show="showPopup" @close="closeOrder" :blockchain_name="usdtObj.blockchain_name" :address="address"
      :amount="amount" :fee="fee" :volume_last="volume_last" :session_token="session_token" :coin=coin> </fx-popup>
  </div>
</template>

<script setup>
import fxPopup from '@/components/fx-popup/confirm-order-crypto.vue'
import { debounce } from '@/utils/index'
import { _getBlock } from "@/service/recharge.api";
import { _widtGetSessionToken, _withdrawFee } from "@/service/withdraw.api";
import { _getAllAssets } from "@/service/user.api.js";
import { showToast } from "vant";
import { ref, onMounted } from 'vue';
import { useRouter } from "vue-router";
import { setStorage } from '@/utils/index'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
const usdtList = ref([])
const usdtObj = ref({})
const selectIndex = ref(0)
const amount = ref('')
const router = useRouter()
const showPopup = ref(false)
let address = ref('')
const coin = ref('USDT')
const session_token = ref('')
const money_wallet = ref('')
const volume_last = ref('')
const fee = ref(0)

onMounted(() => {
  initData()
})
const initData = () => {
  getBlock()
  widtGetSessionToken()
  getAllAssets()
}
const getBlock = () => {
  _getBlock({
    coin: coin.value
  }).then(res => {
    usdtList.value = res
    usdtObj.value = res[0]
  })
}
const widtGetSessionToken = () => {
  _widtGetSessionToken().then((res) => {
    session_token.value = res.session_token;
  });
}
const getAllAssets = () => {
  _getAllAssets()
    .then(res => {
      money_wallet.value = res.money_wallet
    })
}
const pastCont = async () => {
  address.value = await navigator.clipboard.readText();
}
const changeIndex = (index) => {
  selectIndex.value = index
  usdtObj.value = usdtList.value[index]
}
const debounceInput = debounce(function () {
  changeInput()
}, 500)
const changeInput = () => {
  if (amount.value === '') {
    volume_last.value = '0.00'
    fee.value = ''
    return
  }
  _withdrawFee({
    amount: amount.value,
    channel: usdtObj.value.coin + '_' + usdtObj.value.blockchain_name
  }).then((res) => {
    volume_last.value = res.volume_last;
    fee.value = res.fee;
  });
}

const submit = () => {
  if (address.value == '') {
    showToast(t('enterAddress'))
    return
  }
  if (amount.value == '') {
    showToast(t('enterTheWithdrawalAmount'))
    return
  }
  showPopup.value = true
}
const openRecord = () => {
  setStorage('recordId', 4)
  router.push('/Record/DepositAndWithdrawal')
}
const closeOrder = () => {
  showPopup.value = false
}
//全部
const allFun = (() => {
  amount.value = money_wallet.value
})
</script>
<style lang="scss" scoped>
.charge-bank {
  font-size: 14px;
}

.select-item {
  background: $input_background;
  // padding: 0 15px;
  align-items: center;
  height: 50px;
  border-radius: 3px;
}

.title {
  font-size: 26px;
  color: $text_color;
  font-weight: bold;
}

.usd-input {
  height: 50px;
  background: $light-grey;
  font-size: 16px;
}

.centent {
  h2 {
    color: $text_color;
    font-size: 16px;
  }

  p {
    color: $text_color1;
    ;
    line-height: 22px;
  }
}

.ahs {
  color: $text_color1;
  ;
}

.all {
  color: $active_line;
}

.money {
  font-size: 20px;
  font-weight: bold;
}

.quantity-wrap {
  border-top: 1px solid $border_color;
  border-bottom: 1px solid $border_color;
}

.tab-wrap {
  .tab-item {
    width: 118px;
    height: 50px;
    border: 1px solid #D4D7DB;
    border-radius: 4px;
    text-align: center;
    line-height: 50px;
  }

  .active {
    background: url('@/assets/image/order/active-bg.png') no-repeat center;
    background-size: 100% 90%;
    border: none !important;
  }
}

.sweep-img {
  width: 19px;
  height: 19px;
}

:deep(.intl-tel-input.allow-dropdown .flag-container:hover .selected-flag) {
  background: transparent !important;
}

:deep(.van-cell-group) {
  background: transparent !important;
}

:deep(.van-field__control) {
  color: $text_color !important;
}

.currency-title {
  color: $text_color !important;
}

.text-xs {
  font-size: 14px;
}

:deep(.van-cell-group--inset) {
  margin: 0;
}
</style>