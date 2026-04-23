<!-- 成分股 -->
<template>
  <div>
    <div class="ticker-table-box">
      <div class="title">
        <div class="title-item">
          <span>{{ $t("message.user.mingchengdaima") }}</span>
          <div class="to-sort">
            <i
              class="el-icon-caret-top"
              :class="[sortIndex == 1 ? 'color-red' : '']"
              @click="CurrencySort(1)"
            ></i>
            <i
              class="el-icon-caret-bottom"
              :class="[sortIndex == 2 ? 'color-red' : '']"
              @click="CurrencySort(2)"
            ></i>
          </div>
        </div>
        <div class="title-item">
          <span>{{ $t("message.user.zhanbichicang") }}</span>
          <div class="to-sort">
            <i
              class="el-icon-caret-top"
              :class="[sortIndex == 3 ? 'color-red' : '']"
              @click="CurrencySort(3)"
            ></i>
            <i
              class="el-icon-caret-bottom"
              :class="[sortIndex == 4 ? 'color-red' : '']"
              @click="CurrencySort(4)"
            ></i>
          </div>
        </div>
        <div class="title-item">
          <span>{{ $t("message.user.zuixin") }}</span>
          <div class="to-sort">
            <i
              class="el-icon-caret-top"
              :class="[sortIndex == 5 ? 'color-red' : '']"
              @click="CurrencySort(5)"
            ></i>
            <i
              class="el-icon-caret-bottom"
              :class="[sortIndex == 6 ? 'color-red' : '']"
              @click="CurrencySort(6)"
            ></i>
          </div>
        </div>
        <div class="title-item">
          <span>{{ $t("message.user.zhangdiefu") }}</span>
          <div class="to-sort">
            <i
              class="el-icon-caret-top"
              :class="[sortIndex == 5 ? 'color-red' : '']"
              @click="CurrencySort(5)"
            ></i>
            <i
              class="el-icon-caret-bottom"
              :class="[sortIndex == 6 ? 'color-red' : '']"
              @click="CurrencySort(6)"
            ></i>
          </div>
        </div>
      </div>
    </div>
    <div class="list-wrapper over-auto">
      <div class="list-item" v-for="(item, index) in showData" :key="index">
        <!-- 名称代码-->
        <div>
          <div class="symbol">{{ item?.name }}</div>
          <div class="desc">{{ item?.symbol }}</div>
        </div>
        <!-- 持仓占比-->
        <div>{{ item?.positionProportion }}%</div>
        <!-- 最新-->
        <div>{{ item?.close }}</div>
        <!-- 涨跌幅 -->
        <div
          class="ratio"
          :class="[item?.net_change > 0 ? 'color-up' : 'color-down']"
        >
          {{ item?.net_change > 0 ? "+" : "" }}{{ item?.net_change }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import Axios from "@/api/etf.js";
import CAxios from "@/api/currency.js";
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";

const allSymbolStr = ref("");
const infoList = ref([]);
const sortIndex = ref(0);
const timer = ref(null);
const route = useRoute();
const { t } = useI18n();

const props = defineProps({
  CurrencySort: {
    type: Function,
  },
});

const showData = ref([]);
onMounted(async () => {
  await getNameList();
  if (timer.value) {
    clearInterval(timer);
  }
  getRealTimeData();
  // timer.value = setInterval(() => {
  //   getRealTimeData();
  // }, 2000);
});

// 获取所有成分股名称和持仓占比
const getNameList = async () => {
  const symbol = route.params?.id || "GlobalETF500";
  let res = await Axios.getStock({ symbol });
  if (res.code == "0") {
    const symbols = res.data.map((it) => it.relatedStockSymbol);
    infoList.value = res.data; //保存数据
    allSymbolStr.value = symbols.join(",");
  }
};
//实时价格和涨幅
const getRealTimeData = () => {
  const symbol = allSymbolStr.value || "TSLA,SZ300750,00941";
  CAxios.getRealtime({ symbol }).then((res) => {
    if (res.code == "0") {
      showData.value = res.data.map((it) => {
        const curSymbolInfo = infoList.value.filter(
          (item) => item.relatedStockSymbol === it.symbol
        );
        it = {
          ...it,
          ...curSymbolInfo[0],
        };
        return it;
      }); //数据聚合

      //是否过滤
      // if (isFilter) {
      //   allListData = allListData.filter((item) => {
      //     return item.name.indexOf(filterValue.toUpperCase()) !== -1;
      //   });
      // }
      //排序
      // if (sortIndex == 1) {
      //   data.value = allListData.sort(
      //     sortLetterList("symbol")
      //   );
      // }
      // if (sortIndex == 2) {
      //   data.value = allListData.sort(
      //     sortLetterTwoList("symbol")
      //   );
      // }
      // if (sortIndex == 3) {
      //   data.value= allListData.sort(
      //     orderListAsc("close")
      //   );
      // }
    }
  });
};

//获取事实数据
</script>

<style scoped>
.ticker-table-box {
  padding: 8px 16px;
  margin: 0 16px 12px 16px;
  background: #0b0e11;
}

.ticker-table-box .title {
  display: flex;
  /* justify-content: space-between; */
}

.ticker-table-box .title-item {
  display: flex;
  font-size: 12px;
  color: #929aa5;
  width: 18%;
}

.ticker-table-box .title-item .to-sort {
  display: flex;
  flex-direction: column;
  /*font-size: 8px;*/
}

:deep(.to-sort) .el-icon-caret-top,
:deep(.to-sort) .el-icon-caret-bottom {
  font-size: 14px;
  transform: scale(0.8);
}

.over-auto {
  overflow: auto;
}
.list-wrapper {
  padding: 0 16px;
}

.list-wrapper {
  scrollbar-width: none;
  /* firefox */
  -ms-overflow-style: none;
  /* IE 10+ */
}

.list-wrapper::-webkit-scrollbar {
  display: none;
  /* Chrome Safari */
}

.list-item {
  display: flex;
  margin-bottom: 10px;
  font-size: 12px;
}

.list-item > div {
  width: 18%;
}

.list-item .symbol {
  color: #929aa5;
}
</style>
