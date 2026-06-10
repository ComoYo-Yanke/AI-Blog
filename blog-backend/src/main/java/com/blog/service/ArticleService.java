package com.blog.service;

import com.blog.common.PageResult;
import com.blog.dto.ArticleRequest;
import com.blog.entity.Article;

public interface ArticleService {
    PageResult<Article> getPublicArticles(String keyword, int page, int size);
    Article getArticleById(Long id);
    Article createArticle(ArticleRequest request, Long userId);
    Article updateArticle(Long id, ArticleRequest request, Long userId, String role);
    void deleteArticle(Long id, Long userId, String role);
    PageResult<Article> getMyArticles(Long userId, int page, int size);
    PageResult<Article> getUserArticles(Long userId, int page, int size);

    // Admin
    PageResult<Article> getAllArticles(int page, int size);
    void toggleArticleStatus(Long id);
    long getArticleCount();
}
