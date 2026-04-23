<template>
  <div class="pb-12 main recharge-withdraw-page">
    <!-- 头部  -->
    <div class="main-title">
      <div class="title-center">
        <!--  生成订单  -->
        <div class="title-left">
          <div class="title flex">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              class="css-146agw4"
              @click="gotoPrePage"
              width="24"
            >
              <path
                fill-rule="evenodd"
                clip-rule="evenodd"
                d="M11.934 12l3.89 3.89-1.769 1.767L8.398 12l1.768-1.768 3.89-3.889 1.767 1.768-3.889 3.89z"
                fill="currentColor"
              ></path>
            </svg>
            {{ $t("message.user.dingdanyishengcheng") }}
          </div>
          <div class="text mt-2 flex ordering">
            {{ $t("message.user.pleaseAt") }}
            <span class="time">
              <span class="bg-time">{{ hour.substring(0, 1) }}</span>
              <span class="bg-time">{{ hour.substring(1, 2) }}</span>
              <span style="margin: 0 2.5px">:</span>
              <span class="bg-time">{{ minute }}</span>
              <span class="bg-time">{{ secound }}</span>
            </span>
            {{ $t("message.user.paymentSeller")
            }}{{ $t("message.user.maijia") }}
          </div>
        </div>
        <!--  订单号  -->
        <div class="title-right">
          <div class="order-c" style="margin-bottom: 15px">
            <span style="color: #727a89">{{
              $t("message.c2c.dingdanhao")
            }}</span>
            <span style="margin: 0 10px">{{ detailInfo?.orderNo }}</span>
            <img
              @click="handleCopy(detailInfo?.orderNo)"
              src="@/assets/images/c2c/orderSuccess/Group1884.png"
              style="width: 15px; height: 15px"
            />
          </div>
          <div class="order-c">
            <span style="color: #727a89">{{
              $t("message.c2c.chuangjianshijian")
            }}</span>
            <span style="margin: 0 10px">{{
              formatTime(detailInfo.createTime)
            }}</span>
          </div>
        </div>
      </div>
    </div>
    <!-- 中间部分 -->
    <div class="main-center justify-between">
      <div class="main-left">
        <!-- 在线客服 -->
        <div class="bg-gray-100 p-4 mt-5" @click="onRoute">
          <div class="flex">
            <img src="@/assets/images/wallet/chatBlue.svg" />
            <span class="mr-2 ml-2">{{ $t("message.home.dibu18") }}</span>
            <img class="gold" src="@/assets/images/wallet/gold.svg" />
            <img class="gold" src="@/assets/images/wallet/arrow-right.svg" />
          </div>
          <div class="flex step mt-5">
            <img
              class="success mr-1"
              src="@/assets/images/wallet/success.svg"
            />

            <!-- {{ $t("message.user.maijia") }}
            {{ detailInfo.currency }} -->
            {{ $t("message.user.Payconfidence") }}
          </div>
          <div class="flex mt-5">
            <div class="flex step flex-1">
              <img class="mr-2" src="@/assets/images/wallet/success.svg" />
              {{ $t("message.user.customerSupport") }}
            </div>
            <div class="pr-2">
              <el-icon :size="16" color="#9399A4"><ArrowRight /></el-icon>
            </div>
          </div>
        </div>

        <!-- 步骤条 -->
        <div class="main-main">
          <img
            src="@/assets/images/c2c/orderSuccess/line.png"
            class="left-line"
          />
          <div class="main-info">
            <!-- 购买usd -->
            <p class="font-semibold mb-4">
              {{ $t("message.user.goumai") }}USDT
            </p>
            <!-- 总额          :style="{ color: isBuy ? '#4ea372' : '#BD3F4D' }"-->
            <div class="total">
              <div class="t-box">
                <p class="label">
                  {{ $t("message.c2c.zongjia") }}
                </p>
                <p class="price" style="color: '#4ea372'">
                  {{ detailInfo.amount }}
                </p>
              </div>
              <!-- 银行名称 -->
              <div class="t-box">
                <p class="label">
                  {{ $t("message.user.yinhangmingcheng") }}
                </p>
                <p class="price">{{ detailInfo?.paramValue1 }}</p>
              </div>
              <!-- 账户地址 -->
              <div class="t-box">
                <p class="label">
                  {{ $t("message.user.zhanghudizhi") }}
                </p>
                <p class="price">{{ detailInfo.paramValue3 }}</p>
              </div>
            </div>

            <!-- 交易方式 -->
            <p class="font-semibold mb-4 mt-24">
              {{ $t("message.user.payType") }}
            </p>
            <div class="total">
              <div class="t-box">
                <p class="label">
                  {{ $t("message.user.leixing") }}
                </p>
                <p class="price">{{ $t("message.user.yinhangka") }}</p>
              </div>
            </div>

            <!-- 付款完成清联系客服 -->
            <p class="font-semibold mb-4 mt-24">
              {{ $t("message.user.fukuanwancheng") }}
            </p>
          </div>
        </div>
        <!-- 去付款 -->
        <div>
          <el-button type="primary" @click="handleGotoPay">{{
            $t("message.user.toPay")
          }}</el-button>
          <!-- <el-button text class="blue-color" @click="handleCancelOrder">取消订单</el-button> -->
        </div>
      </div>
      <!--右边聊天窗 -->
      <div>
        <div class="service" v-if="!chatShow">
          <img src="@/assets/images/wallet/chat.svg" />
          <div class="text-white ml-2 cursor-pointer" @click="handleShowChat">
            {{ $t("message.user.lianxikefu") }}
          </div>
        </div>
        <RightChat :detailInfo="detailInfo" v-else />
      </div>
    </div>
  </div>
