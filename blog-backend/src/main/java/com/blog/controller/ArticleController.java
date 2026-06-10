package com.blog.controller;

import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.dto.ArticleRequest;
import com.blog.entity.Article;
import com.blog.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // 公开：获取已发布的文章列表（分页）
    @GetMapping("/public")
    public Result<PageResult<Article>> getPublicArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<Article> articles = articleService.getPublicArticles(keyword, page, size);
        return Result.success(articles);
    }

    // 公开：获取文章详情
    @GetMapping("/{id}")
    public Result<Article> getArticleById(@PathVariable Long id) {
        Article article = articleService.getArticleById(id);
        return Result.success(article);
    }

    // 需要登录：创建文章
    @PostMapping
    public Result<Article> createArticle(@Valid @RequestBody ArticleRequest request,
                                         @RequestAttribute("userId") Long userId) {
        Article article = articleService.createArticle(request, userId);
        return Result.success(article);
    }

    // 需要登录：更新文章
    @PutMapping("/{id}")
    public Result<Article> updateArticle(@PathVariable Long id,
                                         @Valid @RequestBody ArticleRequest request,
                                         @RequestAttribute("userId") Long userId,
                                         @RequestAttribute("role") String role) {
        Article article = articleService.updateArticle(id, request, userId, role);
        return Result.success(article);
    }

    // 需要登录：删除文章
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id,
                                      @RequestAttribute("userId") Long userId,
                                      @RequestAttribute("role") String role) {
        articleService.deleteArticle(id, userId, role);
        return Result.success();
    }

    // 获取当前用户的文章（分页）
    @GetMapping("/my")
    public Result<PageResult<Article>> getMyArticles(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<Article> articles = articleService.getMyArticles(userId, page, size);
        return Result.success(articles);
    }
}
