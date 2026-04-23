<!-- 资产 -->
<template>
  <div class="right-view">
    <!-- 头部 -->
    <div class="right-header">
      <div class="right-header-box">
        <div>{{ $t("message.user.zichanzonglan") }}</div>
      </div>
    </div>
    <div class="padding-left-right20">
      <!-- 资产 -->
      <el-table
        :data="assetData"
        class="width100"
        :header-cell-style="getRowClass"
        :row-style="rowStyles"
        style="margin: 20px 0"
        :empty-text="$t('message.home.noData')"
      >
        <el-table-column
          prop="create_time_ts"
          :label="$t('message.hangqing.bizhong')"
        >
          <template #default="scope">
            <div class="flex items-center">
              <img
                class="symbol-img"
                :src="handleSymbolImg(scope.row.symbol)"
              />
              {{ scope.row.symbol.toUpperCase() }}
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="name" :label="$t('message.jiaoyi.qianbaoyue')">
          <template #default="scope">
            <div>{{ Number(scope.row.volume).toFixed(8) }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="amount" :label="$t('message.user.xian14')">
          <template #default="scope">
            <div>{{ Number(scope.row.usable).toFixed(8) }}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="trade_avg_price"
          :label="$t('message.jiaoyi.suocang')"
        >
          <template #default="scope">
            <div>{{ Number(scope.row.lock_amount).toFixed(8) }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="fee" :label="$t('message.jiaoyi.dongjiejine')">
          <template #default="scope">
            <div>{{ Number(scope.row.freeze_amount).toFixed(8) }}</div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <Pagination
        style="margin: 20px 0"
        :noPre="noPre"
        :noNext="noNext"
        :pageNum="pageNum"
        @changePageNum="changePageNum"
      />
    </div>
  </div>
</template>

<script>
import { handleSymbolImg, mergeSort } from "@/utils";
import Pagination from "@/components/common/pagination.vue";
import quotesAxios from "@/api/quotes.js";

export default {
  components: { Pagination },
  name: "AssetOverview",
  data() {
    return {
      assetData: [],
      pageNum: 1,
      noNext: false,
      noPre: false,
      totalPageNum: 0,
    };
  },
  mounted() {
    this.getAssets();
  },
  methods: {
    handleSymbolImg,
    //总账户资产
    getAssets() {
      const params = {
        pageNum: this.pageNum,
      };
      quotesAxios.getPairsWallet(params).then((res) => {
        const assetList = res.data.extends;
        let list = [];
        if (assetList.length) {
          list = assetList.filter((it) => it.symbol && it.volume > 0);
        }

        this.assetData = list;
      });
    },
    //给表头设置背景颜色
    getRowClass({ rowIndex, columnIndex }) {
      if (rowIndex == 0) {
        return { background: "#f8f8f9" };
      }
    },
    //行高修改，需以对象形式返回
    rowStyles({ row, rowIndex }) {
      let styleJson = {
        height: "50px",
      };
      return styleJson;
    },

    //分页
    changePageNum(type) {
      if (type == "next") {
        if (!this.noNext) {
          this.pageNum = this.pageNum + 1;
          this.getAssets();
        }
      } else {
        if (!this.noPre && this.pageNum > 1) {
          this.pageNum = this.pageNum - 1;
          this.getAssetst();
        }
      }
    },
  },
};
</script>

<style scoped lang="css">
.img-style {
  width: 20px;
  height: 20px;
}

.symbol-img {
  width: 20px;
  margin-right: 5px;
}
</style>
