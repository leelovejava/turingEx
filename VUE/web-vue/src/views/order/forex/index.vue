<template>
  <el-tabs v-model="activeName" class="order-tab" @tab-click="handleClick">
    <el-tab-pane
      :label="`${t('message.user.waihui')} ${t('message.user.heyuelishi')}`"
      name="contract"
    >
      <contractHistoryOrder
        type="forex"
        v-if="activeName === 'contract'"
      ></contractHistoryOrder>
    </el-tab-pane>
    <el-tab-pane
      :label="`${t('message.user.waihui')} ${t('message.user.jiaogelishi')}`"
      name="delivery"
    >
      <deliveryHistoryOrder
        v-if="activeName === 'delivery'"
        type="forex"
      ></deliveryHistoryOrder>
    </el-tab-pane>
  </el-tabs>
</template>
<script setup>
import contractHistoryOrder from "../components/contractHistoryOrder.vue";
import deliveryHistoryOrder from "../components/deliveryHistoryOrder.vue";

import { useI18n } from "vue-i18n";
const activeName = ref("contract");
const { t } = useI18n();

const handleClick = (tab, event) => {
  console.log(tab, event);
};
</script>
<style>
.order-tab > .el-tabs__content {
  padding: 32px;
  padding-left: 0;
  font-size: 32px;
}
</style>
