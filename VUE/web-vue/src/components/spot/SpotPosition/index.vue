<template>
  <div class="quotes-footer">
    <!-- tabs -->
    <div class="quotes-footer-title">
      <div
        v-for="(tab, index) in tabs"
        :key="index"
        @click="handleTabClick(index, tab.value)"
        :class="{ 'active-tab': tabIndex === index }"
      >
        <span>{{ tab.name }}</span>
        <span v-if="index === 0">({{ contentList[0].length }})</span>
      </div>
    </div>

    <div class="quotes-footer-main">
      <!--未登录-->
      <template v-if="!existToken">
        <div class="no-login">
          <span style="color: #1d91ff" @click="$router.push('/login')">{{
            $t("message.home.denglu")
          }}</span>
          <span style="margin: 0 4px">{{ $t("message.home.or") }}</span>
          <span
            style="margin: 0 4px; color: #1d91ff"
            @click="$router.push('/register')"
            >{{ $t("message.home.lijizhuce") }}</span
          >
          <span>{{ $t("message.home.trading") }}</span>
        </div>
      </template>
      <!-- 数据 -->
      <template v-else>
        <position-table
          ref="layout"
          @getTime="getTime"
          :current-index="tabIndex"
          @cancelOrder="cancelOrder"
          :tab-list="tabList[tabIndex]"
          :content-list="contentList[tabIndex]"
          :unit="unit"
          :pageType="pageType"
        />
      </template>
    </div>
    <!-- 分页 -->

    <Pagination
      class="pagitation-wrapper"
      style="margin: 20px 0"
      v-if="typeValue == 'hisorders' || typeValue == 'opponent'"
      :noPre="isTop"
      :noNext="isNext"
      :pageNum="pageNum"
      @changePageNum="changePageNum"
    />
  </div>
</template>

