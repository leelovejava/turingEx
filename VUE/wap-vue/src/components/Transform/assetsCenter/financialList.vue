<template>
  <div id="cryptos">
    <div class="w-full mt-9 box-border px-5 py-8 rounded-lg fina-border  financialBackground"
      v-for="(item, index) in list" :key="index">
      <div class="flex justify-between" @click="handleGoOrderDetail(item)">
        <div class="flex ">
          <img v-if="type == 0" :src="item.img ? item.img : fund5Img" alt="" class="w-40 h-40 mr-9" />
          <img v-else :src="item.img ? item.img : machine_fpgaImg" alt="" class="w-40 h-40 mr-9" />
          <div>
            <div class="text-26 font-bold textColor" v-if="type == 0">
              {{ $i18n.locale === 'CN' ? item.financeName_cn : $i18n.locale === 'zh-CN' ? item.financeName :
                item.financeName_en }}
            </div>
            <div class="text-26 font-bold textColor" v-else>
              {{ $i18n.locale === 'CN' ? item.miner_name_cn : $i18n.locale === 'zh-CN' ? item.miner_name :
                item.miner_name_en }}
            </div>
            <!-- <div v-if="tapIndex === 1" class="text-36 font-6">体验矿机3天</div> -->
            <div class="text-26 cl-33 mt-6 mb-4 textColor1">{{ $t('数量') }}
              <span class="ml-2 mr-2 textColor">{{ item.amount }}</span>{{ item.buyCurrency ?
                item.buyCurrency.toUpperCase() : 'USDT' }}
            </div>
            <div class="text-26 cl-33 textColor1">{{ $t('周期') }}&nbsp;<span class="textColor">{{ item.cycle == 0 ?
              $t('无限期') : item.cycle + $t('天') }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-center align-center text-center h-36 ">
          <div class="mr-6 flex flex-col justify-center items-center">
            <span class="h-13 lh-13 text-44" :class="item.profit / 1 >= 0 ? 'text-green' : 'text-red'">{{ (item.profit /
              1).toFixed(2) }}</span>
            <span class="w-40 text-26 mt-4 textColor">{{ $t('累计收益') }}({{ item.outputCurrency ?
              item.outputCurrency.toUpperCase() : 'USDT' }})</span>
          </div>
          <div class="h-36 ">
            <van-icon class="textColor" name="arrow" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Icon } from 'vant';
export default {
  name: "financialList",
  props: ['list', 'btnShow', 'goBack', 'type'], // orderShow 点击跳转到订单详情的时候是否展示赎回按钮,goBack表示订单详情返回一层
  components: {
    [Icon.name]: Icon,
  },
  data() {
    return {
      fund5Img: new URL('@/assets/image/fund5.png', import.meta.url),
      machine_fpgaImg: new URL('@/assets/image/machine_fpga.png', import.meta.url),
    }
  },
  mounted() {
    console.log(this.type)
  },
  methods: {
    handleGoOrderDetail(item) {

      if (this.type === 0) {
        this.$router.push({
          path: '/cryptos/financialOrder',
          query: {
            order_no: item.orderNo,
            showBtn: this.btnShow,
            id: item.uuid,
            goBack: true
          }
        })
      } else {
        this.$router.push({
          path: '/cryptos/miningMachineOrder',
          query: {
            order_no: item.order_no,
            showBtn: this.btnShow,
            goBack: true
          }
        })
      }
    },
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  font-size: 30px;
}
</style>
