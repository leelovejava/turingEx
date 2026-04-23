import { defineStore } from "pinia";

export const useCurrencyStore = defineStore("currency", {
  // state 持久化,存储在了storage里面
  persist: true,
  state: () => {
    return {
      etfCurrency: [], //币种
      usStocksCurrency: [],
      coinCurrency: [],
      forexCurrency: [],
      cnStocksCurrency: [],
      hkStocksCurrency: [],
      twStocksCurrency: [],
    };
  },

  actions: {
    updateCurrency(val, type) {
      // type 是自定义的
      switch (type) {
        case "etf":
          this.etfCurrency = val;
          break;
        case "usStocks":
          this.usStocksCurrency = val;
          break;
        case "twStocks":
          this.twStocksCurrency = val;
          break;
        case "coin":
          this.coinCurrency = val;
          break;
        case "hkStocks":
          this.hkStocksCurrency = val;
          break;
        case "cnStocks":
          this.cnStocksCurrency = val;
          break;
        default:
          this.forexCurrency = val;
      }
    },
  },
});
