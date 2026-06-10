<template>
  <div class="app">
    <header class="global-nav">
      <div class="nav-inner">
        <div class="nav-left">
          <router-link to="/" class="logo">
            <el-icon :size="24"><Reading /></el-icon>
            <span class="logo-text">BlogHub</span>
          </router-link>
          <router-link to="/" class="nav-link">首页</router-link>
        </div>
        <div class="nav-right">
          <!-- 主题切换 -->
          <button class="icon-btn theme-btn" @click="theme.toggle()" :title="theme.isDark ? '切换浅色' : '切换深色'">
            <el-icon :size="18"><Sunny v-if="theme.isDark" /><Moon v-else /></el-icon>
          </button>

          <template v-if="auth.isLoggedIn">
            <router-link to="/messages" class="icon-btn" title="消息">
              <el-badge :value="notifStore.unreadCount" :hidden="notifStore.unreadCount === 0" :max="99">
                <el-icon :size="20"><Bell /></el-icon>
              </el-badge>
            </router-link>
            <router-link to="/articles/create" class="icon-btn" title="写文章">
              <el-icon :size="20"><Edit /></el-icon>
            </router-link>
            <el-dropdown trigger="click">
              <span class="user-menu">
                <el-avatar :size="28" class="nav-avatar">
                  {{ auth.user?.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <span class="username">{{ auth.user?.username }}</span>
                <el-icon :size="14"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push(`/users/${auth.user?.id}`)">
                    <el-icon><User /></el-icon>我的主页
                  </el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/my/articles')">
                    <el-icon><Document /></el-icon>我的文章
                  </el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/my/comments')">
                    <el-icon><ChatDotSquare /></el-icon>我的评论
                  </el-dropdown-item>
                  <el-dropdown-item v-if="auth.isAdmin" divided @click="$router.push('/admin/dashboard')">
                    <el-icon><Setting /></el-icon>管理后台
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link to="/login" class="btn-text">登录</router-link>
            <router-link to="/register" class="btn-primary">注册</router-link>
          </template>
        </div>
      </div>
    </header>

    <main class="main-content">
      <router-view />
    </main>

    <footer class="global-footer">
      <p>BlogHub &mdash; 一个简洁的博客平台</p>
    </footer>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElNotification } from 'element-plus'
import { useAuthStore } from './stores/auth'
import { useNotificationStore } from './stores/notification'
import { useThemeStore } from './stores/theme'
import { connectWebSocket, disconnectWebSocket } from './utils/websocket'

const router = useRouter()
const auth = useAuthStore()
const notifStore = useNotificationStore()
const theme = useThemeStore()

function handleLogout() {
  auth.logout()
  disconnectWebSocket()
  router.push('/')
}

function handleKicked(message) {
  auth.logout()
  disconnectWebSocket()
  ElMessage.warning(message || '您的账号已在其他设备登录')
  router.push('/login')
}

function handleRealtimeNotification(data) {
  notifStore.addRealtimeNotification(data)
  ElNotification({
    title: typeLabel(data.type),
    message: data.content,
    type: 'info',
    duration: 5000,
    onClick: () => router.push('/messages')
  })
}

function typeLabel(type) {
  const map = {
    COMMENT: '新评论', REPLY: '新回复', POST_TOGGLED: '文章状态变更',
    COMMENT_TOGGLED: '评论状态变更', COMMENT_DELETED: '评论被删除', FOLLOW: '新关注'
  }
  return map[type] || '新消息'
}

function startWebSocket() {
  notifStore.fetchUnreadCount()
  connectWebSocket(handleRealtimeNotification, handleKicked)
}

watch(() => auth.isLoggedIn, (loggedIn) => {
  if (loggedIn) startWebSocket()
  else disconnectWebSocket()
})

onMounted(() => {
  if (auth.isLoggedIn) startWebSocket()
})

onUnmounted(() => {
  disconnectWebSocket()
})
</script>

<style>
/* ===== Theme Variables ===== */
:root {
  --bg-primary: #f6f8fa;
  --bg-surface: #ffffff;
  --bg-hover: #f3f4f6;
  --bg-active: #ecf5ff;
  --text-primary: #1f2328;
  --text-secondary: #656d76;
  --text-tertiary: #8c959f;
  --border-default: #d0d7de;
  --border-light: #e8eaed;
  --shadow-sm: 0 1px 3px rgba(0,0,0,.06);
  --shadow-md: 0 4px 12px rgba(0,0,0,.08);
  --accent: #409eff;
  --accent-hover: #337ecc;
  --accent-bg: #ecf5ff;
  --nav-bg: #24292f;
  --nav-text: #ffffff;
  --nav-text-dim: rgba(255,255,255,.75);
  --nav-hover: rgba(255,255,255,.1);
  --danger: #e5534b;
  --success: #2da44e;
  --warning: #e36209;
  --radius: 8px;
  --transition: 0.2s ease;
}

html.dark {
  --bg-primary: #0d1117;
  --bg-surface: #161b22;
  --bg-hover: #1c2129;
  --bg-active: #1a2d42;
  --text-primary: #e6edf3;
  --text-secondary: #8b949e;
  --text-tertiary: #6e7681;
  --border-default: #30363d;
  --border-light: #21262d;
  --shadow-sm: 0 1px 3px rgba(0,0,0,.3);
  --shadow-md: 0 4px 12px rgba(0,0,0,.4);
  --accent: #58a6ff;
  --accent-hover: #79b8ff;
  --accent-bg: #1a2d42;
  --nav-bg: #161b22;
  --nav-text: #e6edf3;
  --nav-text-dim: rgba(230,237,243,.75);
  --nav-hover: rgba(255,255,255,.08);
}

/* ===== Reset & Base ===== */
*, *::before, *::after { margin: 0; padding: 0; box-sizing: border-box; }
html { transition: background var(--transition), color var(--transition); }
body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Noto Sans SC', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: var(--bg-primary);
  color: var(--text-primary);
  line-height: 1.6;
  -webkit-font-smoothing: antialiased;
  transition: background var(--transition), color var(--transition);
}
a { text-decoration: none; color: inherit; }

