<template>
  <div class="w-full">
    <!-- 表头 -->
    <div
      class="table-header flex items-center justify-between w-full font-14"
      style="color: #78808e"
      :class="{ 'opacity-6': isSwitch }"
    >
      <div class="w200">{{ $t("message.c2c.chengduishang") }}</div>
      <div class="w140 flex items-center">
        <div>{{ $t("message.c2c.jiage") }}</div>
        <div
          class="p-2 ml-4 rounded-md"
          style="background: #eff7ff; color: #1a6ebd"
        >
          {{ $t("message.c2c.congdidaogao") }}
        </div>
      </div>
      <div class="w200">{{ $t("message.c2c.xianeshuliang") }}</div>
      <div class="w140">{{ $t("message.c2c.zhifufangshi") }}</div>
      <div class="w200 flex items-center">
        <div class="mr-4">{{ $t("message.c2c.jiaoyi") }}</div>
        <div class="p-2 rounded" style="color: #4ea372; background: #ebfef2">
          {{ $t("message.c2c.tip9") }}
        </div>
      </div>
    </div>
    <!-- 数据 -->
    <div class="main w-full">
      <div class="w-full mt-4" v-for="(item, index) in data" :key="index">
        <div class="tr w-full mb-30">
          <!-- 列表页 -->
          <div v-show="!item.trade">
            <div class="flex justify-between">
              <!-- 承兑商  -->
              <div class="w200 flex flex-col">
                <div class="flex items-center mb-4">
                  <span class="mr-4 font-16" style="color: #1a6ebd">{{
                    item.nick_name
                  }}</span>
                  <img
                    class="w-4 h-4"
                    src="@/assets/images/c2c/orderSuccess/Group1160.png"
                    alt=""
                  />
                </div>
                <div class="flex items-center font-14 text-black">
                  <div>
                    {{ item.thirty_days_order }}
                    {{ $t("message.c2c.chengjiaoliang") }}
                  </div>
                  <div class="w-1 h-8 mx-4 bg-f5"></div>
                  <div>
                    {{ item.thirty_days_order_ratio }}%
                    {{ $t("message.c2c.chengjiaolv") }}
                  </div>
                </div>
              </div>
              <!-- 价格  -->
              <div class="w140 flex flex-col">
                <p>
                  <span class="mr-4 font-20">{{ item.symbol_value }}</span>
                  <span>{{ fiatCurrency.currencySymbol }}</span>
                </p>
              </div>
              <!-- 限额/数量  -->
              <div
                class="w200 flex flex-col font-14 font-500"
                style="color: #78808e"
              >
                <p class="mb-4">
                  <span class="mr-4">{{ $t("message.c2c.shuliang") }}</span>
                  <span
                    >{{ item.coin_amount
                    }}{{ fiatCurrency.currencySymbol }}</span
                  >
                </p>
                <p>
                  <span class="mr-4">{{ $t("message.c2c.xiane") }}</span>
                  <span
                    >{{ fiatCurrency.currencySymbol
                    }}{{ item.investment_min }} -
                    {{ fiatCurrency.currencySymbol
                    }}{{ item.investment_max }}</span
                  >
                </p>
              </div>
              <!-- 支付方式 -->
              <div class="w140 font-14">
                <div
                  class="mr-18 font-600 text-underline text-black"
                  v-for="(_item, _index) in item.pay_type_name.split(',')"
                  :key="_index"
                >
                  {{ _item }}
                </div>
              </div>
              <!-- 买卖按钮 -->
              <div class="w200 flex flex-col">
                <el-button
                  v-show="type === 'buy'"
                  @click="handleClick(index, 0)"
                  class="ml-0 rounded-md"
                  style="background: #62c887"
                  :class="{ disabled: item.unable }"
                  :disabled="item.unable"
                  >{{ $t("message.c2c.goumai") }}{{ exchangeCurrency.symbol }}
                </el-button>

                <el-button
                  v-show="type === 'sell'"
                  @click="handleClick(index, 1)"
                  class="rounded-md"
                  style="margin-left: 0; background: #e05461"
                  :class="{ disabled: item.unable }"
                  :disabled="item.unable"
                  >{{ $t("message.c2c.chushou") }}
                </el-button>
              </div>
            </div>
          </div>
          <!-- 购买点击之后的页面 -->
          <div
            class="flex box-border pl-8 trade-item-wrapper h480"
            v-show="item.trade"
          >
            <!-- 左边 -->
            <div class="flex-1 h-full pt-8 font-14">
              <div class="flex items-center mb-23">
                <div class="flex items-center mr-26">
                  <span class="mr-8 font-16" style="color: #1a6ebd">{{
                    item.nick_name
                  }}</span>
                  <img
                    class="w-4 h-4"
                    src="@/assets/images/c2c/want-buy/Group1160.png"
                    alt=""
                  />
                </div>
                <div class="flex items-center font-14 text-black">
                  <div>
                    {{ item.thirty_days_order }}
                    {{ $t("message.c2c.chengjiaoliang") }}
                  </div>
                  <div class="w-1 h-8 mx-12 bg-f5"></div>
                  <div>
                    {{ item.thirty_days_order_ratio }}%
                    {{ $t("message.c2c.chengjiaolv") }}
                  </div>
                </div>
              </div>
              <!-- 价格那一列 -->
              <div class="flex mb-4">
                <p class="pay-title">
                  <span class="mr-4 color-788">{{
                    $t("message.hangqing.jiage")
                  }}</span>
                  <span class="font-500 text-black"
                    >{{ item.symbol_value }}
                    {{ fiatCurrency.currencySymbol }}</span
                  >
                </p>
                <p class="pay-title">
                  <span class="mr-4 color-788">{{
                    $t("message.c2c.shuliang")
                  }}</span>
                  <span class="font-500 text-black"
                    >{{ item.coin_amount
                    }}{{ fiatCurrency.currencySymbol }}</span
                  >
                </p>
              </div>
              <!-- 支付时效那一列 -->
              <div class="flex">
                <!-- 支付时效 -->
                <p class="pay-title">
                  <span class="mr-4 color-788">{{
                    $t("message.c2c.zhifushixiao")
                  }}</span>
                  <span class="font-500 text-black"
                    >{{ detail.expire_time
                    }}{{ $t("message.c2c.fenzhong") }}</span
                  >
                </p>
                <!-- 卖家收款方式 -->
                <p class="pay-title">
                  <span class="mr-4 color-788">{{
                    $t("message.c2c.tip147")
                  }}</span>
                  <span class="font-500 text-black text-underline">
                    <span
                      style="margin-right: 10px"
                      v-for="(_item, _index) in item.pay_type_name.split(',')"
                      :key="_index"
                      >{{ _item }}</span
                    >
                  </span>
                </p>
              </div>
              <!-- 交易条款-->
              <div class="mt-10 font-16 font-500 text-black">
                {{ $t("message.c2c.jiaoyitiaokuan") }}
              </div>
              <div class="mt-4 font-16 font-500 color-788">
                {{ $t("message.c2c.tip148") }}
              </div>
            </div>
            <!-- 右边 -->
            <div class="w480 h-full pt-4 px-4 input-item box-border">
              <!-- 我要支付 -->
              <input-item
                :isNumber="true"
                :type="type"
                :dataInfo="item"
                :exchangeCurrency="exchangeCurrency"
                :PaymentMethodInfo="item.selectPay"
                @getDetail="getDetail"
                :PaymentMethodList="item.payList"
                :fiatCurrency="fiatCurrency"
                v-show="type === 'buy'"
                @cancel="cancel(index)"
              />
              <input-item
                :isNumber="true"
                :type="type"
                :dataInfo="item"
                :exchangeCurrency="exchangeCurrency"
                :PaymentMethodInfo="item.selectPay"
                @getDetail="getDetail"
                :PaymentMethodList="item.payList"
                :fiatCurrency="fiatCurrency"
                v-show="type === 'sell'"
                @cancel="cancel(index)"
              >
              </input-item>
              <!-- 支付方式 -->
              <div class="mt-8">
                <div class="font-14 color-788">
                  {{ $t("message.c2c.zhifufangshi") }}
                </div>
                <div
                  class="relative w-full h-12 border-1px rounded font-14 font-600 text-black cursor-pointer"
                  @click="handleMouseOver"
                  @mouseleave="handleMouseOut"
                >
                  <!-- 如果 没有付款方式-->
                  <div
                    @click="openUrl('/c2c/user')"
                    v-if="!item.isPay"
                    class="flex justify-center items-center w-full h-full"
                  >
                    <span class="font-14 font-500 blue">{{
                      $t("message.c2c.shezhishoukuanfangshi")
                    }}</span>
                  </div>
                  <!--有付款方式-->
                  <div
                    v-else
                    class="flex justify-center items-center w-full h-full"
                    style="position: relative; z-index: 100"
                  >
                    <div class="flex items-center w-full h-full">
                      <div class="flex-1 flex items-center">
                        <div
                          class="flex justify-center items-center w-100 h-27 ml-9 mr-12 rounded"
                          style="background: #fafafa"
                        >
                          <div
                            class="w-px h-4 mr-6 rounded-lg"
                            style="background: #e6ba41"
                          ></div>
                          <div>{{ item.selectPay.methodName }}</div>
                        </div>
                        <div>{{ item.selectPay.realName }}</div>
                      </div>
                      <img
                        class="Subtract w-4 mr-4"
                        src="@/assets/images/c2c/want-buy/Subtract.png"
                        alt=""
                      />
                    </div>
                    <!-- 选择支付方式   -->
                    <transition name="el-fade-in-linear">
                      <otc-select
                        class="absolute left-0 top-22 w-full"
                        @click="handleMouseOver"
                        v-show="item.isShow"
                        @mouseleave="handleMouseOut"
                      >
                        <div>
                          <div class="px-9">
                            <div
                              class="flex items-center mb-15 py-4 border-b-1px"
                              @click.stop="handleSelectPay(item)"
                              v-for="(item, index) in item.payList"
                              :key="index"
                            >
                              <div
                                class="flex justify-center items-center w-100 h-27 mr-12 rounded"
                                style="background: #fafafa"
                              >
                                <div
                                  class="w-px h-4 mr-6 rounded-lg"
                                  style="background: #e6ba41"
                                ></div>
                                <div>{{ item.methodName }}</div>
                              </div>
                              <div>{{ item.realName }}</div>
                            </div>
                          </div>
                          <div
                            @click="openUrl('/c2c/user')"
                            class="flex items-center pl-8 py-4"
                          >
                            <img
                              class="w-4 h-4"
                              src="@/assets/images/c2c/want-buy/Vector(1).png"
                              alt=""
                            />
                            <span class="ml-4 font-14 font-400 text-black">{{
                              $t("message.user.tianjiashoukuanfangshi")
                            }}</span>
                          </div>
                        </div>
                      </otc-select>
                    </transition>
                  </div>
                </div>
              </div>
              <!-- </template> -->
            </div>
          </div>
        </div>
        <el-divider class="bg-f5" />
      </div>
    </div>
  </div>
