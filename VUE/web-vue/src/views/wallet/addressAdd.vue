<template>
  <div class="css-vp41bv" v-if="isShowPop">
    <div class="css-1bzqcjp">
      <div class="css-7m7e43">
        <div class="css-13r01t2">
          <div class="css-ip5w0j">
            {{ $t("message.user.tianjiatixiandizhi") }}
          </div>

          <div class="css-17mum7g">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              class="css-f1b9tf"
              @click="OnclickClose"
            >
              <path
                d="M19.003 6.42l-1.41-1.41-5.59 5.59-5.59-5.59-1.41 1.41 5.59 5.59-5.59 5.59 1.41 1.41 5.59-5.59 5.59 5.59 1.41-1.41-5.59-5.59 5.59-5.59z"
                fill="currentColor"
              ></path>
            </svg>
          </div>
        </div>
        <div class="css-m0vwdv">
          <div class="css-vurnku">
            <div class="css-154a57d">
              <input
                data-bn-type="input"
                :placeholder="$t('message.user.tip3')"
                class="css-16fg16t"
                v-model="beizhu"
              /><label class="bn-input-label css-5vzups">
                <div class="css-kiaw5d">
                  {{ $t("message.user.dizhibeizhu") }}
                </div>
              </label>
            </div>
          </div>
          <div class="css-3tfm4c"></div>
          <div class="css-vurnku">
            <div class="css-154a57d">
              <input
                data-bn-type="input"
                :placeholder="$t('message.user.tip4')"
                class="css-16fg16t"
                v-model="real_name"
              /><label class="bn-input-label css-5vzups">
                <div class="css-kiaw5d">
                  {{ $t("message.user.zhenshixingming") }}
                </div>
              </label>
            </div>
          </div>
          <div class="css-3tfm4c"></div>
          <div class="css-16vu25q">
            <div class="css-7ng27">
              {{ $t("message.user.bizhong") }}
            </div>
            <div class="css-1dozx3h" @click="OnclickShow">
              <div class="css-1hvffwr">
                {{ listOpData[biChooseIndex].name?.toUpperCase() }}
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

          <div class="css-3tfm4c"></div>
          <div class="css-vurnku">
            <div class="css-154a57d">
              <input
                data-bn-type="input"
                :placeholder="$t('message.user.xian7')"
                class="css-16fg16t"
                v-model="adress"
              /><label class="bn-input-label css-5vzups">
                <div class="css-kiaw5d">
                  {{ $t("message.user.tibidizhi") }}
                </div>
              </label>
            </div>
          </div>
          <div class="css-3tfm4c"></div>
          <div class="css-16vu25q">
            <div class="css-7ng27">
              {{ $t("message.user.zhuanzhangwangluo") }}
            </div>
            <div class="css-1dozx3h" @click="OnclickShow1">
              <div class="css-1hvffwr">
                {{ listOpData1[biChooseIndex1].name?.toUpperCase() }}
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
        <div class="css-1q9xby6">
          <div class="css-7y16gy">
            <button data-bn-type="button" @click="closePop" class="css-12jy5lu">
              {{ $t("message.user.quxiao") }}</button
            ><button
              data-bn-type="button"
              class="css-12jy5lu btnenter"
              @click="OnClickAdd"
            >
              {{ $t("message.user.baocun") }}
            </button>
          </div>
        </div>
      </div>
    </div>
    <list-coins
      :inTitle="listTitle"
      :inIsShowPop="showListcoin"
      :callbackState="ListChangeShow"
      :inLstOption="listOpData"
      :callbackChoose="cbCoinChoose"
    ></list-coins>
    <list-coins
      :inTitle="listTitle1"
      :inIsShowPop="showListcoin1"
      :callbackState="ListChangeShow1"
      :inLstOption="listOpData1"
      :callbackChoose="cbCoinChoose1"
    ></list-coins>
  </div>
</template>

