<template>
  <div class="cry-nav-root mt-5">
    <div class="nav nav-row">
      <div v-for="(item, index) in navList" :key="index" class="list" @click="handleNavClick(item)">
        <div class="imgBox"><img :src="handleImage(item.icon)" alt=""></div>
        <div class="nav-label">{{ item.name }}</div>
      </div>
    </div>
    <div class="nav nav-row">
      <div v-for="(item, index) in navList1" :key="index" class="list" @click="handleNavClick(item)">
        <div class="imgBox"><img :src="handleImage(item.icon)" alt="" v-if="item.icon"></div>
        <div class="nav-label">{{ item.name }}</div>
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
          name: this.$t('AI量化'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/nav/SmartKuangchi.png`, import.meta.url),
          action: 'aiQuantSheet'
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
    handleNavClick(item) {
      if (item.action === 'aiQuantSheet') {
        this.$emit('open-ai-quant-sheet')
        return
      }
      this.goPath(item.path, item.isLogin)
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
.cry-nav-root {
  font-size: 12px;
}

#cryptos {
  .nav-row {
    display: flex;
    align-items: flex-start;
    justify-content: flex-start;
    gap: 4px;
    margin-bottom: 10px;

    &:last-child {
      margin-bottom: 4px;
    }
  }

  .list {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex: 1;
    min-width: 0;
    width: auto;
    max-width: none;
    color: $text_color;
  }

  .imgBox {
    width: 120px;
    height: 120px;
    border-radius: 26px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: $tab_background;
    border: 1px solid $line_color;
    box-shadow: none;

    img {
      width: 98px;
      height: 98px;
      object-fit: contain;
    }
  }

  .nav-label {
    margin-top: 10px;
    width: 100%;
    text-align: center;
    font-size: 26px;
    line-height: 1.4;
    font-weight: 500;
    color: $text_color;
    word-break: break-word;
    white-space: normal;
    padding: 0 2px;
  }
}
</style>