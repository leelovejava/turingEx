<template>
  <div id="cryptos">
    <div class="DeliveryContractHistory px-2 text-28">
      <assets-head :title="routeType == 'cryptos' ? $t('交割合约历史') : $t('期货交易')" :backFunc="() => {
        if ($route.query.goback) {
          $router.push({
            path: '/cryptos/funds',
            query: {
              tab: 2,
              index: 1,// 0: 查看理财订单 1: 矿机
              type: 'cryptos'
            }
          })
        } else {
          $router.go(-1)
        }

      }" />
      <div>
        <van-tabs ref="tabs" v-model:active="type" swipeable @change="onChange">
          <van-tab :title="item.title" v-for="item in selectData" :key="item.title" :name="item.type">

          </van-tab>
        </van-tabs>
        <div class="all-select flex justify-end text-28" v-if="symbol">
          <div class="select-box flex" @click.stop="isAll = !isAll">
            <div class="flex-1 text-28">
              {{ routeType == 'indices' ? symbolETF : symbol.toUpperCase() }}
            </div>
            <van-icon name="arrow-down" />
            <div class="select-data" v-if="isAll">
              <div class="select-item" @click.stop="selectItem(item2)" v-for="(item2, index) in currencyList"
                :key="index">{{ routeType == 'indices' ? item2.name : item2.symbol.toUpperCase() || item2.symbol }}</div>
            </div>
          </div>
        </div>
        <template v-if="type === 'orders'">
          <van-list v-model:loading="loading" :loading-text="$t('加载中...')" :finished="finished"
            :finished-text="dataList.orders.length ? $t('已经全部加载完毕') : ''" @load="onLoad" :offset="30" class="px-5">
            <futrue-hold-list v-if="type === 'orders'" :list-data="dataList.orders">
            </futrue-hold-list>
            <div class="flex flex-col justify-center pt-10 pb-5 items-center" v-if="!dataList.orders.length && !loading">
              <img src="@/assets/image/assets-center/no-data.png" alt="" class="w-24 h-24 no-data-img" />
              <p class="text-grey mt-4">{{ $t('暂无记录') }}</p>
            </div>
          </van-list>
        </template>

        <template v-if="type === 'hisorders'">
          <van-list v-model:loading="loading" :loading-text="$t('加载中...')" :finished="finished"
            :finished-text="dataList.hisorders.length ? $t('已经全部加载完毕') : ''" @load="onLoad" :offset="30" class="px-5">
            <futrue-histroy-position :list-data="dataList.hisorders"></futrue-histroy-position>
            <div class="flex flex-col justify-center pt-10 pb-5 items-center"
              v-if="!dataList.hisorders.length && !loading">
              <img src="@/assets/image/assets-center/no-data.png" alt="" class="w-100 h-100 no-data-img" />
              <p class="text-grey mt-2 text-28">{{ $t('暂无记录') }}</p>
            </div>
          </van-list>
        </template>
      </div>
    </div>
  </div>
</template>
<script>
import { List, Tab, Tabs } from 'vant';
import futrueHoldList from '@/components/Transform/deliveryContract/hold.vue'
import futrueHistroyPosition from '@/components/Transform/deliveryContract/position.vue'
import { _futrueOrderList } from "@/service/trade.api";
import assetsHead from "@/components/Transform/assets-head/index.vue";
import { _getCoins } from "@/service/home.api";
export default {
  data() {
    return {
      type: "orders",
      dataList: {
        orders: [],
        hisorders: []
      },
      symbol: '',
      selectData: [
        { title: this.$t('当前委托'), type: 'orders' },
        { title: this.$t('历史仓位'), type: 'hisorders' },
      ],
      loading: false,
      finished: false,
      page: 1,
      isAll: false,
      currencyList: [],
      symbolETF: '',
      routeType: 'cryptos',
    }
  },
  components: {
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    [List.name]: List,
    futrueHoldList,
    futrueHistroyPosition,
    assetsHead
  },
  mounted() {
    this.getCoins()
    this.symbol = this.$route.query.symbol
    if (this.$route.query.type) {
      this.routeType = this.$route.query.type
    }
  },
  methods: {
    getCoins() {
      _getCoins({ type: this.$route.query.type }).then((res) => {
        console.log(res, 111111)
        this.currencyList = res
        this.currencyList.map((item) => {
          if (item.symbol == this.symbol) {
            this.symbolETF = item.name
          }
        })
      })
    },
    onChange(e) {
      this.isAll = false
      this.dataList[e] = []
      this.finished = false
      this.page = 1
      this.type = e
      this.loading = true;
      if (this.loading) {
        this.fetchData(this.symbol, this.type)
      }
    },
    onClickLeft() {
      this.$router.go(-1)
    },
    fetchData(symbol) {
      // TODO: 分页
      _futrueOrderList(symbol, this.type, this.page).then(data => {
        this.dataList[this.type] = this.dataList[this.type].concat(data)
        this.loading = false
        if (data.length < 10) {
          this.finished = true
        }
        this.page++
      })
    },
    onLoad() {
      this.dataList.orders = []
      this.fetchData(this.symbol, this.type)
    },
    //选择币种
    selectItem(item) {
      this.page = 1
      this.symbol = item.symbol
      this.dataList[this.type] = []
      this.fetchData(this.symbol, this.type)
      this.isAll = false
    }
  },
  beforeRouteEnter(to, from, next) {
    const { query: { symbol } } = to
    next(vm => {
      vm.symbol = symbol
    })
  }
}
</script>
<style lang="scss" scoped>
#cryptos {
  :deep(.van-tab--active) {
    background: $btn_main;
    border-radius: 5px;
    color: $white !important;
  }

  :deep(.van-tabs__nav) {
    background-color: $tab_background;
  }


  .DeliveryContractHistory {
    width: 100%;
    box-sizing: border-box;

    .position-padding {
      //border-bottom: 1px solid #F2F4F9;
      padding: 33px;
      box-sizing: border-box;
      width: 100%;
      float: left;

      .position-tag {
        display: flex;
        align-items: center;

        .position-tag-style {
          padding: 11px 28px;
          margin-right: 22px;
          font-style: normal;
          font-weight: 400;
          font-size: 26.4981px;
          color: $main-btn-color;
        }

        .position-tag-title {
          font-style: normal;
          font-weight: 600;
          font-size: 30.9145px;
          margin-right: 13px;
        }

        .position-tag-title2 {
          font-style: normal;
          font-weight: 400;
          font-size: 30.9145px;
          color: $text_color5;
        }

        .position-tag-img {
          margin-left: 11px;
          width: 40px;
          height: 31px;
        }
      }

      .red {
        background: $red;
        border-radius: 6.6266px;
      }

      .green {
        background: $green;
        border-radius: 6.62453px;
      }

      .position-div1 {
        margin-top: 20px !important;
        width: 100%;
        float: left;

        .position-text1 {
          float: left;
          font-style: normal;
          font-weight: 400;
          font-size: 30px;
          color: $text_color5;
        }

        .position-text2 {
          float: right;
          font-style: normal;
          font-weight: 600;
          font-size: 30px;
        }

        .position-text3 {
          font-style: normal;
          font-weight: 400;
          font-size: 30px;
        }
      }
    }

    .color-red {
      color: $red !important;
    }

    .color-green {
      color: $green !important;
    }

    .color-blue {
      color: $color_main !important;
    }

    .van-tabs__nav {
      background: $tab_background;
    }

    .van-tab--active {
      color: $white;
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
          }
        }
      }
    }
  }
}
</style>
