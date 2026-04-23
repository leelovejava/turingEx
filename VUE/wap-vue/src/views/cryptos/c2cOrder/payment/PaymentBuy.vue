<template>
  <div id="PaymentBuyPage">
    <div id="full">
      <order-nav />
      <div class="mt-2  text-center mainBackground c2cColor">
        <div>
          <h1 class="font-bold text-40">{{ $t("请向卖家付款") }}</h1>
          <div v-if="time" class="flex justify-center items-center mt-4 text-30 font-bold">
            <span>{{ $t("订单将在倒计时结束时取消。") }}&nbsp;</span>
            <van-count-down class="flex font-bold" :time="time">
              <template #default="timeData">
                <span class="block" v-if="timeData.hours">{{
                  timeData.hours
                }}</span>
                <span class="colon" v-if="timeData.hours">:</span>
                <span class="block">{{ timeData.minutes }}</span>
                <span class="colon">:</span>
                <span class="block">{{ timeData.seconds }}</span>
              </template>
            </van-count-down>
          </div>
          <div class="flex justify-center items-center mt-10 text-66 font-bold">
            <div>
              <span class="relative text-48">{{
                currencySymbol
              }}</span>
              <span>{{
                (orderInfo.amount && (orderInfo.amount / 1).toFixed(2)) || "--"
              }}</span>
            </div>
            <img class="relative  left-25 w-8 h-8" src="~@/assets/image/c2c/Group1168.png" alt=""
              @click="copy((orderInfo.amount && (orderInfo.amount / 1).toFixed(2)) || '--')" />
          </div>
          <div class="flex justify-center items-center mt-11" @click="$router.push({ path: '/cryptos/chat' })">
            <img class="w-9 h-8 mr-5" src="~@/assets/image/c2c/Vector.png" alt="" />
            <span class="text-32">{{
              $t("联系卖家")
            }}</span>
          </div>
        </div>
      </div>
      <div class="w-full pt-11 pb-14 mt-10 mainBackground">
        <div class="ml-12 pl-9 pr-8 msg relative c2cColor">
          <div
            class="circle absolute top-0 left-0 z-10 w-11 h-11 flex justify-center items-center btnMain text-white text-26"
            style="border-radius: 50%">
            1
          </div>
          <div
            class="circle absolute bottom-28 left-0 z-10 w-11 h-11 flex justify-center items-center btnMain text-white text-26"
            style="border-radius: 50%">
            2
          </div>
          <div class="text-28">
            {{
              $t(
                "离开APP,登录您与APP上的验证姓名相匹配的银行账户或其他支付方式，转到卖家的以下账户。"
              )
            }}
          </div>
          <div class="mt-10 pt-8 px-5 pb-10 tabBackground rounded-xl msg-wrapper c2cColor">
            <div class="flex items-center">
              <div class="w-2 h-7 rounded-xl mr-5" style="background: #e7bb41"></div>
              <span class="ml-2 text-30 c2cColor">{{ detail.methodName }}</span>
            </div>
            <van-cell-group>
              <van-cell class="order-number" :title="item.name" v-for="item in params" :key="item.id">
                <template #default>
                  <div class="flex justify-end items-center c2cColor">
                    <span class="mr-4">{{ item.value }}</span>
                    <img class="relative w-6 h-7" src="~@/assets/image/c2c/Group1168.png" alt=""
                      @click="copy(item.value)" />
                  </div>
                </template>
              </van-cell>
              <!-- <van-cell class="order-number" :title="$t('银行卡号')">
              <template #default>
                <div class="flex justify-end">
                  <span class="mr-14">{{ detail.paramValue1 }}</span>
                  <img class="relative top-8 w-25 h-29" src="~@/assets/image/c2c/Group1168.png" alt="">
                </div>
              </template>
            </van-cell>
            <van-cell class="order-number" :title="$t('银行名称')">
              <template #default>
                <div class="flex justify-end">
                  <span class="mr-14">{{ detail.paramValue2 }}</span>
                  <img class="relative top-8 w-25 h-29" src="~@/assets/image/c2c/Group1168.png" alt="">
                </div>
              </template>
            </van-cell>
            <van-cell class="order-number" :title="$t('开户支行')">
              <template #default>
                <div class="flex justify-end">
                  <span class="mr-14">{{ detail.paramValue3 }}</span>
                  <img class="relative top-8 w-25 h-29" src="~@/assets/image/c2c/Group1168.png" alt="">
                </div>
              </template>
            </van-cell> -->
            </van-cell-group>
          </div>
          <div class="mt-10 text-28">
            {{ $t("付款后，返回 XX APP，务必点击下方按钮“我已付款”通知卖家。") }}
          </div>
        </div>
      </div>
      <div class="mt-20 px-10 flex text-30">
        <van-button class="w-60 h-20 rounded-xl text-black bg-grey border-none" type="primary" @click="show = true">{{
          $t("遇到问题？") }}</van-button>
        <van-button class="flex-1 h-80 rounded-2xl btnMain text-white border-none payBtn" type="primary" @click="onPayed">
          {{ $t("我已付款，通知卖家") }}</van-button>
      </div>
      <!--  遇到问题  -->
      <div>
        <van-popup class="w-full h-full" v-model:show="show" position="right">
          <question @back="back" :time="time" />
        </van-popup>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapGetters } from "vuex";
