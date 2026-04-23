<template>
  <div class="box-border">
    <div class="pl-8 pr-8 total_wrap">
      <div class="flex justify-between text-grey relative">
        <div class="flex items-center">
          <span class="text-grey text-30">{{ $t("总资产") }}（USDT）</span>
          <div @click.stop="changeEyes">
            <img src="../../../assets/image/assets-center/eye-open.png" class="w-8 h-4" v-show="!eyetel" />
            <img src="../../../assets/image/assets-center/eye-close.png" class="w-8 h-4" v-show="eyetel" />
          </div>
        </div>
        <div class="right-clock" @click="goRouter('/cryptos/exchangeHistory')">
          <img src="../../../assets/image/assets-center/Subtract.png" class="subtract-icon" />
        </div>
      </div>
      <div class="font-bold text-66 mt-4 textColor" v-if="!eyetel">
        {{ funds.total || "--" }}
        <span class="text-40 text-grey">≈{{ currency.currency_symbol
        }}{{ funds.total ? (funds.total * currency.rate).toFixed(2) : " --" }}</span>
      </div>
      <div class="text-66 mt-4 textColor" v-else>********</div>
    </div>
    <div class="pl-8 pr-8 btn-box flex justify-between">
      <div class="text-24 textColor tabBackground" @click="goRouter('/cryptos/recharge/rechargeList')">
        <div class="word-wrap">
          <img :src="handleImage(rechargeIcon)" alt="" />
          <span> {{ $t("充币") }}</span>
        </div>
      </div>
      <div class="text-24 textColor tabBackground" @click="withdrawBtn">
        <div class="word-wrap">
          <img :src="handleImage(withdrawIcon)" alt="" />
          <span>{{ $t("提币") }}</span>
        </div>
      </div>
      <div class="text-24 textColor tabBackground" @click="goRouter('/cryptos/exchangePage')">
        <div class="word-wrap">
          <img :src="handleImage(lrIcon)" alt="" />
          <span>{{ $t("闪兑") }}</span>
        </div>
      </div>
    </div>
    <!--       <div class="divider-line"></div>-->
    <div class="pl-8 pr-8">
      <div class="assets-content-title text-34 titleColor">{{ $t("投资组合") }}</div>
      <div class="assets-list text-30 flex justify-between" v-for="(item, index) in list" :key="index">
        <div class="items-center">
          <img :src="item.img" class="w-11 h-11" />
          <div class="text-grey symbol-name text-30 textColor2">{{ $t(item.text) }}</div>
        </div>
        <div class="flex-col text-30 text-right" v-if="!eyetel">
          <div class="textColor">{{ funds[item.key] || "--" }} USDT</div>
          <div class="text-grey text-30 mb-0">
            ≈{{ currency.currency_symbol }}
            {{ funds[item.key] ? (funds[item.key] * currency.rate).toFixed(2) : " --" }}
          </div>
        </div>
        <div class="flex-col text-30 text-right" v-if="eyetel">
          <div class="textColor">******** USDT</div>
          <div class="text-grey">≈{{ currency.currency_symbol }} ********</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { _getWallet } from "@/service/fund.api";
