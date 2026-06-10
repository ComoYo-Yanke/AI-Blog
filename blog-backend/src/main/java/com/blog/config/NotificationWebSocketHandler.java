package com.blog.config;

import com.blog.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(NotificationWebSocketHandler.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final WebSocketSessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    public NotificationWebSocketHandler(JwtTokenProvider jwtTokenProvider,
                                        WebSocketSessionRegistry sessionRegistry,
                                        ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.sessionRegistry = sessionRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = extractToken(session);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            log.warn("WebSocket rejected: invalid or missing token");
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        String username = jwtTokenProvider.getUsernameFromToken(token);

        session.getAttributes().put("userId", userId);
        session.getAttributes().put("username", username);

        // 顶号：踢掉旧连接
        WebSocketSession old = sessionRegistry.getSession(userId);
        if (old != null && old.isOpen()) {
            try {
                String kickMsg = objectMapper.writeValueAsString(Map.of(
                        "type", "kicked",
                        "message", "您的账号已在其他设备登录，当前设备已被强制下线"
                ));
                old.sendMessage(new TextMessage(kickMsg));
                old.close(CloseStatus.NORMAL);
            } catch (Exception e) {
                log.warn("Failed to kick old session for userId={}", userId);
            }
        }

        sessionRegistry.register(userId, session);
        log.info("WebSocket connected: userId={}, username={}", userId, username);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        if ("ping".equals(payload)) {
            try {
                session.sendMessage(new TextMessage("pong"));
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessionRegistry.unregister(userId);
            log.info("WebSocket disconnected: userId={}", userId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessionRegistry.unregister(userId);
        }
        log.error("WebSocket error for userId={}: {}", userId, exception.getMessage());
    }

    private String extractToken(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) return null;
        String query = uri.getQuery();
        if (query == null) return null;
        try {
            for (String param : query.split("&")) {
                int idx = param.indexOf('=');
                if (idx > 0) {
                    String key = URLDecoder.decode(param.substring(0, idx), StandardCharsets.UTF_8);
                    if ("token".equals(key)) {
                        return URLDecoder.decode(param.substring(idx + 1), StandardCharsets.UTF_8);
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
