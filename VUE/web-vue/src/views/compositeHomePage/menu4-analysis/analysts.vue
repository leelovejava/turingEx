<template>
  <div>
    <pc-section class="index-section1 h-200">
      <h1 class="mt-14">{{ t("m4-an-s0-t1") }}</h1>
    </pc-section>
    <pc-section innerclass="pl-12 my-16">
      <div class="w-586">
        <img
          src="/src/assets/images/compositeHome/menu4/an1.png"
          width="586"
          height="180"
        />
        <div class="p-12 wrapper-shadow">
          <h5 class="fs-24 font-normal mb-4">{{ t("m4-an-s1-t1") }}</h5>
          <h5>{{ t("m4-an-s1-d1") }}</h5>
          <h5>{{ t("m4-an-s1-d2") }}</h5>
        </div>
      </div>
    </pc-section>
    <ready-bg></ready-bg>
  </div>
</template>

<script setup>
import { useI18n } from "vue-i18n";
import readyBg from "@comCompositeHome/readyWithBg.vue";
const { t } = useI18n();
</script>
<style lang="scss" scoped>
.h-200 {
  height: 200px;
}
.w-586 {
  width: 586px;
}

.wrapper-shadow {
  box-shadow: 0px 2px 8px 10px rgba(0, 0, 0, 0.05);
}
</style>
