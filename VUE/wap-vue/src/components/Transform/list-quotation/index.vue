<template>
  <div class="list-quatation">
    <ex-tabs @tabs="onTabs"></ex-tabs>
    <van-list>
      <van-cell>
        <div class="flex items-center w-full text-grey text-26">
          <p class="left text-left">
            <span>{{ $t('交易对') }}
              <!--                      <img src="./icon-sort.png" alt="icon" class="w-13 h-22"/>-->
            </span>
          </p>
          <p class="mid text-right">
            {{ $t('最新价') }}
            <!--                  <img src="./icon-sort.png" alt="icon" class="w-13 h-22 ml-5"/>-->
          </p>
          <p class="right text-right">
            {{ active == 3 ? $t('成交额') : $t('24h涨跌幅') }}
            <!--                  <img src="./icon-sort.png" alt="icon" class="w-13 h-22 ml-5"/>-->
          </p>
        </div>
      </van-cell>
      <transition-group :name="type" tag="div">
        <div v-if="active == 0" :key="active">
          <van-cell v-for="item in listData" :key="item.id">
            <ul class="flex justify-between w-full items-center" @click="onItemClick(item)">
              <li class="flex items-center left">
                <img
                  :src="item.symbol ? `${FILE_URL}/symbol/${item.symbol}.png` : handleImage('../../../assets/loading-default.png')"
                  alt="logo" class="w-16 h-16 rounded-full mr-4" />
                <p class="flex flex-col">
                  <span class="flex items-end text-32 flex items-center">
                    <span class="textColor text-30">{{ item.symbol && item.symbol.toUpperCase() || '--'
                    }}</span>
                    <span class="text-24 text-grey" style="position: relative; top: 1px">
                      /USDT
                    </span>
                    <!-- <span class="text-24 text-grey" style="position: relative; top: 1px">
                      {{ item.name && item.name.replace(item.symbol.toUpperCase(), '') || '--' }}</span> -->
                  </span>
                  <span class="text-24 text-grey text-left">{{ $t('成交量') + ' ' + (item.amount * 1).toFixed(2) }}</span>
                </p>
              </li>
              <li class="flex flex-col items-end mid">
                <p class="textColor text-32">{{ item.close || '--' }}</p>
                <p class="text-24 text-grey">{{ currency.currency_symbol }}
                  {{ item.close && item.symbol.toUpperCase() == 'SHIB' ? (item.close * currency.rate).toFixed(8) :
                    (item.close *
                      currency.rate).toFixed(2) || '--' }}</p>
              </li>
              <li class="right flex items-center justify-end">
                <p class="w-40 text-32 h-16 bg-green text-white border-0 text-center btn" v-if="item.changeRatio > 0">
                  +{{ item.changeRatio || (item.changeRatio === 0 ? 0 : '--') }}%</p>
                <p class="w-40 text-32 h-16 bg-red text-white border-0 text-center btn" v-else>
                  {{ item.changeRatio || (item.changeRatio === 0 ? 0 : '--') }}%</p>
              </li>
            </ul>
          </van-cell>
        </div>
        <div v-else :key="active">
          <van-cell v-for="item in showList" :key="item.id">
            <ul class="flex justify-between w-full items-center" @click="onItemClick(item)">
              <li class="flex items-center left">
                <img :src="`${FILE_URL}/symbol/${item.symbol}.png`" alt="logo" class="w-16 h-16 rounded-full mr-4" />
                <p class="flex flex-col">
                  <span class="flex items-end text-32 flex items-center">
                    <span class="textColor text-30">{{ item.symbol && item.symbol.toUpperCase() || '--'
                    }}</span>
                    <!-- <span class="text-24 text-grey" style="position: relative; top: 1px">
                      {{ item.name && item.name.replace(item.symbol.toUpperCase(), '') || '--' }}</span> -->
                    <span class="text-24 text-grey" style="position: relative; top: 1px">
                      /USDT
                    </span>
                  </span>
                  <span class="text-24 text-grey text-left">{{ $t('成交量') + ' ' + (item.amount * 1).toFixed(2) }}</span>
                </p>
              </li>
              <li class="flex flex-col items-end mid">
                <p class="textColor text-32">{{ item.close }}</p>
                <p class="text-24 text-grey">{{ currency.currency_symbol }} {{ item.close &&
                  item.symbol.toUpperCase() == 'SHIB' ? (item.close * currency.rate).toFixed(8) : (item.close *
                    currency.rate).toFixed(2) || '--' }}</p>
              </li>
              <li class="right flex items-center justify-end text-right">
                <div v-if="active == 3" class="textColor w-162 font-bold text-24">
                  {{ (item.volume * 1).toFixed(2) }}
                </div>
                <template v-else>
                  <p class="w-40 text-32 h-16 bg-green text-white border-0 text-center btn" v-if="item.changeRatio > 0">
                    +{{ item.changeRatio }}%</p>
                  <p class="w-40 text-32 h-16 bg-red text-white border-0 text-center btn" v-else>
                    {{ item.changeRatio || (item.changeRatio === 0 ? 0 : '--') }}%</p>
                </template>
              </li>
            </ul>
          </van-cell>
        </div>
      </transition-group>
    </van-list>
    <!-- <div class="flex flex-col justify-center items-center pb-58 mt-20" @click="$router.push('/quotes/?active=3')" v-if="showMore">
        <p class="text-grey text-28 mb-8">{{ $t('查看') }}</p>
        <img src="./icon-arrow_more.png" alt="more" class="w-24 h-10"/>
    </div> -->
  </div>
