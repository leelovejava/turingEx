<template>
  <div class="secondar-box">
    <ul>
      <li
        v-for="(item, index) in secondaryLineList"
        :key="index"
        :class="[selectIndex == item ? 'active' : '']"
        @click="checkLineType(item)"
      >
        {{ item }}
      </li>
    </ul>
  </div>
</template>
<script>
export default {
  emits: ["checkLineType"],
  name: "secondaryLine",
  data() {
    return {
      secondaryLineList: [
        "VOL",
        "MACD",
        "KDJ",
        "RSI",
        "DMI",
        "OBV",
        "BOLL",
        "SAR",
        "DMA",
        "TRIX",
        "BRAR",
        "VR",
        "EMV",
        "WR",
        "ROC",
        "MTM",
        "PSY",
      ],
      selectIndex: "VOL",
    };
  },
  mounted() {
    this.$emit("checkLineType", this.selectIndex);
  },

  watch: {},
  methods: {
    //选择副线
    checkLineType(item) {
      this.selectIndex = item;
      this.$emit("checkLineType", this.selectIndex);
    },
  },
};
</script>
<style scoped>
.secondar-box {
}
ul {
  display: flex;
  padding-left: 10px;
}
li {
  padding-right: 13px;
  color: #929aa5;
  padding-top: 15px;
}
.active {
  color: #1d91ff !important;
}
</style>
