<template>
  <div>
    <order-list-item
      @openOrder="openOrder(item)"
      v-for="(item, index) in orderList"
      :key="index"
      :item="item"
    />
  </div>
</template>

<script>
import { mapState } from "pinia";
import OrderListItem from "./OrderListItem.vue";
export default {
  name: "OrderList",
  props: ["orderList"],
  components: {
    OrderListItem,
  },
  methods: {
    openOrder(item) {
      this.$router.push(
        "/c2c/orderSuccess?id=" +
          item.order_no +
          "&isBuy=" +
          (item.direction == "buy" ? 1 : 2)
      );
    },
  },
};
</script>

<style scoped></style>
