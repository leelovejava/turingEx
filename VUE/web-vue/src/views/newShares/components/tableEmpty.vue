<template>
  <div class="tableEmpty">
    <img src="@/assets/images/newShares/empty.png" width="127" height="127" />
    <p>{{ t("message.user.zhanwushuju") }}</p>
  </div>
</template>

<script setup>
import { useI18n } from "vue-i18n";

const { t } = useI18n();
</script>

<style lang="scss" scoped>
.tableEmpty {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>
