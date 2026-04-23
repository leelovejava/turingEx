<template>
  <div class="newSharesBox">
    <nav class="newSharesBox-nav">
      <button
        :class="['newSharesBox-nav-button','button-text-style',currentNav.id === item.id ? 'buttonActive' : '']"
        v-for="item in navList"
        :key="item.id"
        @click="navButton(item)"
      >
        {{ t(`message.user.${item.title}`) }}
      </button>
    </nav>
    <div class="newSharesBox-content">
      <component :is="currentNav.component" :tableList="tableList"></component>
    </div>
    <div class="newSharesBox-pagination">
      <pagination
        v-if="currentNav.id === 0"
        :total="total"
        v-model:page="pagination.current"
        v-model:limit="pagination.size"
        @pagination="getList"
      />
    </div>
  </div>
</template>

<script setup>
import Pagination from "@/components/Pagination/index.vue";
import Axios from "@/api/newShares";
import newSharesNav1 from "./components/newSharesNav1.vue";
import newSharesNav2 from "./components/newSharesNav2.vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const emit = defineEmits(["setNavTitle"]);

onMounted(() => {
  emit("setNavTitle", currentNav.value.title);
  getTableList();
});

const navList = reactive([
  {
    id: 0,
    title: "xingurengou",
    component: markRaw(newSharesNav1),
  },
  {
    id: 1,
    title: "yishangshi",
    component: markRaw(newSharesNav2),
  },
]);

const currentNav = ref(navList[0]);
const navButton = (navData) => {
  currentNav.value = navData;
  resetPagination();
  emit("setNavTitle", currentNav.value.title);
  getTableList();
};

const tableList = ref([]);
const total = ref(0);
const pagination = ref({
  current: 1,
  size: 10,
});

const getTableList = () => {
  switch (currentNav.value.id) {
    // 新股认购
    case 0:
      Axios.getNewSharesList(pagination.value).then((res) => {
        if (res.code === 0) {
          tableList.value = res.data;
          total.value = res.total;
        }
      });
      break;
    // 已上市
    case 1:
      Axios.getNewIssueList({type: "US-stocks"}).then((res) => {
        if (res.code === 0) {
          tableList.value = res.data;
        }
      });
      break;
    default:
      break;
  }
};

const getList = (val) => {
  getTableList();
};

const resetPagination = () => {
  tableList.value = [];
  pagination.value.current = 1;
  pagination.value.size = 10;
};
</script>

<style lang="scss" scoped>
.newSharesBox {
  .newSharesBox-nav {
    display: flex;
    margin-bottom: 30px;
    .newSharesBox-nav-button {
      min-width: 107px;
      height: 36px;
      border-radius: 100px;
      color: #747a8f;
      margin-right: 30px;
    }
    .buttonActive {
      background: #2465f1;
      color: #fff;
    }
  }
  .newSharesBox-content {
    min-height: 500px;
  }
  :deep(.newSharesBox-pagination) {
    width: 100%;
    margin-top: 30px;
    // .el-input__wrapper,.btn-prev,.btn-next,.number,.more{
    //     background: transparent;
    // }
  }
}
</style>
