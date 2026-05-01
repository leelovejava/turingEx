<template>
  <section class="worker-order-page">
    <fx-header :title="t('workerOrder.center')" />
    <div class="list">
      <van-empty v-if="!loading && list.length === 0" :description="t('workerOrder.noOrders')" />
      <div v-else class="order-cards">
        <div v-for="item in list" :key="item.id" class="order-card" @click="goDetail(item.id)">
          <div class="order-head">
            <div class="order-title">{{ item.title || "-" }}</div>
            <div class="order-status">{{ item.workOrderStatus === "2" ? t("workerOrder.completed") : t("workerOrder.processing") }}</div>
            <van-icon name="arrow" class="order-arrow" />
          </div>
          <div class="order-meta">{{ t("workerOrder.orderNo") }}: {{ item.workOrderSn || "-" }}</div>
          <div class="order-meta">{{ t("workerOrder.createTime") }}: {{ item.createTime || "-" }}</div>
        </div>
      </div>
    </div>
    <div class="actions fixed-actions">
      <van-button type="primary" block @click="goCreate">{{ t("workerOrder.createOrder") }}</van-button>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { _workerOrderList } from "@/service/workerOrder.api";

const router = useRouter();
const { t } = useI18n();
const loading = ref(false);
const list = ref([]);

const load = () => {
  loading.value = true;
  _workerOrderList({ page_no: 1, page_size: 50 })
    .then((res) => {
      list.value = (res && res.records) || [];
    })
    .finally(() => {
      loading.value = false;
    });
};

const goCreate = () => router.push("/workerOrder/create");
const goDetail = (id) => router.push(`/workerOrder/detail?order_id=${id}`);

onMounted(load);
</script>

<style scoped lang="scss">
.worker-order-page {
  min-height: 100vh;
  background: $mainBgColor;
}

.list {
  padding: 14px 12px 100px;
}

.order-card {
  background: #282d49;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.22);
}

.order-head {
  display: flex;
  align-items: center;
}

.order-title {
  flex: 1;
  min-width: 0;
  font-size: 30px;
  font-weight: 600;
  color: #ffffff;
  line-height: 36px;
  word-break: break-all;
}

.order-status {
  margin-left: 8px;
  font-size: 16px;
  color: #7c8ea5;
  line-height: 22px;
  white-space: nowrap;
}

.order-arrow {
  margin-left: 4px;
  font-size: 16px;
  color: #a9b6c8;
}

.order-meta {
  margin-top: 6px;
  font-size: 16px;
  line-height: 22px;
  color: #7c8ea5;
}

.actions {
  padding: 12px;
}

.fixed-actions {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: $mainBgColor;
  border-top: 1px solid $border_color;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
}

:deep(.fixed-actions .van-button--primary) {
  height: 44px;
  border: 0;
  border-radius: 6px;
  font-size: 16px;
  background: linear-gradient(180deg, #3898ff 0%, #2380e8 100%);
  box-shadow: 0 6px 14px rgba(35, 128, 232, 0.35);
}
</style>
