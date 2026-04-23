<template>
  <div id="fundHome">
    <div class="fundHome">
      <assets-head :title="$t('基金理财')"></assets-head>
      <div class="box-border px-8 mt-10">
        <div class="flex justify-between text-grey relative">
          <div class="flex flex-col">
            <span class="text-grey text-30">{{ $t('托管金额') }}（USDT）</span>
            <span class="text-66 mt-5 font-bold textColor">{{ statiscis.amount_sum }}</span>
          </div>
        </div>
        <div class="flex mt-11 justify-between text-26 mb-2">
          <div class="flex flex-col ">
            <div class="text-grey text-26">{{ $t('预计日收益') }}（USDT）</div>
            <div class="mt-4 mb-2  text-36 textColor">{{ statiscis.today_profit }}</div>
          </div>
          <div class="flex flex-col">
            <div class="text-grey text-26">{{ $t('累计收益') }}（USDT）</div>
            <div class="mt-4 mb-2 text-36 textColor">{{ statiscis.aready_profit }}</div>
          </div>
          <div class="flex flex-col">
            <div class="text-grey text-26">{{ $t('托管订单') }}（USDT）</div>
            <div class="mt-4 mb-2 text-36 textColor">{{ statiscis.order_sum }}</div>
          </div>
        </div>
      </div>
      <div class="flex justify-between box-border px-8 mt-10">
        <div @click="$router.push('/cryptos/funds?tab=3&index=0&type=cryptos')"
          class="greyBg textColor w-96 py-3 rounded-md text-center text-28 mr-5 active">{{ $t('托管订单') }}
        </div>
        <div @click="$router.push('/cryptos/fund-rule')"
          class="greyBg text-grey w-96 py-3 rounded-md text-center text-28">
          {{ $t('规则') }}</div>
      </div>
      <div class="px-8 mt-10 pb-20">
        <div v-for="(item, index) in list" :key="index"
          class="card flex justify-between items-center relative rounded-md borderColor financialBackground ">
          <div class="title absolute px-4 py-2 text-26 text-white bg-green top-0 left-22">{{ $t('日收益率')
          }} <span class="ml-2">{{ item.daily_rate }}%</span></div>
          <div class="flex justify-center items-center">
            <img class="w-32 h-32 mr-5" :src="item.img" alt="">
            <div class="flex flex-col items-start">
              <span class="text-36 font-semibold textColor">
                <template v-if="$i18n.locale === 'CN'">
                  {{ item.name_cn }}
                </template>
                <template v-else-if="$i18n.locale === 'zh-CN'">
                  {{ item.name }}
                </template>
                <template v-else>
                  {{ item.name_en }}
                </template>
              </span>
              <span class="text-26 mt-6 mb-4 textColor">{{ $t('限额') }} {{ item.investment_min + ' ~ ' +
                item.investment_max }}
                USDT</span>
              <span class="text-26 textColor">{{ $t('周期') }} {{ item.cycle }} {{ $t('天') }}</span>
            </div>
          </div>
          <div @click="$router.push({
            path: '/cryptos/fund-buy',
            query: {
              ...item
            }
          })" class="active py-4 px-8 text-30 font-semibold rounded-md">{{ $t('立即买入') }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import assetsHead from '@/components/Transform/assets-head/index.vue';
import { financeStatics, getfinacialProductsList } from "@/service/financialManagement.api.js";

export default {
  name: "fundHome",
  components: {
    assetsHead,
  },
  data() {
    return {
      statiscis: {},
      list: []
    }
  },
  methods: {
  },
  activated() {
    getfinacialProductsList().then(res => {
      //console.log('理财列表', res)
      this.list = res
    })
    financeStatics().then(res => {
      //console.log('理财统计', res)
      this.statiscis = res
    })
  },
  created() {
    getfinacialProductsList().then(res => {
      //console.log('理财列表',res)
      this.list = res
    })
    financeStatics().then(res => {
      //console.log('理财统计',res)
      this.statiscis = res
    })
  }
}
</script>

<style lang="scss" scoped>
#fundHome {

  .fundHome {
    width: 100%;
    box-sizing: border-box;
  }

  .active {
    color: $text_color;

    // @include themify() {
    //   background: themed("btn_main") !important;
    // }

    // @include themify() {
    //   border-color: themed("btn_main") !important;
    // }

  }

  .title {
    border-radius: 0 0 8px 8px;
  }

  .card {
    padding: 80px 22px 22px 22px;
    margin-bottom: 32px;
  }

  .active {
    color: $white;
    background: $btn_main;
    border-color: $btn_main;
  }
}
</style>