/* ===== Global Nav ===== */
.global-nav {
  background: var(--nav-bg);
  color: var(--nav-text);
  height: 56px;
  display: flex;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 1px 3px rgba(0,0,0,.2);
  transition: background var(--transition);
}
.nav-inner {
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.nav-left, .nav-right { display: flex; align-items: center; gap: 12px; }
.logo {
  display: flex; align-items: center; gap: 8px;
  color: var(--nav-text); font-weight: 700; font-size: 18px;
}
.logo-text { letter-spacing: -0.5px; }
.nav-link {
  color: var(--nav-text-dim); font-size: 14px; font-weight: 500;
  padding: 4px 10px; border-radius: 6px; transition: all .15s;
}
.nav-link:hover, .nav-link.router-link-active { color: var(--nav-text); background: var(--nav-hover); }
.icon-btn {
  color: var(--nav-text-dim); padding: 4px 6px; border-radius: 6px; transition: all .15s;
  display: flex; align-items: center; background: none; border: none; cursor: pointer;
}
.icon-btn:hover { color: var(--nav-text); background: var(--nav-hover); }
.theme-btn { font-size: 14px; }
.user-menu {
  display: flex; align-items: center; gap: 6px; cursor: pointer;
  padding: 4px 8px; border-radius: 6px; transition: all .15s;
  color: var(--nav-text-dim);
}
.user-menu:hover { background: var(--nav-hover); color: var(--nav-text); }
.username { font-size: 14px; font-weight: 500; }
.nav-avatar { background: var(--accent) !important; font-weight: 600; }
.btn-text {
  color: var(--nav-text-dim); font-size: 14px; font-weight: 500;
  padding: 6px 12px; border-radius: 6px; transition: all .15s;
}
.btn-text:hover { background: var(--nav-hover); }
.btn-primary {
  color: #fff; font-size: 14px; font-weight: 500;
  background: var(--accent); padding: 6px 14px; border-radius: 6px; transition: all .15s;
}
.btn-primary:hover { background: var(--accent-hover); }

/* ===== Main Content ===== */
.main-content { min-height: calc(100vh - 56px - 60px); }

/* ===== Global Footer ===== */
.global-footer {
  text-align: center; padding: 20px; color: var(--text-secondary); font-size: 13px;
  border-top: 1px solid var(--border-default);
  transition: background var(--transition), color var(--transition), border var(--transition);
}
</style>
