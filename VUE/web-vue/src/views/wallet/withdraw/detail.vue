<template>
  <div class="pb-12 recharge-withdraw-page">
    <!-- 头部  -->
    <Headers
      :title="$t('message.user.tikuan') + $t('message.user.xiangqing')"
    />
    <!-- 联系客服 -->
    <div class="flex justify-between width-1200 margin-center pt-8">
      <div class="fs-20 font-semibold">
        {{
          type === "bank"
            ? $t("message.user.yinhangkatikuan")
            : $t("message.user.usdttikuan")
        }}
      </div>
      <div class="service">
        <img src="@/assets/images/wallet/chat.svg" />
        <div class="text-white ml-2 cursor-pointer" @click="handleShowChat">
          {{ $t("message.user.lianxikefu") }}
        </div>
      </div>
    </div>

    <!-- 步骤条 -->
    <div class="main-center">
      <!-- 虚拟货币 -->
      <div class="main-left" v-if="type === 'crypto'">
        <div class="main-main">
          <img
            src="@/assets/images/c2c/orderSuccess/line.png"
            class="left-line"
          />
          <div class="main-info">
            <p class="font-semibold mb-4">
              {{ $t("message.user.usdttikuan") }}
            </p>
            <div class="total">
              <div class="t-box">
                <p class="label">{{ $t("message.user.bizhong") }}</p>
                <p class="price">
                  {{ detailInfo.coin }}
                </p>
              </div>

              <div class="t-box">
                <p class="label">
                  {{ $t("message.user.tikuan")
                  }}{{ $t("message.user.shuliang") }}
                </p>
                <p class="price">{{ detailInfo.volume }}</p>
              </div>

              <div class="t-box">
                <p class="label">{{ $t("message.user.zhenshidaozhang") }}</p>
                <p class="price">{{ detailInfo.amount }}</p>
              </div>
              <div class="t-box">
                <p class="label">{{ $t("message.user.shouxufei") }}</p>
                <p class="price">{{ detailInfo.fee }}</p>
              </div>
            </div>
            <!-- 地址 -->
            <p class="font-semibold mb-4 mt-24">
              {{ $t("message.user.dizhi") }}
            </p>
            <div class="total">
              <div class="t-box">
                <p class="label">{{ $t("message.user.network") }}</p>
                <p class="price">
                  {{ detailInfo.coin_blockchain.toUpperCase() }}
                </p>
              </div>

              <div class="t-box">
                <p class="label">{{ $t("message.user.xian4") }}</p>
                <p class="price">{{ detailInfo.to }}</p>
              </div>
              <!-- <div class="t-box">
                <p class="label">{{ $t("message.user.zhuanchudizhi") }}</p>
                <p class="price">{{ detailInfo.tx }}</p>
              </div> -->
            </div>

            <!-- 状态 -->
            <StateShow :state="detailInfo.state" needLabel isWithdraw />
          </div>
        </div>
        <!-- 底部 -->
        <div>
          <div>
            <span class="label-color mr-2"
              >{{ $t("message.user.dingdanhao") }}: </span
            >{{ detailInfo.order_no }}
            <img
              class="inline-block"
              @click="handleCopy(detailInfo?.order_no)"
              src="@/assets/images/c2c/orderSuccess/Group1884.png"
              style="width: 15px; height: 15px"
            />
          </div>
          <div>
            <span class="label-color mr-2"
              >{{ $t("message.c2c.chuangjianshijian") }}: </span
            >{{ detailInfo.create_time }}
          </div>
        </div>
      </div>
      <!-- 银行 -->
      <div class="main-left" v-else>
        <div class="main-main">
          <img
            src="@/assets/images/c2c/orderSuccess/line.png"
            class="left-line"
          />
          <div class="main-info">
            <p class="font-semibold mb-4">
              {{ $t("message.user.yinhangkatikuan") }}
            </p>
            <div class="total">
              <div class="t-box">
                <p class="label">{{ $t("message.user.FrenchCurrency") }}</p>
                <p class="price">
                  {{ detailInfo.currency }}
                </p>
              </div>

              <div class="t-box">
                <p class="label">
                  {{ $t("message.user.tikuan")
                  }}{{ $t("message.user.shuliang") }}
                </p>
                <p class="price">{{ detailInfo.amount }}</p>
              </div>

              <div class="t-box">
                <p class="label">{{ $t("message.user.zhenshidaozhang") }}</p>
                <p class="price">{{ detailInfo.amount }}</p>
              </div>
            </div>

            <p class="font-semibold mb-4 mt-24">
              {{ $t("message.user.tikuan") }} {{ $t("yinhang") }}/{{
                $t("zhanghao")
              }}
            </p>
            <div class="total">
              <div class="t-box">
                <p class="label">{{ $t("message.user.yinhangmingcheng") }}</p>
                <p class="price">
                  {{ detailInfo?.paramValue1 }}
                </p>
              </div>

              <div class="t-box">
                <p class="label">{{ $t("message.user.yinhangkazhanghu") }}</p>
                <p class="price">{{ detailInfo?.paramValue3 }}</p>
              </div>
            </div>
            <StateShow
              :state="detailInfo.state"
              needLabel
              :type="type"
              isWithdraw
            />
          </div>
        </div>
        <!-- 底部 -->
        <div>
          <div>
            <span class="label-color mr-2"
              >{{ $t("message.user.dingdanhao") }}:</span
            >{{ detailInfo.orderNo }}
            <img
              class="inline-block"
              @click="handleCopy(detailInfo?.orderNo)"
              src="@/assets/images/c2c/orderSuccess/Group1884.png"
              style="width: 15px; height: 15px"
            />
          </div>
          <div>
            <span class="label-color mr-2"
              >{{ $t("message.c2c.chuangjianshijian") }}:</span
            >{{ detailInfo.createTime }}
          </div>
        </div>
      </div>
      <!--右边聊天窗 -->
      <RightChat :detailInfo="detailInfo" v-if="chatShow"></RightChat>
    </div>
  </div>
