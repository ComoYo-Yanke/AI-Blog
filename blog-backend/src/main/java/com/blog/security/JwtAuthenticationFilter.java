package com.blog.security;

import com.blog.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

@Component
@Order(1)
public class JwtAuthenticationFilter implements Filter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login", "/api/auth/register",
            "/api/articles/public"
    );

    private static final List<String> WS_PATHS = List.of("/ws");

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

        // 尝试从请求中提取用户信息（不强制要求登录）
        String token = extractToken(httpRequest);
        boolean hasValidToken = StringUtils.hasText(token) && jwtTokenProvider.validateToken(token);
        if (hasValidToken) {
            httpRequest.setAttribute("userId", jwtTokenProvider.getUserIdFromToken(token));
            httpRequest.setAttribute("username", jwtTokenProvider.getUsernameFromToken(token));
            httpRequest.setAttribute("role", jwtTokenProvider.getRoleFromToken(token));
        }

        // 放行 WebSocket 路径
        if (isWsPath(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        // 放行公开路径（已登录用户也会带上 userId 属性）
        if (isPublicPath(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        // OPTIONS 请求放行
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        if (!hasValidToken) {
            httpResponse.setContentType("application/json;charset=utf-8");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(httpResponse.getWriter(), Result.unauthorized("未登录或token已过期"));
            return;
        }

        // 管理员路径权限检查
        if (path.startsWith("/api/admin/") && !"ADMIN".equals(jwtTokenProvider.getRoleFromToken(token))) {
            httpResponse.setContentType("application/json;charset=utf-8");
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            objectMapper.writeValue(httpResponse.getWriter(), Result.forbidden("需要管理员权限"));
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isWsPath(HttpServletRequest request) {
        return WS_PATHS.stream().anyMatch(path -> request.getRequestURI().startsWith(path));
    }

    private boolean isPublicPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 登录注册等直接放行
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            return true;
        }

        // 公开文章的GET请求放行
        if ("GET".equalsIgnoreCase(method)) {
            if (path.matches("/api/articles/\\d+") || path.matches("/api/articles/\\d+/comments")) {
                return true;
            }
            // 用户公开资料
            if (path.matches("/api/users/\\d+/profile") ||
                path.matches("/api/users/\\d+/articles") ||
                path.matches("/api/users/\\d+/follow/status") ||
                path.matches("/api/users/\\d+/followers") ||
                path.matches("/api/users/\\d+/following")) {
                return true;
            }
        }

        return false;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
