<template>
  <!-- 委托列表 -->
  <div id="cryptos">
    <template v-if="listData.length > 0">
      <div class="border-b-color" v-for="item in listData" :key="item.order_no">
        <div class="flex justify-between pt-8 pb-5">
          <div class="flex flex-col">
            <div class="flex items-center">
              <div class="text-32" :class="item.direction == 'buy' ? 'text-green' : 'text-red'">
                {{ handleWord(item.direction, item.offset, item.order_price_type) }}
              </div>
              <div class="ml-10 text-32 font-semibold textColor">{{ item.name }}</div>
            </div>
          </div>
          <div class="text-grey text-26 flex flex-col justify-between">
            {{ item.create_time }}
          </div>
        </div>
        <div class="flex justify-between pb-8">
          <div class="flex items-center flex-between">
            <div>
              <van-circle v-model:current-rate="currentRate" :rate="(1 - item.volume / item.volume_open * 100)"
                :speed="100" :text="text" :layer-color="'#EAEBEF'" :stroke-width="60" :size="'65px'" />
            </div>
            <div class="ml-10">
              <div class="flex items-center text-26">
                <div class="text-grey">
                  <div>{{ $t('数量') }}</div>
                  <!-- <di(USDT)v></div> -->
                </div>
                <div class="ml-6 textColor">{{ item.volume_open - item.volume }}/{{ item.volume_open }}</div>
              </div>
              <div class="flex mt-2 items-center text-26">
                <div class="text-grey">{{ $t('价格') }}</div>
                <div class="ml-6 textColor">{{ item.price }}</div>
              </div>
            </div>
          </div>
          <div class="btn-wrap mt-16">
            <button class="order-btn colorMain borderMain  detailBtn  greyBg" @click.stop="goDetail(item)">
              {{ $t('详情') }}</button>
            <button class="ml-5 order-btn border-none greyBg textColor" @click.stop="cancelSingle(item.order_no)">
              {{ $t('撤单') }}</button>
          </div>
        </div>
      </div>
    </template>
    <div class="text-grey text-center py-20 text-30" v-if="!listData.length">{{ $t('还没有委托') }}</div>
  </div>
</template>

<script>
import { Circle, showToast } from "vant";
import { _contractApplyOrderCancel, _recallOrderBatch } from "@/service/trade.api.js";
export default {
  name: "perpetualEntrustList",
  components: {
    [Circle.name]: Circle
  },
  props: {
    listData: {
      type: Array,
      default() {
        return []
      }
    }
  },
  data() {
    return {
      currentRate: 0,
      //委托列表
      //    entrustData:[
      //     { name:"BTC/USDT",create_time:"2022-04-05 22：10：31",direction:"buy",amount:"200",price:"23000"},
      //     { name:"ETH/USDT",create_time:"2022-04-05 22：10：31",direction:"sell",amount:"30",price:"1500"},
      //    ],
      iconShow: false,
    }
  },
  computed: {
    text() {
      return this.currentRate.toFixed(0) + '%';
    },
  },
  mounted() {
  },
  methods: {
    handleWord(direction, offset, price_type) {
      let a = ''
      let b = ''
      if (price_type === 'limit') {
        a = this.$t('限价')
      } else {
        a = this.$t('市价')
      }
      if (direction === 'buy' && offset === 'open') {
        b = this.$t('开多')
      } else if (direction === 'sell' && offset === 'open') {
        b = this.$t('开空')
      } else if (direction === 'buy' && offset === 'close') {
        b = this.$t('平多')
      } else {
        b = this.$t('平空')
      }
      return a + '/' + b
    },
    changeIcon() {
      this.iconShow = !this.iconShow;
    },
    goDetail(item) {
      this.$router.push({
        path: "/cryptos/entrustDetail", query: { order_no: item.order_no }
      });
    },
    cancelSingle(order_no) {//撤销单个委托单
      _contractApplyOrderCancel({ order_no }).then(() => {
        showToast(this.$t('撤单成功'))
        this.$emit('recall', order_no)
      })
    },
    recallAll() { // 全部撤销
      _recallOrderBatch().then(() => {
        showToast(this.$t('撤单成功'))
        this.$emit('recall')
      })
    }
  }
}
</script>
<style lang="scss" scoped>
#cryptos {
  .btn-wrap {
    display: flex;
    justify-content: center;
    align-content: center;


  }

  .detailBtn {
    background: $US_tabActice_background;
    color: $color_main;
  }

  .order-btn {
    font-size: 26px;
    border-radius: 6px;
    height: 53px;
    line-height: 53px;
    padding: 0 20px;
    display: flex;
    align-content: center;
    justify-content: center;
  }

  .van-circle__text {
    color: $text_color;
  }
}
</style>
