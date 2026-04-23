<!-- 市价全平 -->
<template>
  <div v-if="isShow">
    <div
      class="okui-transition-fade okui-dialog market-close-dialog okui-tip-dialog okui-dialog-float okui-transition-fade-entered"
      style="transition-duration: 100ms; z-index: 10053"
    >
      <div class="okui-dialog-window okui-dialog-window-float bottom-align">
        <div
          class="okui-dialog-top-l okui-dialog-top-action-fixed okui-dialog-top-action-hidden"
        >
          <i
            class="icon iconfont okui-dialog-top-action-hidden iconicn-left-arrow-glyph-sm-copy okui-dialog-b-btn"
          ></i>
        </div>
        <div class="okui-dialog-top-r okui-dialog-top-action-fixed">
          <i
            class="icon iconfont iconicn-close-outline-lg-copy okui-dialog-c-btn"
            @click="open"
          ></i>
        </div>
        <div id="scroll-box" class="okui-dialog-scroll-box">
          <div class="okui-dialog-container">
            <div class="okui-dialog-tip-content">
              <div class="okui-dialog-tip-icon-bg info-icon-bg">
                <i class="icon iconfont tip-icon iconinfo info-icon"></i>
              </div>
            </div>
            <div class="market-close-tip">
              <span class="market-close-title"
                >{{ $t("message.home.querendui") }} {{ obj.name }}
                {{ $t("message.home.yongxu") }}
                <em
                  class="position-side"
                  :class="[obj.direction === 'buy' ? 'green' : 'red']"
                >
                  {{
                    obj.direction === "buy"
                      ? $t("message.home.kaiduo")
                      : $t("message.home.kaikong")
                  }}
                  <!-- {{ obj.direction | fliterDir }} -->
                </em>
                {{ obj.lever_rate | leverFilter }}
                {{ $t("message.home.cangweijinxing") }}
                <em class="close-all">{{ $t("message.home.pingcang") }}</em
                >？
              </span>
              <span class="market-close-subtitle">{{
                $t("message.home.ruguofashengpingcangguadan")
              }}</span>
            </div>
          </div>
        </div>
        <div class="okui-dialog-footer-box">
          <div class="okui-dialog-btn-box layout-full">
            <button
              type="button"
              class="okui-btn btn-sm btn-fill-primary dialog-btn"
              @click="submit"
            >
              <span class="btn-content">{{ $t("message.home.queren") }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Axios from "@/api/perpetualContract.js";
import bus from "vue3-eventbus";

export default {
  name: "closeAllDialog",
  data() {
    return {
      isShow: false,
      obj: {},
    };
  },
  filters: {
    fliterDir(val) {
      if (val == "buy") {
        return $t("message.home.kaiduo");
        // return "开多";
      } else if (val == "sell") {
        return $t("message.home.kaikong");
        // return "开空";
      }
    },
    leverFilter(val) {
      if (val) {
        var data = Number(val).toFixed(2);
        return data + "X";
      } else {
        return "1X";
      }
    },
  },
  methods: {
    open(val) {
      this.obj = val;
      console.log(val);
      this.isShow = !this.isShow;
    },
    submit() {
      const data = {
        order_no: this.obj.order_no,
      };
      Axios.orderClose(data).then((res) => {
        //刷新token
        bus.emit("getSesstionToken", "close");
        if (res.code == "0") {
          this.$message({
            message: this.$t("message.home.pingcangchengong"),
            type: "success",
          });
          this.isShow = !this.isShow;
        }
      });
    },
  },
};
</script>
<style lang="css" scoped>
@import url("@/assets/css/commonTrade/closeDialog.css");
</style>
