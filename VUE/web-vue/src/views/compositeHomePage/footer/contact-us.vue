<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h1>{{ t("cu-s0-t1") }}</h1>
      <h5 class="w-500">{{ t("cu-s0-d1") }}</h5>
    </pc-section>
    <pc-section class="py-12" innerclass="grid grid-cols-2 grid-x-12">
      <!-- 电话 -->
      <div>
        <h2 class="fs-24">{{ t("24/7") }}</h2>
        <div class="pr-28 flex justify-between">
          <h5>{{ t("cu-s1-2-d1") }}</h5>
          <h5 class="link-middle-text" @click="gotoPage('mail')">
            {{ $email }}
          </h5>
        </div>
      </div>

      <!-- 其他 -->
      <div>
        <h2 class="fs-24">{{ t("cu-s1-2-t1") }}</h2>

        <div class="pr-16 flex justify-between">
          <h5>{{ t("cu-s1-2-d2") }}</h5>
          <h5 class="link-middle-text" @click="gotoPage('mail')">
            {{ t("marketing@demo.co") }}
          </h5>
        </div>
      </div>
    </pc-section>
  </div>
</template>

<script setup>
import { useI18n } from "vue-i18n";
const { t } = useI18n();

const gotoPage = (path) => {
  if (path === "mail") {
    window.location.href = `mailto:${import.meta.env.VITE_APP__EMAIL}`;
    return;
  }
  // if (path === "tel") {
  //   window.location.href = "tel:+1786 628 1209";
  //   return;
  // }
};
</script>
<style lang="scss" scoped>
.w-500 {
  width: 500px;
}
</style>
