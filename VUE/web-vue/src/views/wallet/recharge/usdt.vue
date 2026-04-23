<!-- usdt充值 -->
<template>
  <div id="__APP">
    <main class="css-1wr4jig">
      <main class="css-xry4yv">
        <div class="css-1ay57iv">
          <!-- 头部 -->
          <div class="css-8hzjvg">
            <div class="css-1eklhqk">
              <div class="css-190uhut">
                <div class="css-11y6cix">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 24 24"
                    fill="none"
                    class="css-146agw4"
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
                    {{ $t("message.user.shuzihuobichongzhi") }}
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- 内容 -->
          <div class="css-1dihobw">
            <div class="css-1eklhqk">
              <div class="css-tu2ioc">
                <!-- form -->
                <div class="css-1ekpz1z mr-126">
                  <!-- 选择币种 -->
                  <div class="css-av4be8">
                    <div class="css-1x9w05y">
                      {{ $t("message.user.xuanzebizhong") }}
                    </div>

                    <div class="css-pnaw5u">
                      <!-- 右边 -->
                      <div class="css-16vu25q">
                        <div class="css-7ng27">
                          {{ $t("message.user.bizhong2") }}
                        </div>
                        <!-- 下拉框 -->
                        <div
                          class="css-1dozx3h"
                          @click="() => handleShowCoinList(true)"
                        >
                          <div class="css-gg0wwx">
                            <img
                              :src="handleSymbolImg(coinList[coinIndex].icon)"
                              width="24"
                              height="24"
                            />
                            <div class="css-15k81fw">
                              <div class="css-4cffwv">
                                <span class="css-1jq4ozh">{{
                                  coinList[coinIndex].name
                                }}</span>
                              </div>
                            </div>
                          </div>
                          <el-icon class="cursor-pointer"
                            ><CaretBottom
                          /></el-icon>
                        </div>
                      </div>
                    </div>
                  </div>
                  <!-- 充值地址 -->
                  <div class="css-1oi6dii">
                    <div class="css-1x9w05y">
                      {{ $t("message.user.chongzhidizhi") }}
                    </div>
                    <div class="css-pnaw5u">
                      <!-- 转账网络 -->
                      <div class="css-6hm6tl">
                        <div class="css-16vu25q">
                          <div class="css-7ng27">
                            {{ $t("message.user.zhuanzhangwangluo") }}
                          </div>
                          <div class="css-1dozx3h">
                            <div
                              class="css-1pysja1"
                              @click="() => handleShowNetList(true)"
                            >
                              <div class="css-151whd6">
                                <div class="css-1c82c04">
                                  {{ netList[netIndex]?.name.toUpperCase() }}
                                </div>
                              </div>
                            </div>
                            <el-icon class="cursor-pointer"
                              ><CaretBottom
                            /></el-icon>
                          </div>
                        </div>

                        <!-- 提示信息 -->
                        <div>
                          <div class="css-15owl46">
                            <div
                              :class="
                                m_showCode ? 'css-cks8qc' : 'css-cks8qcnone'
                              "
                              @click="handleShowCode"
                            >
                              <img
                                :src="this.imgURl"
                                style="height: 120px; width: 120px"
                              />
                              <div>
                                {{ $t("message.user.shiyongjiaoyiping") }}
                              </div>
                            </div>
                            <!-- 地址 -->
                            <div class="css-6hm6tl my-24">
                              <div>
                                {{ $t("message.user.dizhi") }}
                              </div>
                              <div class="css-1cgf63r flex">
                                <input
                                  type="text"
                                  name="FirstName"
                                  v-model="address"
                                  ref="copytxt"
                                  readonly
                                  class="css-vurnku noborder"
                                  style="width: 320px"
                                />
                                <!-- 复制icon -->
                                <div class="css-c1olmu">
                                  <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    class="css-kmjxx1"
                                    @click="copy"
                                  >
                                    <path
                                      fill-rule="evenodd"
                                      clip-rule="evenodd"
                                      d="M9 3h11v13h-3V6H9V3zM4 8v13h11V8.02L4 8z"
                                      fill="currentColor"
                                    ></path>
                                  </svg>
                                </div>
                                <!-- 二维码icon -->
                                <div class="css-c1olmu">
                                  <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    class="css-1en91pv cursor-pointer"
                                    @click="handleShowCode"
                                  >
                                    <path
                                      fill-rule="evenodd"
                                      clip-rule="evenodd"
                                      d="M4 4h7v7H4V4zm0 9h7v7H4v-7zm11 0h-2v4h4v-2h3v-2h-4v2h-1v-2zm5 3h-2v2h-2v2h4v-4zm-5 2h-2v2h2v-2zM13 4h7v7h-7V4zM8.5 6.5h-2v2h2v-2zm-2 9h2v2h-2v-2zm11-9h-2v2h2v-2z"
                                      fill="currentColor"
                                    ></path>
                                  </svg>
                                </div>
                              </div>
                            </div>
                            <!--  -->
                            <div class="my-24">
                              <!-- 第一排 -->
                              <div class="css-4cffwv">
                                <!-- 预计到账 -->
                                <div class="css-15slc0p">
                                  <div class="css-1hufmyh">
                                    {{ $t("message.user.yujidaozhang") }}
                                  </div>
                                  <div class="css-s3l2jm">
                                    <span class="css-197ji3y">1</span>
                                    {{ $t("message.user.ciwangluoqueren") }}
                                  </div>
                                </div>
                                <!-- 预计解锁 -->
                                <div class="css-15slc0p">
                                  <div class="css-1hufmyh">
                                    {{ $t("message.user.yujijiesuo") }}
                                  </div>
                                  <div class="css-s3l2jm">
                                    <span class="css-197ji3y">1</span>
                                    {{ $t("message.user.ciwangluoqueren2") }}
                                  </div>
                                </div>
                              </div>
                              <!-- 第二排 -->
                              <div class="css-4cffwv mt-20">
                                <!-- 最小充币数量 -->
                                <div class="css-15slc0p">
                                  <div class="css-1hufmyh">
                                    {{
                                      $t("message.user.zuixiaobizhongshuliang")
                                    }}
                                  </div>
                                  <div class="css-s3l2jm">
                                    <span class="css-197ji3y">1</span>
                                    USDT
                                  </div>
                                </div>
                                <!-- 充币账户 -->
                                <div class="css-15slc0p">
                                  <div class="css-1hufmyh">
                                    {{ $t("message.user.ti1") }}
                                  </div>
                                  <div class="css-s3l2jm">
                                    {{ $t("message.user.ti3") }}
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                          <!-- 此地址只可接收 -->
                          <ul class="css-1mky3oo my-24">
                            <li>
                              {{ $t("message.user.ti4") }}&nbsp;
                              <span class="css-1s8b8jd">{{
                                coin?.toUpperCase()
                              }}</span>
                            </li>
                            <li>
                              {{ $t("message.user.ti5") }}&nbsp;
                              <span class="css-1s8b8jd">{{
                                blockchain_name?.toUpperCase()
                              }}</span>
                            </li>
                          </ul>
                        </div>
                      </div>
                    </div>
                  </div>
                  <!-- 充币数量 -->
                  <div class="css-1oi6dii">
                    <div class="css-1x9w05y">
                      {{ $t("message.user.ti6") }}
                    </div>
                    <div>
                      <!-- 数量 -->
                      <div>
                        <div class="css-7ng27">
                          {{ $t("message.user.shuliang") }}
                        </div>
                        <el-input
                          style="height: 48px"
                          :placeholder="$t('message.user.ti7')"
                          class="css-1c82c04"
                          type="number"
                          v-model="quantity"
                        ></el-input>
                      </div>
                      <!-- 上传凭证 -->
                      <div class="mt-20">
                        <div class="css-7ng27">
                          {{ $t("message.home.shangchuanfukuanpingzheng") }}
                        </div>
                        <el-upload
                          class="avatar-uploader"
                          :headers="uploadHeader"
                          :action="`${nowUrl}/api/api/uploadFile`"
                          accept=".jpg,.jpeg,.png,.gif.JPG,.JPEG,.PNG,.GIF"
                          :show-file-list="false"
                          :on-success="handelDoucumentsFront"
                          :on-error="onErrorUpload"
                          :before-upload="beforeAvatarUpload"
                        >
                          <img v-if="oneImg" :src="oneImg" class="avatar" />
                          <el-icon v-else class="avatar-uploader-icon"
                            ><Plus
                          /></el-icon>
                        </el-upload>
                        <div class="uploader-tip">
                          {{ $t("message.user.dianjishangchuan") }}
                        </div>
                      </div>
                      <!-- 充币按钮 -->
                      <div class="mt-20">
                        <el-button
                          type="primary"
                          style="width: 484px; height: 52px"
                          @click="handleRecharge"
                        >
                          {{ $t("message.user.ti8") }}</el-button
                        >
                      </div>
                    </div>
                  </div>
                </div>
                <!-- 充值未到账 -->
                <div class="css-107bu5v">
                  <div class="css-vurnku">
                    <div class="css-4krk71">{{ $t("message.user.ti9") }}？</div>
                    <div class="css-17bmjki recharge-question-box">
                      <div>{{ $t("message.user.ti10") }}。</div>
                      <div class="recharge-question-text">
                        <div>* {{ $t("message.user.ti11") }}</div>
                        <div>* {{ $t("message.user.ti12") }}Tag/Memo</div>
                        <div>* {{ $t("message.user.ti13") }}</div>
                      </div>
                    </div>
                  </div>
                </div>
                <!-- 近期充值记录 -->
                <div class="css-wmh5d4">
                  <div class="css-vurnku">
                    <div class="css-1k1bx0u" v-if="recordData.length > 0">
                      {{ $t("message.user.ti15") }}
                    </div>
                    <!-- 列表 -->
                    <div
                      class="css-r27jgl"
                      v-for="item in recordData"
                      :key="item.order_no"
                    >
                      <div class="css-1pysja1">
                        <!-- 数量 币种 状态 -->
                        <div class="css-60y5z1">
                          <div class="css-10nf7hq">
                            <div class="css-10nf7hq">
                              <img
                                class="css-1plro70"
                                :src="handleSymbolImg(item.coin)"
                              />
                              <div class="css-1iq5991">
                                {{ item.amount }} {{ item.coin }}
                              </div>
                            </div>
                            <div class="css-vurnku">
                              <div class="css-1f9551p">
                                <div
                                  class="css-jyc5uj"
                                  style="
                                    background-color: rgb(201, 148, 0);
                                    color: white;
                                  "
                                  v-if="item.status == 0"
                                >
                                  {{ $t("message.user.ti17") }}
                                </div>
                                <div class="css-jyc5uj" v-if="item.status == 1">
                                  {{ $t("message.user.ti18") }}
                                </div>
                                <div style="display: flex">
                                  <div
                                    class="css-jyc5uj"
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
                            </div>
                          </div>
                        </div>
                        <!-- 时间 地址 -->
                        <div class="css-kzcb6r">
                          <div class="css-cp92rp">
                            <div class="css-ig082u">
                              {{ item.createTime }}
                            </div>
                          </div>
                          <div class="css-cp92rp">
                            <div class="css-190cj18">
                              {{ $t("message.user.ti19") }}
                            </div>
                            <div class="css-ig082u">
                              {{ item.coin_blockchain }}
                            </div>
                          </div>
                          <div class="css-cp92rp">
                            <div class="css-190cj18">
                              {{ $t("message.user.ti20") }}
                            </div>
                            <div class="css-ig082u">
                              {{ item.from }}
                            </div>
                          </div>
                          <div class="css-cp92rp">
                            <div class="css-190cj18">TxID</div>
                            <div class="css-ig082u">
                              <div class="css-1f9551p">
                                <div class="css-vurnku">
                                  {{ item.tx ? item.tx : "--" }}
                                </div>
                              </div>
                            </div>
                          </div>
                          <div class="css-cp92rp">
                            <div class="css-190cj18">
                              {{ $t("message.user.ti21") }}
                            </div>
                            <div class="css-ig082u">
                              {{ $t("message.user.ti22") }}
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <!-- 查看全部跳转到钱包历史记录页面 -->
                    <div class="margin-top20 mouse-cursor">
                      <a
                        v-if="recordData.length > 0"
                        class="css-1911t1t"
                        @click="goRouter('/order/walletHistory')"
                      >
                        {{ $t("message.user.ti23") }}</a
                      >
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </main>
    <list-coins
      :inTitle="coinTitle"
      :inIsShowPop="showCoinList"
      :callbackState="handleShowCoinList"
      :inLstOption="coinList"
      :callbackChoose="cbCoinChoose"
    ></list-coins>
    <list-coins
      :inTitle="netTitle"
      :inIsShowPop="showNetList"
      :callbackState="handleShowNetList"
      :noteTitle="noteTitle"
      :inLstOption="netList"
      :callbackChoose="cbNetChoose"
    ></list-coins>
  </div>
