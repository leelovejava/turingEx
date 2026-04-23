<template>
  <div id="__APP">
    <div style="position: relative"></div>
    <div class="css-tq0shg">
      <div class="css-14rvj6w">
        <div class="css-jrzkh7">
          <div id="header_global_js_wxgy34nj" class="css-1aac2e"></div>
        </div>
      </div>
      <main class="css-1wr4jig">
        <main class="css-xry4yv">
          <div class="css-1wr4jig">
            <div class="css-6ul7zn">
              <div class="css-1lcs7oa">
                <div class="css-1m9qn0r">
                  <div class="css-z32tht">
                    <div class="css-c7nxby">
                      <div class="css-k2y2sp">
                        <div data-bn-type="text" class="css-1104sf1">
                          {{ $t("message.user.dizhiguanli") }}
                        </div>
                      </div>
                      <div class="css-1f48ywh">
                        <div class="css-12sryi7">
                          <div class="css-1lcw97r">
                            <div
                              class="bn-input-md css-1s4fl8l"
                              @click="showListcoin = true"
                            >
                              <input
                                data-bn-type="input"
                                disabled
                                :aria-label="$t('message.user.bizhong')"
                                class="bn-sdd-input css-rivkf9"
                              />
                              <div class="bn-input-suffix css-vurnku">
                                <div class="css-13c2b5p">
                                  <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    class="css-1nlwvj5"
                                  >
                                    <path
                                      d="M16.5 8.49v2.25L12 15.51l-4.5-4.77V8.49h9z"
                                      fill="currentColor"
                                    ></path>
                                  </svg>
                                </div>
                              </div>
                              <label class="bn-input-label css-k4png4">{{
                                $t("message.user.bizhong")
                              }}</label>
                            </div>
                            <div class="bn-sdd-value css-1h4bkt6">
                              {{ listOpData[selectIndex].name.toUpperCase() }}
                            </div>
                          </div>
                          <div class="css-1ow4n4l">
                            <div class="bn-sdd-dropdown closing css-1tsr9u1">
                              <ul role="listbox" class="bn-sdd-list css-2rl2kr">
                                <li
                                  role="option"
                                  id="all"
                                  title="all"
                                  class="bn-sdd-option-item css-88fj32"
                                >
                                  <div class="bn-sdd-option css-4cffwv">
                                    <div class="css-1pysja1">
                                      <div
                                        data-bn-type="text"
                                        class="css-10ae9nd"
                                      >
                                        {{ $t("message.user.quanbu") }}
                                      </div>
                                    </div>
                                  </div>
                                </li>
                              </ul>
                            </div>
                          </div>
                          <!-- 搜索 -->
                          <div class="css-1wie70i">
                            <div class="bn-input-md css-b08d2g">
                              <div class="bn-input-prefix css-vurnku">
                                <div class="css-k6nsss">
                                  <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    class="css-phgw1n"
                                  >
                                    <path
                                      fill-rule="evenodd"
                                      clip-rule="evenodd"
                                      d="M17 11a6 6 0 10-12 0 6 6 0 0012 0zm-6-8a8 8 0 110 16 8 8 0 010-16z"
                                      fill="#76808F"
                                    ></path>
                                    <path
                                      fill-rule="evenodd"
                                      clip-rule="evenodd"
                                      d="M20.586 22L15 16.414 16.414 15 22 20.586 20.586 22z"
                                      fill="#76808F"
                                    ></path>
                                  </svg>
                                </div>
                              </div>
                              <input
                                @input="seachByAddress"
                                v-model="address"
                                :aria-label="$t('message.user.sousuo')"
                                :placeholder="$t('message.user.dizhisousuo')"
                                autocomplete="off"
                                class="css-16fg16t"
                              /><label class="bn-input-label css-k4png4">{{
                                $t("message.user.sousuo")
                              }}</label>
                            </div>
                          </div>
                        </div>

                        <div class="css-11234sj" style="position: relative">
                          <button
                            class="css-89jh4p paytypebut"
                            @click="OnClickShowAddre"
                          >
                            {{ $t("message.user.tianjiashoukuanfangshi")
                            }}<i class="el-icon-plus"></i>
                          </button>
                          <!-- 收款数据 -->
                          <div class="pay-type-select" v-if="payTypeShow">
                            <div
                              class="pay-type-select-item"
                              v-for="(item, index) in lstData"
                              :key="index"
                              @click="openPay(item)"
                            >
                              <div class="pay-type-select-item-line"></div>
                              {{ item.name }}
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div class="css-z32tht">
                      <div class="css-joa6mv"></div>
                      <div class="css-1arttng">
                        <div
                          class="rc-table css-xdf65a rc-table-scroll-horizontal"
                        >
                          <div class="rc-table-container">
                            <div
                              class="rc-table-content"
                              style="overflow: auto hidden"
                            >
                              <div
                                class="pay-type-item"
                                v-for="(item, index) in listTableList"
                                :key="index"
                              >
                                <div class="pay-top">
                                  <div class="pay-top-line"></div>
                                  <div>{{ item.methodTypeName }}</div>
                                  <div class="caozuo">
                                    <div
                                      class="caozuo-item"
                                      @click="handleEdit(item)"
                                    >
                                      {{ $t("message.user.bianji") }}
                                    </div>
                                    <div
                                      class="caozuo-item"
                                      @click="OnClickDel(item.uuid)"
                                    >
                                      {{ $t("message.user.shanchu") }}
                                    </div>
                                  </div>
                                </div>
                                <div
                                  class="pay-bottom"
                                  v-if="item.methodType == 1"
                                >
                                  <div class="pay-bottom-item">
                                    <div class="bottom-title">
                                      {{ $t("message.user.xingming") }}
                                    </div>
                                    <div class="bottom-text">
                                      {{ item.realName }}
                                    </div>
                                  </div>
                                  <div class="pay-bottom-item">
                                    <div class="bottom-title">
                                      {{ $t("message.user.yinhangkazhanghu") }}
                                    </div>
                                    <div class="bottom-text">
                                      {{ item.paramValue3 }}
                                    </div>
                                  </div>
                                  <div class="pay-bottom-item">
                                    <div class="bottom-title">
                                      {{ $t("message.user.yinhangmingcheng") }}
                                    </div>
                                    <div class="bottom-text">
                                      {{ item.paramValue1 }}
                                    </div>
                                  </div>
                                  <div class="pay-bottom-item">
                                    <div class="bottom-title">
                                      {{ $t("message.user.kaihuhang") }}
                                    </div>
                                    <div class="bottom-text">
                                      {{ item.paramValue2 }}
                                    </div>
                                  </div>
                                </div>
                                <div
                                  class="pay-bottom"
                                  v-if="item.methodType == 2"
                                >
                                  <div class="pay-bottom-item">
                                    <div class="bottom-title">
                                      {{ $t("message.user.dizhibeizhu") }}
                                    </div>
                                    <div class="bottom-text">
                                      {{ item.remark }}
                                    </div>
                                  </div>
                                  <div class="pay-bottom-item">
                                    <div class="bottom-title">
                                      {{ $t("message.user.zhenshixingming") }}
                                    </div>
                                    <div class="bottom-text">
                                      {{ item.realName }}
                                    </div>
                                  </div>
                                  <div class="pay-bottom-item">
                                    <div class="bottom-title">
                                      {{ $t("message.user.bizhong") }}
                                    </div>
                                    <div class="bottom-text">
                                      {{ item.paramValue1 }}
                                    </div>
                                  </div>
                                  <div class="pay-bottom-item">
                                    <div class="bottom-title">
                                      {{ $t("message.user.dizhi") }}
                                    </div>
                                    <div class="bottom-text">
                                      {{ item.paramValue2 }}
                                    </div>
                                  </div>
                                  <div class="pay-bottom-item">
                                    <div class="bottom-title">
                                      {{ $t("message.user.zhuanzhangwangluo") }}
                                    </div>
                                    <div class="bottom-text">
                                      {{ item.paramValue3 }}
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </main>
      </main>
    </div>
    <!-- 币种添加地址 -->
    <address-add
      @closePop="closePop"
      :inIsShowPop="showAddreChoose"
      :payId="payId"
      :methodId="methodId"
      :callbackState="AddressChangeShow"
      :type="1"
    ></address-add>
    <el-dialog v-model="dialogVisible" width="470px">
      <img class="tip-img" src="@/assets/images/wallet/tip.png" />
      <div class="dig-title">{{ $t("message.user.tip1") }}</div>
      <div class="dig-text">{{ $t("message.user.tip2") }}</div>
      <template #footer>
        <span class="dialog-footer flex justify-center">
          <el-button class="but" @click="dialogVisible = false">{{
            $t("message.user.quxiao")
          }}</el-button>
          <el-button class="but" type="primary" @click="hanldeDelete">{{
            $t("message.user.queding")
          }}</el-button>
        </span>
      </template>
    </el-dialog>
    <list-coins
      :inTitle="listTitle"
      :inIsShowPop="showListcoin"
      :callbackState="ListChangeShow"
      :inLstOption="listOpData"
      :callbackChoose="cbCoinChoose"
    ></list-coins>
  </div>