<script>
import walletAxios from "@/api/wallet.js";
import c2cAxios from "@/api/c2c.js";
export default {
  props: {
    inIsShowPop: {
      type: Boolean,
      default: false,
    },
    inTitle: {
      type: String,
      default: "aaaa",
    },
    callbackState: Function,
    demoArray: {
      type: Array,
      default: () => [],
    },
    methodId: {
      type: String,
      default: "",
    },
    payId: {
      type: String,
      default: "",
    },
    type: {
      type: Number,
    },
  },

  data() {
    return {
      isShowPop: this.inIsShowPop,
      strTitle: this.inTitle,
      showListcoin: false,
      listTitle: this.$t("message.user.ti24"),
      listOpData: [
        { icon: "usdt", name: "usdt" },
        { icon: "btc", name: "btc" },
        { icon: "eth", name: "eth" },
      ],
      biChooseIndex: 0,

      showListcoin1: false,
      listTitle1: this.$t("message.user.ti25"),
      listOpData1: [
        // { icon: "usdt", name: "trc20", code: "USDT" },
        // { icon: "usdt", name: "erc20", code: "USDT" },
      ],
      biChooseIndex1: 0,

      adress: "",
      beizhu: "",
      real_name: "",
    };
  },
  watch: {
    inIsShowPop(newVal) {
      this.isShowPop = newVal; //对父组件传过来的值进行监听，如果改变也对子组件内部的值进行改变
    },
    inTitle(newVal) {
      this.strTitle = newVal; //对父组件传过来的值进行监听，如果改变也对子组件内部的值进行改变
    },
    payId(val) {
      if (val) {
        this.getDetails();
      } else {
        this.real_name = "";
        this.beizhu = "";
        this.adress = "";
        this.biChooseIndex = 0;
        this.biChooseIndex1 = 0;
      }
    },
  },
  components: {},
  created() {},
  mounted() {
    this.getList();
    this.real_name = "";
    this.beizhu = "";
    this.adress = "";
    this.biChooseIndex = 0;
    this.biChooseIndex1 = 0;
  },
  methods: {
    getList(val) {
      walletAxios
        .getChainName({
          coin: this.listOpData[this.biChooseIndex].name,
        })
        .then((res) => {
          this.listOpData1 = [];
          for (var i in res.data) {
            const item = res.data[i];
            this.listOpData1.push({
              icon: item.coin,
              name: item.blockchain_name,
              code: item.coin,
            });
          }

          if (val) {
            for (let i = 0; i < this.listOpData1.length; i++) {
              const element = this.listOpData1[i];
              if (element.name == val) {
                this.blockchainIndex = i;
              }
            }
          } else {
            this.blockchainIndex = 0;
          }
        });
    },

    getDetails() {
      let obj = {
        id: this.payId,
      };
      c2cAxios.getC2cPaymentMethod(obj).then((res) => {
        if (res.code == "0") {
          this.real_name = res.data.realName;
          this.beizhu = res.data.remark;
          this.adress = res.data.paramValue2;
          for (let i = 0; i < this.listOpData.length; i++) {
            const element = this.listOpData[i];
            if (
              element.name.toUpperCase() == res.data.paramValue1.toUpperCase()
            ) {
              this.biChooseIndex = i;
            }
          }
          this.getList(res.data.paramValue3);
        }
      });
    },
    // 添加新的收款方式
    OnClickAdd() {
      if (!this.real_name) {
        this.$message.error(this.$t("message.user.tip4"));
        return;
      }
      if (this.adress.length < 1) {
        this.$message.error(this.$t("message.user.tip5"));
        return;
      }
      if (this.beizhu.length < 1) {
        this.$message.error(this.$t("message.user.tip6"));
        return;
      }
      var addParams = {
        real_name: this.real_name, //真实姓名
        method_config_id: this.methodId, //支付id
        param_value3: this.listOpData1[this.biChooseIndex1].name, //转账网络
        remark: this.beizhu, //地址备注
        param_value2: this.adress, //提币地址
        param_value1: this.listOpData[this.biChooseIndex].name, //币种
      };
      if (this.payId) {
        //修改
        addParams.id = this.payId;
        c2cAxios.updatePayConfig(addParams).then((res) => {
          if (res.code == "0") {
            this.$parent.getAddress();
            this.OnclickClose();
            this.$message({
              message: this.$t("message.user.xiugaichenggong"),
              type: "success",
            });
          }
        });
      } else {
        addParams.type = this.type;
        c2cAxios.addPayConfig(addParams).then((res) => {
          if (res.code == "0") {
            this.$message({
              message: this.$t("message.user.tianjiachenggong"),
              type: "success",
            });

            this.real_name = "";
            this.beizhu = "";
            this.adress = "";
            this.biChooseIndex = 0;
            this.biChooseIndex1 = 0;
            this.$parent.getAddress();
            this.OnclickClose();
            this.getList();
          }
        });
      }
    },
    OnclickClose() {
      this.isShowPop = false;
      this.callbackState(this.isShowPop);
    },
    cbCoinChoose(id) {
      this.biChooseIndex = id;
      this.showListcoin = false;
      this.coin = this.listOpData[id].code;
      this.getList();

      // this.getAdre();
    },

    OnclickShow() {
      this.showListcoin = !this.showListcoin;
    },
    ListChangeShow(state) {
      this.showListcoin = state;
    },
    cbCoinChoose1(id) {
      this.biChooseIndex1 = id;
      this.showListcoin1 = false;
      //this.coin1 = this.listOpData1[id].code;
      // this.getAdre();
    },

    OnclickShow1() {
      this.showListcoin1 = !this.showListcoin1;
    },
    ListChangeShow1(state) {
      this.showListcoin1 = state;
    },
    closePop() {
      this.$emit("closePop");
    },
  },
};
</script>
<style lang="css" scoped>
.btnenter {
  background-color: #1989fa !important;
  color: #fff !important;
  /* cursor: not-allowed; */
}

