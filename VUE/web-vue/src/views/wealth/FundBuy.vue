<template>
  <div>
    <div class="css-1gtg3yn" v-show="panelShow">
      <div class="css-81pxnt"></div>
      <div class="css-n8r1kl">
        <div class="css-1e1f1e0">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            class="css-1iztezc"
          >
            <path
              d="M19.003 6.42l-1.41-1.41-5.59 5.59-5.59-5.59-1.41 1.41 5.59 5.59-5.59 5.59 1.41 1.41 5.59-5.59 5.59 5.59 1.41-1.41-5.59-5.59 5.59-5.59z"
              fill="currentColor"
            ></path>
          </svg>
        </div>
        <div class="style-dialog-body css-1d5e5am">
          <div class="css-xd39qf">
            <!-- 左边 -->
            <div class="css-11xizhn">
              <div class="css-5jwml3">
                {{ this.produceDetail?.[this.getWealthLangToName()] }}

                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 16 16"
                  fill="none"
                  class="css-wl5ai2"
                >
                  <path
                    fill-rule="evenodd"
                    clip-rule="evenodd"
                    d="M14 12.676l-1.324 1.316-4.683-4.675L3.324 14l-1.316-1.324L6.676 8 2 3.324l1.324-1.317 4.669 4.676L12.676 2l1.31 1.324L9.315 8 14 12.676z"
                    fill="currentColor"
                  ></path>
                </svg>
              </div>
              <div class="css-bpq0bb">
                <div class="css-3j2kqe">
                  <div class="css-1tmeg42">
                    <!-- <img :src="produceDetail.img" class="css-19ptuc5" /> -->
                    <el-image
                      :src="`${ConfigURL.HOST_URL}/symbol/${buyCurrency}.png`"
                      class="el-img-style"
                    >
                    <template #error>
                      <div class="image-slot">
                        <img
                          :src="`${ConfigURL.HOST_URL}/symbol/noCoins.png`"
                          class="el-img-style"
                          width="26"
                          height="26"
                        />
                      </div>
                    </template>
                    </el-image>
                    <div class="css-1yxyz72">
                      {{ buyCurrency.toUpperCase() }}
                    </div>
                  </div>
                </div>
                <div class="css-41qqv0">
                  {{ $t("message.user.leixing") }}
                </div>
                <div class="css-ss1w4e">
                  <button data-bn-type="button" class="css-1nks7iw">
                    {{ $t("message.user.dingqi") }}
                  </button>
                </div>
                <div class="css-1tt3bcp">
                  {{ $t("message.user.qixian") }}
                </div>
                <div class="css-18wlaly">
                  <div class="css-1an9uq5">
                    <button
                      data-bn-type="button"
                      class="css-1abocc3"
                      v-if="produceDetail.cycle != 0"
                    >
                      {{ produceDetail.cycle }} {{ $t("message.user.tian") }}
                    </button>
                    <button data-bn-type="button" class="css-1abocc3" v-else>
                      {{ $t("message.user.wuxianqi") }}
                    </button>
                  </div>
                </div>
                <div class="css-1npm23m">
                  <div class="css-381z8q">
                    <span class="css-locd1j"
                      >{{ $t("message.user.keyong") }}
                      {{ usdtBalance }}
                      {{ buyCurrency.toUpperCase() }}
                    </span>
                    <div class="css-1gdyhto">
                      <input
                        :disabled="isDisabled"
                        :aria-label="$t('message.user.tuoguanjine')"
                        :placeholder="$t('message.user.qsr_jine')"
                        class="css-16fg16t"
                        v-model="m_money"
                      />
                      <div class="bn-input-suffix css-vurnku">
                        <div class="css-10nf7hq">
                          {{ buyCurrency.toUpperCase() }}
                          <div class="css-xk20s"></div>
                          <button
                            data-bn-type="button"
                            class="css-joha13"
                            @click="OnClickMax"
                          >
                            {{ $t("message.user.zuida") }}
                          </button>
                        </div>
                      </div>
                      <label class="bn-input-label css-5vzups">{{
                        $t("message.user.tuoguanjine")
                      }}</label>
                    </div>
                    <span class="css-87q6r1"
                      >{{ $t("message.user.keyong") }} {{ usdtBalance }}
                      {{ buyCurrency.toUpperCase() }}</span
                    >
                  </div>
                </div>
                <div class="css-stzlkh">
                  <div class="css-1y7zpd2">
                    {{ $t("message.user.tuoguanshuliangxianzhi") }}
                  </div>
                  <div class="css-1qa1a31">
                    <div class="css-4cffwv">
                      <div class="css-1v4hdqt">
                        {{ $t("message.user.zuishaoketou") }}:
                      </div>
                      <div class="css-4cffwv">
                        {{ produceDetail.investment_min }}
                      </div>
                    </div>
                    <div class="css-17v62m6">
                      <div class="css-1v4hdqt">
                        {{ $t("message.user.zuidaketou") }}:
                      </div>
                      <div class="css-4cffwv">
                        {{ produceDetail.investment_max }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- 右边 -->
            <div class="css-bqom4i">
              <div class="css-1pvy411">{{ $t("message.user.gaikuang") }}</div>
              <!-- 购买日、 起息日 -->
              <div class="css-hx74sz">
                <div class="css-4cffwv">
                  <div class="css-uliqdc">
                    <div class="css-qzgl6x"></div>
                    <div class="css-j5oru9">
                      <div class="css-1o3bjch"></div>
                      <div class="css-1qzy4yi"></div>
                    </div>
                  </div>
                  <div class="css-chrzr4">
                    <span class="key">{{ $t("message.user.goumairi") }}</span
                    ><span class="value" style="direction: ltr">{{
                      produceDetailOpen.create_time
                        ? produceDetailOpen.create_time.split(" ")[0]
                        : "--"
                    }}</span>
                  </div>
                </div>

                <div class="css-4cffwv">
                  <div class="css-uliqdc"><div class="css-qzgl6x"></div></div>
                  <div class="css-chrzr4">
                    <span class="key">{{ $t("message.user.qixiri") }}</span
                    ><span class="value" style="direction: ltr">{{
                      produceDetailOpen.earn_time
                        ? produceDetailOpen.earn_time
                        : "--"
                    }}</span>
                  </div>
                </div>
              </div>
              <div class="css-io94ep">
                <div class="css-4cffwv">
                  <div class="css-uliqdc">
                    <div class="css-1158piw">
                      <div class="css-7pg4mr"></div>
                      <div class="css-8ui98j"></div>
                    </div>
                    <div class="css-qzgl6x"></div>
                  </div>
                  <div class="css-et79qm">
                    <div class="css-dqihcg">
                      <span class="key">{{
                        $t("message.user.paixishijian")
                      }}</span
                      ><span class="value" style="direction: ltr">{{
                        $t("message.user.meitian")
                      }}</span>
                    </div>
                    <div class="css-1ei23rj">
                      <span class="key">{{
                        $t("message.user.jixijieshuri")
                      }}</span
                      ><span class="value" style="direction: ltr">{{
                        produceDetailOpen.stop_time
                      }}</span>
                    </div>
                  </div>
                </div>
                <div class="css-4cffwv">
                  <div class="css-uliqdc">
                    <div class="css-j5oru9">
                      <div class="css-7pg4mr"></div>
                      <div class="css-8ui98j"></div>
                    </div>
                    <div class="css-qzgl6x"></div>
                  </div>
                  <div class="css-1dl7maj">
                    <span class="key">{{
                      $t("message.user.tiqianshuhui")
                    }}</span
                    ><span class="value"
                      >{{ produceDetail.default_ratio }}%</span
                    >
                  </div>
                </div>

                <div class="css-tl5rnx"></div>
                <div class="css-gwlf4x">
                  <div class="key css-10nf7hq">
                    {{ $t("message.user.yujishouyi") }}
                  </div>
                  <div class="value css-4cffwv">
                    <div class="css-1bshycy">
                      {{ expectedProfit }}
                    </div>
                  </div>
                </div>
              </div>

              <div class="css-c8afl8">
                <button
                  data-bn-type="button"
                  id="pos-confirm"
                  type="primary"
                  class="css-ulq18s"
                  @click="OnClickNext"
                >
                  {{ $t("message.user.queding") }}
                </button>
              </div>
            </div>
          </div>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 16 16"
            fill="none"
            class="css-vrya1v"
            @click="closeBtn"
          >
            <path
              fill-rule="evenodd"
              clip-rule="evenodd"
              d="M14 12.676l-1.324 1.316-4.683-4.675L3.324 14l-1.316-1.324L6.676 8 2 3.324l1.324-1.317 4.669 4.676L12.676 2l1.31 1.324L9.315 8 14 12.676z"
              fill="currentColor"
            ></path>
          </svg>
        </div>
      </div>
    </div>
    <!-- 确定认购页 -->
    <el-dialog
      :title="$t('message.user.quedingrengou')"
      v-model="showNext"
      :close-on-click-modal="false"
      width="40%"
      center
    >
      <div class="flex-space">
        <div class="subscribe-text">
          <p>
            {{ $t("message.user.suocangjine") }}({{
              buyCurrency.toUpperCase()
            }})
          </p>
          <p>{{ $t("message.user.yujishouyi") }}</p>
          <p>{{ $t("message.user.tuoguanshijian") }}</p>
          <p>{{ $t("message.user.rishouyi") }}</p>
          <p>{{ $t("message.user.qixishijian") }}</p>
          <p>{{ $t("message.user.daoqishijian") }}</p>
          <p>{{ $t("message.user.dingdanbianhao") }}</p>
          <p>{{ $t("message.user.dingdanshijian") }}</p>
        </div>
        <div class="subscribe-text">
          <p>{{ produceDetailOpen.amount }}</p>
          <p>{{ produceDetailOpen.profit_may }}</p>
          <p>{{ produceDetailOpen.cycle }} {{ $t("message.user.tian") }}</p>
          <p>{{ produceDetailOpen.daily_rate }} %</p>
          <p>{{ produceDetailOpen.earn_time }}</p>
          <p>{{ produceDetailOpen.stop_time }}</p>
          <p>{{ produceDetailOpen.order_no }}</p>
          <p>{{ produceDetailOpen.create_time }}</p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button class="fundBuy-buy-button" @click="confirmSubscribe">{{
            $t("message.user.quedingrengou")
          }}</el-button>
        </span>
      </template>
    </el-dialog>
    <!-- 购买成功提示 -->
    <el-dialog
      title=""
      v-model="successDialog"
      :close-on-click-modal="false"
      width="40%"
      @close="goBack"
      center
    >
      <div class="text-center">
        <img
          src="../../assets/myImages/sucess-yellow.png"
          width="100px"
          height="100px"
        />
        <p style="font-size: 20px">
          <b>{{ $t("message.user.goumaichenggong") }}</b>
        </p>
        <p class="margin-top-bottom20">
          {{ $t("message.user.jiriqikehuodeshouyifencheng") }}
        </p>
        <el-button class="fundBuy-buy-button" @click="checkOrder">{{
          $t("message.user.chakandingdan")
        }}</el-button>
        <div class="mouse-cursor" @click="goBack" style="margin-top: 20px">
          {{ $t("message.user.fanhuijiaoyi") }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import Axios from "@/utils/http";
import wealthAxios from "@/api/wealth.js";
import ConfigURL from "@/config/index";
import { dataTime } from "@/utils/utils";
import { getWealthLangToName } from "@/utils";
export default {
  props: {
    iproduceId: {
      type: String,
      default: "",
    },
    type: {
      type: String,
      default: "financial",
    },
    buyCurrency: {
      type: String,
      default: "USDT",
    },
  },
  data() {
    return {
      usdtBalance: "", //理财的可用余额
      ethBal: "", //矿机的可用余额
      showNext: false,
      //输入框的值默认必须为0，不能为空，否则调用认购接口会报错。
      m_money: 0,
      produceId: this.iproduceId,
      typeName: this.type, //理财用："financial"，矿池用："mine"
      produceDetail: {
        today_rate: 0,
      },
      produceDetailOpen: {},
      panelShow: false,

      isDisabled: false,
      successDialog: false,
      ConfigURL,
    };
  },
  watch: {
    iproduceId: {
      handler: function (newValue) {
        this.produceId = newValue;
        this.getProduceDetail(this.produceId);
        this.getSubscribe(this.produceId, this.m_money);
      },
      deep: true,
    },
  },
  // 预计收益,cycle=0 即无限期时，直接显示‘--’
  computed: {
    expectedProfit() {
      if (this.produceDetailOpen.cycle != 0) {
        if (this.typeName == "financial") {
          return (
            (this.m_money *
              this.produceDetailOpen.daily_rate_max *
              this.produceDetailOpen.cycle) /
            100
          ).toFixed(2);
        } else {
          return (
            (this.m_money *
              this.produceDetailOpen.daily_rate *
              this.produceDetailOpen.cycle) /
            100
          ).toFixed(2);
        }
      } else {
        return "--";
      }
    },
  },
  methods: {
    getWealthLangToName,
    dataTime,
    show() {
      this.m_money = 0;
      this.panelShow = true;
      this.getUsdt();
    },
    closeBtn() {
      this.panelShow = false;
    },
    //获取理财详情
    getProduceDetail(produceId) {
      let apiUrl = "";
      if (this.typeName == "mine") {
        apiUrl = "api/miner!get.action";
      } else {
        apiUrl = "api/finance!get.action";
      }

      Axios.fetch(apiUrl, { id: produceId }).then((res) => {
        this.produceDetail = res.data;
        // 由于理财和矿机接口返回内容不一样 所以做处理
        if(this.typeName == 'financial'){
          this.produceDetail.investment_max = this.produceDetail.investmentMax;
          this.produceDetail.investment_min = this.produceDetail.investmentMin;
        }
        this.isDisabled = this.produceDetail.investment_min == 0;
        this.produceDetail.name = res.data?.[this.getWealthLangToName()];
      });
    },
    //获取可用余额USDT
    getUsdt() {
      wealthAxios
        .getWallet()
        .then((res) => {
          this.usdtBalance = res.data.usdt ? res.data.usdt : 0;
          // let walletList = res.data.extends;
          // let usdtObj = walletList.find((item) => {
          //   return item.symbol.toLowerCase() == this.buyCurrency.toLowerCase();
          // });
          // this.usdtBalance = usdtObj.usable;
          // let ethObj = walletList.find((item) => {
          //   return item.symbol.toLowerCase() == this.buyCurrency.toLowerCase();
          // });
          // this.ethBal = ethObj.usable;
        })
        .catch((err) => {
          console.log(err);
        });
    },
    //点击最大按钮逻辑
    OnClickMax() {
      //可用余额大于最大可投金额时，托管金额= 最大可投金额，否则托管金额=可用余额
      // let money = this.typeName == "mine" ? this.ethBal * 1 : this.usdtBalance * 1;

      this.m_money =
        this.usdtBalance > this.produceDetail.investment_max
          ? this.produceDetail.investment_max
          : this.usdtBalance;
    },
    //打开确定认购弹窗
    OnClickNext() {
      //必须是最小投金额大于0，因矿机存上体验矿机的情况，金额默认为0
      if (!this.m_money && this.produceDetail.investment_min > 0) {
        this.$message.warning(this.$t("message.home.qingshurujine"));
        return;
      }

      if (this.m_money < this.produceDetail.investment_min) {
        this.$message.warning(
          this.$t("message.home.zuishaotou") + this.produceDetail.investment_min
        );
        return;
      }

      if (this.m_money > this.produceDetail.investment_max) {
        this.$message.warning(
          this.$t("message.home.zuidatou") + this.produceDetail.investment_max
        );
        return;
      }

      this.showNext = true;
      this.getSubscribe();
    },
    //获取认购详情
    getSubscribe() {
      let apiUrl = "";
      let submitData = {};
      if (this.typeName == "mine") {
        apiUrl = "api/minerOrder!getOpen.action";
        submitData = {
          minerId: this.produceId,
          amount: this.m_money,
        };
      } else {
        apiUrl = "api/financeOrder!getOpen.action";
        submitData = {
          financeId: this.produceId,
          amount: this.m_money,
        };
      }

      Axios.fetch(apiUrl, submitData).then((res) => {
        this.produceDetailOpen = res.data || {}; //防止引用出错
        // if (res.code !== 0) {
        //   this.showNext = false;
        // }
        // 如果出现金额错误,就不会出现这个弹窗
      });
    },
    //确定认购
    confirmSubscribe() {
      let apiUrl = "";
      let submitData = {};
      if (this.typeName == "mine") {
        apiUrl = "api/minerOrder!open.action";
        submitData = {
          minerId: this.produceDetailOpen.minerId,
          amount: this.produceDetailOpen.amount,
          session_token: this.produceDetailOpen.session_token,
        };
      } else {
        apiUrl = "api/financeOrder!open.action";
        submitData = {
          financeId: this.produceDetailOpen.financeId,
          amount: this.produceDetailOpen.amount,
          session_token: this.produceDetailOpen.session_token,
        };
      }

      Axios.fetch(apiUrl, submitData).then((res) => {
        if (res.code == 0) {
          this.showNext = false;
          this.panelShow = false;
          this.successDialog = true;
        }
      });
    },
    //查看订单
    checkOrder() {
      this.$router.push({
        path: "/wallet/financialAccounts",
        query: {
          type: this.typeName == "mine" ? "1" : "0",
        },
      });
    },
    //返回交易
    goBack() {
      this.successDialog = false;
      this.$parent.getIncome(); // 刷新数据
    },
  },
};
</script>
<style lang="css" scoped>
@import url("@/assets/css/wealth/fundBuy.css");
.subscribe-text > p {
  line-height: 30px;
}
</style>