</template>
<script>
import Axios2 from "@/api/wallet.js";
import { handleSymbolImg } from "@/utils";
import { signatureGenerate } from "@/utils/signatureUtil";
import ListCoins from "@/components/my/listCoins.vue";
import { Plus, CaretBottom, ArrowLeftBold } from "@element-plus/icons-vue";
import { nowUrl } from "@/utils";
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";
export default {
  components: { Plus, CaretBottom, ListCoins },
  data() {
    return {
      showCoinList: false,
      coinTitle: this.$t("message.user.ti24"),
      coinList: [
        { icon: "usdt", name: "USDT" },
        { icon: "btc", name: "BTC" },
        { icon: "eth", name: "ETH" },
        // { icon: "usdc", name: "USDC" },
      ],
      coinIndex: 0,
      showNetList: false,
      netTitle: this.$t("message.user.ti25"),
      noteTitle: this.$t("message.user.ti26"),
      netList: [
        { icon: "usdt", name: "Trc20", code: "USDT" },
        { icon: "btc", name: "Trc201", code: "USDT" },
        { icon: "eth", name: "Trc202", code: "USDT" },
      ],
      netIndex: 0,
      blockchain_name: "", //转账网络名称
      coin: "USDT",
      address: "",

      blockchain: {},
      imgURl: "",
      m_showCode: false,
      quantity: "", //充币数量
      session_token: "", //用于充币提交接口需传的参数
      recordData: [], //充值记录
      rechargeImg: "", //上传接口的图片
      nowUrl,
      oneImg: "", //上传后展示的图片
      uploadHeader: {
        tissuePaper: "",
        sign: "",
        systemRandom: "",
      },
    };
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  mounted() {
    if (this.existToken) {
      this.getAddress();
      this.getSessionToken();
      this.getRechargeRecord();
    }
  },
  methods: {
    handleSymbolImg,
    //上传付款凭证
    handelDoucumentsFront(res, file) {
      const { path, httpUrl } = res.data;
      this.oneImg = httpUrl;
      this.rechargeImg = path;
    },
    onErrorUpload() {
      this.$message.warning(this.$t("message.user.shangchuan3"));
    },
    beforeAvatarUpload(file) {
      const { timestamp, signature, systemRandom } = signatureGenerate();
      this.uploadHeader.tissuePaper = timestamp;
      this.uploadHeader.sign = signature;
      this.uploadHeader.systemRandom = systemRandom;
      let types = [
        "image/jpeg",
        "image/jpg",
        "image/gif",
        "image/bmp",
        "image/png",
      ];
      const isImage = types.includes(file.type);
      const isJPG = file.type === "image/jpeg";
      const isLt5M = file.size / 1024 / 1024 < 5;

      if (!isImage) {
        this.$message.error(this.$t("message.user.shangchuan1"));
      }
      if (!isLt5M) {
        this.$message.error(this.$t("message.user.shangchuan2"));
      }
      return isImage && isLt5M;
    },
    handleShowCode() {
      this.m_showCode = !this.m_showCode;
    },
    // 选中币种
    cbCoinChoose(id) {
      this.coinIndex = id;
      this.showCoinList = false;
      this.coin = this.coinList[id].name;
      this.getAddress();
    },

    handleShowCoinList(state) {
      this.showCoinList = state;
    },

    cbNetChoose(id) {
      this.netIndex = id;
      this.address = this.blockchain[id].address;
      this.blockchain_name = this.blockchain[id].blockchain_name;
      this.showNetList = false;
    },

    handleShowNetList(state) {
      this.showNetList = state;
    },

    //获取sessionToken
    getSessionToken() {
      Axios2.getUSDRechargeToken().then((res) => {
        this.session_token = res.data.session_token;
      });
    },
    // 获取币种的网络
    getAddress() {
      Axios2.getChainName({
        coin: this.coin,
      }).then((res) => {
        this.blockchain = res.data;
        this.netList = [];
        for (var i in res.data) {
          this.netList.push({
            icon: res.data[i].coin,
            name: res.data[i].blockchain_name,
          });
        }
        // 默认为第一个链
        this.netIndex = 0;
        this.address = this.blockchain[0].address;
        this.imgURl = this.blockchain[0].imgStr;
        this.blockchain_name = this.blockchain[0].blockchain_name;
        this.coin = this.blockchain[0].coin;
      });
    },
    copy() {
      var input = this.$refs["copytxt"];
      input.select();
      document.execCommand("Copy"); // 执行浏览器复制命令
      this.$message({
        message: this.$t("message.user.fuzhichenggong"),
        type: "success",
      });
    },
    //充币
    handleRecharge() {
      if (this.quantity && this.quantity > 0) {
        Axios2.rechargeApply({
          session_token: this.session_token,
          amount: this.quantity,
          from: this.address,
          blockchain_name: this.blockchain_name,
          coin: this.coin,
          channel_address: this.address,
          tx: "",
          img: this.rechargeImg,
        })
          .then((res) => {
            if (res.code == "0") {
              this.$message.success(this.$t("message.user.ti27"));
              this.quantity = "";
              this.rechargeImg = "";
              this.oneImg = "";
              this.getRechargeRecord();
            }
          })
          .finally(() => {
            this.getSessionToken();
          });
      } else {
        this.$message.error(this.$t("message.user.xian33"));
      }
    },
    OnClickPre() {
      this.$router.go(-1);
    },
    //充币记录
    getRechargeRecord() {
      Axios2.getCrypotRechargeList({
        page_no: 1,
      }).then((res) => {
        this.recordData = res.data.slice(0, 5);
      });
    },
    goRouter(parmas) {
      this.$router.push(parmas);
    },
  },
};
</script>

<style>
@import "@/assets/css/wallet/recharge.css";
.noborder {
  border: 0px;
}
.mt-20 {
  margin-top: 20px;
}
.my-24 {
  margin: 24px 0;
}
.my-16 {
  margin: 24px 0;
}

.mr-126 {
  margin-right: 126px;
}
.flex {
  display: flex;
}

.uploader-tip {
  font-size: 12px;
  color: #1a6ebd;
  font-weight: 500;
  margin-top: 8px;
}

.fw-500 {
  font-weight: 500;
}

.css-cks8qc {
  box-sizing: border-box;
  margin: 24px 0px 0px;
  min-width: 0px;
  display: flex;
  -webkit-box-align: center;
  align-items: center;
  flex-direction: column;
  padding-top: 24px;
  padding-bottom: 24px;
}
.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 110px;
  height: 110px;
  line-height: 110px;
  text-align: center;
}

.avatar {
  width: 110px;
  height: 110px;
  display: block;
}

.css-cks8qcnone {
  box-sizing: border-box;
  margin: 24px 0px 0px;
  min-width: 0px;
  display: none;
  -webkit-box-align: center;
  align-items: center;
  flex-direction: column;
  padding-top: 24px;
  padding-bottom: 24px;
}
</style>
