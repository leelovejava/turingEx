<!-- 布局入口 -->
<template>
  <div class="app-box">
    <div class="box-view">
      <!-- left -->
      <div class="left-view">
        <div
          v-for="(it, i) in qianbaoList"
          :key="i"
          class="menu-list"
          :class="selectPath == it.urlPath ? 'menu-active-color' : ''"
          @click="goRouter(it.urlPath)"
        >
          <div
            :class="selectPath == it.urlPath ? 'item-active-line' : ''"
          ></div>
          <img :src="it.iconPath" class="icon" />
          <span>{{ $t(`message.user.${it.title}`) }}</span>
        </div>
      </div>
      <!-- right -->
      <div class="right-view">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { qianbaoList } from "@/utils/menuConfig";
const router = useRouter();
const route = useRoute();
const selectPath = ref("");

watch(
  () => route.path,
  (newPath, oldPath) => {
    selectPath.value = newPath;
  },
  { immediate: true }
);

const goRouter = (parmas) => {
  router.push(parmas);
};
</script>

<style scoped>
.icon {
  width: 22px;
  height: 22px;
  margin: 0 10px;
}
</style>
