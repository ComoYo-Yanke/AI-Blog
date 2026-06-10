<template>
  <div class="admin-page">
    <div class="admin-nav">
      <div class="nav-inner">
        <router-link to="/admin/dashboard" class="nav-item">
          <el-icon><DataAnalysis /></el-icon>控制台
        </router-link>
        <router-link to="/admin/users" class="nav-item">
          <el-icon><User /></el-icon>用户管理
        </router-link>
        <router-link to="/admin/articles" class="nav-item active">
          <el-icon><Document /></el-icon>文章管理
        </router-link>
        <router-link to="/admin/comments" class="nav-item">
          <el-icon><ChatDotSquare /></el-icon>评论管理
        </router-link>
      </div>
    </div>

    <div class="page-content">
      <h1 class="page-title">文章管理</h1>
      <div class="table-wrap">
        <el-table :data="articles" border stripe>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
          <el-table-column label="作者" width="140">
            <template #default="{ row }">
              <span class="link" @click="$router.push(`/users/${row.userId}`)">
                {{ row.username || '用户' + row.userId }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="170">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" @click="$router.push(`/articles/${row.id}`)">查看</el-button>
              <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'"
                @click="toggleStatus(row.id)">
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-wrap" v-if="total > 10">
          <el-pagination background layout="prev, pager, next" :total="total"
            :page-size="10" :current-page="currentPage" @current-change="onPageChange" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const articles = ref([])
const total = ref(0)
const currentPage = ref(1)

async function fetchArticles() {
  const params = { page: currentPage.value - 1, size: 10 }
  const res = await request.get('/admin/articles', { params })
  const pageResult = res.data || {}
  articles.value = pageResult.records || []
  total.value = pageResult.total || 0
}

function onPageChange(page) { currentPage.value = page; fetchArticles() }

async function toggleStatus(id) {
  await request.put(`/admin/articles/${id}/status`)
  ElMessage.success('操作成功')
  fetchArticles()
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.substring(0, 10) + ' ' + dateStr.substring(11, 16)
}

onMounted(fetchArticles)
</script>

<style scoped>
.admin-page { max-width: 1200px; margin: 0 auto; padding: 0 24px 24px; }
.admin-nav { margin: 0 -24px; padding: 0 24px; background: var(--bg-surface); border-bottom: 1px solid var(--border-default); }
.nav-inner { display: flex; gap: 0; }
.nav-item {
  display: flex; align-items: center; gap: 6px; padding: 12px 16px;
  font-size: 14px; font-weight: 500; color: var(--text-secondary); border-bottom: 2px solid transparent;
  text-decoration: none; transition: all .15s;
}
.nav-item:hover { color: var(--text-primary); background: var(--bg-hover); }
.nav-item.active { color: var(--text-primary); border-bottom-color: var(--accent); }
.page-content { margin-top: 24px; }
.page-title { font-size: 24px; font-weight: 700; margin-bottom: 20px; }
.table-wrap { background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius); padding: 16px; }
.link { cursor: pointer; color: var(--accent); }
.link:hover { text-decoration: underline; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }
</style>
