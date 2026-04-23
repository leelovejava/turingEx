<template>
  <div class="right-padding">
    <div class="right-title">{{ $t("message.user.tongyong") }}</div>
    <div>
      <div class="uniBox">
        <span class="border_left"></span>
        <span>{{ $t("message.user.yuyanshezhi") }}</span>
      </div>
      <el-select v-model="language" class="maLF" @change="handleChangeLang">
        <el-option
          v-for="item in langOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
    </div>
    <div>
      <div class="uniBox">
        <span class="border_left cColor"></span>
        <span>{{ $t("message.user.jijiafangshi") }}</span>
      </div>
      <el-select v-model="rateVal" class="maLF" @change="handleChangeRate">
        <el-option
          v-for="item in exchangeRateData"
          :key="item.currency"
          :label="item.currency"
          :value="item.currency"
        >
        </el-option>
      </el-select>
    </div>
  </div>
</template>
<script>
// import { mapState } from 'pinia';
import { useI18n } from "vue-i18n";
import { langOptions } from "@/utils/index";
import { mapState, mapActions } from "pinia";
import { useLanguageStore } from "@/store/lang";

export default {
  name: "universal",
  data() {
    return {
      langOptions,
      value: "zh-CN",
      value2: "人民币[CNY]",
      rateVal: "USD",
      priceOptions: [
        {
          value: "台币[TWD]",
          label: "台币[TWD]",
        },
        {
          value: "港元[HKD]",
          label: "港元[HKD]",
        },
        {
          value: "欧元[EUR]",
          label: "欧元[EUR]",
        },
        {
          value: "加币[CAD]",
          label: "加币[CAD]",
        },
        {
          value: "马币[MYR]",
          label: "马币[MYR]",
        },
        {
          value: "人民币[CNY]",
          label: "人民币[CNY]",
        },
        {
          value: "美元[USD]",
          label: "美元[USD]",
        },
        {
          value: "泰铢[THB]",
          label: "泰铢[THB]",
        },
        {
          value: "澳元[AUD]",
          label: "澳元[AUD]",
        },
        {
          value: "韩元[KRW]",
          label: "韩元[KRW]",
        },
      ],
      exchangeRateData: [
        {
          currency: "USD",
          rate: 1,
        },
      ],
    };
  },
  mounted() {
    // this.rateVal = this.rate

    const { t, locale } = useI18n();

    if (localStorage.getItem("SETRATE")) {
      this.rateVal = localStorage.getItem("SETRATE");
    }

    //获取计价的币种
    // Axios.getExchangerateuserconfig().then((res) => {
    //   this.exchangeRateData = res.data;
    //   console.log("res.data = " + JSON.stringify(res.data));
    // });
  },
  computed: {
    ...mapState(useLanguageStore, ["language"]),
  },
  methods: {
    ...mapActions(useLanguageStore, ["updateLang"]),
    handleChangeLang(lang) {
      this.updateLang(lang);
      location.reload();
    },
    handleChangeRate(val) {
      localStorage.setItem("SETRATE", val);
      location.reload();
    },
  },
};
</script>
<style scoped>
.universal {
  padding: 10px;
}

.uniBox {
  display: flex;
  width: 800px;
  height: 74px;
  background-color: rgba(250, 250, 250, 1);
  align-items: center;
  margin: 20px 12px 20px 0;
}

.uniBox span {
  display: inline-block;
  margin-left: 30px;
}

.border_left {
  display: inline-block;
  background-color: rgba(252, 213, 53, 1);
  width: 6px;
  height: 30px;
}

.maLF {
  margin-left: 65px;
  width: 240px;
}

.cColor {
  background-color: rgba(2, 167, 240, 1);
}
</style>
