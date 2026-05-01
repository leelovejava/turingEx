<template>
  <div class="list-hot ex-hot-cards">
    <div class="hotBox">
      <div
        v-for="item in listData"
        :key="item.id"
        class="hot-card"
        @click="onItemClick(item)"
      >
        <p class="hot-pair">
          <strong>{{ item.symbol && item.symbol.toUpperCase() || '--' }}</strong><span class="hot-quote">/USDT</span>
        </p>
        <p class="hot-price" :class="item.changeRatio > 0 ? 'is-up' : 'is-down'">
          {{ formatPrice(item.close) }}
        </p>
        <p class="hot-sub">
          <span class="hot-fiat">
            ≈ {{ currency.currency_symbol }}{{ item.close && (item.close * currency.rate).toFixed(2) || '--' }}
          </span>
          <span class="hot-pct" :class="item.changeRatio > 0 ? 'is-up' : 'is-down'">
            {{ item.changeRatio > 0 ? '▲' : '▼' }}{{ item.changeRatio > 0 ? '+' : '' }}{{ item.changeRatio }}%
          </span>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  props: {
    listData: {
      type: Array,
      default() {
        return []
      }
    }
  },
  computed: {
    ...mapGetters({ currency: 'home/currency' })
  },
  methods: {
    formatPrice(value) {
      if (value === null || value === undefined || value === '') return '--'
      const str = String(value)
      if (!str.includes('.')) return str
      const [intPart, decPart] = str.split('.')
      return `${intPart}.${(decPart || '').slice(0, 4)}`
    },
    onItemClick(item) {
      this.$router.push({
        path: `/cryptos/trendDetails/${item.symbol}`,
        query: { type: 'cryptos' }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  .ex-hot-cards {
    margin-bottom: 16px;
  }

  .hotBox {
    display: flex;
    gap: 14px;
    align-items: stretch;
  }

  .hot-card {
    flex: 1;
    min-width: 0;
    min-height: 236px;
    padding: 22px 16px 18px;
    border-radius: 24px;
    background: #1f2540;
    border: 2px solid rgba(177, 189, 222, 0.2);
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.03);
  }

  .hot-pair {
    margin: 0 0 14px;
    font-size: 24px;
    line-height: 1.2;
    color: #eef1f7;

    strong {
      font-weight: 600;
    }
  }

  .hot-quote {
    color: #d7dcea;
    font-weight: 500;
  }

  .hot-price {
    margin: 0 0 10px;
    font-size: 48px;
    font-weight: 700;
    line-height: 1.1;
  }

  .hot-sub {
    margin: 0;
    display: block;
    font-size: 20px;
    line-height: 1.35;
  }

  .hot-fiat {
    display: block;
    color: #8f96a8;
  }

  .hot-pct {
    display: block;
    margin-top: 2px;
    font-weight: 700;
    font-size: 20px;
  }

  .is-up {
    color: #57d6b4;
  }

  .is-down {
    color: #e46b93;
  }
}
</style>
