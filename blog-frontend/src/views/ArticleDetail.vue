<template>
  <div class="article-page">
    <div class="content-layout">
      <div class="main-col">
        <!-- 文章内容 -->
        <article v-if="article" class="article-card">
          <h1 class="article-title">{{ article.title }}</h1>
          <div class="article-meta">
            <span class="meta-author" @click="$router.push(`/users/${article.userId}`)">
              <el-avatar :size="28" class="author-avatar">
                {{ article.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <span class="author-name">{{ article.username || '用户' + article.userId }}</span>
            </span>
            <span class="meta-date">{{ formatDate(article.createdAt) }}</span>
            <el-tag v-if="article.status === 0" type="danger" size="small">已禁用</el-tag>
          </div>
          <div class="article-body" v-html="renderContent(article.content)"></div>
          <div v-if="canEdit" class="article-actions">
            <el-button @click="$router.push(`/articles/${article.id}/edit`)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button type="danger" @click="handleDelete">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </div>
        </article>
        <el-skeleton v-else :rows="8" animated />

        <!-- 评论区域 -->
        <section class="comments-section">
          <h2 class="section-title">评论 ({{ totalComments }})</h2>

          <div v-if="auth.isLoggedIn" class="comment-form">
            <el-avatar :size="32" class="form-avatar">
              {{ auth.user?.username?.charAt(0)?.toUpperCase() }}
            </el-avatar>
            <div class="form-body">
              <el-input v-model="commentContent" type="textarea" :rows="3"
                placeholder="写下你的评论..." resize="none" />
              <div class="form-footer">
                <span class="form-hint">支持 Markdown 语法</span>
                <el-button type="primary" :disabled="!commentContent.trim()" @click="submitComment">
                  发表评论
                </el-button>
              </div>
            </div>
          </div>
          <div v-else class="login-hint">
            请 <router-link to="/login">登录</router-link> 后参与评论
          </div>

          <!-- 评论列表 -->
          <div v-if="comments.length === 0" class="no-comments">暂无评论，来说点什么吧</div>

          <div v-for="topComment in topLevelComments" :key="topComment.id" class="comment-thread">
            <!-- 父评论 -->
            <div class="comment-card">
              <div class="comment-avatar-col">
                <el-avatar :size="36" class="comment-avatar">
                  {{ topComment.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
              </div>
              <div class="comment-body">
                <div class="comment-header">
                  <span class="comment-author" @click="$router.push(`/users/${topComment.userId}`)">
                    {{ topComment.username || '用户' + topComment.userId }}
                  </span>
                  <span class="comment-time">{{ formatDate(topComment.createdAt) }}</span>
                </div>
                <div class="comment-text">{{ topComment.content }}</div>
                <div class="comment-actions">
                  <button v-if="auth.isLoggedIn" class="action-btn" @click="startReply(topComment)">
                    <el-icon :size="14"><ChatDotSquare /></el-icon>回复
                  </button>
                  <button v-if="canDeleteComment(topComment)" class="action-btn danger"
                    @click="deleteComment(topComment.id)">
                    <el-icon :size="14"><Delete /></el-icon>删除
                  </button>
                </div>
              </div>
            </div>

            <!-- 回复评论 -->
            <div v-for="reply in getReplies(topComment.id)" :key="reply.id" class="comment-card reply-card">
              <div class="comment-avatar-col">
                <el-avatar :size="28" class="comment-avatar small">
                  {{ reply.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
              </div>
              <div class="comment-body">
                <div class="comment-header">
                  <span class="comment-author" @click="$router.push(`/users/${reply.userId}`)">
                    {{ reply.username || '用户' + reply.userId }}
                  </span>
                  <span v-if="reply.replyToUsername" class="reply-to">
                    回复 <strong>@{{ reply.replyToUsername }}</strong>
                  </span>
                  <span class="comment-time">{{ formatDate(reply.createdAt) }}</span>
                </div>
                <div class="comment-text">{{ reply.content }}</div>
                <div class="comment-actions">
                  <button v-if="auth.isLoggedIn" class="action-btn" @click="startReply(topComment)">
                    <el-icon :size="14"><ChatDotSquare /></el-icon>回复
                  </button>
                  <button v-if="canDeleteComment(reply)" class="action-btn danger"
                    @click="deleteComment(reply.id)">
                    <el-icon :size="14"><Delete /></el-icon>删除
                  </button>
                </div>
              </div>
            </div>

            <!-- 回复输入框 -->
            <div v-if="replyTarget?.id === topComment.id" class="reply-editor">
              <el-avatar :size="28" class="comment-avatar small form-avatar">
                {{ auth.user?.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <div class="form-body">
                <el-input v-model="replyContent" type="textarea" :rows="2"
                  :placeholder="'回复 @' + replyTarget.username" resize="none" />
                <div class="form-footer" style="margin-top:8px">
                  <span></span>
                  <div style="display:flex;gap:8px">
                    <el-button size="small" @click="cancelReply">取消</el-button>
                    <el-button type="primary" size="small" :disabled="!replyContent.trim()"
                      @click="submitReply">发送</el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="pagination-wrap" v-if="totalComments > 5">
            <el-pagination background layout="prev, pager, next" :total="totalComments"
              :page-size="5" :current-page="commentCurrentPage" @current-change="onCommentPageChange" />
          </div>
        </section>
      </div>

      <!-- 作者信息侧边栏 -->
      <aside class="side-col">
        <div v-if="article" class="side-card author-card">
          <div class="author-card-top" @click="$router.push(`/users/${article.userId}`)">
            <el-avatar :size="56" class="author-big-avatar">
              {{ article.username?.charAt(0)?.toUpperCase() }}
            </el-avatar>
            <h3>{{ article.username }}</h3>
          </div>
          <el-button style="width:100%;margin-top:12px" @click="$router.push(`/users/${article.userId}`)">
            查看主页
          </el-button>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const article = ref(null)
const comments = ref([])
const commentContent = ref('')
const replyContent = ref('')
const replyTarget = ref(null)
const totalComments = ref(0)
const commentCurrentPage = ref(1)

const canEdit = computed(() => {
  if (!article.value || !auth.isLoggedIn) return false
  return auth.user?.id === article.value.userId || auth.isAdmin
})

const topLevelComments = computed(() => comments.value.filter(c => !c.parentId))

function getReplies(parentId) {
  return comments.value.filter(c => c.parentId === parentId)
}

function canDeleteComment(c) {
  if (!auth.isLoggedIn) return false
  return auth.user?.id === c.userId || auth.isAdmin
}

async function fetchArticle() {
  const res = await request.get(`/articles/${route.params.id}`)
  article.value = res.data
}

async function fetchComments() {
  const params = { page: commentCurrentPage.value - 1, size: 5 }
  const res = await request.get(`/articles/${route.params.id}/comments`, { params })
  const pageResult = res.data || {}
  comments.value = pageResult.records || []
  totalComments.value = pageResult.total || 0
}

function onCommentPageChange(page) {
  commentCurrentPage.value = page
  fetchComments()
}

function startReply(comment) { replyTarget.value = comment; replyContent.value = '' }
function cancelReply() { replyTarget.value = null; replyContent.value = '' }

async function submitComment() {
  if (!commentContent.value.trim()) { ElMessage.warning('请输入评论内容'); return }
  await request.post(`/articles/${route.params.id}/comments`, { content: commentContent.value })
  ElMessage.success('评论成功')
  commentContent.value = ''
  commentCurrentPage.value = 1
  fetchComments()
}

async function submitReply() {
  if (!replyContent.value.trim()) { ElMessage.warning('请输入回复内容'); return }
  await request.post(`/articles/${route.params.id}/comments`, {
    content: replyContent.value, parentId: replyTarget.value.id
  })
  ElMessage.success('回复成功')
  replyTarget.value = null
  replyContent.value = ''
  fetchComments()
}

async function deleteComment(id) {
  await ElMessageBox.confirm('确定删除此评论？', '确认', { type: 'warning' })
  await request.delete(`/comments/${id}`)
  ElMessage.success('删除成功')
  fetchComments()
}

async function handleDelete() {
  await ElMessageBox.confirm('确定删除此文章？此操作不可撤销。', '确认删除', { type: 'warning' })
  await request.delete(`/articles/${article.value.id}`)
  ElMessage.success('删除成功')
  router.push('/')
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.substring(0, 10) + ' ' + dateStr.substring(11, 16)
}

function renderContent(content) {
  return (content || '').replace(/\n/g, '<br>')
}

onMounted(() => { fetchArticle(); fetchComments() })
</script>

<style scoped>
.article-page { max-width: 1200px; margin: 0 auto; padding: 24px; }

/* Layout */
.content-layout {
  display: grid; grid-template-columns: 1fr 260px; gap: 32px; align-items: start;
}
.main-col { min-width: 0; }
.side-col { position: sticky; top: 80px; }

/* Article Card */
.article-card {
  background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius); padding: 32px;
}
.article-title { font-size: 28px; font-weight: 700; line-height: 1.3; margin-bottom: 16px; }
.article-meta {
  display: flex; align-items: center; gap: 16px; padding-bottom: 20px;
  border-bottom: 1px solid var(--border-light); margin-bottom: 24px; font-size: 14px; color: var(--text-secondary);
}
.meta-author { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.meta-author:hover .author-name { color: var(--accent); }
.author-avatar { background: var(--accent) !important; font-weight: 600; }
.author-name { font-weight: 600; color: var(--text-primary); }
.meta-date { margin-left: auto; }
.article-body { font-size: 16px; line-height: 1.8; color: var(--text-primary); }
.article-body :deep(p) { margin-bottom: 16px; }
.article-actions { margin-top: 24px; padding-top: 20px; border-top: 1px solid var(--border-light); display: flex; gap: 8px; }

/* Sidebar Author Card */
.author-card { text-align: center; }
.author-card-top { cursor: pointer; }
.author-big-avatar { background: var(--accent) !important; font-size: 22px; font-weight: 700; margin-bottom: 8px; }
.author-card h3 { font-size: 16px; font-weight: 600; }

/* Comments Section */
.comments-section { margin-top: 32px; }
.section-title { font-size: 18px; font-weight: 600; margin-bottom: 20px; color: var(--text-primary); }
.no-comments { text-align: center; padding: 40px 0; color: var(--text-tertiary); font-size: 14px; }
.login-hint {
  background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius);
  padding: 20px; margin-bottom: 16px; text-align: center; font-size: 14px; color: var(--text-secondary);
}
.login-hint a { color: var(--accent); font-weight: 500; }

/* Comment Form */
.comment-form {
  background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius);
  padding: 20px; margin-bottom: 20px; display: flex; gap: 12px;
}
.form-avatar { background: var(--accent) !important; font-weight: 600; flex-shrink: 0; }
.form-body { flex: 1; }
.form-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 8px; }
.form-hint { font-size: 12px; color: var(--text-tertiary); }

/* Comment Thread */
.comment-thread { margin-bottom: 8px; }
.comment-card {
  background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius);
  padding: 16px; margin-bottom: 8px; display: flex; gap: 12px;
}
.comment-card.reply-card { margin-left: 48px; }
.comment-avatar-col { flex-shrink: 0; }
.comment-avatar { background: var(--accent) !important; font-weight: 600; }
.comment-avatar.small { font-size: 12px; }
.comment-body { flex: 1; min-width: 0; }
.comment-header { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; flex-wrap: wrap; }
.comment-author { font-weight: 600; font-size: 13px; cursor: pointer; color: var(--text-primary); }
.comment-author:hover { color: var(--accent); }
.comment-time { font-size: 12px; color: var(--text-tertiary); margin-left: auto; }
.reply-to { font-size: 12px; color: var(--text-secondary); }
.comment-text { font-size: 14px; line-height: 1.6; color: var(--text-primary); margin: 6px 0; word-break: break-word; }
.comment-actions { display: flex; gap: 4px; margin-top: 4px; }
.action-btn {
  display: inline-flex; align-items: center; gap: 4px; background: none; border: none;
  cursor: pointer; font-size: 12px; color: var(--text-secondary); padding: 2px 6px; border-radius: 4px;
}
.action-btn:hover { color: var(--accent); background: var(--accent-bg); }
.action-btn.danger:hover { color: var(--danger); background: #fff0f0; }

/* Reply Editor */
.reply-editor {
  margin-left: 48px; margin-bottom: 8px; background: var(--bg-surface);
  border: 1px solid var(--border-default); border-radius: var(--radius); padding: 16px; display: flex; gap: 12px;
}

/* Sidebar Card */
.side-card {
  background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius); padding: 20px;
}

.pagination-wrap { display: flex; justify-content: center; margin: 24px 0; }

@media (max-width: 768px) {
  .content-layout { grid-template-columns: 1fr; }
  .side-col { display: none; }
  .comment-card.reply-card { margin-left: 24px; }
  .reply-editor { margin-left: 24px; }
}
</style>
