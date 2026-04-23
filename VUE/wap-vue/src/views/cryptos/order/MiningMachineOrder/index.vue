<template>
  <div id="cryptos">
    <div id="financialOrder" class="financialOrder">
      <assets-head :title="title" :back-func="backFunc"></assets-head>
      <div class="box-border  ">
        <div class="flex flex-col justify-center items-center px-8">
          <div class="text-32 font-normal my-8 textColor">{{ $t('锁仓金额') }}（USDT）</div>
          <div class="text-66 mb-16 textColor">{{ dataObj.amount }}</div>
        </div>

        <div class="px-8 boderT12">
          <div class="flex justify-between flex-row items-center h-20">
            <div class="text-32 font-normal text-grey">{{ $t('锁仓时间') }}</div>
            <div class="text-32 font-normal textColor">{{ dataObj.cycle == 0 ? $t('无限期') : dataObj.cycle + $t('天') }}
            </div>
          </div>
          <div class="flex justify-between items-center h-20">
            <div class="text-32 font-normal text-grey">{{ $t('当日收益') }}</div>
            <div class="text-32 font-normal textColor">{{ dataObj.profit_may / 30 }}&nbsp;{{ dataObj.outputCurrency ?
              dataObj.outputCurrency.toUpperCase() : 'USDT' }}</div>
          </div>
          <div class="flex justify-between items-center h-20">
            <div class="text-32 font-normal text-grey">{{ $t('已获收益') }}</div>
            <div class="text-32 font-normal textColor">{{ dataObj.profit }}</div>
          </div>
          <div class="flex justify-between items-center h-20">
            <div class="text-32 font-normal text-grey">{{ $t('剩余天数') }}</div>
            <div class="text-32 font-normal textColor">{{ dataObj.days + $t('天') }}</div>
          </div>
        </div>
        <div class="px-8 boderT12">
          <div class="flex justify-between items-center h-20">
            <div class="text-32 font-normal text-grey">30{{ $t('天') }}{{ $t('预期收益') }}</div>
            <div class="text-32 font-normal textColor">{{ dataObj.profit_may }}&nbsp;{{ dataObj.outputCurrency ?
              dataObj.outputCurrency.toUpperCase() : 'USDT' }}</div>
          </div>
          <div class="flex justify-between items-center h-20">
            <div class="text-32 font-normal text-grey">{{ $t('日收益率') }}</div>
            <div class="text-32 font-normal textColor">{{ dataObj.daily_rate }}%</div>
          </div>
        </div>

        <div class="px-8 boderT12">
          <div class="flex justify-between items-center h-20">
            <div class="text-32 font-normal text-grey">{{ $t('起息时间') }}</div>
            <div class="text-32 font-normal textColor">{{ dataObj.earn_timeStr }}</div>
          </div>
          <div class="flex justify-between items-center h-20">
            <div class="text-32 font-normal text-grey">{{ $t('到期时间') }}</div>
            <div class="text-32 font-normal textColor">{{ dataObj.stop_timeStr ? dataObj.stop_timeStr : '--' }}</div>
          </div>
        </div>

        <div class="px-8 boderT12">
          <div class="flex justify-between items-center h-20">
            <div class="text-32 font-normal text-grey">{{ $t('订单编号') }}</div>
            <div class="text-32 font-normal textColor">{{ dataObj.order_no }}</div>
          </div>
          <div class="flex justify-between items-center h-20">
            <div class="text-32 font-normal text-grey">{{ $t('订单时间') }}</div>
            <div class="text-32 font-normal textColor">{{ dataObj.create_timeStr }}</div>
          </div>
        </div>
      </div>
      <button v-if="this.showBtn == 'true'" @click="ransom"
        class="textColor btnMain h-20 w-4/5 rounded-lg text-center fixed wz border-none m-auto"
        :disabled="!dataObj.can_close" :class="!dataObj.can_close ? 'greyBg textColor1' : ''">{{ $t('我要赎回') }}
      </button>

    </div>
  </div>
</template>

<script>
import assetsHead from '@/components/Transform/assets-head/index.vue';
import { ransomMachineProduct, getMinerorder } from "@/service/financialManagement.api.js";
import { Button, showSuccessToast, Uploader } from 'vant';
import dayjs from "dayjs";
export default {
  name: "MiningMachineOrder",
  components: {
    assetsHead
  },
  data() {
    return {
      type: '',
      order_no: '',
      showBtn: false,
      title: '',
      dataObj: {
        amount: 0,
        cycle: '',
        daily_profit: '',
        profit: '',
        days: '',
        daily_rate: '',
        earn_time: '',
        stop_time: '',
        order_no: '',
        create_time: ''
      }
    }
  },
  mounted() {
    this.type = this.$route.query.type
    this.order_no = this.$route.query.order_no
    this.showBtn = this.$route.query.showBtn
    this.getOrderDetail()
  },
  methods: {
    backFunc() {
      this.$router.push({
        path: '/cryptos/funds?type=cryptos',
        query: {
          tab: 3,
          index: 1 // 0: 查看理财订单 1: 矿机
        }
      })
    },
    ransom() { // 赎回
      ransomMachineProduct({
        order_no: this.order_no
      }).then(res => {
        showSuccessToast(this.$t('赎回成功'))
        setTimeout(() => {
          this.backFunc()
        }, 1000)
      })
    },
    getOrderDetail() {
      getMinerorder({
        order_no: this.order_no
      }).then(res => {
        this.dataObj = res
        console.log(this.$i18n.locale);
        let title = this.$i18n.locale === 'CN' ? res.miner_name_cn : this.$i18n.locale === 'zh-CN' ? res.miner_name : res.miner_name_en
        this.title = title + ' ' + this.$t('详情')
      })
    }
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  font-size: 30px;

  .financialOrder {
    width: 100%;
    box-sizing: border-box;
  }

  .wz {
    bottom: 44px;
    left: 32px;
    right: 32px;
  }

  .boderT12 {
    // @include themify() {
    //   border-top: 1px solid themed("cont_background");
    // }
  }
}
</style>
