<template>
  <el-dialog
    :title="t('message.user.qurendingdan')"
    v-model="show"
    width="360px"
    :append-to-body="true"
    :show-close="false"
  >
    <div class="body-wrapper">
      <!-- 实际到账 -->
      <div class="text-center">
        {{ t("message.user.zhenshidaozhang") }}
      </div>
      <div class="font-bold text-base text-center mt-2 mb-2">
        {{ pay.volume_last }} USD
      </div>

      <!-- 提交信息 -->
      <section>
        <ul class="mt-2 mb-4 border-b-1">
          <!-- 货币 -->
          <li class="flex justify-between pt-1 pb-1">
            <span class="ash">{{ $t("message.user.huobi") }}</span>
            <span> USD </span>
          </li>
          <!-- 金额 -->
          <li class="flex justify-between pt-1 pb-1">
            <span class="ash">{{ $t("message.user.shuliang") }}</span>
            <span>{{ pay.fa_amount }}</span>
          </li>
          <!-- 手续费 -->
          <li class="flex justify-between pt-1 pb-1">
            <span class="ash">{{ $t("message.user.shouxufei") }}</span>
            <span>{{ pay.fee || "--" }}</span>
          </li>
        </ul>
      </section>
      <!-- 按钮 -->
      <div class="flex w-full justify-center">
        <el-button
          type="primary"
          @click="onConfirm(true)"
          class="w-full btn-submit"
        >
          {{ t("message.user.qurendingdan") }}
        </el-button>
        <el-button @click="() => onConfirm(false)" class="w-full btn-submit">
          {{ t("message.user.quxiao") }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch, onBeforeMount } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
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

const onConfirm = (isConfirm) => {
  emits("close", isConfirm);
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
</style>
