<template>
  <div class="newShares">
    <header class="newShares-header">
      <p class="fs-24 font-bold text-white">
        {{ t(`message.user.${navTitle}`) }}
      </p>
    </header>
    <div class="newShares-box">
      <router-view @setNavTitle="setNavTitle"></router-view>
    </div>
  </div>
</template>

<script setup>
import { useRoute } from "vue-router";
import { useI18n } from "vue-i18n";

const route = useRoute();
const { t } = useI18n();
const navTitle = ref("xingurengou");

watch(route,(newVal) => {
    const { title } = newVal.meta;
    if (title) {
      navTitle.value = title;
    }
  },
  { immediate: true }
);

const setNavTitle = (titleVal) => {
  navTitle.value = titleVal;
};
</script>

<style lang="scss" scoped>
.newShares {
  width: 100%;
  .newShares-header {
    height: 80px;
    background: #282a3a;
    padding: 0 360px;
    display: flex;
    align-items: center;
  }
  .newShares-box {
    min-height: 100vh;
    padding: 30px 360px;
    background: #191b28;
  }
}
</style>
