<template>
  <div class="home">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <div class="search-inner">
        <el-icon :size="18" color="#656d76"><Search /></el-icon>
        <input v-model="keyword" placeholder="搜索文章..." class="search-input"
          @keyup.enter="search" />
        <el-button v-if="keyword" text size="small" @click="keyword='';search()">清除</el-button>
      </div>
    </div>

    <div class="content-layout">
      <div class="main-col">
        <div v-if="keyword" class="search-hint">
          搜索 "<strong>{{ keyword }}</strong>" 的结果 ({{ total }} 篇)
        </div>

        <article v-for="article in articles" :key="article.id" class="article-card"
          @click="$router.push(`/articles/${article.id}`)">
          <div class="card-body">
            <h2 class="article-title">{{ article.title }}</h2>
            <p class="article-summary">{{ article.summary || article.content?.substring(0, 200) }}</p>
          </div>
          <div class="card-footer">
            <div class="card-meta">
              <span class="author" @click.stop="$router.push(`/users/${article.userId}`)">
                <el-avatar :size="18" class="mini-avatar">
                  {{ article.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                {{ article.username || '用户' + article.userId }}
              </span>
              <span class="date">{{ formatDate(article.createdAt) }}</span>
            </div>
          </div>
        </article>

        <el-empty v-if="articles.length === 0 && !keyword" description="还没有文章，成为第一个作者吧" />

        <div class="pagination-wrap" v-if="total > 10">
          <el-pagination background layout="prev, pager, next" :total="total"
            :page-size="10" :current-page="currentPage" @current-change="onPageChange" />
        </div>
      </div>

      <!-- 侧边栏 -->
      <aside class="side-col">
        <div class="side-card">
          <h3>关于 BlogHub</h3>
          <p>一个简洁、优雅的博客平台。分享你的想法，发现有趣的内容。</p>
        </div>
        <div class="side-card" v-if="auth.isLoggedIn">
          <h3>快捷操作</h3>
          <el-button type="primary" style="width:100%" @click="$router.push('/articles/create')">
            <el-icon><Edit /></el-icon>写文章
          </el-button>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import request from '../utils/request'

const router = useRouter()
const auth = useAuthStore()
const articles = ref([])
const keyword = ref('')
const total = ref(0)
const currentPage = ref(1)

async function fetchArticles() {
  const params = { page: currentPage.value - 1, size: 10 }
  if (keyword.value) params.keyword = keyword.value
  const res = await request.get('/articles/public', { params })
  const pageResult = res.data || {}
  articles.value = pageResult.records || []
  total.value = pageResult.total || 0
}
function search() { currentPage.value = 1; fetchArticles() }
function onPageChange(page) { currentPage.value = page; fetchArticles(); window.scrollTo(0, 0) }
function formatDate(dateStr) {
  if (!dateStr) return ''
  return dateStr.substring(0, 10) + ' ' + dateStr.substring(11, 16)
}
onMounted(fetchArticles)
</script>

<style scoped>
.home { max-width: 1200px; margin: 0 auto; padding: 0 24px; }

/* Search */
.search-bar {
  padding: 24px 0;
  border-bottom: 1px solid var(--border-default);
  margin-bottom: 24px;
}
.search-inner {
  display: flex; align-items: center; gap: 8px;
  background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius);
  padding: 10px 16px; max-width: 600px; transition: border-color .15s;
}
.search-inner:focus-within { border-color: var(--accent); box-shadow: 0 0 0 2px rgba(64,158,255,.15); }
.search-input {
  flex: 1; border: none; outline: none; font-size: 15px;
  background: transparent; color: var(--text-primary);
}
.search-input::placeholder { color: var(--text-tertiary); }
.search-hint { margin-bottom: 16px; font-size: 14px; color: var(--text-secondary); }

/* Layout */
.content-layout {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 32px;
  align-items: start;
}
.main-col { min-width: 0; }

/* Article Cards */
.article-card {
  background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius);
  padding: 24px; margin-bottom: 16px; cursor: pointer; transition: all .15s;
}
.article-card:hover { border-color: var(--accent); box-shadow: 0 2px 8px rgba(0,0,0,.06); }
.article-title { font-size: 20px; font-weight: 600; margin-bottom: 8px; color: var(--text-primary); line-height: 1.4; }
.article-summary { font-size: 14px; color: var(--text-secondary); line-height: 1.6; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; }
.card-footer { margin-top: 16px; padding-top: 12px; border-top: 1px solid var(--border-light); }
.card-meta { display: flex; justify-content: space-between; align-items: center; font-size: 13px; color: var(--text-secondary); }
.author { display: flex; align-items: center; gap: 6px; cursor: pointer; font-weight: 500; }
.author:hover { color: var(--accent); }
.mini-avatar { background: var(--accent) !important; font-size: 10px; font-weight: 600; }
.date { color: var(--text-tertiary); }

/* Sidebar */
.side-col { position: sticky; top: 80px; }
.side-card {
  background: var(--bg-surface); border: 1px solid var(--border-default); border-radius: var(--radius);
  padding: 20px; margin-bottom: 16px;
}
.side-card h3 { font-size: 15px; font-weight: 600; margin-bottom: 12px; color: var(--text-primary); }
.side-card p { font-size: 13px; color: var(--text-secondary); line-height: 1.6; }

.pagination-wrap { display: flex; justify-content: center; margin: 32px 0; }

@media (max-width: 768px) {
  .content-layout { grid-template-columns: 1fr; }
  .side-col { display: none; }
}
</style>