</template>
<script>
import AddressAdd from "./addressAdd.vue";
import c2cAxios from "@/api/c2c.js";
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";
export default {
  components: { AddressAdd },
  data() {
    return {
      showAddreChoose: false,
      menuchoose: 0, //地址 ，新地址1
      lstData: [],
      payTypeShow: false,
      methodId: "",
      listTableList: [],
      payId: "",
      dialogVisible: false,
      deleteId: "",
      listTitle: this.$t("message.user.ti24"),
      showListcoin: false,
      listOpData: [
        { icon: "", name: this.$t("message.user.quanbu") },
        { icon: "usdt", name: "usdt" },
        { icon: "btc", name: "btc" },
        { icon: "eth", name: "eth" },
      ], //币种下拉框
      allList: [],
      selectIndex: 0,
      address: "", //搜索框输入的地址
    };
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  mounted() {
    this.lstData = [];

    if (this.existToken) {
      this.getAddress();
    }
  },

  methods: {
    // 选中币种之后
    cbCoinChoose(id) {
      this.listTableList = this.allList;
      this.selectIndex = id;
      if (id !== 0) {
        this.listTableList = this.listTableList.filter((item) => {
          return item.paramValue1 == this.listOpData[this.selectIndex].icon;
        });
      }
      this.showListcoin = false;
    },
    seachByAddress() {
      if (this.address) {
        this.listTableList = this.listTableList.filter((item) => {
          return item.paramValue2.indexOf(this.address) != -1;
        });
      } else {
        this.listTableList = this.allList;
        if (this.selectIndex !== 0) {
          this.listTableList = this.listTableList.filter((item) => {
            return item.paramValue2 == this.listOpData[this.selectIndex].icon;
          });
        }
      }
    },
    ListChangeShow(state) {
      this.showListcoin = state;
    },
    MenuChoose(id) {
      this.menuchoose = id;
    },

    OnClickShowAddre() {
      this.payTypeShow = !this.payTypeShow;
    },
    AddressChangeShow(state) {
      this.showAddreChoose = state;
    },
    openPay(item) {
      this.payTypeShow = false;
      let addParams = {
        id: item.value,
      };
      this.methodId = item.value;
      c2cAxios
        .getPayConfig(addParams)
        .then((res) => {
          if (res.data.methodType == 2) {
            //虚拟币
            this.showAddreChoose = !this.showAddreChoose;
            this.payId = "";
          } else if (res.data.methodType == 1) {
            //银行卡
            this.$router.push(
              "/addressAddBank?type=1&methodId=" + this.methodId
            );
          } else {
            this.$message.error(this.$t("message.user.zanweikaifang"));
          }
        })
        .catch((err) => {
          console.error(err);
        });
    },

    getAddress() {
      this.lstData = [];
      // 获取收款方式，type:1表示银行卡
      c2cAxios
        .payMethodList({ type: 1 })
        .then((res) => {
          console.log(res,'res')
          for (let key in res.data) {
            let obj = {
              name: res.data[key],
              value: key,
            };
            this.lstData.push(obj);
          }
        })
        .catch((err) => {
          console.error(err);
        });
      // 获取用户的收款列表，type:1表示银行卡
      c2cAxios
        .userC2cPaymentMethodList({ type: 1 })
        .then((res) => {
          this.allList = res.data;
          this.listTableList = res.data.filter((item) => {
            return item.methodType == 1 || item.methodType == 2;
          });
        })
        .catch((err) => {
          console.error(err);
        });
    },
    OnClickPre() {
      this.$router.go(-1);
    },
    OnClickDel(id) {
      this.deleteId = id;
      this.dialogVisible = true;
    },
    hanldeDelete() {
      var addParams = {
        id: this.deleteId,
      };

      c2cAxios
        .delPayConfig(addParams)
        .then((res) => {
          if (res.code == "0") {
            this.dialogVisible = false;
            console.error(res);
            this.getAddress();
            this.$message({
              message: this.$t("message.user.shanchuchenggong"),
              type: "success",
            });
          }
        })
        .catch((err) => {
          console.error(err);
        });
    },
    handleEdit(item) {
      if (item.methodType == 2) {
        //虚拟币
        this.showAddreChoose = true;
        this.payId = item.uuid;
      }
      if (item.methodType == 1) {
        //银行卡
        this.$router.push("/addressAddBank?payId=" + item.uuid);
      }
    },
    closePop() {
      this.showAddreChoose = false;
    },
  },
};
</script>
<style scoped lang="css">
@import url("@/assets/css/commonTrade/global.css");

@import url("@/assets/css/wallet/addma.css");

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

.css-tq0shg {
  width: 1200px !important;
  margin: 0 auto;
}

.paytypebut {
  width: 250px;
  height: 40px;
  background: #eaecef !important;
}

.pay-top {
  display: flex;
  justify-content: space-between;
  padding: 15px 30px;
  align-items: center;
  background: #fafafa;
  font-size: 16px;
  font-weight: bold;
  position: relative;
}

.pay-top-line {
  position: absolute;
  height: 25px;
  border-radius: 10px;
  background: #4ba6eb;
  width: 5px;
  left: 16px;
  top: 14px;
}

.pay-type-item {
  border: 1px solid #e7e9eb;
  border-radius: 5px;
  margin-bottom: 40px;
}

.caozuo {
  display: flex;
}

.caozuo-item {
  margin-left: 20px;
  font-size: 14px;
  font-weight: initial;
  cursor: pointer;
}

.pay-bottom {
  padding: 15px 30px;
  display: flex;
}

.pay-bottom-item {
  min-width: 210px;
  word-break: break-all;

  word-wrap: break-word;

  white-space: pre-wrap;
}

.bottom-title {
  color: #727a89;
  font-size: 14px;
}

.bottom-text {
  color: #000000;
  font-size: 14px;
  margin-top: 20px;
}

.pay-type-select {
  position: absolute;
  width: 250px;
  top: 42px;
  background: #ffffff;
  box-shadow: 0px 0px 4px rgba(0, 0, 0, 0.2);
  z-index: 10;
}

.pay-type-select-item {
  height: 40px;
  padding: 0 30px;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.pay-type-select-item-line {
  width: 4px;
  height: 20px;
  background: #4ba6eb;
  border-radius: 6px;
  margin-right: 10px;
}

.pay-type-select-item:hover {
  background: #f5f5f5;
}

.el-button {
  border: 1px solid #dcdfe6;
}

:deep(.el-dialog__header) {
  border-bottom: none !important;
}

.dig-title {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  margin-top: 30px;
}

.dig-text {
  font-size: 14px;
  margin-top: 20px;
  text-align: center;
  margin-bottom: 30px;
}

.tip-img {
  width: 60px;
  height: 60px;
  margin: 0 auto;
  display: block;
}

.dialog-footer {
  display: flex;
}

.dialog-footer .but {
  width: 210px;
}
</style>