/*! CSS Used from: Embedded */
input {
  font-family: inherit;
}

input::-webkit-input-placeholder {
  font-style: normal !important;
}

input::placeholder {
  font-style: normal !important;
}

/*! CSS Used from: Embedded */
button,
input {
  font-family: inherit;
  font-size: 100%;
  line-height: 1.15;
  margin: 0px;
}

button,
input {
  overflow: visible;
}

button {
  text-transform: none;
}

button {
  appearance: button;
}

[type="checkbox"] {
  box-sizing: border-box;
  padding: 0px;
}

[hidden] {
  display: none;
}

/*! CSS Used from: Embedded */
.css-vurnku {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
}

.css-1f9551p {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: inline-block;
  position: relative;
}

.css-1yof1af {
  margin: 0px;
  min-width: 0px;
  box-sizing: content-box;
  position: absolute;
  width: max-content;
  word-break: normal;
  z-index: 1400;
  max-width: 296px;
  font-weight: 400;
  font-size: 12px;
  line-height: 16px;
  color: rgb(255, 255, 255);
  background-color: rgb(94, 102, 115);
  border-radius: 4px;
  padding: 8px 12px;
  filter: drop-shadow(rgba(20, 21, 26, 0.08) 0px 3px 6px)
    drop-shadow(rgba(71, 77, 87, 0.08) 0px 7px 14px)
    drop-shadow(rgba(20, 21, 26, 0.1) 0px 0px 1px);
}

.css-1yof1af[data-popper-placement^="top"] > [data-popper-arrow] {
  bottom: -3px;
}

.css-1yof1af > .gap-fill {
  position: absolute;
  display: block;
}

.css-1yof1af[data-popper-placement^="top"] > [data-popper-arrow]::before {
  transform: rotate(45deg);
}

.css-1yof1af[data-popper-placement^="top"] > .gap-fill {
  bottom: -8px;
  left: 0px;
  width: 100%;
  height: 8px;
}

.css-1yof1af .bn-tooltip-arrow::before {
  background-color: rgb(94, 102, 115);
}

.css-1u9esp9 {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
}

.css-1u9esp9,
.css-1u9esp9::before {
  position: absolute;
  width: 6px;
  height: 6px;
  z-index: -1;
}

.css-1u9esp9::before {
  content: "";
  display: block;
}

.css-1nlwvj5 {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  font-size: 16px;
  fill: currentcolor;
  transform: rotate(0deg);
  color: rgb(112, 122, 138);
  width: 1em;
  height: 1em;
}

.css-16fg16t {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  width: 100%;
  height: 100%;
  padding: 0px;
  outline: none;
  border: none;
  background-color: inherit;
  opacity: 1;
}

