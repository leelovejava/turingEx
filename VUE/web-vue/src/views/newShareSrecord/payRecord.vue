<template>
  <!-- 认缴记录 -->
  <div class="lotteryRecord">
    <sharesHeader
      title1="zhongqianedu"
      title2="renjiaoedu"
      title3="keyongedu"
      :value1="headerList.winningQuota"
      :value2="headerList.subscriptionLimit"
      :value3="headerList.availableLimit"
    />
    <div class="lotteryRecord-table newShares-table">
      <el-table :data="tableData" style="width: 100%">
        <template #empty>
            <table-empty />
        </template>
        <el-table-column :label="t(`message.user.mingchengdaima10`)">
          <template #default="scope">
            <p>{{ scope.row.name }}</p>
            <p class="draw-title-color">{{ scope.row.productName }}</p>
          </template>
        </el-table-column>
        <el-table-column :label="t(`message.user.xianjiachengben`)">
          <template #default="scope">
            <p>{{ scope.row.subPrice }}</p>
            <p>{{ scope.row.subPrice }}</p>
          </template>
        </el-table-column>
        <el-table-column prop="winningNumber" :label="t(`message.user.zhongqianedu`)"/>
        <el-table-column prop="requiredNumber" :label="t(`message.user.renjiaojine`)"/>
        <el-table-column prop="createTime" :label="t(`message.user.shijian10`)"/>
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
import sharesHeader from "../newShares/components/sharesHeader.vue";
import Pagination from "@/components/Pagination/index.vue";
import Axios from "@/api/newShares";
import { useI18n } from "vue-i18n";
import tableEmpty from "../newShares/components/tableEmpty.vue";

const { t } = useI18n();
const headerList = ref({});

onMounted(() => {
  getDataList();

  Axios.getPayOrderHeaderMessage().then((res) => {
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
  Axios.getPayMessageOrderList({ ...pagination.value }).then((res) => {
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
        result = "daiqueren";
        break;
      case 2:
        result = "yirenjiao";
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
.lotteryRecord {
  :deep(.lotteryRecord-table) {
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
