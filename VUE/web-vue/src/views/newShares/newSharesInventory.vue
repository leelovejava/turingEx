<template>
  <!-- 新股库存 -->
  <div class="newSharesInventory">
    <sharesHeader
      :value1="headerList.availableLimit"
      :value2="headerList.inventoryGainsLosses"
      :value3="headerList.marketValue"
    />
    <div class="newSharesInventory-table newShares-table">
      <el-table :data="tableData" style="width: 100%">
        <template #empty>
          <table-empty />
        </template>
        <el-table-column :label="t(`message.user.mingchengdaima10`)">
          <template #default="scope">
            <p>{{ scope.row.symbolName }}</p>
            <p class="draw-title-color">{{ scope.row.symbolCode }}</p>
          </template>
        </el-table-column>
        <el-table-column :label="t(`message.user.jiageshengqingliang`)">
          <template #default="scope">
            <p>{{ scope.row.subPrice }}</p>
            <p>{{ scope.row.subNumber }}</p>
          </template>
        </el-table-column>
        <el-table-column :label="t(`message.user.zhongqianrenjiaoer`)">
          <template #default="scope">
            <p>{{ scope.row.winningNumber }}</p>
            <p>{{ scope.row.requiredNumber }}</p>
          </template>
        </el-table-column>
        <el-table-column :label="t(`message.user.zhuangtai10`)">
          <template #default="scope">
            <p :class="scope.row.status === 2 ? 'isRed' : ''">
              {{ t(`message.user.${getStatus(scope.row.status)}`) }}
            </p>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <pagination
      :total="total"
      v-model:page="pagination.current"
      v-model:limit="pagination.size"
      @pagination="getList"
    />
  </div>
</template>

<script setup>
import sharesHeader from "./components/sharesHeader.vue";
import Pagination from "@/components/Pagination/index.vue";
import Axios from "@/api/newShares";
import { useI18n } from "vue-i18n";
import tableEmpty from "./components/tableEmpty.vue";

const { t } = useI18n();
const headerList = ref({});

onMounted(() => {
  getDataList();

  Axios.getNewSharesHeaderMessage({ type: 2 }).then((res) => {
    if (res.code === 0) {
      headerList.value = res.data;
    }
  });
});

const tableData = ref([]);
const total = ref(0);
const pagination = ref({
  current: 1,
  size: 10,
});
const getDataList = () => {
  Axios.getNewSharesOrderList({ type: 2, ...pagination.value }).then((res) => {
    if (res.code === 0) {
      tableData.value = res.data;
      total.value = res.total;
    }
  });
};

// 状态
const getStatus = computed(() => {
  return function (value) {
    let result = "";
    switch (value) {
      case 1:
        result = "shengouzhong";
        break;
      case 2:
        result = "yizhongqian";
        break;
      case 3:
        result = "weizhongqian";
        break;
      default:
        break;
    }
    return result;
  };
});

const getList = (val) => {
  getDataList();
};
</script>

<style lang="scss" scoped>
@import "../../assets/css/newShares/table.scss";
.newSharesInventory {
  :deep(.newSharesInventory-table) {
    min-height: 500px;
    .el-table__cell {
      border: 0;
    }
  }
}
.isRed {
  color: #f33368;
}
</style>
