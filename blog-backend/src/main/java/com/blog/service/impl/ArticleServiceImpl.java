package com.blog.service.impl;

import com.blog.common.PageResult;
import com.blog.dto.ArticleRequest;
import com.blog.entity.Article;
import com.blog.entity.User;
import com.blog.repository.ArticleRepository;
import com.blog.repository.UserRepository;
import com.blog.service.ArticleService;
import com.blog.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ArticleServiceImpl(ArticleRepository articleRepository, UserRepository userRepository,
                              NotificationService notificationService) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    private void populateUsername(Article article) {
        if (article != null) {
            userRepository.findById(article.getUserId())
                    .ifPresent(u -> article.setUsername(u.getUsername()));
        }
    }

    @Override
    public PageResult<Article> getPublicArticles(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articlePage;
        if (keyword != null && !keyword.isEmpty()) {
            articlePage = articleRepository.searchActiveArticles(keyword, pageable);
        } else {
            articlePage = articleRepository.findByStatusOrderByCreatedAtDesc(1, pageable);
        }
        articlePage.getContent().forEach(a -> {
            a.setContent(null);
            populateUsername(a);
        });
        return PageResult.of(articlePage);
    }

    @Override
    public Article getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        populateUsername(article);
        return article;
    }

    @Override
    public Article createArticle(ArticleRequest request, Long userId) {
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setSummary(request.getSummary());
        article.setUserId(userId);
        article.setStatus(1);
        article = articleRepository.save(article);
        populateUsername(article);
        return article;
    }

    @Override
    public Article updateArticle(Long id, ArticleRequest request, Long userId, String role) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));

        if (!article.getUserId().equals(userId) && !"ADMIN".equals(role)) {
            throw new IllegalArgumentException("无权修改此文章");
        }

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setSummary(request.getSummary());
        article = articleRepository.save(article);
        populateUsername(article);
        return article;
    }

    @Override
    public void deleteArticle(Long id, Long userId, String role) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));

        if (!article.getUserId().equals(userId) && !"ADMIN".equals(role)) {
            throw new IllegalArgumentException("无权删除此文章");
        }

        articleRepository.deleteById(id);
    }

    @Override
    public PageResult<Article> getMyArticles(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articlePage = articleRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        articlePage.getContent().forEach(this::populateUsername);
        return PageResult.of(articlePage);
    }

    // ===== Public profile method =====

    @Override
    public PageResult<Article> getUserArticles(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articlePage = articleRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        articlePage.getContent().forEach(a -> {
            a.setContent(null);
            populateUsername(a);
        });
        return PageResult.of(articlePage);
    }

    // ===== Admin Methods =====

    @Override
    public PageResult<Article> getAllArticles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articlePage = articleRepository.findAll(pageable);
        articlePage.getContent().forEach(this::populateUsername);
        return PageResult.of(articlePage);
    }

    @Override
    public void toggleArticleStatus(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        article.setStatus(article.getStatus() == 1 ? 0 : 1);
        articleRepository.save(article);

        // 通知文章作者
        String action = article.getStatus() == 1 ? "恢复" : "禁用";
        notificationService.sendNotification(
                article.getUserId(),
                "POST_TOGGLED",
                "你的文章《" + article.getTitle() + "》已被管理员" + action,
                article.getId()
        );
    }

    @Override
    public long getArticleCount() {
        return articleRepository.count();
    }
}