</template>

<script>
/* eslint-disable */
import { mapState } from "pinia";
import dayjs from "dayjs";
import Axios from "@/api/c2c.js";
import RightChat from "../components/rightChat.vue";
import { ArrowRight } from "@element-plus/icons-vue";
import { useUserStore } from "@/store/user";

export default {
  name: "orderSuccess",
  components: { RightChat,ArrowRight },
  data() {
    return {
      detailInfo: {},
      interval: null,
      minute: "",
      hour: "",
      secound: "",
      time: 0,
      message: "",
      timer: null,
      isTime: true,
      chatShow: false,
    };
  },
  watch: {
    "$route.query.id"() {},
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  created() {
    this.getC2cOrderDetail();
  },
  methods: {
    handleShowChat() {
      this.chatShow = !this.chatShow;
    },
    handleGotoPay() {
      this.chatShow = true;
    },
    formatTime(time) {
      return dayjs(time).format("YY-MM-DD HH:mm:ss");
    },
    gotoPrePage() {
      this.$router.go(-1);
    },

    // handleCancelOrder(){
    //   if (this.time >= 0) {
    //     this.$router.push({ path: '/cryptos/cancelOrder' })
    //   } else {
    //     this.$message.error(this.$t('订单不能取消'))
    //     // Notify({ type: 'warning', message: this.$t('订单不能取消') })
    //   }
    // },

    //获取订单详情
    getC2cOrderDetail() {
      Axios.c2cOrderGetDetail({
        order_no: this.$route.query.id,
      }).then((res) => {
        if (res.code == "0") {
          this.detailInfo = res.data;

          this.time = parseInt(this.detailInfo.autoCancelTimeRemain);
          if (this.isTime) {
            this.countTime();
          }

          // this.fetchChatList();
        }
      });
    },

    //复制
    handleCopy(context) {
      navigator.clipboard.writeText(context).then(() => {
        this.$message.success(this.$t("message.user.fuzhichenggong"));
      });
    },
    //倒计时 时分秒
    countTime() {
      if (this.detailInfo.autoCancelTimeRemain == 0) {
        this.hour = "00";
        this.minute = "0";
        this.secound = "0";
        return;
      }
      this.isTime = false;
      // 用户输入任意秒数, 函数计算该毫秒数对应的时分秒, 并返回
      let _this = this;
      function getTime(time) {
        // 转换为式分秒
        let h = parseInt((time / 60 / 60) % 24);
        h = h < 10 ? "0" + h : h;
        let m = parseInt((time / 60) % 60);
        m = m < 10 ? "0" + m : m;
        let s = parseInt(time % 60);
        s = s < 10 ? "0" + s : s;
        _this.hour = h;
        _this.minute = m;
        _this.secound = s;
        // 作为返回值返回
        // return [h, m, s]
      }
      // 传入用户输入的数据
      this.interval = setInterval(() => {
        if (this.time <= 0) {
          clearInterval(this.interval);
          this.interval = null;
          this.hour = "0";
          this.minute = "0";
          this.secound = "0";
        }
        getTime(this.time);
        this.time--;
      }, 1000);
    },
  },
  unmounted() {
    if(this.interval){
      clearInterval(this.interval);
      this.interval = null;
    }
  },
};
</script>

<style lang="scss">
@import "@/assets/css/wallet/chat.scss";
.service {
  margin-top: 24px;
  padding: 10px;
  display: flex;
  background: #2555f8;
  border-radius: 40px 0 0 40px;
}
</style>
