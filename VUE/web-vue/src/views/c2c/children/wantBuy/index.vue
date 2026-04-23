<template>
  <layout>
    <template v-slot:menu>
      <!-- 菜单切换 -->
      <div class="banner-bottom">
        <otc-order-header />
      </div>
    </template>
    <template v-slot:content>
      <div>
        <!--选择 出售/购买/币种 -->
        <div class="nav">
          <div class="center-w1200">
            <div class="flex items-center h-60">
              <div
                class="flex items-center justify-center w-140 h-35 mr-33 font-14 border-1px rounded-sm"
              >
                <div
                  class="flex items-center justify-center w-66 h-29 rounded-sm cursor-pointer"
                  :class="{ 'buy-active': type === 'buy' }"
                  @click="navTab('buy')"
                >
                  {{ $t("message.c2c.goumai") }}
                </div>
                <div
                  class="flex items-center justify-center w-66 h-29 rounded-sm cursor-pointer"
                  :class="{ 'sell-active': type === 'sell' }"
                  @click="navTab('sell')"
                >
                  {{ $t("message.c2c.chushou") }}
                </div>
              </div>
              <div class="flex h-full font-14">
                <div
                  class="flex items-center h-full px-5 box-border mr-38 cursor-pointer"
                  v-for="(item, index) in list"
                  :key="index"
                  :class="{ 'nav-active': listIndex === index }"
                  @click="navTbaIndex(index)"
                >
                  {{ item.symbol }}
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- 操作核心区 -->
        <div class="center-w1200 pt-25">
          <!-- 输入金额，选择币种，选择支付方式 -->
          <div class="flex justify-between items-end">
            <div>
              <div class="flex font-14" style="color: #78808e">
                <!-- 输入金额 -->
                <div class="amount font-14">
                  <div class="mb-8">{{ $t("message.jiaoyi.jine") }}</div>
                  <div class="relative w-260 h-46">
                    <otc-input
                      @focus="showAmount = true"
                      @blur="amountBlur"
                      :inputValue="activeAmountValue"
                      :placeholder="$t('message.c2c.tip143')"
                    >
                      <template #right>
                        <div class="flex items-center font-14">
                          <span style="color: #babfc6">{{
                            fiatCurrency.currency_symbol
                          }}</span>
                          <div
                            class="w-1 h-24 ml-14 mr-17"
                            style="background: #eaecef"
                          ></div>
                          <div
                            class="cursor-pointer"
                            @click="searchFun"
                            style="color: #1a6ebd"
                          >
                            {{ $t("message.c2c.sousuo") }}
                          </div>
                        </div>
                      </template>
                    </otc-input>
                    <transition name="el-fade-in-linear">
                      <otc-select
                        v-show="showAmount"
                        @itemSelect="selectAmount"
                        class="w-full px-12 py-10 box-border jineBox"
                        :list="amount"
                        v-model="activeAmount"
                      />
                    </transition>
                  </div>
                </div>
                <!-- 法币 -->
                <div class="mx-20 font-14 text-black">
                  <div class="mb-8">{{ $t("message.c2c.fabi") }}</div>
                  <fiat-currency
                    :isTwo="false"
                    :currencyType="2"
                    :isbg="true"
                    @selectItem="selectItem"
                  />
                </div>
                <!-- 支付方式 -->
                <div class="mx-20 font-14 text-black">
                  <div class="mb-8">{{ $t("message.c2c.zhifufangshi") }}</div>
                  <payment-method
                    @selectPayItem="selectPayItem"
                    :payList="payList"
                  />
                </div>
              </div>
            </div>
            <!-- 刷新设置 -->
            <div class="relative z-10 refresh">
              <div
                class="flex items-center justify-center refresh-btn h-46 font-16 font-600 border-1px rounded-md cursor-pointer bg-white"
                :class="{ 'bg-f5': refreshShow }"
                @click.stop="handleSetRefresh"
              >
                <div v-if="refreshActive === ''" class="flex items-center">
                  <img
                    class="w-24 h-24 mr-11"
                    src="@/assets/images/c2c/want-buy/Group2021.png"
                    alt=""
                  />
                  <span>{{ $t("message.c2c.shuaxinshezhi") }}</span>
                </div>
                <div
                  class="flex items-center justify-center"
                  v-else
                  @click.stop="handlePause"
                >
                  <img
                    class="w-24 h-24 mr-11"
                    src="@/assets/images/c2c/want-buy/zanting.png"
                    alt=""
                  />
                  <span>{{ $t("message.c2c.zanting") }}</span>
                </div>
              </div>
              <!-- 刷新的选项 -->
              <transition name="el-fade-in-linear">
                <otc-select
                  v-show="refreshShow"
                  @itemSelect="selectRefresh"
                  class="absolute top-46 left-0"
                  :list="refreshOptionList"
                  :value="refreshActive"
                />
              </transition>
            </div>
          </div>

          <el-divider class="w-full my-39 mb-34 bg-f5" />
          <!-- 承兑商表格-->
          <want-buy-table
            :type="type"
            :exchangeCurrency="list[listIndex]"
            :fiatCurrency="fiatCurrency"
            :payList="payList"
            :list="dataList"
          />
          <!--分页-->
          <Pagination
            :noPre="noPre"
            :noNext="noNext"
            :pageNum="page_no"
            @changePageNum="changePageNum"
          />
        </div>
      </div>
    </template>
  </layout>
