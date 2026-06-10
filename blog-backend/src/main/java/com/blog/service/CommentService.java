package com.blog.service;

import com.blog.common.PageResult;
import com.blog.dto.CommentRequest;
import com.blog.entity.Comment;

public interface CommentService {
    Comment createComment(Long articleId, CommentRequest request, Long userId);
    void deleteComment(Long commentId, Long userId, String role);
    PageResult<Comment> getArticleComments(Long articleId, int page, int size);
    PageResult<Comment> getMyComments(Long userId, int page, int size);

    // Admin
    PageResult<Comment> getAllComments(int page, int size);
    long getCommentCount();
    void toggleCommentStatus(Long id);
}
