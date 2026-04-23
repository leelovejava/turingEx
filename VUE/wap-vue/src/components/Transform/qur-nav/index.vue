<template>
  <div class="mt-2 text-28">
    <div class="nav mb-2">
      <div v-for="(item, index) in navList" :key="index" class="list" @click="goPath(item.path, item.isLogin)">
        <div class="imgBox"><img :src="handleImage(item.icon)" alt=""></div>
        <div class="mt-4 text-center">{{ item.name }}</div>
      </div>
    </div>
    <div class="nav mb-10">
      <div v-for="(item, index) in navList1" :key="index" class="list" @click="goPath(item.path, item.isLogin)">
        <div class="imgBox"><img :src="item.icon" alt="" v-if="item.icon"></div>
        <div class="mt-4 text-center">{{ item.name }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { themeStore } from '@/store/theme';
const thStore = themeStore()
export default {
  props: {

  },
  components: {

  },
  computed: {
    ...mapGetters('user', ['userInfo']),
  },
  data() {
    return {
      navList: [
        {
          name: this.$t('闪兑'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/indexNav/3259.png`, import.meta.url),
          path: '/cryptos/exchangePage',
          isLogin: true
        },
        {
          name: this.$t('返佣'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/indexNav/3260.png`, import.meta.url),
          path: '/promote',
          isLogin: true
        },
        {
          name: this.$t('合约'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/indexNav/3271.png`, import.meta.url),
          path: '/cryptos/perpetualContract/AAPL?type=US-stocks&selectIndex=1'
        },
        {
          name: this.$t('理财'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/indexNav/3262.png`, import.meta.url),
          path: '/cryptos/fund'
        },
        {
          name: this.$t('客服'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/indexNav/3270.png`, import.meta.url),
          path: '/customerService'
        },
      ],
      navList1: [
        {
          name: this.$t('股票'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/indexNav/3258.png`, import.meta.url),
          path: '/quotes/openTrade?tabActive=0&symbol=AAPL&type=US-stocks',
          isLogin: false
        },
        {
          name: this.$t('质押'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/indexNav/3257.png`, import.meta.url),
          path: '/cryptos/pledgeLoan'
        },
        {
          name: this.$t('账变记录'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/indexNav/3268.png`, import.meta.url),
          path: '/cryptos/accountChange?type=US-stocks'
        },
        {
          name: this.$t('分享'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/indexNav/3269.png`, import.meta.url),
          path: '/ShareQRCode'
        },
        {
          name: this.$t('更多'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/indexNav/3267.png`, import.meta.url),
          path: '/more'
        }
      ]
    }
  },
  methods: {
    handleImage(url) {
      return new URL(url, import.meta.url).href
    },
    goPath(path, isLogin) {
      if (!path) {
        return
      } else if (isLogin) {
        if (!this.userInfo.token) {
          this.$router.push('/login')
        } else {
          this.$router.push(path)
        }
      } else {
        this.$router.push(path)
      }
    }
  },
  activated() {
    this.navList = [
      {
        name: this.$t('闪兑'),
        icon: `../../../assets/theme/dark/image/nav/exchange.png`,
        path: '/cryptos/exchangePage',
        isLogin: true
      },
      {
        name: this.$t('账变记录'),
        icon: `../../../assets/theme/dark/image/nav/record.png`,
        path: '/cryptos/accountChange?type=cryptos',
        isLogin: true
      },
      // {
      //   name: this.$t('现货账户'),
      //   icon: `./img/3259.png`,
      //   path: '/cryptos/funds'
      // },
      {
        name: this.$t('SpotTrading'),
        icon: `../../../assets/theme/dark/image/nav/coin.png`,
        path: '/cryptos/funds?type=cryptos'
      },
      {
        name: this.$t('永续合约'),
        icon: `../../../assets/theme/dark/image/nav/trading.png`,
        path: '/cryptos/trendDetails/btc'
      },
      {
        name: this.$t('交割合约'),
        icon: `../../../assets/theme/dark/image/nav/delivery.png`,
        path: '/cryptos/trendDetails/btc?selectIndex=2'
      },
    ]
    this.navList1 = [
      {
        name: this.$t('资金'),
        icon: new URL('@/assets/theme/dark/image/nav/asset.png', import.meta.url),
        path: '/cryptos/funds?type=cryptos',
        isLogin: true
      },
      // {
      //   name: this.$t('基金理财'),
      //   icon: new URL('@/assets/theme/dark/image/nav/financialmanagement.png', import.meta.url),
      //   path: '/cryptos/fund'
      // },
      // {
      //   name: this.$t('智能矿池'),
      //   icon: new URL('@/assets/theme/dark/image/nav/SmartKuangchi.png', import.meta.url),
      //   path: '/cryptos/machine'
      // },
      // {
      //   name: this.$t('质押借币'),
      //   icon: new URL('@/assets/theme/dark/image/nav/PledgeLoan.png', import.meta.url),
      //   path: '/cryptos/pledgeLoan'
      // },
      // {
      //   name: this.$t('助力贷'),
      //   icon: `../../../assets/theme/dark/image/nav/Helpoan.png`,
      //   path: '/cryptos/loan'
      // },
    ]
  },
}
</script>

<style lang="scss" scoped>
.nav {
  display: flex;
  align-items: center;
  font-size: 26px;
  color: #21262F;
}

.list {
  display: flex;
  flex-direction: column;
  align-items: center;
  // margin-right: 50px;
  width: 20%;
  color: $text_color;

  &:last-child {
    margin-right: 0px;
  }

  .text-center {
    height: 70px;
    word-wrap: break-word;
    white-space: normal;
    width: 100%;
    text-align: center;
  }
}

.imgBox {
  width: 90px;
  height: 90px;

  img {
    width: 100%;
    height: 100%;
  }
}
</style>