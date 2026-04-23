<!-- 提现 -->
<template>
  <div id="__APP">
    <div class="css-tq0shg">
      <main class="css-1wr4jig">
        <main class="css-xry4yv">
          <div class="css-1w42vtu">
            <!-- 头部数字货币提现 -->
            <div class="css-n1tycz">
              <div class="css-11y6cix">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 24 24"
                  fill="none"
                  class="css-rxoglf"
                  @click="OnClickPre"
                >
                  <path
                    fill-rule="evenodd"
                    clip-rule="evenodd"
                    d="M11.934 12l3.89 3.89-1.769 1.767L8.398 12l1.768-1.768 3.89-3.889 1.767 1.768-3.889 3.89z"
                    fill="currentColor"
                  ></path>
                </svg>
                <div class="css-1djsyd6">
                  {{ $t("message.user.xian1") }}
                </div>
              </div>
            </div>

            <div class="css-h4euq4">
              <div class="css-1k549dr">
                <div class="css-17kv38s">
                  <div class="css-yc6oq3"></div>
                </div>
                <div class="css-162e6pn">
                  <!-- form数据 -->
                  <div class="css-unl981">
                    <!-- 选择币种 -->
                    <div class="css-av4be8">
                      <div class="css-1x9w05y">
                        {{ $t("message.user.xian2") }}
                      </div>
                      <div class="css-pnaw5u">
                        <div class="css-16vu25q">
                          <div class="css-7ng27">
                            {{ $t("message.user.xian3") }}
                          </div>
                          <div class="css-1dozx3h" @click="OnclickShow">
                            <div class="css-1pysja1">
                              <div class="css-hwqc42">
                                <div class="css-1pi1v2y">
                                  <img
                                    flex="1"
                                    :src="
                                      handleSymbolImg(
                                        coinList[biChooseIndex].icon
                                      )
                                    "
                                    width="24"
                                    height="24"
                                  />
                                  <div class="css-15k81fw">
                                    <div class="css-vurnku">
                                      <span class="css-1c82c04">{{
                                        coinList[biChooseIndex].name
                                      }}</span>
                                    </div>

                                    <div title="Bitcoin" class="css-ruyrf1">
                                      {{ coinList[biChooseIndex].name }}
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 16 16"
                              fill="none"
                              class="css-1nlwvj5"
                            >
                              <path
                                d="M11 5.632v1.4L8.2 10 5.4 7.032v-1.4H11z"
                                fill="currentColor"
                              ></path>
                            </svg>
                          </div>
                        </div>
                      </div>
                    </div>
                    <!--提币地址 -->
                    <div class="css-1oi6dii">
                      <div class="css-1x9w05y">
                        {{ $t("message.user.xian4") }}
                      </div>
                      <div class="css-pnaw5u">
                        <div class="css-vurnku">
                          <!-- 方式选择 -->
                          <div class="css-ylrg3u">
                            <div class="css-4cffwv">
                              <div
                                :class="
                                  m_menuchoose == 0
                                    ? 'css-1xwn3t'
                                    : 'css-vuv0mn'
                                "
                                @click="MenuChoose(0)"
                              >
                                {{ $t("message.user.shiyongxindizhi") }}
                              </div>
                              <div
                                :class="
                                  m_menuchoose == 1
                                    ? 'css-1vurpwy'
                                    : 'css-q8u9rk'
                                "
                                @click="MenuChoose(1)"
                              >
                                {{ $t("message.user.dizhibo") }}
                              </div>
                            </div>
                            <router-link to="/addressma">
                              <div
                                data-bn-type="link"
                                id="crypto_withdraw_address_management"
                                target="_blank"
                                class="css-lmq5by"
                                href="/zh-CN/my/security/address-management"
                              >
                                {{ $t("message.user.dizhiguanli") }}
                              </div>
                            </router-link>
                          </div>

                          <div class="css-vurnku">
                            <div class="css-vurnku">
                              <!--地址簿下拉框  -->
                              <div class="css-1pxm4lx" v-if="m_menuchoose == 1">
                                <div class="css-16vu25q">
                                  <div class="css-7ng27">
                                    {{ $t("message.user.xian5") }}
                                  </div>
                                  <div
                                    class="css-1dozx3h"
                                    @click="OnClickShowAddre"
                                  >
                                    <div class="css-1hvffwr">
                                      {{ $t("message.user.xian6") }}
                                    </div>
                                    <svg
                                      xmlns="http://www.w3.org/2000/svg"
                                      viewBox="0 0 16 16"
                                      fill="none"
                                      class="css-1nlwvj5"
                                    >
                                      <path
                                        d="M11 5.632v1.4L8.2 10 5.4 7.032v-1.4H11z"
                                        fill="currentColor"
                                      ></path>
                                    </svg>
                                  </div>
                                </div>
                              </div>
                              <!--提币地址  -->
                              <div class="css-154a57d" v-if="m_menuchoose == 0">
                                <input
                                  :placeholder="$t('message.user.xian7')"
                                  class="css-16fg16t"
                                  v-model="address"
                                /><label class="bn-input-label css-5vzups">
                                  <div class="css-kiaw5d">
                                    {{ $t("message.user.xian8") }}
                                  </div>
                                </label>
                              </div>
                            </div>
                          </div>
                          <!-- 转账网络 -->
                          <div class="css-1pxm4lx" v-if="m_menuchoose == 0">
                            <div class="css-vurnku">
                              <div class="css-16vu25q">
                                <div class="css-7ng27">
                                  {{ $t("message.user.xian9") }}
                                </div>
                                <div class="css-1dozx3h" @click="OnclickShow1">
                                  <div
                                    class="css-1hvffwr"
                                    v-if="netList.length"
                                  >
                                    {{
                                      netList[
                                        blockchainIndex
                                      ].name?.toUpperCase()
                                    }}
                                  </div>
                                  <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    viewBox="0 0 16 16"
                                    fill="none"
                                    class="css-1nlwvj5"
                                  >
                                    <path
                                      d="M11 5.632v1.4L8.2 10 5.4 7.032v-1.4H11z"
                                      fill="currentColor"
                                    ></path>
                                  </svg>
                                </div>
                              </div>
                            </div>
                          </div>
                          <div style="margin-bottom: 20px"></div>
                          <div class="css-vurnku" v-if="m_menuchoose == 0">
                            <div class="css-vurnku">
                              <!---->
                              <div class="css-154a57d">
                                <input
                                  type="number"
                                  :placeholder="$t('message.user.xian10')"
                                  v-model="inputNum"
                                  class="css-16fg16t"
                                /><label class="bn-input-label css-5vzups">
                                  <div class="css-kiaw5d">
                                    {{ $t("message.user.xian11") }}
                                  </div>
                                </label>
                              </div>
                            </div>
                          </div>
                          <div style="margin-bottom: 20px"></div>
                          <div class="css-vurnku" v-if="m_menuchoose == 0">
                            <div class="css-vurnku">
                              <!---->
                              <div class="css-154a57d">
                                <input
                                  type="password"
                                  :placeholder="$t('message.user.zijinmima')"
                                  v-model="inputPwd"
                                  class="css-16fg16t"
                                /><label class="bn-input-label css-5vzups">
                                  <div class="css-kiaw5d">
                                    {{ $t("message.user.zijinmima") }}
                                  </div>
                                </label>
                              </div>
                            </div>
                          </div>
                          <div class="css-vurnku" v-if="m_menuchoose == 0">
                            <div class="css-vurnku">
                              <div class="css-8mvhti">
                                <!-- 到账数量 -->
                                <div class="css-1hythwr">
                                  <div class="css-kiaw5d">
                                    {{ $t("message.user.xian12") }}
                                  </div>
                                  <div class="css-rjs222">
                                    {{ reciveMoney }}
                                  </div>
                                </div>
                                <!-- <div  class="css-1hythwr">
                                  <el-button type="primary" @click="formSubmit"> {{ $t('message.user.xian13') }}</el-button>
                                </div> -->
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <!-- 展示数据 -->
                  <div class="css-4cffwv">
                    <div class="css-yttbih"></div>
                    <div class="css-1hythwr">
                      <div class="css-8mvhti">
                        <!-- 可用余额 -->
                        <div class="css-1hythwr">
                          <div class="css-v0h2s1">
                            {{ this.coinList[this.biChooseIndex].name }}
                            {{ $t("message.user.xian14") }}
                          </div>
                          <div class="css-rjs222">
                            {{ itemAllMoney }}
                          </div>
                        </div>
                        <!-- 最小提币数量 -->
                        <div class="css-1hythwr">
                          <div class="css-v0h2s1">
                            {{ $t("message.user.xian15") }}
                          </div>
                          <div class="css-rjs222">
                            {{ miniMoney }}
                          </div>
                        </div>
                      </div>
                      <div class="css-1dnnrwx">
                        <!-- 提币手续费 -->
                        <div class="css-1hythwr">
                          <div class="css-v0h2s1">
                            {{ $t("message.user.xian16") }}
                          </div>
                          <div class="css-rjs222">
                            {{ feeFee }}
                          </div>
                        </div>
                        <!-- 24H剩余提现额度 -->
                        <div class="css-1hythwr">
                          <div class="css-v0h2s1">
                            {{ $t("message.user.xian17") }}
                          </div>
                          <div class="css-rjs222">
                            <div class="css-4cffwv">
                              <div class="css-qcbo30">
                                {{ itemAllMoney }}
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <!-- 提现按钮 -->
                  <div class="css-1hythwr submit-but">
                    <el-button type="primary" @click="formSubmit">
                      {{ $t("message.user.xian13") }}</el-button
                    >
                  </div>
                </div>
                <div class="css-1ejdi4m"></div>
                <!-- 右边提现提示 -->
                <div class="css-3cav34">
                  <div class="css-4krk71">
                    {{ $t("message.user.xian18") }}？
                  </div>
                  <div class="css-17bmjki recharge-question-box">
                    <div>{{ $t("message.user.xian19") }}。</div>
                    <div class="recharge-question-text">
                      <div>* {{ $t("message.user.xian20") }}</div>
                    </div>
                  </div>
                  <!-- <el-button type="primary" @click="contactBtn">
                        {{$t('message.user.xian21')}}
                        </el-button> -->
                </div>
                <!-- 提现记录 -->
                <div class="css-1uvknfx">
                  <div class="css-15owl46">
                    <div class="css-fkm53z" v-if="listData.length > 0">
                      {{ $t("message.user.xian22") }}
                    </div>
                    <div
                      class="css-pe35up"
                      style="min-height: 0"
                      v-for="item in listData"
                      :key="item.order_no"
                    >
                      <div class="css-1fjndyf">
                        <div class="css-1f4k85w">
                          <div class="css-3j2kqe">
                            <div class="css-uliqdc">
                              <div class="css-1p1xuqp">
                                <div class="css-10nf7hq">
                                  <img
                                    :src="handleSymbolImg(item.coin)"
                                    class="css-1leo1x6"
                                  />
                                  <div class="css-rjqmed">
                                    {{ item.volume }} {{ item.coin }}
                                  </div>
                                  <div
                                    class="css-1q5mwn2"
                                    style="
                                      background-color: rgb(201, 148, 0);
                                      color: white;
                                    "
                                    v-if="item.status == 0"
                                  >
                                    {{ $t("message.user.xian23") }}
                                  </div>
                                  <div
                                    class="css-1q5mwn2"
                                    v-if="item.status == 1"
                                  >
                                    {{ $t("message.user.xian24") }}
                                  </div>
                                  <div
                                    class="css-1q5mwn2"
                                    v-if="item.status == 2"
                                    style="background-color: red; color: white"
                                  >
                                    {{ $t("message.user.shibai") }}
                                  </div>
                                  <div
                                    v-if="item.status == 2"
                                    style="margin-left: 30px"
                                  >
                                    <span
                                      >{{ $t("message.user.yuanyin") }}:</span
                                    >
                                    <span>{{ item.failure_msg }}</span>
                                  </div>
                                </div>
                              </div>
                              <div class="css-12iiuk5">
                                <div class="css-7rgokv">
                                  {{ item.createTime }}
                                </div>
                                <div class="css-1i03ndq">
                                  <div class="css-cp92rp">
                                    <div class="css-fhtmef">
                                      {{ $t("message.user.xian25") }}
                                    </div>
                                    <div class="css-af5usq">
                                      {{
                                        item.coin_blockchain
                                          ? item.coin_blockchain
                                          : "--"
                                      }}
                                    </div>
                                    <div
                                      class="css-fhtmef"
                                      style="margin-left: 15px"
                                    >
                                      {{ $t("message.user.xian26") }}
                                    </div>
                                    <div class="css-af5usq">
                                      {{ item.to }}
                                    </div>
                                  </div>
                                </div>
                                <div class="css-1i03ndq">
                                  <div class="css-cp92rp">
                                    <div class="css-190cj18">TxID</div>
                                    <div class="css-af5usq">
                                      {{ item.tx ? item.tx : "--" }}
                                    </div>
                                  </div>
                                </div>
                                <div
                                  class="css-1i03ndq"
                                  style="margin-left: 10px"
                                >
                                  <div class="css-fhtmef">
                                    {{ $t("message.user.xian27") }}
                                  </div>
                                  <div class="css-af5usq">
                                    {{ $t("message.user.xian28") }}
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <!-- 查看全部跳转到钱包历史记录页面 -->
                    <div class="margin-top20 mouse-cursor">
                      <a
                        v-if="listData.length > 0"
                        class="css-1911t1t"
                        @click="goRouter('/order/walletHistory')"
                      >
                        {{ $t("message.user.xian29") }}</a
                      >
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </main>
      </main>
      <!-- 提现币种 -->
      <list-coins
        :inTitle="listTitle"
        :inIsShowPop="showCoinModal"
        :callbackState="ListChangeShow"
        :inLstOption="coinList"
        :callbackChoose="cbCoinChoose"
      ></list-coins>
      <!-- 地址簿 -->
      <address-choose
        :inIsShowPop="showAddreChoose"
        :callbackState="AddressChangeShow"
        :callback="cbAdressChoose"
      ></address-choose>
      <!-- 转账网络 -->
      <list-coins
        :inTitle="listTitle1"
        :inIsShowPop="showNetModal"
        :callbackState="ListChangeShow1"
        :inLstOption="netList"
        :callbackChoose="cbNetChoose"
      ></list-coins>
    </div>
  </div>
