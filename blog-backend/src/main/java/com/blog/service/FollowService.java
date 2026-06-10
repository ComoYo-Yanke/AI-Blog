package com.blog.service;

import com.blog.entity.Follow;
import com.blog.entity.User;

import java.util.List;

public interface FollowService {
    void follow(Long followerId, Long followingId);
    void unfollow(Long followerId, Long followingId);
    boolean isFollowing(Long followerId, Long followingId);
    long getFollowerCount(Long userId);
    long getFollowingCount(Long userId);
    List<User> getFollowers(Long userId);
    List<User> getFollowing(Long userId);
    long countAll();
}
