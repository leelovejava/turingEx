<template>
  <section class="notice-detail-page">
    <div class="notice-header">
      <van-icon name="arrow-left" class="back-icon" @click="router.back()" />
      <span class="header-title">Notification Details</span>
      <span style="width:60px"></span>
    </div>
    <div v-if="notice" class="detail-body">
      <div class="detail-title">{{ notice.title }}</div>
      <div class="detail-content">{{ notice.content }}</div>
      <div class="detail-time">{{ formatTime(notice.createTime) }}</div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { _noticeList, _noticeRead } from '@/service/notice.api'

const router = useRouter()
const route = useRoute()
const notice = ref(null)

const formatTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  const pad = n => String(n).padStart(2, '0')
  return `${pad(d.getMonth()+1)}/${pad(d.getDate())}/${d.getFullYear()} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

onMounted(() => {
  const id = route.query.id
  _noticeList({ pageNo: 1, pageSize: 50 }).then(res => {
    notice.value = (res?.records || []).find(n => String(n.id) === String(id)) || null
    if (notice.value?.status === 1) _noticeRead(id)
  })
})
</script>

<style scoped lang="scss">
.notice-detail-page {
  min-height: 100vh;
  background: $mainBgColor;
}
.notice-header {
  display: flex;
  align-items: center;
  padding: 12px 14px;
  border-bottom: 1px solid $border_color;
  .back-icon { font-size: 20px; color: $text_color; cursor: pointer; }
  .header-title { flex: 1; text-align: center; font-size: 16px; font-weight: 600; color: $text_color; }
}
.detail-body {
  padding: 20px 16px;
}
.detail-title {
  font-size: 16px;
  font-weight: 600;
  color: $text_color;
  margin-bottom: 12px;
}
.detail-content {
  font-size: 14px;
  color: $text_color1;
  line-height: 1.6;
  margin-bottom: 24px;
}
.detail-time {
  text-align: right;
  font-size: 12px;
  color: $text_color1;
}
</style>
