<template>
  <div class="page">
    <div class="content-layout">
      <div class="main-col">
        <div class="page-header">
          <h1>我的文章</h1>
          <el-button type="primary" @click="$router.push('/articles/create')">
            <el-icon><Edit /></el-icon>写文章
          </el-button>
        </div>

        <div v-for="article in articles" :key="article.id" class="article-card">
          <div class="card-main" @click="$router.push(`/articles/${article.id}`)">
            <h3 class="card-title">{{ article.title }}</h3>
            <p class="card-summary">{{ article.summary || article.content?.substring(0, 200) }}</p>
            <div class="card-meta">
              <span>{{ formatDate(article.createdAt) }}</span>
              <el-tag :type="article.status === 1 ? 'success' : 'danger'" size="small">
                {{ article.status === 1 ? '已发布' : '已禁用' }}
              </el-tag>
            </div>
          </div>
          <div class="card-actions">
            <el-button size="small" @click="$router.push(`/articles/${article.id}/edit`)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(article.id)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </div>
        </div>

        <el-empty v-if="articles.length === 0" description="还没有写过文章" />

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
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const articles = ref([])
const total = ref(0)
const currentPage = ref(1)

async function fetchArticles() {
  const params = { page: currentPage.value - 1, size: 10 }
  const res = await request.get('/articles/my', { params })
  const pageResult = res.data || {}
  articles.value = pageResult.records || []
  total.value = pageResult.total || 0
}

function onPageChange(page) { currentPage.value = page; fetchArticles() }

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除此文章？此操作不可撤销。', '确认删除', { type: 'warning' })
  await request.delete(`/articles/${id}`)
  ElMessage.success('删除成功')
  fetchArticles()
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.substring(0, 10) + ' ' + dateStr.substring(11, 16)
}

onMounted(fetchArticles)
</script>

<style scoped>
.page { max-width: 900px; margin: 0 auto; padding: 24px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h1 { font-size: 24px; font-weight: 700; }
.article-card {
  background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius);
  padding: 20px; margin-bottom: 12px; display: flex; justify-content: space-between;
  align-items: flex-start; gap: 16px; transition: all .15s;
}
.article-card:hover { border-color: var(--accent); }
.card-main { flex: 1; cursor: pointer; min-width: 0; }
.card-title { font-size: 17px; font-weight: 600; margin-bottom: 6px; }
.card-summary { font-size: 13px; color: var(--text-secondary); display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.card-meta { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; font-size: 12px; color: var(--text-tertiary); }
.card-actions { display: flex; gap: 8px; flex-shrink: 0; }
.pagination-wrap { display: flex; justify-content: center; margin: 24px 0; }
</style>