</template>

<script>
import Headers from "../components/headers.vue";
import StateShow from "../components/StateShow.vue";
import RightChat from "../components/rightChat.vue";
import { mapState } from "pinia";
import Axios from "@/api/c2c.js";
import walletAxios from "@/api/wallet.js";
import { useUserStore } from "@/store/user";

export default {
  name: "withdrawDetail",
  components: { Headers, StateShow, RightChat },
  data() {
    return {
      detailInfo: {},
      chatShow: false,

      message: "",

      type: "bank",
    };
  },

  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  mounted() {
    this.type = this.$route.query.type;
    this.getC2cOrderDetail();
  },
  methods: {
    handleShowChat() {
      this.chatShow = !this.chatShow;
    },

    //复制
    handleCopy(context) {
      navigator.clipboard.writeText(context).then(() => {
        this.$message.success(this.$t("message.user.fuzhichenggong"));
      });
    },

    //获取订单详情
    getC2cOrderDetail() {
      let detailFun = Axios.c2cOrderGetDetail;

      if (this.type === "crypto") {
        detailFun = walletAxios.getWithdrawOrderDetail;
      }
      detailFun({
        order_no: this.$route.query.id,
      }).then((res) => {
        if (res.code == "0") {
          this.detailInfo = res.data;

          // this.fetchChatList();
        }
      });
    },
  },
};
</script>

<style lang="scss" scoped>
:deep(.head-content) {
  padding: 24px 0 !important;
  width: 1200px;
  margin: 0 auto;
}
</style>

<style lang="scss">
@import "@/assets/css/wallet/chat.scss";
.service {
  padding: 10px;
  display: flex;
  background: #2555f8;
  border-radius: 40px 0 0 40px;
}
</style>
