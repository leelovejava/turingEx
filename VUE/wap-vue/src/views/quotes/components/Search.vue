<template>
  <div id="cryptos">
    <div class="tradeRecord">
      <van-loading color="#1194F7" class="loading-box" v-if="isLoading" />
      <div class="content">
        <van-tabs v-model:active="active" class="w-full px-2 box-border" sticky @click="changeType">
          <van-tab :title="$t('当前委托')" class="text-30">
            <!-- <div class="all-select flex justify-end">
              <div class="select-box flex" @click.stop="isAll = !isAll">
                <div class="flex-1">
                  {{ symbol.toUpperCase() }}/USDT
                </div>
                <van-icon name="arrow-down" />
                <div class="select-data" v-if="isAll">
                  <div class="select-item" @click.stop="selectItem(item)" v-for="(item, index) in currencyList"
                    :key="index">{{ item.symbol.toUpperCase() }}/USDT
                  </div>
                </div>
              </div>
            </div> -->
            <entrust-item v-for="item in entrustList" :key="item.order_no" :entrust="item" :state="item.state"
              @cancelOrder="cancelOrder" />
          </van-tab>
          <van-tab :title="$t('历史委托')">
            <!-- <div class="all-select flex justify-end">
              <div class="select-box flex" @click.stop="isAll = !isAll">
                <div class="flex-1">
                  {{ symbol.toUpperCase() }}/USDT
                </div>
                <van-icon name="arrow-down" />
                <div class="select-data" v-if="isAll">
                  <div class="select-item" @click.stop="selectItem(item)" v-for="(item, index) in currencyList"
                    :key="index">{{ item.symbol.toUpperCase() }}/USDT
                  </div>
                </div>
              </div>
            </div> -->
            <entrust-item v-for="item in entrustList" :key="item.order_no" :entrust="item" :state="item.state"
              @cancelOrder="cancelOrder" />
          </van-tab>
          <van-tab :title="$t('成交历史')" class="text-30">
            <!-- <div class="all-select flex justify-end">
              <div class="select-box flex" @click.stop="isAll = !isAll">
                <div class="flex-1">
                  {{ symbol.toUpperCase() }}/USDT
                </div>
                <van-icon name="arrow-down" />
                <div class="select-data" v-if="isAll">
                  <div class="select-item" @click.stop="selectItem(item)" v-for="(item, index) in currencyList"
                    :key="index">{{ item.symbol.toUpperCase() }}/USDT
                  </div>
                </div>
              </div>
            </div> -->
            <entrust-item v-for="item in entrustList" :key="item.order_no" :entrust="item" :state="item.state"
              @cancelOrder="cancelOrder" />
          </van-tab>
        </van-tabs>
        <div v-if="!entrustList.length" class="flex flex-col justify-center items-center pt-44">
          <img src="@/assets/image/assets-center/no-data.png" alt="no-date" class="w-20 h-20 no-data-img" />
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
import TradeApi from "@/service/trading.js";
import { mapGetters } from 'vuex'
// import { _getCoins, _getHomeList } from '@/Api/home.api'
import { useUserStore } from '@/store/user';

export default {
  props: {

  },
  computed: {
    ...mapGetters('user', ['userInfo']),
  },
  components: {
    assetsHead,
    [Tabs.name]: Tabs,
    [Tab.name]: Tab,
    EntrustItem
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
      coinPrice: 0,
      isLoading: false
    }
  },
  mounted() {
    const userStore = useUserStore()
    if (!userStore.userInfo.token) {
      this.$router.push('/login')
      return
    }
    // this.getCoins()
    this.symbol = this.$route.query.symbol
    this.active = this.$route.query.selectIndex !== undefined ? +this.$route.query.selectIndex : 0
    this.getOrderList(this.type)
    // this.getCoinPrce(this.symbol)
  },
  methods: {
    getCoins() {
      _getCoins().then((res) => {
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
      this.isLoading = true;
      TradeApi.tradeRecord({
        page_no: 1,
        symbol: this.symbol,
        type: type,
        symbolType: this.$route.query.type,
        isAll: true
      }).then(res => {
        this.isLoading = false;
        this.entrustList = res;
      }).catch(() => {
        this.isLoading = false;
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
        showToast(this.$t('success'));
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
  .loading-box {
    position: absolute;
    top: 200px;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 999;
  }

  .tradeRecord {
    box-sizing: border-box;
    padding: 0 5px;

    :deep(.van-tab) {
      color: $text_color1;
    }

    :deep(.van-tabs__nav) {
      background-color: $tab_background;
    }

    :deep(.van-tab--active) {
      color: $text_color;
    }

    :deep(.van-tabs__wrap) {
      height: 40px !important;
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
          ;
        }
      }
    }
  }
}
</style>
