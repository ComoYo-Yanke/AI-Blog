package com.blog.repository;

import com.blog.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Article> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Article> findByStatusOrderByCreatedAtDesc(Integer status);

    Page<Article> findByStatusOrderByCreatedAtDesc(Integer status, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.status = 1 AND (a.title LIKE %:keyword% OR a.content LIKE %:keyword%) ORDER BY a.createdAt DESC")
    List<Article> searchActiveArticles(String keyword);

    @Query("SELECT a FROM Article a WHERE a.status = 1 AND (a.title LIKE %:keyword% OR a.content LIKE %:keyword%) ORDER BY a.createdAt DESC")
    Page<Article> searchActiveArticles(String keyword, Pageable pageable);

    long countByStatus(Integer status);
}