<script>
import PositionTable from "./positionTable.vue";
import Axios from "@/api/currency.js";
import quotesAxios from "@/api/quotes.js";
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";
import { ElMessage } from "element-plus";
import Pagination from "@/components/common/pagination.vue";
export default {
  name: "spotPosition",
  props: {
    paramsType: {
      type: String,
      default: "indices",
    },
    pageType: {
      type: String,
    },
    unit: {
      type: String,
    },
  },
  components: {
    PositionTable,
    Pagination,
  },
  data() {
    return {
      checked1: false,
      checked2: false,
      pageNum: 1,
      type: "orders",
      tabs: [
        { name: this.$t("message.home.dangqianweituo"), value: "orders" },
        { name: this.$t("message.home.lishiweituo"), value: "hisorders" },
        { name: this.$t("message.home.lishichengjiao"), value: "hisorders" },
        { name: this.$t("message.home.zichanguanli"), value: "pairs" },
      ],
      tabIndex: 0,
      tabList: [
        [
          { name: this.$t("message.home.shijian") },
          { name: this.$t("message.home.tradingPair") },
          { name: this.$t("message.jiaoyi.leixing") },
          { name: this.$t("message.home.direction"), dropDown: false },
          { name: this.$t("message.home.jiage") },
          { name: this.$t("message.home.shuliang") },
          { name: this.$t("message.home.completeness") },
          { name: this.$t("message.home.chengjiaojine") },
          { name: this.$t("message.jiaoyi.chufajiage") },
          { name: this.$t("message.jiaoyi.chexiao") },
        ],
        [
          { name: this.$t("message.home.shijian") },
          { name: this.$t("message.home.tradingPair") },
          { name: this.$t("message.jiaoyi.leixing") },
          { name: this.$t("message.home.direction"), dropDown: false },
          { name: this.$t("message.home.pingjunjiage") },
          { name: this.$t("message.jiaoyi.weituojiage") },
          { name: this.$t("message.user.chengjiaoshuliang") },
          { name: this.$t("message.home.chengjiaojine") },
          { name: this.$t("message.jiaoyi.chufajiage") },
          { name: this.$t("message.user.zhuangtai") },
        ],
        [
          { name: this.$t("message.home.shijian") },
          { name: this.$t("message.home.tradingPair") },
          { name: this.$t("message.home.direction"), dropDown: false },
          { name: this.$t("message.home.chengjiaojunjia") },
          { name: this.$t("message.user.chengjiaoshuliang") },
          { name: this.$t("message.home.shouxufei") },
          // { name: this.$t('message.home.role') },
          { name: this.$t("message.home.chengjiaojine") },
        ],
        [
          { name: this.$t("message.hangqing.bizhong") },
          { name: this.$t("message.jiaoyi.qianbaoyue") },
          { name: this.$t("message.user.xian14") },
          { name: this.$t("message.jiaoyi.suocang"), value: "pairs" },
          { name: this.$t("message.jiaoyi.dongjiejine") },
          // { name: this.$t('message.home.currency') },
          // { name: this.$t('message.home.total') },
          // { name: this.$t('message.home.keyongzichan') },
          // { name: this.$t('message.home.orderLock') },
          // { name: `BTC ${this.$t('message.home.valuation')}`, textRight: true },
        ],
      ],
      contentList: [[], [], [], []],
      orderType: "orders",
      endTime: "",
      startTime: "",
      typeValue: "orders",
      timer: null,
      isNext: false,
      isTop: false,
    };
  },
  created() {
    if (this.existToken) {
      this.init();
      this.timer = setInterval(() => {
        this.init();
      }, 2000);
    }
  },
  watch: {
    "$route.params.id"() {
      if (this.existToken) {
        this.init();
      }
    },
  },
  unmounted() {
    clearInterval(this.timer);
    this.timer = null
  },
  methods: {
    // 开始查询
    init() {
      if(this.$route.params.id == 'undefined'){
        return;
      }
      let obj = {
        page_no: this.pageNum,
        type: this.orderType,
        symbolType: this.paramsType,
        symbol: this.$route.params.id,
      };
      // 历史委托， 限价
      if (this.tabIndex == 1) {
        obj.orderPriceType = "limit";
      }
      //历史成交
      if (this.tabIndex == 2) {
        obj.orderPriceType = "opponent";
      }

      if (this.tabIndex == 1 || this.tabIndex == 2) {
        if (this.startTime) {
          obj.startTime = this.startTime;
        }
        if (this.endTime) {
          obj.endTime = this.endTime;
        }
      }
      // 获取交易记录
      Axios.currencyTradeRecord(obj).then((res) => {
        if (res.code == 0) {
          if (res.data.length == 0 || res.data.length < 10) {
            this.isNext = true;
          } else {
            this.isNext = false;
          }
          this.contentList[this.tabIndex] = res.data;
        }
      });
    },
    //撤单
    cancelOrder(item) {
      Axios.cancelCurrencyOrder({
        order_no: item.order_no,
      }).then((res) => {
        ElMessage({
          message: this.$t("message.home.caozuochenggong"),
          type: "success",
        });
        this.init();
      });
    },
    //选择时间
    getTime(obj) {
      this.pageNum = 1;
      this.startTime = obj.startTime;
      this.endTime = obj.endTime;
      this.init();
    },
    handleTabClick(index, value) {
      clearInterval(this.timer);
      this.startTime = "";
      this.endTime = "";
      if (index == 1 || index == 2) {
        this.$refs.layout.clearTime();
      }
      this.tabIndex = index;
      this.typeValue = value;
      // 资产
      if (value === "pairs") {
        quotesAxios.getPairsWallet().then((res) => {
          const assetList = res.data.extends;
          let list = [];
          if (assetList.length) {
            list = assetList.filter((it) => it.symbol && it.volume > 0);
          }
          this.contentList[this.tabIndex] = list;
        });
      } else {
        this.pageNum = 1;
        this.orderType = value;
        this.init();
        this.timer = setInterval(() => {
          this.init();
        }, 2000);
      }
    },
    refreshApi() {
      this.handleTabClick(this.tabIndex, this.typeValue);
    },
    //分页
    changePageNum(type) {
      if (type == "next") {
        if (!this.isNext) {
          this.pageNum = this.pageNum + 1;
          this.init();
        }
      } else {
        if (!this.isTop && this.pageNum > 1) {
          this.pageNum = this.pageNum - 1;
          this.init();
        }
      }
    },
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
};
</script>

<style scoped>
.quotes-footer {
  padding: 12px 14px;
  border-top: 1px solid #24272c;
  background: #171a1e;
}

.quotes-footer-title {
  position: relative;
  display: flex;
  font-size: 16px;
  font-weight: 500;
  color: #929aa5;
  user-select: none;
}

.quotes-footer-title > div {
  margin-right: 40px;
  cursor: pointer;
}

.quotes-footer-title .active-tab {
  color: #1d91ff;
}

.no-login {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 239px;
  font-size: 14px;
  color: #fff;
  cursor: pointer;
}

.checkbox-wrapper {
  position: absolute;
  top: 50%;
  right: 0px;
  transform: translateY(-50%);
  margin-right: 0 !important;
}

:deep（.checkbox-wrapper） .el-checkbox__label {
  font-size: 12px;
  color: #929aa5;
}

:deep（.checkbox-wrapper） .el-checkbox__inner {
  transform: scale(0.7);
  border: 1px solid #5f6672;
  background-color: #171a1e;
}
</style>
