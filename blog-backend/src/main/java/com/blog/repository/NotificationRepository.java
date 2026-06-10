package com.blog.repository;

import com.blog.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    long countByRecipientIdAndIsRead(Long recipientId, Integer isRead);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = 1 WHERE n.recipientId = :recipientId AND n.isRead = 0")
    void markAllAsRead(Long recipientId);
}
