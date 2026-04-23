<template>
  <div class="mt-5 text-28">
    <div class="nav mb-2">
      <div v-for="(item, index) in navList" :key="index" class="list" @click="goPath(item.path, item.isLogin)">
        <div class="imgBox"><img :src="handleImage(item.icon)" alt=""></div>
        <div class="mt-1 text-center">{{ item.name }}</div>
      </div>
    </div>
    <div class="nav mb-2">
      <div v-for="(item, index) in navList1" :key="index" class="list" @click="goPath(item.path, item.isLogin)">
        <div class="imgBox"><img :src="handleImage(item.icon)" alt="" v-if="item.icon"></div>
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
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/nav/exchange.png`, import.meta.url),
          path: '/cryptos/exchangePage',
          isLogin: true
        },
        {
          name: this.$t('账变记录'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/nav/record.png`, import.meta.url),
          path: '/cryptos/accountChange?type=cryptos',
          isLogin: true
        },
        // {
        //   name: this.$t('现货账户'),
        //   icon: `../../../assets/theme/dark/image/nav/account.png`,
        //   path: '/cryptos/funds'
        // },
        {
          name: this.$t('SpotTrading'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/nav/coin.png`, import.meta.url),
          path: '/cryptos/trade/btc'
        },
        {
          name: this.$t('永续合约'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/nav/trading.png`, import.meta.url),
          path: '/cryptos/perpetualContract/btc?type=cryptos'
        },
        {
          name: this.$t('交割合约'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/nav/delivery.png`, import.meta.url),
          path: '/cryptos/perpetualContract/btc?selectIndex=2&type=cryptos'
        },

      ],
      navList1: [
        {
          name: this.$t('资金'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/nav/asset.png`, import.meta.url),
          path: '/cryptos/funds?type=cryptos',
          isLogin: true
        },
        {
          name: this.$t('基金理财'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/nav/financialmanagement.png`, import.meta.url),
          path: '/cryptos/fund'
        },
        {
          name: this.$t('智能矿池'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/nav/SmartKuangchi.png`, import.meta.url),
          path: '/cryptos/machine'
        },
        // {
        //   name: this.$t('质押借币'),
        //   icon: new URL('@/assets/theme/dark/image/nav/PledgeLoan.png', import.meta.url),
        //   path: '/cryptos/pledgeLoan'
        // },
        // {
        //   name: this.$t('助力贷'),
        //   icon: new URL('@/assets/theme/dark/image/nav/Helpoan.png', import.meta.url),
        //   path: '/cryptos/loan'
        // },
        {
          name: this.$t('C2C'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/nav/c2c.png`, import.meta.url),
          path: '/cryptos/wantBuy'
        },
        {
          name: this.$t('更多'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/nav/more.png`, import.meta.url),
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
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
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
      line-height: 30px;
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
}
</style>