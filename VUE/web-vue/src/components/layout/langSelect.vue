<!--  语言汇率弹窗-->
<template>
  <div v-if="isShow">
    <div class="css-xplkkx">
      <div class="css-14doc9z">
        <div class="css-xhj0x8" @click="isShowClick">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            class="css-1iztezc"
          >
            <path
              d="M6.697 4.575L4.575 6.697 9.88 12l-5.304 5.303 2.122 2.122L12 14.12l5.303 5.304 2.122-2.122L14.12 12l5.304-5.303-2.122-2.122L12 9.88 6.697 4.575z"
              fill="currentColor"
            ></path>
          </svg>
        </div>
        <div class="css-1r3gvce">
          <div
            v-for="(val, index) in titleList"
            :key="index"
            :class="[active == index ? 'css-t94xhh' : 'css-ibfrnl']"
            @click="tabIndex(index)"
          >
            {{ val }}
          </div>
        </div>
        <div class="css-1tzfn88">
          <div class="css-cbzd9" v-if="active == 0">
            <div class="css-1p8zxym">
              {{ $t("message.user.qingxuanzeyuyanhediqu") }}
            </div>
            <div class="css-q2wk8b">
              <button
                data-active="true"
                id="fiatlngdialog_ba-languageRegion-zh-CN"
                :class="[
                  langActive == index ? 'rest_css-1szzq31' : '',
                  'css-1szzq3l',
                ]"
                style="width: 125px"
                v-for="(lang, index) in langOptions"
                :key="index"
                @click="changeLang(index, lang)"
              >
                <div class="css-1hv3jiv">
                  {{ lang.label }}
                </div>
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="currentColor"
                  class="css-j1zueq"
                >
                  <use xlink:href="#checkmark-f"></use>
                </svg>
              </button>
            </div>
          </div>
          <!-- 汇率 -->
          <!-- <div class="css-fyzeir" v-else-if="active == 1">
            <div  class="css-1p8zxym">
              {{ $t("message.user.qingxuanzehuilv") }}
            </div>
            <div class="css-q2wk8b">
              <button
                data-active="true"
                id="fiatlngdialog_ba-Currency-CNY"
                class="css-1szzq3l"
                v-for="(val, index) in exchangeRateData"
                :class="[
                  moneyIndex == index ? 'rest_css-1szzq31' : '',
                  'css-1szzq3l',
                ]"
                @click="chooseRate(index, val)"
                :key="index"
              >
                <div  class="css-1hv3jiv">
                  {{ val.currency }}-{{ val.currency_symbol }}
                </div>
              </button>
            </div>
          </div> -->
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { strFirstBit } from "@/utils";
import { mapState, mapActions } from "pinia";
import { useUserStore } from "@/store/user";
import { useLanguageStore } from "@/store/lang";
import { langOptions } from "@/utils";
export default {
  name: "langSelect",
  data() {
    return {
      isShow: false,
      titleList: [this.$t("message.user.yuyanhediqu")],
      langOptions,
      active: 0, //选中汇率还是多语言
      langActive: 0,
      moneyIndex: 0,
      exchangeRateData: [], //TODO
    };
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
    ...mapState(useLanguageStore, ["language"]),

    // ...mapState({
    //   exchangeRateData: (state) => state.exchangeRateData,
    // }),
  },
  methods: {
    ...mapActions(useLanguageStore, ["updateLang"]),
    isShowClick(type) {
      if (type == "lang") {
        this.active = 0;
        const langKeyList = this.langOptions.map((it) => it.value);
        this.langActive = langKeyList.indexOf(this.language);
      } else if (type == "exchange") {
        this.active = 1;
        this.moneyIndex = strFirstBit(this.exchangeRateData).indexOf(
          this.$store.state.rate
        );
      }
      this.isShow = !this.isShow;
    },
    tabIndex(index) {
      this.active = index;
    },
    changeLang(index, lang) {
      this.langActive = index;
      this.updateLang(lang.value);
      this.isShowClick();
      location.reload();
    },
    // chooseRate(index, rate) {
    //   this.$GloExchangeRate = rate;
    //   this.moneyIndex = index;
    //   this.$store.commit("SETRATE", rate.currency);
    //   this.isShowClick("exchange");
    // },
  },
};
</script>
<style scoped>
@import url("@/assets/css/login/langMoney.css");
</style>
