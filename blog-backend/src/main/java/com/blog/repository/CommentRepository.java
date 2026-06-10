package com.blog.repository;

import com.blog.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticleIdOrderByCreatedAtDesc(Long articleId);

    Page<Comment> findByArticleIdOrderByCreatedAtDesc(Long articleId, Pageable pageable);

    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Comment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    long countByStatus(Integer status);
}
