<template>
  <!-- 邮箱注册页面 -->
  <div class="register">
    <!-- 顶部关闭/返回按钮 -->
    <div class="top" @click="router.go(-1)">
      <img src="../../assets/image/icon-close.png" alt="" />
    </div>

    <!-- 步骤指示器，当前为第1步 -->
    <Step :step="1"></Step>

    <!-- 页面标题 -->
    <div class="title textColor">{{ $t("register") }}</div>

    <!-- 注册方式标签栏：目前只保留邮箱注册 -->
    <div class="flex re-tab">
      <div class="textColor1 active">{{ $t("email") }}</div>
    </div>

    <!-- 邮箱输入框 -->
    <ExInput :label="$t('email')" :placeholderText="$t('entryEmail')" v-model="username" />

    <!-- 手机号输入框（带国际区号选择） -->
    <ExInput
      :label="$t('phoneNum')"
      :placeholderText="$t('entryPhone')"
      v-model="phone"
      :area="true"
      :dialCode="dialCode"
      @selectArea="onSelectArea"
      :icon="icon"
    />

    <!-- 登录密码输入框 -->
    <ExInput :label="$t('setPassword')" :placeholderText="$t('passwordTips')" v-model="password" typeText="password" />

    <!-- 确认密码输入框 -->
    <ExInput :label="$t('repassword')" :placeholderText="$t('surePassword')" v-model="repassword" typeText="password" />

    <!-- 资金密码输入框（6位数字） -->
    <ExInput :label="$t('setSafeword')" :placeholderText="$t('safewordTips')" v-model="safeword" typeText="password" />

    <!-- 邮箱验证码输入区（含发送验证码按钮和倒计时） -->
    <div class="inputCom">
      <p class="label textColor">{{ $t("验证码") }}</p>
      <div class="iptbox inputBackground">
        <input class="inputBackground textColor" type="text" :placeholder="$t('entryVerifyCode')" v-model="verifyCode" />
        <span @click="senCode">{{ $t("sendVerifyCode") }}
          <template v-if="time">({{ time }})s</template>
        </span>
      </div>
    </div>

    <!-- 邀请码输入框（选填） -->
    <ExInput :label="$t('invitCode')" :placeholderText="$t('entryInvitCode')" v-model="invitCode" :clearBtn="false" />

    <!-- 服务协议勾选 -->
    <div class="protocol textColor">
      <i @click="agreeProt">
        <img v-show="agree" src="../../assets/image/login/prot2.png" alt="" />
        <img v-show="!agree" src="../../assets/image/login/prot1.png" alt="" />
      </i>
      {{ $t("readAgree")
      }}<span class="colorMain" @click="router.push('/aboutUs?serviceTerm=1')">{{
  $t("serviceConf")
}}</span>
    </div>

    <!-- 注册提交按钮 -->
    <van-button class="w-full" style="margin-top: 10px" type="primary" @click="register">{{ $t("register") }}
    </van-button>

    <!-- 底部跳转：已有账号？去登录 -->
    <div class="noTips textColor">
      {{ $t("hasAccount")
      }}<span class="colorMain" @click="router.push('/login')"> {{ $t("goLogin") }}</span>
    </div>

    <!-- 国际区号选择弹窗组件 -->
    <nationality-list ref="controlChildRef" :title="$t('selectArea')" @getName="getName"></nationality-list>

    <!-- 滑块验证码组件（注册前的人机验证） -->
    <Vcode :imgs="[img1, img2]" :show="show" @success="onSuccess" :canvasHeight="200" @fail="onFail" @close="show = false"
      sliderText="" :successText="$t('vertifyPass')" :failText="$t('vertifuFail')" />

    <!-- 调试信息展示 -->
    <div>{{ msg }}</div>
  </div>
</template>

<script setup>
// ==================== 组件导入 ====================

// 自定义输入框组件
import ExInput from "@/components/ex-input/index.vue";
// 注册步骤组件
import Step from "./step.vue";
// 发送验证码API
import { _sendVerifyCode } from "@/service/login.api";
// 邮箱注册API
import { _bindEmailRegister } from "@/service/user.api.js";
// 用户状态管理Store
import { useUserStore } from "@/store/user";
// Store中的action类型常量
import { GET_USERINFO } from "@/store/types.store";
// 国际区号选择弹窗组件
import nationalityList from "../authentication/components/nationalityList.vue";
// 滑块验证码组件
import Vcode from "vue3-puzzle-vcode";
// 滑块验证用的图片素材
import img1 from "../../assets/image/slider/1.png";
import img2 from "../../assets/image/slider/2.png";
// 工具函数：从storage中读取数据
import { getStorage } from "@/utils/index";
// 国际化
import { useI18n } from "vue-i18n";
// 路由
import { useRouter } from "vue-router";
// Vue组合式API
import { ref, onMounted, reactive, onUnmounted } from "vue";
// Vant轻提示
import { showToast } from "vant";
// 全局Store实例
import store from "@/store/store";

// ==================== 组合式API初始化 ====================

const { t } = useI18n();
const router = useRouter();
const userStore = useUserStore();

// ==================== 响应式状态变量 ====================

// 滑块验证码是否显示
const show = ref(false);
// 调试消息
const msg = ref("");
// 验证码发送倒计时（秒）
const time = ref(0);
// 是否同意服务协议
const agree = ref(false);
// 邮箱地址（绑定为username）
const username = ref("");
// 手机号码（不含区号前缀）
const phone = ref("");
// 登录密码
const password = ref("");
// 确认密码
const repassword = ref("");
// 邮箱验证码
const verifyCode = ref("");
// 资金密码（6位数字）
const safeword = ref("");
// 国际区号（如86、852等）
const dialCode = ref(0);
// 区号对应的国旗图标
const icon = ref("");
// 计时器状态（使用reactive确保定时器可被清除）
const state = reactive({ timer: null });
// 邀请码
const invitCode = ref("");

