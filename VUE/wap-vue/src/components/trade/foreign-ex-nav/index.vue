<template>
  <div id="cryptos" class="mt-10">
    <div class="nav mb-5">
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
    defaultEtfListData: {
      type: Object,
      default() {
        return {}
      }
    }
  },
  components: {
  },
  watch: {
    defaultEtfListData(val) {
      const symbol = val && val.length > 0 ? val[0].symbol : 'USDSGD'
      this.navList = [
        {
          name: this.$t('永续合约'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon2-2.png`, import.meta.url),
          path: `/foreign/coinChart?symbol=USDSGD`
        },
        {
          name: this.$t('交割合约'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon3-3.png`, import.meta.url),
          path: `/foreign/deliveryContract/USDSGD`
        },
        {
          name: this.$t('账变记录'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon4-4.png`, import.meta.url),
          path: '/cryptos/accountChange?type=forex'
        },
        {
          name: this.$t('身份认证'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon08.png`, import.meta.url),
          path: `/certificationCenter`
        },
      ]
    }
  },
  data() {
    return {
      navList: [
        {
          name: this.$t('永续合约'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon2-2.png`, import.meta.url),
          path: `/foreign/coinChart?symbol=USDSGD`
        },
        {
          name: this.$t('交割合约'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon3-3.png`, import.meta.url),
          path: `/foreign/deliveryContract/USDSGD`
        },
        {
          name: this.$t('账变记录'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon4-4.png`, import.meta.url),
          path: '/cryptos/accountChange?type=forex'
        },
        {
          name: this.$t('身份认证'),
          icon: new URL(`../../../assets/theme/${thStore.theme}/image/etfNav/icon08.png`, import.meta.url),
          path: `/certificationCenter`
        },
      ],
    }
  },
  mounted() {

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
    font-weight: 600;
    font-size: 12px;
    line-height: 17px;
    text-align: center;
    height: 30px;
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
