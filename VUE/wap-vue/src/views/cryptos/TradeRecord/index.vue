<template>
  <div id="cryptos">
    <div class="tradeRecord">
      <assets-head :title="$t('交易记录')" />
      <div class="content">
        <van-tabs v-model:active="active" class="w-full px-5 box-border" sticky @click="changeType">
          <van-tab :title="$t('当前委托')" class="text-30">
            <div class="all-select flex justify-end text-28">
              <div class="select-box flex" @click.stop="isAll = !isAll">
                <div class="flex-1">
                  {{ symbol.toUpperCase() }}
                </div>
                <van-icon name="arrow-down" />
                <div class="select-data" v-if="isAll">
                  <div class="select-item" @click.stop="selectItem(item)" v-for="(item, index) in currencyList"
                    :key="index">{{ item.symbol.toUpperCase() }}
                  </div>
                </div>
              </div>
            </div>
            <entrust-item v-for="item in entrustList" :key="item.order_no" :entrust="item" :state="item.state"
              @cancelOrder="cancelOrder" />
          </van-tab>
          <van-tab :title="$t('历史委托')">
            <div class="all-select flex justify-end text-28">
              <div class="select-box flex" @click.stop="isAll = !isAll">
                <div class="flex-1 text-30 ">
                  {{ symbol.toUpperCase() }}
                </div>
                <van-icon name="arrow-down" />
                <div class="select-data" v-if="isAll">
                  <div class="select-item" @click.stop="selectItem(item)" v-for="(item, index) in currencyList"
                    :key="index">{{ item.symbol.toUpperCase() }}
                  </div>
                </div>
              </div>
            </div>
            <history-item unit="USDT" :coinPrice="coinPrice" v-for="item in entrustList" :key="item.order_no"
              :entrust="item" :state="item.state" @cancelOrder="cancelOrder" />
          </van-tab>
          <van-tab :title="$t('成交历史')" class="text-30">
            <div class="all-select flex justify-end text-28">
              <div class="select-box flex" @click.stop="isAll = !isAll">
                <div class="flex-1">
                  {{ symbol.toUpperCase() }}
                </div>
                <van-icon name="arrow-down" />
                <div class="select-data" v-if="isAll">
                  <div class="select-item" @click.stop="selectItem(item)" v-for="(item, index) in currencyList"
                    :key="index">{{ item.symbol.toUpperCase() }}
                  </div>
                </div>
              </div>
            </div>
            <entrust-item v-for="item in entrustList" :key="item.order_no" :entrust="item" :state="item.state"
              @cancelOrder="cancelOrder" />
          </van-tab>
        </van-tabs>
        <div v-if="!entrustList.length" class="flex flex-col justify-center items-center pt-40">
          <img src="@/assets/image/assets-center/no-data.png" alt="no-date" class="w-24 h-24 no-data-img" />
          <p class="textColor">{{ $t('暂无数据') }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Tabs, Tab, showToast } from 'vant';
import assetsHead from "@/components/Transform/assets-head/index.vue";
import EntrustItem from '@/components/Transform/entrust-item/index.vue'
import historyItem from '@/components/Transform/history-item/index.vue'
import TradeApi from "@/service/trading.js";
import { _getCoins, _getHomeList } from '@/service/home.api'
export default {
  props: {

  },
  components: {
    assetsHead,
    [Tabs.name]: Tabs,
    [Tab.name]: Tab,
    EntrustItem,
    historyItem
  },
  data() {
    return {
      active: 0,
      entrustList: [],
      historyList: [],
      finishList: [],
      type: 'orders',
      isAll: false,
      symbol: '',
      currencyList: [],
      coinPrice: 0
    }
  },
  mounted() {
    this.getCoins()
    this.symbol = this.$route.params.symbol
    this.getOrderList(this.type)
    this.getCoinPrce(this.symbol)
  },
  methods: {
    getCoins() {
      _getCoins({ type: this.$route.query.type }).then((res) => {
        console.log(res)
        this.currencyList = res
      })
    },
    getCoinPrce(val) {
      _getHomeList(val).then((res) => {
        this.coinPrice = res[0].close
      })
    },
    getOrderList(type) {
      TradeApi.tradeRecord({
        page_no: 1,
        symbol: this.symbol,
        type: type
      }).then(res => {
        console.log(res, 2222)
        this.entrustList = res;
      }).catch(() => {

      })
    },
    changeType() {
      this.isAll = false
      this.entrustList = [];
      if (this.active == 0) {
        this.type = 'orders'
      } else if (this.active == 1) {
        this.type = 'hisorders'
      } else {
        this.type = 'opponent'
      }
      this.getOrderList(this.type);
    },
    cancelOrder(order) {
      TradeApi.tradeCancel({
        order_no: order,
      }).then(res => {
        showToast(this.$t('成功'));
        this.getOrderList(this.type)
      }).catch(() => {

      })
    },
    //选择币种
    selectItem(item) {
      this.symbol = item.symbol
      this.getOrderList()
      this.getCoinPrce(item.symbol)
      this.isAll = false
    }
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  .tradeRecord {
    padding: 0 20px;
    box-sizing: border-box;

    :deep(.van-tab) {
      color: $text_color1;
    }

    :deep(.van-tabs__nav) {
      background-color: $selectSymbol_background;
    }

    :deep(.van-tab--active) {
      background: $btn_main;
      border-radius: 5px;
      color: $white !important;
    }

    :deep(.van-tabs__wrap) {
      // height: 88px !important;
    }

    :deep(.van-tabs__line) {
      bottom: 10px;
    }
  }

  .listItem {
    border-bottom: 1px solid #EAEBEE;
    display: flex;
    justify-content: space-between;
  }

  .all-select {
    padding: 30px 0;

    .select-box {
      height: 70px;
      background: $selectSymbol_background;
      color: $text_color6;
      display: flex;
      align-items: center;
      font-size: 28px;
      padding: 0 20px;
      position: relative;
      border: 1px solid $border_color;
      min-width: 260px;

      .select-data {
        position: absolute;
        top: 75px;
        left: 0;
        z-index: 10;
        width: 100%;
        height: 300px;
        overflow-y: auto;

        background: $grey_bg;


        .select-item {
          padding: 20px 20px;
          text-align: center;

          border-bottom: 1px solid $border_color;
          font-size: 30px;
        }
      }
    }
  }
}
</style>