// ==================== 生命周期钩子 ====================

// 组件挂载时：读取缓存中的邀请码，清除遗留的计时器
onMounted(() => {
  const usercode = getStorage("usercode");
  if (usercode) invitCode.value = usercode;
  clearInterval(state.timer);
  state.timer = null;
});

// 组件卸载时：清除计时器，防止内存泄漏
onUnmounted(() => {
  clearInterval(state.timer);
  state.timer = null;
});

// ==================== 方法 ====================

/**
 * 发送邮箱验证码
 * 校验邮箱格式后调用API发送验证码，并启动60秒倒计时
 */
const senCode = () => {
  if (time.value > 0) return;
  const match = username.value.search(/@/);
  if (username.value == "" || match == -1) {
    showToast(t("entryCorrectEmail"));
    return;
  }

  _sendVerifyCode({ target: username.value })
    .then(() => {
      time.value = 60;
      state.timer = setInterval(() => {
        if (time.value > 0) {
          time.value = time.value - 1;
        } else {
          time.value = 0;
          clearInterval(state.timer);
          state.timer = null;
        }
      }, 1000);
    })
    .catch((error) => {
      showToast(t(error.msg));
    });
};

/**
 * 滑块验证码验证成功回调
 * 验证通过后立即调用注册接口
 */
const onSuccess = () => {
  registerApi();
  show.value = false;
};

/**
 * 滑块验证码验证失败回调
 */
const onFail = () => {
  msg.value = "";
};

// 国际区号选择弹窗的ref引用
const controlChildRef = ref(null);

/**
 * 打开国际区号选择弹窗
 */
const onSelectArea = () => {
  controlChildRef.value.open();
};

/**
 * 区号选择回调
 * @param {Object} params - 选中的国家/地区信息
 * @param {string} params.code - 国旗图标代码
 * @param {number} params.dialCode - 国际区号
 */
const getName = (params) => {
  icon.value = params.code;
  dialCode.value = params.dialCode;
};

/**
 * 切换服务协议同意状态
 */
const agreeProt = () => {
  agree.value = !agree.value;
};

/**
 * 注册表单校验
 * 依次校验：邮箱格式 -> 手机号格式 -> 密码非空/长度 -> 确认密码一致
 *         -> 资金密码非空 -> 验证码长度 -> 协议同意
 * 全部通过后显示滑块验证码
 */
const register = () => {
  const match = username.value.search(/@/);
  if (username.value == "" || match == -1) {
    showToast(t("entryCorrectEmail"));
    return;
  }
  if (!/(^[1-9]\d*$)/.test(phone.value)) {
    showToast(t("entryPhone"));
    return;
  }
  if (password.value == "") {
    showToast(t("entryPassword"));
    return;
  }
  if (password.value.length < 6) {
    showToast(t("passwordTips"));
    return;
  }
  if (repassword.value !== password.value) {
    showToast(t("noSamePassword"));
    return;
  }
  if (safeword.value == "") {
    showToast(t("safewordTips"));
    return;
  }
  if (verifyCode.value.length < 6) {
    showToast(t("entryVerifyTips"));
    return;
  }
  if (!agree.value) {
    showToast(t("agreeServiceCond"));
    return;
  }
  show.value = true;
};

/**
 * 调用邮箱注册接口
 * 将邮箱、密码、手机号（区号+号码）、验证码、邀请码、资金密码提交到后端
 * 注册类型type固定为"2"（邮箱注册）
 * 成功后更新用户Store并跳转至身份认证页面
 */
const registerApi = () => {
  _bindEmailRegister({
    username: username.value,
    password: password.value,
    type: "2",
    verifcode: verifyCode.value,
    usercode: invitCode.value,
    safeword: safeword.value,
    phone: `${dialCode.value}${phone.value}`,
  }).then((res) => {
    userStore[GET_USERINFO](res);
    store.state.user.userInfo = res;
    router.push("/identity");
  });
};
</script>

<style lang="scss" scoped>
/* ==================== 页面整体布局 ==================== */
.register {
  width: 100%;
  box-sizing: border-box;
  padding: 16px;
  font-size: 13px;
}

/* 顶部返回按钮 */
.top {
  padding-left: 9px;
  padding-top: 9px;

  img {
    width: 18px;
    height: 18px;
  }
}

/* 页面标题样式 */
.title {
  font-weight: 700;
  font-size: 26px;
  margin-top: 27px;
  margin-bottom: 33px;
}

/* 注册方式标签栏 */
.re-tab {
  margin-bottom: 22px;

  div {
    padding: 0 18px;
    height: 34px;
    line-height: 34px;
    text-align: center;
    border-radius: 4px;
    margin-right: 10px;
  }

  .active {
    background: $US_tabActice_background;
    color: $color_main;
  }
}

/* 底部跳转链接区 */
.noTips {
  margin-top: 24px;
}

/* 服务协议勾选区 */
.protocol {
  display: flex;
  align-items: center;
  height: 15px;

  i {
    width: 15px;
    height: 15px;
    margin-right: 9px;

    img {
      width: 100%;
      height: 100%;
    }
  }
}

/* 验证码输入区域（输入框 + 发送按钮） */
.iptbox {
  height: 44px;
  margin-top: 8px;
  margin-bottom: 18px;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 6px;

  input {
    flex: 1;
    height: 100%;
    border: none;
  }

  span {
    color: $color_main;
  }
}
</style>
