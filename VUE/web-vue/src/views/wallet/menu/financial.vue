<!-- 理财账户 -->
<template>
  <div class="right-view">
    <!-- 头部 -->
    <div class="right-header">
      <div class="right-header-box">
        <div>{{ $t("message.user.licaizhanghu") }}</div>
        <button class="light-grey-button" @click="goHistory">
          {{ $t("message.user.licailishi") }}
        </button>
      </div>
    </div>
    <div class="padding-left-right20">
      <div class="margin-top-bottom20">
        <total-assets :pageType="'financial'"></total-assets>
        <!-- 选项卡按钮切换 -->
        <div class="tab-customize-box">
          <div
            class="tab-customize"
            :class="heYueType == 0 ? 'tabActive' : ''"
            @click="ChooseHeYueType(0)"
          >
            {{ $t("message.user.jijinlicai") }}
          </div>
          <div
            class="tab-customize"
            :class="heYueType == 1 ? 'tabActive' : ''"
            @click="ChooseHeYueType(1)"
          >
            {{ $t("message.user.kuangchisuocang") }}
          </div>
        </div>
      </div>
      <div>
        <!-- 理财账户列表 -->
        <el-table
          v-if="heYueType == 0"
          :data="tableData"
          class="width100"
          :header-cell-style="getRowClass"
          :empty-text="$t('message.home.noData')"
        >
          <el-table-column
            prop="createTime"
            :label="$t('message.user.goumaishijian')"
          ></el-table-column>
          <el-table-column
            prop="orderNo"
            :label="$t('message.user.dingdanhao')"
          ></el-table-column>
          <el-table-column
            prop="amount"
            :label="
              $t('message.user.tuoguanjine') +
              '/' +
              $t('message.user.tuoguanshijian')
            "
          >
            <template #default="scope">
              <div>
                <span class="green"
                  >{{ scope.row.amount }} ({{
                    scope.row.buyCurrency.toUpperCase()
                  }})</span
                >
                <span style="margin-left: 10px"
                  >{{ scope.row.cycle }} {{ $t("message.user.tian") }}</span
                >
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="daily_rate"
            :label="$t('message.user.dangrishouyi')"
          >
            <template #default="scope">
              <div>{{ scope.row.dailyRate }} %</div>
            </template>
          </el-table-column>
          <el-table-column prop="symbol_to" :label="$t('message.user.yihuoli')">
            <template #default="scope">
              <div>
                {{ scope.row.profit }} ({{
                  scope.row.buyCurrency.toUpperCase()
                }})
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="symbol_to"
            :label="$t('message.user.shengyutianshu')"
          >
            <template #default="scope">
              <div>{{ scope.row.days }} {{ $t("message.user.tian") }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="state" :label="$t('message.user.caozuo')">
            <template #default="scope">
              <el-button
                type="primary"
                size="small"
                @click="goDetail(scope.row)"
                >{{ $t("message.user.xiangqing") }}</el-button
              >
            </template>
          </el-table-column>
        </el-table>
        <!-- 锁仓矿池列表，我的锁仓：state为1，  已解锁：state:2  -->
        <el-table
          v-if="heYueType == 1"
          :data="mineData"
          class="width100"
          :header-cell-style="getRowClass"
          :empty-text="$t('message.home.noData')"
        >
          <el-table-column
            prop="order_no"
            :label="$t('message.user.dingdanhao')"
          ></el-table-column>
          <el-table-column
            prop="create_time"
            :label="$t('message.user.tuoguanshijian')"
          ></el-table-column>
          <!-- 简体中文：miner_name，繁体：miner_name_cn 英文：miner_name_cn -->
          <el-table-column
            :prop="getLocalLan()"
            :label="$t('message.user.kuangjimingcheng')"
          ></el-table-column>
          <el-table-column
            prop="amount"
            :label="$t('message.user.suocangjine')"
          >
            <template #default="scope">
              <div>
                <span
                  >{{ scope.row.amount }} ({{
                    scope.row.buyCurrency?.toUpperCase()
                  }})</span
                >
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="cycle" :label="$t('message.user.zhouqi')">
            <template #default="scope">
              <div>
                {{
                  scope.row.cycle == 0
                    ? $t("message.user.wuqixian")
                    : scope.row.cycle
                }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="daily_rate"
            :label="$t('message.user.dangrishouyi')"
          >
            <template #default="scope">
              <div>{{ scope.row.daily_rate }} %</div>
            </template>
          </el-table-column>
          <el-table-column prop="symbol_to" :label="$t('message.user.yihuoli')">
            <template #default="scope">
              <div>
                {{ scope.row.profit }} （{{
                  scope.row.buyCurrency?.toUpperCase()
                }}）
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="symbol_to"
            :label="$t('message.user.shengyutianshu')"
          >
            <template #default="scope">
              <div>{{ scope.row.days }} {{ $t("message.user.tian") }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="state" :label="$t('message.user.jiesuo')">
            <template #default="scope">
              <div>
                {{
                  scope.row.state == "1"
                    ? $t("message.user.shoudongjiesuo")
                    : $t("message.user.yijiesuo")
                }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="state" :label="$t('message.user.caozuo')">
            <template #default="scope">
              <el-button
                type="primary"
                size="small"
                @click="goMineDetail(scope.row.order_no, scope.row.id)"
                >{{ $t("message.user.xiangqing") }}</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- 理财订单详情 -->
      <el-dialog
        :title="$t('message.user.dingdanxiangqing')"
        v-model="centerDialogVisible"
        width="50%"
        center
      >
        <div class="dialog-style">
          <div class="dialog-content">
            <p>{{ $t("message.user.tuoguanjine") }}</p>
            <p>{{ $t("message.user.yihuoqushouyi") }}</p>
            <p>{{ $t("message.user.yuqishouyi") }}</p>
            <p>{{ $t("message.user.tuoguanshijian") }}</p>
            <p>{{ $t("message.user.rishouyi") }}</p>
            <p>{{ $t("message.user.qixishijian") }}</p>
            <p>{{ $t("message.user.daoqishijian") }}</p>
            <p>{{ $t("message.user.shengyutianshu") }}</p>

            <p>{{ $t("message.user.weiyuejin") }}</p>
            <p>{{ $t("message.user.shuhuibenjin") }}</p>
            <p>{{ $t("message.user.dingdanbianhao") }}</p>
            <p>{{ $t("message.user.dingdanshijian") }}</p>
          </div>
          <div class="dialog-content">
            <p>{{ detailData.amount }}</p>
            <p>{{ detailData.profit }}</p>
            <p>{{ detailData.profit_may }}</p>
            <p>
              {{
                detailData.cycle != 0
                  ? detailData.cycle + $t("message.user.tian")
                  : $t("message.user.wuxianqi")
              }}
            </p>
            <p>{{ detailData.daily_rate }} %</p>
            <p>{{ detailData.earn_timeStr }}</p>
            <p>{{ detailData.stop_timeStr }}</p>
            <p>{{ detailData.days }}</p>
            <p>{{ detailData.default_amount }}</p>
            <p>{{ detailData.principal_amount }}</p>
            <p>{{ detailData.order_no }}</p>
            <p>{{ detailData.create_timeStr }}</p>
          </div>
        </div>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="centerDialogVisible = false">{{
              $t("message.user.quxiao")
            }}</el-button>
            <el-button type="primary" @click="closeFinance">{{
              $t("message.user.quedingshuhui")
            }}</el-button>
          </span>
        </template>
      </el-dialog>
      <!-- 矿池锁仓订单详情 -->
      <el-dialog
        :title="$t('message.user.dingdanxiangqing')"
        v-model="minerDialog"
        width="50%"
        center
      >
        <div class="dialog-style">
          <div class="dialog-content">
            <p>{{ $t("message.user.suocangjine") }}</p>
            <p>{{ $t("message.user.yihuoqushouyi") }}</p>
            <p>{{ $t("message.user.yuqishouyi") }}</p>
            <p>{{ $t("message.user.zhouqi") }}</p>
            <p>
              {{ $t("message.user.rishouyi") }}
            </p>
            <p>{{ $t("message.user.qixishijian") }}</p>
            <p>{{ $t("message.user.daoqishijian") }}</p>
            <p>{{ $t("message.user.shengyutianshu") }}</p>

            <!-- <p>违约金</p>
                    <p>赎回本金</p> -->
            <p>{{ $t("message.user.dingdanbianhao") }}</p>
            <p>{{ $t("message.user.dingdanshijian") }}</p>
          </div>
          <div class="dialog-content">
            <p>{{ minerDetail.amount }}</p>
            <p>{{ minerDetail.profit }}</p>
            <p>{{ minerDetail.profit_may }}</p>
            <p>
              {{
                minerDetail.cycle != 0
                  ? minerDetail.cycle + $t("message.user.tian")
                  : $t("message.user.wuxianqi")
              }}
            </p>
            <p>{{ minerDetail.daily_rate }} %</p>
            <p>{{ minerDetail.earn_timeStr }}</p>
            <p>{{ minerDetail.stop_timeStr || "--" }}</p>
            <p>{{ minerDetail.days }}</p>
            <!-- <p>{{ minerDetail.default_amount}}</p>
                    <p>{{ minerDetail.principal_amount}}</p> -->
            <p>{{ minerDetail.order_no }}</p>
            <p>{{ minerDetail.create_timeStr }}</p>
          </div>
        </div>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="minerDialog = false">{{
              $t("message.user.quxiao")
            }}</el-button>
            <el-button
              type="primary"
              @click="unlock"
              :disabled="!minerDetail.can_close"
              >{{ $t("message.user.jiesuo") }}</el-button
            >
          </span>
        </template>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import totalAssets from "../components/totalAssets.vue";
import Axios2 from "@/api/wallet.js";
export default {
  components: { totalAssets },
  name: "financialAccounts",
  data() {
    return {
      MenuChooseIndex: 1,
      total: 0,
      tableData: [], //理财列表数据
      mineData: [], //矿池锁仓列表数据
      heYueType: 0, //为0 表示理财tab
      centerDialogVisible: false,
      detailData: {}, //理财详情数据
      orderId: "", //理财订单id
      minerDialog: false,
      minerDetail: {}, //矿机详情数据
      minerId: "", //矿机订单id
    };
  },
  mounted() {
    //  type = 1 表示查看矿池锁仓的订单，为0表示查看基金理财的订单
    if (this.$route.query?.type) {
      this.heYueType = this.$route.query.type;
    }
    this.getAssetsAll();
    this.getList();
    this.getMineList();
  },
  methods: {
    ChooseHeYueType(index) {
      this.heYueType = index;
    },
    //理财账户列表数据
    getList() {
      Axios2.listFinanceOrder({
        state: 1,
        page_no: 1,
      }).then((res) => {
        this.tableData = res.data;
      });
    },
    //矿池锁仓列表
    getMineList() {
      Axios2.listMinerOrder({
        state: 1,
        page_no: 1,
      }).then((res) => {
        this.mineData = res.data;
      });
    },
    //理财详情
    goDetail(data) {
      const { orderNo, uuid } = data;
      this.orderId = uuid; //??原来是id
      this.centerDialogVisible = true;
      Axios2.goDetail({
        order_no:orderNo,
      }).then((res) => {
        this.detailData = res.data;
      });
    },
    //赎回理财
    closeFinance() {
      let that = this;
      that
        .$confirm(
          this.$t("message.user.xianzaishifoushuhui"),
          this.$t("message.user.tishi"),
          {
            confirmButtonText: this.$t("message.user.queding"),
            cancelButtonText: this.$t("message.user.quxiao"),
            type: "warning",
          }
        )
        .then(() => {
          // 赎回
          Axios2.financeOrderOfClose({
            id: that.orderId,
          }).then((res) => {
            if (res.code == 0) {
              that.$message({
                type: "success",
                message: this.$t("message.user.shuihuichenggong"),
              });
            }
            that.centerDialogVisible = false;
            that.getList();
            that.$router.push({
              path: "/order/financialOrder",
              query: {
                type: "financial",
              },
            });
          });
        })
        .catch(() => {
          that.$message({
            type: "info",
            message: this.$t("message.user.yiquxiao"),
          });
          that.centerDialogVisible = false;
        });
    },
    //矿池详情
    goMineDetail(order_no, id) {
      this.minerDialog = true;
      this.minerId = order_no;
      Axios2.goMineDetail({
        order_no,
      }).then((res) => {
        this.minerDetail = res.data;
      });
    },
    //解锁
    unlock() {
      let that = this;
      that
        .$confirm(
          this.$t("message.user.shifouxianzaijiesuo"),
          this.$t("message.user.tishi"),
          {
            confirmButtonText: this.$t("message.user.queding"),
            cancelButtonText: this.$t("message.user.quxiao"),
            type: "warning",
          }
        )
        .then(() => {
          Axios2.minerOrderOfClose({
            order_no: that.minerId,
          }).then((res) => {
            if (res.code == 0) {
              that.$message({
                type: "success",
                message: this.$t("message.user.jiesuochenggong"),
              });
              that.heYueType = 1;
            }
            that.minerDialog = false;
            that.getMineList();
            that.$router.push({
              path: "/order/financialOrder",
              query: {
                type: "miner",
              },
            });
          });
        })
        .catch(() => {
          that.$message({
            type: "info",
            message: this.$t("message.user.yiquxiao"),
          });
          that.minerDialog = false;
        });
    },
    //给表头设置背景颜色
    getRowClass({ rowIndex, columnIndex }) {
      if (rowIndex == 0) {
        return { background: "#f8f8f9" };
      }
    },

    //总账户资产
    getAssetsAll() {
      Axios2.getAllAssets().then((res) => {
        if (res.code == 0) {
          this.total = res.data.total;
        }
      });
    },
    goHistory() {
      this.$router.push({
        path: "/order/financialOrder",
      });
    },
    getLocalLan() {
      var lang = JSON.parse(localStorage.getItem("lang"));
      // 简体中文：miner_name，繁体：miner_name_cn miner_name_en
      if (lang == "en") {
        return "miner_name_en";
      } else if (lang == "cht") {
        return "miner_name_cn";
      } else if (lang == "zh-CN") {
        return "miner_name";
      }
      return "miner_name_en";
    },
  },
};
</script>
<style scoped lang="css">
/* @import url("@/assets/css/wallet/walletOverview.css");
@import url("../../assets/global.css"); */

.table_all >>> th.el-table__cell {
  background-color: #fafafa;
}
</style>
