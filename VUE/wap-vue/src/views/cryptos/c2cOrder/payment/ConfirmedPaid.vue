<template>
  <div id="cryptos">
    <div id="full" class="flex flex-col w-full h-full">
      <order-nav @back="backRouter" :back="false" />
      <div class="flex-1 " style="overflow-y: auto">
        <div class="pb-10 text-center c2cColor mainBackground">
          <div>
            <h1 class="font-semibold text-36">{{ $t("请确认已收到付款") }}</h1>
            <div class="flex justify-center items-center mt-10 text-60 font-semibold">
              <span class="relative  mr-10 text-44">{{
                detail.currency
              }}</span>
              <span>{{ detail.amount }}</span>
              <!--            <span class="relative bottom-5 ml-16 font-normal text-44">USDT</span>-->
            </div>
            <div class="mt-8">
              <van-icon class="" color="#9399A4" name="arrow-down" />
            </div>
            <div class="flex justify-center items-center mt-12" @click="
              $router.push({
                path: '/cryptos/chat',
                query: JSON.stringify(),
              })
              ">
              <van-badge class="w-9 h-8 mr-5">
                <img class="w-full h-full" src="~@/assets/image/c2c/Vector.png" alt="" />
              </van-badge>
              <span class="text-32">{{ $t("联系买家") }}</span>
            </div>
          </div>
        </div>
        <div class="w-full pt-12 pb-8 ">
          <div class="ml-12 pl-10 pr-8 msg relative">
            <div
              class="circle absolute top-0 left-0 z-10 w-11 h-11 flex justify-center items-center bg-blue text-white text-26"
              style="border-radius: 50%">
              1
            </div>
            <div
              class="circle absolute bottom-90 left-0 z-10 w-11 h-11 flex justify-center items-center bg-blue text-white text-26"
              style="border-radius: 50%">
              2
            </div>
            <div class="text-30 c2cColor">
              {{ $t("登陆您下方的收款帐户，确认买家的付款已到账。") }}
            </div>
            <div class="mt-5 px-6 py-8 mainBackground rounded-xl msg-wrapper">
              <div class="flex items-center">
                <div class="w-2 h-7 rounded-xl mr-5" style="background: #e7bb41"></div>
                <span class="ml-2 text-30 c2cColor">{{ detail.methodName }}</span>
              </div>
              <van-cell-group>
                <van-cell class="order-number " v-for="item in params" :key="item.id">
                  <template #title>
                    <div>
                      <span class="mr-3 textColor">{{ item.name }}</span>
                    </div>
                  </template>
                  <template #default>
                    <div class="flex justify-end">
                      <span class="mr-3 textColor">{{ item.value }}</span>
                    </div>
                  </template>
                </van-cell>
                <!-- <van-cell class="order-number" title="银行卡号">
                <template #default>
                  <div class="flex justify-end">
                    <span class="mr-14">{{ orderInfo.paramValue1 }}</span>
                  </div>
                </template>
              </van-cell>
              <van-cell
                class="order-number buyer"
                style="background: #f5f5f5"
                title="买家实名"
              >
                <template #default>
                  <div class="flex justify-end">
                    <span class="mr-14">{{ $t(orderInfo.realName) }}</span>
                  </div>
                </template>
              </van-cell> -->
              </van-cell-group>
            </div>
            <div class="mt-10 text-30 c2cColor">
              {{
                $t("确认收到款项后，返回平台，点击下方按钮「我已确认收款」。")
              }}
            </div>
            <div class="flex items-center mt-6">
              <img class="w-6 h-6" src="~@/assets/image/c2c/Group41-2.png" alt="" />
              <span class="ml-4 text-24 text-blue">{{
                $t("若您未收到款项，请勿点击按钮，避免资产损失。")
              }}</span>
            </div>
          </div>
        </div>
        <div class="px-8 my-8 mainBackground">
          <van-collapse v-model="activeNames">
            <van-collapse-item name="1">
              <template #title>
                <span class="c2cColor text-28">{{ $t("交易条款") }}</span>
              </template>
              <van-divider />
              <p class="text-28 c2cColor pt-5">{{ $t("资金绝对安全") }}</p>
              <p class="mt-5 text-28 c2cColor">
                {{ $t("平时订单较多，看见了会立马打款。急单勿拍！") }}
              </p>
            </van-collapse-item>
          </van-collapse>
        </div>
      </div>
      <div class="px-10 pt-14 pb-10 flex text-30 mainBackground">
        <van-button class="w-60 h-20 mr-4 rounded-xl c2cColor greyBg border-none" type="primary"
          style="margin-right: 16px;" @click="$router.push({ path: '/cryptos/appeal/page' })">{{ $t("帮助") }}
        </van-button>
        <van-button class="flex-1 h-20 rounded-xl bg-blue text-white border-none" type="primary" @click="handleClick">{{
          $t("我已确认收款")
        }}
        </van-button>
      </div>

      <!--  弹窗  -->
      <van-popup v-model:show="show" round position="bottom">
        <div class="pt-11">
          <div class="mb-10 font-semibold text-32 text-center">
            {{ $t("您是否已收到款项？") }}
          </div>
          <div class="">
            <van-radio-group v-model="radio">
              <van-radio name="1" class="p-8" :class="{ active: radio === '1' }">
                <span>{{ $t("我还没登陆收款账户确认款项无误。") }}</span>
                <img class="w-10 h-10 gou" src="~@/assets/image/c2c/Group2318.png" alt="" />
                <template #icon="props">
                  <img class="img-icon" :src="props.checked ? activeIcon : inactiveIcon" />
                </template>
              </van-radio>
              <van-radio name="2" class="p-8" :class="{ active: radio === '2' }">
                <span>{{
                  $t(
                    "我已确认收款无误，付款人与买家在DOME上的验证姓名一致，确认放行数字货币给买家。"
                  )
                }}</span>
                <img class="w-10 h-10 gou" src="~@/assets/image/c2c/Group2318.png" alt="" />
                <template #icon="props">
                  <img class="img-icon" :src="props.checked ? activeIcon : inactiveIcon" />
                </template>
              </van-radio>
            </van-radio-group>
          </div>
          <div class="mt-14">
            <div class="flex box-border pl-6 pb-8 pr-12">
              <img class="w-9 h-9 mr-4" src="~@/assets/image/c2c/Group41.png" alt="" />
              <div class="text-24">
                <p class="text-28 textColor1">{{ $t("温馨提示") }}</p>
                <p class="my-4">
                  {{
                    $t(
                      "1.收款时，请勿盲目相信转账截图，务必打开收款账户核对款项无误。"
                    )
                  }}
                </p>
                <p class="my-4">
                  {{ $t("2.若付款仍在进行中，请等待款项到账后再放币。") }}
                </p>
                <p>
                  {{
                    $t(
                      "3.请勿接受第三方付款。若收到与APP上的验证姓名不相匹配的款项，请立即退款，并避免因放行后遭银行拒付而造成财务损失。"
                    )
                  }}
                </p>
              </div>
            </div>
          </div>
          <div class="mt-16 px-8 pb-10 flex text-30 mainBgColor">
            <van-button class="w-60 h-20 rounded-xl c2cColor border-none financialBut" style="margin-right: 16px;"
              @click="$router.push({ path: '/cryptos/appeal/page' })" type="primary">{{ $t("帮助") }}
            </van-button>
            <van-button class="flex-1 h-20 rounded-2xl bg-blue text-white border-none" type="primary"
              :disabled="radio === '1'" @click="onConfirm">{{ $t("我已确认收款") }}
            </van-button>
          </div>
        </div>
      </van-popup>
      <div v-if="isLoading" class="fixed top-0 left-0 w-full h-full loading-wrapper">
        <van-loading color="#fff" />
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import {
  Badge,
  Button,
  Cell,
  CellGroup,
  Collapse,
  CollapseItem,
  CountDown,
  Divider,
  Icon,
  Popup,
  Radio,
  RadioGroup,
  Loading,
  Toast,
} from "vant";
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import Question from "@/views/cryptos/c2cOrder/payment/components/Question.vue";
import otcApi from "@/service/otc.api";

