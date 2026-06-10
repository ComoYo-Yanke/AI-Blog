package com.blog.service.impl;

import com.blog.common.PageResult;
import com.blog.dto.CommentRequest;
import com.blog.config.SensitiveWordFilter;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CommentRepository;
import com.blog.repository.UserRepository;
import com.blog.service.CommentService;
import com.blog.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final SensitiveWordFilter sensitiveWordFilter;

    public CommentServiceImpl(CommentRepository commentRepository, ArticleRepository articleRepository,
                              UserRepository userRepository, NotificationService notificationService,
                              SensitiveWordFilter sensitiveWordFilter) {
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.sensitiveWordFilter = sensitiveWordFilter;
    }

    @Override
    public Comment createComment(Long articleId, CommentRequest request, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));

        if (article.getStatus() == 0) {
            throw new IllegalArgumentException("文章已禁用，无法评论");
        }

        // 如果是回复，验证父评论存在
        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("父评论不存在"));
            if (!parent.getArticleId().equals(articleId)) {
                throw new IllegalArgumentException("父评论不属于该文章");
            }
        }

        Comment comment = new Comment();
        // 敏感词过滤
        comment.setContent(sensitiveWordFilter.filter(request.getContent()));
        comment.setUserId(userId);
        comment.setArticleId(articleId);
        comment.setParentId(request.getParentId());
        comment.setStatus(1);
        comment = commentRepository.save(comment);

        // 发送通知
        String commenterName = userRepository.findById(userId)
                .map(User::getUsername).orElse("未知用户");

        // 通知文章作者（除非作者就是评论者自己）
        if (!article.getUserId().equals(userId)) {
            notificationService.sendNotification(
                    article.getUserId(),
                    "COMMENT",
                    commenterName + " 评论了你的文章《" + article.getTitle() + "》",
                    articleId
            );
        }

        // 如果是回复，通知父评论作者（除非回复自己）
        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId()).orElse(null);
            if (parent != null && !parent.getUserId().equals(userId)) {
                notificationService.sendNotification(
                        parent.getUserId(),
                        "REPLY",
                        commenterName + " 在《" + article.getTitle() + "》中回复了你的评论",
                        articleId
                );
            }
        }

        return comment;
    }

    @Override
    public void deleteComment(Long commentId, Long userId, String role) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));

        if (!comment.getUserId().equals(userId) && !"ADMIN".equals(role)) {
            throw new IllegalArgumentException("无权删除此评论");
        }

        // 管理员删除他人评论时通知评论作者
        if ("ADMIN".equals(role) && !comment.getUserId().equals(userId)) {
            notificationService.sendNotification(
                    comment.getUserId(),
                    "COMMENT_DELETED",
                    "你的评论已被管理员删除",
                    comment.getArticleId()
            );
        }

        // 同时删除该评论的所有回复
        List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(commentId);
        commentRepository.deleteAll(replies);
        commentRepository.deleteById(commentId);
    }

    @Override
    public PageResult<Comment> getArticleComments(Long articleId, int page, int size) {
        if (!articleRepository.existsById(articleId)) {
            throw new IllegalArgumentException("文章不存在");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByArticleIdOrderByCreatedAtDesc(articleId, pageable);
        populateUsernames(commentPage.getContent());
        return PageResult.of(commentPage);
    }

    @Override
    public PageResult<Comment> getMyComments(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        populateUsernames(commentPage.getContent());
        return PageResult.of(commentPage);
    }

    private void populateUsernames(List<Comment> comments) {
        for (Comment c : comments) {
            userRepository.findById(c.getUserId()).ifPresent(u -> c.setUsername(u.getUsername()));
            if (c.getParentId() != null) {
                commentRepository.findById(c.getParentId()).ifPresent(parent -> {
                    userRepository.findById(parent.getUserId()).ifPresent(u -> c.setReplyToUsername(u.getUsername()));
                });
            }
        }
    }

    // ===== Admin Methods =====

    @Override
    public void toggleCommentStatus(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));
        comment.setStatus(comment.getStatus() == 1 ? 0 : 1);
        commentRepository.save(comment);

        // 通知评论作者
        String action = comment.getStatus() == 1 ? "恢复" : "禁用";
        notificationService.sendNotification(
                comment.getUserId(),
                "COMMENT_TOGGLED",
                "你的评论已被管理员" + action,
                comment.getArticleId()
        );
    }

    @Override
    public PageResult<Comment> getAllComments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findAll(pageable);
        populateUsernames(commentPage.getContent());
        return PageResult.of(commentPage);
    }

    @Override
    public long getCommentCount() {
        return commentRepository.count();
    }
}
