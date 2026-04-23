<template>
  <div class="ready-bg">
    <h2 class="text-white">{{ t("ready-t1") }}</h2>
    <h5 class="text-white">{{ t("ready-d1") }}</h5>
    <el-button class="blue-middle-btn my-4">{{ t("ready-b1") }}</el-button>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";

const route = useRoute();
const { t } = useI18n();
</script>
<style lang="scss" scoped>
.ready-bg {
  background: url(@/assets/images/compositeHome/ready-bg.png) no-repeat center;
  background-size: auto 100%;
  height: 394px;
  width: 100%;
  display: flex;
  color: white;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>
