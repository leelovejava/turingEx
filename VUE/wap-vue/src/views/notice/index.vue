<template>
  <section class="notice-page">
    <div class="notice-header">
      <van-icon name="arrow-left" class="back-icon" @click="router.back()" />
      <span class="header-title">Notification Messages</span>
      <span class="read-all" @click="handleReadAll">Mark All as Read</span>
    </div>
    <van-empty v-if="!loading && list.length === 0" description="No notifications" />
    <div v-else class="notice-list">
      <div v-for="item in list" :key="item.id" class="notice-item" @click="goDetail(item)">
        <div class="notice-row">
          <span class="notice-title">{{ item.title }}</span>
          <span class="notice-status" :class="{ unread: item.status === 1 }">
            {{ item.status === 1 ? 'Unread' : 'Read' }}
          </span>
        </div>
        <div class="notice-row">
          <span class="notice-content">{{ item.content }}</span>
          <span class="notice-time">{{ formatTime(item.createTime) }}</span>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { _noticeList, _noticeReadAll } from '@/service/notice.api'

const router = useRouter()
const list = ref([])
const loading = ref(false)

const load = () => {
  loading.value = true
  _noticeList({ pageNo: 1, pageSize: 50 })
    .then(res => { list.value = res?.records || [] })
    .finally(() => { loading.value = false })
}

const handleReadAll = () => {
  _noticeReadAll().then(() => load())
}

const goDetail = (item) => {
  router.push({ path: '/notice/detail', query: { id: item.id } })
}

const formatTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  const pad = n => String(n).padStart(2, '0')
  return `${pad(d.getMonth()+1)}/${pad(d.getDate())}/${d.getFullYear()} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

onMounted(load)
</script>

<style scoped lang="scss">
.notice-page {
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
  .read-all { font-size: 13px; color: #f0a500; cursor: pointer; white-space: nowrap; }
}
.notice-list { padding: 0 14px; }
.notice-item {
  padding: 14px 0;
  border-bottom: 1px solid $border_color;
  cursor: pointer;
}
.notice-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 4px;
}
.notice-title { font-size: 14px; font-weight: 600; color: $text_color; }
.notice-content { font-size: 13px; color: $text_color1; flex: 1; margin-right: 8px; }
.notice-status { font-size: 12px; color: $text_color1; white-space: nowrap; }
.notice-status.unread { color: #f0a500; }
.notice-time { font-size: 12px; color: $text_color1; white-space: nowrap; }
</style>
