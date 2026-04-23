<template>
  <div
    class="w-full p-4 mt-4 bg-white"
    style="cursor: pointer"
    @click.stop="openOrder"
  >
    <div class="flex justify-between items-center p-4 bg-fa">
      <div class="color-788 font-14 font-semibold bg-fa">
        <span
          :style="{ color: item.direction === 'buy' ? '#4EA372' : '#E05461' }"
          >{{
            item.direction === "buy"
              ? $t("message.c2c.goumai")
              : $t("message.c2c.chushou")
          }}</span
        >
        <span class="mx-2 font-200">|</span>
        <span>{{ item.create_time }}</span>
      </div>
      <div class="flex items-center">
        <span
          class="mr-2 font-500 font-14 text-underline"
          style="color: #373840"
          >{{ item.order_no }}</span
        >
        <img
          width="12"
          @click.stop="_copy(item.order_no)"
          src="@/assets/images/c2c/order/Group1884.png"
          alt=""
        />
      </div>
    </div>
    <div class="flex pt-4 pl-4 font-16 info">
      <div class="info-item">
        <img
          class="mr-1"
          width="24"
          height="24"
          src="@/assets/images/c2c/order/Group1450.png"
          alt=""
        />
        <span>{{ item.symbol.toUpperCase() }}</span>
      </div>
      <div class="info-item font-semibold">
        <span class="mr-1">{{ getDecimal(item) }}</span>
        <span>{{ item.currency }}</span>
      </div>
      <div class="info-item">
        <span class="mr-1">{{ item.symbol_value }}</span>
        <span>{{ item.currency }}</span>
      </div>
      <div class="info-item">
        <span class="mr-1">{{ item.coin_amount }}</span>
        <span>{{ item.symbol }}</span>
      </div>
      <div class="info-item color-1A6 text-underline">
        {{ item.c2c_user_nick_name }}
      </div>
      <div class="flex-1 font-semibold">
        <div>{{ statusFilter(item.state) }}</div>
      </div>
      <div class="flex-1 font-semibold text-right color-1A6">
        {{ $t("message.c2c.lianxi") }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "OrderListItem",
  props: ["item"],
  computed: {},
  filters: {},
  methods: {
    openOrder() {
      this.$emit("openOrder", this.itme);
    }, // 复制操作
    _copy(context) {
      navigator.clipboard.writeText(context).then(() => {
        this.$message.success(this.$t("message.user.fuzhichenggong"));
      });
    },
    getDecimal(item) {
      return (
        parseFloat(item.symbol_value) * parseFloat(item.coin_amount)
      ).toFixed(5);
    },
    statusFilter(key) {
      key = parseInt(key);
      let title = "";
      switch (key) {
        case 0:
          title = this.$t("message.c2c.weifukuan");
          break;
        case 1:
          title = this.$t("message.c2c.yifukuan");
          break;
        case 2:
          title = this.$t("message.c2c.shenshuzhong");
          break;
        case 3:
          title = this.$t("message.c2c.yiwancheng");
          break;
        case 4:
          title = this.$t("message.c2c.yiquxiao");
          break;
        case 5:
          title = this.$t("message.c2c.yichaoshi");
          break;
        default:
          break;
      }
      return title;
    },
  },
};
</script>

<style lang="scss" scoped>
.info {
  .info-item {
    flex: 1;
    display: flex;
    justify-content: start;
  }

  & > div:not(:first-child) {
    transform: translateX(-4px);
  }
}

.color-1A6 {
  color: #1a6ebd;
}
</style>
