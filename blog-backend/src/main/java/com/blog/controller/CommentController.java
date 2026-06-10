package com.blog.controller;

import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.dto.CommentRequest;
import com.blog.entity.Comment;
import com.blog.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 获取文章评论（公开，分页）
    @GetMapping("/articles/{articleId}/comments")
    public Result<PageResult<Comment>> getArticleComments(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<Comment> comments = commentService.getArticleComments(articleId, page, size);
        return Result.success(comments);
    }

    // 添加评论（需登录）
    @PostMapping("/articles/{articleId}/comments")
    public Result<Comment> createComment(@PathVariable Long articleId,
                                         @Valid @RequestBody CommentRequest request,
                                         @RequestAttribute("userId") Long userId) {
        Comment comment = commentService.createComment(articleId, request, userId);
        return Result.success(comment);
    }

    // 删除评论
    @DeleteMapping("/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id,
                                      @RequestAttribute("userId") Long userId,
                                      @RequestAttribute("role") String role) {
        commentService.deleteComment(id, userId, role);
        return Result.success();
    }

    // 获取我的评论（分页）
    @GetMapping("/comments/my")
    public Result<PageResult<Comment>> getMyComments(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<Comment> comments = commentService.getMyComments(userId, page, size);
        return Result.success(comments);
    }
}
