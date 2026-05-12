<template>
  <div class="list-quatation">
    <ex-tabs @tabs="onTabs"></ex-tabs>
    <van-list>
      <van-cell>
        <div class="list-head text-grey">
          <p class="left text-left">{{ $t('交易对') }}</p>
          <p class="mid text-right">{{ $t('最新价') }}</p>
          <p class="right text-right">{{ active == 3 ? $t('24h成交额') : $t('24h涨跌幅') }}</p>
        </div>
      </van-cell>
      <transition-group :name="type" tag="div">
        <div v-if="active == 0" :key="active">
          <van-cell v-for="item in listData" :key="item.id">
            <ul class="row-card flex justify-between w-full items-center" @click="onItemClick(item)">
              <li class="flex items-center left">
                <img
                  :src="item.symbol ? `${FILE_URL}/symbol/${item.symbol}.png` : handleImage('../../../assets/loading-default.png')"
                  alt="logo" class="w-16 h-16 rounded-full mr-4" />
                <p class="flex flex-col">
                  <span class="flex items-end text-32 flex items-center">
                    <span class="textColor text-30">{{ symbolUpper(item.symbol) }}</span>
                    <span class="text-24 text-grey" style="position: relative; top: 1px">/USDT</span>
                  </span>
                  <span class="text-24 text-grey text-left">{{ $t('成交量') + ' ' + (item.amount * 1).toFixed(2) }}</span>
                </p>
              </li>
              <li class="flex flex-col items-end mid">
                <p class="textColor text-32">{{ item.close || '--' }}</p>
                <p class="text-24 text-grey">{{ currency.currency_symbol }}
                  {{ item.close && symbolUpper(item.symbol) == 'SHIB' ? (item.close * currency.rate).toFixed(8) : (item.close * currency.rate).toFixed(2) || '--' }}</p>
              </li>
              <li class="right flex items-center justify-end">
                <p class="text-32 text-right pct-up" v-if="item.changeRatio > 0">▲ +{{ item.changeRatio || (item.changeRatio === 0 ? 0 : '--') }}%</p>
                <p class="text-32 text-right pct-down" v-else>▼ {{ item.changeRatio || (item.changeRatio === 0 ? 0 : '--') }}%</p>
              </li>
            </ul>
          </van-cell>
        </div>
        <div v-else :key="active">
          <van-cell v-for="item in showList" :key="item.id">
            <ul class="row-card flex justify-between w-full items-center" @click="onItemClick(item)">
              <li class="flex items-center left">
                <img :src="`${FILE_URL}/symbol/${item.symbol}.png`" alt="logo" class="w-16 h-16 rounded-full mr-4" />
                <p class="flex flex-col">
                  <span class="flex items-end text-32 flex items-center">
                    <span class="textColor text-30">{{ symbolUpper(item.symbol) }}</span>
                    <span class="text-24 text-grey" style="position: relative; top: 1px">/USDT</span>
                  </span>
                  <span class="text-24 text-grey text-left">{{ $t('成交量') + ' ' + (item.amount * 1).toFixed(2) }}</span>
                </p>
              </li>
              <li class="flex flex-col items-end mid">
                <p class="textColor text-32">{{ item.close }}</p>
                <p class="text-24 text-grey">{{ currency.currency_symbol }} {{ item.close && symbolUpper(item.symbol) == 'SHIB' ? (item.close * currency.rate).toFixed(8) : (item.close * currency.rate).toFixed(2) || '--' }}</p>
              </li>
              <li class="right flex items-center justify-end text-right">
                <div v-if="active == 3" class="textColor font-bold text-24" style="white-space:nowrap">{{ formatVolume(item.volume) }}</div>
                <template v-else>
                  <p class="text-32 text-right pct-up" v-if="item.changeRatio > 0">▲ +{{ item.changeRatio }}%</p>
                  <p class="text-32 text-right pct-down" v-else>▼ {{ item.changeRatio || (item.changeRatio === 0 ? 0 : '--') }}%</p>
                </template>
              </li>
            </ul>
          </van-cell>
        </div>
      </transition-group>
    </van-list>
  </div>
</template>

