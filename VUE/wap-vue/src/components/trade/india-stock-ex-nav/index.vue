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
          name: this.$t('印度股交易'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon1-1.png`, import.meta.url),
          path: '/quotes/openTrade?tabActive=0&symbol=indiaADEL&type=INDIA-stocks',
          isLogin: true
        },
        {
          name: this.$t('永续合约'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon2-2.png`, import.meta.url),
          path: '/cryptos/perpetualContract/indiaADEL?type=INDIA-stocks&selectIndex=1'
        },
        {
          name: this.$t('交割合约'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon3-3.png`, import.meta.url),
          path: '/cryptos/perpetualContract/indiaADEL?type=INDIA-stocks&selectIndex=2'
        },
        {
          name: this.$t('账变记录'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon4-4.png`, import.meta.url),
          path: '/cryptos/accountChange?type=INDIA-stocks'
        },

      ],
      navList1: [
        {
          name: this.$t('助力贷'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/nav/asset.png`, import.meta.url),
          path: '/cryptos/loan',
          isLogin: true
        },
        {
          name: this.$t('新股认购'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon5-5.png`, import.meta.url),
          path: '/ipo/index?tabActive=0&stock=US-stocks&stockActive=3'
        },
        {
          name: this.$t('期货交易'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon3-3.png`, import.meta.url),
          path: '/cryptos/perpetualContract/AAPL?type=US-stocks&selectIndex=2'
        },
        {
          name: this.$t('客服'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/indexNav/3270.png`, import.meta.url),
          path: '/customerService'
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
.nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
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
    height: 40px;
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
</style>
