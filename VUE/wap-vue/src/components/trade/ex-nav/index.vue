<template>
  <div id="cryptos" class="mt-5">
    <div class="nav mb-10">
      <div v-for="(item, index) in navList" :key="index" class="list" @click="goPath(item.name)">
        <div class="imgBox"><img :src="handleImage(item.icon)" alt=""></div>
        <div class="mt-4 text-center title">{{ item.name }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { themeStore } from '@/store/theme';
const thStore = themeStore()
export default {
  props: {
    symbolType: {
      type: String,
      default: 'indices'
    },
  },
  data() {
    return {
      navList: [
        {
          name: this.$t('买入'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon01.png`, import.meta.url),
        },
        {
          name: this.$t('卖出'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon02.png`, import.meta.url),
        },
        // {
        //   name: this.$t('撤单'),
        //   icon: new URL('@/assets/image/exNav/icon03.png', import.meta.url),
        // },
        {
          name: this.$t('持仓'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon03.png`, import.meta.url),
        },
        {
          name: this.$t('查询'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon04.png`, import.meta.url),
        },
      ],
      pathList: {
        'indices': {
          [this.$t('买入')]: '/quotes/openTrade?tabActive=0&symbol=GlobalETF500&from=trade&type=indices',
          [this.$t('卖出')]: '/quotes/openTrade?tabActive=1&symbol=GlobalETF500&from=trade&type=indices',
          // [this.$t('撤单')]: '/quotes/openTrade?tabActive=2&symbol=GlobalETF500&from=trade&type=indices',
          [this.$t('持仓')]: '/quotes/openTrade?tabActive=3&symbol=GlobalETF500&from=trade&type=indices',
          [this.$t('查询')]: '/quotes/openTrade?tabActive=4&symbol=GlobalETF500&from=trade&type=indices',
        },
        'cryptos': {
          [this.$t('买入')]: '/cryptos/perpetualContract/btc?tabActive=0&from=trade&type=cryptos',
          [this.$t('卖出')]: '/cryptos/perpetualContract/btc?tabActive=1&from=trade&type=cryptos',
          // [this.$t('撤单')]: '/quotes/openTrade?tabActive=2&from=trade',
          [this.$t('持仓')]: '/cryptos/perpetualContract/btc?from=trade&type=cryptos&tradeTabActive=1&navActive=0',
          [this.$t('查询')]: '/cryptos/perpetualHistory?symbol=btc&type=cryptos&tradeTabActive=1&navActive=2',
        },
        'forex': {
          [this.$t('买入')]: '/foreign/coinChart?symbol=XAUUSD&tabActive=0&from=trade&type=forex',
          [this.$t('卖出')]: '/foreign/coinChart?symbol=XAUUSD&tabActive=1&from=trade&type=forex',
          // [this.$t('撤单')]: '/quotes/openTrade?tbActive=2&symbol=XAUUSD&from=trade',
          [this.$t('持仓')]: '/position/index?tabActive=3&from=trade&type=forex',
          [this.$t('查询')]: '/history/list?tabActive=4&from=trade&type=forex',
        },
        'US-stocks': {
          [this.$t('买入')]: '/quotes/openTrade?tabActive=0&symbol=AAPL&from=trade&type=US-stocks',
          [this.$t('卖出')]: '/quotes/openTrade?tabActive=1&symbol=AAPL&from=trade&type=US-stocks',
          // [this.$t('撤单')]: '/quotes/openTrade?tabActive=2&symbol=AAPL&from=trade&type=US-stocks',
          [this.$t('持仓')]: '/quotes/openTrade?tabActive=3&symbol=AAPL&from=trade&type=US-stocks',
          [this.$t('查询')]: '/quotes/openTrade?tabActive=4&symbol=AAPL&from=trade&type=US-stocks',
        },
        'HK-stocks': {
          [this.$t('买入')]: '/quotes/openTrade?tabActive=0&symbol=00139&from=trade&type=HK-stocks',
          [this.$t('卖出')]: '/quotes/openTrade?tabActive=1&symbol=00139&from=trade&type=HK-stocks',
          // [this.$t('撤单')]: '/quotes/openTrade?tabActive=2&symbol=00139&from=trade&type=HK-stocks',
          [this.$t('持仓')]: '/quotes/openTrade?tabActive=3&symbol=00139&from=trade&type=HK-stocks',
          [this.$t('查询')]: '/quotes/openTrade?tabActive=4&symbol=00139&from=trade&type=HK-stocks',
        },
        'TW-stocks': {
          [this.$t('买入')]: '/quotes/openTrade?tabActive=0&symbol=2002&type=TW-stocks',
          [this.$t('卖出')]: '/quotes/openTrade?tabActive=1&symbol=2002&type=TW-stocks',
          // [this.$t('撤单')]: '/quotes/openTrade?tabActive=2&symbol=00139&from=trade&type=HK-stocks',
          [this.$t('持仓')]: '/quotes/openTrade?tabActive=3&symbol=2002&type=TW-stocks',
          [this.$t('查询')]: '/quotes/openTrade?tabActive=4&symbol=2002&type=TW-stocks',
        },
        'JP-stocks': {
          [this.$t('买入')]: '/quotes/openTrade?tabActive=0&symbol=2002&type=JP-stocks',
          [this.$t('卖出')]: '/quotes/openTrade?tabActive=1&symbol=2002&type=JP-stocks',
          // [this.$t('撤单')]: '/quotes/openTrade?tabActive=2&symbol=00139&from=trade&type=JP-stocks',
          [this.$t('持仓')]: '/quotes/openTrade?tabActive=3&symbol=2002&type=JP-stocks',
          [this.$t('查询')]: '/quotes/openTrade?tabActive=4&symbol=2002&type=JP-stocks',
        },
        'A-stocks': {
          [this.$t('买入')]: '/quotes/openTrade?tabActive=0&symbol=SH688981&type=A-stocks&from=trade',
          [this.$t('卖出')]: '/quotes/openTrade?tabActive=1&symbol=SH688981&type=A-stocks&from=trade',
          [this.$t('持仓')]: '/quotes/openTrade?tabActive=3&symbol=SH688981&type=A-stocks&from=trade',
          [this.$t('查询')]: '/quotes/openTrade?tabActive=4&symbol=SH688981&type=A-stocks&from=trade',
        },
        'UK-stocks': {
          [this.$t('买入')]: '/quotes/openTrade?tabActive=0&symbol=2002&type=UK-stocks',
          [this.$t('卖出')]: '/quotes/openTrade?tabActive=1&symbol=2002&type=UK-stocks',
          // [this.$t('撤单')]: '/quotes/openTrade?tabActive=2&symbol=00139&from=trade&type=UK-stocks',
          [this.$t('持仓')]: '/quotes/openTrade?tabActive=3&symbol=2002&type=UK-stocks',
          [this.$t('查询')]: '/quotes/openTrade?tabActive=4&symbol=2002&type=UK-stocks',
        },
        'DE-stocks': {
          [this.$t('买入')]: '/quotes/openTrade?tabActive=0&symbol=2002&type=DE-stocks',
          [this.$t('卖出')]: '/quotes/openTrade?tabActive=1&symbol=2002&type=DE-stocks',
          // [this.$t('撤单')]: '/quotes/openTrade?tabActive=2&symbol=00139&from=trade&type=DE-stocks',
          [this.$t('持仓')]: '/quotes/openTrade?tabActive=3&symbol=2002&type=DE-stocks',
          [this.$t('查询')]: '/quotes/openTrade?tabActive=4&symbol=2002&type=DE-stocks',
        },
        'BZ-stocks': {
          [this.$t('买入')]: '/quotes/openTrade?tabActive=0&symbol=2002&type=BZ-stocks',
          [this.$t('卖出')]: '/quotes/openTrade?tabActive=1&symbol=2002&type=BZ-stocks',
          // [this.$t('撤单')]: '/quotes/openTrade?tabActive=2&symbol=00139&from=trade&type=BZ-stocks',
          [this.$t('持仓')]: '/quotes/openTrade?tabActive=3&symbol=2002&type=BZ-stocks',
          [this.$t('查询')]: '/quotes/openTrade?tabActive=4&symbol=2002&type=BZ-stocks',
        },
        'INDIA-stocks':{
          [this.$t('买入')]: '/quotes/openTrade?tabActive=0&symbol=2002&type=INDIA-stocks',
          [this.$t('卖出')]: '/quotes/openTrade?tabActive=1&symbol=2002&type=INDIA-stocks',
          // [this.$t('撤单')]: '/quotes/openTrade?tabActive=2&symbol=00139&from=trade&type=INDIA-stocks',
          [this.$t('持仓')]: '/quotes/openTrade?tabActive=3&symbol=2002&type=INDIA-stocks',
          [this.$t('查询')]: '/quotes/openTrade?tabActive=4&symbol=2002&type=INDIA-stocks',
        },
      }
    }
  },
  methods: {
    handleImage(url) {
      return new URL(url, import.meta.url).href
    },
    goPath(name) {
      const pathName = this.pathList[this.symbolType][name]
      if (!pathName) return
      this.$router.push(pathName)
    }
  },
  // activated() {
  //   this.navList = [
  //     {
  //       name: this.$t('买入'),
  //       icon: `./img/icon01.png`,
  //       path: '/cryptos/exchangePage'
  //     },
  //     {
  //       name: this.$t('卖出'),
  //       icon: `./img/icon02.png`,
  //       path: '/cryptos/accountChange'
  //     },
  //     {
  //       name: this.$t('撤单'),
  //       icon: `./img/icon03.png`,
  //       path: '/cryptos/funds'
  //     },
  //     {
  //       name: this.$t('持仓'),
  //       icon: `./img/icon04.png`,

  //       path: '/cryptos/funds'
  //     },
  //     {
  //       name: this.$t('查询'),
  //       icon: `./img/icon05.png`,
  //       path: '/cryptos/trendDetails/btc'
  //     },
  //   ]
  // },
}
</script>

<style lang="scss" scoped>
#cryptos {
  .nav {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 26px;
    color: #21262F;
  }

  .list {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    flex: 1;
    color: $text_color;

    &:last-child {
      margin-right: 0px;
    }

    .title {
      font-family: 'PingFang HK';
      font-style: normal;
      font-weight: 600;
      font-size: 12px;
      line-height: 17px;
      text-align: center;
    }
  }

  .imgBox {
    width: 48px;
    height: 48px;

    img {
      width: 100%;
      height: 100%;
    }
  }
}
</style>
