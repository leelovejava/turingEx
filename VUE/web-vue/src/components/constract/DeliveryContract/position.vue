<template>
  <!-- 交割合约仓位列表页面  -->
  <div>
    <div class="delivery-page">
      <div class="delegate-left">
        <div class="account-box">
          <div class="account-tab-box">
            <div class="okui-tabs account-tab">
              <div
                class="okui-tabs-pane-list okui-tabs-pane-list-lg okui-tabs-pane-list-grey okui-tabs-pane-list-underline account-tab-header"
              >
                <!-- tab栏 -->
                <div class="okui-tabs-pane-list-container">
                  <!-- 持有仓位 -->
                  <div
                    @click="changeType('orders')"
                    :class="type == 'orders' ? 'position-tab-active' : ''"
                    class="mr-16"
                  >
                    {{ $t("message.home.chiyoucangwei") }}({{ orderNumber }})
                  </div>
                  <!-- 历史仓位 -->
                  <div
                    @click="changeType('hisorders')"
                    :class="type == 'hisorders' ? 'position-tab-active' : ''"
                    class="mr-16"
                  >
                    {{ $t("message.home.lishicangwei") }}
                  </div>
                  <!-- 资产管理 -->
                  <div
                    @click="changeType('assets')"
                    :class="type == 'assets' ? 'position-tab-active' : ''"
                  >
                    {{ $t("message.home.zichanguanli") }}
                  </div>
                </div>
              </div>
              <!-- 持有或历史仓位-->
              <template v-if="type == 'hisorders' || type == 'orders'">
                <div
                  class="delegate-box"
                  :class="[currencyData.length > 0 ? 'h400' : 'h20']"
                >
                  <!-- 表头 -->
                  <div class="delegate-title">
                    <div>{{ $t("message.home.bizhong") }}</div>
                    <div class="flex-2">
                      {{ $t("message.home.kaicangshijian") }}
                    </div>
                    <div>{{ $t("message.home.fangxiang") }}</div>
                    <div>{{ $t("message.home.zhangshu") }}</div>
                    <div>{{ $t("message.home.shouxufei") }}</div>
                    <div>{{ $t("message.home.jiaogeshijian") }}</div>
                    <div>{{ $t("message.home.goumaijia") }}</div>
                    <div>{{ $t("message.home.jiesuanjia") }}</div>
                    <div>{{ $t("message.home.yingkui") }}</div>
                    <div class="flex-2">
                      {{ $t("message.home.daoqishijian") }}
                    </div>
                    <div class="flex-2" v-if="type == 'orders'">
                      {{ $t("message.home.caozuo") }}
                    </div>
                  </div>
                  <!-- 空数据 -->
                  <div
                    class="delegate-content empty-box"
                    v-if="currencyData.length == 0"
                  >
                    <span class="empty-desc">{{
                      $t("message.home.zanwujilu")
                    }}</span>
                  </div>
                  <!-- 表格内容 -->
                  <div v-else class="delegate-content">
                    <div
                      class="delegate-item"
                      v-for="(item, i) in currencyData"
                      :key="i"
                    >
                      <div>{{ item.name }}</div>
                      <div class="flex-2">
                        {{ getFormatTime(item.open_time) }}
                      </div>
                      <div v-if="item.direction == 'buy'" class="green">
                        {{ $t("message.jiaoyi.zuoduomairu") }}
                      </div>
                      <div v-if="item.direction == 'sell'" class="red">
                        {{ $t("message.jiaoyi.zuokongmairu") }}
                      </div>
                      <div v-if="!item.direction">--</div>
                      <div>{{ item.amount }}</div>
                      <div>{{ item.fee }}</div>
                      <div>{{ item.time_num }} {{ item.time_unit }}</div>
                      <div>{{ item.open_price }}</div>
                      <div>{{ item.close_price }}</div>
                      <div v-if="item.profit < 0" class="red">
                        {{ item.profit }}
                      </div>
                      <div v-else class="green">
                        {{ item.profit }}
                      </div>

                      <!-- <div>{{ item.order_no }}</div>
                            <div v-if="item.state=='submitted'">已提交</div>
                            <div v-if="item.state=='created'">已完成</div> -->
                      <!--                    <div>{{ item.settlement_time }}</div>-->
                      <div class="flex-2">
                        {{ getFormatTime(item.settlement_time) }}
                      </div>
                      <div
                        v-if="type == 'orders'"
                        class="mouse-cursor"
                        @click="openDetail(item)"
                      >
                        <el-button class="btn" size="min" type="primary">{{
                          $t("message.home.xiangqing")
                        }}</el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </template>
              <!-- 资产管理 -->
              <template v-else>
                <div
                  class="delegate-box"
                  :class="[currencyData.length > 0 ? 'h400' : 'h20']"
                >
                  <div class="delegate-title">
                    <div>{{ $t("message.jiaoyi.bizhong") }}</div>
                    <div>{{ $t("message.jiaoyi.qianbaoyue") }}</div>
                    <div>{{ $t("message.jiaoyi.keyongjine") }}</div>
                    <div>{{ $t("message.jiaoyi.suocang") }}</div>
                    <div>{{ $t("message.jiaoyi.dongjiejine") }}</div>
                  </div>
                  <div
                    class="delegate-content empty-box"
                    v-if="currencyData.length == 0"
                  >
                    <span class="empty-desc">{{
                      $t("message.home.zanwujilu")
                    }}</span>
                  </div>
                  <div v-else class="delegate-content">
                    <div
                      v-for="(item = {}, index) in currencyData"
                      class="delegate-item"
                      :key="index"
                    >
                      <img
                        class="symbol-img"
                        :src="handleSymbolImg(item.symbol)"
                      />
                      <div>{{ item.symbol.toUpperCase() }}/USD</div>
                      <div>{{ Number(item.volume).toFixed(8) }}</div>
                      <div>{{ Number(item.usable).toFixed(8) }}</div>
                      <div>{{ Number(item.usable).toFixed(8) }}</div>
                      <div>
                        {{ (item.frozenAmount + item.frozenAmount).toFixed(8) }}
                      </div>
                    </div>
                  </div>
                </div>
              </template>
              <!-- 分页 -->
              <Pagination
                class="pagitation-wrapper"
                style="margin-top: -20px"
                v-if="
                  (type == 'hisorders' || type == 'orders') &&
                  currencyDataLength
                "
                :noPre="noPre"
                :noNext="noNext"
                :pageNum="pageNum"
                @changePageNum="changePageNum"
              />

              <div class="no-login" v-if="!existToken">
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
            </div>
          </div>
        </div>
      </div>
      <div class="delegate-right">
        <div class="assets-title">{{ $t("message.jiaoyi.zican") }}</div>
        <div class="assets-btns">
          <el-button type="info" size="small" @click="goRouter('/recharge')">{{
            $t("message.user.chongzhi")
          }}</el-button>
          <el-button type="info" size="small" @click="goRouter('/exchange')">{{
            $t("message.user.duihuan")
          }}</el-button>
          <el-button type="info" size="small" @click="goRouter('/withdraw')">{{
            $t("message.user.tixian")
          }}</el-button>
        </div>
        <div class="money-box" v-if="existToken">
          <div class="money-left">{{ $t("message.jiaoyi.qianbaoyue") }}</div>
          <div class="money-right">{{ moneyInfo.money_wallet }}USD</div>
        </div>
        <div class="money-box" v-if="existToken">
          <div class="money-left">
            {{ $t("message.jiaoyi.weishixianyinkui") }}
          </div>
          <div class="money-right">{{ moneyInfo.money_futures_profit }}USD</div>
        </div>
      </div>
    </div>
    <!-- 订单页弹窗   :pageData="newPageData"-->
    <delivery-detail-dialog
      :typeValue="2"
      :dialogVisible="dialogVisible"
      @closeDialog="handleCloseDetailDialog"
    ></delivery-detail-dialog>
  </div>
