<template>
  <el-dialog
    v-model="show"
    width="360px"
    :append-to-body="true"
    :before-close="close"
  >
    <div class="body-wrapper text-black">
      <div class="flex justify-center">
        <img class="success-img" src="@/assets/images/wallet/success.svg" />
      </div>
      <p class="title mt-2">
        {{ t("message.user.tikuan") }}
        {{ t("message.user.successfulApplication") }}
      </p>
      <ul class="mt-8">
        <li class="mt-4">
          <div class="fs-16 font-semibold">
            {{ t("message.user.tikuan") }}
            {{ t("message.user.applicationSubmitted") }}
          </div>
          <p class="mt-2">{{ getCurrentTime() }}</p>
        </li>
        <li class="mt-4">
          <div class="fs-16 font-semibold">
            {{ t("message.user.tikuan") }}
            {{ t("message.user.applicationSubmitted") }}
          </div>
          <p class="mt-2">
            {{ t("message.user.tikuan")
            }}{{ t("message.user.ContactCustomerService") }}
          </p>
        </li>
        <li class="mt-4">
          <div class="fs-16 font-semibold">
            {{ t("message.user.tikuan") }} {{ t("message.user.succeeded") }}
          </div>
          <p class="mt-2">{{ t("message.user.Youinformation") }}</p>
        </li>
      </ul>

      <!-- 按钮 -->
      <div class="flex w-full justify-center mt-4">
        <el-button
          type="primary"
          @click="handleViewHistory"
          class="w-full btn-submit"
        >
          {{ t("message.user.chakantixianlishi") }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref } from "vue";
import { useI18n } from "vue-i18n";
import dayjs from "dayjs";
import { useRouter } from "vue-router";
const router = useRouter();
const { t } = useI18n();
const props = defineProps({
  isShow: {
    type: Boolean,
    default: false,
  },
});
const show = ref();

const emits = defineEmits(["close"]);
onBeforeMount(() => {
  show.value = props.isShow;
});
watch(props, async (newprops) => {
  show.value = newprops.isShow;
});

const getCurrentTime = () => {
  return dayjs().format("YYYY-MM-DD HH:mm:ss");
};

const handleViewHistory = () => {
  emits("close");
  router.push("/withdraw/record");
};
const close = ()=>{
  emits("close");
  show.value = false
}
</script>
<style lang="scss" scoped>
.success-img {
  width: 48px;
  height: 48px;
}
.title {
  text-align: center;
  color: #1f2025;
  font-weight: bold;
  font-size: 20px;
}
</style>
