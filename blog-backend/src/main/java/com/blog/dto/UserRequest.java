package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    private String email;

    private String role;
}
