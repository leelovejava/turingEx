<template>
  <section class="worker-order-detail">
    <fx-header :title="t('workerOrder.detail')" />
    <div class="head" v-if="order">
      <div class="title">{{ order.title }}</div>
      <div class="meta">{{ t("workerOrder.orderNo") }}: {{ order.workOrderSn }}</div>
      <div class="meta">{{ t("workerOrder.status") }}: {{ order.workOrderStatus === "2" ? t("workerOrder.completed") : t("workerOrder.processing") }}</div>
    </div>

    <van-empty v-if="!loading && contents.length === 0" :description="t('workerOrder.noReplies')" />
    <div class="msg-list">
      <div v-for="item in contents" :key="item.id" class="msg-item">
        <div class="msg-role">{{ item.replyRoleType === 2 ? t("workerOrder.customerService") : t("workerOrder.me") }}</div>
        <div class="msg-content">{{ item.content }}</div>
        <div class="msg-time">{{ item.createTime }}</div>
      </div>
    </div>

    <div class="reply-box" v-if="order && order.workOrderStatus !== '2'">
      <van-field v-model="replyContent" type="textarea" rows="3" :placeholder="t('workerOrder.enterReply')" />
      <van-button type="primary" block @click="reply">{{ t("workerOrder.sendReply") }}</van-button>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { useI18n } from "vue-i18n";
import { showToast } from "vant";
import { _workerOrderDetail, _workerOrderReply } from "@/service/workerOrder.api";

const route = useRoute();
const { t } = useI18n();
const loading = ref(false);
const order = ref(null);
const contents = ref([]);
const replyContent = ref("");
const orderId = ref("");

const load = () => {
  if (!orderId.value) return;
  loading.value = true;
  _workerOrderDetail({ order_id: orderId.value })
    .then((res) => {
      order.value = res.order || null;
      contents.value = res.contents || [];
    })
    .finally(() => {
      loading.value = false;
    });
};

const reply = () => {
  if (!replyContent.value.trim()) {
    showToast(t("workerOrder.enterReply"));
    return;
  }
  _workerOrderReply({ order_id: orderId.value, content: replyContent.value.trim() }).then(() => {
    replyContent.value = "";
    showToast(t("workerOrder.sendSuccess"));
    load();
  });
};

onMounted(() => {
  orderId.value = route.query.order_id || "";
  load();
});
</script>

<style scoped lang="scss">
.worker-order-detail {
  min-height: 100vh;
  background: $mainBgColor;
  padding-bottom: 170px;
}

.head {
  padding: 12px;
}

.title {
  font-size: 30px;
  font-weight: 600;
  color: #ffffff;
  line-height: 42px;
}

.meta {
  margin-top: 8px;
  color: #7c8ea5;
  font-size: 16px;
  line-height: 22px;
}

.msg-list {
  padding: 10px 12px 0;
}

.msg-item {
  padding: 12px;
  margin-bottom: 10px;
  border-radius: 10px;
  background: #282d49;
}

.msg-role {
  color: #7c8ea5;
  font-size: 16px;
  line-height: 22px;
}

.msg-content {
  margin-top: 8px;
  color: #ffffff;
  white-space: pre-wrap;
  font-size: 16px;
  line-height: 22px;
}

.msg-time {
  margin-top: 8px;
  color: #7c8ea5;
  font-size: 16px;
  line-height: 22px;
}

.reply-box {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 12px;
  border-top: 1px solid $border_color;
  background: $mainBgColor;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
}

:deep(.reply-box .van-cell),
:deep(.reply-box .van-field) {
  background: #282d49;
  border-radius: 4px;
}

:deep(.reply-box .van-field__control) {
  color: #ffffff;
}

:deep(.reply-box .van-field__control::placeholder) {
  color: #7c8ea5;
}

:deep(.reply-box .van-cell::after),
:deep(.reply-box .van-hairline--top-bottom::after) {
  border: 0;
}

:deep(.reply-box .van-button--primary) {
  margin-top: 2px;
  border-radius: 0 0 4px 4px;
}
</style>