import { Cell, CellGroup, CountDown, Button, Popup, showToast } from "vant";
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import Question from "@/views/cryptos/c2cOrder/payment/components/Question.vue";
import otcApi from "@/service/otc.api";

export default {
  name: "payMent",
  // props: ['time'],
  data() {
    return {
      time: null,
      show: false,
      interval: null,
      detail: {},
      params: [],
      orderInfo: {},
    };
  },
  async created() {
    const advId = this.$store.state.c2c.adv_id;
    const order_no = this.$store.state.c2c.order_no;
    let res = await otcApi.ctcOrderGetDetail({ order_no, language: this.$i18n.locale });
    this.orderInfo = res;
    this.time = this.orderInfo.autoCancelTimeRemain * 1000;
    const methodType = this.orderInfo.methodType; // 支付方式
    this.interval = setInterval(() => {
      this.time -= 1000;
    }, 1000);
    otcApi.ctcPaymentMethodPayList({ id: advId, language: this.$i18n.locale }).then((res) => {
      // 获取支付方式
      const payMethod = res.find(
        (item) => item.methodType === methodType / 1
      );
      if (payMethod) {
        this.detail = payMethod;
        console.log(this.detail);
        this.params = [{ id: 0, name: this.$t('姓名'), value: this.detail.realName }];
        for (let i = 1; i < 16; i++) {
          this.params.push({
            id: i,
            name: this.detail["paramName" + i],
            value: this.detail["paramValue" + i],
          });
        }
        this.params = this.params.filter((item) => item.name && item.value);
      }
    });

  },
  methods: {
    back() {
      this.show = false;
    },
    onPayed() {
      // 我已支付
      this.$router.push("/cryptos/withdraw/securityVerification?type=buy");
    },
    copy(text) {
      // 复制
      if (!text) {
        return
      }
      navigator.clipboard.writeText(text).then(() => {
        showToast(this.$t('复制成功'));
      })
    },
  },
  computed: {
    ...mapState("c2cBuy", ["totalPrice"]),
    ...mapGetters('c2c', ['currencySymbol'])
  },
  components: {
    [CountDown.name]: CountDown,
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    [Button.name]: Button,
    [Popup.name]: Popup,
    OrderNav,
    Question,
  },
  beforeDestory() {
    clearInterval(this.interval);
  },
};
</script>

<style lang="scss" scoped>
#PaymentBuyPage {
  font-size: 30px;

  :deep(#full) {


    .van-count-down {
      font-size: 26px;
      color: $blue;
    }

    .van-cell {
      margin-top: 28px;
      color: $text_color1;
      background: $tab_background;

      .van-cell__title,
      .van-cell__value {
        font-size: 26px;
      }
    }

    .van-cell::after {
      display: none;
    }

    .van-cell-group {
      color: $text_color1;
      background: $tab_background;
    }
  }


  .msg {
    //border-left: 1px solid #EAEBEE;

    &:after {
      content: "";
      position: absolute;
      top: 0;
      left: 0;
      width: 1px;
      height: 93%;
      background: #eaebee;
    }
  }

  .msg-wrapper {
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  }

  .circle {
    transform: translateX(-50%);
  }

  :deep(.van-cell) {
    padding: 0 !important;
  }

  .payBtn {
    margin-left: 10px;
  }
}
</style>
