<template>
  <el-table
    class="asset"
    :data="tableData"
    :empty-text="$t('message.home.noData')"
  >
    <!-- 显示资产name和图片 -->
    <el-table-column :label="$t('message.home.bizhong')">
      <template #default="scope">
        <div class="symbol_name">
          <div>
            <img class="symbol-img" :src="handleSymbolImg(scope.row.symbol)" />
          </div>
          <div>{{ scope.row.symbol.toUpperCase() }}</div>
        </div>
      </template>
    </el-table-column>
    <el-table-column
      v-for="(item, index) in tables"
      :key="index"
      :prop="item.prop"
      :label="$t(item.label)"
      :formatter="item.formatter"
    />
  </el-table>
</template>

<script>
import Axios from "@/api/perpetualContract.js";
import { handleSymbolImg } from "@/utils";
export default {
  name: "asset",
  props: {
    symbol: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      timer: null,
      tables: [
        {
          prop: "volume",
          label: "message.jiaoyi.qianbaoyue",
          formatter: this.priceFormatter,
        },
        {
          prop: "usable",
          label: "message.user.xian14",
          formatter: this.priceFormatter,
        },
        {
          prop: "lock_amount",
          label: "message.jiaoyi.suocang",
          formatter: this.priceFormatter,
        },
        {
          prop: "freeze_amount",
          label: "message.jiaoyi.dongjiejine",
          formatter: this.priceFormatter,
        },
      ],
      tableData: [],
    };
  },
  mounted() {
    this.getList();
    this.timer = setInterval(() => {
      this.getList();
    }, 2000);
  },

  unmounted() {
    clearInterval(this.timer);
  },
  methods: {
    handleSymbolImg,
    //获取列表数据
    getList() {
      const data = {
        // symbol: this.symbol,
        symbol: "",
      };

      Axios.getAssets(data).then((res) => {
        const data = res.data?.extends;
        this.tableData = data;
        this.tableData.map((item) => {
          item.freeze_amount = item.freeze_amount + item.frozenAmount;
        });
        this.tableData = data.filter((item) => item.volume > 0);
      });
    },
    priceFormatter(row, column, cellValue, index) {
      return bigDecimal.round(cellValue, 8);
    },
  },
};
</script>
<style scoped>
.symbol_name {
  display: flex;
}
.symbol-img {
  width: 20px;
  height: 20px;
  margin-right: 5px;
}
</style>