.css-16fg16t::-webkit-input-placeholder {
  color: rgb(183, 189, 198);
  font-size: 14px;
}

.css-16fg16t::placeholder {
  color: rgb(183, 189, 198);
  font-size: 14px;
}

.css-1iztezc {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  color: currentcolor;
  font-size: 24px;
  fill: currentcolor;
  width: 1em;
  height: 1em;
}

.css-16vu25q {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  width: 100%;
}

.css-b41z7l {
  box-sizing: border-box;
  margin: 0px 8px 0px 0px;
  min-width: 0px;
  flex-shrink: 0;
  width: 16px;
  height: 16px;
  line-height: 16px;
}

.css-b41z7l > svg {
  box-sizing: border-box;
  cursor: pointer;
  border: 1px solid rgb(183, 189, 198);
  border-radius: 2px;
  max-width: 100%;
  max-height: 100%;
  background-color: transparent;
  fill: transparent;
}

.css-b41z7l > svg path {
  fill: inherit;
}

.css-b41z7l input:checked ~ svg {
  border: none;
  background-color: rgb(240, 185, 11);
  fill: rgb(255, 255, 255);
}

.css-b41z7l input:disabled ~ svg {
  cursor: not-allowed;
  background-color: rgb(234, 236, 239);
  fill: rgb(234, 236, 239);
  border: none;
}

.css-b41z7l input:checked:disabled ~ svg {
  fill: rgb(183, 189, 198);
}

.css-p19g2b {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  position: absolute;
  opacity: 0;
  z-index: -1;
  width: 1px;
  height: 1px;
  overflow: hidden;
}

.css-f1b9tf {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  color: rgb(112, 122, 138);
  font-size: 24px;
  fill: rgb(112, 122, 138);
  width: 1em;
  height: 1em;
}

.css-vp41bv {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: flex;
  position: fixed;
  -webkit-box-align: center;
  align-items: center;
  -webkit-box-pack: center;
  justify-content: center;
  z-index: 1200;
  inset: 0px;
  background-color: rgba(0, 0, 0, 0.5);
}

.css-1bzqcjp {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  position: relative;
  background-color: rgb(255, 255, 255);
  animation: 0.3s ease-in-out 0s 1 normal none running animation-1wqz9z0;
  box-shadow: none;
  border-radius: 0px;
}

@media screen and (min-width: 767px) {
  .css-1bzqcjp {
    box-shadow: rgba(0, 0, 0, 0.1) 0px 10px 40px;
    border-radius: 0px;
  }
}

@media screen and (min-width: 1023px) {
  .css-1bzqcjp {
    box-shadow: rgba(0, 0, 0, 0.1) 0px 10px 40px;
    border-radius: 4px;
  }
}

.css-7m7e43 {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  width: 100vw;
  height: 100vh;
}

@media screen and (min-width: 767px) {
  .css-7m7e43 {
    width: 480px;
    height: auto;
  }
}

@media screen and (min-width: 1023px) {
  .css-7m7e43 {
    width: 480px;
    height: auto;
  }
}

.css-13r01t2 {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: flex;
  position: relative;
  padding: 20px 24px;
  -webkit-box-align: center;
  align-items: center;
}

.css-ip5w0j {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  font-weight: 500;
  font-size: 20px;
  line-height: 24px;
  color: rgb(30, 35, 41);
}

.css-17mum7g {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: block;
  position: absolute;
  right: 20px;
  top: 20px;
  cursor: pointer;
}

.css-154a57d {
  box-sizing: border-box;
  margin: 24px 0px 0px;
  min-width: 0px;
  display: inline-flex;
  position: relative;
  -webkit-box-align: center;
  align-items: center;
  line-height: 1.6;
  border: 1px solid rgb(234, 236, 239);
  border-radius: 4px;
  width: 100%;
  height: 48px;
}

.css-154a57d .bn-input-label {
  font-size: 12px;
}

.css-154a57d:hover:not(.bn-input-status-disabled):not(.bn-input-status-error) {
  border-color: rgb(240, 185, 11);
}

.css-154a57d input {
  color: rgb(30, 35, 41);
  font-size: 14px;
  padding-left: 8px;
  padding-right: 8px;
}

