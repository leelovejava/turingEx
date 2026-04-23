<!-- 图片验证组件 -->
<template>
  <el-dialog
    :title="t('message.home.tupianyanzheng')"
    v-model="isShow"
    width="380px"
    :before-close="handleClose"
    center
  >
    <slide-verify
      ref="slideblockRef"
      @again="onAgain"
      @fulfilled="onFulfilled"
      @success="onSuccess"
      @fail="onFail"
      @refresh="onRefresh"
      :slider-text="text"
      :accuracy="accuracy"
    ></slide-verify>
  </el-dialog>
</template>

<script setup>
import Axios from "@/api/login.js";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import SlideVerify from "vue3-slide-verify";
import { useUserStore } from "@/store/user";
import { setStorage } from "@/utils/index";

const { t } = useI18n();
const router = useRouter();
const userStore = useUserStore();
const isShow = ref(false);
const registObj = ref({});
const slideblockRef = ref();
const text = t("message.home.xiangyouhuadong");
const accuracy = 5;
const msg = ref("");

const isShowFunc = (data, params) => {
  if (Object.keys(params).length > 0) {
    registObj.value = params;
  }
  isShow.value = true;
  slideblockRef.value?.refresh();
};

const handleClose = () => {
  isShow.value = false;
};

const onSuccess = () => {
  ElMessage.success(t("message.home.tupianyanzhengtongguo"));
  if (Object.keys(registObj.value).length > 0) {
    Axios.accountRegister({ ...registObj.value }).then((res) => {
      if (res.code == "0") {
        ElMessage.success(t("message.home.zhucechenggong"));
        setStorage("spToken", res.data.token);
        setStorage("username", res.data.username);
        userStore.updateUserInfo(res.data);
        router.push("/idSet");
      } else {
        isShow.value = !isShow.value;
      }
    });
  }
};

const onFulfilled = () => {};
const onRefresh = () => {};
const onFail = () => {
  ElMessage.error(t("message.home.yanzhengbutonguo"));
  msg.value = t("message.home.yanzhengbutonguo");
};

const onAgain = () => {
  ElMessage.error(t("message.home.yanzhengbutonguo"));
  msg.value = "try again";
  // 刷新
  slideblockRef.value?.refresh();
};

defineExpose({
  isShowFunc,
}); //在语法糖里面需要把函数暴露出去
</script>
