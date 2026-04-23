<!-- 合约账户 -->
<template>
  <div class="right-view">
    <!-- 头部 -->
    <div class="right-header">
      <div class="right-header-box">
        <div>{{ $t(`message.user.${headerMap[pageType]}`) }}</div>
        <div>
          <el-button
            type="primary"
            style="width: 100px; margin-right: 10px"
            @click="gotoPage('/recharge')"
            >{{ $t("message.user.chongzhi") }}</el-button
          >
          <button class="light-grey-button" @click="gotoPage('/withdraw')">
            {{ $t("message.user.tixian") }}
          </button>
          <button class="light-grey-button" @click="gotoPage('/exchange')">
            {{ $t("message.user.duihuan") }}
          </button>
        </div>
      </div>
    </div>
    <div class="padding-left-right20">
      <div class="margin-top-bottom20">
        <!-- 总资产 -->
        <total-assets
          :pageType="pageType"
          :paramsType="paramsType"
        ></total-assets>
        <!-- tab切换 -->
        <div class="tab-customize-box">
          <div
            class="tab-customize"
            :class="heYueType == 0 ? 'tabActive' : ''"
            @click="ChooseHeYueType(0)"
          >
            {{ $t("message.user.yongxuheyue") }}
          </div>
          <div
            class="tab-customize"
            :class="heYueType == 1 ? 'tabActive' : ''"
            @click="ChooseHeYueType(1)"
          >
            {{ $t("message.home.jiaogeheyue") }}
          </div>
          <div
            v-if="pageType !== 'forex'"
            class="tab-customize"
            :class="heYueType == 2 ? 'tabActive' : ''"
            @click="ChooseHeYueType(2)"
          >
            {{ $t(`message.home.${spotLabel}`) }}
          </div>
        </div>
      </div>
      <!-- 永续合约列表 -->
      <el-table
        v-if="heYueType == 0"
        :data="contractData"
        class="width100"
        :header-cell-style="getRowClass"
        :row-style="rowStyles"
        style="margin: 20px 0"
        :empty-text="$t('message.home.noData')"
      >
        <el-table-column
          prop="create_time_ts"
          :label="$t('message.home.kaicangshijian')"
          :formatter="getFormatTime"
        >
        </el-table-column>
        <el-table-column prop="name" :label="$t('message.user.heyue')">
          <template #default="scope">
            <div>{{ scope.row.name }} {{ $t("message.user.yongxu") }}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="amount"
          :label="$t('message.user.shuliang')"
        ></el-table-column>
        <el-table-column
          prop="trade_avg_price"
          :label="$t('message.home.jiage')"
        >
          <!-- 当前持仓和限价持仓的价格 -->
          <template #default="scope">
            <div>{{ scope.row.trade_avg_price || scope.row.price }}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="fee"
          :label="$t('message.user.shouxufei')"
        ></el-table-column>
        <el-table-column
          prop="order_price_type"
          :label="$t('message.jiaoyi.leixing')"
        >
          <template #default="scope">
            {{
              scope.row.order_price_type == "limit"
                ? $t("message.home.xianjiaweituo")
                : $t("message.home.shijiaweituo")
            }}
          </template>
          <!-- :label="$t('message.user.weishixianyingkui(shouyilv)')" -->
          <!-- <template #default="scope">
            <div :class="scope.row.profit > 0 ? 'green' : 'red'">
              <span>{{ Number(scope.row?.profit).toFixed(2) }} </span>
              <span>({{ scope.row.change_ratio }}%)</span>
            </div>
          </template> -->
        </el-table-column>
        <el-table-column prop="" :label="$t('message.home.caozuo')">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="goContractPage(scope.row.symbol)"
              >{{ $t("message.home.home6") }}</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <!-- 交割合约列表 -->
      <el-table
        v-if="heYueType == 1"
        :data="deliveryData"
        class="width100"
        :empty-text="$t('message.home.noData')"
        :header-cell-style="getRowClass"
        :row-style="rowStyles"
        style="margin: 20px 0"
      >
        <el-table-column
          prop="open_time"
          :label="$t('message.home.kaicangshijian')"
        >
          <template #default="scope">
            <div>{{ scope.row.open_time }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="name" :label="$t('message.user.heyue')">
          <template #default="scope">
            <div>{{ scope.row.name }} {{ $t("message.user.jiaoge") }}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="amount"
          :label="$t('message.user.shuliang')"
        ></el-table-column>
        <el-table-column
          prop="open_price"
          :label="$t('message.user.kaicangjiage')"
        ></el-table-column>
        <!-- <el-table-column prop="mark_price" label="标记价格"></el-table-column> -->
        <el-table-column prop="" :label="$t('message.user.weishixianyingkui')">
          <template #default="scope">
            <div :class="scope.row.profit > 0 ? 'green' : 'red'">
              <span>{{ Number(scope.row?.profit).toFixed(2) }} </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="time_num"
          :label="$t('message.home.jiaogeshijian')"
        >
          <template #default="scope">
            <div>{{ scope.row.time_num }} {{ scope.row.time_unit }}</div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 现货 -->
      <el-table
        v-if="heYueType == 2"
        :data="spotData"
        class="width100"
        :header-cell-style="getRowClass"
        :row-style="rowStyles"
        style="margin: 20px 0"
        :empty-text="$t('message.home.noData')"
      >
        <el-table-column
          prop="create_time"
          :label="$t('message.home.kaicangshijian')"
        >
          <template #default="scope">
            <div>{{ scope.row.create_time }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="name" :label="$t('message.home.jiaoyidui')">
          <template #default="scope">
            <div class="flex-row-center">
              <span class="itemContentCenter" style="margin-left: 5px">{{
                scope.row?.name
              }}</span>
            </div>
          </template>
        </el-table-column>
        <!-- 类型 -->
        <el-table-column
          prop="order_price_type"
          :label="$t('message.jiaoyi.leixing')"
        >
          <template #default="scope">
            <div>
              {{
                scope.row.order_price_type == "limit"
                  ? $t("message.home.xianjiaweituo")
                  : $t("message.home.shijiaweituo")
              }}
            </div>
          </template>
        </el-table-column>
        <!-- 方向 -->

        <el-table-column prop="offset" :label="$t('message.home.fangxiang')">
          <template #default="scope">
            <div :class="[scope.row.offset == 'open' ? 'buy' : 'sell']">
              {{
                scope.row.offset == "open"
                  ? $t("message.jiaoyi.mairu")
                  : $t("message.home.maichu")
              }}
            </div>
          </template>
        </el-table-column>
        <!-- 委托价格 -->
        <el-table-column
          prop="price"
          :label="$t('message.jiaoyi.weituojiage')"
        ></el-table-column>
        <!-- 成交数量 -->
        <el-table-column
          prop="volume"
          :label="$t('message.user.shuliang')"
        ></el-table-column>
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
import totalAssets from "./totalAssets.vue";
import dayjs from "dayjs";
import { mapState } from "pinia";
import { handleSymbolImg, mergeSort } from "@/utils";
import { useUserStore } from "@/store/user";
import Pagination from "@/components/common/pagination.vue";
import Axios2 from "@/api/wallet.js";

export default {
  components: { totalAssets, Pagination },
  name: "Accounts",
  props: {
    pageType: {
      type: String,
      default: "etf", //页面类型
    },
    paramsType: {
      type: String,
      default: "indices", //
    },
    spotLabel: {
      type: String,
      default: "xianhuojiaoyishouye", //默认
    },
  },
  data() {
    return {
      total: 0,
      heYueType: 0,
      contractData: [],
      deliveryData: [],
      spotData: [],
      contractTotalData: [],
      deliveryTotalData: [],
      spotTotalData: [],
      timer: null, //定时器
      futureTimer: null, //交割合约定时器
      spotTimer: null, //
      pageNum: 1, // 切换
      noNext: false,
      noPre: false,
      totalPageNum: 0,
      headerMap: {
        etf: "etfZhanghu",
        coin: "shuzihuobiZhanghu",
        forex: "waihuiZhanghu",
        usStocks: "meiguZhanghu",
        cnStocks: "AguZhanghu",
        hkStocks: "gangguZhanghu",
        twStocks: "taiguZhanghu",
      },
    };
  },
  mounted() {
    this.getOrderList();
    this.timer = setInterval(() => {
      if (this.existToken) {
        this.getOrderList();
      }
    }, 2000);

    this.getAssetsAll();
  },
  methods: {
    handleSymbolImg,
    mergeSort,
    getFormatTime(row, column, cellValue, index) {
      return dayjs.unix(cellValue).format("YYYY-MM-DD HH:mm:ss");
    },
    gotoPage(path) {
      this.$router.push(path);
    },
    //总账户资产
    getAssetsAll() {
      Axios2.getAllAssets().then((res) => {
        if (res.code == 0) {
          this.total = res.data.total;
        }
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
    ChooseHeYueType(index) {
      this.heYueType = index;
      this.pageNum = 1;
      if (this.heYueType == 0) {
        this.getOrderList(); //先调用，然后再轮训
        this.timer = setInterval(() => {
          this.getOrderList();
        }, 2000);
        clearInterval(this.futureTimer);
        this.futureTimer = null;
        return;
      }
      if (this.heYueType == 1) {
        if (this.existToken) {
          this.getFutureList();
        }
        clearInterval(this.timer);
        this.timer = null;
        return;
      }
      if (this.heYueType == 2) {
        if (this.existToken) {
          this.getSpotList();
        }
        clearInterval(this.timer);
        this.timer = null;
      }
    },
    //获取永续合约持有仓位列表+委托持仓，没有分页，前端分页
    async getOrderList() {
      if (this.existToken) {
        const data = {
          page_no: this.pageNum,
          type: "orders",
          symbolType: this.paramsType,
        };

        Promise.all([
          Axios2.getOrderList(data),
          Axios2.getOrderApplyList(data),
        ]).then((res) => {
          const marketList = res[0].data;
          const limitList = res[1].data;
          const sortList = mergeSort([...marketList, ...limitList]); // 按照时间排序，create_time_ts
          this.contractData = sortList.slice(0, 10);
          this.contractTotalData = sortList;
          const total = Math.ceil(sortList.length / 10);
          this.totalPageNum = parseInt(sortList.length / 10);
          this.noNext = this.pageNum > total || this.pageNum == total;
        });
      }
    },
    //交割合约持有仓位列表
    getFutureList() {
      if (this.existToken) {
        const data = {
          page_no: this.pageNum,
          type: "orders",
          symbolType: this.paramsType,
        };

        Axios2.getFutureList(data).then((res) => {
          this.deliveryTotalData = res.data;
          this.deliveryData = res.data.slice(0, 10); //裁剪
          const total = Math.ceil(res.data.length / 10);
          this.totalPageNum = parseInt(res.data.length / 10);
          this.noNext = this.pageNum > total || this.pageNum == total;
        });
      }
    },
    //新货持有仓位列表
    getSpotList() {
      if (this.existToken) {
        const data = {
          page_no: this.pageNum,
          type: "orders",
          isAll: true,
          symbolType: this.paramsType,
        };

        Axios2.getSpotList(data).then((res) => {
          this.spotTotalData = res.data;
          this.spotData = res.data.slice(0, 10); //裁剪
          const total = Math.ceil(res.data.length / 10);
          this.totalPageNum = parseInt(res.data.length / 10);
          this.noNext = this.pageNum > total || this.pageNum == total;
        });
      }
    },

    // 合约页面
    goContractPage(symbol) {
      const routerName = "sustainable";
      const time = new Date().getTime();
      this.$router.push(
        `/${this.pageType}/constract/${symbol}?timestamp=${time}&RouterName=${routerName}`
      );
    },
    //分页
    changePageNum(type) {
      if (type == "next") {
        if (this.pageNum > this.totalPageNum) {
          return;
        }
        if (!this.noNext) {
          this.pageNum = this.pageNum + 1;
        }
      } else {
        if (!this.noPre && this.pageNum > 1) {
          this.pageNum = this.pageNum - 1;
        }
      }
      //取数据,永续
      const start = (this.pageNum - 1) * 10;
      let end = start + 10;
      if (this.heYueType == 0) {
        this.contractData = this.contractTotalData.slice(start, end);
      } else if (this.heYueType == 1) {
        this.deliveryData = this.deliveryTotalData.slice(start, end);
      } else {
        this.spotData = this.spotTotalData.slice(start, end);
      }
    },
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  //销毁定时器
  unmounted() {
    clearInterval(this.timer);
    clearInterval(this.futureTimer);
    clearInterval(this.spotTimer);
    this.timer = null;
    this.futureTimer = null;
    this.spotTimer = null;
  },
};
</script>

<style scoped lang="css">
.img-style {
  width: 20px;
  height: 20px;
}
</style>
