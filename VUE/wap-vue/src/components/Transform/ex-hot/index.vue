<template>
  <div class="list-hot text-32">
    <div class="hotBox">
      <div v-for="item in listData" :key="item.id" class="box-border">
        <ul class="box-border flex flex-col w-full px-4" @click="onItemClick(item)">
          <li class="flex items-center justify-between mb-4">
            <p class="flex items-end items-center">
              <strong>{{ item.symbol && item.symbol.toUpperCase() || '--' }}</strong>
              <span class="grey">{{ item.name && item.name.replace(item.symbol.toUpperCase(), '') ||
                '--' }}</span>
              <span class="text-22" :class="item.changeRatio > 0 ? 'green' : 'red'">{{ item.changeRatio > 0
                ?
                '+' : '' }}{{ item.changeRatio }}%</span>
            </p>
          </li>
          <li class="mb-4">
            <p>
              <strong class="font-bold text-36" :class="item.changeRatio > 0 ? 'green' : 'red'">{{
                item.close || '--' }}</strong><br />
              <span class="grey text-28">≈ {{ currency.currency_symbol }}{{ item.close && (item.close
                *
                currency.rate).toFixed(2) || '--'
              }}</span>
            </p>
          </li>
        </ul>
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
  .list-hot {
    font-size: 26px;

    p {
      color: $text_color1;
    }
  }

  .red {
    color: #E35561
  }

  .green {
    color: $green
  }

  .grey {
    color: $text_color1;
  }

  .hotBox {
    display: flex;

    >div {
      flex: 1;
    }
  }
}
</style>