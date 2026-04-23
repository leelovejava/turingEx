<!-- 日期搜索组件 -->
<template>
  <div class="search-header">
    <div
      class="changdate"
      :class="{ active: isActive == index }"
      v-for="(item, index) in datedata"
      :key="index"
      @click="changeDate(item, index)"
    >
      {{ item.txt }}
    </div>
    <div class="datepicker">
      <div class="time-text">{{ $t("message.home.shijian") }}</div>
      <el-date-picker
        v-model="dateValue"
        type="daterange"
        :range-separator="$t('message.home.to')"
        :start-placeholder="$t('message.user.xuanzekaishishijian')"
        :end-placeholder="$t('message.user.xuanzejiehsushijian')"
        @change="dataPickerChange"
      >
      </el-date-picker>
    </div>
    <span class="btn" @click="handleSearch">{{
      $t("message.home.chaxun")
    }}</span>
    <span class="btn" @click="handleReset">{{
      $t("message.home.chongzhi")
    }}</span>
  </div>
</template>

<script>
import dayjs from "dayjs";
export default {
  name: "dateSearch",
  data() {
    return {
      datedata: [
        { txt: this.$t("message.home.oneDay"), value: 1 },
        { txt: this.$t("message.home.aWeek"), value: 7 },
        { txt: this.$t("message.home.aMonth"), value: 30 },
        { txt: this.$t("message.home.threeMonths"), value: 90 },
      ],
      isActive: -1,
      dateValue: "",
      endTime: "",
      startTime: "",
    };
  },

  methods: {
    handleSearch() {
      this.$parent.getList(this.startTime, this.endTime, true);
    },
    handleReset() {
      this.dateValue = [];
      this.startTime = undefined;
      this.endTime = undefined;
      this.isActive = -1;
      this.handleSearch();
    },
    dataPickerChange(val) {
      if (val && val.length > 1) {
        this.startTime = dayjs(val[0]).format("YYYY-MM-DD");
        this.endTime = dayjs(val[1]).format("YYYY-MM-DD");
      }else{
        this.startTime = '';
        this.endTime = '';
      }
    },
    changeDate(item, index) {
      this.isActive = index;
      const time = dayjs().format("YYYY-MM-DD");
      this.endTime = time;

      switch (item.value) {
        case 1:
          this.startTime = time;
          break;
        case 7:
          this.startTime = dayjs().subtract(7, "day").format("YYYY-MM-DD");
          break;
        case 30:
          this.startTime = dayjs().subtract(1, "month").format("YYYY-MM-DD");
          break;
        case 90:
          this.startTime = dayjs().subtract(3, "month").format("YYYY-MM-DD");
          break;
        default:
          break;
      }
      this.handleSearch();
    },
  },
};
</script>
<style scoped>
.search-header {
  line-height: 40px;
}
.search-header div {
  display: inline-block;
}
.changdate {
  width: 90px;
  text-align: center;
  cursor: pointer;
}

.changdate:hover {
  background: #24282d;
}
.active {
  background: #24282d;
  color: #1c73c7;
}
.time-text {
  margin-top: -4px;
  margin-right: 10px;
}
.datepicker {
  margin-left: 10px;
  /* margin-top: 4px; */
}

.search-header .btn {
  display: inline-block;
  margin-left: 20px;
  line-height: 25px;
  text-align: center;
  background: #24282d;
  border-radius: 4px;
  cursor: pointer;
  padding: 2px 8px;
}
.search-header .btn:hover {
  color: #1c73c7;
}
</style>