</template>

<script>
import OtcSelect from "./OtcSelect.vue";
import InputItem from "./InputItem.vue";
import Axios from "@/api/c2c.js";
import { getParamsLang } from "@/utils/index";

export default {
  name: "WantBuyTabel",
  props: ["type", "list", "fiatCurrency", "payList", "exchangeCurrency"],
  data() {
    return {
      isSwitch: false, //在table下展开买卖详情
      showPayment: false,
      data: [], //承兑商列表
      dataInfo: {}, //选中了某一个承兑商
      PaymentMethodList: [],

      detail: {},
    };
  },
  watch: {
    list(val) {
      this.data = val.map((item) => {
        item = {
          ...item,
          isPay: false, //默认是没有支付方式的
          isShow: false,
          payList: [],
          selectPay: {},
        };

        return item;
      });
    },
  },
  methods: {
    getParamsLang,
    // 购买/出售 点击
    handleClick(index, val) {
      this.dataInfo = this.data[index]; //当前点击的承兑商数据
      this.data[index].trade = true;
      this.isSwitch = true;
      if (val == 0) {
        // 购买
        this.getPayList();
      } else {
        // 出售
        this.getMyPayList();
      }
    },
    getDetail(val) {
      this.detail = val;
    },
    cancel(index) {
      this.data[index].trade = false;
      this.isSwitch = false;
    },
    handleMouseOver() {
      this.dataInfo.isShow = true;
    },
    handleMouseOut() {
      this.dataInfo.isShow = false;
    },
    handleSelectPay(item) {
      this.dataInfo.selectPay = item;
      this.dataInfo.isShow = false;
    },
    // 获取当前承兑商的支付方式
    getPayList() {
      Axios.ctcPaymentMethodPayList({
        id: this.dataInfo.id,
        language: this.getParamsLang(),
      }).then((res) => {
        if (res.code == "0") {
          let payArry = this.dataInfo.pay_type_name.split(",");
          payArry.map((item) => {
            res.data.map((item1) => {
              if (item == item1.methodName) {
                //有配置对应支付方式
                this.dataInfo?.payList.push(item1);
              }
            });
          });
          console.log(
            "获取当前承兑商的支付方式",
            this.dataInfo,
            this.dataInfo.payList
          );
          if (this.dataInfo.payList.length > 0) {
            this.dataInfo.selectPay = this.dataInfo.payList[0];
            this.dataInfo.isPay = true;
          } else {
            this.dataInfo.isPay = false;
          }
        }
      });
    },
    getMyPayList() {
      let language = this.getParamsLang();
      this.PaymentMethodList = [];
      Axios.userC2cPaymentMethodList({ language, type: 2 }).then((res) => {
        if (res.code == "0") {
          let payArry = this.dataInfo.pay_type_name.split(",");
          payArry.map((item) => {
            res.data.map((item1) => {
              if (item == item1.methodName) {
                //有配置对应支付方式
                this.dataInfo.payList.push(item1);
              }
            });
          });
          if (this.dataInfo.payList.length > 0) {
            this.dataInfo.selectPay = this.dataInfo.payList[0];
            this.dataInfo.isPay = true;
          } else {
            this.dataInfo.isPay = false;
          }
        }
      });
    },
    openUrl(val) {
      this.$router.push(val);
    },
  },
  components: {
    OtcSelect,
    InputItem,
  },
};
</script>

<style lang="scss" scoped>
.w480 {
  width: 480px;
}

.w200 {
  width: 200px;
}
.w140 {
  width: 140px;
}
.h480 {
  height: 480px;
}

.top-22 {
  top: 50px;
}
.text-underline {
  text-decoration: underline;
}

.blue {
  color: #1a6ebd;
}

.border-1px {
  border: 1px solid #e9ebf1;
}

.border-b-1px {
  border-bottom: 1px solid #e9ebf1;
}

:deep {
  .el-button {
    width: 136px;
    border: none;
    padding: 9px 32px;
    font-weight: 600;
    font-size: 16px;
    color: #fff;
  }

  .el-divider {
    background: #f5f5f5;
  }

  .disabled {
    background: #eaecef !important;
    color: #b8bdc5;
  }
}

.trade-item-wrapper {
  background: #ffffff;
  box-shadow: 0px 0px 12px rgba(0, 0, 0, 0.15);
}

.color-788 {
  color: #78808e;
}

.opacity-6 {
  opacity: 0.6;
}

.input-item {
  border-left: 1px solid #eaecef;
}
.pay-title {
  width: 300px;
}
</style>
