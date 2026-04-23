<template>
  <pc-section class="bg-white">
    <!-- 1.路由 -->
    <navigator></navigator>
    <!-- 2.内容 -->
    <div class="flex">
      <!-- 2.1左边 -->
      <section class="left">
        <!-- 菜单 -->
        <el-collapse
          v-if="needMenu"
          v-model="getActiveMenuName"
          @change="handleChange"
          class="collapse-wrapper mb-16"
        >
          <el-collapse-item
            class="item"
            :title="t(item.label)"
            :name="item.menu ? item.path : 'noexpand'"
            :class="[
              route.path == item.path ? 'active-item' : '',
              !item.menu || item.menu.length === 0 ? 'no-need-arrow' : '',
            ]"
            v-for="(item, index) in collapseList"
            :key="index"
            :disabled="!item.menu"
            @click="gotoPage(item.path)"
          >
            <!-- 二级菜单    -->
            <div v-if="item.menu && item.menu.length" class="second-wrap">
              <div
                class="second-item"
                :class="route.path === m.path ? 'second-active' : ''"
                v-for="(m, i) in item.menu"
                :key="i"
                @click="gotoPage(m.path, $event, true)"
              >
                {{ t(m.label) }}
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
        <!-- 准备好了吗 -->
        <div class="ready">
          <p class="mb-4">{{ t("ready-t1") }}</p>
          <h5 class="mb-4">{{ t("ready-d1") }}</h5>
          <el-button class="blue-large-btn" @click="gotoLogin">
            {{ t("ready-b1") }}
          </el-button>
        </div>
      </section>
      <!-- 2.2右边 -->
      <section class="right">
        <slot name="content"></slot>
      </section>
    </div>
  </pc-section>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import navigator from "./navigator.vue";
import router from "@/router";
import { useCurrencyStore } from "@/store/currency.js";
import { useUserStore } from "@/store/user";

const userStore = useUserStore();
const currencyStore = useCurrencyStore();

const route = useRoute();
const { t } = useI18n();
const props = defineProps({
  collapseList: {
    type: Array,
    default: [],
  },
  needMenu: {
    type: Boolean,
    default: true,
  },
});

const getActiveMenuName = computed(() => {
  let name = route.path;
  const arr = route.path.split("/");
  const len = arr.length;
  if (len > 3) {
    // 为二级组件
    arr.splice(-1);
    name = arr.join("/");
  }
  return [name];
});
const gotoPage = (path, e, prevent) => {
  if (prevent) {
    e.stopPropagation();
  }
  router.push(path);
};

const gotoLogin = () => {
  if (!userStore.existToken) {
    router.push("/login");
    return;
  }
  router.push(`/usStocks/spot/${currencyStore.usStocksCurrency[0]?.symbol}`);
};

// 路由切换
const handleChange = (val) => {
  // val是个数组
};
</script>

<style lang="scss" scoped>
.left {
  margin-right: 50px;
  width: 350px;
  .el-collapse {
    border-top: none;
  }
}
// icon不展示
.no-need-arrow :deep(.el-collapse-item__arrow) {
  display: none;
}

// 一级menu
.item :deep(.el-collapse-item__header) {
  color: #000 !important;
  cursor: pointer !important;
}
.active-item :deep(.el-collapse-item__header) {
  color: #233ad4 !important;
}
// 二级menu
.second-wrap {
  background: #f6f6f6;
  padding-left: 20px;
  padding-top: 20px;
  .second-item {
    height: 50px;
    cursor: pointer;
  }

  .second-item:hover {
    color: #233ad4;
  }

  .second-active {
    color: #233ad4;
  }
}
.ready {
  border: 1px solid #d8dee1;
  padding: 24px;
  width: 352px;
  p {
    font-size: 32px;
    font-weight: 600;
  }
}
.right {
  width: 900px;
}
</style>
