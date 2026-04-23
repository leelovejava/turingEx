<template>
  <div class="nowSharesInventory">
    <sharesHeader
      :value1="headerList.availableLimit"
      :value2="headerList.inventoryGainsLosses"
      :value3="headerList.marketValue"
    />
    <div class="nowSharesInventory-box">
      <div class="nowSharesInventory-box-content" v-for="(item, index) in tableData" :key="index">
        <div class="nowSharesInventory-box-content-text flex-justify-align-center">
          <div class="flex-column-center align-center">
            <p class="text-white">{{ item.symbolName }}</p>
            <p>{{ item.symbolCode }}</p>
          </div>
          <!-- <div>
                <p class="text-white">{{item.createTime}}</p>
            </div> -->
        </div>
        <div class="nowSharesInventory-box-content-text" v-for="(val, key, i) in listValue" :key="i">
          <div>
            <p>{{ t(`message.user.${val}`) }}</p>
          </div>
          <div :class="i === 0 || i === 1 ? setColor(item[key]) : ''">
            <p>{{ i === 1 ? `${item[key]}%` : item[key] }}</p>
          </div>
        </div>
        <button class="nowSharesInventory-box-content-button" v-click="() => sellButton(item)">
          {{ t(`message.user.mai10`) }}
        </button>
      </div>
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
import { ElMessage } from "element-plus";

const { t } = useI18n();
const headerList = ref({});

onMounted(() => {
  getDataList();

  Axios.getNewSharesHeaderMessage({ type: 3 }).then((res) => {
    if (res.code === 0) {
      headerList.value = res.data;
    }
  });
});

const setColor = computed(() => {
  return function (val) {
    if (val > 0) {
      return "isRed";
    }
    if (val < 0) {
      return "isGreen";
    }
  };
});

const tableData = ref([]);
const total = ref(0);
const pagination = ref({
  current: 1,
  size: 10,
});
const getDataList = () => {
  Axios.getNewSharesOrderList({ type: 3, ...pagination.value }).then((res) => {
    if (res.code === 0) {
      tableData.value = res.data;
      total.value = res.total;
    }
  });
};

const listValue = {
  inventoryGainsLosses: "kucunshunyi",
  inventoryGainsLossesValue: "shunyibaifenbi",
  subNumber: "jiaoyigushi",
  marketValue: "kucunshizhi",
  subPrice: "mairu10",
  closePrice: "xianjia10",
};

const getList = (val) => {
  getDataList();
};

const resetPagination = () => {
  pagination.value.current = 1;
  pagination.value.size = 10;
};

const sellButton = (data) => {
  Axios.getNewSharesOrderList({ orderNo: data.orderNo }).then((res) => {
    if (res.code === 0) {
      ElMessage({
        message: t(`message.user.maichuchenggong`),
        type: "success",
      });
      resetPagination();
      getDataList();
    }
  });
};
</script>

<style lang="scss" scoped>
.nowSharesInventory {
  .nowSharesInventory-box {
    min-height: 500px;
    margin-top: 40px;
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    .nowSharesInventory-box-content {
      width: 384px;
      height: 360px;
      padding: 18px 17px 28px 15px;
      background: #1d2336;
      border-radius: 15px;
      // margin-right: 24px;
      margin-bottom: 29px;
      color: #b7bdd1;
      font-size: 14px;
      &:nth-last-of-type(1) {
        margin-right: 0;
      }
      .nowSharesInventory-box-content-text {
        display: flex;
        justify-content: space-between;
        margin-bottom: 10px;
        &:nth-last-of-type(1) {
          margin-bottom: 30px;
        }
      }
      .isGreen {
        color: #05cda5;
      }
      .isRed {
        color: #f33368;
      }
      .nowSharesInventory-box-content-button {
        width: 100%;
        height: 42px;
        background: #f43368;
        color: #fff;
        border-radius: 5px;
      }
    }
  }
}
</style>
