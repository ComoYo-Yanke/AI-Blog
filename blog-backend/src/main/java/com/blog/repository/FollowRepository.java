package com.blog.repository;

import com.blog.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    long countByFollowerId(Long followerId);

    long countByFollowingId(Long followingId);

    List<Follow> findByFollowerIdOrderByCreatedAtDesc(Long followerId);

    List<Follow> findByFollowingIdOrderByCreatedAtDesc(Long followingId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Follow f WHERE f.followerId = :followerId AND f.followingId = :followingId")
    int removeByFollowerIdAndFollowingId(@Param("followerId") Long followerId,
                                         @Param("followingId") Long followingId);
}
