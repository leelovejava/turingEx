<template>
  <section class="worker-order-create">
    <fx-header :title="t('workerOrder.createOrder')" />
    <div class="form">
      <van-field
        v-model="title"
        :label="t('workerOrder.title')"
        :placeholder="t('workerOrder.pleaseEnterTitle')"
        maxlength="100"
      />
      <van-field
        v-model="content"
        type="textarea"
        rows="5"
        autosize
        :label="t('workerOrder.content')"
        :placeholder="t('workerOrder.pleaseEnterContent')"
        maxlength="1000"
        show-word-limit
      />
      <div class="submit">
        <van-button type="primary" block @click="submit">{{ t('workerOrder.submit') }}</van-button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { showToast } from "vant";
import { _workerOrderCreate } from "@/service/workerOrder.api";

const router = useRouter();
const { t } = useI18n();
const title = ref("");
const content = ref("");

const submit = () => {
  if (!title.value.trim()) {
    showToast(t("workerOrder.pleaseEnterTitle"));
    return;
  }
  _workerOrderCreate({ title: title.value.trim(), content: content.value.trim() }).then(() => {
    showToast(t("workerOrder.submitSuccess"));
    router.replace("/workerOrder");
  });
};
</script>

<style scoped lang="scss">
.worker-order-create {
  min-height: 100vh;
  background: $mainBgColor;
}
.form {
  padding: 12px;
  border-radius: 10px;
}

:deep(.van-cell),
:deep(.van-field) {
  background: $input_background;
  color: $text_color;
}

:deep(.van-field__label) {
  color: $text_color;
}

:deep(.van-field__control) {
  color: $text_color;
}

:deep(.van-field__control::placeholder) {
  color: $text_color1;
}

:deep(.van-hairline--top-bottom::after),
:deep(.van-cell::after) {
  border-color: $border_color;
}
.submit {
  margin-top: 16px;
}

:deep(.submit .van-button--primary) {
  background: $color_main;
  border-color: $color_main;
}
</style>