.css-154a57d input {
  padding-left: 12px;
  padding-right: 12px;
}

.css-5vzups {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  position: absolute;
  top: -24px;
  left: 0px;
  line-height: 24px;
  transition-property: top, font-size;
  transition-duration: 0.3s;
  transition-timing-function: ease;
  z-index: 1;
  cursor: text;
  color: rgb(71, 77, 87);
  font-size: 14px;
}

.css-kiaw5d {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  font-size: 14px;
  line-height: 20px;
}

.css-3tfm4c {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  height: 24px;
}

.css-7ng27 {
  box-sizing: border-box;
  margin: 0px 0px 4px;
  min-width: 0px;
  font-size: 14px;
  line-height: 20px;
  color: rgb(71, 77, 87);
}

.css-1dozx3h {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: flex;
  width: 100%;
  height: 48px;
  background-color: rgb(255, 255, 255);
  padding-left: 12px;
  padding-right: 12px;
  border-radius: 4px;
  border-style: solid;
  border-width: 1px;
  border-color: rgb(234, 236, 239);
  -webkit-box-pack: justify;
  justify-content: space-between;
  -webkit-box-align: center;
  align-items: center;
  cursor: auto;
}

.css-1dozx3h:hover {
  border-color: rgb(240, 185, 11);
}

.css-1hvffwr {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  font-size: 14px;
  color: rgb(183, 189, 198);
  line-height: 20px;
}

.css-jy76yn {
  box-sizing: border-box;
  margin: 6px 0px 0px;
  min-width: 0px;
  display: flex;
  -webkit-box-align: center;
  align-items: center;
  color: rgb(71, 77, 87);
  font-size: 14px;
}

.css-19l0k9u {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  font-weight: 400;
  font-size: 14px;
  line-height: 20px;
  cursor: pointer;
}

.css-12uuvlr {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  max-width: 258px;
  text-align: center;
}

.css-1yjog82 {
  box-sizing: border-box;
  margin: 0px 0px 0px 8px;
  min-width: 0px;
  height: 16px;
}

.css-ew2l8i {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  color: currentcolor;
  font-size: 16px;
  fill: currentcolor;
  width: 1em;
  height: 1em;
}

.css-1pxm4lx {
  box-sizing: border-box;
  margin: 24px 0px 0px;
  min-width: 0px;
}

.css-6hm6tl {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  color: rgb(30, 35, 41);
}

.css-1q9xby6 {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  padding: 16px 24px 24px;
}

.css-7y16gy {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: flex;
  -webkit-box-pack: end;
  justify-content: flex-end;
}

.css-12jy5lu {
  margin: 0px 8px 0px 0px;
  appearance: none;
  user-select: none;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-flex;
  -webkit-box-align: center;
  align-items: center;
  -webkit-box-pack: center;
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
  background-color: rgb(234, 236, 239);
  width: 128px;
  height: 40px;
}

.css-12jy5lu:disabled {
  cursor: not-allowed;
  box-shadow: none;
  color: rgb(183, 189, 198);
  background-color: rgb(245, 245, 245);
}

.css-oc3ep0 {
  margin: 0px;
  appearance: none;
  user-select: none;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-flex;
  -webkit-box-align: center;
  align-items: center;
  -webkit-box-pack: center;
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
  color: rgb(24, 26, 32);
  border-radius: 4px;
  min-height: 24px;
  border: none;
  background-image: none;
  background-color: rgb(252, 213, 53);
  width: 128px;
  height: 40px;
}

.css-oc3ep0:disabled {
  cursor: not-allowed;
  background-image: none;
  background-color: rgb(234, 236, 239);
  color: rgb(183, 189, 198);
}

.css-oc3ep0:disabled:not(.inactive) {
  background-color: rgb(234, 236, 239);
  color: rgb(183, 189, 198);
  cursor: not-allowed;
}

.css-m0vwdv {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  overflow-y: auto;
  padding: 24px 24px 32px;
  height: calc(100vh - 160px);
}

@media screen and (min-width: 767px) {
  .css-m0vwdv {
    height: 484px;
  }
}

@media screen and (min-width: 1023px) {
  .css-m0vwdv {
    height: 484px;
  }
}
</style>
