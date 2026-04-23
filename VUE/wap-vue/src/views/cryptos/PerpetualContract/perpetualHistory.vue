<template>
  <!-- U本位合约历史 -->
  <div id="cryptos">
    <div class="perpetualHistory px-2 text-28">
      <assets-head :title="$t('U本位合约历史')" :backFunc="() => {
        if ($route.query.goback) {
          $router.push({
            path: '/cryptos/funds',
            query: {
              tab: 2,
              index: 0, // 0: 查看理财订单 1: 矿机
              type: 'cryptos'
            }
          })
        } else {
          //$router.push(`/cryptos/perpetualContract/${symbol}?selectIndex=1`)
          if (this.$route.query?.from === 'trade') {
            this.$router.push('/trade/index?tabActive=1')
          } else {
            $router.go(-1)
          }
        }
      }" />
      <div>
        <van-tabs ref="tabs" v-model:active="type" swipeable @change="onChange" sticky>
          <van-tab :title="item.title" v-for="item in selectData" :key="item.title" :name="item.type">
            <div class="all-select flex justify-end text-28" v-if="symbol">
              <div class="select-box flex" @click.stop="isAll = !isAll">
                <div class="flex-1 text-28">
                  {{ CurrencyType == 'indices' ? symbolETF : symbol == 'all' ? $t('全部') : symbol.toUpperCase() }}
                </div>
                <van-icon name="arrow-down" />
                <div class="select-data" v-if="isAll">
                  <div class="select-item" @click.stop="selectItem(item2)" v-for="(item2, index) in currencyList"
                    :key="index">{{ CurrencyType == 'indices' ? item2.name : item2.symbol == 'all' ?
                      $t('全部') : item2.symbol.toUpperCase() }}
                  </div>
                </div>
              </div>
            </div>
            <template v-if="type === 'orders'">
              <van-list v-model:loading="loading" :loading-text="$t('加载中...')" :finished="finished"
                :finished-text="dataList.orders.length ? $t('已经全部加载完毕') : ''" @load="onLoad" :offset="30" class="px-5">
                <PerpetualEntrustList v-if="type === 'orders'" :list-data="dataList.orders" @recall="recall">
                </PerpetualEntrustList>
                <div class="flex flex-col justify-center pt-12 pb-5 items-center"
                  v-if="!dataList.orders.length && !loading">
                  <img src="@/assets/image/assets-center/no-data.png" alt="" class="w-24 h-24 no-data-img" />
                  <p class="text-grey mt-5">{{ $t('暂无记录') }}</p>
                </div>
              </van-list>
            </template>

            <template v-if="type === 'hisorders'">
              <van-list v-model:loading="loading" :loading-text="$t('加载中...')" :finished="finished"
                :finished-text="dataList.hisorders.length ? $t('已经全部加载完毕') : ''" @load="onLoad" :offset="30" class="px-5">
                <PerpetualHistoryPosition :list-data="dataList.hisorders"></PerpetualHistoryPosition>
                <div class="flex flex-col justify-center pt-12 pb-5 items-center"
                  v-if="!dataList.hisorders.length && !loading">
                  <img src="@/assets/image/assets-center/no-data.png" alt="" class="w-24 h-24 no-data-img" />
                  <p class="text-grey mt-5">{{ $t('暂无记录') }}</p>
                </div>
              </van-list>
            </template>
          </van-tab>
        </van-tabs>
      </div>
    </div>
  </div>
</template>

<script>
import assetsHead from "@/components/Transform/assets-head/index.vue";
import PerpetualEntrustList from '@/components/Transform/perpetual-entrust-list/index.vue';
import PerpetualHistoryPosition from '@/components/Transform/perpetual-history-position/index.vue';
import { contractOrder, _contractApplyOrderList } from "@/service/trade.api";
import { List, Tab, Tabs } from 'vant';
import { _getCoins } from '@/service/home.api'
import { setSessionStorage, getSessionStorage } from '@/utils/utis'
export default {
  data() {
    return {
      type: "orders",
      dataList: {
        orders: [],
        hisorders: []
      },
      symbol: 'all',
      isAll: false,
      selectData: [
        { title: this.$t('当前委托'), type: 'orders' },
        { title: this.$t('历史仓位'), type: 'hisorders' },
      ],
      loading: false,
      finished: false,
      page: 1,
      currencyList: [],
      symbolETF: '',
      CurrencyType: ''

    };
  },
  mounted() {
    if (getSessionStorage('cryptoHistoryType')) {
      this.type = getSessionStorage('cryptoHistoryType')
    }
    //this.symbol = this.$route.query.symbol
    this.CurrencyType = this.$route.query.type
    this.getCoins()
  },
  methods: {
    getCoins() {
      _getCoins({ type: this.$route.query.type }).then((res) => {
        console.log(res)
        this.currencyList = res
        this.currencyList.map((item) => {
          if (item.symbol == this.symbol) {
            this.symbolETF = item.name
          }
        })
        this.currencyList.unshift({
          symbol: 'all'
        })
      })
    },
    onChange(e) {
      this.dataList[e] = []
      this.finished = false
      this.page = 1
      this.type = e
      this.loading = true;
      this.isAll = false
      setSessionStorage('cryptoHistoryType', this.type)
      if (this.loading) {
        this.fetchList(this.symbol)
      }
    },
    onClickLeft() {
      this.$router.go(-1);
    },
    async fetchList(symbol) {
      const _api = this.type === 'orders' ? _contractApplyOrderList : contractOrder
      const type = this.type
      _api({
        symbol: symbol == 'all' ? '' : symbol,
        type,
        page_no: this.page,
        symbolType: this.CurrencyType
      }).then(data => {
        this.dataList[type] = this.dataList[type].concat(data)
        this.loading = false
        if (data.length < 10) {
          this.finished = true
        }
        this.page++
      })
    },
    recall() {
      this.page = 1;
      this.dataList.orders = []
      this.fetchList(this.symbol)
    },
    onLoad() {
      this.dataList.orders = []
      this.fetchList(this.symbol)
    },
    //选择币种
    selectItem(item) {
      this.dataList[this.type] = []
      this.page = 1
      this.symbol = item.symbol
      this.fetchList(this.symbol)
      this.isAll = false
      this.symbolETF = item.name
    }
  },
  components: {
    PerpetualEntrustList,
    PerpetualHistoryPosition,
    assetsHead,
    [List.name]: List,
    [Tabs.name]: Tabs,
    [Tab.name]: Tab
  },
}

</script>
<style lang="scss" scoped>
#cryptos {


  :deep(.van-tabs__nav) {
    background-color: $tab_background;
  }

  :deep(.van-tab--active) {
    background: $btn_main;
    border-radius: 5px;
    color: $white !important;
  }

  .perpetualHistory {
    width: 100%;
    box-sizing: border-box;
  }

  .active-line {
    position: relative;
    padding-bottom: 30px;
    color: $white !important;
  }

  .active-line::after {
    content: '';
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    bottom: 0;
    right: 0;
    width: 280px;
    height: 8px;
    background-color: $color_main;
  }

  .all-select {
    padding: 30px 0;
    margin-right: 20px;

    .select-box {
      height: 70px;
      background: $selectSymbol_background;
      color: $text_color6;
      display: flex;
      align-items: center;
      font-size: 14px;
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
        height: 500px;
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
