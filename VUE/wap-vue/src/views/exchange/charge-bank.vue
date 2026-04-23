<template>
  <div class="charge-bank pb-20">
    <fx-header>
      <template #title>
        {{ $t('BankCarddeposit') }}
      </template>
      <template #right>
        <van-icon @click="openRecord" name="orders-o"></van-icon>
      </template>
    </fx-header>
    <div class="px-6">
      <div class="pt-6">
        <h1 class="px-4 title"> {{ $t('BankCarddeposit') }}</h1>
        <van-action-sheet v-model:show="show" :actions="actions" @select="onSelect"></van-action-sheet>
        <ul class="flex flex-col" >
          <li class="flex flex-col px-4 mt-6" v-if="false">
            <p>{{ $t('nation') }}</p>
            <div class="select-item flex">
              <div class="flex-1">
                <fx-country-list @excountry="excountry"></fx-country-list>
              </div>
            </div>

          </li>
          <li class="flex flex-col px-4 mt-6" @click="showActions()" v-if="false">
            <p>{{ $t('FrenchCurrency') }}</p>
            <div class="select-item flex">
              <div class="flex-1 text-base">
                <span v-if="fiatValue">{{ fiatValue }}</span>
                <span v-else style="color: #C0C4CC;font-size: 14px;">{{ $t('selectFrenchCurrency') }}</span>
              </div>
              <div>
                <van-icon name="arrow-down" color="#878A96" size="16" />
              </div>
            </div>

          </li>

          <li class="flex flex-col px-4 mt-6">
            <p>BANK NAME：</p>
            <input placeholder="" value="UJJIVAN SMALL FINANCE BANK LIMITED" class="mt-2 usd-input pl-3"/>
          </li>

          <li class="flex flex-col px-4 mt-6">
            <p>BRANCH : </p>
            <input placeholder="" value="KENGERI" class="mt-2 usd-input pl-3"/>
          </li>

          <li class="flex flex-col px-4 mt-6">
            <p>HOLD NAME： </p>
            <input placeholder="" value="MALLESHWARAM" class="mt-2 usd-input pl-3"/>
          </li>

          <li class="flex flex-col px-4 mt-6">
            <p>ACC：</p>
            <input placeholder="" value="1733120040000004" class="mt-2 usd-input pl-3"/>
          </li>

          <li class="flex flex-col px-4 mt-6">
            <p>IFSC :</p>
            <input placeholder="" value="UJVN0001733" class="mt-2 usd-input pl-3"/>
          </li>


          <li class="flex flex-col px-4 mt-6">
            <p>TYPE : </p>
            <input placeholder="" VALUE="CURRENT" class="mt-2 usd-input pl-3"/>
          </li>


          <!-- <li class="flex flex-col px-4 mt-6"> -->
            <!-- <p>{{ $t('payAccount') }}</p> -->
            <!-- <input :placeholder="$t('enterBankcardnumber') " class="mt-2 usd-input pl-3" type="number" v-model="bankId" /> -->
          <!-- </li> -->


          <li class="flex flex-col px-4 mt-6">
            <p>{{ $t('RechargeAmount') }}</p>
            <input :placeholder="$t('RechargeRange') + '10-999999'" class="mt-2 usd-input pl-3" type="number"
              v-model="amount" />
          </li>
        </ul>
      </div>
<!--@todo ee-->
      <div class="px-4 mt-4 centent" v-if="false">
        <h2>{{ $t('RechargeInstructions') }}</h2>
        <p class="mt-2 text-xs">{{ $t('desc1') }}</p>
        <p class="mt-2 text-xs">{{ $t('desc2') }}</p>
        <p class="mt-2 text-xs">{{ $t('desc3') }}</p>
        <p class="mt-2 text-xs">{{ $t('desc4') }}</p>
        <p class="mt-2 text-xs">{{ $t('desc5') }}</p>
      </div>
      <div class="px-4 mt-6">
        <van-button class="w-full" type="primary" @click="submit">{{ $t('submit') }}</van-button>
      </div>
    </div>
    <fx-popup v-model:show="showPopup" @close="closeOrder" :payInfo="payInfo" :bankId="bankId" :bankType="'recharge'"
      :fiatValue="fiatValue"></fx-popup>
  </div>
</template>

