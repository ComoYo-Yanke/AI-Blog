package com.blog.service.impl;

import com.blog.common.PageResult;
import com.blog.config.WebSocketSessionRegistry;
import com.blog.entity.Notification;
import com.blog.repository.NotificationRepository;
import com.blog.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final WebSocketSessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   WebSocketSessionRegistry sessionRegistry,
                                   ObjectMapper objectMapper) {
        this.notificationRepository = notificationRepository;
        this.sessionRegistry = sessionRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    public PageResult<Notification> getNotifications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifPage = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId, pageable);
        return PageResult.of(notifPage);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndIsRead(userId, 0);
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("通知不存在"));
        if (!notification.getRecipientId().equals(userId)) {
            throw new IllegalArgumentException("无权操作此通知");
        }
        notification.setIsRead(1);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    @Override
    public void sendNotification(Long recipientId, String type, String content, Long relatedId) {
        Notification notification = new Notification();
        notification.setRecipientId(recipientId);
        notification.setType(type);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);
        notification = notificationRepository.save(notification);

        // 通过 WebSocket 推送给目标用户
        WebSocketSession session = sessionRegistry.getSession(recipientId);
        if (session != null && session.isOpen()) {
            try {
                Map<String, Object> payload = Map.of(
                        "id", notification.getId(),
                        "type", notification.getType(),
                        "content", notification.getContent(),
                        "relatedId", notification.getRelatedId() != null ? notification.getRelatedId() : 0,
                        "isRead", 0,
                        "createdAt", notification.getCreatedAt().toString()
                );
                String json = objectMapper.writeValueAsString(payload);
                session.sendMessage(new TextMessage(json));
            } catch (Exception e) {
                log.error("Failed to send WebSocket notification to userId={}: {}", recipientId, e.getMessage());
            }
        }
    }
}
