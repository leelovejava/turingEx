<template>
  <div>
    <!--没有订单-->
    <div v-if="orderDataList.length === 0" class="flex items-center flex-col">
      <img src="@/assets/images/c2c/order/noData.png" width="104" />
      <div>{{ $t("message.home.zanwujilu") }}</div>
    </div>
    <!--有订单-->
    <order-list :orderList="orderDataList" v-else />
  </div>
</template>

<script>
import OrderList from "./order-list/OrderList.vue";

export default {
  name: "orderTable",
  props: ["orderDataList"],
  components: {
    OrderList,
  },
};
</script>

<style scoped></style>
