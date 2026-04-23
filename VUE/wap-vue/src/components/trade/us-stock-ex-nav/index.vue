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
    initObjData: {
      type: Object,
      default() {
        return {}
      }
    },
    stockActive: {
      type: Number,
      default: 0
    }
  },
  components: {

  },
  watch: {
    initObjData(val) {
      this.navList = [
        {
          name: this.$t('USstocktrading'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon1-1.png`, import.meta.url),
          path: '/quotes/openTrade?tabActive=0&symbol=AAPL&type=US-stocks'
        },
        {
          name: this.$t('永续合约'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon2-2.png`, import.meta.url),
          path: `/cryptos/perpetualContract/${val.symbol}?type=US-stocks&selectIndex=1`
        },
        {
          name: this.$t('期货交易'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon3-3.png`, import.meta.url),
          path: `/cryptos/perpetualContract/${val.symbol}?type=US-stocks&selectIndex=2` 
        },
        {
          name: this.$t('账变记录'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon4-4.png`, import.meta.url),
          path: '/cryptos/accountChange?type=US-stocks'
        },
        {
          name: this.$t('新股认购'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon5-5.png`, import.meta.url),
          path: `/ipo?stock=US-stocks&stockActive=${this.$props.stockActive}`
        },
        {
          name: this.$t('新股库存'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon6-6.png`, import.meta.url),
          path: `/ipo/stock?type=newStock&stock=US-stocks&stockActive=${this.$props.stockActive}`
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
  justify-content: flex-start;
  align-items: center;
  flex-wrap: wrap;
  font-size: 26px;
  color: #21262F;
}

.list {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 25%;
  color: $text_color;
  flex-shrink: 0;

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
