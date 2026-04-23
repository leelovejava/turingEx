<template>
  <div id="cryptos">
    <div id="c2cAnswer">
      <normal-head title="C2C交易相关问答" />
      <div class="px-8">
        <div class="mt-16 font-bold text-50">{{ $t('C2C交易相关问答') }}</div>
        <div class="mt-5 mb-14 col">{{ $t('最新更新时间：') }}{{ $t(fullTime) }}</div>
        <div class="mb-12" v-if="detail">
          <div class="text-28 font-semibold mb-7">{{ $t(`1.${detail.title}`) }}</div>
          <div class="text-28 font-normal">{{ $t(detail.content) }}</div>
        </div>
        <!--    <div class="mb-12">-->
        <!--      <div class="text-28 font-semibold mb-7">{{$t('2.什么是广告？')}}</div>-->
        <!--      <div class="text-28 font-normal">{{$t('用户发布的购买或出售数字货币的需求。')}}</div>-->
        <!--    </div>-->
        <!--    <div class="mb-12">-->
        <!--      <div class="text-28 font-semibold mb-7">{{$t('3.什么是广告方？')}}</div>-->
        <!--      <div class="text-28 font-normal">{{$t('发布广告的用户称之为广告方。')}}</div>-->
        <!--    </div>-->
        <!--    <div class="mb-12">-->
        <!--      <div class="text-28 font-semibold mb-7">{{$t('4.什么是放行？')}}</div>-->
        <!--      <div class="text-28 font-normal">{{$t('出售数字货币方，在收到与订单相符的钱之后，释放订单中的数字货币给交易对手。')}}</div>-->
        <!--    </div>-->
        <!--    <div class="mb-12">-->
        <!--      <div class="text-28 font-semibold mb-7">{{$t('5.如何划转？')}}</div>-->
        <!--      <div class="text-28 font-normal">{{$t('点击钱包或资金（APP客户端），在存有数字货币的相应资产中，点击划转，选择想要划转到的钱包和数量，确认划转。')}}</div>-->
        <!--    </div>-->
        <!--    <div class="mb-12">-->
        <!--      <div class="text-28 font-semibold mb-7">{{$t('6.什么是申诉？')}}</div>-->
        <!--      <div class="text-28 font-normal">{{$t('当用户和交易对手产生纠纷或分歧时，可以点申诉按钮发起申诉，申诉之后平台客户会介入。')}}</div>-->
        <!--    </div>-->
        <!--    <div class="mb-12">-->
        <!--      <div class="text-28 font-semibold mb-7">{{$t('7.取消申诉？')}}</div>-->
        <!--      <div class="text-28 font-normal">{{$t('当用户和交易对手产生分歧或纠纷发起了申诉，在双方达成一致后，由发起方或平台客服取消申诉，订单回到待放行状态，不会取消。')}}</div>-->
        <!--    </div>-->
      </div>
    </div>
  </div>
</template>

<script>
import {
  formatTime
} from "@/utils/utis";
import otcApi from "@/service/otc.api";
import {
  mapState
} from "vuex";
import NormalHead from "@/components/normal-head";
import { showLoadingToast, closeToast } from "vant";

export default {
  name: "c2cTransactionAnswer",
  props: ['content_code'],
  data() {
    return {
      detail: null,
    }
  },
  created() {
    this.getHelpCenterDetail();
  },
  methods: {
    getHelpCenterDetail() {
      showLoadingToast({
        duration: 0,
        forbidClick: true,
        message: this.$t('加载中')
      })
      const params = {
        content_code: this.content_code,
        language: this.language
      }
      otcApi.getC2cHelpCenterDetail(params).then(res => {
        closeToast();
        this.detail = res;

      })
    }
  },
  computed: {
    ...mapState('language', ['language']),
    fullTime() {
      if (!this.detail) return ''
      return formatTime(new Date(this.detail.createTime), 'yyyy年MM月dd日')
    }
  },
  components: {
    NormalHead,
  },
}
</script>

<style lang="scss" scoped>
#cryptos {
  font-size: 30px;

  .col {
    color: #868D9A;
  }
}
</style>