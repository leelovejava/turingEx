<template>
  <div>
    <el-dialog
      :title="t('message.user.qurendingdan')"
      class="dialog-box"
      v-model="isShow"
      width="370px"
      :append-to-body="true"
    >
      <div class="body-wrapper">
        <!-- 实际到账 -->
        <div class="text-center mt-4">
          {{ t("message.user.zhenshidaozhang") }}
        </div>
        <div class="font-bold text-base text-center mt-2 mb-2">
          {{ pay.coin_amount }} USD
        </div>

        <!-- 提交信息 -->
        <section>
          <ul class="mt-2 mb-4 border-b-1">
            <li class="flex justify-between pt-1 pb-1">
              <span class="ash">{{ $t("message.user.huobi") }}</span>
              <span>{{ pay.currency }}</span>
            </li>
            <li class="flex justify-between pt-1 pb-1">
              <span class="ash">{{ $t("message.user.shuliang") }}</span>
              <span>{{ pay.fa_amount }}</span>
            </li>
            <li class="flex justify-between pt-1 pb-1">
              <span class="ash">{{ $t("message.user.shouxufei") }}</span>
              <span>{{ pay.fee || "--" }}</span>
            </li>
          </ul>
        </section>
        <!-- 按钮 -->
        <div class="flex justify-center">
          <el-button
            type="primary"
            @click="onConfirm"
            class="w-full btn-submit"
          >
            {{ t("message.user.qurendingdan") }}
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { ref, watch, onBeforeMount } from "vue";
import { useI18n } from "vue-i18n";
import Axios2 from "@/api/wallet";
const { t } = useI18n();
const router = useRouter();
const props = defineProps({
  showPopup: {
    type: Boolean,
    default: false,
  },
  payInfo: {
    type: Object,
    default: {},
  },
});
const emits = defineEmits(["close", "getToken"]);
let pay = ref({});
const isShow = ref();
onBeforeMount(() => {
  pay.value = props.payInfo;
  isShow.value = props.showPopup;
});

const onConfirm = () => {
  emits("close");
  const params = { ...props.payInfo, safeword: "" };
  Axios2.applyOrder(params)
    .then((res) => {
      router.push(`/bank/orderSuccess?id=${res.data.order_no}`);
    })
    .catch(() => {
      emits("getToken"); //重新获取token
    });
};
watch(props, async (newprops) => {
  pay.value = newprops.payInfo;
  isShow.value = newprops.showPopup;
});
</script>
<style lang="scss" scoped>
$black: #000;
$text_color1: #868d9a; //文字浅色
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
</style>
