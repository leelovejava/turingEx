<template>
  <div class="sharesHeader">
    <div>
      <p class="fs-14 draw-title-color" style="margin-bottom: 5px">
        {{ t(`message.user.${title1}`) }}
      </p>
      <p class="fs-22">{{ value1 }}</p>
    </div>
    <div>
      <p class="fs-14 draw-title-color" style="margin-bottom: 5px">
        {{ t(`message.user.${title2}`) }}
      </p>
      <p class="fs-22" style="color: #05cda5">{{ value2 }}</p>
    </div>
    <div>
      <p class="fs-14 draw-title-color" style="margin-bottom: 5px">
        {{ t(`message.user.${title3}`) }}
      </p>
      <p class="fs-22">{{ value3 }}</p>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const props = defineProps({
  title1: {
    type: String,
    default: "shizhi",
  },
  title2: {
    type: String,
    default: "kucunshunyi",
  },
  title3: {
    type: String,
    default: "keyongedu",
  },
  value1: {
    type: [String, Number],
    default: 0,
  },
  value2: {
    type: [String, Number],
    default: 0,
  },
  value3: {
    type: [String, Number],
    default: 0,
  },
});
</script>

<style lang="scss" scoped>
.sharesHeader {
  display: flex;
  color: #fff;
  justify-content: space-between;
  border-bottom: 1px solid #232a40;
  padding-bottom: 25px;
}
</style>