</template>

<script>
import { List, Cell } from 'vant'
import { mapGetters, mapActions } from 'vuex'
import { fixDate, setStorage } from "@/utils";
import ExTabs from "@/components/Transform/ex-tabs/index.vue";
import { FILE_URL } from '@/config'
import { SET_CURRENCY } from "@/store/const.store";
export default {
  name: 'ListQuotation',
  data() {
    return {
      fixDate,
      FILE_URL,
      active: 0,
      type: 'left' //left 从左往右 right 从有王座
    }
  },
  props: {
    showMore: {
      type: Boolean,
      default: true
    },
    listData: {
      type: Array,
      default() {
        return []
      }
    },
    tabActive: {
      type: Number,
      default: 0
    },
  },
  computed: {
    ...mapGetters({ currency: 'home/currency' }),
  },
  components: {
    [List.name]: List,
    [Cell.name]: Cell,
    ExTabs
  },
  mounted() {
    this.SET_CURRENCY()
  },
  methods: {
    ...mapActions('home', [SET_CURRENCY]),
    onItemClick(item) {
      if (this.tabActive == 2) { //现货
        this.$router.push({
          path: `/cryptos/trade/${item.symbol}`
        });
      } else {
        setStorage('symbol', item.symbol)
        this.$router.push({
          path: `/cryptos/perpetualContract/${item.symbol}`,
          query: { type: 'cryptos' }
        });
      }
    },
    handleImage(url) {
      return new URL(url, import.meta.url).href
    },
    onTabs(val) {
      if (this.active < val) {
        this.type = 'right'
      } else {
        this.type = 'left'
      }
      this.active = val
      if (val == 0) {
        this.showList = [...this.listData];
      } else if (val == 1) {
        this.showList = [...this.listData].sort(this.compare("changeRatio", 'up'))
      } else if (val == 2) {
        this.showList = [...this.listData].sort(this.compare("changeRatio", 'down'))
      } else if (val == 3) {
        this.showList = [...this.listData].sort(this.compare("volume", 'up'))
      }
    },
    compare(p, type) { //这是比较函数
      return function (m, n) {
        var a = m[p];
        var b = n[p];
        if (a == b) {
          return
        }
        if (type == 'up') {
          return b - a; //升序
        } else if (type == 'down') {
          return a - b; //降序
        } else {
          return a - b;
        }
      }
    }
  },
  watch: {
    listData() {
      if (this.active == 0) {
        this.showList = [...this.listData];
      } else if (this.active == 1) {
        this.showList = [...this.listData].sort(this.compare("changeRatio", 'up'))
      } else if (this.active == 2) {
        this.showList = [...this.listData].sort(this.compare("changeRatio", 'down'))
      } else if (this.active == 3) {
        this.showList = [...this.listData].sort(this.compare("volume", 'up'))
      }
      this.$forceUpdate()
    }
  }
}
</script>
<style lang="scss" scoped>
#cryptos {

  .left-enter-active,
  .left-leave-active,
  .right-enter-active,
  .right-leave-active {
    will-change: transform;
    transition: all 250ms;
  }

  .left-leave-active,
  .right-leave-active {
    display: none;
  }

  .left-enter {
    opacity: 0;
    transform: translate3d(-100%, 0, 0);
  }

  .left-leave {
    opacity: 0;
    transform: translate3d(0%, 0, 0)
  }

  .right-enter {
    opacity: 0;
    transform: translate3d(100%, 0, 0);
  }

  .right-leave {
    opacity: 0;
    transform: translate3d(0%, 0, 0)
  }

  .btn {
    border-radius: 9px;
    line-height: 71px;
  }

  .left {
    width: 382px
  }

  .mid {
    width: 185px;
  }

  .right {
    width: 182px;
    margin-left: 38px;
  }

}
</style>
