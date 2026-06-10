package com.blog.service.impl;

import com.blog.entity.Follow;
import com.blog.entity.User;
import com.blog.repository.FollowRepository;
import com.blog.repository.UserRepository;
import com.blog.service.FollowService;
import com.blog.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public FollowServiceImpl(FollowRepository followRepository, UserRepository userRepository,
                              NotificationService notificationService) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("不能关注自己");
        }
        if (!userRepository.existsById(followingId)) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new IllegalArgumentException("已关注该用户");
        }
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        followRepository.save(follow);

        // 通知被关注者
        String followerName = userRepository.findById(followerId)
                .map(User::getUsername).orElse("未知用户");
        notificationService.sendNotification(
                followingId,
                "FOLLOW",
                followerName + " 关注了你",
                followerId
        );
    }

    @Override
    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        if (!followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new IllegalArgumentException("未关注该用户");
        }
        followRepository.removeByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Override
    public long getFollowerCount(Long userId) {
        return followRepository.countByFollowingId(userId);
    }

    @Override
    public long getFollowingCount(Long userId) {
        return followRepository.countByFollowerId(userId);
    }

    @Override
    public long countAll() {
        return followRepository.count();
    }

    @Override
    public List<User> getFollowers(Long userId) {
        List<Follow> follows = followRepository.findByFollowingIdOrderByCreatedAtDesc(userId);
        return follows.stream()
                .map(f -> userRepository.findById(f.getFollowerId()).orElse(null))
                .filter(u -> u != null)
                .peek(u -> u.setPassword(null))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getFollowing(Long userId) {
        List<Follow> follows = followRepository.findByFollowerIdOrderByCreatedAtDesc(userId);
        return follows.stream()
                .map(f -> userRepository.findById(f.getFollowingId()).orElse(null))
                .filter(u -> u != null)
                .peek(u -> u.setPassword(null))
                .collect(Collectors.toList());
    }
}
