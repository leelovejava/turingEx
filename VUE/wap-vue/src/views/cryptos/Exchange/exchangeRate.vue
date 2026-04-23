<template>
  <div id="#cryptos">
    <div class="exchangeRate">
      <assets-head :title="$t('汇率设置')" />
      <div v-for="(item, index) in exchangeratelist_get_data" :key="index"
        class="flex justify-between items-center lang-padding text-36 box-border h-32 px-8" @click="handleSetLang(item)">
        <div class="lang-title flex items-center text-26 textColor">
          <img class="w-12 h-12 mr-5" :src="item.url" alt="">
          {{ item.currency }}
        </div>

        <img class="w-12 h-12" v-if="item.currency == show_ex" src="../../../assets/image/public/checked.png" />
      </div>
    </div>
  </div>
</template>
<script>
import { _exchangeratelist, _exchangerateuserconfig } from "@/service/trade.api";
import { _getExchangeRate } from '@/service/home.api'
import assetsHead from "@/components/Transform/assets-head/index.vue";
import { mapActions } from "vuex";
import { SET_CURRENCY } from "@/store/const.store";

export default {
  data() {
    return {
      show_ex: {},
      list: [
        { title: 'USD', key: 'USD' },
        { title: 'CNY', key: 'CNY' },
        { title: 'ERU', key: 'ERU' },
        { title: 'JPY', key: 'JPY' }
      ],
      exchangeratelist_get_data: []
    }
  },
  components: {
    assetsHead
  },
  mounted() {
    this.init()
  },
  methods: {
    ...mapActions('home', [SET_CURRENCY]),
    init() {
      // this.show_ex = this.$store.state.home.currency.currency
      this.getExchangeRate()
      this.exchangeratelist_get()
    },
    handleImage(url) {
      return new URL(url, import.meta.url).href
    },
    getExchangeRate() {
      _getExchangeRate().then((res) => {
        this.show_ex = res.currency
      })
    },
    exchangeratelist_get() {
      const t = this
      _exchangeratelist({}).then((data) => {
        t.exchangeratelist_get_data = data
        t.exchangeratelist_get_data.forEach((item) => {
          item.image = new URL(`../../../assets/image/exchange-rate/${item.currency}.png`, import.meta.url)
          item.url = this.handleImage(item.image)
        })
      })
    },
    handleSetLang(e) {
      const t = this
      _exchangerateuserconfig({ rateId: e.uuid }).then((res) => {
        this.SET_CURRENCY()
        t.show_ex = e.currency

      })

    },
    onClickLeft() {
      this.$router.go(-1)
      console.log(this.$i18n.locale)
    },
  }
}
</script>
<style lang="scss" scoped>
#cryptos {
  .exchangeRate {
    width: 100%;
    box-sizing: border-box;
  }

  .lang-padding {
    border-bottom: 1px solid $line_color;
    font-weight: 400;
    color: $black;
  }



}

.px-35 {
  padding: 0 20px !important;
}

.h-127 {
  height: 127px;
}

.lang-title {
  font-size: 30px;
}
</style>