export default {
  name: "ConfirmedPaid",
  data() {
    return {
      isLoading: false,
      show: false,
      activeNames: [],
      radio: "1",
      activeIcon: new URL('@/assets/image/c2c/Group23172.png', import.meta.url),
      inactiveIcon: new URL('@/assets/image/c2c/Group2317.png', import.meta.url),
      passwd: "",
      detail: {},
      params: [],
    };
  },
  mounted() {
    const order_no = this.$store.state.c2c.order_no;
    otcApi.ctcOrderGetDetail({ order_no, language: this.$i18n.locale }).then((res) => {
      this.detail = res;
      this.params = [{ id: 0, name: this.$t('姓名'), value: this.detail.realName }];
      for (let i = 1; i < 16; i++) {
        this.params.push({
          id: i,
          name: this.detail["paramName" + i],
          value: this.detail["paramValue" + i],
        });
      }
      this.params = this.params.filter((item) => item.name && item.value);
      console.log(this.detail);
    });
  },
  activated() {
    // const order_no = this.$store.state.c2c.order_no;
    // otcApi.ctcOrderGetDetail({ order_no, language: this.$i18n.locale }).then((res) => {
    //   this.detail = res;
    //   this.params = [{ id: 0, name: this.$t('姓名'), value: this.detail.realName }];
    //   for (let i = 1; i < 16; i++) {
    //     this.params.push({
    //       id: i,
    //       name: this.detail["paramName" + i],
    //       value: this.detail["paramValue" + i],
    //     });
    //   }
    //   this.params = this.params.filter((item) => item.name && item.value);
    //   console.log(this.detail);
    // });
    // this.$bus.$on("returnPwd", (pwd) => {
    //   this.passwd = pwd;
    //   this.enterTrade();
    // });
  },
  methods: {
    onConfirm() {
      this.$router.push({
        path: "/cryptos/withdraw/securityVerification",
        query: {
          type: "sell",
        },
      });
    },
    back() {
      this.show = false;
    },
    backRouter() {
      this.$router.push('/cryptos/wantBuy')
    },
    handleClick() {
      console.log("handle");
      this.show = true;
    },
    enterTrade() {
      // 订单放行
      otcApi.ctcOrderPass({
        order_no: this.detail.orderNo,
        safe_password: this.passwd,
      }).then((res) => {
        this.isLoading = false;
        this.$router.replace({
          path: "/cryptos/tradeSuccessSell",
        });
      });
    },
  },
  // watch: {
  //   passwd() {
  //     if (this.passwd === '000000') {
  //       console.log(111);

  //       this.isLoading = true;

  //       // 发送请求
  //       this.enterTrade();
  //     }
  //   }
  // },
  components: {
    [CountDown.name]: CountDown,
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    [Button.name]: Button,
    [Popup.name]: Popup,
    [Icon.name]: Icon,
    [Badge.name]: Badge,
    [Collapse.name]: Collapse,
    [CollapseItem.name]: CollapseItem,
    [Divider.name]: Divider,
    [RadioGroup.name]: RadioGroup,
    [Radio.name]: Radio,
    [Loading.name]: Loading,
    OrderNav,
    Question,
  },
  beforeUnmount() {
    // this.$bus.$off("returnPwd");
  },
};
</script>

