<template>
  <div id="cryptos">
    <div class="poolLock">
      <assets-head :title="$t('矿池锁仓')"></assets-head>
      <div class="px-8 pb-10">
        <div class="swiper">
          <div class="flex items-center ">
            <img class="w-32 h-32" :src="imgUrl" alt="">
            <!-- <img class="w-128 h-128" :src="`@/views/cryptos/financialManagement/images/${ImageStyle(data.mine_index)}.png`"
              alt=""> -->
            <span class="ml-7 text-white font-semibold text-50">
              <template v-if="$i18n.locale === 'CN'">
                {{ data.name_cn }}
              </template>
              <template v-if="$i18n.locale === 'zh-CN'">
                {{ data.name }}
              </template>
              <template v-else>
                {{ data.name_en }}
              </template>
            </span>
          </div>
          <div class="mt-10 flex">
            <div class="flex flex-col items-start">
              <span class="text-26 text-grey h-16">{{ $t('适用算法') }}</span>
              <span class="text-40 font-bold text-white mt-16">{{ data.algorithm }}</span>
            </div>
            <div class="flex flex-col items-start ml-16">
              <span class="text-26 text-grey h-16">{{ $t('官方功耗') }}</span>
              <span class="text-40 font-bold text-white mt-16">{{ data.power }}w</span>
            </div>
            <div class="flex flex-col items-start ml-16">
              <span class="text-26 text-grey h-16">{{ $t('额定算力') }}</span>
              <span class="text-40 font-bold text-white mt-16">{{ data.computing_power + data.computing_power_unit
              }}</span>
            </div>
          </div>
        </div>
        <div class="mt-16 mb-5 text-color text-30 textColor">{{ $t('挖矿收益') }}</div>
        <div class="mb-40">
          <div class="flex greyBg textColor text-26 py-6">
            <span class="flex-1 text-center">{{ $t('币种') }}</span>
            <span class="flex-1 text-center">{{ $t('预计日收益') }}</span>
            <!-- <span class="flex-1 text-center">{{$t('净利润/天')}}</span> -->
            <span class="flex-1 text-center">{{ $t('预计净利润') }}</span>
            <span class="flex-1 text-center">{{ $t('操作') }}</span>
          </div>
          <div class="borderColor flex mainBackground text-26 py-6 textColor">
            <span class="flex-1 flex items-center justify-center">{{ data.miner_profit_symbol }}</span>
            <span class="flex-1 flex items-center justify-center">{{ data.daily_rate }}{{ data.test === true ? '' : '%'
            }}</span>
            <!-- <span class="flex-1 flex items-center justify-center text-green">{{ data.all_rate }}</span> -->
            <span class="flex-1 flex items-center justify-center text-green">{{
              calcMiniProfit(data.test, data.daily_rate, data.investment_min, data.investment_max) }}</span>
            <span class="flex-1 flex justify-center">
              <div @click="data.on_sale === '0' ? null : $router.push({
                path: '/cryptos/machine-buy',
                //query: data
                query: { obj: JSON.stringify(data) }
              })" class="btn w-40 h-16 text-center text-white" :class="data.on_sale === '0' ? 'inactive' : 'active'">
                {{ data.on_sale === '0' ? $t('已锁定') : data.test === true ? $t('体验') : $t('租用') }}
              </div>
            </span>
          </div>
          <div class="mt-11 mb-5 text-color text-30 textColor">{{ $t('基础数据') }}</div>
          <div>
            <div class="borderColor flex justify-between items-center text-30 py-8 px-8">
              <span class="text-grey">{{ $t('生产厂家') }}</span>
              <span class="textColor">{{ data.product_factory }}</span>
            </div>
            <div class="border-round flex justify-between items-center text-30 py-8 px-8">
              <span class="text-grey">{{ $t('额定算力') }}</span>
              <span class="textColor">{{ data.computing_power + data.computing_power_unit }}</span>
            </div>
            <div class="border-round flex justify-between items-center text-30 py-8 px-8">
              <span class="text-grey">{{ $t('墙上功耗') }}</span>
              <span class="textColor">{{ data.power }}</span>
            </div>
            <div class="border-round flex justify-between items-center text-30 py-8 px-8">
              <span class="text-grey">{{ $t('外箱尺寸') }}</span>
              <span class="textColor">{{ data.product_size }}</span>
            </div>
            <div class="border-round flex justify-between items-center text-30 py-8 px-8">
              <span class="text-grey">{{ $t('整机重量') }}</span>
              <span class="textColor">{{ data.weight }}KG</span>
            </div>
            <div class="border-round flex justify-between items-center text-30 py-8 px-8">
              <span class="text-grey">{{ $t('工作温度') }}</span>
              <span class="textColor">{{ data.work_temperature_min }}℃ ~ {{ data.work_temperature_max }}℃</span>
            </div>
            <div class="border-round flex justify-between items-center text-30 py-8 px-8">
              <span class="text-grey">{{ $t('工作湿度') }}</span>
              <span class="textColor">{{ data.work_humidity_min }}%RH ~ {{ data.work_humidity_max }}%RH</span>
            </div>
            <div class="border-round flex justify-between items-center text-30 py-8 px-8">
              <span class="text-grey">{{ $t('网络链接') }}</span>
              <span class="textColor">{{ data.internet }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import assetsHead from '@/components/Transform/assets-head/index.vue';
export default {
  name: "PooLock",
  data() {
    return {
      data: {
        imgUrl: ''
      }
    }
  },
  components: {
    assetsHead,
  },
  created() {
    //this.data = this.$route.query;
    this.data = this.$route.query.obj && JSON.parse(this.$route.query.obj)
    let imgUrl = new URL(`../../../assets/image/machine/${this.ImageStyle(this.data.mine_index)}.png`, import.meta.url)
    this.imgUrl = this.handleImage(imgUrl)
  },
  methods: {
    handleImage(url) {
      return new URL(url, import.meta.url).href
    },
    calcMiniProfit(test, daily_rate, invest_min, invest_max) {
      if (test) {
        return daily_rate;
      }
      let daily_base = daily_rate * 0.01;
      let daily_min = (daily_base * invest_min).toFixed(1);
      let daily_max = (daily_base * invest_max).toFixed(1);
      return daily_min + '-' + daily_max;
    },
    ImageStyle(index_id) {
      switch (index_id) {
        case 1: {
          return 'machine_asic'
        }
        case 2: {
          return 'machine_ex'
        }
        case 3: {
          return 'machine_fpga'
        }
        case 4: {
          return 'machine_ipfs'
        }
        case 5: {
          return 'machine_gpu'
        }
        case 6: {
          return 'machine_ck6'
        }
        case 0:
        default: {
          return 'machine1'
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  .active {
    background: $btn_main;
  }

  .inactive {
    background: $grey_bg;
  }

  .poolLock {
    width: 100%;
    box-sizing: border-box;
  }

  .swiper {
    background: url("./images/machine-bg.png") no-repeat center center;
    padding: 32px 32px 50px 32px;
    box-sizing: border-box;
    border-radius: 9px;
  }

  .btn {
    border-radius: 6px;
    line-height: 60px
  }

  .border-round {
    border-bottom: 1px solid $border_color;
    border-right: 1px solid $border_color;
    border-left: 1px solid $border_color;
  }
}
</style>
