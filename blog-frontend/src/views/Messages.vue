<template>
  <div class="msg-page">
    <div class="content-layout">
      <div class="main-col">
        <div class="page-header">
          <h1>消息中心</h1>
          <el-button v-if="notifStore.unreadCount > 0" type="primary" text
            @click="handleMarkAllRead">全部标为已读</el-button>
        </div>

        <el-empty v-if="notifStore.notifications.length === 0" description="暂无消息" />

        <div v-else class="notif-list">
          <div v-for="item in notifStore.notifications" :key="item.id"
            class="notif-item" :class="{ unread: item.isRead === 0 }"
            @click="handleClick(item)">
            <div class="notif-icon-col">
              <el-avatar :size="36" class="notif-avatar" :class="iconClass(item.type)">
                <el-icon :size="18"><component :is="notifIcon(item.type)" /></el-icon>
              </el-avatar>
            </div>
            <div class="notif-body">
              <p class="notif-text">{{ item.content }}</p>
              <span class="notif-time">{{ formatDate(item.createdAt) }}</span>
            </div>
            <div class="notif-right">
              <span v-if="item.isRead === 0" class="unread-dot"></span>
              <el-button v-if="item.isRead === 0" text size="small"
                @click.stop="handleMarkRead(item)">标为已读</el-button>
            </div>
          </div>
        </div>

        <div class="pagination-wrap" v-if="notifStore.total > 10">
          <el-pagination background layout="prev, pager, next" :total="notifStore.total"
            :page-size="10" :current-page="currentPage" @current-change="onPageChange" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '../stores/notification'

const router = useRouter()
const notifStore = useNotificationStore()
const currentPage = ref(1)

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.substring(0, 10) + ' ' + dateStr.substring(11, 16)
}

function iconClass(type) {
  const map = { COMMENT: 'blue', REPLY: 'green', POST_TOGGLED: 'orange', COMMENT_TOGGLED: 'orange', COMMENT_DELETED: 'red', FOLLOW: 'purple' }
  return map[type] || 'default'
}

function notifIcon(type) {
  if (type === 'COMMENT' || type === 'REPLY') return 'ChatDotSquare'
  if (type === 'POST_TOGGLED' || type === 'COMMENT_TOGGLED' || type === 'COMMENT_DELETED') return 'Warning'
  if (type === 'FOLLOW') return 'User'
  return 'Bell'
}

function handleClick(item) {
  if (item.isRead === 0) notifStore.markAsRead(item.id)
  const relatedId = item.relatedId
  if (item.type === 'FOLLOW') router.push(`/users/${relatedId}`)
  else if (relatedId) router.push(`/articles/${relatedId}`)
}

function handleMarkRead(item) { notifStore.markAsRead(item.id) }
function handleMarkAllRead() { notifStore.markAllAsRead() }

function onPageChange(page) {
  currentPage.value = page
  notifStore.fetchNotifications(page - 1, 10)
}

onMounted(() => { notifStore.fetchNotifications(0, 10) })
</script>

<style scoped>
.msg-page { max-width: 800px; margin: 0 auto; padding: 24px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h1 { font-size: 24px; font-weight: 700; }

.notif-item {
  display: flex; align-items: center; gap: 14px; padding: 16px;
  background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius);
  margin-bottom: 8px; cursor: pointer; transition: all .15s;
}
.notif-item:hover { border-color: var(--accent); }
.notif-item.unread { background: var(--accent-bg); border-color: #c4dcff; }
.notif-icon-col { flex-shrink: 0; }
.notif-avatar { font-size: 14px; }
.notif-avatar.blue { background: var(--accent) !important; }
.notif-avatar.green { background: var(--success) !important; }
.notif-avatar.orange { background: var(--warning) !important; }
.notif-avatar.red { background: var(--danger) !important; }
.notif-avatar.purple { background: #8250df !important; }
.notif-body { flex: 1; min-width: 0; }
.notif-text { font-size: 14px; color: var(--text-primary); margin: 0 0 4px; line-height: 1.5; }
.notif-time { font-size: 12px; color: var(--text-tertiary); }
.notif-right { display: flex; align-items: center; gap: 8px; }
.unread-dot { width: 8px; height: 8px; border-radius: 50%; background: var(--accent); flex-shrink: 0; }
.pagination-wrap { display: flex; justify-content: center; margin: 24px 0; }
</style>
