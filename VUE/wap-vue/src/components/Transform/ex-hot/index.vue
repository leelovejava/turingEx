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
          {{ item.close || '--' }}
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
import { fixDate } from "@/utils";
export default {
  data() {
    return {
      fixDate
    }
  },
  components: {
  },
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
    onItemClick(item) { // 点击进入合约交易
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
    gap: 8px;
    align-items: stretch;
  }

  .hot-card {
    flex: 1;
    min-width: 0;
    padding: 18px 12px 16px;
    border-radius: 18px;
    background: #1a1a1c;
    border: 1px solid rgba(255, 255, 255, 0.08);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
    cursor: pointer;
    transition: background 0.15s ease, border-color 0.15s ease;

    &:active {
      background: #222226;
      border-color: rgba(255, 255, 255, 0.12);
    }
  }

  .hot-pair {
    margin: 0 0 10px;
    font-size: 26px;
    line-height: 1.2;
    color: #fff;

    strong {
      font-weight: 700;
    }
  }

  .hot-quote {
    color: rgba(255, 255, 255, 0.45);
    font-weight: 500;
  }

  .hot-price {
    margin: 0 0 8px;
    font-size: 36px;
    font-weight: 700;
    line-height: 1.2;
    letter-spacing: -0.02em;
  }

  .hot-sub {
    margin: 0;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 6px 8px;
    font-size: 20px;
    line-height: 1.3;
  }

  .hot-fiat {
    color: rgba(255, 255, 255, 0.45);
  }

  .hot-pct {
    font-weight: 600;
  }

  .is-up {
    color: #34d399;
  }

  .is-down {
    color: #f87171;
  }
}
</style>
