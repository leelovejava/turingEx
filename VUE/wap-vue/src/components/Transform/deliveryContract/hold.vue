<template>
  <!-- 持有仓位列表 -->
  <div id="cryptos">
    <div class="border-b-color text-28" v-for="item in listData" :key="item.order_no">
      <div class="flex justify-between pt-11 pb-11">
        <div class="flex flex-col">
          <div class="flex items-center">
            <div class="pl-7 pr-7 pt-2 pb-2 text-white open-btn text-28"
              :class="item.direction == 'buy' ? ' bg-green' : 'bg-red'">
              {{ item.direction == 'buy' ? $t('开多') : $t('开空') }}
            </div>
            <div class="ml-4 text-32 text-600">
              <span class="textColor">{{ item.name }} {{ $t('交割') }}</span>
              <span class="text-grey text-28 ml-4 mr-4">{{ $t('全仓') }} {{ item.lever_rate
              }}x</span>
            </div>
            <img v-if="item.direction == 'buy'" src="@/assets/image/public/green-leverage.png" alt="" class="w-8 h-8" />
            <img v-else src="@/assets/image/public/red-leverage.png" alt="" class="w-8 h-8" />
          </div>
        </div>
      </div>
      <div class="flex justify-between text-28">
        <div>
          <div class="text-grey">{{ $t('购买价') }}( {{ routeType == 'cryptos' ? 'USDT' : 'USD' }})</div>
          <div class="mt-5 textColor">{{ item.open_price }}</div>
        </div>
        <div>
          <div class="text-grey text-right">{{ $t('现价') }}</div>
          <div class="mt-5" :class="item.close_price > item.open_price ? 'text-green' : 'text-red'">
            {{ item.close_price }}</div>
        </div>
      </div>
      <div class="flex pt-10 pb-10 text-28">
        <div class="flex-1">
          <div class="text-grey">{{ $t('方向') }}</div>
          <div class="mt-5" :class="item.direction === 'buy' ? 'text-green' : 'text-red'">{{ item.direction ===
            'buy' ? $t('开多') : $t('开空') }}</div>
        </div>
        <div class="flex-1 text-28">
          <div class="text-grey text-center">{{ $t('数量') }}</div>
          <div class="mt-5 text-center textColor">{{ item.volume }}</div>
        </div>
        <div class="flex-1 flex flex-col items-end text-28">
          <div class="text-grey">{{ $t('盈亏') }}</div>
          <div class="mt-5" :class="item.profit / 1 > 0 ? 'text-green' : 'text-red'">
            {{ item.profit / 1 > 0 ? '+' + item.profit : item.profit }}
          </div>
        </div>
      </div>
      <div class="flex pb-8 text-28">
        <div class="flex-1">
          <div class="text-grey">{{ $t('剩余时间') }}</div>
          <div class="mt-5 textColor">{{ fomatTime(item.remain_time) }}</div>
        </div>
        <div class="flex-1">
          <div class="text-grey  text-center">{{ $t('交割时间') }}</div>
          <div class="mt-5  text-center textColor">{{ item.time_num + item.time_unit }}</div>
        </div>
        <div class="flex-1">
          <div class="text-grey text-right">{{ $t('操作') }}</div>
          <div class="mt-5 colorMain text-right" @click="goDetail(item)">{{ $t('详情') }}</div>
        </div>
      </div>
    </div>
    <van-popup v-model:show="show">
      <popup-delivery :showBtns="true" :price="price" :detailData="detailData" :key="detailData.order_no"
        @close="onClose" />
    </van-popup>
  </div>
</template>

<script>
import PopupDelivery from '../popup-delivery/index.vue'
import { Popup } from 'vant';
import { _futrueOrderDetail } from '@/service/trade.api'
export default {
  name: "deliveryHoldList",
  data() {
    return {
      show: false,
      iconShow: false,
      detailData: {},
      price: '0.00',
      timer: null,
      routeType: 'cryptos'
    }
  },
  components: {
    [Popup.name]: Popup,
    PopupDelivery
  },
  props: {
    listData: {
      type: Array,
      default() {
        return []
      }
    }

  },
  mounted() {
    this.routeType = this.$route.query.type
  },
  methods: {
    fomatTime(time) {
      let arr = time.split(':')
      let day = Math.floor(arr[0] / 24)
      let hour = arr[0] % 24 >= 10 ? arr[0] % 24 : '0' + arr[0] % 24
      let min = arr[1] >= 10 ? arr[1] : '0' + arr[1]
      let sec = arr[2] >= 10 ? arr[2] : '0' + arr[2]
      if (day >= 1) {
        return day + this.$t('天') + ' ' + hour + ':' + min + ':' + sec
      } else {
        return hour + ':' + min + ':' + sec
      }
    },
    changeIcon() {
      this.iconShow = !this.iconShow;
    },
    goDetail(item) {
      this.detailData = item
      this.show = true
      this.price = item.close_price
      this.clearIntervalFun()
      // 当前数据是定时轮询地数据, 不必拿最新地数据了
      this.timer = setInterval(() => {
        _futrueOrderDetail(item.order_no).then(data => {
          this.detailData = data
          if (data.state === 'created') {
            this.clearIntervalFun()
          }
        })
      }, 1000);
      // this.$router.push({
      //     path:"/orderDetail?order_no=" + item.order_no
      // });
    },
    onClose() { // 关闭
      this.show = false
      this.clearIntervalFun()
    },
    clearIntervalFun() {
      clearInterval(this.timer)
    }
  }
}
</script>
<style lang="scss" scoped></style>
