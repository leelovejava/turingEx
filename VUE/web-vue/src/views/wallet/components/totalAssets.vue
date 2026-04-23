<template>
  <div class="asset-box">
    <div class="assets-title flex-row-center">
      <p>{{ $t("message.user.zongzicanguzhi") }}</p>
      <div class="margin-left10 mouse-cursor" @click.stop="smallEyes">
        <img
          src="../../../assets/myImages/icon-image/wallet-overview/eyeClose.png"
          width="28px"
          height="28px"
          v-show="eyetel"
        />
        <img
          src="../../../assets/myImages/icon-image/wallet-overview/eyeOpen.png"
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
</template>

<script>
import Axios2 from "@/api/wallet.js";
export default {
  props: {
    pageType: {
      type: String,
      default: "etf", //页面类型
    },
    paramsType: {
      type: String,
      default: "indices", //
    },
  },
  data() {
    return {
      total: 0,
      eyetel: false,
    };
  },
  mounted() {
    this.getAssetsAll();
  },
  methods: {
    getAssetsAll() {
      Axios2.getAggregationAssets({}).then((res) => {
        if (res.code == 0) {
          if (this.pageType === "financial") {
            const { money_miner, money_finance } = res.data?.all;
            this.total = Number(money_miner) + Number(money_finance);
            return;
          }
          this.total = res.data?.[this.paramsType]?.symbol_type_asserts;
        }
      });
    },
    smallEyes() {
      this.eyetel = !this.eyetel;
    },
  },
};
</script>
