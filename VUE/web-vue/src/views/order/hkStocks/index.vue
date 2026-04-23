<template>
  <el-tabs v-model="activeName" class="order-tab" @tab-click="handleClick">
    <el-tab-pane
      :label="`${t('message.user.ganggu')} ${t('message.user.xianhuolishi')}`"
      name="spot"
    >
      <spotOrder v-if="activeName === 'spot'" type="HK-stocks"></spotOrder>
    </el-tab-pane>
    <el-tab-pane
      :label="`${t('message.user.ganggu')} ${t('message.user.heyuelishi')}`"
      name="contract"
    >
      <contractHistoryOrder
        type="HK-stocks"
        v-if="activeName === 'contract'"
      ></contractHistoryOrder>
    </el-tab-pane>
    <el-tab-pane
      :label="`${t('message.user.ganggu')} ${t('message.user.jiaogelishi')}`"
      name="delivery"
    >
      <deliveryHistoryOrder
        v-if="activeName === 'delivery'"
        type="HK-stocks"
      ></deliveryHistoryOrder>
    </el-tab-pane>
  </el-tabs>
</template>
<script setup>
import contractHistoryOrder from "../components/contractHistoryOrder.vue";
import deliveryHistoryOrder from "../components/deliveryHistoryOrder.vue";
import spotOrder from "../components/spotOrder.vue";
import { useI18n } from "vue-i18n";
const activeName = ref("spot");
const { t } = useI18n();

const handleClick = (tab, event) => {
  console.log(tab, event);
};
</script>
