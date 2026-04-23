<!-- 矿池锁仓页面 -->
<template>
  <div>
    <!-- 上半部分 -->
    <div class="miner-background">
      <div style="width: 1200px; margin: 0 auto">
        <!-- 标题 -->
        <div style="width: 1200px; height: auto; padding-top: 30px">
          <div class="wealth-title">
            {{ $t("message.home.kuangchi_1", { TITLE: $title }) }}
          </div>
          <div class="wealth-content">{{ $t("message.home.kuangchi_2") }}</div>
        </div>
        <!-- 订单、日收益、累计收益、托管中订单 -->
        <div class="wealth-list" style="width: 800px; height: 140px">
          <div>
            <p>{{ $t("message.user.dingdan") }}</p>
            <p>
              {{ (amount_sum * 1).toFixed(1) }}
              {{ outputCurrency ? outputCurrency.toUpperCase() : "USDT" }}
            </p>
            <p>≈ $ {{ (amountSumValue * 1).toFixed(1) }}</p>
          </div>
          <div>
            <p>{{ $t("message.user.rishouyi") }}</p>
            <p>
              {{ (today_profit * 1).toFixed(1) }}
              {{ outputCurrency ? outputCurrency.toUpperCase() : "USDT" }}
            </p>
            <p>≈ $ {{ (todayProfitValue * 1).toFixed(1) }}</p>
          </div>
          <div>
            <p>{{ $t("message.user.leijishouyi") }}</p>
            <p>
              {{ (aready_profit * 1).toFixed(1) }}
              {{ outputCurrency ? outputCurrency.toUpperCase() : "USDT" }}
            </p>
            <p>≈ $ {{ (areadyProfitValue * 1).toFixed(1) }}</p>
          </div>
          <div>
            <p>{{ $t("message.user.tuoguanzhongdingdan") }}</p>
            <p>{{ order_sum }}</p>
          </div>
        </div>
        <!-- 账户、历史 -->
        <div style="width: 1200px; height: 100px">
          <div
            style="
              width: 800px;
              height: 100px;
              margin: auto;
              display: flex;
              position: relative;
            "
          >
            <div
              class="theme-color wealth-zhanghu text-decoration-underline"
              @click="goAccounts"
            >
              {{ $t("message.user.zhanghu") }}
            </div>
            <div
              class="theme-color wealth-licai text-decoration-underline"
              @click="goHistory"
            >
              {{ $t("message.user.lishi") }}
            </div>
          </div>
        </div>
        <!-- 基金理财+矿石锁仓 tab -->
        <div className="tabWrapper">
          <a
            class="theme-color wealth-btn-licai-grey mouse-cursor"
            @click="goRouter('/fundMa')"
            >{{ $t("message.user.jijinlicai_1") }}</a
          >
          <a class="theme-color wealth-btn-kuangchi" style="position: relative"
            >{{ $t("message.user.kuangchisuokuang_1") }}

            <div class="wealth-btn-rect-kuangchi"></div>
          </a>
        </div>
      </div>
    </div>
    <!-- checkbox & 搜索 -->
    <div
      class="content-view-box item-box-wealth"
      style="background-color: white"
    >
      <div
        style="position: absolute; margin-left: 0px; height: 44px; width: 500px"
      >
        <input
          type="checkbox"
          id="checkbox1"
          v-model="checked1"
          class="css-input"
          scoped
        />
        <label
          for="checkbox"
          style="background-color: white; margin-left: 5px"
          >{{ $t("message.home.OnlyDisplayAndPurchase") }}</label
        >

        <input
          type="checkbox"
          id="checkbox2"
          v-model="checked2"
          style="margin-left: 30px"
          class="css-input"
          scoped
        />
        <label
          for="checkbox"
          style="background-color: white; margin-left: 5px"
          >{{ $t("message.home.MatchMyAssets") }}</label
        >
      </div>

      <div
        class="css-search-1"
        style="position: absolute; margin-left: 800px; margin-top: -22px"
      >
        <div class="css-search-2">
          <div class="css-search-3">
            <div class="bn-input-prefix css-search-4">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="none"
                class="css-search-5"
              >
                <path
                  fill-rule="evenodd"
                  clip-rule="evenodd"
                  d="M11 6a5 5 0 110 10 5 5 0 010-10zm0-3a8 8 0 017.021 11.838l3.07 3.07-1.59 1.591-1.591 1.591-3.07-3.07A8 8 0 1111 3z"
                  fill="currentColor"
                ></path>
              </svg>
            </div>
            <input
              id="lockMiner_main_search"
              :placeholder="$t('message.hangqing.sousuo')"
              class="css-search-6"
              value=""
            />
          </div>
        </div>
      </div>
    </div>
    <!-- 列表 -->
    <div class="content-view-box padding-top-bottom30">
      <el-table
        :data="tableData"
        style="width: 100%"
        :empty-text="$t('message.home.noData')"
      >
        <el-table-column
          prop="date"
          :label="$t('message.user.xiangmumingcheng')"
        >
          <template #default="scope">
            <div class="flex-row-center">
              <img
                v-if="scope.$index === 0"
                class="w-24"
                src="@/assets/images/wealth/machine1.png"
                alt=""
              />

              <img
                v-else-if="scope.$index === 1"
                class="w-24"
                src="@/assets/images/wealth/machine_asic.png"
                alt=""
              />
              <img
                v-else-if="scope.$index === 2"
                class="w-24"
                src="@/assets/images/wealth/machine_ex.png"
                alt=""
              />
              <img
                v-else-if="scope.$index === 3"
                class="w-24"
                src="@/assets/images/wealth/machine_fpga.png"
                alt=""
              />
              <img
                v-else-if="scope.$index === 4"
                class="w-24"
                src="@/assets/images/wealth/machine_ipfs.png"
                alt=""
              />
              <img
                v-else-if="scope.$index === 5"
                class="w-24"
                src="@/assets/images/wealth/machine_gpu.png"
                alt=""
              />
              <img
                v-else-if="scope.$index === 6"
                class="w-24"
                src="@/assets/images/wealth/machine_ck6.png"
                alt=""
              />
              <img
                v-else
                class="w-24"
                src="@/assets/images/wealth/machine_ex.png"
                alt=""
              />
              <div class="font-size16 margin-left10">
                {{ scope.row?.[this.nameLang] }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="daily_rate" :label="$t('message.user.rishouyi')">
          <template #default="scope">
            <div class="green font-bold font-size16">
              {{ scope.row.daily_rate }}%
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="cycle" :label="$t('message.user.zhouqi')">
          <template #default="scope">
            <div class="font-size16 font-bold">
              {{
                scope.row.cycle != 0
                  ? scope.row.cycle
                  : $t("message.user.wuxianqi")
              }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="address" :label="$t('message.user.danbixiane')">
          <template #default="scope">
            <div class="font-size16 font-bold">
              {{ scope.row.investment_min }} - {{ scope.row.investment_max }}
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="$t('message.user.caozuo')">
          <template #default="scope">
            <button
              class="wealth-buy-button"
              type="primary"
              @click="buyBtn(scope.row.id)"
            >
              {{ $t("message.user.mairu") }}
            </button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 相关问题 -->
    <div class="content-view-box">
      <div class="font-size26 recharge-question-text">
        {{ $t("message.user.xiangguanwenti") }}
      </div>
      <el-collapse>
        <el-collapse-item :title="'1.' + $t('message.user.chang5')" name="1">
          <div>
            {{ $t("message.user.chang6") }}
          </div>
        </el-collapse-item>
        <el-collapse-item :title="'2.' + $t('message.user.chang9')" name="2">
          <el-table :data="tableData">
            <el-table-column
              prop="name"
              :label="$t('message.user.kuangjimingcheng')"
            >
              <template #default="scope">
                <div class="font-size16">
                  {{ scope.row?.[this.nameLang] }}
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="investment_min"
              :label="$t('message.user.kuangjijine')"
            >
              <template #default="scope">
                <div class="font-size16 font-bold">
                  {{ scope.row.investment_min }} -
                  {{ scope.row.investment_max }}
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="cycle" :label="$t('message.user.zhouqi')">
              <template #default="scope">
                <div class="font-size16 font-bold">
                  {{
                    scope.row.cycle != 0
                      ? scope.row.cycle
                      : $t("message.user.wuxianqi")
                  }}
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="daily_rate"
              :label="$t('message.user.rishouyi')"
            >
              <template #default="scope">
                <div class="font-bold font-size16">
                  {{ scope.row.daily_rate }} %
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <el-collapse-item :title="'3.' + $t('message.user.chang7')" name="3">
          <div class="margin-bottom20">
            {{ $t("message.user.chang8") }}
          </div>
          <el-table :data="textData" border>
            <el-table-column
              prop="recommend"
              :label="$t('message.user.tuijianyonghu')"
            >
            </el-table-column>
            <el-table-column prop="revenue" :label="$t('message.user.chang9')">
            </el-table-column>
            <el-table-column
              prop="subscribe"
              :label="$t('message.user.chang10')"
            >
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
    </div>
    <!-- 购买弹窗 -->
    <fund-buy
      v-if="buyCurrency"
      :iproduceId="showProductId"
      :type="typeName"
      :buyCurrency="buyCurrency"
      ref="panelShow"
    ></fund-buy>
  </div>
</template>

<script>
import Axios from "@/api/wealth.js";
import FundBuy from "./FundBuy.vue";
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";
import { getWealthLangToName } from "@/utils";
export default {
  components: { FundBuy },
  data() {
    return {
      amount_sum: "",
      amountSumValue: "",
      today_profit: "",
      todayProfitValue: "",
      aready_profit: "",
      areadyProfitValue: "",
      order_sum: "",
      outputCurrency: "",
      buyCurrency: "",
      // 以上是接口返回的
      nameLang: "name",
      tableData: [],
      showProductId: "",
      typeName: "mine",

      textData: [
        {
          recommend: this.$t("message.user.yijiyonghu"),
          revenue: "20%",
          subscribe: "5%",
        },
        {
          recommend: this.$t("message.user.erjiyonghu"),
          revenue: "10%",
          subscribe: "3%",
        },
        {
          recommend: this.$t("message.user.sanjiyonghu"),
          revenue: "5%",
          subscribe: "1%",
        },
      ],
      checked1: false,
      checked2: false,
      tableOrderData: [],
      mineOrderData: [],
      tableDataAll: [],
      money: 0,
      checkdIndex: 0,
    };
  },
  mounted() {
    this.nameLang = this.getWealthLangToName();
    this.getlist();
    this.getIncome();
    if (this.existToken) {
      this.getMineOrderList();
      this.getUsdt();
    }

    let self = this;
    let lockMiner_main_search = document.getElementById(
      "lockMiner_main_search"
    );
    lockMiner_main_search.oninput = function () {
      self.search(lockMiner_main_search.value);
    };
  },
  methods: {
    getWealthLangToName,
    goAccounts() {
      this.$router.push({
        path: "/wallet/financialAccounts",
        query: {
          type: 1,
        },
      });
    },
    goHistory() {
      this.$router.push({
        path: "/order/financialOrder",
        query: {
          type: "miner",
        },
      });
    },
    getlist() {
      Axios.getMinerList().then((res) => {
        this.tableDataAll = res.data;
        this.tableData = [].concat(this.tableDataAll);
      });
    },
    //理财收益统计
    getIncome() {
      Axios.getMinerOrderSum().then((res) => {
        const {
          amount_sum,
          amountSumValue,
          today_profit,
          todayProfitValue,
          aready_profit,
          areadyProfitValue,
          order_sum,
          outputCurrency,
          buyCurrency,
        } = res.data;
        this.amount_sum = amount_sum;
        this.amountSumValue = amountSumValue;
        this.today_profit = today_profit;
        this.todayProfitValue = todayProfitValue;
        this.aready_profit = aready_profit;
        this.areadyProfitValue = areadyProfitValue;
        this.order_sum = order_sum;
        this.outputCurrency = outputCurrency;
        this.buyCurrency = buyCurrency || "usdt";
      });
    },
    //理财账户列表数据
    getOrderList() {
      Axios.getFinanceOrderList({
        state: 1,
        page_no: 1,
      }).then((res) => {
        this.tableOrderData = res.data;
      });
    },
    //矿池锁仓列表
    getMineOrderList() {
      Axios.getMinerOrderList({
        state: 1,
        page_no: 1,
      }).then((res) => {
        this.mineOrderData = res.data;
      });
    },
    //获取可用余额USDT
    getUsdt() {
      Axios.getWallet()
        .then((res) => {
          this.money = res.data.usdt;
        })
        .catch((err) => {
          console.log(err);
        });
    },
    //买入
    buyBtn(id) {
      if (!this.existToken) {
        this.$router.push("/login");
      } else {
        this.$refs.panelShow.show();
        this.showProductId = id;
      }
    },
    goRouter(parmas) {
      if (parmas != "/loginOut") {
        this.$router.push(parmas);
      } else {
        Axios.loginOut().then((res) => {
          if (res.code == "0") {
            localStorage.removeItem("spToken");
            localStorage.removeItem("vuex");
            localStorage.removeItem("username");
            this.$router.push("/login");
            window.location.reload();
          }
        });
      }
    },
    Checkd() {
      if (!this.checked1 && !this.checked2) {
        //所有的
        this.tableData = [].concat(this.tableDataAll);
      } else if (this.checked1 && this.checked2) {
        let newTable = [];
        let table1 = [].concat(this.tableDataAll);
        for (let i = 0; i < table1.length; i++) {
          let t = table1[i];
          if (t.investment_min <= this.money && t.on_sale == "1") {
            newTable.push(JSON.parse(JSON.stringify(t)));
          }
        }

        let newTable2 = [];
        for (let i = 0; i < newTable.length; i++) {
          let t = newTable[i];
          if (t.investment_min <= this.money && t.on_sale == "1") {
            newTable2.push(JSON.parse(JSON.stringify(t)));
          }
        }

        this.tableData = newTable2;
      } else if (this.checked1) {
        //可以购买
        let newTable = [];
        let table1 = [].concat(this.tableDataAll);

        for (let i = 0; i < table1.length; i++) {
          let t = table1[i];
          // console.log("t="+JSON.stringify(t));
          if (t.investment_min <= this.money && t.on_sale == "1") {
            newTable.push(JSON.parse(JSON.stringify(t)));
          }
        }
        this.tableData = newTable;
      } else if (this.checked2) {
        //已经

        let newTable = [];
        let table1 = [].concat(this.tableDataAll);

        for (let i = 0; i < table1.length; i++) {
          let t = table1[i];

          if (t.investment_min <= this.money && t.on_sale == "1") {
            newTable.push(JSON.parse(JSON.stringify(t)));
          }
        }

        this.tableData = newTable;
      }
    },
    search(value) {
      let newTable = [];
      let table1 = [].concat(this.tableDataAll);
      for (let i = 0; i < table1.length; i++) {
        let t = table1[i];
        if (t.name_en && t.name_en.indexOf(value) >= 0) {
          newTable.push(JSON.parse(JSON.stringify(t)));
        } else if (t.name_cn && t.name_cn.indexOf(value) >= 0) {
          newTable.push(JSON.parse(JSON.stringify(t)));
        } else if (t.name && t.name.indexOf(value) >= 0) {
          newTable.push(JSON.parse(JSON.stringify(t)));
        }
      }

      this.tableData = newTable;
    },
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
};
</script>

<style lang="scss" scoped>
@import url("@/assets/css/wealth/wealth.css");

.tabWrapper {
  width: 1200px;
  height: 160px;
  position: relative;
  display: flex;
  padding-top: 130px;
  a {
    text-decoration: none;
  }
}
</style>
