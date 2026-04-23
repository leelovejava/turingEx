<template>
  <div class="select-time">
    <div
      class="cursor-pointer item"
      v-for="(item, index) in timeTab"
      :key="index"
      :class="{ active: timeTypeInx === index }"
      @click="selectTimeType(item, index)"
    >
      {{ item.txt }}
    </div>
    <div class="time-select-wrapper" style="position: relative; top: 1px">
      <span>{{ $t("message.home.shijian") }}</span>
      <el-date-picker
        v-model="timeVal"
        type="daterange"
        :range-separator="$t('message.home.to')"
        :start-placeholder="$t('message.user.xuanzekaishishijian')"
        @change="changeTime"
        :value-format="'YYYY-MM-DD'"
        :end-placeholder="$t('message.user.xuanzejiehsushijian')"
      >
      </el-date-picker>
      <div>
        <div class="cursor-pointer search" @click="handleSearch">
          {{ $t("message.home.chaxun") }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "SelectTime",
  emits: ["getTime"],
  data() {
    return {
      timeVal: "",
      timeTab: [
        { txt: this.$t("message.home.oneDay"), value: 1 },
        { txt: this.$t("message.home.aWeek"), value: 7 },
        { txt: this.$t("message.home.aMonth"), value: 30 },
        { txt: this.$t("message.home.threeMonths"), value: 90 },
      ],
      timeTypeInx: "",
      endTime: "",
      startTime: "",
    };
  },
  methods: {
    selectTimeType(item, index) {
      this.timeVal = "";
      this.timeTypeInx = index;
      this.endTime = this.getBeforeDate(0);
      this.startTime = this.getBeforeDate(item.value);
      let obj = {
        endTime: this.endTime,
        startTime: this.startTime,
      };
      this.$emit("getTime", obj);
    },
    handleSearch() {
      let obj = {
        endTime: this.endTime,
        startTime: this.startTime,
      };
      this.clearTab();
      this.$emit("getTime", obj);
    },
    changeTime(val) {
      this.startTime = val[0];
      this.endTime = val[1];
    },
    clearTab() {
      (this.timeTypeInx = ""), (this.endTime = ""), (this.startTime = "");
    },
    getBeforeDate(n) {
      var n = n;
      var d = new Date();
      var year = d.getFullYear();
      var mon = d.getMonth() + 1;
      var day = d.getDate();
      if (day <= n) {
        if (mon > 1) {
          mon = mon - 1;
        } else {
          year = year - 1;
          mon = 12;
        }
      }
      d.setDate(d.getDate() - n);
      year = d.getFullYear();
      mon = d.getMonth() + 1;
      day = d.getDate();
      let s =
        year +
        "-" +
        (mon < 10 ? "0" + mon : mon) +
        "-" +
        (day < 10 ? "0" + day : day);
      return s;
    },
  },
};
</script>

<style scoped>
.select-time {
  display: flex;
  align-items: center;
  margin-top: 5px;
  margin-bottom: 19px;
}

.select-time .item {
  padding: 3px 20px;
  margin-right: 20px;
  font-size: 12px;
  color: #929aa5;
}

.select-time .active {
  background: #25282d;
  border-radius: 5px;
  color: #1d91ff;
}

.time-select-wrapper {
  display: flex;
  align-items: center;
}

.time-select-wrapper span {
  margin-right: 10px;
  color: #929aa5;
}

.search {
  padding: 3px 10px;
  margin-left: 25px;
  text-align: center;
  font-size: 12px;
  background: #24282d;
  border-radius: 3px;
  color: #fff;
}

:deep(.el-input__inner) {
  height: 26px;
  background-color: transparent;
  border: none;
}

:deep(.el-range-editor.el-input__wrapper) {
  background: #24272c !important;
  border-color: #909090 !important;
  line-height: 34px;
  box-shadow: 0 0 0 1px #24272c inset;
}

:deep(.el-date-editor .el-range-separator) {
  font-size: 12px;
  color: #fff;
}

:deep(.el-date-editor .el-range-input::placeholder) {
  font-size: 14px;
  color: #5f6672;
}

:deep(.el-date-editor .el-range__icon) {
  display: none;
}

/* :deep(.el-date-editor .el-range__close-icon) {
  display: none;
} */

:deep(.el-range-editor.el-input__inner) {
  padding: 0;
}

:deep(
    .el-date-editor--datetimerange.el-input,
    .el-date-editor--datetimerange.el-input__inner
  ) {
  width: 252px;
}

:deep(.el-date-editor .el-range-separator) {
  line-height: 26px;
}
</style>
