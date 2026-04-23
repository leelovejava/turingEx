<template>
  <div class="right-view">
    <div class="right-header">
      <div class="account-view">
        <div class="account-left-header light-background-color">
          <div class="font-size28 font-bold">
            {{ $t("message.user.zhanghuanquan") }}
          </div>
          <!-- 验证方式 -->
          <div class="account-icon-view">
            <verify-img :verrify="googleverif" label="gugeyanzhengqi" />
            <verify-img :verrify="emailverif" label="youxiangrenzheng" />
            <verify-img :verrify="identityverif" label="shenfenrenzheng" />
            <verify-img :verrify="advancedverif" label="gaojirenzheng" />
          </div>
        </div>
        <div class="header-icon-view">
          <img
            src="@/assets/myImages/icon-image/account-security/security-icon.png"
            width="122"
            height="100"
          />
        </div>
      </div>
    </div>
    <!-- 多重验证-->
    <div class="account-content-box">
      <div class="margin-left10 font-size20 margin-top-bottom10">
        {{ $t("message.user.duochongrenzheng") }}
      </div>
      <!-- 谷歌验证 -->
      <div class="account-list">
        <div class="flex-row-center">
          <img
            class="list-left-img"
            src="@/assets/myImages/icon-image/account-security/1.png"
            alt=""
          />
          <div class="margin-left10">
            <div>
              {{ $t("message.user.gugeyanzhengqi") }}（{{
                $t("message.user.tuijian")
              }}）
            </div>
            <div class="linght-grey-color margin-top5">
              {{ $t("message.user.baohunindejiaoyihezhanghuanquan") }}
            </div>
          </div>
        </div>
        <div class="flex-row-center list-icon justify-flex-end">
          <verify-img
            :verrify="googleverif"
            :label="googleverif ? 'qiyong' : 'weibangding'"
          />
        </div>
        <div class="justify-flex-end">
          <button
            v-if="!googleverif"
            class="light-grey-button"
            @click="() => handleModalStatus(true, 'isShowGoogle')"
          >
            {{ $t("message.user.bangding") }}
          </button>
          <button v-else class="light-grey-button" @click="unbind(1)">
            {{ $t("message.user.chongzhi2") }}
          </button>
        </div>
      </div>
      <!-- 邮箱验证 -->
      <div class="account-list">
        <div class="flex-row-center">
          <img
            class="list-left-img"
            src="@/assets/myImages/icon-image/account-security/3.png"
            alt=""
          />
          <div class="margin-left10">
            <div>{{ $t("message.user.youxiangrenzheng") }}</div>
            <div class="linght-grey-color margin-top5">
              {{ $t("message.user.baohunindejiaoyihezhanghuanquan") }}
            </div>
          </div>
        </div>
        <div class="flex-row-center list-icon justify-flex-end">
          <verify-img :verrify="emailverif" />
          <div v-if="emailverif">{{ email }}</div>
          <div v-else>{{ $t("message.user.weibangding") }}</div>
        </div>
        <div class="justify-flex-end">
          <button
            v-if="!emailverif"
            class="light-grey-button"
            @click="() => handleModalStatus(true, 'isShowEmail')"
          >
            {{ $t("message.user.bangding") }}
          </button>
          <button v-else class="light-grey-button" @click="unbind(3)">
            {{ $t("message.user.chongzhi2") }}
          </button>
        </div>
      </div>
      <!-- 身份认证 -->
      <div class="account-list">
        <div class="flex-row-center">
          <img
            class="list-left-img"
            src="@/assets/myImages/icon-image/account-security/4.png"
            alt=""
          />
          <div class="margin-left10">
            <div>{{ $t("message.user.shenfenrenzheng") }}</div>
            <div class="linght-grey-color margin-top5">
              {{ $t("message.user.baohunindejiaoyihezhanghuanquan")
              }}{{ kyc_status }}
            </div>
          </div>
        </div>
        <div class="flex-row-center list-icon justify-flex-end">
          <verify-img :verrify="identityverif" />
          <div v-if="identifyStatus == 3" class="red">
            {{ $t("message.user.shenheweitongguo")
            }}{{ identifyMsg ? `-${identifyMsg}` : "" }}
          </div>
          <div v-else-if="identifyStatus == 1" class="yellow">
            {{ $t("message.user.shenhezhong") }}
          </div>
          <div v-else>
            {{ $t(`message.user.${statusMap[identifyStatus]}`) }}
          </div>
        </div>
        <div class="justify-flex-end">
          <button
            class="light-grey-button"
            @click="() => handleModalStatus(true, 'isShowIdentity')"
          >
            {{
              !identityverif
                ? $t("message.user.bangding")
                : $t("message.user.chakan")
            }}
          </button>
        </div>
      </div>
      <!-- 高级认证 -->
      <div class="account-list margin-bottom30">
        <div class="flex-row-center">
          <img
            class="list-left-img"
            src="@/assets/myImages/icon-image/account-security/5.png"
            alt=""
          />
          <div class="margin-left10">
            <div>{{ $t("message.user.gaojirenzheng") }}</div>
            <div class="linght-grey-color margin-top5">
              {{ $t("message.user.baohunindejiaoyihezhanghuanquan") }}
            </div>
          </div>
        </div>
        <div class="flex-row-center list-icon justify-flex-end">
          <verify-img :verrify="advancedverif" />
          <div v-if="highStatus == 3" class="red">
            {{ $t("message.user.shenheweitongguo")
            }}{{ highMsg ? `-${highMsg}` : "" }}
          </div>
          <div v-else-if="highStatus == 1" class="yellow">
            {{ $t("message.user.shenhezhong") }}
          </div>
          <div v-else>
            {{ $t(`message.user.${statusMap[highStatus]}`) }}
          </div>
        </div>
        <div class="justify-flex-end">
          <button class="light-grey-button" @click="openAdvance">
            {{
              !advancedverif
                ? $t("message.user.bangding")
                : $t("message.user.chakan")
            }}
          </button>
        </div>
      </div>
    </div>
    <!-- 账号设置-->
    <div class="account-content-box">
      <div class="margin-left10 font-size20 margin-top-bottom10">
        {{ $t("message.user.zhanghaoshezhi") }}
      </div>
      <!-- 登录密码 -->
      <div class="account-list">
        <div class="flex-row-center">
          <img
            class="list-left-img"
            src="@/assets/myImages/icon-image/account-security/6.png"
            alt=""
          />
          <div class="margin-left10">
            <div>{{ $t("message.user.denglumima") }}</div>
            <div class="linght-grey-color margin-top5">
              {{ $t("message.user.yongyuguanlininzhanghudedenglumima") }}
            </div>
          </div>
        </div>
        <div class="justify-flex-end">
          <button
            class="light-grey-button"
            @click="() => handleModalStatus(true, 'isShowLoginPassword')"
          >
            {{ $t("message.user.xiugai") }}
          </button>
        </div>
      </div>
      <!-- 资金密码 -->
      <div class="account-list">
        <div class="flex-row-center">
          <img
            class="list-left-img"
            src="@/assets/myImages/icon-image/account-security/7.png"
            alt=""
          />
          <div class="margin-left10">
            <div>{{ $t("message.user.zijinmima") }}</div>
            <div class="linght-grey-color margin-top5">
              {{ $t("message.user.yongyuguanlininzhanghudezijinmima") }}
            </div>
          </div>
        </div>
        <div class="justify-flex-end">
          <button
            class="light-grey-button"
            @click="() => handleModalStatus(true, 'isShowFund')"
          >
            {{ $t("message.user.xiugai") }}
          </button>
        </div>
      </div>
      <!-- 人工重置 -->
      <div class="account-list">
        <div class="flex-row-center">
          <img
            class="list-left-img"
            src="@/assets/myImages/icon-image/account-security/8.png"
            alt=""
          />
          <div class="margin-left10">
            <div>{{ $t("message.user.rengongchongzhi") }}</div>
            <div class="linght-grey-color margin-top5">
              {{ $t("message.user.lianxirengongkefuchongzhi") }}
            </div>
          </div>
        </div>
        <div class="flex-row-center list-icon justify-flex-end">
          <div class="red" v-if="manualStatus == 1">
            {{ $t("message.user.shenhezhong") }}
          </div>
          <div class="green" v-if="manualStatus == 2">
            {{ $t("message.user.shenhetongguo") }}
          </div>
          <div class="red" v-if="manualStatus == 3">
            {{ $t("message.user.shenheweitongguo")
            }}{{ manualMsg ? `-${manualMsg}` : "" }}
          </div>
        </div>
        <div class="justify-flex-end">
          <button class="light-grey-button" @click="unbind(0)">
            {{ $t("message.user.chongzhi2") }}
          </button>
        </div>
      </div>
    </div>
    <!-- 弹窗 -->
    <email-bind
      v-if="isShowEmail"
      :isShowEmail="isShowEmail"
      @changeModalShow="handleModalStatus"
    ></email-bind>

    <google-bind
      v-if="isShowGoogle"
      :isShowGoogle="isShowGoogle"
      @changeModalShow="handleModalStatus"
    ></google-bind>
    <login-password
      v-if="isShowLoginPassword"
      :isShowLoginPassword="isShowLoginPassword"
      @changeModalShow="handleModalStatus"
    ></login-password>
    <funds-password
      v-if="isShowFund"
      :isShowFund="isShowFund"
      @changeModalShow="handleModalStatus"
    ></funds-password>
    <identity-authenty
      v-if="isShowIdentity"
      :isShowIdentity="isShowIdentity"
      @changeModalShow="handleModalStatus"
    ></identity-authenty>
    <advance-certify
      v-if="isShowAdvance"
      :isShowAdvance="isShowAdvance"
      @changeModalShow="handleModalStatus"
    ></advance-certify>
    <manual-reset
      v-if="isShowManual"
      :isShowManual="isShowManual"
      :status="manualStatus"
      :selectType="radioType"
      @changeModalShow="handleModalStatus"
    ></manual-reset>
  </div>
