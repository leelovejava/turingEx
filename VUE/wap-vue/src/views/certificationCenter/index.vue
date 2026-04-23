<template>
  <div class="certificationCenter">
    <fx-header @back="loginOut">
      <template #title>
        {{ $t('certificationCenter') }}
      </template>
    </fx-header>
    <div class="box">
      <div class="justify-between1 flex user-info">
        <div>{{ $t('personalCenter') }}</div>
        <div v-if="nationality" class="iti-flag" :class="nationality" style="transform: scale(2.1)"></div>
      </div>
      <div class="flex">
        <div class="gn flex">
          <img src="../../assets/image/idImg/cftIcon.png" alt="" class="w-64 h-64">
          <span class="gn-span" @click="showClick">{{ $t('viewCurrentFeatures') }}</span>
        </div>
      </div>
    </div>
    <div class="contentBox pt-5 pb-5">
      <div class="btnBox2">
        <div class="textColor4 textColor5 flex" style="justify-content: center;align-items: center;"
          :class="{ 'activeStyle': active == 0 }" @click="changeTab(0)">
          <img v-if="identityverif" src="../../assets/image/idImg/finishIcon.png" alt="" class="w-10 h-10 mr-2" />
          <img v-else src="../../assets/image/idImg/finishIcon1.png" alt="" class="w-10 h-10 mr-2" />
          <span>{{ $t('primaryCertification') }}</span>
        </div>
        <div class="textColor4 flex" style="justify-content: center;align-items: center;"
          :class="{ 'activeStyle': active == 1 }" @click="changeTab(1)">
          <img v-if="identityverif" src="../../assets/image/idImg/finishIcon.png" alt="" class="w-10 h-10 mr-2" />
          <img v-else src="../../assets/image/idImg/finishIcon1.png" alt="" class="w-10 h-10 mr-2" />
          <span>{{ $t('advancedCertification') }}</span>
        </div>
      </div>
      <div v-show="active == 0">
        <div class="justify-between23">
          <div class="textColor1">{{ $t('require') }}</div>
          <div class="border-radius-left textColor2 yaoqu flex"
            :class="{ 'status0': kyc_status == 0 || kyc_status == 3, 'status1': kyc_status == 1, 'status2': kyc_status == 2 }">
            <img :src="handImg(`status${kyc_status}`)" alt="" />
            <span class="textColor-span flex items-center">{{
              fixState(kyc_status)
            }}</span>
          </div>
        </div>
        <div class="px-32-1">
          <div class="flex items-center textColor span2">
            <img src="../../assets/image/idImg/info.png" alt="" />
            <span class="textColor textColor666">{{ $t('information') }}</span>
          </div>
          <div class="flex items-center textColor span2">
            <img src="../../assets/image/idImg/identity.png" alt="" />
            <span class="textColor textColor666">{{ $t('governmentIssuedID') }}</span>
          </div>
        </div>
        <div class="px-32-1 px-32-2">
          <div class="textColor span32">{{ $t('featuresAndLimitations') }}</div>
          <!-- <content-com class="mt-40" :contentObj="coinObj" :state="identityverif"></content-com> -->
          <!-- <content-com class="mt-40" :contentObj="c2cObj" :state="identityverif"></content-com> -->
          <!-- <content-com class="mt-40" :contentObj="currencyObj" :state="identityverif"></content-com> -->
          <div class="flex text-grey text-24 mt-64">
            <img src="../../assets/image/idImg/suditIcon.png" alt="" />
            <span class="ml-24">{{ $t('reviewTime') }}:{{ $t('thereDays') }}</span>
          </div>
          <div class=" text-red text-20">
            <p v-show="(kyc_status == 3)">{{ $t('certificationRefusal') }}:{{ turnDownMsg }}</p>
          </div>
          <div class="rounded-lg text-center btn"
            :class="kyc_status == 0 || kyc_status == 3 ? 'btnMain text-white' : 'bgDark text-grey'" @click="openUlr(1)">{{
              fixBtnState(kyc_status) }}</div>
        </div>

      </div>
      <div v-show="active == 1">
        <div class="justify-between23">
          <div class="textColor1">{{ $t('require') }}</div>
          <div class="border-radius-left textColor2 flex yaoqu"
            :class="{ 'status0': advStatus == 0 || advStatus == 3, 'status1': advStatus == 1, 'status2': advStatus == 2 }">
            <img :src="handImg(`status${advStatus}`)" alt="" />
            <span class="textColor-span flex items-center">{{ fixState(advStatus) }}</span>
          </div>
        </div>
        <div class="px-32-1">
          <div class="flex items-center textColor span2">
            <img src="../../assets/image/idImg/address.png" alt="" />
            <span class="textColor textColor666">{{ $t('familyAddress') }}</span>
          </div>
          <div class="flex items-center textColor span2">
            <img src="../../assets/image/idImg/address.png" alt="" />
            <span class="textColor textColor666">{{ $t('workAddress') }}</span>
          </div>
          <div class="flex items-center textColor span2">
            <img src="../../assets/image/idImg/connect.png" alt="" />
            <span class=" textColor textColor666">{{ $t('contactRelatives') }}</span>
          </div>
        </div>
        <div class="px-32-1 px-32-2">
          <div class="textColor">{{ $t('featuresAndLimitations') }}</div>
          <!-- <content-com class="mt-40" :contentObj="advCoinObj" :state="advancedverif"></content-com> -->
          <!-- <content-com class="mt-40" :contentObj="advC2cObj" :state="advancedverif"></content-com> -->
          <!-- <content-com class="mt-40" :contentObj="advCurrencyObj" :state="advancedverif"></content-com> -->
          <div class="flex  text-grey text-24 mt-64">
            <img class="img3" src="../../assets/image/idImg/suditIcon.png" alt="" />
            <span class="ml-24 fs">{{ $t('notaryTime') }}</span>
          </div>
          <div class="btnMain btnMain1 rounded-lg py-26 text-center mt-16"
            :class="advStatus == 0 || advStatus == 3 ? 'btnMain text-black' : 'bgDark'" @click="openUlr(2)">
            {{ fixBtnState(advStatus) }}
          </div>

        </div>
      </div>
    </div>
    <van-popup round v-model:show="show" position="bottom" :close-on-click-overlay="false" @close="onClose">
      <div class="pop-box relative mainBackground">
        <img :src="handImg('icon-close')" class=" absolute h-10 w-10  top-46" @click="onClose" />
        <div class="pt-46 textColor text-40 textColor9">{{ $t('hasTheFunction') }}</div>
        <div class=" border-b-color" v-for="(item, index) in currentList" :key="index">
          <div class="text-32 text-grey flex items-center">
            <img :src="handImg(item.icon)" class="tk-img" alt="" />
            <span class="textColor mr-10">{{ item.title }}</span>
            <span>{{ item.des }}</span>
          </div>
          <div class="text-grey text-grey1 text-24 " v-for="(str, index) in item.arr" :class="{ 'colorMain': index == 1 }"
            :key="index">{{ str }}</div>
        </div>
        <div class="btnMain btnMain1 text-white text-34 py-26 rounded-lg text-center mt-68" @click="onClose">{{
          $t('confirm')
        }}</div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { _getIdentify, _info } from "@/service/user.api.js";
