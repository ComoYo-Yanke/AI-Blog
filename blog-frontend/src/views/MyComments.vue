<template>
  <div class="page">
    <div class="content-layout">
      <div class="main-col">
        <div class="page-header">
          <h1>我的评论</h1>
        </div>

        <div v-for="c in comments" :key="c.id" class="comment-card">
          <div class="comment-body">
            <p class="comment-text">{{ c.content }}</p>
            <div class="comment-meta">
              <span class="clickable" @click="$router.push(`/articles/${c.articleId}`)">查看原文</span>
              <span class="comment-time">{{ formatDate(c.createdAt) }}</span>
            </div>
          </div>
          <el-button type="danger" size="small" @click="handleDelete(c.id)">
            <el-icon><Delete /></el-icon>删除
          </el-button>
        </div>

        <el-empty v-if="comments.length === 0" description="还没有发表过评论" />

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

const comments = ref([])
const total = ref(0)
const currentPage = ref(1)

async function fetchComments() {
  const params = { page: currentPage.value - 1, size: 10 }
  const res = await request.get('/comments/my', { params })
  const pageResult = res.data || {}
  comments.value = pageResult.records || []
  total.value = pageResult.total || 0
}

function onPageChange(page) { currentPage.value = page; fetchComments() }

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除此评论？', '确认删除', { type: 'warning' })
  await request.delete(`/comments/${id}`)
  ElMessage.success('删除成功')
  fetchComments()
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.substring(0, 10) + ' ' + dateStr.substring(11, 16)
}

onMounted(fetchComments)
</script>

<style scoped>
.page { max-width: 900px; margin: 0 auto; padding: 24px; }
.page-header { margin-bottom: 24px; }
.page-header h1 { font-size: 24px; font-weight: 700; }
.comment-card {
  background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius);
  padding: 20px; margin-bottom: 12px; display: flex; justify-content: space-between;
  align-items: flex-start; gap: 16px; transition: all .15s;
}
.comment-card:hover { border-color: var(--accent); }
.comment-body { flex: 1; min-width: 0; }
.comment-text { font-size: 14px; color: var(--text-primary); line-height: 1.6; margin-bottom: 10px; }
.comment-meta { display: flex; justify-content: space-between; font-size: 12px; color: var(--text-tertiary); }
.clickable { cursor: pointer; color: var(--accent); }
.clickable:hover { text-decoration: underline; }
.comment-time { color: var(--text-tertiary); }
.pagination-wrap { display: flex; justify-content: center; margin: 24px 0; }
</style>