</template>
<script>
import ListCoins from "@/components/my/listCoins.vue";
import AddressChoose from "../addressChoose.vue";
import Axios2 from "@/api/wallet.js";
import { handleSymbolImg } from "@/utils";
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";
export default {
  components: { AddressChoose, ListCoins },
  data() {
    return {
      showCoinModal: false,
      listTitle: this.$t("message.user.xian30"),
      coinList: [
        { icon: "usdt", name: "USDT", min: 10 },
        // { icon: "btc", name: "BTC", min: 0.0000094 },
        // { icon: "eth", name: "ETH", min: 0.000094 },
      ], //币种
      biChooseIndex: 0,
      m_menuchoose: 0,
      showAddreChoose: false,
      showNetModal: false,
      listTitle1: this.$t("message.user.xian31"),
      netList: [],
      blockchainIndex: 0,
      address: "",
      walletList: [],
      blockchain: [], //"ERC20", "TRC20", "OMNI"
      feeFee: "",
      miniMoney: "",
      m_minMoeny: 0,
      reciveMoney: "",
      itemAllMoney: "",
      moneymap: null,
      inputNum: "1",
      session_token: "",
      inputPwd: "",
      listData: [], //提现记录列表数据
    };
  },
  mounted() {
    this.moneymap = new Map();

    if (this.existToken) {
      this.getWallet();
      this.getList();
      this.getWithdraw_open();
      this.getWithdrawRecord();
    }
  },
  watch: {
    inputNum(val) {
      this.getFee();
    },
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  methods: {
    handleSymbolImg,
    // 获取钱包
    getWallet() {
      Axios2.getWalletList().then((res) => {
        var jsonArray = res;
        for (var i in jsonArray.data) {
          if (i == "USDT") {
            jsonArray.data[i].amount = jsonArray.data[i].money;
          }
          this.moneymap.set(i, jsonArray.data[i]);
        }
        this.ShowNowMoney();
      });
    },
    // 获取链的名称
    getList() {
      Axios2.getChainName({
        coin: this.coinList[this.biChooseIndex].name,
      }).then((res) => {
        this.blockchain = res.data;
        this.netList = [];
        for (var i in res.data) {
          this.netList.push({
            icon: res.data[i].coin,
            name: res.data[i].blockchain_name,
          });
        }
        this.blockchainIndex = 0;
        this.getFee();
        this.getMinMoney();
      });
    },
    // 提现数量限制
    getMinMoney() {
      var channel = this.coinList[this.biChooseIndex].name;
      Axios2.url("api/withdraw/limit", {
        channel: channel,
      }).then((res) => {
        this.miniMoney = res.data.limit;
      });
    },
    // 获取手续费和可用余额
    getFee() {
      var channel = this.coinList[this.biChooseIndex].name;
      Axios2.getWithdrawFee({
        amount: this.inputNum,
        channel: channel,
      }).then((res) => {
        this.reciveMoney = res.data.volume_last;
        this.feeFee = res.data.fee;
      });
    },
    getWithdraw_open() {
      Axios2.getWithdrawToken().then((res) => {
        this.session_token = res.data.session_token;
      });
    },
    formSubmit() {
      if (this.address.length < 1) {
        this.$message.error(this.$t("message.user.xian32"));
        return;
      }
      if (this.inputPwd.length < 6) {
        this.$message.error(this.$t("message.user.qsr_zijinmima6wei"));
        return;
      }

      if (this.inputNum.length < 1) {
        this.$message.error(this.$t("message.user.xian33"));
        return;
      }
      let num = Number(this.inputNum);
      if (num < this.m_minMoeny) {
        this.$message.error(this.$t("message.user.xian34"));
        return;
      }
      let name = this.coinList[this.biChooseIndex].name;
      let item = this.moneymap.get(name);

      if (num > item.amount) {
        this.$message.error(this.$t("message.user.xian35"));
        return;
      }

      var channel =
        this.coinList[this.biChooseIndex].name +
        "_" +
        this.blockchain[this.blockchainIndex].blockchain_name;

      var withdrawParams = {
        from: this.address,
        channel: channel,
        amount: this.inputNum,
        safeword: this.inputPwd,
        session_token: this.session_token,
      };
      // 申请提现
      Axios2.withdrawApply(withdrawParams).then((res) => {
        var jsonArray = res;
        if (jsonArray.code == 0) {
          this.$message.success(this.$t("message.user.xian37"));
          this.address = "";
          this.inputPwd = "";
          this.getWithdrawRecord();
        } else if (jsonArray.code == "105") {
          this.$message.error(
            this.$t("message.user.xian38") +
              jsonArray.msg +
              this.$t("message.user.xian39")
          );
        }
        this.getWithdraw_open();
      });
    },
    cbAdressChoose(address) {
      this.address = address;
      this.showAddreChoose = false;
      this.m_menuchoose = 0;
    },
    // 选择币种后的刷新
    cbCoinChoose(id) {
      this.biChooseIndex = id;
      this.showCoinModal = false;
      this.getList();
      this.ShowNowMoney();
    },
    // 获取账户里面提现币种的金额
    ShowNowMoney() {
      let name = this.coinList[this.biChooseIndex].name;
      // 因为有外汇美股，所以moneymap只有ETHUSDT，
      let item = this.moneymap.get(name);
      this.itemAllMoney = `${item.amount}${name}`;
    },
    // 展示币种
    OnclickShow() {
      this.showCoinModal = true;
    },
    ListChangeShow(state) {
      this.showCoinModal = state;
    },

    MenuChoose(id) {
      this.m_menuchoose = id;
    },
    // 从地址簿中选择
    OnClickShowAddre() {
      this.showAddreChoose = !this.showAddreChoose;
    },
    AddressChangeShow(state) {
      this.showAddreChoose = state;
    },
    cbNetChoose(id) {
      this.blockchainIndex = id;
      this.showNetModal = false;
    },

    OnclickShow1() {
      this.showNetModal = !this.showCoinModal;
    },
    ListChangeShow1(state) {
      this.showNetModal = state;
    },
    OnClickPre() {
      this.$router.go(-1);
    },
    //查询提币记录
    getWithdrawRecord() {
      Axios2.getWithdrawList({
        page_no: 1,
      }).then((res) => {
        this.listData = res.data.slice(0, 5);
      });
    },
    // //联系在线客服
    // contactBtn() {

    // },
    goRouter(parmas) {
      this.$router.push(parmas);
    },
  },
};
</script>
<style scoped lang="scss">
@import url("@/assets/css/wallet/withdraw.css");

.css-kiaw5d {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  font-size: 14px;
  line-height: 20px;
}

.btnQu {
  margin: 0px;
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  font-family: inherit;
  text-align: center;
  text-decoration: none;
  outline: none;
  padding: 6px 12px;
  min-width: 52px;
  font-weight: 500;
  font-size: 14px;
  line-height: 20px;
  word-break: keep-all;
  color: rgb(30, 35, 41);
  border-radius: 4px;
  min-height: 24px;
  border: none;
  background-color: rgb(241 192 34);

  max-width: 200px;
}

/*! CSS Used from: Embedded */
a,
a:active,
a:visited {
  text-decoration: none;
}

/*! CSS Used from: Embedded */
a {
  background-color: transparent;
}

/*! CSS Used from: Embedded */
.css-lmq5by {
  box-sizing: border-box;
  margin: 0px 0px 0px 24px;
  min-width: 0px;
  text-decoration: none;
  color: rgb(201, 148, 0);
  font-size: 14px;
  line-height: 20px;
}

.css-lmq5by:hover {
  text-decoration: underline;
  color: rgb(240, 185, 11);
}

.css-vuv0mn {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  padding: 10px 16px;
  font-size: 14px;
  line-height: 20px;
  color: rgb(112, 122, 138);
  background-color: rgb(255, 255, 255);
  border-radius: 4px;
  cursor: pointer;
  text-align: center;
}

.css-q8u9rk {
  box-sizing: border-box;
  margin: 0px 0px 0px 24px;
  min-width: 0px;
  padding: 10px 16px;
  font-size: 14px;
  line-height: 20px;
  color: rgb(112, 122, 138);
  background-color: rgb(255, 255, 255);
  border-radius: 4px;
  cursor: pointer;
  text-align: center;
}

.css-1vurpwy {
  box-sizing: border-box;
  margin: 0px 0px 0px 24px;
  min-width: 0px;
  padding: 10px 16px;
  font-size: 14px;
  line-height: 20px;
  color: rgb(30, 35, 41);
  background-color: rgb(245, 245, 245);
  border-radius: 4px;
  cursor: pointer;
  text-align: center;
}

.submit-but {
  width: 500px;
  margin-left: 200px;
  margin-top: 50px;
  .el-button--primary {
    padding: 20px;
  }
}
</style>