import { reactive, onMounted, ref } from 'vue';
import contentCom from "./contentCom.vue";
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const turnDownMsg = ref('')
const show = ref(false)
const active = ref(0)
const kyc_status = ref(0)
const advStatus = ref(0)
const identityverif = ref(false)
const advancedverif = ref(false)
const nationality = ref('')
const coinObj = ref({
  title: t('fiatCurrencyLimit'),
  list: [
    {
      left: t('recharge'),
      right: t('unlimited')
    },
    {
      left: t('withdraw'),
      right: t('unlimited')
    }
  ]
})
const c2cObj = ref({
  title: t('c2cTradeLimit'),
  list: [
    {
      left: 'C2C',
      right: t('unlimited')
    },
  ]
})
const currencyObj = ref({
  title: t('cryptoCurrencyLimit'),
  list: [
    {
      left: t('recharge'),
      right: t('unlimited')
    },
    {
      left: t('withdraw'),
      right: t('unlimited')
    }
  ]
})
const advCoinObj = ref({
  title: t('fiatCurrencyLimit'),
  list: [
    {
      left: t('recharge'),
      right: t('unlimited')
    },
    {
      left: t('withdraw'),
      right: t('unlimited')
    }
  ]
})
const advC2cObj = ref({
  title: t('c2cTradeLimit'),
  list: [
    {
      left: 'C2C',
      right: t('unlimited')
    },
  ]
})