<style lang="scss" scoped>
#cryptos {
  font-size: 30px;

  #full {
    :deep(.van-count-down) {
      font-size: 26px;
      color: $blue;
    }

    .msg-wrapper {
      .van-cell {
        margin-top: 28px;
        padding: 0;
        color: #fff;

        .van-cell__title,
        .van-cell__value {
          color: $text_color;
          font-size: 26px;
        }
      }
    }

    :deep(.van-cell-group) {
      background: $main_background;
    }

    :deep(.van-cell),
    :deep(.van-collapse-item__content) {
      background: $main_background;
      padding-top: 20px;
      padding-bottom: 20px;
    }

    :deep(.van-cell::after) {
      display: none;
    }

    :deep(.van-collapse-item__title--expanded::after) {
      display: none;
    }

    :deep(.van-collapse-item__content) {
      padding: 20px 0;
    }

    :deep(.van-radio__label) {
      position: relative;
      width: 664px;
      margin-left: 20px;
      color: $text_color1;

      .gou {
        display: none;
        position: absolute;
        top: 50%;
        right: -54px;
        transform: translateY(-50%);
      }
    }

    :deep(.loading-wrapper) {
      z-index: 20;
      background: rgba(0, 0, 0, 0.3);

      .van-loading {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
      }
    }

    :deep(.van-divider) {
      border-color: $line_color;
    }
  }



  .active {
    // background: #fafafa;
    background: none !important;
    border: none !important;

    .gou {
      display: block !important;
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
      height: 86%;
      background: #eaebee;
    }
  }

  .msg-wrapper {
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  }

  .circle {
    transform: translateX(-50%);
  }

  .buyer {
    padding: 10px 18px !important;
    font-size: 28px;
  }

  .img-icon {
    width: 32px;
    height: 32px;
  }
}
</style>
