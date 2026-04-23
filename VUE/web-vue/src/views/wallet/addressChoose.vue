<template>
  <div class="css-vp41bv" v-if="isShowPop">
    <div class="css-snh7a0">
      <div class="css-hp9x1w">
        <!-- 头部 -->
        <div class="css-1dcbbbv">
          <div class="modaltitle css-4cffwv">
            <!-- title-从地址簿中选择 -->
            <div data-bn-type="text" class="css-e6jk6i">
              <div data-bn-type="text" class="css-irv4ls">
                {{ $t("message.user.xian6") }}
              </div>
            </div>
          </div>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            class="css-10uq0b5"
            @click="OnclickClose"
          >
            <path
              d="M6.697 4.575L4.575 6.697 9.88 12l-5.304 5.303 2.122 2.122L12 14.12l5.303 5.304 2.122-2.122L14.12 12l5.304-5.303-2.122-2.122L12 9.88 6.697 4.575z"
              fill="currentColor"
            ></path>
          </svg>
        </div>
        <div class="css-1gpt75q">
          <div class="css-m5fuap">
            <div class="css-1se472i">
              <!--没有地址  -->
              <div class="css-is7dhq" v-if="lstAdress.length == 0">
                <div class="css-1334ft5">
                  <!-- 空图片 -->
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 96 96"
                    fill="none"
                    class="css-fjdvjp"
                  >
                    <path
                      fill-rule="evenodd"
                      clip-rule="evenodd"
                      d="M16 17h8v4h-8v-4zM16 63h8v4h-8v-4zM16 29h8v4h-8v-4zM16 75h8v4h-8v-4z"
                      fill="#E6E8EA"
                    ></path>
                    <path
                      fill-rule="evenodd"
                      clip-rule="evenodd"
                      d="M24 8h60v80H24V8zm26.001 45c9.941 0 18-8.06 18-18 0-9.941-8.059-18-18-18s-18 8.059-18 18c0 9.94 8.059 18 18 18z"
                      fill="url(#not-found-contacts-light_svg__paint0_linear)"
                    ></path>
                    <path
                      fill-rule="evenodd"
                      clip-rule="evenodd"
                      d="M76 88V23h8v65h-8z"
                      fill="#E6E8EA"
                    ></path>
                    <path
                      d="M50 23a5 5 0 11-.001 10 5 5 0 010-10zM44 36h12a4 4 0 014 4v4H40v-4a4 4 0 014-4z"
                      fill="#E6E8EA"
                    ></path>
                    <path
                      d="M9.829 76.829l14.5-14.5-5.657-5.657-14.5 14.5 5.657 5.657z"
                      fill="url(#not-found-contacts-light_svg__paint1_linear)"
                    ></path>
                    <path
                      fill-rule="evenodd"
                      clip-rule="evenodd"
                      d="M51 46c0-8.837-7.163-16-16-16s-16 7.163-16 16 7.163 16 16 16 16-7.163 16-16zm4 0c0-11.046-8.954-20-20-20s-20 8.954-20 20 8.954 20 20 20 20-8.954 20-20z"
                      fill="url(#not-found-contacts-light_svg__paint2_linear)"
                    ></path>
                    <path
                      d="M29 14l3-3 3 3-3 3-3-3zM87 45l3-3 3 3-3 3-3-3z"
                      fill="#E6E8EA"
                    ></path>
                    <path d="M57 65l4-4 4 4-4 4-4-4z" fill="#fff"></path>
                    <defs>
                      <linearGradient
                        id="not-found-contacts-light_svg__paint0_linear"
                        x1="84"
                        y1="10.162"
                        x2="84"
                        y2="88"
                        gradientUnits="userSpaceOnUse"
                      >
                        <stop stop-color="#F5F5F5"></stop>
                        <stop offset="1" stop-color="#E6E8EA"></stop>
                      </linearGradient>
                      <linearGradient
                        id="not-found-contacts-light_svg__paint1_linear"
                        x1="14.251"
                        y1="76.829"
                        x2="14.251"
                        y2="56.672"
                        gradientUnits="userSpaceOnUse"
                      >
                        <stop stop-color="#929AA5"></stop>
                        <stop offset="1" stop-color="#76808F"></stop>
                      </linearGradient>
                      <linearGradient
                        id="not-found-contacts-light_svg__paint2_linear"
                        x1="15"
                        y1="46"
                        x2="55"
                        y2="46"
                        gradientUnits="userSpaceOnUse"
                      >
                        <stop stop-color="#929AA5"></stop>
                        <stop offset="1" stop-color="#76808F"></stop>
                      </linearGradient>
                    </defs>
                  </svg>

                  <div data-bn-type="text" class="css-77a6ym">
                    {{ $t("message.user.tip17") }}
                  </div>
                  <button
                    data-bn-type="button"
                    class="css-1fcvsm5"
                    @click="gotoAddressPage"
                  >
                    {{ $t("message.user.tianjiatixiandizhi") }}
                  </button>
                </div>
              </div>
              <!-- 数据 -->
              <el-row class="diff">
                <el-col :span="24" :offset="0">
                  <el-table
                    stripe
                    :data="lstAdress"
                    style="width: 100%"
                    class="table_all"
                    @row-click="OnClickItem"
                  >
                    <el-table-column
                      prop="paramValue2"
                      :label="$t('message.user.tibidizhi')"
                    >
                    </el-table-column>
                    <el-table-column
                      :label="$t('message.user.zhuanzhangwangluo')"
                      prop="paramValue3"
                    >
                    </el-table-column>
                    <el-table-column
                      :label="$t('message.user.beizhuxinxi')"
                      prop="remark"
                    >
                    </el-table-column>
                  </el-table>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
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
    callback: Function,
    demoArray: {
      type: Array,
      default: () => [],
    },
  },

  data() {
    return {
      isShowPop: this.inIsShowPop,
      strTitle: this.inTitle,
      lstAdress: [],
    };
  },
  watch: {
    inIsShowPop(newVal) {
      this.isShowPop = newVal; //对父组件传过来的值进行监听，如果改变也对子组件内部的值进行改变
    },
    inTitle(newVal) {
      this.strTitle = newVal;
    },
  },

  mounted() {
    this.getAddress();
  },
  methods: {
    getAddress() {
      c2cAxios.userC2cPaymentMethodList({ type: 1 }).then((res) => {
        this.lstAdress = res.data.filter((item) => {
          return item.methodType == 2; //币收款
        });
      });
    },
    OnclickClose() {
      this.isShowPop = false;
      this.callbackState(this.isShowPop);
    },
    OnClickItem(e) {
      this.callback(e.paramValue2);
    },
    gotoAddressPage() {
      this.$router.push("/addressma");
    },
  },
};
</script>
<style scoped>
@import url("@/assets/css/wallet/addresschoose.css");
.btn:hover {
  z-index: 1;
  background-color: rgb(207, 207, 202);
}
.bg-purple {
  text-align: center;
}
</style>
<style lang="css" scoped>
/*! CSS Used from: Embedded */
button {
  font-family: inherit;
  font-size: 100%;
  line-height: 1.15;
  margin: 0px;
}
button {
  overflow: visible;
}
button {
  text-transform: none;
}
button {
  appearance: button;
}
/*! CSS Used from: Embedded */
.css-4cffwv {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: flex;
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
.css-snh7a0 {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  position: relative;
  box-shadow: rgba(20, 21, 26, 0.16) 0px 8px 16px,
    rgba(71, 77, 87, 0.16) 0px 16px 32px, rgba(20, 21, 26, 0.1) 0px 0px 1px;
  border-radius: 6px;
  background-color: rgb(255, 255, 255);
  animation: 0.3s ease-in-out 0s 1 normal none running animation-1wqz9z0;
}
.css-hp9x1w {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: flex;
  background-color: rgb(255, 255, 255);
  position: relative;
  flex-direction: column;
  border-radius: 6px;
  padding-left: 0px;
  padding-right: 0px;
}
.css-hp9x1w > div:first-of-type {
  padding-left: 24px;
  padding-right: 24px;
}
.css-1dcbbbv {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: flex;
  position: relative;
  height: 64px;
  -webkit-box-align: center;
  align-items: center;
  width: 100%;
  -webkit-box-pack: justify;
  justify-content: space-between;
}
.css-e6jk6i {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  font-weight: 500;
  font-size: 20px;
  line-height: 24px;
}
.css-irv4ls {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  font-weight: 500;
  font-size: 20px;
  line-height: 28px;
  color: rgb(30, 35, 41);
}
.css-10uq0b5 {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  color: rgb(112, 122, 138);
  font-size: 24px;
  fill: rgb(112, 122, 138);
  width: 1em;
  height: 1em;
}
.css-10uq0b5:hover {
  color: rgb(240, 185, 11);
  cursor: pointer;
}
.css-1gpt75q {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: flex;
  flex-direction: column;
  height: 100%;
  padding-bottom: 16px;
  width: 100%;
}
@media screen and (min-width: 767px) {
  .css-1gpt75q {
    padding-bottom: 24px;
    width: 720px;
  }
}
@media screen and (min-width: 1023px) {
  .css-1gpt75q {
    width: 720px;
  }
}
.css-m5fuap {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  flex: 1 1 0%;
  overflow: hidden auto;
}
.css-1se472i {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  height: 100%;
}
@media screen and (min-width: 767px) {
  .css-1se472i {
    height: auto;
    max-height: 484px;
  }
}
@media screen and (min-width: 1023px) {
  .css-1se472i {
    height: auto;
  }
}
.css-is7dhq {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: flex;
  width: 100%;
  -webkit-box-pack: center;
  justify-content: center;
  padding-top: 32px;
  padding-bottom: 52px;
}
.css-1334ft5 {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  max-width: 484px;
  text-align: center;
}
.css-fjdvjp {
  margin: 32px auto;
  box-sizing: border-box;

  min-width: 0px;
  color: currentcolor;
  font-size: 96px;
  fill: currentcolor;
  width: 1em;
  height: 1em;
}
.css-77a6ym {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  font-size: 14px;
  line-height: 20px;
  color: rgb(71, 77, 87);
}
.css-1fcvsm5 {
  margin: 24px 0px 0px;
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
  padding: 12px 24px;
  min-width: 80px;
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
}
.css-1fcvsm5:disabled {
  cursor: not-allowed;
  background-image: none;
  background-color: rgb(234, 236, 239);
  color: rgb(183, 189, 198);
}
.css-1fcvsm5:disabled:not(.inactive) {
  background-color: rgb(234, 236, 239);
  color: rgb(183, 189, 198);
  cursor: not-allowed;
}
</style>