<script>
import { List, Cell } from 'vant'
import { mapGetters, mapActions } from 'vuex'
import { setStorage } from "@/utils";
import ExTabs from "@/components/Transform/ex-tabs/index.vue";
import { FILE_URL } from '@/config'
import { SET_CURRENCY } from "@/store/const.store";
export default {
  name: 'ListQuotation',
  data() {
    return {
      FILE_URL,
      active: 0,
      type: 'left'
    }
  },
  props: {
    listData: { type: Array, default() { return [] } },
    tabActive: { type: Number, default: 0 },
  },
  computed: {
    ...mapGetters({ currency: 'home/currency' }),
  },
  components: { [List.name]: List, [Cell.name]: Cell, ExTabs },
  mounted() { this.SET_CURRENCY() },
  methods: {
    ...mapActions('home', [SET_CURRENCY]),
    onItemClick(item) {
      if (!item || !item.symbol) return
      if (this.tabActive == 2) {
        this.$router.push({ path: `/cryptos/trade/${item.symbol}` });
      } else {
        setStorage('symbol', item.symbol)
        this.$router.push({ path: `/cryptos/perpetualContract/${item.symbol}`, query: { type: 'cryptos' } });
      }
    },
    symbolUpper(symbol) {
      return symbol ? String(symbol).toUpperCase() : '--'
    },
    handleImage(url) { return new URL(url, import.meta.url).href },
    formatVolume(val) {
      const n = val * 1
      if (n >= 1e9) return (n / 1e9).toFixed(2) + 'B'
      if (n >= 1e6) return (n / 1e6).toFixed(2) + 'M'
      if (n >= 1e3) return (n / 1e3).toFixed(2) + 'K'
      return n.toFixed(2)
    },
    onTabs(val) {
      this.type = this.active < val ? 'right' : 'left'
      this.active = val
      if (val == 0) this.showList = [...this.listData]
      else if (val == 1) this.showList = [...this.listData].sort((a, b) => b.changeRatio - a.changeRatio)
      else if (val == 2) this.showList = [...this.listData].sort((a, b) => a.changeRatio - b.changeRatio)
      else if (val == 3) this.showList = [...this.listData].sort((a, b) => b.volume - a.volume)
    }
  },
  watch: {
    listData() {
      if (this.active == 0) this.showList = [...this.listData]
      else if (this.active == 1) this.showList = [...this.listData].sort((a, b) => b.changeRatio - a.changeRatio)
      else if (this.active == 2) this.showList = [...this.listData].sort((a, b) => a.changeRatio - b.changeRatio)
      else if (this.active == 3) this.showList = [...this.listData].sort((a, b) => b.volume - a.volume)
      this.$forceUpdate()
    }
  }
}
</script>
<style lang="scss" scoped>
#cryptos {
  :deep(.van-cell) {
    background: transparent;
    padding: 0;
    margin-bottom: 12px;
  }

  :deep(.van-cell__value) { color: $text_color; }

  .list-head {
    display: grid;
    grid-template-columns: 52% 26% 22%;
    column-gap: 8px;
    align-items: center;
    padding: 6px 2px 8px;
    color: $text_color1;
    font-size: 30px;
    white-space: nowrap;
  }

  .left { width: 52%; min-width: 0; }
  .mid { width: 26%; min-width: 92px; overflow: hidden; }
  .right { width: 22%; min-width: 100px; text-align: right; overflow: hidden; }

  .row-card {
    background: $main2_background;
    border: 1px solid $line_color;
    border-radius: 22px;
    padding: 20px 18px;
  }

  .text-grey { color: $text_color1 !important; }
  .textColor { color: $text_color !important; }

  .pct-up, .pct-down {
    min-width: 130px;
    font-size: 38px !important;
    font-weight: 700;
    background: transparent !important;
    border: 0 !important;
    border-radius: 0 !important;
    padding: 0 !important;
    height: auto !important;
    line-height: 1.2 !important;
    text-align: right !important;
  }

  .pct-up { color: #57d6b4 !important; }
  .pct-down { color: #e46b93 !important; }

  .list-quatation .left .text-30 { font-size: 30px !important; font-weight: 700; }
  .list-quatation .mid .text-32 { font-size: 36px !important; font-weight: 700; }
  .list-quatation .text-24 { font-size: 28px !important; }
}
</style>
