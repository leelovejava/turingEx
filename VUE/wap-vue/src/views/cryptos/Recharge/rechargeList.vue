<template>
  <div id="cryptos">
    <div class="recharge box-border">
      <assets-head :title="$t('充值通道')" v-if="!isForeign" :back-func="backFunc">
        <img src="../../../assets/image/assets-center/exchange.png" alt="exchange-img" class="w-11 h-9"
          @click="goRouter('/cryptos/assetsCenter/rechargeWithdrawRecord')" />
      </assets-head>
      <assets-head v-else :title="$t('充值通道')">
        <img src="../../../assets/image/assets-center/exchange.png" alt="exchange-img" class="w-11 h-9"
          @click="goRouter('/cryptos/assetsCenter/rechargeWithdrawRecord')" />
      </assets-head>
      <div class="pl-9 pr-9">
        <div class="recharge-title text-36 textColor">{{ $t('请选择充值币种') }}</div>
        <div class="recharge-list flex justify-between">
          <div class="item-view flex flex-col items-center justify-center text-center w-48 h-56 box-border"
            @click="selectSymbol('usdt')">
            <img src="../../../assets/image/symbol/usdt.png" class="w-24 h-92" />
            <div class="text-grey text-26 mt-8">{{ $t('USDT充值') }}</div>
          </div>
          <div class="item-view  flex flex-col items-center justify-center text-center w-48 h-56 box-border"
            @click="selectSymbol('btc')">
            <img src="../../../assets/image/symbol/btc.png" class="w-24 h-92" />
            <div class="text-grey text-26 mt-8">{{ $t('BTC充值') }}</div>
          </div>
          <div class="item-view flex flex-col items-center justify-center text-center w-48 h-56 box-border"
            @click="selectSymbol('eth')">
            <img src="../../../assets/image/symbol/eth.png" class="w-24 h-92" />
            <div class="text-grey text-26 mt-8">{{ $t('ETH充值') }}</div>
          </div>
        </div>
      </div>
      <div class="textColor mt-10">
        <div class="pl-9 pr-9 h-20 border-b-color flex justify-between text-28" v-for="(item, index) in list" :key="index"
          @click="toPath(item.url)">
          <div class="flex items-center">
            <img :src="handleImage(item.imgPath)" class="w-11 h-11 rounded-full mr-5" />
            <span class="textColor1">{{ item.name }} {{ $t('官方充值通道') }}</span>
          </div>
          <div>
            <van-icon class="textColor1" name="arrow" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import assetsHead from "@/components/Transform/assets-head/index.vue";
import { Icon } from 'vant';
export default {
  name: 'rechageList',
  components: {
    assetsHead,
    [Icon.name]: Icon,
  },
  data() {
    return {
      list: [
        {
          name: 'Huobi',
          url: 'https://www.huobi.com/en-us/',
          imgPath: new URL('@/assets/image/huobi.png', import.meta.url),
        },
        {
          name: 'Binance',
          url: 'https://www.binance.com/en',
          imgPath: new URL('@/assets/image/bian.png', import.meta.url),
        },
        {
          name: 'Coinbase',
          url: 'https://www.coinbase.com/',
          imgPath: new URL('@/assets/image/coinbase.png', import.meta.url),
        },
        {
          name: 'Crypto',
          url: 'https://www.crypto.com/',
          imgPath: new URL('@/assets/image/crypto.png', import.meta.url),
        }
      ],
      isForeign: false
    }
  },
  created() {
    if (this.$route.query.isForeign) {
      this.isForeign = this.$route.query.isForeign
    }
  },
  methods: {
    handleImage(url) {
      return new URL(url, import.meta.url).href
    },
    toPath(url) {
      const a = document.createElement('a');
      a.href = url;
      a.target = "_bank";
      document.body.append(a);
      a.click();
      document.body.removeChild(a)
      // if (navigator.userAgent.search('Html5Plus') != -1) {
      //     plus.runtime.openURL(url)
      // } else {
      //     window.open(url)
      // }
    },
    backFunc() {
      this.$router.push('/quotes/index?tabActive=1')
    },
    selectSymbol(symbol) {
      this.$router.push({
        path: "/cryptos/recharge/rechargePage",
        query: {
          'symbol': symbol
        }
      });
    },
    goRouter(params) {
      this.$router.push({
        path: params,
        query: {
          back: "1"
        }
      });
    }
  }
}
</script>
<style lang="scss" scoped>
#cryptos {
  .recharge {
    width: 100%;
    box-sizing: border-box;
  }

  .recharge-title {
    margin: 86px 0 104px 0;
  }

  .recharge-list>div {
    border: 2px solid $border_color;
    border-radius: 15px;
    box-sizing: border-box;
  }
}
</style>
