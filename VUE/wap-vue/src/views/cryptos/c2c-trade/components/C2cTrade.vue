<template>
  <div id="C2cTradPage">
    <div class="c2cTrade">
      <normal-head :title="title" />
      <div class="w-full px-8 box-border relative tabBackground pb-5">
        <div class="flex pt-8 box-border items-center">
          <span class="text-grey text-26 mr-5">{{ $t("单价") }}</span>
          <span class="text-green text-26 mr-9">{{ currencySymbol }} {{ detail.symbol_value }}</span>
          <!-- <span class="text-green text-26 mr-35">{{ currency.currency_symbol }} {{ detail.symbol_value }}</span> -->
          <img @click="refresh" class="w-6 h-6" src="~@/assets/image/otc/buy/vector_1.png" alt=""
            style="transition: all ease 0.5s" />
        </div>
        <div class="flex mt-5 box-border items-center">
          <span class="text-grey text-26 mr-5">{{ $t("限额") }}</span>
          <!-- <span class="text-black text-26 mr-35">{{currencySymbol}} {{detail.investment_min}} - {{currencySymbol}} {{detail.investment_max}}</span> -->
          <span class="c2cColor text-26 mr-9">{{ currencySymbol }} {{ detail.investment_min }} -
            {{ currencySymbol }} {{ detail.investment_max }}</span>
        </div>
        <slot name="trade"></slot>
        <div class="h-24 px-8 mt-10 flex items-center rounded-lg tips c2cTipBackground">
          <img class="w-7 h-7 mr-4" src="~@/assets/image/c2c/Group41.png" alt="" />
          <p class="text-28 textColor">
            {{ $t("保护资产安全，请提高防范意识！") }}
          </p>
        </div>
      </div>
      <div class="px-10 pb-10 mt-8 border-bottom-1px c2cColor">
        <div class="">
          <h2 class="text-28 font-normal">{{ $t("交易信息") }}</h2>
          <div class="flex justify-between mt-10">
            <span class="text-grey">{{ $t("付款时限") }}</span>
            <span>{{ detail.expire_time }} {{ $t("分钟") }}</span>
          </div>
          <div class="flex justify-between mt-10">
            <span class="text-grey">{{ $t("卖家昵称") }}</span>
            <div>
              <span class="mr-6">{{ detail.nick_name }}</span>
              <van-icon class="font-bold text-grey" name="arrow" />
            </div>
          </div>
          <div class="flex justify-between mt-10">
            <span class="text-grey">{{ $t("交易方式") }}</span>
            <div class="h-14 tabBackground rounded-md">
              <div v-if="detail.direction === 'buy'">
                <van-popover get-container="#quick" v-model="showPopover" :actions="payList" @select="onSelect"
                  placement="left-end" :theme="$store.state.home.theme" :offset="[20, 20]">
                  <template #reference>
                    <div class="flex justify-center items-center w-full h-full">
                      <div class="w-1 h-7 border-ra" style="background: #e7bb41"></div>
                      <span v-if="detail.direction === 'sell'" class="ml-3 mr-4">{{ fullMethodName }}</span>
                      <span v-else class="ml-3 mr-4">{{ methodName }}</span>
                      <img class="w-7 h-7" src="@/assets/image/c2c/Group1504.png" alt="" />
                    </div>
                  </template>
                </van-popover>
              </div>
              <div v-else>
                <div class="pl-2">
                  {{ detail.pay_type_name && detail.pay_type_name.replaceAll(',', ' | ') }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="px-8">
        <slot name="desc"></slot>
      </div>
    </div>
  </div>
</template>

<script>
import NormalHead from "@/components/Transform/normal-head/index.vue";
import { DropdownMenu, DropdownItem, Popover } from "vant";
import { mapGetters } from "vuex";
import { Icon, Divider } from "vant";
import { mapState } from "vuex";
import otcApi from "@/service/otc.api";

export default {
  name: "C2cTrade",
  props: {
    title: {},
    type: {},
    cid: {},
    bankCarNumber: {},
    detail: {
      type: Object,
      default() {
        return {};
      },
    },
  },
  data() {
    return {
      num: 0, // 刷新按钮点击
      payList: [],
      payType: "", // 支付id 买
      methodName: "", // 支付方式 买
      showPopover: false,
      fullMethodName: ''
    };

  },
  computed: {
    ...mapGetters(['theme'])
  },
  created() {
    // 支付方式
    // this.detail.pay_type.split(',').map((item, index) => {
    //   const text = this.detail.pay_type_name.split(',')[index]
    //   if (index === 0) {
    //     this.payType = item;
    //     this.methodName = text
    //   }
    //   this.payList.push({
    //     value: item,
    //     text
    //   })
    // })
    // let { reciveType } = this.$route.query
    // if (reciveType) {
    //   reciveType = JSON.parse(reciveType)
    //   this.fullMethodName = '111'
    //   console.log('reciveType', reciveType)
    // }
    if (this.detail.direction === "buy") {

      otcApi.ctcPaymentMethodPayList({ id: this.detail.id, language: this.$i18n.locale }).then((res) => {
        console.log("res", res);
        res.map((item) => {
          this.payList.push({
            value: item.uuid,
            text: item.methodName,
          });
        });
        this.payType = this.payList[0].value;
        this.methodName = this.payList[0].text;
      });

    } else {
      otcApi.ctcPaymentMethodUserPay({ id: this.detail.id, language: this.$i18n.locale }).then(res => {
        this.payList = res
        this.payList.map(item => {
          item.text = item.methodName
          item.value = item.uuid
        })
        this.payType = this.payList[0].value;
        this.methodName = this.payList[0].text;
      })
    }

    otcApi.c2cGetPayCurrencyList().then(res => {
      console.log(res);
    })

  },
  watch: {
    payType(val) {
      this.$emit("payType", val);
    },
  },
  methods: {
    handleImage(url) {
      return new URL(url, import.meta.url).href
    },
    // 刷新点击
    refresh(e) {
      this.num++;
      e.target.style.transform = `rotateZ(-${this.num * 360}deg)`;
      this.$emit("refresh");
    },
    onSelect(action) {
      this.payType = action.value;
      this.methodName = action.text;
    },
  },
  computed: {
    ...mapState("home", ["currency"]),
    ...mapGetters("c2c", ["direction", "currencySymbol"]),
    // fullMethodName() {
    //   if (this.paymentMethodName) {
    //     return this.paymentMethodName;
    //   } else {
    //     return "请选择";
    //   }
    // },
  },
  components: {
    [Icon.name]: Icon,
    [Divider.name]: Divider,
    [DropdownMenu.name]: DropdownMenu,
    [DropdownItem.name]: DropdownItem,
    [Popover.name]: Popover,
    NormalHead,
  },
};
</script>

<style lang="scss" scoped>
#C2cTradPage {
  font-size: 30px;

  .c2cTrade {
    :deep(.van-icon) {
      font-size: 16px;
    }

    :deep(.van-button) {
      border-radius: 10px;
      background: #2EBD85;
    }
  }

  .border-bottom-1px {
    position: relative;

    &:after {
      position: absolute;
      box-sizing: border-box;
      content: ' ';
      pointer-events: none;
      top: -50%;
      right: -50%;
      bottom: -50%;
      left: -50%;
      border-bottom: 1px solid #252738;
      -webkit-transform: scale(.5);
      transform: scale(.5);
    }
  }
}
</style>
