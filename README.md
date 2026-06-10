<span><a href="#english">English</a> | <a href="#chinese">中文</a> </span>
# <span id="english">BlogHub</span>

<p align="center">
  <strong>An enterprise-grade full-stack blog platform with real-time capabilities</strong>
  <br>
  企业级全栈博客平台 — 实时消息推送 · 主题切换 · 敏感词过滤
</p>

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white" />
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?logo=springboot&logoColor=white" />
  <img alt="Vue" src="https://img.shields.io/badge/Vue-3.4-4FC08D?logo=vuedotjs&logoColor=white" />
  <img alt="MySQL" src="https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white" />
  <img alt="WebSocket" src="https://img.shields.io/badge/WebSocket-Real--time-010101?logo=websocket&logoColor=white" />
  <img alt="License" src="https://img.shields.io/badge/license-MIT-blue.svg" />
</p>

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [System Architecture](#system-architecture)
3. [Technology Stack](#technology-stack)
4. [Feature Matrix](#feature-matrix)
5. [Database Design](#database-design)
6. [API Reference](#api-reference)
7. [Security Design](#security-design)
8. [WebSocket & Real-time Notifications](#websocket--real-time-notifications)
9. [Quick Start](#quick-start)
10. [Project Structure](#project-structure)
11. [Theme System](#theme-system)
12. [Deployment](#deployment)

---

## Project Overview

BlogHub is a production-ready, full-stack blogging platform designed with enterprise-grade architecture patterns. It delivers a complete content management experience with real-time user interaction powered by WebSocket technology.

### Key Differentiators

| Feature | Description |
|---------|-------------|
| **Stateless Auth** | JWT-based authentication — no server-side sessions, ready for horizontal scaling |
| **Raw WebSocket** | Zero-dependency real-time push — no STOMP, no SockJS, native browser API |
| **Session Kick-out** | Cross-device session management via WebSocket registry |
| **Nested Comments** | Multi-level threaded comments with `parent_id` self-referencing FK |
| **Sensitive Word Filter** | Case-insensitive pattern matching on comment submission |
| **Theme Engine** | CSS custom properties with `localStorage` persistence, 14-component coverage |
| **Role-based Access** | `USER` / `ADMIN` roles enforced at filter and controller level |

---

## System Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     Client Browser                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────────┐  │
│  │  Vue 3   │  │  Pinia   │  │  Native WebSocket    │  │
│  │  Router  │  │  Stores  │  │  Client              │  │
│  └────┬─────┘  └────┬─────┘  └──────────┬───────────┘  │
└───────┼──────────────┼──────────────────┼──────────────┘
        │ HTTP/REST    │                  │ ws://
        ▼              │                  ▼
┌─────────────────────────────────────────────────────────┐
│                   Spring Boot 3.2                        │
│                                                         │
│  ┌─────────────┐  ┌──────────────┐  ┌───────────────┐  │
│  │  JWT Filter │  │  Controller  │  │  WebSocket    │  │
│  │  (Stateless │  │  Layer       │  │  Handler      │  │
│  │   Auth)     │  │              │  │               │  │
│  └──────┬──────┘  └──────┬───────┘  └───────┬───────┘  │
│         │                │                   │          │
│         └────────────────┼───────────────────┘          │
│                          ▼                              │
│  ┌──────────────────────────────────────────────────┐   │
│  │               Service Layer                       │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────────────┐  │   │
│  │  │ Article  │ │ Comment  │ │ Notification     │  │   │
│  │  │ Service  │ │ Service  │ │ Service          │  │   │
│  │  └────┬─────┘ └────┬─────┘ └────────┬─────────┘  │   │
│  └───────┼─────────────┼───────────────┼────────────┘   │
│          ▼             ▼               ▼                │
│  ┌──────────────────────────────────────────────────┐   │
│  │           Spring Data JPA Repositories            │   │
│  └──────────────────────┬───────────────────────────┘   │
└─────────────────────────┼───────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────┐
│                    MySQL 8.0                             │
│  ┌────────┐ ┌─────────┐ ┌─────────┐ ┌──────────┐       │
│  │  user  │ │ article │ │ comment │ │ follow   │       │
│  └────────┘ └─────────┘ └─────────┘ └──────────┘       │
│                    ┌──────────────┐                      │
│                    │ notification │                      │
│                    └──────────────┘                      │
└─────────────────────────────────────────────────────────┘
```

### Architectural Decisions

| Decision | Rationale | Trade-offs |
|----------|-----------|------------|
| Raw WebSocket over STOMP | Fewer abstraction layers; easier debugging; zero frontend npm deps | Must implement ping/pong and reconnect manually |
| JWT stateless auth | No server session store; horizontal scaling ready | Cannot revoke individual tokens before expiry |
| `ConcurrentHashMap` for sessions | O(1) lookup; thread-safe; JVM-internal | Lost on restart; single-node only (no Redis fallback) |
| `ddl-auto: update` | Auto-creates tables on first run; no manual migration | Not suitable for production (use Flyway/Liquibase) |
| CSS custom properties | Native browser API; zero build overhead; smooth transitions | IE11 not supported (not a concern) |
| `@Modifying` + `@Query` for unfollow | Guaranteed atomic DELETE JPQL; avoids derived-method pitfalls | Slightly more verbose than derived methods |

---

## Technology Stack

### Backend

| Dependency | Version | Purpose |
|------------|---------|---------|
| JDK | 21 | Runtime — virtual threads, pattern matching, records |
| Spring Boot | 3.2.0 | Application framework — DI, MVC, auto-configuration |
| spring-boot-starter-web | — | REST API + embedded Tomcat 10 |
| spring-boot-starter-data-jpa | — | ORM via Hibernate 6; declarative repositories |
| spring-boot-starter-security | — | Authentication filter chain; BCrypt password hashing |
| spring-boot-starter-websocket | — | `@EnableWebSocket` + `WebSocketConfigurer` |
| spring-boot-starter-validation | — | Jakarta Bean Validation on DTOs |
| mysql-connector-j | — | MySQL 8.0 JDBC Type 4 driver |
| jjwt (api + impl + jackson) | 0.12.5 | HMAC-SHA JWT creation, parsing, and verification |
| Lombok | 1.18.x | `@Data`, constructor generation, boilerplate elimination |

### Frontend

| Dependency | Version | Purpose |
|------------|---------|---------|
| Vue | 3.4.x | Composition API with `<script setup>`, reactive refs |
| Vite | 5.0.x | ESBuild-based dev server; Rollup production builds |
| Vue Router | 4.2.x | Hash-free SPA routing with navigation guards |
| Pinia | 2.1.x | Modular reactive stores — auth, notification, theme |
| Element Plus | 2.4.x | 80+ components — tables, forms, dialogs, pagination |
| Axios | 1.6.x | Promise-based HTTP; request/response interceptors |
| Native WebSocket | — | Browser built-in — `new WebSocket()`, `onmessage`, `send()` |

---

## Feature Matrix

### Core Features

| Category | Feature | Backend Implementation | Frontend Implementation |
|----------|---------|----------------------|------------------------|
| **Auth** | Register/Login | `BCryptPasswordEncoder` + JWT sign | `auth.js` Pinia store + axios interceptor |
| | Role-based guard | `JwtAuthenticationFilter` checks `role` claim | `router.beforeEach` checks `auth.isAdmin` |
| | Session kick-out | `WebSocketSessionRegistry` sends "kicked" to old session | `ws.onmessage` → `handleKicked()` → logout |
| **Articles** | CRUD | `ArticleService` with owner/admin checks | `CreateArticle.vue` / `EditArticle.vue` forms |
| | Search | JPQL `LIKE %keyword%` on title + content | `Home.vue` search bar with debounce |
| | Status toggle | `toggleArticleStatus()` with WebSocket notify | Admin `ArticleManage.vue` table |
| **Comments** | Create with reply | `CommentService.createComment()` validates parent | `ArticleDetail.vue` threaded UI |
| | Nested display | `parent_id` FK → tree grouping in `topLevelComments` | `v-for` top-level + `getReplies()` |
| | Sensitive filter | `SensitiveWordFilter` — `Pattern.quote()` + `(?i)` regex | Transparent to user (auto-replace) |
| | Admin moderation | `toggleCommentStatus()` + `deleteComment()` with notify | Admin `CommentManage.vue` |
| **Social** | Follow/Unfollow | `FollowService` with `@Modifying` JPQL delete | `UserProfile.vue` toggle button |
| | Follow lists | `findByFollowingIdOrderByCreatedAtDesc()` | Tab navigation in profile |
| **Notifications** | Persist + Push | `NotificationServiceImpl` — DB insert + `session.sendMessage()` | `notification.js` store + badge |
| | Read tracking | `countByRecipientIdAndIsRead()` + `markAllAsRead()` JPQL | `Messages.vue` with mark-read buttons |
| **Theme** | Light/Dark | N/A (CSS-only) | CSS vars on `:root` / `html.dark` |
| | Persistence | N/A | `theme.js` Pinia store + `localStorage` |
| **Admin** | Dashboard | `/api/admin/stats` aggregated counts | 4 stat cards with icons |

### Notification Trigger Matrix

| Trigger Point | Type | Recipient | `relatedId` → Navigation |
|---------------|------|-----------|---------------------------|
| Comment on article | `COMMENT` | Article author | `articleId` → `/articles/{id}` |
| Reply to comment | `REPLY` | Parent comment author | `articleId` → `/articles/{id}` |
| Admin toggles article | `POST_TOGGLED` | Article author | `article.getId()` → `/articles/{id}` |
| Admin toggles comment | `COMMENT_TOGGLED` | Comment author | `comment.getArticleId()` → `/articles/{id}` |
| Admin deletes comment | `COMMENT_DELETED` | Comment author | `comment.getArticleId()` → `/articles/{id}` |
| User follows | `FOLLOW` | Followed user | `followerId` → `/users/{id}` |

---

## Database Design

### Entity-Relationship Diagram

```
     ┌──────────┐
     │   user   │
     │──────────│
     │ id (PK)  │◄──────────────────────┐
     │ username │                       │
     │ password │──┐                    │
     │ email    │  │                    │
     │ role     │  │  ┌──────────┐      │
     │ status   │  │  │  follow  │      │
     └────┬─────┘  │  │──────────│      │
          │        │  │ id (PK)  │      │
          │        ├──│ follower │──────┤
          │        │  │ following│──────┘
          │        │  └──────────┘
          │        │
          │        │  ┌───────────┐
          │        │  │  article  │
          │        │  │───────────│
          │        ├──│ id (PK)   │
          │        │  │ title     │
          │        │  │ content   │
          │        │  │ user_id FK│
          │        │  │ status    │
          │        │  └─────┬─────┘
          │        │        │
          │        │  ┌─────▼──────┐
          │        │  │  comment   │
          │        │  │────────────│
          │        ├──│ user_id FK │
          │        │  │ article_id │
          │        │  │ parent_id ◄├── self-referencing FK
          │        │  │ content    │
          │        │  │ status     │
          │        │  └────────────┘
          │        │
          │        │  ┌──────────────┐
          │        └──│ notification │
          │           │──────────────│
          │           │ id (PK)      │
          │           │ recipient_id │
          │           │ type         │
          │           │ content      │
          │           │ related_id   │
          │           │ is_read      │
          └───────────│ created_at   │
                      └──────────────┘
```

### Table Specifications

```sql
user           (id, username UNIQUE, password, email, role DEFAULT 'USER',
                status DEFAULT 1, created_at, updated_at)

article        (id, title, content TEXT, summary, user_id FK→user,
                status DEFAULT 1, created_at, updated_at)

comment        (id, content TEXT, user_id FK→user, article_id FK→article,
                parent_id FK→comment NULLABLE, status DEFAULT 1,
                created_at, updated_at)

follow         (id, follower_id FK→user, following_id FK→user,
                UNIQUE(follower_id, following_id), created_at)

notification   (id, recipient_id FK→user, type VARCHAR(30),
                content VARCHAR(500), related_id, is_read DEFAULT 0, created_at)
```

---

## API Reference

### Authentication

All authenticated endpoints require `Authorization: Bearer <token>`. The JWT payload:

```json
{
  "sub": "<userId>",
  "username": "<username>",
  "role": "USER|ADMIN",
  "iat": 1718000000,
  "exp": 1718086400
}
```

Token lifetime: 24 hours (configurable via `app.jwt.expiration-ms`).

### Public Endpoints

```
GET    /api/articles/public?keyword=&page=0&size=10     Article list with full-text search
GET    /api/articles/{id}                               Article detail with author info
GET    /api/articles/{id}/comments?page=0&size=10       Paginated article comments
GET    /api/users/{id}/profile                          User profile + follower/following counts
GET    /api/users/{id}/articles?page=0&size=10          User's published articles
GET    /api/users/{id}/follow/status                    Follow status + counts
GET    /api/users/{id}/followers                        Follower user list
GET    /api/users/{id}/following                        Following user list
POST   /api/auth/login          { username, password }  Login → returns JWT + user object
POST   /api/auth/register       { username, password, email }  Register new account
```

### Authenticated Endpoints

```
POST   /api/articles                 { title, content, summary }    Create article
PUT    /api/articles/{id}            { title, content, summary }    Update (owner or admin)
DELETE /api/articles/{id}                                           Delete (owner or admin)
POST   /api/articles/{id}/comments   { content, parentId? }         Create comment or reply
DELETE /api/comments/{id}                                           Delete (owner or admin)
POST   /api/users/{id}/follow                                       Follow user
DELETE /api/users/{id}/follow                                       Unfollow user
GET    /api/comments/my?page=0&size=10                              My comment history
GET    /api/articles/my?page=0&size=10                              My article list
GET    /api/notifications?page=0&size=10                            Notification list
GET    /api/notifications/unread-count                              Unread badge count
PUT    /api/notifications/{id}/read                                 Mark single notification read
PUT    /api/notifications/read-all                                  Mark all notifications read
```

### Admin Endpoints

```
GET    /api/admin/stats                                             Dashboard aggregated stats
GET    /api/admin/users                                             All user list
POST   /api/admin/users           { username, password, email, role }  Create user
PUT    /api/admin/users/{id}      { username, password?, email, role }  Update user
DELETE /api/admin/users/{id}                                        Delete user
PUT    /api/admin/users/{id}/status                                 Toggle user active/disabled
GET    /api/admin/articles?page=0&size=10                           All articles
PUT    /api/admin/articles/{id}/status                              Toggle article active/disabled
GET    /api/admin/comments?page=0&size=10                           All comments
PUT    /api/admin/comments/{id}/status                              Toggle comment active/disabled
```

### WebSocket Protocol

```
Endpoint:    ws://localhost:8080/ws?token=<JWT>

Server → Client (notification):
  { "id": 1, "type": "COMMENT", "content": "...", "relatedId": 5, "isRead": 0, "createdAt": "..." }

Server → Client (kick-out):
  { "type": "kicked", "message": "您的账号已在其他设备登录，当前设备已被强制下线" }

Client → Server (heartbeat):
  "ping"  →  Server responds "pong"
```

---

## Security Design

### Filter Chain Execution Order

```
1. JwtAuthenticationFilter (@Order(1))
   ├── extractToken() from Authorization header
   ├── set userId/username/role attributes (if JWT valid)
   ├── WebSocket path?      → pass through
   ├── Public path?          → pass through (with user attrs if logged in)
   ├── OPTIONS?              → pass through
   ├── No valid token?       → 401 Unauthorized
   ├── Admin path + !ADMIN?  → 403 Forbidden
   └── Otherwise             → pass through

2. Spring Security Filter Chain
   └── .anyRequest().permitAll()  (authentication handled by custom filter)
```

**Critical design detail:** User attributes are extracted *before* the public path check. This means public endpoints like `GET /api/users/{id}/profile` receive `currentUserId` when the user is logged in, which enables correct `isFollowing` status detection in the profile response.

### Password Security

```
Registration:  raw password → BCryptPasswordEncoder.encode() → stored hash
Login:         raw password → BCryptPasswordEncoder.matches(storedHash, raw) → boolean
```

BCrypt work factor: 10 (default). No plain-text password storage.

---

## WebSocket & Real-time Notifications

### Why Raw WebSocket Instead of STOMP?

The initial implementation used Spring's STOMP message broker with `SimpMessagingTemplate.convertAndSendToUser()`. This required:

- STOMP client library on the frontend (`@stomp/stompjs` + `sockjs-client` = ~30 KB)
- Complex principal resolution through `HandshakeInterceptor` → `ChannelInterceptor` chain
- Debugging STOMP frame-level issues was time-consuming

The refactored implementation uses Spring's raw `WebSocketHandler`:

- **Zero** npm dependencies for WebSocket
- Transparent session management via `ConcurrentHashMap`
- Simple JSON message format — trivially debuggable
- Direct `session.sendMessage()` calls — no intermediate broker

### Session Lifecycle

```
Connection:
  ws.onopen → server.afterConnectionEstablished()
  → extract JWT from URL query parameter
  → validate token → get userId
  → kick old session: old.send(kicked_msg) → old.close()
  → registry.register(userId, session)

Heartbeat:
  client setInterval(30s) → ws.send("ping")
  server → session.sendMessage("pong")

Disconnection:
  ws.onclose → server.afterConnectionClosed()
  → registry.unregister(userId)
```

### Notification Delivery

```
1. Business event occurs (comment/follow/admin action)
2. Service calls notificationService.sendNotification(userId, type, content, relatedId)
3. Save Notification entity to DB (persistent)
4. session = registry.getSession(userId)
5. if session exists && session.isOpen():
     session.sendMessage(JSON.stringify(notification))
   else:
     // User offline — notification available on next page load via REST API
6. Frontend ws.onmessage:
   → if data.type === "kicked": handleKicked()
   → else: store.addRealtimeNotification() + ElNotification popup
```

---

## Quick Start

### Prerequisites

- **JDK 21** or higher
- **Maven 3.8** or higher
- **MySQL 8.0** or higher
- **Node.js 18** or higher

### Step 1 — Database

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p blog < init.sql
```

### Step 2 — Configuration

Edit `blog-backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    username: <your-db-user>
    password: <your-db-password>
```

### Step 3 — Backend

```bash
cd blog-backend
mvn spring-boot:run
# Starts on http://localhost:8080
# Default admin: admin / admin123 (auto-created by DataInitializer)
```

### Step 4 — Frontend

```bash
cd blog-frontend
npm install
npm run dev
# Starts on http://localhost:5173
```

### Default Accounts

| Role | Username | Password | Source |
|------|----------|----------|--------|
| Admin | `admin` | `admin123` | `DataInitializer.java` |

### URL Map

| Page | URL |
|------|-----|
| Home | `http://localhost:5173/` |
| Login | `http://localhost:5173/login` |
| Register | `http://localhost:5173/register` |
| Messages | `http://localhost:5173/messages` |
| Admin Dashboard | `http://localhost:5173/admin/dashboard` |

---

## Project Structure

```
blog_work/
├── README.md                        # This document
├── sp.md                            # Technical presentation script
├── init.sql                         # Database DDL + seed data
├── sql.sql                          # MySQL dump (backup snapshot)
│
├── blog-backend/                    # Spring Boot 3.2 Application
│   ├── pom.xml
│   └── src/main/java/com/blog/
│       ├── BlogApplication.java              # @SpringBootApplication entry
│       ├── common/
│       │   ├── PageResult.java               # Paginated response DTO
│       │   ├── Result.java                   # { code, message, data } wrapper
│       │   └── RoleEnum.java                 # USER / ADMIN constants
│       ├── config/
│       │   ├── DataInitializer.java          # CommandLineRunner → seed admin
│       │   ├── NotificationWebSocketHandler.java  # WebSocket connect/auth/push
│       │   ├── SecurityConfig.java           # Filter chain + BCrypt bean
│       │   ├── SensitiveWordFilter.java      # Profanity filter (CN/EN)
│       │   ├── WebConfig.java                # CORS /api/**
│       │   ├── WebSocketConfig.java          # WebSocket endpoint /ws
│       │   └── WebSocketSessionRegistry.java # userId ↔ session map
│       ├── controller/                       # 6 REST controllers
│       ├── dto/                              # 5 request body classes
│       ├── entity/                           # 5 JPA @Entity classes
│       ├── exception/                        # @ControllerAdvice handler
│       ├── repository/                       # 4 JpaRepository interfaces
│       ├── security/
│       │   ├── JwtAuthenticationFilter.java  # @Order(1) token filter
│       │   └── JwtTokenProvider.java         # HMAC-SHA sign + verify
│       └── service/                          # 5 interfaces + 4 impls
│
└── blog-frontend/                    # Vue 3 + Vite SPA
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── App.vue                   # Global nav, theme, WebSocket lifecycle
        ├── main.js                   # App bootstrap, plugin registration
        ├── router/index.js           # 10 routes + auth guards
        ├── stores/
        │   ├── auth.js               # Token, user, isLoggedIn, isAdmin
        │   ├── notification.js        # List, unreadCount, CRUD actions
        │   └── theme.js             # isDark, toggle, localStorage sync
        ├── utils/
        │   ├── request.js            # Axios: JWT interceptor, error handler
        │   └── websocket.js          # Native WS: connect, reconnect, kicked
        ├── styles/
        │   └── theme.css             # Element Plus dark mode overrides
        └── views/                    # 14 page components
            ├── Home.vue / ArticleDetail.vue
            ├── Login.vue / Register.vue
            ├── UserProfile.vue / Messages.vue
            ├── CreateArticle.vue / EditArticle.vue
            ├── MyArticles.vue / MyComments.vue
            └── admin/ (Dashboard, UserManage, ArticleManage, CommentManage)
```

---

## Theme System

The theme engine uses 12 CSS custom properties with zero runtime JavaScript overhead in rendering:

```css
:root {
  --bg-primary: #f6f8fa;
  --bg-surface: #ffffff;
  --text-primary: #1f2328;
  --text-secondary: #656d76;
  --text-tertiary: #8c959f;
  --border-default: #d0d7de;
  --border-light: #e8eaed;
  --accent: #409eff;
  --accent-hover: #337ecc;
  --accent-bg: #ecf5ff;
  --nav-bg: #24292f;
}

html.dark {
  --bg-primary: #0d1117;
  --bg-surface: #161b22;
  --text-primary: #e6edf3;
  /* ... all 12 variables remapped for dark */
}
```

All 14 page components exclusively use `var(--*)` in `<style scoped>` — no hardcoded colors. Element Plus components are styled via a separate `theme.css` override sheet. Theme preference is persisted via `localStorage` and a Pinia store, with smooth CSS transitions on toggle.

---

## Deployment

### Production Build

```bash
# Backend
cd blog-backend
mvn clean package -DskipTests
java -jar target/blog-backend-1.0.0.jar

# Frontend
cd blog-frontend
npm run build
# Serve dist/ with nginx or any static file server
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/blog` | DB connection URL |
| `SPRING_DATASOURCE_USERNAME` | `root` | Database user |
| `SPRING_DATASOURCE_PASSWORD` | — | Database password |
| `APP_JWT_SECRET` | (from application.yml) | HMAC-SHA signing key |
| `APP_JWT_EXPIRATION_MS` | `86400000` | Token lifetime in ms |

### Production Considerations

- Replace `ddl-auto: update` with Flyway or Liquibase for schema migration
- Use Redis-backed session registry for multi-node WebSocket
- Set `show-sql: false` and tune Hibernate batch settings
- Enable HTTPS for JWT transmission
- Configure nginx reverse proxy with WebSocket upgrade support

---

## License

MIT License

---

*For a detailed technical walkthrough covering architecture decisions, code patterns, and trade-off analysis, see [sp.md](./sp.md).*

---

# <span id="chinese">BlogHub</span>

<p align="center">
  <strong>企业级具备实时能力的全栈博客平台</strong>
  <br>
  企业级全栈博客平台 — 实时消息推送 · 主题切换 · 敏感词过滤
</p>

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white" />
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?logo=springboot&logoColor=white" />
  <img alt="Vue" src="https://img.shields.io/badge/Vue-3.4-4FC08D?logo=vuedotjs&logoColor=white" />
  <img alt="MySQL" src="https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white" />
  <img alt="WebSocket" src="https://img.shields.io/badge/WebSocket-实时通信-010101?logo=websocket&logoColor=white" />
  <img alt="License" src="https://img.shields.io/badge/许可证-MIT-blue.svg" />
</p>

---

## 目录

1. [项目概述](#项目概述)
2. [系统架构](#系统架构)
3. [技术栈](#技术栈)
4. [功能矩阵](#功能矩阵)
5. [数据库设计](#数据库设计)
6. [接口参考文档](#接口参考文档)
7. [安全设计](#安全设计)
8. [WebSocket 与实时消息通知](#websocket-与实时消息通知)
9. [快速上手](#快速上手)
10. [项目结构](#项目结构)
11. [主题系统](#主题系统)
12. [部署方案](#部署方案)

---

## 项目概述

BlogHub 是一套可直接用于生产环境、采用企业级架构规范开发的全栈博客平台。依托 WebSocket 技术提供完整内容管理能力与实时用户交互体验。

### 核心差异化优势

| 特性 | 说明 |
|---------|-------------|
| **无状态鉴权** | 基于 JWT 认证，不服务端存储会话，支持水平扩容 |
| **原生 WebSocket** | 零依赖实时推送，不使用 STOMP、SockJS，直接调用浏览器原生 API |
| **会话强制下线** | 通过 WebSocket 会话注册表实现跨设备会话管理 |
| **嵌套评论** | 依靠 `parent_id` 自关联外键实现多层级楼中楼评论 |
| **敏感词过滤器** | 提交评论时进行大小写不敏感正则匹配过滤 |
| **主题引擎** | CSS 自定义变量搭配 `localStorage` 持久化，覆盖全部14个页面组件 |
| **基于角色的访问控制** | 在过滤器与控制器双层校验 `USER` / `ADMIN` 角色权限 |

---

## 系统架构

```
┌─────────────────────────────────────────────────────────┐
│                     客户端浏览器                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────────┐  │
│  │  Vue 3   │  │  Pinia   │  │  原生 WebSocket 客户端 │  │
│  │ 路由管理 │  │ 状态存储 │  │                      │  │
│  └────┬─────┘  └────┬─────┘  └──────────┬───────────┘  │
└───────┼──────────────┼──────────────────┼──────────────┘
        │ HTTP/REST    │                  │ ws://
        ▼              │                  ▼
┌─────────────────────────────────────────────────────────┐
│                   Spring Boot 3.2                        │
│                                                         │
│  ┌─────────────┐  ┌──────────────┐  ┌───────────────┐  │
│  │ JWT 过滤器  │  │ 控制器控制层 │  │ WebSocket处理器 │  │
│  │ (无状态鉴权)│  │              │  │               │  │
│  └──────┬──────┘  └──────┬───────┘  └───────┬───────┘  │
│         │                │                   │          │
│         └────────────────┼───────────────────┘          │
│                          ▼                              │
│  ┌──────────────────────────────────────────────────┐   │
│  │               业务服务层                           │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────────────┐  │   │
│  │  │ 博文服务 │ │ 评论服务 │ │ 消息通知服务     │  │   │
│  │  └────┬─────┘ └────┬─────┘ └────────┬─────────┘  │   │
│  └───────┼─────────────┼───────────────┼────────────┘   │
│          ▼             ▼               ▼                │
│  ┌──────────────────────────────────────────────────┐   │
│  │           Spring Data JPA 数据仓库层               │   │
│  └──────────────────────┬───────────────────────────┘   │
└─────────────────────────┼───────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────┐
│                    MySQL 8.0                             │
│  ┌────────┐ ┌─────────┐ ┌─────────┐ ┌──────────┐       │
│  │ 用户表 │ │ 博文表 │ │ 评论表 │ │ 关注关系表 │       │
│  └────────┘ └─────────┘ └─────────┘ └──────────┘       │
│                    ┌──────────────┐                      │
│                    │ 消息通知表   │                      │
│                    └──────────────┘                      │
└─────────────────────────────────────────────────────────┘
```

### 架构选型决策说明

| 选型 | 选型理由 | 取舍与弊端 |
|----------|-----------|------------|
| 原生 WebSocket 替代 STOMP | 更少抽象封装层，便于调试；前端无需额外 NPM 依赖 | 需手动实现心跳保活与断线重连逻辑 |
| JWT 无状态鉴权 | 无需服务端会话存储，天然支持集群水平扩容 | 令牌未过期前无法单独撤销单个用户令牌 |
| 使用 `ConcurrentHashMap` 存储在线会话 | 查询复杂度 O(1)、线程安全、基于 JVM 原生容器 | 服务重启会话全部丢失；仅单机可用，无 Redis 分布式方案兜底 |
| 配置 `ddl-auto: update` | 首次启动自动建表，无需手动执行迁移脚本 | 不适合生产环境，正式环境应改用 Flyway/Liquibase |
| CSS 自定义变量实现主题 | 浏览器原生能力，无打包性能损耗，切换过渡顺滑 | 不兼容 IE11（本项目无需适配） |
| 取关逻辑使用 `@Modifying` + `@Query` | 保证 DELETE JPQL 语句原子性，规避框架衍生查询方法的并发缺陷 | 相比内置查询方法代码略繁琐 |

---

## 技术栈

### 后端

| 依赖组件 | 版本 | 用途 |
|------------|---------|---------|
| JDK | 21 | 运行时 — 支持虚拟线程、模式匹配、记录类语法 |
| Spring Boot | 3.2.0 | 应用框架 — 依赖注入、MVC、自动配置 |
| spring-boot-starter-web | — | REST 接口 + 内置 Tomcat 10 |
| spring-boot-starter-data-jpa | — | 基于 Hibernate 6 的 ORM，声明式数据仓库 |
| spring-boot-starter-security | — | 认证过滤器链路；BCrypt 密码哈希加密 |
| spring-boot-starter-websocket | — | `@EnableWebSocket` + `WebSocketConfigurer` |
| spring-boot-starter-validation | — | DTO 层 Jakarta 参数校验 |
| mysql-connector-j | — | MySQL 8.0 JDBC Type 4 驱动 |
| jjwt (api + impl + jackson) | 0.12.5 | 基于 HMAC-SHA 实现 JWT 创建、解析、校验 |
| Lombok | 1.18.x | 自动生成 `@Data`、构造函数，消除样板代码 |

### 前端

| 依赖组件 | 版本 | 用途 |
|------------|---------|---------|
| Vue | 3.4.x | 组合式 API + `<script setup>`，响应式数据 |
| Vite | 5.0.x | 基于 ESBuild 的开发服务；Rollup 生产打包 |
| Vue Router | 4.2.x | 无哈希 SPA 路由，搭配路由导航守卫 |
| Pinia | 2.1.x | 模块化响应式状态仓库 — 登录、通知、主题 |
| Element Plus | 2.4.x | 80+ UI 组件 — 表格、表单、弹窗、分页 |
| Axios | 1.6.x | Promise 封装 HTTP；统一请求/响应拦截器 |
| 原生 WebSocket | — | 浏览器内置 — `new WebSocket()`、`onmessage`、`send()` |

---

## 功能矩阵

### 核心功能

| 分类 | 功能 | 后端实现 | 前端实现 |
|----------|---------|----------------------|------------------------|
| **账号鉴权** | 注册/登录 | `BCryptPasswordEncoder` + JWT 签发 | Pinia `auth.js` 仓库 + axios 拦截器 |
| | 角色路由拦截 | `JwtAuthenticationFilter` 解析 `role` 载荷 | `router.beforeEach` 判断 `auth.isAdmin` |
| | 会话强制下线 | `WebSocketSessionRegistry` 向旧会话推送下线指令 | `ws.onmessage` → `handleKicked()` → 退出登录 |
| **博文模块** | 增删改查 | `ArticleService` 校验作者/管理员权限 | `CreateArticle.vue` / `EditArticle.vue` 表单 |
| | 搜索功能 | JPQL `LIKE %关键词%` 匹配标题与正文 | `Home.vue` 防抖搜索框 |
| | 状态上下架 | `toggleArticleStatus()` 变更并推送 WebSocket 通知 | 管理员后台 `ArticleManage.vue` 表格 |
| **评论模块** | 发布评论/回复 | `CommentService.createComment()` 校验父评论合法性 | `ArticleDetail.vue` 层级评论 UI |
| | 层级展示 | 依靠 `parent_id` 外键在 `topLevelComments` 组装树形结构 | `v-for` 渲染顶层评论 + `getReplies()` 加载子评论 |
| | 敏感词过滤 | `SensitiveWordFilter` — `Pattern.quote()` + 大小写忽略正则 | 用户无感知自动替换违规词汇 |
| | 管理员审核 | `toggleCommentStatus()` + `deleteComment()` 并推送通知 | 管理员后台 `CommentManage.vue` |
| **社交互动** | 关注/取关 | `FollowService` 使用 `@Modifying` JPQL 删除关联 | `UserProfile.vue` 切换按钮 |
| | 关注/粉丝列表 | `findByFollowingIdOrderByCreatedAtDesc()` 分页查询 | 个人主页标签页切换 |
| **消息通知** | 持久化+实时推送 | `NotificationServiceImpl` — 数据库插入 + `session.sendMessage()` | `notification.js` 仓库 + 未读角标 |
| | 已读状态管理 | `countByRecipientIdAndIsRead()` + `markAllAsRead()` JPQL | `Messages.vue` 一键标为已读按钮 |
| **主题** | 亮色/暗黑模式 | 无后端逻辑（纯 CSS 实现） | 在 `:root` / `html.dark` 定义 CSS 变量 |
| | 配置持久化 | 无后端逻辑 | Pinia `theme.js` + `localStorage` 存储 |
| **管理员** | 数据看板 | `/api/admin/stats` 聚合统计数据 | 四个带图标的统计卡片 |

### 消息通知触发规则表

| 触发场景 | 类型编码 | 接收人 | `relatedId` 跳转路径 |
|---------------|------|-----------|---------------------------|
| 博文收到新评论 | `COMMENT` | 博文作者 | `articleId` → `/articles/{id}` |
| 回复某条评论 | `REPLY` | 父评论作者 | `articleId` → `/articles/{id}` |
| 管理员修改博文状态 | `POST_TOGGLED` | 博文作者 | `article.getId()` → `/articles/{id}` |
| 管理员修改评论状态 | `COMMENT_TOGGLED` | 评论作者 | `comment.getArticleId()` → `/articles/{id}` |
| 管理员删除评论 | `COMMENT_DELETED` | 评论作者 | `comment.getArticleId()` → `/articles/{id}` |
| 用户关注他人 | `FOLLOW` | 被关注用户 | `followerId` → `/users/{id}` |

---

## 数据库设计

### 实体关系 ER 图

```
     ┌──────────┐
     │   user   │ 用户表
     │──────────│
     │ id (主键)│◄──────────────────────┐
     │ 用户名   │                       │
     │ 密码     │──┐                    │
     │ 邮箱     │  │                    │
     │ 角色     │  │  ┌──────────┐      │
     │ 状态     │  │  │  follow  │关注关系表
     └────┬─────┘  │  │──────────│      │
          │        │  │ id (主键)│      │
          │        ├──│ follower │关注人ID──────┤
          │        │  │ following│被关注人ID──────┘
          │        │  └──────────┘
          │        │
          │        │  ┌───────────┐
          │        │  │  article  │博文表
          │        │  │───────────│
          │        ├──│ id (主键) │
          │        │  │ 标题     │
          │        │  │ 正文     │
          │        │  │ user_id 外键│
          │        │  │ 状态     │
          │        │  └─────┬─────┘
          │        │        │
          │        │  ┌─────▼──────┐
          │        │  │  comment   │评论表
          │        │  │────────────│
          │        ├──│ user_id 外键 │
          │        │  │ article_id │
          │        │  │ parent_id ◄├── 自关联外键
          │        │  │ 内容    │
          │        │  │ 状态     │
          │        │  └────────────┘
          │        │
          │        │  ┌──────────────┐
          │        └──│ notification │消息通知表
          │           │──────────────│
          │           │ id (主键)      │
          │           │ recipient_id 接收用户ID │
          │           │ type 通知类型         │
          │           │ content 通知文案      │
          │           │ related_id 关联业务ID   │
          │           │ is_read 是否已读      │
          └───────────│ created_at 创建时间   │
                      └──────────────┘
```

### 数据表字段规范

```sql
user           (id, username 唯一, password, email, role 默认 'USER',
                status 默认 1, created_at, updated_at)

article        (id, title, content 大文本, summary, user_id 外键关联user,
                status 默认 1, created_at, updated_at)

comment        (id, content 大文本, user_id 外键, article_id 外键,
                parent_id 外键关联自身comment 允许为空, status 默认 1,
                created_at, updated_at)

follow         (id, follower_id 关注人外键, following_id 被关注人外键,
                联合唯一约束(follower_id, following_id), created_at)

notification   (id, recipient_id 接收用户外键, type VARCHAR(30) 通知类型,
                content VARCHAR(500) 通知内容, related_id 关联ID, is_read 默认 0 未读, created_at)
```

---

## 接口参考文档

### 鉴权通用规则

所有需要登录的接口请求头必须携带 `Authorization: Bearer <令牌>`。JWT 载荷结构：

```json
{
  "sub": "<用户ID>",
  "username": "<用户名>",
  "role": "USER普通用户|ADMIN管理员",
  "iat": 签发时间戳,
  "exp": 过期时间戳
}
```

令牌有效期：24 小时（可通过配置 `app.jwt.expiration-ms` 修改）。

### 公开免登接口

```
GET    /api/articles/public?keyword=&page=0&size=10     博文列表+全文检索
GET    /api/articles/{id}                               博文详情+作者信息
GET    /api/articles/{id}/comments?page=0&size=10       博文分页评论
GET    /api/users/{id}/profile                          用户主页 + 粉丝/关注数量
GET    /api/users/{id}/articles?page=0&size=10          指定用户已发布博文
GET    /api/users/{id}/follow/status                    双向关注状态与统计
GET    /api/users/{id}/followers                        用户粉丝分页列表
GET    /api/users/{id}/following                        用户关注分页列表
POST   /api/auth/login          { username, password }  登录，返回JWT与用户信息
POST   /api/auth/register       { username, password, email }  注册新账号
```

### 登录授权接口

```
POST   /api/articles                 { title, content, summary }    发布博文
PUT    /api/articles/{id}            { title, content, summary }    编辑博文（作者/管理员）
DELETE /api/articles/{id}                                           删除博文（作者/管理员）
POST   /api/articles/{id}/comments   { content, parentId? }         发表评论/回复
DELETE /api/comments/{id}                                           删除评论（作者/管理员）
POST   /api/users/{id}/follow                                       关注用户
DELETE /api/users/{id}/follow                                       取关用户
GET    /api/comments/my?page=0&size=10                              我的评论历史
GET    /api/articles/my?page=0&size=10                              我的博文列表
GET    /api/notifications?page=0&size=10                            消息通知列表
GET    /api/notifications/unread-count                              未读消息角标数量
PUT    /api/notifications/{id}/read                                 单条标为已读
PUT    /api/notifications/read-all                                  全部标为已读
```

### 管理员专属接口

```
GET    /api/admin/stats                                             后台看板聚合统计数据
GET    /api/admin/users                                             全部用户列表
POST   /api/admin/users           { username, password, email, role }  新建用户
PUT    /api/admin/users/{id}      { username, password?, email, role }  修改用户信息
DELETE /api/admin/users/{id}                                        删除用户
PUT    /api/admin/users/{id}/status                                 启用/禁用用户账号
GET    /api/admin/articles?page=0&size=10                           全站博文列表
PUT    /api/admin/articles/{id}/status                              博文上下架切换
GET    /api/admin/comments?page=0&size=10                           全站评论列表
PUT    /api/admin/comments/{id}/status                              评论启用/隐藏切换
```

### WebSocket 通信协议

```
连接地址:    ws://localhost:8080/ws?token=<JWT令牌>

服务端 → 客户端（消息通知）:
  { "id": 1, "type": "COMMENT", "content": "...", "relatedId": 5, "isRead": 0, "createdAt": "..." }

服务端 → 客户端（强制下线）:
  { "type": "kicked", "message": "您的账号已在其他设备登录，当前设备已被强制下线" }

客户端 → 服务端（心跳保活）:
  "ping"  → 服务端回复 "pong"
```

---

## 安全设计

### 过滤器执行顺序

```
1. JwtAuthenticationFilter (@Order(1))
   ├── 从 Authorization 请求头提取 token
   ├── 解析并挂载 userId/用户名/角色 到请求属性（令牌合法时）
   ├── 当前是 WebSocket 路径？      → 直接放行
   ├── 当前是公开接口？          → 放行（登录状态携带用户属性）
   ├── OPTIONS 跨域预检请求？              → 放行
   ├── 无有效令牌？       → 返回 401 未授权
   ├── 管理员接口且角色非ADMIN？  → 返回 403 禁止访问
   └── 其他情况             → 放行后续链路

2. Spring Security 安全过滤器链
   └── .anyRequest().permitAll() （鉴权逻辑由自定义过滤器全权处理）
```

**关键设计细节：** 用户属性会**先于公开路径判断**完成解析。因此像 `GET /api/users/{id}/profile` 这类公开接口，登录用户访问时可携带当前登录ID，用于正确返回「是否已关注」状态。

### 密码安全机制

```
注册流程: 原始密码 → BCryptPasswordEncoder.encode() → 存储哈希密文
登录校验: 原始密码 → BCryptPasswordEncoder.matches(存储哈希, 原始密码) → 布尔结果
```

BCrypt 迭代强度：10（默认值）。全程不存储明文密码。

---

## WebSocket 与实时消息通知

### 为何选用原生 WebSocket 而非 STOMP？

项目最初版本使用 Spring STOMP 消息代理，依赖 `SimpMessagingTemplate.convertAndSendToUser()`，存在以下问题：
- 前端必须引入 STOMP 客户端库（`@stomp/stompjs` + `sockjs-client`，体积约 30KB）
- 需通过 `HandshakeInterceptor` → `ChannelInterceptor` 复杂链路解析登录身份
- STOMP 帧层级问题调试成本极高

重构后使用 Spring 原生 `WebSocketHandler` 优势：
- WebSocket 前端零 NPM 依赖
- 通过 `ConcurrentHashMap` 透明管理在线会话
- 极简 JSON 消息格式，调试直观
- 直接调用 `session.sendMessage()`，无中间消息代理层

### 会话完整生命周期

```
建立连接:
  ws.onopen → 服务端 afterConnectionEstablished()
  → 从 URL 参数提取 JWT
  → 校验令牌获取 userId
  → 踢出该用户旧会话：旧会话发送下线消息并关闭连接
  → 注册表绑定 userId 与当前会话

心跳保活:
  前端每30秒定时 → ws.send("ping")
  服务端收到后 → session.sendMessage("pong")

断开连接:
  ws.onclose → 服务端 afterConnectionClosed()
  → 从会话注册表移除该用户会话
```

### 消息推送完整流程

```
1. 业务事件触发（评论/关注/管理员操作）
2. 业务层调用 notificationService.sendNotification(用户ID, 通知类型, 内容, 关联ID)
3. 将通知持久化存入数据库（离线用户后续可接口读取）
4. 从会话注册表查询该用户在线会话
5. 若会话存在且处于打开状态：
     session.sendMessage(JSON.stringify(通知对象))
   否则：
     // 用户离线，下次页面加载通过REST接口拉取历史通知
6. 前端 ws.onmessage 监听消息：
   → 如果 type 为 "kicked"：执行下线逻辑
   → 否则：存入通知仓库 + 弹出 ElementPlus 消息提示
```

---

## 快速上手

### 环境前置要求
- **JDK 21** 或更高版本
- **Maven 3.8** 或更高版本
- **MySQL 8.0** 或更高版本
- **Node.js 18** 或更高版本

### 步骤1 — 初始化数据库
```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p blog < init.sql
```

### 步骤2 — 配置文件修改
编辑 `blog-backend/src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    username: <你的数据库账号>
    password: <你的数据库密码>
```

### 步骤3 — 启动后端
```bash
cd blog-backend
mvn spring-boot:run
# 服务地址 http://localhost:8080
# 默认管理员账号：admin / admin123（由 DataInitializer 自动初始化）
```

### 步骤4 — 启动前端
```bash
cd blog-frontend
npm install
npm run dev
# 前端访问地址 http://localhost:5173
```

### 默认账号信息
| 角色 | 用户名 | 密码 | 来源 |
|------|----------|----------|--------|
| 管理员 | `admin` | `admin123` | `DataInitializer.java` |

### 页面地址对照表
| 页面 | 访问地址 |
|------|-----|
| 首页 | `http://localhost:5173/` |
| 登录页 | `http://localhost:5173/login` |
| 注册页 | `http://localhost:5173/register` |
| 消息通知页 | `http://localhost:5173/messages` |
| 管理员数据看板 | `http://localhost:5173/admin/dashboard` |

---

## 项目结构

```
blog_work/
├── README.md                        # 本文档
├── sp.md                            # 技术汇报演示文稿
├── init.sql                         # 数据库建表语句 + 初始化数据
├── sql.sql                          # MySQL 备份快照
│
├── blog-backend/                    # Spring Boot 3.2 后端工程
│   ├── pom.xml
│   └── src/main/java/com/blog/
│       ├── BlogApplication.java              # 程序入口 @SpringBootApplication
│       ├── common/
│       │   ├── PageResult.java               # 分页统一返回DTO
│       │   ├── Result.java                   # 全局统一返回体 { code, message, data }
│       │   └── RoleEnum.java                 # USER / ADMIN 角色枚举
│       ├── config/
│       │   ├── DataInitializer.java          # CommandLineRunner 初始化管理员账号
│       │   ├── NotificationWebSocketHandler.java  # WebSocket连接鉴权与消息推送
│       │   ├── SecurityConfig.java           # 安全过滤器链 + BCrypt 加密Bean
│       │   ├── SensitiveWordFilter.java      # 中英文敏感词过滤工具
│       │   ├── WebConfig.java                # 全局跨域 CORS 配置 /api/**
│       │   ├── WebSocketConfig.java          # WebSocket 端点 /ws 配置
│       │   └── WebSocketSessionRegistry.java # userId ↔ 在线会话映射管理
│       ├── controller/                       # 6 个 REST 接口控制器
│       ├── dto/                              # 5 个请求入参实体类
│       ├── entity/                           # 5 个 JPA 数据库实体
│       ├── exception/                        # 全局异常统一拦截 @ControllerAdvice
│       ├── repository/                       # 4 个 JpaRepository 数据仓库
│       ├── security/
│       │   ├── JwtAuthenticationFilter.java  # 优先级1的令牌鉴权过滤器
│       │   └── JwtTokenProvider.java         # HMAC-SHA 签发、校验JWT工具
│       └── service/                          # 5 套业务接口 + 4 套实现类
│
└── blog-frontend/                    # Vue 3 + Vite 单页应用
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── App.vue                   # 全局导航栏、主题管理、WebSocket生命周期
        ├── main.js                   # 项目入口，注册全部插件
        ├── router/index.js           # 10条路由 + 登录权限守卫
        ├── stores/
        │   ├── auth.js               # 令牌、用户信息、登录/管理员状态
        │   ├── notification.js        # 消息列表、未读计数、消息操作
        │   └── theme.js             # 暗黑模式切换、localStorage同步
        ├── utils/
        │   ├── request.js            # Axios封装：JWT拦截、统一错误处理
        │   └── websocket.js          # 原生WS封装：连接、重连、下线处理
        ├── styles/
        │   └── theme.css             # Element Plus 暗黑模式样式覆盖
        └── views/                    # 14 个页面组件
            ├── Home.vue / ArticleDetail.vue
            ├── Login.vue / Register.vue
            ├── UserProfile.vue / Messages.vue
            ├── CreateArticle.vue / EditArticle.vue
            ├── MyArticles.vue / MyComments.vue
            └── admin/ (数据看板、用户管理、博文管理、评论管理)
```

---

## 主题系统

主题引擎使用 12 个 CSS 自定义变量，页面渲染无任何 JS 性能损耗：
```css
:root {
  --bg-primary: #f6f8fa;
  --bg-surface: #ffffff;
  --text-primary: #1f2328;
  --text-secondary: #656d76;
  --text-tertiary: #8c959f;
  --border-default: #d0d7de;
  --border-light: #e8eaed;
  --accent: #409eff;
  --accent-hover: #337ecc;
  --accent-bg: #ecf5ff;
  --nav-bg: #24292f;
}

html.dark {
  --bg-primary: #0d1117;
  --bg-surface: #161b22;
  --text-primary: #e6edf3;
  /* 剩余10个变量全部重新映射适配暗黑模式 */
}
```

全站14个页面组件全部在 `<style scoped>` 中使用 `var(--*)` 变量，无硬编码色值。Element Plus 组件样式通过独立 `theme.css` 文件覆盖重写。用户主题偏好存入 `localStorage`，由 Pinia 仓库管理，切换时 CSS 过渡动画顺滑。

---

## 部署方案

### 生产环境打包构建
```bash
# 后端打包
cd blog-backend
mvn clean package -DskipTests
java -jar target/blog-backend-1.0.0.jar

# 前端打包
cd blog-frontend
npm run build
# 将 dist/ 目录使用 Nginx 或任意静态文件服务器托管
```

### 环境变量配置表
| 环境变量 | 默认值 | 说明 |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/blog` | 数据库连接地址 |
| `SPRING_DATASOURCE_USERNAME` | `root` | 数据库账号 |
| `SPRING_DATASOURCE_PASSWORD` | — | 数据库密码 |
| `APP_JWT_SECRET` | (取自application.yml) | HMAC-SHA 签名密钥 |
| `APP_JWT_EXPIRATION_MS` | `86400000` | 令牌有效期，单位毫秒 |

### 生产环境优化注意事项
- 移除 `ddl-auto: update`，改用 Flyway / Liquibase 管理数据库版本迁移
- 多节点集群部署 WebSocket 时，使用 Redis 实现分布式会话注册表
- 关闭 SQL 日志打印 `show-sql: false`，优化 Hibernate 批量插入参数
- 全站启用 HTTPS 传输，防止 JWT 令牌明文泄露
- Nginx 反向代理配置 WebSocket Upgrade 升级头支持

---

## 开源许可证

MIT 许可证

---

*如需查看包含架构选型、代码规范、取舍分析的完整技术讲解文档，请参阅 [sp.md](./sp.md)。*

