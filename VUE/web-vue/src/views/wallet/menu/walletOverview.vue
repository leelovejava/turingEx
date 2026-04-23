<!-- 钱包总览 -->
<template>
  <div class="right-view">
    <div class="right-header">
      <div class="right-header-box">
        <div>{{ $t("message.user.qianbaozonglan") }}</div>
        <div>
          <el-button
            type="primary"
            style="width: 100px; margin-right: 10px"
            @click="goRecharge"
            >{{ $t("message.user.chongzhi") }}</el-button
          >
          <button class="light-grey-button" @click="goWithdraw">
            {{ $t("message.user.tixian") }}
          </button>
          <button class="light-grey-button" @click="goExchange">
            {{ $t("message.user.duihuan") }}
          </button>
        </div>
      </div>
    </div>
    <div class="wallet-box">
      <div>
        <div class="asset-box padding-left-right20">
          <div class="assets-title flex-row-center">
            <p>{{ $t("message.user.zongzicanguzhi") }}</p>
            <div class="margin-left10 mouse-cursor" @click.stop="smallEyes">
              <img
                src="/src/assets/myImages/icon-image/wallet-overview/eyeClose.png"
                width="28px"
                height="28px"
                v-show="eyetel"
              />
              <img
                src="/src/assets/myImages/icon-image/wallet-overview/eyeOpen.png"
                width="28px"
                height="28px"
                v-show="!eyetel"
              />
            </div>
          </div>
          <div class="assets-size" v-if="!eyetel">
            <span class="assets-black">{{ total }}</span>
            <span class="assets-grey">≈ $ {{ total }}</span>
          </div>
          <div class="assets-size" v-else>
            <span class="assets-black">********</span>
            <span class="assets-grey">≈ $ ********</span>
          </div>
        </div>
        <!-- 具体账户 -->
        <div class="wallet-content-box margin-top20">
          <div v-for="(item, index) in menuList" :key="index">
            <div class="flex-row-center">
              <img :src="item.iconPath" alt="" class="assets-icon-22" />
              <div class="margin-left10">
                {{ $t(`message.user.${item.title}`) }}
              </div>
            </div>

            <div v-if="!eyetel">{{ item.value }} USDT ≈ ${{ item.value }}</div>
            <div v-else>******** USDT ≈ $ ********</div>
          </div>
        </div>
      </div>
      <!-- 充提记录 -->
      <div class="recharge-withdraw-box">
        <div class="margin-bottom20">
          <div class="font-size20">
            {{ $t("message.user.jinqichongtijilu") }}
          </div>
          <button class="light-grey-button" @click="goWalletHistory">
            {{ $t("message.user.chakanquanbu") }}
          </button>
        </div>

        <div v-if="listData.length === 0" class="empty">
          {{ $t("message.home.noData") }}
        </div>

        <div
          v-else
          class="list-view"
          v-for="(item, index) in listData"
          :key="item + index"
        >
          <div class="flex-row-center">
            <img
              src="/src/assets/myImages/icon-image/wallet-overview/recharge.png"
              alt=""
              class="assets-icon"
              v-if="item.category == 'recharge'"
            />
            <img
              src="/src/assets/myImages/icon-image/wallet-overview/withdraw.png"
              alt=""
              class="assets-icon"
              v-else
            />
            <div class="flex-column">
              <div v-if="item.category == 'recharge'">
                {{ $t("message.user.chongzhi") }}
              </div>
              <div v-else>{{ $t("message.user.tixian") }}</div>
              <div class="linght-grey-color">{{ item.createTimeStr }}</div>
            </div>
          </div>
          <div class="flex-column" style="text-align: right">
            <div>{{ item.amount }} {{ item.wallet_type }}</div>
            <div class="red" v-if="item.status == 0">
              {{ $t("message.user.querenzhong") }}
            </div>
            <div class="green" v-if="item.status == 1">
              {{ $t("message.user.chenggong") }}
            </div>
            <div class="red" v-if="item.status == 2">
              {{ $t("message.user.shibai") }}
            </div>
          </div>
        </div>
        <!-- <div>
                <div class="flex-row-center">
                   <img src="/src/assets/myImages/icon-image/wallet-overview/no-recharge.png" alt="" class="assets-icon"/>
                   <div class="flex-column">
                      <div>{{ $t('message.user.chongzhiweidaozhang') }}</div>
                      <a class="linght-grey-color contract-line">{{ $t('message.user.lianxikefu') }}</a>
                   </div>
                </div>
             </div> -->
      </div>
    </div>
  </div>