</template>

<script>
import OtcOrderHeader from "@/views/c2c/components/otc-order-header/index.vue";
import OtcInput from "@/views/c2c/components/OtcInput.vue";
import Axios from "@/api/c2c.js";
import Pagination from "@/components/common/pagination.vue";
import FiatCurrency from "./components/fiatCurrency.vue";
import PaymentMethod from "./components/PaymentMethod.vue";
import WantBuyTable from "./components/WantBuyTable.vue";
import OtcSelect from "./components/OtcSelect.vue";

import Layout from "@/views/c2c/layout.vue";
import { getParamsLang } from "@/utils/index";

export default {
  name: "wantBuy",
  data() {
    return {
      activeNames: [],
      fiatCurrency: {}, // 法币
      showAmount: false, // 显示金额选择
      type: "buy",
      list: [],
      listIndex: 0,
      amount: [
        { title: "100", value: 100 },
        { title: "1000", value: 1000 },
        { title: "5000", value: 5000 },
        { title: "1" + this.$t("message.c2c.wan"), value: 10000 },
        { title: "10" + this.$t("message.c2c.wan"), value: 100000 },
        { title: "20" + this.$t("message.c2c.wan"), value: 200000 },
      ],
      activeAmount: "",
      refreshOptionList: [
        { title: this.$t("message.c2c.zanbuchuli"), value: 0 },
        { title: this.$t("message.c2c.tip144"), value: 5 },
        { title: this.$t("message.c2c.tip145"), value: 10 },
        { title: this.$t("message.c2c.tip146"), value: 20 },
      ],
      refreshActive: "",
      refreshShow: false,
      dataList: [], //承兑商订单列表
      page_no: 1,
      payList: [], //支持的支付方式
      payInfo: [],
      method_type: -1,
      noNext: false,
      noPre: false,
      totalPageNum: 0,
      timer: null,
      activeAmountValue: "",
      allList: [],
      buyBi: true,
      timeOutTimer1:null
    };
  },
  mounted() {
    this.getSymbolList();
    this.getPayList();
  },

  unmounted() {
    clearInterval(this.timer);
    this.timer = null
    
    if(this.timeOutTimer1){
      clearTimeout(this.timeOutTimer1)
      this.timeOutTimer1 = null
    }
  },
  methods: {
    getParamsLang,
    //搜索
    searchFun() {
      if (this.activeAmountValue != "") {
        this.dataList = this.allList.filter((item) => {
          return (
            +this.activeAmountValue >= item.investment_min &&
            item.investment_max >= +this.activeAmountValue
          );
        });
      } else {
        this.dataList = this.allList;
      }
    },
    selectAmount(val) {
      this.activeAmountValue = val.value;
    },
    // ？？？
    amountBlur() {
      this.timeOutTimer1 = setTimeout(() => {
        this.showAmount = false;
      }, 200);
    },
    // 刷新设置
    handleSetRefresh() {
      this.refreshShow = !this.refreshShow;
    },
    // 选中了几秒刷新
    selectRefresh(val) {
      this.refreshShow = false;
      this.refreshActive = val;
      clearInterval(this.timer);
      if (val.value != 0) {
        this.timer = setInterval(() => {
          this.getAdvertList();
        }, val.value * 1000);
      }
    },
    getSymbolList() {
      Axios.c2cSymbolList().then((res) => {
        if (res.code == "0") {
          for (const key in res.data) {
            let obj = {
              symbol: res.data[key],
              value: key,
            };
            this.list.push(obj);
          }
          this.getAdvertList();
        }
      });
    },
    navTab(val) {
      this.type = val;
      this.getAdvertList();
    },
    navTbaIndex(val) {
      this.listIndex = val;
      this.getAdvertList();
    },
    //分页
    changePageNum(type) {
      if (type == "next") {
        console.log(this.page_no, this.totalPageNum);
        if (this.page_no > this.totalPageNum) {
          return;
        }
        if (!this.noNext) {
          this.page_no = this.page_no + 1;
        }
      } else {
        if (!this.noPre && this.page_no > 1) {
          this.page_no = this.page_no - 1;
        }
      }
    },
    // 购买币种时， 获取承兑商列表
    getAdvertList() {
      let language = this.getParamsLang();
      let obj = {
        page_no: this.page_no,
        direction: this.type,
        currency: this.fiatCurrency.title,
        symbol: this.list.length > 0 ? this.list[this.listIndex].value : "usdt",
        language,
      };
      if (this.method_type != -1) {
        obj.method_type = this.method_type;
      }
      Axios.c2cAdvertList(obj).then((res) => {
        if (res.code == "0") {
          this.allList = res.data;
          this.allList.map((item) => {
            item.trade = false;
            return item;
          });
          this.dataList = res.data;
          this.dataList.map((item) => {
            item.trade = false;
            return item;
          });
          this.searchFun();
          this.totalPageNum = parseInt(res.data.length / 10);
          const total = Math.ceil(res.data.length / 10);
          this.noNext = this.pageNum > total || this.pageNum == total;
        }
      });
    },
    //获取支付方式
    getPayList() {
      let language = this.getParamsLang();
      let obj = {
        language,
      };

      Axios.c2cPaymentMethodList(obj).then((res) => {
        if (res.code == "0") {
          for (const key in res.data) {
            let jsonData = {
              title: res.data[key],
              value: key,
            };
            this.payList.push(jsonData);
          }
        }
      });
    },
    selectItem(val) {
      console.log(val);
      this.page_no = 1;
      this.fiatCurrency = val;
      this.getAdvertList();
    },
    //  选择支付方式
    selectPayItem(val) {
      this.page_no = 1;
      this.method_type = val.value;
      this.payInfo = val;
      this.getAdvertList();
    },
    openUrl(val) {
      this.$router.push(val);
    },
    // 点击暂停
    handlePause() {
      this.refreshActive = "";
      clearInterval(this.timer);
    },
  },
  watch: {
    refreshActive() {
      this.refreshShow = false;
    },
  },
  components: {
    OtcSelect,
    OtcInput,
    FiatCurrency,
    PaymentMethod,
    WantBuyTable,
    OtcOrderHeader,
    Pagination,
    Layout,
  },
};
</script>

