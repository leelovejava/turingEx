<template>
  <div id="cryptos">
    <div id="rule">
      <assets-head :title="$t('规则')" :back-func="backFunc"></assets-head>
      <div class="w-full h-1 greyBg"></div>
      <div class="box-border px-8">
        <div class="mt-4 flex text-66 items-center">
          <img src="@/assets/image/assets-center/logo.png" alt="" class="w-24 h-24 mr-10">
          <div class="text-60 textColor">{{ $t('理财规则') }}</div>
        </div>
        <div class="text-32 mt-6 text-grey">{{ $t('理财说明') }}</div>
        <div class="text-32 mt-14 h-9 textColor">{{ $t('收益计算') }}</div>
        <div class="mt-5 text-30 text-grey text-left">
          {{ $t('会员在平台存入了10000U，选择了周期为5天 ，日收益为0.3%~0.5%的理财产品，则每天收益如下：') }}<br />
          <br />
          {{ $t('最低：') }}<span class="textColor">{{ $t('10000U X 0.3%=30U') }}</span> <br />
          <br />
          {{ $t('最高：') }}<span class="textColor">{{ $t('10000U X 0.5%=50U') }}</span> <br />
          <br />
          {{ $t('既：5天后可以获得') }}<span class="textColor">150U~250U</span> {{ $t('的收益，收益每日下发，下发的收益可随时存取，存入本金，到期满后，自动返还至您的账户')
          }}
        </div>
        <div class="text-32 mt-14 h-9 textColor">{{ $t('关于违约金') }}</div>
        <div class="mt-5 text-30 text-grey text-left">
          {{ $t('若您希望转出未到期的本金，则会产生违约金，') }} <span class="textColor">{{ $t('违约金=结算比例x剩余天数x投资数量。') }}</span> <br /> <br />
          {{ $t('举例：该产品的违约金结算比例为') }} <span class="textColor">0.4%</span>，{{ $t('剩余') }} <span class="textColor"> 3</span>
          {{ $t('天到期，投资数量为') }}<span class="textColor">1000</span>,{{ $t('则') }} <span class="textColor">{{
            $t('违约金=0.4%x3x1000=12U') }}</span>，{{ $t('实际退还本金为') }} <span class="textColor">{{ $t('1000U-12U=988U')
  }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import assetsHead from '@/components/Transform/assets-head/index.vue';
export default {
  name: "fundRule",
  components: {
    assetsHead
  },
  methods: {
    backFunc() {
      console.log('this.$route.query.back', this.$route.query.back)
      if (this.$route.query.back / 1 === 0) {
        this.$router.push({
          path: '/cryptos/funds', query: {
            tab: 3,
            index: 0,
            type: 'cryptos'
          }
        })
      } else {
        this.$router.go(-1)
      }

    }
  },
}
</script>

<style lang="scss" scoped>
#cryptos {
  #rule {
    width: 100%;
    box-sizing: border-box;
  }
}
</style>
