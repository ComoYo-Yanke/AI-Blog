package com.blog.controller;

import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.entity.Notification;
import com.blog.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public Result<PageResult<Notification>> getNotifications(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(notificationService.getNotifications(userId, page, size));
    }

    @GetMapping("/unread-count")
    public Result<Map<String, Long>> getUnreadCount(@RequestAttribute("userId") Long userId) {
        long count = notificationService.getUnreadCount(userId);
        return Result.success(Map.of("count", count));
    }

    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id,
                                   @RequestAttribute("userId") Long userId) {
        notificationService.markAsRead(id, userId);
        return Result.success();
    }

    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(@RequestAttribute("userId") Long userId) {
        notificationService.markAllAsRead(userId);
        return Result.success();
    }
}