<style lang="scss" scoped>
@import "@/assets/css/c2c/init.scss";
// menu的样式
.banner-bottom {
  width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 20;
}

.introduce {
  border-radius: 8px;
  box-shadow: 0px 0px 4px rgba(0, 0, 0, 0.2);
}

.buy-active {
  background: #62c887;
  color: #fff;
}

.sell-active {
  background: #e05461;
  color: #fff;
}

.border-1px {
  border: 1px solid #e9ebf1;
}

.nav {
  background: #ffffff;
  box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.1);
}

.nav-active {
  position: relative;
  color: $blue;

  &:after {
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    content: "";
    border-bottom: 2px solid $blue;
  }
}

:deep {
  .amount {
    .active {
      border: 1px solid $blue !important;
      background: $blue !important;
      color: $white !important;
    }

    .otc-select-wrapper {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 10px;

      .item {
        justify-content: center;
        height: 37px;
        font-size: 12px;
        color: #000;
        border-radius: 4px;
        border: 1px solid #e6e8ea;
        box-sizing: border-box;
      }
    }
  }

  .refresh {
    .otc-select-wrapper {
      padding: 13px 0;
      .item {
        line-height: 41px;
        font-size: 14px;
        padding: {
          top: 0;
          right: 18px;
          left: 18px;
        }

        &:last-child {
          padding: {
            bottom: 0;
          }
        }
      }
    }
  }

  .el-pager {
    li.active {
      border-radius: 6px;
      background: #eaecef;
      color: #171a1e;
    }

    li {
      &:hover {
        color: #409eff;
      }
    }
  }

  .el-collapse-item__header {
    height: 114px;
    line-height: 114px;
    font-size: 24px;
    font-weight: 700;
  }

  .el-collapse-item__header {
    font-weight: 700;
  }
}

.center-w1200 {
  width: 1200px;
  margin: 0 auto;
}

.jineBox {
  position: relative;
  z-index: 100;
  background: #fff;
}

.refresh-btn {
  width: 250px;
}

.introduce-title {
  width: 300px;
  word-break: break-all;
  word-wrap: break-word;
}

.want-img {
  display: block;
  margin: 0 20px;
}
</style>
