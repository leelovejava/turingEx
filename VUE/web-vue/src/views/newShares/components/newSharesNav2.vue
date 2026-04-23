<template>
  <!-- 已上市 -->
  <div class="newSharesNav2">
    <div class="newSharesNav2-table newShares-table">
      <el-table :data="dataList" @sort-change="sortChange" style="width: 100%">
        <template #empty>
          <table-empty />
        </template>
        <el-table-column :label="t(`message.user.gupiaomingcheng`)">
          <template #default="scope">
            <div>
              <p>{{ scope.row.name }}</p>
              <p class="fs-14 draw-title-color">{{ scope.row.symbol }}</p>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          v-for="(value, key, i) in listValue"
          :key="i"
          :prop="key"
          :label="t(`message.user.${value}`)"
          sortable="custom"
        >
          <template #default="scope" v-if="key === 'changeRatio' || key === 'firstDayChangeRatio'">
            <div>
              <p :class="parseFloat(scope.row[key]) > 0 ? 'top-color' : 'down-color'">
                {{ scope.row[key] }}
              </p>
            </div>
          </template>
          <template #default="scope" v-else>
            <div>
              <p>{{ scope.row[key] || "--" }}</p>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { watch } from "vue";
import { useI18n } from "vue-i18n";
import tableEmpty from "./tableEmpty.vue";

const { t } = useI18n();
const props = defineProps({
  tableList: {
    type: Array,
    default: () => [],
  },
});

const dataList = ref([]);

watch(() => props.tableList,(newVal) => {
    dataList.value = newVal;
  }
);

const listValue = {
  curPrice: "zuixinjia10",
  issuePrice: "faxingjia",
  listingDate: "yujishangshishijian",
  changeRatio: "shangshiyilaizhangfu",
  firstDayChangeRatio: "shourizhangfu",
};

const sortChange = (val) => {
  switch (val.order) {
    case "ascending":
      dataList.value = props.tableList.map((item) => item).sort((a, b) => {
          return parseFloat(b[val.prop]) - parseFloat(a[val.prop]);
        });
      break;
    case "descending":
      dataList.value = props.tableList.map((item) => item).sort((a, b) => {
          return parseFloat(a[val.prop]) - parseFloat(b[val.prop]);
        });
      break;
    default:
      dataList.value = props.tableList;
      break;
  }
};
</script>

<style lang="scss" scoped>
@import "../../../assets/css/newShares/table.scss";
</style>