</template>
<script>
import Axios from "@/api/delivery.js";
import dayjs from "dayjs";
import bus from "vue3-eventbus";
import deliveryDetailDialog from "./DetailDialog.vue";
import Pagination from "@/components/common/pagination.vue";

import quotesAxios from "@/api/quotes.js";
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";
import AxiosCurrency from "@/api/currency.js";
import { handleSymbolImg } from "@/utils";

export default {
  name: "deliveryDelegateList",
  components: { deliveryDetailDialog, Pagination },
  props: {
    pageData: {
      type: Object,
      default: function () {
        return {};
      },
    },
  },

  watch: {
    pageData(val) {
      this.newPageData = val;
    },
    "$route.params.id"() {
      if (this.existToken) {
        this.getList();
      }
    },
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  data() {
    return {
      type: "orders", //类型
      currencyData: [],
      symbol: "btc",
      currencyDataLength: 0,
      pageNum: 1,
      timer: undefined, //定时器
      timer1: undefined,
      dialogVisible: false,
      moneyInfo: {
        money_wallet: 0,
        money_futures_profit: 0,
      },
      orderNumber: 0,
      noNext: false,
      noPre: false,
    };
  },
  mounted() {
    if (this.existToken) {
      this.getCurrencyPaypal();
      this.getList(this.type);
      // this.timer1 = setInterval(() => {
      //   this.getCurrencyPaypal();
      // }, 2000);
      // this.timer = setInterval(() => {
      //   this.getList(this.type);
      // }, 1000);
    }
  },
  //销毁定时器
  unmounted() {
    clearInterval(this.timer);
    clearInterval(this.timer1);
  },
  methods: {
    handleSymbolImg,
    getFormatTime(time) {
      return dayjs.unix(time).format("YYYY-MM-DD HH:mm:ss");
    },
    //路由跳转
    goRouter(params) {
      this.$router.push(params);
    },
    // 获取钱包
    getCurrencyPaypal() {
      AxiosCurrency.currencyPaypal().then((res) => {
        if (res.code == 0) {
          this.moneyInfo = res.data;
        }
      });
    },
    sortData(a, b) {
      return new Date(b.open_time).getTime() - new Date(a.open_time).getTime();
    },

    // 切换tab
    changeType(type) {
      this.currencyData = [];
      this.currencyDataLength = 0;
      if (!this.existToken) {
        return;
      }
      this.type = type;
      this.pageNum = 1;
      if (this.type == "orders") {
        this.getList(this.type);
        this.timer = setInterval(() => {
          this.getList(this.type);
        }, 1000);
      } else if (this.type == "hisorders") {
        clearInterval(this.timer);
        this.getList(this.type);
      } else {
        clearInterval(this.timer);
        this.getPairsWalletList();
      }
    },
    // 获取仓位数据
    getList(type) {
      if(this.$route.params.id == 'undefined'){
        return
      }
      if (this.existToken) {
        Axios.futuresList({
          page_no: this.pageNum,
          type: this.type,
          symbol: this.$route.params.id,
        }).then((res) => {
          if (res.code == 0) {
            if (this.type == "orders") {
              this.orderNumber = res?.total || res.data.length;
            }
            this.currencyData = res.data.sort(this.sortData);
            this.currencyDataLength = res.data.length;
            if (this.currencyDataLength == 0 || this.currencyDataLength < 10) {
              this.noNext = true;
            } else {
              this.noNext = false;
            }
          }
        });
      }
    },
    // 获取资产
    getPairsWalletList() {
      quotesAxios.getPairsWallet().then((res) => {
        const list = res.data.extends;
        this.currencyData = list.filter(
          (item) => item.symbol && item.volume > 0
        );
        this.currencyDataLength = list.length;
      });
    },
    openDetail(item) {
      let obj = {
        order_no: item.order_no,
        symbol: item.symbol,
      };
      bus.emit("showDetail", obj);
      this.dialogVisible = true;
    },
    //分页
    changePageNum(type) {
      if (type == "next") {
        if (!this.noNext) {
          this.pageNum = this.pageNum + 1;
          this.getList();
        }
      } else {
        if (!this.noPre && this.pageNum > 1) {
          this.pageNum = this.pageNum - 1;
          this.getList();
        }
      }
    },
    handleCloseDetailDialog() {
      this.dialogVisible = false;
    },
  },
};
</script>
<style lang="scss" scoped>
.delivery-page {
  width: 100%;
  background: #171a1e;
  position: absolute;
  display: flex;
  min-height: 260px;
  background: rgb(18, 18, 18);
}
.delegate-box {
  /* padding-bottom: 50px; */
  background: rgb(18, 18, 18);
  position: relative;
}

.delegate-title {
  font-weight: bold;
}

.delegate-content {
  overflow: scroll;
  padding-bottom: 20px;
  height: 495px;
}

.delegate-title,
.delegate-item {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0 20px;
  color: #929aa5;
}

.delegate-title > div,
.delegate-item > div {
  flex: 1;
  padding: 20px 0;
}

.line-grey-border {
  border-bottom: 1px solid #d1d1d1;
}

.okui-tabs {
  height: auto !important;
}

.position-tab-active {
  color: #1d91ff !important;
  border-bottom: 2px solid #1d91ff !important;
  font-weight: 600;
}

.symbol-img {
  width: 20px;
  height: 20px;
  margin-right: 5px;
}

.no-login {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 400px;
  font-size: 14px;
  color: #fff;
  cursor: pointer;
}

.delegate-left {
  flex: 1;
  border-right: 1px solid #24272c;
  border-bottom: 1px solid #24272c;
}

.delegate-right {
  width: 300px;
  height: 100%;
  box-sizing: border-box;
  padding: 0 20px;
}

.assets-title {
  color: #fff;
  font-weight: 400;
  padding: 20px 0;
}

.el-button + .el-button {
  margin-left: 0 !important;
}

.assets-btns {
  display: grid;
  grid-template-columns: auto auto auto;
  grid-column-gap: 12px;
}

.assets-btns button {
  font-size: 12px;
  background: #171a1e;
  border: none;
  color: #707a8a;
}

.money-box {
  display: flex;
  justify-content: space-between;
  line-height: 35px;
  height: 35px;
}

.money-left {
  color: #929aa5;
}

.money-right {
  color: #fff;
}

.btn {
  background: #409eff !important;
  color: #fff !important;
}

.empty-box {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
