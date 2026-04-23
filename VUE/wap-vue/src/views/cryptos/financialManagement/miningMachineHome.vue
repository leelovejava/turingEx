<template>
  <div id="miningMachineHome">
    <div class="miningMachineHome">
      <assets-head :title="$t('矿机')" :showLeft="true" :goHome="goHome"></assets-head>

      <div class="box-border px-8 mt-10">
        <div class="flex justify-between text-grey relative">
          <div class="flex flex-col">
            <span class="text-grey text-30">{{ $t('托管金额') }}（{{ minglist.buyCurrency
              ? minglist.buyCurrency.toUpperCase() : 'USDT' }}）</span>
            <span class="text-66 mt-55 font-bold textColor">{{ minglist.amount_sum }}</span>
          </div>
        </div>
        <div class="flex mt-11 justify-between text-26 mb-9">
          <div class="flex flex-col ">
            <div class="text-grey text-26">{{ $t('预计日收益')
            }}（{{ minglist.outputCurrency ? minglist.outputCurrency.toUpperCase() : 'USDT' }}）</div>
            <div class="mt-4 mb-2 text-36 textColor">{{ minglist.today_profit }}</div>
          </div>
          <div class="flex flex-col">
            <div class="text-grey text-26">{{ $t('累计收益')
            }}（{{ minglist.outputCurrency ? minglist.outputCurrency.toUpperCase() : 'USDT' }}）</div>
            <div class="mt-4 mb-2 text-36 textColor">{{ minglist.aready_profit }}</div>
          </div>
          <div class="flex flex-col">
            <div class="text-grey text-26">{{ $t('托管订单') }}</div>
            <div class="mt-4 mb-2 text-36 textColor">{{ minglist.order_sum }}</div>
          </div>
        </div>
      </div>
      <div class="flex justify-between box-border px-8 mt-11">
        <div @click="$router.push('/cryptos/funds?tab=3&index=1')"
          class="greyBg textColor w-96 py-3 rounded-md text-28 text-center mr-5 active activeBKClick">{{ $t('托管订单') }}
        </div>
        <div @click="$router.push('/cryptos/machine-rule')"
          class="greyBg text-grey w-96 py-3 rounded-md text-28 text-center activeClick">{{ $t('规则') }}</div>
      </div>
      <div class="px-8 mt-10 pb-20">
        <div v-for="(item, index) in list" :key="index"
          class="card flex justify-between items-center relative rounded-md borderColor financialBackground">
          <div class="title absolute px-4 py-2 text-26 text-white top-0 left-22"
            :class="item.on_sale === '0' ? 'inactive' : 'bg-green'">
            <!-- {{item.on_sale==='0'?$t('已锁定，联系客服'):$t('日收益率')}}{{item.on_sale==='0'?null:item.daily_rate}}{{item.on_sale==='0'?null:index===0?'':'%'}} -->
            {{ item.on_sale === '0' ? $t('已锁定，联系客服') : (item.test === 'Y' ? item.cycle + $t('天') + $t('预期收益') + ' ' +
              item.all_rate
              +
              'USDT' : $t('30天预期收益') + ' ' + item.all_rate + '%') }}
          </div>
          <div class="flex justify-start items-center flex-1" @click="click_buy(item, index)">
            <img v-if="index === 0" class="w-32 h-32 mr-4" src="./images/machine1.png" alt="">
            <img v-else-if="index === 1" class="w-32 h-32 mr-4" src="./images/machine_asic.png" alt="">
            <img v-else-if="index === 2" class="w-32 h-32 mr-4" src="./images/machine_ex.png" alt="">
            <img v-else-if="index === 3" class="w-32 h-32 mr-4" src="./images/machine_fpga.png" alt="">
            <img v-else-if="index === 4" class="w-32 h-32 mr-4" src="./images/machine_ipfs.png" alt="">
            <img v-else-if="index === 5" class="w-32 h-32 mr-4" src="./images/machine_gpu.png" alt="">
            <img v-else-if="index === 6" class="w-32 h-32 mr-4" src="./images/machine_ck6.png" alt="">
            <img v-else class="w-32 h-32 mr-4" src="./images/machine_ex.png" alt="">
            <div class="flex flex-col items-start">
              <span class="text-36 font-semibold textColor" style="max-width:200px;">
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
              <!-- <span class="text-26 mt-6 mb-4 textColor">{{item.on_sale==='0'?$t('已锁定，联系客服'):$t('日收益率')}}{{' '}}{{item.on_sale==='0'?null:item.daily_rate}}{{item.on_sale==='0'?null:index===0?'':'%'}}</span> -->
              <span class="text-26 mt-6 mb-4 textColor">{{ $t('日收益率') }}{{ item.daily_rate }}{{ item.test === 'Y' ?
                'USDT'
                : '%'
              }}</span>
              <span class="text-26 mb-4 textColor">{{ $t('限额') }} {{ item.investment_min + '-' }}{{ item.investment_max
              }}
                {{ item.buy_currency ? item.buy_currency.toUpperCase() : '--' }}</span>
              <span class="text-26 textColor">{{ $t('周期') }} {{ item.cycle == 0 ? $t('无限期') : item.cycle + $t('天')
              }}</span>
            </div>
          </div>
          <div @click="item.on_sale === '0' ? null : $router.push({
            path: '/cryptos/machine-buy',
            query: { obj: JSON.stringify(item) }
          })" class="py-4 w-44 text-28 font-semibold text-center rounded-md"
            :class="item.on_sale === '0' ? 'inactive activeDisableClick' : 'active activeBKClick'">{{
              item.on_sale === '0' ? $t('已锁定') : item.test ? $t('立即体验') : $t('立即租用') }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import assetsHead from '@/components/Transform/assets-head/index.vue';
import { getMachineList } from "@/service/financialManagement.api.js";
import { getMiningRevenueStatisticsList } from "@/service/financialManagement.api.js";
export default {
  name: "fundHome",
  components: {
    assetsHead,
  },
  data() {
    return {
      goHome: false,
      minglist: {},
      list: []
    }
  },
  methods: {
    click_share() {
      this.$router.push('/promote');
    },
    click_buy(item, index) {
      item['mine_index'] = index
      this.$router.push({
        path: '/cryptos/pool-lock',
        //query: item
        query: { obj: JSON.stringify(item) }
      })
    }
  },
  activated() {
    getMachineList().then(res => {
      //console.log('矿机列表', res)
      this.list = res
      //console.log(this.list)
    })
    getMiningRevenueStatisticsList(
      this.$store.state.user.userInfo.token
    ).then(res => {
      //console.log('矿机收益', res)
      this.minglist = res
    })
  },
  created() {
    console.log(this.$i18n.locale)
    getMachineList().then(res => {
      //console.log('矿机列表',res)
      this.list = res
    })
    getMiningRevenueStatisticsList(
      this.$store.state.user.userInfo.token
    ).then(res => {
      //console.log('矿机收益',res)
      this.minglist = res
    })
  }
}
</script>

<style lang="scss" scoped>
#miningMachineHome {
  .activeBKClick {
    &:active {

      background: $btn_main;
      border-color: $btn_main;

      opacity: 0.5;
    }
  }

  .activeClick {
    &:active {
      background: $grey_bg;
      opacity: 0.6;
    }
  }

  .activeDisableClick {
    &:active {
      background: $grey_bg;
      opacity: 0.5;
    }
  }

  .miningMachineHome {
    width: 100%;
    box-sizing: border-box;
  }

  .active {
    color: $white;

    background: $btn_main;
    border-color: $btn_main;
  }

  .inactive {
    color: $text_color;
    background: $grey_bg;
    border-color: $grey_bg;
  }

  .title {
    border-radius: 0 0 8px 8px;
  }

  .card {
    padding: 80px 22px 22px 22px;
    margin-bottom: 32px;
  }
}
</style>