const advCurrencyObj = ref({
  title: t('cryptoCurrencyLimit'),
  list: [
    {
      left: t('recharge'),
      right: t('unlimited')
    },
    {
      left: t('withdraw'),
      right: t('unlimited')
    }
  ]
})

const currentList = ref([])
const ownList = ref([
  {
    icon: '1',
    title: t('unlimited'),
    des: t('daily'),
    arr: [t('fiatDepositAndWithdrawalLimit')]
  },
  {
    icon: '2',
    title: t('unlimited'),
    des: '',
    arr: [t('digitalCurrencyRecharge'), t('increaseTheLimit')]
  },
  {
    icon: '3',
    title: t('unlimited'),
    des: t('daily'),
    arr: [t('digitalCurrencyWithdrawalLimit')]
  },
  {
    icon: '4',
    title: t('unlimited'),
    des: '',
    arr: [t('c2cTradeLimit')]
  },
  {
    icon: '5',
    title: 'OTC',
    des: '',
    arr: [t('otherFunctions')]
  }
])
const advOwnList = ref([
  {
    icon: '1',
    title: t('unlimited'),
    des: t('daily'),
    arr: [t('fiatDepositAndWithdrawalLimit')]
  },
  {
    icon: '2',
    title: t('unlimited'),
    des: '',
    arr: [t('digitalCurrencyRecharge'), t('increaseTheLimit')]
  },
  {
    icon: '3',
    title: t('unlimited'),
    des: t('daily'),
    arr: [t('digitalCurrencyWithdrawalLimit')]
  },
  {
    icon: '4',
    title: t('unlimited'),
    des: '',
    arr: [t('c2cTradeLimit')]
  },
  {
    icon: '5',
    title: 'OTC',
    des: '',
    arr: [t('otherFunctions')]
  }
])
onMounted(() => {
  currentList.value = ownList.value
  getLocaluserAction();
  getIdentify();
})
const handImg = (url) => {
  return new URL(`../../assets/image/idImg/${url}.png`, import.meta.url).href
}
const loginOut = () => {
  router.push('/my/index')
}
const changeTab = (index) => {
  active.value = index
  if (index == 0) {
    currentList.value = ownList.value
  } else {
    currentList.value = advOwnList.value
  }
  console.log(currentList);
  console.log(currentList.value);
}
const showClick = () => {
  show.value = true
  console.log(show.value);
}

const onClose = () => {
  show.value = false
}
const getLocaluserAction = () => {
  _info().then(res => {
    identityverif.value = res.identityverif
    advancedverif.value = res.advancedverif
    nationality.value = res.nationality
    kyc_status.value = res.kyc_status || 0
    advStatus.value = res.kyc_high_level_status || 0
    console.log(advStatus.value);
  })
}
const getIdentify = () => {
  _getIdentify().then(data => {
    turnDownMsg.value = data.msg;
  })
}
const fixState = (kyc_status) => {
  let str = ''
  if (kyc_status == 0) {
    str = t('notCertified')
  } else if (kyc_status == 1) {
    str = t('reviewing')
  } else if (kyc_status == 2) {
    str = t('verified')
  } else {
    str = t('notCertified')
  }
  return str;
}
const fixBtnState = (kyc_status) => {
  let str = ''
  if (kyc_status == 0) {
    str = t('authentication')
  } else if (kyc_status == 1) {
    str = t('auditDetails')
  } else if (kyc_status == 2) {
    str = t('certificationDetails')
  } else {
    str = t('authentication')
  }
  return str;
}
const openUlr = (index) => {
  if (index == 1) {
    router.push('/authentication')
  } else {
    router.push('/advancedCtf')
  }
}
</script>
<style lang="scss" scoped>
@import "@/views/authentication/components/intl.css";

.certificationCenter {
  width: 100%;
  box-sizing: border-box;
  font-size: 14px;
}

.btnMain {
  height: 45px !important;
  line-height: 45px !important;
  font-size: 14px;
}

.box {
  padding-left: 2rem;
  padding-right: 2rem;
  padding-bottom: 2.625rem;
  // background: $tab_background;

  .user-info {
    overflow: hidden;
    padding: 30px 10px 10px;
    font-size: 16px;
  }

}

