<template>
  <div class="admin-page">
    <!-- 管理子导航 -->
    <div class="admin-nav">
      <div class="nav-inner">
        <router-link to="/admin/dashboard" class="nav-item" :class="{ active: $route.path === '/admin/dashboard' }">
          <el-icon><DataAnalysis /></el-icon>控制台
        </router-link>
        <router-link to="/admin/users" class="nav-item">
          <el-icon><User /></el-icon>用户管理
        </router-link>
        <router-link to="/admin/articles" class="nav-item">
          <el-icon><Document /></el-icon>文章管理
        </router-link>
        <router-link to="/admin/comments" class="nav-item">
          <el-icon><ChatDotSquare /></el-icon>评论管理
        </router-link>
      </div>
    </div>

    <div class="page-content">
      <h1 class="page-title">管理控制台</h1>
      <div class="stats-grid">
        <div class="stat-card" v-for="s in statItems" :key="s.label">
          <div class="stat-icon" :style="{ background: s.bg }">
            <el-icon :size="28" :color="s.color"><component :is="s.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-num">{{ s.value }}</div>
            <div class="stat-label">{{ s.label }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '../../utils/request'

const stats = ref({ userCount: 0, articleCount: 0, commentCount: 0, followCount: 0 })

const statItems = computed(() => [
  { label: '用户总数', value: stats.value.userCount, icon: 'User', color: '#409eff', bg: '#ecf5ff' },
  { label: '文章总数', value: stats.value.articleCount, icon: 'Document', color: '#2da44e', bg: '#e6f4ea' },
  { label: '评论总数', value: stats.value.commentCount, icon: 'ChatDotSquare', color: '#e36209', bg: '#fff3e6' },
  { label: '关注关系', value: stats.value.followCount, icon: 'Star', color: '#8250df', bg: '#f3f0ff' }
])

async function fetchStats() {
  const res = await request.get('/admin/stats')
  stats.value = res.data || {}
}

onMounted(fetchStats)
</script>

<style scoped>
.admin-page { max-width: 1200px; margin: 0 auto; padding: 0 24px 24px; }

/* Admin sub-nav */
.admin-nav {
  margin: 0 -24px; padding: 0 24px;
  background: var(--bg-surface); border-bottom: 1px solid var(--border-default);
}
.nav-inner { display: flex; gap: 0; }
.nav-item {
  display: flex; align-items: center; gap: 6px; padding: 12px 16px;
  font-size: 14px; font-weight: 500; color: var(--text-secondary); border-bottom: 2px solid transparent;
  text-decoration: none; transition: all .15s;
}
.nav-item:hover { color: var(--text-primary); background: var(--bg-primary); }
.nav-item.active, .nav-item.router-link-active { color: var(--text-primary); border-bottom-color: var(--accent); }

/* Content */
.page-content { margin-top: 24px; }
.page-title { font-size: 24px; font-weight: 700; margin-bottom: 24px; }

/* Stats */
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.stat-card {
  background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius);
  padding: 24px; display: flex; align-items: center; gap: 16px;
}
.stat-icon {
  width: 56px; height: 56px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.stat-num { font-size: 32px; font-weight: 700; color: var(--text-primary); line-height: 1.1; }
.stat-label { font-size: 13px; color: var(--text-secondary); margin-top: 2px; }

@media (max-width: 768px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
