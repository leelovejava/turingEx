<template>
  <div>
    <el-dialog
      v-if="detailData"
      :title="
        typeValue == 1
          ? detailData.name?.toUpperCase() + $t('message.user.jiaoge')
          : $t('message.user.dingdanxiangqing')
      "
      v-model="visible"
      :close-on-click-modal="false"
      width="500px"
      center
      class="detailWrapper"
      :append-to-body="true"
    >
      <div class="dialog-box">
        <!-- 交割倒计时 -->
        <div class="circleBox" v-show="showProgress">
          <div class="countdown">
            <div v-if="day" class="time-div">{{ day ? day + " d " : "" }}</div>
            <div v-if="day" class="dot">ꔷ</div>
            <div v-if="hour" class="time-div">
              {{ hour ? hour + " h " : "" }}
            </div>
            <div v-if="hour" class="dot">ꔷ</div>
            <div v-if="minute" class="time-div">
              {{ minute ? minute + " m " : "" }}
            </div>
            <div v-if="minute" class="dot">ꔷ</div>
            <div v-if="secound" class="time-div">{{ secound + " s" }}</div>
          </div>
        </div>
        <!-- 盈亏 -->
        <div v-show="showProfit" class="font-size18 text-center yingkui">
          <div v-if="detailData.profit > 0" class="green">
            {{ $t("message.home.yingkui") }} {{ detailData.profit }} {{ unit }}
          </div>
          <div v-else class="red">
            {{ $t("message.home.yingkui") }} {{ detailData.profit }} {{ unit }}
          </div>
        </div>
        <!-- 交割信息 -->
        <div class="delivery-text margin-top20">
          <div class="delivery-text-left">
            {{ $t("message.jiaoyi.jiaoyibizhong") }}
          </div>
          <div>{{ detailData?.name?.toUpperCase() }}</div>
        </div>
        <div class="delivery-text">
          <div class="delivery-text-left">
            {{ $t("message.home.fangxiang") }}
          </div>
          <div v-if="detailData.direction == 'buy'" class="green">
            {{ $t("message.jiaoyi.zuoduomairu") }}
          </div>
          <div v-if="detailData.direction == 'sell'" class="red">
            {{ $t("message.jiaoyi.zuokongmairu") }}
          </div>
        </div>
        <!-- {{ daojishi }} -->
        <div class="delivery-text">
          <div class="delivery-text-left">
            {{ $t("message.home.gouamijia") }}
          </div>
          <div>{{ detailData.open_price }}</div>
        </div>
        <div class="delivery-text">
          <div class="delivery-text-left">
            {{ $t("message.home.shuliang") }}
          </div>
          <div>{{ detailData.volume }}</div>
        </div>
        <div class="delivery-text">
          <div class="delivery-text-left">
            {{ $t("message.jiaoyi.yujishouyi") }}
          </div>
          <div :class="[detailData.profit > 0 ? 'green' : 'red']">
            {{ detailData.profit }}
          </div>
        </div>
        <div class="delivery-text" v-if="typeValue == 1">
          <div class="delivery-text-left">
            {{ $t("message.home.jiaogeshijian") }}
          </div>
          <div>{{ detailData.time_num }} {{ detailData.time_unit }}</div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <!-- <el-button v-if="typeValue == 1" @click="visible = false">{{ '取消'}}</el-button> -->
          <el-button
            type="info"
            @click="visible = false"
            class="continueOrder"
            >{{ $t("message.home.jixuxiadan") }}</el-button
          >
          <!-- <el-button v-if="typeValue == 1" type="info" @click="visible = false">{{$t('message.home.jixuxiadan')}}</el-button> -->
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { mapState, mapActions, mapStores } from "pinia";
import { useUserStore } from "@/store/user";
import Axios from "@/api/delivery.js";
import bus from "vue3-eventbus";
export default {
  emits: ["closeDialog"],
  props: {
    dialogVisible: {
      type: Boolean,
      default: false, // 弹窗是否展示
    },
    unit: {
      type: String,
      default: "",
    },

    typeValue: {
      type: Number,
    },
  },
  data() {
    return {
      percentValue: 100.0, //进度条的值
      daojishi: 30, //当前倒计时时间
      daojishi_status: null, //倒计时状态
      daojishi_s: 0, //倒计时 秒
      daojishi_baifenbi: 0, //倒计时百分比
      showProgress: true, //是否显示倒计时进度条
      showProfit: false, //显示收益
      detailData: {}, //订单详情
      secound: "",
      minute: "",
      hour: "",
      day: "",
      // nowPrice: "", //现价
      order_no: "", //订单编号
      symbol: "", //币种
      showBtn: false, //显示按钮
      timeout: null,
    };
  },
  watch: {
    visible(val) {
      if (val) {
        this.showProfit = false;
        this.showProgress = true;
      }
    },
  },

  computed: {
    ...mapState(useUserStore, ["existToken"]),
    visible: {
      get() {
        return this.dialogVisible;
      },
      set() {
        this.$emit("closeDialog");
      },
    },
  },
  mounted() {
    bus.on("showDetail", (obj) => {
      this.order_no = obj.order_no;
      this.symbol = obj.symbol;
      if (this.order_no && this.existToken) {
        this.futuresOrder(this.order_no);
      }
    });
  },
  methods: {
    clearLoopTimeout() {
      clearTimeout(this.timeout);
      this.timeout = null;
    },
    handleTimeEnd(order) {
      Axios.futuresDetail({
        order_no: order,
      }).then((res) => {
        this.clearLoopTimeout();
        this.detailData = res.data;
        if (res.data.state !== "created") {
          this.timeout = setTimeout(() => {
            this.handleTimeEnd(order);
          }, 1000);
        } else {
          this.showProfit = true;
          this.showBtn = true;
        }
      });
    },
    //倒计时 天时分秒
    countTime() {
      const t = this;
      if (t.daojishi > 0) {
        t.deliveryTime = t.daojishi;
        t.percentValue = t.percentValue - t.daojishi_baifenbi;
      }
      if (t.daojishi == 0) {
        t.deliveryTime = 0;
        t.percentValue = 0;
        clearTimeout(t.daojishi_status);
        t.daojishi_status = undefined;
        //倒计时结束，关闭进度条显示盈亏数据
        this.showProgress = false;
        this.handleTimeEnd(this.order_no);
        return;
      }
      let sub = t.daojishi;

      let day = parseInt(sub / 86400);
      let hour = parseInt((sub % 86400) / 3600);
      let min = parseInt(((sub % 86400) % 3600) / 60);
      let sec = parseInt(((sub % 86400) % 3600) % 60);
      t.secound = sec;
      t.day = day;
      t.hour = hour;
      t.minute = min;
      t.daojishi--;

      t.daojishi_status = setTimeout(() => {
        t.countTime();
      }, 1000);
    },
    // 根据订单号查看订单详情
    futuresOrder(order_no) {
      const t = this;
      Axios.futuresDetail({
        order_no,
      }).then((res) => {
        this.detailData = res.data;
        //拆分时间
        let arr = res.data.remain_time.split(":");
        //生成剩余时间秒数
        let miaoshu =
          parseInt(arr[0]) * 3600 + parseInt(arr[1]) * 60 + parseInt(arr[2]);

        //倒计时百分比计算
        t.daojishi_baifenbi = Math.round((100 / miaoshu) * 100) / 100;
        // console.log(t.daojishi_baifenbi)
        //倒计时 时间 单位秒
        t.daojishi = miaoshu;
        //提前清空定时器避免bug
        clearTimeout(t.daojishi_status);
        t.daojishi_status = undefined;
        t.percentValue = 100;
        //倒计时方法
        t.countTime();
      });
    },
  },
  unmounted() {
     // 清除定时器
    this.clearLoopTimeout()
    clearTimeout(this.daojishi_status);
    this.daojishi_status = null;
  }
};
</script>

<style lang="scss">
.detailWrapper {
  background: #1f2328 !important;
  border-radius: 5px !important;

  .el-dialog__title {
    color: #fff !important;
  }
}
</style>

<style scoped>
.dialog-box {
  width: 430px;
  margin: 0 auto;
}

.circleBox {
  position: relative;
  text-align: center;
}

.circleCenter {
  width: 150px;
  text-align: center;
  position: absolute;
  top: 60px;
  left: 50px;
}

.delivery-text {
  display: flex;
}

.delivery-text > div {
  /* flex: 1; */
  padding: 10px 0;
}

.countdown {
  display: flex;
  align-items: center;
  justify-content: center;
}

.time-div {
  width: 65px;
  height: 42px;
  background: #1d91ff;
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.dot {
  color: #da9226;
  font-size: 20px;
  font-weight: bold;
  margin: 0 10px;
}

.delivery-text-left {
  color: #929aa5;
  flex: 1;
}

.yingkui {
  padding: 15px 0 0 0;
}

.continueOrder {
  width: 100%;
  height: 50px;
  background: #1d91ff;
  border: none;
}
</style>