.justify-between1 {
  height: 2rem;
  line-height: 2rem;
  font-size: 1.5rem;
  padding-top: 1rem;
}

.gn {
  background: $recommend_bg;
  border-radius: 3rem;
  margin-top: 2rem;
  display: flex;
  align-items: center;
  padding-left: 1.75rem;
  padding-right: 1.75rem;
  height: 50px;
  line-height: 50px;
}

.btnBox2 {
  padding-left: 2rem;
  padding-right: 2rem;
  display: flex;
}

.textColor4 {
  margin-top: 2rem;
  // background: $light-grey;
  color: $text_color;
  padding: 2px 20px;
  text-align: center;
  height: 40px;
  padding: 10px;
  border-radius: 2.5rem;
  box-sizing: border-box;
  line-height: normal;
}

.textColor5 {
  margin-right: 2rem;
}

.justify-between23 {
  padding-left: 2rem;
  padding-right: 2rem;
  margin-top: 2rem;
  display: flex;
}

.textColor1 {
  height: 3rem;
  line-height: 3rem;
}

.textColor2 {
  position: absolute;
  right: 0;
  // width: 27%;
}

.textColor666 {
  margin-left: 1rem;
}

.px-32-1 {
  padding-left: 2rem;
  padding-right: 2rem;
}

.contentBox {}

.btnMain {
  background: $btn_main !important;
  border-radius: 0.5rem;
  line-height: 3.4rem;
  height: 3.4rem;
  margin-top: 2rem;
  color: $text_color;
}

.gn img {
  width: 20px;
  height: 20px;
}

.textColor-span {
  margin-left: 0;
}

.span2 {
  font-size: 14px;
  height: 5rem;
  line-height: 2rem;
  margin: 1.5rem 0rem;
}

.span32 {
  padding-right: 2rem;
  margin-top: 2rem;
}

.px-32-2 .textColor img {
  width: 2rem;
  height: 2rem;
}

.btn {
  margin-top: 2rem;
  background: $btn_main;
  border-radius: 0.5rem;
  line-height: 45px;
  height: 45px;
  color: $main-btn-color;
}

.text-red {
  color: $red;
}

.mt-64 {
  margin-top: 2rem;
}

.mt-40-1 {
  height: 2rem;
  line-height: 2rem;
}

.ml-24 {
  margin-left: 5px;
}

.px-32-1 img {
  width: 20px;
  height: 20px;
}

.px-32-2 .mt-40 {
  margin-top: 0;
}


.activeStyle {
  background: $btn_main;
  color: $main-btn-color;
}

.status0 {
  background: $cont_background;
  color: $red;
}

.status1 {
  background: $cont_background;
  color: #DBAE18;
}

.status2 {
  background: #E6F6F0;
  color: #03A66D;
}

.status3 {
  background: $cont_background;
  color: $red;
}

.fs {
  font-size: 14px;
  margin-left: 10px;
  color: $text_color1;
  ;
}

.text-grey .img3 {
  width: 20px;
  height: 20px;
}

.border-radius-left {
  border-top-left-radius: 40px;
  border-bottom-left-radius: 40px;
}

.van-popup.van-popup--bottom {
  border-top-left-radius: 30px;
  border-top-right-radius: 30px;
}

.yaoqu {
  height: 40px;
  line-height: 40px;
  padding: 0 10px;
}

.yaoqu img {
  width: 20px;
  height: 20px;
  margin-top: 10px;
  margin-right: 5px;
}

.tk-img {
  width: 20px;
  height: 20px;
}

.mainBackground {
  padding: 1rem 1rem;
}

.text-grey {
  margin-top: 15px;
}

.textColor9 {
  height: 4rem;
  line-height: 4rem;
}

.absolute {
  margin-top: 0.9rem;
  right: 1rem;
}

.text-grey1 {
  margin-left: 3rem;
  font-size: 14px;
  color: $text_color1 !important;

}

.colorMain {
  color: $color_main !important;
}

.mr-10 {
  margin-right: 0.5rem;
  margin-left: 1rem;
}

.user-info {
  justify-content: space-between;
  align-items: center;
}

.border-b-color {
  padding-bottom: 10px;
  border-bottom: 1px solid $border_color;
}
</style>