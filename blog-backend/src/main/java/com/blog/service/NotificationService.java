package com.blog.service;

import com.blog.common.PageResult;
import com.blog.entity.Notification;

public interface NotificationService {
    PageResult<Notification> getNotifications(Long userId, int page, int size);
    long getUnreadCount(Long userId);
    void markAsRead(Long notificationId, Long userId);
    void markAllAsRead(Long userId);

    // 创建通知并推送 WebSocket
    void sendNotification(Long recipientId, String type, String content, Long relatedId);
}
