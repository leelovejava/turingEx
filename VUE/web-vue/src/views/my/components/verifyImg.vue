<template>
  <div class="flex-row-center">
    <img
      v-if="verrify"
      src="@/assets/myImages/icon-image/account-security/success-green.png"
      alt=""
      width="25"
      height="25"
    />
    <img
      v-else
      src="@/assets/myImages/icon-image/account-security/error.png"
      width="25"
      height="25"
      alt=""
    />
    <div v-if="label">{{ t(`message.user.${label}`) }}</div>
  </div>
</template>
<script setup>
import { useI18n } from "vue-i18n";
const { t } = useI18n();
const props = defineProps({
  verrify: {
    type: Boolean,
  },
  label: {
    type: String,
  },
});
</script>
