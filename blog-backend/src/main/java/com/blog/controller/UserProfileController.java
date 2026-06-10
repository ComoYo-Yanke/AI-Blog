package com.blog.controller;

import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.entity.Article;
import com.blog.entity.User;
import com.blog.service.ArticleService;
import com.blog.service.FollowService;
import com.blog.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserService userService;
    private final ArticleService articleService;
    private final FollowService followService;

    public UserProfileController(UserService userService, ArticleService articleService,
                                  FollowService followService) {
        this.userService = userService;
        this.articleService = articleService;
        this.followService = followService;
    }

    // 获取用户公开资料
    @GetMapping("/{id}/profile")
    public Result<Map<String, Object>> getUserProfile(@PathVariable Long id,
                                                       @RequestAttribute(value = "userId", required = false) Long currentUserId) {
        User user = userService.getCurrentUser(id);
        user.setPassword(null);

        Map<String, Object> profile = new HashMap<>();
        profile.put("user", user);
        profile.put("followerCount", followService.getFollowerCount(id));
        profile.put("followingCount", followService.getFollowingCount(id));

        if (currentUserId != null) {
            profile.put("isFollowing", followService.isFollowing(currentUserId, id));
        } else {
            profile.put("isFollowing", false);
        }

        return Result.success(profile);
    }

    // 获取用户的公开文章
    @GetMapping("/{id}/articles")
    public Result<PageResult<Article>> getUserArticles(@PathVariable Long id,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        PageResult<Article> articles = articleService.getUserArticles(id, page, size);
        return Result.success(articles);
    }

    // 关注用户
    @PostMapping("/{id}/follow")
    public Result<Void> followUser(@PathVariable Long id,
                                   @RequestAttribute("userId") Long currentUserId) {
        followService.follow(currentUserId, id);
        return Result.success();
    }

    // 取消关注
    @DeleteMapping("/{id}/follow")
    public Result<Void> unfollowUser(@PathVariable Long id,
                                     @RequestAttribute("userId") Long currentUserId) {
        followService.unfollow(currentUserId, id);
        return Result.success();
    }

    // 获取关注状态
    @GetMapping("/{id}/follow/status")
    public Result<Map<String, Object>> getFollowStatus(@PathVariable Long id,
                                                        @RequestAttribute(value = "userId", required = false) Long currentUserId) {
        Map<String, Object> result = new HashMap<>();
        if (currentUserId != null) {
            result.put("isFollowing", followService.isFollowing(currentUserId, id));
        } else {
            result.put("isFollowing", false);
        }
        result.put("followerCount", followService.getFollowerCount(id));
        result.put("followingCount", followService.getFollowingCount(id));
        return Result.success(result);
    }

    // 获取关注列表
    @GetMapping("/{id}/following")
    public Result<List<User>> getFollowing(@PathVariable Long id) {
        List<User> users = followService.getFollowing(id);
        return Result.success(users);
    }

    // 获取粉丝列表
    @GetMapping("/{id}/followers")
    public Result<List<User>> getFollowers(@PathVariable Long id) {
        List<User> users = followService.getFollowers(id);
        return Result.success(users);
    }
}