</template>
<script>
import AdvanceCertify from "./components/advanceCertify.vue";
import emailBind from "./components/emailBind.vue";
import FundsPassword from "./components/fundsPassword.vue";
import GoogleBind from "./components/googleBind.vue";
import IdentityAuthenty from "./components/identityAuthenty.vue";
import LoginPassword from "./components/loginPassword.vue";
import ManualReset from "./components/manualReset.vue";
import PhoneBind from "./components/phoneBind.vue";
import verifyImg from "./components/verifyImg.vue";
import Axios2 from "@/api/my.js";
import { ElMessage } from "element-plus";
export default {
  components: {
    emailBind,
    PhoneBind,
    GoogleBind,
    LoginPassword,
    FundsPassword,
    IdentityAuthenty,
    AdvanceCertify,
    ManualReset,
    verifyImg,
  },
  data() {
    return {
      //认证状态，false为未绑定，true为绑定
      googleverif: false,
      emailverif: false,
      phoneverif: false,
      identityverif: false,
      advancedverif: false,
      //弹窗
      isShowEmail: false,
      isShowGoogle: false,
      isShowLoginPassword: false,
      isShowFund: false,
      isShowIdentity: false,
      isShowAdvance: false,
      isShowManual: false,
      email: "",
      phone: "",
      showEmail: "",
      identifyStatus: "", //身份认证状态

      identifyMsg: "", //身份认证驳回原因
      highStatus: "", //高级认证状态
      highMsg: "", //高级认证驳回原因
      // ("0已申请未审核=未绑定 ，1审核中 ，2 审核通过,3审核未通过")
      statusMap: [
        "weibangding",
        "shenhezhong",
        "yirenzheng",
        "shenheweitongguo",
      ],
      manualStatus: null, //人工重置的审核状态
      manualMsg: "", //人工重置驳回原因
      radioType: 0, //人工重置单选框选中的值
      kyc_status: "",
    };
  },
  mounted() {
    // if(this.$store.state.token){
    this.getUserInfo();
    this.getManualInfo();
    this.getIdentifyInfo();
    this.getHighInfo();
    // }
  },
  methods: {
    // 获取用户信息
    getUserInfo() {
      Axios2.getUserInfo().then((res) => {
        const {
          phone,
          email,
          googleverif,
          emailverif,
          phoneverif,
          identityverif,
          advancedverif,
          kyc_status,
        } = res.data || {};
        this.googleverif = googleverif;
        this.emailverif = emailverif;
        this.phoneverif = phoneverif;
        this.identityverif = identityverif;
        this.advancedverif = advancedverif;
        this.kyc_status = kyc_status;

        //邮箱和手机事情打*显示的
        if (email) {
          let arremail = email.split("@");
          this.email = email.substring(0, 3) + "*****" + "@" + arremail[1];
        }

        if (phone) {
          this.phone = phone.replace(phone.substr(3, 4), "****");
        }
      });
    },
    // 获取人工审核的状态
    getManualInfo() {
      Axios2.getSafewordApply({}).then((res) => {
        if (res.data.length > 0) {
          this.manualStatus = res.data[0].status;
          this.manualMsg = res.data[0].msg;
        }
      });
    },
    // 获取身份认证的状态
    getIdentifyInfo() {
      Axios2.getIdentify({}).then((res) => {
        this.identifyStatus = res.data.status;
        this.identifyMsg = res.data.msg;
      });
    },
    // 获取高级认证，测试要加的，神烦
    getHighInfo() {
      Axios2.getHighIdentify().then((res) => {
        this.highStatus = res.data.status;
        this.highMsg = res.data.msg;
      });
    },
    // 弹窗控制
    handleModalStatus(status, type) {
      this[type] = status;
    },

    //高级认证
    openAdvance() {
      if (!this.identityverif) {
        ElMessage.warning(
          this.$t("message.home.shenfenrenzhenghoujinxinggaojirenzheng")
        );
        return;
      }
      this.isShowAdvance = true;
    },

    //重置谷歌，邮箱和人工重置功能
    unbind(type) {
      if (!this.identityverif) {
        ElMessage.warning(
          this.$t("message.home.weishimingrenzhnegqingxainshimingrenzheng")
        );
        return;
      }
      this.getManualInfo();
      this.radioType = type; //0 人工重置，1.谷歌重置 3.邮箱重置
      this.isShowManual = true;
    },
  },
};
</script>
<style scoped>
/* 头部样式 */
.account-view {
  display: flex;
}

.account-view > div {
  flex: 1;
}

.account-left-header {
  display: flex;
  flex-direction: column;
  padding: 25px 50px;
  min-width: 1000px;
}

.account-left-header > div {
  flex: 1;
}

.account-icon-view {
  display: flex;
  margin-top: 15px;
}

.account-icon-view > div {
  flex-direction: row;
  flex: 1;
}

.account-icon-view > div > img,
.list-icon > img {
  width: 25px;
  height: 25px;
  display: inline-block;
  margin-right: 10px;
}

.header-icon-view {
  display: flex;
  justify-content: flex-end;
  padding-right: 50px;
  padding-top: 10px;
}

/* 内容块 */
.account-list {
  display: flex;
  align-items: center;
  padding: 24px 0;
  border-bottom: 1px solid #e5e5e5;
}

.account-list > div {
  flex: 1;
  display: flex;
}

.list-img {
  display: inline-block;
  width: 25px;
  height: 25px;
}

.list-left-img {
  display: inline-block;
  width: 24px;
  height: 24px;
}

.account-content-box {
  margin: 20px 50px;
}

.margin-top5 {
  margin-top: 5px;
}
</style>
