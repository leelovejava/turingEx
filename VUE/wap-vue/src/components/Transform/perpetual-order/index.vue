<template>
  <!-- 永续合约订单列表页 -->
  <div id="cryptos">
    <div class="items-center mt-4">
      <div class="flex justify-between border-b-color">
        <div class="flex ">
          <template v-if="topIndex / 1 === 1">
            <div class="px-2 py-2 flex  items-center textColor1 text-28" @click="tabClick('1')"
              :class="type == '1' ? 'active-line' : ''">{{ $t('持有仓位') }}<span v-if="type == '1'">({{
                orderHold.length }})</span></div>
            <div class="px-2 ml-12 py-2 flex  items-center textColor1y text-28" @click="tabClick('2')"
              :class="type == '2' ? 'active-line' : ''">{{ $t('当前委托') }}</div>
          </template>
          <template v-else>
            <div class="px-2 py-2 flex  items-center textColor1 text-28" @click="tabClick('3')"
              :class="type == '3' ? 'active-line' : ''">{{ $t('持有仓位') }}<span v-if="type == '2'">({{
                orderCur.length
              }})</span></div>
            <div class="px-2 ml-12 py-2 flex  items-center textColor1 text-28" @click="tabClick('4')"
              :class="type == '4' ? 'active-line' : ''">{{ $t('历史仓位') }}</div>
          </template>
        </div>
        <img src="../../../assets/image/public/record.png" alt="record-img" class="w-16 h-9 pr-8 record-img"
          @click="goHistory" />
      </div>
      <!-- 永续-->
      <template v-if="topIndex / 1 === 1">
        <!-- 委托列表 -->
        <div v-if="type == '1'">
          <PerpetualPositionList :list-data="orderHold" @sell="$emit('recall', $event)"></PerpetualPositionList>
        </div>
        <!-- 持有仓位列表 -->
        <div v-if="type == '2'">
          <PerpetualEntrustList :list-data="orderCur" @recall="$emit('recall', $event)"></PerpetualEntrustList>
        </div>
      </template>
      <!-- 交割-->
      <template v-else>
        <div v-if="type == '3'">
          <futrue-hold-list :price="price" :list-data="futrueHold" />
          <div class="text-grey text-center py-72 text-30" v-if="futrueHold.length == 0">{{ $t('您目前没有持仓') }}</div>
        </div>
        <div v-if="type == '4'">
          <futrue-histroy-position :price="price" :list-data="futrueHistroy" />
          <div class="text-grey text-center py-72 text-30" v-if="futrueHistroy.length == 0">{{ $t('您目前没有持仓') }}</div>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import PerpetualEntrustList from '../perpetual-entrust-list/index.vue';
import PerpetualPositionList from '../perpetual-position-list/index.vue';
import futrueHoldList from '../deliveryContract/hold.vue'
import futrueHistroyPosition from '../deliveryContract/position.vue'
import { mapGetters } from 'vuex'

export default {
  name: "perpetualOrder",
  data() {
    return {
      type: '1', // 1：永续持有，2：永续当前，3：交割持有，4：交割历史
    };
  },
  props: {
    topIndex: {
      type: [Number, String],
      default: 1
    },
    symbol: {
      type: String,
      default: ''
    },
    orderCur: { //
      type: Array,
      default() {
        return []
      }
    },
    orderHold: {
      type: Array,
      default() {
        return []
      }
    },
    futrueHold: {
      type: Array,
      default() {
        return []
      }
    },
    futrueHistroy: {
      type: Array,
      default() {
        return []
      }
    },
    price: {
      type: [Number, String],
      default: '0.00'
    },
  },
  watch: {
    topIndex(val) {
      if (val / 1 === 1) {
        this.type = '1'
        this.tabClick('1')
      } else {
        this.type = '3'
        this.tabClick('3')
      }
    }
  },
  mounted() {
    if (this.topIndex == 1) {
      this.type = '1'
      this.tabClick('1')
    } else {
      this.type = '3'
      this.tabClick('3')
    }
  },
  activated() {
    if (this.topIndex == 1) {
      this.type = '1'
      this.tabClick('1')
    } else {
      this.type = '3'
      this.tabClick('3')
    }
  },
  methods: {
    tabClick(type) {
      this.type = type;
      if (type == '1') { //  && !this.orderCur.length
        //this.$emit('tab', 'fetchOrderListCur')
        this.$emit('tab', 'fetchOrderListHold')
      }
      if (type === '2') { //  && !this.orderHold.length
        //this.$emit('tab', 'fetchOrderListHold')
        this.$emit('tab', 'fetchOrderListCur')
      }
      if (type === '3') {
        this.$emit('tab', 'fetchFutrueHoldList')
      }
      if (type === '4') {
        this.$emit('tab', 'fetchFutrueHistory')
      }
      // this.$emit(type)
    },
    goHistory() {
      if (this.userInfo.token) {
        let type = 'cryptos'
        if (this.$route.query.type) {
          type = this.$route.query.type
        }
        const url = this.topIndex / 1 === 1 ? '/cryptos/perpetualHistory' : '/cryptos/deliveryContractHistory'
        this.$router.push({
          path: url, query: { symbol: this.symbol, type: type }
        });
      } else {
        this.$router.push('/login')
      }

    }
  },
  components: { PerpetualEntrustList, PerpetualPositionList, futrueHistroyPosition, futrueHoldList },
  computed: {
    ...mapGetters('user', ['userInfo']),
  },
}

</script>

<style lang="scss" scoped>
#cryptos {
  .all-cancel-btn {
    background-color: #EAEBEF;
  }

  .cancel-btn {
    // background-color: #EAEBEF;
  }

  .active-line {
    // position: relative;
    // padding-bottom: 16px;
    // color: $text-color;
    position: relative;
    // padding: 15px 0;
    color: $white !important;
    border-radius: 8px;
    background-color: $color_main
  }

  // .active-line::after {
  //   content: '';
  //   position: absolute;
  //   left: 0px;
  //   bottom: 0;
  //   right: 0;
  //   width: 280px;
  //   height: 4px;
  //   background-color: $btn_main;
  // }

  .record-img {
    margin-top: 10px;
  }

  .textColor1 {
    color: $text_color;
  }
}
</style>