</template>
<script>
import Axios2 from "@/api/wallet.js";
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";
import { qianbaoList } from "@/utils/menuConfig";

export default {
  name: "WalletOverview",
  data() {
    return {
      MenuChooseIndex: 0,
      total: 0, //总资产估值
      eyetel: false,
      listData: [], //充提列表
      moneyObj: {},
      menuList: [],
      newQianbaoList: [],
    };
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  mounted() {
    if (this.existToken) {
      this.getAssetsAll();
      this.getWalletHistory();
    }
    this.newQianbaoList = qianbaoList.slice(0, -1);
    this.menuList = this.newQianbaoList.map((it) => {
      return {
        ...it,
        value: this.moneyObj[it.key],
      };
    });
  },
  methods: {
    goRecharge() {
      this.$router.push({
        path: "/recharge",
      });
    },
    goWithdraw() {
      this.$router.push({
        path: "/withdraw",
      });
    },
    goExchange() {
      this.$router.push({
        path: "/exchange",
      });
    },
    //小眼睛图标切换
    smallEyes() {
      this.eyetel = !this.eyetel;
    },
    changeMenuIndex(index) {
      this.MenuChooseIndex = index;
    },

    getAssetsAll() {
      Axios2.getAggregationAssets({}).then((res) => {
        if (res.code == 0) {
          const { all, cryptos, forex, indices } = res.data;
          const { money_miner, money_finance } = res.data?.all;
          this.moneyObj = {
            usdt: all.money_wallet,
            etf: indices?.symbol_type_asserts,
            crypto: cryptos?.symbol_type_asserts,
            forex: forex?.symbol_type_asserts,
            us: res.data?.["US-stocks"]?.symbol_type_asserts,
            cn: res.data?.["A-stocks"]?.symbol_type_asserts,
            hk: res.data?.["HK-stocks"]?.symbol_type_asserts,
            tw: res.data?.["TW-stocks"]?.symbol_type_asserts,
            financial: Number(money_miner) + Number(money_finance),
          };
          this.total = all.total;
          this.menuList = this.newQianbaoList.map((it) => {
            return {
              ...it,
              value: this.moneyObj[it.key],
            };
          });
          return;
        }
      });
    },
    goWalletHistory() {
      this.$router.push({
        path: "/order/walletHistory",
      });
    },
    getWalletHistory() {
      Axios2.getWalletHistory({
        page_no: 1,
      }).then((res) => {
        if (res.code == "0") {
          this.listData = res.data.slice(0, 5);
        }
      });
    },
  },
};
</script>
<style scoped lang="css">
.table_all >>> th.el-table__cell {
  background-color: #fafafa;
}

/* 资产 */
.wallet-box {
  display: flex;
}

.wallet-box > div {
  flex: 1;
}

.wallet-box > div:nth-child(1) {
  display: flex;
  flex-direction: column;
}

.wallet-content-box > div {
  display: flex;
  justify-content: space-between;
  padding: 8px 20px;
}

.wallet-content-box > div:hover,
.list-view:hover {
  cursor: pointer;
  background-color: #fafafa;
}

.wallet-content-box > div > div {
  line-height: 50px;
}

.assets-icon {
  width: 50px;
  height: 50px;
}

.assets-icon-22 {
  width: 22px;
  height: 22px;
}

/* 充提记录 */
.recharge-withdraw-box {
  margin-top: 15px;
  padding: 0 50px;
}

.recharge-withdraw-box > div {
  display: flex;
  justify-content: space-between;
}

.flex-column {
  display: flex;
  flex-direction: column;
  margin-left: 10px;
}

.flex-column > div {
  margin: 5px 0;
}

.list-view {
  margin-bottom: 15px;
}

.contract-line {
  text-decoration: underline;
  cursor: pointer;
}

.empty {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 206px;
  font-size: 14px;
  color: #929aa5;
}
</style>
