<template>
  <!-- 1.路由 -->
  <h5 class="fs-12 mt-4 mb-8">{{ t("home") }}/ {{ t(getRouteName) }}</h5>
</template>
<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
const router = useRouter();
const route = useRoute();
const { t } = useI18n();
const routeList = computed(() => router.options.routes);
const forexRoutes = routeList.value.filter((it) => it.path === "/");
const getRouteName = computed(() => {
  const rp = route.path;
  const item = forexRoutes[0].children.find((it) => it.path === rp); //获取匹配路径的路由
  return item?.label || "";
});
</script>

<style lang="scss" scoped>
.fs-12 {
  font-size: 12px;
}
</style>