import { Dialog } from "vant";
import { mapGetters } from "vuex";
import { themeStore } from "@/store/theme";
const thStore = themeStore();
const THEME = thStore.theme;
export default {
  name: "AssetsPage",
  components: {
    [Dialog.name]: Dialog,
  },
  props: ["funds"],
  data() {
    return {
      THEME,
      // total:"",
      // status:"", //身份认证状态 0已申请未审核，1审核中，2审核通过，3审核未通过
      eyetel: false,
      active: 0,
      rechargeIcon: new URL(
        `../../../assets/theme/${thStore.theme}/image/assets/up.png`,
        import.meta.url
      ),
      withdrawIcon: new URL(
        `../../../assets/theme/${thStore.theme}/image/assets/down.png`,
        import.meta.url
      ),
      lrIcon: new URL(
        `../../../assets/theme/${thStore.theme}/image/assets/l_r.png`,
        import.meta.url
      ),
    };
  },
  computed: {
    ...mapGetters("home", ["currency"]),
    list() {
      return [
        {
          id: 1,
          text: this.$t("现货账户"),
          icon: "spot",
          key: "money_all_coin",
          img: new URL(
            "../../../assets/image/assets-center/funds/spot.png",
            import.meta.url
          ),
        },
        {
          id: 2,
          text: this.$t("合约账户 (U本位)"),
          icon: "contract_u",
          key: "money_contract",
          img: new URL(
            "../../../assets/image/assets-center/funds/spot.png",
            import.meta.url
          ),
        },
        {
          id: 3,
          text: this.$t("交割合约账户"),
          icon: "contract_d",
          key: "money_futures",
          img: new URL(
            "../../../assets/image/assets-center/funds/contract_d.png",
            import.meta.url
          ),
        },
        {
          id: 4,
          text: this.$t("理财账户"),
          icon: "invest",
          key: "money_finance",
          img: new URL(
            "../../../assets/image/assets-center/funds/invest.png",
            import.meta.url
          ),
        },
        {
          id: 5,
          text: this.$t("矿机资产"),
          icon: "mining",
          key: "money_miner",
          img: new URL(
            "../../../assets/image/assets-center/funds/mining.png",
            import.meta.url
          ),
        },
      ];
    },
  },
  mounted() {
    // this.getData();
    // this.getList();
  },
  methods: {
    handleImage(url) {
      return new URL(url, import.meta.url).href;
    },
    onClickLeft() {
      this.$router.go(-1);
    },
    goRouter(parmas) {
      if (parmas === "/cryptos/exchangeHistory") {
        this.$router.push({ path: parmas, query: { type: 0 } });
      } else {
        this.$router.push(parmas);
      }
    },
    getList() {
      _getWallet().then((data) => {
        this.list = data.extends;
      });
    },
    changeEyes() {
      this.eyetel = !this.eyetel;
    },
    beforeClose(action, done) {
      if (action === "confirm") {
        setTimeout(done, 1000);
      } else {
        done();
      }
    },
    //跳转提币页面
    withdrawBtn() {
      //身份已认证，直接跳转，未认证则提示跳身份认证页
      // if(this.funds.status==2) {
      //   this.$router.push({
      //     path:'/cryptos/withdraw/withdrawPage'
      //   });
      // }else{
      //   Dialog.confirm({
      //     confirmButtonText:this.$t('确定'),
      //     cancelButtonText:this.$t('取消'),
      //     title: this.$t('提示'),
      //     message: this.$t('请进行身份KYC认证'),
      //     beforeClose: (action, done) => {
      //       if (action === 'confirm') {
      //         done();
      //         this.$router.push({
      //           path:'/authentication'
      //         });
      //       } else {
      //         done();
      //       }
      //     },
      //   });
      // }
      this.$router.push({
        path: "/cryptos/withdraw/withdrawPage",
      });
    },
  },
};
</script>

<style lang="scss" scoped>
#cryptos {
  .right-clock {
    position: absolute;
    top: 0;
    right: -3px;
  }

  .total_wrap {
    margin-top: 44px;
  }

  .asset-title-box {
    margin: 40px 0;
  }

  .btn-box {
    margin-top: 44px;
  }

  .btn-box>div {
    width: 234px;
    padding: 20px 15px;
    box-sizing: border-box;
    text-align: center;
    border-radius: 8.8px;
    font-size: 26.5px;
    display: flex;
    justify-content: center;
    align-items: center;

    .word-wrap {
      display: flex;
      align-items: center;
      justify-content: center;

      img {
        width: 40px;
        height: 40px;
        margin-right: 22px;
      }

      span {
        font-size: 26.5px;
      }
    }
  }

  .mr60 {
    margin-right: 60px;
  }

  .divider-line {
    border-bottom: 1px solid $border-grey;
    padding-bottom: 80px;
  }

  .assets-content-title {
    margin-top: 60px;
    margin-bottom: 55px;
  }

  .assets-list {
    margin-bottom: 35px;
  }

  .assets-list>div {
    display: flex;
  }

  .assets-list>div:nth-child(2) div:nth-child(1) {
    margin-bottom: 16px;
    font-size: 30px;
    font-weight: 700;
  }

  .symbol-name {
    margin-left: 17px;
    //margin-top:10px;
  }

  .subtract-icon {
    width: 32px;
    height: 40px;
  }
}
</style>
