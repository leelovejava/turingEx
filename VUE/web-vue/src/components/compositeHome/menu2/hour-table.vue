<!-- 组件 -->
<template>
  <div>
    <h2 class="my-8">
      {{ t(`m2-th-s${index + 2}-t1`) }}
    </h2>
    <h5 v-if="beforeDesc" class="my-8">
      {{ t(`${beforeDesc}`) }}
    </h5>
    <el-table :data="tableData[index]" style="width: 100%" class="self-table">
      <el-table-column
        v-for="(item, i) in tableList[index]"
        :key="i"
        :prop="item.prop"
        :label="t(item.label)"
      />
    </el-table>
    <h5 v-if="afterDesc" class="my-8" v-html="t(`${afterDesc}`)"></h5>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import {
  tableData,
  tableList,
} from "@/views/compositeHomePage/menu2-trading/trading-hours";
const route = useRoute();
const { t } = useI18n();
const props = defineProps({
  beforeDesc: {
    type: String,
    default: "",
  },
  afterDesc: {
    type: String,
    default: "",
  },
  index: {
    type: Number,
    default: 1,
  },
});
</script>
<style lang="scss" scoped>
.section1 {
  border-bottom: 1px solid #d8dee1;
  padding-bottom: 12px;
}
</style>
