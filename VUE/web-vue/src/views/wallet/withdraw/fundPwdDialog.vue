<template>
  <div>
    <el-dialog
      :title="t('message.user.anquanyanzheng')"
      v-model="show"
      width="360px"
      :append-to-body="true"
      :show-close="false"
    >
      <div class="body-wrapper">
        <div class="bg-gray p-4">
          {{ t("message.user.shuruzijinmima") }}
          <div class="font-bold text-base text-center mb-2">
            <el-input
              show-password
              clearable
              :placeholder="$t('qsr_zijinmima')"
              class="mt-2"
              type="number"
              v-model="password"
              @change="pwdInputChange"
            ></el-input>
          </div>
        </div>

        <!-- 按钮 -->
        <div class="flex justify-center mt-4">
          <el-button
            type="primary"
            @click="onConfirm(true)"
            class="w-full btn-submit"
          >
            {{ t("message.home.queren") }}
          </el-button>
          <el-button @click="() => onConfirm(false)" class="w-full btn-submit">
            {{ t("message.home.quxiao") }}
          </el-button>
        </div>
        <!-- 资金密码不可用 -->
        <p
          class="text-center mt-2 blue-color cursor-pointer"
          @click="router.push('/my/security')"
        >
          {{ $t("message.user.enterNotPassword") }}
        </p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { ref, onBeforeMount } from "vue";
import { useI18n } from "vue-i18n";
import { ElMessage } from "element-plus";
import Axios2 from "@/api/wallet";

const { t } = useI18n();
const router = useRouter();
const props = defineProps({
  isShow: {
    type: Boolean,
    default: false,
  },
  payInfo: {
    type: Object,
    default: {},
  },
});

const password = ref();
const pay = ref({});
const show = ref();
const emits = defineEmits(["close"]);
onBeforeMount(() => {
  pay.value = props.payInfo;
  show.value = props.isShow;
});

watch(props, async (newprops) => {
  pay.value = newprops.payInfo;
  show.value = newprops.isShow;
});
const pwdInputChange = (val) => {};

// 确认
const onConfirm = (isConfirm) => {
  console.log(888888, isConfirm);
  if (!isConfirm) {
    emits("close");
    return;
  }

  if (password.value.length < 6) {
    ElMessage.error(t("message.user.funpasswordTips"));
    return false;
  }

  pay.value.safeword = password.value;
  Axios2.getBankRechargeToken()
    .then((res) => {
      pay.value.session_token = res.data.session_token;
    })
    .then(() => {
      Axios2.applyOrder(pay.value).then((res) => {
        if (res.code == 0) {
          // router.push(`/bank/orderSuccess?id=${res.data.order_no}`);

          emits("close", isConfirm);
        } else {
          ElMessage.error(res.msg);
        }
      });
    });
};
</script>
<style lang="scss" scoped>
$input_background: #27293b;
$text_color: #fff;
$text_color1: #868d9a; //文字浅色
$black: #000;
.pay-info {
  color: $black;
  font-size: 14px;
}

.title {
  text-align: center;
  color: #1f2025;
  font-weight: bold;
  font-size: 20px;
}

.ash {
  color: $text_color1;
}

.money-title {
  font-size: 24px;
  font-weight: bold;
}
.bg-gray {
  background: #fafafa;
}
.el-dialog__body {
  padding-top: 12px;
}
</style>
