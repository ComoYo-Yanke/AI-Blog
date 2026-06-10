package com.blog.controller;

import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.dto.UserRequest;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.service.ArticleService;
import com.blog.service.CommentService;
import com.blog.service.FollowService;
import com.blog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final ArticleService articleService;
    private final CommentService commentService;
    private final FollowService followService;

    public AdminController(UserService userService, ArticleService articleService, CommentService commentService,
                           FollowService followService) {
        this.userService = userService;
        this.articleService = articleService;
        this.commentService = commentService;
        this.followService = followService;
    }

    // ===== 用户管理 =====

    @GetMapping("/users")
    public Result<List<Map<String, Object>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<Map<String, Object>> result = new ArrayList<>();
        for (User u : users) {
            u.setPassword(null);
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", u.getId());
            userMap.put("username", u.getUsername());
            userMap.put("email", u.getEmail());
            userMap.put("role", u.getRole());
            userMap.put("status", u.getStatus());
            userMap.put("createdAt", u.getCreatedAt());
            userMap.put("updatedAt", u.getUpdatedAt());
            userMap.put("followerCount", followService.getFollowerCount(u.getId()));
            userMap.put("followingCount", followService.getFollowingCount(u.getId()));
            result.add(userMap);
        }
        return Result.success(result);
    }

    @PostMapping("/users")
    public Result<User> createUser(@Valid @RequestBody UserRequest request) {
        User user = userService.createUser(request);
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/users/{id}")
    public Result<User> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        User user = userService.updateUser(id, request);
        user.setPassword(null);
        return Result.success(user);
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return Result.success();
    }

    // ===== 文章管理 =====

    @GetMapping("/articles")
    public Result<PageResult<Article>> getAllArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(articleService.getAllArticles(page, size));
    }

    @PutMapping("/articles/{id}/status")
    public Result<Void> toggleArticleStatus(@PathVariable Long id) {
        articleService.toggleArticleStatus(id);
        return Result.success();
    }

    // ===== 评论管理 =====

    @GetMapping("/comments")
    public Result<PageResult<Comment>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(commentService.getAllComments(page, size));
    }

    @PutMapping("/comments/{id}/status")
    public Result<Void> toggleCommentStatus(@PathVariable Long id) {
        commentService.toggleCommentStatus(id);
        return Result.success();
    }

    // ===== 统计数据 =====

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userService.getAllUsers().size());
        stats.put("articleCount", articleService.getArticleCount());
        stats.put("commentCount", commentService.getCommentCount());
        stats.put("followCount", followService.countAll());
        return Result.success(stats);
    }
}
