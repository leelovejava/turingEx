<template>
  <div v-if="isShow">
    <div
      style="z-index: 10012"
      class="okline-dialog-box okline-1655925962926 pc dark"
    >
      <div class="okline-dialog-content">
        <div class="okline-settings-container">
          <div class="settings-title">
            {{ $t("message.home.zhibiaoshezhi") }}
          </div>
          <div class="settings-content">
            <div
              class="settings-name checked"
              indictype="mainIndics"
              indicname="MA"
            >
              <el-checkbox-group v-model="region">
                <el-checkbox label="EMA" name="type"
                  >EMA
                  {{
                    $t("message.home.zhishupinghuayidongpingjunxian")
                  }}</el-checkbox
                >
                <el-checkbox label="MA" name="type"
                  >MA {{ $t("message.home.yidongpingjunxian") }}</el-checkbox
                >
                <el-checkbox label="BOLL" name="type"
                  >BOLL {{ $t("message.home.bulinxian") }}</el-checkbox
                >
                <el-checkbox label="SAR" name="type">SAR</el-checkbox>
                <el-checkbox label="VOL" name="type"
                  >VOLUME {{ $t("message.home.chengjiaoliang") }}</el-checkbox
                >
                <el-checkbox label="MACD" name="type"
                  >MACD
                  {{ $t("message.home.zhishupinghuayidong") }}</el-checkbox
                >
                <el-checkbox label="KDJ" name="type"
                  >KDJ {{ $t("message.home.suijizhibiao") }}</el-checkbox
                >
              </el-checkbox-group>
            </div>
            <div class="btnBox">
              <div class="params-btn reset-btn" @click="restKline">
                {{ $t("message.home.chongzhi") }}
              </div>
              <div class="params-btn confirm-btn" @click="close">
                {{ $t("message.home.queding") }}
              </div>
            </div>
          </div>
          <div class="close-btn" @click="close">
            <i class="iconfont icon-cancel"></i>
          </div>
          <div class="back-btn hide"><i class="iconfont icon-Unfold"></i></div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import bus from "vue3-eventbus";
export default {
  name: "klineDialog",
  data() {
    //   ['VOL', 'MACD', 'KDJ']
    return {
      region: ["MA"],
      isShow: false,
    };
  },
  watch: {
    region(val) {
      bus.emit("aMsg", val);
    },
  },
  mounted() {
    this.restKline();
  },
  methods: {
    restKline() {
      this.region = ["MA"];
      bus.emit("removeKline", "remove");
    },
    close() {
      this.isShow = !this.isShow;
    },
  },
};
</script>
<style>
@import url("@/assets/css/commonTrade/kline.css");
</style>
