<!-- 兑换 -->
<template>
  <div id="__APP">
    <div class="css-ye7z7w">
      <div class="header css-uliqdc">
        <h1 class="main css-vurnku">
          {{ $t("message.user.dui1") }}
        </h1>
      </div>

      <div class="content css-81xrsn">
        <!-- 头部兑换 -->
        <div class="title-box-view">
          <div class="css-11y6cix">
            <el-icon class="cursor-pointer"
              ><ArrowLeftBold @click="OnClickPre"
            /></el-icon>
            <div class="css-1djsyd6">
              {{ $t("message.user.dui2") }}
            </div>
          </div>
          <el-button
            type="info"
            plain
            size="small"
            @click="goRouter('/order/exchangeOrder')"
            >{{ $t("message.user.dui3") }}</el-button
          >
        </div>

        <div class="css-19he09o">
          <div class="first-row css-vurnku">
            <!-- 卖出label -->
            <div class="label css-3j2kqe">
              <div class="css-vurnku">
                {{ $t("message.user.dui4") }}
              </div>
              <div class="css-fqmucm" v-if="walletList.length > 0">
                {{ $t("message.user.dui5") }}&nbsp;
                <div class="balance css-vurnku">
                  {{ itemChoose?.value }} &nbsp;
                </div>
                {{ itemChoose?.name }}
              </div>
            </div>
            <!-- 卖出 -->
            <div class="css-15owl46">
              <!-- 卖出输入 -->
              <div class="css-16qol6v">
                <input
                  type="number"
                  v-model="InputMoney"
                  :placeholder="$t('message.user.dui6')"
                  class="css-16fg16t"
                />
                <div class="bn-input-suffix css-vurnku">
                  <div class="css-10nf7hq">
                    <div class="css-1qwckn7" @click="OnClickMax">
                      {{ $t("message.user.dui7") }}
                    </div>
                    <!-- 输入框展示的币种 -->
                    <div class="css-tiagpy common-suffix" @click="OnclickShow">
                      <div
                        class="css-9wgib6 pl-16"
                        v-if="walletList.length > 0"
                      >
                        <img
                          :src="handleSymbolImg(itemChoose?.name)"
                          class="css-1bmz5aq"
                        />
                        {{ itemChoose?.name }}
                      </div>
                      <el-icon><CaretBottom /></el-icon>
                    </div>
                  </div>
                </div>
              </div>
              <!-- 下拉框搜索 -->
              <div class="search css-1e9tnd" v-if="optionShow">
                <div class="bn-sdd-dropdown showing css-16j7pg9">
                  <!-- 查询  -->
                  <div class="inputWrapper">
                    <el-input
                      v-model="input1"
                      class="input-with-select"
                      clearable
                      @input="handleSearch1"
                    >
                      <template #prepend>
                        <el-icon><Search /></el-icon>
                      </template>
                    </el-input>
                  </div>
                  <!-- 列表  -->
                  <ul role="listbox" class="bn-sdd-list css-2rl2kr">
                    <li
                      role="option"
                      class="bn-sdd-option-item css-88fj32"
                      v-for="(item, index) in showWallet1"
                      :key="index"
                      @click="OnclickItem(item)"
                    >
                      <div class="bn-sdd-option css-4cffwv">
                        <div class="css-1pysja1">
                          <div class="css-176wmh3">
                            <img
                              :src="handleSymbolImg(item.name)"
                              class="css-1m0agl6"
                            />
                            {{ item.name }}
                          </div>
                        </div>
                      </div>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
          <!-- 转换icon -->
          <div class="revert able css-4cffwv">
            <div class="icon css-4cffwv">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="none"
                class="css-ew2l8i"
                @click="OnClickSwap"
              >
                <path
                  d="M7 2l5.5 5.49-1.42 1.41L8 5.81V21H6V5.81L2.92 8.9 1.51 7.49 7 2zM17 22l-5.5-5.5 1.42-1.41L16 18.18V3h2v15.18l3.09-3.09 1.41 1.41L17 22z"
                  fill="#76808F"
                ></path>
              </svg>
            </div>
          </div>
          <!-- 买入 -->
          <div class="second-row css-vurnku">
            <div class="label css-4cffwv">
              <div class="css-vurnku">
                {{ $t("message.user.dui8") }}
              </div>
            </div>
            <div class="css-15owl46">
              <div class="css-16qol6v">
                <input
                  type="number"
                  :disabled="str3"
                  :placeholder="str3 || $t('message.user.dui9')"
                  class="css-16fg16t"
                />
                <div class="bn-input-suffix css-vurnku">
                  <div
                    class="css-10nf7hq css-tiagpy common-suffix"
                    @click="OnclickShow1"
                  >
                    <div class="css-9wgib6 pl-16" v-if="walletList.length > 0">
                      <img
                        :src="handleSymbolImg(itemChoose1?.name)"
                        class="css-1bmz5aq"
                      />
                      {{ itemChoose1?.name }}
                    </div>
                    <el-icon><CaretBottom /></el-icon>
                    <!-- </div> -->
                  </div>
                </div>
              </div>
              <div class="search css-1e9tnd" v-if="optionShow1">
                <div class="bn-sdd-dropdown showing css-16j7pg9">
                  <div class="inputWrapper">
                    <el-input v-model="input2" clearable @input="handleSearch2">
                      <template #prepend>
                        <el-icon><Search /></el-icon>
                      </template>
                    </el-input>
                  </div>
                  <ul role="listbox" class="bn-sdd-list css-2rl2kr">
                    <li
                      role="option"
                      id="BTC"
                      title="BTC"
                      class="bn-sdd-option-item css-88fj32"
                      v-for="(item, index) in showWallet2"
                      :key="index"
                      @click="OnclickItem1(item)"
                    >
                      <div class="bn-sdd-option css-4cffwv">
                        <div class="css-1pysja1">
                          <div class="css-176wmh3">
                            <img
                              :src="handleSymbolImg(item.name)"
                              class="css-1m0agl6"
                            />
                            {{ item.name }}
                          </div>
                        </div>
                      </div>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
          <!-- 输入金额 -->
          <!-- <button
            data-bn-type="button"
            disabled=""
            class="css-1ugffcr"
            v-if="InputMoney > 0"
          >
            {{ $t("message.user.dui10") }}
          </button> -->
          <!-- 预览  -->
          <button
            data-bn-type="button"
            class="css-1ugffcr"
            v-if="
              InputMoney >= 0 &&
              itemChoose?.name !== itemChoose1?.name &&
              !StateYulan
            "
            @click="OnClickPreview"
          >
            {{ $t("message.user.dui11") }}
          </button>
          <!-- 预览兑换结果  -->
          <div
            class="css-11h9ki5"
            v-if="StateYulan && itemChoose?.name !== itemChoose1?.name"
          >
            <div class="css-vurnku">
              <div class="css-1fxp3sh">
                <!-- 价格 -->
                <div class="css-vurnku">
                  {{ $t("message.user.dui12") }}
                </div>
                <!-- 1 USDT = 7.54671416 YTCC  -->
                <div class="css-vurnku">
                  {{ str1 }}
                </div>
              </div>
              <div class="css-u2x846">
                <!-- 反向价格 -->
                <div class="css-vurnku">
                  {{ $t("message.user.dui13") }}
                </div>
                <!-- 1 YTCC = 0.13250800 USDT -->
                <div class="css-vurnku">
                  {{ str2 }}
                </div>
              </div>
              <div class="css-195wmkv">
                <!-- 你将得到 -->
                <div class="css-1auudvs">
                  {{ $t("message.user.dui14") }}
                </div>
                <!-- 7594713.14944059 YTCC -->
                <div class="css-ypadzx">
                  {{ str3 }}
                </div>
              </div>
              <div class="css-1ufr6xa">
                <div class="css-fzyuis">
                  <!-- 该报价已过期，请重新获取报价。 -->
                </div>
              </div>
              <div class="css-dr9j81">
                <button
                  data-bn-type="button"
                  class="css-119bdia"
                  @click="OnClickBack"
                >
                  {{ $t("message.user.dui15") }}</button
                ><button
                  data-bn-type="button"
                  class="css-poesxm"
                  @click="OnClickDuiHuan"
                >
                  {{ $t("message.user.dui16") }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Axios2 from "@/api/wallet.js";
import { handleSymbolImg } from "@/utils";
import { Search, ArrowLeftBold, CaretBottom } from "@element-plus/icons-vue";
export default {
  components: { Search, ArrowLeftBold, CaretBottom },
  data() {
    return {
      options: [],
      itemChoose: {},
      optionShow: false,
      itemChoose1: {},
      optionShow1: false,
      InputMoney: null,
      StateYulan: false,
      walletList: [],
      str1: "",
      str2: "",
      str3: "", //买入的金额
      session_token: "",
      input1: "",
      input2: "",
      showWallet1: [],
      showWallet2: [],
    };
  },
  mounted() {
    let spToken = localStorage.getItem("spToken");
    this.getWalletList();
    if (spToken) {
      this.getExchangeapplyorderView();
    }
  },
  watch: {
    // 监听价格输入
    InputMoney(newInputMoney, oldInputMoney) {
      if (newInputMoney >= 10 && this.itemChoose != this.itemChoose1) {
        this.getFee();
      }
    },
  },
  methods: {
    handleSymbolImg,
    OnClickSwap() {
      let temp = this.itemChoose;
      this.itemChoose = this.itemChoose1;
      this.itemChoose1 = temp;
      this.clearSwapData();
    },
    clearSwapData() {
      this.str1 = "";
      this.str2 = "";
      this.str3 = "";
      this.InputMoney = "";
    },
    OnClickMax() {
      this.InputMoney = this.itemChoose?.value;
    },
    //获取所有币种及其价值
    getWalletList() {
      Axios2.getWalletList().then((res) => {
        var jsonArray = res;
        this.walletList = [{ name: "USDT", value: 0 }];
        for (var i in jsonArray.data) {
          if (i == "USDT") {
            this.walletList[0].value = jsonArray.data[i].money;
          } else {
            this.walletList.push({
              name: i,
              value: jsonArray.data[i].amount,
            });
          }
        }

        this.showWallet1 = this.walletList;
        this.showWallet2 = this.walletList;
        this.itemChoose = this.walletList[0];
        this.itemChoose1 = this.walletList[1];
      });
    },
    getFee() {
      const params = {
        symbol: this.itemChoose.name,
        symbol_to: this.itemChoose1.name,
        volume: this.InputMoney,
      };

      Axios2.getFee(params)
        .then((res) => {
          const { get_rate, get_volume } = res.data;
          // 1A = 多少B
          this.str1 = `1 ${this.itemChoose.name} = ${get_rate} ${this.itemChoose1.name}`;
          // 1B = 多少A
          this.str2 = `1 ${this.itemChoose1.name} = ${1 / get_rate} ${
            this.itemChoose.name
          }`;
          // 最终得到多少个B
          this.str3 = `${get_volume} ${this.itemChoose1.name}`;
        })
        .catch((params) => {
          // this.fee = 0;
          //this.money = 0;
        });
    },
    OnClickPreview() {
      this.StateYulan = true;
      this.getFee();
    },
    OnClickDuiHuan() {
      if (this.InputMoney > this.itemChoose.value) {
        this.$message.error(this.$t("message.user.jinebuzu"));
        return;
      }
      this.getTrans();
    },
    // 获取token
    getExchangeapplyorderView() {
      Axios2.getExchangeapplyorderView().then((res) => {
        this.session_token = res.data.session_token;
      });
    },
    getTrans() {
      var params = {
        symbol: this.itemChoose?.name,
        symbol_to: this.itemChoose1?.name,
        volume: this.InputMoney,
        session_token: this.session_token,
      };

      Axios2.getTrans(params).then((res) => {
        var jsonArray = res;
        if (jsonArray.code == 0) {
          this.$message.success(this.$t("message.user.tijiaochenggong"));
          // 兑换成功之后需要币种价值接口
          this.getWalletList();
        }
        this.getExchangeapplyorderView();
      });
    },
    OnClickBack() {
      this.StateYulan = false;
    },

    // 卖出币种切换
    OnclickItem(item) {
      this.itemChoose = item;
      this.StateYulan = false;
      this.optionShow = false;
      this.clearSwapData();
    },
    OnclickShow() {
      this.optionShow = !this.optionShow;
    },
    // 买入币种切换
    OnclickItem1(item) {
      this.itemChoose1 = item;
      this.optionShow1 = false;
      this.StateYulan = false;
      // 切换之后
      this.getFee();
    },
    OnclickShow1() {
      this.optionShow1 = !this.optionShow1;
    },
    OnClickPre() {
      this.$router.go(-1);
    },
    //跳转到兑换历史
    goRouter(parmas) {
      this.$router.push(parmas);
    },

    //
    handleSearch1() {
      this.showWallet1 = this.walletList.filter((it) =>
        it.name.includes(this.input1)
      );
    },
    handleSearch2() {
      this.showWallet2 = this.walletList.filter((it) =>
        it.name.includes(this.input2)
      );
    },
  },
};
</script>

<style scoped lang="css">
@import url("@/assets/css/wallet/recharge.css");
@import url("@/assets/css/wallet/exchange.css");

.diff {
  margin-bottom: 100px;
}
.pl-16 {
  padding-left: 16px;
}

.title-box-view {
  border-bottom: 1px solid #e5e5e5;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 588px;
  padding-bottom: 15px;
  margin-bottom: 10px;
}
.common-suffix {
  position: relative;
  cursor: pointer;
  border-left: 1px solid #eaecef;
}

.inputWrapper {
  padding: 8px 12px;

  width: 100%;
}

/* @import '@/assets/css/wallet/recharge.css';
@import '@/assets/css/wallet/exchange.css'; */
</style>
