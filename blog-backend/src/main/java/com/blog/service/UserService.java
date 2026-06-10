package com.blog.service;

import com.blog.dto.LoginRequest;
import com.blog.dto.RegisterRequest;
import com.blog.dto.UserRequest;
import com.blog.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map<String, Object> login(LoginRequest request);
    void register(RegisterRequest request);
    User getCurrentUser(Long userId);

    // Admin
    List<User> getAllUsers();
    User createUser(UserRequest request);
    User updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
    void toggleUserStatus(Long id);
}
