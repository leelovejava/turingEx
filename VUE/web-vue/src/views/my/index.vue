<!-- 布局入口 -->
<template>
  <div class="app-box">
    <div class="box-view">
      <!-- left -->
      <div class="left-view">
        <div
          v-for="(it, i) in list"
          :key="i"
          class="menu-list"
          :class="selectPath == it.url ? 'menu-active-color' : ''"
          @click="goRouter(it.url)"
        >
          <div :class="selectPath == it.url ? 'item-active-line' : ''"></div>
          <img :src="$getImages(`headIcon/personal-menu/${it.imgKey}.png`)" />
          <span>{{ $t(`message.user.${it.label}`) }}</span>
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
const router = useRouter();
const route = useRoute();
const selectPath = ref("");

const list = [
  {
    imgKey: "account-security",
    label: "zhanghuanquan",
    url: "/my/security",
  },
  {
    imgKey: "universal",
    label: "tongyong",
    url: "/my/universal",
  },

  {
    imgKey: "help-center",
    label: "bangzhuzhongxin",
    url: "/my/helpCenter",
  },
  {
    imgKey: "notice",
    label: "gonggaozhongxin",
    url: "/my/announcement",
  },
];

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