<script setup>
import fxCountryList from '@/components/fx-country-list/index.vue'
import fxPopup from '@/components/fx-popup/confirm-order.vue'
import { ref, watch, onBeforeMount } from 'vue';
import { useRouter } from "vue-router";
import { _getCurrencyList, _getPaymentMethod } from "@/service/trade.api.js";
import { _getSessionToken } from "@/service/recharge.api";
import { useLanguageStore } from "@/store/language.store.js";
import { useUserStore } from "@/store/user.js";
import { _uploadImage } from '@/service/upload.api.js'
import { useI18n } from "vue-i18n";
import { showToast, showDialog } from 'vant';
import { setStorage } from '@/utils/index'
import { _bankPaymentMethodList } from '@/service/user.api.js'
const { t } = useI18n()

const languageStore = useLanguageStore();
const useStore = useUserStore();
const actions = ref([])
const show = ref(false)

const fiatValue = ref('')
const rate = ref(1)

const amount = ref('')
const router = useRouter()
const showPopup = ref(false)

const frontFile = ref([])
const bankId = ref(null)


const currencies = ref([]);
const payInfo = ref({})
onBeforeMount(() => {
  c2cPaymentMethodList()
  // 获取法币列表
  _getCurrencyList().then(res => {
    currencies.value = res.map(item => {
      return {
        name: item.currency,
        rate: item.rate
      }
    });
    actions.value = currencies.value
  })

  // _getPaymentMethod({ token: useStore.userInfo.token, language: 'en' }).then(res => {
  //   console.log(res);
  // })
  _getSessionToken({}).then(res => {
    console.log(res)
    payInfo.value.payment_method_id = res.gf_payment_method_id
    payInfo.value.session_token = res.session_token
  })
})

//获取提现列表
const c2cPaymentMethodList = () => {
  _bankPaymentMethodList().then((res) => {
    // payList.value = res
    // console.log(payList.value)
    // actions1.value = payList.value


    if (res.length === 0) {
      showDialog({
        title: t('tip'),
        message: t('Bindbankcard'),
        confirmButtonText: t('confirm')
      }).then(() => {
        // on close
        router.push('/payMentMethod/list')
      });
    }
  })
}

const openRecord = () => {
  setStorage('recordId', 1)
  router.push('/Record/DepositAndWithdrawal')
}

// 文件上传
const afterRead = (file) => {
  file.status = 'uploading';
  file.message = t('uploading')

  _uploadImage(file).then(data => {
    file.status = 'success';
    file.message = t('uploadSuccess');
    payInfo.value.img = data
    frontFile.value = [file]
  }).catch(err => {
    file.status = 'failed';
    file.message = t('uploadFailed');
  })
};


// 显示弹框
const showActions = () => {
  show.value = true
}

const onSelect = (value) => {
  fiatValue.value = value.name
  rate.value = value.rate
  show.value = false
}

const submit = () => {
  let index = currencies.value.findIndex(i => i.name === "INR");
  if (index > -1){
    fiatValue.value = currencies.value[index].name
    rate.value = currencies.value[index].rate
  }
  // if (!fiatValue.value) {
  //   showToast(t('selectFrenchCurrency'))
  //   return
  // }
  if (!amount.value) {
    showToast(t('enterRechargeAmount'))
    return
  }


  let numReg = /^[0-9]+([.]{1}[0-9]+){0,1}$/;
  if (!numReg.test(amount.value)) {
    showToast(t('amountNumber'));
    return;
  }
  payInfo.value.nationality = 'India (भारत)'
  payInfo.value.direction = 'recharge'
  payInfo.value.currency = "INR"
  payInfo.value.rate = rate.value
  payInfo.value.coin_amount = Math.floor((amount.value / rate.value) * 100) / 100
  payInfo.value.fa_amount = amount.value
  console.log(payInfo.value)
  showPopup.value = true
}
const closeOrder = () => {
  showPopup.value = false
}
const excountry = (item) => {
  payInfo.value.nationality = item.name
}

</script>
<style lang="scss" scoped>
.charge-bank {
  font-size: 15px;
}

.select-item {
  background: $input_background;
  padding: 0 15px;
  align-items: center;
  height: 50px;
  border-radius: 3px;
  margin-top: 10px;
  color: $text_color;
}

.title {
  font-size: 26px;
  color: $text_color;
  font-weight: bold;
}

.usd-input {
  height: 50px;
  background: $input_background;
  font-size: 14px;
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

.tips {
  color: $text_color1;
  ;
  padding: 10px 0;
  font-size: 14px;
}

:deep(.intl-tel-input.allow-dropdown .flag-container:hover .selected-flag) {
  background: transparent !important;
}

:deep(.van-action-sheet) {
  overflow: auto;
}

:deep(.van-icon-orders-o) {
  color: $text_color;
}

.text-xs {
  font-size: 14px;
}
</style>