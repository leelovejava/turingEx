<template>
  <!-- 持有仓位列表 -->
  <div id="cryptos">
    <!--        <div class="flex justify-between" v-if="listData.length">-->
    <!--            <div class="flex" @click.stop="changeIcon">-->
    <!--                <img v-show="!iconShow" src="../../assets/image/public/grey-rounded.png" alt="" class="w-38 h-38"/>-->
    <!--                <img v-show="iconShow" src="../../assets/image/public/blue-rounded.png" alt="" class="w-38 h-38"/>-->
    <!--                <div class="ml-37xw">{{ $t('隐藏其它合约') }}</div>-->
    <!--            </div>-->
    <!--            <button class="border-none all-cancel-btn pl-34 pr-34 pt-10 pb-10 text-26" @click="onSellAll">{{ $t('一键平仓') }}</button>-->
    <!--        </div>-->
    <div class="border-b-color" v-for="item in listData" :key="item.order_no">
      <div class="flex justify-between pt-11 pb-11">
        <div class="flex flex-col">
          <div class="flex items-center">
            <div class="pl-7 pr-7 pt-3 pb-3 text-white open-btn text-28"
              :class="item.direction == 'buy' ? ' bg-green' : 'bg-red'">
              {{ item.direction == 'buy' ? $t('开多') : $t('开空') }}
            </div>
            <div class="ml-5 text-32 font-semibold ">
              <span class="textColor">{{ item.name }} {{ $t('永续') }}</span>
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
          <div class="text-grey">{{ routeType == 'cryptos' ? $t('未实现盈亏（USDT）') : $t('未实现盈亏（USD）') }}</div>
          <div class="mt-5" :class="item.profit > 0 ? 'text-green' : 'text-red'">
            {{ item.profit > 0 ? '+' + item.profit.toFixed(4) : item.profit.toFixed(4) }}</div>
        </div>
        <div>
          <div class="text-grey">ROE</div>
          <div class="mt-5" :class="item.change_ratio / 1 > 0 ? 'text-green' : 'text-red'">{{ item.change_ratio
          }}%
          </div>
        </div>
      </div>
      <div class="flex pt-11 pb-8 text-28">
        <div class="flex-1">
          <div class="text-grey">{{ $t('持仓数量') }}</div>
          <div class="mt-5 textColor">{{ item.volume / (item.lever_rate ? item.lever_rate : 1) }}*{{
            item.lever_rate ? item.lever_rate : 1 }}x</div>
        </div>
        <div class="flex-1 text-center  text-28">
          <div class="text-grey">{{ $t('保证金') }} ( {{ routeType == 'cryptos' ? 'USDT' : 'USD' }})</div>
          <div class="mt-5 textColor">{{ item.deposit }}</div>
        </div>
        <div class="flex-1 flex flex-col items-end  text-28">
          <div class="text-grey">{{ $t('开仓价格') }}</div>
          <div class="mt-5 textColor">{{ item.trade_avg_price }}</div>
        </div>
      </div>
      <div class="flex pb-8 text-28">
        <div class="flex-1">
          <div class="text-grey">{{ $t('标记价格') }}</div>
          <div class="mt-5 textColor">{{ item.mark_price }}</div>
        </div>
        <div class="flex-1 flex flex-col items-center justify-end">
          <button class="text-30 detail-btn border-light-blue greyBg colorMain w-32 h-16" @click="goDetail(item)">{{
            $t('详情') }}</button>
        </div>
        <div class="flex-1 flex flex-col items-end justify-end">
          <button class="greyBg textColor border-none pl-8 pr-8 pt-2 pb-2 rounded" @click="onSell(item.order_no)">
            {{ $t('平仓') }}</button>
        </div>
      </div>
    </div>
    <div class="text-grey text-center py-72 text-30" v-if="!listData.length">{{ $t('您目前没有持仓') }}</div>
  </div>
</template>

<script>
import { _orderSellBatch, _contractOrderClose } from "@/service/trade.api";
import { showConfirmDialog, showToast } from 'vant';
//import { i18n } from "@/i18n";
export default {
  name: "perpetualPositionList",
  data() {
    return {
      iconShow: false,
      routeType: 'cryptos'
    }
  },
  props: {
    type: {
      type: String,
      default: '2' // 2 永续合约历史持仓，3交割合约持仓
    },
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
    changeIcon() {
      this.iconShow = !this.iconShow;
    },
    goDetail(item) {
      this.$router.push({ path: "/cryptos/orderDetail", query: { order_no: item.order_no } });
    },
    onSell(order_no) { // 平仓单个
      showConfirmDialog({
        confirmButtonText: this.$t('确定'),
        cancelButtonText: this.$t('取消'),
        title: this.$t('平仓提示'),
        message: this.$t('是否平仓?'),
      }).then(() => {
        _contractOrderClose({ order_no }).then(() => {
          showToast(this.$t('平仓成功'))
          this.$emit('sell', order_no)
        })
      }).catch(() => { });
    },
    onSellAll() { // 平仓全部
      _orderSellBatch().then(() => {
        this.showToast(this.$t('平仓成功'))
        this.$emit('sell')
      })
    }
  }
}
</script>
<style lang="scss" scoped>
#cryptos {
  .open-btn {
    border-radius: 7px;
  }

  .detail-btn {
    border: 1px solid;
    border-radius: 7px;
    background: $US_tabActice_background;
    color: $color_main;
  }
}
</style>
