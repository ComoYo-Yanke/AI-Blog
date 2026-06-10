import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '../utils/request'

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref([])
  const unreadCount = ref(0)
  const total = ref(0)

  async function fetchNotifications(page = 0, size = 10) {
    const res = await request.get('/notifications', { params: { page, size } })
    notifications.value = res.data.records
    total.value = res.data.total
    return res.data
  }

  async function fetchUnreadCount() {
    const res = await request.get('/notifications/unread-count')
    unreadCount.value = res.data.count
  }

  async function markAsRead(id) {
    await request.put(`/notifications/${id}/read`)
    unreadCount.value = Math.max(0, unreadCount.value - 1)
    const notif = notifications.value.find(n => n.id === id)
    if (notif) notif.isRead = 1
  }

  async function markAllAsRead() {
    await request.put('/notifications/read-all')
    unreadCount.value = 0
    notifications.value.forEach(n => n.isRead = 1)
  }

  function addRealtimeNotification(notification) {
    notifications.value.unshift(notification)
    unreadCount.value++
    total.value++
  }

  return {
    notifications,
    unreadCount,
    total,
    fetchNotifications,
    fetchUnreadCount,
    markAsRead,
    markAllAsRead,
    addRealtimeNotification
  }
})
