<template>
  <div id="cryptos" class="mt-10">
    <div class="nav">
      <div v-for="(item, index) in navList" :key="index" class="list" @click="goPath(item.path, item.name)">
        <div class="imgBox"><img :src="handleImage(item.icon)" alt=""></div>
        <div class="mt-10 text-center title">{{ item.name }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { themeStore } from '@/store/theme';
const thStore = themeStore()

export default {
  props: {
    prominentListData: {
      type: Object,
      default() {
        return {}
      }
    }
  },
  components: {

  },
  watch: {
    prominentListData(val) {
      this.navList = [
        {
          name: this.$t('Astocktrading'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon5-5.png`, import.meta.url),
          path: `/quotes/openTrade?tabActive=0&symbol=${val[0].symbol}&type=A-stocks`
        },
        {
          name: this.$t('永续合约'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon2-2.png`, import.meta.url),
          path: `/cryptos/perpetualContract/${val[0].symbol}?type=A-stocks&selectIndex=1`
        },
        {
          name: this.$t('交割合约'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon3-3.png`, import.meta.url),
          path: `/cryptos/perpetualContract/${val[0].symbol}?type=A-stocks&selectIndex=2`
        },
        {
          name: this.$t('账变记录'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon4-4.png`, import.meta.url),
          path: '/cryptos/accountChange?type=A-stocks'
        },
      ]
    }
  },
  data() {
    return {
      navList: [

      ],
    }
  },
  methods: {
    handleImage(url) {
      return new URL(url, import.meta.url).href
    },
    goPath(path, name) {
      if (!path) {
        return
      } else {
        this.$router.push(path)
      }
    }
  },
}
</script>

<style lang="scss" scoped>
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
